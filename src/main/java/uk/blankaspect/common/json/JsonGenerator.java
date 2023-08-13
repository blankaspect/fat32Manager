/*====================================================================*\

JsonGenerator.java

Class: JSON generator.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.json;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import uk.blankaspect.common.basictree.AbstractNode;
import uk.blankaspect.common.basictree.ListNode;
import uk.blankaspect.common.basictree.MapNode;
import uk.blankaspect.common.basictree.NodeTypeException;

//----------------------------------------------------------------------


// CLASS: JSON GENERATOR


/**
 * This class implements a generator that transforms a tree of values that are represented by {@linkplain AbstractNode
 * nodes} into JSON text.  The generator operates in one of several {@linkplain Mode <i>modes</i>}, which control the
 * way in which whitespace is written between the tokens of the JSON text.
 */

public class JsonGenerator
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * This is an enumeration of the modes that control the way in which whitespace is written between the tokens of the
	 * JSON text.
	 */

	public enum Mode
	{
		/**
		 * JSON text is written on a single line with no space between tokens.
		 */
		DENSE,

		/**
		 * JSON text is written on a single line with a space between some tokens.
		 */
		COMPACT,

		/**
		 * JSON text may be written on multiple lines with a space between some tokens.  Compound values (array and
		 * object) are written on a single line in some cases:
		 * <ul>
		 *   <li>The opening and closing brackets and elements of an array are written on a single line if they all fit
		 *       on the line; otherwise, they are split over multiple lines, as necessary.</li>
		 *   <li>An object is written on a single line if it is empty or contains a single property that fits on the
		 *       line along with its opening and closing braces; otherwise, its properties and closing brace are each
		 *       written on a separate line, and its opening brace is written on a separate line if the <i>opening
		 *       bracket on the same line</i> flag is {@code false}.</li>
		 * </ul>
		 */
		NORMAL,

		/**
		 * JSON text may be written on multiple lines with a space between some tokens.  Compound values (array and
		 * object) are written on a single line in some cases:
		 * <ul>
		 *   <li>An empty array is written on a single line.  The elements and closing bracket of a non-empty array are
		 *       each written on a separate line, and the opening bracket is written on a separate line if the
		 *       <i>opening bracket on same line</i> flag is {@code false}.</li>
		 *   <li>An empty object is written on a single line.  The properties and closing brace of a non-empty object
		 *       are each written on a separate line, and the opening brace is written on a separate line if the
		 *       <i>opening bracket on the same line</i> flag is {@code false}.</li>
		 * </ul>
		 */
		EXPANDED
	}

	/** The default mode of a generator. */
	private static final	Mode	DEFAULT_MODE	= Mode.NORMAL;

	/** The default number of spaces by which indentation will be increased from one level to the next. */
	private static final	int		DEFAULT_INDENT_INCREMENT	= 2;

	/** The default maximum line length. */
	private static final	int		DEFAULT_MAX_LINE_LENGTH		= 80;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The mode of this generator, which controls the way in which whitespace is written between the tokens of the JSON
		text. */
	private	Mode	mode;

	/** Flag: if {@code true}, the opening bracket of a JSON object or array is written on the same line as the
		non-whitespace text that precedes it. */
	private	boolean	openingBracketOnSameLine;

	/** The number of spaces by which indentation is increased from one level to the next. */
	private	int		indentIncrement;

	/** The maximum length of a line of JSON text. */
	private int		maxLineLength;

	/** An array of spaces that is used to indent line of generated JSON text. */
	private	char[]	spaces;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a JSON generator with default values for the mode ({@link Mode#NORMAL NORMAL}), the
	 * <i>opening bracket on the same line</i> flag ({@code false}), the indent increment (2) and the maximum line
	 * length (80).
	 */

	public JsonGenerator()
	{
		// Call alternative constructor
		this(DEFAULT_MODE, false, DEFAULT_INDENT_INCREMENT, DEFAULT_MAX_LINE_LENGTH);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a JSON generator with the specified mode.  Default values will be used for the
	 * <i>opening bracket on the same line</i> flag ({@code false}), the indent increment (2) and the maximum line
	 * length (80), which are applicable only in a multi-line mode ({@link Mode#NORMAL NORMAL} or {@link Mode#EXPANDED
	 * EXPANDED}).
	 *
	 * @param mode
	 *          the mode of the generator, which controls the way in which whitespace is written between the tokens of
	 *          the JSON text.
	 */

	public JsonGenerator(
		Mode	mode)
	{
		// Call alternative constructor
		this(mode, false, DEFAULT_INDENT_INCREMENT, DEFAULT_MAX_LINE_LENGTH);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a JSON generator with the specified mode and <i>opening bracket on the same line</i>
	 * flag.  Default values will be used for the indent increment (2) and the maximum line length (80), which are
	 * applicable only in a multi-line mode ({@link Mode#NORMAL NORMAL} or {@link Mode#EXPANDED EXPANDED}).
	 *
	 * @param mode
	 *          the mode of the generator, which controls the way in which whitespace is written between the tokens of
	 *          the JSON text.
	 * @param openingBracketOnSameLine
	 *          if {@code true}, the opening bracket of a JSON object or array will be written on the same line as the
	 *          non-whitespace text that precedes it.
	 */

	public JsonGenerator(
		Mode	mode,
		boolean	openingBracketOnSameLine)
	{
		// Call alternative constructor
		this(mode, openingBracketOnSameLine, DEFAULT_INDENT_INCREMENT, DEFAULT_MAX_LINE_LENGTH);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a JSON generator with the specified mode, <i>opening bracket on the same line</i> flag,
	 * indent increment and maximum line length.
	 *
	 * @param mode
	 *          the mode of the generator, which controls the way in which whitespace is written between the tokens of
	 *          the JSON text.
	 * @param openingBracketOnSameLine
	 *          if {@code true}, the opening bracket of a JSON object or array will be written on the same line as the
	 *          non-whitespace text that precedes it.
	 * @param indentIncrement
	 *          the number of spaces by which indentation will be increased from one level to the next.  This parameter
	 *          has no effect in a single-line mode ({@link Mode#DENSE DENSE} or {@link Mode#COMPACT COMPACT}).
	 * @param maxLineLength
	 *          the maximum length of a line of JSON text.  This parameter has no effect in a single-line mode ({@link
	 *          Mode#DENSE DENSE} or {@link Mode#COMPACT COMPACT}).
	 */

	public JsonGenerator(
		Mode	mode,
		boolean	openingBracketOnSameLine,
		int		indentIncrement,
		int		maxLineLength)
	{
		// Initialise instance variables
		this.mode = mode;
		this.openingBracketOnSameLine = openingBracketOnSameLine;
		this.indentIncrement = indentIncrement;
		this.maxLineLength = maxLineLength;
		spaces = new char[0];
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Generates and returns JSON text for the specified {@linkplain AbstractNode JSON value} in accordance with the
	 * properties of this generator:
	 * <ul>
	 *   <li>mode,</li>
	 *   <li><i>opening bracket on the same line</i> flag,</li>
	 *   <li>indent increment,</li>
	 *   <li>maximum line length.</li>
	 * </ul>
	 *
	 * @param  value
	 *           the JSON value for which JSON text will be generated.
	 * @return JSON text for <i>value</i>.
	 * @throws NodeTypeException
	 *           if any node in the tree whose root is <i>value</i> does not represent a JSON value.
	 */

	public String generate(
		AbstractNode	value)
	{
		StringBuilder buffer = new StringBuilder(256);
		appendValue(value, 0, buffer);
		if (isMultilineMode())
			buffer.append('\n');
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this generator should write the JSON text for the specified {@linkplain JsonValue JSON
	 * value} on a single line.  This method always returns {@code true} for simple values (null, Boolean, number and
	 * string); for compound values (array and object), it applies a heuristic that takes into account the mode and
	 * maximum line length of this generator and the specified indentation.
	 *
	 * @param  value
	 *           the JSON value of interest.
	 * @param  indent
	 *           the number of spaces by which the JSON text is indented.
	 * @return {@code true} if this generator should write the JSON text for {@ode value} on a single line.
	 */

	private boolean isValueOnSingleLine(
		AbstractNode	value,
		int				indent)
	{
		boolean singleLine = true;
		if (isMultilineMode())
		{
			// Case: array
			if (value instanceof ListNode)
			{
				ListNode list = (ListNode)value;
				int numElements = list.getNumElements();
				if (((mode == Mode.EXPANDED) && (numElements > 0))
					|| list.getElements().stream().anyMatch(element -> !isValueOnSingleLine(element, indent + 2)))
					singleLine = false;
				else if (numElements > 1)
				{
					int lineLength = indent + 2 + 2 * numElements;
					for (int i = 0; i < numElements; i++)
					{
						lineLength += list.get(i).toString().length();
						if (lineLength > maxLineLength)
						{
							singleLine = false;
							break;
						}
					}
				}
			}

			// Case: object
			else if (value instanceof MapNode)
			{
				MapNode map = (MapNode)value;
				int numProperties = map.getNumPairs();
				singleLine = (numProperties == 0)
								|| ((mode != Mode.EXPANDED) && (numProperties == 1)
									&& isValueOnSingleLine(map.getPairIterator().next().getValue(), indent + 2));
			}
		}
		return singleLine;
	}

	//------------------------------------------------------------------

	/**
	 * Generates the JSON text for the specified {@linkplain AbstractNode JSON value} and appends it to the specified
	 * buffer.
	 *
	 * @param  value
	 *           the JSON value for which JSON text will be generated.
	 * @param  indent
	 *           the number of spaces by which a line of the JSON text is indented.
	 * @param  buffer
	 *           the buffer to which the JSON text will be appended.
	 * @throws NodeTypeException
	 *           if <i>value</i> does not represent a JSON value.
	 */

	private void appendValue(
		AbstractNode	value,
		int				indent,
		StringBuilder	buffer)
	{
		// Update array of spaces for indent; append indent
		if (isMultilineMode())
		{
			// Get indent of children of target value
			int childIndent = indent + indentIncrement;

			// Update array of spaces
			if (spaces.length < childIndent)
			{
				spaces = new char[childIndent];
				Arrays.fill(spaces, ' ');
			}

			// Append indent
			buffer.append(spaces, 0, indent);
		}

		// Append array
		if (value instanceof ListNode)
			appendArray((ListNode)value, indent, buffer);

		// Append object
		else if (value instanceof MapNode)
			appendObject((MapNode)value, indent, buffer);

		// Append simple value
		else if (JsonUtils.isSimpleJsonValue(value))
			buffer.append(value);

		// Not a JSON value
		else
			throw new NodeTypeException(value.getType());
	}

	//------------------------------------------------------------------

	/**
	 * Generates the JSON text for the specified {@linkplain ListNode JSON array} and appends it to the specified
	 * buffer.
	 *
	 * @param array
	 *          the JSON array for which JSON text will be generated.
	 * @param indent
	 *          the number of spaces by which a line of the JSON text is indented.
	 * @param buffer
	 *          the buffer to which the JSON text will be appended.
	 */

	private void appendArray(
		ListNode		array,
		int				indent,
		StringBuilder	buffer)
	{
		// Get number of elements
		int numElements = array.getNumElements();

		// If required, remove LF and indent before opening bracket
		if (openingBracketOnSameLine)
			removeLineFeedAndIndent(buffer);

		// Append opening bracket
		buffer.append(JsonConstants.ARRAY_START_CHAR);

		// Case: elements of array are on a single line
		if (isValueOnSingleLine(array, indent))
		{
			// Append elements
			for (int i = 0; i < numElements; i++)
			{
				// Append separator between elements
				if (i > 0)
					buffer.append(JsonConstants.ARRAY_ELEMENT_SEPARATOR_CHAR);

				// Append space after separator
				if (mode != Mode.DENSE)
					buffer.append(' ');

				// Append element
				appendValue(array.get(i), 0, buffer);
			}

			// Append space before closing bracket
			if (mode != Mode.DENSE)
				buffer.append(' ');
		}

		// Case: elements of array are not on a single line
		else
		{
			// Append LF after opening brace
			buffer.append('\n');

			// Get indent of children of target value
			int childIndent = indent + indentIncrement;

			// Initialise line length
			int lineLength = 0;

			// Append elements
			for (int i = 0; i < numElements; i++)
			{
				// Get element
				AbstractNode element = array.get(i);

				// Set 'more elements' flag
				boolean moreElements = (i < numElements - 1);

				// Case: element is a container
				if (element.isContainer())
				{
					// Append LF after previous element
					if (lineLength > 0)
						buffer.append('\n');

					// Append indent and element
					appendValue(element, childIndent, buffer);

					// Append separator between elements
					if (moreElements)
						buffer.append(JsonConstants.ARRAY_ELEMENT_SEPARATOR_CHAR);

					// Append LF after element
					buffer.append('\n');

					// Reset line length
					lineLength = 0;
				}

				// Case: element is not a container
				else
				{
					// Convert element to string
					String elementStr = element.toString();

					// If not start of line, wrap line if necessary
					if (lineLength > 0)
					{
						// Increment line length past end of element
						lineLength += elementStr.length() + 1;
						if (moreElements)
							++lineLength;

						// If line would be too long, wrap it
						if (lineLength > maxLineLength)
						{
							buffer.append('\n');
							lineLength = 0;
						}
					}

					// Case: start of line
					if (lineLength == 0)
					{
						// Get index of start of element
						int index = buffer.length();

						// Append indent
						buffer.append(spaces, 0, childIndent);

						// Append element
						buffer.append(elementStr);

						// Append separator between elements
						if (moreElements)
							buffer.append(JsonConstants.ARRAY_ELEMENT_SEPARATOR_CHAR);

						// If expanded mode, append LF after separator ...
						if (mode == Mode.EXPANDED)
							buffer.append('\n');

						// ... otherwise, increment line length
						else
							lineLength = buffer.length() - index;
					}

					// Case: not start of line
					else
					{
						// Append space before element
						buffer.append(' ');

						// Append element
						buffer.append(elementStr);

						// Append separator between elements
						if (moreElements)
							buffer.append(JsonConstants.ARRAY_ELEMENT_SEPARATOR_CHAR);
					}
				}
			}

			// If not start of line, append LF
			if (lineLength > 0)
				buffer.append('\n');

			// Append indent before closing bracket
			buffer.append(spaces, 0, indent);
		}

		// Append closing bracket
		buffer.append(JsonConstants.ARRAY_END_CHAR);
	}

	//------------------------------------------------------------------

	/**
	 * Generates the JSON text for the specified {@linkplain MapNode JSON object} and appends it to the specified
	 * buffer.
	 *
	 * @param object
	 *          the JSON object for which JSON text will be generated.
	 * @param indent
	 *          the number of spaces by which a line of the JSON text is indented.
	 * @param buffer
	 *          the buffer to which the JSON text will be appended.
	 */

	private void appendObject(
		MapNode			object,
		int				indent,
		StringBuilder	buffer)
	{
		// If required, remove LF and indent before opening brace
		if (openingBracketOnSameLine)
			removeLineFeedAndIndent(buffer);

		// Append opening brace
		buffer.append(JsonConstants.OBJECT_START_CHAR);

		// Case: properties of object are on a single line
		if (isValueOnSingleLine(object, indent))
		{
			// Append properties
			Iterator<Map.Entry<String, AbstractNode>> it = object.getPairIterator();
			while (it.hasNext())
			{
				// Append space after separator
				if (mode != Mode.DENSE)
					buffer.append(' ');

				// Get property
				Map.Entry<String, AbstractNode> property = it.next();

				// Append name of property
				buffer.append(object.keyToString(property.getKey()));

				// Append separator between name and value
				buffer.append(JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR);

				// Append space after separator
				if (mode != Mode.DENSE)
					buffer.append(' ');

				// Append value of property
				appendValue(property.getValue(), 0, buffer);

				// Append separator between properties
				if (it.hasNext())
					buffer.append(JsonConstants.OBJECT_PROPERTY_SEPARATOR_CHAR);
			}

			// Append space before closing brace
			if (mode != Mode.DENSE)
				buffer.append(' ');
		}

		// Case: properties of object are not on a single line
		else
		{
			// Get indent of children of target value
			int childIndent = indent + indentIncrement;

			// Append LF after opening brace
			buffer.append('\n');

			// Append properties
			Iterator<Map.Entry<String, AbstractNode>> it = object.getPairIterator();
			while (it.hasNext())
			{
				// Get index of start of property
				int index = buffer.length();

				// Append indent before name of property
				buffer.append(spaces, 0, childIndent);

				// Get property
				Map.Entry<String, AbstractNode> property = it.next();

				// Append name of property
				buffer.append(object.keyToString(property.getKey()));

				// Append separator between name and value
				buffer.append(JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR);

				// Get value of property
				AbstractNode value = property.getValue();

				// If value of property is on single line, append it with a single space before it ...
				if (isValueOnSingleLine(value, buffer.length() - index + 1))
					appendValue(value, 1, buffer);

				// ... otherwise, append LF, indent and value of property
				else
				{
					buffer.append('\n');
					appendValue(value, childIndent, buffer);
				}

				// Append separator between properties
				if (it.hasNext())
					buffer.append(JsonConstants.OBJECT_PROPERTY_SEPARATOR_CHAR);

				// Append LF after property
				buffer.append('\n');
			}

			// Append indent before closing brace
			buffer.append(spaces, 0, indent);
		}

		// Append closing brace
		buffer.append(JsonConstants.OBJECT_END_CHAR);
	}

	//------------------------------------------------------------------

	/**
	 * Removes a line feed and indent from the end of the specified buffer.
	 *
	 * @param buffer
	 *          the buffer from which a line feed and indent will be removed.
	 */

	private void removeLineFeedAndIndent(
		StringBuilder	buffer)
	{
		int index = buffer.length();
		while (--index >= 0)
		{
			char ch = buffer.charAt(index);
			if (ch != ' ')
			{
				if (ch == '\n')
				{
					buffer.setLength(index);
					buffer.append(' ');
				}
				break;
			}
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the current mode of this generator allows JSON text to be written on more than one line.
	 *
	 * @return {@code true} if the current mode of this generator allows JSON text to be written on more than one line;
	 *         {@code false} otherwise.
	 */

	private boolean isMultilineMode()
	{
		return (mode == Mode.NORMAL) || (mode == Mode.EXPANDED);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
