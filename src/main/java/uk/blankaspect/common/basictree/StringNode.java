/*====================================================================*\

StringNode.java

Class: string node.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.basictree;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: STRING NODE


/**
 * This class implements a {@linkplain AbstractNode node} that contains a string.
 * <p style="margin-bottom: 0.25em;">
 * The default string representation of a string node begins and ends with quotation marks, between which are zero or
 * more literal characters or escape sequences.  Within the quotation marks, the following characters must be escaped:
 * </p>
 * <ul style="margin-top: 0.25em;">
 *   <li>{@code U+0022 : } quotation mark ("),</li>
 *   <li>{@code U+005C : } reverse solidus (\),</li>
 *   <li>a control character in the range {@code U+0000} to {@code U+001F} inclusive.</li>
 * </ul>
 * <p>
 * An escape sequence consists of a prefix (a reverse solidus, '\', U+005C) followed by either a single character or,
 * for a Unicode escape, a 'u' and four hexadecimal-digit characters; see {@link #escape(CharSequence)}.
 * </p>
 */

public class StringNode
	extends AbstractNode
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The character that denotes the start of the string representation of a string node. */
	public static final		char	START_CHAR	= '"';

	/** The character that denotes the end of the string representation of a string node. */
	public static final		char	END_CHAR	= START_CHAR;

	/** The character with which an escape sequence begins. */
	public static final		char	ESCAPE_PREFIX_CHAR	= '\\';

	/** The string with which an escape sequence begins. */
	public static final		String	ESCAPE_PREFIX		= Character.toString(ESCAPE_PREFIX_CHAR);

	/** The character that denotes a Unicode escape sequence when it follows the {@linkplain #ESCAPE_PREFIX escape
		prefix}. */
	public static final		char	UNICODE_ESCAPE_CHAR	= 'u';

	/** The string with which a Unicode escape sequence begins. */
	public static final		String	UNICODE_ESCAPE_PREFIX	= ESCAPE_PREFIX + UNICODE_ESCAPE_CHAR;

	/** The number of hexadecimal digits in a Unicode escape sequence. */
	public static final		int		UNICODE_SEQUENCE_LENGTH	= 4;

	/** Hexadecimal-digit characters. */
	public static final		char[]	HEX_DIGITS	= "0123456789ABCDEF".toCharArray();

	/** The type of a string node. */
	public static final		NodeType	TYPE	= new NodeType(NodeType.ANY, StringNode.class);

	/** Mappings from literal characters to their corresponding characters in an escape sequence. */
	protected static final	char[][]	ESCAPE_MAPPINGS	=
	{
		{ '\\', '\\' },
		{ '\"', '\"' },
		{ '\b', 'b' },
		{ '\t', 't' },
		{ '\n', 'n' },
		{ '\f', 'f' },
		{ '\r', 'r' }
	};

	/** The minimum value of a Unicode high surrogate. */
	private static final	int		UNICODE_MIN_HIGH_SURROGATE	= 0xD800;

	/** The maximum value of a Unicode high surrogate. */
	private static final	int		UNICODE_MAX_HIGH_SURROGATE	= 0xDBFF;

	/** The minimum value of a Unicode low surrogate. */
	private static final	int		UNICODE_MIN_LOW_SURROGATE	= 0xDC00;

	/** The maximum value of a Unicode low surrogate. */
	private static final	int		UNICODE_MAX_LOW_SURROGATE	= 0xDFFF;

	/** The minimum code point of Unicode plane 1. */
	private static final	int		UNICODE_PLANE1_MIN_CODE_POINT	= 0x10000;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The value of this string node. */
	private	String	value;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a string node that has no parent and has the specified value.
	 *
	 * @param  value
	 *           the value of the string node.
	 * @throws IllegalArgumentException
	 *           if {@code value} is {@code null}.
	 */

	public StringNode(
		String	value)
	{
		// Call alternative constructor
		this(null, value);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a string node that has the specified parent and value.
	 *
	 * @param  parent
	 *           the parent of the string node.
	 * @param  value
	 *           the value of the string node.
	 * @throws IllegalArgumentException
	 *           if {@code value} is {@code null}.
	 */

	public StringNode(
		AbstractNode	parent,
		String			value)
	{
		// Call superclass constructor
		super(parent);

		// Validate argument
		if (value == null)
			throw new IllegalArgumentException("Null value");

		// Initialise instance variables
		this.value = value;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a representation of the Unicode value of the specified character as a string of four hexadecimal-digit
	 * characters.
	 *
	 * @param  ch
	 *           the character whose string representation of its Unicode value is desired.
	 * @return the representation of the Unicode value of {@code ch} as a string of four hexadecimal-digit characters.
	 */

	public static String charToUnicodeHex(
		char	ch)
	{
		int code = ch;
		char[] digits = new char[UNICODE_SEQUENCE_LENGTH];
		int i = digits.length;
		while (i > 0)
		{
			digits[--i] = HEX_DIGITS[code & 0x0F];
			code >>>= 4;
		}
		return new String(digits);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the Unicode escape sequence of the specified character.
	 *
	 * @param  ch
	 *           the character whose Unicode escape sequence is desired.
	 * @return the Unicode escape sequence of {@code ch}.
	 */

	public static String unicodeEscape(
		char	ch)
	{
		return UNICODE_ESCAPE_PREFIX + charToUnicodeHex(ch);
	}

	//------------------------------------------------------------------

	/**
	 * Transforms the specified character sequence to a string, replacing each occurrence of one of the following
	 * characters with an escape sequence, and returns the resulting string:
	 * <ul>
	 *   <li>a quotation mark ("),</li>
	 *   <li>a reverse solidus (\),</li>
	 *   <li>a character outside the range U+0020 to U+007E inclusive.</li>
	 * </ul>
	 * <p>
	 * Each occurrence of the following characters in the input sequence is replaced by the corresponding two-character
	 * escape sequence in the rightmost column of the table:
	 * </p>
	 * <table>
	 *   <caption><i>Two-character escape sequences</i></caption>
	 *   <tbody>
	 *     <tr>
	 *       <td>U+0008</td>
	 *       <td>&nbsp;</td>
	 *       <td>backspace</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \b}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+0009</td>
	 *       <td>&nbsp;</td>
	 *       <td>tab</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \t}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+000A</td>
	 *       <td>&nbsp;</td>
	 *       <td>line feed</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \n}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+000C</td>
	 *       <td>&nbsp;</td>
	 *       <td>form feed</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \f}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+000D</td>
	 *       <td>&nbsp;</td>
	 *       <td>carriage return</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \r}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+0022</td>
	 *       <td>&nbsp;</td>
	 *       <td>quotation mark (")</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \"}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+005C</td>
	 *       <td>&nbsp;</td>
	 *       <td>reverse solidus (\)</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \\}</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * <p>
	 * In addition, each character in the input sequence that lies outside the range U+0020 to U+007E inclusive and is
	 * not in the table above is replaced by its six-character Unicode escape sequence, {@code \}{@code u}<i>{@code
	 * nnnn}</i>, where <i>{@code n}</i> is a hexadecimal-digit character.
	 * </p>
	 * <p>
	 * Characters in the input sequence that are not escaped appear in the output string unchanged.
	 * </p>
	 * <p>
	 * The returned string contains only printable characters from the US-ASCII character encoding (ie, characters in
	 * the range U+0020 to U+007E inclusive).
	 * </p>
	 *
	 * @param  seq
	 *           the character sequence that will be escaped.
	 * @return a string in which each character in {@code seq} that must be escaped or is greater than U+007E is
	 *         replaced by an escape sequence.
	 */

	public static String escape(
		CharSequence	seq)
	{
		return escape(seq, true);
	}

	//------------------------------------------------------------------

	/**
	 * Transforms the specified character sequence to a string, replacing each occurrence of one of the following
	 * characters with an escape sequence, and returns the resulting string:
	 * <ul>
	 *   <li>a quotation mark ("),</li>
	 *   <li>a reverse solidus (\),</li>
	 *   <li>a character in the range U+0000 to U+001F inclusive,</li>
	 *   <li>a character in the range U+007F to U+009F inclusive,</li>
	 *   <li>optionally, a character whose code is greater than or equal to U+00A0.</li>
	 * </ul>
	 * <p>
	 * Each occurrence of the following characters in the input sequence is replaced by the corresponding two-character
	 * escape sequence in the rightmost column of the table:
	 * </p>
	 * <table>
	 *   <caption><i>Two-character escape sequences</i></caption>
	 *   <tbody>
	 *     <tr>
	 *       <td>U+0008</td>
	 *       <td>&nbsp;</td>
	 *       <td>backspace</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \b}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+0009</td>
	 *       <td>&nbsp;</td>
	 *       <td>tab</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \t}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+000A</td>
	 *       <td>&nbsp;</td>
	 *       <td>line feed</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \n}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+000C</td>
	 *       <td>&nbsp;</td>
	 *       <td>form feed</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \f}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+000D</td>
	 *       <td>&nbsp;</td>
	 *       <td>carriage return</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \r}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+0022</td>
	 *       <td>&nbsp;</td>
	 *       <td>quotation mark (")</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \"}</td>
	 *     </tr>
	 *     <tr>
	 *       <td>U+005C</td>
	 *       <td>&nbsp;</td>
	 *       <td>reverse solidus (\)</td>
	 *       <td>&nbsp;&nbsp;&nbsp;</td>
	 *       <td>{@code \\}</td>
	 *     </tr>
	 *   </tbody>
	 * </table>
	 * <p>
	 * In addition, each <i>control character</i> in the input sequence (that is, each character in the range U+0000 to
	 * U+001F inclusive or in the range U+007F to U+009F inclusive) is replaced by its six-character Unicode escape
	 * sequence, {@code \}{@code u}<i>{@code nnnn}</i>, where <i>{@code n}</i> is a hexadecimal-digit character.
	 * </p>
	 * <p>
	 * If the {@code printableAsciiOnly} flag is {@code true}, each character in the input sequence whose code is
	 * greater than or equal to U+00A0 is replaced by its six-character Unicode escape sequence.  In this case, the
	 * returned string will contain only printable characters from the US-ASCII character encoding (ie, characters in
	 * the range U+0020 to U+007E inclusive).
	 * </p>
	 * <p>
	 * Characters in the input sequence that are not escaped appear in the output string unchanged.
	 * </p>
	 *
	 * @param  seq
	 *           the character sequence that will be escaped.
	 * @param  printableAsciiOnly
	 *           if {@code true}, the characters of {@code seq} will be escaped where necessary so that the returned
	 *           string contains only printable characters from the US-ASCII character encoding (ie, characters in the
	 *           range U+0020 to U+007E inclusive).
	 * @return a string in which each character in {@code seq} that must be escaped or that satisfies any of the
	 *         conditions described above is replaced by an escape sequence.
	 */

	public static String escape(
		CharSequence	seq,
		boolean			printableAsciiOnly)
	{
		// Initialise buffer for output string
		int inLength = seq.length();
		StringBuilder buffer = new StringBuilder(inLength + inLength / 2);

		// Process input sequence
		for (int i = 0; i < inLength; i++)
		{
			// Get next character from input sequence
			char ch = seq.charAt(i);

			// Process surrogate pair
			if ((ch >= UNICODE_MIN_HIGH_SURROGATE) && (ch <= UNICODE_MAX_HIGH_SURROGATE) && (i + 1 < inLength))
			{
				char ch1 = seq.charAt(i + 1);
				if ((ch1 >= UNICODE_MIN_LOW_SURROGATE) && (ch1 <= UNICODE_MAX_LOW_SURROGATE))
				{
					if (printableAsciiOnly)
					{
						buffer.append(unicodeEscape(ch));
						buffer.append(unicodeEscape(ch1));
					}
					else
					{
						buffer.appendCodePoint(UNICODE_PLANE1_MIN_CODE_POINT
												+ (ch - UNICODE_MIN_HIGH_SURROGATE << 10)
												+ (ch1 - UNICODE_MIN_LOW_SURROGATE));
					}
					++i;
					continue;
				}
			}

			// Search for standard escape sequence for character
			char[] pair = null;
			for (int j = 0; j < ESCAPE_MAPPINGS.length; j++)
			{
				char[] pair0 = ESCAPE_MAPPINGS[j];
				if (ch == pair0[0])
				{
					pair = pair0;
					break;
				}
			}

			// If there is a standard escape sequence for character, use it ...
			if (pair != null)
			{
				buffer.append(ESCAPE_PREFIX_CHAR);
				buffer.append(pair[1]);
			}

			// ... otherwise, if character is not printable ASCII, replace it with its Unicode escape sequence ...
			else if ((printableAsciiOnly && ((ch < '\u0020') || (ch > '\u007E')))
						|| ((ch >= '\u0000') && (ch <= '\u001F'))
						|| ((ch >= '\u007F') && (ch <= '\u009F')))
				buffer.append(unicodeEscape(ch));

			// ... otherwise, append the character unchanged
			else
				buffer.append(ch);
		}

		// Return output string
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Calls {@link #escape(CharSequence)} on the specified character sequence, encloses the returned string in
	 * quotation marks and returns the resulting string.
	 *
	 * @param  seq
	 *           the character sequence that will be escaped and quoted.
	 * @return a string consisting of {@code seq} escaped and enclosed in quotation marks.
	 */

	public static String escapeAndQuote(
		CharSequence	seq)
	{
		return escapeAndQuote(seq, true);
	}

	//------------------------------------------------------------------

	/**
	 * Calls {@link #escape(CharSequence, boolean)} on the specified character sequence, encloses the returned string in
	 * quotation marks and returns the resulting string.
	 *
	 * @param  seq
	 *           the character sequence that will be escaped and quoted.
	 * @param  printableAsciiOnly
	 *           if {@code true}, the characters of {@code seq} will be escaped where necessary so that the returned
	 *           string contains only printable characters from the US-ASCII character encoding (ie, characters in the
	 *           range U+0020 to U+007E inclusive).
	 * @return a string consisting of {@code seq} escaped and enclosed in quotation marks.
	 */

	public static String escapeAndQuote(
		CharSequence	seq,
		boolean			printableAsciiOnly)
	{
		return START_CHAR + escape(seq, printableAsciiOnly) + END_CHAR;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a list of string nodes for the specified values, preserving the order of the elements, and returns the
	 * list, which may be used to construct a {@linkplain ListNode list node}.
	 *
	 * @param  values
	 *           the values for which string nodes will be created.
	 * @return a list of string nodes whose underlying values are {@code values}.
	 */

	public static List<StringNode> valuesToNodes(
		String...	values)
	{
		List<StringNode> outValues = new ArrayList<>();
		for (String value : values)
			outValues.add(new StringNode(value));
		return outValues;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a list of string nodes for the specified values, preserving the order of the elements, and returns the
	 * list, which may be used to construct a {@linkplain ListNode list node}.
	 *
	 * @param  values
	 *           the values for which string nodes will be created.
	 * @return a list of string nodes whose underlying values are {@code values}.
	 */

	public static List<StringNode> valuesToNodes(
		Iterable<String>	values)
	{
		List<StringNode> outValues = new ArrayList<>();
		for (String value : values)
			outValues.add(new StringNode(value));
		return outValues;
	}

	//------------------------------------------------------------------

	/**
	 * Returns an array whose elements are the values of the specified {@linkplain StringNode string nodes}, with the
	 * order of the elements preserved.
	 *
	 * @param  nodes
	 *           the string nodes whose values will be extracted into an array.
	 * @return an array whose elements are the values of {@code nodes}.
	 */

	public static String[] nodesToArray(
		Collection<? extends StringNode>	nodes)
	{
		int numNodes = nodes.size();
		String[] values = new String[numNodes];
		int index = 0;
		for (StringNode node : nodes)
			values[index++] = node.getValue();
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list whose elements are the values of the specified {@linkplain StringNode string nodes}, with the
	 * order of the elements preserved.
	 *
	 * @param  nodes
	 *           the string nodes whose values will be extracted into a list.
	 * @return a list whose elements are the values of {@code nodes}.
	 */

	public static List<String> nodesToList(
		Iterable<? extends StringNode>	nodes)
	{
		List<String> values = new ArrayList<>();
		for (StringNode node : nodes)
			values.add(node.getValue());
		return values;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * @return {@link #TYPE}.
	 */

	@Override
	public NodeType getType()
	{
		return TYPE;
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 * For a string node, this method always returns {@code false}.
	 *
	 * @return {@code false}.
	 */

	@Override
	public boolean isContainer()
	{
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified object is an instance of {@code StringNode} <i>and</i> it has the same
	 * value as this string node.
	 *
	 * @param  obj
	 *           the object with which this string node will be compared.
	 * @return {@code true} if {@code obj} is an instance of {@code StringNode} <i>and</i> it has the same value as this
	 *         string node; {@code false} otherwise.
	 */

	@Override
	public boolean equals(
		Object	obj)
	{
		return (this == obj) || ((obj instanceof StringNode other) && value.equals(other.value));
	}

	//------------------------------------------------------------------

	/**
	 * Returns the hash code of this string node.
	 *
	 * @return the hash code of this string node.
	 */

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a copy of this string node that has no parent.
	 *
	 * @return a copy of this string node that has no parent.
	 */

	@Override
	public StringNode clone()
	{
		return (StringNode)super.clone();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of this string node.
	 *
	 * @return a string representation of this string node.
	 */

	@Override
	public String toString()
	{
		return escapeAndQuote(value, true);
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public String toString(
		boolean	printableAsciiOnly)
	{
		return escapeAndQuote(value, printableAsciiOnly);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of this string node.
	 *
	 * @return the value of this string node.
	 */

	public String getValue()
	{
		return value;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
