/*====================================================================*\

SimpleTableView.java

Class: simple table view.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.tableview;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.application.Platform;

import javafx.beans.InvalidationListener;

import javafx.beans.property.ReadOnlyObjectWrapper;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;

import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Region;

import javafx.scene.paint.Color;

import javafx.stage.Window;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.ui.jfx.math.FxGeomUtils;

import uk.blankaspect.ui.jfx.popup.CellPopUpManager;
import uk.blankaspect.ui.jfx.popup.LabelPopUpManager;
import uk.blankaspect.ui.jfx.popup.PopUpUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;
import uk.blankaspect.ui.jfx.style.StyleSelector;

//----------------------------------------------------------------------


// CLASS: SIMPLE TABLE VIEW


public class SimpleTableView<S>
	extends TableView<S>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The extra width of the table to allow for the vertical scroll bar. */
	private static final	double	EXTRA_WIDTH	= 17.0;

	/** The padding around the label of a header cell. */
	private static final	Insets	HEADER_CELL_LABEL_PADDING	= new Insets(3.0, 4.0, 3.0, 4.0);

	/** The delay (in milliseconds) before a pop-up for a header cell is displayed after it is activated. */
	private static final	int		HEADER_CELL_POP_UP_DELAY	= 1000;

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			TableViewStyle.ColourKey.HEADER_CELL_BACKGROUND,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_TABLE_VIEW)
						.desc(FxStyleClass.COLUMN_HEADER)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_TABLE_VIEW)
						.desc(FxStyleClass.FILLER)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			TableViewStyle.ColourKey.HEADER_CELL_BORDER,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_TABLE_VIEW)
						.desc(FxStyleClass.COLUMN_HEADER)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_TABLE_VIEW)
						.desc(FxStyleClass.FILLER)
						.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.SIMPLE_TABLE_VIEW)
									.desc(FxStyleClass.COLUMN_HEADER)
									.build())
						.borders(Side.RIGHT, Side.BOTTOM)
						.build(),
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.SIMPLE_TABLE_VIEW)
									.desc(FxStyleClass.FILLER)
									.build())
						.borders(Side.BOTTOM)
						.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	SIMPLE_TABLE_VIEW	= StyleConstants.CLASS_PREFIX + "simple-table-view";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The manager of the pop-up windows that are displayed for the cells of this table view. */
	private	CellPopUpManager	cellPopUpManager;

	/** A list of the items that are represented in this table view. */
	private	ElasticList<S>		itemList;

	/** A list of the columns of this table view. */
	private	List<IColumn<S, ?>>	columns;

	/** A list of the cells of this table view. */
	private	List<Cell<?>>		cells;

	/** The manager of pop-ups for the header cells of this table view. */
	private	LabelPopUpManager	headerPopUpManager;

	/** Flag: if {@code true}, the header of this table view has been initialised. */
	private	boolean				headerInitialised;

	// WORKAROUND for a bug in JavaFX: isFocused() sometimes returns false when the table view has focus
	/** Flag: if {@code true}, this table view has keyboard focus. */
	private	boolean				focused;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(SimpleTableView.class, COLOUR_PROPERTIES, RULE_SETS,
									   TableViewStyle.class);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SimpleTableView(
		Collection<? extends IColumn<S, ?>>	columns)
	{
		// Initialise instance variables
		cellPopUpManager = new CellPopUpManager(Cell.POP_UP_DELAY);
		itemList = new ElasticList<>(this);
		this.columns = new ArrayList<>(columns);
		cells = new ArrayList<>();

		// Create columns
		double width = EXTRA_WIDTH;
		for (IColumn<S, ?> column : columns)
		{
			// Get preferred width of column
			double columnWidth = column.getPrefWidth() + Cell.LABEL_PADDING.getLeft() + Cell.LABEL_PADDING.getRight() + 1.0;

			// Create table column
			TableColumn<S, ?> tableColumn = column.createColumn(this);

			// Set properties of table column
			tableColumn.setId(column.getId());
			tableColumn.setPrefWidth(columnWidth);
			tableColumn.setCellFactory(column0 -> new Cell(column));

			// Add column to list
			getColumns().add(tableColumn);

			// Increment width
			width += columnWidth;
		}

		// Set properties
		setPrefWidth(width);
		getStyleClass().addAll(TableViewStyle.StyleClass.TABLE_VIEW, StyleClass.SIMPLE_TABLE_VIEW);

		// Set row factory
		if (StyleManager.INSTANCE.notUsingStyleSheet())
		{
			setRowFactory(table ->
			{
				TableRow<S> row = new TableRow<>();
				row.setBackground(SceneUtils.createColouredBackground(
						getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_EMPTY)));
				return row;
			});
		}

		// Update cell backgrounds on change of state
		if (StyleManager.INSTANCE.notUsingStyleSheet())
		{
			// Update cell backgrounds when selection changes
			getSelectionModel().getSelectedIndices().addListener((InvalidationListener) observable ->
					updateCellBackgrounds());

			// Update cell backgrounds when focus changes
// WORKAROUND : see 'focused' instance variable
//			focusedProperty().addListener(observable -> updateCellBackgrounds());
			focusedProperty().addListener((observable, oldFocused, newFocused) ->
			{
				focused = newFocused;
				updateCellBackgrounds();
			});

			// Update cell backgrounds when focused row changes
			getFocusModel().focusedIndexProperty().addListener(observable -> updateCellBackgrounds());
		}

		// Ensure cells are redrawn if scroll bar is hidden
		widthProperty().addListener(observable -> Platform.runLater(() -> refresh()));
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
		return StyleManager.INSTANCE.getColourOrDefault(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected void layoutChildren()
	{
		// Call superclass method
		super.layoutChildren();

		// Set background and border of column headers and filler to the right of column headers
		if (StyleManager.INSTANCE.notUsingStyleSheet())
		{
			// Get colours
			Color backgroundColour = getColour(TableViewStyle.ColourKey.HEADER_CELL_BACKGROUND);
			Color borderColour = getColour(TableViewStyle.ColourKey.HEADER_CELL_BORDER);

			// Set background and border of column headers
			for (Node node : lookupAll(StyleSelector.COLUMN_HEADER))
			{
				if (node instanceof Region header)
				{
					header.setBackground(SceneUtils.createColouredBackground(backgroundColour));
					header.setBorder(SceneUtils.createSolidBorder(borderColour, Side.RIGHT, Side.BOTTOM));
				}
			}

			// Set background and border of filler
			Node node = lookup(StyleSelector.FILLER);
			if (node instanceof Region filler)
			{
				filler.setBackground(SceneUtils.createColouredBackground(backgroundColour));
				filler.setBorder(SceneUtils.createSolidBorder(borderColour, Side.BOTTOM));
			}
		}

		// Set alignment and padding of header labels
		if (!headerInitialised)
		{
			for (Node node : lookupAll(StyleSelector.COLUMN_HEADER_LABEL))
			{
				if (node instanceof Labeled label)
				{
					// Get column
					Parent columnHeader = node.getParent();
					String id = columnHeader.getId();
					IColumn<S, ?> column = columns.stream()
													.filter(column0 -> column0.getId().equals(id))
													.findFirst()
													.orElse(null);

					// Get alignment of column and insets of column header
					HPos hAlignment = (column == null) ? IColumn.DEFAULT_H_ALIGNMENT : column.getHAlignment();
					Insets insets = (columnHeader instanceof Region region) ? region.getInsets() : Insets.EMPTY;

					// Set properties of label
					label.setAlignment(FxGeomUtils.getPos(VPos.CENTER, hAlignment));
					label.setPadding(HEADER_CELL_LABEL_PADDING);
					label.setTextFill(getColour(TableViewStyle.ColourKey.CELL_TEXT));

					// Create pop-up for label
					if (column != null)
					{
						// Create pop-up manager
						if (headerPopUpManager == null)
						{
							headerPopUpManager = new LabelPopUpManager((text, graphic) ->
							{
								Label popUpLabel = new Label(text, graphic);
								popUpLabel.setPadding(Cell.LABEL_PADDING);
								popUpLabel.setBackground(SceneUtils.createColouredBackground(
										getColour(TableViewStyle.ColourKey.CELL_POPUP_BACKGROUND)));
								popUpLabel.setBorder(SceneUtils.createSolidBorder(
										getColour(TableViewStyle.ColourKey.CELL_POPUP_BORDER)));
								popUpLabel.getStyleClass().add(TableViewStyle.StyleClass.CELL_POPUP_LABEL);
								return popUpLabel;
							});
							headerPopUpManager.setDelay(HEADER_CELL_POP_UP_DELAY);
						}

						// Create pop-up for label
						VHPos.H hPos = null;
						double x = 0.0;
						switch (hAlignment)
						{
							case LEFT:
								if (insets != null)
									x = -(insets.getLeft() + 1.0);
								hPos = VHPos.H.LEFT;
								break;

							case CENTER:
								hPos = VHPos.H.CENTRE;
								break;

							case RIGHT:
								if (insets != null)
									x = insets.getRight();
								hPos = VHPos.H.RIGHT;
								break;
						}
						PopUpUtils.createPopUp(headerPopUpManager, label, VHPos.of(VHPos.V.TOP, hPos),
											   VHPos.of(VHPos.V.BOTTOM, hPos), x, 0.0, () -> column.getLongTitle(),
											   null);
					}
				}
			}

			// Prevent reinitialisation of header
			headerInitialised = true;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void setItems(
		Collection<? extends S>	items)
	{
		itemList.update(items);
	}

	//------------------------------------------------------------------

	private Window getWindow()
	{
		return SceneUtils.getWindow(this);
	}

	//------------------------------------------------------------------

	private void updateCellBackgrounds()
	{
		for (Cell<?> cell : cells)
			cell.updateBackground();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: COLUMN


	public interface IColumn<S, T>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The default gap between the text and graphic of the label of a cell. */
		double	DEFAULT_GRAPHIC_TEXT_GAP	= 6.0;

		/** The default horizontal alignment a cell. */
		HPos	DEFAULT_H_ALIGNMENT	= HPos.LEFT;

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		String getId();

		//--------------------------------------------------------------

		String getTitle();

		//--------------------------------------------------------------

		default String getLongTitle()
		{
			return getTitle();
		}

		//--------------------------------------------------------------

		default double getGraphicTextGap()
		{
			return DEFAULT_GRAPHIC_TEXT_GAP;
		}

		//--------------------------------------------------------------

		default HPos getHAlignment()
		{
			return DEFAULT_H_ALIGNMENT;
		}

		//--------------------------------------------------------------

		double getPrefWidth();

		//--------------------------------------------------------------

		default TableColumn<S, T> createColumn(
			SimpleTableView<S>	tableView)
		{
			TableColumn<S, T> column = new TableColumn<>(getTitle());
			column.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(getValue(features.getValue())));
			return column;
		}

		//--------------------------------------------------------------

		T getValue(
			S	item);

		//--------------------------------------------------------------

		String getText(
			T	value);

		//--------------------------------------------------------------

		default Node getGraphic(
			T	value)
		{
			return null;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: CELL


	private class Cell<T>
		extends TableCell<S, T>
		implements CellPopUpManager.ICell<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The padding around the label of a cell. */
		private static final	Insets	LABEL_PADDING	= new Insets(2.0, 6.0, 2.0, 6.0);

		/** The delay (in milliseconds) before a pop-up for a cell is displayed after it is activated. */
		private static final	int		POP_UP_DELAY	= 500;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	IColumn<S, T>	column;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Cell(
			IColumn<S, T>	column)
		{
			// Initialise instance variables
			this.column = column;

			// Set properties
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			setAlignment(FxGeomUtils.getPos(VPos.CENTER, column.getHAlignment()));
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
		public String getIdentifier()
		{
			return (getItem() == null) ? null : getIndex() + ":" + column.getId();
		}

		//--------------------------------------------------------------

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
						getColour(TableViewStyle.ColourKey.CELL_POPUP_BACKGROUND)));
				label.setBorder(SceneUtils.createSolidBorder(getColour(TableViewStyle.ColourKey.CELL_POPUP_BORDER)));
				label.getStyleClass().add(TableViewStyle.StyleClass.CELL_POPUP_LABEL);
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
			VHPos pos = null;
			double x = 0.0;
			switch (getAlignment().getHpos())
			{
				case LEFT:
					pos = VHPos.CENTRE_LEFT;
					x = -1.0;
					break;

				case CENTER:
					pos = VHPos.CENTRE_CENTRE;
					break;

				case RIGHT:
					pos = VHPos.CENTRE_RIGHT;
					x = 1.0;
					break;
			}

			Node node = getGraphic();
			return (node == null) ? null
								  : PopUpUtils.createLocator(node, pos, pos, x, 0.0)
												.getLocation(content.getLayoutBounds(), null);
		}

		//--------------------------------------------------------------

		/**
		 * {@inheritDoc}
		 */

		@Override
		public Window getWindow()
		{
			return SimpleTableView.this.getWindow();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected void updateItem(
			T		item,
			boolean	empty)
		{
			// Call superclass method
			super.updateItem(item, empty);

			// Update background
			updateBackground();

			// Set border
			setBorder(empty ? null
							: SceneUtils.createSolidBorder(
										getColour(TableViewStyle.ColourKey.CELL_BORDER), Side.RIGHT, Side.BOTTOM));

			// Set graphic
			setGraphic(empty ? null : createLabel());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void updateBackground()
		{
			if (StyleManager.INSTANCE.notUsingStyleSheet())
			{
				int index = getIndex();
				boolean selected = getSelectionModel().getSelectedIndices().contains(index);
// WORKAROUND
//				boolean focused = getTableView().isFocused();
				Color colour = isEmpty()
									? null
									: selected
											? focused
													? getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_SELECTED_FOCUSED)
													: getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_SELECTED)
											: (index % 2 == 0)
													? getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_EVEN)
													: getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_ODD);
				if (!selected && focused && (getFocusModel().getFocusedIndex() == index))
				{
					setBackground(SceneUtils.createColouredBackground(
							getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_FOCUSED), new Insets(0.0, 1.0, 1.0, 0.0),
							colour, new Insets(1.0, 1.0, 2.0, 0.0)));
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
			// Create label
			Label label = null;
			T item = getItem();
			if (item != null)
			{
				// Get alignment of cell
				Pos alignment = getAlignment();

				// Create label
				label = new Label(column.getText(item), column.getGraphic(item));
				label.setGraphicTextGap(column.getGraphicTextGap());
				label.setAlignment(alignment);
				if (alignment.getHpos() == HPos.RIGHT)
					label.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
				label.setPadding(LABEL_PADDING);
				label.setTextFill(getColour(TableViewStyle.ColourKey.CELL_TEXT));
				label.getStyleClass().add(TableViewStyle.StyleClass.CELL_LABEL);
			}

			// Return label
			return label;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
