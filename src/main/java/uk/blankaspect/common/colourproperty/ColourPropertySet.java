/*====================================================================*\

ColourPropertySet.java

Class: set of colour properties.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.colourproperty;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import java.util.stream.Collectors;

import uk.blankaspect.common.colour.ColourConstants;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.LocationException;
import uk.blankaspect.common.exception2.OuterIOException;
import uk.blankaspect.common.exception2.UrlException;

import uk.blankaspect.common.function.IFunction1;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// CLASS: SET OF COLOUR PROPERTIES


public class ColourPropertySet
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The character that separates the key and value of a property in a file of properties. */
	public static final		char	PROPERTY_SEPARATOR_CHAR	= '=';

	/** The character that denotes the start of a comment in a file of properties. */
	public static final		char	PROPERTY_COMMENT_CHAR	= '#';

	/** The string that denotes the start of a comment in a file of properties. */
	public static final		String	PROPERTY_COMMENT		= Character.toString(PROPERTY_COMMENT_CHAR);

	/** The filename extension of a Java class file. */
	private static final	String	CLASS_FILENAME_EXTENSION	= ".class";

	/** The prefix of a JAR-scheme URL. */
	private static final	String	JAR_URL_PREFIX	= "jar:";

	/** The separator between the pathname of the JAR file and the name of an entry in the URL of the JAR file. */
	private static final	String	JAR_URL_SEPARATOR	= "!/";

	private static final	char	JAR_ENTRY_PATHNAME_SEPARATOR_CHAR	= '/';

	private static final	char	PATHNAME_SEPARATOR_CHAR	= '/';

	private static final	char	CLASS_NAME_SEPARATOR_CHAR	= '.';
	private static final	String	CLASS_NAME_SEPARATOR		= Character.toString(CLASS_NAME_SEPARATOR_CHAR);

	private static final	int		COLOUR_PROPERTIES_RELATIVE_PATHNAME_LENGTH	= 3;

	/** The timeout (in seconds) of a connection. */
	private static final	int		CONNECTION_TIMEOUT	= 30;

	/** The timeout (in seconds) of a read operation. */
	private static final	int		READ_TIMEOUT	= 15;

	/** Error messages. */
	private interface ErrorMsg
	{
		String	CLASS_FILE_RESOURCE_NOT_FOUND =
				"Class: %s\nThe class-file resource was not found.";

		String	MALFORMED_JAR_URL =
				"The URL of the JAR file is malformed.";

		String	CANNOT_EXTRACT_CLASS_FILE_PARENT =
				"Cannot extract the parent directory from the location of the class file.";

		String	FAILED_TO_CONVERT_RESOURCE_URL =
				"Failed to convert the resource URL to a file-system location.";

		String	FAILED_TO_OPEN_CONNECTION_TO_JAR =
				"Failed to open a connection to the JAR file.";

		String	FAILED_TO_OPEN_CONNECTION =
				"Failed to open a connection.";

		String	FAILED_TO_CLOSE_CONNECTION =
				"Failed to close the connection.";

		String	FAILED_TO_CONNECT_TO_FILE =
				"Failed to connect to the file.";

		String	FILE_IS_TOO_LONG =
				"The file is too long to be read.";

		String	ERROR_READING_FILE =
				"An error occurred when reading the file.";

		String	ERROR_WRITING_FILE =
				"An error occurred when writing the file.";

		String	ERROR_TRAVERSING_DIRECTORY =
				"An error occurred when traversing the directory structure.";

		String	MALFORMED_KEY_VALUE_PAIR =
				"Line %d: The key-value pair is malformed.";

		String	DUPLICATE_KEY =
				"Line %d: The key '%s' occurs more than once.";

		String	INVALID_COLOUR =
				"Line %d: The colour specifier is invalid.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Map<String, Map<String, Map<String, String>>>	colourPropertySets;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ColourPropertySet()
	{
		// Initialise instance variables
		colourPropertySets = new LinkedHashMap<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String filenameToClassName(
		String	filename)
	{
		String className = null;
		if (filename.endsWith(ColourPropertyConstants.COLOUR_PROPERTIES_FILENAME_SUFFIX))
		{
			int index = filename.length() - ColourPropertyConstants.COLOUR_PROPERTIES_FILENAME_SUFFIX.length();
			className = filename.substring(0, index);
		}
		return className;
	}

	//------------------------------------------------------------------

	public static String locationToClassName(
		Path	location)
	{
		return filenameToClassName(location.getFileName().toString());
	}

	//------------------------------------------------------------------

	public static String normaliseColourString(
		String	str)
	{
		// Split input string into components
		String[] strs = str.strip().split(ColourConstants.RGB_SEPARATOR_REGEX, -1);
		int numComponents = strs.length;
		if (numComponents > 4)
			throw new IllegalArgumentException("Malformed colour");

		// Parse RGB components
		int value = 0;
		int index = 0;
		while (index < 3)
		{
			if ((index == 0) || (numComponents > 2))
			{
				try
				{
					value = Integer.parseInt(strs[index]);
					if ((value < ColourConstants.MIN_RGB_COMPONENT_VALUE)
							|| (value > ColourConstants.MAX_RGB_COMPONENT_VALUE))
						throw new IllegalArgumentException("RGB component out of bounds: " + value);
				}
				catch (NumberFormatException e)
				{
					throw new IllegalArgumentException("Invalid RGB component");
				}
			}
			++index;
		}

		// Parse opacity
		double opacity = ColourConstants.MAX_OPACITY;
		if ((numComponents == 2) || (numComponents == 4))
		{
			try
			{
				opacity = Double.parseDouble(strs[numComponents - 1]);
				if ((opacity < ColourConstants.MIN_OPACITY) || (opacity > ColourConstants.MAX_OPACITY))
					throw new IllegalArgumentException("Opacity out of bounds: " + opacity);
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException("Invalid opacity");
			}
		}

		// Create and return normalised string
		StringBuilder buffer = new StringBuilder(32);
		buffer.append(strs[0]);
		if (numComponents > 2)
		{
			buffer.append(ColourConstants.RGB_SEPARATOR);
			buffer.append(strs[1]);
			buffer.append(ColourConstants.RGB_SEPARATOR);
			buffer.append(strs[2]);
		}
		if (opacity < ColourConstants.MAX_OPACITY)
		{
			buffer.append(ColourConstants.RGB_SEPARATOR);
			buffer.append(ColourConstants.OPACITY_FORMATTER.format(opacity));
		}
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public static Map<String, String> readColourProperties(
		URL	url)
		throws UrlException
	{
		// Read file as text; replace CR+LF with LF
		String text = readTextFile(url).replace("\r\n", "\n");

		// Split text into lines; parse colour properties; return map of properties
		try
		{
			return parseColourProperties(StringUtils.split(text, '\n'));
		}
		catch (BaseException e)
		{
			throw new UrlException(e, url);
		}
	}

	//------------------------------------------------------------------

	public static Map<String, String> readColourProperties(
		Path	file)
		throws FileException
	{
		// Read lines of file
		List<String> lines = null;
		try
		{
			lines = Files.readAllLines(file);
		}
		catch (IOException e)
		{
			throw new FileException(ErrorMsg.ERROR_READING_FILE, e, file);
		}

		// Parse colour properties; return map of properties
		try
		{
			return parseColourProperties(lines);
		}
		catch (BaseException e)
		{
			throw new FileException(e, file);
		}
	}

	//------------------------------------------------------------------

	public static List<String> updateColourProperties(
		Path				file,
		Map<String, String>	properties)
		throws FileException
	{
		// Read lines of file
		List<String> lines = null;
		try
		{
			lines = Files.readAllLines(file);
		}
		catch (IOException e)
		{
			throw new FileException(ErrorMsg.ERROR_READING_FILE, e, file);
		}

		// Initialise lists of keys
		List<String> keysFound = new ArrayList<>();
		List<String> keysNotFound = new ArrayList<>(properties.keySet());

		// Replace values of properties for which there is an entry in map
		boolean changed = false;
		int lineIndex = 0;
		while (lineIndex < lines.size())
		{
			// Remove any comment from line
			String line = lines.get(lineIndex++);
			int index = line.indexOf(PROPERTY_COMMENT_CHAR);
			if (index >= 0)
				line = line.substring(0, index);

			// Skip blank line
			if (line.isBlank())
				continue;

			// Split line into key and value
			index = line.indexOf(PROPERTY_SEPARATOR_CHAR);
			if (index < 0)
				throw new FileException(ErrorMsg.MALFORMED_KEY_VALUE_PAIR, file, lineIndex);

			// Extract key
			String key = line.substring(0, index).strip();
			if (key.isEmpty())
				throw new FileException(ErrorMsg.MALFORMED_KEY_VALUE_PAIR, file, lineIndex);
			if (keysFound.contains(key))
				throw new FileException(ErrorMsg.DUPLICATE_KEY, file, lineIndex, key);
			keysFound.add(key);

			// If there is an entry for key in map of properties and value is different from value in file, replace
			// value of property in line of text
			String value = properties.get(key);
			if (value != null)
			{
				// Remove key from list of keys not found
				keysNotFound.remove(key);

				// Replace value of property
				if (!value.equals(line.substring(index + 1).strip()))
				{
					lines.set(lineIndex - 1, key + " " + PROPERTY_SEPARATOR_CHAR + " " + value);
					changed = true;
				}
			}
		}

		// If any properties have changed, write file
		if (changed)
		{
			try
			{
				Files.writeString(file, StringUtils.join('\n', true, lines));
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.ERROR_WRITING_FILE, file);
			}
		}

		// Return list of keys not found
		return keysNotFound;
	}

	//------------------------------------------------------------------

	/**
	 * Reads the file that is denoted by the specified URL, which is expected to be text encoded as UTF-8, and returns
	 * the text content.
	 *
	 * @param  url
	 *           the location of the file.
	 * @return the text content of the file denoted by {@code url}.
	 * @throws UrlException
	 *           if an error occurs when reading the file.
	 */

	private static String readTextFile(
		URL	url)
		throws UrlException
	{
		URLConnection connection = null;
		InputStream inStream = null;
		try
		{
			// Open connection
			try
			{
				connection = url.openConnection();
				connection.setConnectTimeout(CONNECTION_TIMEOUT * 1000);
				connection.setReadTimeout(READ_TIMEOUT * 1000);
			}
			catch (Exception e)
			{
				throw new UrlException(ErrorMsg.FAILED_TO_OPEN_CONNECTION, e, url);
			}

			// Get content length
			long contentLength = connection.getContentLengthLong();
			if (contentLength > Integer.MAX_VALUE)
				throw new UrlException(ErrorMsg.FILE_IS_TOO_LONG, url);
			int length = (int)contentLength;

			// Initialise buffer for file content
			byte[] buffer = null;

			// Open input stream on connection
			try
			{
				inStream = connection.getInputStream();
			}
			catch (Exception e)
			{
				throw new UrlException(ErrorMsg.FAILED_TO_CONNECT_TO_FILE, e, url);
			}

			// Allocate buffer for file content
			buffer = new byte[length];

			// Read content
			try
			{
				int readLength = 0;
				for (int offset = 0; offset < length; offset += readLength)
				{
					readLength = inStream.read(buffer, offset, length - offset);
					if (readLength < 0)
						throw new UrlException(ErrorMsg.ERROR_READING_FILE, url);
				}
			}
			catch (IOException e)
			{
				throw new UrlException(ErrorMsg.ERROR_READING_FILE, e, url);
			}

			// Close input stream
			try
			{
				inStream.close();
			}
			catch (IOException e)
			{
				throw new UrlException(ErrorMsg.FAILED_TO_CLOSE_CONNECTION, e, url);
			}
			finally
			{
				inStream = null;
			}

			// Return content of file as text
			return new String(buffer, StandardCharsets.UTF_8);
		}
		catch (UrlException e)
		{
			// Close input stream
			if (inStream != null)
			{
				try
				{
					inStream.close();
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

	private static Map<String, String> parseColourProperties(
		List<String>	lines)
		throws BaseException
	{
		// Initialise map of properties
		Map<String, String> properties = new LinkedHashMap<>();

		// Parse properties
		int lineIndex = 0;
		while (lineIndex < lines.size())
		{
			// Remove any comment from line
			String line = lines.get(lineIndex++);
			int index = line.indexOf(PROPERTY_COMMENT_CHAR);
			if (index >= 0)
				line = line.substring(0, index);

			// Skip blank line
			if (line.isBlank())
				continue;

			// Split line into key and value
			index = line.indexOf(PROPERTY_SEPARATOR_CHAR);
			if (index < 0)
				throw new BaseException(ErrorMsg.MALFORMED_KEY_VALUE_PAIR, lineIndex);

			// Extract key
			String key = line.substring(0, index).strip();
			if (key.isEmpty())
				throw new BaseException(ErrorMsg.MALFORMED_KEY_VALUE_PAIR, lineIndex);
			if (properties.containsKey(key))
				throw new BaseException(ErrorMsg.DUPLICATE_KEY, lineIndex, key);

			// Extract value; validate and normalise colour string
			String value = null;
			try
			{
				value = normaliseColourString(line.substring(index + 1));
			}
			catch (IllegalArgumentException e)
			{
				throw new BaseException(ErrorMsg.INVALID_COLOUR, e, lineIndex);
			}

			// Add property to map
			properties.put(key, value);
		}

		// Return map of colours
		return properties;
	}

	//------------------------------------------------------------------

	private static <T> Map<String, T> copyMap(
		Map<String, T>		map,
		IFunction1<T, T>	valueMapper)
	{
		Map<String, T> copy = new LinkedHashMap<>();
		for (Map.Entry<String, T> entry : map.entrySet())
			copy.put(entry.getKey(), valueMapper.invoke(entry.getValue()));
		return copy;
	}

	//------------------------------------------------------------------

	private static <T> Map<String, T> wrapMap(
		Map<String, T>		map,
		IFunction1<T, T>	valueMapper)
	{
		return map.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Map<String, Map<String, Map<String, String>>> getColourPropertySets()
	{
		return copyMap(colourPropertySets, map1 -> copyMap(map1, map2 -> copyMap(map2, IFunction1.identity())));
	}

	//------------------------------------------------------------------

	public Map<String, Map<String, Map<String, String>>> getReadOnlyColourPropertySets()
	{
		return wrapMap(colourPropertySets, map1 -> wrapMap(map1, map2 -> wrapMap(map2, IFunction1.identity())));
	}

	//------------------------------------------------------------------

	public Map<String, String> getColourProperties(
		String	themeId)
	{
		return colourPropertySets.entrySet().stream()
				.flatMap(entry ->
						entry.getValue().entrySet().stream().filter(entry0 -> entry0.getKey().equals(themeId)))
				.flatMap(entry -> entry.getValue().entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2));
	}

	//------------------------------------------------------------------

	public void readColourProperties(
		Iterable<Class<?>>	classes)
		throws BaseException
	{
		// Clear map of colour-property sets
		colourPropertySets.clear();

		// Initialise list of URLs of JARs
		List<URL> jarUrls = new ArrayList<>();

		// Read colour-property sets from JARs or directory structures that contain class files of specified classes
		for (Class<?> cls : classes)
		{
			// Get URL of class file
			String filename = cls.getSimpleName() + CLASS_FILENAME_EXTENSION;
			URL url = cls.getResource(filename);
			if (url == null)
				throw new BaseException(ErrorMsg.CLASS_FILE_RESOURCE_NOT_FOUND, cls.getName());

			// Case: class file is in JAR
			if (url.toString().startsWith(JAR_URL_PREFIX))
			{
				// Extract URL of JAR file from URL of class file
				String urlStr = url.toString();
				int index = urlStr.indexOf(JAR_URL_SEPARATOR);
				if (index < 0)
					throw new UrlException(ErrorMsg.MALFORMED_JAR_URL, url);
				urlStr = urlStr.substring(0, index + JAR_URL_SEPARATOR.length());

				// Create URL of JAR file
				try
				{
					url = new URI(urlStr).toURL();
				}
				catch (URISyntaxException | MalformedURLException e)
				{
					throw new LocationException(ErrorMsg.MALFORMED_JAR_URL, urlStr);
				}

				// If JAR has not already been processed, read colour properties from its entries
				if (!jarUrls.contains(url))
				{
					// Add URL of JAR to list
					jarUrls.add(url);

					// Read colour properties from JAR entries
					try
					{
						URLConnection connection = url.openConnection();
						if (connection instanceof JarURLConnection jarConnection)
							readPropertiesFromJar(url, jarConnection.getJarFile());
						else
							throw new UrlException(ErrorMsg.FAILED_TO_OPEN_CONNECTION_TO_JAR, url);
					}
					catch (IOException e)
					{
						throw new UrlException(ErrorMsg.FAILED_TO_OPEN_CONNECTION_TO_JAR, e, url);
					}
				}
			}

			// Case: class file is not in JAR
			else
			{
				// Convert URL to file-system location
				Path file = null;
				try
				{
					file = Path.of(url.toURI());
				}
				catch (Exception e)
				{
					throw new UrlException(ErrorMsg.FAILED_TO_CONVERT_RESOURCE_URL, url);
				}

				// Convert class name to expected pathname of class file
				String classFilePathname = cls.getName().replace(CLASS_NAME_SEPARATOR_CHAR, File.separatorChar)
												+ CLASS_FILENAME_EXTENSION;

				// Test whether actual pathname of class file corresponds to expected pathname
				String pathname = file.toString();
				if (!pathname.endsWith(classFilePathname))
					throw new FileException(ErrorMsg.CANNOT_EXTRACT_CLASS_FILE_PARENT, file);

				// Get root directory
				Path rootDirectory = Path.of(pathname.substring(0, pathname.length() - classFilePathname.length()));

				// Traverse directory structure, reading colour properties from files
				readPropertiesFromDirectoryStructure(rootDirectory);
			}
		}
	}

	//------------------------------------------------------------------

	public void readColourProperties(
		Class<?>...	classes)
		throws BaseException
	{
		readColourProperties(List.of(classes));
	}

	//------------------------------------------------------------------

	private void readPropertiesFromJar(
		URL		url,
		JarFile	jarFile)
		throws LocationException
	{
		// Create list of JAR entries
		List<? extends JarEntry> jarEntries = Collections.list(jarFile.entries());

		// Search JAR entries for colour-properties files
		for (JarEntry entry : jarEntries)
		{
			// Test for directory
			if (entry.isDirectory())
				continue;

			// Get pathname of JAR entry
			String pathname = entry.getName();

			// Split pathname into its elements
			List<String> elements = StringUtils.split(pathname, JAR_ENTRY_PATHNAME_SEPARATOR_CHAR);

			// Test for minimum number of pathname elements
			int numElements = elements.size();
			if (numElements < COLOUR_PROPERTIES_RELATIVE_PATHNAME_LENGTH)
				continue;

			// Test for 'themes' directory
			int index = numElements - COLOUR_PROPERTIES_RELATIVE_PATHNAME_LENGTH;
			if (!elements.get(index).equals(ColourPropertyConstants.THEMES_DIRECTORY_NAME))
				continue;

			// Get filename
			String filename = elements.get(index + 2);

			// Test for name of colour-properties file
			if (!filename.endsWith(ColourPropertyConstants.COLOUR_PROPERTIES_FILENAME_SUFFIX))
				continue;

			// Extract class name from pathname elements
			String packageName = StringUtils.join(CLASS_NAME_SEPARATOR_CHAR, elements.subList(0, index));
			String className = packageName + CLASS_NAME_SEPARATOR + filenameToClassName(filename);

			// Get ID of theme from pathname element
			String themeId = elements.get(index + 1);

			// Initialise URL of JAR entry
			URL jarEntryUrl = null;
			String urlStr = url.toString() + pathname;
			try
			{
				jarEntryUrl = new URI(urlStr).toURL();
			}
			catch (URISyntaxException | MalformedURLException e)
			{
				throw new LocationException(ErrorMsg.MALFORMED_JAR_URL, urlStr);
			}

			// Read colour properties from file and add them to map of colour-property sets
			try
			{
				Map<String, String> properties = readColourProperties(jarEntryUrl);
				colourPropertySets.computeIfAbsent(className, key -> new LinkedHashMap<>()).put(themeId, properties);
			}
			catch (BaseException e)
			{
				throw new UrlException(e, jarEntryUrl);
			}
		}
	}

	//------------------------------------------------------------------

	private void readPropertiesFromDirectoryStructure(
		Path	rootDirectory)
		throws LocationException
	{
		Path normRootDirectory = rootDirectory.normalize();
		try
		{
			Files.walkFileTree(normRootDirectory, new SimpleFileVisitor<>()
			{
				@Override
				public FileVisitResult visitFile(
					Path				file,
					BasicFileAttributes	attrs)
					throws IOException
				{
					// Get name of file
					String filename = file.getFileName().toString();

					// Test for name of colour-properties file
					if (!filename.endsWith(ColourPropertyConstants.COLOUR_PROPERTIES_FILENAME_SUFFIX))
						return FileVisitResult.CONTINUE;

					// Get pathname of file relative to root directory
					String pathname = normRootDirectory.relativize(file.normalize()).toString()
											.replace(File.separatorChar, PATHNAME_SEPARATOR_CHAR);

					// Split relative pathname into its elements
					List<String> elements = StringUtils.split(pathname, PATHNAME_SEPARATOR_CHAR);

					// Test for minimum number of pathname elements
					int numElements = elements.size();
					if (numElements < COLOUR_PROPERTIES_RELATIVE_PATHNAME_LENGTH)
						return FileVisitResult.CONTINUE;

					// Test for 'themes' directory
					int index = numElements - COLOUR_PROPERTIES_RELATIVE_PATHNAME_LENGTH;
					if (!elements.get(index).equals(ColourPropertyConstants.THEMES_DIRECTORY_NAME))
						return FileVisitResult.CONTINUE;

					// Extract class name from pathname elements
					String packageName = StringUtils.join(CLASS_NAME_SEPARATOR_CHAR, elements.subList(0, index));
					String className = packageName + CLASS_NAME_SEPARATOR + filenameToClassName(filename);

					// Get ID of theme from pathname element
					String themeId = elements.get(index + 1);

					// Read colour properties from file and add them to map of colour-property sets
					try
					{
						Map<String, String> properties = readColourProperties(file);
						colourPropertySets.computeIfAbsent(className, key -> new LinkedHashMap<>())
								.put(themeId, properties);
					}
					catch (FileException e)
					{
						throw new OuterIOException(e);
					}

					// Continue to traverse the tree
					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch (Exception e)
		{
			// If cause was a file exception, throw it
			FileException.throwCause(e);

			// Throw exception
			throw new FileException(ErrorMsg.ERROR_TRAVERSING_DIRECTORY, e, normRootDirectory);
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
