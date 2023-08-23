/*====================================================================*\

FilteredListView.java

Class: filtered list view.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.listview;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.css.PseudoClass;

import javafx.event.ActionEvent;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;

import javafx.scene.Node;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.HBox;

import javafx.scene.paint.Color;

import javafx.scene.text.Font;

import javafx.stage.Window;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.common.matcher.OrderedCharacterMatcher;
import uk.blankaspect.common.matcher.SimpleWildcardPatternMatcher;

import uk.blankaspect.ui.jfx.filter.SubstringFilterPane;
import uk.blankaspect.ui.jfx.filter.SubstringFilterUtils;

import uk.blankaspect.ui.jfx.font.FontUtils;

import uk.blankaspect.ui.jfx.popup.CellPopUpManager;
import uk.blankaspect.ui.jfx.popup.PopUpUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.Text2;
import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: FILTERED STRING LIST VIEW


/**
 * This class extends {@link ListView} with the ability to filter the items that are displayed in the list.
 *
 * @param <T>
 *          the type of the items that are displayed in the list view.
 */

public class FilteredListView<T>
	extends ListView<T>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default padding at the top and bottom of the label of a cell. */
	public static final		double	DEFAULT_CELL_VERTICAL_PADDING	= 2.0;

	/** The default gap between the graphic and text of a cell of this list view. */
	private static final	double	DEFAULT_GRAPHIC_TEXT_GAP	= 6.0;

	/** The default filter mode. */
	private static final	SubstringFilterPane.FilterMode	DEFAULT_FILTER_MODE	=
			SubstringFilterPane.FilterMode.WILDCARD_START;

	/** The pseudo-class that is associated with the <i>highlighted</i> state. */
	private static final	PseudoClass	HIGHLIGHTED_PSEUDO_CLASS	= PseudoClass.getPseudoClass(PseudoClassKey.HIGHLIGHTED);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ListViewStyle.ColourKey.CELL_TEXT,
			CssSelector.builder()
						.cls(StyleClass.TEXT_SPAN)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ListViewStyle.ColourKey.CELL_TEXT_SELECTED,
			CssSelector.builder()
						.cls(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.SELECTED)
						.desc(StyleClass.TEXT_SPAN)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TEXT_SPAN_HIGHLIGHTED,
			CssSelector.builder()
						.cls(StyleClass.TEXT_SPAN).pseudo(PseudoClassKey.HIGHLIGHTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TEXT_SPAN_HIGHLIGHTED_SELECTED,
			CssSelector.builder()
						.cls(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.SELECTED)
						.desc(StyleClass.TEXT_SPAN).pseudo(PseudoClassKey.HIGHLIGHTED)
						.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.TEXT_SPAN).pseudo(PseudoClassKey.HIGHLIGHTED)
									.build())
						.boldFont()
						.build()
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	TEXT_SPAN	= StyleConstants.CLASS_PREFIX + "filtered-list-view-text-span";
	}

	/** Keys of CSS pseudo-classes. */
	public interface PseudoClassKey
	{
		String	HIGHLIGHTED	= "highlighted";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	TEXT_SPAN_HIGHLIGHTED_SELECTED	= PREFIX + "textSpan.highlighted.selected";
		String	TEXT_SPAN_HIGHLIGHTED			= PREFIX + "textSpan.highlighted";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The provider of a string representation and a graphical representation of each item of this list view. */
	private	IConverter<T>											converter;

	/** The background colour of the selected row when the list view does not have focus. */
	private	Color													selectedBackgroundColour;

	/** The background colour of the cell of a focused row when the list view has focus. */
	private	Color													focusedBackgroundColour;

	/** The background colour of the selected row when the list view has focus. */
	private	Color													selectedFocusedBackgroundColour;

	/** The colour of text. */
	private	Color													textColour;

	/** The colour of highlighted text. */
	private	Color													highlightedTextColour;

	/** The gap between the graphic and text of a cell of this list view. */
	private	double													graphicTextGap;

	/** The padding at the top and bottom of a cell. */
	private	double													cellVerticalPadding;

	/** Flag: if {@code true}, the labels of cells will be truncated, avoiding a horizontal scroll bar. */
	private	boolean													truncateCells;

	/** The filter mode. */
	private	SimpleObjectProperty<SubstringFilterPane.FilterMode>	filterMode;

	/** The filter. */
	private	String													filter;

	/** The matcher that is used to filter items when the filter mode is {@linkplain
		SubstringFilterPane.FilterMode#FRAGMENTED &apos;fragmented&apos;}. */
	private	OrderedCharacterMatcher									characterMatcher;

	/** The manager of the pop-up windows that are displayed for the cells of this list view. */
	private	CellPopUpManager										cellPopUpManager;

	/** The unfiltered items that are displayed in this list view. */
	private	List<T>													unfilteredItems;

	/** The cells of this list view. */
	private	List<Cell>												cells;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(FilteredListView.class, COLOUR_PROPERTIES, RULE_SETS,
									   ListViewStyle.class);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a filtered list view.
	 *
	 * @param converter
	 *          the provider of a string representation and a graphical representation of each item of the list view.
	 */

	public FilteredListView(
		IConverter<T>	converter)
	{
		// Initialise instance variables
		this.converter = converter;
		selectedBackgroundColour = getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_SELECTED);
		focusedBackgroundColour = getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_FOCUSED);
		selectedFocusedBackgroundColour = getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_SELECTED_FOCUSED);
		textColour = getColour(ListViewStyle.ColourKey.CELL_TEXT);
		highlightedTextColour = getColour(ColourKey.TEXT_SPAN_HIGHLIGHTED);
		graphicTextGap = DEFAULT_GRAPHIC_TEXT_GAP;
		cellVerticalPadding = DEFAULT_CELL_VERTICAL_PADDING;
		filterMode = new SimpleObjectProperty<>(DEFAULT_FILTER_MODE);
		filterMode.addListener(observable -> update());
		filter = "";
		characterMatcher = new OrderedCharacterMatcher();
		cellPopUpManager = new CellPopUpManager(Cell.POP_UP_DELAY);
		unfilteredItems = new ArrayList<>();
		cells = new ArrayList<>();

		// Set properties
		setCellFactory(listView -> new Cell());
		getStyleClass().add(ListViewStyle.StyleClass.LIST_VIEW);

		// Fire 'action' event when item is selected and 'Enter' key is pressed
		addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if (event.getCode() == KeyCode.ENTER)
			{
				// If item is selected, fire 'action' event
				if (!getSelectionModel().isEmpty())
					fireEvent(new ActionEvent(ActionEvent.ACTION, this));
			}
		});

		// Fire 'action' event when mouse is double-clicked on item in list
		addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
		{
			if ((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2)
					&& !getSelectionModel().isEmpty())
				fireEvent(new ActionEvent(ActionEvent.ACTION, this));
		});

		// Update cell backgrounds on change of state
		if (StyleManager.INSTANCE.notUsingStyleSheet())
		{
			// Update cell backgrounds when selection changes
			getSelectionModel().selectedIndexProperty().addListener(observable -> updateCellBackgrounds());

			// Update cell backgrounds when focus changes
			focusedProperty().addListener(observable -> updateCellBackgrounds());
		}

		// Ensure cells are redrawn if scroll bar is hidden
		widthProperty().addListener(observable -> Platform.runLater(() -> refresh()));

		// Update list
		update();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the specified text to a form in which a filter can be applied to it.
	 *
	 * @param  text
	 *           the text that will be normalised.
	 * @return the input text converted to a form in which a filter can be applied to it.
	 */

	private static String normalise(
		String	str)
	{
		return str.toLowerCase();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the colour that is associated with the specified key in the colour map of the selected theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the selected theme of the style
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
	 * Returns the provider of a string representation and a graphical representation of each item of this list view.
	 *
	 * @return the provider of a string representation and a graphical representation of each item of this list view.
	 */

	public IConverter<T> getConverter()
	{
		return converter;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the provider of a string representation and a graphical representation of each item of this list view to the
	 * specified value.
	 *
	 * @param converter
	 *          the provider of a string representation and a graphical representation of each item of this list view.
	 */

	public void setConverter(
		IConverter<T>	converter)
	{
		// Update instance variable
		this.converter = converter;

		// Update list view
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background colour of the selected row when the list view does not have focus to the specified value.
	 *
	 * @param colour
	 *          the value to which the background colour of the selected row when the list view does not have focus
	 *          will be set.
	 */

	public void setSelectedBackgroundColour(
		Color	colour)
	{
		// Update instance variable
		selectedBackgroundColour = colour;

		// Update cell backgrounds
		updateCellBackgrounds();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background colour of the cell of a focused row when the list view has focus to the specified value.
	 *
	 * @param colour
	 *          the value to which the background colour of the cell of a focused row when the list view has focus will
	 *          be set.
	 */

	public void setFocusedBackgroundColour(
		Color	colour)
	{
		// Update instance variable
		focusedBackgroundColour = colour;

		// Update cell backgrounds
		updateCellBackgrounds();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background colour of the selected row when the list view has focus to the specified value.
	 *
	 * @param colour
	 *          the value to which the background colour of the selected row when the list view has focus will be set.
	 */

	public void setSelectedFocusedBackgroundColour(
		Color	colour)
	{
		// Update instance variable
		selectedFocusedBackgroundColour = colour;

		// Update cell backgrounds
		updateCellBackgrounds();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the colour of text to the specified value.
	 *
	 * @param colour
	 *          the value to which the colour of text will be set.
	 */

	public void setTextColour(
		Color	colour)
	{
		// Update instance variable
		textColour = colour;

		// Update list view
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the colour of highlighted text to the specified value.
	 *
	 * @param colour
	 *          the value to which the colour of highlighted text will be set.
	 */

	public void setHighlightedTextColour(
		Color	colour)
	{
		// Update instance variable
		highlightedTextColour = colour;

		// Update list view
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the gap between the graphic and text of a cell to the specified value.
	 *
	 * @param gap
	 *          the value to which the gap between the graphic and text of a cell will be set.
	 */
	public void setGraphicTextGap(
		double	gap)
	{
		// Update instance variable
		graphicTextGap = gap;

		// Update list view
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the padding at the top and bottom of a cell to the specified value.
	 *
	 * @param padding
	 *          the value to which the padding at the top and bottom of a cell will be set.
	 */

	public void setCellVerticalPadding(
		double	padding)
	{
		// Update instance variable
		cellVerticalPadding = padding;

		// Redraw cells
		refresh();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>truncate cells</i> flag to the specified value.
	 *
	 * @param truncate
	 *          the value to which the <i>truncate cells</i> flag will be set.
	 */

	public void setTruncateCells(
		boolean	truncate)
	{
		// Update instance variable
		truncateCells = truncate;

		// Set preferred width of cells
		for (Cell cell : cells)
			cell.setPrefWidth(truncate ? 0.0 : Cell.USE_COMPUTED_SIZE);

		// Redraw cells
		refresh();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the filter mode to the specified value.
	 *
	 * @param filterMode
	 *          the value to which the filter mode will be set.
	 */

	public void setFilterMode(
		SubstringFilterPane.FilterMode	filterMode)
	{
		this.filterMode.set(filterMode);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the filter mode as a property.
	 *
	 * @return the filter mode as a property.
	 */

	public ObjectProperty<SubstringFilterPane.FilterMode> filterModeProperty()
	{
		return filterMode;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the filter to the specified value.
	 *
	 * @param filter
	 *          the value to which the filter will be set.
	 */

	public void setFilter(
		String	filter)
	{
		// Update instance variable
		this.filter = (filter == null) ? "" : normalise(filter);

		// Update list view
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Returns an unmodifiable list of the unfiltered items that are displayed in this list view.
	 *
	 * @return an unmodifiable list of the unfiltered items that are displayed in this list view.
	 */

	public List<T> getUnfilteredItems()
	{
		return Collections.unmodifiableList(unfilteredItems);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the unfiltered items to the specified values.
	 *
	 * @param items
	 *          the unfiltered items that will be displayed in this list view.
	 */

	@SuppressWarnings("unchecked")
	public void setUnfilteredItems(
		T...	items)
	{
		setUnfilteredItems(Arrays.asList(items));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the unfiltered items to the specified values.
	 *
	 * @param items
	 *          the unfiltered items that will be displayed in this list view.
	 */

	public void setUnfilteredItems(
		Iterable<? extends T>	items)
	{
		// Update instance variable
		unfilteredItems.clear();
		for (T item : items)
			unfilteredItems.add(item);

		// Update list view
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a label of a cell to represent the specified item.
	 *
	 * @param  item
	 *           the item for which a label will be created.
	 * @return a label of a cell to represent {@code item}.
	 */

	protected Label createCellLabel(
		T	item)
	{
		// Create container for graphic and text
		HBox container = new HBox(graphicTextGap);
		container.setAlignment(Pos.CENTER_LEFT);

		// Get graphic for item
		Node graphic = converter.getGraphic(item);
		if (graphic != null)
			container.getChildren().add(graphic);

		// Get text for item
		String text = converter.getText(item);

		// Get text nodes for spans that result from applying filter to text
		SubstringFilterUtils.IDecorator decorator = (textNode, highlighted) ->
		{
			if (StyleManager.INSTANCE.notUsingStyleSheet())
				textNode.setFill(highlighted ? highlightedTextColour : textColour);
			textNode.setFont(highlighted ? FontUtils.boldFont() : Font.getDefault());
			textNode.getStyleClass().add(StyleClass.TEXT_SPAN);
			textNode.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, highlighted);
		};
		List<Text2> textNodes = SubstringFilterUtils.createTextNodes(filterMode.get(), filter, text,
																	 FilteredListView::normalise, decorator);

		// If text is not decomposed, create single text node ...
		if (textNodes == null)
		{
			Text2 textNode = Text2.createCentred(text);
			decorator.apply(textNode, false);
			container.getChildren().add(textNode);
		}

		// ... otherwise, wrap text nodes in group
		else if (!textNodes.isEmpty())
			container.getChildren().add(TextUtils.createGroup(textNodes));

		// Create label
		Label label = new Label(null, container);
		label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		label.setPadding(new Insets(cellVerticalPadding, Cell.LABEL_HORIZONTAL_PADDING,
									cellVerticalPadding, Cell.LABEL_HORIZONTAL_PADDING));
		label.getStyleClass().add(ListViewStyle.StyleClass.CELL_LABEL);

		// Return label
		return label;
	}

	//------------------------------------------------------------------

	/**
	 * Updates this list view.
	 */

	private void update()
	{
		// Initialise list of filtered items
		ObservableList<T> filteredItems = FXCollections.observableArrayList();

		// If there is no filter, add all items ...
		if (filter.isEmpty())
			filteredItems.addAll(unfilteredItems);

		// ... otherwise, apply filter to items
		else
		{
			switch (filterMode.get())
			{
				case FRAGMENTED:
					characterMatcher.setTarget(filter);
					unfilteredItems.stream()
									.filter(item -> characterMatcher.setSource(normalise(converter.getText(item))).match())
									.forEach(item -> filteredItems.add(item));
					break;

				case WILDCARD_ANYWHERE:
				{
					SimpleWildcardPatternMatcher matcher = SimpleWildcardPatternMatcher.anywhereIgnoreCase(filter);
					unfilteredItems.stream()
									.filter(item -> matcher.match(converter.getText(item)))
									.forEach(item -> filteredItems.add(item));
					break;
				}

				case WILDCARD_START:
				{
					SimpleWildcardPatternMatcher matcher = SimpleWildcardPatternMatcher.startIgnoreCase(filter);
					unfilteredItems.stream()
									.filter(item -> matcher.match(converter.getText(item)))
									.forEach(item -> filteredItems.add(item));
					break;
				}

				case WILDCARD_ALL:
				{
					SimpleWildcardPatternMatcher matcher = SimpleWildcardPatternMatcher.allIgnoreCase(filter);
					unfilteredItems.stream()
									.filter(item -> matcher.match(converter.getText(item)))
									.forEach(item -> filteredItems.add(item));
					break;
				}
			}
		}

		// Get selected item
		T selectedItem = getSelectionModel().getSelectedItem();

		// Clear selection
		getSelectionModel().clearSelection();

		// Set filtered items
		setItems(filteredItems);

		// Update list view
		refresh();

		// Get index of previously selected item
		int index = -1;
		if (selectedItem != null)
			index = filteredItems.indexOf(selectedItem);

		// Select item and make it visible
		if (index < 0)
		{
			if (!filteredItems.isEmpty())
				getSelectionModel().select(0);
		}
		else
		{
			getSelectionModel().select(index);
			scrollTo(index);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns the window that contains this list view.
	 *
	 * @return the window that contains this list view.
	 */

	private Window getWindow()
	{
		return SceneUtils.getWindow(this);
	}

	//------------------------------------------------------------------

	/**
	 * Updates the backgrounds of the cells of this list view.
	 */

	private void updateCellBackgrounds()
	{
		for (Cell cell : cells)
			cell.updateBackground();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: CONVERTER


	/**
	 * This interface defines the methods that return a graphical representation and a string representation of an item
	 * of type {@code T}.
	 */

	@FunctionalInterface
	public interface IConverter<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns a string representation of the specified item.
		 *
		 * @param  item
		 *           the item whose string representation is requried.
		 * @return a string representation of {@code item}.
		 */

		String getText(
			T	item);

		//--------------------------------------------------------------

		/**
		 * Returns a graphical representation of the specified item.  By default, this method returns {@code null}.
		 *
		 * @param  item
		 *           the item whose graphical representation is requried.
		 * @return a graphical representation of {@code item}.
		 */

		default Node getGraphic(
			T	item)
		{
			return null;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: CELL OF FILTERED LIST VIEW


	/**
	 * This class implements a cell of the enclosing instance of {@link FilteredListView}.
	 */

	private class Cell
		extends ListCell<T>
		implements CellPopUpManager.ICell<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/** The padding at the left and right of the label of a cell. */
		private static final	double	LABEL_HORIZONTAL_PADDING	= 6.0;

		/** The delay (in milliseconds) before a pop-up for a cell is displayed after it is activated. */
		private static final	int		POP_UP_DELAY	= 500;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a cell for the enclosing list view.
		 */

		private Cell()
		{
			// Set properties
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			if (truncateCells)
				setPrefWidth(0.0);
			setAlignment(Pos.CENTER_LEFT);
			setPadding(Insets.EMPTY);

			// Add cell to list
			cells.add(this);

			// Activate pop-up for cell; clear selection if cell is empty
			addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
			{
				// Activate pop-up for cell
				if (event.getButton() == MouseButton.PRIMARY)
					cellPopUpManager.activate(getIdentifier(), cells.iterator());

				// Clear selection if cell is empty
				if (isEmpty())
					getSelectionModel().clearSelection();
			});

			// When mouse leaves cell, deactivate any cell pop-up
			addEventHandler(MouseEvent.MOUSE_EXITED, event -> cellPopUpManager.deactivate());

			// When a mouse button is released, deactivate any cell pop-up
			addEventFilter(MouseEvent.MOUSE_RELEASED, event ->
			{
				if (cellPopUpManager.deactivate())
					event.consume();
			});
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : CellPopUpManager.ICell interface
	////////////////////////////////////////////////////////////////////

		/**
		 * {@inheritDoc}
		 */

		@Override
		public Node getPopUpContent()
		{
			// Create label
			Label label = createLabel();

			// Set properties of label
			if (label != null)
			{
				label.setBackground(SceneUtils.createColouredBackground(
							getColour(ListViewStyle.ColourKey.CELL_POPUP_BACKGROUND)));
				label.setBorder(SceneUtils.createSolidBorder(getColour(ListViewStyle.ColourKey.CELL_POPUP_BORDER)));
				label.getStyleClass().add(ListViewStyle.StyleClass.CELL_POPUP_LABEL);
			}

			// Return label
			return label;
		}

		//--------------------------------------------------------------

		/**
		 * {@inheritDoc}
		 */

		@Override
		public Point2D getPrefPopUpLocation(
			Node	content)
		{
			Node node = getGraphic();
			return (node == null) ? null
								  : PopUpUtils.createLocator(node, VHPos.CENTRE_LEFT, VHPos.CENTRE_LEFT, -1.0, 0.0)
												.getLocation(content.getLayoutBounds(), null);
		}

		//--------------------------------------------------------------

		/**
		 * {@inheritDoc}
		 */

		@Override
		public Window getWindow()
		{
			return FilteredListView.this.getWindow();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * {@inheritDoc}
		 */

		@Override
		protected void updateItem(
			T		item,
			boolean	empty)
		{
			// Call superclass method
			super.updateItem(item, empty);

			// Set background
			updateBackground();

			// Set graphic
			setGraphic(empty ? null : createLabel());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Updates the background of this cell.
		 */

		private void updateBackground()
		{
			if (StyleManager.INSTANCE.notUsingStyleSheet())
			{
				int index = getIndex();
				boolean selected = (getSelectionModel().getSelectedIndex() == index);
				boolean focused = getListView().isFocused();
				Color colour = isEmpty()
									? null
									: selected
											? focused
													? selectedFocusedBackgroundColour
													: selectedBackgroundColour
											: (index % 2 == 0)
													? getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_EVEN)
													: getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_ODD);
				if (!selected && focused && (getFocusModel().getFocusedIndex() == index))
				{
					setBackground(SceneUtils.createColouredBackground(focusedBackgroundColour, new Insets(0.0, 0.0, 1.0, 0.0),
																	  colour, new Insets(1.0, 1.0, 2.0, 1.0)));
				}
				else
					setBackground(SceneUtils.createColouredBackground(colour));
			}
		}

		//--------------------------------------------------------------

		/**
		 * Creates and returns a label for the item of this cell.
		 *
		 * @return a label for the item of this cell.
		 */

		private Label createLabel()
		{
			// Get item
			T item = getItem();

			// Create label and return it
			return (item == null) ? null : createCellLabel(item);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
