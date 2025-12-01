/*====================================================================*\

LocationChooserPane.java

Class: file-system location chooser pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.locationchooser;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.lang.invoke.MethodHandles;

import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.nio.file.attribute.FileTime;

import java.text.DecimalFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.util.function.Predicate;

import java.util.stream.Collectors;

import javafx.application.Platform;

import javafx.beans.InvalidationListener;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.concurrent.Task;

import javafx.css.PseudoClass;

import javafx.event.ActionEvent;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;

import javafx.scene.Cursor;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import javafx.scene.image.ImageView;

import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.shape.Shape;

import javafx.stage.Window;
import javafx.stage.WindowEvent;

import uk.blankaspect.common.collection.CollectionUtils;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.FileException;

import uk.blankaspect.common.filesystem.DirectoryUtils;
import uk.blankaspect.common.filesystem.PathUtils;

import uk.blankaspect.common.function.IFunction0;
import uk.blankaspect.common.function.IFunction1;
import uk.blankaspect.common.function.IFunction2;
import uk.blankaspect.common.function.IProcedure0;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.common.geometry.VHDirection;
import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.common.logging.Logger;

import uk.blankaspect.common.misc.SystemUtils;

import uk.blankaspect.common.os.OsUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.text.Tabulator;

import uk.blankaspect.ui.jfx.button.Buttons;
import uk.blankaspect.ui.jfx.button.GraphicButton;
import uk.blankaspect.ui.jfx.button.ImageDataButton;

import uk.blankaspect.ui.jfx.clipboard.ClipboardUtils;

import uk.blankaspect.ui.jfx.container.LabelTitledPane;
import uk.blankaspect.ui.jfx.container.SplitPane2;

import uk.blankaspect.ui.jfx.dialog.ButtonInfo;
import uk.blankaspect.ui.jfx.dialog.ErrorDialog;
import uk.blankaspect.ui.jfx.dialog.MessageListDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleProgressDialog;

import uk.blankaspect.ui.jfx.filler.FillerUtils;

import uk.blankaspect.ui.jfx.filter.SubstringFilterPane;
import uk.blankaspect.ui.jfx.filter.SuggestionFilterPane;

import uk.blankaspect.ui.jfx.image.ImageData;
import uk.blankaspect.ui.jfx.image.ImageUtils;
import uk.blankaspect.ui.jfx.image.MessageIcon32;

import uk.blankaspect.ui.jfx.label.Labels;

import uk.blankaspect.ui.jfx.listview.FilteredListView;
import uk.blankaspect.ui.jfx.listview.ListViewStyle;

import uk.blankaspect.ui.jfx.math.FxGeomUtils;

import uk.blankaspect.ui.jfx.observer.ChangeNotifier;

import uk.blankaspect.ui.jfx.popup.CellPopUpManager;
import uk.blankaspect.ui.jfx.popup.LabelPopUpManager;
import uk.blankaspect.ui.jfx.popup.PopUpEvent;
import uk.blankaspect.ui.jfx.popup.PopUpManager;
import uk.blankaspect.ui.jfx.popup.PopUpUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.shape.Shapes;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;

import uk.blankaspect.ui.jfx.style.AbstractTheme;
import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;
import uk.blankaspect.ui.jfx.style.StyleSelector;

import uk.blankaspect.ui.jfx.tableview.ElasticList;
import uk.blankaspect.ui.jfx.tableview.TableViewStyle;

import uk.blankaspect.ui.jfx.text.Text2;
import uk.blankaspect.ui.jfx.text.TextUtils;

import uk.blankaspect.ui.jfx.textfield.FilterFactory;
import uk.blankaspect.ui.jfx.textfield.PathnameField;

import uk.blankaspect.ui.jfx.tooltip.TooltipDecorator;

import uk.blankaspect.ui.jfx.treeview.TreeViewStyle;

import uk.blankaspect.ui.jfx.window.WindowUtils;

//----------------------------------------------------------------------


// CLASS: FILE-SYSTEM LOCATION CHOOSER PANE


/**
 * This class implements a pane in which a user may choose the location of a file or directory.
 */

