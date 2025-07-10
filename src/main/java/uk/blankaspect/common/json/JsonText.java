/*====================================================================*\

JsonText.java

Class: generated JSON text.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.json;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.text.ITextLine;
import uk.blankaspect.common.text.ITextSpan;

//----------------------------------------------------------------------


// CLASS: GENERATED JSON TEXT


/**
 * This class implements a buffer for JSON text.  Text is added to the buffer with the methods of this class.
 * <p>
 * The JSON text consists of a sequence of {@linkplain Line lines}.  A line consists of a sequence of {@linkplain Span
 * spans}; each span is associated with a {@linkplain Token token}.  The lines and spans may be extracted from the
 * buffer.
 * </p>
 */

public class JsonText
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The buffer that contains JSON text. */
	private	StringBuilder	buffer;

	/** A list of the lines of text in {@link #buffer}. */
	private	List<Line>		lines;

	/** An array of spaces that is used by {@link #appendSpaces(int)}. */
	private	char[]			spaces;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of JSON text.
	 */

	public JsonText()
	{
		// Initialise instance variables
		buffer = new StringBuilder(1024);
		lines = new ArrayList<>();
		spaces = new char[64];
		Arrays.fill(spaces, ' ');
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public String toString()
	{
		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the buffer that contains the JSON text.
	 *
	 * @return the buffer that contains the JSON text.
	 */

	public StringBuilder buffer()
	{
		return buffer;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the lines of this JSON text within the {@linkplain #buffer() buffer}.
	 *
	 * @return a list of the lines of this JSON text within the buffer.
	 */

	public List<Line> lines()
	{
		return lines;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the last line of this JSON text within the {@linkplain #buffer() buffer}.
	 *
	 * @return the last line of this JSON text within the buffer, or {@code null} if there are no lines.
	 */

	public Line lastLine()
	{
		return lines.isEmpty() ? null : lines.get(lines.size() - 1);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the length of the {@linkplain #lastLine() last line} of this JSON text.
	 *
	 * @return the length of the last line of JSON text, or 0 if there are no lines.
	 */

	public int lastLineLength()
	{
		Line line = lastLine();
		return (line == null) ? 0 : buffer.length() - line.offset;
	}

	//------------------------------------------------------------------

	/**
	 * Appends the specified character to this JSON text, and adds a new span for the character that associates it with
	 * the specified token.
	 *
	 * @param ch
	 *          the character that will be appended to this JSON text.
	 * @param token
	 *          the token with which {@code ch} will be associated in the span that is created for it.
	 */

	public void append(
		char	ch,
		Token	token)
	{
		// Get the last line
		Line line = lastLine();

		// Create a new line, if necessary
		if (line == null)
			line = appendNewLine();

		// Add a span for the character
		line.addSpan(1, token);

		// Append the character to the buffer
		buffer.append(ch);
	}

	//------------------------------------------------------------------

	/**
	 * Appends the specified text to this JSON text, and adds a new span for the text that associates it with the
	 * specified token.
	 *
	 * @param text
	 *          the text that will be appended to this JSON text.
	 * @param token
	 *          the token with which {@code text} will be associated in the span that is created for it.
	 */

	public void append(
		CharSequence	text,
		Token			token)
	{
		int length = text.length();
		if (length > 0)
		{
			// Get the last line
			Line line = lastLine();

			// Create a new line, if necessary
			if (line == null)
				line = appendNewLine();

			// Add a span for the text
			line.addSpan(text.length(), token);

			// Append the text to the buffer
			buffer.append(text);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Appends a single space character (U+0020) to this JSON text, and adds a new span for the space.
	 */

	public void appendSpace()
	{
		append(' ', Token.SPACE);
	}

	//------------------------------------------------------------------

	/**
	 * Appends the specified number of space characters (U+0020) to this JSON text, and adds a new span for the spaces.
	 *
	 * @param numSpaces
	 *          the number of space characters that will be appended to this JSON text.
	 */

	public void appendSpaces(
		int	numSpaces)
	{
		if (numSpaces > 0)
		{
			// Get the last line
			Line line = lastLine();

			// Create a new line, if necessary
			if (line == null)
				line = appendNewLine();

			// Add a span for the spaces
			line.addSpan(numSpaces, Token.SPACE);

			// If the current array of spaces is too short, create a new array
			if (spaces.length < numSpaces)
			{
				spaces = new char[numSpaces];
				Arrays.fill(spaces, ' ');
			}

			// Append the spaces to the buffer
			buffer.append(spaces, 0, numSpaces);
		}
	}

	//------------------------------------------------------------------

	/**
	 * If this JSON text is not empty, a line-feed character (U+000A) is appended to it and a new span for it is added
	 * to the current line.  A new {@linkplain Line line} is created and added to the list of lines, and the new line is
	 * returned.
	 *
	 * @return a new line that was added to the list of lines.
	 */

	public Line appendNewLine()
	{
		// Get the last line
		Line line = lastLine();

		// If there is a line, add a span for the new line and append an LF character to the buffer
		if (line != null)
		{
			line.addSpan(1, Token.NEW_LINE);
			buffer.append('\n');
		}

		// Add a new line to the list of lines
		line = new Line();
		lines.add(line);

		// Return the new line
		return line;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: JSON TOKEN


	/**
	 * This is an enumeration of the tokens that may be associated with elements of JSON text.
	 */

	public enum Token
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/**
		 * A JSON null value.
		 */
		NULL_VALUE,

		/**
		 * A JSON Boolean value.
		 */
		BOOLEAN_VALUE,

		/**
		 * A JSON number value.
		 */
		NUMBER_VALUE,

		/**
		 * A JSON string value.
		 */
		STRING_VALUE,

		/**
		 * The start or end delimiter of a JSON array.
		 */
		ARRAY_DELIMITER,

		/**
		 * The name of a member of a JSON object.
		 */
		OBJECT_MEMBER_NAME,

		/**
		 * The separator between the name and value of a member of a JSON object.
		 */
		OBJECT_NAME_VALUE_SEPARATOR,

		/**
		 * The start or end delimiter of a JSON object.
		 */
		OBJECT_DELIMITER,

		/**
		 * The separator between adjacent elements of a JSON array or adjacent members of a JSON object.
		 */
		ITEM_SEPARATOR,

		/**
		 * A sequence of space characters (U+0020).
		 */
		SPACE,

		/**
		 * A line-feed character (U+000A).
		 */
		NEW_LINE;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The key that is associated with this token. */
		private	String	key;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an enumeration constant for a token.
		 */

		private Token()
		{
			// Initialise instance variables
			key = StringUtils.toCamelCase(name());
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: TEXT SPAN


	/**
	 * This record encapsulates a subsequence of the text of a {@link Line} that is associated with a JSON token.
	 *
	 * @param line
	 *          the line of which the span is a subsequence.
	 * @param length
	 *          the length of the text of the span.
	 * @param token
	 *          the token with which the span will be associated.
	 */

	public record Span(
		Line	line,
		int		length,
		Token	token)
		implements ITextSpan
	{

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ITextSpan interface
	////////////////////////////////////////////////////////////////////

		/**
		 * {@inheritDoc}
		 */

		@Override
		public int offset()
		{
			int offset = line.offset;
			for (Span span : line.spans)
			{
				if (this == span)
					return offset;
				offset += length;
			}
			return -1;
		}

		//--------------------------------------------------------------

		/**
		 * {@inheritDoc}
		 */

		@Override
		public List<String> keys()
		{
			return List.of(token.key);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: LINE OF TEXT


	/**
	 * This class encapsulates a line of text, which consists of a sequence of {@linkplain Span spans}.
	 */

	public class Line
		implements ITextLine
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The offset to the start of this line in the buffer of the enclosing instance of {@link JsonText}. */
		private	int			offset;

		/** A list of the spans within this line. */
		private	List<Span>	spans;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a line.
		 */

		private Line()
		{
			// Initialise instance variables
			offset = JsonText.this.buffer.length();
			spans = new ArrayList<>();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : ITextLine interface
	////////////////////////////////////////////////////////////////////

		/**
		 * {@inheritDoc}
		 */

		@Override
		public String text()
		{
			return JsonText.this.buffer.substring(offset, endOffset());
		}

		//--------------------------------------------------------------

		/**
		 * {@inheritDoc}
		 */

		@Override
		public int offset()
		{
			return offset;
		}

		//--------------------------------------------------------------

		/**
		 * {@inheritDoc}
		 */

		@Override
		public List<Span> spans()
		{
			return spans;
		}

		//--------------------------------------------------------------

		/**
		 * {@inheritDoc}
		 */

		@Override
		public int numSpans()
		{
			return spans.size();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * {@inheritDoc}
		 */

		@Override
		public String toString()
		{
			return JsonText.this.buffer.substring(offset, endOffset());
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns {@code true} if this line is empty.
		 *
		 * @return {@code true} if this line is empty.
		 */

		public boolean isEmpty()
		{
			return (length() == 0);
		}

		//--------------------------------------------------------------

		/**
		 * Combines adjacent spans that are associated with the same token.
		 */

		public void normaliseSpans()
		{
			for (int i = spans.size() - 1; i > 0; i--)
			{
				Span span1 = spans.get(i - 1);
				Span span2 = spans.get(i);
				if (span1.token == span2.token)
				{
					spans.remove(i);
					spans.remove(i - 1);
					spans.add(new Span(this, span1.length + span2.length, span1.token));
				}
			}
		}

		//--------------------------------------------------------------

		/**
		 * Adds a span of the specified length to the list of spans and associates the span with the specified token.
		 *
		 * @param length
		 *          the length of the span.
		 * @param token
		 *          the token with which the span will be associated.
		 */

		private void addSpan(
			int		length,
			Token	token)
		{
			spans.add(new Span(this, length, token));
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
