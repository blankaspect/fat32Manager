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

import org.w3c.dom.Element;

import uk.blankaspect.common.basictree.AbstractNode;
import uk.blankaspect.common.basictree.BooleanNode;
import uk.blankaspect.common.basictree.DoubleNode;
import uk.blankaspect.common.basictree.IntNode;
import uk.blankaspect.common.basictree.ListNode;
import uk.blankaspect.common.basictree.LongNode;
import uk.blankaspect.common.basictree.MapNode;
import uk.blankaspect.common.basictree.NullNode;
import uk.blankaspect.common.basictree.StringNode;

import uk.blankaspect.common.jsonxml.ElementKind;
import uk.blankaspect.common.jsonxml.IElementFacade;
import uk.blankaspect.common.jsonxml.JsonXmlUtils;

//----------------------------------------------------------------------


// CLASS: JSON PARSER


/**
 * <p style="margin-bottom: 0.25em;">
 * This class implements a parser that transforms JSON text into either a tree of nodes or a tree of XML elements:
 * </p>
 * <ul>
 *   <li>
 *     The {@code parse(\u2026)} methods transform JSON text into a tree of {@linkplain AbstractNode nodes}.  Each node
 *     corresponds to a JSON value.
 *   </li>
 *   <li>
 *     The {@code parseToXml(\u2026)} methods transform JSON text into a tree of {@linkplain Element XML elements}.
 *     Each XML element corresponds to a JSON value.
 *   </li>
 * </ul>
 * <p>
 * The input text of the parser is expected to conform to the JSON grammar as specified in <a
 * href="https://www.rfc-editor.org/rfc/rfc8259.html">IETF RFC 8259</a>.
 * </p>
 * <p>
 * The parser is implemented as a <a href="https://en.wikipedia.org/wiki/Finite-state_machine">finite-state machine</a>
 * (FSM) that terminates with an exception at the first error in the input text.  The FSM combines the lexical analysis
 * and parsing of the input text with the generation of the output (a tree of {@linkplain AbstractNode nodes} or
 * {@linkplain Element XML elements} that correspond to JSON values).
 * </p>
 */

