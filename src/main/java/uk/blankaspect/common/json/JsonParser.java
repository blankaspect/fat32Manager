/*====================================================================*\

JsonParser.java

Class: JSON parser.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.json;

//----------------------------------------------------------------------


// IMPORTS


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import java.math.BigDecimal;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.util.ArrayDeque;
import java.util.Deque;

import uk.blankaspect.common.basictree.AbstractNode;
import uk.blankaspect.common.basictree.BooleanNode;
import uk.blankaspect.common.basictree.DoubleNode;
import uk.blankaspect.common.basictree.IntNode;
import uk.blankaspect.common.basictree.ListNode;
import uk.blankaspect.common.basictree.LongNode;
import uk.blankaspect.common.basictree.MapNode;
import uk.blankaspect.common.basictree.NullNode;
import uk.blankaspect.common.basictree.StringNode;

//----------------------------------------------------------------------

/**
 * This class implements a parser that transforms JSON text into a tree of values that are represented by {@linkplain
 * AbstractNode nodes}.  The input text of the parser is expected to conform to the JSON grammar as specified in the
 * following documents:
 * <ul>
 *   <li><a href="https://tools.ietf.org/html/rfc7159">IETF RFC7159</a></li>
 *   <li><a href="https://www.ecma-international.org/publications/standards/Ecma-404.htm">ECMA-404</a></li>
 * </ul>
 * <p>
 * The parser is implemented in the {@link #parse(Reader)} method as a <a
 * href="https://en.wikipedia.org/wiki/Finite-state_machine">finite-state machine</a> (FSM) that terminates with an
 * exception at the first error in the input text.  The FSM combines the lexical analysis and parsing of the input text
 * with the generation of the output (a tree of {@linkplain AbstractNode nodes} that represent JSON values).
 * </p>
 */

public class JsonParser
{
	/** Whitespace characters. */
	private static final	String	WHITESPACE	= "\t\n\r ";

	/** Structural characters. */
	private static final	char[]	STRUCTURAL_CHARS	= new char[]
	{
		JsonConstants.ARRAY_START_CHAR,
		JsonConstants.ARRAY_END_CHAR,
		JsonConstants.OBJECT_START_CHAR,
		JsonConstants.OBJECT_END_CHAR,
		JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR,
		JsonConstants.OBJECT_PROPERTY_SEPARATOR_CHAR
	};

	/** Value terminators: the union of whitespace characters and structural characters. */
	private static final	String	VALUE_TERMINATORS	= WHITESPACE + new String(STRUCTURAL_CHARS);

	/** The prefix of a four-hex-digit Unicode representation of a character. */
	private static final	String	UNICODE_PREFIX	= "U+";

	/** The number of hexadecimal digits in a Unicode escape sequence. */
	private static final	int		UNICODE_SEQUENCE_LENGTH	= 4;

	/** Miscellaneous strings. */
	private static final	String	CHARACTER_NOT_ALLOWED_STR	= "the character %s at index %s is not allowed.";
	private static final	String	ENDED_PREMATURELY_STR		= "it ended prematurely at index %s.";
	private static final	String	UNSUPPORTED_OPERATION_STR	= "Unsupported operation";

	/** Mappings from characters in an escape sequence to their corresponding literal characters. */
	private static final	char[][]	ESCAPE_MAPPINGS	=
	{
		{ '\\', '\\' },
		{ '\"', '\"' },
		{ '/',  '/' },
		{ 'b',  '\b' },
		{ 't',  '\t' },
		{ 'n',  '\n' },
		{ 'f',  '\f' },
		{ 'r',  '\r' }
	};

	/** The states of the parser. */
	private enum State
	{
		VALUE_START,
		VALUE_END,
		LITERAL_VALUE,
		NUMBER_VALUE,
		STRING_VALUE,
		PROPERTY_START,
		PROPERTY_NAME_START,
		PROPERTY_NAME,
		PROPERTY_NAME_END,
		PROPERTY_END,
		ARRAY_ELEMENT_START,
		ARRAY_ELEMENT_END,
		DONE
	}

