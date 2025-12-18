/*====================================================================*\

SectorClusterViewDialog.java

Class: sector/cluster-view dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.lang.invoke.MethodHandles;

import java.nio.channels.FileChannel;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import java.util.stream.Collectors;

import javafx.application.Platform;

import javafx.beans.property.SimpleIntegerProperty;

import javafx.concurrent.Task;

import javafx.event.EventType;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.Cursor;
import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.text.Text;

import javafx.stage.Window;

import uk.blankaspect.common.basictree.DoubleNode;
import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.common.bytechannel.ChannelUtils;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.UnexpectedRuntimeException;

import uk.blankaspect.common.filesystem.FilenameUtils;
import uk.blankaspect.common.filesystem.PathUtils;

import uk.blankaspect.common.function.IFunction0;
import uk.blankaspect.common.function.IFunction1;
import uk.blankaspect.common.function.IFunction2;
import uk.blankaspect.common.function.IProcedure0;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.common.logging.Logger;

import uk.blankaspect.common.message.MessageConstants;

import uk.blankaspect.common.misc.SystemUtils;

import uk.blankaspect.common.number.NumberUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.driveio.VolumeException;

import uk.blankaspect.ui.jfx.button.Buttons;
import uk.blankaspect.ui.jfx.button.ButtonUtils;
import uk.blankaspect.ui.jfx.button.GraphicButton;
import uk.blankaspect.ui.jfx.button.ImageDataButton;
import uk.blankaspect.ui.jfx.button.SlideButton;

import uk.blankaspect.ui.jfx.container.PaneStyle;
import uk.blankaspect.ui.jfx.container.PathnamePane;

import uk.blankaspect.ui.jfx.dialog.ErrorDialog;
import uk.blankaspect.ui.jfx.dialog.NotificationDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleProgressDialog;
import uk.blankaspect.ui.jfx.dialog.WarningDialog;

import uk.blankaspect.ui.jfx.filler.FillerUtils;

import uk.blankaspect.ui.jfx.font.Fonts;
import uk.blankaspect.ui.jfx.font.FontUtils;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

import uk.blankaspect.ui.jfx.io.IOUtils;

import uk.blankaspect.ui.jfx.label.Labels;
import uk.blankaspect.ui.jfx.label.MultiTextLabeller;

import uk.blankaspect.ui.jfx.locationchooser.LocationChooser;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;
import uk.blankaspect.ui.jfx.spinner.SpinnerFactory;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;
import uk.blankaspect.ui.jfx.style.StyleSelector;

import uk.blankaspect.ui.jfx.text.TextUtils;

import uk.blankaspect.ui.jfx.textfield.PathnameField;

import uk.blankaspect.ui.jfx.tooltip.TooltipDecorator;

import uk.blankaspect.ui.jfx.window.WindowState;

//----------------------------------------------------------------------


// CLASS: SECTOR/CLUSTER-VIEW DIALOG


/**
 * This class implements a modal dialog that contains a navigable view of the sectors or clusters of a volume.
 */

