/*====================================================================*\

JsonUtils.java

Class: JSON-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.json;

//----------------------------------------------------------------------


// IMPORTS


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

import uk.blankaspect.common.basictree.AbstractNode;

import uk.blankaspect.common.filesystem.PathUtils;

//----------------------------------------------------------------------


// CLASS: JSON-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JSON.
 */

public class JsonUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The filename extension of a temporary file. */
	public static final	String	TEMPORARY_FILENAME_EXTENSION	= ".$tmp";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private JsonUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the specified {@linkplain AbstractNode node} corresponds to a JSON value.
	 *
	 * @param  node
	 *           the node of interest.
	 * @return {@code true} if {@code node} corresponds to a JSON value; {@code false} otherwise.
	 */

	public static boolean isJsonValue(
		AbstractNode	node)
	{
		return node.getType().isAnyOf(JsonConstants.NODE_TYPES);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified {@linkplain AbstractNode node} corresponds to a simple JSON value (ie, a
	 * null, a Boolean, a number or a string).
	 *
	 * @param  node
	 *           the node of interest.
	 * @return {@code true} if {@code node} corresponds to a JSON null, Boolean, number or string; {@code false}
	 *         otherwise.
	 */

	public static boolean isSimpleJsonValue(
		AbstractNode	node)
	{
		return node.getType().isAnyOf(JsonConstants.SIMPLE_NODE_TYPES);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified {@linkplain AbstractNode node} corresponds to a compound JSON value (ie, an
	 * array or object).
	 *
	 * @param  node
	 *           the node of interest.
	 * @return {@code true} if {@code node} corresponds to a JSON array or object; {@code false} otherwise.
	 */

	public static boolean isCompoundJsonValue(
		AbstractNode	node)
	{
		return node.getType().isAnyOf(JsonConstants.COMPOUND_NODE_TYPES);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified {@linkplain AbstractNode node} corresponds to a JSON number.
	 *
	 * @param  node
	 *           the node of interest.
	 * @return {@code true} if {@code node} corresponds to a JSON number; {@code false} otherwise.
	 */

	public static boolean isJsonNumber(
		AbstractNode	node)
	{
		return node.getType().isAnyOf(JsonConstants.NUMBER_TYPES);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified {@linkplain AbstractNode node} corresponds to a JSON container (ie, an
	 * array or object).
	 *
	 * @deprecated
	 *   This method has been replaced by {@link #isCompoundJsonValue} and will eventually be removed.
	 * @param  node
	 *           the node of interest.
	 * @return {@code true} if {@code node} corresponds to a JSON array or object; {@code false} otherwise.
	 */

	@Deprecated
	public static boolean isJsonContainer(
		AbstractNode	node)
	{
		return isCompoundJsonValue(node);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified sequence of characters is found in the specified file, which is expected to
	 * contain text with the UTF-8 character encoding.
	 *
	 * @param  file
	 *           the file that will be searched.
	 * @param  target
	 *           the sequence of characters that will be searched for in {@code file}.
	 * @return {@code true} if {@code file} contains {@code target}.
	 * @throws IOException
	 *           if an error occurs when reading the file.
	 */

	public static boolean containsText(
		Path			file,
		CharSequence	target)
		throws IOException
	{
		// Validate arguments
		if (file == null)
			throw new IllegalArgumentException("Null file");
		if (target == null)
			throw new IllegalArgumentException("Null target");

		// Search for target in character stream and return the result
		try (BufferedReader reader = Files.newBufferedReader(file))
		{
			return containsText(reader, target);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified sequence of characters is found in the specified character stream.
	 *
	 * @param  inputStream
	 *           the character stream that will be searched.
	 * @param  target
	 *           the sequence of characters that will be searched for in {@code inputSteam}.
	 * @return {@code true} if {@code target} is found in {@code inputStream}.
	 * @throws IOException
	 *           if an error occurs when reading from the input stream.
	 */

	public static boolean containsText(
		Reader			inputStream,
		CharSequence	target)
		throws IOException
	{
		// Validate arguments
		if (inputStream == null)
			throw new IllegalArgumentException("Null input stream");
		if (target == null)
			throw new IllegalArgumentException("Null target");

		// Get length of target
		int targetLength = target.length();

		// If target is empty, it is deemed to be found in any input
		if (targetLength == 0)
			return true;

		// Create array of target characters
		char[] targetChars = new char[targetLength];
		for (int i = 0; i < targetLength; i++)
			targetChars[i] = target.charAt(i);

		// Initialise circular buffer for characters from input stream
		char buffer[] = new char[targetLength];
		int index = 0;
		boolean full = false;

		// Read from input stream until end of input stream is reached or target is found
		while (true)
		{
			// Read next character from input stream
			int ch = inputStream.read();

			// If end of stream, stop
			if (ch < 0)
				break;

			// Add character to tail of buffer
			buffer[index++] = (char)ch;

			// If index is at end of buffer, wrap it around
			if (index >= targetLength)
			{
				index = 0;
				full = true;
			}

			// If buffer is full, compare it with target
			if (full)
			{
				// Initialise index to target characters
				int i = 0;

				// Case: head of buffer is at start of array
				if (index == 0)
				{
					// Compare buffer with target
					while (i < targetLength)
					{
						if (targetChars[i] != buffer[i++])
						{
							i = -1;
							break;
						}
					}
				}

				// Case: head of buffer is not at start of array
				else
				{
					// Compare front part of buffer, from head of buffer to end of array, with front part of target
					int j = index;
					int length = targetLength - index;
					while (i < length)
					{
						if (targetChars[i++] != buffer[j++])
						{
							i = -1;
							break;
						}
					}

					// If front parts matched, compare back part of buffer, from start of array to tail of buffer, with
					// back part of target
					if (i >= 0)
					{
						j = 0;
						while (i < targetLength)
						{
							if (targetChars[i++] != buffer[j++])
							{
								i = -1;
								break;
							}
						}
					}
				}

				// If buffer matched target, indicate target was found
				if (i == targetLength)
					return true;
			}
		}

		// Indicate target was not found
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Opens a character stream on the specified file, which is expected to contain text with the UTF-8 character
	 * encoding, parses the character stream as JSON text and returns the resulting JSON value.
	 *
	 * @param  file
	 *           the file whose content will be parsed as JSON text.
	 * @return the JSON value that results from parsing the content of {@code file}, if the file contains valid JSON
	 *         text.
	 * @throws IOException
	 *           if an error occurs when reading the file.
	 * @throws JsonParser.ParseException
	 *           if an error occurs when parsing the content of the file.
	 */

	public static AbstractNode readFile(
		Path	file)
		throws IOException, JsonParser.ParseException
	{
		try (BufferedReader reader = Files.newBufferedReader(file))
		{
			return JsonParser.builder().build().parse(reader);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Writes the specified JSON value to the specified file as JSON text.  The text is generated by a new instance of
	 * {@link JsonGenerator} that has default values for the mode, the <i>opening bracket on the same line</i> flag,
	 * the indent increment and the maximum line length.  The file is written to a new file in the parent directory of
	 * the specified file, and the new file is then renamed to the specified file.
	 *
	 * @param  file
	 *           the file to which the JSON text of {@code value} will be written.
	 * @param  value
	 *           the JSON value whose JSON text will be written to {@code file}.
	 * @throws IOException
	 *           if an error occurs when writing the file.
	 */

	public static void writeFile(
		Path			file,
		AbstractNode	value)
		throws IOException
	{
		writeFile(file, value, JsonGenerator.builder().build());
	}

	//------------------------------------------------------------------

	/**
	 * Writes the specified JSON value to the specified file as JSON text that is generated by the specified instance of
	 * {@link JsonGenerator}.  The file is written to a new file in the parent directory of the specified file, and the
	 * new file is then renamed to the specified file.
	 *
	 * @param  file
	 *           the file to which the JSON text of {@code value} will be written.
	 * @param  value
	 *           the JSON value whose JSON text will be written to {@code file}.
	 * @param  generator
	 *           the object that will generate the JSON text for {@code value}.
	 * @throws IOException
	 *           if an error occurs when writing the file.
	 */

	public static void writeFile(
		Path			file,
		AbstractNode	value,
		JsonGenerator	generator)
		throws IOException
	{
		// Convert the JSON value to text; write the text to the file
		writeText(file, generator.generate(value).toString());
	}

	//------------------------------------------------------------------

	/**
	 * Writes the specified text to the specified file with the UTF-8 character encoding.  The file is written to a
	 * new file in the parent directory of the specified file, and the new file is then renamed to the specified file.
	 *
	 * @param  file
	 *           the file to which {@code text} will be written.
	 * @param  text
	 *           the text that will be written to {@code file}.
	 * @throws IOException
	 *           if an error occurs when reading permissions of an existing file or when writing the file.
	 */

	public static void writeText(
		Path	file,
		String	text)
		throws IOException
	{
		Path tempFile = null;
		try
		{
			// Read file permissions of an existing file
			FileAttribute<?>[] attrs = {};
			if (Files.exists(file))
			{
				try
				{
					PosixFileAttributes posixAttrs = Files.readAttributes(file, PosixFileAttributes.class);
					attrs = new FileAttribute<?>[] { PosixFilePermissions.asFileAttribute(posixAttrs.permissions()) };
				}
				catch (UnsupportedOperationException e)
				{
					// ignore
				}
			}

			// Create the parent directories of the output file
			Path parent = PathUtils.absParent(file);
			if (parent != null)
				Files.createDirectories(parent);

			// Create a temporary file in the parent directory of the output file
			tempFile = tempLocation(file);
			Files.createFile(tempFile, attrs);

			// Write the text to the temporary file
			Files.writeString(tempFile, text);

			// Rename the temporary file to the output file
			Files.move(tempFile, file, StandardCopyOption.REPLACE_EXISTING);

			// Invalidate the temporary file
			tempFile = null;
		}
		finally
		{
			// Delete the temporary file
			if ((tempFile != null) && Files.exists(tempFile))
				Files.delete(tempFile);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns a temporary file-system location that has the same parent directory as the specified location.
	 *
	 * @param  location
	 *           the file-system location for which a temporary location is sought.
	 * @return a temporary file-system location that has the same parent directory as {@code location}.
	 */

	public static Path tempLocation(
		Path	location)
	{
		// Get input name
		String inName = location.getFileName().toString();

		// Find an output name that does not conflict with an existing name
		Path outLocation = null;
		int index = 0;
		while (true)
		{
			String outName = inName + indexToTempSuffix(index++);
			outLocation = location.resolveSibling(outName);
			if (!Files.exists(outLocation, LinkOption.NOFOLLOW_LINKS))
				break;
			++index;
		}

		// Return output location
		return outLocation;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a temporary-filename suffix for the specified index.
	 *
	 * @param  index
	 *           the index for which a temporary-filename suffix is desired.
	 * @return a temporary-filename suffix for {@code index}.
	 */

	private static String indexToTempSuffix(
		int	index)
	{
		String prefix = "";
		String str = Integer.toString(index);
		switch (str.length())
		{
			case 1:
				prefix = "00";
				break;

			case 2:
				prefix = "0";
				break;
		}
		return "." + prefix + str + TEMPORARY_FILENAME_EXTENSION;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
