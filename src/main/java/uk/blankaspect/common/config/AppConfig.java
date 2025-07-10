/*====================================================================*\

AppConfig.java

Class: configuration of an application.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.config;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import uk.blankaspect.common.basictree.AbstractNode;
import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.common.exception2.FileException;

import uk.blankaspect.common.json.JsonGenerator;
import uk.blankaspect.common.json.JsonParser;
import uk.blankaspect.common.json.JsonUtils;

//----------------------------------------------------------------------


// CLASS: CONFIGURATION OF AN APPLICATION


/**
 * This class implements the configuration of an application.  The configuration may be stored as a JSON file.
 */

public class AppConfig
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The version of a configuration file. */
	private static final	int		VERSION	= 0;

	/** The suffix of the name of a configuration file. */
	private static final	String	CONFIG_FILENAME_SUFFIX	= "-config.json";

	/** Miscellaneous strings. */
	private static final	String	CONFIGURATION_STR	= "configuration";

	/** Keys of properties. */
	private interface PropertyKey
	{
		String	CONFIGURATION	= "configuration";
		String	DESCRIPTION		= "description";
		String	ID				= "id";
		String	VERSION			= "version";
	}

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	APP_NO_CONFIG_FILE	= "blankaspect.app.noConfigFile";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	ERROR_READING_FILE				= "An error occurred when reading the file.";
		String	ERROR_WRITING_FILE				= "An error occurred when writing the file.";
		String	NOT_A_CONFIG_FILE				= "The file is not a configuration file for %s.";
		String	MALFORMED_CONFIG_FILE			= "The configuration file is malformed.";
		String	UNEXPECTED_CONFIG_FILE_FORMAT	= "The configuration file does not have the expected format.";
		String	UNSUPPORTED_CONFIG_FILE_VERSION	= "Version %d of the configuration file is not supported.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The identifier of a configuration file. */
	private	String	id;

	/** The key of the application. */
	private	String	appKey;

	/** The short name of the application. */
	private	String	appShortName;

	/** The long name of the application. */
	private	String	appLongName;

	/** The parent directory of the configuration file. */
	private	Path	directory;

	/** The location of the configuration file. */
	private	Path	file;

	/** The root node of this configuration. */
	private	MapNode	config;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a configuration of an application.
	 *
	 * @param id
	 *          the identifier of a configuration file.
	 * @param appKey
	 *          the key of the application.
	 * @param appShortName
	 *          the short name of the application.
	 * @param appLongName
	 *          the long name of the application.
	 */

	public AppConfig(
		String	id,
		String	appKey,
		String	appShortName,
		String	appLongName)
	{
		// Call alternative constructor
		this(id, appKey, appShortName, appLongName, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a configuration of an application.
	 *
	 * @param id
	 *          the identifier of a configuration file.
	 * @param appKey
	 *          the key of the application.
	 * @param appShortName
	 *          the short name of the application.
	 * @param appLongName
	 *          the long name of the application.
	 * @param directory
	 *          the parent directory of the configuration file; {@code null} for the current directory.
	 */

	public AppConfig(
		String	id,
		String	appKey,
		String	appShortName,
		String	appLongName,
		Path	directory)
	{
		// Initialise instance variables
		this.id = id;
		this.appKey = appKey;
		this.appShortName = appShortName;
		this.appLongName = appLongName;
		this.directory = directory;
		config = new MapNode();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the application should neither read nor write a configuration file.  This method returns
	 * {@code true} if a system property with the key {@code blankaspect.app.noConfigFile} is defined and the value of
	 * the property is "true", ignoring letter case.
	 *
	 * @return {@code true} if the application should neither read nor write a configuration file.
	 */

	public static boolean noConfigFile()
	{
		return Boolean.getBoolean(SystemPropertyKey.APP_NO_CONFIG_FILE);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the location of the configuration file.
	 *
	 * @return the location of the configuration file.
	 */

	public Path getFile()
	{
		return file;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the root node of this configuration.
	 *
	 * @return the root node of this configuration.
	 */

	public MapNode getConfig()
	{
		return config;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the parent directory of the configuration file to the specified value.
	 *
	 * @param directory
	 *          the parent directory of the configuration file; {@code null} for the current directory.
	 */

	public void setDirectory(
		Path	directory)
	{
		this.directory = directory;
	}

	//------------------------------------------------------------------

	/**
	 * Reads and parses a configuration file, and sets the state of this configuration from it.
	 *
	 * @throws FileException
	 *           if an error occurs when reading or parsing the configuration file.
	 */

	public void read()
		throws FileException
	{
		// Get pathname of configuration file
		String filename = appKey + CONFIG_FILENAME_SUFFIX;
		file = (directory == null) ? Path.of(filename) : directory.resolve(filename);

		// If a configuration file exists, read it
		if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS))
		{
			// Test for configuration file
			try
			{
				if (!JsonUtils.containsText(file, id))
					throw new FileException(ErrorMsg.NOT_A_CONFIG_FILE, file, appShortName);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.ERROR_READING_FILE, file);
			}

			// Read file and parse it as JSON
			AbstractNode root = null;
			try
			{
				root = JsonUtils.readFile(file);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.ERROR_READING_FILE, file);
			}
			catch (JsonParser.ParseException e)
			{
				throw new FileException(ErrorMsg.MALFORMED_CONFIG_FILE, file);
			}

			// Test for expected type of JSON value
			if (!(root instanceof MapNode rootNode))
				throw new FileException(ErrorMsg.UNEXPECTED_CONFIG_FILE_FORMAT, file);

			// Test ID
			if (!rootNode.getString(PropertyKey.ID, "").equals(id))
				throw new FileException(ErrorMsg.NOT_A_CONFIG_FILE, file, appShortName);

			// Test version
			String key = PropertyKey.VERSION;
			if (!rootNode.hasInt(key))
				throw new FileException(ErrorMsg.UNEXPECTED_CONFIG_FILE_FORMAT, file);
			int version = rootNode.getInt(key);
			if (version != VERSION)
				throw new FileException(ErrorMsg.UNSUPPORTED_CONFIG_FILE_VERSION, file, version);

			// Get configuration
			key = PropertyKey.CONFIGURATION;
			if (rootNode.hasMap(key))
				config = rootNode.getMapNode(key);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this configuration can be written to a file.
	 *
	 * @return {@code true} if this configuration can be written to a file.
	 */

	public boolean canWrite()
	{
		return (file != null);
	}

	//------------------------------------------------------------------

	/**
	 * Writes this configuration to a file.
	 *
	 * @throws FileException
	 *           if an error occurs when writing the configuration file.
	 */

	public void write()
		throws FileException
	{
		// Create root node
		MapNode rootNode = new MapNode();

		// Add ID
		rootNode.addString(PropertyKey.ID, id);

		// Add version
		rootNode.addInt(PropertyKey.VERSION, VERSION);

		// Add description
		rootNode.addString(PropertyKey.DESCRIPTION, appLongName + " : " + CONFIGURATION_STR);

		// Add configuration object
		rootNode.add(PropertyKey.CONFIGURATION, config);

		// Generate JSON text and write it to file
		try
		{
			JsonUtils.writeFile(file, rootNode, JsonGenerator.builder().maxLineLength(128).build());
		}
		catch (IOException e)
		{
			throw new FileException(ErrorMsg.ERROR_WRITING_FILE, file);
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