	/** The states of the number validator. */
	private enum NumberState
	{
		INTEGER_PART_SIGN,
		INTEGER_PART_FIRST_DIGIT,
		INTEGER_PART_DIGITS,
		FRACTION_PART_FIRST_DIGIT,
		FRACTION_PART_DIGITS,
		EXPONENT_SIGN,
		EXPONENT_FIRST_DIGIT,
		EXPONENT_DIGITS,
		DONE
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	ERROR_READING_FROM_STREAM		= "An error occured when reading from the input stream.";
		String	PREMATURE_END_OF_TEXT			= "The input text ended prematurely.";
		String	EXTRANEOUS_TEXT					= "There is extraneous text after the JSON value.";
		String	VALUE_EXPECTED					= "A value was expected.";
		String	PROPERTY_NAME_EXPECTED			= "A property name was expected.";
		String	NAME_SEPARATOR_EXPECTED			= "A name separator was expected.";
		String	END_OF_OBJECT_EXPECTED			= "An end-of-object character was expected.";
		String	ARRAY_ELEMENT_EXPECTED			= "An array element was expected.";
		String	END_OF_ARRAY_EXPECTED			= "An end-of-array character was expected.";
		String	ILLEGAL_CHARACTER_IN_STRING		= "The character '%s' is not allowed in a string.";
		String	ILLEGAL_VALUE					= "The value is illegal.";
		String	ILLEGAL_ESCAPE_SEQUENCE			= "The escape sequence '%s' is illegal.";
		String	ILLEGAL_UNICODE_ESCAPE_SEQUENCE	= "The Unicode escape sequence '%s' is illegal.";
		String	DUPLICATE_PROPERTY_NAME			= "The object has more than one property with the name '%s'.";
		String	INVALID_NUMBER					= "The number is not valid";
		String	TOO_LARGE_FOR_INTEGER			= "The number is too large for an integer.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The character stream from which the JSON text is read. */
	private	Reader			inputStream;

	/** The last character that was read from {@link #inputStream}. */
	private	char			inputChar;

	/** Flag: if {@code true}, the end of the input stream has been reached. */
	private	boolean			endOfInput;

	/** Flag: if {@code true}, the last character that was read from the input stream has been pushed back. */
	private	boolean			pushedBack;

	/** The index of the next character in the input stream. */
	private	int				index;

	/** The index of the current line in the input text. */
	private	int				lineIndex;

	/** The index of the start of the current line in the input text. */
	private	int				lineStartIndex;

	/** The index of the current token in the input text. */
	private	int				tokenIndex;

	/** A buffer for the current token. */
	private	StringBuilder	tokenBuffer;

	/** Flag: if {@code true}, a JSON number that is deemed to be an integer but is too large to be stored as a
		{@linkplain Long signed 64-bit integer} will be stored as a {@linkplain Double double-precision floating-point
		number}. */
	private	boolean			storeExcessiveIntegerAsFP;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a JSON parser.
	 */

