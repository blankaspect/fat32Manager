/*====================================================================*\

PathnameField.java

Class: pathname field.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.textfield;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import java.util.List;

import javafx.css.PseudoClass;

import javafx.event.Event;
import javafx.event.EventType;

import javafx.geometry.Bounds;

import javafx.scene.control.TextField;

import javafx.scene.input.TransferMode;

import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.filesystem.PathnameUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.clipboard.ClipboardUtils;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

import uk.blankaspect.ui.jfx.image.MessageIcon24;

import uk.blankaspect.ui.jfx.locationchooser.LocationChooser;

import uk.blankaspect.ui.jfx.popup.MessagePopUp;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;
import uk.blankaspect.ui.jfx.style.StyleUtils;

//----------------------------------------------------------------------


// CLASS: PATHNAME FIELD


/**
 * This class implements a text field in which a pathname of a file or directory may be edited.
 * <p>
 * The methods {@link #getPathname()} and {@link #getLocation()} expand special constructs for system properties,
 * environment variables and the user's home directory:
 * </p>
 * <ol>
 *   <li>
 *     If the first character of the pathname is '~', the '~' is replaced by the absolute pathname of the user's home
 *     directory.
 *   </li>
 *   <li>
 *     For each substring of the pathname that has the form "${&lt;<i>name</i>&gt;}", where &lt;<i>name</i>&gt; matches
 *     the name of a system property or environment variable, the substring is replaced by the value of the property or
 *     variable.  A system property takes precedence over an environment variable with the same name.
 *   </li>
 * </ol>
 * <p>
 * If a field is editable, a pathname may be imported into it by dragging a file from, for example, a file browser and
 * dropping it onto the field.
 * </p>
 */