public class LocationChooserPane
	extends VBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The horizontal gap between adjacent controls. */
	private static final	double	CONTROL_H_GAP	= 6.0;

	/** The vertical gap between adjacent controls. */
	private static final	double	CONTROL_V_GAP	= 5.0;

	/** The spacing between adjacent buttons of a button pane. */
	private static final	double	BUTTON_PANE_SPACING	= 1.0;

	/** The spacing between adjacent components of the top pane. */
	private static final	double	TOP_PANE_SPACING	= 4.0;

	/** The padding around the top pane. */
	private static final	Insets	TOP_PANE_PADDING	= new Insets(3.0, 4.0, 3.0, 4.0);

	/** The height of the focus indicator for the tree view and table view. */
	private static final	double	VIEW_FOCUS_INDICATOR_HEIGHT	= 2.0;

	/** The padding around a non-editable text field. */
	private static final	Insets	NON_EDITABLE_FIELD_PADDING	= new Insets(3.0, 5.0, 3.0, 5.0);

	/** The preferred number of columns of the name field. */
	private static final	int		NAME_FIELD_NUM_COLUMNS	= 40;

	/** The padding around the name pane. */
	private static final	Insets	NAME_PANE_PADDING	= new Insets(6.0, 8.0, 6.0, 8.0);

	/** The delay (in milliseconds) before a pop-up for a cell of a tree view or table view is displayed after it is
		activated. */
	private static final	int		CELL_POP_UP_DELAY	= 500;

	/** The default display name of a Unix-like root directory. */
	private static final	String	DEFAULT_ROOT_DIR_DISPLAY_NAME	= "\u00ABROOT\u00BB";

	/** The default initial directory. */
	private static final	Path	DEFAULT_INITIAL_DIRECTORY	= SystemUtils.workingDirectory();

	/** The initial part of the pathname of the root directory of a removable medium under Linux. */
	private static final	String	LINUX_MEDIA_PATHNAME_PREFIX	= "/media/%s/";

	/** The converter for the name-filter pane. */
	private static final	FilteredListView.IConverter<Path>	NAME_FILTER_CONVERTER	=
			location -> location.getFileName().toString();

	/** Key combination: open previous directory. */
	private static final	KeyCombination	KEY_COMBO_OPEN_PREVIOUS	=
			new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);

	/** Key combination: open next directory. */
	private static final	KeyCombination	KEY_COMBO_OPEN_NEXT		=
			new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);

	/** Key combination: open parent directory. */
	private static final	KeyCombination	KEY_COMBO_OPEN_PARENT	=
			new KeyCodeCombination(KeyCode.UP, KeyCombination.ALT_DOWN);

	/** Key combination: refresh views. */
	private static final	KeyCombination	KEY_COMBO_REFRESH1		=
			new KeyCodeCombination(KeyCode.R, KeyCombination.ALT_DOWN);

	/** Key combination: refresh views. */
	private static final	KeyCombination	KEY_COMBO_REFRESH2		=
			new KeyCodeCombination(KeyCode.F5);

	/** Miscellaneous strings. */
	private static final	String	ELLIPSIS_STR					= "...";
	private static final	String	BACK_TO_STR						= "Back to ";
	private static final	String	FORWARD_TO_STR					= "Forward to ";
	private static final	String	ROOT_DIRECTORY_STR				= "root directory";
	private static final	String	HOME_DIRECTORY_STR				= "Home directory";
	private static final	String	NEW_DIRECTORY_STR				= "New directory";
	private static final	String	REFRESH_STR						= "Refresh";
	private static final	String	SHOW_PATHNAME_FIELD_STR			= "Show pathname field";
	private static final	String	SHOW_DIRECTORY_BAR_STR			= "Show directory bar";
	private static final	String	NAME_STR						= "Name";
	private static final	String	SUGGESTIONS_STR					= "Ctrl+Space for suggestions";
	private static final	String	FILTER_MODE_STR					= "Filter mode\n(Ctrl+M in 'Name' field)";
	private static final	String	FILTER_STR						= "Filter";
	private static final	String	COPY_PATHNAME_STR				= "Copy pathname";
	private static final	String	NO_STR							= "No ";
	private static final	String	NO_MATCHING_STR					= "No matching ";
	private static final	String	DIRECTORIES_STR					= "directories";
	private static final	String	ENTRIES_STR						= "entries";
	private static final	String	SCANNING_FILE_SYSTEM_STR		= "Scanning file system";
	private static final	String	SCANNING_ROOT_DIRECTORIES_STR	= "Scanning root directories";
	private static final	String	READING_DIRECTORY_STR			= "Reading directory";

	/** The pseudo-class that is associated with the <i>highlighted</i> state. */
	private static final	PseudoClass	HIGHLIGHTED_PSEUDO_CLASS	=
			PseudoClass.getPseudoClass(PseudoClassKey.HIGHLIGHTED);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.NON_EDITABLE_FIELD_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.NON_EDITABLE_FIELD)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.NON_EDITABLE_FIELD_BORDER,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.NON_EDITABLE_FIELD)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.DIRECTORY_BAR_TEXT,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_BAR)
					.desc(StyleClass.DIRECTORY_BAR_TEXT)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.DIRECTORY_BAR_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.DIRECTORY_BAR_BORDER,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.DIRECTORY_BAR_BUTTON_BACKGROUND_HOVERED,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.desc(GraphicButton.StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.HOVERED)
					.desc(GraphicButton.StyleClass.INNER_VIEW)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.DIRECTORY_BAR_BUTTON_BORDER_HOVERED,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.desc(GraphicButton.StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.HOVERED)
					.desc(GraphicButton.StyleClass.INNER_VIEW)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.DIRECTORY_BAR_ARROWHEAD,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.desc(StyleClass.ARROWHEAD)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.FOCUS_INDICATOR_PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.FOCUS_INDICATOR_PANE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			Color.TRANSPARENT,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.FOCUS_INDICATOR)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.FOCUS_INDICATOR,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(StyleClass.FOCUS_INDICATOR).pseudo(PseudoClassKey.HIGHLIGHTED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.TABLE_VIEW_HEADER_CELL_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(TableViewStyle.StyleClass.TABLE_VIEW)
					.desc(FxStyleClass.COLUMN_HEADER)
					.build(),
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(TableViewStyle.StyleClass.TABLE_VIEW)
					.desc(FxStyleClass.FILLER)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.TABLE_VIEW_HEADER_CELL_BORDER,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(TableViewStyle.StyleClass.TABLE_VIEW)
					.desc(FxStyleClass.COLUMN_HEADER)
					.build(),
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(TableViewStyle.StyleClass.TABLE_VIEW)
					.desc(FxStyleClass.FILLER)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			TableViewStyle.ColourKey.CELL_BACKGROUND_EMPTY,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(TableViewStyle.StyleClass.TABLE_VIEW)
					.desc(StyleClass.PLACEHOLDER_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.TABLE_VIEW_PLACEHOLDER_TEXT,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_PANE)
					.desc(TableViewStyle.StyleClass.TABLE_VIEW)
					.desc(StyleClass.PLACEHOLDER_LABEL)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.LOCATION_CHOOSER_PANE)
						.desc(StyleClass.FOCUS_INDICATOR_PANE)
						.build())
				.borders(Side.TOP)
				.build(),
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.LOCATION_CHOOSER_PANE)
						.desc(TableViewStyle.StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.COLUMN_HEADER)
						.build())
				.borders(Side.RIGHT, Side.BOTTOM)
				.build(),
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.LOCATION_CHOOSER_PANE)
						.desc(TableViewStyle.StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.FILLER)
						.build())
				.borders(Side.BOTTOM)
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	LOCATION_CHOOSER_PANE	= StyleConstants.CLASS_PREFIX + "location-chooser-pane";

		String	ARROWHEAD				= StyleConstants.CLASS_PREFIX + "arrowhead";
		String	DIRECTORY_BAR			= StyleConstants.CLASS_PREFIX + "directory-bar";
		String	DIRECTORY_BAR_TEXT		= StyleConstants.CLASS_PREFIX + "directory-bar-text";
		String	FOCUS_INDICATOR			= StyleConstants.CLASS_PREFIX + "focus-indicator";
		String	FOCUS_INDICATOR_PANE	= StyleConstants.CLASS_PREFIX + "focus-indicator-pane";
		String	NON_EDITABLE_FIELD		= StyleConstants.CLASS_PREFIX + "noneditable-field";
		String	PLACEHOLDER_LABEL		= StyleConstants.CLASS_PREFIX + "placeholder-label";
	}

	/** Keys of CSS pseudo-classes. */
	private interface PseudoClassKey
	{
		String	HIGHLIGHTED	= "highlighted";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	DIRECTORY_BAR_ARROWHEAD					= PREFIX + "directoryBar.arrowhead";
		String	DIRECTORY_BAR_BACKGROUND				= PREFIX + "directoryBar.background";
		String	DIRECTORY_BAR_BORDER					= PREFIX + "directoryBar.border";
		String	DIRECTORY_BAR_BUTTON_BACKGROUND_HOVERED	= PREFIX + "directoryBar.button.background.hovered";
		String	DIRECTORY_BAR_BUTTON_BORDER_HOVERED		= PREFIX + "directoryBar.button.border.hovered";
		String	DIRECTORY_BAR_TEXT						= PREFIX + "directoryBar.text";
		String	FOCUS_INDICATOR							= PREFIX + "focusIndicator";
		String	FOCUS_INDICATOR_PANE_BORDER				= PREFIX + "focusIndicator.pane.border";
		String	NON_EDITABLE_FIELD_BACKGROUND			= PREFIX + "nonEditableField.background";
		String	NON_EDITABLE_FIELD_BORDER				= PREFIX + "nonEditableField.border";
		String	TABLE_VIEW_HEADER_CELL_BACKGROUND		= PREFIX + "tableView.header.cell.background";
		String	TABLE_VIEW_HEADER_CELL_BORDER			= PREFIX + "tableView.header.cell.border";
		String	TABLE_VIEW_PLACEHOLDER_TEXT				= PREFIX + "tableView.placeholder.text";
	}

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	USER_HOME_DIR	= "user.home";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FAILED_TO_CREATE_DIRECTORY =
				"Failed to create the directory.";

		String	FAILED_TO_DETERMINE_HIDDEN_STATUS =
				"Failed to determine the 'hidden' status of the following directories:";
	}

	/** Image identifiers. */
	private interface ImageId
	{
		String	PREFIX = MethodHandles.lookup().lookupClass().getEnclosingClass().getName() + ".";

		String	DIRECTORY		= PREFIX + "directory";
		String	FILE			= PREFIX + "file";
		String	ARROW_UP		= PREFIX + "arrowUp";
		String	ARROW_DOWN		= PREFIX + "arrowDown";
		String	ARROW_LEFT		= PREFIX + "arrowLeft";
		String	ARROW_RIGHT		= PREFIX + "arrowRight";
		String	HOME			= PREFIX + "home";
		String	NEW_DIRECTORY	= PREFIX + "newDirectory";
		String	REFRESH			= PREFIX + "refresh";
		String	BUTTON_BAR		= PREFIX + "buttonBar";
		String	PENCIL			= PREFIX + "pencil";
		String	EXPAND			= PREFIX + "expand";
		String	COLLAPSE		= PREFIX + "collapse";
		String	SELECT			= PREFIX + "select";
		String	COPY			= PREFIX + "copy";
		String	COPY_LINES		= PREFIX + "copyLines";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** The display name of a Unix-like root directory. */
	private static	String	rootDirDisplayName	= DEFAULT_ROOT_DIR_DISPLAY_NAME;

	/** The index of the last thread that was created for a background task. */
	private static	int		threadIndex;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	ObservableList<Path>				selectedLocations;
	private	DirectoryTreeView					treeView;
	private	DirectoryTableView					tableView;
	private	SplitPane2							splitPane;
	private	Double								splitPaneDividerPosition;
	private	StackPane							bottomLeftPane;
	private	TextField							nameField;
	private	SuggestionFilterPane<Path>			nameFilterPane;
	private	CollectionSpinner<LocationMatcher>	filterSpinner;
	private	GridPane							namePane;
	private	StackPane							bottomRightPane;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(LocationChooserPane.class, COLOUR_PROPERTIES, RULE_SETS,
									   ListViewStyle.class, TableViewStyle.class, TreeViewStyle.class);

		// Create images from image data
		ImageData.add(ImageId.DIRECTORY,                                   ImgData.DIRECTORY);
		ImageData.add(ImageId.FILE,          AbstractTheme.MONO_IMAGE_KEY, ImgData.FILE);
		ImageData.add(ImageId.ARROW_UP,      AbstractTheme.MONO_IMAGE_KEY, ImgData.ARROW_UP);
		ImageData.add(ImageId.ARROW_DOWN,    AbstractTheme.MONO_IMAGE_KEY, ImgData.ARROW_DOWN);
		ImageData.add(ImageId.ARROW_LEFT,    AbstractTheme.MONO_IMAGE_KEY, ImgData.ARROW_LEFT);
		ImageData.add(ImageId.ARROW_RIGHT,   AbstractTheme.MONO_IMAGE_KEY, ImgData.ARROW_RIGHT);
		ImageData.add(ImageId.HOME,          AbstractTheme.MONO_IMAGE_KEY, ImgData.HOME);
		ImageData.add(ImageId.NEW_DIRECTORY, AbstractTheme.MONO_IMAGE_KEY, ImgData.NEW_DIRECTORY);
		ImageData.add(ImageId.REFRESH,       AbstractTheme.MONO_IMAGE_KEY, ImgData.REFRESH);
		ImageData.add(ImageId.BUTTON_BAR,    AbstractTheme.MONO_IMAGE_KEY, ImgData.BUTTON_BAR);
		ImageData.add(ImageId.PENCIL,        AbstractTheme.MONO_IMAGE_KEY, ImgData.PENCIL);
		ImageData.add(ImageId.EXPAND,        AbstractTheme.MONO_IMAGE_KEY, ImgData.EXPAND);
		ImageData.add(ImageId.COLLAPSE,      AbstractTheme.MONO_IMAGE_KEY, ImgData.COLLAPSE);
		ImageData.add(ImageId.SELECT,        AbstractTheme.MONO_IMAGE_KEY, ImgData.SELECT);
		ImageData.add(ImageId.COPY,          AbstractTheme.MONO_IMAGE_KEY, ImgData.COPY);
		ImageData.add(ImageId.COPY_LINES,    AbstractTheme.MONO_IMAGE_KEY, ImgData.COPY_LINES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public LocationChooserPane(
		MatcherScope	scope,
		SelectionMode	selectionMode,
		Path			initialDirectory,
		String			initialFilename,
		boolean			ignoreFilenameCase,
		boolean			showHiddenEntries,
		boolean			addAccelerators)
	{
		// Call alternative constructor
		this(scope, selectionMode, ignoreFilenameCase, showHiddenEntries, addAccelerators, 0, Collections.emptyList());
	}

	//------------------------------------------------------------------

	public LocationChooserPane(
		MatcherScope		scope,
		SelectionMode		selectionMode,
		boolean				ignoreFilenameCase,
		boolean				showHiddenEntries,
		boolean				addAccelerators,
		int					filterIndex,
		LocationMatcher...	filters)
	{
		// Call alternative constructor
		this(scope, selectionMode, ignoreFilenameCase, showHiddenEntries, addAccelerators, filterIndex,
			 List.of(filters));
	}

	//------------------------------------------------------------------

	public LocationChooserPane(
		MatcherScope							scope,
		SelectionMode							selectionMode,
		boolean									ignoreFilenameCase,
		boolean									showHiddenEntries,
		boolean									addAccelerators,
		int										filterIndex,
		Collection<? extends LocationMatcher>	filters)
	{
		// Validate arguments
		if (scope == null)
			throw new IllegalArgumentException("Null scope");
		if (filters == null)
			throw new IllegalArgumentException("Null filters");
		if ((scope != MatcherScope.FILES_AND_DIRECTORIES)
				&& filters.stream().anyMatch(filter -> filter.getScope() != scope))
			throw new IllegalArgumentException("Scope of a filter is inconsistent with scope of chooser");
		if (!filters.isEmpty() && ((filterIndex < 0) || (filterIndex >= filters.size())))
			throw new IllegalArgumentException("Filter index out of bounds: " + filterIndex);

		// Set style class
		getStyleClass().add(StyleClass.LOCATION_CHOOSER_PANE);

		// Button: previous directory
		ImageDataButton previousButton = new ImageDataButton(ImageId.ARROW_LEFT);
		previousButton.setDisable(true);
		previousButton.setOnAction(event -> tableView.openPreviousDirectory());

		// Button: next directory
		ImageDataButton nextButton = new ImageDataButton(ImageId.ARROW_RIGHT);
		nextButton.setDisable(true);
		nextButton.setOnAction(event -> tableView.openNextDirectory());

		// Button: open parent directory
		ImageDataButton openParentButton = new ImageDataButton(ImageId.ARROW_UP);
		openParentButton.setDisable(true);
		openParentButton.setOnAction(event -> tableView.openParentDirectory());

		// Button: home
		String homePathname = System.getProperty(SystemPropertyKey.USER_HOME_DIR);
		ImageDataButton homeButton = new ImageDataButton(ImageId.HOME, HOME_DIRECTORY_STR);
		homeButton.setDisable(homePathname == null);
		homeButton.setOnAction(event ->
		{
			// Open directory in table view
			tableView.openDirectory(Path.of(homePathname));

			// Request focus on table view
			tableView.requestFocus();
		});

		// Button: new directory
		ImageDataButton newDirectoryButton = new ImageDataButton(ImageId.NEW_DIRECTORY, NEW_DIRECTORY_STR);
		newDirectoryButton.setDisable(true);
		newDirectoryButton.setOnAction(event ->
		{
			Path directory = tableView.directory;
			if (directory != null)
			{
				try
				{
					List<String> names = DirectoryUtils.listDirectories(directory).stream()
							.map(dir -> dir.getFileName().toString())
							.toList();
					String name = new NameDialog(getWindow(), NEW_DIRECTORY_STR,
												 name0 -> !name0.isBlank() && !names.contains(name0)).showDialog();
					if (name != null)
					{
						// Get name of new directory
						Path newDirectory = null;
						try
						{
							newDirectory = directory.resolve(name);
						}
						catch (Exception e)
						{
							throw new BaseException(ErrorMsg.FAILED_TO_CREATE_DIRECTORY, e);
						}

						// Create directory
						try
						{
							// Create directory
							Files.createDirectory(newDirectory);

							// Update tree view
							treeView.update();

							// Update table view
							tableView.setDirectory(directory, true, false);

							// Request focus on table view
							tableView.requestFocus();

							// Select new directory in table view
							tableView.selectEntry(name);
						}
						catch (Exception e)
						{
							throw new FileException(ErrorMsg.FAILED_TO_CREATE_DIRECTORY, e, newDirectory);
						}
					}
				}
				catch (BaseException e)
				{
					ErrorDialog.show(getWindow(), NEW_DIRECTORY_STR, e);
				}
			}
		});

		// Create procedure to refresh tree view and table view
		IProcedure0 refreshView = () ->
		{
			// Update tree view
			treeView.update();

			// Update table view
			Path directory = tableView.directory;
			if (directory != null)
				tableView.setDirectory(directory, true, false);
		};

		// Button: refresh
		ImageDataButton refreshButton = new ImageDataButton(ImageId.REFRESH, REFRESH_STR);
		refreshButton.setOnAction(event -> refreshView.invoke());

		// Create left button pane
		HBox leftButtonPane = new HBox(BUTTON_PANE_SPACING, previousButton, nextButton, openParentButton, homeButton,
									   newDirectoryButton, refreshButton);
		leftButtonPane.setAlignment(Pos.CENTER_LEFT);

		// Create directory bar
		DirectoryBar directoryBar = new DirectoryBar();
		directoryBar.directory.addListener((observable, oldDirectory, directory) ->
		{
			// Open directory in table view
			tableView.openDirectory(directory);

			// Request focus on table view
			if (!treeView.isFocused())
				tableView.requestFocus();
		});

		// Create pathname field
		PathnameField pathnameField = new PathnameField();
		pathnameField.setVisible(false);
		pathnameField.setShowInvalidPathnameError(true);
		pathnameField.setOnAction(event ->
		{
			// Get location from pathname field
			Path location = pathnameField.getLocation();

			// If there is a location, open directory in table view
			if (location != null)
			{
				// Open directory in table view
				tableView.openDirectory(location);

				// Request focus on table view
				tableView.requestFocus();
			}
		});

		// Create navigation pane
		StackPane navigationPane = new StackPane(directoryBar, pathnameField);
		HBox.setHgrow(navigationPane, Priority.ALWAYS);

		// Button: navigation mode
		ImageDataButton navigationModeButton = new ImageDataButton(ImageId.PENCIL, SHOW_PATHNAME_FIELD_STR);
		navigationModeButton.setOnAction(event ->
		{
			if (directoryBar.isVisible())
			{
				directoryBar.setVisible(false);
				pathnameField.setVisible(true);
				pathnameField.requestFocus();
				pathnameField.selectAll();
				navigationModeButton.setImage(ImageData.image(ImageId.BUTTON_BAR));
				navigationModeButton.setTooltipText(SHOW_DIRECTORY_BAR_STR);
			}
			else
			{
				directoryBar.setVisible(true);
				pathnameField.setVisible(false);
				navigationModeButton.setImage(ImageData.image(ImageId.PENCIL));
				navigationModeButton.setTooltipText(SHOW_PATHNAME_FIELD_STR);
			}
		});

		// Create right button pane
		HBox rightButtonPane = new HBox(BUTTON_PANE_SPACING, navigationModeButton);
		rightButtonPane.setAlignment(Pos.CENTER_LEFT);

		// Create pane for buttons and navigation pane
		HBox topPane = new HBox(TOP_PANE_SPACING, leftButtonPane, navigationPane, rightButtonPane);
		topPane.setAlignment(Pos.CENTER_LEFT);
		topPane.setPadding(TOP_PANE_PADDING);

		// Create tree view
		treeView = new DirectoryTreeView(ignoreFilenameCase, showHiddenEntries);

		// Create focus indicator for tree view
		Region treeFocusIndicator = new Region();
		treeFocusIndicator.setMinHeight(VIEW_FOCUS_INDICATOR_HEIGHT);
		treeFocusIndicator.prefWidthProperty().bind(treeView.widthProperty().add(2.0));
		treeFocusIndicator.getStyleClass().add(StyleClass.FOCUS_INDICATOR);
		treeView.focusedProperty().addListener((observable, oldFocused, focused) ->
		{
			treeFocusIndicator.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, focused);
			treeFocusIndicator.setBackground(
					SceneUtils.createColouredBackground(focused ? getColour(ColourKey.FOCUS_INDICATOR)
																: Color.TRANSPARENT));
		});

		// Create table view
		String placeholderText = (filters.isEmpty() ? NO_STR : NO_MATCHING_STR)
									+ ((scope == MatcherScope.DIRECTORIES) ? DIRECTORIES_STR : ENTRIES_STR);
		tableView = new DirectoryTableView(scope.getUnconditionalMatcher(), selectionMode, placeholderText,
										   ignoreFilenameCase);

		// Create focus indicator for table view
		Region tableFocusIndicator = new Region();
		tableFocusIndicator.setMinHeight(VIEW_FOCUS_INDICATOR_HEIGHT);
		tableFocusIndicator.prefWidthProperty().bind(tableView.widthProperty().add(2.0));
		tableFocusIndicator.getStyleClass().add(StyleClass.FOCUS_INDICATOR);
		tableView.focusedProperty().addListener((observable, oldFocused, focused) ->
		{
			tableFocusIndicator.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, focused);
			tableFocusIndicator.setBackground(
					SceneUtils.createColouredBackground(focused ? getColour(ColourKey.FOCUS_INDICATOR)
																: Color.TRANSPARENT));
		});

		// Create focus-indicator pane
		HBox focusIndicatorPane = new HBox(treeFocusIndicator, FillerUtils.hBoxFiller(0.0), tableFocusIndicator);
		focusIndicatorPane.setAlignment(Pos.CENTER);
		focusIndicatorPane.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.FOCUS_INDICATOR_PANE_BORDER),
																  Side.TOP));
		focusIndicatorPane.getStyleClass().add(StyleClass.FOCUS_INDICATOR_PANE);

		// Create temporary container for tree view and table view
		HBox tempPane = new HBox(treeView, tableView);
		setVgrow(tempPane, Priority.ALWAYS);

		// Create pane for name field and filter choice box
		namePane = new GridPane();
		namePane.setHgap(CONTROL_H_GAP);
		namePane.setVgap(CONTROL_V_GAP);
		namePane.setAlignment(Pos.CENTER);
		namePane.setPadding(NAME_PANE_PADDING);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
		column.setFillWidth(false);
		namePane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		column.setFillWidth(false);
		namePane.getColumnConstraints().add(column);

		// Initialise row index
		int row = 0;

		// Create function to match a file-system location
		Predicate<Path> locationMatcher = location ->
				(filters.isEmpty() && scope.matches(location)) || filterSpinner.getItem().matches(location);

		// Create name field
		nameField = new TextField()
		{
			@Override
			public void paste()
			{
				// If system clipboard has a matching location, set it on this field ...
				if (ClipboardUtils.locationMatches(locationMatcher))
				{
					// Get first matching location from system clipboard
					Path location = ClipboardUtils.firstMatchingLocation(locationMatcher);

					// Set absolute location on this field
					if (location != null)
					{
						setText(PathUtils.absString(location));
						end();
					}
				}

				// ... otherwise, call superclass method to paste text into this field
				else
					super.paste();
			}
		};
		nameField.setPrefColumnCount(NAME_FIELD_NUM_COLUMNS);

		// Create filter pane or initialise name field
		switch (selectionMode)
		{
			case SINGLE:
			{
				// Create filter pane
				nameFilterPane = new SuggestionFilterPane<>(NAME_FILTER_CONVERTER, nameField,
															SubstringFilterPane.FilterMode.WILDCARD_START, false, true);
				namePane.addRow(row++, new Label(NAME_STR), nameFilterPane);

				// Set properties of name field
				TooltipDecorator.addTooltip(nameField, () -> (nameField.getLength() == 0) ? SUGGESTIONS_STR : null);

				// Update selected location when name changes
				nameField.textProperty().addListener((observable, oldName, name) ->
				{
					Path directory = tableView.directory;
					if ((directory == null) || StringUtils.isNullOrBlank(name))
						tableView.selectedLocations.clear();
					else
					{
						try
						{
							tableView.selectedLocations.setAll(directory.resolve(name));
						}
						catch (InvalidPathException e)
						{
							// ignore
						}
					}
				});

				// If Enter is pressed in name field, notify listeners that a location was chosen
				nameField.addEventHandler(ActionEvent.ACTION, event -> notifyLocationsChosen());

				// Set text of filter-mode button
				nameFilterPane.getFilterModeButton().setTooltipText(FILTER_MODE_STR);
				break;
			}

			case MULTIPLE:
				// Set properties of name field
				nameField.setEditable(false);
				nameField.setPadding(NON_EDITABLE_FIELD_PADDING);
				nameField.setBackground(SceneUtils.createColouredBackground(
																getColour(ColourKey.NON_EDITABLE_FIELD_BACKGROUND)));
				nameField.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.NON_EDITABLE_FIELD_BORDER)));
				nameField.getStyleClass().add(StyleClass.NON_EDITABLE_FIELD);
				namePane.addRow(row++, new Label(NAME_STR), nameField);
				break;
		}

		// Set drag-and-drop handler to accept a location that matches the current filter
		nameField.setOnDragOver(event ->
		{
			// Accept drag if dragboard contains a location that matches the current filter
			if (ClipboardUtils.locationMatches(event.getDragboard(), locationMatcher))
				event.acceptTransferModes(TransferMode.COPY);

			// Consume event
			event.consume();
		});

		// Set drag-and-drop handler to set matching location(s) on name field
		nameField.setOnDragDropped(event ->
		{
			switch (selectionMode)
			{
				case SINGLE:
				{
					// Get first matching location from clipboard
					Path location = ClipboardUtils.firstMatchingLocation(event.getDragboard(), locationMatcher);

					// Indicate that drag-and-drop is complete
					event.setDropCompleted(true);

					// Set pathname of location on name field
					if (location != null)
						nameField.setText(location.toString());
					break;
				}

				case MULTIPLE:
				{
					// Get matching locations from clipboard
					List<Path> locations = ClipboardUtils.matchingLocations(event.getDragboard(), locationMatcher);

					// Indicate that drag-and-drop is complete
					event.setDropCompleted(true);

					// Set pathnames of locations on name field; set selected locations
					if (!locations.isEmpty())
					{
						// Set pathnames of locations on name field
						StringBuilder buffer = new StringBuilder(1024);
						for (Path location : locations)
						{
							if (!buffer.isEmpty())
								buffer.append(' ');
							buffer.append('"');
							buffer.append(location.toString().replace("\"", "\\\""));
							buffer.append('"');
						}
						nameField.setText(buffer.toString());

						// Set selected locations
						tableView.selectedLocations.setAll(locations);
					}
					break;
				}
			}

			// Consume event
			event.consume();
		});

		// Create spinner for filter
		if (!filters.isEmpty())
		{
			// Create spinner
			filterSpinner = CollectionSpinner.leftRightH(HPos.CENTER, true, filters, null, null,
														 LocationMatcher::getDescription);
			filterSpinner.setItem(null);
			filterSpinner.itemProperty().addListener((observable, oldFilter, filter) ->
			{
				// Clear name field
				nameField.clear();

				// Update filter of table view
				tableView.filter = filter;

				// Force update of table view
				Path directory = tableView.directory;
				if (directory != null)
					tableView.setDirectory(directory, true, false);

				// Fire event to notify listeners that content of tooltip pop-up has changed
				filterSpinner.fireEvent(new PopUpEvent(PopUpEvent.CONTENT_CHANGED));
			});
			filterSpinner.setItem(filters.stream().skip(filterIndex).findFirst().orElse(null));

			// Add tooltip to spinner
			LabelPopUpManager popUpManager = TooltipDecorator.addTooltip(filterSpinner, () ->
			{
				List<String> suffixes = filterSpinner.getItem().getFilenameSuffixes();
				return suffixes.isEmpty() ? "" : suffixes.stream().collect(Collectors.joining(", *", "*", ""));
			});

			// Keep tooltip active when navigating through filters by making pop-up of empty tooltip transparent
			popUpManager.setPopUpDecorator(popUp ->
			{
				if ((popUp.getContent().iterator().next() instanceof Label label) && label.getText().isEmpty())
					popUp.getProperties().put(PopUpManager.SUPPRESS_POP_UP_PROPERTY_KEY, "");
			});

			// Add spinner to container
			namePane.addRow(row++, new Label(FILTER_STR), filterSpinner);
		}

		// Create bottom-left and bottom-right panes
		bottomLeftPane = new StackPane();
		bottomRightPane = new StackPane();

		// Create bottom pane
		HBox bottomPane = new HBox(bottomLeftPane, namePane, bottomRightPane);
		bottomPane.setAlignment(Pos.CENTER);

		// Add children to this pane
		getChildren().addAll(topPane, focusIndicatorPane, tempPane, bottomPane);

		// Create procedure to update table view with directory selected in tree view
		IProcedure0 updateTableViewWithTreeViewSelection = () ->
		{
			DirectoryTreeView.DirectoryItem item =
					(DirectoryTreeView.DirectoryItem)treeView.getSelectionModel().getSelectedItem();
			if (item != null)
			{
				Path directory = item.getValue();
				if (directory != null)
					tableView.setDirectory(directory);
			}
		};

		// Update table view when primary mouse button is clicked on directory in tree view
		treeView.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
		{
			if (event.getButton() == MouseButton.PRIMARY)
				updateTableViewWithTreeViewSelection.invoke();
		});

		// Update table view when Enter or space key is pressed on directory in tree view
		treeView.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if (List.of(KeyCode.ENTER, KeyCode.SPACE).contains(event.getCode()) && !event.isControlDown())
			{
				// Update table view
				updateTableViewWithTreeViewSelection.invoke();

				// Consume event
				event.consume();
			}
		});

		// Create procedure to update pathname field
		IProcedure1<Path> updatePathnameField = directory ->
		{
			String pathname = (directory == null) ? "" : PathUtils.absString(directory);
			pathnameField.setText(pathname);
			Platform.runLater(() ->
			{
				pathnameField.end();
				pathnameField.deselect();
			});
		};

		// Update components when directory changes
		tableView.directoryChangedNotifier.addListener(observable ->
		{
			// Get directory
			Path directory = tableView.directory;

			// Update 'open parent directory' button
			String text = (directory == null) ? null : tableView.getOpenParentDirectoryCommand();
			openParentButton.setTooltipText(text);
			openParentButton.setDisable(text == null);

			// Update 'new directory' and 'refresh' buttons
			newDirectoryButton.setDisable(directory == null);
			refreshButton.setDisable(directory == null);

			// Update directory bar
			directoryBar.directory.set(directory);

			// Update pathname field
			updatePathnameField.invoke(directory);

			// Clear name field
			nameField.clear();

			// Update list of suggestions for name filter
			if (nameFilterPane != null)
				nameFilterPane.setSuggestions(tableView.getFilteredLocations());
		});

		// Update name field when table-view selection changes
		switch (selectionMode)
		{
			case SINGLE:
				tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldItem, item) ->
				{
					if ((item != null) && tableView.filter.matches(item.location))
						nameField.setText(item.location.getFileName().toString());
					else
						nameField.clear();
				});
				break;

			case MULTIPLE:
				tableView.selectedLocations.addListener((InvalidationListener) observable ->
				{
					if (tableView.selectedLocations.isEmpty())
						nameField.clear();
					else
					{
						StringBuilder buffer = new StringBuilder(256);
						for (Path location : tableView.selectedLocations)
						{
							if (!buffer.isEmpty())
								buffer.append(' ');
							buffer.append('"');
							buffer.append(location.getFileName().toString().replace("\"", "\\\""));
							buffer.append('"');
						}
						nameField.setText(buffer.toString());
					}
				});
				break;
		}

		// Update buttons when table-view history changes
		tableView.historyChangedNotifier.addListener(observable ->
		{
			// Update 'previous directory' button
			previousButton.setDisable(!tableView.history.hasPrevious());
			previousButton.setTooltipText(getQuotedName(tableView.history.getPrevious(), BACK_TO_STR));

			// Update 'next directory' button
			nextButton.setDisable(!tableView.history.hasNext());
			nextButton.setTooltipText(getQuotedName(tableView.history.getNext(), FORWARD_TO_STR));
		});

		// Open previous directory when mouse 'back' button is pressed; open next directory when mouse 'forward' button
		// is pressed
		addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
		{
			switch (event.getButton())
			{
				case BACK:
					// Open previous directory
					tableView.openPreviousDirectory();

					// Consume event
					event.consume();
					break;

				case FORWARD:
					// Open next directory
					tableView.openNextDirectory();

					// Consume event
					event.consume();
					break;

				default:
					// do nothing
					break;
			}
		});

		// Add accelerators to scene
		if (addAccelerators)
		{
			sceneProperty().addListener((observable, oldScene, scene) ->
			{
				// Remove accelerators from old scene
				if (oldScene != null)
					oldScene.getAccelerators().clear();

				// Add accelerators to new scene
				if (scene != null)
				{
					scene.getAccelerators().put(KEY_COMBO_OPEN_PREVIOUS, tableView::openPreviousDirectory);
					scene.getAccelerators().put(KEY_COMBO_OPEN_NEXT,     tableView::openNextDirectory);
					scene.getAccelerators().put(KEY_COMBO_OPEN_PARENT,   tableView::openParentDirectory);
					scene.getAccelerators().put(KEY_COMBO_REFRESH1,      refreshView::invoke);
					scene.getAccelerators().put(KEY_COMBO_REFRESH2,      refreshView::invoke);
				}
			});
		}

		// When widths of tree view and table view have been determined, replace temporary container of tree view and
		// table view with split pane
		tableView.layoutBoundsProperty().addListener((observable, oldBounds, bounds) ->
		{
			if (splitPane == null)
			{
				if ((splitPaneDividerPosition == null) && (bounds != null)
						&& (bounds.getWidth() > 0.0) && (bounds.getHeight() > 0.0))
				{
					// Get width of table view
					double tableWidth = bounds.getWidth();

					// If width of tree view has been been determined, calculate divider position
					bounds = treeView.getLayoutBounds();
					if ((bounds != null) && (bounds.getWidth() > 0.0) && (bounds.getHeight() > 0.0))
					{
						// Get width of tree view
						double treeWidth = bounds.getWidth();

						// Calculate divider position
						splitPaneDividerPosition = treeWidth / (treeWidth + tableWidth);
					}
				}

				// Replace temporary container of tree view and table view with split pane
				if (splitPaneDividerPosition != null)
				{
					// Remove temporary container from this pane
					int index = getChildren().indexOf(tempPane);
					getChildren().remove(index);

					// Remove tree view and table view from temporary container
					tempPane.getChildren().clear();

					// Create split pane for tree view and table view, and add split pane to this pane
					splitPane = new SplitPane2(treeView, tableView);
					splitPane.setDividerPosition(0, splitPaneDividerPosition);
					setVgrow(splitPane, Priority.ALWAYS);
					getChildren().add(index, splitPane);
				}
			}
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void setRootDirDisplayName(
		String	name)
	{
		rootDirDisplayName = (name == null) ? DEFAULT_ROOT_DIR_DISPLAY_NAME : name;
	}

	//------------------------------------------------------------------

	private static boolean isRoot(
		Path	directory)
	{
		return (directory != null) && (directory.getNameCount() == 0);
	}

	//------------------------------------------------------------------

	private static String getRootName(
		Path	directory)
	{
		try
		{
			String name = Files.getFileStore(directory).toString();
			if (name.startsWith("/ "))
				name = rootDirDisplayName;
			return name;
		}
		catch (IOException e)
		{
			return directory.toString();
		}
	}

	//------------------------------------------------------------------

	private static String getQuotedName(
		Path	directory,
		String	prefix)
	{
		return (directory == null) ? null
								   : prefix + (isRoot(directory) ? ROOT_DIRECTORY_STR
																 : quote(directory.getFileName().toString()));
	}

	//------------------------------------------------------------------

	private static String quote(
		String	text)
	{
		return "'" + text + "'";
	}

	//------------------------------------------------------------------

	private static void copyToClipboard(
		Window	window,
		String	title,
		String	text)
	{
		try
		{
			ClipboardUtils.putTextThrow(text);
		}
		catch (BaseException e)
		{
			ErrorDialog.show(window, title, e);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Executes the specified task on a background thread.
	 *
	 * @param task
	 *          the task that will be executed.
	 */

	private static void executeTask(
		Task<?>	task)
	{
		ExecutorService executor = Executors.newSingleThreadExecutor(runnable ->
				new Thread(runnable, MethodHandles.lookup().lookupClass().getSimpleName() + "-" + ++threadIndex));
		executor.execute(task);
		executor.shutdown();
	}

	//------------------------------------------------------------------

	private static ImageView icon(
		String	imageId)
	{
		return ImageUtils.smoothImageView(ImageData.image(imageId));
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

	private static List<Path> findLinuxMediaRootDirectories()
	{
		List<Path> directories = new ArrayList<>();
		String mediaPathnamePrefix = String.format(LINUX_MEDIA_PATHNAME_PREFIX, System.getProperty("user.name"));
		for (FileStore store : FileSystems.getDefault().getFileStores())
		{
			String text = store.toString();
			if (text.startsWith(mediaPathnamePrefix))
			{
				Path location = Path.of(text.split("\\s+")[0]);
				if (Files.isDirectory(location, LinkOption.NOFOLLOW_LINKS))
					directories.add(location);
			}
		}
		return directories;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Path getDirectory()
	{
		return tableView.directory;
	}

	//------------------------------------------------------------------

	public int getFilterIndex()
	{
		return (filterSpinner == null) ? -1 : filterSpinner.getValue();
	}

	//------------------------------------------------------------------

	public ObservableList<Path> getSelectedLocations()
	{
		if (selectedLocations == null)
			selectedLocations = FXCollections.unmodifiableObservableList(tableView.selectedLocations);
		return selectedLocations;
	}

	//------------------------------------------------------------------

	public void notifyLocationsChosen()
	{
		if (!tableView.selectedLocations.isEmpty())
		{
			fireEvent(new LocationChooserEvent(LocationChooserEvent.LOCATIONS_CHOSEN, this,
											   tableView.selectedLocations));
		}
	}

	//------------------------------------------------------------------

	public GridPane getNamePane()
	{
		return namePane;
	}

	//------------------------------------------------------------------

	public StackPane getBottomLeftPane()
	{
		return bottomLeftPane;
	}

	//------------------------------------------------------------------

	public StackPane getBottomRightPane()
	{
		return bottomRightPane;
	}

	//------------------------------------------------------------------

	public double getSplitPaneDividerPosition()
	{
		return splitPane.getDividerPositions()[0];
	}

	//------------------------------------------------------------------

	public void setSplitPaneDividerPosition(
		double	position)
	{
		if (splitPane == null)
			splitPaneDividerPosition = position;
		else
			splitPane.setDividerPosition(0, position);
	}

	//------------------------------------------------------------------

	public double[] getTableViewColumnWidths()
	{
		List<TableColumn<DirectoryEntry, ?>> columns = tableView.getColumns();
		double[] widths = new double[columns.size()];
		for (int i = 0; i < widths.length; i++)
			widths[i] = columns.get(i).getWidth();
		return widths;
	}

	//------------------------------------------------------------------

	public void setTableViewColumnWidths(
		double[]	widths)
	{
		List<TableColumn<DirectoryEntry, ?>> columns = tableView.getColumns();
		for (int i = 0; i < widths.length; i++)
			columns.get(i).setPrefWidth(widths[i]);
	}

	//------------------------------------------------------------------

	public void initDirectoryTree(
		Path	directory,
		String	filename)
	{
		// Create procedure that will be executed when task has finished
		IProcedure0	onFinished = () ->
		{
			// Set root of tree view
			treeView.init();

			// Set directory on table view
			tableView.setDirectory((directory == null) ? DEFAULT_INITIAL_DIRECTORY : directory);

			// Assume focus on name field
			Node focusNode = nameField;

			// Set filename on name field
			if (filename != null)
			{
				// Set filename on name field
				nameField.setText(filename);

				// Select entry in table view
				if (tableView.selectEntry(filename))
					focusNode = tableView;
			}

			// Request focus on table view or name field
			focusNode.requestFocus();
		};

		// Create task to read root directories
		Task<Void> task = new Task<>()
		{
			{
				// Initialise task status
				updateTitle(SCANNING_FILE_SYSTEM_STR);
				updateMessage(SCANNING_ROOT_DIRECTORIES_STR + " " + ELLIPSIS_STR);
				updateProgress(-1, 1);
			}

			@Override
			protected Void call()
				throws Exception
			{
				// Get root directories
				List<Path> directories = new ArrayList<>();
				for (Path directory : FileSystems.getDefault().getRootDirectories())
					directories.add(directory);

				// Linux: get root directories of removable media
				if (OsUtils.isUnixLike())
					directories.addAll(findLinuxMediaRootDirectories());

				// Read subdirectories of root directories
				for (Path directory : directories)
				{
					// Update task status
					String message = READING_DIRECTORY_STR + " " + directory;
					updateMessage(message);

					// Read subdirectories
					try
					{
						DirectoryUtils.listDirectories(directory);
					}
					catch (FileException e)
					{
						Logger.INSTANCE.error(message, e);
					}
				}

				// Return nothing
				return null;
			}

			@Override
			protected void succeeded()
			{
				// Perform remaining initialisation
				onFinished.invoke();
			}

			@Override
			protected void failed()
			{
				// Display error message
				ErrorDialog.show(getWindow(), getTitle(), getException());

				// Perform remaining initialisation
				onFinished.invoke();
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(getWindow(), task);

		// Execute task on background thread
		executeTask(task);
	}

	//------------------------------------------------------------------

	private Window getWindow()
	{
		return SceneUtils.getWindow(this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: DIRECTORY HISTORY


	private static class History
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MAX_LENGTH	= 256;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int					index;
		private	LinkedList<Path>	directories;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private History()
		{
			// Initialise instance variables
			directories = new LinkedList<>();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private boolean hasPrevious()
		{
			return (index > 0);
		}

		//--------------------------------------------------------------

		private boolean hasNext()
		{
			return (index < directories.size() - 1);
		}

		//--------------------------------------------------------------

		private Path getPrevious()
		{
			return hasPrevious() ? directories.get(index - 1) : null;
		}

		//--------------------------------------------------------------

		private Path getNext()
		{
			return hasNext() ? directories.get(index + 1) : null;
		}

		//--------------------------------------------------------------

		private Path previous()
		{
			return hasPrevious() ? directories.get(--index) : null;
		}

		//--------------------------------------------------------------

		private Path next()
		{
			return hasNext() ? directories.get(++index) : null;
		}

		//--------------------------------------------------------------

		private void add(
			Path	directory)
		{
			// Don't add directory if it is current directory
			if ((index < directories.size()) && directories.get(index).equals(directory))
				return;

			// Increment index
			if (!directories.isEmpty())
				++index;

			// Remove directories after current directory
			while (directories.size() > index)
				directories.removeLast();

			// Remove oldest directories while list is full
			while (directories.size() >= MAX_LENGTH)
			{
				directories.removeFirst();
				if (--index < 0)
					index = 0;
			}

			// Add directory
			directories.add(directory);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: DIRECTORY ENTRY


	private static class DirectoryEntry
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	Path		location;
		private	boolean		isDirectory;
		private	long		size;
		private	FileTime	modificationTime;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DirectoryEntry(
			Path	location,
			boolean	isDirectory)
		{
			// Initialise instance variables
			this.location = location;
			this.isDirectory = isDirectory;
			try
			{
				size = -1;
				if (!isDirectory)
					size = Files.size(location);
			}
			catch (IOException e)
			{
				// ignore
			}
			try
			{
				modificationTime = Files.getLastModifiedTime(location, LinkOption.NOFOLLOW_LINKS);
			}
			catch (IOException e)
			{
				// ignore
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		private static Comparator<DirectoryEntry> nameComparator(
			boolean	ignoreCase)
		{
			return ignoreCase ? Comparator.comparing(DirectoryEntry::getName, String.CASE_INSENSITIVE_ORDER)
							  : Comparator.comparing(DirectoryEntry::getName);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return location.toString();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private String getName()
		{
			return location.getFileName().toString();
		}

		//--------------------------------------------------------------

		private LocalDateTime getLocalModificationTime()
		{
			return (modificationTime == null)
								? null
								: LocalDateTime.ofInstant(modificationTime.toInstant(), ZoneId.systemDefault());
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: DIRECTORY TREE VIEW


	private static class DirectoryTreeView
		extends TreeView<Path>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** Miscellaneous strings. */
		private static final	String	EXPAND_STR		= "Expand";
		private static final	String	COLLAPSE_STR	= "Collapse";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The manager of the pop-up windows that are displayed for the cells of this tree view. */
		private	CellPopUpManager	cellPopUpManager;

		/** A list of the cells of this tree view. */
		private	List<Cell>			cells;

		/** Flag: if {@code true}, the letter case of filenames is ignored when sorting directory entries. */
		private	boolean				ignoreFilenameCase;

		/** Flag: if {@code true}, directories considered to be hidden are included in this tree view. */
		private	boolean				showHiddenDirectories;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DirectoryTreeView(
			boolean	ignoreFilenameCase,
			boolean	showHiddenDirectories)
		{
			// Initialise instance variables
			cellPopUpManager = new CellPopUpManager(CELL_POP_UP_DELAY);
			cells = new ArrayList<>();
			this.ignoreFilenameCase = ignoreFilenameCase;
			this.showHiddenDirectories = showHiddenDirectories;

			// Set properties
			setCellFactory(treeView -> new Cell());
			setShowRoot(false);
			getStyleClass().add(TreeViewStyle.StyleClass.TREE_VIEW);

			// Update cell backgrounds on change of state
			if (StyleManager.INSTANCE.notUsingStyleSheet())
			{
				// Update cell backgrounds when selection changes
				getSelectionModel().selectedIndexProperty().addListener(observable -> updateCellBackgrounds());

				// Update cell backgrounds when focus changes
				focusedProperty().addListener(observable -> updateCellBackgrounds());

				// Update cell backgrounds when focused item changes
				getFocusModel().focusedItemProperty().addListener(observable -> updateCellBackgrounds());
			}

			// Ensure cells are redrawn if scroll bar is hidden
			widthProperty().addListener(observable -> Platform.runLater(this::refresh));

			// Display context menu on request
			addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event ->
			{
				// Get selected item
				TreeItem<Path> treeItem = getSelectionModel().getSelectedItem();

				// If an item is selected, show context menu for it
				if (treeItem != null)
				{
					Path directory = treeItem.getValue();
					if (directory != null)
					{
						// Create context menu
						ContextMenu menu = new ContextMenu();

						// Menu item: expand/collapse
						if (!treeItem.isLeaf())
						{
							String text = treeItem.isExpanded() ? COLLAPSE_STR : EXPAND_STR;
							ImageView graphic = icon(treeItem.isExpanded() ? ImageId.COLLAPSE : ImageId.EXPAND);
							MenuItem menuItem = new MenuItem(text, graphic);
							menuItem.setOnAction(event0 ->
							{
								treeItem.setExpanded(!treeItem.isExpanded());
								refresh();
							});
							menu.getItems().add(menuItem);
						}

						// Display context menu at location of event
						if (!menu.getItems().isEmpty())
							menu.show(getWindow(), event.getScreenX(), event.getScreenY());
					}
				}
			});
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void init()
		{
			DirectoryItem root = new DirectoryItem(null);
			root.setExpanded(true);
			setRoot(root);
		}

		//--------------------------------------------------------------

		private Window getWindow()
		{
			return SceneUtils.getWindow(this);
		}

		//--------------------------------------------------------------

		private void update()
		{
			// Create list of directories that are associated with tree items that are expanded
			List<Path> directories = new ArrayList<>();
			int row = 0;
			while (true)
			{
				TreeItem<Path> item = getTreeItem(row++);
				if (item == null)
					break;
				if (item.isExpanded())
					directories.add(item.getValue());
			}

			// Get root item
			TreeItem<Path> root = getRoot();

			// Declare class to reset child lists of tree items
			class Search1
			{
				void visit(DirectoryItem item)
				{
					if (item.childListInitialised)
					{
						for (TreeItem<Path> child : item.getChildren())
							visit((DirectoryItem)child);
					}
					item.childListInitialised = false;

					if (item != root)
						item.setExpanded(false);
				}
			}

			// Reset child lists of tree items
			new Search1().visit((DirectoryItem)root);

			// Declare class to expand tree items that were previously expanded
			class Search2
			{
				void visit(TreeItem<Path> item)
				{
					if ((item == root) || directories.contains(item.getValue()))
					{
						item.setExpanded(true);

						for (TreeItem<Path> child : item.getChildren())
							visit(child);
					}
				}
			}

			// Expand tree items that were previously expanded
			new Search2().visit(root);

			// Clear selection
			getSelectionModel().clearSelection();
		}

		//--------------------------------------------------------------

		/**
		 * Updates the backgrounds of the cells of this tree view.
		 */

		private void updateCellBackgrounds()
		{
			for (Cell cell : cells)
				cell.updateBackground();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Member classes : inner classes
	////////////////////////////////////////////////////////////////////


		// CLASS: DIRECTORY-TREE ITEM


		/**
		 * This class implements a tree item for the enclosing tree view.
		 */

		private class DirectoryItem
			extends TreeItem<Path>
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			/** Miscellaneous strings. */
			private static final	String	EXPAND_DIRECTORY_STR	= "Expand directory";
			private static final	String	OK_STR					= "OK";

		////////////////////////////////////////////////////////////////
		//  Instance variables
		////////////////////////////////////////////////////////////////

			/** Flag: if {@code true}, the list of children of this tree item has been initialised by {@link
				#getChildren()}. */
			private	boolean childListInitialised;

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			/**
			 * Creates a new instance of a {@link TreeItem} for the specified directory.
			 *
			 * @param directory
			 *          the directory with which the tree item will be associated.
			 */

			private DirectoryItem(
				Path	directory)
			{
				// Call superclass constructor
				super(directory);
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			/**
			 * {@inheritDoc}
			 */

			@Override
			public ObservableList<TreeItem<Path>> getChildren()
			{
				// Get list of children
				ObservableList<TreeItem<Path>> children = super.getChildren();

				// If list of children is not initialised, create child item for each subdirectory and add it to list
				if (!childListInitialised)
				{
					// Set cursor to 'busy'
					DirectoryTreeView.this.setCursor(Cursor.WAIT);
					Platform.requestNextPulse();

					// Initialise list of child items
					List<DirectoryItem> items = new ArrayList<>();

					// Get directory
					Path directory = getValue();

					// Case: root directory
					if (directory == null)
					{
						// Add items for root directories
						for (Path rootDirectory : FileSystems.getDefault().getRootDirectories())
							items.add(new DirectoryItem(rootDirectory));

						// Linux: add items for root directories of removable media
						if (OsUtils.isUnixLike())
						{
							for (Path rootDirectory : findLinuxMediaRootDirectories())
								items.add(new DirectoryItem(rootDirectory));
						}
					}

					// Case: not root directory
					else
					{
						try
						{
							// Initialise list of subdirectories whose 'hidden' status could not be determined
							List<String> indeterminateLocations = new ArrayList<>();

							// Add items for subdirectories
							for (Path location : DirectoryUtils.listDirectories(directory))
							{
								try
								{
									if (showHiddenDirectories || !Files.isHidden(location))
										items.add(new DirectoryItem(location));
								}
								catch (IOException e)
								{
									indeterminateLocations.add(location.getFileName().toString());
								}
							}

							// Report subdirectories whose 'hidden' status could not be determined
							if (!indeterminateLocations.isEmpty())
							{
								Platform.runLater(() ->
								{
									MessageListDialog.show(
											getWindow(), EXPAND_DIRECTORY_STR, MessageIcon32.WARNING.get(),
											ErrorMsg.FAILED_TO_DETERMINE_HIDDEN_STATUS, indeterminateLocations, true,
											ButtonInfo.of(HPos.RIGHT, OK_STR));
								});
							}
						}
						catch (FileException e)
						{
							// Report exception whose cause is not 'access denied'
							if (!(e.getCause() instanceof AccessDeniedException))
								Platform.runLater(() -> ErrorDialog.show(getWindow(), EXPAND_DIRECTORY_STR, e));
						}
					}

					// Sort items
					items.sort(Comparator.<DirectoryItem, String>comparing(item ->
						{
							Path location = item.getValue();
							if (location != null)
								location = location.getFileName();
							return (location == null) ? null : location.toString();
						},
						Comparator.<String>nullsFirst(ignoreFilenameCase ? String.CASE_INSENSITIVE_ORDER
																		 : Comparator.naturalOrder()))
					);

					// Set items on list of children
					children.setAll(items);

					// Update 'initialised' flag
					childListInitialised = true;

					// Restore default cursor
					DirectoryTreeView.this.setCursor(Cursor.DEFAULT);
				}

				// Return list of children
				return children;
			}

			//----------------------------------------------------------

			/**
			 * {@inheritDoc}
			 */

			@Override
			public boolean isLeaf()
			{
				return getChildren().isEmpty();
			}

			//----------------------------------------------------------

		}

		//==============================================================

	////////////////////////////////////////////////////////////////////
	//  Member classes : inner classes
	////////////////////////////////////////////////////////////////////


		// CLASS: CELL


		/**
		 * This class implements a cell of the enclosing tree view.
		 */

		private class Cell
			extends TreeCell<Path>
			implements CellPopUpManager.ICell<Path>
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			/** The padding around the label of a cell. */
			private static final	Insets	LABEL_PADDING	= new Insets(2.0, 0.0, 3.0, 0.0);

			/** The padding around the pop-up for a cell. */
			private static final	Insets	POP_UP_PADDING	= new Insets(1.0, 5.0, 2.0, 5.0);

			/** The padding around the disclosure node. */
			private static final	Insets	DISCLOSURE_NODE_PADDING	= new Insets(6.0, 6.0, 4.0, 8.0);

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			/**
			 * Creates a new instance of a cell of the enclosing tree view.
			 */

			private Cell()
			{
				// Set properties
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				setPadding(Insets.EMPTY);
				setPrefWidth(0.0);
				setAlignment(Pos.TOP_LEFT);

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
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : CellPopUpManager.ICell interface
		////////////////////////////////////////////////////////////////

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
					label.setPadding(POP_UP_PADDING);
					label.setBackground(SceneUtils.createColouredBackground(
							getColour(TreeViewStyle.ColourKey.CELL_POPUP_BACKGROUND)));
					label.setBorder(SceneUtils.createSolidBorder(getColour(TreeViewStyle.ColourKey.CELL_POPUP_BORDER)));
					label.getStyleClass().add(TreeViewStyle.StyleClass.CELL_POPUP_LABEL);
				}

				// Return label
				return label;
			}

			//----------------------------------------------------------

			/**
			 * {@inheritDoc}
			 */

			@Override
			public Point2D getPrefPopUpLocation(
				Node	content)
			{
				Node node = getGraphic();
				return (node == null) ? null
									  : PopUpUtils.createLocator(node, VHPos.CENTRE_LEFT, VHPos.CENTRE_LEFT, -6.0, 0.0)
													.getLocation(content.getLayoutBounds(), null);
			}

			//----------------------------------------------------------

			/**
			 * {@inheritDoc}
			 */

			@Override
			public Window getWindow()
			{
				return DirectoryTreeView.this.getWindow();
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			/**
			 * {@inheritDoc}
			 */

			@Override
			protected void layoutChildren()
			{
				// Adjust padding around disclosure node
				if (lookup(StyleSelector.TREE_CELL_DISCLOSURE_NODE) instanceof Region region)
					region.setPadding(DISCLOSURE_NODE_PADDING);

				// Call superclass method
				super.layoutChildren();
			}

			//----------------------------------------------------------

			/**
			 * {@inheritDoc}
			 */

			@Override
			protected void updateItem(
				Path	directory,
				boolean	empty)
			{
				// Call superclass method
				super.updateItem(directory, empty);

				// Set background
				updateBackground();

				// Set graphic
				setGraphic(empty ? null : createLabel());
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods
		////////////////////////////////////////////////////////////////

			/**
			 * Updates the background colour of this cell and the colour of its disclosure arrow.
			 */

			private void updateBackground()
			{
				if (StyleManager.INSTANCE.notUsingStyleSheet())
				{
					// Set colour of background
					TreeItem<Path> item = getTreeItem();
					boolean selected = getSelectionModel().getSelectedItems().contains(item);
					boolean focused = getTreeView().isFocused();
					Color colour = selected
										? focused
												? getColour(TreeViewStyle.ColourKey.CELL_BACKGROUND_SELECTED_FOCUSED)
												: getColour(TreeViewStyle.ColourKey.CELL_BACKGROUND_SELECTED)
										: getColour(TreeViewStyle.ColourKey.CELL_BACKGROUND);
					if (!isEmpty() && !selected && focused && (getFocusModel().getFocusedItem() == item))
					{
						setBackground(SceneUtils.createColouredBackground(
								getColour(TreeViewStyle.ColourKey.CELL_BACKGROUND_FOCUSED), Insets.EMPTY, colour,
								new Insets(1.0)));
					}
					else
						setBackground(SceneUtils.createColouredBackground(colour));

					// Set colour of disclosure arrow
					if (lookup(StyleSelector.TREE_CELL_DISCLOSURE_ARROW) instanceof Region arrow)
					{
						String key = selected ? TreeViewStyle.ColourKey.CELL_DISCLOSURE_ARROW_SELECTED
											  : TreeViewStyle.ColourKey.CELL_DISCLOSURE_ARROW;
						arrow.setBackground(SceneUtils.createColouredBackground(getColour(key)));
					}
				}
			}

			//----------------------------------------------------------

			/**
			 * Creates and returns a label for the directory that is associated with this cell.
			 *
			 * @return a label for the directory that is associated with this cell, or {@code null} if there is no such
			 *         directory.
			 */

			private Label createLabel()
			{
				// Create label
				Label label = null;
				Path directory = getItem();
				if (directory != null)
				{
					label = new Label(isRoot(directory) ? getRootName(directory) : directory.getFileName().toString());
					label.setPadding(LABEL_PADDING);
					label.setTextFill(getColour(TreeViewStyle.ColourKey.CELL_TEXT));
					label.getStyleClass().add(TreeViewStyle.StyleClass.CELL_LABEL);
				}

				// Return label
				return label;
			}

			//----------------------------------------------------------

		}
	}

	//==================================================================


	// CLASS: DIRECTORY TABLE VIEW


	private static class DirectoryTableView
		extends TableView<DirectoryEntry>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The extra width of the table to allow for the vertical scroll bar. */
		private static final	double	EXTRA_WIDTH	= 17.0;

		/** The padding around the label of a header cell. */
		private static final	Insets	HEADER_CELL_LABEL_PADDING	= new Insets(1.0, 4.0, 2.0, 4.0);

		/** The delay (in milliseconds) before a pop-up for a header cell is displayed after it is activated. */
		private static final	int		HEADER_CELL_POP_UP_DELAY	= 1000;

		/** The padding around the label of a cell. */
		private static final	Insets	CELL_LABEL_PADDING	= new Insets(2.0, 6.0, 2.0, 6.0);

		/** The factor by which the size of the default font is multiplied to give the size of the font of the
			placeholder label. */
		private static final	double	PLACEHOLDER_LABEL_FONT_SIZE_FACTOR	= 1.2;

		/** The formatter that is applied to the size of a file. */
		private static final	DecimalFormat	SIZE_FORMATTER;

		/** The formatter that is applied to the last modification time of a file or directory. */
		private static final	DateTimeFormatter	MODIFICATION_TIME_FORMATTER	=
				DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

		/** Miscellaneous strings. */
		private static final	String	OPEN_DIRECTORY_STR		= "Open directory";
		private static final	String	UP_TO_STR				= "Up to ";
		private static final	String	UNKNOWN_STR				= "Unknown";
		private static final	String	UPDATE_ENTRIES_STR		= "Update entries";
		private static final	String	COPY_ALL_ENTRIES_STR	= "Copy all entries";

		/** Error messages. */
		private interface ErrorMsg
		{
			String	DIRECTORY_HAS_NO_ROOT		= "The directory has no root.";
			String	DIRECTORY_DOES_NOT_EXIST	= "The directory does not exist.";
		}

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The manager of the pop-up windows that are displayed for the cells of this table view. */
		private	CellPopUpManager			cellPopUpManager;

		/** A list of the items that are represented in this table view. */
		private	ElasticList<DirectoryEntry>	itemList;

		/** A list of the cells of this table view. */
		private	List<Cell>					cells;

		/** The location of the directory that is represented in this table view. */
		private	Path						directory;

		/** A list of the entries of the current directory. */
		private	List<DirectoryEntry>		entries;

		/** The history of directories that were opened on the table view. */
		private	History						history;

		/** The notifier of changes to {@link #directory}. */
		private	ChangeNotifier<Void>		directoryChangedNotifier;

		/** The notifier of changes to {@link #history}. */
		private	ChangeNotifier<Void>		historyChangedNotifier;

		/** The location of the directory entries that was selected by the user. */
		private	ObservableList<Path>		selectedLocations;

		/** The filter that is applied to the entries of the current directory. */
		private	LocationMatcher				filter;

		/** Flag: if {@code true}, the letter case of filenames is ignored when sorting directory entries. */
		private	boolean						ignoreFilenameCase;

		/** The manager of pop-ups for the header cells of this table view. */
		private	LabelPopUpManager			headerPopUpManager;

		/** Flag: if {@code true}, the header of this table view has been initialised. */
		private	boolean						headerInitialised;

		/** The label that is displayed when this table view is empty. */
		private	Label						placeholderLabel;

		/** Flag: if {@code true}, the {@link #setDirectory(Path, boolean, boolean)} method is being executed. */
		private	boolean						updatingDirectory;

		// WORKAROUND for a bug in JavaFX: isFocused() sometimes returns false when the table view has focus
		/** Flag: if {@code true}, this table view has keyboard focus. */
		private	boolean						focused;

	////////////////////////////////////////////////////////////////////
	//  Static initialiser
	////////////////////////////////////////////////////////////////////

		static
		{
			SIZE_FORMATTER = new DecimalFormat();
			SIZE_FORMATTER.setGroupingSize(3);
		}

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DirectoryTableView(
			LocationMatcher	filter,
			SelectionMode	selectionMode,
			String			placeholderText,
			boolean			ignoreFilenameCase)
		{
			// Initialise instance variables
			cellPopUpManager = new CellPopUpManager(CELL_POP_UP_DELAY);
			itemList = new ElasticList<>(this);
			cells = new ArrayList<>();
			entries = Collections.emptyList();
			history = new History();
			directoryChangedNotifier = new ChangeNotifier<>();
			historyChangedNotifier = new ChangeNotifier<>();
			selectedLocations = FXCollections.observableArrayList();
			this.filter = filter;
			this.ignoreFilenameCase = ignoreFilenameCase;

			// Create columns
			double width = EXTRA_WIDTH;
			for (Column column : Column.VISIBLE_COLUMNS)
			{
				// Create table column
				TableColumn<DirectoryEntry, DirectoryEntry> tableColumn = column.createColumn(this, ignoreFilenameCase);

				// Set properties of table column
				tableColumn.setId(column.getKey());
				tableColumn.setPrefWidth(column.prefWidth);

				// Add column to list
				getColumns().add(tableColumn);

				// Increment width
				width += column.prefWidth;
			}

			// Set properties
			getSelectionModel().setSelectionMode(selectionMode);
			setPrefWidth(width);
			getStyleClass().add(TableViewStyle.StyleClass.TABLE_VIEW);

			// Set row factory
			if (StyleManager.INSTANCE.notUsingStyleSheet())
			{
				setRowFactory(table ->
				{
					TableRow<DirectoryEntry> row = new TableRow<>();
					row.setBackground(SceneUtils.createColouredBackground(
							getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_EMPTY)));
					return row;
				});
			}

			// Set placeholder
			placeholderLabel = Labels.expansive(placeholderText, PLACEHOLDER_LABEL_FONT_SIZE_FACTOR,
												getColour(ColourKey.TABLE_VIEW_PLACEHOLDER_TEXT),
												getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_EMPTY));
			placeholderLabel.getStyleClass().add(StyleClass.PLACEHOLDER_LABEL);
			setPlaceholder(placeholderLabel);

			// Update cell backgrounds on change of state
			if (StyleManager.INSTANCE.notUsingStyleSheet())
			{
				// Update cell backgrounds when selection changes
				getSelectionModel().getSelectedIndices().addListener((InvalidationListener) observable ->
						updateCellBackgrounds());

				// Update cell backgrounds when focus changes
// WORKAROUND : see 'focused' instance variable
//				focusedProperty().addListener(observable -> updateCellBackgrounds());
				focusedProperty().addListener((observable, oldFocused, newFocused) ->
				{
					focused = newFocused;
					updateCellBackgrounds();
				});

				// Update cell backgrounds when focused row changes
				getFocusModel().focusedIndexProperty().addListener(observable -> updateCellBackgrounds());
			}

			// If multiple selection, update list of selected locations when selection changes
			if (selectionMode == SelectionMode.MULTIPLE)
			{
				getSelectionModel().getSelectedItems().addListener((InvalidationListener) observable ->
						selectedLocations.setAll(getFilteredSelectedLocations()));
			}

			// Ensure cells are redrawn if scroll bar is hidden
			widthProperty().addListener(observable -> Platform.runLater(this::refresh));

			// Handle key press
			addEventHandler(KeyEvent.KEY_PRESSED, event ->
			{
				switch (event.getCode())
				{
					case ENTER:
					{
						List<DirectoryEntry> entries = getSelectionModel().getSelectedItems();
						if (!CollectionUtils.isNullOrEmpty(entries))
						{
							DirectoryEntry entry = entries.get(0);
							if (entry.isDirectory)
								openDirectory(entry);
						}
						break;
					}

					case UP:
						if (event.isControlDown() && (directory.getParent() != null))
							openParentDirectory();
						break;

					case BACK_SPACE:
						openPreviousDirectory();
						break;

					default:
						// do nothing
						break;
				}
			});
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

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
				Color backgroundColour = getColour(ColourKey.TABLE_VIEW_HEADER_CELL_BACKGROUND);
				Color borderColour = getColour(ColourKey.TABLE_VIEW_HEADER_CELL_BORDER);

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
				if (lookup(StyleSelector.FILLER) instanceof Region filler)
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
								popUpLabel.setPadding(CELL_LABEL_PADDING);
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
											   VHPos.of(VHPos.V.BOTTOM, hPos), x, 0.0, () -> column.longText, null);
					}
				}

				// Prevent reinitialisation of header
				headerInitialised = true;
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void setDirectory(
			Path	directory)
		{
			setDirectory(directory, false);
		}

		//--------------------------------------------------------------

		private void setDirectory(
			Path	directory,
			boolean	forceUpdate)
		{
			setDirectory(directory, forceUpdate, (directory != null));
		}

		//--------------------------------------------------------------

		private void setDirectory(
			Path	directory,
			boolean	forceUpdate,
			boolean	updateHistory)
		{
			if (!updatingDirectory)
			{
				// Prevent re-entry to this method
				updatingDirectory = true;

				// Make location absolute and normalise it
				if (directory != null)
					directory = PathUtils.abs(directory);

				// Update instance variable
				this.directory = directory;

				// Update directory entries
				updateEntries();

				// Update history
				if ((directory != null) && updateHistory)
				{
					// Add directory to history
					history.add(directory);

					// Notify listeners of change to history
					historyChangedNotifier.notifyChange();
				}

				// Notify listeners of change to directory
				directoryChangedNotifier.notifyChange();

				// Allow entry to this method
				updatingDirectory = false;
			}
		}

		//--------------------------------------------------------------

		private void updateEntries()
		{
			// Update directory entries
			entries = Collections.emptyList();
			if (directory != null)
			{
				// Add subdirectories to list of entries
				List<DirectoryEntry> subdirectories = new ArrayList<>();
				try
				{
					for (Path location : DirectoryUtils.listDirectories(directory))
						subdirectories.add(new DirectoryEntry(location, true));
				}
				catch (FileException e)
				{
					if (!(e.getCause() instanceof AccessDeniedException))
						ErrorDialog.show(getWindow(), UPDATE_ENTRIES_STR, e);
				}
				subdirectories.sort(DirectoryEntry.nameComparator(ignoreFilenameCase));
				entries = new ArrayList<>(subdirectories);

				// Add files to list of entries
				List<DirectoryEntry> files = new ArrayList<>();
				try
				{
					for (Path location : DirectoryUtils.listFiles(directory))
						files.add(new DirectoryEntry(location, false));
				}
				catch (FileException e)
				{
					if (!(e.getCause() instanceof AccessDeniedException))
						ErrorDialog.show(getWindow(), UPDATE_ENTRIES_STR, e);
				}
				files.sort(DirectoryEntry.nameComparator(ignoreFilenameCase));
				entries.addAll(files);
			}

			// Apply filter to directory entries
			List<DirectoryEntry> filteredEntries = new ArrayList<>();
			for (DirectoryEntry entry : entries)
			{
				if (entry.isDirectory || filter.matches(entry.location))
					filteredEntries.add(entry);
			}

			// Update list of items with filtered entries
			itemList.update(filteredEntries);

			// Redraw cells
			refresh();

			// Display first item
			scrollTo(0);
		}

		//--------------------------------------------------------------

		private void openDirectory(
			DirectoryEntry	entry)
		{
			// Set directory on table view
			setDirectory(entry.location);

			// Request focus
			requestFocus();
		}

		//--------------------------------------------------------------

		private void openDirectory(
			Path	location)
		{
			// Validate location and set directory on table view
			try
			{
				// Make location absolute and normalise it
				location = PathUtils.abs(location);

				// If location denotes a file, get its parent ...
				Path directory = null;
				if (Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS))
					directory = location.getParent();

				// ... otherwise, validate elements of location
				else if (location != null)
				{
					// Initialise directory
					directory = location.getRoot();
					if (directory == null)
						throw new FileException(ErrorMsg.DIRECTORY_HAS_NO_ROOT, location);

					// Test elements of location
					for (Path element : location)
					{
						directory = directory.resolve(element);
						if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS))
							throw new FileException(ErrorMsg.DIRECTORY_DOES_NOT_EXIST, directory);
					}
				}

				// Set directory on table view
				setDirectory(directory, true);
			}
			catch (FileException e)
			{
				// Show error dialog
				ErrorDialog.show(getWindow(), OPEN_DIRECTORY_STR, e);

				// Restore directory
				setDirectory(directory, true);
			}
		}

		//--------------------------------------------------------------

		private void openParentDirectory()
		{
			// Get parent of current directory
			Path parent = directory.getParent();

			// If current directory has parent, set it on table view
			if (parent != null)
			{
				// Get name of current directory
				String name = directory.getFileName().toString();

				// Set directory on table view
				setDirectory(parent);

				// Request focus
				requestFocus();

				// Select entry for previous directory
				if (name != null)
					selectEntry(name);
			}
		}

		//--------------------------------------------------------------

		private void openPreviousDirectory()
		{
			if (history.hasPrevious())
			{
				// Set previous directory on table view
				setDirectory(history.previous(), false, false);

				// Notify listeners of change to history
				historyChangedNotifier.notifyChange();

				// Request focus
				requestFocus();
			}
		}

		//--------------------------------------------------------------

		private void openNextDirectory()
		{
			if (history.hasNext())
			{
				// Set next directory on table view
				setDirectory(history.next(), false, false);

				// Notify listeners of change to history
				historyChangedNotifier.notifyChange();

				// Request focus
				requestFocus();
			}
		}

		//--------------------------------------------------------------

		private String getOpenParentDirectoryCommand()
		{
			return isRoot(directory) ? null : getQuotedName(directory.getParent(), UP_TO_STR);
		}

		//--------------------------------------------------------------

		private void updateCellBackgrounds()
		{
			for (Cell cell : cells)
				cell.updateBackground();
		}

		//--------------------------------------------------------------

		private LocationChooserPane getChooserPane()
		{
			return (LocationChooserPane)SceneUtils.searchAscending(this, node -> node instanceof LocationChooserPane);
		}

		//--------------------------------------------------------------

		private Window getWindow()
		{
			return SceneUtils.getWindow(this);
		}

		//--------------------------------------------------------------

		private List<Path> getFilteredLocations()
		{
			return getItems().stream()
					.map(entry -> entry.location)
					.filter(location -> filter.matches(location))
					.toList();
		}

		//--------------------------------------------------------------

		private List<Path> getFilteredSelectedLocations()
		{
			return getSelectionModel().getSelectedItems().stream()
					 .map(entry -> entry.location)
					 .filter(location -> filter.matches(location))
					 .toList();
		}

		//--------------------------------------------------------------

		private boolean selectEntry(
			String	name)
		{
			// Search for entry in items of table view
			for (DirectoryEntry entry : getItems())
			{
				if (name.equals(entry.getName()))
				{
					// Select entry
					getSelectionModel().select(entry);

					// Make entry visible in viewport of table view
					scrollTo(entry);

					// Indicate entry selected
					return true;
				}
			}

			// Indicate entry not selected
			return false;
		}

		//--------------------------------------------------------------

		private void copyEntryText()
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
						text = text.substring(0, index) + "-".repeat(table.maxLineLength()) + "\n"
								+ text.substring(index);
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
					for (DirectoryEntry entry : getItems())
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
				ErrorDialog.show(getWindow(), COPY_ALL_ENTRIES_STR, e);
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Enumerated types
	////////////////////////////////////////////////////////////////////


		// ENUMERATION: COLUMN OF TABLE VIEW


		private enum Column
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			ATTRIBUTES
			(
				"Attr",
				"Attributes",
				HPos.LEFT,
				"D"
			)
			{
				@Override
				protected String getValueString(
					DirectoryEntry	entry)
				{
					return entry.isDirectory ? "D" : "";
				}

				//------------------------------------------------------

				@Override
				protected Comparator<DirectoryEntry> getComparator(
					boolean	ignoreFilenameCase)
				{
					return Comparator.comparing(entry -> entry.isDirectory);
				}

				//------------------------------------------------------
			},

			NAME
			(
				"Name",
				null,
				HPos.LEFT,
				"M".repeat(32)
			)
			{
				@Override
				protected String getValueString(
					DirectoryEntry	entry)
				{
					return entry.getName();
				}

				//------------------------------------------------------

				@Override
				protected Comparator<DirectoryEntry> getComparator(
					boolean	ignoreFilenameCase)
				{
					return DirectoryEntry.nameComparator(ignoreFilenameCase);
				}

				//------------------------------------------------------
			},

			SIZE
			(
				"Size",
				"Size of file",
				HPos.RIGHT,
				SIZE_FORMATTER.format(0xFF_FFFF_FFFFL)
			)
			{
				@Override
				protected String getValueString(
					DirectoryEntry	entry)
				{
					return entry.isDirectory
										? ""
										: (entry.size < 0)
												? UNKNOWN_STR
												: SIZE_FORMATTER.format(entry.size);
				}

				//------------------------------------------------------

				@Override
				protected Comparator<DirectoryEntry> getComparator(
					boolean	ignoreFilenameCase)
				{
					return Comparator.comparingLong(entry -> entry.size);
				}

				//------------------------------------------------------
			},

			MODIFICATION_TIME
			(
				"Modified",
				"Date/time of last modification",
				HPos.LEFT,
				MODIFICATION_TIME_FORMATTER.format(LocalDateTime.now())
			)
			{
				@Override
				protected String getValueString(
					DirectoryEntry	entry)
				{
					return (entry.modificationTime == null)
										? ""
										: MODIFICATION_TIME_FORMATTER.format(entry.getLocalModificationTime());
				}

				//------------------------------------------------------

				@Override
				protected Comparator<DirectoryEntry> getComparator(
					boolean	ignoreFilenameCase)
				{
					return Comparator.comparing(entry -> entry.modificationTime);
				}

				//------------------------------------------------------
			};

			private static final	EnumSet<Column>	VISIBLE_COLUMNS	= EnumSet.of(NAME, SIZE, MODIFICATION_TIME);

		////////////////////////////////////////////////////////////////
		//  Instance variables
		////////////////////////////////////////////////////////////////

			private	String	text;
			private	String	longText;
			private	HPos	hAlignment;
			private	double	prefWidth;

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private Column(
				String	text,
				String	longText,
				HPos	hAlignment,
				String	prototypeText)
			{
				// Initialise instance variables
				this.text = text;
				this.longText = (longText == null) ? text : longText;
				this.hAlignment = hAlignment;
				prefWidth = TextUtils.textWidthCeil(prototypeText) + CELL_LABEL_PADDING.getLeft()
								+ CELL_LABEL_PADDING.getRight() + 1.0;
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Class methods
		////////////////////////////////////////////////////////////////

			/**
			 * Returns the column that is associated with the specified key.
			 *
			 * @param  key
			 *          the key whose associated column is required.
			 * @return the column that is associated with {@code key}, or {@code null} if there is no such column.
			 */

			private static Column forKey(
				String	key)
			{
				return Arrays.stream(values()).filter(value -> value.getKey().equals(key)).findFirst().orElse(null);
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Abstract methods
		////////////////////////////////////////////////////////////////

			protected abstract String getValueString(
				DirectoryEntry	entry);

			//----------------------------------------------------------

			protected abstract Comparator<DirectoryEntry> getComparator(
				boolean	ignoreFilenameCase);

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

		////////////////////////////////////////////////////////////////
		//  Instance methods
		////////////////////////////////////////////////////////////////

			private TableColumn<DirectoryEntry, DirectoryEntry> createColumn(
				DirectoryTableView	tableView,
				boolean				ignoreFilenameCase)
			{
				TableColumn<DirectoryEntry, DirectoryEntry> column = new TableColumn<>(toString());
				column.setCellFactory(column0 -> tableView.new Cell(this));
				column.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue()));
				column.setComparator(Comparator.<DirectoryEntry, Boolean>comparing(entry -> !entry.isDirectory)
											.thenComparing(getComparator(ignoreFilenameCase)));

				return column;
			}

			//----------------------------------------------------------

			private String getKey()
			{
				return StringUtils.toCamelCase(name());
			}

			//----------------------------------------------------------

		}

		//==============================================================

	////////////////////////////////////////////////////////////////////
	//  Member classes : inner classes
	////////////////////////////////////////////////////////////////////


		// CLASS: CELL


		private class Cell
			extends TableCell<DirectoryEntry, DirectoryEntry>
			implements CellPopUpManager.ICell<DirectoryEntry>
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			private static final	double	GRAPHIC_TEXT_GAP	= 6.0;

			private static final	String	SELECT_STR	= "Select";
			private static final	String	OPEN_STR	= "Open ";

		////////////////////////////////////////////////////////////////
		//  Instance variables
		////////////////////////////////////////////////////////////////

			private	Column	column;

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

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

				// Open directory when mouse is double-clicked on directory entry
				addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
				{
					if ((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2))
					{
						DirectoryEntry entry = getItem();
						if (entry != null)
						{
							if (entry.isDirectory)
								openDirectory(entry);
							else if (filter.matches(entry.location))
							{
								selectedLocations.setAll(List.of(entry.location));
								getChooserPane().notifyLocationsChosen();
							}
						}
					}
				});

				// Display context menu in response to context-menu request
				addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event ->
				{
					// Initialise set of menu sections
					enum Section
					{
						OPEN_DIRECTORY,
						COPY
					}
					EnumSet<Section> sections = EnumSet.noneOf(Section.class);

					// Get entry
					DirectoryEntry entry = getItem();

					// Create context menu
					ContextMenu menu = new ContextMenu();

					// Add menu item: select
					if (entry != null)
					{
						// Get locations of selected entries that match filter
						List<Path> locations = getFilteredSelectedLocations();

						// Add menu item
						if (!locations.isEmpty())
						{
							MenuItem menuItem = new MenuItem(SELECT_STR, icon(ImageId.SELECT));
							menuItem.setOnAction(event0 ->
							{
								selectedLocations.setAll(locations);
								getChooserPane().notifyLocationsChosen();
							});
							menu.getItems().add(menuItem);
						}
					}

					// Add menu item: open directory
					if ((entry != null) && entry.isDirectory)
					{
						// Add separator
						if (!menu.getItems().isEmpty())
							menu.getItems().add(new SeparatorMenuItem());

						// Add menu item
						MenuItem menuItem = new MenuItem(OPEN_STR + quote(entry.getName()), icon(ImageId.ARROW_DOWN));
						menuItem.setOnAction(event0 -> openDirectory(entry));
						menu.getItems().add(menuItem);

						// Add section to set
						sections.add(Section.OPEN_DIRECTORY);
					}

					// Add menu item: open parent directory
					String text = getOpenParentDirectoryCommand();
					if (text != null)
					{
						// Add separator
						if (!sections.contains(Section.OPEN_DIRECTORY) && !menu.getItems().isEmpty())
							menu.getItems().add(new SeparatorMenuItem());

						// Add menu item
						MenuItem menuItem = new MenuItem(text, icon(ImageId.ARROW_UP));
						menuItem.setOnAction(event0 -> openParentDirectory());
						menu.getItems().add(menuItem);

						// Add section to set
						sections.add(Section.OPEN_DIRECTORY);
					}

					// Add menu item: copy pathname
					if (entry != null)
					{
						// Add separator
						if (!menu.getItems().isEmpty())
							menu.getItems().add(new SeparatorMenuItem());

						// Add menu item
						MenuItem menuItem = new MenuItem(COPY_PATHNAME_STR, icon(ImageId.COPY));
						menuItem.setOnAction(event0 ->
								copyToClipboard(getWindow(), COPY_PATHNAME_STR, entry.location.toString()));
						menu.getItems().add(menuItem);

						// Add section to set
						sections.add(Section.COPY);
					}

					// Add menu item: copy all entries
					if (!entries.isEmpty())
					{
						// Add separator
						if (!sections.contains(Section.COPY) && !menu.getItems().isEmpty())
							menu.getItems().add(new SeparatorMenuItem());

						// Add menu item
						MenuItem menuItem = new MenuItem(COPY_ALL_ENTRIES_STR + ELLIPSIS_STR, icon(ImageId.COPY_LINES));
						menuItem.setOnAction(event0 -> copyEntryText());
						menu.getItems().add(menuItem);

						// Add section to set
						sections.add(Section.COPY);
					}

					// Display context menu
					if (!menu.getItems().isEmpty())
						menu.show(getWindow(), event.getScreenX(), event.getScreenY());
				});
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : CellPopUpManager.ICell interface
		////////////////////////////////////////////////////////////////

			/**
			 * {@inheritDoc}
			 */

			@Override
			public String getIdentifier()
			{
				return (getItem() == null) ? null : getIndex() + ":" + column.getKey();
			}

			//----------------------------------------------------------

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
					label.setBorder(SceneUtils.createSolidBorder(
							getColour(TableViewStyle.ColourKey.CELL_POPUP_BORDER)));
					label.getStyleClass().add(TableViewStyle.StyleClass.CELL_POPUP_LABEL);
				}

				// Return label
				return label;
			}

			//----------------------------------------------------------

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

			//----------------------------------------------------------

			/**
			 * {@inheritDoc}
			 */

			@Override
			public Window getWindow()
			{
				return DirectoryTableView.this.getWindow();
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			@Override
			protected void updateItem(
				DirectoryEntry	entry,
				boolean			empty)
			{
				// Call superclass method
				super.updateItem(entry, empty);

				// Update background
				updateBackground();

				// Set border
				setBorder(empty ? null
								: SceneUtils.createSolidBorder(
											getColour(TableViewStyle.ColourKey.CELL_BORDER), Side.RIGHT, Side.BOTTOM));

				// Set graphic
				setGraphic(empty ? null : createLabel());
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods
		////////////////////////////////////////////////////////////////

			private void updateBackground()
			{
				if (StyleManager.INSTANCE.notUsingStyleSheet())
				{
					int index = getIndex();
					boolean selected = getSelectionModel().getSelectedIndices().contains(index);
// WORKAROUND
//					boolean focused = getTableView().isFocused();
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
								getColour(TableViewStyle.ColourKey.CELL_BACKGROUND_FOCUSED),
								new Insets(0.0, 1.0, 1.0, 0.0), colour, new Insets(1.0, 1.0, 2.0, 0.0)));
					}
					else
						setBackground(SceneUtils.createColouredBackground(colour));
				}
			}

			//----------------------------------------------------------

			private String getLabelText()
			{
				String text = null;
				switch (column)
				{
					case ATTRIBUTES:
						break;

					case NAME:
						text = getItem().getName();
						break;

					case SIZE:
					{
						Long size = getItem().size;
						if ((size != null) && (size >= 0))
							text = SIZE_FORMATTER.format(size);
						break;
					}

					case MODIFICATION_TIME:
					{
						LocalDateTime dateTime = getItem().getLocalModificationTime();
						if (dateTime != null)
							text = MODIFICATION_TIME_FORMATTER.format(dateTime);
						break;
					}
				}
				return text;
			}

			//----------------------------------------------------------

			private Node getLabelGraphic()
			{
				Node graphic = null;
				switch (column)
				{
					case NAME:
						graphic = icon(getItem().isDirectory ? ImageId.DIRECTORY : ImageId.FILE);
						break;

					case ATTRIBUTES:
					case SIZE:
					case MODIFICATION_TIME:
						break;
				}
				return graphic;
			}

			//----------------------------------------------------------

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
					label.setPadding(CELL_LABEL_PADDING);
					label.setTextFill(getColour(TableViewStyle.ColourKey.CELL_TEXT));
					label.getStyleClass().add(TableViewStyle.StyleClass.CELL_LABEL);
				}

				// Return label
				return label;
			}

			//----------------------------------------------------------

		}

		//==============================================================


		// CLASS: 'COPY ENTRY TEXT' DIALOG


		private class CopyEntryTextDialog
			extends SimpleModalDialog<CopyEntryTextDialog.State>
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

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

		////////////////////////////////////////////////////////////////
		//  Class variables
		////////////////////////////////////////////////////////////////

			private static	State	state	= new State(EnumSet.allOf(Column.class), false, FieldSeparator.SPACES);

		////////////////////////////////////////////////////////////////
		//  Instance variables
		////////////////////////////////////////////////////////////////

			private	State	result;

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

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
						CollectionSpinner.leftRightH(HPos.CENTER, true, FieldSeparator.class, state.fieldSeparator,
													 null, null);
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
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			@Override
			protected State getResult()
			{
				return result;
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Enumerated types
		////////////////////////////////////////////////////////////////


			// ENUMERATION: FIELD SEPARATOR


			private enum FieldSeparator
			{

			////////////////////////////////////////////////////////////
			//  Constants
			////////////////////////////////////////////////////////////

				SPACES
				(
					"Spaces"
				),

				TAB
				(
					"Tab"
				);

			////////////////////////////////////////////////////////////
			//  Instance variables
			////////////////////////////////////////////////////////////

				private	String	text;

			////////////////////////////////////////////////////////////
			//  Constructors
			////////////////////////////////////////////////////////////

				private FieldSeparator(
					String	text)
				{
					// Initialise instance variables
					this.text = text;
				}

				//------------------------------------------------------

			////////////////////////////////////////////////////////////
			//  Instance methods : overriding methods
			////////////////////////////////////////////////////////////

				@Override
				public String toString()
				{
					return text;
				}

				//------------------------------------------------------

			}

			//==========================================================

		////////////////////////////////////////////////////////////////
		//  Member records
		////////////////////////////////////////////////////////////////


			// RECORD: STATE


			private record State(
				EnumSet<Column>	columns,
				boolean			includeHeader,
				FieldSeparator	fieldSeparator)
			{ }

			//==========================================================

		}

		//==============================================================

	}

	//==================================================================


	// CLASS: DIRECTORY BAR


	private static class DirectoryBar
		extends HBox
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	Object	ELEMENT_NODE_KEY	= new Object();

		private static final	String	SEPARATOR	= "/";

		private static final	double	ARROWHEAD_HEIGHT_FACTOR	= 1.125;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	SimpleObjectProperty<Path>	directory;
		private	int							elementIndex;
		private	GraphicButton				previousElementButton;
		private	GraphicButton				nextElementButton;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DirectoryBar()
		{
			// Initialise instance variables
			directory = new SimpleObjectProperty<>();
			directory.addListener(observable -> update());

			// Set properties
			setAlignment(Pos.CENTER_LEFT);
			setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.DIRECTORY_BAR_BACKGROUND)));
			setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.DIRECTORY_BAR_BORDER)));
			getStyleClass().add(StyleClass.DIRECTORY_BAR);

			// Calculate size of icon of navigation buttons
			double textHeight = TextUtils.textHeight();
			double arrowheadHeight = Math.rint(ARROWHEAD_HEIGHT_FACTOR * textHeight);

			// Create factory for navigation button
			IFunction1<GraphicButton, Shape> navigationButtonFactory = arrowhead ->
			{
				// Set properties of icon
				arrowhead.setFill(getColour(ColourKey.DIRECTORY_BAR_ARROWHEAD));
				arrowhead.getStyleClass().add(StyleClass.ARROWHEAD);

				// Create button
				GraphicButton button = new GraphicButton(Shapes.tile(arrowhead, textHeight));
				button.setBackgroundColour(getColour(ColourKey.DIRECTORY_BAR_BUTTON_BACKGROUND_HOVERED),
										   GraphicButton.State.HOVERED);
				button.setBorderColour(getColour(ColourKey.DIRECTORY_BAR_BUTTON_BORDER_HOVERED),
									   GraphicButton.State.HOVERED);
				return button;
			};

			// Button: previous element
			Shape arrowhead = Shapes.arrowhead01(VHDirection.LEFT, arrowheadHeight);
			previousElementButton = navigationButtonFactory.invoke(arrowhead);
			previousElementButton.setDisable(true);
			previousElementButton.setOnAction(event ->
			{
				if (elementIndex > 0)
				{
					--elementIndex;
					update();
				}
			});
			getChildren().add(previousElementButton);

			// Filler
			getChildren().add(FillerUtils.hBoxFiller(0.0));

			// Button: next element
			arrowhead = Shapes.arrowhead01(VHDirection.RIGHT, arrowheadHeight);
			nextElementButton = navigationButtonFactory.invoke(arrowhead);
			nextElementButton.setDisable(true);
			nextElementButton.setOnAction(event ->
			{
				Path dir = directory.get();
				int numElements = (dir == null) ? 0 : dir.getNameCount() + 1;
				if (elementIndex < numElements - 1)
				{
					++elementIndex;
					update();
				}
			});
			getChildren().add(nextElementButton);

			// Set minimum width of this container
			setMinWidth(2.0 + getButtonMaxX(previousElementButton) - getButtonMaxX(nextElementButton));

			// Update this container when its layout bounds change
			layoutBoundsProperty().addListener(observable -> update());
		}

		//--------------------------------------------------------------

		private static double getButtonMaxX(
			GraphicButton	button)
		{
			return button.getGraphic().getLayoutBounds().getMaxX() + 2.0 * GraphicButton.BORDER_WIDTH
						+ GraphicButton.DEFAULT_PADDING.getLeft() + GraphicButton.DEFAULT_PADDING.getRight();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void update()
		{
			// Initialise list of element-node information
			List<NodeInfo> nodeInfos = new ArrayList<>();

			// Create element nodes and populate list of element-node information
			Path directory = this.directory.get();
			if (directory != null)
			{
				// Create factory for button for element of pathname
				IFunction2<GraphicButton, Path, String> buttonFactory = (location, text) ->
				{
					// Create graphic for button
					Group textGroup = Text2.createTile(text, getColour(ColourKey.DIRECTORY_BAR_TEXT));
					textGroup.getChildren().get(1).getStyleClass().add(StyleClass.DIRECTORY_BAR_TEXT);

					// Create button
					GraphicButton button = new GraphicButton(textGroup);
					button.getProperties().put(ELEMENT_NODE_KEY, "");
					button.setBackgroundColour(getColour(ColourKey.DIRECTORY_BAR_BUTTON_BACKGROUND_HOVERED),
											   GraphicButton.State.HOVERED);
					button.setBorderColour(getColour(ColourKey.DIRECTORY_BAR_BUTTON_BORDER_HOVERED),
										   GraphicButton.State.HOVERED);
					button.setOnAction(event -> this.directory.set(location));

					// Display context menu on request
					button.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event ->
					{
						// Get window
						Window window = SceneUtils.getWindow(this);

						// Create context menu
						ContextMenu menu = new ContextMenu();

						// Menu item: copy pathname
						MenuItem menuItem = new MenuItem(COPY_PATHNAME_STR, icon(ImageId.COPY));
						menuItem.setOnAction(event0 -> copyToClipboard(window, COPY_PATHNAME_STR, location.toString()));
						menu.getItems().add(menuItem);

						// Display context menu at location of event
						menu.show(window, event.getScreenX(), event.getScreenY());
					});

					// Return button
					return button;
				};

				// Make location absolute and normalise it
				directory = PathUtils.abs(directory);

				// Create button for root and add it to list
				Path cumulativeDirectory = directory.getRoot();
				GraphicButton button = buttonFactory.invoke(cumulativeDirectory, getRootName(cumulativeDirectory));
				nodeInfos.add(new NodeInfo(button));

				// Create buttons for remaining elements of pathname
				for (Path element : directory)
				{
					// Update directory
					cumulativeDirectory = cumulativeDirectory.resolve(element);

					// Create separator and add it to list
					Group separator = Text2.createTile(SEPARATOR, getColour(ColourKey.DIRECTORY_BAR_TEXT));
					separator.getChildren().get(1).getStyleClass().add(StyleClass.DIRECTORY_BAR_TEXT);
					separator.getProperties().put(ELEMENT_NODE_KEY, "");
					nodeInfos.add(new NodeInfo(separator));

					// Create button and add it to list
					button = buttonFactory.invoke(cumulativeDirectory, element.toString());
					nodeInfos.add(new NodeInfo(button));
				}
			}

			// Adjust element index
			int numElementNodes = nodeInfos.size();
			int numElements = numElementNodes / 2 + 1;
			elementIndex = Math.min(Math.max(0, elementIndex), numElements - 1);

			// Update navigation buttons
			previousElementButton.setDisable(elementIndex <= 0);
			nextElementButton.setDisable(elementIndex >= numElements - 1);

			// Remove all old element nodes
			Iterator<Node> it = getChildren().iterator();
			while (it.hasNext())
			{
				if (it.next().getProperties().containsKey(ELEMENT_NODE_KEY))
					it.remove();
			}

			// Insert new element nodes
			if (numElementNodes > 0)
			{
				// Get available width of this container
				double width = getWidth() - getButtonMaxX(previousElementButton) - getButtonMaxX(nextElementButton);

				// Insert element nodes
				int index = 1;
				double maxX = 0.0;
				int i0 = 2 * elementIndex - 1;
				for (int i = i0; i < numElementNodes; i += 2)
				{
					// Increment maximum x coordinate
					if (i > i0)
						maxX += nodeInfos.get(i).maxX;
					maxX += nodeInfos.get(i + 1).maxX;

					// If maximum x coordinate exceeds width, stop
					if (maxX > width)
						break;

					// Insert separator and element node
					if (i > i0)
						getChildren().add(index++, nodeInfos.get(i).node);
					getChildren().add(index++, nodeInfos.get(i + 1).node);
				}
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Member classes : non-inner classes
	////////////////////////////////////////////////////////////////////


		// CLASS: ELEMENT-NODE INFORMATION


		private static class NodeInfo
		{

		////////////////////////////////////////////////////////////////
		//  Instance variables
		////////////////////////////////////////////////////////////////

			private	Node	node;
			private	double	maxX;

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private NodeInfo(
				Node	node)
			{
				// Initialise instance variables
				this.node = node;
				maxX = (node instanceof GraphicButton button) ? getButtonMaxX(button)
															  : node.getLayoutBounds().getMaxX();
			}

			//----------------------------------------------------------

		}

		//==============================================================

	}

	//==================================================================


	// CLASS: NAME DIALOG


	/**
	 * This class implements a modal dialog in which a name of a file or directory may be edited.
	 */

	private static class NameDialog
		extends SimpleModalDialog<String>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The gap between adjacent controls in the control pane. */
		private static final	double 	CONTROL_PANE_H_GAP	= 6.0;

		/** The number of columns of the text field. */
		private static final	int		TEXT_FIELD_NUM_COLUMNS	= 24;

		/** Invalid filename characters on UNIX-like systems. */
		private static final	String	INVALID_FILENAME_CHARS_UNIX		= "/";

		/** Invalid filename characters on Windows. */
		private static final	String	INVALID_FILENAME_CHARS_WINDOWS	= "<>:\"/\\|?*";

		/** Miscellaneous strings. */
		private static final	String	NAME_STR	= "Name";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The result of this dialog. */
		private	String	result;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a modal dialog in which a name of a file or directory may be edited.
		 *
		 * @param owner
		 *          the window that will own this dialog, or {@code null} for a top-level dialog that has no owner.
		 * @param title
		 *          the title of the dialog.
		 * @param validator
		 *          the validator of the content of the text field.
		 */

		private NameDialog(
			Window				owner,
			String				title,
			Predicate<String>	validator)
		{
			// Call superclass constructor
			super(owner, NameDialog.class.getCanonicalName(), title);

			// Allow dialog to be resized
			setResizable(true);

			// Get invalid filename characters
			String invalidFilenameChars = OsUtils.isWindows() ? INVALID_FILENAME_CHARS_WINDOWS
															  : INVALID_FILENAME_CHARS_UNIX;

			// Create text field
			TextField textField = new TextField("");
			textField.setPrefColumnCount(TEXT_FIELD_NUM_COLUMNS);
			textField.setTextFormatter(new TextFormatter<>(FilterFactory.createFilter((ch, index, text) ->
					(invalidFilenameChars.indexOf(ch) < 0) ? Character.toString(ch) : "")));
			HBox.setHgrow(textField, Priority.ALWAYS);

			// Create control pane
			HBox controlPane = new HBox(CONTROL_PANE_H_GAP, Labels.hNoShrink(NAME_STR), textField);
			controlPane.setAlignment(Pos.CENTER);

			// Add control pane to content pane
			addContent(controlPane);

			// Create button: OK
			Button okButton = Buttons.hNoShrink(OK_STR);
			okButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
			okButton.setOnAction(event ->
			{
				result = textField.getText();
				hide();
			});
			addButton(okButton, HPos.RIGHT);

			// Create procedure to update 'OK' button
			IProcedure0 updateOkButton = () -> okButton.setDisable(!validator.test(textField.getText()));

			// Disable 'OK' button if content of text field is invalid
			textField.textProperty().addListener(observable -> updateOkButton.invoke());

			// Fire 'OK' button when 'Enter' key is pressed in text field
			textField.setOnAction(event -> okButton.fire());

			// Update 'OK' button
			updateOkButton.invoke();

			// Create button: cancel
			Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
			cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
			cancelButton.setOnAction(event -> requestClose());
			addButton(cancelButton, HPos.RIGHT);

			// Fire 'cancel' button if Escape key is pressed; fire 'OK' button if Ctrl+Enter is pressed
			setKeyFireButton(cancelButton, okButton);

			// When dialog is shown, prevent its height from changing; request focus on text field
			addEventHandler(WindowEvent.WINDOW_SHOWN, event ->
			{
				// Prevent height of dialog from changing
				WindowUtils.preventHeightChange(this);

				// Request focus on text field
				textField.requestFocus();
				textField.selectAll();
			});
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * {@inheritDoc}
		 */

		@Override
		protected String getResult()
		{
			return result;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Image data
////////////////////////////////////////////////////////////////////////

	/**
	 * PNG image data.
	 */

	private interface ImgData
	{
		// File: directory
		byte[]	DIRECTORY	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x1F, (byte)0xF3, (byte)0xFF,
			(byte)0x61, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFA, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0x60, (byte)0x18, (byte)0xFA, (byte)0xE0,
			(byte)0x71, (byte)0x5F, (byte)0x28, (byte)0xE7, (byte)0xAD, (byte)0x29, (byte)0x7E, (byte)0x8F,
			(byte)0x6F, (byte)0x4C, (byte)0xF0, (byte)0xF8, (byte)0x0F, (byte)0xC2, (byte)0x20, (byte)0x36,
			(byte)0x48, (byte)0x0C, (byte)0x5D, (byte)0x1D, (byte)0x4E, (byte)0x70, (byte)0x63, (byte)0xA2,
			(byte)0x67, (byte)0xD3, (byte)0x93, (byte)0x75, (byte)0xC5, (byte)0x9F, (byte)0xFE, (byte)0xFF,
			(byte)0xB8, (byte)0xF1, (byte)0x1F, (byte)0x84, (byte)0x9F, (byte)0xAC, (byte)0x2F, (byte)0xFE,
			(byte)0x7C, (byte)0x6B, (byte)0xB2, (byte)0x77, (byte)0x13, (byte)0xBA, (byte)0x3A, (byte)0x9C,
			(byte)0xE0, (byte)0xE6, (byte)0x44, (byte)0xAF, (byte)0x9F, (byte)0xDF, (byte)0x1F, (byte)0xAE,
			(byte)0xBF, (byte)0xF5, (byte)0xF7, (byte)0xED, (byte)0x81, (byte)0xAB, (byte)0x20, (byte)0x0C,
			(byte)0x62, (byte)0xDF, (byte)0x98, (byte)0xEC, (byte)0xFD, (byte)0x0B, (byte)0xE6, (byte)0x22,
			(byte)0x14, (byte)0x3C, (byte)0xC9, (byte)0xF3, (byte)0x33, (byte)0xBA, (byte)0x7E, (byte)0x06,
			(byte)0x90, (byte)0xC4, (byte)0x9F, (byte)0x17, (byte)0x1B, (byte)0x88, (byte)0xC2, (byte)0x20,
			(byte)0xB5, (byte)0xE8, (byte)0xFA, (byte)0xA9, (byte)0x6B, (byte)0xC0, (byte)0x9B, (byte)0x83,
			(byte)0x4D, (byte)0x98, (byte)0xCE, (byte)0xC6, (byte)0x87, (byte)0x27, (byte)0x7A, (byte)0x4E,
			(byte)0x80, (byte)0x1B, (byte)0xF0, (byte)0xF5, (byte)0xDA, (byte)0xEC, (byte)0xFF, (byte)0xB7,
			(byte)0x67, (byte)0x04, (byte)0xFC, (byte)0xFF, (byte)0x72, (byte)0x6D, (byte)0xEA, (byte)0xFF,
			(byte)0x1F, (byte)0x0F, (byte)0x16, (byte)0xE0, (byte)0xC5, (byte)0x1F, (byte)0xCF, (byte)0xF7,
			(byte)0xFD, (byte)0xBF, (byte)0x31, (byte)0xD9, (byte)0xE7, (byte)0xF3, (byte)0xF5, (byte)0x7E,
			(byte)0x0F, (byte)0x05, (byte)0xB0, (byte)0x01, (byte)0xBF, (byte)0x9E, (byte)0xAC, (byte)0xFE,
			(byte)0x7F, (byte)0x77, (byte)0x6E, (byte)0xD8, (byte)0xFF, (byte)0xB7, (byte)0x47, (byte)0x9B,
			(byte)0x30, (byte)0x14, (byte)0xA3, (byte)0xE3, (byte)0x6F, (byte)0x77, (byte)0x80, (byte)0x16,
			(byte)0xCD, (byte)0x0A, (byte)0xFA, (byte)0x72, (byte)0x6B, (byte)0x92, (byte)0x57, (byte)0x24,
			(byte)0xDC, (byte)0x0B, (byte)0x4F, (byte)0xD7, (byte)0xE7, (byte)0xFC, (byte)0x7F, (byte)0xB6,
			(byte)0x31, (byte)0x17, (byte)0x43, (byte)0x31, (byte)0x36, (byte)0xFC, (byte)0x78, (byte)0x75,
			(byte)0xEA, (byte)0xD7, (byte)0x5B, (byte)0x53, (byte)0x7C, (byte)0xE6, (byte)0x22, (byte)0xC2,
			(byte)0x60, (byte)0xA2, (byte)0xC7, (byte)0xFF, (byte)0x7B, (byte)0xF3, (byte)0x22, (byte)0xFE,
			(byte)0x7F, (byte)0xBF, (byte)0x3B, (byte)0x17, (byte)0x43, (byte)0x31, (byte)0x3A, (byte)0x7E,
			(byte)0x73, (byte)0xA0, (byte)0x0E, (byte)0x98, (byte)0xD0, (byte)0x7C, (byte)0xEF, (byte)0xA1,
			(byte)0x24, (byte)0x34, (byte)0x60, (byte)0xDC, (byte)0xFE, (byte)0xC3, (byte)0x08, (byte)0x1C,
			(byte)0x5C, (byte)0x78, (byte)0xA2, (byte)0xD7, (byte)0xFF, (byte)0x5B, (byte)0xFD, (byte)0xDE,
			(byte)0x9A, (byte)0x70, (byte)0xCD, (byte)0x83, (byte)0x02, (byte)0x00, (byte)0x00, (byte)0xC9,
			(byte)0xF5, (byte)0xA1, (byte)0x50, (byte)0x6F, (byte)0x4D, (byte)0x66, (byte)0x72, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE,
			(byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/file02
		byte[]	FILE	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x96, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x7D, (byte)0xD0, (byte)0xD1, (byte)0x06, (byte)0x02,
			(byte)0x41, (byte)0x14, (byte)0x80, (byte)0xE1, (byte)0x7F, (byte)0x44, (byte)0x22, (byte)0x11,
			(byte)0x59, (byte)0x56, (byte)0x24, (byte)0x22, (byte)0xA5, (byte)0x96, (byte)0x2C, (byte)0x91,
			(byte)0x48, (byte)0x24, (byte)0x22, (byte)0xD1, (byte)0x4D, (byte)0xAF, (byte)0x57, (byte)0xAC,
			(byte)0xBD, (byte)0x9D, (byte)0x77, (byte)0x4C, (byte)0x4B, (byte)0x33, (byte)0x7B, (byte)0x4E,
			(byte)0x7B, (byte)0x2A, (byte)0xE7, (byte)0x62, (byte)0xC6, (byte)0xF1, (byte)0xF9, (byte)0x8D,
			(byte)0xE1, (byte)0xC9, (byte)0xFF, (byte)0x89, (byte)0x47, (byte)0x81, (byte)0x0F, (byte)0x53,
			(byte)0xB2, (byte)0xFC, (byte)0x06, (byte)0x7C, (byte)0xBD, (byte)0xF0, (byte)0x3C, (byte)0x34,
			(byte)0x91, (byte)0x75, (byte)0x1B, (byte)0x17, (byte)0x6E, (byte)0x73, (byte)0xEE, (byte)0x64,
			(byte)0x16, (byte)0xF4, (byte)0xE8, (byte)0xD0, (byte)0x7A, (byte)0xB7, (byte)0x34, (byte)0x11,
			(byte)0x90, (byte)0xD0, (byte)0xA7, (byte)0x5B, (byte)0x75, (byte)0xE2, (byte)0x6B, (byte)0xCA,
			(byte)0x26, (byte)0x18, (byte)0x91, (byte)0x32, (byte)0x88, (byte)0x1D, (byte)0x79, (byte)0x95,
			(byte)0x80, (byte)0x29, (byte)0x63, (byte)0x86, (byte)0x75, (byte)0xC7, (byte)0x59, (byte)0x90,
			(byte)0x31, (byte)0x63, (byte)0xA2, (byte)0x3A, (byte)0x06, (byte)0xAC, (byte)0x59, (byte)0xB1,
			(byte)0x50, (byte)0x1D, (byte)0x03, (byte)0x76, (byte)0x6C, (byte)0xC8, (byte)0x55, (byte)0xC7,
			(byte)0x80, (byte)0x23, (byte)0x7B, (byte)0xB6, (byte)0xAA, (byte)0x63, (byte)0xC0, (byte)0x85,
			(byte)0x13, (byte)0x07, (byte)0xD5, (byte)0x31, (byte)0xE0, (byte)0xC6, (byte)0x95, (byte)0xB3,
			(byte)0xEA, (byte)0x34, (byte)0x40, (byte)0xFC, (byte)0x1E, (byte)0x99, (byte)0xE2, (byte)0x03,
			(byte)0xFC, (byte)0x9E, (byte)0x17, (byte)0xB3, (byte)0x8F, (byte)0xAC, (byte)0xC7, (byte)0x25,
			(byte)0xB5, (byte)0x00, (byte)0xD7, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49,
			(byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/arrowUp01
		byte[]	ARROW_UP	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0F,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x91, (byte)0xDF, (byte)0x5D,
			(byte)0xC1, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x6E, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0xCD, (byte)0xC9, (byte)0x31, (byte)0x0E, (byte)0x40,
			(byte)0x40, (byte)0x10, (byte)0x85, (byte)0xE1, (byte)0x7F, (byte)0xA3, (byte)0x97, (byte)0x88,
			(byte)0x0B, (byte)0x88, (byte)0x13, (byte)0xA8, (byte)0xDC, (byte)0x49, (byte)0xB7, (byte)0xDD,
			(byte)0x66, (byte)0x0F, (byte)0xA5, (byte)0x72, (byte)0x0C, (byte)0x85, (byte)0x5E, (byte)0xE1,
			(byte)0x24, (byte)0x82, (byte)0x47, (byte)0x34, (byte)0xBB, (byte)0xAC, (byte)0x5A, (byte)0x5E,
			(byte)0x66, (byte)0x5E, (byte)0x66, (byte)0x3E, (byte)0x36, (byte)0xBE, (byte)0x12, (byte)0x9F,
			(byte)0xE5, (byte)0x07, (byte)0xE3, (byte)0xD9, (byte)0xF1, (byte)0x09, (byte)0xA6, (byte)0x65,
			(byte)0x15, (byte)0xAF, (byte)0xB4, (byte)0x2F, (byte)0x4C, (byte)0xC1, (byte)0x22, (byte)0x3C,
			(byte)0xB3, (byte)0x50, (byte)0x44, (byte)0x8C, (byte)0xA1, (byte)0x17, (byte)0x4C, (byte)0x9A,
			(byte)0x51, (byte)0x33, (byte)0x60, (byte)0x42, (byte)0x6E, (byte)0xF4, (byte)0x9C, (byte)0x71,
			(byte)0xDA, (byte)0x4E, (byte)0xBD, (byte)0xD3, (byte)0x84, (byte)0x9C, (byte)0xD1, (byte)0x51,
			(byte)0x63, (byte)0x05, (byte)0x56, (byte)0xDD, (byte)0x91, (byte)0x05, (byte)0x7C, (byte)0x1F,
			(byte)0x17, (byte)0x07, (byte)0x9F, (byte)0xDF, (byte)0x70, (byte)0x4E, (byte)0x45, (byte)0x9E,
			(byte)0xE4, (byte)0x67, (byte)0x0E, (byte)0xCB, (byte)0x9A, (byte)0xC5, (byte)0x85, (byte)0x6E,
			(byte)0xA3, (byte)0x96, (byte)0xC3, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49,
			(byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/arrowDown01
		byte[]	ARROW_DOWN	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x5B, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xF8, (byte)0xC7, (byte)0x80, (byte)0x1F,
			(byte)0x62, (byte)0x08, (byte)0xA0, (byte)0x43, (byte)0x74, (byte)0xEE, (byte)0x7F, (byte)0x86,
			(byte)0xFF, (byte)0x43, (byte)0x59, (byte)0x01, (byte)0xC3, (byte)0x61, (byte)0x86, (byte)0x13,
			(byte)0x0C, (byte)0xDC, (byte)0x20, (byte)0x05, (byte)0x40, (byte)0xF2, (byte)0x04, (byte)0xC3,
			(byte)0x61, (byte)0x4C, (byte)0x05, (byte)0x17, (byte)0x80, (byte)0x52, (byte)0x0B, (byte)0xC0,
			(byte)0x0A, (byte)0x40, (byte)0xE4, (byte)0x05, (byte)0x4C, (byte)0x05, (byte)0xDA, (byte)0x0C,
			(byte)0x5F, (byte)0xC1, (byte)0xD2, (byte)0x20, (byte)0xF8, (byte)0x9D, (byte)0x41, (byte)0x1F,
			(byte)0x43, (byte)0x01, (byte)0x90, (byte)0x99, (byte)0x00, (byte)0x57, (byte)0x90, (byte)0x80,
			(byte)0x24, (byte)0x8A, (byte)0x60, (byte)0x02, (byte)0x39, (byte)0x50, (byte)0x2B, (byte)0x50,
			(byte)0xC4, (byte)0x50, (byte)0x38, (byte)0x20, (byte)0xE7, (byte)0x01, (byte)0x9D, (byte)0x8A,
			(byte)0x53, (byte)0x01, (byte)0x36, (byte)0x88, (byte)0x21, (byte)0x80, (byte)0x0E, (byte)0x01,
			(byte)0xD5, (byte)0x43, (byte)0xF4, (byte)0x41, (byte)0xDC, (byte)0x94, (byte)0x2B, (byte)0x84,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44,
			(byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/arrowLeft01
		byte[]	ARROW_LEFT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x57, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xF8, (byte)0xC7, (byte)0x80, (byte)0x1F,
			(byte)0x62, (byte)0x08, (byte)0xA0, (byte)0x43, (byte)0x0C, (byte)0x01, (byte)0x74, (byte)0x88,
			(byte)0x21, (byte)0x80, (byte)0x0E, (byte)0x91, (byte)0x99, (byte)0xDA, (byte)0x0C, (byte)0x17,
			(byte)0x18, (byte)0x0E, (byte)0xE3, (byte)0x54, (byte)0xC0, (byte)0x90, (byte)0xC0, (byte)0xF0,
			(byte)0x95, (byte)0xE1, (byte)0x3F, (byte)0xC3, (byte)0x09, (byte)0xAC, (byte)0x0A, (byte)0x18,
			(byte)0x38, (byte)0x19, (byte)0x66, (byte)0x02, (byte)0x25, (byte)0xFF, (byte)0x33, (byte)0x2C,
			(byte)0x62, (byte)0xE0, (byte)0xC6, (byte)0xAE, (byte)0xE0, (byte)0x04, (byte)0x58, (byte)0x1A,
			(byte)0x03, (byte)0x92, (byte)0xA0, (byte)0x80, (byte)0x90, (byte)0x15, (byte)0x60, (byte)0x06,
			(byte)0x3E, (byte)0x47, (byte)0x42, (byte)0x99, (byte)0xF8, (byte)0xBD, (byte)0x89, (byte)0x0B,
			(byte)0x62, (byte)0x08, (byte)0xA0, (byte)0x43, (byte)0x0C, (byte)0x01, (byte)0x74, (byte)0x88,
			(byte)0x21, (byte)0x80, (byte)0x0E, (byte)0x01, (byte)0xF8, (byte)0x6F, (byte)0xF4, (byte)0x2D,
			(byte)0xBB, (byte)0x7D, (byte)0x67, (byte)0x6C, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/arrowRight01
		byte[]	ARROW_RIGHT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x54, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xF8, (byte)0xC7, (byte)0x80, (byte)0x1F,
			(byte)0x62, (byte)0x08, (byte)0xA0, (byte)0x43, (byte)0x0C, (byte)0x01, (byte)0x74, (byte)0x88,
			(byte)0x21, (byte)0x80, (byte)0x0E, (byte)0x31, (byte)0x05, (byte)0x0E, (byte)0x33, (byte)0x5C,
			(byte)0x60, (byte)0xD0, (byte)0xC6, (byte)0xA7, (byte)0xE0, (byte)0x04, (byte)0xC3, (byte)0x7F,
			(byte)0x86, (byte)0xAF, (byte)0x0C, (byte)0x09, (byte)0xB8, (byte)0x15, (byte)0x70, (byte)0x32,
			(byte)0xCC, (byte)0x04, (byte)0x2A, (byte)0xF9, (byte)0xCF, (byte)0xB0, (byte)0x88, (byte)0x81,
			(byte)0x1B, (byte)0xAE, (byte)0x00, (byte)0x2C, (byte)0x80, (byte)0x09, (byte)0x8F, (byte)0x13,
			(byte)0xAF, (byte)0x80, (byte)0xA0, (byte)0x15, (byte)0x28, (byte)0x0A, (byte)0x08, (byte)0x3A,
			(byte)0x92, (byte)0x90, (byte)0x37, (byte)0xD1, (byte)0x21, (byte)0x86, (byte)0x00, (byte)0x3A,
			(byte)0xC4, (byte)0x10, (byte)0x40, (byte)0x87, (byte)0x18, (byte)0x02, (byte)0xE8, (byte)0x10,
			(byte)0x00, (byte)0x65, (byte)0xF9, (byte)0xF4, (byte)0x2B, (byte)0x68, (byte)0xC8, (byte)0x2B,
			(byte)0x83, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E,
			(byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/home01
		byte[]	HOME	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x9B, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xF8, (byte)0xC7, (byte)0x80, (byte)0x1F,
			(byte)0xA2, (byte)0x73, (byte)0xF9, (byte)0x18, (byte)0xF8, (byte)0xF0, (byte)0x28, (byte)0x60,
			(byte)0xB0, (byte)0x65, (byte)0xB8, (byte)0x07, (byte)0x84, (byte)0xB6, (byte)0x0C, (byte)0x06,
			(byte)0x0C, (byte)0x07, (byte)0x18, (byte)0x0C, (byte)0xD0, (byte)0x14, (byte)0x30, (byte)0xB0,
			(byte)0x30, (byte)0x34, (byte)0x30, (byte)0x3C, (byte)0x63, (byte)0xF0, (byte)0x63, (byte)0x70,
			(byte)0x63, (byte)0x78, (byte)0xC2, (byte)0xB0, (byte)0x96, (byte)0xE1, (byte)0x3F, (byte)0x83,
			(byte)0x03, (byte)0x8A, (byte)0x02, (byte)0x06, (byte)0x4D, (byte)0x86, (byte)0x33, (byte)0x0C,
			(byte)0xDB, (byte)0x19, (byte)0x24, (byte)0xC1, (byte)0x6C, (byte)0x51, (byte)0x86, (byte)0x3D,
			(byte)0x0C, (byte)0x1F, (byte)0x18, (byte)0x02, (byte)0xE0, (byte)0x0A, (byte)0x18, (byte)0x18,
			(byte)0x19, (byte)0xD2, (byte)0x18, (byte)0x3E, (byte)0x31, (byte)0xE4, (byte)0x23, (byte)0xAC,
			(byte)0x02, (byte)0x8A, (byte)0xC5, (byte)0x31, (byte)0x7C, (byte)0x64, (byte)0x28, (byte)0x67,
			(byte)0x60, (byte)0x82, (byte)0x28, (byte)0x58, (byte)0xCA, (byte)0x70, (byte)0x8C, (byte)0xE1,
			(byte)0x3F, (byte)0xB2, (byte)0x34, (byte)0x58, (byte)0xC9, (byte)0x7F, (byte)0xA0, (byte)0xE8,
			(byte)0x52, (byte)0x88, (byte)0x02, (byte)0x2D, (byte)0xA0, (byte)0xFD, (byte)0xD8, (byte)0x14,
			(byte)0xB0, (byte)0x30, (byte)0x68, (byte)0x21, (byte)0xDC, (byte)0x80, (byte)0x45, (byte)0x01,
			(byte)0x94, (byte)0x46, (byte)0xE6, (byte)0x32, (byte)0x5C, (byte)0x05, (byte)0xEA, (byte)0xFB,
			(byte)0xCF, (byte)0x70, (byte)0x15, (byte)0x9F, (byte)0x02, (byte)0x6D, (byte)0x20, (byte)0xA4,
			(byte)0x95, (byte)0x02, (byte)0x28, (byte)0x04, (byte)0x29, (byte)0x80, (byte)0xB2, (byte)0x51,
			(byte)0x14, (byte)0xE0, (byte)0x86, (byte)0x18, (byte)0x02, (byte)0xE8, (byte)0x10, (byte)0x00,
			(byte)0x54, (byte)0xD4, (byte)0xDF, (byte)0x65, (byte)0x5E, (byte)0x2D, (byte)0xE8, (byte)0xEC,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44,
			(byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/directoryNew
		byte[]	NEW_DIRECTORY	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xDA, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x75, (byte)0x90, (byte)0x3F, (byte)0x0E, (byte)0x01,
			(byte)0x51, (byte)0x10, (byte)0xC6, (byte)0x7F, (byte)0x85, (byte)0x66, (byte)0x13, (byte)0x07,
			(byte)0xA0, (byte)0xA2, (byte)0xC5, (byte)0x6E, (byte)0xE7, (byte)0x30, (byte)0x7A, (byte)0x07,
			(byte)0xD0, (byte)0xB1, (byte)0xD9, (byte)0x86, (byte)0x38, (byte)0x80, (byte)0xCE, (byte)0x0D,
			(byte)0x14, (byte)0x28, (byte)0xD1, (byte)0x8B, (byte)0x70, (byte)0x03, (byte)0xC5, (byte)0x46,
			(byte)0x70, (byte)0x00, (byte)0x89, (byte)0x52, (byte)0x2C, (byte)0x12, (byte)0xD6, (byte)0xEC,
			(byte)0x78, (byte)0xC4, (byte)0x3E, (byte)0x91, (byte)0xC9, (byte)0xBE, (byte)0xC9, (byte)0xF7,
			(byte)0xE7, (byte)0x7D, (byte)0x6F, (byte)0x66, (byte)0x79, (byte)0x90, (byte)0x2E, (byte)0x32,
			(byte)0x04, (byte)0xEC, (byte)0xB9, (byte)0xC9, (byte)0xD7, (byte)0xC5, (byte)0x11, (byte)0xFC,
			(byte)0x23, (byte)0x2F, (byte)0x98, (byte)0x50, (byte)0x26, (byte)0x47, (byte)0x85, (byte)0x21,
			(byte)0x4B, (byte)0xC1, (byte)0x96, (byte)0x21, (byte)0x60, (byte)0xA2, (byte)0xFD, (byte)0xAC,
			(byte)0xE7, (byte)0x14, (byte)0xDF, (byte)0x36, (byte)0xEC, (byte)0x29, (byte)0x6B, (byte)0x8F,
			(byte)0xF5, (byte)0x74, (byte)0xD9, (byte)0x82, (byte)0x43, (byte)0x48, (byte)0x2C, (byte)0x15,
			(byte)0xEA, (byte)0x8B, (byte)0x57, (byte)0xF2, (byte)0x44, (byte)0x8A, (byte)0x63, (byte)0xE9,
			(byte)0x79, (byte)0x2E, (byte)0xD0, (byte)0x61, (byte)0xA4, (byte)0xEE, (byte)0x31, (byte)0x6D,
			(byte)0x4D, (byte)0xA8, (byte)0x7C, (byte)0x25, (byte)0x78, (byte)0x49, (byte)0xC2, (byte)0x89,
			(byte)0x82, (byte)0x82, (byte)0x02, (byte)0x47, (byte)0xBD, (byte)0x37, (byte)0xFC, (byte)0x9A,
			(byte)0x61, (byte)0x46, (byte)0xD3, (byte)0x78, (byte)0x3F, (byte)0x33, (byte)0x38, (byte)0xAC,
			(byte)0x84, (byte)0x76, (byte)0x65, (byte)0x0B, (byte)0x4F, (byte)0xFA, (byte)0x5C, (byte)0xB6,
			(byte)0x48, (byte)0x1B, (byte)0x74, (byte)0x51, (byte)0x9F, (byte)0x1D, (byte)0x77, (byte)0x09,
			(byte)0x6F, (byte)0x91, (byte)0x11, (byte)0x6C, (byte)0x5E, (byte)0x6B, (byte)0x08, (byte)0xF5,
			(byte)0x1A, (byte)0x2D, (byte)0x5D, (byte)0x3D, (byte)0x35, (byte)0x50, (byte)0xE5, (byte)0x40,
			(byte)0x31, (byte)0x9D, (byte)0xF4, (byte)0x66, (byte)0xC5, (byte)0x40, (byte)0x96, (byte)0x0D,
			(byte)0xB5, (byte)0x1F, (byte)0xD9, (byte)0xB0, (byte)0x89, (byte)0x61, (byte)0x40, (byte)0xDF,
			(byte)0x96, (byte)0x45, (byte)0x30, (byte)0x6C, (byte)0x62, (byte)0x58, (byte)0x27, (byte)0xBF,
			(byte)0xC8, (byte)0x92, (byte)0xEB, (byte)0x6F, (byte)0x96, (byte)0x3F, (byte)0xC3, (byte)0x45,
			(byte)0x94, (byte)0x8C, (byte)0xD5, (byte)0xBE, (byte)0x6B, (byte)0xD7, (byte)0x13, (byte)0xF9,
			(byte)0x47, (byte)0xD1, (byte)0x11, (byte)0x97, (byte)0xB4, (byte)0x7A, (byte)0x82, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE,
			(byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/refresh
		byte[]	REFRESH	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xC4, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x7D, (byte)0xCF, (byte)0x4B, (byte)0x0E, (byte)0x82,
			(byte)0x30, (byte)0x00, (byte)0x04, (byte)0xD0, (byte)0xE9, (byte)0x4A, (byte)0xD8, (byte)0x48,
			(byte)0x3C, (byte)0x13, (byte)0xC4, (byte)0x0F, (byte)0x9E, (byte)0xC1, (byte)0x13, (byte)0x90,
			(byte)0x08, (byte)0xBA, (byte)0xD5, (byte)0xE8, (byte)0x96, (byte)0x98, (byte)0x78, (byte)0x03,
			(byte)0x43, (byte)0xF4, (byte)0x0E, (byte)0x4A, (byte)0xDC, (byte)0x7A, (byte)0x1C, (byte)0xE3,
			(byte)0xE7, (byte)0x08, (byte)0xE0, (byte)0xD0, (byte)0x16, (byte)0x28, (byte)0xD6, (byte)0x98,
			(byte)0x49, (byte)0x9A, (byte)0x76, (byte)0xFA, (byte)0xDA, (byte)0xA6, (byte)0x28, (byte)0xF0,
			(byte)0x3F, (byte)0xE6, (byte)0xD4, (byte)0xC5, (byte)0x14, (byte)0x0B, (byte)0x26, (byte)0x84,
			(byte)0x6B, (byte)0x01, (byte)0x08, (byte)0x24, (byte)0x78, (byte)0xA1, (byte)0xD4, (byte)0x79,
			(byte)0x62, (byte)0x0E, (byte)0x61, (byte)0x00, (byte)0x6E, (byte)0x9F, (byte)0x58, (byte)0x5F,
			(byte)0xE0, (byte)0xC3, (byte)0x63, (byte)0x02, (byte)0xE4, (byte)0x5C, (byte)0xED, (byte)0x4D,
			(byte)0x10, (byte)0xB3, (byte)0x58, (byte)0xB7, (byte)0xD7, (byte)0xB2, (byte)0xD9, (byte)0xB1,
			(byte)0x89, (byte)0x34, (byte)0x80, (byte)0x83, (byte)0x37, (byte)0xCE, (byte)0xE6, (byte)0xB6,
			(byte)0xBC, (byte)0x33, (byte)0xE7, (byte)0x43, (byte)0x3D, (byte)0x05, (byte)0x26, (byte)0xD4,
			(byte)0x7E, (byte)0x17, (byte)0xB0, (byte)0x0D, (byte)0xD8, (byte)0x8E, (byte)0x15, (byte)0x58,
			(byte)0x72, (byte)0xDA, (byte)0xB7, (byte)0xC0, (byte)0x80, (byte)0x6D, (byte)0xD2, (byte)0x02,
			(byte)0xEF, (byte)0x27, (byte)0x88, (byte)0x15, (byte)0x08, (byte)0x39, (byte)0x0D, (byte)0x2C,
			(byte)0x30, (byte)0x64, (byte)0x3B, (byte)0x52, (byte)0xC0, (byte)0xC5, (byte)0x1D, (byte)0xB7,
			(byte)0xFA, (byte)0xDF, (byte)0x5C, (byte)0xA7, (byte)0x72, (byte)0xBC, (byte)0xE2, (byte)0x01,
			(byte)0x47, (byte)0x02, (byte)0x0E, (byte)0x33, (byte)0xEA, (byte)0x4D, (byte)0x03, (byte)0x4A,
			(byte)0xA4, (byte)0xD8, (byte)0x1A, (byte)0xDF, (byte)0x2C, (byte)0xAA, (byte)0x4F, (byte)0x1D,
			(byte)0x59, (byte)0x1C, (byte)0x1A, (byte)0x50, (byte)0x25, (byte)0x53, (byte)0x77, (byte)0xD6,
			(byte)0xA7, (byte)0x04, (byte)0x22, (byte)0xAC, (byte)0x3A, (byte)0x40, (byte)0x3E, (byte)0xD4,
			(byte)0x00, (byte)0x33, (byte)0x1A, (byte)0x68, (byte)0x62, (byte)0x6D, (byte)0x7F, (byte)0xE7,
			(byte)0x03, (byte)0x72, (byte)0x7E, (byte)0xD2, (byte)0x9E, (byte)0x78, (byte)0x1E, (byte)0x74,
			(byte)0xDB, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E,
			(byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/buttonBar
		byte[]	BUTTON_BAR	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x53, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x8D, (byte)0xD0, (byte)0xCB, (byte)0x09, (byte)0x40,
			(byte)0x21, (byte)0x10, (byte)0x43, (byte)0xD1, (byte)0xAC, (byte)0x66, (byte)0x65, (byte)0x37,
			(byte)0xF6, (byte)0x64, (byte)0xF9, (byte)0x76, (byte)0xA0, (byte)0x3C, (byte)0x75, (byte)0x25,
			(byte)0x37, (byte)0x30, (byte)0x3E, (byte)0x0E, (byte)0x88, (byte)0x86, (byte)0xE0, (byte)0x4F,
			(byte)0x53, (byte)0x39, (byte)0x0B, (byte)0xC8, (byte)0x02, (byte)0xB2, (byte)0x80, (byte)0xA4,
			(byte)0xAA, (byte)0x96, (byte)0xA8, (byte)0x52, (byte)0xA8, (byte)0x24, (byte)0x62, (byte)0x17,
			(byte)0xBA, (byte)0xC6, (byte)0xD2, (byte)0xD7, (byte)0xD2, (byte)0x67, (byte)0xA7, (byte)0x30,
			(byte)0x4E, (byte)0x77, (byte)0x8F, (byte)0x3E, (byte)0xFB, (byte)0x57, (byte)0xF0, (byte)0x8D,
			(byte)0x71, (byte)0x04, (byte)0x2F, (byte)0x76, (byte)0x8B, (byte)0xF7, (byte)0x33, (byte)0xF9,
			(byte)0x31, (byte)0x64, (byte)0x01, (byte)0x59, (byte)0x40, (byte)0x16, (byte)0xD0, (byte)0x07,
			(byte)0x12, (byte)0x6C, (byte)0xB0, (byte)0x5D, (byte)0xD8, (byte)0xC7, (byte)0x42, (byte)0x4D,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44,
			(byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/pencil
		byte[]	PENCIL	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xA9, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x7D, (byte)0xCE, (byte)0x3F, (byte)0x0A, (byte)0xC2,
			(byte)0x30, (byte)0x14, (byte)0x80, (byte)0xF1, (byte)0x4F, (byte)0x70, (byte)0x14, (byte)0x07,
			(byte)0x71, (byte)0xA8, (byte)0xBB, (byte)0x6B, (byte)0x75, (byte)0x15, (byte)0xC1, (byte)0xA2,
			(byte)0xBD, (byte)0x96, (byte)0x8B, (byte)0x78, (byte)0x02, (byte)0x17, (byte)0xEB, (byte)0x05,
			(byte)0xC4, (byte)0xDD, (byte)0xC1, (byte)0x4E, (byte)0x1E, (byte)0xC0, (byte)0x53, (byte)0x08,
			(byte)0x3A, (byte)0xF9, (byte)0x17, (byte)0x37, (byte)0x07, (byte)0x07, (byte)0xF1, (byte)0x99,
			(byte)0x46, (byte)0x69, (byte)0x1F, (byte)0x49, (byte)0x09, (byte)0x69, (byte)0x42, (byte)0x7F,
			(byte)0x1F, (byte)0xBC, (byte)0xF0, (byte)0xC6, (byte)0xB5, (byte)0x68, (byte)0x52, (byte)0xB3,
			(byte)0x37, (byte)0x4D, (byte)0xE6, (byte)0xE7, (byte)0x90, (byte)0x3B, (byte)0x0F, (byte)0xD6,
			(byte)0x04, (byte)0xCE, (byte)0x40, (byte)0xF8, (byte)0x22, (byte)0xBB, (byte)0xCA, (byte)0x8C,
			(byte)0x2D, (byte)0x15, (byte)0x0F, (byte)0xCB, (byte)0xD9, (byte)0xA0, (byte)0xCF, (byte)0x89,
			(byte)0xB6, (byte)0x9F, (byte)0x7B, (byte)0x4C, (byte)0x65, (byte)0x50, (byte)0xAB, (byte)0xC8,
			(byte)0x03, (byte)0xCE, (byte)0xC4, (byte)0x96, (byte)0x27, (byte)0x5C, (byte)0xCD, (byte)0xBD,
			(byte)0x9C, (byte)0x73, (byte)0x81, (byte)0x9B, (byte)0xFF, (byte)0x81, (byte)0x8F, (byte)0x6D,
			(byte)0x40, (byte)0xC4, (byte)0x93, (byte)0x94, (byte)0xBA, (byte)0x7D, (byte)0xDA, (byte)0xED,
			(byte)0xFB, (byte)0xCC, (byte)0x62, (byte)0x90, (byte)0xB0, (byte)0x62, (byte)0xCE, (byte)0x9E,
			(byte)0x58, (byte)0xF3, (byte)0x2F, (byte)0x38, (byte)0xD2, (byte)0x91, (byte)0xEF, (byte)0x82,
			(byte)0x9D, (byte)0x66, (byte)0x13, (byte)0x10, (byte)0xCA, (byte)0x80, (byte)0x0D, (byte)0x5D,
			(byte)0x96, (byte)0x72, (byte)0x2A, (byte)0xCE, (byte)0x82, (byte)0x31, (byte)0x2F, (byte)0xA1,
			(byte)0x83, (byte)0x0C, (byte)0x19, (byte)0x69, (byte)0xCE, (byte)0x82, (byte)0x44, (byte)0x92,
			(byte)0x50, (byte)0x43, (byte)0x2E, (byte)0x28, (byte)0x5F, (byte)0x1F, (byte)0x8B, (byte)0x52,
			(byte)0xC9, (byte)0x6E, (byte)0x7C, (byte)0x8A, (byte)0xFF, (byte)0x0E, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42,
			(byte)0x60, (byte)0x82
		};

		// File: mono/expand
		byte[]	EXPAND	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x69, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xF8, (byte)0xC7, (byte)0x80, (byte)0x1F,
			(byte)0xA2, (byte)0x73, (byte)0xD3, (byte)0x19, (byte)0xD2, (byte)0xF0, (byte)0x28, (byte)0x60,
			(byte)0xB0, (byte)0x64, (byte)0xF8, (byte)0xC1, (byte)0xF0, (byte)0x8B, (byte)0xC1, (byte)0x16,
			(byte)0x87, (byte)0x02, (byte)0x06, (byte)0x09, (byte)0x86, (byte)0x27, (byte)0x0C, (byte)0xFF,
			(byte)0x81, (byte)0xF0, (byte)0x39, (byte)0x83, (byte)0x34, (byte)0x16, (byte)0x05, (byte)0x0C,
			(byte)0xAC, (byte)0x0C, (byte)0x87, (byte)0xC0, (byte)0xD2, (byte)0x20, (byte)0x78, (byte)0x9C,
			(byte)0x81, (byte)0x1D, (byte)0x53, (byte)0xC1, (byte)0x4C, (byte)0xB8, (byte)0x34, (byte)0x08,
			(byte)0x2E, (byte)0xC0, (byte)0x50, (byte)0x80, (byte)0x0B, (byte)0x62, (byte)0x08, (byte)0xA0,
			(byte)0x43, (byte)0x0C, (byte)0x01, (byte)0x74, (byte)0x88, (byte)0x21, (byte)0x80, (byte)0x0E,
			(byte)0x11, (byte)0x0C, (byte)0x54, (byte)0x47, (byte)0xCE, (byte)0xC0, (byte)0x54, (byte)0x80,
			(byte)0xEC, (byte)0xCD, (byte)0x63, (byte)0x58, (byte)0xBC, (byte)0x49, (byte)0x30, (byte)0xA0,
			(byte)0xC0, (byte)0x1C, (byte)0x0B, (byte)0xBC, (byte)0x41, (byte)0x0D, (byte)0xE6, (byte)0xA6,
			(byte)0xE1, (byte)0x8D, (byte)0x2C, (byte)0x6C, (byte)0x10, (byte)0x00, (byte)0x25, (byte)0x0D,
			(byte)0xEE, (byte)0x50, (byte)0xD6, (byte)0xD5, (byte)0xCF, (byte)0xEB, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42,
			(byte)0x60, (byte)0x82
		};

		// File: mono/collapse
		byte[]	COLLAPSE	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x6A, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xF8, (byte)0xC7, (byte)0x80, (byte)0x1F,
			(byte)0x62, (byte)0x08, (byte)0xA0, (byte)0x43, (byte)0x04, (byte)0x63, (byte)0x26, (byte)0xC3,
			(byte)0x7F, (byte)0x24, (byte)0x38, (byte)0x03, (byte)0x53, (byte)0x01, (byte)0x2B, (byte)0xC3,
			(byte)0x21, (byte)0xB8, (byte)0xF4, (byte)0x31, (byte)0x06, (byte)0x76, (byte)0x0C, (byte)0x05,
			(byte)0x40, (byte)0xA6, (byte)0x04, (byte)0xC3, (byte)0x13, (byte)0xB0, (byte)0xF4, (byte)0x73,
			(byte)0x06, (byte)0x69, (byte)0x24, (byte)0x51, (byte)0x04, (byte)0x13, (byte)0xC8, (byte)0xB1,
			(byte)0x60, (byte)0xF8, (byte)0xC1, (byte)0xF0, (byte)0x8B, (byte)0xC1, (byte)0x16, (byte)0x45,
			(byte)0x0C, (byte)0x99, (byte)0x03, (byte)0xE4, (byte)0xA6, (byte)0x31, (byte)0xA4, (byte)0xA1,
			(byte)0x89, (byte)0xA0, (byte)0x72, (byte)0x31, (byte)0x21, (byte)0x86, (byte)0x00, (byte)0x3A,
			(byte)0x44, (byte)0xE7, (byte)0xA6, (byte)0xE3, (byte)0xB5, (byte)0x82, (byte)0xC1, (byte)0x12,
			(byte)0xAF, (byte)0x23, (byte)0x09, (byte)0x78, (byte)0x13, (byte)0x25, (byte)0xA0, (byte)0x8E,
			(byte)0x63, (byte)0x09, (byte)0x28, (byte)0xB4, (byte)0xA0, (byte)0x5E, (byte)0x80, (byte)0xA1,
			(byte)0x00, (byte)0x17, (byte)0xC4, (byte)0x10, (byte)0x40, (byte)0x87, (byte)0x00, (byte)0x19,
			(byte)0xB5, (byte)0xEE, (byte)0x50, (byte)0xD8, (byte)0x2A, (byte)0xAA, (byte)0xCC, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE,
			(byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/selectFromList
		byte[]	SELECT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x4C, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xF8, (byte)0xC7, (byte)0x80, (byte)0x1F,
			(byte)0x82, (byte)0xD0, (byte)0x7F, (byte)0x74, (byte)0x88, (byte)0xA1, (byte)0xE0, (byte)0xF7,
			(byte)0x5F, (byte)0x64, (byte)0x48, (byte)0x13, (byte)0x05, (byte)0xE8, (byte)0x10, (byte)0x28,
			(byte)0xD6, (byte)0xC5, (byte)0xC0, (byte)0x8B, (byte)0xAC, (byte)0x40, (byte)0x0D, (byte)0x05,
			(byte)0x3A, (byte)0x80, (byte)0xC5, (byte)0x5E, (byte)0x33, (byte)0xE4, (byte)0x33, (byte)0x30,
			(byte)0x61, (byte)0x2A, (byte)0x40, (byte)0x35, (byte)0x6B, (byte)0x12, (byte)0xA6, (byte)0x02,
			(byte)0x84, (byte)0x09, (byte)0xCF, (byte)0x18, (byte)0xD2, (byte)0x10, (byte)0x26, (byte)0x10,
			(byte)0x72, (byte)0x03, (byte)0x41, (byte)0x5F, (byte)0x50, (byte)0xAE, (byte)0x00, (byte)0x1D,
			(byte)0xA2, (byte)0x29, (byte)0xC0, (byte)0x0F, (byte)0x31, (byte)0x04, (byte)0xD0, (byte)0x21,
			(byte)0x00, (byte)0xBF, (byte)0x93, (byte)0x0F, (byte)0x4C, (byte)0xD6, (byte)0x58, (byte)0x64,
			(byte)0x3D, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E,
			(byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};

		// File: mono/copy
		byte[]	COPY	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x59, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xF8, (byte)0xC7, (byte)0x80, (byte)0x1F,
			(byte)0x22, (byte)0x33, (byte)0xDF, (byte)0x33, (byte)0xFC, (byte)0x07, (byte)0xC3, (byte)0xDF,
			(byte)0x0C, (byte)0x49, (byte)0xD8, (byte)0x15, (byte)0xFC, (byte)0x87, (byte)0xD1, (byte)0x0C,
			(byte)0x8F, (byte)0x19, (byte)0x32, (byte)0xF1, (byte)0x2B, (byte)0x50, (byte)0x45, (byte)0x28,
			(byte)0xC1, (byte)0xA6, (byte)0x00, (byte)0x62, (byte)0xD5, (byte)0x6F, (byte)0x14, (byte)0x05,
			(byte)0x98, (byte)0xF6, (byte)0xC3, (byte)0x95, (byte)0xA3, (byte)0x71, (byte)0xE1, (byte)0xF6,
			(byte)0xE3, (byte)0x56, (byte)0x00, (byte)0xB5, (byte)0x1F, (byte)0x97, (byte)0x02, (byte)0xB8,
			(byte)0xFD, (byte)0x38, (byte)0x14, (byte)0x20, (byte)0x78, (byte)0x04, (byte)0x14, (byte)0x00,
			(byte)0xE1, (byte)0x7B, (byte)0xBC, (byte)0x0A, (byte)0x90, (byte)0xD8, (byte)0x98, (byte)0x42,
			(byte)0x64, (byte)0x2A, (byte)0x80, (byte)0x05, (byte)0x14, (byte)0x04, (byte)0x42, (byte)0xED,
			(byte)0x47, (byte)0x52, (byte)0x80, (byte)0x1B, (byte)0x02, (byte)0x00, (byte)0xE5, (byte)0x32,
			(byte)0xF8, (byte)0x0C, (byte)0x4A, (byte)0x24, (byte)0x84, (byte)0xAC, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42,
			(byte)0x60, (byte)0x82
		};

		// File: mono/copy-lines
		byte[]	COPY_LINES	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x6D, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x7D, (byte)0xD0, (byte)0xBB, (byte)0x0D, (byte)0xC0,
			(byte)0x20, (byte)0x0C, (byte)0x04, (byte)0xD0, (byte)0x5B, (byte)0x26, (byte)0x8B, (byte)0xA5,
			(byte)0xCB, (byte)0x34, (byte)0x4C, (byte)0x94, (byte)0x69, (byte)0xD2, (byte)0xD0, (byte)0xD1,
			(byte)0x46, (byte)0x8A, (byte)0x14, (byte)0x82, (byte)0x6C, (byte)0xFE, (byte)0x5C, (byte)0xAC,
			(byte)0x2B, (byte)0x30, (byte)0xE8, (byte)0xC9, (byte)0x27, (byte)0x81, (byte)0x17, (byte)0x76,
			(byte)0xFA, (byte)0x31, (byte)0x20, (byte)0x4A, (byte)0x1E, (byte)0xEC, (byte)0x1C, (byte)0xC4,
			(byte)0x72, (byte)0xE2, (byte)0xC2, (byte)0x61, (byte)0x83, (byte)0xAD, (byte)0x91, (byte)0x09,
			(byte)0xC0, (byte)0xA5, (byte)0xDC, (byte)0x5A, (byte)0x34, (byte)0x80, (byte)0xB5, (byte)0xBF,
			(byte)0xEE, (byte)0x9B, (byte)0xAE, (byte)0x11, (byte)0x1E, (byte)0x27, (byte)0x9C, (byte)0x05,
			(byte)0x72, (byte)0x3F, (byte)0x01, (byte)0x63, (byte)0x3F, (byte)0x01, (byte)0x2D, (byte)0xC2,
			(byte)0x57, (byte)0x20, (byte)0x1B, (byte)0x9C, (byte)0xF6, (byte)0xA7, (byte)0x84, (byte)0x05,
			(byte)0x8C, (byte)0x1B, (byte)0xEA, (byte)0xCC, (byte)0x37, (byte)0x50, (byte)0xD0, (byte)0x3D,
			(byte)0x12, (byte)0x50, (byte)0x3E, (byte)0x4A, (byte)0x93, (byte)0xFB, (byte)0x3B, (byte)0xF0,
			(byte)0x9F, (byte)0x0F, (byte)0xA9, (byte)0xA0, (byte)0xF2, (byte)0xD4, (byte)0x33, (byte)0x76,
			(byte)0x77, (byte)0x50, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45,
			(byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};
	}

	//==================================================================

}

//----------------------------------------------------------------------
