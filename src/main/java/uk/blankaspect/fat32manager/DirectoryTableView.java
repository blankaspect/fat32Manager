/*====================================================================*\

DirectoryTableView.java

Class: directory table view.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;

import javafx.beans.InvalidationListener;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleSetProperty;

import javafx.collections.FXCollections;

import javafx.concurrent.Task;

import javafx.css.PseudoClass;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import javafx.scene.image.Image;

import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

import javafx.stage.Window;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.function.IFunction0;
import uk.blankaspect.common.function.IFunction2;
import uk.blankaspect.common.function.IProcedure0;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.common.logging.Logger;

import uk.blankaspect.common.message.MessageConstants;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.task.ITaskStatus;

import uk.blankaspect.common.text.Tabulator;

import uk.blankaspect.driveio.VolumeException;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.clipboard.ClipboardUtils;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

import uk.blankaspect.ui.jfx.container.LabelTitledPane;

import uk.blankaspect.ui.jfx.dialog.NotificationDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleProgressDialog;

import uk.blankaspect.ui.jfx.font.FontUtils;

import uk.blankaspect.ui.jfx.image.HatchedImageFactory;
import uk.blankaspect.ui.jfx.image.MessageIcon32;

import uk.blankaspect.ui.jfx.label.Labels;

import uk.blankaspect.ui.jfx.math.FxGeomUtils;

import uk.blankaspect.ui.jfx.popup.CellPopUpManager;
import uk.blankaspect.ui.jfx.popup.LabelPopUpManager;
import uk.blankaspect.ui.jfx.popup.PopUpUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.DataUriImageMap;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;
import uk.blankaspect.ui.jfx.style.StyleSelector;

import uk.blankaspect.ui.jfx.tableview.ElasticList;
import uk.blankaspect.ui.jfx.tableview.TableViewStyle;

import uk.blankaspect.ui.jfx.text.Text2;
import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: DIRECTORY TABLE VIEW


public class DirectoryTableView
	extends TableView<Fat32Directory.Entry>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default delay (in milliseconds) before a pop-up for a header cell is displayed after it is activated. */
	public static final		int		DEFAULT_HEADER_CELL_POP_UP_DELAY	= 1500;

	/** The extra width of the table to allow for the vertical scroll bar. */
	private static final	double	EXTRA_WIDTH	= 17.0;

	/** The padding around the label of a header cell. */
	private static final	Insets	HEADER_CELL_LABEL_PADDING	= new Insets(3.0, 4.0, 3.0, 4.0);

	/** The width of the header image. */
	private static final	int		HEADER_IMAGE_WIDTH	= 120;

	/** The height of the header image. */
	private static final	int		HEADER_IMAGE_HEIGHT	= 60;

	/** The spacing between adjacent hatching lines of the header image. */
	private static final	double	HEADER_IMAGE_SPACING	= 3.0;

	/** The factor by which the size of the default font is multiplied to give the size of the font of the placeholder
		label. */
	private static final	double	PLACEHOLDER_LABEL_FONT_SIZE_FACTOR	= 1.25;

	/** The modes that determine the background colour of header cells. */
	public enum HeaderMode
	{
		NONE,
		DELETED,
		PREVIEW
	}

	/** Miscellaneous strings. */
	private static final	String	ELLIPSIS_STR			= "...";
	private static final	String	NO_ENTRIES_STR			= "No entries";
	private static final	String	VIEW_SECTOR_STR			= "View sector";
	private static final	String	VIEW_CLUSTER_STR		= "View cluster";
	private static final	String	COPY_ENTRY_STR			= "Copy entry";
	private static final	String	COPY_ALL_ENTRIES_STR	= "Copy all entries";
	private static final	String	DEFRAGMENT_FILE_STR		= "Defragment file";
	private static final	String	NOT_FRAGMENTED_STR		= "The file was not fragmented.";
	private static final	String	NOT_ENOUGH_SPACE_STR	= "There was not enough space to defragment the file.";
	private static final	String	DEFRAGMENTED_STR		= "The file was defragmented.";
	private static final	String	INVALID_CLUSTERS_STR	= "Invalid clusters";

	/** The pseudo-class that is associated with the <i>deleted</i> state. */
	private static final	PseudoClass	DELETED_PSEUDO_CLASS	= PseudoClass.getPseudoClass(PseudoClassKey.DELETED);

	/** The pseudo-class that is associated with the <i>preview</i> state. */
	private static final	PseudoClass	PREVIEW_PSEUDO_CLASS	= PseudoClass.getPseudoClass(PseudoClassKey.PREVIEW);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.HEADER_CELL_BORDER,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_TABLE_VIEW)
					.desc(FxStyleClass.COLUMN_HEADER)
					.build(),
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_TABLE_VIEW)
					.desc(FxStyleClass.FILLER)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.CELL_ATTRIBUTE_TEXT,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_TABLE_VIEW)
					.desc(TableViewStyle.StyleClass.CELL_LABEL)
					.desc(StyleClass.ATTRIBUTE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			TableViewStyle.ColourKey.CELL_BACKGROUND_EMPTY,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_TABLE_VIEW)
					.desc(StyleClass.PLACEHOLDER_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.PLACEHOLDER_TEXT,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_TABLE_VIEW)
					.desc(StyleClass.PLACEHOLDER_LABEL)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	DIRECTORY_TABLE_VIEW	= StyleConstants.APP_CLASS_PREFIX + "directory-table-view";

		String	ATTRIBUTE			= StyleConstants.CLASS_PREFIX + "attribute";
		String	PLACEHOLDER_LABEL	= StyleConstants.CLASS_PREFIX + "placeholder-label";
	}

	/** Keys of CSS pseudo-classes. */
	private interface PseudoClassKey
	{
		String	DELETED	= "deleted";
		String	PREVIEW	= "preview";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	CELL_ATTRIBUTE_TEXT				= PREFIX + "cell.attribute.text";
		String	HEADER_CELL_BACKGROUND1			= PREFIX + "header.cell.background1";
		String	HEADER_CELL_BACKGROUND2			= PREFIX + "header.cell.background2";
		String	HEADER_CELL_BACKGROUND1_DELETED	= PREFIX + "header.cell.background1.deleted";
		String	HEADER_CELL_BACKGROUND2_DELETED	= PREFIX + "header.cell.background2.deleted";
		String	HEADER_CELL_BACKGROUND1_PREVIEW	= PREFIX + "header.cell.background1.preview";
		String	HEADER_CELL_BACKGROUND2_PREVIEW	= PREFIX + "header.cell.background2.preview";
		String	HEADER_CELL_BORDER				= PREFIX + "header.cell.border";
		String	PLACEHOLDER_TEXT				= PREFIX + "placeholder.text";
	}

	/** Keys of images that are used in CSS rule sets. */
	private interface ImageKey
	{
		String	HEADER_CELL_BACKGROUND			= ColourKey.HEADER_CELL_BACKGROUND1;
		String	HEADER_CELL_BACKGROUND_DELETED	= ColourKey.HEADER_CELL_BACKGROUND1_DELETED;
		String	HEADER_CELL_BACKGROUND_PREVIEW	= ColourKey.HEADER_CELL_BACKGROUND1_PREVIEW;
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** The delay (in milliseconds) before a pop-up for a header cell is displayed after it is activated. */
	private static	int	headerCellPopUpDelay	= DEFAULT_HEADER_CELL_POP_UP_DELAY;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The manager of the pop-up windows that are displayed for the cells of this table view. */
	private		CellPopUpManager					cellPopUpManager;

	/** A list of the items that are represented in this table view. */
	protected	ElasticList<Fat32Directory.Entry>	itemList;

	/** The directory that is represented in this table view. */
	private		Fat32Directory						directory;

	/** A list of the columns of this table view. */
	protected	List<Column>						columns;

	/** A list of the cells of this table view. */
	private		List<Cell<?>>						cells;

	/** The maximum width of an attribute ID character. */
	private		double								attrIdCharMaxWidth;

	/** Flag: if {@code true}, the entries for special directories are displayed. */
	private		boolean								showSpecialDirectories;

	/** The mode that determines the background colour of the header cells of this table view. */
	private		HeaderMode							headerMode;

	/** The manager of pop-ups for the header cells of this table view. */
	private		LabelPopUpManager					headerPopUpManager;

	/** Flag: if {@code true}, the header of this table view has been initialised. */
	private		boolean								headerInitialised;

	// WORKAROUND for a bug in JavaFX: isFocused() sometimes returns false when the table view has focus
	/** Flag: if {@code true}, this table view has keyboard focus. */
	private		boolean								focused;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Load default colours of this class
		try
		{
			StyleManager.INSTANCE.loadDefaultColours(DirectoryTableView.class);
		}
		catch (LocationException e)
		{
			Logger.INSTANCE.error(e);
		}
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected DirectoryTableView()
	{
		// Initialise instance variables
		cellPopUpManager = new CellPopUpManager(Cell.POP_UP_DELAY);
		itemList = new ElasticList<>(this);
		cells = new ArrayList<>();
		headerMode = HeaderMode.NONE;

		// Set properties
		getStyleClass().addAll(StyleClass.DIRECTORY_TABLE_VIEW, TableViewStyle.StyleClass.TABLE_VIEW);

		// Create procedure to create rule sets
		IFunction0<List<CssRuleSet>> createRuleSets = () ->
		{
			// Create hatched images and add them to data-scheme URI map
			try
			{
				// Create function to create hatched image
				IFunction2<Image, String, String> createImage = (colour1Key, colour2Key) ->
				{
					return HatchedImageFactory.diagonal(HEADER_IMAGE_WIDTH, HEADER_IMAGE_HEIGHT, HEADER_IMAGE_SPACING,
														getColour(colour1Key), getColour(colour2Key));
				};

				// Create hatched images and add them to data-scheme URI map
				DataUriImageMap.INSTANCE.put(ImageKey.HEADER_CELL_BACKGROUND,
											 createImage.invoke(ColourKey.HEADER_CELL_BACKGROUND1,
																ColourKey.HEADER_CELL_BACKGROUND2));
				DataUriImageMap.INSTANCE.put(ImageKey.HEADER_CELL_BACKGROUND_DELETED,
											 createImage.invoke(ColourKey.HEADER_CELL_BACKGROUND1_DELETED,
																ColourKey.HEADER_CELL_BACKGROUND2_DELETED));
				DataUriImageMap.INSTANCE.put(ImageKey.HEADER_CELL_BACKGROUND_PREVIEW,
											 createImage.invoke(ColourKey.HEADER_CELL_BACKGROUND1_PREVIEW,
																ColourKey.HEADER_CELL_BACKGROUND2_PREVIEW));
			}
			catch (BaseException e)
			{
				Logger.INSTANCE.error(e);
			}

			// Create and return CSS rule sets
			return List.of
			(
				RuleSetBuilder.create()
						.selector(CssSelector.builder()
								.cls(StyleClass.DIRECTORY_TABLE_VIEW)
								.desc(FxStyleClass.COLUMN_HEADER)
								.build())
						.selector(CssSelector.builder()
								.cls(StyleClass.DIRECTORY_TABLE_VIEW)
								.desc(FxStyleClass.FILLER)
								.build())
						.repeatingImageBackground(ImageKey.HEADER_CELL_BACKGROUND)
						.build(),
				RuleSetBuilder.create()
						.selector(CssSelector.builder()
								.cls(StyleClass.DIRECTORY_TABLE_VIEW)
								.desc(FxStyleClass.COLUMN_HEADER).pseudo(PseudoClassKey.DELETED)
								.build())
						.selector(CssSelector.builder()
								.cls(StyleClass.DIRECTORY_TABLE_VIEW)
								.desc(FxStyleClass.FILLER).pseudo(PseudoClassKey.DELETED)
								.build())
						.repeatingImageBackground(ImageKey.HEADER_CELL_BACKGROUND_DELETED)
						.build(),
				RuleSetBuilder.create()
						.selector(CssSelector.builder()
								.cls(StyleClass.DIRECTORY_TABLE_VIEW)
								.desc(FxStyleClass.COLUMN_HEADER).pseudo(PseudoClassKey.PREVIEW)
								.build())
						.selector(CssSelector.builder()
								.cls(StyleClass.DIRECTORY_TABLE_VIEW)
								.desc(FxStyleClass.FILLER).pseudo(PseudoClassKey.PREVIEW)
								.build())
						.repeatingImageBackground(ImageKey.HEADER_CELL_BACKGROUND_PREVIEW)
						.build(),
				RuleSetBuilder.create()
						.selector(CssSelector.builder()
								.cls(StyleClass.DIRECTORY_TABLE_VIEW)
								.desc(FxStyleClass.COLUMN_HEADER)
								.build())
						.borders(Side.RIGHT, Side.BOTTOM)
						.build(),
				RuleSetBuilder.create()
						.selector(CssSelector.builder()
								.cls(StyleClass.DIRECTORY_TABLE_VIEW)
								.desc(FxStyleClass.FILLER)
								.build())
						.borders(Side.BOTTOM)
						.build()
			);
		};

		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(getClass(), COLOUR_PROPERTIES, createRuleSets.invoke(), TableViewStyle.class);

		// Update rule sets when theme changes
		StyleManager.INSTANCE.themeProperty().addListener(observable ->
		{
			// Update rule sets
			StyleManager.INSTANCE.updateRuleSets(getClass(), createRuleSets.invoke());

			// Redraw cells
			refresh();
		});

		// Create columns
		setColumns(EnumSet.allOf(Column.class), true);

		// Set row factory
		if (StyleManager.INSTANCE.notUsingStyleSheet())
		{
			setRowFactory(table ->
			{
				TableRow<Fat32Directory.Entry> row = new TableRow<>();
				row.setBackground(SceneUtils.createColouredBackground(
						getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_EMPTY)));
				return row;
			});
		}

		// Set placeholder
		Label placeholderLabel = Labels.expansive(NO_ENTRIES_STR, PLACEHOLDER_LABEL_FONT_SIZE_FACTOR,
												  getColour(ColourKey.PLACEHOLDER_TEXT),
												  getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_EMPTY));
		placeholderLabel.getStyleClass().add(StyleClass.PLACEHOLDER_LABEL);
		setPlaceholder(placeholderLabel);

		// Update cell backgrounds on change of state
		if (StyleManager.INSTANCE.notUsingStyleSheet())
		{
			// Update cell backgrounds when selection changes
			getSelectionModel().selectedIndexProperty().addListener(observable -> updateCellBackgrounds());

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
		widthProperty().addListener(observable -> Platform.runLater(this::refresh));

		// Display context menu in response to request
		addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event ->
		{
			// Get directory
			Fat32Directory directory = getDirectory();

			// Get selected entry
			Fat32Directory.Entry entry = getSelectionModel().getSelectedItem();

			// Get volume
			Fat32Volume volume = directory.getVolume();

			// Create context menu
			ContextMenu menu = new ContextMenu();

			// Add menu items
			int index = 0;
			addContextMenuItems(menu, index++, directory, entry);

			// Add menu items: view sector, view cluster
			if ((entry != null) && (entry.getClusterIndex() > 0))
			{
				// Add separator
				if (!menu.getItems().isEmpty())
					menu.getItems().add(new SeparatorMenuItem());

				// Add menu item: view sector
				MenuItem menuItem = new MenuItem(VIEW_SECTOR_STR, Images.icon(Images.ImageId.VIEW_SECTOR));
				menuItem.setDisable(entry.getSectorIndex() > Integer.MAX_VALUE);
				menuItem.setOnAction(event0 ->
				{
					try
					{
						Fat32Fat.IndexFinder indexFinder =
								(headerMode == HeaderMode.DELETED)
												? volume.getFat().indexFinder(entry.getClusterIndex(), -1)
												: volume.getFat().indexFinder(entry);
						SectorClusterViewDialog.showSector(getWindow(), volume, indexFinder);
					}
					catch (VolumeException e)
					{
						Utils.showErrorMessage(getWindow(), VIEW_SECTOR_STR, e);
					}
				});
				menu.getItems().add(menuItem);

				// Add menu item: view cluster
				menuItem = new MenuItem(VIEW_CLUSTER_STR, Images.icon(Images.ImageId.VIEW_CLUSTER));
				menuItem.setOnAction(event0 ->
				{
					try
					{
						Fat32Fat.IndexFinder indexFinder =
								(headerMode == HeaderMode.DELETED)
												? volume.getFat().indexFinder(entry.getClusterIndex(), -1)
												: volume.getFat().indexFinder(entry);
						SectorClusterViewDialog.showCluster(getWindow(), volume, indexFinder);
					}
					catch (VolumeException e)
					{
						Utils.showErrorMessage(getWindow(), VIEW_CLUSTER_STR, e);
					}
				});
				menu.getItems().add(menuItem);
			}

			// Add menu items
			addContextMenuItems(menu, index++, directory, entry);

			// Add menu item: copy entry
			boolean hasCopy = false;
			if ((entry != null) && !entry.isSpecialDirectory())
			{
				// Add separator
				if (!menu.getItems().isEmpty())
					menu.getItems().add(new SeparatorMenuItem());

				// Add menu item
				MenuItem menuItem = new MenuItem(COPY_ENTRY_STR, Images.icon(Images.ImageId.COPY));
				menuItem.setOnAction(event0 -> Utils.copyToClipboard(getWindow(), COPY_ENTRY_STR, entry.toString()));
				menu.getItems().add(menuItem);
				hasCopy = true;
			}

			// Add menu item: copy all entries
			List<Fat32Directory.Entry> entries = getItems();
			if (!entries.isEmpty())
			{
				// Add separator
				if (!menu.getItems().isEmpty() && !hasCopy)
					menu.getItems().add(new SeparatorMenuItem());

				// Add menu item
				MenuItem menuItem = new MenuItem(COPY_ALL_ENTRIES_STR + ELLIPSIS_STR,
												 Images.icon(Images.ImageId.COPY_LINES));
				menuItem.setOnAction(event0 -> onCopyEntryText());
				menu.getItems().add(menuItem);
			}

			// Add menu item: defragment file
			if ((entry != null) && (entry.getKind() == Fat32Directory.Entry.Kind.FILE))
			{
				// Add separator
				if (!menu.getItems().isEmpty())
					menu.getItems().add(new SeparatorMenuItem());

				// Add menu item
				MenuItem menuItem = new MenuItem(DEFRAGMENT_FILE_STR, Images.icon(Images.ImageId.DEFRAGMENT));
				try
				{
					menuItem.setDisable(!volume.isEntryFragmented(entry));
				}
				catch (VolumeException e)
				{
					Logger.INSTANCE.error(e);
				}
				menuItem.setOnAction(event0 -> onDefragmentFile(entry));
				menu.getItems().add(menuItem);
			}

			// Display context menu
			if (!menu.getItems().isEmpty())
				menu.show(getWindow(), event.getScreenX(), event.getScreenY());
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
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected void layoutChildren()
	{
		// Call superclass method
		super.layoutChildren();

		// Create function to create background colour
		IFunction2<Color, String, String> createColour = (colour1Key, colour2Key) ->
				ColourUtils.interpolateRgb(getColour(colour1Key), getColour(colour2Key), 0.5);

		// Get colours of background and border of column headers
		boolean deleted = (headerMode == HeaderMode.DELETED);
		boolean preview = (headerMode == HeaderMode.PREVIEW);
		Color backgroundColour = switch (headerMode)
		{
			case NONE    -> createColour.invoke(ColourKey.HEADER_CELL_BACKGROUND1,
												ColourKey.HEADER_CELL_BACKGROUND2);
			case DELETED -> createColour.invoke(ColourKey.HEADER_CELL_BACKGROUND1_DELETED,
												ColourKey.HEADER_CELL_BACKGROUND2_DELETED);
			case PREVIEW -> createColour.invoke(ColourKey.HEADER_CELL_BACKGROUND1_PREVIEW,
												ColourKey.HEADER_CELL_BACKGROUND2_PREVIEW);
		};
		Color borderColour = getColour(ColourKey.HEADER_CELL_BORDER);

		// Set background and border of column headers
		for (Node node : lookupAll(StyleSelector.COLUMN_HEADER))
		{
			if (node instanceof Region header)
			{
				if (StyleManager.INSTANCE.notUsingStyleSheet())
				{
					header.setBackground(SceneUtils.createColouredBackground(backgroundColour));
					header.setBorder(SceneUtils.createSolidBorder(borderColour, Side.RIGHT, Side.BOTTOM));
				}
				header.pseudoClassStateChanged(DELETED_PSEUDO_CLASS, deleted);
				header.pseudoClassStateChanged(PREVIEW_PSEUDO_CLASS, preview);
			}
		}

		// Set background and border of filler
		if (lookup(StyleSelector.FILLER) instanceof Region filler)
		{
			if (StyleManager.INSTANCE.notUsingStyleSheet())
			{
				filler.setBackground(SceneUtils.createColouredBackground(backgroundColour));
				filler.setBorder(SceneUtils.createSolidBorder(borderColour, Side.BOTTOM));
			}
			filler.pseudoClassStateChanged(DELETED_PSEUDO_CLASS, deleted);
			filler.pseudoClassStateChanged(PREVIEW_PSEUDO_CLASS, preview);
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
					Column column = Column.forKey(columnHeader.getId());

					// Get alignment of column and insets of column header
					HPos hAlignment = column.hAlignment;
					Insets insets = (columnHeader instanceof Region region) ? region.getInsets() : Insets.EMPTY;

					// Set properties of label
					label.setAlignment(FxGeomUtils.getPos(VPos.CENTER, hAlignment));
					label.setPadding(HEADER_CELL_LABEL_PADDING);
					label.setTextFill(getColour(TableViewStyle.ColourKey.CELL_TEXT));
					label.getStyleClass().add(TableViewStyle.StyleClass.CELL_LABEL);

					// Create pop-up manager for label
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
						headerPopUpManager.setDelay(headerCellPopUpDelay);
					}

					// Create pop-up for label
					VHPos.H hPos = switch (hAlignment)
					{
						case LEFT   -> VHPos.H.LEFT;
						case CENTER -> VHPos.H.CENTRE;
						case RIGHT  -> VHPos.H.RIGHT;
					};
					double x = switch (hAlignment)
					{
						case LEFT   -> (insets == null) ? 0.0 : -(insets.getLeft() + 1.0);
						case CENTER -> 0.0;
						case RIGHT  -> (insets == null) ? 0.0 : insets.getRight();
					};
					PopUpUtils.createPopUp(headerPopUpManager, label, VHPos.of(VHPos.V.TOP, hPos),
										   VHPos.of(VHPos.V.BOTTOM, hPos), x, 0.0, () -> column.longText, null);
				}

				// Header is initialised
				headerInitialised = true;
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Fat32Directory getDirectory()
	{
		return directory;
	}

	//------------------------------------------------------------------

	public void setDirectory(
		Fat32Directory	directory)
	{
		// Update instance variable
		this.directory = directory;

		// Update directory entries
		updateEntries();
	}

	//------------------------------------------------------------------

	public void setHeaderMode(
		HeaderMode	mode)
	{
		// Update instance variable
		headerMode = mode;

		// Allow header to be reinitialised
		headerInitialised = false;

		// Update UI
		setNeedsLayout(true);
	}

	//------------------------------------------------------------------

	public void setHeaderCellPopUpDelay(
		int	delay)
	{
		// Update class variable
		headerCellPopUpDelay = (delay > 0) ? delay : -1;

		// Update pop-up manager
		headerPopUpManager.setDelay(headerCellPopUpDelay);
	}

	//------------------------------------------------------------------

	public void setShowSpecialDirectories(
		boolean	show)
	{
		// Update instance variable
		showSpecialDirectories = show;

		// Update directory entries
		updateEntries();
	}

	//------------------------------------------------------------------

	public void updateEntries()
	{
		// Update directory entries
		Fat32Directory directory = getDirectory();
		itemList.update((directory == null)
								? Collections.emptyList()
								: showSpecialDirectories
										? directory.getEntries()
										: directory.getEntries().stream()
												.filter(entry -> !entry.isSpecialDirectory())
												.toList());

		// Redraw cells
		refresh();

		// Display first item
		scrollTo(0);
	}

	//------------------------------------------------------------------

	public void setColumns(
		Map<String, Double>	columnWidths)
	{
		// Create list of columns from keys
		List<Column> columns = new ArrayList<>();
		for (String key : columnWidths.keySet())
		{
			Column column = Column.forKey(key);
			if (column != null)
			{
				column.prefWidth = columnWidths.get(key);
				columns.add(column);
			}
		}

		// Set columns
		if (!columns.isEmpty())
			setColumns(columns, true);
	}

	//------------------------------------------------------------------

	public void setColumns(
		Collection<Column>	columns,
		boolean				ignoreOldColumns)
	{
		// Get list of current columns
		List<Column> oldColumns = ignoreOldColumns ? Collections.emptyList() : this.columns;

		// Update instance variable
		this.columns = new ArrayList<>(columns);

		// Clear columns
		getColumns().clear();

		// Clear cells
		cells.clear();

		// Initialise list of table columns
		List<TableColumn<Fat32Directory.Entry, ?>> tableColumns = new ArrayList<>();

		// Create procedure to create table column and add it to list
		IProcedure1<Column> addColumn = column ->
		{
			// Create table column
			TableColumn<Fat32Directory.Entry, ?> tableColumn = column.createColumn(this);

			// Set properties of table column
			tableColumn.setId(column.getKey());
			tableColumn.setPrefWidth(column.prefWidth);

			// Add column to list
			tableColumns.add(tableColumn);
		};

		// Add old columns
		for (Column column : oldColumns)
		{
			if (columns.contains(column))
				addColumn.invoke(column);
		}

		// Add new columns
		for (Column column : columns)
		{
			if (!oldColumns.contains(column))
				addColumn.invoke(column);
		}

		// Allow header to be reinitialised
		headerInitialised = false;

		// Add columns to table view
		getColumns().addAll(tableColumns);

		// Set preferred width
		double width = EXTRA_WIDTH;
		for (Column column : columns)
			width += column.prefWidth;
		setPrefWidth(width);
	}

	//------------------------------------------------------------------

	protected void addContextMenuItems(
		ContextMenu				menu,
		int						index,
		Fat32Directory			directory,
		Fat32Directory.Entry	entry)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	protected void onCellDoubleClicked(
		Fat32Directory.Entry	entry)
	{
		// do nothing
	}

	//------------------------------------------------------------------

	protected Window getWindow()
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

	private void onCopyEntryText()
	{
		// Display dialog for selecting columns and field separator
		CopyEntryTextDialog.State result = new CopyEntryTextDialog(COPY_ALL_ENTRIES_STR).showDialog();
		if (result == null)
			return;

		// Get columns as list
		List<Column> columns = new ArrayList<>(result.columns);

		// Get number of columns
		int numColumns = columns.size();

		// Initialise text
		String text = null;

		// Convert entries to text
		switch (result.fieldSeparator)
		{
			case SPACES:
			{
				// Create array of flags for right-aligned columns
				boolean[] rightAligned = new boolean[numColumns];
				for (int i = 0; i < numColumns; i++)
					rightAligned[i] = (columns.get(i).hAlignment == HPos.RIGHT);

				// Create array of gaps between columns
				int[] gaps = new int[numColumns - 1];
				Arrays.fill(gaps, 2);

				// Create list of rows of fields
				List<String[]> rows = new ArrayList<>();
				int numEntries = getItems().size();
				for (int i = result.includeHeader ? -1 : 0; i < numEntries; i++)
				{
					String[] fields = new String[numColumns];
					for (int j = 0; j < numColumns; j++)
					{
						Column column = columns.get(j);
						fields[j] = (i < 0) ? column.text : column.getValueString(getItems().get(i));
					}
					rows.add(fields);
				}

				// Tabulate rows
				Tabulator.Result table = Tabulator.tabulate(numColumns, rightAligned, gaps, rows);
				text = table.text();
				if (result.includeHeader)
				{
					int index = text.indexOf('\n') + 1;
					text = text.substring(0, index) + "-".repeat(table.maxLineLength()) + "\n" + text.substring(index);
				}
				break;
			}

			case TAB:
			{
				// Initialise buffer
				StringBuilder buffer = new StringBuilder(1024);

				// Append column headers
				if (result.includeHeader)
				{
					for (int i = 0; i < numColumns; i++)
					{
						if (i > 0)
							buffer.append('\t');
						buffer.append(columns.get(i).text);
					}
					buffer.append('\n');
				}

				// Append entries
				for (Fat32Directory.Entry entry : getItems())
				{
					for (int i = 0; i < numColumns; i++)
					{
						if (i > 0)
							buffer.append('\t');
						buffer.append(columns.get(i).getValueString(entry));
					}
					buffer.append('\n');
				}

				// Set result
				text = buffer.toString();
				break;
			}
		}

		// Put text on system clipboard
		try
		{
			ClipboardUtils.putTextThrow(text);
		}
		catch (BaseException e)
		{
			Utils.showErrorMessage(getWindow(), COPY_ALL_ENTRIES_STR, e);
		}
	}

	//------------------------------------------------------------------

	private void onDefragmentFile(
		Fat32Directory.Entry	entry)
	{
		// Log description of task
		Logger.INSTANCE.info(DEFRAGMENT_FILE_STR + " " + entry.getPathname());

		// Declare record for result of task
		record Result(
			Fat32Volume.DefragStatus			status,
			List<Fat32Volume.InvalidCluster>	invalidClusters)
		{ }

		// Create task to defragment file
		Task<Result> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(DEFRAGMENT_FILE_STR);
				updateProgress(-1, 1);
			}

			@Override
			protected Result call()
				throws Exception
			{
				// Get volume
				Fat32Volume volume = directory.getVolume();

				// Create task status
				ITaskStatus taskStatus = createTaskStatus();

				// Validate cluster chain
				List<Fat32Volume.InvalidCluster> invalidClusters = new ArrayList<>();
				volume.validateClusterChains(List.of(entry), invalidClusters, taskStatus);

				// Defragment file
				Fat32Volume.DefragStatus status = taskStatus.isCancelled()
														? Fat32Volume.DefragStatus.CANCEL
														: invalidClusters.isEmpty()
																? volume.defragmentFile(entry, taskStatus)
																: null;

				// If task has been cancelled, change state to 'cancelled'
				hardCancel(false);

				// Return result
				return new Result(status, invalidClusters);
			}

			@Override
			protected void succeeded()
			{
				// Refresh table view
				refresh();

				// Get result
				Result result = getValue();

				// Case: no invalid clusters
				if (result.invalidClusters.isEmpty())
				{
					// Report result
					String text = switch (result.status)
					{
						case NOT_FRAGMENTED   -> NOT_FRAGMENTED_STR;
						case NOT_ENOUGH_SPACE -> NOT_ENOUGH_SPACE_STR;
						case SUCCESS          -> DEFRAGMENTED_STR;
						case CANCEL           -> null;
					};
					if (text != null)
					{
						MessageIcon32 icon = switch (result.status)
						{
							case NOT_FRAGMENTED   -> MessageIcon32.INFORMATION;
							case NOT_ENOUGH_SPACE -> MessageIcon32.ALERT;
							case SUCCESS          -> MessageIcon32.INFORMATION;
							case CANCEL           -> null;
						};
						String message = entry.getPathname() + MessageConstants.LABEL_SEPARATOR + text;
						NotificationDialog.show(getWindow(), DEFRAGMENT_FILE_STR, icon.get(), message);
					}
				}

				// Case: invalid clusters
				else
				{
					InvalidClusterDialog.show(getWindow(), getTitle() + " : " + INVALID_CLUSTERS_STR,
											  result.invalidClusters);
				}
			}

			@Override
			protected void failed()
			{
				// Refresh table view
				refresh();

				// Display error message in dialog
				showErrorMessage(getWindow());
			}

			@Override
			protected void cancelled()
			{
				// Refresh table view
				refresh();
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(getWindow(), task, SimpleProgressDialog.CancelMode.NO_INTERRUPT);

		// Execute task on background thread
		Fat32ManagerApp.executeTask(task);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: COLUMN OF TABLE VIEW


	protected enum Column
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		ATTRIBUTES
		(
			"Attrs",
			"Attributes",
			HPos.CENTER,
			Fat32Directory.Attr.DISPLAYED_ATTRS.length * Fat32Directory.Attr.getMaxIdCharWidth()
		)
		{
			@Override
			protected String getValueString(
				Fat32Directory.Entry	entry)
			{
				return entry.getAttributeString();
			}

			//----------------------------------------------------------

			@Override
			protected TableColumn<Fat32Directory.Entry, Set<Fat32Directory.Attr>> createColumn(
				DirectoryTableView	tableView)
			{
				TableColumn<Fat32Directory.Entry, Set<Fat32Directory.Attr>> column = new TableColumn<>(toString());
				column.setCellFactory(column0 -> tableView.new AttrCell(this));
				column.setCellValueFactory(features ->
						new ReadOnlyObjectWrapper<>(features.getValue().getAttributes()));
				column.setComparator(Comparator.comparingInt(Fat32Directory.Attr::attrsToBitArray));
				return column;
			}

			//----------------------------------------------------------
		},

		NAME
		(
			"Name",
			null,
			HPos.LEFT,
			TextUtils.textHeightCeil(16.0)
		)
		{
			@Override
			protected String getValueString(
				Fat32Directory.Entry	entry)
			{
				return entry.getName();
			}

			//----------------------------------------------------------

			@Override
			protected TableColumn<Fat32Directory.Entry, String> createColumn(
				DirectoryTableView	tableView)
			{
				TableColumn<Fat32Directory.Entry, String> column = new TableColumn<>(toString());
				column.setCellFactory(column0 -> tableView.new NameCell(this));
				column.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getName()));
				return column;
			}

			//----------------------------------------------------------
		},

		LENGTH
		(
			"Length",
			"Length of file",
			HPos.RIGHT,
			TextUtils.textWidth(Utils.formatDecimal(0xFF_FFFF_FFFFL))
		)
		{
			@Override
			protected String getValueString(
				Fat32Directory.Entry	entry)
			{
				return (entry.isDirectory() || entry.isVolumeLabel()) ? "" : Utils.formatDecimal(entry.getFileLength());
			}

			//----------------------------------------------------------

			@Override
			protected TableColumn<Fat32Directory.Entry, Long> createColumn(
				DirectoryTableView	tableView)
			{
				TableColumn<Fat32Directory.Entry, Long> column = new TableColumn<>(toString());
				column.setCellFactory(column0 -> tableView.new NumberCell(this));
				column.setCellValueFactory(features ->
				{
					Fat32Directory.Entry entry = features.getValue();
					Long value = (entry.isDirectory() || entry.isVolumeLabel()) ? null : entry.getFileLength();
					return new ReadOnlyObjectWrapper<>(value);
				});
				return column;
			}

			//----------------------------------------------------------
		},

		CREATION_TIME
		(
			"Created",
			"Date/time of creation",
			HPos.LEFT,
			TextUtils.textWidth(Fat32Directory.CREATION_TIME_FORMATTER.format(LocalDateTime.now()))
		)
		{
			@Override
			protected String getValueString(
				Fat32Directory.Entry	entry)
			{
				LocalDateTime time = entry.getCreationTime();
				return (time == null) ? "" : Fat32Directory.CREATION_TIME_FORMATTER.format(time);
			}

			//----------------------------------------------------------

			@Override
			protected TableColumn<Fat32Directory.Entry, LocalDateTime> createColumn(
				DirectoryTableView	tableView)
			{
				TableColumn<Fat32Directory.Entry, LocalDateTime> column = new TableColumn<>(toString());
				column.setCellFactory(column0 ->
						tableView.new DateTimeCell(this, Fat32Directory.CREATION_TIME_FORMATTER));
				column.setCellValueFactory(features ->
						new ReadOnlyObjectWrapper<>(features.getValue().getCreationTime()));
				return column;
			}

			//----------------------------------------------------------
		},

		MODIFICATION_TIME
		(
			"Modified",
			"Date/time of last modification",
			HPos.LEFT,
			TextUtils.textWidth(Fat32Directory.LAST_MODIFICATION_TIME_FORMATTER.format(LocalDateTime.now()))
		)
		{
			@Override
			protected String getValueString(
				Fat32Directory.Entry	entry)
			{
				LocalDateTime time = entry.getLastModificationTime();
				return (time == null) ? "" : Fat32Directory.LAST_MODIFICATION_TIME_FORMATTER.format(time);
			}

			//----------------------------------------------------------

			@Override
			protected TableColumn<Fat32Directory.Entry, LocalDateTime> createColumn(
				DirectoryTableView	tableView)
			{
				TableColumn<Fat32Directory.Entry, LocalDateTime> column = new TableColumn<>(toString());
				column.setCellFactory(column0 ->
						tableView.new DateTimeCell(this, Fat32Directory.LAST_MODIFICATION_TIME_FORMATTER));
				column.setCellValueFactory(features ->
						new ReadOnlyObjectWrapper<>(features.getValue().getLastModificationTime()));
				return column;
			}

			//----------------------------------------------------------
		},

		ACCESS_DATE
		(
			"Accessed",
			"Date of last access",
			HPos.LEFT,
			TextUtils.textWidth(Fat32Directory.ACCESS_DATE_FORMATTER.format(LocalDate.now()))
		)
		{
			@Override
			protected String getValueString(
				Fat32Directory.Entry	entry)
			{
				LocalDate date = entry.getAccessDate();
				return (date == null) ? "" : Fat32Directory.ACCESS_DATE_FORMATTER.format(date);
			}

			//----------------------------------------------------------

			@Override
			protected TableColumn<Fat32Directory.Entry, LocalDate> createColumn(
				DirectoryTableView	tableView)
			{
				TableColumn<Fat32Directory.Entry, LocalDate> column = new TableColumn<>(toString());
				column.setCellFactory(column0 -> tableView.new DateCell(this, Fat32Directory.ACCESS_DATE_FORMATTER));
				column.setCellValueFactory(features ->
						new ReadOnlyObjectWrapper<>(features.getValue().getAccessDate()));
				return column;
			}

			//----------------------------------------------------------
		},

		FIRST_CLUSTER
		(
			"First cluster",
			"Index of first cluster",
			HPos.RIGHT,
			TextUtils.textWidth(Utils.formatDecimal(0xFFFF_FFFFL))
		)
		{
			@Override
			protected String getValueString(
				Fat32Directory.Entry	entry)
			{
				int index = entry.getClusterIndex();
				return (index == 0) ? "" : Utils.formatDecimal(index);
			}

			//----------------------------------------------------------

			@Override
			protected TableColumn<Fat32Directory.Entry, Long> createColumn(
				DirectoryTableView	tableView)
			{
				TableColumn<Fat32Directory.Entry, Long> column = new TableColumn<>(toString());
				column.setCellFactory(column0 -> tableView.new NumberCell(this));
				column.setCellValueFactory(features ->
				{
					int value = features.getValue().getClusterIndex();
					return new ReadOnlyObjectWrapper<>((value == 0) ? null : Long.valueOf(value));
				});
				return column;
			}

			//----------------------------------------------------------
		},

		NUM_CLUSTERS
		(
			"Num clusters",
			"Number of clusters",
			HPos.RIGHT,
			TextUtils.textWidth(Utils.formatDecimal(0xFFFF_FFFFL))
		)
		{
			@Override
			protected String getValueString(
				Fat32Directory.Entry	entry)
			{
				int numClusters = entry.getNumClusters();
				return (numClusters == 0) ? "" : Utils.formatDecimal(numClusters);
			}

			//----------------------------------------------------------

			@Override
			protected TableColumn<Fat32Directory.Entry, Long> createColumn(
				DirectoryTableView	tableView)
			{
				TableColumn<Fat32Directory.Entry, Long> column = new TableColumn<>(toString());
				column.setCellFactory(column0 -> tableView.new NumberCell(this));
				column.setCellValueFactory(features ->
				{
					int value = features.getValue().getNumClusters();
					return new ReadOnlyObjectWrapper<>((value == 0) ? null : Long.valueOf(value));
				});
				return column;
			}

			//----------------------------------------------------------
		},

		FIRST_SECTOR
		(
			"First sector",
			"Index of first sector",
			HPos.RIGHT,
			TextUtils.textWidth(Utils.formatDecimal(0xFFFF_FFFFL))
		)
		{
			@Override
			protected String getValueString(
				Fat32Directory.Entry	entry)
			{
				long index = entry.getSectorIndex();
				return (index == 0) ? "" : Utils.formatDecimal(index);
			}

			//----------------------------------------------------------

			@Override
			protected TableColumn<Fat32Directory.Entry, Long> createColumn(
				DirectoryTableView	tableView)
			{
				TableColumn<Fat32Directory.Entry, Long> column = new TableColumn<>(toString());
				column.setCellFactory(column0 -> tableView.new NumberCell(this));
				column.setCellValueFactory(features ->
				{
					long value = features.getValue().getSectorIndex();
					return new ReadOnlyObjectWrapper<>((value == 0) ? null : Long.valueOf(value));
				});
				return column;
			}

			//----------------------------------------------------------
		};

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	text;
		private	String	longText;
		private	HPos	hAlignment;
		private	double	prefWidth;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Column(
			String	text,
			String	longText,
			HPos	hAlignment,
			double	textWidth)
		{
			// Initialise instance variables
			this.text = text;
			this.longText = (longText == null) ? text : longText;
			this.hAlignment = hAlignment;
			prefWidth = Math.ceil(textWidth + Cell.LABEL_PADDING.getLeft() + Cell.LABEL_PADDING.getRight() + 1.0);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the column that is associated with the specified key.
		 *
		 * @param  key
		 *          the key whose associated column is required.
		 * @return the column that is associated with {@code key}, or {@code null} if there is no such column.
		 */

		protected static Column forKey(
			String	key)
		{
			return Arrays.stream(values()).filter(value -> value.getKey().equals(key)).findFirst().orElse(null);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Abstract methods
	////////////////////////////////////////////////////////////////////

		protected abstract String getValueString(
			Fat32Directory.Entry	entry);

		//--------------------------------------------------------------

		protected abstract TableColumn<Fat32Directory.Entry, ?> createColumn(
			DirectoryTableView	tableView);

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		protected String getLongText()
		{
			return longText;
		}

		//--------------------------------------------------------------

		private String getKey()
		{
			return StringUtils.toCamelCase(name());
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: CELL


	private abstract class Cell<T>
		extends TableCell<Fat32Directory.Entry, T>
		implements CellPopUpManager.ICell<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The gap between the text and graphic of the label of a cell. */
		private static final	double	GRAPHIC_TEXT_GAP	= 6.0;

		/** The padding around the label of a cell. */
		private static final	Insets	LABEL_PADDING	= new Insets(2.0, 6.0, 2.0, 6.0);

		/** The delay (in milliseconds) before a pop-up for a cell is displayed after it is activated. */
		private static final	int		POP_UP_DELAY	= 500;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	Column	column;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Cell(
			Column	column)
		{
			// Initialise instance variables
			this.column = column;

			// Set properties
			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			setAlignment(FxGeomUtils.getPos(VPos.CENTER, column.hAlignment));
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
			addEventHandler(MouseEvent.MOUSE_EXITED, event ->
			{
				if (CellPopUpManager.deactivatePopUpOnMouseExited())
					cellPopUpManager.deactivate();
			});

			// When a mouse button is released, deactivate any cell pop-up
			addEventFilter(MouseEvent.MOUSE_RELEASED, event ->
			{
				if (cellPopUpManager.deactivate())
					event.consume();
			});

			// Handle mouse double-clicked on directory entry
			addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
			{
				if ((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2))
					onCellDoubleClicked(getEntry());
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
			return (getItem() == null) ? null : getIndex() + ":" + column.getKey();
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
			VHPos pos = switch (getAlignment().getHpos())
			{
				case LEFT   -> VHPos.CENTRE_LEFT;
				case CENTER -> VHPos.CENTRE_CENTRE;
				case RIGHT  -> VHPos.CENTRE_RIGHT;
			};
			double x = switch (getAlignment().getHpos())
			{
				case LEFT   -> -1.0;
				case CENTER -> 0.0;
				case RIGHT  -> 1.0;
			};
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
			return DirectoryTableView.this.getWindow();
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
							: SceneUtils.createSolidBorder(getColour(TableViewStyle.ColourKey.CELL_BORDER),
														   Side.RIGHT, Side.BOTTOM));

			// Set graphic
			setGraphic(empty ? null : createLabel());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		protected Fat32Directory.Entry getEntry()
		{
			List<Fat32Directory.Entry> entries = getItems();
			int index = getIndex();
			return ((index < 0) || (index >= entries.size())) ? null : entries.get(index);
		}

		//--------------------------------------------------------------

		protected void updateBackground()
		{
			if (!StyleManager.INSTANCE.notUsingStyleSheet())
				return;

			int index = getIndex();
			boolean selected = (getSelectionModel().getSelectedIndex() == index);
// WORKAROUND
//			boolean focused = getTableView().isFocused();
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

		//--------------------------------------------------------------

		protected String getLabelText()
		{
			return null;
		}

		//--------------------------------------------------------------

		protected Node getLabelGraphic()
		{
			return null;
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
			if (getItem() != null)
			{
				// Get alignment of cell
				Pos alignment = getAlignment();

				// Create label
				label = new Label(getLabelText(), getLabelGraphic());
				label.setGraphicTextGap(GRAPHIC_TEXT_GAP);
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


	// CLASS: ATTRIBUTES CELL


	private class AttrCell
		extends Cell<Set<Fat32Directory.Attr>>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	char	NO_ATTR_CHAR	= '\u00B7';

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private AttrCell(
			Column	column)
		{
			// Call superclass constructor
			super(column);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected Group getLabelGraphic()
		{
			// Initialise group
			Group group = null;

			// Create text nodes for attributes
			Set<Fat32Directory.Attr> attrs = getItem();
			if (attrs != null)
			{
				// Get number of attributes
				int numAttrs = Fat32Directory.Attr.DISPLAYED_ATTRS.length;

				// Get maximum width of attribute character
				if (attrIdCharMaxWidth == 0.0)
					attrIdCharMaxWidth = Fat32Directory.Attr.getMaxIdCharWidth();

				// Create group with background rectangle
				group = new Group(new Rectangle((double)numAttrs * attrIdCharMaxWidth, 0.0, Color.TRANSPARENT));

				// Create text nodes and add them to group
				double x = 0.0;
				for (Fat32Directory.Attr attr : Fat32Directory.Attr.DISPLAYED_ATTRS)
				{
					// Create text node
					char ch = attrs.contains(attr) ? attr.getIdChar() : NO_ATTR_CHAR;
					Text2 textNode = Text2.createCentred(Character.toString(ch));

					// Set properties of text node
					if (ch != NO_ATTR_CHAR)
						textNode.setFont(FontUtils.boldFont());
					textNode.setFill(getColour(ColourKey.CELL_ATTRIBUTE_TEXT));
					textNode.getStyleClass().add(StyleClass.ATTRIBUTE);

					// Set x coordinate of text node
					textNode.relocate(x + 0.5 * (attrIdCharMaxWidth - textNode.getWidth()), 0.0);

					// Add text node to group
					group.getChildren().add(textNode);

					// Increment x coordinate
					x += attrIdCharMaxWidth;
				}
			}

			// Return group
			return group;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: NAME CELL


	private class NameCell
		extends Cell<String>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private NameCell(
			Column	column)
		{
			// Call superclass constructor
			super(column);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getLabelText()
		{
			return getItem();
		}

		//--------------------------------------------------------------

		@Override
		protected Node getLabelGraphic()
		{
			Node graphic = null;
			Fat32Directory.Entry entry = getEntry();
			if (entry != null)
			{
				String imageId = null;
				if (entry.isVolumeLabel())
					imageId = Images.ImageId.VOLUME_LABEL;
				else if (entry.isDirectory())
					imageId = Images.ImageId.DIRECTORY;
				else
					imageId = Images.ImageId.FILE;
				graphic = Images.icon(imageId);
			}
			return graphic;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: NUMBER CELL


	private class NumberCell
		extends Cell<Long>
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private NumberCell(
			Column	column)
		{
			// Call superclass constructor
			super(column);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getLabelText()
		{
			Long item = getItem();
			return (item == null) ? null : Utils.formatDecimal(item);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: DATE CELL


	private class DateCell
		extends Cell<LocalDate>
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	DateTimeFormatter	formatter;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DateCell(
			Column				column,
			DateTimeFormatter	formatter)
		{
			// Call superclass constructor
			super(column);

			// Initialise instance variables
			this.formatter = formatter;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getLabelText()
		{
			LocalDate item = getItem();
			return (item == null) ? null : formatter.format(item);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: DATE/TIME CELL


	private class DateTimeCell
		extends Cell<LocalDateTime>
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	DateTimeFormatter	formatter;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DateTimeCell(
			Column				column,
			DateTimeFormatter	formatter)
		{
			// Call superclass constructor
			super(column);

			// Initialise instance variables
			this.formatter = formatter;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected String getLabelText()
		{
			LocalDateTime item = getItem();
			return (item == null) ? null : formatter.format(item);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: 'COPY ENTRY TEXT' DIALOG


	private class CopyEntryTextDialog
		extends SimpleModalDialog<CopyEntryTextDialog.State>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	double	COLUMNS_PANE_GAP	= 8.0;

		private static final	Insets	COLUMNS_PANE_PADDING	= new Insets(8.0);

		private static final	double	ROWS_PANE_H_GAP	= 6.0;
		private static final	double	ROWS_PANE_V_GAP	= 6.0;

		private static final	Insets	ROWS_PANE_PADDING	= new Insets(0.0, 8.0, 6.0, 8.0);

		private static final	double	OUTER_PANE_GAP	= 3.0;

		private static final	Insets	OUTER_PANE_PADDING	= new Insets(3.0, 3.0, 0.0, 3.0);

		private static final	String	COLUMNS_STR			= "Columns";
		private static final	String	ROWS_STR			= "Rows";
		private static final	String	INCLUDE_HEADER_STR	= "Include header";
		private static final	String	FIELD_SEPARATOR_STR	= "Field separator";
		private static final	String	COPY_STR			= "Copy";

	////////////////////////////////////////////////////////////////////
	//  Class variables
	////////////////////////////////////////////////////////////////////

		private static	State	state	= new State(EnumSet.allOf(Column.class), false, FieldSeparator.SPACES);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	State	result;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private CopyEntryTextDialog(
			String	title)
		{
			// Call superclass constructor
			super(getWindow(), MethodHandles.lookup().lookupClass().getCanonicalName(), null, title);

			// Create pane: columns
			VBox columnsPane = new VBox(COLUMNS_PANE_GAP);
			columnsPane.setMaxWidth(Region.USE_PREF_SIZE);
			columnsPane.setPadding(COLUMNS_PANE_PADDING);

			// Create check boxes for columns
			SimpleSetProperty<Column> columns =
					new SimpleSetProperty<>(FXCollections.observableSet(EnumSet.copyOf(state.columns)));
			EnumMap<Column, CheckBox> columnCheckBoxes = new EnumMap<>(Column.class);
			for (Column column : Column.values())
			{
				CheckBox checkBox = new CheckBox(column.longText);
				checkBox.setSelected(state.columns.contains(column));
				checkBox.selectedProperty().addListener((observable, oldSelected, selected) ->
				{
					if (selected)
						columns.add(column);
					else
						columns.remove(column);
				});
				columnCheckBoxes.put(column, checkBox);
				columnsPane.getChildren().add(checkBox);
			}

			// Create titled pane: columns
			LabelTitledPane titledColumnsPane = new LabelTitledPane(COLUMNS_STR, columnsPane);
			VBox.setVgrow(titledColumnsPane, Priority.ALWAYS);

			// Create pane: rows
			GridPane rowsPane = new GridPane();
			rowsPane.setHgap(ROWS_PANE_H_GAP);
			rowsPane.setVgap(ROWS_PANE_V_GAP);
			rowsPane.setAlignment(Pos.CENTER);
			rowsPane.setPadding(ROWS_PANE_PADDING);

			// Initialise column constraints
			ColumnConstraints column = new ColumnConstraints();
			column.setMinWidth(Region.USE_PREF_SIZE);
			column.setHalignment(HPos.RIGHT);
			column.setHgrow(Priority.NEVER);
			rowsPane.getColumnConstraints().add(column);

			column = new ColumnConstraints();
			column.setHalignment(HPos.LEFT);
			column.setHgrow(Priority.NEVER);
			column.setFillWidth(false);
			rowsPane.getColumnConstraints().add(column);

			// Initialise row index
			int row = 0;

			// Create check box: include header
			CheckBox includeHeaderCheckBox = new CheckBox(INCLUDE_HEADER_STR);
			includeHeaderCheckBox.setSelected(state.includeHeader);
			GridPane.setMargin(includeHeaderCheckBox, new Insets(0.0, 0.0, 2.0, 0.0));
			rowsPane.add(includeHeaderCheckBox, 1, row++);

			// Create spinner: field separator
			CollectionSpinner<FieldSeparator> fieldSeparatorSpinner =
					CollectionSpinner.leftRightH(HPos.CENTER, true, FieldSeparator.class, state.fieldSeparator, null,
												 null);
			rowsPane.addRow(row++, new Label(FIELD_SEPARATOR_STR), fieldSeparatorSpinner);

			// Create titled pane: rows
			LabelTitledPane titledRowsPane = new LabelTitledPane(ROWS_STR, rowsPane);

			// Create outer pane
			VBox outerPane = new VBox(OUTER_PANE_GAP, titledColumnsPane, titledRowsPane);
			outerPane.setAlignment(Pos.TOP_CENTER);
			outerPane.setPadding(OUTER_PANE_PADDING);

			// Set outer pane as content pane
			setContent(outerPane);

			// Create function to get state from components of user interface
			IFunction0<State> getState = () ->
					new State(EnumSet.copyOf(columns), includeHeaderCheckBox.isSelected(),
							  fieldSeparatorSpinner.getItem());

			// Create button: copy
			Button copyButton = Buttons.hNoShrink(COPY_STR);
			copyButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
			copyButton.setOnAction(event ->
			{
				result = getState.invoke();
				requestClose();
			});
			addButton(copyButton, HPos.RIGHT);

			// Create procedure to update 'copy' button
			IProcedure0 updateCopyButton = () -> copyButton.setDisable(columns.isEmpty());

			// Update 'copy' button when set of selected columns changes
			columns.addListener((InvalidationListener) observable -> updateCopyButton.invoke());

			// Update 'copy' button
			updateCopyButton.invoke();

			// Create button: cancel
			Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
			cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
			cancelButton.setOnAction(event -> requestClose());
			addButton(cancelButton, HPos.RIGHT);

			// Fire 'cancel' button if Escape key is pressed
			setKeyFireButton(cancelButton, null);

			// Save dialog state when dialog is closed
			setOnHiding(event -> state = getState.invoke());

			// Apply new style sheet to scene
			applyStyleSheet();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected State getResult()
		{
			return result;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Enumerated types
	////////////////////////////////////////////////////////////////////


		// ENUMERATION: FIELD SEPARATOR


		private enum FieldSeparator
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			SPACES
			(
				"Spaces"
			),

			TAB
			(
				"Tab"
			);

		////////////////////////////////////////////////////////////////
		//  Instance variables
		////////////////////////////////////////////////////////////////

			private	String	text;

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private FieldSeparator(
				String	text)
			{
				// Initialise instance variables
				this.text = text;
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			@Override
			public String toString()
			{
				return text;
			}

			//----------------------------------------------------------

		}

		//==============================================================

	////////////////////////////////////////////////////////////////////
	//  Member records
	////////////////////////////////////////////////////////////////////


		// RECORD: STATE


		private record State(
			EnumSet<Column>	columns,
			boolean			includeHeader,
			FieldSeparator	fieldSeparator)
		{ }

		//==============================================================

	}

	//==================================================================

}

//----------------------------------------------------------------------
