/*====================================================================*\

SuggestionFilterPane.java

Class: suggestion filter pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.filter;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import java.util.stream.Stream;

import javafx.event.ActionEvent;

import javafx.geometry.Bounds;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import javafx.scene.paint.Color;

import javafx.stage.Popup;
import javafx.stage.WindowEvent;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IFunction0;

import uk.blankaspect.ui.jfx.label.Labels;

import uk.blankaspect.ui.jfx.listview.FilteredListView;
import uk.blankaspect.ui.jfx.listview.ListViewStyle;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: SUGGESTION FILTER PANE


/**
 * This class extends {@link SubstringFilterPane} to display a list of suggestions in a pop-up window below the text
 * field.  The items in the list are filtered by the content of the text field.
 *
 * @param <T>
 *          the type of the items that are displayed in the list of suggestions.
 */

public class SuggestionFilterPane<T>
	extends SubstringFilterPane
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The factor by which the size of the default font is multiplied to give the size of the font of the
		placeholder label for the list view. */
	private static final	double	LIST_VIEW_PLACEHOLDER_LABEL_FONT_SIZE_FACTOR	= 1.2;

	/** The preferred number of rows of the list view. */
	private static final	int		LIST_VIEW_NUM_ROWS	= 8;

	/** The key combination that causes the list of suggestions to be displayed. */
	private static final	KeyCombination	KEY_COMBO_LIST_TRIGGER	=
			new KeyCodeCombination(KeyCode.SPACE, KeyCombination.CONTROL_DOWN);

	/** Key combinations that do not cause the list of suggestions to be displayed. */
	private static final	KeyCombination[]	LIST_NON_TRIGGER_KEY_COMBINATIONS	=
	{
		new KeyCodeCombination(KeyCode.ENTER),
		new KeyCodeCombination(KeyCode.TAB),
		new KeyCodeCombination(KeyCode.DELETE),
	};

	/** The default key combination that advances the filter mode to the next value in the cycle. */
	private static final	KeyCombination	DEFAULT_KEY_COMBO_NEXT_FILTER_MODE	=
			new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN);

	/** Miscellaneous strings. */
	private static final	String	NO_SUGGESTIONS_STR	= "No suggestions";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		/** The background colour of the placeholder label of the list view. */
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ListViewStyle.ColourKey.CELL_BACKGROUND_EMPTY,
			CssSelector.builder()
					.cls(StyleClass.PLACEHOLDER_LABEL)
					.build()
		),

		/** The text colour of the placeholder label of the list view. */
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.LIST_VIEW_PLACEHOLDER_TEXT,
			CssSelector.builder()
					.cls(StyleClass.PLACEHOLDER_LABEL)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	PLACEHOLDER_LABEL =
				StyleConstants.CLASS_PREFIX + "suggestion-filter-pane-list-view-placeholder-label";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	LIST_VIEW_PLACEHOLDER_TEXT	= PREFIX + "listView.placeholder.text";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The list view in which suggestions are displayed. */
	private	FilteredListView<T>	listView;

	/** The pop-up window in which the {@linkplain #listView list of suggestions} is displayed. */
	private	Popup				popUp;

	/** Flag: if {@code true}, 'key pressed' events for the <i>Home</i> and <i>End</i> keys that occur on {@link
		#listView} will be intercepted and 'redirected' to the text field. */
	private	boolean				redirectHomeEndKeys;

	/** The key combination that advances the filter mode to the next value in the cycle. */
	private	KeyCombination		filterModeKeyCombination;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(SuggestionFilterPane.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a suggestion text-field pane with the specified filter mode.
	 *
	 * @param converter
	 *          the provider of a string representation and a graphical representation of each item of the list view.
	 * @param filterMode
	 *          the filter mode of the pane.
	 */

	public SuggestionFilterPane(
		FilteredListView.IConverter<T>	converter,
		FilterMode						filterMode)
	{
		// Call alternative constructor
		this(converter, null, filterMode, false, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a suggestion text-field pane with an optional button for clearing the text field and an
	 * optional <i>filter mode</i> button.
	 *
	 * @param converter
	 *          the provider of a string representation and a graphical representation of each item of the list view.
	 * @param initialFilterMode
	 *          the initial filter mode of the pane, which can be changed only if the pane has a filter-mode button.
	 * @param hasClearButton
	 *          if {@code true}, the pane will have a button for clearing the text field.
	 * @param hasFilterModeButton
	 *          if {@code true}, the pane will have a button for changing the filter mode.
	 */

	public SuggestionFilterPane(
		FilteredListView.IConverter<T>	converter,
		FilterMode						initialFilterMode,
		boolean							hasClearButton,
		boolean							hasFilterModeButton)
	{
		// Call alternative constructor
		this(converter, null, initialFilterMode, hasClearButton, hasFilterModeButton);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a suggestion text-field pane with an optional button for clearing the text field and an
	 * optional <i>filter mode</i> button.
	 *
	 * @param converter
	 *          the provider of a string representation and a graphical representation of each item of the list view.
	 * @param filterField
	 *          the text field for a filter that will appear in this pane.  If it is {@code null}, a new instance of a
	 *          text field will be created.
	 * @param initialFilterMode
	 *          the initial filter mode of the pane, which can be changed only if the pane has a filter-mode button.
	 * @param hasClearButton
	 *          if {@code true}, the pane will have a button for clearing the text field.
	 * @param hasFilterModeButton
	 *          if {@code true}, the pane will have a button for changing the filter mode.
	 */

	public SuggestionFilterPane(
		FilteredListView.IConverter<T>	converter,
		TextField						filterField,
		FilterMode						initialFilterMode,
		boolean							hasClearButton,
		boolean							hasFilterModeButton)
	{
		// Call superclass constructor
		super(filterField, initialFilterMode, hasClearButton, hasFilterModeButton);

		// Initialise instance variables
		redirectHomeEndKeys = true;
		filterModeKeyCombination = DEFAULT_KEY_COMBO_NEXT_FILTER_MODE;

		// Get text field
		TextField textField = getTextField();

		// Create list view for filtered items
		listView = new FilteredListView<>(converter);
		listView.setFilterMode(initialFilterMode);
		listView.prefWidthProperty().bind(textField.widthProperty());
		listView.setPrefHeight((double)LIST_VIEW_NUM_ROWS
								* (TextUtils.textHeight() + 2.0 * FilteredListView.DEFAULT_CELL_VERTICAL_PADDING + 1.0)
								+ 2.0);

		// Set placeholder on list view
		Label placeholderLabel =
				Labels.expansive(NO_SUGGESTIONS_STR, LIST_VIEW_PLACEHOLDER_LABEL_FONT_SIZE_FACTOR,
								 getColour(ColourKey.LIST_VIEW_PLACEHOLDER_TEXT),
								 getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_EMPTY));
		placeholderLabel.getStyleClass().add(StyleClass.PLACEHOLDER_LABEL);
		listView.setPlaceholder(placeholderLabel);

		// Update list view when filter changes
		textField.textProperty().addListener((observable, oldText, text) -> listView.setFilter(text));

		// Create pop-up for list
		popUp = new Popup();
		popUp.getContent().add(listView);
		popUp.setAutoHide(true);

		// When pop-up is shown, bind filter mode of list view to filter mode of this pane; select first item in list
		// view
		popUp.addEventHandler(WindowEvent.WINDOW_SHOWN, event ->
		{
			// Update list view when filter mode changes
			listView.filterModeProperty().bind(filterModeProperty());

			// Select first item in list view
			if (!listView.getItems().isEmpty())
				listView.getSelectionModel().select(0);
		});

		// When pop-up is hidden, unbind filter mode of list view from filter mode of this pane
		popUp.addEventHandler(WindowEvent.WINDOW_HIDING, event -> listView.filterModeProperty().unbind());

		// Display list when a recognised key is pressed on text field
		textField.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			// Create function to test whether key event causes suggestion list to be displayed
			IFunction0<Boolean> showList = () ->
			{
				if (KEY_COMBO_LIST_TRIGGER.match(event))
					return true;

				if (Stream.of(LIST_NON_TRIGGER_KEY_COMBINATIONS).anyMatch(keyCombo -> keyCombo.match(event)))
					return false;

				if (event.isControlDown() || event.isAltDown())
					return false;

				KeyCode keyCode = event.getCode();
				return !(keyCode.isNavigationKey() || keyCode.isFunctionKey() || keyCode.isModifierKey());
			};

			// Change filter mode
			if (filterModeKeyCombination.match(event))
			{
				// Change filter mode
				if (hasFilterModeButton)
					nextFilterMode();

				// Consume event
				event.consume();
			}

			// Hide pop-up
			else if (event.getCode() == KeyCode.ESCAPE)
			{
				// Hide pop-up
				popUp.hide();

				// Consume event
				event.consume();
			}

			// Display suggestion list in pop-up
			else if (showList.invoke() && !popUp.isShowing())
			{
				// Display pop-up
				Bounds bounds = textField.localToScreen(textField.getLayoutBounds());
				popUp.show(textField, bounds.getMinX() - 1.0, bounds.getMaxY());

				// Update list view
				listView.setFilter(textField.getText());

				// Consume event
				if (event.getCode() != KeyCode.BACK_SPACE)
					event.consume();
			}
		});

		// Hide list when 'action' event occurs on text field
		textField.addEventFilter(ActionEvent.ACTION, event ->
		{
			if (popUp.isShowing())
				popUp.hide();
		});

		// Intercept presses of navigation keys on the list, and simulate the behaviour that would have occurred if the
		// events had occurred on the text field
		listView.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if (redirectHomeEndKeys)
			{
				int position = textField.getCaretPosition();
				switch (event.getCode())
				{
					case LEFT:
						if (position > 0)
							textField.positionCaret(position - 1);
						event.consume();
						break;

					case RIGHT:
						if (position < textField.getLength())
							textField.positionCaret(position + 1);
						event.consume();
						break;

					case HOME:
						if (event.isShiftDown())
							textField.selectHome();
						else
							textField.home();
						event.consume();
						break;

					case END:
						if (event.isShiftDown())
							textField.selectEnd();
						else
							textField.end();
						event.consume();
						break;

					default:
						// do nothing
						break;
				}
			}
		});

		// Handle key presses on list
		listView.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if (filterModeKeyCombination.match(event))
			{
				// Change filter mode
				if (hasFilterModeButton)
					nextFilterMode();

				// Consume event
				event.consume();
			}
			else
			{
				switch (event.getCode())
				{
					case ESCAPE:
					case TAB:
						// Hide pop-up
						popUp.hide();

						// Consume event
						event.consume();
						break;

					default:
						// do nothing
						break;
				}
			}
		});

		// Commit selected item when mouse is double-clicked on item in list
		listView.addEventHandler(ActionEvent.ACTION, event ->
		{
			// Get selected item
			String text = converter.getText(listView.getSelectionModel().getSelectedItem());

			// Set it on text field
			textField.setText(text);
			textField.end();

			// Close pop-up
			popUp.hide();
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the colour that is associated with the specified key in the colour map of the current theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the current theme of the style
	 *         manager, or {@link StyleManager#DEFAULT_COLOUR} if there is no such colour.
	 */

	private static Color getColour(
		String	key)
	{
		return StyleManager.INSTANCE.getColourOrDefault(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the list view of suggestions that is displayed by this pane.
	 *
	 * @return the list view of suggestions that is displayed by this pane.
	 */

	public FilteredListView<T> getListView()
	{
		return listView;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the suggestions that are displayed by this pane to the specified values.
	 *
	 * @param items
	 *          the suggestions that will be displayed by this pane.
	 */

	@SuppressWarnings("unchecked")
	public void setSuggestions(
		T...	items)
	{
		listView.setUnfilteredItems(items);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the suggestions that are displayed by this pane to the specified values.
	 *
	 * @param items
	 *          the suggestions that will be displayed by this pane.
	 */

	public void setSuggestions(
		Iterable<? extends T> items)
	{
		listView.setUnfilteredItems(items);
	}

	//------------------------------------------------------------------

	/**
	 * Sets a flag that determines whether 'key pressed' events for the <i>Home</i> and <i>End</i> keys that occur on
	 * the list view will be intercepted and 'redirected' to the text field by simulating the behaviour that would have
	 * occurred if the events had occurred on the text field.
	 *
	 * @param redirect
	 *          if {@code true}, 'key pressed' events for the <i>Home</i> and <i>End</i> keys will be 'redirected' to
	 *          the text field.
	 */

	public void setRedirectHomeEndKeys(
		boolean	redirect)
	{
		redirectHomeEndKeys = redirect;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the key combination that advances the filter mode to the next value in the cycle.
	 *
	 * @param keyCombination
	 *          the key combination that will advances the filter mode to the next value in the cycle.  If it is {@code
	 *          null}, the default key combination will be set.
	 */

	public void setFilterModeKeyCombination(
		KeyCombination	keyCombination)
	{
		filterModeKeyCombination = (keyCombination == null) ? DEFAULT_KEY_COMBO_NEXT_FILTER_MODE : keyCombination;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