public class JsonParser
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

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
		JsonConstants.OBJECT_MEMBER_SEPARATOR_CHAR
	};

	/** Value terminators: the union of whitespace characters and structural characters. */
	private static final	String	VALUE_TERMINATORS	= WHITESPACE + new String(STRUCTURAL_CHARS);

	/** The prefix of a four-hex-digit Unicode representation of a character. */
	private static final	String	UNICODE_PREFIX	= "U+";

	/** Miscellaneous strings. */
	private static final	String	CHARACTER_NOT_ALLOWED_STR	= "the character %s at index %d is not allowed.";
	private static final	String	ENDED_PREMATURELY_STR		= "it ended prematurely at index %d.";
	private static final	String	UNSUPPORTED_OPERATION_STR	= "Unsupported operation";
	private static final	String	NULL_INPUT_STREAM_STR		= "Null input stream";
	private static final	String	NULL_READER_STR				= "Null reader";
	private static final	String	NULL_TEXT_STR				= "Null text";
	private static final	String	NO_XML_ELEMENT_FACADE_STR	= "No XML element facade";
	private static final	String	INVALID_PARENT_STR			= "Unexpected error: invalid parent";

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
		ARRAY_ELEMENT_START,
		ARRAY_ELEMENT_END,
		OBJECT_MEMBER_START,
		OBJECT_MEMBER_NAME_START,
		OBJECT_MEMBER_NAME,
		OBJECT_MEMBER_NAME_END,
		OBJECT_MEMBER_END,
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
		String	ERROR_READING_FROM_STREAM =
				"An error occured when reading from the input stream.";

		String	PREMATURE_END_OF_TEXT =
				"The input text ended prematurely.";

		String	EXTRANEOUS_TEXT =
				"There is extraneous text after the JSON value.";

		String	VALUE_EXPECTED =
				"A value was expected.";

		String	OBJECT_MEMBER_NAME_EXPECTED =
				"The name of an object member was expected.";

		String	NAME_SEPARATOR_EXPECTED =
				"A name separator was expected.";

		String	END_OF_OBJECT_EXPECTED =
				"An end-of-object character was expected.";

		String	ARRAY_ELEMENT_EXPECTED =
				"An array element was expected.";

		String	END_OF_ARRAY_EXPECTED =
				"An end-of-array character was expected.";

		String	ILLEGAL_CHARACTER_IN_STRING =
				"The character '%s' is not allowed in a string.";

		String	ILLEGAL_VALUE =
				"The value is illegal.";

		String	ILLEGAL_ESCAPE_SEQUENCE =
				"The escape sequence '%s' is illegal.";

		String	ILLEGAL_UNICODE_ESCAPE_SEQUENCE =
				"The Unicode escape sequence '%s' is illegal.";

		String	DUPLICATE_OBJECT_MEMBER_NAME =
				"The object has more than one member with the name '%s'.";

		String	INVALID_NUMBER =
				"The number is not valid";

		String	NOT_A_VALID_NUMBER =
				"'%s' is not a valid number";

		String	TOO_LARGE_FOR_INTEGER =
				"The number is too large for an integer.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The reader of the character stream that is the source of the JSON text. */
	private	Reader			inputReader;

	/** The last character that was read from {@link #inputReader}. */
	private	char			inputChar;

	/** Flag: if {@code true}, the end of the input has been reached. */
	private	boolean			endOfInput;

	/** Flag: if {@code true}, the last character that was read from the input has been pushed back. */
	private	boolean			pushedBack;

	/** The index of the next character in the input text. */
	private	int				index;

	/** The index of the current line in the input text. */
	private	int				lineIndex;

	/** The index of the start of the current line in the input text. */
	private	int				lineStartIndex;

	/** The index of the current token in the input text. */
	private	int				tokenIndex;

	/** A buffer for the current token. */
	private	StringBuilder	tokenBuffer;

	/** A buffer for the characters of a Unicode escape sequence. */
	private	char[]			unicodeSeqChars;

	/** The interface through which XML elements are created and their attributes accessed. */
	private	IElementFacade	xmlElementFacade;

	/** Flag: if {@code true}, a JSON number that is deemed to be an integer but is too large to be stored as a {@code
		long} will be stored as a {@code double}. */
	private	boolean			storeExcessiveIntegerAsFP;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a JSON parser that is initialised from the state of the specified builder.
	 *
	 * @param builder
	 *          the builder from whose state the parser will be initialised.
	 */

	private JsonParser(
		Builder	builder)
	{
		// Initialise instance variables
		tokenBuffer = new StringBuilder();
		unicodeSeqChars = new char[StringNode.UNICODE_SEQUENCE_LENGTH];
		xmlElementFacade = builder.elementFacade;
		storeExcessiveIntegerAsFP = builder.storeExcessiveIntegerAsFP;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a builder for a JSON parser.
	 *
	 * @return a new instance of a builder for a JSON parser.
	 */

	public static Builder builder()
	{
		return new Builder();
	}

	//------------------------------------------------------------------

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

	/**
	 * Creates and returns a new instance of a {@linkplain InputStreamReader reader} for the specified byte stream and
	 * character encoding.
	 *
	 * @param  inputStream
	 *           the byte stream for which a reader is desired.
	 * @param  encoding
	 *           the character encoding of {@code inputStream}; if {@code null}, the UTF-8 encoding will be used.
	 * @return a new instance of a {@linkplain InputStreamReader reader} for {@code inputStream} and {@code encoding}.
	 */

	private static InputStreamReader reader(
		InputStream	inputStream,
		Charset		encoding)
	{
		return new InputStreamReader(inputStream, (encoding == null) ? StandardCharsets.UTF_8 : encoding);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Parses the specified text.  If the text conforms to the JSON grammar, the JSON text is transformed into a tree of
	 * {@linkplain AbstractNode nodes} that correspond to JSON values, and the root of the tree is returned.
	 *
	 * @param  text
	 *           the text that will be parsed as JSON text.
	 * @return the root of the tree of {@linkplain AbstractNode nodes} that was created from parsing the JSON text that
	 *         was read from {@code text}.
	 * @throws IllegalArgumentException
	 *           if {@code text} is {@code null}.
	 * @throws ParseException
	 *           if an error occurs when parsing the input text.
	 */

	public AbstractNode parse(
		CharSequence	text)
		throws ParseException
	{
		// Validate argument
		if (text == null)
			throw new IllegalArgumentException(NULL_TEXT_STR);

		// Parse input text and return result
		return parse(reader(text));
	}

	//------------------------------------------------------------------

	/**
	 * Parses the text that is composed of characters that are read from the specified byte stream using the UTF-8
	 * character encoding.  If the text conforms to the JSON grammar, the JSON text is transformed into a tree of
	 * {@linkplain AbstractNode nodes} that correspond to JSON values, and the root of the tree is returned.
	 *
	 * @param  inputStream
	 *           the byte stream from which the JSON text will be read.
	 * @return the root of the tree of {@linkplain AbstractNode nodes} that was created from parsing the JSON text that
	 *         was read from {@code inputStream}.
	 * @throws IllegalArgumentException
	 *           if {@code inputStream} is {@code null}.
	 * @throws ParseException
	 *           if an error occurs when parsing the input text.
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
	 * character encoding.  If the text conforms to the JSON grammar, the JSON text is transformed into a tree of
	 * {@linkplain AbstractNode nodes} that correspond to JSON values, and the root of the tree is returned.
	 *
	 * @param  inputStream
	 *           the byte stream from which the JSON text will be read.
	 * @param  encoding
	 *           the character encoding of {@code inputStream}; if {@code null}, the UTF-8 encoding will be used.
	 * @return the root of the tree of {@linkplain AbstractNode nodes} that was created from parsing the JSON text that
	 *         was read from {@code inputStream}.
	 * @throws IllegalArgumentException
	 *           if {@code inputStream} is {@code null}.
	 * @throws ParseException
	 *           if an error occurs when parsing the input text.
	 */

	public AbstractNode parse(
		InputStream	inputStream,
		Charset		encoding)
		throws ParseException
	{
		// Validate arguments
		if (inputStream == null)
			throw new IllegalArgumentException(NULL_INPUT_STREAM_STR);

		// Create reader for input stream; read input, parse it and return result
		return parse(reader(inputStream, encoding));
	}

	//------------------------------------------------------------------

	/**
	 * Parses the text that is read from a character stream by the specified reader.  If the text conforms to the JSON
	 * grammar, the JSON text is transformed into a tree of {@linkplain AbstractNode nodes} that correspond to JSON
	 * values, and the root of the tree is returned.
	 *
	 * @param  reader
	 *           the reader of the character stream that is the source of the JSON text.
	 * @return the root of the tree of {@linkplain AbstractNode nodes} that was created from parsing the JSON text that
	 *         was read from {@code inputStream}.
	 * @throws IllegalArgumentException
	 *           if {@code reader} is {@code null}.
	 * @throws ParseException
	 *           if an error occurs when parsing the input text.
	 */

	public AbstractNode parse(
		Reader	reader)
		throws ParseException
	{
		// Validate argument
		if (reader == null)
			throw new IllegalArgumentException(NULL_READER_STR);

		// Initialise instance variables
		inputReader = reader;

		// Parse input text and return result
		return parse(false).node;
	}

	//------------------------------------------------------------------

	/**
	 * Parses the specified text.  If the text conforms to the JSON grammar, the JSON text is transformed into a tree of
	 * {@linkplain Element XML elements} that correspond to JSON values, and the root of the tree is returned.
	 *
	 * @param  text
	 *           the text that will be parsed as JSON text.
	 * @return the root of the tree of XML elements that was created from parsing the JSON text that was read from
	 *         {@code text}.
	 * @throws IllegalArgumentException
	 *           if {@code text} is {@code null}.
	 * @throws IllegalStateException
	 *           if no {@linkplain IElementFacade XML element facade} has been set on this parser.
	 * @throws ParseException
	 *           if an error occurs when parsing the input text.
	 */

	public Element parseToXml(
		CharSequence	text)
		throws ParseException
	{
		// Validate argument
		if (text == null)
			throw new IllegalArgumentException(NULL_TEXT_STR);

		// Parse input text and return result
		return parseToXml(reader(text));
	}

	//------------------------------------------------------------------

	/**
	 * Parses the text that is composed of characters that are read from the specified byte stream using the UTF-8
	 * character encoding.  If the text conforms to the JSON grammar, the JSON text is transformed into a tree of
	 * {@linkplain Element XML elements} that correspond to JSON values, and the root of the tree is returned.
	 *
	 * @param  inputStream
	 *           the byte stream from which the JSON text will be read.
	 * @return the root of the tree of XML elements that was created from parsing the JSON text that was read from
	 *         {@code inputStream}.
	 * @throws IllegalArgumentException
	 *           if {@code inputStream} is {@code null}.
	 * @throws IllegalStateException
	 *           if no {@linkplain IElementFacade XML element facade} has been set on this parser.
	 * @throws ParseException
	 *           if an error occurs when parsing the input text.
	 */

	public Element parseToXml(
		InputStream		inputStream)
		throws ParseException
	{
		return parseToXml(inputStream, StandardCharsets.UTF_8);
	}

	//------------------------------------------------------------------

	/**
	 * Parses the text that is composed of characters that are read from the specified byte stream using the specified
	 * character encoding.  If the text conforms to the JSON grammar, the JSON text is transformed into a tree of
	 * {@linkplain Element XML elements} that correspond to JSON values, and the root of the tree is returned.
	 *
	 * @param  inputStream
	 *           the byte stream from which the JSON text will be read.
	 * @param  encoding
	 *           the character encoding of {@code inputStream}; if {@code null}, the UTF-8 encoding will be used.
	 * @return the root of the tree of XML elements that was created from parsing the JSON text that was read from
	 *         {@code inputStream}.
	 * @throws IllegalArgumentException
	 *           if {@code inputStream} is {@code null}.
	 * @throws IllegalStateException
	 *           if no {@linkplain IElementFacade XML element facade} has been set on this parser.
	 * @throws ParseException
	 *           if an error occurs when parsing the input text.
	 */

	public Element parseToXml(
		InputStream		inputStream,
		Charset			encoding)
		throws ParseException
	{
		// Validate arguments
		if (inputStream == null)
			throw new IllegalArgumentException(NULL_INPUT_STREAM_STR);

		// Create reader for input stream; read input, parse it and return result
		return parseToXml(reader(inputStream, encoding));
	}

	//------------------------------------------------------------------

	/**
	 * Parses the text that is read from a character stream by the specified reader.  If the text conforms to the JSON
	 * grammar, the JSON text is transformed into a tree of {@linkplain Element XML elements} that correspond to JSON
	 * values, and the root of the tree is returned.
	 *
	 * @param  reader
	 *           the reader of the character stream that is the source of the JSON text.
	 * @return the root of the tree of XML elements that was created from parsing the JSON text that was read from
	 *         {@code inputStream}.
	 * @throws IllegalArgumentException
	 *           if {@code reader} is {@code null}.
	 * @throws IllegalStateException
	 *           if no {@linkplain IElementFacade XML element facade} has been set on this parser.
	 * @throws ParseException
	 *           if an error occurs when parsing the input text.
	 */

	public Element parseToXml(
		Reader	reader)
		throws ParseException
	{
		// Validate argument
		if (reader == null)
			throw new IllegalArgumentException(NULL_READER_STR);

		// Test for XML element facade
		if (xmlElementFacade == null)
			throw new IllegalStateException(NO_XML_ELEMENT_FACADE_STR);

		// Initialise instance variables
		inputReader = reader;

		// Parse input text and return result
		return parse(true).xmlElement;
	}

	//------------------------------------------------------------------

	/**
	 * Parses the text that is read from a character stream by the reader that was set on this parser.  If the text
	 * conforms to the JSON grammar, the JSON text is transformed into a tree of either {@linkplain AbstractNode nodes}
	 * or {@linkplain Element XML elements} that correspond to JSON values; the {@code toXml} flag determines the kind
	 * of tree.  The root of the tree is returned.
	 *
	 * @param  toXml
	 *           if {@code true}, the JSON text is transformed into a tree of {@linkplain Element XML elements};
	 *           otherwise, the JSON text is transformed into a tree of {@linkplain AbstractNode nodes}.
	 * @return a pairing of the root of a tree of {@linkplain AbstractNode nodes} and the root of a tree of {@linkplain
	 *         Element XML elements}.  The inapplicable element of the pair is {@code null}.
	 * @throws ParseException
	 *           if an error occurs when parsing the input text.
	 */

	private Result parse(
		boolean	toXml)
		throws ParseException
	{
		// Reset instance variables
		index = 0;
		lineIndex = 0;
		lineStartIndex = 0;
		tokenIndex = 0;
		tokenBuffer.setLength(0);

		// Initialise local variables
		Deque<MemberInfo> memberInfoStack = new ArrayDeque<>();
		int memberIndex = 0;
		int memberLineIndex = 0;
		AbstractNode node = null;
		Element xmlElement = null;
		State state = State.VALUE_START;

		// Parse text
		while (state != State.DONE)
		{
			// Get next character from input stream
			char ch = nextChar();

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
							if (toXml)
							{
								if ((xmlElement == null) || (xmlElement.getParentNode() != null))
								{
									throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex,
															 index - lineStartIndex);
								}
							}
							else
							{
								if ((node == null) || !node.isRoot())
								{
									throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex,
															 index - lineStartIndex);
								}
							}

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
						if (toXml)
						{
							if ((xmlElement != null) && (xmlElement.getParentNode() == null)
									&& !ElementKind.isCompound(xmlElement))
							{
								throw new ParseException(ErrorMsg.EXTRANEOUS_TEXT, lineIndex,
														 tokenIndex - lineStartIndex);
							}
						}
						else
						{
							if ((node != null) && node.isRoot() && !node.isContainer())
							{
								throw new ParseException(ErrorMsg.EXTRANEOUS_TEXT, lineIndex,
														 tokenIndex - lineStartIndex);
							}
						}

						// Clear token buffer
						tokenBuffer.setLength(0);

						// Set next state according to character
						switch (ch)
						{
							case StringNode.START_CHAR:
								state = State.STRING_VALUE;
								break;

							case JsonConstants.ARRAY_START_CHAR:
								if (toXml)
									xmlElement = addChild(xmlElement, ElementKind.ARRAY, null);
								else
									node = new ListNode(node);
								state = State.ARRAY_ELEMENT_START;
								break;

							case JsonConstants.OBJECT_START_CHAR:
								if (toXml)
									xmlElement = addChild(xmlElement, ElementKind.OBJECT, null);
								else
									node = new MapNode(node);
								state = State.OBJECT_MEMBER_START;
								break;

							case JsonConstants.ARRAY_END_CHAR:
							case JsonConstants.OBJECT_END_CHAR:
							case JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR:
							case JsonConstants.OBJECT_MEMBER_SEPARATOR_CHAR:
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
					if (toXml)
					{
						// Get parent of current value
						Element parent = (xmlElement == null) ? null : (Element)xmlElement.getParentNode();

						// If current value has no parent (ie, it is the root value), move to next value (which should
						// not exist) ...
						if (parent == null)
							state = State.VALUE_START;

						// ... otherwise, if parent is array or object, add value to it ...
						else if (ElementKind.isCompound(parent))
						{
							// Case: parent is JSON array
							if (ElementKind.ARRAY.matches(parent))
							{
								// Add element to its parent array
								parent.appendChild(xmlElement);

								// Set next state
								state = State.ARRAY_ELEMENT_END;
							}

							// Case: parent is JSON object
							else
							{
								// Get info about current member from stack
								MemberInfo memberInfo = memberInfoStack.removeFirst();

								// Set 'name' attribute of member
								JsonXmlUtils.setName(xmlElementFacade, xmlElement, memberInfo.name);

								// Add member to its parent object
								parent.appendChild(xmlElement);

								// Set next state
								state = State.OBJECT_MEMBER_END;
							}

							// Push back element/member separator or array/object terminator
							pushBackChar();

							// Set current value to previous parent
							xmlElement = parent;
						}

						// ... otherwise, throw exception
						else
							throw new RuntimeException(INVALID_PARENT_STR);
					}
					else
					{
						// Get parent of current value
						AbstractNode parent = (node == null) ? null : node.getParent();

						// If current value has no parent (ie, it is the root value), move to next value (which should
						// not exist) ...
						if (parent == null)
							state = State.VALUE_START;

						// ... otherwise, if parent is array or object, add value to it ...
						else if (parent.isContainer())
						{
							// Case: parent is JSON array
							if (parent instanceof ListNode array)
							{
								// Add element to its parent array
								array.add(node);

								// Set next state
								state = State.ARRAY_ELEMENT_END;
							}

							// Case: parent is JSON object
							else if (parent instanceof MapNode object)
							{
								// Get information about current object member from stack
								MemberInfo memberInfo = memberInfoStack.removeFirst();

								// Test for duplicate member name
								if (object.hasKey(memberInfo.name))
								{
									throw new ParseException(ErrorMsg.DUPLICATE_OBJECT_MEMBER_NAME,
															 memberInfo.lineIndex, memberInfo.index - lineStartIndex,
															 memberInfo.name);
								}

								// Add member to its parent object value
								object.add(memberInfo.name, node);

								// Set next state
								state = State.OBJECT_MEMBER_END;
							}

							// Push back element/member separator or array/object terminator
							pushBackChar();

							// Set current value to previous parent
							node = parent;
						}

						// ... otherwise, throw exception
						else
							throw new RuntimeException(INVALID_PARENT_STR);
					}
					break;
				}

				//----  JSON literal value (null or Boolean)
				case LITERAL_VALUE:
				{
					// If end of current token, add null node or Boolean node according to token ...
					if (isValueTerminator(ch))
					{
						if (toXml)
						{
							String str = tokenBuffer.toString();
							switch (str)
							{
								case NullNode.VALUE:
									xmlElement = addChild(xmlElement, ElementKind.NULL, null);
									break;

								case BooleanNode.VALUE_FALSE:
								case BooleanNode.VALUE_TRUE:
									xmlElement = addChild(xmlElement, ElementKind.BOOLEAN, str);
									break;

								default:
									throw new ParseException(ErrorMsg.ILLEGAL_VALUE, lineIndex,
															 tokenIndex - lineStartIndex);
							}
						}
						else
						{
							switch (tokenBuffer.toString())
							{
								case NullNode.VALUE:
									node = new NullNode(node);
									break;

								case BooleanNode.VALUE_FALSE:
									node = new BooleanNode(node, false);
									break;

								case BooleanNode.VALUE_TRUE:
									node = new BooleanNode(node, true);
									break;

								default:
									throw new ParseException(ErrorMsg.ILLEGAL_VALUE, lineIndex,
															 tokenIndex - lineStartIndex);
							}
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

					// Push back start of number
					pushBackChar();

					// Validate number; put valid number in token buffer
					validateNumber();

					// Get string representation of number
					String numberStr = tokenBuffer.toString();

					// Process number string
					try
					{
						// If generating XML, add number element to parent ...
						if (toXml)
							xmlElement = addChild(xmlElement, ElementKind.NUMBER, numberStr);

						// ... otherwise, parse number
						else
						{
							// Create new instance of BigDecimal from token
							BigDecimal number = new BigDecimal(numberStr);

							// If number is integer, add node of smallest type ...
							if (numberStr.indexOf('.') < 0)
							{
								try
								{
									node = new IntNode(node, number.intValueExact());
								}
								catch (ArithmeticException e)
								{
									try
									{
										node = new LongNode(node, number.longValueExact());
									}
									catch (ArithmeticException e0)
									{
										if (storeExcessiveIntegerAsFP)
											node = new DoubleNode(node, number.doubleValue());
										else
										{
											throw new ParseException(ErrorMsg.TOO_LARGE_FOR_INTEGER, lineIndex,
																	 tokenIndex - lineStartIndex);
										}
									}
								}
							}

							// ... otherwise, add node for double-precision FP
							else
								node = new DoubleNode(node, number.doubleValue());
						}

						// Push back terminator
						pushBackChar();

						// Set next state
						state = State.VALUE_END;
					}
					catch (NumberFormatException e)
					{
						String causeMessage = e.getMessage();
						throw new ParseException(((causeMessage == null) || causeMessage.isEmpty())
														? ErrorMsg.NOT_A_VALID_NUMBER + "."
														: ErrorMsg.NOT_A_VALID_NUMBER + ": " + causeMessage,
												 lineIndex, tokenIndex - lineStartIndex, numberStr);
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
						// Add string node
						if (toXml)
							xmlElement = addChild(xmlElement, ElementKind.STRING, tokenBuffer.toString());
						else
							node = new StringNode(node, tokenBuffer.toString());

						// Set next state
						state = State.VALUE_END;
					}
					break;
				}

				//----  Start of member of JSON object
				case OBJECT_MEMBER_START:
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

						// ... otherwise, expect another member
						else
						{
							// Push back start of member
							pushBackChar();

							// Set next state
							state = State.OBJECT_MEMBER_NAME_START;
						}
					}
					break;
				}

				//----  Start of name of member of JSON object
				case OBJECT_MEMBER_NAME_START:
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

						// Test for start of name of member
						if (ch != StringNode.START_CHAR)
						{
							throw new ParseException(ErrorMsg.OBJECT_MEMBER_NAME_EXPECTED, lineIndex,
													 tokenIndex - lineStartIndex);
						}

						// Update member-related variables
						memberIndex = tokenIndex;
						memberLineIndex = lineIndex;

						// Clear token buffer
						tokenBuffer.setLength(0);

						// Set next state
						state = State.OBJECT_MEMBER_NAME;
					}
					break;
				}

				//----  Name of member of JSON object
				case OBJECT_MEMBER_NAME:
				{
					// Test for premature end of input stream
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

					// Parse name of member; put valid name in token buffer; if name is valid, put information about
					// member on stack
					if (parseString(ch))
					{
						// Create information about member and put it on stack
						memberInfoStack.addFirst(new MemberInfo(tokenBuffer.toString(), memberIndex, memberLineIndex));

						// Set next state
						state = State.OBJECT_MEMBER_NAME_END;
					}
					break;
				}

				//----  End of name of member of JSON object
				case OBJECT_MEMBER_NAME_END:
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
						// Test for separator of object name and value
						if (ch != JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR)
						{
							throw new ParseException(ErrorMsg.NAME_SEPARATOR_EXPECTED, lineIndex,
													 tokenIndex - lineStartIndex);
						}

						// Set next state
						state = State.VALUE_START;
					}

					break;
				}

				//----  End of member of JSON object
				case OBJECT_MEMBER_END:
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
							case JsonConstants.OBJECT_MEMBER_SEPARATOR_CHAR:
								state = State.OBJECT_MEMBER_NAME_START;
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
							if (toXml)
							{
								if ((xmlElement == null) || (xmlElement.getChildNodes().getLength() > 0))
								{
									throw new ParseException(ErrorMsg.ARRAY_ELEMENT_EXPECTED, lineIndex,
															 index - 1 - lineStartIndex);
								}
							}
							else
							{
								if (!((node instanceof ListNode listNode) && listNode.isEmpty()))
								{
									throw new ParseException(ErrorMsg.ARRAY_ELEMENT_EXPECTED, lineIndex,
															 index - 1 - lineStartIndex);
								}
							}

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

		// Return result
		return new Result(node, xmlElement);
	}

	//------------------------------------------------------------------

	/**
	 * Reads and returns the next character from the input stream.  If a character has been {@linkplain #pushBackChar()
	 * pushed back}, the last character that was read from the input stream is returned.
	 *
	 * @return the next character from the input stream, or a space character (U+0020) if the end of the input stream
	 *         has been reached.
	 * @throws ParseException
	 *           if an error occurs when reading from the input stream.
	 */

	private char nextChar()
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
				// Read next character from input
				int ch = inputReader.read();

				// Set flag to indicate end of input
				endOfInput = (ch < 0);

				// If not end of input, increment input index
				if (!endOfInput)
					++index;

				// Update last character that was read from input
				inputChar = endOfInput ? ' ' : (char)ch;
			}
			catch (IOException e)
			{
				throw new ParseException(ErrorMsg.ERROR_READING_FROM_STREAM, e, lineIndex, index - lineStartIndex);
			}
		}

		// Return last character that was read from input
		return inputChar;
	}

	//------------------------------------------------------------------

	/**
	 * Pushes the last character that was read from input stream back to the stream.  This method may be called only
	 * once after each call to {@link #nextChar()}.
	 *
	 * @throws IllegalStateException
	 *           if a character is already pushed back or {@link #nextChar()} has not yet been called on the input
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
	 * Creates an element of the specified kind and with the specified value, and adds the new element to the specified
	 * parent.
	 *
	 * @param  parent
	 *           the parent to which the new element will be added.
	 * @param  elementKind
	 *           the kind of the new element.
	 * @param  value
	 *           the value of the new element.
	 * @return the new element that was added to {@code parent}.
	 */

	private Element addChild(
		Element		parent,
		ElementKind	elementKind,
		String		value)
	{
		Element element = elementKind.createElement(xmlElementFacade);
		if (parent != null)
			parent.appendChild(element);
		if (value != null)
			JsonXmlUtils.setValue(xmlElementFacade, element, value);
		return element;
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
		// Get index of character at which validation failed
		int errorIndex = index - 1 - tokenIndex;

		// Initialise secondary message
		String message = null;

		// If character is terminator, secondary message is 'ended prematurely' ...
		if (isValueTerminator(ch))
			message = String.format(ENDED_PREMATURELY_STR, errorIndex);

		// ... otherwise, secondary message is 'character is not allowed'
		else
		{
			String charStr = ((ch < '\u0020') || (ch > '\u007E')) ? UNICODE_PREFIX + StringNode.charToUnicodeHex(ch)
																  : "'" + Character.toString(ch) + "'";
			message = String.format(CHARACTER_NOT_ALLOWED_STR, charStr, errorIndex);
		}

		// Throw exception
		throw new ParseException(ErrorMsg.INVALID_NUMBER + ": " + message, lineIndex, tokenIndex - lineStartIndex);
	}

	//------------------------------------------------------------------

	/**
	 * Validates a JSON number at the {@linkplain #index current index} within the input text.  This method only checks
	 * that a number conforms to the JSON grammar; the number is subsequently parsed by calling {@link
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
			char ch = nextChar();

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
	 * Parses a JSON string at the {@linkplain #index current index} within the input text, and sets the resulting
	 * string in the {@linkplain #tokenBuffer token buffer}.  This method is not called on the quotation mark (U+0022)
	 * at the start of the string.
	 *
	 * @param  ch
	 *           the current character of the JSON string representation.
	 * @return {@code true} if the string has been parsed successfully, {@code false} if the end of the string has not
	 *         been reached.
	 * @throws ParseException
	 *           if an error occurs when parsing the JSON string.
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
		{
			throw new ParseException(ErrorMsg.ILLEGAL_CHARACTER_IN_STRING, lineIndex, tokenIndex - lineStartIndex,
									 UNICODE_PREFIX + StringNode.charToUnicodeHex(ch));
		}

		// If character is escape prefix, parse escape sequence
		if (ch == StringNode.ESCAPE_PREFIX_CHAR)
		{
			// Get first character of escape sequence after prefix
			ch = nextChar();

			// Test whether input stream ended before first character of escape sequence after prefix
			if (endOfInput)
				throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);

			// Initialise index of start of escape sequence without prefix
			int startIndex = index;

			// Case: Unicode escape sequence
			if (ch == StringNode.UNICODE_ESCAPE_CHAR)
			{
				// Read Unicode escape sequence from input stream
				for (int i = 0; i < StringNode.UNICODE_SEQUENCE_LENGTH; i++)
				{
					// Get next character from input stream
					unicodeSeqChars[i] = nextChar();

					// Test whether input stream ended before end of Unicode escape sequence
					if (endOfInput)
						throw new ParseException(ErrorMsg.PREMATURE_END_OF_TEXT, lineIndex, index - lineStartIndex);
				}

				// Parse Unicode escape sequence
				int value = 0;
				for (int i = 0; i < StringNode.UNICODE_SEQUENCE_LENGTH; i++)
				{
					// Decode hex-digit character
					ch = unicodeSeqChars[i];
					int digit = ((ch >= '0') && (ch <= '9'))
										? ch - '0'
										: ((ch >= 'A') && (ch <= 'F'))
												? ch - 'A' + 10
												: ((ch >= 'a') && (ch <= 'f'))
														? ch - 'a' + 10
														: -1;
					if (digit < 0)
					{
						--startIndex;
						throw new ParseException(ErrorMsg.ILLEGAL_UNICODE_ESCAPE_SEQUENCE, lineIndex,
												 startIndex - lineStartIndex,
												 StringNode.UNICODE_ESCAPE_PREFIX + new String(unicodeSeqChars));
					}

					// Update value
					value <<= 4;
					value |= digit;
				}
				ch = (char)value;
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

	/**
	 * Creates and returns a new instance of a {@linkplain Reader reader} for the specified text.
	 *
	 * @param  text
	 *           the text for which a reader is desired.
	 * @return a new instance of a {@linkplain Reader reader} for {@code text}.
	 */

	private Reader reader(
		CharSequence	text)
	{
		return new Reader()
		{
			@Override
			public int read()
				throws IOException
			{
				return (index < text.length()) ? text.charAt(index) : -1;
			}

			@Override
			public int read(
				char[]	buffer,
				int		offset,
				int		length)
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
		};
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: INFORMATION ABOUT A MEMBER OF A JSON OBJECT


	/**
	 * This record encapsulates information about a member of a JSON object.
	 *
	 * @param name
	 *          the name of a member of a JSON object.
	 * @param index
	 *          the index of the name of the member in the input text.
	 * @param lineIndex
	 *          the index of the line containing the name of the member in the input text.
	 */

	private record MemberInfo(
		String	name,
		int		index,
		int		lineIndex)
	{ }

	//==================================================================


	// RECORD: RESULT OF PARSING JSON TEXT


	/**
	 * This record is a pairing of the result of transforming some JSON text to a tree of {@linkplain AbstractNode
	 * nodes} and the result of transforming some JSON text to a tree of {@linkplain Element XML elements}.  The
	 * inapplicable element of the pair is {@code null}.
	 *
	 * @param node
	 *          the root of a tree of {@link AbstractNode}s.
	 * @param xmlElement
	 *          the root of a tree of {@link Element}s.
	 */

	private record Result(
		AbstractNode	node,
		Element			xmlElement)
	{ }

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: BUILDER FOR JSON PARSER


	/**
	 * This class implements a builder for a {@linkplain JsonParser JSON parser}.
	 */

	public static class Builder
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The interface through which XML elements are created and their attributes accessed. */
		private	IElementFacade	elementFacade;

		/** Flag: if {@code true}, a JSON number that is deemed to be an integer but is too large to be stored as a
			{@code long} will be stored as a {@code double}. */
		private	boolean			storeExcessiveIntegerAsFP;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a builder for a {@linkplain JsonParser JSON parser}.
		 */

		private Builder()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Sets the interface through which XML elements are created and their attributes accessed.
		 *
		 * @param  elementFacade
		 *           the interface through which XML elements will be created and their attributes accessed.
		 * @return this builder.
		 */

		public Builder elementFacade(
			IElementFacade	elementFacade)
		{
			// Update instance variable
			this.elementFacade = elementFacade;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets or clears the flag that determines whether a JSON number that is deemed to be an integer but is too
		 * large to be stored as a signed 64-bit integer (ie, a {@code long}) will be stored as a double-precision
		 * floating-point number (ie, a {@code double}).
		 *
		 * @param  storeExcessiveIntegerAsFP
		 *           if {@code true} a JSON number that is deemed to be an integer but is too large for a {@code long}
		 *           will be stored as a {@code double}.
		 * @return this builder.
		 */

		public Builder storeExcessiveIntegerAsFP(
			boolean	storeExcessiveIntegerAsFP)
		{
			// Update instance variable
			this.storeExcessiveIntegerAsFP = storeExcessiveIntegerAsFP;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates and returns a new instance of a JSON parser that is initialised from the state of this builder.
		 *
		 * @return a new instance of a JSON parser.
		 */

		public JsonParser build()
		{
			return new JsonParser(this);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


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
		 *          the items whose string representations will replace placeholders in {@code message}.
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
		 *          the items whose string representations will replace placeholders in {@code message}.
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

}

//----------------------------------------------------------------------
