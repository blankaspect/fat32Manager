/*====================================================================*\

AbstractTheme.java

Class: theme.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Function;

import javafx.css.CssParser;

import javafx.css.CssParser.ParseError;

import javafx.scene.image.Image;

import javafx.scene.paint.Color;

import uk.blankaspect.common.colourproperty.ColourPropertySet;

import uk.blankaspect.common.css.CssRuleSet;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.FileException;
import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.resource.ResourceUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.tuple.IStrKVPair;
import uk.blankaspect.common.tuple.StrKVPair;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

//----------------------------------------------------------------------


// CLASS: ABSTRACT THEME


public abstract class AbstractTheme
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int	COLOUR_PROPERTIES_SEPARATOR_LINE_LENGTH	= 80;

	private static final	String[]	COLOUR_PROPERTIES_HEADER_LINES	=
	{
		"A colour value may have 1, 2, 3 or 4 components:",
		"  1 : <grey>",
		"  2 : <grey>, <opacity>",
		"  3 : <red>, <green>, <blue>",
		"  4 : <red>, <green>, <blue>, <opacity>",
		"where",
		"  <grey>, <red>, <green>, <blue> are integers in the interval [0, 255]",
		"  <opacity> is a floating-point number in the interval [0, 1]",
	};

	/** Keys of properties. */
	public interface PropertyKey
	{
		String	NAME	= "name";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FAILED_TO_READ_COLOURS			= "Failed to read the set of colours.";
		String	MALFORMED_KEY_VALUE_PAIR		= "Line %d: The key-value pair is malformed.";
		String	DUPLICATE_KEY					= "Line %d: The key '%s' appears more than once.";
		String	INVALID_COLOUR					= "Line %d: The colour specifier is invalid.";
		String	ERROR_PARSING_BASE_STYLE_SHEET	= "An error occurred when parsing the base style sheet:";
		String	ERRORS_PARSING_BASE_STYLE_SHEET	= "%d errors occurred when parsing the base style sheet.\n"
													+ "First error:";
		String	ERROR_WRITING_FILE				= "An error occurred when writing the file.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Map<String, String>	properties;
	private	String				baseStyleSheet;
	private	Map<String, Color>	defaultColours;
	private	Map<String, Color>	colours;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	protected AbstractTheme()
	{
		// Initialise instance variables
		properties = new HashMap<>();
		defaultColours = new LinkedHashMap<>();
		colours = new LinkedHashMap<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Map<String, Color> parseColours(
		List<String> lines)
		throws BaseException
	{
		// Initialise map of colours
		Map<String, Color> colours = new LinkedHashMap<>();

		// Parse list of colours
		int lineIndex = 0;
		while (lineIndex < lines.size())
		{
			// Remove any comment from line
			String line = lines.get(lineIndex++);
			int index = line.indexOf(ColourPropertySet.PROPERTY_COMMENT_CHAR);
			if (index >= 0)
				line = line.substring(0, index);

			// Skip blank line
			if (line.isBlank())
				continue;

			// Split line into key and value
			index = line.indexOf(ColourPropertySet.PROPERTY_SEPARATOR_CHAR);
			if (index < 0)
				throw new BaseException(ErrorMsg.MALFORMED_KEY_VALUE_PAIR, lineIndex);

			// Extract key
			String key = line.substring(0, index).trim();
			if (key.isEmpty())
				throw new BaseException(ErrorMsg.MALFORMED_KEY_VALUE_PAIR, lineIndex);
			if (colours.containsKey(key))
				throw new BaseException(ErrorMsg.DUPLICATE_KEY, lineIndex, key);

			// Parse colour
			Color colour = null;
			try
			{
				colour = ColourUtils.parseRgb(line.substring(index + 1).trim());
			}
			catch (IllegalArgumentException e)
			{
				throw new BaseException(ErrorMsg.INVALID_COLOUR, e, lineIndex);
			}

			// Add colour to map
			colours.put(key, colour);
		}

		// Return map of colours
		return colours;
	}

	//------------------------------------------------------------------

	public static Map<String, Color> readColours(
		Class<?>	cls,
		String		pathname)
		throws LocationException
	{
		// Read lines of resource
		List<String> lines = null;
		try
		{
			lines = ResourceUtils.readLines(cls, pathname);
		}
		catch (IOException e)
		{
			throw new LocationException(ErrorMsg.FAILED_TO_READ_COLOURS, e, pathname);
		}

		// Parse colours
		Map<String, Color> colours = null;
		try
		{
			colours = parseColours(lines);
		}
		catch (BaseException e)
		{
			throw new LocationException(e, pathname);
		}

		// Return map of colours
		return colours;
	}

	//------------------------------------------------------------------

	public static Map<String, Color> readColours(
		Path	location)
		throws FileException
	{
		// Read lines of file
		List<String> lines = null;
		try
		{
			lines = Files.readAllLines(location);
		}
		catch (IOException e)
		{
			throw new FileException(ErrorMsg.FAILED_TO_READ_COLOURS, e, location);
		}

		// Parse colours
		Map<String, Color> colours = null;
		try
		{
			colours = parseColours(lines);
		}
		catch (BaseException e)
		{
			throw new FileException(e, location);
		}

		// Return map of colours
		return colours;
	}

	//------------------------------------------------------------------

	public static void writeColours(
		Path				location,
		Map<String, Color>	colours,
		String				headerComment)
		throws FileException
	{
		Function<Map.Entry<String, Color>, StrKVPair> mapper = entry ->
				StrKVPair.of(entry.getKey(), ColourUtils.colourToRgbString(entry.getValue()));
		writeColours(location, colours.entrySet().stream().map(mapper).toList(), headerComment);
	}

	//------------------------------------------------------------------

	public static void writeColours(
		Path						location,
		List<? extends IStrKVPair>	colourProperties,
		String						headerComment)
		throws FileException
	{
		// Initialise buffer
		StringBuilder buffer = new StringBuilder(4096);

		// Create procedure to append separator line
		IProcedure0 appendSeparatorLine = () ->
		{
			buffer.append(ColourPropertySet.PROPERTY_COMMENT_CHAR);
			buffer.append("-".repeat(COLOUR_PROPERTIES_SEPARATOR_LINE_LENGTH - 1));
			buffer.append('\n');
		};

		// Append header comment
		if (headerComment != null)
		{
			String indent = "  ";

			// Append separator line
			appendSeparatorLine.invoke();

			// Append empty comment line
			buffer.append(ColourPropertySet.PROPERTY_COMMENT_CHAR);
			buffer.append('\n');

			// Split header comment into lines
			List<String> lines = StringUtils.split(headerComment, '\n', true);
			if (!lines.isEmpty())
				lines.add("");

			// Add lines of predefined comment
			Collections.addAll(lines, COLOUR_PROPERTIES_HEADER_LINES);

			// Append lines of comment
			for (String line : lines)
			{
				buffer.append(ColourPropertySet.PROPERTY_COMMENT_CHAR);
				if (!line.isBlank())
				{
					buffer.append(indent);
					buffer.append(line);
				}
				buffer.append('\n');
			}

			// Append empty comment line
			buffer.append(ColourPropertySet.PROPERTY_COMMENT_CHAR);
			buffer.append('\n');

			// Append separator line
			appendSeparatorLine.invoke();

			// Append blank line between header comment and properties
			if (!colourProperties.isEmpty())
				buffer.append('\n');
		}

		// Append properties
		for (IStrKVPair property : colourProperties)
		{
			String key = property.key();
			if (!key.isEmpty())
			{
				buffer.append(key);
				buffer.append(' ');
				if (!key.equals(ColourPropertySet.PROPERTY_COMMENT))
				{
					buffer.append(ColourPropertySet.PROPERTY_SEPARATOR_CHAR);
					buffer.append(' ');
				}
				buffer.append(property.value());
			}
			buffer.append('\n');
		}

		// If there is a header comment, append separator line after properties
		if (headerComment != null)
		{
			buffer.append('\n');
			appendSeparatorLine.invoke();
		}

		// Write text to file
		try
		{
			Files.writeString(location, buffer);
		}
		catch (IOException e)
		{
			throw new FileException(ErrorMsg.ERROR_WRITING_FILE, e, location);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	public abstract String getId();

	//------------------------------------------------------------------

	public abstract String getName();

	//------------------------------------------------------------------

	public abstract double getBrightnessDelta1();

	//------------------------------------------------------------------

	public abstract Image processImage(
		Image	image);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getProperty(
		String	key)
	{
		return properties.get(key);
	}

	//------------------------------------------------------------------

	public String getBaseStyleSheet()
	{
		return baseStyleSheet;
	}

	//------------------------------------------------------------------

	public boolean hasDefaultColour(
		String	key)
	{
		return defaultColours.containsKey(key);
	}

	//------------------------------------------------------------------

	public boolean hasColour(
		String	key)
	{
		return colours.containsKey(key) || defaultColours.containsKey(key);
	}

	//------------------------------------------------------------------

	public Color getDefaultColour(
		String	key)
	{
		return defaultColours.get(key);
	}

	//------------------------------------------------------------------

	public Color getColour(
		String	key)
	{
		// Get colour from map
		Color colour = colours.get(key);

		// If map did not contain target colour, get default colour
		if (colour == null)
			colour = defaultColours.get(key);

		// Return colour
		return colour;
	}

	//------------------------------------------------------------------

	public Color addColour(
		String	key,
		Color	colour)
	{
		// Validate arguments
		if (key == null)
			throw new IllegalArgumentException("Null key");
		if (colour == null)
			throw new IllegalArgumentException("Null colour");

		// Add colour to map
		return colours.put(key, colour);
	}

	//------------------------------------------------------------------

	public void addColours(
		Map<String, Color>	colours)
	{
		// Validate argument
		if (colours == null)
			throw new IllegalArgumentException("Null colours");

		// Add colours to map
		this.colours.putAll(colours);
	}

	//------------------------------------------------------------------

	public void addColours(
		Iterable<? extends IStrKVPair>	properties)
	{
		// Validate argument
		if (properties == null)
			throw new IllegalArgumentException("Null properties");

		// Add colours to map
		int index = 0;
		for (IStrKVPair property : properties)
		{
			try
			{
				colours.put(property.key(), ColourUtils.parseRgb(property.value()));
				++index;
			}
			catch (IllegalArgumentException e)
			{
				throw new IllegalArgumentException(e.getMessage() + ": index " + index);
			}
		}
	}

	//------------------------------------------------------------------

	public void addColours(
		IStrKVPair...	properties)
	{
		// Validate argument
		if (properties == null)
			throw new IllegalArgumentException("Null properties");

		// Add colours to map
		addColours(Arrays.asList(properties));
	}

	//------------------------------------------------------------------

	public Color removeColour(
		String	key)
	{
		return colours.remove(key);
	}

	//------------------------------------------------------------------

	public void clearColours()
	{
		colours.clear();
	}

	//------------------------------------------------------------------

	public void loadDefaultColours(
		String	pathname)
		throws LocationException
	{
		defaultColours.putAll(readColours(getClass(), pathname));
	}

	//------------------------------------------------------------------

	public void loadColours(
		String	pathname)
		throws LocationException
	{
		colours.putAll(readColours(getClass(), pathname));
	}

	//------------------------------------------------------------------

	public void loadColours(
		Path	location)
		throws FileException
	{
		colours.putAll(readColours(location));
	}

	//------------------------------------------------------------------

	public void resolveColourProperties(
		CssRuleSet	ruleSet)
	{
		for (String name : ruleSet.propertyNames())
		{
			// Get property value
			String value = ruleSet.propertyValue(name);

			// Replace colour keys in property value with colour strings
			if (value != null)
			{
				StringBuilder buffer = new StringBuilder(128);
				boolean changed = false;
				int index = 0;
				int endIndex = value.length();
				while (index < endIndex)
				{
					int startIndex = index;
					index = value.indexOf(StyleConstants.COLOUR_KEY_PREFIX_CHAR, index);
					if (index < 0)
					{
						index = endIndex;
						buffer.append(value, startIndex, index);
					}
					else
					{
						buffer.append(value, startIndex, index);
						startIndex = index;
						while (++index < endIndex)
						{
							char ch = value.charAt(index);
							if (!(Character.isLetterOrDigit(ch) || (ch == '.')))
								break;
						}
						String key = value.substring(startIndex + 1, index);
						Color colour = getColour(key);
						if (colour == null)
							buffer.append(value, startIndex, index);
						else
						{
							buffer.append(ColourUtils.colourToCssRgbaString(colour));
							changed = true;
						}
					}
				}
				if (changed)
					ruleSet.addProperty(name, buffer.toString());
			}
		}
	}

	//------------------------------------------------------------------

	public void readBaseStyleSheet(
		String	pathname)
		throws LocationException
	{
		// Invalidate style sheet
		baseStyleSheet = null;

		// Read style sheet
		String text = null;
		try
		{
			Class<?> cls = getClass();
			if (ResourceUtils.hasResource(cls, pathname))
				text = ResourceUtils.readText(cls, pathname).replace("\r\n", "\n");
		}
		catch (IOException e)
		{
			throw new LocationException(ErrorMsg.FAILED_TO_READ_COLOURS, e, pathname);
		}

		// Parse style sheet
		CssParser.errorsProperty().clear();
		new CssParser().parse(text);
		List<ParseError> errors = new ArrayList<>(CssParser.errorsProperty());
		int numErrors = errors.size();
		if (numErrors > 0)
		{
			String message = errors.get(0).getMessage();
			if (numErrors == 1)
				throw new LocationException(ErrorMsg.ERROR_PARSING_BASE_STYLE_SHEET + "\n" + message, pathname);
			throw new LocationException(ErrorMsg.ERRORS_PARSING_BASE_STYLE_SHEET + "\n" + message, pathname, numErrors);
		}

		// Update instance variable with text of style sheet from which comments and empty lines have been removed
		baseStyleSheet = text.replaceAll("(?s)/\\*.*?\\*/", "")		// remove comments
								.replaceAll("[ \t]+\n", "\n")		// remove space at end of each line
								.replaceAll("\n\n+", "\n")			// remove empty lines
								.replaceAll("}\n", "}\n\n")			// insert empty line after each block
								.replaceAll("\n\n+$", "\n");		// remove empty lines at end of text
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
