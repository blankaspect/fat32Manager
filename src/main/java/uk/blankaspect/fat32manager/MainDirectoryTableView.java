/*====================================================================*\

MainDirectoryTableView.java

Class: main directory table view.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javafx.beans.InvalidationListener;

import javafx.concurrent.Task;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.stage.Window;

import uk.blankaspect.common.collection.CollectionUtils;

import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.function.IFunction0;
import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.logging.Logger;

import uk.blankaspect.common.message.MessageConstants;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.task.ITaskStatus;

import uk.blankaspect.driveio.VolumeException;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.dialog.ConfirmationDialog;
import uk.blankaspect.ui.jfx.dialog.NotificationDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleProgressDialog;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

import uk.blankaspect.ui.jfx.observer.ChangeNotifier;

//----------------------------------------------------------------------


// CLASS: MAIN DIRECTORY TABLE VIEW


public class MainDirectoryTableView
	extends DirectoryTableView
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The key combination that invokes the <i>open directory</i> command. */
	private static final	KeyCombination	KEY_COMBO_OPEN			= new KeyCodeCombination(KeyCode.ENTER);

	/** The key combination that invokes the <i>open parent directory</i> command. */
	private static final	KeyCombination	KEY_COMBO_OPEN_PARENT	=
			new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN);

	/** The key combinations that invoke the <i>refresh directory</i> command. */
	private static final	List<KeyCombination>	KEY_COMBOS_REFRESH	= List.of
	(
		new KeyCodeCombination(KeyCode.F5),
		new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN)
	);

	/** The key of the instance of {@link TextAreaDialog} that may be displayed by the <i>refresh directory</i>
		command. */
	private static final	String	REFRESH_DIRECTORY_KEY	= "refreshDirectory";

	/** Miscellaneous strings. */
	private static final	String	ELLIPSIS_STR			= "...";
	private static final	String	OPEN_STR				= "Open ";
	private static final	String	UP_TO_STR				= "Up to ";
	private static final	String	ROOT_DIRECTORY_STR		= "root directory";
	private static final	String	OPEN_DIRECTORY_STR		= "Open directory";
	private static final	String	OPEN_PREVIOUS_STR		= "Open previous directory";
	private static final	String	OPEN_NEXT_STR			= "Open next directory";
	private static final	String	REFRESH_DIRECTORY_STR	= "Refresh directory";
	private static final	String	READING_FATS_STR		= "Reading reserved sectors and FATs";
	private static final	String	READING_DIRECTORY_STR	= "Reading directory";
	private static final	String	MESSAGES_STR			= "Messages";
	private static final	String	EDIT_VOLUME_LABEL_STR	= "Edit volume label";
	private static final	String	UPDATE_VOLUME_LABEL_STR	= "Update volume label";
	private static final	String	DEFRAGMENT_FILE_STR		= "Defragment file";
	private static final	String	NOT_FRAGMENTED_STR		= "The file was not fragmented.";
	private static final	String	NOT_ENOUGH_SPACE_STR	= "There was not enough space to defragment the file.";
	private static final	String	DEFRAGMENTED_STR		= "The file was defragmented.";
	private static final	String	INVALID_CLUSTERS_STR	= "Invalid clusters";
	private static final	String	ERASE_STR				= "Erase";
	private static final	String	ERASE_FILE_STR			= "Erase file";
	private static final	String	FILE_ERASED_STR			= "The file was erased successfully.";
	private static final	String	ERASE_DIRECTORY_STR		= "Erase directory";
	private static final	String	DIRECTORY_ERASED_STR	= "The directory was erased successfully.";
	private static final	String	WARN_CANCEL_STR			=
			"WARNING" + MessageConstants.LABEL_SEPARATOR
			+ "The volume may be corrupted if you cancel the operation while it is running.";
	private static final	String	VOLUME_MODIFIED_STR		=
			"The volume was modified before the operation was cancelled.";

	/** Error messages. */
	private interface ErrorMsg
	{
		String	DIRECTORY_DOES_NOT_EXIST =
				"The directory does not exist.";

		String	DIRECTORY_NO_LONGER_EXISTS =
				"The directory no longer exists.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The history of directories that were opened on the current volume. */
	private	History					history;

	/** The notifier of changes to the current directory. */
	private	ChangeNotifier<Void>	directoryChangedNotifier;

	/** The notifier of changes to {@link #history}. */
	private	ChangeNotifier<Void>	historyChangedNotifier;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public MainDirectoryTableView()
	{
		// Call superclass constructor
		super();

		// Initialise instance variables
		history = new History();
		directoryChangedNotifier = new ChangeNotifier<>();
		historyChangedNotifier = new ChangeNotifier<>();

		// Handle 'key pressed' events
		addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if (KEY_COMBO_OPEN.match(event))
			{
				Fat32Directory.Entry entry = getSelectionModel().getSelectedItem();
				if ((entry != null) && entry.isDirectory()
						&& !entry.getName().equals(Fat32Directory.SPECIAL_DIRECTORY_NAME_THIS))
					openDirectory(entry);
				event.consume();
			}
			else if (KEY_COMBO_OPEN_PARENT.match(event))
			{
				openParentDirectory();
				event.consume();
			}
			else if (KEY_COMBOS_REFRESH.stream().anyMatch(keyCombo -> keyCombo.match(event)))
			{
				refreshDirectory();
				event.consume();
			}
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	protected static String getQuotedName(
		List<String>	names,
		String			prefix)
	{
		if (CollectionUtils.isNullOrEmpty(names))
			return null;

		String name = names.get(names.size() - 1);
		return prefix + (name.isEmpty() ? ROOT_DIRECTORY_STR : quote(name));
	}

	//------------------------------------------------------------------

	private static String quote(
		String	text)
	{
		return "'" + text + "'";
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public void setDirectory(
		Fat32Directory	directory)
	{
		// Set directory
		super.setDirectory(directory);

		// Notify listeners of change to directory
		directoryChangedNotifier.notifyChange();
	}

	//------------------------------------------------------------------

	@Override
	public void updateEntries()
	{
		// Restore default header-cell background colour
		setHeaderMode(HeaderMode.NONE);

		// Call superclass method
		super.updateEntries();
	}

	//------------------------------------------------------------------

	@Override
	protected void addContextMenuItems(
		ContextMenu				menu,
		int						index,
		Fat32Directory			directory,
		Fat32Directory.Entry	entry)
	{
		// Test for entry
		if (entry == null)
			return;

		// Add menu items for index
		switch (index)
		{
			case 0:
			{
				// Add menu item: open directory
				if (entry.isRegularDirectory())
				{
					MenuItem menuItem =
							new MenuItem(OPEN_STR + quote(entry.getName()), Images.icon(Images.ImageId.ARROW_DOWN));
					menuItem.setOnAction(event0 -> openDirectory(entry));
					menu.getItems().add(menuItem);
				}

				// Add menu item: open parent directory
				String text = getOpenParentDirectoryCommand();
				if (text != null)
				{
					MenuItem menuItem = new MenuItem(text, Images.icon(Images.ImageId.ARROW_UP));
					menuItem.setOnAction(event0 -> openParentDirectory());
					menu.getItems().add(menuItem);
				}
				break;
			}

			case 1:
			{
				// Add menu item: edit volume label
				if (entry.isVolumeLabel() && directory.isRoot())
				{
					// Add separator
					if (!menu.getItems().isEmpty())
						menu.getItems().add(new SeparatorMenuItem());

					// Add menu item
					MenuItem menuItem =
							new MenuItem(EDIT_VOLUME_LABEL_STR + ELLIPSIS_STR, Images.icon(Images.ImageId.PENCIL));
					menuItem.setOnAction(event0 -> editVolumeLabel(entry));
					menu.getItems().add(menuItem);
				}
				break;
			}

			case 2:
			{
				// Case: file
				if (entry.isFile())
				{
					// Add separator
					if (!menu.getItems().isEmpty())
						menu.getItems().add(new SeparatorMenuItem());

					// Add menu item: erase file
					MenuItem menuItem = new MenuItem(ERASE_FILE_STR, Images.icon(Images.ImageId.ERASER));
					menuItem.setOnAction(event0 -> eraseFile(entry));
					menu.getItems().add(menuItem);

					// Add separator
					menu.getItems().add(new SeparatorMenuItem());

					// Add menu item: defragment file
					menuItem = new MenuItem(DEFRAGMENT_FILE_STR, Images.icon(Images.ImageId.DEFRAGMENT));
					try
					{
						menuItem.setDisable(!directory.getVolume().isEntryFragmented(entry));
					}
					catch (VolumeException e)
					{
						Logger.INSTANCE.error(e);
					}
					menuItem.setOnAction(event0 -> defragmentFile(entry));
					menu.getItems().add(menuItem);
				}

				// Case: regular directory
				else if (entry.isRegularDirectory())
				{
					// Add separator
					if (!menu.getItems().isEmpty())
						menu.getItems().add(new SeparatorMenuItem());

					// Add menu item: erase directory
					MenuItem menuItem = new MenuItem(ERASE_DIRECTORY_STR, Images.icon(Images.ImageId.ERASER));
					menuItem.setOnAction(event0 -> eraseDirectory(entry));
					menu.getItems().add(menuItem);
				}
				break;
			}
		}
	}

	//------------------------------------------------------------------

	@Override
	protected void onCellDoubleClicked(
		Fat32Directory.Entry	entry)
	{
		if ((entry != null) && entry.isDirectory()
				&& !entry.getName().equals(Fat32Directory.SPECIAL_DIRECTORY_NAME_THIS))
			openDirectory(entry);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public History getHistory()
	{
		return history;
	}

	//------------------------------------------------------------------

	public void clearHistory()
	{
		// Clear history
		history.clear();

		// Notify listeners of change
		historyChangedNotifier.notifyChange();
	}

	//------------------------------------------------------------------

	public void addToHistory(
		Fat32Directory	directory)
	{
		// Add directory to history
		history.add(directory.getNames());

		// Notify listeners of change
		historyChangedNotifier.notifyChange();
	}

	//------------------------------------------------------------------

	public void addDirectoryChangedListener(
		InvalidationListener	listener)
	{
		directoryChangedNotifier.addListener(listener);
	}

	//------------------------------------------------------------------

	public void addHistoryChangedListener(
		InvalidationListener	listener)
	{
		historyChangedNotifier.addListener(listener);
	}

	//------------------------------------------------------------------

	public void selectColumns()
	{
		Set<Column> columns = new ColumnSelectionDialog(window(), this.columns).showDialog();
		if (columns != null)
			setColumns(columns, false);
	}

	//------------------------------------------------------------------

	public void sortByName(
		boolean	preview,
		boolean	recursive,
		boolean	ignoreCase)
	{
		// Sort directory entries
		if (preview)
		{
			// Sort entries
			List<Fat32Directory.Entry> entries = itemList.getItems();
			entries.sort(ignoreCase ? Fat32Directory.Entry.NAME_IGNORE_CASE_COMPARATOR
									: Fat32Directory.Entry.NAME_COMPARATOR);

			// Clear sort order
			getSortOrder().clear();

			// Highlight column headers to indicate preview
			setHeaderMode(HeaderMode.PREVIEW);

			// Update directory entries
			itemList.update(entries);

			// Display first item
			scrollTo(0);
		}
		else
		{
			// Create procedure to update UI when task finishes
			IProcedure0 onFinished = () ->
			{
				// Clear sort order
				getSortOrder().clear();

				// Update directory entries
				updateEntries();
			};

			// Create task to sort directory entries
			SortingTask task = new SortingTask(recursive, ignoreCase)
			{
				@Override
				protected void succeeded()
				{
					// Update UI
					onFinished.invoke();
				}

				@Override
				protected void failed()
				{
					// Update UI
					onFinished.invoke();

					// Display error message in dialog
					showErrorMessage(window());
				}

				@Override
				protected void cancelled()
				{
					// Update UI
					onFinished.invoke();
				}
			};

			// Show progress of task in dialog
			new SimpleProgressDialog(window(), task, SimpleProgressDialog.CancelMode.NO_INTERRUPT);

			// Execute 'sort' task on background thread
			Fat32ManagerApp.executeTask(task);
		}
	}

	//------------------------------------------------------------------

	public void refreshDirectory()
	{
		// Get directory and volume
		Fat32Directory directory = getDirectory();
		Fat32Volume volume = directory.getVolume();

		// Get names of directories from root to current directory
		List<String> names = directory.getNames();

		// Create container for local variables
		class Vars
		{
			Fat32Directory	directory;
		}
		Vars vars = new Vars();

		// Declare record for result of task
		record Result(
			Fat32Directory	directory,
			List<String>	messages)
		{ }

		// Log title of task
		String title = REFRESH_DIRECTORY_STR;
		Logger.INSTANCE.info(title + " : " + getDirectory().getPathname());

		// Create task to read directories
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
				// Initialise volume
				updateMessage(READING_FATS_STR);
				try
				{
					volume.init();
				}
				catch (VolumeException e)
				{
					Fat32ManagerApp.instance().invalidateVolume();
					throw e;
				}

				// Initialise list of messages
				List<String> messages = new ArrayList<>();

				// Read directories
				updateMessage(READING_DIRECTORY_STR);
				for (int i = 0; i < names.size(); i++)
				{
					// Get directory from its name
					if (i == 0)
						vars.directory = volume.getRootDir();
					else
					{
						String name = names.get(i);
						Fat32Directory.Entry entry = vars.directory.findEntry(name);
						if (entry == null)
						{
							String pathname = vars.directory.getPathname() + Fat32Directory.NAME_SEPARATOR + name;
							throw new LocationException(ErrorMsg.DIRECTORY_NO_LONGER_EXISTS, pathname);
						}
						vars.directory = vars.directory.getSubdirectory(entry);
					}

					// Read directory
					vars.directory.read(messages);
				}

				// Return list of messages
				return new Result(vars.directory, messages);
			}

			@Override
			protected void succeeded()
			{
				// Get result
				Result result = getValue();

				// Update directory
				setDirectory(result.directory);

				// Display messages in dialog
				if (!result.messages.isEmpty())
				{
					TextAreaDialog.show(window(), REFRESH_DIRECTORY_KEY, getTitle() + " : " + MESSAGES_STR,
										String.join("\n\n", result.messages));
				}
			}

			@Override
			protected void failed()
			{
				// Update directory
				setDirectory(vars.directory);

				// Display error message in dialog
				showErrorMessage(window());
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(window(), task);

		// Execute task on background thread
		Fat32ManagerApp.executeTask(task);
	}

	//------------------------------------------------------------------

	public String getOpenParentDirectoryCommand()
	{
		Fat32Directory directory = getDirectory();
		return directory.isRoot() ? null : getQuotedName(List.of(directory.getParent().getName()), UP_TO_STR);
	}

	//------------------------------------------------------------------

	public void openParentDirectory()
	{
		if (!getDirectory().isRoot())
		{
			// Get name of current directory
			String name = getDirectory().getName();

			// Set directory on table view
			Fat32Directory directory = getDirectory().getParent();
			setDirectory(directory);

			// Add directory to history
			addToHistory(directory);

			// Request focus
			requestFocus();

			// Select entry for previous directory
			if (name != null)
			{
				Fat32Directory.Entry entry = getDirectory().findEntry(name);
				if (entry != null)
				{
					getSelectionModel().select(entry);
					scrollTo(entry);
				}
			}
		}
	}

	//------------------------------------------------------------------

	public void openPreviousDirectory()
	{
		if (history.hasPrevious())
			openDirectory(false);
	}

	//------------------------------------------------------------------

	public void openNextDirectory()
	{
		if (history.hasNext())
			openDirectory(true);
	}

	//------------------------------------------------------------------

	public void openDirectory(
		Fat32Directory	directory)
	{
		// Set directory on table view
		setDirectory(directory);

		// Add directory to history
		if (directory != null)
			addToHistory(directory);

		// Request focus
		requestFocus();
	}

	//------------------------------------------------------------------

	public void openDirectory(
		String	pathname)
	{
		if (!StringUtils.isNullOrEmpty(pathname))
		{
			// Get list of directory names
			List<String> names = StringUtils.split(pathname, Fat32Directory.NAME_SEPARATOR_CHAR);

			// Search for directory
			Fat32Directory directory = null;
			String errorPathname = null;
			try
			{
				DirectorySearchResult result = findDirectory(names);
				directory = result.directory;
				errorPathname = result.errorPathname;
			}
			catch (VolumeException e)
			{
				Utils.showErrorMessage(window(), OPEN_DIRECTORY_STR, e);
			}

			// If no directory was found, use current directory
			if (directory == null)
				directory = getDirectory();

			// Open directory
			openDirectory(directory);

			// If target pathname was not reached, display error mesage
			if (errorPathname != null)
			{
				Utils.showErrorMessage(window(), OPEN_DIRECTORY_STR,
									   new LocationException(ErrorMsg.DIRECTORY_DOES_NOT_EXIST, errorPathname));
			}
		}
	}

	//------------------------------------------------------------------

	private void openDirectory(
		Fat32Directory.Entry	entry)
	{
		if (entry.getName().equals(Fat32Directory.SPECIAL_DIRECTORY_NAME_PARENT))
			openParentDirectory();
		else
		{
			// Set directory on table view
			try
			{
				// Set directory on table view
				Fat32Directory directory = getDirectory().getSubdirectory(entry);
				setDirectory(directory);

				// Add directory to history
				addToHistory(directory);
			}
			catch (WrappedVolumeException e)
			{
				// Display error message in dialog
				Utils.showErrorMessage(window(), OPEN_DIRECTORY_STR, e);
			}

			// Request focus
			requestFocus();
		}
	}

	//------------------------------------------------------------------

	private void openDirectory(
		boolean	forward)
	{
		// Get title of error dialog
		String errorDialogTitle = forward ? OPEN_NEXT_STR : OPEN_PREVIOUS_STR;

		// Search for directory
		Fat32Directory directory = null;
		try
		{
			DirectorySearchResult result = findDirectory(forward ? history.next() : history.previous());
			directory = result.directory;
			if (result.errorPathname != null)
			{
				if (forward)
				{
					history.previous();
					history.clearNext();
				}
				else
				{
					history.next();
					history.clearPrevious();
				}
				Utils.showErrorMessage(window(), errorDialogTitle,
									   new LocationException(ErrorMsg.DIRECTORY_DOES_NOT_EXIST, result.errorPathname));
			}
		}
		catch (VolumeException e)
		{
			Utils.showErrorMessage(window(), errorDialogTitle, e);
		}

		// If no directory was found, clear history ...
		if (directory == null)
			clearHistory();

		// ... otherwise, update table view
		else
		{
			// Set previous directory on table view
			setDirectory(directory);

			// Notify listeners of change to history
			historyChangedNotifier.notifyChange();

			// Request focus
			requestFocus();
		}
	}

	//------------------------------------------------------------------

	private DirectorySearchResult findDirectory(
		List<String>	names)
		throws VolumeException
	{
		Fat32Directory directory = null;
		String errorPathname = null;
		try
		{
			for (int i = 0; i < names.size(); i++)
			{
				String name = names.get(i);
				if ((i == 0) && name.isEmpty())
					directory = getDirectory().getVolume().getRootDir();
				else
				{
					Fat32Directory.Entry entry = (directory == null) ? null : directory.findEntry(name);
					if ((entry == null) || !entry.isDirectory())
					{
						errorPathname = StringUtils.join(Fat32Directory.NAME_SEPARATOR_CHAR, names.subList(0, i + 1));
						break;
					}
					directory = directory.getDirectory(entry);
				}
			}
		}
		catch (WrappedVolumeException e)
		{
			if (e.getCause() instanceof VolumeException ve)
				throw ve;
		}
		return new DirectorySearchResult(directory, errorPathname);
	}

	//------------------------------------------------------------------

	private void editVolumeLabel(
		Fat32Directory.Entry	entry)
	{
		// Display dialog to edit volume label
		VolumeLabelDialog.Result result = VolumeLabelDialog.show(window(), EDIT_VOLUME_LABEL_STR, entry.getName(),
																 entry.getLastModificationTime());
		if (result == null)
			return;

		// Log title of task
		String title = UPDATE_VOLUME_LABEL_STR;
		Logger.INSTANCE.info(title);

		// Create task to update volume label
		Task<Void> task = new AbstractTask<>()
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
				// Update volume label
				getDirectory().getVolume().updateVolumeLabel(result.volumeLabel(), result.modTime(),
															 createTaskStatus());

				// Return nothing
				return null;
			}

			@Override
			protected void succeeded()
			{
				// Redraw cells
				refresh();
			}

			@Override
			protected void failed()
			{
				// Display error message in dialog
				showErrorMessage(window());
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(window(), task);

		// Execute task on background thread
		Fat32ManagerApp.executeTask(task);
	}

	//------------------------------------------------------------------

	private void eraseFile(
		Fat32Directory.Entry	entry)
	{
		// Display dialog for filler value
		Integer fillerValue = ErasureFillerValueDialog.show(window(), ERASE_FILE_STR);
		if (fillerValue == null)
			return;

		// Display warning about cancelling the operation
		if (!ConfirmationDialog.show(window(), ERASE_FILE_STR, MessageIcon32.WARNING.get(), WARN_CANCEL_STR, ERASE_STR))
			return;

		// Log description of task
		Logger.INSTANCE.info(ERASE_FILE_STR + " " + entry.getPathname());

		// Create container for local variables
		class Vars
		{
			boolean modified;
		}
		Vars vars = new Vars();

		// Create task to erase file
		Task<Void> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(ERASE_FILE_STR);
				updateProgress(-1, 1);
			}

			@Override
			protected Void call()
				throws Exception
			{
				// Create task status
				ITaskStatus taskStatus = createTaskStatus();

				// Erase file
				vars.modified = entry.getDirectory().getVolume().eraseFile(entry, fillerValue.byteValue(), taskStatus);

				// If task has been cancelled, change state to 'cancelled'
				hardCancel(false);

				// Return nothing
				return null;
			}

			@Override
			protected void succeeded()
			{
				// Refresh directory
				refreshDirectory();

				// Report success
				String message = entry.getPathname() + MessageConstants.LABEL_SEPARATOR + FILE_ERASED_STR;
				NotificationDialog.show(window(), getTitle(), MessageIcon32.INFORMATION.get(), message);
			}

			@Override
			protected void failed()
			{
				// Refresh directory
				refreshDirectory();

				// Display error message in dialog
				showErrorMessage(window());
			}

			@Override
			protected void cancelled()
			{
				// Refresh directory
				refreshDirectory();

				// Report volume modified
				if (vars.modified)
					NotificationDialog.show(window(), getTitle(), MessageIcon32.ALERT.get(), VOLUME_MODIFIED_STR);
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(window(), task, SimpleProgressDialog.CancelMode.NO_INTERRUPT);

		// Execute task on background thread
		Fat32ManagerApp.executeTask(task);
	}

	//------------------------------------------------------------------

	private void eraseDirectory(
		Fat32Directory.Entry	entry)
	{
		// Display dialog for filler value
		Integer fillerValue = ErasureFillerValueDialog.show(window(), ERASE_DIRECTORY_STR);
		if (fillerValue == null)
			return;

		// Display warning about cancelling the operation
		if (!ConfirmationDialog.show(window(), ERASE_DIRECTORY_STR, MessageIcon32.WARNING.get(), WARN_CANCEL_STR,
									 ERASE_STR))
			return;

		// Log description of task
		Logger.INSTANCE.info(ERASE_DIRECTORY_STR + " " + entry.getPathname());

		// Create container for local variables
		class Vars
		{
			boolean modified;
		}
		Vars vars = new Vars();

		// Create task to erase directory
		Task<Void> task = new AbstractTask<>()
		{
			{
				// Initialise task
				updateTitle(ERASE_DIRECTORY_STR);
				updateProgress(-1, 1);
			}

			@Override
			protected Void call()
				throws Exception
			{
				// Create task status
				ITaskStatus taskStatus = createTaskStatus();

				// Erase directory
				vars.modified =
						entry.getDirectory().getVolume().eraseDirectory(entry, fillerValue.byteValue(), taskStatus);

				// If task has been cancelled, change state to 'cancelled'
				hardCancel(false);

				// Return nothing
				return null;
			}

			@Override
			protected void succeeded()
			{
				// Refresh directory
				refreshDirectory();

				// Report success
				String message = entry.getPathname() + MessageConstants.LABEL_SEPARATOR + DIRECTORY_ERASED_STR;
				NotificationDialog.show(window(), getTitle(), MessageIcon32.INFORMATION.get(), message);
			}

			@Override
			protected void failed()
			{
				// Refresh directory
				refreshDirectory();

				// Display error message in dialog
				showErrorMessage(window());
			}

			@Override
			protected void cancelled()
			{
				// Refresh directory
				refreshDirectory();

				// Report volume modified
				if (vars.modified)
					NotificationDialog.show(window(), getTitle(), MessageIcon32.ALERT.get(), VOLUME_MODIFIED_STR);
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(window(), task, SimpleProgressDialog.CancelMode.NO_INTERRUPT);

		// Execute task on background thread
		Fat32ManagerApp.executeTask(task);
	}

	//------------------------------------------------------------------

	private void defragmentFile(
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
				Fat32Volume volume = entry.getDirectory().getVolume();

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
				// Refresh directory
				refreshDirectory();

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
						NotificationDialog.show(window(), getTitle(), icon.get(), message);
					}
				}

				// Case: invalid clusters
				else
				{
					InvalidClusterDialog.show(window(), getTitle() + " : " + INVALID_CLUSTERS_STR,
											  result.invalidClusters);
				}
			}

			@Override
			protected void failed()
			{
				// Refresh directory
				refreshDirectory();

				// Display error message in dialog
				showErrorMessage(window());
			}

			@Override
			protected void cancelled()
			{
				// Refresh directory
				refreshDirectory();
			}
		};

		// Show progress of task in dialog
		new SimpleProgressDialog(window(), task, SimpleProgressDialog.CancelMode.NO_INTERRUPT);

		// Execute task on background thread
		Fat32ManagerApp.executeTask(task);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: RESULT OF SEARCH FOR DIRECTORY


	private record DirectorySearchResult(
		Fat32Directory	directory,
		String			errorPathname)
	{ }

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: DIRECTORY HISTORY


	public static class History
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MAX_LENGTH	= 256;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int							index;
		private	LinkedList<List<String>>	directories;

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

		public boolean hasPrevious()
		{
			return (index > 0);
		}

		//--------------------------------------------------------------

		public boolean hasNext()
		{
			return (index < directories.size() - 1);
		}

		//--------------------------------------------------------------

		public List<String> getPrevious()
		{
			return hasPrevious() ? directories.get(index - 1) : null;
		}

		//--------------------------------------------------------------

		public List<String> getNext()
		{
			return hasNext() ? directories.get(index + 1) : null;
		}

		//--------------------------------------------------------------

		private List<String> previous()
		{
			return hasPrevious() ? directories.get(--index) : null;
		}

		//--------------------------------------------------------------

		private List<String> next()
		{
			return hasNext() ? directories.get(++index) : null;
		}

		//--------------------------------------------------------------

		private void add(
			List<String>	names)
		{
			// Validate argument
			if (names == null)
				throw new IllegalArgumentException("Null list of names");

			// Don't add directory if it is current directory
			if ((index < directories.size()) && names.equals(directories.get(index)))
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
			directories.add(List.copyOf(names));
		}

		//--------------------------------------------------------------

		private void clear()
		{
			// Reset instance variables
			directories.clear();
			index = 0;
		}

		//--------------------------------------------------------------

		private void clearPrevious()
		{
			if (hasPrevious())
			{
				directories = new LinkedList<>(directories.subList(index, directories.size()));
				index = 0;
			}
		}

		//--------------------------------------------------------------

		private void clearNext()
		{
			if (hasNext())
				directories = new LinkedList<>(directories.subList(0, index + 1));
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: COLUMN-SELECTION DIALOG


	private static class ColumnSelectionDialog
		extends SimpleModalDialog<Set<Column>>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The spacing between adjacent children of the control pane. */
		private static final	double	CONTROL_PANE_SPACING	= 8.0;

		/** The padding around the control pane. */
		private static final	Insets	CONTROL_PANE_PADDING	= new Insets(2.0, 4.0, 2.0, 4.0);

		/** Miscellaneous strings. */
		private static final	String	SELECT_TABLE_COLUMNS_STR	= "Select table columns";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	Set<Column>	result;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a modal dialog in which the columns of a {@link MainDirectoryTableView} may be
		 * selected.
		 *
		 * @param owner
		 *          the window that will own this dialog, or {@code null} if the dialog has no owner.
		 * @param initialColumns
		 *          the columns that will be initially selected.
		 */

		private ColumnSelectionDialog(
			Window				owner,
			Collection<Column>	initialColumns)
		{
			// Call superclass constructor
			super(owner, MethodHandles.lookup().lookupClass().getCanonicalName(), null, SELECT_TABLE_COLUMNS_STR);

			// Create control pane
			VBox controlPane = new VBox(CONTROL_PANE_SPACING);
			controlPane.setMaxWidth(Region.USE_PREF_SIZE);
			controlPane.setAlignment(Pos.CENTER_LEFT);
			controlPane.setPadding(CONTROL_PANE_PADDING);

			// Create a check box for each possible column and add it to control pane
			EnumMap<Column, CheckBox> checkBoxes = new EnumMap<>(Column.class);
			for (Column column : Column.values())
			{
				CheckBox checkBox = new CheckBox(column.getLongText());
				checkBox.setSelected(initialColumns.contains(column));
				checkBoxes.put(column, checkBox);
				controlPane.getChildren().add(checkBox);
			}

			// Create function to return set of selected columns
			IFunction0<EnumSet<Column>> selectedColumns = () ->
			{
				EnumSet<Column> columns = EnumSet.noneOf(Column.class);
				for (Column column : checkBoxes.keySet())
				{
					if (checkBoxes.get(column).isSelected())
						columns.add(column);
				}
				return columns;
			};

			// Add control pane to content pane
			addContent(controlPane);

			// Create button: OK
			Button okButton = Buttons.hNoShrink(OK_STR);
			okButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
			okButton.setOnAction(event ->
			{
				result = selectedColumns.invoke();
				hide();
			});
			addButton(okButton, HPos.RIGHT);

			// Create button: cancel
			Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
			cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
			cancelButton.setOnAction(event -> requestClose());
			addButton(cancelButton, HPos.RIGHT);

			// Fire 'cancel' button if Escape key is pressed; fire 'OK' button if Ctrl+Enter is pressed
			setKeyFireButton(cancelButton, okButton);

			// Create procedure to update 'OK' button
			IProcedure0 updateOkButton = () -> okButton.setDisable(selectedColumns.invoke().isEmpty());

			// Update 'OK' button if 'selected' state of a check box changes
			checkBoxes.values().stream().forEach(checkBox ->
					checkBox.selectedProperty().addListener(observable -> updateOkButton.invoke()));

			// Update 'OK' button
			updateOkButton.invoke();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected Set<Column> getResult()
		{
			return result;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: SORTING TASK


	private class SortingTask
		extends AbstractTask<Void>
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	COUNTING_DIRECTORIES_STR	= "Counting directories";
		private static final	String	SORTING_DIRECTORY_STR		= "Sorting directory entries";
		private static final	String	SORTING_STR					= "Sorting";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	boolean	recursive;
		private	boolean	ignoreCase;
		private	int		numDirectories;
		private	int		numDirectoriesSorted;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private SortingTask(
			boolean	recursive,
			boolean	ignoreCase)
		{
			// Initialise instance variables
			this.recursive = recursive;
			this.ignoreCase = ignoreCase;

			// Set title
			updateTitle(SORTING_DIRECTORY_STR);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		protected Void call()
			throws Exception
		{
			// Set message and indeterminate progress while counting directories
			updateMessage(COUNTING_DIRECTORIES_STR);
			updateProgress(-1, 1);

			// Count directories
			Fat32Directory directory = getDirectory();
			countDirectories(directory);

			// Reset progress
			updateProgress(0, numDirectories);

			// Sort directory entries
			sortByName(directory);

			// If task has been cancelled, change state to 'cancelled'
			hardCancel(false);

			// Return nothing
			return null;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void countDirectories(
			Fat32Directory	directory)
			throws VolumeException
		{
			// Test whether task has been cancelled
			if (isCancelled())
				return;

			// Increment number of directories
			++numDirectories;

			// Count subdirectories
			if (recursive)
			{
				for (Fat32Directory subdirectory : directory.getChildren())
				{
					// Test whether task has been cancelled
					if (isCancelled())
						break;

					// Process subdirectory
					if (subdirectory.getEntryInParent().isRegularDirectory())
						countDirectories(subdirectory);
				}
			}
		}

		//--------------------------------------------------------------

		private void sortByName(
			Fat32Directory	directory)
			throws VolumeException
		{
			// Test whether task has been cancelled
			if (isCancelled())
				return;

			// Update message with pathname of directory
			updateMessage(SORTING_STR + MessageConstants.SPACE_SEPARATOR + directory.getPathname());

			// Sort directory entries
			directory.sortByName(ignoreCase);

			// Update progress
			updateProgress(++numDirectoriesSorted, numDirectories);

			// Sort subdirectories
			if (recursive)
			{
				for (Fat32Directory subdirectory : directory.getChildren())
				{
					// Test whether task has been cancelled
					if (isCancelled())
						break;

					// Sort subdirectory
					if (subdirectory.getEntryInParent().isRegularDirectory())
						sortByName(subdirectory);
				}
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
