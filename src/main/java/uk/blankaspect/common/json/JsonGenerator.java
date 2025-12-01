/*====================================================================*\

JsonGenerator.java

Class: generator of JSON text.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.json;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Iterator;
import java.util.Map;

import uk.blankaspect.common.basictree.AbstractNode;
import uk.blankaspect.common.basictree.BooleanNode;
import uk.blankaspect.common.basictree.ListNode;
import uk.blankaspect.common.basictree.MapNode;
import uk.blankaspect.common.basictree.NodeTypeException;
import uk.blankaspect.common.basictree.NullNode;
import uk.blankaspect.common.basictree.StringNode;

//----------------------------------------------------------------------


// CLASS: GENERATOR OF JSON TEXT


/**
 * This class implements a generator that transforms a tree of values that are represented by {@linkplain AbstractNode
 * nodes} into JSON text.  The generator operates in one of several {@linkplain OutputMode <i>output modes</i>}, which
 * control the way in which whitespace is written between the tokens of the JSON text.
 */

public class JsonGenerator
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The way in which whitespace is written between the tokens of the JSON text. */
	private	OutputMode					outputMode;

	/** The circumstance in which a new line is written before the opening bracket of a JSON array or the opening brace
		of a JSON object. */
	private	NewLineBeforeLeftBracket	newLineBeforeLeftBracket;

	/** The number of spaces by which indentation is increased from one level to the next. */
	private	int							indentIncrement;

	/** The maximum length of a line of JSON text without wrapping. */
	private	int							maxLineLength;

	/** Flag: if {@code true}, the values of JSON strings and the names of the members of JSON objects will be escaped
		so that they contain only printable characters from the US-ASCII character encoding (ie, characters in the range
		U+0020 to U+007E inclusive). */
	private	boolean						printableAsciiOnly;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a JSON generator that is initialised from the state of the specified builder.
	 *
	 * @param builder
	 *          the builder from whose state the generator will be initialised.
	 */

	private JsonGenerator(
		Builder	builder)
	{
		// Initialise instance variables
		outputMode = builder.outputMode;
		newLineBeforeLeftBracket = builder.newLineBeforeLeftBracket;
		indentIncrement = builder.indentIncrement;
		maxLineLength = builder.maxLineLength;
		printableAsciiOnly = builder.printableAsciiOnly;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a builder for a JSON generator.
	 *
	 * @return a new instance of a builder for a JSON generator.
	 */

	public static Builder builder()
	{
		return new Builder();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain JsonText.Token JSON token} that corresponds to the specified value.
	 *
	 * @param  value
	 *           the value whose corresponding JSON token is sought.
	 * @return the JSON token that corresponds to {@code value}, or {@code null} if there is no such token.
	 */

	public static JsonText.Token getToken(
		AbstractNode	value)
	{
		if (value instanceof NullNode)
			return JsonText.Token.NULL_VALUE;
		if (value instanceof BooleanNode)
			return JsonText.Token.BOOLEAN_VALUE;
		if (JsonUtils.isJsonNumber(value))
			return JsonText.Token.NUMBER_VALUE;
		if (value instanceof StringNode)
			return JsonText.Token.STRING_VALUE;
		return null;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Generates and returns JSON text for the specified {@linkplain AbstractNode node} in accordance with the
	 * properties of this generator:
	 * <ul>
	 *   <li>output mode,</li>
	 *   <li><i>new line before left bracket</i> parameter,</li>
	 *   <li>indent increment,</li>
	 *   <li>maximum line length,</li>
	 *   <li><i>printable ASCII only</i> flag.</li>
	 * </ul>
	 *
	 * @param  node
	 *           the value for which JSON text will be generated.
	 * @return the JSON text that was generated for {@code node}.
	 * @throws NodeTypeException
	 *           if any node in the tree whose root is {@code node} does not correspond to a JSON value.
	 */

	public JsonText generate(
		AbstractNode	node)
	{
		// Initialise JSON text
		JsonText text = new JsonText();

		// Append JSON text for value
		appendValue(node, 0, text);

		// Append new line in multi-line mode
		if (isMultilineMode())
			text.appendNewLine();

		// Return JSON text
		return text;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a JSON string representation of the name of the specified member of an object.
	 *
	 * @param  member
	 *           the object member of interest.
	 * @return a JSON string representation of the name of {@code member}.
	 */

	private String memberNameToString(
		Map.Entry<String, AbstractNode>	member)
	{
		return MapNode.keyToString(member.getKey(), printableAsciiOnly);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the maximum length that is available to append the JSON text of the specified node to the last line of
	 * the specified JSON text.
	 *
	 * @param  node
	 *           the node whose JSON text is considered for appending to the last line of {@code text}.
	 * @param  text
	 *           the JSON text whose last line would have the JSON text of {@code node} appended to it.
	 * @return the maximum length that is available to append the JSON text of {@code node} to the last line of {@code
	 *         text}.
	 */

	private int computeMaxLineLength(
		AbstractNode	node,
		JsonText		text)
	{
		int maxLength = maxLineLength - text.lastLineLength() - 2;
		if (node.getParent() instanceof ListNode listNode)
		{
			if (listNode.get(listNode.getNumElements() - 1) == node)
				++maxLength;
		}
		else if (node.getParent() instanceof MapNode mapNode)
		{
			Iterator<Map.Entry<String, AbstractNode>> it = mapNode.getPairIterator();
			while (it.hasNext())
			{
				if (it.next().getValue() == node)
				{
					if (!it.hasNext())
						++maxLength;
					break;
				}
			}
		}
		return Math.max(0, maxLength);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this generator should write the JSON text for the specified {@linkplain AbstractNode
	 * node} on a single line.  This method always returns {@code true} in single-line output mode and for simple
	 * values (null, Boolean, number and string).  For compound values (array and object), it applies a heuristic that
	 * takes into account the output mode and the specified maximum length of a line of text.
	 *
	 * @param  node
	 *           the node of interest.
	 * @param  maxLength
	 *           the maximum length of a line of text.
	 * @return {@code true} if this generator should write the JSON text for {@code node} on a single line.
	 */

	private boolean isValueOnSingleLine(
		AbstractNode	node,
		int				maxLength)
	{
		// Always on a single line in single-line output mode
		if (!isMultilineMode())
			return true;

		// Assume a single line
		boolean singleLine = true;

		// Case: array
		if (node instanceof ListNode listNode)
		{
			int numElements = listNode.getNumElements();
			if (((outputMode == OutputMode.EXPANDED) && (numElements > 0))
					|| listNode.getElements().stream().anyMatch(element -> !isValueOnSingleLine(element, maxLength)))
				singleLine = false;
			else if (numElements > 1)
			{
				int length = 0;
				for (int i = 0; i < numElements; i++)
				{
					length += listNode.get(i).toString(printableAsciiOnly).length() + 2;
					if (length > maxLength)
					{
						singleLine = false;
						break;
					}
				}
			}
		}

		// Case: object
		else if (node instanceof MapNode mapNode)
		{
			singleLine = switch (mapNode.getNumPairs())
			{
				case 0 -> true;
				case 1 ->
				{
					if (outputMode == OutputMode.EXPANDED)
						yield false;
					Map.Entry<String, AbstractNode> member = mapNode.getPairIterator().next();
					yield isValueOnSingleLine(member.getValue(), maxLength - memberNameToString(member).length());
				}
				default -> false;
			};
		}

		// Return result
		return singleLine;
	}

	//------------------------------------------------------------------

	/**
	 * Generates the JSON text for the specified {@linkplain AbstractNode node} and appends it to the specified text.
	 *
	 * @param  node
	 *           the node corresponding to a JSON value for which JSON text will be generated.
	 * @param  indent
	 *           the number of spaces by which a line of the JSON text is indented.
	 * @param  text
	 *           the JSON text to which the text that is generated by this method will be appended.
	 * @throws NodeTypeException
	 *           if {@code node} does not represent a JSON value.
	 */

	private void appendValue(
		AbstractNode	node,
		int				indent,
		JsonText		text)
	{
		// Append indent
		if (isMultilineMode())
			text.appendSpaces(indent);

		// Case: array
		if (node instanceof ListNode listNode)
			appendArray(listNode, indent, text);

		// Case: object
		else if (node instanceof MapNode mapNode)
			appendObject(mapNode, indent, text);

		// Case: simple value
		else
		{
			// Get token
			JsonText.Token token = getToken(node);
			if (token == null)
				throw new NodeTypeException(node.getType());

			// Append JSON text of value
			text.append(node.toString(printableAsciiOnly), token);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Generates the JSON text for the specified {@linkplain ListNode JSON array} and appends it to the specified text.
	 *
	 * @param array
	 *          the node corresponding to a JSON array for which JSON text will be generated.
	 * @param indent
	 *          the number of spaces by which a line of the JSON text is indented.
	 * @param text
	 *          the JSON text to which the text that is generated by this method will be appended.
	 */

	private void appendArray(
		ListNode	array,
		int			indent,
		JsonText	text)
	{
		// If required, remove line break and indent before opening bracket
		removeLineBreakAndIndent(text);

		// Append opening bracket
		text.append(JsonConstants.ARRAY_START_CHAR, JsonText.Token.ARRAY_DELIMITER);

		// Get number of array elements
		int numElements = array.getNumElements();

		// Case: elements of array are on a single line
		if (isValueOnSingleLine(array, computeMaxLineLength(array, text)))
		{
			// Append elements
			for (int i = 0; i < numElements; i++)
			{
				// Append separator between elements
				if (i > 0)
					text.append(JsonConstants.ARRAY_ELEMENT_SEPARATOR_CHAR, JsonText.Token.ITEM_SEPARATOR);

				// Append space after separator
				if (outputMode != OutputMode.DENSE)
					text.appendSpace();

				// Append element
				appendValue(array.get(i), 0, text);
			}

			// Append space before closing bracket
			if (outputMode != OutputMode.DENSE)
				text.appendSpace();
		}

		// Case: elements of array are not on a single line
		else
		{
			// Append new line after opening brace
			text.appendNewLine();

			// Get indent of children of target value
			int childIndent = indent + indentIncrement;

			// Append elements
			for (int i = 0; i < numElements; i++)
			{
				// Get element
				AbstractNode element = array.get(i);

				// Get length of current line
				int lineLength = text.lastLineLength();

				// Set 'more elements' flag
				boolean moreElements = (i < numElements - 1);

				// Calculate maximum length of single line of text
				int maxLength = maxLineLength - ((lineLength == 0) ? childIndent : lineLength + 1) - 2;
				if (moreElements)
					--maxLength;

				// Case: element is on a single line
				if (isValueOnSingleLine(element, maxLength))
				{
					// Convert element to string
					String elementStr = element.toString(printableAsciiOnly);

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
							text.appendNewLine();
							lineLength = 0;
						}
					}

					// Append indent or space before element
					if (lineLength == 0)
						text.appendSpaces(childIndent);
					else
						text.appendSpace();

					// Append element
					text.append(elementStr, getToken(element));

					// Append separator between elements
					if (moreElements)
						text.append(JsonConstants.ARRAY_ELEMENT_SEPARATOR_CHAR, JsonText.Token.ITEM_SEPARATOR);

					// If expanded mode, append new line after element
					if (outputMode == OutputMode.EXPANDED)
						text.appendNewLine();
				}

				// Case: element is not on a single line
				else
				{
					// Append new line after previous element
					if (lineLength > 0)
						text.appendNewLine();

					// Append indent and element
					appendValue(element, childIndent, text);

					// Append separator between elements
					if (moreElements)
						text.append(JsonConstants.ARRAY_ELEMENT_SEPARATOR_CHAR, JsonText.Token.ITEM_SEPARATOR);

					// Append new line after element
					text.appendNewLine();
				}
			}

			// If not start of line, append new line
			if (text.lastLineLength() > 0)
				text.appendNewLine();

			// Append indent before closing bracket
			text.appendSpaces(indent);
		}

		// Append closing bracket
		text.append(JsonConstants.ARRAY_END_CHAR, JsonText.Token.ARRAY_DELIMITER);
	}

	//------------------------------------------------------------------

	/**
	 * Generates the JSON text for the specified {@linkplain MapNode JSON object} and appends it to the specified text.
	 *
	 * @param object
	 *          the node corresponding to a JSON object for which JSON text will be generated.
	 * @param indent
	 *          the number of spaces by which a line of the JSON text is indented.
	 * @param text
	 *          the JSON text to which the text that is generated by this method will be appended.
	 */

	private void appendObject(
		MapNode		object,
		int			indent,
		JsonText	text)
	{
		// If required, remove line break and indent before opening brace
		removeLineBreakAndIndent(text);

		// Append opening brace
		text.append(JsonConstants.OBJECT_START_CHAR, JsonText.Token.OBJECT_DELIMITER);

		// Case: members of object are on a single line
		if (isValueOnSingleLine(object, computeMaxLineLength(object, text)))
		{
			// Append members of object
			Iterator<Map.Entry<String, AbstractNode>> it = object.getPairIterator();
			while (it.hasNext())
			{
				// Append space after separator
				if (outputMode != OutputMode.DENSE)
					text.appendSpace();

				// Get member of object
				Map.Entry<String, AbstractNode> member = it.next();

				// Append name of member
				text.append(memberNameToString(member), JsonText.Token.OBJECT_MEMBER_NAME);

				// Append separator between name and value
				text.append(JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR, JsonText.Token.OBJECT_NAME_VALUE_SEPARATOR);

				// Append space after separator
				if (outputMode != OutputMode.DENSE)
					text.appendSpace();

				// Append value of member
				appendValue(member.getValue(), 0, text);

				// Append separator between members of object
				if (it.hasNext())
					text.append(JsonConstants.OBJECT_MEMBER_SEPARATOR_CHAR, JsonText.Token.ITEM_SEPARATOR);
			}

			// Append space before closing brace
			if (outputMode != OutputMode.DENSE)
				text.appendSpace();
		}

		// Case: members of object are not on a single line
		else
		{
			// Get indent of children of target value
			int childIndent = indent + indentIncrement;

			// Append new line after opening brace
			text.appendNewLine();

			// Append members of object
			Iterator<Map.Entry<String, AbstractNode>> it = object.getPairIterator();
			while (it.hasNext())
			{
				// Append indent before name of member
				text.appendSpaces(childIndent);

				// Get member
				Map.Entry<String, AbstractNode> member = it.next();

				// Append name of member
				text.append(memberNameToString(member), JsonText.Token.OBJECT_MEMBER_NAME);

				// Append separator between name and value
				text.append(JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR, JsonText.Token.OBJECT_NAME_VALUE_SEPARATOR);

				// Get value of member
				AbstractNode value = member.getValue();

				// Calculate maximum length of single line of text
				int maxLength = maxLineLength - text.lastLineLength() - 3;
				if (it.hasNext())
					--maxLength;

				// If value of member is on single line, append it with a single space before it ...
				if (isValueOnSingleLine(value, maxLength))
					appendValue(value, 1, text);

				// ... otherwise, append new line, indent and value of member
				else
				{
					text.appendNewLine();
					appendValue(value, childIndent, text);
				}

				// Append separator between members of object
				if (it.hasNext())
					text.append(JsonConstants.OBJECT_MEMBER_SEPARATOR_CHAR, JsonText.Token.ITEM_SEPARATOR);

				// Append new line after member
				text.appendNewLine();
			}

			// Append indent before closing brace
			text.appendSpaces(indent);
		}

		// Append closing brace
		text.append(JsonConstants.OBJECT_END_CHAR, JsonText.Token.OBJECT_DELIMITER);
	}

	//------------------------------------------------------------------

	/**
	 * Removes a line break and indent from the end of the specified JSON text.
	 *
	 * @param text
	 *          the JSON text from which a line break and indent will be removed.
	 */

	private void removeLineBreakAndIndent(
		JsonText	text)
	{
		// Do nothing if there are fewer than two lines of JSON text or there should always be a new line before a left
		// bracket
		if ((text.lines().size() < 2) || (newLineBeforeLeftBracket == NewLineBeforeLeftBracket.ALWAYS))
			return;

		// Get last line of text
		JsonText.Line line = text.lastLine();

		// Consolidate adjacent spans of spaces
		line.normaliseSpans();

		// Get offset to start of last line
		int offset = line.offset();

		// If the last line contains only spaces and the penultimate line has an unwanted LF, replace the LF and the
		// spaces after it with a single space
		if ((line.spans().size() == 1) && (line.spans().get(0).token() == JsonText.Token.SPACE)
				&& ((newLineBeforeLeftBracket == NewLineBeforeLeftBracket.NEVER)
						|| ((offset > 1)
							&& (text.buffer().charAt(offset - 2) == JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR))))
		{
			// Remove last line of spaces
			text.lines().remove(text.lines().size() - 1);

			// Get new last line
			line = text.lastLine();

			// Remove LF from last line
			line.spans().remove(line.spans().size() - 1);
			text.buffer().setLength(offset - 1);

			// Replace removed LF with space
			text.appendSpace();
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the output mode of this generator allows JSON text to be written on more than one line.
	 *
	 * @return {@code true} if the output mode of this generator allows JSON text to be written on more than one line;
	 *         {@code false} otherwise.
	 */

	private boolean isMultilineMode()
	{
		return (outputMode == OutputMode.NORMAL) || (outputMode == OutputMode.EXPANDED);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: BUILDER FOR JSON GENERATOR


	/**
	 * This class implements a builder for a {@linkplain JsonGenerator JSON generator}.
	 */

	public static class Builder
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The default output mode of a generator. */
		private static final	OutputMode	DEFAULT_OUTPUT_MODE	= OutputMode.NORMAL;

		/** The default circumstance in which a new line is written before the opening bracket of a JSON array or the
			opening brace of a JSON object. */
		private static final	NewLineBeforeLeftBracket	DEFAULT_NEW_LINE_BEFORE_LEFT_BRACKET	=
				NewLineBeforeLeftBracket.EXCEPT_AFTER_NAME;

		/** The default number of spaces by which indentation will be increased from one level to the next. */
		private static final	int		DEFAULT_INDENT_INCREMENT	= 2;

		/** The default maximum line length. */
		private static final	int		DEFAULT_MAX_LINE_LENGTH		= 96;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The way in which whitespace is written between the tokens of the JSON text. */
		private	OutputMode					outputMode;

		/** The circumstance in which a new line is written before the opening bracket of a JSON array or the opening
			brace of a JSON object. */
		private	NewLineBeforeLeftBracket	newLineBeforeLeftBracket;

		/** The number of spaces by which indentation is increased from one level to the next. */
		private	int							indentIncrement;

		/** The maximum length of a line of JSON text without wrapping. */
		private	int							maxLineLength;

		/** Flag: if {@code true}, the values of JSON strings and the names of the members of JSON objects will be
			escaped so that they contain only printable characters from the US-ASCII character encoding (ie, characters
			in the range U+0020 to U+007E inclusive). */
		private	boolean						printableAsciiOnly;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a builder for a {@linkplain JsonGenerator JSON generator}.
		 */

		private Builder()
		{
			// Initialise instance variables
			outputMode = DEFAULT_OUTPUT_MODE;
			newLineBeforeLeftBracket = DEFAULT_NEW_LINE_BEFORE_LEFT_BRACKET;
			indentIncrement = DEFAULT_INDENT_INCREMENT;
			maxLineLength = DEFAULT_MAX_LINE_LENGTH;
			printableAsciiOnly = true;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Sets the way in which whitespace is written between the tokens of the JSON text.  The default value is {@link
		 * OutputMode#NORMAL NORMAL}.
		 *
		 * @param  outputMode
		 *           the way in which whitespace is written between the tokens of the JSON text.
		 * @return this builder.
		 */

		public Builder outputMode(
			OutputMode	outputMode)
		{
			// Update instance variable
			this.outputMode = outputMode;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the circumstance in which a new line is written before the opening bracket of a JSON array or the
		 * opening brace of a JSON object.  The default value is {@link NewLineBeforeLeftBracket#EXCEPT_AFTER_NAME
		 * EXCEPT_AFTER_NAME}.
		 *
		 * @param  newLineBeforeLeftBracket
		 *           the circumstance in which a new line is written before the opening bracket of a JSON array or the
		 *           opening brace of a JSON object.
		 * @return this builder.
		 */

		public Builder newLineBeforeLeftBracket(
			NewLineBeforeLeftBracket	newLineBeforeLeftBracket)
		{
			// Update instance variable
			this.newLineBeforeLeftBracket = newLineBeforeLeftBracket;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the number of spaces by which indentation is increased from one level to the next.  The default value is
		 * 2.
		 *
		 * @param  indentIncrement
		 *           the number of spaces by which indentation is increased from one level to the next.
		 * @return this builder.
		 */

		public Builder indentIncrement(
			int	indentIncrement)
		{
			// Update instance variable
			this.indentIncrement = indentIncrement;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the maximum length of a line of JSON text without wrapping.  The default value is 96.
		 *
		 * @param  maxLineLength
		 *           the maximum length of a line of JSON text without wrapping.
		 * @return this builder.
		 */

		public Builder maxLineLength(
			int	maxLineLength)
		{
			// Update instance variable
			this.maxLineLength = maxLineLength;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the flag that determines whether JSON string values and the names of the members of JSON objects will be
		 * escaped so that they contain only printable characters from the US-ASCII character encoding (ie, characters
		 * in the range U+0020 to U+007E inclusive).  The default value is {@code true}.
		 *
		 * @param  printableAsciiOnly
		 *           if {@code true}, the values of JSON strings and the names of the members of JSON objects will be
		 *           escaped so that they contain only printable characters from the US-ASCII character encoding.
		 * @return this builder.
		 */

		public Builder printableAsciiOnly(
			boolean	printableAsciiOnly)
		{
			// Update instance variable
			this.printableAsciiOnly = printableAsciiOnly;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates and returns a new instance of a JSON generator that is initialised from the state of this builder.
		 *
		 * @return a new instance of a JSON generator.
		 */

		public JsonGenerator build()
		{
			return new JsonGenerator(this);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
