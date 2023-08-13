/*====================================================================*\

SimpleTextListView.java

Class: simple text-list view.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.listview;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.application.Platform;

import javafx.beans.InvalidationListener;

import javafx.collections.FXCollections;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.Node;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Region;

import javafx.scene.paint.Color;

import javafx.stage.Window;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IFunction1;

import uk.blankaspect.common.geometry.VHPos;

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
import uk.blankaspect.ui.jfx.style.StyleSelector;
import uk.blankaspect.ui.jfx.style.StyleUtils;

//----------------------------------------------------------------------


// CLASS: SIMPLE TEXT-LIST VIEW


/**
 * This class implements a simple JavaFX {@linkplain ListView list view} in which items are displayed as text.
 *
 * @param <T>
 *          the type of the items that are displayed in the list view.
 */

public class SimpleTextListView<T>
	extends ListView<T>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default padding at the top and bottom of the label of a cell. */
	private static final	double	DEFAULT_CELL_VERTICAL_PADDING	= 2.0;

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.UNIFORM_CELL_TEXT,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_TEXT_LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.UNIFORM_CELL_BACKGROUND,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_TEXT_LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_TEXT_LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.SELECTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ListViewStyle.ColourKey.CELL_BACKGROUND_EMPTY,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_TEXT_LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.EMPTY)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.UNIFORM_CELL_BORDER,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_TEXT_LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL)
						.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.SIMPLE_TEXT_LIST_VIEW)
									.desc(FxStyleClass.LIST_CELL)
									.build())
						.borders(Side.BOTTOM)
						.build(),
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.SIMPLE_TEXT_LIST_VIEW)
									.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.EMPTY)
									.build())
						.emptyBorder()
						.build()
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	SIMPLE_TEXT_LIST_VIEW	= StyleConstants.CLASS_PREFIX + "simple-text-list-view";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	UNIFORM_CELL_BACKGROUND	= "simpleTextListView.uniform.cell.background";
		String	UNIFORM_CELL_BORDER		= "simpleTextListView.uniform.cell.border";
		String	UNIFORM_CELL_TEXT		= "simpleTextListView.uniform.cell.text";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The function that produces a string representation of a list item. */
	private	IFunction1<String, T>	converter;

	/** The padding at the top and bottom of a cell. */
	private	double					cellVerticalPadding;

	/** Flag: if {@code true}, the cells of this list view have a uniform background colour. */
	private	boolean					uniformCells;

	/** Flag: if {@code true}, the labels of cells will be truncated, avoiding a horizontal scroll bar. */
	private	boolean					truncateCells;

	/** The manager of the pop-up windows that are displayed for the cells of this list view. */
	private	CellPopUpManager		cellPopUpManager;

	/** The cells of this list view. */
	private	List<Cell>				cells;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(SimpleTextListView.class, COLOUR_PROPERTIES, RULE_SETS,
									   ListViewStyle.class);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a simple text list view.
	 */

	public SimpleTextListView()
	{
		// Call alternative constructor
		this(null, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a simple text list view.
	 *
	 * @param converter
	 *          the function that will produce a string representation of a list item.
	 */

	public SimpleTextListView(
		IFunction1<String, T>	converter)
	{
		// Call alternative constructor
		this(null, converter);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a simple text list view in which the specified items are displayed.
	 *
	 * @param items
	 *          the items that will be displayed in the list view; ignored if {@code null}.
	 * @param converter
	 *          the function that will produce a string representation of a list item.
	 */

	public SimpleTextListView(
		Collection<? extends T>	items,
		IFunction1<String, T>	converter)
	{
		// Initialise instance variables
		cellVerticalPadding = DEFAULT_CELL_VERTICAL_PADDING;
		cellPopUpManager = new CellPopUpManager(Cell.POP_UP_DELAY);
		cells = new ArrayList<>();

		// Set properties
		setCellFactory(listView -> new Cell());
		setConverter(converter);
		getStyleClass().addAll(StyleClass.SIMPLE_TEXT_LIST_VIEW, ListViewStyle.StyleClass.LIST_VIEW);

		// Update cell backgrounds on change of state
		if (StyleManager.INSTANCE.notUsingStyleSheet())
		{
			// Update cell backgrounds when selection changes
			getSelectionModel().getSelectedIndices().addListener((InvalidationListener) observable ->
					updateCellBackgrounds());

			// Update cell backgrounds when focus changes
			focusedProperty().addListener(observable -> updateCellBackgrounds());

			// Update cell backgrounds when focused row changes
			getFocusModel().focusedIndexProperty().addListener(observable -> updateCellBackgrounds());
		}

		// Ensure cells are redrawn if scroll bar is hidden
		widthProperty().addListener(observable -> Platform.runLater(() -> refresh()));

		// Set background of empty cells
		if (StyleManager.INSTANCE.notUsingStyleSheet())
		{
			skinProperty().addListener((observable, oldSkin, skin) ->
			{
				if (skin != null)
				{
					Node node = lookup(StyleSelector.VIRTUAL_FLOW);
					if (node instanceof Region region)
					{
						region.setBackground(SceneUtils.createColouredBackground(
								getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_EMPTY)));
					}
				}
			});
		}

		// Set items
		if (items != null)
			setItems(FXCollections.observableArrayList(items));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

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
		Color colour = StyleManager.INSTANCE.getColour(key);
		return (colour == null) ? StyleManager.DEFAULT_COLOUR : colour;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the function that produces a string representation of a list item.
	 *
	 * @param converter
	 *          the function that will be applied to a list item to convert it to a string.  If {@code null}, a list
	 *          item will be converted to a string by calling its {@link Object#toString() toString()} method.
	 */

	public void setConverter(
		IFunction1<String, T>	converter)
	{
		this.converter = (converter == null) ? item -> (item == null) ? "" : item.toString()
											 : converter;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the padding at the top and bottom of the cells of this list view to the specified value.
	 *
	 * @param padding
	 *          the value to which the padding at the top and bottom of the cells of this list view will be set.
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
	 * Sets the <i>uniform cells</i> flag to the specified value.
	 *
	 * @param uniform
	 *          the value to which the <i>uniform cells</i> flag will be set.
	 */

	public void setUniformCells(
		boolean	uniform)
	{
		// Update instance variable
		uniformCells = uniform;

		// Add or remove base style class
		StyleUtils.addRemoveStyleClass(this, ListViewStyle.StyleClass.LIST_VIEW, !uniform);

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
	 * Creates and returns a new instance of a label of a cell to represent the specified item.
	 *
	 * @param  item
	 *           the item for which a label will be created.
	 * @return a label of a cell to represent {@code item}.
	 */

	protected Label createCellLabel(
		T	item)
	{
		Label label = new Label(converter.invoke(item));
		label.setPadding(new Insets(cellVerticalPadding, Cell.LABEL_HORIZONTAL_PADDING,
									cellVerticalPadding, Cell.LABEL_HORIZONTAL_PADDING));
		label.setTextFill(getColour(uniformCells ? ColourKey.UNIFORM_CELL_TEXT : ListViewStyle.ColourKey.CELL_TEXT));
		label.getStyleClass().add(ListViewStyle.StyleClass.CELL_LABEL);
		return label;
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
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: CELL OF LIST VIEW


	/**
	 * This class implements a cell of the enclosing list view.
	 */

	private class Cell
		extends ListCell<T>
		implements CellPopUpManager.ICell<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
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
			return SimpleTextListView.this.getWindow();
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

			// Set border
			Color colour = empty
								? Color.TRANSPARENT
								: uniformCells
										? getColour(ColourKey.UNIFORM_CELL_BORDER)
										: getColour(ListViewStyle.ColourKey.CELL_BORDER);
			setBorder(SceneUtils.createSolidBorder(colour, Side.BOTTOM));

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
				if (uniformCells)
				{
					Color colour = isEmpty() ? null : getColour(ColourKey.UNIFORM_CELL_BACKGROUND);
					setBackground(SceneUtils.createColouredBackground(colour));
				}
				else
				{
					int index = getIndex();
					boolean selected = getSelectionModel().getSelectedIndices().contains(index);
					boolean focused = getListView().isFocused();
					Color colour = isEmpty()
										? null
										: selected
												? focused
														? getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_SELECTED_FOCUSED)
														: getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_SELECTED)
												: (index % 2 == 0)
														? getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_EVEN)
														: getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_ODD);
					if (!selected && focused && (getFocusModel().getFocusedIndex() == index))
					{
						setBackground(SceneUtils.createColouredBackground(
								getColour(ListViewStyle.ColourKey.CELL_BACKGROUND_FOCUSED), new Insets(0.0, 0.0, 1.0, 0.0),
								colour, new Insets(1.0, 1.0, 2.0, 1.0)));
					}
					else
						setBackground(SceneUtils.createColouredBackground(colour));
				}
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
