/*====================================================================*\

SubstringFilterPane.java

Class: substring-filter pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.filter;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.event.ActionEvent;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Group;

import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.button.GraphicButton;

import uk.blankaspect.ui.jfx.icon.Icons;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.Text2;

//----------------------------------------------------------------------


// CLASS: SUBSTRING-FILTER PANE


/**
 * This class implements a pane containing a text field for a filter, an optional button for clearing the field and an
 * optional button for selecting the mode of the filter.
 */

public class SubstringFilterPane
	extends HBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * The modes in which the filter of a {@link SubstringFilterPane} can operate.
	 */
	public enum FilterMode
	{
		FRAGMENTED,
		WILDCARD_ANYWHERE,
		WILDCARD_START,
		WILDCARD_ALL
	}

	/** The gap between adjacent children of a filter pane. */
	private static final	double	GAP	= 2.0;

	/** A map of the templates for filter modes. */
	private static final	Map<FilterMode, String>	FILTER_MODE_TEMPLATES	= new EnumMap<>(Map.of
	(
		FilterMode.FRAGMENTED,        "-a-b-",
		FilterMode.WILDCARD_ANYWHERE, "\u2013a?*\u2013",
		FilterMode.WILDCARD_START,    "a?*\u2014",
		FilterMode.WILDCARD_ALL,      "a?*"
	));

	/** The extra gap for a <i>filter mode</i> button. */
	private static final	double	FILTER_MODE_BUTTON_EXTRA_GAP	= 2.0;

	/** Miscellaneous strings. */
	private static final	String	CLEAR_FIELD_STR	= "Clear field";
	private static final	String	FILTER_MODE_STR	= "Filter mode";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.CLEAR_FIELD_BUTTON_DISC,
			CssSelector.builder()
					.cls(StyleClass.SUBSTRING_FILTER_PANE)
					.desc(Icons.StyleClass.CLEAR01_DISC)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.CLEAR_FIELD_BUTTON_CROSS,
			CssSelector.builder()
					.cls(StyleClass.SUBSTRING_FILTER_PANE)
					.desc(Icons.StyleClass.CLEAR01_CROSS)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.FILTER_MODE_BUTTON_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.SUBSTRING_FILTER_PANE)
					.desc(StyleClass.FILTER_MODE_BUTTON).pseudo(GraphicButton.PseudoClassKey.INACTIVE)
					.desc(GraphicButton.StyleClass.INNER_VIEW)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.FILTER_MODE_BUTTON_BORDER,
			CssSelector.builder()
					.cls(StyleClass.SUBSTRING_FILTER_PANE)
					.desc(StyleClass.FILTER_MODE_BUTTON).pseudo(GraphicButton.PseudoClassKey.INACTIVE)
					.desc(GraphicButton.StyleClass.INNER_VIEW)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.FILTER_MODE_BUTTON_TEXT,
			CssSelector.builder()
					.cls(StyleClass.FILTER_MODE_BUTTON_TEXT)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	SUBSTRING_FILTER_PANE	= StyleConstants.CLASS_PREFIX + "substring-filter-pane";

		String	FILTER_MODE_BUTTON		= StyleConstants.CLASS_PREFIX + "filter-mode-button";
		String	FILTER_MODE_BUTTON_TEXT	= SUBSTRING_FILTER_PANE + "-filter-mode-button-text";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	CLEAR_FIELD_BUTTON_CROSS		= PREFIX + "clearFieldButton.cross";
		String	CLEAR_FIELD_BUTTON_DISC			= PREFIX + "clearFieldButton.disc";
		String	FILTER_MODE_BUTTON_BACKGROUND	= PREFIX + "filterModeButton.background";
		String	FILTER_MODE_BUTTON_BORDER		= PREFIX + "filterModeButton.border";
		String	FILTER_MODE_BUTTON_TEXT			= PREFIX + "filterModeButton.text";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The <i>filter mode</i> property of this pane. */
	private	SimpleObjectProperty<FilterMode>	filterMode;

	/** The text field of this pane. */
	private	TextField							textField;

	/** The <i>filter mode</i> button of this pane. */
	private	GraphicButton						filterModeButton;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(SubstringFilterPane.class, COLOUR_PROPERTIES,
									   GraphicButton.class);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a filter pane containing a new instance of a text field and with the specified filter
	 * mode.
	 *
	 * @param filterMode
	 *          the initial filter mode of the <i>filter mode</i> button.
	 */

	public SubstringFilterPane(
		FilterMode	filterMode)
	{
		// Call alternative constructor
		this(null, filterMode, false, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a filter pane containing a new instance of a text field, an optional button for
	 * clearing the text field and an optional <i>filter mode</i> button.
	 *
	 * @param initialFilterMode
	 *          the initial filter mode of the pane, which can be changed only if the pane has a filter-mode button.
	 * @param hasClearButton
	 *          if {@code true}, the pane will have a button for clearing the text field.
	 * @param hasFilterModeButton
	 *          if {@code true}, the pane will have a button for changing the filter mode.
	 */

	public SubstringFilterPane(
		FilterMode	initialFilterMode,
		boolean		hasClearButton,
		boolean		hasFilterModeButton)
	{
		// Call alternative constructor
		this(null, initialFilterMode, hasClearButton, hasFilterModeButton);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a filter pane containing the specified text field, an optional button for clearing the
	 * text field and an optional <i>filter mode</i> button.
	 *
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

	public SubstringFilterPane(
		TextField	filterField,
		FilterMode	initialFilterMode,
		boolean		hasClearButton,
		boolean		hasFilterModeButton)
	{
		// Call superclass constructor
		super(GAP);

		// Validate arguments
		if (initialFilterMode == null)
			throw new IllegalArgumentException("Null initial filter mode");

		// Initialise instance variables
		filterMode = new SimpleObjectProperty<>();

		// Set properties
		setAlignment(Pos.CENTER_LEFT);
		getStyleClass().add(StyleClass.SUBSTRING_FILTER_PANE);

		// Create text field, if necessary
		textField = (filterField == null) ? new TextField() : filterField;
		HBox.setHgrow(textField, Priority.ALWAYS);

		// Add text field to this pane
		getChildren().add(textField);

		// Create 'clear field' button
		if (hasClearButton)
		{
			// Create button
			GraphicButton clearFieldButton =
					new GraphicButton(Icons.clear01(getColour(ColourKey.CLEAR_FIELD_BUTTON_DISC),
													getColour(ColourKey.CLEAR_FIELD_BUTTON_CROSS)),
									  CLEAR_FIELD_STR);
			clearFieldButton.disableProperty().bind(textField.textProperty().isEmpty());
			clearFieldButton.setOnAction(event ->
			{
				textField.clear();
				Platform.runLater(textField::requestFocus);
			});

			// Add button to this pane
			getChildren().add(clearFieldButton);
		}

		// Create 'filter mode' button
		if (hasFilterModeButton)
		{
			// Create components of graphic for each filter mode
			EnumMap<FilterMode, Text2> textNodes = new EnumMap<>(FilterMode.class);
			double maxWidth = 0.0;
			for (FilterMode filterMode : FILTER_MODE_TEMPLATES.keySet())
			{
				// Create text node
				Text2 textNode = Text2.createCentred(FILTER_MODE_TEMPLATES.get(filterMode));
				textNode.setFill(getColour(ColourKey.FILTER_MODE_BUTTON_TEXT));
				textNode.getStyleClass().add(StyleClass.FILTER_MODE_BUTTON_TEXT);
				textNodes.put(filterMode, textNode);

				// Update maximum width
				double width = textNode.getWidth();
				if (maxWidth < width)
					maxWidth = width;
			}

			// Centre graphic of each filter mode horizontally
			double buttonWidth = 2.0 * (Math.ceil(0.5 * maxWidth) + 1.0);
			for (FilterMode filterMode : textNodes.keySet())
			{
				Text2 textNode = textNodes.get(filterMode);
				textNode.relocate(Math.floor(0.5 * (buttonWidth - textNode.getWidth())), 0.0);
			}

			// Create graphic for button
			Group graphic = new Group(new Rectangle(buttonWidth, Text2.textHeight(), Color.TRANSPARENT),
									  textNodes.get(initialFilterMode));

			// Create button
			filterModeButton = new GraphicButton(graphic, FILTER_MODE_STR);
			filterModeButton.setPadding(Insets.EMPTY);
			filterModeButton.setBackgroundColour(getColour(ColourKey.FILTER_MODE_BUTTON_BACKGROUND));
			filterModeButton.setBorderColour(getColour(ColourKey.FILTER_MODE_BUTTON_BORDER));
			filterModeButton.getStyleClass().add(StyleClass.FILTER_MODE_BUTTON);
			if (!hasClearButton)
				setMargin(filterModeButton, new Insets(0.0, 0.0, 0.0, FILTER_MODE_BUTTON_EXTRA_GAP));

			// Update button when filter mode changes
			filterMode.addListener((observable, oldMode, mode) -> graphic.getChildren().set(1, textNodes.get(mode)));

			// Cycle through filter modes when button is pressed
			filterModeButton.addEventHandler(ActionEvent.ACTION, event -> nextFilterMode());

			// Add button to this pane
			getChildren().add(filterModeButton);
		}

		// Initialise filter mode
		filterMode.set(initialFilterMode);
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
	 * Returns the text field of this pane.
	 *
	 * @return the text field of this pane.
	 */

	public TextField getTextField()
	{
		return textField;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the <i>filter mode</i> button of this pane.
	 *
	 * @return the <i>filter mode</i> button of this pane.
	 */

	public GraphicButton getFilterModeButton()
	{
		return filterModeButton;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the filter mode of this pane.
	 *
	 * @return the filter mode of this pane.
	 */

	public FilterMode getFilterMode()
	{
		return filterMode.get();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the filter mode of this pane as a read-only property.
	 *
	 * @return the filter mode of this pane as a read-only property.
	 */

	public ReadOnlyObjectProperty<FilterMode> filterModeProperty()
	{
		return filterMode;
	}

	//------------------------------------------------------------------

	/**
	 * Advances the filter mode to the next value in the cycle.
	 */

	protected void nextFilterMode()
	{
		filterMode.set(FilterMode.values()[(filterMode.get().ordinal() + 1) % FilterMode.values().length]);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
