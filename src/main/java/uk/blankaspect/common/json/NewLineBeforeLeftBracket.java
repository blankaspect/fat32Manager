/*====================================================================*\

NewLineBeforeLeftBracket.java

Enumeration: control of new line before start of JSON object or array.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.json;

//----------------------------------------------------------------------


// ENUMERATION: CONTROL OF NEW LINE BEFORE START OF JSON OBJECT OR ARRAY


/**
 * This is an enumeration of the circumstances in which a new line is written before the opening bracket of a JSON array
 * or the opening brace of a JSON object.
 */

public enum NewLineBeforeLeftBracket
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * Never write a new line (LF, U+000A) before the opening bracket of a JSON array or the opening brace of a JSON
	 * object.
	 */
	NEVER,

	/**
	 * Write a new line (LF, U+000A) before the opening bracket of a JSON array or the opening brace of a JSON
	 * object except when the bracket or brace occurs after the name of a member of a JSON object.
	 */
	EXCEPT_AFTER_NAME,

	/**
	 * Always write a new line (LF, U+000A) before the opening bracket of a JSON array or the opening brace of a
	 * JSON object.
	 */
	ALWAYS
}

//----------------------------------------------------------------------
