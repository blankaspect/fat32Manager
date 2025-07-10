/*====================================================================*\

LocationChooser.java

Class: file-system location chooser.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.locationchooser;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.InvalidationListener;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;

import javafx.scene.input.KeyEvent;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;

import javafx.scene.paint.Color;

import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import uk.blankaspect.common.collection.CollectionUtils;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.filesystem.PathnameUtils;
import uk.blankaspect.common.filesystem.PathUtils;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.os.OsUtils;

import uk.blankaspect.common.stack.StackUtils;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.container.PaneStyle;

import uk.blankaspect.ui.jfx.exec.ExecUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: FILE-SYSTEM LOCATION CHOOSER


public class LocationChooser
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler on platforms other than Windows. */
	private static final	int		WINDOW_SHOWN_DELAY	= 150;

	/** The delay (in milliseconds) in a <i>WINDOW_SHOWN</i> event handler on Windows. */
	private static final	int		WINDOW_SHOWN_DELAY_WINDOWS	= 50;

	/** The delay (in milliseconds) before making the dialog window visible by restoring its opacity. */
	private static final	int		WINDOW_VISIBLE_DELAY	= 50;

	/** Miscellaneous strings. */
	private static final	String	SELECT_STR	= "Select";
	private static final	String	OPEN_STR	= "Open";
	private static final	String	SAVE_STR	= "Save";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.LOCATION_CHOOSER_DIALOG)
					.desc(StyleClass.OUTER_BUTTON_PANE)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.LOCATION_CHOOSER_DIALOG)
						.desc(StyleClass.OUTER_BUTTON_PANE)
						.build())
				.borders(Side.LEFT)
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	LOCATION_CHOOSER_DIALOG	= StyleConstants.CLASS_PREFIX + "location-chooser-dialog";
		String	OUTER_BUTTON_PANE		= StyleConstants.CLASS_PREFIX + "outer-button-pane";
	}

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	WINDOW_SHOWN_DELAY	= "blankaspect.ui.jfx.locationChooser.windowShownDelay";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** A map of the states of instances of {@link Dialog}. */
	private static	Map<String, DialogState>	dialogStates	= new HashMap<>();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	MatcherScope			scope;
	private	int						filterIndex;
	private	List<LocationMatcher>	filters;
	private	Path					initialDirectory;
	private	String					initialFilename;
	private	Path					finalDirectory;
	private	int						finalFilterIndex;
	private	boolean					ignoreFilenameCase;
	private	boolean					showHiddenEntries;
	private	String					dialogStateKey;
	private	String					dialogTitle;
	private	IDialogLocator			dialogLocator;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(LocationChooser.class, COLOUR_PROPERTIES, RULE_SETS,
									   PaneStyle.class);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected LocationChooser(
		MatcherScope	scope)
	{
		// Validate argument
		if (scope == null)
			throw new IllegalArgumentException("Null scope");

		// Initialise instance variables
		this.scope = scope;
		filterIndex = -1;
		filters = new ArrayList<>();
		ignoreFilenameCase = OsUtils.isWindows();
		showHiddenEntries = OsUtils.isUnixLike();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static LocationChooser forFiles()
	{
		return new LocationChooser(MatcherScope.FILES);
	}

	//------------------------------------------------------------------

	public static LocationChooser forDirectories()
	{
		return new LocationChooser(MatcherScope.DIRECTORIES);
	}

	//------------------------------------------------------------------

	public static DialogState getDialogState(
		String	key)
	{
		return dialogStates.get(key);
	}

	//------------------------------------------------------------------

	public static void setDialogState(
		String		key,
		DialogState	state)
	{
		dialogStates.put(key, state);
	}

	//------------------------------------------------------------------

	public static Path existingDirectory(
		Path	location,
		Path	defaultDirectory)
	{
		return ((location != null) && Files.isDirectory(location, LinkOption.NOFOLLOW_LINKS)) ? location
																							  : defaultDirectory;
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

	public Path getInitialDirectory()
	{
		return initialDirectory;
	}

	//------------------------------------------------------------------

	public void setInitialDirectory(
		Path	location)
	{
		initialDirectory = location;
	}

	//------------------------------------------------------------------

	public void setInitialDirectory(
		String	pathname)
	{
		initialDirectory = (pathname == null) ? null : Path.of(PathnameUtils.parsePathname(pathname));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the initial directory of this chooser to the specified location, if it denotes an existing directory.
	 *
	 * @param directory
	 *          the location of the directory to which the initial directory of this chooser will be set, if the
	 *          directory exists.
	 */

	public void initDirectory(
		Path	directory)
	{
		if ((directory != null) && Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS))
			setInitialDirectory(directory);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the initial directory of this chooser to either the specified location, if it denotes an existing directory,
	 * or the specified default location, which is assumed to exist.
	 *
	 * @param directory
	 *          the location of the directory to which the initial directory of this chooser will be set, if the
	 *          directory exists.
	 * @param defaultDirectory
	 *          the location of the default directory, which is assumed to exist.
	 */

	public void initDirectory(
		Path	directory,
		Path	defaultDirectory)
	{
		setInitialDirectory(existingDirectory(directory, defaultDirectory));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the initial directory of this chooser to the parent of the specified location, if the parent denotes an
	 * existing directory.
	 *
	 * @param location
	 *          the location to whose parent the initial directory of this chooser will be set, if the parent exists.
	 */

	public void initDirectoryWithParent(
		Path	location)
	{
		initDirectory((location == null) ? null : PathUtils.absParent(location));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the initial directory of this chooser to either the parent of the specified location, if the parent denotes
	 * an existing directory, or the specified default location, which is assumed to exist.
	 *
	 * @param location
	 *          the location to whose parent the initial directory of this chooser will be set, if the parent exists.
	 * @param defaultDirectory
	 *          the location of the default directory, which is assumed to exist.
	 */

	public void initDirectoryWithParent(
		Path	location,
		Path	defaultDirectory)
	{
		initDirectory((location == null) ? null : PathUtils.absParent(location), defaultDirectory);
	}

	//------------------------------------------------------------------

	public String getInitialFilename()
	{
		return initialFilename;
	}

	//------------------------------------------------------------------

	public void setInitialFilename(
		String	name)
	{
		initialFilename = name;
	}

	//------------------------------------------------------------------

	public Path getFinalDirectory()
	{
		return finalDirectory;
	}

	//------------------------------------------------------------------

	public LocationMatcher getFinalFilter()
	{
		return filters.get(finalFilterIndex);
	}

	//------------------------------------------------------------------

	public int getFinalFilterIndex()
	{
		return finalFilterIndex;
	}

	//------------------------------------------------------------------

	public void setIgnoreFilenameCase(
		boolean	ignoreCase)
	{
		ignoreFilenameCase = ignoreCase;
	}

	//------------------------------------------------------------------

	public void setShowHiddenEntries(
		boolean	show)
	{
		showHiddenEntries = show;
	}

	//------------------------------------------------------------------

	public List<LocationMatcher> getFilters()
	{
		return Collections.unmodifiableList(filters);
	}

	//------------------------------------------------------------------

	public void clearFilters()
	{
		filters.clear();
	}

	//------------------------------------------------------------------

	public void addFilter(
		LocationMatcher	filter)
	{
		// Validate argument
		if (filter == null)
			throw new IllegalArgumentException("Null filter");
		if ((scope != MatcherScope.FILES_AND_DIRECTORIES) && (filter.getScope() != scope))
			throw new IllegalArgumentException("Scope of filter is inconsistent with scope of chooser");

		// Add filter to list and adjust filter index
		if (filters.add(filter))
			filterIndex = Math.max(0, filterIndex);
	}

	//------------------------------------------------------------------

	public void addFilters(
		Iterable<? extends LocationMatcher>	filters)
	{
		for (LocationMatcher filter : filters)
			addFilter(filter);
	}

	//------------------------------------------------------------------

	public void addFilters(
		LocationMatcher...	filters)
	{
		for (LocationMatcher filter : filters)
			addFilter(filter);
	}

	//------------------------------------------------------------------

	public void removeFilter(
		LocationMatcher	filter)
	{
		// Validate argument
		if (filter == null)
			throw new IllegalArgumentException("Null filter");

		// Remove filter from list and adjust filter index
		if (filters.remove(filter))
			filterIndex = Math.min(filterIndex, filters.size() - 1);
	}

	//------------------------------------------------------------------

	public void setInitialFilter(
		int	index)
	{
		// Validate argument
		if (!filters.isEmpty() && ((index < 0) || (index >= filters.size())))
			throw new IllegalArgumentException("Index out of bounds: " + index);

		// Update instance variable
		filterIndex = index;
	}

	//------------------------------------------------------------------

	public void setDialogStateKey()
	{
		setDialogStateKey(-1);
	}

	//------------------------------------------------------------------

	public void setDialogStateKey(
		int	index)
	{
		StackWalker.StackFrame sf = StackUtils.stackFrame(1);
		dialogStateKey = sf.getClassName() + "." + sf.getMethodName() + ((index < 0) ? "" : "-" + index);
	}

	//------------------------------------------------------------------

	public void setDialogStateKey(
		String	key)
	{
		dialogStateKey = key;
	}

	//------------------------------------------------------------------

	public void setDialogTitle(
		String	title)
	{
		dialogTitle = title;
	}

	//------------------------------------------------------------------

	public void setDialogTitle(
		IDialogLocator	locator)
	{
		dialogLocator = locator;
	}

	//------------------------------------------------------------------

	public Path showDialog(
		Window	owner,
		String	acceptText)
	{
		Dialog dialog = new Dialog(SelectionMode.SINGLE, owner, acceptText);
		dialog.showAndWait();
		return CollectionUtils.isNullOrEmpty(dialog.result) ? null : dialog.result.get(0);
	}

	//------------------------------------------------------------------

	public List<Path> showMultipleDialog(
		Window	owner,
		String	acceptText)
	{
		Dialog dialog = new Dialog(SelectionMode.MULTIPLE, owner, acceptText);
		dialog.showAndWait();
		return dialog.result;
	}

	//------------------------------------------------------------------

	public Path showSelectDialog(
		Window	owner)
	{
		return showDialog(owner, SELECT_STR);
	}

	//------------------------------------------------------------------

	public List<Path> showSelectMultipleDialog(
		Window	owner)
	{
		return showMultipleDialog(owner, SELECT_STR);
	}

	//------------------------------------------------------------------

	public Path showOpenDialog(
		Window	owner)
	{
		return showDialog(owner, OPEN_STR);
	}

	//------------------------------------------------------------------

	public List<Path> showOpenMultipleDialog(
		Window	owner)
	{
		return showMultipleDialog(owner, OPEN_STR);
	}

	//------------------------------------------------------------------

	public Path showSaveDialog(
		Window	owner)
	{
		return showDialog(owner, SAVE_STR);
	}

	//------------------------------------------------------------------

	public Path appendFilenameSuffix(
		Path	file)
	{
		LocationMatcher filter = getFinalFilter();
		if (filter != FileMatcher.ALL_FILES)
		{
			String filename = file.getFileName().toString();
			if (filename.indexOf('.') < 0)
				return file.resolveSibling(filename + filter.getFilenameSuffixes().get(0));
		}
		return file;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: DIALOG-LOCATOR FUNCTION


	/**
	 * This functional interface defines a method that returns the location of a {@link LocationChooser.Dialog} given
	 * the width and height of the dialog.
	 */

	@FunctionalInterface
	public interface IDialogLocator
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the location of a dialog with the specified width and height.
		 *
		 * @param  width
		 *           the width of the dialog.
		 * @param  height
		 *           the height of the dialog.
		 * @return the location of the dialog whose dimensions are {@code width} and {@code height}.
		 */

		Point2D getLocation(
			double	width,
			double	height);

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: DIALOG STATE


	public record DialogState(
		int			x,
		int			y,
		int			width,
		int			height,
		double		splitPaneDividerPosition,
		double[]	tableViewColumnWidths)
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		public DialogState
		{
			if (tableViewColumnWidths == null)
				throw new IllegalArgumentException("Null column widths");
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: LOCATION-CHOOSER DIALOG


	/**
	 * This class implements a modal dialog in which a file-system location may be chosen.
	 */

	private class Dialog
		extends Stage
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	double	BUTTON_PANE_H_GAP	= 10.0;
		private static final	double	BUTTON_PANE_V_GAP	= 6.0;

		private static final	Insets	BUTTON_PANE_PADDING	= new Insets(8.0, 14.0, 8.0, 14.0);

		/** The margins that are applied to the visual bounds of each screen when determining whether the saved location
			of the window is within a screen. */
		private static final	Insets	SCREEN_MARGINS	= new Insets(0.0, 32.0, 32.0, 0.0);

		/** Miscellaneous strings. */
		private static final	String	CANCEL_STR	= "Cancel";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The result of this dialog. */
		private	List<Path>	result;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a modal dialog in which a file-system location may be chosen.
		 *
		 * @param selectionMode
		 *          the selection mode of the location chooser.
		 * @param owner
		 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
		 * @param acceptText
		 *          the text of the <i>accept</i> button.
		 */

		private Dialog(
			SelectionMode	selectionMode,
			Window			owner,
			String			acceptText)
		{
			// Set properties
			initModality(Modality.APPLICATION_MODAL);
			initOwner(owner);
			if (dialogTitle != null)
				setTitle(dialogTitle);

			// Make window invisible and prevent it from being resized until it is displayed
			setOpacity(0.0);

			// Set icons to those of owner
			if (owner instanceof Stage stage)
				getIcons().addAll(stage.getIcons());

			// Create location-chooser pane
			LocationChooserPane chooserPane = new LocationChooserPane(scope, selectionMode, ignoreFilenameCase,
																	  showHiddenEntries, true, filterIndex, filters);
			chooserPane.getStyleClass().add(StyleClass.LOCATION_CHOOSER_DIALOG);
			chooserPane.addEventHandler(LocationChooserEvent.LOCATIONS_CHOSEN, event ->
			{
				List<Path> locations = event.getLocations();
				if (locations != null)
				{
					result = locations;
					requestClose();
				}
			});

			// Allow name pane of location-chooser pane to grow horizontally
			HBox.setHgrow(chooserPane.getNamePane(), Priority.SOMETIMES);

			// Button: accept
			Button acceptButton = Buttons.hExpansive(acceptText);
			acceptButton.setOnAction(event -> chooserPane.notifyLocationsChosen());

			// Create procedure to update 'accept' button
			IProcedure0 updateAcceptButton = () ->
					acceptButton.setDisable(chooserPane.getSelectedLocations().isEmpty());

			// Update 'accept' button
			updateAcceptButton.invoke();

			// Update 'accept' button when selected locations in location-chooser pane change
			chooserPane.getSelectedLocations().addListener((InvalidationListener) observable ->
					updateAcceptButton.invoke());

			// Button: cancel
			Button cancelButton = Buttons.hExpansive(CANCEL_STR);
			cancelButton.setOnAction(event -> requestClose());

			// Create button pane
			TilePane buttonPane = new TilePane(BUTTON_PANE_H_GAP, BUTTON_PANE_V_GAP, acceptButton, cancelButton);
			buttonPane.setPrefColumns(buttonPane.getChildren().size());
			buttonPane.setAlignment(Pos.CENTER);
			buttonPane.setPadding(BUTTON_PANE_PADDING);

			// Get outer button pane from location-chooser pane
			StackPane outerButtonPane = chooserPane.getBottomRightPane();
			outerButtonPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER),
																   Side.LEFT));
			outerButtonPane.getStyleClass().add(StyleClass.OUTER_BUTTON_PANE);
			outerButtonPane.getChildren().add(buttonPane);

			// Create scene and set it on this window
			setScene(new Scene(chooserPane));
			sizeToScene();

			// Add style sheet to scene
			StyleManager.INSTANCE.addStyleSheet(getScene());

			// When window is shown, set its size and location after a delay
			addEventHandler(WindowEvent.WINDOW_SHOWN, event ->
			{
				// Create container for dimensions of window
				class Dimensions
				{
					double	w;
					double	h;

					void update()
					{
						w = getWidth();
						h = getHeight();
					}
				}
				Dimensions dims = new Dimensions();
				dims.update();

				// Set location and size of window after a delay
				ExecUtils.afterDelay(getWindowShownDelay(), () ->
				{
					// Set state of window and chooser pane to stored value
					DialogState state = (dialogStateKey == null) ? null : dialogStates.get(dialogStateKey);
					if (state != null)
					{
						chooserPane.setSplitPaneDividerPosition(state.splitPaneDividerPosition());
						chooserPane.setTableViewColumnWidths(state.tableViewColumnWidths());
						setX(state.x());
						setY(state.y());
						setWidth(state.width());
						setHeight(state.height());
						dims.update();
					}

					// If there is no stored dialog state, set location of window relative to owner or in centre of
					// screen ...
					Point2D location = null;
					if (state == null)
					{
						// If there is a locator, locate window relative to it ...
						if (dialogLocator != null)
							location = dialogLocator.getLocation(dims.w, dims.h);

						// ... otherwise, if there is no owner that is showing, locate window relative to screen ...
						else if ((owner == null) || !owner.isShowing())
							location = SceneUtils.centreInScreen(dims.w, dims.h);

						// ... otherwise, locate window relative to owner
						else
						{
							location = SceneUtils.getRelativeLocation(dims.w, dims.h, owner.getX(), owner.getY(),
																	  owner.getWidth(), owner.getHeight());
						}
					}

					// ... otherwise, ensure that window rectangle intersects a screen
					else if (Screen.getScreensForRectangle(getX(), getY(), dims.w, dims.h).isEmpty())
					{
						// Remove dialog state from map
						dialogStates.remove(dialogStateKey);

						// Get location of window within primary screen
						location = SceneUtils.centreInScreen(dims.w, dims.h);
					}

					// Set location of window
					if (location != null)
					{
						// If top centre of window is not within a screen, centre window within primary screen
						if (!SceneUtils.isWithinScreen(location.getX() + 0.5 * dims.w, location.getY(), SCREEN_MARGINS))
							location = SceneUtils.centreInScreen(dims.w, dims.h);

						// Set location of window
						setX(location.getX());
						setY(location.getY());
					}

					// Perform remaining initialisation after a delay
					ExecUtils.afterDelay(WINDOW_VISIBLE_DELAY, () ->
					{
						// Make window visible
						setOpacity(1.0);

						// Initialise directory tree in chooser pane
						chooserPane.initDirectoryTree(initialDirectory, initialFilename);
					});
				});
			});

			// Save state of dialog when it is closed
			addEventHandler(WindowEvent.WINDOW_HIDING, event ->
			{
				// Update final directory and filter index
				finalDirectory = chooserPane.getDirectory();
				finalFilterIndex = chooserPane.getFilterIndex();

				// Save dialog state
				if (dialogStateKey != null)
				{
					// Uniconify window
					if (isIconified())
						setIconified(false);

					// Unmaximise window
					if (isMaximized())
						setMaximized(false);

					// Save state of window and chooser pane
					DialogState state = new DialogState((int)Math.round(getX()), (int)Math.round(getY()),
														(int)Math.round(getWidth()), (int)Math.round(getHeight()),
														chooserPane.getSplitPaneDividerPosition(),
														chooserPane.getTableViewColumnWidths().clone());
					dialogStates.put(dialogStateKey, state);
				}
			});

			// If dialog has owner, request focus on it when dialog is closed
			if (owner != null)
				addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> owner.requestFocus());

			// Fire 'cancel' button if Escape key is pressed; fire 'accept' button if Ctrl+Enter key combination is
			// pressed
			addEventFilter(KeyEvent.KEY_PRESSED, event ->
			{
				switch (event.getCode())
				{
					case ESCAPE:
						// Fire 'cancel' button
						cancelButton.fire();

						// Consume event
						event.consume();
						break;

					case ENTER:
						if (event.isControlDown())
						{
							// Fire 'accept' button
							acceptButton.fire();

							// Consume event
							event.consume();
						}
						break;

					default:
						// do nothing
						break;
				}
			});
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Fires a <i>request to close window</i> event on this dialog.
		 */

		private void requestClose()
		{
			fireEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST));
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