public class PathnameField
	extends TextField
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default number of columns of the text field. */
	public static final		int		DEFAULT_NUM_COLUMNS	= 40;

	/** The pseudo-class that is associated with the <i>invalid</i> state. */
	private static final	PseudoClass	INVALID_PSEUDO_CLASS	= PseudoClass.getPseudoClass(PseudoClassKey.INVALID);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.CONTROL_INNER_BACKGROUND,
			ColourKey.BACKGROUND_INVALID,
			CssSelector.builder()
						.cls(StyleClass.PATHNAME_FIELD).pseudo(PseudoClassKey.INVALID)
						.build()
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	PATHNAME_FIELD	= StyleConstants.CLASS_PREFIX + "pathname-field";
	}

	/** Keys of CSS pseudo-classes. */
	private interface PseudoClassKey
	{
		String	INVALID	= "invalid";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	BACKGROUND_INVALID	= "pathnameField.background.invalid";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	NOT_A_VALID_PATHNAME	= "'%s' is not a valid pathname.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** Flag: if {@code true}, {@link #getLocation()} displays an error message if an {@link InvalidPathException} is
		thrown. */
	private	boolean			showInvalidPathnameError;

	/** The pop-up window in which {@link #getLocation()} displays an error message if an {@link InvalidPathException}
		is thrown. */
	private	MessagePopUp	invalidPathnamePopUp;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(PathnameField.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an empty pathname field.  The field has the {@linkplain #DEFAULT_NUM_COLUMNS default
	 * number of columns}.
	 */

	public PathnameField()
	{
		// Call alternative constructor
		this(DEFAULT_NUM_COLUMNS);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a pathname field that is initialised with the specified pathname.  The field has the
	 * {@linkplain #DEFAULT_NUM_COLUMNS default number of columns}.
	 *
	 * @param pathname
	 *          the pathname with which the field will be initialised.
	 */

	public PathnameField(
		String	pathname)
	{
		// Call alternative constructor
		this(pathname, DEFAULT_NUM_COLUMNS);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a pathname field that is initialised with the specified {@linkplain Path location}.
	 * The field has the {@linkplain #DEFAULT_NUM_COLUMNS default number of columns}.
	 *
	 * @param location
	 *          the location with whose pathname the field will be initialised.
	 */

	public PathnameField(
		Path	location)
	{
		// Call alternative constructor
		this(location, DEFAULT_NUM_COLUMNS);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an empty pathname field.  The field has the specified number of columns.
	 *
	 * @param numColumns
	 *          the preferred number of columns of the field, ignored if zero or negative.
	 */

	public PathnameField(
		int	numColumns)
	{
		// Set preferred number of columns
		if (numColumns > 0)
			setPrefColumnCount(numColumns);

		// Set style class
		getStyleClass().add(StyleClass.PATHNAME_FIELD);

		// Update background colour when content of field changes
		textProperty().addListener(observable ->
		{
			// Hide 'invalid pathname' pop-up
			hideInvalidPathnamePopUp();

			// Update background colour
			try
			{
				// Test pathname
				if (!isEmpty())
					Path.of(getPathname());

				// Clear 'invalid' style
				pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
				setStyle(null);
			}
			catch (InvalidPathException e)
			{
				// Set 'invalid' style
				pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
				StyleUtils.setProperty(this, FxProperty.CONTROL_INNER_BACKGROUND.getName(),
									   ColourUtils.colourToHexString(getColour(ColourKey.BACKGROUND_INVALID)));
			}
		});

		// Handle DRAG_OVER drag events
		setOnDragOver(event ->
		{
			// Test whether field is editable and clipboard contains files
			if (isEditable() && event.getDragboard().hasFiles())
				event.acceptTransferModes(TransferMode.COPY);

			// Consume event
			event.consume();
		});

		// Handle DRAG_DROPPED drag events
		setOnDragDropped(event ->
		{
			// Get first file-system location from dragboard
			Path location = ClipboardUtils.firstLocation(event.getDragboard());

			// Indicate that drag-and-drop is complete
			event.setDropCompleted(true);

			// If there is a location, set text to its pathname
			if (location != null)
			{
				// Set text to pathname of first file
				setText(denormalisePathname(locationToPathname(location)));

				// Position caret at end of text
				end();

				// Notify listeners of import
				fireEvent(new PathnameEvent(PathnameEvent.PATHNAME_IMPORTED, this));
			}

			// Consume event
			event.consume();
		});
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a pathname field that is initialised with the specified pathname.  The field has the
	 * specified number of columns.
	 *
	 * @param pathname
	 *          the pathname with which the field will be initialised.
	 * @param numColumns
	 *          the preferred number of columns of the field, ignored if zero or negative.
	 */

	public PathnameField(
		String	pathname,
		int		numColumns)
	{
		// Call alternative constructor
		this(numColumns);

		// Set properties
		setPathname(pathname);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a pathname field that is initialised with the specified {@linkplain Path location}.
	 * The field has the specified number of columns.
	 *
	 * @param location
	 *          the location with which the field will be initialised.
	 * @param numColumns
	 *          the number of columns of the field.
	 */

	public PathnameField(
		Path	location,
		int		numColumns)
	{
		// Call alternative constructor
		this(numColumns);

		// Set properties
		if (location != null)
			setLocation(location);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the specified location to an absolute pathname and returns the result.
	 *
	 * @param  location
	 *           the location that will be converted to an absolute pathname.  The location may be {@code null}.
	 * @return the absolute pathname of {@code location}, or {@code null} if {@code location} is {@code null}.
	 */

	private static String locationToPathname(
		Path	location)
	{
		return (location == null) ? null : location.toAbsolutePath().toString();
	}

	//------------------------------------------------------------------

	/**
	 * Replaces all occurrences of '/' in the specified pathname with the system's line separator and returns the
	 * result.
	 *
	 * @param  pathname
	 *           the pathname that will be denormalised.  The pathname may be {@code null}.
	 * @return {@code pathname} with all occurrences of '/' replaced with the system's line separator, or {@code null}
	 *         if {@code pathname} is {@code null}.
	 */

	private static String denormalisePathname(
		String	pathname)
	{
		return (pathname == null) ? null : pathname.replace('/', File.separatorChar);
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
		Color colour = StyleManager.INSTANCE.getColour(key);
		return (colour == null) ? StyleManager.DEFAULT_COLOUR : colour;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the content of this field (the pathname) after applying the following steps to it:
	 * <ol>
	 *   <li>
	 *     If the first character of the pathname is '~', the '~' is replaced by the absolute pathname of the user's
	 *     home directory.
	 *   </li>
	 *   <li>
	 *     For each substring of the pathname that has the form "${&lt;<i>name</i>&gt;}", where &lt;<i>name</i>&gt;
	 *     matches the name of a system property or environment variable, the substring is replaced by the value of the
	 *     property or variable.  A system property takes precedence over an environment variable with the same name.
	 *   </li>
	 * </ol>
	 *
	 * @return the content of this field after applying the steps described above.
	 */

	public String getPathname()
	{
		return PathnameUtils.parsePathname(getText());
	}

	//------------------------------------------------------------------

	/**
	 * Returns the content of this field as a {@linkplain Path location} after applying the steps described in {@link
	 * #getPathname()}.  If the pathname is invalid, an error message is displayed in a pop-up window below the field.
	 *
	 * @return the content of this field as a {@linkplain Path location} after applying the steps described in {@link
	 *         #getPathname()}, or {@code null} if the field is empty or the return value of {@link #getPathname()} is
	 *         not a valid pathname.
	 */

	public Path getLocation()
	{
		Path location = null;
		String pathname = getPathname();
		if (!StringUtils.isNullOrEmpty(pathname))
		{
			try
			{
				location = Path.of(pathname);
			}
			catch (InvalidPathException e)
			{
				// Hide 'invalid pathname' pop-up
				hideInvalidPathnamePopUp();

				// Show 'invalid pathname' pop-up
				if (showInvalidPathnameError)
				{
					invalidPathnamePopUp = new MessagePopUp(MessageIcon24.ERROR,
															String.format(ErrorMsg.NOT_A_VALID_PATHNAME, pathname));
					Bounds bounds = localToScreen(getLayoutBounds());
					invalidPathnamePopUp.show(this, bounds.getMinX(), bounds.getMaxY() - 1.0);
				}
			}
		}
		return location;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this field is empty.
	 *
	 * @return {@code true} if this field is empty.
	 */

	public boolean isEmpty()
	{
		return (getLength() == 0);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the content of this field to the specified pathname, converting it to UNIX style if the field's UNIX-style
	 * flag is set.
	 *
	 * @param pathname
	 *          the pathname that will set on this field.
	 */

	public void setPathname(
		String	pathname)
	{
		setText(denormalisePathname(pathname));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the content of this field to the absolute pathname of the specified location.
	 *
	 * @param location
	 *          the location to whose absolute pathname the content of this field will be set.
	 */

	public void setLocation(
		Path	location)
	{
		setPathname(locationToPathname(location));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the flag that controls whether {@link #getLocation()} displays an error message if an {@link
	 * InvalidPathException} is thrown.
	 *
	 * @param showError
	 *          if {@code true}, {@link #getLocation()} will display an error message if an {@link InvalidPathException}
	 *          is thrown.
	 */

	public void setShowInvalidPathnameError(
		boolean	showError)
	{
		showInvalidPathnameError = showError;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the initial directory and filename of the specified location chooser from the location that is returned by
	 * {@link #getLocation()}, if it is valid.
	 * <p>
	 * If the location that is returned by {@link #getLocation()} is not {@code null}, the following steps are
	 * performed:
	 * </p>
	 * <ol>
	 *   <li>
	 *     The initial filename of the chooser is set to the filename of the location.
	 *   </li>
	 *   <li>
	 *     The location is made absolute and its parent directory (referred to subsequently as <i>the parent</i>) is
	 *     extracted.
	 *   </li>
	 *   <li>
	 *     If the parent denotes an existing directory, the initial directory of the chooser is set to the parent.
	 *   </li>
	 * </ol>
	 * <p>
	 * If {@link #getLocation()} returns {@code null}, there is no effect on the location chooser.
	 * </p>
	 *
	 * @param chooser
	 *          the location chooser that will be initialised.
	 */

	public void initChooser(
		LocationChooser	chooser)
	{
		Path location = getLocation();
		if (location != null)
		{
			chooser.setInitialFilename(location.getFileName().toString());
			chooser.initDirectoryWithParent(location);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Sets the initial directory and filename of the specified location chooser from the location that is returned by
	 * {@link #getLocation()}, if it is valid; otherwise, sets the initial directory of the chooser to the specified
	 * default location, which is expected to exist.
	 * <p>
	 * If the location that is returned by {@link #getLocation()} is not {@code null}, the following steps are
	 * performed:
	 * </p>
	 * <ol>
	 *   <li>
	 *     The initial filename of the chooser is set to the filename of the location.
	 *   </li>
	 *   <li>
	 *     The location is made absolute and its parent directory (referred to subsequently as <i>the parent</i>) is
	 *     extracted.
	 *   </li>
	 *   <li>
	 *     If the parent denotes an existing directory, the initial directory of the chooser is set to the parent.
	 *   </li>
	 *   <li>
	 *     If the parent does not denote an existing directory, the initial directory of the chooser is set to the
	 *     default location.
	 *   </li>
	 * </ol>
	 * <p>
	 * If {@link #getLocation()} returns {@code null}, the initial directory of the chooser is set to the specified
	 * default location.
	 * </p>
	 *
	 * @param chooser
	 *          the location chooser that will be initialised.
	 * @param defaultDirectory
	 *          the location of the default initial directory.
	 */

	public void initChooser(
		LocationChooser	chooser,
		Path			defaultDirectory)
	{
		Path location = getLocation();
		if (location != null)
			chooser.setInitialFilename(location.getFileName().toString());
		chooser.initDirectoryWithParent(location, defaultDirectory);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the initial directory and filename of the specified location chooser from the location that is returned by
	 * {@link #getLocation()}, if it is valid; otherwise, sets the initial directory of the chooser to one of two
	 * specified default locations, as described below.
	 * <p>
	 * If the location that is returned by {@link #getLocation()} is not {@code null}, the following steps are
	 * performed:
	 * </p>
	 * <ol>
	 *   <li>
	 *     The initial filename of the chooser is set to the filename of the location.
	 *   </li>
	 *   <li>
	 *     The location is made absolute and its parent directory (referred to subsequently as <i>the parent</i>) is
	 *     extracted.
	 *   </li>
	 *   <li>
	 *     If the parent denotes an existing directory, the initial directory of the chooser is set to the parent.
	 *   </li>
	 *   <li>
	 *     If the parent does not denote an existing directory and the first default location denotes an existing
	 *     directory, the initial directory of the chooser is set to the first default location.
	 *   </li>
	 *   <li>
	 *     If neither the parent nor the first default location denotes an existing directory, the initial directory of
	 *     the chooser is set to the second default location.
	 *   </li>
	 * </ol>
	 * <p>
	 * If {@link #getLocation()} returns {@code null}, the initial directory of the chooser is set to one of the default
	 * locations as described in steps 4 and 5 above.
	 * </p>
	 *
	 * @param chooser
	 *          the location chooser that will be initialised.
	 * @param defaultDirectory1
	 *          the location of the first default initial directory.
	 * @param defaultDirectory2
	 *          the location of the second default initial directory.
	 */

	public void initChooser(
		LocationChooser	chooser,
		Path			defaultDirectory1,
		Path			defaultDirectory2)
	{
		initChooser(chooser, LocationChooser.existingDirectory(defaultDirectory1, defaultDirectory2));
	}

	//------------------------------------------------------------------

	/**
	 * Hides the pop-up window in which {@link #getLocation()} displays an error message if an {@link
	 * InvalidPathException} is thrown.
	 */

	protected void hideInvalidPathnamePopUp()
	{
		if (invalidPathnamePopUp != null)
		{
			invalidPathnamePopUp.hide();
			invalidPathnamePopUp = null;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: PATHNAME-FIELD EVENT


	/**
	 * This class implements an event that is fired by a {@linkplain PathnameField pathname field}.
	 */

	public static class PathnameEvent
		extends Event
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** An event that signals that a pathname has been imported into the field. */
		public static final	EventType<PathnameEvent>	PATHNAME_IMPORTED	= new EventType<>("PATHNAME_IMPORTED");

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an event of the specified type and for the specified source.
		 *
		 * @param eventType
		 *          the type of the event.
		 * @param source
		 *          the source of the event.
		 */

		private PathnameEvent(
			EventType<PathnameEvent>	eventType,
			PathnameField				source)
		{
			// Call superclass constructor
			super(source, null, eventType);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * {@inheritDoc}
		 */

		@Override
		public PathnameField getSource()
		{
			return (PathnameField)source;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
