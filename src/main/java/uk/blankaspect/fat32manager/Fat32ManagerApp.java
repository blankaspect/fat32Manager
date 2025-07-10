/*====================================================================*\

Fat32ManagerApp.java

Class: FAT32 manager application.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.beans.property.SimpleObjectProperty;

import javafx.concurrent.Task;

import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;

import javafx.scene.Scene;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.stage.Stage;

import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.common.build.BuildUtils;

import uk.blankaspect.common.cls.ClassUtils;

import uk.blankaspect.common.config.AppAuxDirectory;
import uk.blankaspect.common.config.AppConfig;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.function.IProcedure0;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.common.logging.Logger;
import uk.blankaspect.common.logging.LoggerUtils;
import uk.blankaspect.common.logging.LogLevel;

import uk.blankaspect.common.message.MessageConstants;

import uk.blankaspect.common.os.OsUtils;

import uk.blankaspect.common.resource.ResourceProperties;
import uk.blankaspect.common.resource.ResourceUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.thread.DaemonFactory;

import uk.blankaspect.common.tree.TreeUtils;

import uk.blankaspect.driveio.BlockVolume;
import uk.blankaspect.driveio.DriveIO;
import uk.blankaspect.driveio.IVolumeAccessor;
import uk.blankaspect.driveio.Volume;
import uk.blankaspect.driveio.VolumeException;

import uk.blankaspect.ui.jfx.button.ImageDataButton;

import uk.blankaspect.ui.jfx.container.PropertiesPane;

import uk.blankaspect.ui.jfx.dialog.ButtonInfo;
import uk.blankaspect.ui.jfx.dialog.ConfirmationDialog;
import uk.blankaspect.ui.jfx.dialog.ExceptionDialog;
import uk.blankaspect.ui.jfx.dialog.MessageListDialog;
import uk.blankaspect.ui.jfx.dialog.NotificationDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleProgressDialog;

import uk.blankaspect.ui.jfx.exec.ExecUtils;

import uk.blankaspect.ui.jfx.image.ImageData;
import uk.blankaspect.ui.jfx.image.MessageIcon32;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.window.WindowState;

//----------------------------------------------------------------------


// CLASS: FAT32 MANAGER APPLICATION


public class Fat32ManagerApp
	extends Application
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The short name of the application. */
	private static final	String	SHORT_NAME	= "FAT32Manager";

	/** The long name of the application. */
	private static final	String	LONG_NAME	= "FAT32 manager";

	/** The name of the application when used as a key. */
	private static final	String	NAME_KEY	= "fat32Manager";

	private static final	String	BUILD_PROPERTIES_FILENAME	= "build.properties";

	private static final	String	LIBRARY_DIRECTORY_NAME	= "lib";

	/** The name of the log file. */
	private static final	String	LOG_FILENAME	= NAME_KEY + ".log";

	/** The number of lines of a previous log file that are retained. */
	private static final	int		LOG_NUM_RETAINED_LINES	= 10000;

	/** The logging threshold. */
	private static final	LogLevel	LOG_THRESHOLD	= LogLevel.INFO;

	/** The formatting parameters of the logger. */
	private static final	Logger.Params	LOG_PARAMS	= new Logger.Params
	(
		null,
		null,
		EnumSet.of(Logger.Field.TIMESTAMP, Logger.Field.LEVEL, Logger.Field.SOURCE_LOCATION),
		true
	);

	/** The filename of the CSS style sheet. */
	private static final	String	STYLE_SHEET_FILENAME	= NAME_KEY + "-%02d.css";

	/** The interval (in milliseconds) between successive checks of the current volume. */
	private static final	int		CHECK_VOLUME_INTERVAL	= 500;

	/** The suffix of the name of a thread on which a check of the current volume is performed. */
	private static final	String	CHECK_VOLUME_THREAD_NAME_SUFFIX	= "checkVolume";

	/** The delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler on platforms other than Windows. */
	private static final	int		WINDOW_SHOWN_DELAY	= 150;

	/** The delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler on Windows. */
	private static final	int		WINDOW_SHOWN_DELAY_WINDOWS	= 50;

	/** The delay (in milliseconds) before making the main window visible by restoring its opacity. */
	private static final	int		WINDOW_VISIBLE_DELAY	= 50;

	/** The margins that are applied to the visual bounds of each screen when determining whether the saved location of
		the main window is within a screen. */
	private static final	Insets	SCREEN_MARGINS	= new Insets(0.0, 32.0, 32.0, 0.0);

	/** The key combination for the command that opens the previous directory. */
	private static final	KeyCombination	KEY_COMBO_OPEN_PREVIOUS	=
			new KeyCodeCombination(KeyCode.LEFT, KeyCombination.ALT_DOWN);

	/** The key combination for the command that opens the next directory. */
	private static final	KeyCombination	KEY_COMBO_OPEN_NEXT		=
			new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.ALT_DOWN);

	/** The key combination for the command that opens the parent directory. */
	private static final	KeyCombination	KEY_COMBO_OPEN_PARENT	=
			new KeyCodeCombination(KeyCode.UP, KeyCombination.ALT_DOWN);

	/** Miscellaneous strings. */
	private static final	String	ELLIPSIS_STR				= "...";
	private static final	String	STARTING_STR				= "Starting";
	private static final	String	TERMINATING_STR				= "Terminating";
	private static final	String	PID_STR						= "PID = ";
	private static final	String	ARGS1_STR					= "args[";
	private static final	String	ARGS2_STR					= "] = ";
	private static final	String	JAVA_VERSION_STR			= "Java version";
	private static final	String	CONFIG_ERROR_STR			= "Configuration error";
	private static final	String	FATAL_ERROR_STR				= "Fatal error";
	private static final	String	VOLUME_STR					= "Volume";
	private static final	String	NO_VOLUMES_TO_OPEN_STR		= "There are no volumes to open.";
	private static final	String	OPEN_VOLUME_STR				= "Open volume";
	private static final	String	OPEN_STR					= "Open";
	private static final	String	OPENING_STR					= "Opening";
	private static final	String	READING_FATS_STR			= "Reading reserved sectors and FATs";
	private static final	String	READING_ROOT_DIRECTORY_STR	= "Reading root directory";
	private static final	String	CLOSE_STR					= "Close";
	private static final	String	VIEW_SECTORS_STR			= "View sectors";
	private static final	String	VIEW_CLUSTERS_STR			= "View clusters";
	private static final	String	VALIDATE_CLUSTER_CHAINS_STR	= "Validate cluster chains";
	private static final	String	INVALID_CLUSTERS_STR		= "Invalid clusters";
	private static final	String	CLUSTER_CHAINS_VALID_STR	= "All the cluster chains were valid.";
	private static final	String	UNEXPECTED_NUM_CLUSTERS_STR	= "The total length of the cluster chains was %d.\n"
																	+ "The expected value was %d.";
	private static final	String	ERASE_UNUSED_CLUSTERS_STR	= "Erase unused clusters";
	private static final	String	UNUSED_CLUSTERS_ERASED_STR	= "All unused clusters were successfully erased.";
	private static final	String	FORMAT_STR					= "Format";
	private static final	String	FORMAT_VOLUME_STR			= "Format volume";
	private static final	String	SEARCHING_FOR_VOLUMES_STR	= "Searching for volumes";
	private static final	String	NO_VOLUMES_TO_FORMAT_STR	= "No volumes are eligible to be formatted.";
	private static final	String	FORMATTED_STR				=
		"""
		The volume was formatted.
		Its capacity is %,d bytes.""" + MessageConstants.LABEL_SEPARATOR +
		"""
		The operating system may not be able to detect the new format
		unless you unmount or eject the volume.""";
	private static final	String	DETERMINING_NUM_SECTORS_STR	= "Determining the number of available sectors";
	private static final	String	PROPERTIES_STR				= "Properties";
	private static final	String	OF_STR						= " of ";
	private static final	String	EXIT_STR					= "Exit";
	private static final	String	EDIT_STR					= "Edit";
	private static final	String	PREFERENCES_STR				= "Preferences";
	private static final	String	DIRECTORY_STR				= "Directory";
	private static final	String	SORT_BY_NAME_STR			= "Sort entries by name";
	private static final	String	CONFIRM_SORT_STR			= "The sorting operation may modify the volume.\n"
																	+ "Do you want to proceed?";
	private static final	String	SORT_STR					= "Sort";
	private static final	String	VIEW_SECTOR_STR				= "View sector";
	private static final	String	VIEW_CLUSTER_STR			= "View cluster";
	private static final	String	VIEW_DELETED_ENTRIES_STR	= "View deleted entries";
	private static final	String	DEFRAGMENT_FILES_STR		= "Defragment files";
	private static final	String	SEARCHING_FOR_FILES_STR		= "Searching for files";
	private static final	String	DEFRAGMENTING_FILES_STR		= "Defragmenting files";
	private static final	String	NO_FILES_TO_DEFRAGMENT_STR	= "There are no files to defragment.";
	private static final	String	NOT_ENOUGH_SPACE_STR		= """
		The following files could not be defragmented because
		there was not enough space on the volume:""";
	private static final	String	OK_STR						= "OK";
	private static final	String	CONTINUE_STR				= "Continue";
	private static final	String	CANCEL_STR					= "Cancel";
	private static final	String	SEARCHING_ENTRIES_STR		= "Searching directory entries";
	private static final	String	VIEW_STR					= "View";
	private static final	String	SELECT_TABLE_COLUMNS_STR	= "Select table columns";
	private static final	String	TOOLS_STR					= "Tools";
	private static final	String	MESSAGES_STR				= "Messages";

	/** Keys of properties. */
	private interface PropertyKey
	{
		String	APPEARANCE					= "appearance";
		String	COLUMN_HEADER_POP_UP_DELAY	= "columnHeaderPopUpDelay";
		String	COLUMN_WIDTHS				= "columnWidths";
		String	DELETED_ENTRIES_DIALOG		= "deletedEntriesDialog";
		String	DIRECTORY_ENTRIES			= "directoryEntries";
		String	FIX_INVALID_DATES_TIMES		= "fixInvalidDatesTimes";
		String	FORMAT						= "format";
		String	MAIN_WINDOW					= "mainWindow";
		String	SECTOR_CLUSTER_VIEW_DIALOG	= "sectorClusterViewDialog";
		String	SHOW_SPECIAL_DIRECTORIES	= "showSpecialDirectories";
		String	THEME						= "theme";
		String	VIEW						= "view";
	}

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	USE_STYLE_SHEET_FILE	= "useStyleSheetFile";
		String	VOLUME_ACCESSOR			= "volumeAccessor";
		String	WINDOW_SHOWN_DELAY		= "windowShownDelay";
	}

	/** Keys that are associated with dialogs. */
	private interface DialogKey
	{
		String	OPEN_VOLUME				= "openVolume";
		String	SHOW_DELETED_ENTRIES	= "showDeletedEntries";
		String	VOLUME_PROPERTIES		= "volumeProperties";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	VOLUME_ACCESSOR_CLASS_NOT_FOUND =
				"The volume-accessor class '%s' was not found.";

		String	INVALID_VOLUME_ACCESSOR_CLASS =
				"The class '%s' is not a valid volume accessor.";

		String	FAILED_TO_CREATE_VOLUME_ACCESSOR =
				"Failed to create a volume accessor.";

		String	INVALID_LIBRARY =
				"The library '%s' is not valid.";

		String	FAILED_TO_LOAD_LIBRARY =
				"Failed to load the library '%s'.";

		String	VOLUME_NOT_MOUNTED =
				"Volume: %s\nThe volume is not mounted.";

		String	UNSUPPORTED_SECTOR_SIZE =
				"The formatter does not support a sector size of %d bytes.";

		String	START_SECTOR_OUT_OF_BOUNDS =
				"The start sector of the partition is out of bounds.";

		String	TOO_FEW_SECTORS =
				"The volume has too few sectors to format as FAT32.";

		String	TOO_MANY_SECTORS =
				"The volume has too many sectors to format as FAT32.";

		String	INVALID_FORMAT_PARAMS =
				"The format parameters are invalid.";

		String	NO_AUXILIARY_DIRECTORY =
				"The location of the auxiliary directory could not be determined.";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** The instance of this application. */
	private static	Fat32ManagerApp	instance;

	/** The index of the last thread that was created for a background task. */
	private static	int				threadIndex;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	ResourceProperties					buildProperties;
	private	String								versionStr;
	private	Preferences							preferences;
	private	FormatParams						formatParams;
	private	SimpleObjectProperty<Fat32Volume>	volume;
	private	IVolumeAccessor						volumeAccessor;
	private	WindowState							mainWindowState;
	private	Map<String, Double>					tableViewColumnWidths;
	private	Stage								primaryStage;
	private	DirectoryPane						directoryPane;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Initialise logger and open log file
		LoggerUtils.openLogger(LOG_THRESHOLD, LOG_PARAMS,
							   AppAuxDirectory.resolve(NAME_KEY, Fat32ManagerApp.class, LOG_FILENAME),
							   LOG_NUM_RETAINED_LINES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Fat32ManagerApp()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void main(
		String[]	args)
	{
		launch(args);
	}

	//------------------------------------------------------------------

	public static Fat32ManagerApp instance()
	{
		return instance;
	}

	//------------------------------------------------------------------

	public static void executeTask(
		Runnable	task)
	{
		ExecutorService executor = Executors.newSingleThreadExecutor(runnable ->
				new Thread(runnable, NAME_KEY + "-" + ++threadIndex));
		executor.execute(task);
		executor.shutdown();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler.
	 *
	 * @return the delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler.
	 */

	private static int getWindowShownDelay()
	{
		int delay = OsUtils.isWindows() ? WINDOW_SHOWN_DELAY_WINDOWS : WINDOW_SHOWN_DELAY;
		String value = System.getProperty(SystemPropertyKey.WINDOW_SHOWN_DELAY);
		if (value != null)
		{
			try
			{
				delay = Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		return delay;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public void init()
	{
		instance = this;
	}

	//------------------------------------------------------------------

	@Override
	public void start(
		Stage	primaryStage)
	{
		// Make main window invisible until it is shown
		primaryStage.setOpacity(0.0);

		// Log stack trace of uncaught exception
		if (ClassUtils.isFromJar(getClass()))
		{
			Thread.setDefaultUncaughtExceptionHandler((thread, exception) ->
			{
				try
				{
					Logger.INSTANCE.error(exception);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			});
		}

		// Initialise instance variables
		preferences = new Preferences();
		formatParams = new FormatParams();
		volume = new SimpleObjectProperty<>();
		mainWindowState = new WindowState(false, true);
		tableViewColumnWidths = new LinkedHashMap<>();
		this.primaryStage = primaryStage;

		// Write 'starting' message to log
		StringBuilder buffer = new StringBuilder(256);
		buffer.append(STARTING_STR);
		buffer.append(' ');
		buffer.append(SHORT_NAME);
		buffer.append(" : ");
		buffer.append(PID_STR);
		buffer.append(ProcessHandle.current().pid());

		// Write command-line arguments to log
		List<String> args = getParameters().getRaw();
		if (!args.isEmpty())
		{
			for (int i = 0; i < args.size(); i++)
			{
				buffer.append('\n');
				buffer.append(ARGS1_STR);
				buffer.append(i);
				buffer.append(ARGS2_STR);
				buffer.append(args.get(i));
			}
		}
		Logger logger = Logger.INSTANCE;
		logger.saveParams();
		logger.setPrefix("-".repeat(64));
		logger.info(buffer.toString());
		logger.restoreParams();

		// Write Java version to log
		logger.info(JAVA_VERSION_STR + " " + System.getProperty("java.version"));

		// Read build properties and initialise version string
		try
		{
			buildProperties =
					new ResourceProperties(ResourceUtils.normalisedPathname(getClass(), BUILD_PROPERTIES_FILENAME));
			versionStr = BuildUtils.versionString(getClass(), buildProperties);
		}
		catch (LocationException e)
		{
			e.printStackTrace();
		}

		// Handle volume change
		volume.addListener((observable, oldVolume, volume) ->
		{
			// Reset sector/cluster-view dialog
			SectorClusterViewDialog.reset();

			// Update user interface
			SceneUtils.runOnFxApplicationThread(() ->
			{
				// Case: no volume
				if (volume == null)
				{
					// Update window title
					primaryStage.setTitle(LONG_NAME + " " + versionStr);

					// Update table view
					MainDirectoryTableView tableView = getTableView();
					tableView.clearHistory();
					tableView.setDirectory(null);

					// Disable control pane of directory pane
					directoryPane.getControlPane().setDisable(true);
				}

				// Case: volume
				else
				{
					// Update window title
					primaryStage.setTitle(LONG_NAME + " - " + Utils.volumeDisplayName(volume));
				}
			});
		});

		// Create container for local variables
		class Vars
		{
			Configuration	config;
			BaseException	configException;
		}
		Vars vars = new Vars();

		// Read configuration file and decode configuration
		try
		{
			// Initialise configuration
			vars.config = new Configuration();

			// Read and decode configuration
			if (!AppConfig.noConfigFile())
			{
				// Read configuration file
				vars.config.read();

				// Decode configuration
				decodeConfig(vars.config.getConfig());
			}
		}
		catch (BaseException e)
		{
			vars.configException = e;
		}

		// Get style manager
		StyleManager styleManager = StyleManager.INSTANCE;

		// Select theme from system property
		String themeId = System.getProperty(StyleManager.SystemPropertyKey.THEME);
		if (!StringUtils.isNullOrEmpty(themeId))
			styleManager.selectThemeOrDefault(themeId);

		// Set ID and style-sheet filename on style manager
		if (Boolean.getBoolean(SystemPropertyKey.USE_STYLE_SHEET_FILE))
		{
			styleManager.setId(getClass().getSimpleName());
			styleManager.setStyleSheetFilename(STYLE_SHEET_FILENAME);
		}

		// Update images
		ImageData.updateImages();

		// Create directory pane
		directoryPane = new DirectoryPane();
		directoryPane.getControlPane().setDisable(true);
		if (vars.config != null)
		{
			MainDirectoryTableView tableView = getTableView();
			tableView.setColumns(tableViewColumnWidths);
			tableView.setShowSpecialDirectories(preferences.isShowSpecialDirectories());
		}

		// Create main pane
		StackPane mainPane = new StackPane(directoryPane);
		VBox.setVgrow(mainPane, Priority.ALWAYS);

		// Create scene
		Scene scene = new Scene(new VBox(createMenuBar(), mainPane));

		// Add accelerators to scene
		scene.getAccelerators().put(KEY_COMBO_OPEN_PREVIOUS, getTableView()::openPreviousDirectory);
		scene.getAccelerators().put(KEY_COMBO_OPEN_NEXT,     getTableView()::openNextDirectory);
		scene.getAccelerators().put(KEY_COMBO_OPEN_PARENT,   getTableView()::openParentDirectory);

		// Add style sheet to scene
		styleManager.addStyleSheet(scene);

		// Update images of image buttons when theme changes
		StyleManager.INSTANCE.themeProperty().addListener(observable ->
		{
			ImageData.updateImages();
			ImageDataButton.updateButtons();
		});

		// Set properties of main window
		primaryStage.getIcons().addAll(Images.APP_ICON_IMAGES);
		primaryStage.setTitle(LONG_NAME + " " + versionStr);

		// Set scene on main window
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();

		// When main window is shown, set its size and location after a delay
		primaryStage.setOnShown(event ->
		{
			// Set size and location of main window after a delay
			ExecUtils.afterDelay(getWindowShownDelay(), () ->
			{
				// Get size of window from saved state
				Dimension2D size = mainWindowState.getSize();

				// Set size of window
				if (size != null)
				{
					primaryStage.setWidth(size.getWidth());
					primaryStage.setHeight(size.getHeight());
				}

				// Get location of window from saved state
				Point2D location = mainWindowState.getLocation();

				// Invalidate location if top centre of window is not within a screen
				double width = primaryStage.getWidth();
				if ((location != null)
						&& !SceneUtils.isWithinScreen(location.getX() + 0.5 * width, location.getY(), SCREEN_MARGINS))
					location = null;

				// If there is no location, centre window within primary screen
				if (location == null)
					location = SceneUtils.centreInScreen(width, primaryStage.getHeight());

				// Set location of window
				primaryStage.setX(location.getX());
				primaryStage.setY(location.getY());

				// Perform remaining initialisation after a delay
				ExecUtils.afterDelay(WINDOW_VISIBLE_DELAY, () ->
				{
					// Make window visible
					primaryStage.setOpacity(1.0);

					// Report any configuration error
					if (vars.configException != null)
					{
						Utils.showErrorMessage(primaryStage, SHORT_NAME + " : " + CONFIG_ERROR_STR,
											   vars.configException);
					}

					// Perform remaining initialisation
					initialise();
				});
			});
		});

		// Write configuration file when main window is closed
		if (vars.config != null)
		{
			primaryStage.setOnHiding(event ->
			{
				// Update state of main window
				mainWindowState.restoreAndUpdate(primaryStage);

				// Write configuration
				if (vars.config.canWrite())
				{
					try
					{
						// Encode configuration
						encodeConfig(vars.config.getConfig());

						// Write configuration file
						vars.config.write();
					}
					catch (FileException e)
					{
						// Display error message in dialog
						Utils.showErrorMessage(primaryStage, SHORT_NAME + " : " + CONFIG_ERROR_STR, e);
					}
				}
			});
		}

		// Display main window
		primaryStage.show();
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void stop()
	{
		// Write 'terminating' message to log
		Logger.INSTANCE.info(TERMINATING_STR + " " + SHORT_NAME);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void invalidateVolume()
	{
		// Close volume
		try
		{
			Fat32Volume volume = getVolume();
			if (volume != null)
				volume.close();
		}
		catch (VolumeException e)
		{
			// ignore
		}

		// Invalidate volume
		volume.set(null);
	}

	//------------------------------------------------------------------

	public IVolumeAccessor getVolumeAccessor()
	{
		return volumeAccessor;
	}

	//------------------------------------------------------------------

	private Fat32Volume getVolume()
	{
		return volume.get();
	}

	//------------------------------------------------------------------

	private MainDirectoryTableView getTableView()
	{
		return directoryPane.getTableView();
	}

	//------------------------------------------------------------------

	private Fat32Directory getDirectory()
	{
		return directoryPane.getTableView().getDirectory();
	}

	//------------------------------------------------------------------

	private void showMessageDialog(
		String			title,
		String			message,
		MessageIcon32	icon)
	{
		NotificationDialog.show(primaryStage, title, icon.get(), message);
	}

	//------------------------------------------------------------------

	private void encodeConfig(
		MapNode	rootNode)
	{
		// Clear properties
		rootNode.clear();

		// Encode theme ID
		String themeId = StyleManager.INSTANCE.getThemeId();
		if (themeId != null)
			rootNode.addMap(PropertyKey.APPEARANCE).addString(PropertyKey.THEME, themeId);

		// Encode state of main window
		MapNode mainWindowNode = mainWindowState.encodeTree();
		rootNode.add(PropertyKey.MAIN_WINDOW, mainWindowNode);

		// Create view node
		MapNode viewNode = mainWindowNode.addMap(PropertyKey.VIEW);

		// Encode column-header pop-up delay
		viewNode.addInt(PropertyKey.COLUMN_HEADER_POP_UP_DELAY, preferences.getColumnHeaderPopUpDelay());

		// Encode 'show special directories' flag
		viewNode.addBoolean(PropertyKey.SHOW_SPECIAL_DIRECTORIES, preferences.isShowSpecialDirectories());

		// Encode widths of columns of directory table
		MapNode columnsNode = new MapNode();
		for (TableColumn<Fat32Directory.Entry, ?> column : getTableView().getColumns())
			columnsNode.addDouble(column.getId(), column.getWidth());
		if (!columnsNode.isEmpty())
			viewNode.add(PropertyKey.COLUMN_WIDTHS, columnsNode);

		// Encode state of deleted-entries dialog
		MapNode deletedEntriesDialogNode = DeletedEntriesDialog.encodeState();
		if (!deletedEntriesDialogNode.isEmpty())
			rootNode.add(PropertyKey.DELETED_ENTRIES_DIALOG, deletedEntriesDialogNode);

		// Encode state of sector/cluster-view dialog
		MapNode sectorClusterViewDialogNode = SectorClusterViewDialog.encodeState();
		if (!sectorClusterViewDialogNode.isEmpty())
			rootNode.add(PropertyKey.SECTOR_CLUSTER_VIEW_DIALOG, sectorClusterViewDialogNode);

		// Create directory-entries node
		MapNode dirEntriesNode = rootNode.addMap(PropertyKey.DIRECTORY_ENTRIES);

		// Encode 'fix invalid dates and times' flag
		dirEntriesNode.addBoolean(PropertyKey.FIX_INVALID_DATES_TIMES, preferences.isFixDirEntryDatesTimes());

		// Encode format parameters
		formatParams = FormatDialog.getParams();
		formatParams.update(preferences);
		rootNode.add(PropertyKey.FORMAT, formatParams.encode());
	}

	//------------------------------------------------------------------

	/**
	 * Decodes the configuration of this application from the specified root node.
	 *
	 * @param rootNode
	 *          the root node of the configuration.
	 */

	private void decodeConfig(
		MapNode	rootNode)
	{
		// Decode theme ID
		String key = PropertyKey.APPEARANCE;
		if (rootNode.hasMap(key))
		{
			String themeId = rootNode.getMapNode(key).getString(PropertyKey.THEME, StyleManager.DEFAULT_THEME_ID);
			StyleManager.INSTANCE.selectThemeOrDefault(themeId);
		}

		// Decode properties of main window
		key = PropertyKey.MAIN_WINDOW;
		if (rootNode.hasMap(key))
		{
			// Decode state of main window
			MapNode mainWindowNode = rootNode.getMapNode(key);
			mainWindowState.decodeTree(mainWindowNode);

			// Decode view properties
			key = PropertyKey.VIEW;
			if (mainWindowNode.hasMap(key))
			{
				// Get view node
				MapNode viewNode = mainWindowNode.getMapNode(key);

				// Decode column-header pop-up delay
				preferences.setColumnHeaderPopUpDelay(
						viewNode.getInt(PropertyKey.COLUMN_HEADER_POP_UP_DELAY,
										DirectoryTableView.DEFAULT_HEADER_CELL_POP_UP_DELAY));

				// Decode 'show special directories' flag
				preferences.setShowSpecialDirectories(viewNode.getBoolean(PropertyKey.SHOW_SPECIAL_DIRECTORIES, false));

				// Decode widths of columns of directory table
				tableViewColumnWidths.clear();
				key = PropertyKey.COLUMN_WIDTHS;
				if (viewNode.hasMap(key))
				{
					MapNode columnsNode = viewNode.getMapNode(key);
					for (String key0 : columnsNode.getKeys())
						tableViewColumnWidths.put(key0, columnsNode.getDouble(key0));
				}
			}
		}

		// Decode state of deleted-entries dialog
		key = PropertyKey.DELETED_ENTRIES_DIALOG;
		if (rootNode.hasMap(key))
			DeletedEntriesDialog.decodeState(rootNode.getMapNode(key));

		// Decode state of sector/cluster-view dialog
		key = PropertyKey.SECTOR_CLUSTER_VIEW_DIALOG;
		if (rootNode.hasMap(key))
			SectorClusterViewDialog.decodeState(rootNode.getMapNode(key));

		// Decode properties relating to directory entries
		key = PropertyKey.DIRECTORY_ENTRIES;
		if (rootNode.hasMap(key))
		{
			// Get directory-entries node
			MapNode dirEntriesNode = rootNode.getMapNode(key);

			// Decode 'fix invalid dates and times' flag
			preferences.setFixDirEntryDatesTimes(dirEntriesNode.getBoolean(PropertyKey.FIX_INVALID_DATES_TIMES, false));
		}

		// Decode format parameters
		key = PropertyKey.FORMAT;
		if (rootNode.hasMap(key))
		{
			formatParams.decode(rootNode.getMapNode(key));
			preferences.update(formatParams);
		}
	}

	//------------------------------------------------------------------

	private void initialise()
	{
		// Get name of volume-accessor class from system property
		String accessorClassName = System.getProperty(SystemPropertyKey.VOLUME_ACCESSOR);

		// If there is a volume-accessor class name, create instance of class and set volume accessor to it
		if (accessorClassName != null)
		{
			try
			{
				try
				{
					// Get class from its name
					Class<?> cls = Class.forName(accessorClassName);

					// Test for volume accessor
					if (!IVolumeAccessor.class.isAssignableFrom(cls))
						throw new BaseException(ErrorMsg.INVALID_VOLUME_ACCESSOR_CLASS, accessorClassName);

					// Create instance of volume accessor
					volumeAccessor = (IVolumeAccessor)cls.getDeclaredConstructor().newInstance();
				}
				catch (ClassNotFoundException e)
				{
					throw new BaseException(ErrorMsg.VOLUME_ACCESSOR_CLASS_NOT_FOUND, accessorClassName);
				}
			}
			catch (Throwable e)
			{
				Utils.showErrorMessage(primaryStage, SHORT_NAME + " : " + FATAL_ERROR_STR,
									   ErrorMsg.FAILED_TO_CREATE_VOLUME_ACCESSOR, e);
				Platform.exit();
			}
		}

		// Load library of native methods
		if (volumeAccessor == null)
		{
			volumeAccessor = DriveIO.ACCESSOR;
			String libraryName = System.mapLibraryName(DriveIO.NAME);
			try
			{
				if (!DriveIO.initLibrary(ResourceUtils.packagePathname(getClass()) + "/" + LIBRARY_DIRECTORY_NAME,
										 AppAuxDirectory.resolve(NAME_KEY, getClass(), LIBRARY_DIRECTORY_NAME)))
				{
					Utils.showErrorMessage(primaryStage, SHORT_NAME + " : " + FATAL_ERROR_STR,
										   String.format(ErrorMsg.INVALID_LIBRARY, libraryName));
					Platform.exit();
				}
			}
			catch (Throwable e)
			{
				Utils.showErrorMessage(primaryStage, SHORT_NAME + " : " + FATAL_ERROR_STR,
									   String.format(ErrorMsg.FAILED_TO_LOAD_LIBRARY, libraryName), e);
				Platform.exit();
			}
		}

		// Set format parameters on format dialog
		FormatDialog.setParams(formatParams);

		// Start periodically checking current volume
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable ->
				DaemonFactory.create(getClass().getSimpleName() + "-" + CHECK_VOLUME_THREAD_NAME_SUFFIX, runnable));
		executor.scheduleWithFixedDelay(() ->
		{
			Fat32Volume volume = getVolume();
			if ((volume != null) && !volume.isOpen())
			{
				String name = volume.getName();
				if (!volumeAccessor.isVolumeMounted(name))
				{
					Platform.runLater(() ->
					{
						// Invalidate volume
						invalidateVolume();

						// Display error message
						Utils.showErrorMessage(primaryStage, VOLUME_STR + " " + Utils.volumeDisplayName(name),
											   String.format(ErrorMsg.VOLUME_NOT_MOUNTED, name));
					});
				}
			}
		},
		CHECK_VOLUME_INTERVAL, CHECK_VOLUME_INTERVAL, TimeUnit.MILLISECONDS);

		// Open volume that was specified on command line
		List<String> args = getParameters().getRaw();
		if (!args.isEmpty())
			openVolume(args.get(0), null);
	}

	//------------------------------------------------------------------

	private MenuBar createMenuBar()
	{
		// Create menu bar
		MenuBar menuBar = new MenuBar();
		menuBar.setPadding(Insets.EMPTY);

		// Create menu: volume
		Menu menu = new Menu(VOLUME_STR);
		menuBar.getMenus().add(menu);

		// Add menu item: open
		MenuItem menuItem = new MenuItem(OPEN_STR + ELLIPSIS_STR);
		menuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		menuItem.setOnAction(event -> onOpenVolume());
		menu.getItems().add(menuItem);

		// Add separator
		menu.getItems().add(new SeparatorMenuItem());

		// Add menu item: close
		menuItem = new MenuItem(CLOSE_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
		menuItem.setOnAction(event -> onCloseVolume());
		menu.getItems().add(menuItem);

		// Add separator
		menu.getItems().add(new SeparatorMenuItem());

		// Add menu item: view sectors
		menuItem = new MenuItem(VIEW_SECTORS_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setAccelerator(new KeyCodeCombination(KeyCode.F9));
		menuItem.setOnAction(event -> SectorClusterViewDialog.showSector(primaryStage, getVolume(), -1));
		menu.getItems().add(menuItem);

		// Add menu item: view clusters
		menuItem = new MenuItem(VIEW_CLUSTERS_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setAccelerator(new KeyCodeCombination(KeyCode.F9, KeyCombination.CONTROL_DOWN));
		menuItem.setOnAction(event -> SectorClusterViewDialog.showCluster(primaryStage, getVolume(), -1));
		menu.getItems().add(menuItem);

		// Add separator
		menu.getItems().add(new SeparatorMenuItem());

		// Add menu item: validate cluster chains
		menuItem = new MenuItem(VALIDATE_CLUSTER_CHAINS_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setOnAction(event -> onValidateClusterChains());
		menu.getItems().add(menuItem);

		// Add menu item: erase unused clusters
		menuItem = new MenuItem(ERASE_UNUSED_CLUSTERS_STR + ELLIPSIS_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setOnAction(event -> onEraseUnusedClusters());
		menu.getItems().add(menuItem);

		// Add separator
		menu.getItems().add(new SeparatorMenuItem());

		// Add menu item: properties
		menuItem = new MenuItem(PROPERTIES_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setAccelerator(new KeyCodeCombination(KeyCode.F12));
		menuItem.setOnAction(event -> onShowProperties());
		menu.getItems().add(menuItem);

		// Add separator
		menu.getItems().add(new SeparatorMenuItem());

		// Add menu item: exit
		menuItem = new MenuItem(EXIT_STR);
		menuItem.setOnAction(event -> Platform.exit());
		menu.getItems().add(menuItem);

		// Create menu: edit
		menu = new Menu(EDIT_STR);
		menuBar.getMenus().add(menu);

		// Add menu item: preferences
		menuItem = new MenuItem(PREFERENCES_STR);
		menuItem.setOnAction(event -> onEditPreferences());
		menu.getItems().add(menuItem);

		// Create menu: directory
		menu = new Menu(DIRECTORY_STR);
		menuBar.getMenus().add(menu);

		// Add menu item: sort by name
		menuItem = new MenuItem(SORT_BY_NAME_STR + ELLIPSIS_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setOnAction(event -> onSortByName());
		menu.getItems().add(menuItem);

		// Add separator
		menu.getItems().add(new SeparatorMenuItem());

		// Add menu item: view sector
		menuItem = new MenuItem(VIEW_SECTOR_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setOnAction(event ->
		{
			Fat32Volume volume0 = getVolume();
			Fat32Directory directory = getDirectory();
			Fat32Directory.Entry entry = directory.getEntryInParent();
			if (entry == null)
				SectorClusterViewDialog.showSector(primaryStage, volume0, directory.getSectorIndex());
			else
			{
				try
				{
					SectorClusterViewDialog.showSector(primaryStage, volume0, volume0.getFat().indexFinder(entry));
				}
				catch (VolumeException e)
				{
					Utils.showErrorMessage(primaryStage, VIEW_SECTOR_STR, e);
				}
			}
		});
		menu.getItems().add(menuItem);

		// Add menu item: view cluster
		menuItem = new MenuItem(VIEW_CLUSTER_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setOnAction(event ->
		{
			Fat32Volume volume0 = getVolume();
			Fat32Directory directory = getDirectory();
			Fat32Directory.Entry entry = directory.getEntryInParent();
			if (entry == null)
				SectorClusterViewDialog.showCluster(primaryStage, volume0, directory.getClusterIndex());
			else
			{
				try
				{
					SectorClusterViewDialog.showCluster(primaryStage, volume0, volume0.getFat().indexFinder(entry));
				}
				catch (VolumeException e)
				{
					Utils.showErrorMessage(primaryStage, VIEW_CLUSTER_STR, e);
				}
			}
		});
		menu.getItems().add(menuItem);

		// Add separator
		menu.getItems().add(new SeparatorMenuItem());

		// Add menu item: view deleted entries
		menuItem = new MenuItem(VIEW_DELETED_ENTRIES_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setOnAction(event -> onViewDeletedEntries());
		menu.getItems().add(menuItem);

		// Add separator
		menu.getItems().add(new SeparatorMenuItem());

		// Add menu item: defragment files
		menuItem = new MenuItem(DEFRAGMENT_FILES_STR + ELLIPSIS_STR);
		menuItem.disableProperty().bind(volume.isNull());
		menuItem.setOnAction(event -> onDefragmentFiles());
		menu.getItems().add(menuItem);

		// Create menu: view
		menu = new Menu(VIEW_STR);
		menuBar.getMenus().add(menu);

		// Add menu item: select table columns
		menuItem = new MenuItem(SELECT_TABLE_COLUMNS_STR + ELLIPSIS_STR);
		menuItem.setOnAction(event -> onSelectTableColumns());
		menu.getItems().add(menuItem);

		// Create menu: tools
		menu = new Menu(TOOLS_STR);
		menuBar.getMenus().add(menu);

		// Add menu item: format
		menuItem = new MenuItem(FORMAT_VOLUME_STR + ELLIPSIS_STR);
		menuItem.setOnAction(event -> onFormat());
		menu.getItems().add(menuItem);

		// Return menu bar
		return menuBar;
	}

	//------------------------------------------------------------------

	private void openVolume(
		String		name,
		IProcedure0	completionHandler)
	{
		// Log title of task
		String title = OPEN_VOLUME_STR + " : " + name;
		Logger.INSTANCE.info(title);

		// Declare record for result of task
		record Result(
			Fat32Volume		volume,
			List<String>	messages)
		{ }

		// Create task to open volume
		Task<Result> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(title);
				updateMessage(OPENING_STR + MessageConstants.SPACE_SEPARATOR + name);
				updateProgress(-1, 1);
			}

			@Override
			protected Result call()
				throws Exception
			{
				// Initialise list of messages
				List<String> messages = new ArrayList<>();

				// Open volume
				Fat32Volume volume = null;
				try
				{
					// Create volume
					volume = new Fat32Volume(name, volumeAccessor);
					volume.setFixDirEntryDatesTimes(preferences.isFixDirEntryDatesTimes());

					// Initialise volume
					updateMessage(READING_FATS_STR);
					try
					{
						volume.init();
					}
					catch (VolumeException e)
					{
						invalidateVolume();
						throw e;
					}

					// Read root directory
					updateMessage(READING_ROOT_DIRECTORY_STR);
					volume.getRootDir().read(messages);

					// Return result
					return new Result(volume, messages);
				}
				finally
				{
					try
					{
						if ((volume != null) && volume.isOpen())
							volume.close();
					}
					catch (VolumeException e)
					{
						// ignore
					}
				}
			}

			@Override
			protected void succeeded()
			{
				// Get result
				Result result = getValue();

				// Update instance variable
				volume.set(result.volume);

				// Enable control pane of directory pane
				directoryPane.getControlPane().setDisable(false);

				// Clear history of table view
				getTableView().clearHistory();

				// Set root directory on table view
				Fat32Directory directory = result.volume.getRootDir();
				getTableView().setDirectory(directory);

				// Add directory to history of table view
				getTableView().addToHistory(directory);

				// Display messages in dialog
				if (!result.messages.isEmpty())
				{
					TextAreaDialog.show(primaryStage, DialogKey.OPEN_VOLUME, getTitle() + " : " + MESSAGES_STR,
										String.join("\n\n", result.messages));
				}

				// Call completion handler
				if (completionHandler != null)
					completionHandler.invoke();
			}

			@Override
			protected void failed()
			{
				// Display error message in dialog
				showErrorMessage(primaryStage);
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(primaryStage, task);

		// Execute task on background thread
		executeTask(task);
	}

	//------------------------------------------------------------------

	private void formatVolume1(
		String	name)
	{
		// Log title of task
		String title = FORMAT_STR + " " + name;
		Logger.INSTANCE.info(title);

		// Declare record for result of task
		record Result(
			int	bytesPerSector,
			int	startSector,
			int	numSectors)
		{ }

		// Create task to get number of sectors and number of hidden sectors
		Task<Result> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(title);
				updateMessage(DETERMINING_NUM_SECTORS_STR);
				updateProgress(-1, 1);
			}

			@Override
			protected Result call()
				throws Exception
			{
				// Get volume information
				BlockVolume blockVolume = new BlockVolume(name, volumeAccessor);
				blockVolume.updateInfo();

				// Test for supported sector size
				int bytesPerSector = blockVolume.getBytesPerSector();
				if (!Fat32Volume.isSupportedSectorSize(bytesPerSector))
					throw new VolumeException(ErrorMsg.UNSUPPORTED_SECTOR_SIZE, bytesPerSector);

				// Get start sector
				long startSector0 = blockVolume.getStartSector();
				if (startSector0 > Integer.MAX_VALUE)
					throw new VolumeException(ErrorMsg.START_SECTOR_OUT_OF_BOUNDS);
				int startSector = (int)startSector0;

				// Get number of sectors
				long numSectors0 = blockVolume.getNumSectors();
				if (numSectors0 < preferences.getFormatMinNumSectors())
					throw new VolumeException(ErrorMsg.TOO_FEW_SECTORS);
				if (numSectors0 > Fat32Volume.FORMAT_MAX_NUM_SECTORS)
					throw new VolumeException(ErrorMsg.TOO_MANY_SECTORS);
				int numSectors = (int)numSectors0;

				// Return result
				return new Result(bytesPerSector, startSector, numSectors);
			}

			@Override
			protected void succeeded()
			{
				// Get result of task
				Result result = getValue();

				// Format volume
				formatVolume2(name, result.bytesPerSector, result.startSector, result.numSectors);
			}

			@Override
			protected void failed()
			{
				// Display error message in dialog
				showErrorMessage(primaryStage);
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(primaryStage, task);

		// Execute task on background thread
		executeTask(task);
	}

	//------------------------------------------------------------------

	private void formatVolume2(
		String	name,
		int		bytesPerSector,
		int		startSector,
		int		numSectors)
	{
		// Get title of task
		String title = FORMAT_STR + " " + name;

		// Display dialog for format parameters
		FormatDialog.Result result = FormatDialog.show(primaryStage, title, bytesPerSector, numSectors);
		if (result == null)
			return;

		// Get format parameters
		Fat32Volume.FormatParams params =
				Fat32Volume.getFormatParams(result.getBytesPerSector(), numSectors, result.getMinNumReservedSectors(),
											result.getSectorsPerCluster(), result.getClusterAlignment(),
											result.isAlignFatsToClusters());

		// if format parameters are invalid, display error dialog and stop
		if (params == null)
		{
			Utils.showErrorMessage(primaryStage, title, ErrorMsg.INVALID_FORMAT_PARAMS);
			return;
		}

		// Log title of task
		Logger.INSTANCE.info(title);

		// Create task to format volume
		Task<Void> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(title);
			}

			@Override
			protected Void call()
				throws Exception
			{
				// Format volume
				Fat32Volume.format(name, result.getVolumeId(), result.getVolumeLabel(), result.getFormatterName(),
								   bytesPerSector, startSector, numSectors, params.minNumReservedSectors(),
								   result.getSectorsPerCluster(), params.sectorsPerFat(), volumeAccessor,
								   createTaskStatus());

				// Return nothing
				return null;
			}

			@Override
			protected void succeeded()
			{
				// Open volume
				openVolume(name, () ->
				{
					// Calculate capacity of volume
					Fat32Volume volume = getVolume();
					long capacity = (long)volume.getNumUnusedClusters() * volume.getBytesPerCluster();

					// Show capacity in dialog
					showMessageDialog(getTitle(), String.format(FORMATTED_STR, capacity), MessageIcon32.INFORMATION);
				});
			}

			@Override
			protected void failed()
			{
				// Display error message in dialog
				showErrorMessage(primaryStage);
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(primaryStage, task);

		// Execute task on background thread
		executeTask(task);
	}

	//------------------------------------------------------------------

	private void defragmentFiles(
		Iterator<Fat32Directory.Entry>	entryIt,
		List<Fat32Directory.Entry>		notEnoughSpaceEntries,
		int								numClustersProcessed,
		int 							totalNumClusters)
	{
		// Create container for local variables
		class Vars
		{
			int	numClustersProcessed;
		}
		Vars vars = new Vars();
		vars.numClustersProcessed = numClustersProcessed;

		// Log description of task
		Logger.INSTANCE.info(DEFRAGMENTING_FILES_STR + " : " + numClustersProcessed + "/" + totalNumClusters);

		// Create procedure that will be invoked when task is finished
		IProcedure0 onTaskFinished = () ->
		{
			// Refresh table view
			getTableView().refresh();

			// Display list of files that were not defragmented because there was not enough space
			if (!notEnoughSpaceEntries.isEmpty())
			{
				// Create list of pathnames from directory entries
				List<String> pathnames =
						notEnoughSpaceEntries.stream().map(entry -> entry.getPathname()).toList();

				// Sort pathnames
				Collections.sort(pathnames, (pathname1, pathname2) ->
				{
					// Split first pathname into its elements
					List<String> elements1 = StringUtils.split(pathname1, Fat32Directory.NAME_SEPARATOR_CHAR);
					int numElements1 = elements1.size();

					// Split second pathname into its elements
					List<String> elements2 = StringUtils.split(pathname2, Fat32Directory.NAME_SEPARATOR_CHAR);
					int numElements2 = elements2.size();

					// Compare number of elements
					int result = Integer.compare(numElements1, numElements2);

					// If pathnames have the same number of elements, compare elements, ignoring letter case
					if (result == 0)
					{
						for (int i = 0; i < numElements1; i++)
						{
							result = elements1.get(i).compareToIgnoreCase(elements2.get(i));
							if (result != 0)
								break;
						}
					}

					// Return result
					return result;
				});

				// Display pathnames in dialog
				MessageListDialog.show(primaryStage, DEFRAGMENT_FILES_STR, MessageIcon32.ALERT.get(),
									   NOT_ENOUGH_SPACE_STR, pathnames, true, ButtonInfo.of(HPos.RIGHT, OK_STR));
			}
		};

		// Create task to defragment files
		Task<Void> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(DEFRAGMENT_FILES_STR);
				updateProgress(-1, 1);
			}

			@Override
			protected Void call()
				throws Exception
			{
				// Defragment files
				while (entryIt.hasNext())
				{
					// Test whether task has been cancelled
					if (isCancelled())
						break;

					// Get directory entry
					Fat32Directory.Entry entry = entryIt.next();

					// Defragment file
					try
					{
						Fat32Volume.DefragStatus status =
								getVolume().defragmentFile(entry, vars.numClustersProcessed, totalNumClusters,
														   createTaskStatus());
						if (status == Fat32Volume.DefragStatus.NOT_ENOUGH_SPACE)
							notEnoughSpaceEntries.add(entry);
					}
					catch (VolumeException e)
					{
						throw new LocationException(e.getMessage(), e.getCause(), entry.getPathname());
					}
					finally
					{
						vars.numClustersProcessed += entry.getNumClusters();
					}
				}

				// If task has been cancelled, change state to 'cancelled'
				hardCancel(false);

				// Return nothing
				return null;
			}

			@Override
			protected void succeeded()
			{
				// Invoke post-task procedure
				onTaskFinished.invoke();
			}

			@Override
			protected void failed()
			{
				// If there are more files to process, display error and ask whether to continue ...
				if (entryIt.hasNext())
				{
					// If 'continue' was chosen, continue with next file
					if (ExceptionDialog.show(primaryStage, getTitle(), MessageIcon32.ERROR, getException(),
											 ButtonInfo.of(HPos.RIGHT, CONTINUE_STR, CANCEL_STR)) == 0)
						defragmentFiles(entryIt, notEnoughSpaceEntries, vars.numClustersProcessed, totalNumClusters);

					// ... otherwise, invoke post-task procedure
					else
						onTaskFinished.invoke();
				}

				// ... otherwise, display error
				else
				{
					// Display error message in dialog
					showErrorMessage(primaryStage);

					// Invoke post-task procedure
					onTaskFinished.invoke();
				}
			}

			@Override
			protected void cancelled()
			{
				// Invoke post-task procedure
				onTaskFinished.invoke();
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(primaryStage, task, SimpleProgressDialog.CancelMode.NO_INTERRUPT);

		// Execute task on background thread
		executeTask(task);
	}

	//------------------------------------------------------------------

	private void onOpenVolume()
	{
		try
		{
			// Get names of volumes
			List<String> names = volumeAccessor.getVolumeNames();

			// Test for volumes
			if (names.isEmpty())
			{
				NotificationDialog.show(primaryStage, OPEN_VOLUME_STR, MessageIcon32.ALERT.get(),
										NO_VOLUMES_TO_OPEN_STR);
				return;
			}

			// Display dialog for selecting volume
			Fat32Volume volume = getVolume();
			String name = (volume == null) ? null : volume.getName();
			name = VolumeSelectionDialog.show(primaryStage, OPEN_VOLUME_STR, names, name);

			// Open volume
			if (name != null)
				openVolume(name, null);
		}
		catch (VolumeException e)
		{
			Utils.showErrorMessage(primaryStage, OPEN_VOLUME_STR, e);
		}
	}

	//------------------------------------------------------------------

	private void onCloseVolume()
	{
		invalidateVolume();
	}

	//------------------------------------------------------------------

	private void onValidateClusterChains()
	{
		// Log title of task
		String title = VALIDATE_CLUSTER_CHAINS_STR;
		Logger.INSTANCE.info(title);

		// Declare record for result of task
		record Result(
			int									numClusters,
			List<Fat32Volume.InvalidCluster>	invalidClusters)
		{ }

		// Create task to validate cluster chains
		Task<Result> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(title);
				updateProgress(-1, 1);
			}

			@Override
			protected Result call()
				throws Exception
			{
				// Initialise result
				List<Fat32Volume.InvalidCluster> invalidClusters = new ArrayList<>();

				// Create list of invalid entries in cluster chains
				Fat32Volume volume = getVolume();
				int numClusters =
						volume.validateClusterChains(volume.getRootDir(), invalidClusters, createTaskStatus());

				// If task has been cancelled, change state to 'cancelled'
				hardCancel(false);

				// Return result
				return new Result(numClusters, invalidClusters);
			}

			@Override
			protected void succeeded()
			{
				// Get result
				Result result = getValue();

				// If there were no invalid clusters, display 'all chains valid' message ...
				if (result.invalidClusters.isEmpty())
				{
					// Display 'all chains valid' message
					showMessageDialog(getTitle(), CLUSTER_CHAINS_VALID_STR, MessageIcon32.INFORMATION);

					// Display message if total number of clusters differs from expected value
					Fat32Volume volume = getVolume();
					int expectedNumClusters =
							volume.getNumClusters() - volume.getNumUnusedClusters() - volume.getRootDirNumClusters();
					if (result.numClusters != expectedNumClusters)
					{
						showMessageDialog(
								getTitle(),
								String.format(UNEXPECTED_NUM_CLUSTERS_STR, result.numClusters, expectedNumClusters),
								MessageIcon32.WARNING);
					}
				}

				// ... otherwise, display list of invalid clusters
				else
					InvalidClusterDialog.show(primaryStage, INVALID_CLUSTERS_STR, result.invalidClusters);
			}

			@Override
			protected void failed()
			{
				// Display error message in dialog
				showErrorMessage(primaryStage);
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(primaryStage, task, SimpleProgressDialog.CancelMode.NO_INTERRUPT);

		// Execute task on background thread
		executeTask(task);
	}

	//------------------------------------------------------------------

	private void onEraseUnusedClusters()
	{
		// Display dialog
		Integer fillerValue = EraseUnusedClustersDialog.show(primaryStage, ERASE_UNUSED_CLUSTERS_STR);

		// Erase unused clusters
		if (fillerValue != null)
		{
			// Log title of task
			String title = ERASE_UNUSED_CLUSTERS_STR;
			Logger.INSTANCE.info(title);

			// Create task to erase unused clusters
			Task<Void> task = new AbstractTask<>()
			{
				{
					// Initialise task
					updateTitle(title);
				}

				@Override
				protected Void call()
					throws Exception
				{
					// Erase unused clusters of volume
					getVolume().eraseUnusedClusters(fillerValue.byteValue(), createTaskStatus());

					// If task has been cancelled, change state to 'cancelled'
					hardCancel(false);

					// Return nothing
					return null;
				}

				@Override
				protected void succeeded()
				{
					showMessageDialog(getTitle(), UNUSED_CLUSTERS_ERASED_STR, MessageIcon32.INFORMATION);
				}

				@Override
				protected void failed()
				{
					// Display error message in dialog
					showErrorMessage(primaryStage);
				}
			};

			// Show progress of task in dialog
			new SimpleProgressDialog(primaryStage, task, SimpleProgressDialog.CancelMode.NO_INTERRUPT);

			// Execute task on background thread
			executeTask(task);
		}
	}

	//------------------------------------------------------------------

	private void onShowProperties()
	{
		Fat32Volume volume = getVolume();
		PropertiesPane.create()
				.verticalGap(4.0)
				.padding(new Insets(0.0, 2.0, 0.0, 2.0))
				.valueLabelPadding(new Insets(1.0, 6.0, 1.0, 6.0))
				.nameConverter(name -> name.chars().allMatch(ch -> Character.isUpperCase(ch))
																			? name
																			: StringUtils.firstCharToLowerCase(name))
				.properties1(volume.getProperties())
				.showDialog(primaryStage, DialogKey.VOLUME_PROPERTIES,
							PROPERTIES_STR + OF_STR + Utils.volumeDisplayName(volume));
	}

	//------------------------------------------------------------------

	private void onEditPreferences()
	{
		// Display dialog
		Preferences result = PreferencesDialog.show(primaryStage, preferences);

		// If dialog was accepted, update and apply preferences
		if (result != null)
		{
			// Update instance variable
			preferences = result;

			// Apply table-view preferences
			DirectoryTableView tableView = getTableView();
			tableView.setShowSpecialDirectories(preferences.isShowSpecialDirectories());
			tableView.setHeaderCellPopUpDelay(preferences.getColumnHeaderPopUpDelay());

			// Apply directory-entry preferences
			Fat32Volume volume = getVolume();
			if (volume != null)
				volume.setFixDirEntryDatesTimes(preferences.isFixDirEntryDatesTimes());
		}
	}

	//------------------------------------------------------------------

	private void onSortByName()
	{
		SortDialog.Result result = SortDialog.show(primaryStage, SORT_BY_NAME_STR);
		if (result != null)
		{
			if (result.preview()
					|| ConfirmationDialog.show(primaryStage, SORT_BY_NAME_STR, MessageIcon32.QUESTION.get(),
											   CONFIRM_SORT_STR, SORT_STR))
				getTableView().sortByName(result.preview(), result.recursive(), result.ignoreCase());
		}
	}

	//------------------------------------------------------------------

	private void onViewDeletedEntries()
	{
		// Log title of task
		String title = VIEW_DELETED_ENTRIES_STR;
		Logger.INSTANCE.info(title + " : " + getDirectory().getPathname());

		// Declare record for result of task
		record Result(
			List<Fat32Directory.Entry>	deletedEntries,
			List<String>				messages)
		{ }

		// Create task to find deleted entries in current directory
		Task<Result> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(title);
				updateMessage(SEARCHING_ENTRIES_STR);
				updateProgress(-1, 1);
			}

			@Override
			protected Result call()
				throws Exception
			{
				// Find deleted entries in current directory
				List<String> messages = new ArrayList<>();
				List<Fat32Directory.Entry> entries = getDirectory().findDeletedEntries(messages);

				// Return list of entries
				return new Result(entries, messages);
			}

			@Override
			protected void succeeded()
			{
				// Get result of task
				Result result = getValue();

				// Display deleted entries in dialog
				DeletedEntriesDialog dialog =
						new DeletedEntriesDialog(primaryStage, getDirectory(), result.deletedEntries);
				if (!result.messages.isEmpty())
				{
					dialog.setOnShown(event ->
					{
						TextAreaDialog.show(dialog, DialogKey.SHOW_DELETED_ENTRIES, getTitle() + " : " + MESSAGES_STR,
											String.join("\n\n", result.messages));
					});
				}
				dialog.showDialog();
			}

			@Override
			protected void failed()
			{
				// Display error message in dialog
				showErrorMessage(primaryStage);
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(primaryStage, task);

		// Execute task on background thread
		executeTask(task);
	}

	//------------------------------------------------------------------

	private void onDefragmentFiles()
	{
		// Display dialog for recursive operation
		Boolean recursive = DefragmentFilesDialog.show(primaryStage, DEFRAGMENT_FILES_STR);
		if (recursive == null)
			return;

		// Log title of task
		Logger.INSTANCE.info(DEFRAGMENT_FILES_STR + " : " + SEARCHING_FOR_FILES_STR);

		// Declare record for result of task
		record Result(
			List<Fat32Directory.Entry>			entries,
			List<Fat32Volume.InvalidCluster>	invalidClusters)
		{ }

		// Create task to create list of fragmented files
		Task<Result> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(DEFRAGMENT_FILES_STR);
				updateMessage(SEARCHING_FOR_FILES_STR);
				updateProgress(-1, 1);
			}

			@Override
			protected Result call()
				throws Exception
			{
				// Initialise list of directory entries for files
				List<Fat32Directory.Entry> entries = new ArrayList<>();

				// Get current volume
				Fat32Volume volume = getVolume();

				// Create procedure to add fragmented files to list of directory entries
				IProcedure1<Fat32Directory> addFiles = directory ->
				{
					for (Fat32Directory.Entry entry : directory.getEntries())
					{
						try
						{
							if (entry.isFile() && volume.isEntryFragmented(entry))
								entries.add(entry);
						}
						catch (VolumeException e)
						{
							entries.add(entry);
						}
					}
				};

				// Populate list of directory entries for files
				if (recursive)
				{
					TreeUtils.visitDepthFirst(getDirectory(), true, true, directory ->
					{
						addFiles.invoke(directory);
						return true;
					});
				}
				else
					addFiles.invoke(getDirectory());

				// Validate cluster chains
				List<Fat32Volume.InvalidCluster> invalidClusters = new ArrayList<>();
				volume.validateClusterChains(entries, invalidClusters, createTaskStatus());

				// Sort directory entries in descending order of number of clusters
				entries.sort(Comparator.comparing(Fat32Directory.Entry::getNumClusters).reversed());

				// Return result
				return new Result(entries, invalidClusters);
			}

			@Override
			protected void succeeded()
			{
				// Get result
				Result result = getValue();

				// Case: all clusters were valid
				if (result.invalidClusters.isEmpty())
				{
					// If no files, report and stop ...
					if (result.entries.isEmpty())
					{
						NotificationDialog.show(primaryStage, getTitle(), MessageIcon32.INFORMATION.get(),
												NO_FILES_TO_DEFRAGMENT_STR);
					}

					// ... otherwise, defragment files
					else
					{
						// Count total number of clusters in entries
						int totalNumClusters =
								result.entries.stream().mapToInt(Fat32Directory.Entry::getNumClusters).sum();

						// Defragment files
						defragmentFiles(result.entries.iterator(), new ArrayList<>(), 0, totalNumClusters);
					}
				}

				// Case: invalid clusters
				else
				{
					InvalidClusterDialog.show(primaryStage, getTitle() + " : " + INVALID_CLUSTERS_STR,
											  result.invalidClusters);
				}
			}

			@Override
			protected void failed()
			{
				// Display error message in dialog
				showErrorMessage(primaryStage);
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(primaryStage, task);

		// Execute task on background thread
		executeTask(task);
	}

	//------------------------------------------------------------------

	private void onSelectTableColumns()
	{
		getTableView().selectColumns();
	}

	//------------------------------------------------------------------

	private void onFormat()
	{
		// Create task to get names of volumes that are eligible to be formatted
		Task<List<String>> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(FORMAT_VOLUME_STR);
				updateMessage(SEARCHING_FOR_VOLUMES_STR);
				updateProgress(-1, 1);
			}

			@Override
			protected List<String> call()
				throws Exception
			{
				// Initialise list of volume names
				List<String> volumeNames = new ArrayList<>();

				// Get open volume
				Fat32Volume openVolume = getVolume();

				// Get names of eligible volumes
				for (String name : volumeAccessor.getVolumeNames())
				{
					if ((openVolume == null) || !name.equals(openVolume.getName()))
					{
						try
						{
							// Get volume information
							BlockVolume blockVolume = new BlockVolume(name, volumeAccessor);
							blockVolume.updateInfo();

							// If volume is eligible to be formatted, add its name to list
							if (!preferences.isFormatRemovableMediaOnly()
									|| (blockVolume.getMediumKind() == Volume.MediumKind.REMOVABLE))
								volumeNames.add(name);
						}
						catch (VolumeException e)
						{
							// ignore
						}
					}
				}

				// Return volume names
				return volumeNames;
			}

			@Override
			protected void succeeded()
			{
				// Get list of volume names
				List<String> volumeNames = getValue();

				// If no volume names, report and stop ...
				if (volumeNames.isEmpty())
				{
					NotificationDialog.show(primaryStage, getTitle(), MessageIcon32.ALERT.get(),
											NO_VOLUMES_TO_FORMAT_STR);
				}

				// ... otherwise, display dialog for selecting volume
				else
				{
					// Select volume
					String volumeName = VolumeSelectionDialog.show(primaryStage, getTitle(), volumeNames, null);

					// If volume was selected, proceed
					if (volumeName != null)
						formatVolume1(volumeName);
				}
			}

			@Override
			protected void failed()
			{
				// Display error message in dialog
				showErrorMessage(primaryStage);
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(primaryStage, task);

		// Execute task on background thread
		executeTask(task);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: CONFIGURATION


	/**
	 * This class implements the configuration of the application.
	 */

	private static class Configuration
		extends AppConfig
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The identifier of a configuration file. */
		private static final	String	ID	= "S4QL2ZT0S1NOY9P0WUKMPOZP7";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of the configuration of the application.
		 *
		 * @throws BaseException
		 *           if the configuration directory could not be determined.
		 */

		private Configuration()
			throws BaseException
		{
			// Call superclass constructor
			super(ID, NAME_KEY, SHORT_NAME, LONG_NAME);

			// Determine location of config file
			if (!noConfigFile())
			{
				// Get location of parent directory of config file
				AppAuxDirectory.Directory directory =
						AppAuxDirectory.getDirectory(NAME_KEY, getClass().getEnclosingClass());
				if (directory == null)
					throw new BaseException(ErrorMsg.NO_AUXILIARY_DIRECTORY);

				// Set parent directory of config file
				setDirectory(directory.location());
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
