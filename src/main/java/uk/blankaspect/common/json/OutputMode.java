/*====================================================================*\

OutputMode.java

Enumeration: output mode of a JSON generator.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.json;

//----------------------------------------------------------------------


// ENUMERATION: OUTPUT MODE OF A JSON GENERATOR


/**
 * This is an enumeration of the ways in which a JSON generator can write whitespace between the tokens of the JSON
 * text.
 */

public enum OutputMode
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * JSON text is written on a single line with no space between tokens.
	 */
	DENSE,

	/**
	 * JSON text is written on a single line with a space between some tokens.
	 */
	COMPACT,

	/**
	 * JSON text may be written on multiple lines with a space between some tokens.  Compound values (array and object)
	 * are written on a single line in some cases:
	 * <ul>
	 *   <li>
	 *     The opening and closing brackets and elements of an array are written on a single line if they all fit on
	 *     the line; otherwise, they are split over multiple lines, as necessary.
	 *   </li>
	 *   <li>
	 *     An object is written on a single line if it is empty or contains a single member that fits on the line
	 *     along with its opening and closing braces; otherwise, its members and closing brace are each written on a
	 *     separate line, and a {@link NewLineBeforeLeftBracket} parameter determines whether the opening brace is
	 *     written on a separate line.
	 *   </li>
	 * </ul>
	 */
	NORMAL,

	/**
	 * JSON text may be written on multiple lines with a space between some tokens.  Compound values (array and object)
	 * are written on a single line in some cases:
	 * <ul>
	 *   <li>
	 *     An empty array is written on a single line.  The elements and closing bracket of a non-empty array are
	 *     each written on a separate line, and the opening bracket is written on a separate line if the <i>opening
	 *     bracket on same line</i> flag is {@code false}.
	 *   </li>
	 *   <li>
	 *     An empty object is written on a single line.  The members and closing brace of a non-empty object are
	 *     each written on a separate line, and a {@link NewLineBeforeLeftBracket} parameter determines whether the
	 *     opening brace is written on a separate line.
	 *   </li>
	 * </ul>
	 */
	EXPANDED
}

//----------------------------------------------------------------------
