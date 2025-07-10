/*====================================================================*\

StyleManager.java

Class: JavaFX style manager.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import java.math.BigInteger;

import java.net.URI;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.scene.Scene;

import javafx.scene.paint.Color;

import javafx.stage.Window;

import uk.blankaspect.common.collection.ArraySet;

import uk.blankaspect.common.colourproperty.ColourPropertyConstants;

import uk.blankaspect.common.css.CssConstants;
import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssUtils;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.LocationException;
import uk.blankaspect.common.exception2.UnexpectedRuntimeException;

import uk.blankaspect.common.filesystem.DirectoryUtils;
import uk.blankaspect.common.filesystem.FileSystemUtils;

import uk.blankaspect.common.logging.Logger;

import uk.blankaspect.common.misc.SystemUtils;

import uk.blankaspect.common.resource.ResourceUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.dialog.ErrorDialog;

import uk.blankaspect.ui.jfx.style.themes.Themes;

//----------------------------------------------------------------------


// CLASS: JAVAFX STYLE MANAGER


public class StyleManager
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		String	DEFAULT_THEME_ID	= "light";

	public static final		Color	DEFAULT_COLOUR	= Color.grayRgb(128, 0.5);

	private static final	String	INITIALISE_THEMES_STR		= "Initialise themes";
	private static final	String	REGISTER_CLASS_STR			= "Register class";
	private static final	String	LOAD_DEFAULT_COLOURS_STR	= "Load default colours";
	private static final	String	LOAD_DEPENDENCY_STR			= "Load dependency";
	private static final	String	APPLY_STYLE_SHEET_STR		= "Apply style sheet";
	private static final	String	WRITE_STYLE_SHEET_STR		= "Write style sheet";
	private static final	String	NULL_ID_STR					= "Null ID";

	private static final	char	CLASS_NAME_LIST_COMMENT_CHAR	= '#';

	private static final	char	STYLE_SHEET_ID_DELIMITER	= '~';
	private static final	String	STYLE_SHEET_ID	=
			STYLE_SHEET_ID_DELIMITER + StyleManager.class.getName() + STYLE_SHEET_ID_DELIMITER;

	private static final	String	DEFAULT_COLOURS_FILENAME	= "colours.properties";
	private static final	String	BASE_STYLE_SHEET_FILENAME	= "base.css";

	private static final	String	URI_DATA_SCHEME_PREFIX	= "data:";
	private static final	String	URI_FILE_SCHEME_PREFIX	= "file:";

	private static final	String	HASH_NAME	= "SHA-1";

	private static final	MessageDigest	HASH;

	private static final	BigInteger	ID_MASK;

	public static final		StyleManager	INSTANCE	= new StyleManager();

	/** Keys of system properties. */
	public interface SystemPropertyKey
	{
		String	THEME	= "blankaspect.ui.jfx.theme";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FAILED_TO_CREATE_TEMPORARY_DIRECTORY =
				"Failed to create a temporary directory.";

		String	NO_DEFAULT_THEME =
				"There is no default theme.";

		String	UNSUPPORTED_THEME =
				"The theme with ID '%s' is not supported.";

		String	NO_COLOUR_FOR_PROPERTY1 =
				"Colour key: %s\nThere is no colour for the property.";

		String	NO_COLOUR_FOR_PROPERTY2 =
				"Selector(s): %s\nProperty: %s\n" + NO_COLOUR_FOR_PROPERTY1;

		String	FAILED_TO_READ_DEFAULT_COLOUR_PROVIDERS =
				"Failed to read the list of default-colour providers.";

		String	CANNOT_FIND_DEFAULT_COLOUR_PROVIDER =
				"Class: %s\nCannot find the default-colour provider.";

		String	FAILED_TO_CONVERT_URI =
				"Failed to convert the URI to a file-system location.";

		String	ERROR_READING_FILE =
				"An error occurred when reading the file.";

		String	FAILED_TO_LOAD_CLASS =
				"Class: %s\nFailed to load the class.";

		String	DEPENDENCY_CYCLE =
				"Class: %s\nA cycle of dependencies was detected when registering the class.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String								id;
	private	List<AbstractTheme>					themes;
	private SimpleObjectProperty<AbstractTheme>	theme;
	private	Map<Class<?>, List<ColourProperty>>	colourProperties;
	private	Map<Class<?>, List<CssRuleSet>>		ruleSets;
	private	List<Class<?>>						registeringClasses;
	private	Path								tempDirectory;
	private	boolean								notUsingStyleSheet;
	private	int									styleSheetIndex;
	private	String								styleSheetFilename;
	private	Path								styleSheetLocation;
	private	byte[]								styleSheetHashValue;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Initialise hash function
		try
		{
			HASH = MessageDigest.getInstance(HASH_NAME);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new UnexpectedRuntimeException(e);
		}

		// Initialise mask for random ID
		byte[] magnitude = new byte[8];
		Arrays.fill(magnitude, (byte)0xFF);
		ID_MASK = new BigInteger(1, magnitude);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private StyleManager()
	{
		// Initialise instance variables
		theme = new SimpleObjectProperty<>();
		colourProperties = new LinkedHashMap<>();
		ruleSets = new LinkedHashMap<>();
		registeringClasses = new ArrayList<>();
		notUsingStyleSheet = Boolean.getBoolean(StyleConstants.SystemPropertyKey.NO_STYLE_SHEET);

		// Initialise themes
		try
		{
			// Initialise base class of themes
			AbstractTheme.init(this);

			// Create themes
			themes = Themes.instances();

			// Initialise themes
			for (AbstractTheme theme : themes)
			{
				// Get class of theme
				Class<?> cls = theme.getClass();

				// Read base style sheet of theme
				theme.readBaseStyleSheet(ResourceUtils.normalisedPathname(cls, BASE_STYLE_SHEET_FILENAME));

				// Load default colours of theme
				String pathname = ResourceUtils.normalisedPathname(cls, DEFAULT_COLOURS_FILENAME);
				if (ResourceUtils.hasResource(cls, pathname))
					theme.loadDefaultColours(pathname, colourKeyPrefix(cls));
			}
		}
		catch (BaseException e)
		{
			Logger.INSTANCE.error(getClass().getSimpleName() + " : " + INITIALISE_THEMES_STR, e);
		}

		// Set default theme
		AbstractTheme defaultTheme = findDefaultTheme();
		if (defaultTheme == null)
			Logger.INSTANCE.error(getClass().getSimpleName() + " : " + ErrorMsg.NO_DEFAULT_THEME);
		else
			theme.set(defaultTheme);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String colourKeyPrefix(
		Class<?>	cls)
	{
		return cls.getCanonicalName() + ".";
	}

	//------------------------------------------------------------------

	public static Map<String, Color> readColours(
		Class<?>	cls)
		throws LocationException
	{
		// Get ID of theme from system property
		String themeId = System.getProperty(SystemPropertyKey.THEME, DEFAULT_THEME_ID);

		// Read colours from resource
		String pathname = coloursResourcePathname(cls, themeId);
		return ResourceUtils.hasResource(cls, pathname) ? AbstractTheme.readColours(cls, pathname, colourKeyPrefix(cls))
														: null;
	}

	//------------------------------------------------------------------

	public static String randomId()
	{
		UUID uid = UUID.randomUUID();
		BigInteger u64 = BigInteger.valueOf(uid.getMostSignificantBits()).and(ID_MASK);
		BigInteger l64 = BigInteger.valueOf(uid.getLeastSignificantBits()).and(ID_MASK);
		BigInteger id128 = u64.shiftLeft(64).or(l64);
		return StringUtils.padBefore(id128.toString(36), 25, '0');
	}

	//------------------------------------------------------------------

	private static String resourcePath(
		String...	elements)
	{
		return StringUtils.join(ResourceUtils.PATHNAME_SEPARATOR_CHAR, elements);
	}

	//------------------------------------------------------------------

	private static String coloursResourcePathname(
		Class<?>	cls,
		String		themeId)
	{
		String pathname = resourcePath(ColourPropertyConstants.THEMES_DIRECTORY_NAME, themeId,
									   cls.getSimpleName() + ColourPropertyConstants.COLOUR_PROPERTIES_FILENAME_SUFFIX);
		return ResourceUtils.normalisedPathname(cls, pathname);
	}

	//------------------------------------------------------------------

	private static boolean isManagedStyleSheet(
		String	uri)
		throws LocationException
	{
		// Initialise result
		boolean managed = false;

		// Case: data-scheme URI
		if (uri.startsWith(URI_DATA_SCHEME_PREFIX))
		{
			// Test for style-sheet ID at start of URI
			int startIndex = uri.indexOf(STYLE_SHEET_ID_DELIMITER);
			if (startIndex >= 0)
			{
				int endIndex = uri.indexOf(STYLE_SHEET_ID_DELIMITER, startIndex + 1);
				managed = (endIndex >= 0) && uri.substring(startIndex, endIndex + 1).equals(STYLE_SHEET_ID);
			}
		}

		// Case: file-scheme URI
		else if (uri.startsWith(URI_FILE_SCHEME_PREFIX))
		{
			// Convert style-sheet URI to file-system location
			Path location = null;
			try
			{
				location = Path.of(URI.create(uri));
			}
			catch (Exception e)
			{
				throw new LocationException(ErrorMsg.FAILED_TO_CONVERT_URI, uri);
			}

			// Read first line of file and test for style-sheet ID
			try (BufferedReader reader = Files.newBufferedReader(location))
			{
				managed = reader.readLine().contains(STYLE_SHEET_ID);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.ERROR_READING_FILE, e, location);
			}
		}

		// Return result
		return managed;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void setId(
		String	id)
	{
		// Validate argument
		if (id == null)
			throw new IllegalArgumentException(NULL_ID_STR);

		// Update instance variable
		this.id = id;
	}

	//------------------------------------------------------------------

	public void setStyleSheetFilename(
		String	filename)
	{
		// Validate argument
		if (filename == null)
			throw new IllegalArgumentException("Null filename");

		// Update instance variable
		styleSheetFilename = filename;
	}

	//------------------------------------------------------------------

	public AbstractTheme getTheme()
	{
		return theme.get();
	}

	//------------------------------------------------------------------

	public List<AbstractTheme> getThemes()
	{
		return Collections.unmodifiableList(themes);
	}

	//------------------------------------------------------------------

	public String getThemeId()
	{
		AbstractTheme theme = getTheme();
		return (theme == null) ? null : theme.id();
	}

	//------------------------------------------------------------------

	public List<String> getThemeIds()
	{
		return themes.stream().map(theme -> theme.id()).toList();
	}

	//------------------------------------------------------------------

	public ReadOnlyObjectProperty<AbstractTheme> themeProperty()
	{
		return theme;
	}

	//------------------------------------------------------------------

	public boolean hasTheme(
		String	id)
	{
		return (findTheme(id) != null);
	}

	//------------------------------------------------------------------

	public AbstractTheme findTheme(
		String	id)
	{
		// Validate argument
		if (id == null)
			throw new IllegalArgumentException(NULL_ID_STR);

		// Search for theme with target ID
		for (AbstractTheme theme : themes)
		{
			if (theme.id().equals(id))
				return theme;
		}
		return null;
	}

	//------------------------------------------------------------------

	public AbstractTheme findDefaultTheme()
	{
		return findTheme(DEFAULT_THEME_ID);
	}

	//------------------------------------------------------------------

	public void selectTheme(
		String	id)
	{
		// Validate argument
		if (id == null)
			throw new IllegalArgumentException(NULL_ID_STR);

		// If theme is different from current theme, update theme and invalidate style sheet
		if (!id.equals(getThemeId()))
		{
			// Find theme for ID
			AbstractTheme theme = findTheme(id);
			if (theme == null)
				throw new UnsupportedThemeException();

			// Update theme
			this.theme.set(theme);

			// Invalidate style sheet
			invalidateStyleSheet();
		}
	}

	//------------------------------------------------------------------

	public void selectThemeOrDefault(
		String	id)
	{
		// Validate argument
		if (id == null)
			throw new IllegalArgumentException(NULL_ID_STR);

		// If theme is different from current theme, update theme and invalidate style sheet
		if (!id.equals(getThemeId()))
		{
			// Find theme for ID
			AbstractTheme theme = findTheme(id);
			if (theme == null)
			{
				Logger.INSTANCE.error(getClass().getSimpleName() + " : "
										+ String.format(ErrorMsg.UNSUPPORTED_THEME, id));
				theme = findDefaultTheme();
			}

			// Update theme and invalidate style sheet
			if (theme != null)
			{
				// Update theme
				this.theme.set(theme);

				// Invalidate style sheet
				invalidateStyleSheet();
			}
		}
	}

	//------------------------------------------------------------------

	public Color getColour(
		String	key)
	{
		AbstractTheme theme = getTheme();
		if (theme == null)
			theme = findDefaultTheme();
		return (theme == null) ? null : theme.getColour(key);
	}

	//------------------------------------------------------------------

	public Color getColourOrDefault(
		String	key)
	{
		Color colour = getColour(key);
		if (colour == null)
		{
			colour = DEFAULT_COLOUR;
			System.err.println(String.format(ErrorMsg.NO_COLOUR_FOR_PROPERTY1, key));
		}
		return colour;
	}

	//------------------------------------------------------------------

	public void register(
		Class<?>	cls,
		Class<?>...	dependencies)
	{
		register(cls, List.of(), List.of(), dependencies);
	}

	//------------------------------------------------------------------

	public void register(
		Class<?>				cls,
		List<ColourProperty>	colourProperties,
		Class<?>...				dependencies)
	{
		register(cls, colourProperties, List.of(), dependencies);
	}

	//------------------------------------------------------------------

	public void register(
		Class<?>				cls,
		List<ColourProperty>	colourProperties,
		List<CssRuleSet>		ruleSets,
		Class<?>...				dependencies)
	{
		try
		{
			// Test for cycle of dependencies
			if (registeringClasses.contains(cls))
			{
				reportException(REGISTER_CLASS_STR, new BaseException(ErrorMsg.DEPENDENCY_CYCLE, cls.getName()));
				return;
			}

			// Add class to list
			registeringClasses.add(cls);

			// Load default colours
			try
			{
				loadDefaultColours(cls);
			}
			catch (LocationException e)
			{
				reportException(LOAD_DEFAULT_COLOURS_STR, e);
			}

			// Load dependencies, which may cause this method to be re-entered
			registerDependencies(dependencies);

			// Add colour properties to map
			this.colourProperties.computeIfAbsent(cls, key -> new ArrayList<>()).addAll(colourProperties);

			// Add rule sets to map
			this.ruleSets.computeIfAbsent(cls, key -> new ArrayList<>()).addAll(ruleSets);

			// Invalidate style sheet
			invalidateStyleSheet();
		}
		finally
		{
			// Remove class from list
			registeringClasses.remove(cls);
		}
	}

	//------------------------------------------------------------------

	public void registerRuleSets(
		Class<?>			cls,
		List<CssRuleSet>	ruleSets,
		Class<?>...			dependencies)
	{
		register(cls, List.of(), ruleSets, dependencies);
	}

	//------------------------------------------------------------------

	public void registerDependencies(
		Class<?>...	classes)
	{
		// Load classes
		for (Class<?> cls : classes)
		{
			String name = cls.getName();
			try
			{
				Class.forName(name);
			}
			catch (Throwable e)
			{
				reportException(LOAD_DEPENDENCY_STR, new BaseException(ErrorMsg.FAILED_TO_LOAD_CLASS, e, name));
			}
		}
	}

	//------------------------------------------------------------------

	public void updateColourProperties(
		Class<?>					cls,
		Collection<ColourProperty>	colourProperties)
	{
		// Update map of colour properties
		this.colourProperties.put(cls, new ArrayList<>(colourProperties));

		// Invalidate style sheet
		invalidateStyleSheet();
	}

	//------------------------------------------------------------------

	public void updateRuleSets(
		Class<?>				cls,
		Collection<CssRuleSet>	ruleSets)
	{
		// Update map of rule sets
		this.ruleSets.put(cls, new ArrayList<>(ruleSets));

		// Invalidate style sheet
		invalidateStyleSheet();
	}

	//------------------------------------------------------------------

	public boolean notUsingStyleSheet()
	{
		return notUsingStyleSheet;
	}

	//------------------------------------------------------------------

	public void invalidateStyleSheet()
	{
		styleSheetHashValue = null;
	}

	//------------------------------------------------------------------

	public void addStyleSheet(
		Scene	scene)
	{
		if (!notUsingStyleSheet)
			scene.getStylesheets().add(getStyleSheetUri());
	}

	//------------------------------------------------------------------

	public void setStyleSheet(
		Scene	scene)
	{
		if (!notUsingStyleSheet)
			scene.getStylesheets().setAll(getStyleSheetUri());
	}

	//------------------------------------------------------------------

	public void reapplyStylesheet()
	{
		// Test whether style sheets are being used
		if (notUsingStyleSheet)
			return;

		// Initialise URI of current style sheet
		String uri = null;

		// Search for JavaFX windows whose scenes have style sheets that were applied by this class
		for (Window window : Window.getWindows())
		{
			// Get list of style sheets of scene
			List<String> uris = window.getScene().getStylesheets();

			// If scene has style sheets that were applied by this class, replace them with current style sheet
			boolean replaced = false;
			for (int i = uris.size() - 1; i >= 0; i--)
			{
				try
				{
					if (isManagedStyleSheet(uris.get(i)))
					{
						if (replaced)
							uris.remove(i);
						else
						{
							if (uri == null)
								uri = getStyleSheetUri();
							uris.set(i, uri);
							replaced = true;
						}
					}
				}
				catch (LocationException e)
				{
					reportException(APPLY_STYLE_SHEET_STR, e);
				}
			}
		}
	}

	//------------------------------------------------------------------

	public void reapplyStylesheet(
		Scene	scene)
	{
		// Test whether style sheets are being used
		if (notUsingStyleSheet)
			return;

		// Get list of style sheets of scene
		List<String> uris = scene.getStylesheets();

		// If scene has style sheets that were applied by this class, replace them with current style sheet
		boolean replaced = false;
		for (int i = uris.size() - 1; i >= 0; i--)
		{
			try
			{
				if (isManagedStyleSheet(uris.get(i)))
				{
					if (replaced)
						uris.remove(i);
					else
					{
						uris.set(i, getStyleSheetUri());
						replaced = true;
					}
				}
			}
			catch (LocationException e)
			{
				reportException(APPLY_STYLE_SHEET_STR, e);
			}
		}
	}

	//------------------------------------------------------------------

	public void loadDefaultColours(
		String	listPathname)
		throws LocationException
	{
		// Read list of class names from resource
		List<String> lines = null;
		try
		{
			lines = ResourceUtils.readLines(getClass(), listPathname);
		}
		catch (IOException e)
		{
			throw new LocationException(ErrorMsg.FAILED_TO_READ_DEFAULT_COLOUR_PROVIDERS, e, listPathname);
		}

		// Parse list of class names
		int lineIndex = 0;
		while (lineIndex < lines.size())
		{
			// Remove any comment from line
			String line = lines.get(lineIndex++);
			int index = line.indexOf(CLASS_NAME_LIST_COMMENT_CHAR);
			if (index >= 0)
				line = line.substring(0, index);

			// Remove leading and trailing whitespace
			String className = line.strip();

			// Skip empty line
			if (className.isEmpty())
				continue;

			// Load class
			Class<?> cls = null;
			try
			{
				cls = Class.forName(className, false, getClass().getClassLoader());
			}
			catch (Throwable e)
			{
				throw new LocationException(ErrorMsg.CANNOT_FIND_DEFAULT_COLOUR_PROVIDER, e, listPathname, className);
			}

			// Load default colours for class
			loadDefaultColours(cls);
		}
	}

	//------------------------------------------------------------------

	public void loadDefaultColours(
		Class<?>	cls)
		throws LocationException
	{
		for (AbstractTheme theme : themes)
		{
			String pathname = coloursResourcePathname(cls, theme.id());
			if (ResourceUtils.hasResource(cls, pathname))
				theme.loadDefaultColours(pathname, colourKeyPrefix(cls));
		}
	}

	//------------------------------------------------------------------

	public void loadColours(
		String	filenamePrefix,
		Path	directory,
		String	keyPrefix)
		throws FileException
	{
		for (AbstractTheme theme : themes)
		{
			String pathname = ColourPropertyConstants.THEMES_DIRECTORY_NAME + File.separator
								+ theme.id() + File.separator
								+ filenamePrefix + ColourPropertyConstants.COLOUR_PROPERTIES_FILENAME_SUFFIX;
			Path location = directory.resolve(pathname);
			if (Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS))
				theme.loadColours(location, keyPrefix);
		}
	}

	//------------------------------------------------------------------

	public void loadColours(
		Map<String, Path>	locations,
		String				keyPrefix)
		throws FileException
	{
		for (String id : locations.keySet())
		{
			AbstractTheme theme = findTheme(id);
			if (theme != null)
				theme.loadColours(locations.get(id), keyPrefix);
		}
	}

	//------------------------------------------------------------------

	public String getCssText()
	{
		// Allocate buffer for CSS test
		StringBuilder buffer = new StringBuilder(4096);

		// Append comment containing ID
		buffer.append(CssConstants.COMMENT_PREFIX);
		buffer.append(' ');
		buffer.append(STYLE_SHEET_ID);
		buffer.append(' ');
		buffer.append(CssConstants.COMMENT_SUFFIX);
		buffer.append("\n\n");

		// Get current theme
		AbstractTheme theme = getTheme();

		// Append base style sheet
		String styleSheet = theme.getBaseStyleSheet();
		if (styleSheet != null)
			buffer.append(styleSheet);

		// Initialise list of all rule sets
		List<CssRuleSet> allRuleSets = new ArrayList<>();

		// Create union of classes for which there are colour properties and class for which there are rule sets
		List<Class<?>> classes = new ArraySet<>();
		classes.addAll(colourProperties.keySet());
		classes.addAll(ruleSets.keySet());

		// Populate list of all rule sets
		for (Class<?> cls : classes)
		{
			// Add rule sets
			for (CssRuleSet ruleSet : ruleSets.get(cls))
			{
				CssRuleSet ruleSetCopy = ruleSet.clone();
				if (theme != null)
					theme.resolveColourProperties(ruleSetCopy);
				allRuleSets.add(ruleSetCopy);
			}

			// Add rule sets for colour properties
			if (theme != null)
			{
				for (ColourProperty property : colourProperties.get(cls))
				{
					CssRuleSet ruleSet = property.toRuleSet(theme);
					if (ruleSet == null)
					{
						String selectors = String.join(", ", property.getSelectors());
						String colourKey = property.getColourKey();
						System.err.println(String.format(ErrorMsg.NO_COLOUR_FOR_PROPERTY2, selectors,
														 property.getFxProperty().getName(),
														 (colourKey == null) ? "" : colourKey));
					}
					else
						allRuleSets.add(ruleSet);
				}
			}
		}

		// Merge rule sets
		CssRuleSet.merge(allRuleSets);

		// Convert rule sets to text
		for (CssRuleSet ruleSet : allRuleSets)
		{
			if (!buffer.isEmpty())
				buffer.append('\n');

			for (String str : ruleSet.toStrings())
			{
				buffer.append(str);
				buffer.append('\n');
			}
		}

		// Return CSS text
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public String getStyleSheetUri()
	{
		String location = null;
		if (styleSheetFilename == null)
			location = CssUtils.styleSheetToDataUri(getCssText());
		else
		{
			try
			{
				location = writeStyleSheet().toUri().toString();
			}
			catch (FileException e)
			{
				reportException(WRITE_STYLE_SHEET_STR, e);
			}
		}
		return location;
	}

	//------------------------------------------------------------------

	public Path writeStyleSheet()
		throws FileException
	{
		return writeStyleSheet(false);
	}

	//------------------------------------------------------------------

	public Path writeStyleSheet(
		boolean	unconditional)
		throws FileException
	{
		// Test for style-sheet filename
		if (styleSheetFilename == null)
			throw new IllegalStateException("No style-sheet filename");

		// Get text of style sheet
		String text = getCssText();

		// Calculate hash of text
		HASH.reset();
		byte[] hashValue = HASH.digest(text.getBytes(StandardCharsets.UTF_8));

		// If file-writing is unconditional or style sheet has changed since it was last written to file, write
		// style-sheet file
		if (unconditional || !Arrays.equals(hashValue, styleSheetHashValue))
		{
			Path file = getTempDirectory().resolve(String.format(styleSheetFilename, styleSheetIndex));
			IOUtils.writeTextFile(file, text);
			++styleSheetIndex;
			styleSheetLocation = file;
			styleSheetHashValue = hashValue;
		}

		// Return location of style sheet
		return styleSheetLocation;
	}

	//------------------------------------------------------------------

	public Path getTempDirectory()
		throws FileException
	{
		if (tempDirectory == null)
		{
			// Get pathname of system temporary directory
			Path sysTempDirectory = SystemUtils.tempDirectory();

			// Find available subdirectory of system temporary directory
			String namePrefix = ((id == null) ? getClass().getSimpleName() + "-" + randomId() : id) + "-";
			Path directory = FileSystemUtils.findAvailableLocationDateTime(sysTempDirectory, namePrefix, "", 0);

			// Create directory
			try
			{
				tempDirectory = Files.createDirectories(directory);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CREATE_TEMPORARY_DIRECTORY, e, directory);
			}

			// Add shutdown hook to delete temporary directory
			Runtime.getRuntime().addShutdownHook(new Thread(() ->
			{
				try
				{
					DirectoryUtils.deleteDirectory(tempDirectory);
					tempDirectory = null;
				}
				catch (FileException e)
				{
					e.printStackTrace();
					Logger.INSTANCE.error(e);
				}
			}));
		}
		return tempDirectory;
	}

	//------------------------------------------------------------------

	private void reportException(
		String			message,
		BaseException	exception)
	{
		String text = getClass().getSimpleName() + " : " + message;
		Logger.INSTANCE.error(text, exception);
		ErrorDialog.show(null, text, exception);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: 'UNSUPPORTED THEME' EXCEPTION


	public static class UnsupportedThemeException
		extends RuntimeException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private UnsupportedThemeException()
		{
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