	public JsonParser()
	{
		// Initialise instance variables
		tokenBuffer = new StringBuilder();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the specified character is whitespace.
	 *
	 * @param  ch
	 *           the character that will be tested.
	 * @return {@code true} if {@code ch} is whitespace.
	 */

	private static boolean isWhitespace(
		char	ch)
	{
		return (WHITESPACE.indexOf(ch) >= 0);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified character is a value terminator (ie, either whitespace or a structural
	 * character).
	 *
	 * @param  ch
	 *           the character that will be tested.
	 * @return {@code true} if {@code ch} is whitespace or a structural character.
	 */

	private static boolean isValueTerminator(
		char	ch)
	{
		return (VALUE_TERMINATORS.indexOf(ch) >= 0);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets or clears the flag that determines whether a JSON number that is deemed to be an integer but is too large to
	 * be stored as a signed 64-bit integer (ie, a {@code long}) will be stored as a double-precision floating-point
	 * number (ie, a {@code double}).
	 *
	 * @param storeExcessiveIntegerAsFP
	 *          if {@code true} a JSON number that is deemed to be an integer but is too large for a {@code long} will
	 *          be stored as a {@code double}.
	 */

	public void setStoreExcessiveIntegerAsFP(
		boolean	storeExcessiveIntegerAsFP)
	{
		this.storeExcessiveIntegerAsFP = storeExcessiveIntegerAsFP;
	}

	//------------------------------------------------------------------

	/**
	 * Parses the specified text.  If the text conforms to the JSON grammar, this method transforms it into a tree of
	 * {@linkplain AbstractNode nodes} that represent JSON values and returns the root of the tree.
	 *
	 * @param  text
	 *           the text that will be parsed as JSON text.
	 * @return the tree of JSON values that was created from parsing {@code text}.
	 * @throws ParseException
	 *           if an error occurred when parsing the input text.
	 */

	public AbstractNode parse(
		CharSequence	text)
		throws ParseException
	{
		// Validate argument
		if (text == null)
			throw new IllegalArgumentException("Null text");

		// Parse text
		return parse(new Reader()
		{
			@Override
			public int read()
				throws IOException
			{
				return (index < text.length()) ? text.charAt(index) : -1;
			}

			@Override
			public int read(char[] buffer,
							int    offset,
							int    length)
				throws IOException
			{
				throw new IOException(UNSUPPORTED_OPERATION_STR);
			}

			@Override
			public void close()
				throws IOException
			{
				throw new IOException(UNSUPPORTED_OPERATION_STR);
			}
		});
	}

	//------------------------------------------------------------------

	/**
	 * Parses the text that is composed of characters that are read from the specified byte stream using the UTF-8
	 * character encoding.  If the text conforms to the JSON grammar, this method transforms it into a tree of
	 * {@linkplain AbstractNode nodes} that represent JSON values and returns the root of the tree.
	 *
	 * @param  inputStream
	 *           the byte stream from which the JSON text will be read.
	 * @return the tree of JSON values that was created from parsing the text that was read from {@code inputStream}.
	 * @throws ParseException
	 *           if an error occurred when parsing the input text.
	 */

	public AbstractNode parse(
		InputStream	inputStream)
		throws ParseException
	{
		return parse(inputStream, StandardCharsets.UTF_8);
	}

	//------------------------------------------------------------------

	/**
	 * Parses the text that is composed of characters that are read from the specified byte stream using the specified
	 * character encoding.  If the text conforms to the JSON grammar, this method transforms it into a tree of
	 * {@linkplain AbstractNode nodes} that represent JSON values and returns the root of the tree.
	 *
	 * @param  inputStream
	 *           the byte stream from which the JSON text will be read.
	 * @param  encoding
	 *           the character encoding of {@code inputStream}; if {@code null}, the UTF-8 encoding will be used.
	 * @return the tree of JSON values that was created from parsing the text that was read from {@code inputStream}.
	 * @throws ParseException
	 *           if an error occurred when parsing the input text.
	 */

	public AbstractNode parse(
		InputStream	inputStream,
		Charset		encoding)
		throws ParseException
	{
		// Validate argument
		if (inputStream == null)
			throw new IllegalArgumentException("Null input stream");

		// Read text from input stream and parse it
		return parse(new InputStreamReader(inputStream, (encoding == null) ? StandardCharsets.UTF_8 : encoding));
	}

	//------------------------------------------------------------------

	/**
	 * Parses the text that is composed of characters that are read from the specified input stream.  If the text
	 * conforms to the JSON grammar, this method transforms it into a tree of {@linkplain AbstractNode nodes} that
	 * represent JSON values and returns the root of the tree.
	 *
	 * @param  inputStream
	 *           the character stream from which the JSON text will be read.
	 * @return the tree of JSON values that was created from parsing the text that was read from {@code inputStream}.
	 * @throws ParseException
	 *           if an error occurred when parsing the input text.
	 */

	public AbstractNode parse(
		Reader	inputStream)
		throws ParseException
	{
		// Validate argument
		if (inputStream == null)
			throw new IllegalArgumentException("Null input stream");

		// Initialise instance variables
		this.inputStream = inputStream;

		// Reset instance variables
		index = 0;
		lineIndex = 0;
		lineStartIndex = 0;
		tokenIndex = 0;
		tokenBuffer.setLength(0);

		// Initialise local variables
		Deque<PropertyName> propertyNameStack = new ArrayDeque<>();
		int propertyIndex = 0;
		int propertyLineIndex = 0;
		AbstractNode value = null;
		State state = State.VALUE_START;

		// Parse text
		while (state != State.DONE)
		{
			// Get next character from input stream
			char ch = getNextChar();

			// Execute finite-state machine
			switch (state)
			{
				//----  Start of JSON value
				case VALUE_START:
				{
					// Case: character is whitespace
					if (isWhitespace(ch))
					{
						// If end of input stream, test whether parsing is complete ...
						if (endOfInput)
						{
							// If current value is not root, input stream has ended prematurely ...
							if (!value.isRoot())
								throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex,
														 index - lineStartIndex);

							// ... otherwise, parsing is complete
							state = State.DONE;
						}

						// ... otherwise, if character is LF, start new line
						else
							newLine(ch);
					}

					// Case: character is not whitespace
					else
					{
						// Update index of current token
						tokenIndex = index - 1;

						// Test for extraneous text after root value
						if ((value != null) && value.isRoot() && !value.isContainer())
							throw new ParseException(ErrorMsg.EXTRANEOUS_TEXT, lineIndex, tokenIndex - lineStartIndex);

						// Clear token buffer
						tokenBuffer.setLength(0);

						// Set next state according to character
						switch (ch)
						{
							case StringNode.START_CHAR:
								state = State.STRING_VALUE;
								break;

							case JsonConstants.ARRAY_START_CHAR:
								value = new ListNode(value);
								state = State.ARRAY_ELEMENT_START;
								break;

							case JsonConstants.OBJECT_START_CHAR:
								value = new MapNode(value);
								state = State.PROPERTY_START;
								break;

							case JsonConstants.ARRAY_END_CHAR:
							case JsonConstants.OBJECT_END_CHAR:
							case JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR:
							case JsonConstants.OBJECT_PROPERTY_SEPARATOR_CHAR:
								throw new ParseException(ErrorMsg.VALUE_EXPECTED, lineIndex,
														 tokenIndex - lineStartIndex);

							default:
								// Push back start of number or literal
								pushBackChar();

								// Set next state
								state = ((ch == '-') || ((ch >= '0') && (ch <= '9'))) ? State.NUMBER_VALUE
																					  : State.LITERAL_VALUE;
								break;
						}
					}
					break;
				}

				//----  End of JSON value
				case VALUE_END:
				{
					// Get parent of current value
					AbstractNode parent = value.getParent();

					// If current value has no parent (ie, it is the root value), move to next value (which should not
					// exist) ...
					if (parent == null)
						state = State.VALUE_START;

					// ... otherwise, if parent is array or object, add value to it
					else if (parent.isContainer())
					{
						// Case: parent is array
						if (parent instanceof ListNode)
						{
							// Add element to its parent array
							((ListNode)parent).add(value);

							// Set next state
							state = State.ARRAY_ELEMENT_END;
						}

						// Case: parent is object
						else if (parent instanceof MapNode)
						{
							// Cast parent to map node
							MapNode object = (MapNode)parent;

							// Get name of current property from stack
							PropertyName propertyName = propertyNameStack.removeFirst();

							// Test for duplicate property name
							if (object.hasKey(propertyName.name))
								throw new ParseException(ErrorMsg.DUPLICATE_PROPERTY_NAME, propertyName.lineIndex,
														 propertyName.index - lineStartIndex, propertyName.name);

							// Add property to its parent object value
							object.add(propertyName.name, value);

							// Set next state
							state = State.PROPERTY_END;
						}

						// Push back element/property separator or array/object terminator
						pushBackChar();

						// Set current value to previous parent
						value = parent;
					}

					// ... otherwise, throw exception
					else
						throw new RuntimeException("Unexpected error: invalid parent");
					break;
				}

				//----  JSON literal value (null or Boolean)
				case LITERAL_VALUE:
				{
					// If end of current token, set new null value or Boolean value according to token ...
					if (isValueTerminator(ch))
					{
						switch (tokenBuffer.toString())
						{
							case NullNode.VALUE:
								value = new NullNode(value);
								break;

							case BooleanNode.VALUE_FALSE:
								value = new BooleanNode(value, false);
								break;

							case BooleanNode.VALUE_TRUE:
								value = new BooleanNode(value, true);
								break;

							default:
								throw new ParseException(ErrorMsg.ILLEGAL_VALUE, lineIndex,
														 tokenIndex - lineStartIndex);
						}

						// Push back value terminator
						pushBackChar();

						// Set next state
						state = State.VALUE_END;
					}

					// ... otherwise, append character to current token
					else
						tokenBuffer.append(ch);
					break;
				}

				//----  JSON number value
				case NUMBER_VALUE:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					try
					{
						// Push back start of number
						pushBackChar();

						// Validate number; put valid number in token buffer
						validateNumber();

						// Parse number by creating new instance of BigDecimal from token
						String numberStr = tokenBuffer.toString();
						BigDecimal number = new BigDecimal(numberStr);

						// If number is integer, create JSON number of smallest type ...
						if (numberStr.indexOf('.') < 0)
						{
							try
							{
								value = new IntNode(value, number.intValueExact());
							}
							catch (ArithmeticException e)
							{
								try
								{
									value = new LongNode(value, number.longValueExact());
								}
								catch (ArithmeticException e0)
								{
									if (!storeExcessiveIntegerAsFP)
										throw new ParseException(ErrorMsg.TOO_LARGE_FOR_INTEGER, lineIndex,
																 tokenIndex - lineStartIndex);
									value = new DoubleNode(value, number.doubleValue());
								}
							}
						}

						// ... otherwise, create JSON number for double-precision FP
						else
							value = new DoubleNode(value, number.doubleValue());

						// Push back terminator
						pushBackChar();

						// Set next state
						state = State.VALUE_END;
					}
					catch (NumberFormatException e)
					{
						String causeMessage = e.getMessage();
						throw new ParseException(((causeMessage == null) || causeMessage.isEmpty())
															? ErrorMsg.INVALID_NUMBER + "."
															: ErrorMsg.INVALID_NUMBER + ": " + causeMessage,
												 lineIndex, tokenIndex - lineStartIndex);
					}
					break;
				}

				//----  JSON string value
				case STRING_VALUE:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					// Parse string; put valid string in token buffer; if string is valid, create JSON string value
					if (parseString(ch))
					{
						// Create JSON string value from token
						value = new StringNode(value, tokenBuffer.toString());

						// Set next state
						state = State.VALUE_END;
					}
					break;
				}

				//----  Start of property of JSON object
				case PROPERTY_START:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					// Case: character is whitespace
					if (isWhitespace(ch))
					{
						// If character is LF, start new line
						newLine(ch);
					}

					// Case: character is not whitespace
					else
					{
						// If end-of-object character, empty object has ended ...
						if (ch == JsonConstants.OBJECT_END_CHAR)
							state = State.VALUE_END;

						// ... otherwise, expect another property
						else
						{
							// Push back start of property
							pushBackChar();

							// Set next state
							state = State.PROPERTY_NAME_START;
						}
					}
					break;
				}

				//----  Start of name of property of JSON object
				case PROPERTY_NAME_START:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					// Case: character is whitespace
					if (isWhitespace(ch))
					{
						// If character is LF, start new line
						newLine(ch);
					}

					// Case: character is not whitespace
					else
					{
						// Update index of current token
						tokenIndex = index - 1;

						// Test for start of property name
						if (ch != StringNode.START_CHAR)
							throw new ParseException(ErrorMsg.PROPERTY_NAME_EXPECTED, lineIndex,
													 tokenIndex - lineStartIndex);

						// Update property variables
						propertyIndex = tokenIndex;
						propertyLineIndex = lineIndex;

						// Clear token buffer
						tokenBuffer.setLength(0);

						// Set next state
						state = State.PROPERTY_NAME;
					}
					break;
				}

				//----  Name of property of JSON object
				case PROPERTY_NAME:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					// Parse property name; put valid property name in token buffer; if property name is valid, put it
					// on stack
					if (parseString(ch))
					{
						// Create property name and put it on stack
						propertyNameStack.addFirst(new PropertyName(tokenBuffer.toString(), propertyIndex,
																	propertyLineIndex));

						// Set next state
						state = State.PROPERTY_NAME_END;
					}
					break;
				}