public class SectorClusterViewDialog
	extends SimpleModalDialog<Void>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The minimum index of a sector. */
	private static final	int		MIN_SECTOR_INDEX	= 0;

	/** The minimum absolute index of a cluster. */
	private static final	int		MIN_ABS_CLUSTER_INDEX	= Fat32Fat.MIN_CLUSTER_INDEX;

	/** The horizontal gap between adjacent controls. */
	private static final	double	CONTROL_H_GAP	= 6.0;

	/** The vertical gap between adjacent controls. */
	private static final	double	CONTROL_V_GAP	= 5.0;

	/** The length of a block of sectors or clusters when navigating. */
	private static final	int		NAVIGATION_BLOCK_LENGTH	= 10;

	/** The maximum length of the list of the sectors or clusters that were most recently viewed. */
	private static final	int		MAX_HISTORY_LENGTH	= 16;

	private static final	char	CONTROL_CHAR_PLACEHOLDER	= '\u00B7';	// middle dot

	private static final	int		INDEX_SPINNER_NUM_DIGITS	= 10;

	private static final	double	LOCATION_PANE_GAP	= 6.0;

	private static final	Insets	LOCATION_PANE_PADDING	= new Insets(1.0, 6.0, 1.0, 6.0);

	private static final	Insets	BUTTON_PADDING	= new Insets(1.0, 2.0, 1.0, 2.0);

	private static final	double	DATA_UNIT_PANE_GAP	= 10.0;

	private static final	double	NAVIGATION_PANE_GAP	= 10.0;

	private static final	Insets	NAVIGATION_BUTTON_PANE_PADDING	= new Insets(0.0, 3.0, 0.0, 3.0);

	private static final	Insets	SAVE_SECTOR_CLUSTER_PANE_PADDING	= new Insets(6.0, 10.0, 6.0, 10.0);

	private static final	double	OUTER_CONTROL_PANE_GAP	= 10.0;

	private static final	Insets	OUTER_CONTROL_PANE_PADDING	= new Insets(5.0, 10.0, 5.0, 10.0);

	/** The padding around an offset label. */
	private static final	Insets	OFFSET_LABEL_PADDING	= new Insets(2.0, 5.0, 2.0, 5.0);

	private static final	Insets	MENU_ITEM_PADDING	= new Insets(1.0, 1.0, 1.0, 6.0);

	private static final	KeyCombination	KEY_COMBO_COPY	=
			new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

	/** Miscellaneous strings. */
	private static final	String	ABS_TITLE_STR				= "[Abs]";
	private static final	String	CHAIN_TITLE_STR				= "[Chain]";
	private static final	String	CHAIN_STR					= "Chain";
	private static final	String	READING_SECTORS_STR			= "Reading sectors";
	private static final	String	SWITCH_SECTOR_CLUSTER_STR	= "Switch to %ss";
	private static final	String	SECTOR_STR					= "Sector";
	private static final	String	SECTORS_STR					= "Sectors";
	private static final	String	CLUSTER_STR					= "Cluster";
	private static final	String	SECTOR_HISTORY_STR			= "Sector history";
	private static final	String	CLUSTER_HISTORY_STR			= "Cluster history";
	private static final	String	PREVIOUS_SECTOR_STR			= "Previous sector";
	private static final	String	PREVIOUS_CLUSTER_STR		= "Previous cluster";
	private static final	String	NEXT_SECTOR_STR				= "Next sector";
	private static final	String	NEXT_CLUSTER_STR			= "Next cluster";
	private static final	String	SECTORS_BACK_STR			= NAVIGATION_BLOCK_LENGTH + " sectors back";
	private static final	String	CLUSTERS_BACK_STR			= NAVIGATION_BLOCK_LENGTH + " clusters back";
	private static final	String	SECTORS_FORWARD_STR			= NAVIGATION_BLOCK_LENGTH + " sectors forward";
	private static final	String	CLUSTERS_FORWARD_STR		= NAVIGATION_BLOCK_LENGTH + " clusters forward";
	private static final	String	FIRST_SECTOR_STR			= "First sector";
	private static final	String	FIRST_CLUSTER_STR			= "First cluster";
	private static final	String	LAST_SECTOR_STR				= "Last sector";
	private static final	String	LAST_CLUSTER_STR			= "Last cluster";
	private static final	String	OFFSET_DEC_STR				= "Offset (dec)\n(Ctrl+mousePress in data view)";
	private static final	String	OFFSET_HEX_STR				= "Offset (hex)\n(Ctrl+mousePress in data view)";
	private static final	String	SAVE_STR					= "Save";
	private static final	String	READ_SECTOR_STR				= "Read sector";
	private static final	String	READ_CLUSTER_STR			= "Read cluster";
	private static final	String	READING_SECTOR_STR			= "Reading sector";
	private static final	String	READING_CLUSTER_STR			= "Reading cluster";
	private static final	String	COPY_STR					= "Copy";
	private static final	String	WRITING_STR					= "Writing";
	private static final	String	FILE_WRITTEN_STR			= "The file was written successfully.";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.LOCATION_PANE_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
					.desc(StyleClass.LOCATION_PANE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.LOCATION_PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
					.desc(StyleClass.LOCATION_PANE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BUTTON_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
					.desc(GraphicButton.StyleClass.GRAPHIC_BUTTON).pseudo(GraphicButton.PseudoClassKey.INACTIVE)
					.desc(GraphicButton.StyleClass.INNER_VIEW)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.BUTTON_BORDER,
			CssSelector.builder()
					.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
					.desc(GraphicButton.StyleClass.GRAPHIC_BUTTON).pseudo(GraphicButton.PseudoClassKey.INACTIVE)
					.desc(GraphicButton.StyleClass.INNER_VIEW)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.OFFSET_LABEL_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
					.desc(StyleClass.OFFSET_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.OFFSET_LABEL_BORDER,
			CssSelector.builder()
					.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
					.desc(StyleClass.OFFSET_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
					.desc(StyleClass.SAVE_SECTOR_CLUSTER_PANE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
					.desc(StyleClass.CONTENT_PANE)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
						.desc(StyleClass.SECTOR_CLUSTER_DATA_AREA)
						.desc(FxStyleClass.TEXT)
						.build())
				.grayFontSmoothing()
				.build(),
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
						.desc(StyleClass.OFFSET_LABEL)
						.desc(FxStyleClass.TEXT)
						.build())
				.grayFontSmoothing()
				.build(),
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
						.desc(StyleClass.LOCATION_PANE)
						.build())
				.borders(Side.TOP, Side.BOTTOM)
				.build(),
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
						.desc(StyleClass.SAVE_SECTOR_CLUSTER_PANE)
						.build())
				.borders(Side.TOP)
				.build(),
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT)
						.desc(StyleClass.CONTENT_PANE)
						.build())
				.borders(Side.BOTTOM)
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	SECTOR_CLUSTER_VIEW_DIALOG_ROOT	= StyleConstants.APP_CLASS_PREFIX + "sector-cluster-view-dialog-root";

		String	CONTENT_PANE				= StyleConstants.CLASS_PREFIX + "content-pane";
		String	LOCATION_PANE				= StyleConstants.CLASS_PREFIX + "location-pane";
		String	OFFSET_LABEL				= StyleConstants.CLASS_PREFIX + "offset-label";
		String	SAVE_SECTOR_CLUSTER_PANE	= StyleConstants.CLASS_PREFIX + "save-sector-cluster-pane";
		String	SECTOR_CLUSTER_DATA_AREA	= StyleConstants.CLASS_PREFIX + "sector-cluster-data-area";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	BUTTON_BACKGROUND			= PREFIX + "button.background";
		String	BUTTON_BORDER				= PREFIX + "button.border";
		String	LOCATION_PANE_BACKGROUND	= PREFIX + "locationPane.background";
		String	LOCATION_PANE_BORDER		= PREFIX + "locationPane.border";
		String	OFFSET_LABEL_BACKGROUND		= PREFIX + "offsetLabel.background";
		String	OFFSET_LABEL_BORDER			= PREFIX + "offsetLabel.border";
		String	OPERATION_BUTTON_BACKGROUND	= PREFIX + "operationButton.background";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FAILED_TO_OPEN_FILE =
				"Failed to open the file.";

		String	FAILED_TO_CLOSE_FILE =
				"Failed to close the file.";

		String	FAILED_TO_LOCK_FILE =
				"Failed to lock the file.";

		String	FAILED_TO_READ_FILE_ATTRIBUTES =
				"Failed to read the attributes of the file.";

		String	FAILED_TO_CREATE_DIRECTORY =
				"Failed to create the directory.";

		String	FAILED_TO_CREATE_TEMPORARY_FILE =
				"Failed to create a temporary file.";

		String	FAILED_TO_DELETE_FILE =
				"Failed to delete the existing file.";

		String	FAILED_TO_RENAME_FILE =
				"Temporary file: %s\nFailed to rename the temporary file to the specified filename.";

		String	ERROR_WRITING_FILE =
				"An error occurred when writing the file.";

		String	NO_DIRECTORY =
				"No directory was specified.";

		String	NO_FILENAME_COMPONENTS =
				"No prefix, suffix or index was specified.";

		String	SECTOR_INDEX_OUT_OF_BOUNDS =
				"Sector %d\nA sector index may not be greater than " + Integer.MAX_VALUE + ".";

		String	NO_CORRESPONDING_CLUSTER =
				"No cluster corresponds to sector %d.";

		String	ERROR_READING_SECTORS =
				"An error occurred when reading sectors from the volume.";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	State					state				= new State();
	private static	SimpleIntegerProperty	absSectorIndex		= new SimpleIntegerProperty(MIN_SECTOR_INDEX);
	private static	SimpleIntegerProperty	absClusterIndex		= new SimpleIntegerProperty(MIN_ABS_CLUSTER_INDEX);
	private static	Deque<Integer>			absSectorHistory	= new LinkedList<>();
	private static	Deque<Integer>			absClusterHistory	= new LinkedList<>();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Fat32Volume				volume;
	private	Fat32Fat.IndexFinder	indexFinder;
	private	boolean					chainMode;
	private	int						maxSectorIndex;
	private	int						minClusterIndex;
	private	int						maxClusterIndex;
	private int						chainSectorIndex;
	private	int						chainClusterIndex;
	private	Deque<Integer>			chainSectorHistory;
	private	Deque<Integer>			chainClusterHistory;
	private	int						numOffsetDigitsDec;
	private	int						numOffsetDigitsHex;
	private	boolean					updatingSpinners;
	private	DataArea				dataArea;
	private	Spinner<Integer>		sectorIndexSpinner;
	private	ImageDataButton			sectorHistoryButton;
	private	Spinner<Integer>		clusterIndexSpinner;
	private	ImageDataButton			clusterHistoryButton;
	private	Label					decOffsetLabel;
	private	Label					hexOffsetLabel;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(SectorClusterViewDialog.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a modal dialog that contains a navigable view of the sectors or clusters of a volume or
	 * the cluster chain of a file or directory.
	 *
	 * @param owner
	 *          the window that will own this dialog, or {@code null} for a top-level dialog that has no owner.
	 * @param volume
	 *          the volume whose sectors or clusters will be read and displayed.
	 * @param initialDataUnit
	 *          the initial data unit (sector or cluster) of the dialog.
	 * @param indexFinder
	 *          the object that is used to navigate a cluster chain, or {@code null} if there is no cluster chain.
	 * @param initialIndex
	 *          the index of the sector or cluster that will be initially read and displayed; ignored if {@code
	 *          indexFinder} is not {@code null}.  If the index is negative and {@code indexFinder} is {@code null}, the
	 *          last sector or cluster to be displayed in the previous instance of the dialog will be used.
	 */

	private SectorClusterViewDialog(
		Window					owner,
		Fat32Volume				volume,
		DataUnit				initialDataUnit,
		Fat32Fat.IndexFinder	indexFinder,
		int						initialIndex)
	{
		// Call superclass constructor
		super(owner, Utils.volumeDisplayName(volume), state.getLocator(), state.getSize());

		// Set style class on root node of scene graph
		getScene().getRoot().getStyleClass().add(StyleClass.SECTOR_CLUSTER_VIEW_DIALOG_ROOT);

		// Set data unit
		state.dataUnit = initialDataUnit;

		// Initialise instance variables
		this.volume = volume;
		this.indexFinder = indexFinder;
		chainMode = (indexFinder != null);
		chainSectorHistory = new LinkedList<>();
		chainClusterHistory = new LinkedList<>();

		// Get number of decimal digits in offset
		numOffsetDigitsDec = NumberUtils.getNumDecDigitsInt(volume.getBytesPerCluster() - 1);

		// Get number of hex digits in offset field
		int mask = Integer.highestOneBit(volume.getBytesPerCluster() - DataArea.BYTES_PER_LINE);
		while (mask != 0)
		{
			++numOffsetDigitsHex;
			mask >>>= 4;
		}

		// Set properties
		setResizable(true);

		// Create absolute-location title label
		Label absLocationTitleLabel = Labels.hNoShrink(ABS_TITLE_STR);
		absLocationTitleLabel.setFont(FontUtils.boldFont());

		// Create absolute location label
		Label absLocationLabel = Labels.hNoShrink();

		// Create chain-location title label
		Label chainLocationTitleLabel = Labels.hNoShrink(CHAIN_TITLE_STR);
		chainLocationTitleLabel.setFont(FontUtils.boldFont());

		// Create chain location label
		Label chainLocationLabel = Labels.hNoShrink();

		// Create filler
		Region locationPaneFiller = FillerUtils.hBoxFiller(4.0);

		// Create location pane
		HBox locationPane = new HBox(LOCATION_PANE_GAP);
		locationPane.setAlignment(Pos.CENTER_LEFT);
		locationPane.setPadding(LOCATION_PANE_PADDING);
		locationPane.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.LOCATION_PANE_BACKGROUND)));
		locationPane.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.LOCATION_PANE_BORDER),
															Side.TOP, Side.BOTTOM));
		locationPane.getStyleClass().add(StyleClass.LOCATION_PANE);

		// Create text area for sector or cluster data
		dataArea = new DataArea();
		VBox.setVgrow(dataArea, Priority.ALWAYS);

		// Set dimensions of data area
		if (state.dataAreaSize == null)
		{
			// Calculate height of line of text
			double lineHeight = TextUtils.textHeight();

			// Adjust for caret height
			lineHeight += 2.0;

			// Set preferred height of data area
			dataArea.setPrefHeight(lineHeight * DataArea.DEFAULT_NUM_ROWS + 7.0);
		}
		else
			dataArea.setPrefSize(state.dataAreaSize.getWidth(), state.dataAreaSize.getHeight());

		// Initialise row index
		int row = 0;

		// Create procedure to right-align items of context menu
		IProcedure1<ContextMenu> alignMenuItems = menu ->
		{
			menu.setOnShown(event0 ->
			{
				// Get maximum width of menu-item label
				double maxWidth = 0.0;
				for (MenuItem menuItem : menu.getItems())
				{
					if (menuItem.getStyleableNode().lookup(StyleSelector.LABEL) instanceof Label label)
						maxWidth = Math.max(label.getWidth(), maxWidth);
				}

				// Set width of menu-item labels to that of widest label
				for (MenuItem menuItem : menu.getItems())
				{
					if (menuItem.getStyleableNode() instanceof Region container)
					{
						container.setPadding(MENU_ITEM_PADDING);
						if (container.lookup(StyleSelector.LABEL) instanceof Label label)
						{
							label.setAlignment(Pos.CENTER_RIGHT);
							label.setPrefWidth(maxWidth);
						}
					}
				}
			});
		};

		// Create factory for navigation button
		IFunction2<ImageDataButton, String, String> navigationButtonFactory = (id, text) ->
		{
			ImageDataButton button = Images.imageButton(id, text);
			button.setBackgroundColour(getColour(ColourKey.BUTTON_BACKGROUND));
			button.setBorderColour(getColour(ColourKey.BUTTON_BORDER));
			button.setPadding(BUTTON_PADDING);
			return button;
		};

		// Create spinner: sector index
		sectorIndexSpinner = SpinnerFactory.integerSpinner(MIN_SECTOR_INDEX, maxSectorIndex, getSectorIndex(),
														   INDEX_SPINNER_NUM_DIGITS);
		sectorIndexSpinner.valueProperty().addListener((observable, oldIndex, index) ->
		{
			if (!updatingSpinners)
				goToSector(index);
		});

		// Create button: sector history
		sectorHistoryButton = navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_DOWN, SECTOR_HISTORY_STR);
		sectorHistoryButton.setOnAction(event ->
		{
			// Create context menu
			ContextMenu menu = new ContextMenu();

			// Add items for previous indices to context menu
			for (Integer index : chainMode ? chainSectorHistory : absSectorHistory)
			{
				MenuItem menuItem = new MenuItem(index.toString());
				menuItem.setOnAction(event0 -> goToSector(index));
				menu.getItems().add(menuItem);
			}

			// Right-align menu items
			alignMenuItems.invoke(menu);

			// Display context menu below button
			Bounds bounds = sectorHistoryButton.localToScreen(sectorHistoryButton.getLayoutBounds());
			menu.show(this, bounds.getMinX(), bounds.getMaxY());
		});
		HBox.setMargin(sectorHistoryButton, new Insets(0.0, 0.0, 0.0, 2.0));

		// Create button: first sector
		ImageDataButton firstSectorButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_BAR_LEFT, FIRST_SECTOR_STR);
		firstSectorButton.setOnAction(event -> goToSector(MIN_SECTOR_INDEX));

		// Create button: sectors back
		ImageDataButton sectorsBackButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_DOUBLE_LEFT, SECTORS_BACK_STR);
		sectorsBackButton.setOnAction(event -> goToSector(getSectorIndex() - NAVIGATION_BLOCK_LENGTH));

		// Create button: previous sector
		ImageDataButton prevSectorButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_LEFT, PREVIOUS_SECTOR_STR);
		prevSectorButton.setOnAction(event -> goToSector(getSectorIndex() - 1));

		// Create button: next sector
		ImageDataButton nextSectorButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_RIGHT, NEXT_SECTOR_STR);
		nextSectorButton.setOnAction(event -> goToSector(getSectorIndex() + 1));

		// Create button: sectors forward
		ImageDataButton sectorsForwardButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_DOUBLE_RIGHT, SECTORS_FORWARD_STR);
		sectorsForwardButton.setOnAction(event -> goToSector(getSectorIndex() + NAVIGATION_BLOCK_LENGTH));

		// Create button: last sector
		ImageDataButton lastSectorButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_BAR_RIGHT, LAST_SECTOR_STR);
		lastSectorButton.setOnAction(event -> goToSector(maxSectorIndex));

		// Create sector navigation-button pane
		HBox sectorButtonPane = new HBox(4.0, firstSectorButton, sectorsBackButton, prevSectorButton, nextSectorButton,
										 sectorsForwardButton, lastSectorButton);
		sectorButtonPane.setAlignment(Pos.CENTER_LEFT);
		sectorButtonPane.setPadding(NAVIGATION_BUTTON_PANE_PADDING);

		// Create procedure to update location text and sector navigation buttons
		IProcedure0 updateSector = () ->
		{
			// Create function to create location text
			IFunction2<String, Integer, Boolean> createLocationText = (index, chain) ->
			{
				String text = SECTOR_STR + " " + index;
				Fat32Volume.ClusterIndex indices = chain ? chainSectorIndexToClusterIndex(index)
														 : volume.sectorIndexToClusterIndex(index);
				if (indices != null)
					text += "  \u2022  " + CLUSTER_STR + " " + indices.clusterIndex() + "." + indices.sectorIndex();
				return text;
			};

			// Update location text
			absLocationLabel.setText(createLocationText.invoke(getAbsSectorIndex(), false));
			if (chainMode)
				chainLocationLabel.setText(createLocationText.invoke(chainSectorIndex, true));

			// Update navigation buttons
			int index = chainMode ? chainSectorIndex : getAbsSectorIndex();
			firstSectorButton.setDisable(index == MIN_SECTOR_INDEX);
			sectorsBackButton.setDisable(index == MIN_SECTOR_INDEX);
			prevSectorButton.setDisable(index == MIN_SECTOR_INDEX);
			nextSectorButton.setDisable(index == maxSectorIndex);
			sectorsForwardButton.setDisable(index == maxSectorIndex);
			lastSectorButton.setDisable(index == maxSectorIndex);
		};

		// Update title and sector navigation buttons
		updateSector.invoke();

		// Update title and navigation buttons when absolute sector index changes
		absSectorIndex.addListener(observable -> updateSector.invoke());

		// Create sector navigation pane
		HBox sectorNavigationPane =
				new HBox(NAVIGATION_PANE_GAP, sectorIndexSpinner, sectorHistoryButton, sectorButtonPane);
		sectorNavigationPane.setAlignment(Pos.CENTER_LEFT);
		sectorNavigationPane.setVisible(false);

		// Create spinner: cluster index
		clusterIndexSpinner = SpinnerFactory.integerSpinner(minClusterIndex, maxClusterIndex, getClusterIndex(),
															INDEX_SPINNER_NUM_DIGITS);
		clusterIndexSpinner.valueProperty().addListener((observable, oldIndex, index) ->
		{
			if (!updatingSpinners)
				goToCluster(index);
		});

		// Create button: cluster history
		clusterHistoryButton = navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_DOWN, CLUSTER_HISTORY_STR);
		clusterHistoryButton.setOnAction(event ->
		{
			// Create context menu
			ContextMenu menu = new ContextMenu();

			// Add items for previous indices to context menu
			for (Integer index : chainMode ? chainClusterHistory : absClusterHistory)
			{
				MenuItem menuItem = new MenuItem(index.toString());
				menuItem.setOnAction(event0 -> goToCluster(index));
				menu.getItems().add(menuItem);
			}

			// Right-align menu items
			alignMenuItems.invoke(menu);

			// Display context menu below button
			Bounds bounds = clusterHistoryButton.localToScreen(clusterHistoryButton.getLayoutBounds());
			menu.show(this, bounds.getMinX(), bounds.getMaxY());
		});
		HBox.setMargin(clusterHistoryButton, new Insets(0.0, 0.0, 0.0, 2.0));

		// Create button: first cluster
		ImageDataButton firstClusterButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_BAR_LEFT, FIRST_CLUSTER_STR);
		firstClusterButton.setOnAction(event -> goToCluster(minClusterIndex));

		// Create button: clusters back
		ImageDataButton clustersBackButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_DOUBLE_LEFT, CLUSTERS_BACK_STR);
		clustersBackButton.setOnAction(event -> goToCluster(getClusterIndex() - NAVIGATION_BLOCK_LENGTH));

		// Create button: previous cluster
		ImageDataButton prevClusterButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_LEFT, PREVIOUS_CLUSTER_STR);
		prevClusterButton.setOnAction(event -> goToCluster(getClusterIndex() - 1));

		// Create button: next cluster
		ImageDataButton nextClusterButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_RIGHT, NEXT_CLUSTER_STR);
		nextClusterButton.setOnAction(event -> goToCluster(getClusterIndex() + 1));

		// Create button: clusters forward
		ImageDataButton clustersForwardButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_DOUBLE_RIGHT, CLUSTERS_FORWARD_STR);
		clustersForwardButton.setOnAction(event -> goToCluster(getClusterIndex() + NAVIGATION_BLOCK_LENGTH));

		// Create button: last cluster
		ImageDataButton lastClusterButton =
				navigationButtonFactory.invoke(Images.ImageId.ARROWHEAD_BAR_RIGHT, LAST_CLUSTER_STR);
		lastClusterButton.setOnAction(event -> goToCluster(maxClusterIndex));

		// Create cluster navigation-button pane
		HBox clusterButtonPane = new HBox(4.0, firstClusterButton, clustersBackButton, prevClusterButton,
										  nextClusterButton, clustersForwardButton, lastClusterButton);
		clusterButtonPane.setAlignment(Pos.CENTER_LEFT);
		clusterButtonPane.setPadding(NAVIGATION_BUTTON_PANE_PADDING);

		// Create procedure to update location text and cluster navigation buttons
		IProcedure0 updateCluster = () ->
		{
			// Create function to create location text
			IFunction2<String, Integer, Boolean> createLocationText = (index, chain) ->
			{
				long sectorIndex = chain ? index * volume.getSectorsPerCluster()
										 : volume.clusterIndexToSectorIndex(index);
				return CLUSTER_STR + " " + index + "  \u2022  " + SECTORS_STR + " " + sectorIndex + "\u2013"
						+ (sectorIndex + volume.getSectorsPerCluster() - 1);
			};

			// Update location text
			absLocationLabel.setText(createLocationText.invoke(getAbsClusterIndex(), false));
			if (chainMode)
				chainLocationLabel.setText(createLocationText.invoke(chainClusterIndex, true));

			// Update navigation buttons
			int index = chainMode ? chainClusterIndex : getAbsClusterIndex();
			firstClusterButton.setDisable(index == minClusterIndex);
			clustersBackButton.setDisable(index == minClusterIndex);
			prevClusterButton.setDisable(index == minClusterIndex);
			nextClusterButton.setDisable(index == maxClusterIndex);
			clustersForwardButton.setDisable(index == maxClusterIndex);
			lastClusterButton.setDisable(index == maxClusterIndex);
		};

		// Update title and cluster navigation buttons
		updateCluster.invoke();

		// Update title and navigation buttons when cluster index changes
		absClusterIndex.addListener(observable -> updateCluster.invoke());

		// Create cluster navigation pane
		HBox clusterNavigationPane = new HBox(NAVIGATION_PANE_GAP, clusterIndexSpinner, clusterHistoryButton,
											  clusterButtonPane);
		clusterNavigationPane.setAlignment(Pos.CENTER_LEFT);
		clusterNavigationPane.setVisible(false);

		// Create navigation pane
		StackPane navigationPane = new StackPane(sectorNavigationPane, clusterNavigationPane);

		// Create slide-button pane
		GridPane slideButtonPane = new GridPane();
		slideButtonPane.setHgap(5.0);
		slideButtonPane.setVgap(CONTROL_V_GAP);
		slideButtonPane.setAlignment(Pos.CENTER);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setHalignment(HPos.RIGHT);
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setFillWidth(false);
		slideButtonPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		column.setFillWidth(false);
		slideButtonPane.getColumnConstraints().add(column);

		// Initialise row index
		row = 0;

		// Create 'chain' label
		Label chainLabel = new Label(CHAIN_STR);
		chainLabel.setDisable(indexFinder == null);

		// Create 'chain' button
		SlideButton chainButton = new SlideButton();
		chainButton.setPadding(BUTTON_PADDING);
		chainButton.setSelected(chainMode);
		chainButton.setDisable(indexFinder == null);
		slideButtonPane.addRow(row++, chainLabel, chainButton);

		// Create label: save sector/cluster
		Label saveSectorClusterLabel = new Label();
		MultiTextLabeller<DataUnit> saveSectorClusterLabeller =
				new MultiTextLabeller<>(saveSectorClusterLabel,
										Arrays.stream(DataUnit.values())
												.collect(Collectors.toMap(unit -> unit,
																		  unit -> SAVE_STR + " " + unit.lcText)));

		// Create 'save' button
		SlideButton saveButton = new SlideButton();
		saveButton.setSelected(!state.savePaneHidden);
		slideButtonPane.addRow(row++, saveSectorClusterLabel, saveButton);

		// Create function to return tooltip text of data-unit button
		IFunction0<String> dataUnitButtonTooltipText = () ->
				String.format(SWITCH_SECTOR_CLUSTER_STR, state.dataUnit.next().lcText);

		// Create data-unit button
		ImageDataButton dataUnitButton =
				Images.imageButton(Images.ImageId.SECTOR_OR_CLUSTER, dataUnitButtonTooltipText.invoke());
		dataUnitButton.setBackgroundColour(getColour(ColourKey.BUTTON_BACKGROUND));
		dataUnitButton.setBorderColour(getColour(ColourKey.BUTTON_BORDER));
		dataUnitButton.setPadding(BUTTON_PADDING);

		// Create data-unit label
		Label dataUnitLabel = Labels.hNoShrink();
		MultiTextLabeller<DataUnit> sectorClusterLabeller =
				new MultiTextLabeller<>(dataUnitLabel,
										Arrays.stream(DataUnit.values())
												.collect(Collectors.toMap(unit -> unit, unit -> unit.text)));

		// Create data-unit pane
		HBox dataUnitPane = new HBox(DATA_UNIT_PANE_GAP, dataUnitButton, dataUnitLabel);
		dataUnitPane.setAlignment(Pos.CENTER_LEFT);

		// Create central control pane
		HBox centralControlPane = new HBox(CONTROL_H_GAP, dataUnitPane, navigationPane);
		centralControlPane.setAlignment(Pos.CENTER_LEFT);

		// Create pane for saving a sector or cluster
		SaveSectorClusterPane saveSectorClusterPane = new SaveSectorClusterPane();
		saveSectorClusterPane.setPadding(SAVE_SECTOR_CLUSTER_PANE_PADDING);
		saveSectorClusterPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER),
																	 Side.TOP));
		saveSectorClusterPane.getStyleClass().add(StyleClass.SAVE_SECTOR_CLUSTER_PANE);

		// Create procedure to update state and components according to chain mode
		IProcedure0 updateChainMode = () ->
		{
			if (chainMode)
			{
				// Update sector and cluster bounds
				minClusterIndex = 0;
				maxClusterIndex = indexFinder.getMaxChainIndex();
				maxSectorIndex = (maxClusterIndex + 1) * volume.getSectorsPerCluster() - 1;

				// Update children of location pane
				locationPane.getChildren().setAll(absLocationTitleLabel, absLocationLabel, locationPaneFiller,
												  chainLocationTitleLabel, chainLocationLabel);

				// Update history buttons
				sectorHistoryButton.setDisable(chainSectorHistory.isEmpty());
				clusterHistoryButton.setDisable(chainClusterHistory.isEmpty());
			}
			else
			{
				// Update sector and cluster bounds
				long numSectors = volume.getNumSectors();
				maxSectorIndex = (numSectors > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int)(numSectors - 1);
				minClusterIndex = MIN_ABS_CLUSTER_INDEX;
				maxClusterIndex = volume.getMaxClusterIndex();

				// Update children of location pane
				locationPane.getChildren().setAll(absLocationLabel);

				// Update history buttons
				sectorHistoryButton.setDisable(absSectorHistory.isEmpty());
				clusterHistoryButton.setDisable(absClusterHistory.isEmpty());
			}

			// Update location text and cluster navigation buttons
			updateSector.invoke();
			updateCluster.invoke();

			// Prevent listeners on spinners from updating sector/cluster
			updatingSpinners = true;

			// Update bounds of sector-index spinner
			SpinnerValueFactory<Integer> factory = sectorIndexSpinner.getValueFactory();
			if (factory instanceof SpinnerValueFactory.IntegerSpinnerValueFactory integerFactory)
			{
				integerFactory.setMax(maxSectorIndex);
				integerFactory.setValue(MIN_SECTOR_INDEX);
			}

			// Update bounds of cluster-index spinner
			factory = clusterIndexSpinner.getValueFactory();
			if (factory instanceof SpinnerValueFactory.IntegerSpinnerValueFactory integerFactory)
			{
				integerFactory.setMin(minClusterIndex);
				integerFactory.setMax(maxClusterIndex);
				integerFactory.setValue(minClusterIndex);
			}

			// Allow listeners on spinners to update sector/cluster
			updatingSpinners = false;
		};

		// Toggle chain mode when 'chain' button is fired
		if (chainButton != null)
		{
			chainButton.setOnAction(event ->
			{
				// Toggle chain mode
				chainMode = !chainMode;

				// Update state and components
				updateChainMode.invoke();

				// Update sector or cluster
				switch (state.dataUnit)
				{
					case SECTOR:
						// Read sector
						readSector(getSectorIndex());

						// Update title and sector navigation buttons
						updateSector.invoke();
						break;

					case CLUSTER:
						// Read cluster
						readCluster(getClusterIndex());

						// Update title and cluster navigation buttons
						updateCluster.invoke();
						break;
				}
			});
		}

		// Update state and components according to chain mode
		updateChainMode.invoke();

		// Create procedure to update components according to data unit
		IProcedure0 updateDataUnit = () ->
		{
			// Update tooltip of data-unit button
			dataUnitButton.setTooltipText(dataUnitButtonTooltipText.invoke());

			// Update text of labels
			sectorClusterLabeller.selectText(state.dataUnit);
			saveSectorClusterLabeller.selectText(state.dataUnit);

			// Show navigation pane for data unit
			switch (state.dataUnit)
			{
				case SECTOR:
					sectorNavigationPane.setVisible(true);
					clusterNavigationPane.setVisible(false);
					updateSector.invoke();
					break;

				case CLUSTER:
					sectorNavigationPane.setVisible(false);
					clusterNavigationPane.setVisible(true);
					updateCluster.invoke();
					break;
			}

			// Update 'save' pane
			saveSectorClusterPane.updateDataUnit();
		};

		// Change data unit when data-unit button is fired
		dataUnitButton.setOnAction(event ->
		{
			// Update dialog state
			state.dataUnit = state.dataUnit.next();

			// Update components
			updateDataUnit.invoke();

			// Read sector or cluster
			String title = String.format(SWITCH_SECTOR_CLUSTER_STR, state.dataUnit.lcText);
			switch (state.dataUnit)
			{
				case SECTOR:
				{
					int index = 0;
					if (chainMode)
						index = chainClusterIndex * volume.getSectorsPerCluster();
					else
					{
						long index0 = volume.clusterIndexToSectorIndex(getAbsClusterIndex());
						if (index0 > Integer.MAX_VALUE)
						{
							WarningDialog.show(getWindow(), title,
											   String.format(ErrorMsg.SECTOR_INDEX_OUT_OF_BOUNDS, index0));
							index0 = MIN_SECTOR_INDEX;
						}
						index = (int)index0;
					}
					readSector(index);
					break;
				}

				case CLUSTER:
				{
					Fat32Volume.ClusterIndex indices = null;
					if (chainMode)
						indices = chainSectorIndexToClusterIndex(chainSectorIndex);
					else
					{
						indices = volume.sectorIndexToClusterIndex(getAbsSectorIndex());
						if (indices == null)
						{
							WarningDialog.show(getWindow(), title,
											   String.format(ErrorMsg.NO_CORRESPONDING_CLUSTER, getAbsSectorIndex()));
							indices = new Fat32Volume.ClusterIndex(MIN_ABS_CLUSTER_INDEX, 0);
						}
					}
					readCluster(indices.clusterIndex());
					break;
				}
			}
		});

		// Update components according to data unit
		updateDataUnit.invoke();

		// Get width of offset text
		double textWidth = Math.max(TextUtils.textWidth(Fonts.monoFont(), "0".repeat(numOffsetDigitsDec)),
									TextUtils.textWidth(Fonts.monoFont(), "D".repeat(numOffsetDigitsHex)));

		// Create factory for offset labels
		IFunction1<Label, String> offsetLabelFactory = tooltipText ->
		{
			Label label = new Label();
			label.setMinWidth(Region.USE_PREF_SIZE);
			label.setMaxWidth(Double.MAX_VALUE);
			label.setAlignment(Pos.CENTER_RIGHT);
			label.setFont(Fonts.monoFont());
			label.setPadding(OFFSET_LABEL_PADDING);
			label.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.OFFSET_LABEL_BACKGROUND)));
			label.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.OFFSET_LABEL_BORDER)));
			Insets insets = label.getInsets();
			label.setPrefWidth(Math.ceil(textWidth + insets.getLeft() + insets.getRight()));
			label.getStyleClass().add(StyleClass.OFFSET_LABEL);
			TooltipDecorator.addTooltip(label, tooltipText);
			return label;
		};

		// Create offset labels
		decOffsetLabel = offsetLabelFactory.invoke(OFFSET_DEC_STR);
		hexOffsetLabel = offsetLabelFactory.invoke(OFFSET_HEX_STR);

		// Create offset pane
		VBox offsetPane = new VBox(3.0, decOffsetLabel, hexOffsetLabel);
		offsetPane.setAlignment(Pos.CENTER_LEFT);

		// Create control pane
		HBox controlPane = new HBox(OUTER_CONTROL_PANE_GAP, slideButtonPane, centralControlPane, offsetPane);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(OUTER_CONTROL_PANE_PADDING);

		// Create content pane
		VBox contentPane = new VBox(locationPane, dataArea, controlPane);
		if (!state.savePaneHidden)
			contentPane.getChildren().add(saveSectorClusterPane);
		contentPane.setAlignment(Pos.TOP_CENTER);
		contentPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER), Side.BOTTOM));
		contentPane.getStyleClass().add(StyleClass.CONTENT_PANE);

		// Show or hide 'save' pane when 'selected' state of 'save' button changes
		saveButton.selectedProperty().addListener((observable, oldSelected, selected) ->
		{
			// Set preferred size of sector area to its current size
			dataArea.setPrefSize(dataArea.getWidth(), dataArea.getHeight());

			// Case: show 'save' pane
			if (selected)
				contentPane.getChildren().add(saveSectorClusterPane);

			// Case: hide 'save' pane
			else
				contentPane.getChildren().remove(saveSectorClusterPane);

			// Resize window
			sizeToScene();
		});

		// Set content pane as content
		setContent(contentPane);

		// Create button: copy
		Button copyButton = Buttons.hNoShrink(COPY_STR);
		copyButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		copyButton.setOnAction(event ->
		{
			// Get text that is selected in sector area
			String text = dataArea.getSelectedText();

			// If no text is selected, use entire text
			if (text.isEmpty())
				text = dataArea.getText() + "\n";

			// Put text on clipboard
			Utils.copyToClipboard(this, COPY_STR, text);
		});
		addButton(copyButton, HPos.LEFT);

		// Create button: close
		Button closeButton = Buttons.hNoShrink(CLOSE_STR);
		closeButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		closeButton.setOnAction(event -> requestClose());
		addButton(closeButton, HPos.RIGHT);

		// Fire 'close' button if Escape key is pressed
		setKeyFireButton(closeButton, null);

		// Save state of window when dialog is closed
		setOnHiding(event ->
		{
			// Update state
			state.savePaneHidden = !saveButton.isSelected();
			state.dataAreaSize = new Dimension2D(dataArea.getWidth(), dataArea.getHeight());
			saveSectorClusterPane.updateState();

			// Save state of window
			state.restoreAndUpdate(this, true);
		});

		// Fire 'copy' button if Ctrl+C is pressed
		addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if (KEY_COMBO_COPY.match(event) && (!dataArea.isFocused() || (dataArea.getSelection().getLength() == 0)))
			{
				copyButton.fire();
				event.consume();
			}
		});

		// Change cursor of data-area content to default when Ctrl key is pressed
		addEventFilter(KeyEvent.ANY, event ->
		{
			if (event.getCode() == KeyCode.CONTROL)
			{
				Node content = dataArea.lookup(StyleSelector.TEXT_AREA_CONTENT);
				if (content != null)
				{
					EventType<KeyEvent> eventType = event.getEventType();
					if (eventType == KeyEvent.KEY_PRESSED)
						content.setCursor(Cursor.DEFAULT);
					else if (eventType == KeyEvent.KEY_RELEASED)
						content.setCursor(Cursor.TEXT);
				}
			}
		});

		// Read and display initial sector or cluster
		switch (state.dataUnit)
		{
			case SECTOR:
				// Request focus on sector-index spinner
				Platform.runLater(() ->
				{
					sectorIndexSpinner.getEditor().requestFocus();
					sectorIndexSpinner.getEditor().selectAll();
				});

				// Read sector and display it
				readSector((initialIndex < 0) ? getSectorIndex() : clampSectorIndex(initialIndex));
				break;

			case CLUSTER:
				// Request focus on cluster-index spinner
				Platform.runLater(() ->
				{
					clusterIndexSpinner.getEditor().requestFocus();
					clusterIndexSpinner.getEditor().selectAll();
				});

				// Read cluster and display it
				readCluster((initialIndex < 0) ? getClusterIndex() : clampClusterIndex(initialIndex));
				break;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and displays a new instance of a modal dialog that contains a navigable view of the sectors or clusters
	 * of a volume.  The initial data unit is <i>sector</i>.
	 *
	 * @param owner
	 *          the window that will own this dialog, or {@code null} for a top-level dialog that has no owner.
	 * @param volume
	 *          the volume whose sectors or clusters will be read and displayed.
	 * @param initialIndex
	 *          the index of the sector or cluster that will be initially read and displayed.  If it is negative, the
	 *          last sector or cluster to be displayed in the previous instance of the dialog will be used.
	 */

	public static void showSector(
		Window		owner,
		Fat32Volume	volume,
		long		initialIndex)
	{
		// Validate sector index
		if (initialIndex > Integer.MAX_VALUE)
		{
			ErrorDialog.show(owner, Utils.volumeDisplayName(volume) + " - " + SECTOR_STR,
							 String.format(ErrorMsg.SECTOR_INDEX_OUT_OF_BOUNDS, initialIndex));
			return;
		}

		// Create dialog and display it
		new SectorClusterViewDialog(owner, volume, DataUnit.SECTOR, null, (int)initialIndex).showDialog();
	}

	//------------------------------------------------------------------

	public static void showSector(
		Window					owner,
		Fat32Volume				volume,
		Fat32Fat.IndexFinder	indexFinder)
	{
		new SectorClusterViewDialog(owner, volume, DataUnit.SECTOR, indexFinder, -1).showDialog();
	}

	//------------------------------------------------------------------

	public static void showCluster(
		Window		owner,
		Fat32Volume	volume,
		int			initialIndex)
	{
		new SectorClusterViewDialog(owner, volume, DataUnit.CLUSTER, null, initialIndex).showDialog();
	}

	//------------------------------------------------------------------

	public static void showCluster(
		Window					owner,
		Fat32Volume				volume,
		Fat32Fat.IndexFinder	indexFinder)
	{
		new SectorClusterViewDialog(owner, volume, DataUnit.CLUSTER, indexFinder, -1).showDialog();
	}

	//------------------------------------------------------------------

	public static void reset()
	{
		absSectorIndex.set(MIN_SECTOR_INDEX);
		absClusterIndex.set(MIN_ABS_CLUSTER_INDEX);
		absSectorHistory.clear();
		absClusterHistory.clear();
	}

	//------------------------------------------------------------------

	public static MapNode encodeState()
	{
		return state.encodeTree();
	}

	//------------------------------------------------------------------

	public static void decodeState(
		MapNode	mapNode)
	{
		state.decodeTree(mapNode);
	}

	//------------------------------------------------------------------

	private static int getAbsSectorIndex()
	{
		return absSectorIndex.get();
	}

	//------------------------------------------------------------------

	private static int getAbsClusterIndex()
	{
		return absClusterIndex.get();
	}

	//------------------------------------------------------------------

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

	private int getSectorIndex()
	{
		return chainMode ? chainSectorIndex : getAbsSectorIndex();
	}

	//------------------------------------------------------------------

	private int getClusterIndex()
	{
		return chainMode ? chainClusterIndex : getAbsClusterIndex();
	}

	//------------------------------------------------------------------

	private int clampSectorIndex(
		int	index)
	{
		return Math.min(Math.max(MIN_SECTOR_INDEX, index), maxSectorIndex);
	}

	//------------------------------------------------------------------

	private int clampClusterIndex(
		int	index)
	{
		return Math.min(Math.max(minClusterIndex, index), maxClusterIndex);
	}

	//------------------------------------------------------------------

	private Fat32Volume.ClusterIndex chainSectorIndexToClusterIndex(
		int	index)
	{
		int sectorsPerCluster = volume.getSectorsPerCluster();
		return new Fat32Volume.ClusterIndex(index / sectorsPerCluster, index % sectorsPerCluster);
	}

	//------------------------------------------------------------------

	private long chainSectorIndexToAbs(
		int	index)
	{
		try
		{
			Fat32Volume.ClusterIndex clusterIndex = chainSectorIndexToClusterIndex(index);
			return volume.clusterIndexToSectorIndex(
					indexFinder.find(clusterIndex.clusterIndex())) + clusterIndex.sectorIndex();
		}
		catch (VolumeException e)
		{
			Logger.INSTANCE.error(e);
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

	private int chainClusterIndexToAbs(
		int	index)
	{
		try
		{
			return indexFinder.find(index);
		}
		catch (VolumeException e)
		{
			Logger.INSTANCE.error(e);
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

	private void goToSector(
		int	index)
	{
		index = clampSectorIndex(index);
		if (index != getSectorIndex())
			readSector(index);
	}

	//------------------------------------------------------------------

	private void goToCluster(
		int	index)
	{
		index = clampClusterIndex(index);
		if (index != getClusterIndex())
			readCluster(index);
	}

	//------------------------------------------------------------------

	private Window getWindow()
	{
		return this;
	}

	//------------------------------------------------------------------

	private void readSector(
		int	index)
	{
		// Get absolute sector index
		long index0 = chainMode ? chainSectorIndexToAbs(index) : index;
		if (index0 > Integer.MAX_VALUE)
		{
			WarningDialog.show(getWindow(), READ_SECTOR_STR,
							   String.format(ErrorMsg.SECTOR_INDEX_OUT_OF_BOUNDS, index0));
			index0 = MIN_SECTOR_INDEX;
		}
		int absIndex = (int)index0;

		// Create task to read sector
		Task<byte[]> task = new Task<>()
		{
			{
				// Initialise task
				updateTitle(READ_SECTOR_STR + " " + absIndex);
				updateMessage(READING_SECTOR_STR + " " + absIndex);
				updateProgress(-1, 1);
			}

			@Override
			protected byte[] call()
				throws Exception
			{
				return volume.readSector(absIndex);
			}

			@Override
			protected void succeeded()
			{
				// Update sector index and history
				if (chainMode)
				{
					// Update sector index
					chainSectorIndex = index;

					// Update sector history
					chainSectorHistory.remove(Integer.valueOf(index));
					chainSectorHistory.addFirst(index);
					while (chainSectorHistory.size() > MAX_HISTORY_LENGTH)
						chainSectorHistory.removeLast();

					// Enable/disable sector-history button
					sectorHistoryButton.setDisable(chainSectorHistory.isEmpty());

					// Update absolute sector index
					absSectorIndex.set(absIndex);
				}
				else
				{
					// Update absolute sector index
					absSectorIndex.set(index);

					// Update sector history
					absSectorHistory.remove(Integer.valueOf(index));
					absSectorHistory.addFirst(index);
					while (absSectorHistory.size() > MAX_HISTORY_LENGTH)
						absSectorHistory.removeLast();

					// Enable/disable sector-history button
					sectorHistoryButton.setDisable(absSectorHistory.isEmpty());
				}

				// Update value of sector-index spinner
				sectorIndexSpinner.getValueFactory().setValue(index);

				// Update data area
				dataArea.update(getValue());
			}

			@Override
			protected void failed()
			{
				// Display error message
				ErrorDialog.show(getWindow(), getTitle(), getException());
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(getWindow(), task);

		// Execute task on background thread
		Fat32ManagerApp.executeTask(task);
	}

	//------------------------------------------------------------------

	private void readCluster(
		int	index)
	{
		// Get absolute cluster index
		int absIndex = chainMode ? chainClusterIndexToAbs(index) : index;

		// Create task to read cluster
		Task<byte[]> task = new Task<>()
		{
			{
				// Initialise task
				updateTitle(READ_CLUSTER_STR + " " + absIndex);
				updateMessage(READING_CLUSTER_STR + " " + absIndex);
				updateProgress(-1, 1);
			}

			@Override
			protected byte[] call()
				throws Exception
			{
				return volume.readCluster(absIndex);
			}

			@Override
			protected void succeeded()
			{
				// Update sector index and history
				if (chainMode)
				{
					// Update cluster index
					chainClusterIndex = index;

					// Update cluster history
					chainClusterHistory.remove(Integer.valueOf(index));
					chainClusterHistory.addFirst(index);
					while (chainClusterHistory.size() > MAX_HISTORY_LENGTH)
						chainClusterHistory.removeLast();

					// Enable/disable cluster-history button
					clusterHistoryButton.setDisable(chainClusterHistory.isEmpty());

					// Update absolute cluster index
					absClusterIndex.set(absIndex);
				}
				else
				{
					// Update absolute cluster index
					absClusterIndex.set(index);

					// Update cluster history
					absClusterHistory.remove(Integer.valueOf(index));
					absClusterHistory.addFirst(index);
					while (absClusterHistory.size() > MAX_HISTORY_LENGTH)
						absClusterHistory.removeLast();

					// Enable/disable cluster-history button
					clusterHistoryButton.setDisable(absClusterHistory.isEmpty());
				}

				// Update value of cluster-index spinner
				clusterIndexSpinner.getValueFactory().setValue(index);

				// Update data area
				dataArea.update(getValue());
			}

			@Override
			protected void failed()
			{
				// Display error message
				ErrorDialog.show(getWindow(), getTitle(), getException());
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(getWindow(), task);

		// Execute task on background thread
		Fat32ManagerApp.executeTask(task);
	}

	//------------------------------------------------------------------

	private void writeFile(
		Path		file,
		long		sectorIndex,
		int			length,
		boolean		append,
		String		title,
		IProcedure0	onFileWritten)
	{
		// Seek confirmation to replace existing file
		if (!append && !IOUtils.replaceExistingFile(file, getWindow(), title))
			return;

		// Create task to write file
		Task<Void> task = new Task<>()
		{
			{
				// Initialise task
				updateTitle(title);
				updateProgress(-1, 1);
			}

			@Override
			protected Void call()
				throws Exception
			{
				// Read sectors
				updateMessage(READING_SECTORS_STR);
				byte[] data = null;
				try
				{
					int numSectors = NumberUtils.roundUpQuotientInt(length, volume.getBytesPerSector());
					data = volume.readSectors(sectorIndex, numSectors);
				}
				catch (Exception e)
				{
					throw new BaseException(ErrorMsg.ERROR_READING_SECTORS, e);
				}

				// Write or append data to file
				updateMessage(WRITING_STR + MessageConstants.SPACE_SEPARATOR + file);
				if (append || !Files.exists(file, LinkOption.NOFOLLOW_LINKS))
					appendData(file, data, length);
				else
					writeData(file, data, length);

				// Return nothing
				return null;
			}

			@Override
			protected void succeeded()
			{
				// Call completion function
				onFileWritten.invoke();

				// Notify success
				NotificationDialog.show(getWindow(), getTitle(), MessageIcon32.INFORMATION.get(),
										file + MessageConstants.LABEL_SEPARATOR + FILE_WRITTEN_STR);
			}

			@Override
			protected void failed()
			{
				// Display error message in dialog
				ErrorDialog.show(getWindow(), getTitle(), getException());
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(getWindow(), task);

		// Execute task on background thread
		Fat32ManagerApp.executeTask(task);
	}

	//------------------------------------------------------------------

	private void writeData(
		Path	file,
		byte[]	data,
		int		length)
		throws FileException
	{
		// Initialise variables
		FileChannel channel = null;
		Path tempFile = null;
		boolean oldFileDeleted = false;

		// Write file
		try
		{
			// Read file permissions of an existing file
			FileAttribute<?>[] attrs = {};
			if (Files.exists(file, LinkOption.NOFOLLOW_LINKS))
			{
				try
				{
					PosixFileAttributes posixAttrs =
							Files.readAttributes(file, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
					attrs = new FileAttribute<?>[] { PosixFilePermissions.asFileAttribute(posixAttrs.permissions()) };
				}
				catch (UnsupportedOperationException e)
				{
					// ignore
				}
				catch (Exception e)
				{
					throw new FileException(ErrorMsg.FAILED_TO_READ_FILE_ATTRIBUTES, e, file);
				}
			}

			// Create parent directory
			Path directory = PathUtils.absParent(file);
			try
			{
				Files.createDirectories(directory);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CREATE_DIRECTORY, e, directory);
			}

			// Create temporary file
			try
			{
				tempFile = FilenameUtils.tempLocation(file);
				Files.createFile(tempFile, attrs);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CREATE_TEMPORARY_FILE, e, tempFile);
			}

			// Open channel for writing
			try
			{
				channel = FileChannel.open(tempFile, StandardOpenOption.WRITE);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_OPEN_FILE, e, tempFile);
			}

			// Lock channel
			try
			{
				if (channel.tryLock() == null)
					throw new FileException(ErrorMsg.FAILED_TO_LOCK_FILE, tempFile);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_LOCK_FILE, e, tempFile);
			}

			// Write file
			try
			{
				ChannelUtils.write(channel, data, 0, length);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.ERROR_WRITING_FILE, e, tempFile);
			}

			// Close channel
			try
			{
				channel.close();
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CLOSE_FILE, e, tempFile);
			}
			finally
			{
				channel = null;
			}

			// Delete any existing file
			try
			{
				Files.deleteIfExists(file);
				oldFileDeleted = true;
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_DELETE_FILE, e, file);
			}

			// Rename temporary file
			try
			{
				Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_RENAME_FILE, e, file, PathUtils.abs(tempFile));
			}
		}
		catch (FileException e)
		{
			// Close channel
			if (channel != null)
			{
				try
				{
					channel.close();
				}
				catch (Exception e0)
				{
					// ignore
				}
			}

			// Delete temporary file
			if (!oldFileDeleted && (tempFile != null))
			{
				try
				{
					Files.deleteIfExists(tempFile);
				}
				catch (Exception e0)
				{
					// ignore
				}
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

	private void appendData(
		Path	file,
		byte[]	data,
		int		length)
		throws FileException
	{
		FileChannel channel = null;
		try
		{
			// Create parent directory
			Path directory = PathUtils.absParent(file);
			try
			{
				Files.createDirectories(directory);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CREATE_DIRECTORY, e, directory);
			}

			// Open channel for writing
			try
			{
				channel = FileChannel.open(file, StandardOpenOption.WRITE, StandardOpenOption.CREATE,
										   StandardOpenOption.APPEND);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_OPEN_FILE, e, file);
			}

			// Lock channel
			try
			{
				if (channel.tryLock() == null)
					throw new FileException(ErrorMsg.FAILED_TO_LOCK_FILE, file);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_LOCK_FILE, e, file);
			}

			// Write file
			try
			{
				ChannelUtils.write(channel, data, 0, length);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.ERROR_WRITING_FILE, e, file);
			}

			// Close channel
			try
			{
				channel.close();
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CLOSE_FILE, e, file);
			}
			finally
			{
				channel = null;
			}
		}
		catch (FileException e)
		{
			// Close channel
			if (channel != null)
			{
				try
				{
					channel.close();
				}
				catch (Exception e0)
				{
					// ignore
				}
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: DATA UNIT


	private enum DataUnit
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		SECTOR
		(
			"Sector"
		),

		CLUSTER
		(
			"Cluster"
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	text;
		private	String	lcText;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DataUnit(
			String	text)
		{
			// Initialise instance variables
			this.text = text;
			lcText = text.toLowerCase();
		}

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

		private String getKey()
		{
			return StringUtils.toCamelCase(name());
		}

		//--------------------------------------------------------------

		private DataUnit next()
		{
			return values()[(ordinal() + 1) % values().length];
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// ENUMERATION: FILE-WRITING OPERATION


	private enum Operation
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		WRITE_SECTOR
		(
			"Write sector"
		),

		APPEND_SECTOR
		(
			"Append sector"
		),

		WRITE_CLUSTER
		(
			"Write cluster"
		),

		APPEND_CLUSTER
		(
			"Append cluster"
		);

		private static final	List<Operation>	SECTOR_OPERATIONS	= List.of(WRITE_SECTOR, APPEND_SECTOR);
		private static final	List<Operation>	CLUSTER_OPERATIONS	= List.of(WRITE_CLUSTER, APPEND_CLUSTER);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	text;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Operation(
			String	text)
		{
			// Initialise instance variables
			this.text = text;
		}

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

		private boolean isAppend()
		{
			return (this == APPEND_SECTOR) || (this == APPEND_CLUSTER);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: STATE


	public static class State
		extends WindowState
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The default data unit. */
		private static final	DataUnit	DEFAULT_DATA_UNIT	= DataUnit.SECTOR;

		/** The default file index. */
		private static final	int		DEFAULT_FILE_INDEX	= 0;

		/** The default number of file-index digits. */
		private static final	int		DEFAULT_FILE_INDEX_NUM_DIGITS	= 3;

		/** Keys of properties. */
		private interface PropertyKey
		{
			String	DATA_AREA_SIZE				= "dataAreaSize";
			String	DATA_UNIT					= "dataUnit";
			String	DIRECTORY					= "directory";
			String	FILE_INDEX					= "fileIndex";
			String	FILE_INDEX_AUTOINCREMENT	= "fileIndexAutoincrement";
			String	FILE_INDEX_ENABLED			= "fileIndexEnabled";
			String	FILE_INDEX_NUM_DIGITS		= "fileIndexNumDigits";
			String	FILENAME_PREFIX				= "filenamePrefix";
			String	FILENAME_SUFFIX				= "filenameSuffix";
			String	HIDDEN						= "hidden";
			String	SAVE_SECTOR_OR_CLUSTER		= "saveSectorOrCluster";
			String	VIEW						= "view";
		}

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	DataUnit	dataUnit;
		private	Dimension2D	dataAreaSize;
		private	boolean		savePaneHidden;
		private	String		saveDirectory;
		private	String		saveFilenamePrefix;
		private	String		saveFilenameSuffix;
		private	boolean		saveFileIndexEnabled;
		private	int			saveFileIndex;
		private	int			saveFileIndexNumDigits;
		private	boolean		saveFileIndexAutoincrement;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private State()
		{
			// Call superclass constructor
			super(true, true);

			// Initialise instance variables
			dataUnit = DEFAULT_DATA_UNIT;
			savePaneHidden = true;
			saveFilenamePrefix = "";
			saveFilenameSuffix = "";
			saveFileIndex = DEFAULT_FILE_INDEX;
			saveFileIndexNumDigits = DEFAULT_FILE_INDEX_NUM_DIGITS;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Encodes this dialog state as a tree of {@linkplain AbstractNode nodes} and returns the root node.
		 *
		 * @return the root node of the tree of {@linkplain AbstractNode nodes} that encodes this dialog state.
		 */

		@Override
		public MapNode encodeTree()
		{
			// Call superclass method
			MapNode rootNode = super.encodeTree();

			// Encode data unit
			rootNode.addString(PropertyKey.DATA_UNIT, dataUnit.getKey());

			// Create view node
			MapNode viewNode = rootNode.addMap(PropertyKey.VIEW);

			// Encode size of data area
			if (dataAreaSize != null)
				viewNode.addDoubles(PropertyKey.DATA_AREA_SIZE, dataAreaSize.getWidth(), dataAreaSize.getHeight());

			// Create 'save' node
			MapNode saveNode = rootNode.addMap(PropertyKey.SAVE_SECTOR_OR_CLUSTER);

			// Encode 'hidden' flag
			saveNode.addBoolean(PropertyKey.HIDDEN, savePaneHidden);

			// Encode directory
			if (!StringUtils.isNullOrEmpty(saveDirectory))
				saveNode.addString(PropertyKey.DIRECTORY, saveDirectory);

			// Encode filename prefix
			if (!StringUtils.isNullOrEmpty(saveFilenamePrefix))
				saveNode.addString(PropertyKey.FILENAME_PREFIX, saveFilenamePrefix);

			// Encode filename suffix
			if (!StringUtils.isNullOrEmpty(saveFilenameSuffix))
				saveNode.addString(PropertyKey.FILENAME_SUFFIX, saveFilenameSuffix);

			// Encode flag: file index enabled
			saveNode.addBoolean(PropertyKey.FILE_INDEX_ENABLED, saveFileIndexEnabled);

			// Encode file index
			saveNode.addInt(PropertyKey.FILE_INDEX, saveFileIndex);

			// Encode number of digits in file index
			saveNode.addInt(PropertyKey.FILE_INDEX_NUM_DIGITS, saveFileIndexNumDigits);

			// Encode flag: autoincrement file index
			saveNode.addBoolean(PropertyKey.FILE_INDEX_AUTOINCREMENT, saveFileIndexAutoincrement);

			// Return root node
			return rootNode;
		}

		//--------------------------------------------------------------

		/**
		 * Decodes this dialog state from the tree of {@linkplain AbstractNode nodes} whose root is the specified node.
		 *
		 * @param rootNode
		 *          the root of the tree of {@linkplain AbstractNode nodes} from which this dialog state will be
		 *          decoded.
		 */

		@Override
		public void decodeTree(
			MapNode	rootNode)
		{
			// Call superclass method
			super.decodeTree(rootNode);

			// Decode data unit
			dataUnit = rootNode.getEnumValue(DataUnit.class, PropertyKey.DATA_UNIT, DataUnit::getKey,
											 DEFAULT_DATA_UNIT);

			// Decode view properties
			String key = PropertyKey.VIEW;
			if (rootNode.hasMap(key))
			{
				// Get view node
				MapNode viewNode = rootNode.getMapNode(key);

				// Decode size of data area
				key = PropertyKey.DATA_AREA_SIZE;
				if (viewNode.hasList(key))
				{
					List<DoubleNode> nodes = viewNode.getListNode(key).doubleNodes();
					if (nodes.size() >= 2)
					{
						double width = nodes.get(0).getValue();
						double height = nodes.get(1).getValue();
						if ((width > 0.0) && (height > 0.0))
							dataAreaSize = new Dimension2D(width, height);
					}
				}
			}

			// Decode 'save' properties
			key = PropertyKey.SAVE_SECTOR_OR_CLUSTER;
			if (rootNode.hasMap(key))
			{
				// Get 'save' node
				MapNode saveNode = rootNode.getMapNode(key);

				// Decode flag: 'save' pane hidden
				savePaneHidden = saveNode.getBoolean(PropertyKey.HIDDEN, true);

				// Decode directory
				key = PropertyKey.DIRECTORY;
				if (saveNode.hasString(key))
					saveDirectory = saveNode.getString(key);

				// Decode filename prefix
				key = PropertyKey.FILENAME_PREFIX;
				if (saveNode.hasString(key))
					saveFilenamePrefix = saveNode.getString(key);

				// Decode filename suffix
				key = PropertyKey.FILENAME_SUFFIX;
				if (saveNode.hasString(key))
					saveFilenameSuffix = saveNode.getString(key);

				// Decode flag: file index enabled
				saveFileIndexEnabled = saveNode.getBoolean(PropertyKey.FILE_INDEX_ENABLED, false);

				// Decode file index
				saveFileIndex = saveNode.getInt(PropertyKey.FILE_INDEX, DEFAULT_FILE_INDEX);

				// Decode number of digits in file index
				saveFileIndexNumDigits = saveNode.getInt(PropertyKey.FILE_INDEX_NUM_DIGITS,
														 DEFAULT_FILE_INDEX_NUM_DIGITS);

				// Decode flag: autoincrement file index
				saveFileIndexAutoincrement = saveNode.getBoolean(PropertyKey.FILE_INDEX_AUTOINCREMENT, false);
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns a locator function that returns the location from this dialog state.
		 *
		 * @return a locator function that returns the location from this dialog state, or {@code null} if the location
		 *         is {@code null}.
		 */

		private ILocator getLocator()
		{
			Point2D location = getLocation();
			return (location == null) ? null : (width, height) -> location;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: TEXT AREA FOR SECTOR OR CLUSTER DATA


	private class DataArea
		extends TextArea
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	DEFAULT_NUM_ROWS	= 20;

		private static final	int	BYTES_PER_GROUP	= 4;
		private static final	int	BLOCKS_PER_LINE	= 4;
		private static final	int	BYTES_PER_LINE	= BLOCKS_PER_LINE * BYTES_PER_GROUP;

		private static final	int	GROUP_WIDTH	= BYTES_PER_GROUP * 3 + 1;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int		dataLength;
		private	int[]	selectedRange;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DataArea()
		{
			// Get number of columns
			int numColumns = numOffsetDigitsHex + BYTES_PER_LINE * (3 + 1) + 7;

			// Set properties
			setPrefColumnCount(numColumns);
			setEditable(false);
			setFont(Fonts.monoFont());
			getStyleClass().add(StyleClass.SECTOR_CLUSTER_DATA_AREA);

			// Display offset of character under mouse cursor when primary mouse button is held down with Ctrl over data
			// area
			addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
			{
				if ((event.getButton() == MouseButton.PRIMARY) && event.isControlDown()
						&& lookup(StyleSelector.TEXT) instanceof Text text)
				{
					// Get location of mouse cursor in text
					Point2D location = text.sceneToLocal(event.getSceneX(), event.getSceneY());

					// If text node contains mouse cursor ...
					if (text.contains(location))
					{
						// Get index of character at mouse cursor
						int index = text.hitTest(location).getCharIndex();

						// Get index of line of text
						int lineLength = numColumns + 1;
						int lineIndex = index / lineLength;

						// Get offset of character within line
						int offset = index % lineLength;

						// Initialise selection indices and byte offset
						int selectionStart = 0;
						int selectionEnd = 0;
						int byteOffset = -1;

						// Get start and end offsets of hex region
						int startOffset = numOffsetDigitsHex + 3;
						int endOffset = startOffset + BYTES_PER_LINE * 3 + 2;

						// If character is in hex region ...
						if ((offset >= startOffset) && (offset < endOffset))
						{
							// Get offset of character within block
							offset -= startOffset;
							int blockIndex = offset / GROUP_WIDTH;
							offset %= GROUP_WIDTH;

							// If character is hex digit, calculate selection indices and byte offset
							if ((offset < BYTES_PER_GROUP * 3 - 1) && (offset % 3 < 2))
							{
								offset /= 3;
								selectionStart = startOffset + blockIndex * GROUP_WIDTH + offset * 3;
								selectionEnd = selectionStart + 2;
								byteOffset = lineIndex * BYTES_PER_LINE + blockIndex * BYTES_PER_GROUP + offset;
							}
						}

						// ... otherwise, test for character in character region
						else
						{
							// Get start and end offsets of character region
							startOffset = endOffset + 2;
							endOffset = startOffset + BYTES_PER_LINE;

							// If character is in character region, calculate selection indices and byte offset
							if ((offset >= startOffset) && (offset < endOffset))
							{
								selectionStart = offset;
								selectionEnd = selectionStart + 1;
								byteOffset = lineIndex * BYTES_PER_LINE + offset - startOffset;
							}
						}

						// If there was a hit, select text and update offset label
						if ((byteOffset >= 0) && (byteOffset < dataLength))
						{
							// Select text
							int lineOffset = lineIndex * lineLength;
							selectionStart += lineOffset;
							selectionEnd += lineOffset;
							selectedRange = new int[] { selectionStart, selectionEnd };
							selectRange(selectionStart, selectionEnd);

							// Update offset labels
							decOffsetLabel.setText(Integer.toString(byteOffset));
							hexOffsetLabel.setText(Integer.toHexString(byteOffset).toUpperCase());
						}
					}
				}
			});

			// Clear offset label when primary mouse button is released over data area
			addEventFilter(MouseEvent.MOUSE_RELEASED, event ->
			{
				if (event.getButton() == MouseButton.PRIMARY)
				{
					// Clear selection
					if (selectedRange != null)
					{
						deselect();
						selectedRange = null;
					}

					// Clear offset labels
					decOffsetLabel.setText(null);
					hexOffsetLabel.setText(null);
				}
			});
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void update(
			byte[]	data)
		{
			// Convert data to text
			StringBuilder buffer1 = new StringBuilder(data.length * 5);
			StringBuilder buffer2 = new StringBuilder();
			for (int i = 0; i < data.length; i++)
			{
				// If start of line, append line feed and hex representation of offset
				if (i % BYTES_PER_LINE == 0)
				{
					if (i > 0)
						buffer1.append('\n');
					buffer1.append(NumberUtils.uIntToHexString(i, numOffsetDigitsHex, '0'));
					buffer1.append(": ");
				}

				// Insert extra space before every block
				if (i % BYTES_PER_GROUP == 0)
					buffer1.append(' ');

				// Append hex representation of byte
				buffer1.append(NumberUtils.uIntToHexString(data[i], 2, '0'));
				buffer1.append(' ');

				// Convert byte to character and append character to secondary buffer
				char ch = (char)(data[i] & 0xFF);
				buffer2.append(Character.isISOControl(ch) ? CONTROL_CHAR_PLACEHOLDER : ch);

				// If end of line, append secondary buffer to primary buffer
				if (i % BYTES_PER_LINE == BYTES_PER_LINE - 1)
				{
					buffer1.append(' ');
					buffer1.append(buffer2);
					buffer2.setLength(0);
				}
			}

			// If secondary buffer is not empty, append it to primary buffer
			if (buffer2.length() > 0)
			{
				buffer1.append(" ".repeat((BYTES_PER_LINE - buffer2.length()) * 3 + 1));
				buffer1.append(buffer2);
				buffer1.append(' ');
			}

			// Update length of data
			dataLength = data.length;

			// Set text on text area
			setText(buffer1.toString());
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: PANE FOR SAVING A SECTOR OR CLUSTER


	private class SaveSectorClusterPane
		extends GridPane
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The padding around the operation button. */
		private static final	Insets	OPERATION_BUTTON_PADDING	= new Insets(3.0, 8.0, 3.0, 8.0);

		/** The preferred number of columns of the <i>directory</i> field. */
		private static final	int		DIRECTORY_FIELD_NUM_COLUMNS	= 40;

		/** The preferred number of columns of the <i>filename prefix</i> field. */
		private static final	int		FILENAME_PREFIX_FIELD_NUM_COLUMNS	= 16;

		/** The preferred number of columns of the <i>filename suffix</i> field. */
		private static final	int		FILENAME_SUFFIX_FIELD_NUM_COLUMNS	= FILENAME_PREFIX_FIELD_NUM_COLUMNS;

		/** The minimum value of the <i>file index</i> spinner. */
		private static final	int		MIN_FILE_INDEX	= 0;

		/** The maximum value of the <i>file index</i> spinner. */
		private static final	int		MAX_FILE_INDEX	= 999_999_999;

		/** The number of digits of the <i>file index</i> spinner. */
		private static final	int		FILE_INDEX_SPINNER_NUM_DIGITS	= 9;

		/** The minimum value of the <i>number of file-index digits</i> spinner. */
		private static final	int		MIN_NUM_FILE_INDEX_DIGITS	= 1;

		/** The maximum value of the <i>number of file-index digits</i> spinner. */
		private static final	int		MAX_NUM_FILE_INDEX_DIGITS	= 9;

		/** The number of digits of the <i>number of file-index digits</i> spinner. */
		private static final	int		NUM_FILE_INDEX_DIGITS_SPINNER_NUM_DIGITS	= 1;

		/** The minimum value of the <i>data length</i> spinner. */
		private static final	int		MIN_DATA_LENGTH	= 1;

		/** The default initial directory of a file chooser. */
		private static final	Path	DEFAULT_DIRECTORY	= SystemUtils.workingDirectory();

		/** Miscellaneous strings. */
		private static final	String	DIRECTORY_STR			= "Directory";
		private static final	String	CHOOSE_DIRECTORY_STR	= "Choose directory";
		private static final	String	FILENAME_PREFIX_STR		= "Filename prefix";
		private static final	String	FILE_INDEX_STR			= "File index";
		private static final	String	NUMBER_OF_DIGITS_STR	= "Number of digits";
		private static final	String	AUTOINCREMENT_INDEX_STR	= "Autoincrement index";
		private static final	String	FILENAME_SUFFIX_STR		= "Filename suffix";
		private static final	String	DATA_LENGTH_STR			= "Data length";
		private static final	String	OPERATION_STR			= "Operation";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	CollectionSpinner<Operation>	operationSpinner;
		private	PathnameField					directoryField;
		private	TextField						filenamePrefixField;
		private	TextField						filenameSuffixField;
		private	CheckBox						fileIndexCheckBox;
		private	Spinner<Integer>				fileIndexSpinner;
		private	Spinner<Integer>				numIndexDigitsSpinner;
		private	CheckBox						autoincrementIndexCheckBox;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private SaveSectorClusterPane()
		{
			// Set properties
			setHgap(CONTROL_H_GAP);
			setVgap(CONTROL_V_GAP);
			setAlignment(Pos.CENTER);

			// Initialise column constraints
			ColumnConstraints column = new ColumnConstraints();
			column.setMinWidth(Region.USE_PREF_SIZE);
			column.setHalignment(HPos.RIGHT);
			column.setFillWidth(false);
			getColumnConstraints().add(column);

			column = new ColumnConstraints();
			column.setHalignment(HPos.LEFT);
			column.setHgrow(Priority.ALWAYS);
			getColumnConstraints().add(column);

			// Initialise row index
			int row = 0;

			// Spinner: operation
			operationSpinner = CollectionSpinner.leftRightH(HPos.CENTER, true, Operation.class, null, null, null);
			addRow(row++, new Label(OPERATION_STR), operationSpinner);

			// Directory chooser: directory
			LocationChooser directoryChooser = LocationChooser.forDirectories();
			directoryChooser.setDialogTitle(DIRECTORY_STR);
			directoryChooser.setDialogStateKey();

			// Pathname field: directory
			directoryField = new PathnameField(state.saveDirectory, DIRECTORY_FIELD_NUM_COLUMNS);
			directoryField.setShowInvalidPathnameError(true);
			directoryField.setLocationMatcher(PathnameField.DIRECTORY_MATCHER);

			// Pathname pane: directory
			PathnamePane directoryPane = new PathnamePane(directoryField, true, event ->
			{
				// Set initial directory of directory chooser from content of output-directory field
				directoryField.initChooser(directoryChooser, DEFAULT_DIRECTORY);

				// Display directory-selection dialog
				Path directory = directoryChooser.showSelectDialog(getWindow());

				// Update directory field
				if (directory != null)
					directoryField.setLocation(directory);
			});
			TooltipDecorator.addTooltip(directoryPane.getButton(), CHOOSE_DIRECTORY_STR);
			addRow(row++, new Label(DIRECTORY_STR), directoryPane);

			// Text field: filename prefix
			filenamePrefixField = new TextField(state.saveFilenamePrefix);
			filenamePrefixField.setPrefColumnCount(FILENAME_PREFIX_FIELD_NUM_COLUMNS);
			setFillWidth(filenamePrefixField, false);
			addRow(row++, new Label(FILENAME_PREFIX_STR), filenamePrefixField);

			// Check box: file index
			fileIndexCheckBox = new CheckBox(FILE_INDEX_STR);
			fileIndexCheckBox.setSelected(state.saveFileIndexEnabled);
			ButtonUtils.setActuatorLabelGap(fileIndexCheckBox, 8.0);

			// Spinner: file index
			fileIndexSpinner = SpinnerFactory.integerSpinner(MIN_FILE_INDEX, MAX_FILE_INDEX, state.saveFileIndex,
															 FILE_INDEX_SPINNER_NUM_DIGITS);
			HBox.setMargin(fileIndexSpinner, new Insets(0.0, 10.0, 0.0, 0.0));

			// Spinner: number of index digits
			numIndexDigitsSpinner = SpinnerFactory.integerSpinner(MIN_NUM_FILE_INDEX_DIGITS, MAX_NUM_FILE_INDEX_DIGITS,
																  state.saveFileIndexNumDigits,
																  NUM_FILE_INDEX_DIGITS_SPINNER_NUM_DIGITS);
			HBox.setMargin(numIndexDigitsSpinner, new Insets(0.0, 10.0, 0.0, 0.0));

			// Create procedure to update maximum index
			IProcedure0 updateIndexSpinner = () ->
			{
				int numDigits = numIndexDigitsSpinner.getValue();
				int maxValue = 1;
				for (int i = 0; i < numDigits; i++)
					maxValue *= 10;
				((IntegerSpinnerValueFactory)fileIndexSpinner.getValueFactory()).setMax(maxValue - 1);
			};

			// Update maximum index
			updateIndexSpinner.invoke();

			// Update maximum index when number of index digits changes
			numIndexDigitsSpinner.valueProperty().addListener(observable -> updateIndexSpinner.invoke());

			// Check box: autoincrement file index
			autoincrementIndexCheckBox = new CheckBox(AUTOINCREMENT_INDEX_STR);
			autoincrementIndexCheckBox.setSelected(state.saveFileIndexAutoincrement);

			// Pane: file index
			HBox fileIndexPane = new HBox(CONTROL_H_GAP, fileIndexSpinner, Labels.hNoShrink(NUMBER_OF_DIGITS_STR),
										  numIndexDigitsSpinner, autoincrementIndexCheckBox);
			fileIndexPane.setAlignment(Pos.CENTER_LEFT);
			fileIndexPane.disableProperty().bind(fileIndexCheckBox.selectedProperty().not());
			addRow(row++, fileIndexCheckBox, fileIndexPane);

			// Text field: filename suffix
			filenameSuffixField = new TextField(state.saveFilenameSuffix);
			filenameSuffixField.setPrefColumnCount(FILENAME_SUFFIX_FIELD_NUM_COLUMNS);
			setFillWidth(filenameSuffixField, false);
			addRow(row++, new Label(FILENAME_SUFFIX_STR), filenameSuffixField);

			// Create function to return location of output file
			IFunction1<Path, String> getFile = title ->
			{
				// Initialise node that is associated with an error
				Node errorNode = null;

				// Get location of file from controls
				try
				{
					// Get directory
					Path directory = directoryField.getLocation();
					if (directory == null)
					{
						errorNode = directoryField;
						throw directoryField.isEmpty() ? new BaseException(ErrorMsg.NO_DIRECTORY) : BaseException.EMPTY;
					}

					// Create filename from components
					String filename = filenamePrefixField.getText();
					if (fileIndexCheckBox.isSelected())
					{
						int numDigits = numIndexDigitsSpinner.getValue();
						filename += NumberUtils.uIntToDecString(fileIndexSpinner.getValue(), numDigits, '0');
					}
					filename += filenameSuffixField.getText();
					if (filename.isBlank())
					{
						errorNode = filenamePrefixField;
						throw new BaseException(ErrorMsg.NO_FILENAME_COMPONENTS);
					}

					// Return absolute location of file
					return PathUtils.abs(directory.resolve(filename));
				}
				catch (BaseException e)
				{
					// Display error message in dialog
					if (e != BaseException.EMPTY)
						ErrorDialog.show(getWindow(), title, e);

					// Request focus
					errorNode.requestFocus();

					// Return no file
					return null;
				}
			};

			// Get volume parameters
			int bytesPerSector = volume.getBytesPerSector();
			int sectorsPerCluster = volume.getSectorsPerCluster();

			// Spinner: data length
			Spinner<Integer> dataLengthSpinner =
					SpinnerFactory.integerSpinner(MIN_DATA_LENGTH, bytesPerSector, bytesPerSector,
												  NumberUtils.getNumDecDigitsInt(sectorsPerCluster * bytesPerSector));

			// Button: operation
			Button operationButton = Buttons.hNoShrink();
			MultiTextLabeller<Operation> operationLabeller =
					new MultiTextLabeller<>(operationButton,
											Arrays.stream(Operation.values())
													.collect(Collectors.toMap(op -> op, op -> op.text)));
			operationButton.setPadding(OPERATION_BUTTON_PADDING);
			ButtonUtils.setBackgroundColour(operationButton, getColour(ColourKey.OPERATION_BUTTON_BACKGROUND));

			// Create bottom pane
			HBox bottomPane = new HBox(dataLengthSpinner, FillerUtils.hBoxFiller(24.0), operationButton);
			bottomPane.setAlignment(Pos.BOTTOM_LEFT);
			addRow(row++, new Label(DATA_LENGTH_STR), bottomPane);

			// Create procedure to update components when operation changes
			IProcedure0 updateOperation = () ->
			{
				// Update operation button
				operationLabeller.selectText(operationSpinner.getItem());

				// Update data length and maximum data length
				int length = switch (state.dataUnit)
				{
					case SECTOR  -> bytesPerSector;
					case CLUSTER -> sectorsPerCluster * bytesPerSector;
				};
				IntegerSpinnerValueFactory factory = (IntegerSpinnerValueFactory)dataLengthSpinner.getValueFactory();
				factory.setMax(length);
				factory.setValue(length);
			};

			// Update components for initial operation
			updateOperation.invoke();

			// Update components when operation changes
			operationSpinner.itemProperty().addListener(observable -> updateOperation.invoke());

			// Set action of operation button
			operationButton.setOnAction(event ->
			{
				Operation operation = operationSpinner.getItem();
				long index = switch (state.dataUnit)
				{
					case SECTOR ->
					{
						int sectorIndex = getSectorIndex();
						yield chainMode ? chainSectorIndexToAbs(sectorIndex) : sectorIndex;
					}

					case CLUSTER ->
					{
						int clusterIndex = getClusterIndex();
						if (chainMode)
							clusterIndex = chainClusterIndexToAbs(clusterIndex);
						yield volume.clusterIndexToSectorIndex(clusterIndex);
					}
				};

				Path file = getFile.invoke(operation.text);
				if (file != null)
				{
					writeFile(file, index, dataLengthSpinner.getValue(), operation.isAppend(), operation.text, () ->
					{
						if (fileIndexCheckBox.isSelected() && autoincrementIndexCheckBox.isSelected())
							fileIndexSpinner.getValueFactory().setValue(fileIndexSpinner.getValue() + 1);
					});
				}
			});
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void updateDataUnit()
		{
			List<Operation> operations = switch (state.dataUnit)
			{
				case SECTOR  -> Operation.SECTOR_OPERATIONS;
				case CLUSTER -> Operation.CLUSTER_OPERATIONS;
			};
			operationSpinner.setItems(operations);
			operationSpinner.setItem(operations.get(0));
		}

		//--------------------------------------------------------------

		private void updateState()
		{
			state.saveDirectory = directoryField.getText();
			state.saveFilenamePrefix = filenamePrefixField.getText();
			state.saveFilenameSuffix = filenameSuffixField.getText();
			state.saveFileIndexEnabled = fileIndexCheckBox.isSelected();
			state.saveFileIndex = fileIndexSpinner.getValue();
			state.saveFileIndexNumDigits = numIndexDigitsSpinner.getValue();
			state.saveFileIndexAutoincrement = autoincrementIndexCheckBox.isSelected();
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