				//----  End of name of property of JSON object
				case PROPERTY_NAME_END:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					// Case: character is whitespace
					if (isWhitespace(ch))
					{
						// If character is LF, start new line
						newLine(ch);
					}

					// Case: character is not whitespace
					else
					{
						// Test for property-name separator
						if (ch != JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR)
							throw new ParseException(ErrorMsg.NAME_SEPARATOR_EXPECTED, lineIndex,
													 tokenIndex - lineStartIndex);

						// Set next state
						state = State.VALUE_START;
					}

					break;
				}

				//----  End of property of JSON object
				case PROPERTY_END:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					// Case: character is whitespace
					if (isWhitespace(ch))
					{
						// If character is LF, start new line
						newLine(ch);
					}

					// Case: character is not whitespace
					else
					{
						// Update index of current token
						tokenIndex = index - 1;

						// Set next state according to current character
						switch (ch)
						{
							case JsonConstants.OBJECT_PROPERTY_SEPARATOR_CHAR:
								state = State.PROPERTY_NAME_START;
								break;

							case JsonConstants.OBJECT_END_CHAR:
								state = State.VALUE_END;
								break;

							default:
								throw new ParseException(ErrorMsg.END_OF_OBJECT_EXPECTED, lineIndex,
														 tokenIndex - lineStartIndex);
						}
					}
					break;
				}

				//----  Start of element of JSON array
				case ARRAY_ELEMENT_START:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					// Case: character is whitespace
					if (isWhitespace(ch))
					{
						// If character is LF, start new line
						newLine(ch);
					}

					// Case: character is not whitespace
					else
					{
						// If end-of-array character, array has ended ...
						if (ch == JsonConstants.ARRAY_END_CHAR)
						{
							// Test for empty array
							if (!((ListNode)value).isEmpty())
								throw new ParseException(ErrorMsg.ARRAY_ELEMENT_EXPECTED, lineIndex,
														 index - 1 - lineStartIndex);

							// Set next state
							state = State.VALUE_END;
						}

						// ... otherwise, expect another element
						else
						{
							// Push back start of element
							pushBackChar();

							// Set next state
							state = State.VALUE_START;
						}
					}
					break;
				}

				//----  End of element of JSON array
				case ARRAY_ELEMENT_END:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					// Case: character is whitespace
					if (isWhitespace(ch))
					{
						// If character is LF, start new line
						newLine(ch);
					}

					// Case: character is not whitespace
					else
					{
						// Update index of current token
						tokenIndex = index - 1;

						// Set next state according to current character
						switch (ch)
						{
							case JsonConstants.ARRAY_ELEMENT_SEPARATOR_CHAR:
								state = State.ARRAY_ELEMENT_START;
								break;

							case JsonConstants.ARRAY_END_CHAR:
								state = State.VALUE_END;
								break;

							default:
								throw new ParseException(ErrorMsg.END_OF_ARRAY_EXPECTED, lineIndex,
														 tokenIndex - lineStartIndex);
						}
					}
					break;
				}

				//----  Parsing completed successfully
				case DONE:
					// do nothing
					break;
			}
		}

		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Reads and returns the next character from the input stream.  If a character has been {@linkplain #pushBackChar()
	 * pushed back}, the last character that was read from input stream is returned.
	 *
	 * @return the next character from the input stream, or a space character (U+0020) if the end of the input stream
	 *         has been reached.
	 * @throws ParseException
	 *           if an error occurred when reading from the input stream.
	 */

	private char getNextChar()
		throws ParseException
	{
		// Case: a character is pushed back
		if (pushedBack)
		{
			// Clear 'pushed back' flag
			pushedBack = false;

			// Increment input index
			++index;
		}

		// Case: no character is pushed back
		else
		{
			try
			{
				// Read next character from input stream
				int ch = inputStream.read();

				// Set flag to indicate end of input stream
				endOfInput = (ch < 0);

				// If not end of input stream, increment input index
				if (!endOfInput)
					++index;

				// Update last character that was read from input stream
				inputChar = endOfInput ? ' ' : (char)ch;
			}
			catch (IOException e)
			{
				throw new ParseException(ErrorMsg.ERROR_READING_FROM_STREAM, e, lineIndex, index - lineStartIndex);
			}
		}

		// Return last character that was read from input stream
		return inputChar;
	}

	//------------------------------------------------------------------

	/**
	 * Pushes the last character that was read from input stream back to the stream.  This method may be called only
	 * once after each call to {@link #getNextChar()}.
	 *
	 * @throws IllegalStateException
	 *           if a character is already pushed back or {@link #getNextChar()} has not yet been called on the input
	 *           stream.
	 */

	private void pushBackChar()
	{
		// Test whether a character is already pushed back
		if (pushedBack)
			throw new IllegalStateException("Character already pushed back");

		// Test for start of input stream
		if (index == 0)
			throw new IllegalStateException("Start of input stream");

		// Set 'pushed back' flag
		pushedBack = true;

		// Decrement input index
		--index;
	}

	//------------------------------------------------------------------

	/**
	 * Increments the index of the current line in the input stream and resets the index of the start of the current
	 * line in the input stream if the specified character (from the input stream) is a line feed (U+000A).
	 *
	 * @param ch
	 *          the next character from the input stream.
	 */

	private void newLine(
		char	ch)
	{
		if (ch == '\n')
		{
			++lineIndex;
			lineStartIndex = index;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Throws a {@link ParseException} when the validation of a JSON number with {@link #validateNumber(CharSequence)}
	 * fails at the specified character.  The detail message of the exception contains a reference to the index of the
	 * character at which validation failed.
	 *
	 * @param  ch
	 *           the character that caused the validation of a number to fail.
	 * @throws ParseException
	 */

	private void throwNumberException(
		char	ch)
		throws ParseException
	{
		// Get string representation of index of character
		String indexStr = Integer.toString(index - 1 - tokenIndex);

		// Initialise secondary message
		String message = null;

		// If character is terminator, secondary message is 'ended prematurely' ...
		if (isValueTerminator(ch))
			message = String.format(ENDED_PREMATURELY_STR, indexStr);

		// ... otherwise, secondary message is 'character is not allowed'
		else
		{
			String charStr = ((ch < '\u0020') || (ch > '\u007E')) ? UNICODE_PREFIX + StringNode.charToUnicodeHex(ch)
																  : "'" + Character.toString(ch) + "'";
			message = String.format(CHARACTER_NOT_ALLOWED_STR, charStr, indexStr);
		}

		// Throw exception
		throw new ParseException(ErrorMsg.INVALID_NUMBER + ": " + message, lineIndex, tokenIndex - lineStartIndex);
	}

	//------------------------------------------------------------------

	/**
	 * Validates a JSON number at the {@linkplain #index current index} within the specified text.  This method only
	 * checks that a number conforms to the JSON grammar; the number is subsequently parsed by calling {@link
	 * BigDecimal#BigDecimal(String)}.
	 * <p>
	 * The use of a finite-state machine to validate a JSON number is preferred to a regular expression because it is
	 * faster.
	 * </p>
	 *
	 * @throws ParseException
	 *           if the text at the current index is not a valid JSON representation of a number.
	 */

	private void validateNumber()
		throws ParseException
	{
		NumberState state = NumberState.INTEGER_PART_SIGN;
		while (state != NumberState.DONE)
		{
			// Get next character from input stream
			char ch = getNextChar();

			// Execute finite-state machine
			switch (state)
			{
				//----  Sign of integer part
				case INTEGER_PART_SIGN:
				{
					// If minus sign, append it to current token ...
					if (ch == '-')
						tokenBuffer.append(ch);

					// ... otherwise, push back first character of number
					else
						pushBackChar();

					// Set next state
					state = NumberState.INTEGER_PART_FIRST_DIGIT;
					break;
				}

				//----  First digit of integer part
				case INTEGER_PART_FIRST_DIGIT:
				{
					// Test for decimal digit
					if ((ch < '0') || (ch > '9'))
						throwNumberException(ch);

					// Append digit to current token
					tokenBuffer.append(ch);

					// Set next state
					state = NumberState.INTEGER_PART_DIGITS;
					break;
				}

				//----  Remaining digits of integer part
				case INTEGER_PART_DIGITS:
				{
					// If end of number, stop validation ...
					if (isValueTerminator(ch))
						state = NumberState.DONE;

					// ... otherwise, if decimal point, append it to token ...
					else if (ch == '.')
					{
						// Append decimal point to current token
						tokenBuffer.append(ch);

						// Set next state
						state = NumberState.FRACTION_PART_FIRST_DIGIT;
					}

					// ... otherwise, if exponent prefix, append it to token ...
					else if ((ch == 'E') || (ch == 'e'))
					{
						// Append exponent prefix to current token
						tokenBuffer.append(ch);

						// Set next state
						state = NumberState.EXPONENT_SIGN;
					}

					// ... otherwise, if decimal digit, process it ...
					else if ((ch >= '0') && (ch <= '9'))
					{
						switch (tokenBuffer.length())
						{
							// Positive number may not have leading zero
							case 1:
								if (tokenBuffer.charAt(0) == '0')
								{
									pushBackChar();
									throwNumberException('0');
								}
								break;

							// Negative number may not have leading zero
							case 2:
								if ((tokenBuffer.charAt(0) == '-') && (tokenBuffer.charAt(1) == '0'))
								{
									pushBackChar();
									throwNumberException('0');
								}
								break;

							default:
								// do nothing
								break;
						}

						// Append digit to current token
						tokenBuffer.append(ch);
					}

					// ... otherwise, throw an exception
					else
						throwNumberException(ch);

					break;
				}

				//----  First digit of fraction part
				case FRACTION_PART_FIRST_DIGIT:
				{
					// Test for decimal digit
					if ((ch < '0') || (ch > '9'))
						throwNumberException(ch);

					// Append digit to current token
					tokenBuffer.append(ch);

					// Set next state
					state = NumberState.FRACTION_PART_DIGITS;
					break;
				}

				//----  Remaining digits of fraction part
				case FRACTION_PART_DIGITS:
				{
					// If end of number, stop validation ...
					if (isValueTerminator(ch))
						state = NumberState.DONE;

					// ... otherwise, if exponent prefix, append it to token ...
					else if ((ch == 'E') || (ch == 'e'))
					{
						// Append exponent prefix to current token
						tokenBuffer.append(ch);

						// Set next state
						state = NumberState.EXPONENT_SIGN;
					}

					// ... otherwise, if decimal digit, append it to current token ...
					else if ((ch >= '0') && (ch <= '9'))
						tokenBuffer.append(ch);

					// ... otherwise, throw an exception
					else
						throwNumberException(ch);
					break;
				}

				//----  Sign of exponent
				case EXPONENT_SIGN:
				{
					// If sign of exponent, append it to token ...
					if ((ch == '-') || (ch == '+'))
						tokenBuffer.append(ch);

					// ... otherwise, push back first digit of exponent
					else
						pushBackChar();

					// Set next state
					state = NumberState.EXPONENT_FIRST_DIGIT;
					break;
				}

				//----  First digit of exponent
				case EXPONENT_FIRST_DIGIT:
				{
					// Test for decimal digit
					if ((ch < '0') || (ch > '9'))
						throwNumberException(ch);

					// Append digit to current token
					tokenBuffer.append(ch);

					// Set next state
					state = NumberState.EXPONENT_DIGITS;
					break;
				}

				//----  Remaining digits of exponent
				case EXPONENT_DIGITS:
				{
					// If end of number, stop validation ...
					if (isValueTerminator(ch))
						state = NumberState.DONE;

					// ... otherwise, if decimal digit, append it to current token ...
					else if ((ch >= '0') && (ch <= '9'))
						tokenBuffer.append(ch);

					// ... otherwise, throw an exception
					else
						throwNumberException(ch);
					break;
				}

				//----  Validation completed successfully
				case DONE:
					// do nothing
					break;
			}
		}
	}

	//------------------------------------------------------------------

	/**
	 * Parses a JSON string at the {@linkplain #index current index} within the specified text, and sets the resulting
	 * string in the {@linkplain #tokenBuffer token buffer}.  This method is not called on the quotation mark (U+0022)
	 * at the start of the string.
	 *
	 * @param  ch
	 *           the current character of the JSON string representation.
	 * @return {@code true} if the string has been parsed successfully, {@code false} if the end of the string has not
	 *         been reached.
	 * @throws ParseException
	 *           if an error occurred when parsing the JSON string.
	 */

	private boolean parseString(
		char	ch)
		throws ParseException
	{
		// Test for end of string
		if (ch == StringNode.END_CHAR)
			return true;

		// Test for control character
		if (ch < '\u0020')
			throw new ParseException(ErrorMsg.ILLEGAL_CHARACTER_IN_STRING, lineIndex, tokenIndex - lineStartIndex,
									 UNICODE_PREFIX + StringNode.charToUnicodeHex(ch));

		// If character is escape prefix, parse escape sequence
		if (ch == StringNode.ESCAPE_PREFIX_CHAR)
		{
			// Get first character of escape sequence after prefix
			ch = getNextChar();

			// Test whether input stream ended before first character of escape sequence after prefix
			if (endOfInput)
				throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

			// Initialise index of start of escape sequence without prefix
			int startIndex = index;

			// Case: Unicode escape sequence
			if (ch == StringNode.UNICODE_ESCAPE_CHAR)
			{
				// Read Unicode escape sequence from input stream
				char[] unicodeSeqChars = new char[UNICODE_SEQUENCE_LENGTH];
				for (int i = 0; i < UNICODE_SEQUENCE_LENGTH; i++)
				{
					// Get next character of Unicode escape sequence from input stream
					unicodeSeqChars[i] = getNextChar();

					// Test whether input stream ended before end of Unicode escape sequence
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);
				}
				String unicodeSeq = new String(unicodeSeqChars);

				// Parse Unicode escape sequence
				try
				{
					ch = (char)Integer.parseUnsignedInt(unicodeSeq, 16);
				}
				catch (NumberFormatException e)
				{
					--startIndex;
					throw new ParseException(ErrorMsg.ILLEGAL_UNICODE_ESCAPE_SEQUENCE, lineIndex,
											 startIndex - lineStartIndex, StringNode.ESCAPE_PREFIX + ch + unicodeSeq);
				}
			}

			// Case: escape sequence other than Unicode
			else
			{
				boolean found = false;
				for (int i = 0; i < ESCAPE_MAPPINGS.length; i++)
				{
					char[] pair = ESCAPE_MAPPINGS[i];
					if (ch == pair[0])
					{
						ch = pair[1];
						found = true;
						break;
					}
				}
				if (!found)
				{
					--startIndex;
					throw new ParseException(ErrorMsg.ILLEGAL_ESCAPE_SEQUENCE, lineIndex, startIndex - lineStartIndex,
											 StringNode.ESCAPE_PREFIX + ch);
				}
			}
		}

		// Append character to buffer
		tokenBuffer.append(ch);

		// Indicate that the end of the string has not been reached
		return false;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: PARSE EXCEPTION


	/**
	 * This class implements an exception that is thrown if an error occurs when parsing JSON text.
	 */

	public static class ParseException
		extends Exception
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The zero-based index of the line of the input text at which the exception occurred. */
		private	int	lineIndex;

		/** The zero-based index of the column of the input text at which the exception occurred. */
		private	int	columnIndex;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an exception with the specified message, line index, column index and optional
		 * replacements for placeholders in the message.
		 *
		 * @param message
		 *          the message of the exception.
		 * @param lineIndex
		 *          the zero-based index of the line at which the exception occurred.
		 * @param columnIndex
		 *          the zero-based index of the column at which the exception occurred.
		 * @param replacements
		 *          the objects whose string representations will replace placeholders in {@code message}.
		 */

		private ParseException(
			String		message,
			int			lineIndex,
			int			columnIndex,
			Object...	replacements)
		{
			// Call alternative constructor
			this(message, null, lineIndex, columnIndex, replacements);
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new instance of an exception with the specified message, cause, line index, column index and
		 * optional replacements for placeholders in the message.
		 *
		 * @param message
		 *          the message of the exception.
		 * @param cause
		 *          the underlying cause of the exception, which may be {@code null}.
		 * @param lineIndex
		 *          the zero-based index of the line at which the exception occurred.
		 * @param columnIndex
		 *          the zero-based index of the column at which the exception occurred.
		 * @param replacements
		 *          the objects whose string representations will replace placeholders in {@code message}.
		 */

		private ParseException(
			String		message,
			Throwable	cause,
			int			lineIndex,
			int			columnIndex,
			Object...	replacements)
		{
			// Call superclass constructor
			super("(" + (lineIndex + 1) + ", " + (columnIndex + 1) + "): " + String.format(message, replacements),
				  cause);

			// Initialise instance variables
			this.lineIndex = lineIndex;
			this.columnIndex = columnIndex;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the zero-based index of the line of the input text at which the exception occurred.
		 *
		 * @return the zero-based index of the line of the input text at which the exception occurred.
		 * @see    #getColumnIndex()
		 */

		public int getLineIndex()
		{
			return lineIndex;
		}

		//--------------------------------------------------------------

		/**
		 * Returns the zero-based index of the column of the input text at which the exception occurred.
		 *
		 * @return the zero-based index of the column of the input text at which the exception occurred.
		 * @see    #getLineIndex()
		 */

		public int getColumnIndex()
		{
			return columnIndex;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: JSON OBJECT PROPERTY NAME


	/**
	 * This class encapsulates the name of a property of a JSON object and its location in the input text.
	 */

	private static class PropertyName
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The name of a property of a JSON object. */
		private	String	name;

		/** The index of the property name in the input text. */
		private	int		index;

		/** The index of the line containing the property name in the input text. */
		private	int		lineIndex;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a property-name object.
		 *
		 * @param name
		 *          the name of a property of a JSON object.
		 * @param index
		 *          the index of the property name in the input text.
		 * @param lineIndex
		 *          the index of the line containing the property name in the input text.
		 */

		private PropertyName(
			String	name,
			int		index,
			int		lineIndex)
		{
			// Initialise instance variables
			this.name = name;
			this.index = index;
			this.lineIndex = lineIndex;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
