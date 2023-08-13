/*====================================================================*\

SimpleWildcardPatternMatcher.java

Class: simple pattern matcher that supports wildcards.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.matcher;

//----------------------------------------------------------------------


// IMPORTS


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.blankaspect.common.regex.RegexUtils;

//----------------------------------------------------------------------


// CLASS: SIMPLE PATTERN MATCHER THAT SUPPORTS WILDCARDS


/**
 * This class implements a means of searching some text for an occurrence of a pattern, which may contain the following
 * wildcard characters:
 * <dl>
 *   <dt>?</dt>
 *   <dd style="margin-left: 0.8em;">Matches a single character.</dd>
 *   <dt>*</dt>
 *   <dd style="margin-left: 0.8em;">Matches a sequence of zero or more characters.</dd>
 * </dl>
 * <p style="margin-bottom: 0.3em;">
 * The text may be searched in any of the following ways, which are referred to as <i>matching modes</i>:
 * </p>
 * <ul>
 *   <li>The target pattern matches any part of the input text.</li>
 *   <li>The target pattern matches the start of the input text.</li>
 *   <li>The target pattern matches all of the input text.</li>
 * </ul>
 * <p>
 * The search is performed by converting the wildcard pattern to an equivalent {@linkplain Pattern regular-expression
 * pattern} and using methods of the {@link Matcher} class to search for the regex pattern in the input text.
 * </p>
 */

public class SimpleWildcardPatternMatcher
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The wildcard that matches a single character. */
	public static final		char	SINGLE_WILDCARD_CHAR	= '?';

	/** The wildcard that matches a sequence of zero or more characters. */
	public static final		char	MULTIPLE_WILDCARD_CHAR	= '*';

	/** The regular-expression construct that matches any character. */
	private static final	char	DOT_CHAR	= '.';

	/** The regular-expression construct that matches any character zero or more times reluctantly. */
	private static final	String	RELUCTANT_MATCH_ANY	= ".*?";

	/**
	 * The ways in which matching may be performed.
	 */
	public enum Mode
	{
		/** The target pattern matches any part of the input text. */
		ANYWHERE,

		/** The target pattern matches the start of the input text. */
		START,

		/** The target pattern matches all of the input text. */
		ALL
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The target pattern that is matched against input text. */
	private	String	pattern;

	/** The matching mode. */
	private	Mode	mode;

	/** The regular-expression pattern that was generated from {@link #pattern}. */
	private	Pattern	regexPattern;

	/** The matcher that was created from {@link #regexPattern}. */
	private	Matcher	matcher;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a matcher that may be used to match the specified target pattern against input text in
	 * a way that is determined by the specified mode and 'ignore case' flag.
	 *
	 * @param pattern
	 *          the target pattern that will be matched against input text by the {@link #match(CharSequence)} method.
	 * @param mode
	 *          the way in which {@code pattern} will be matched against input text.
	 * @param ignoreCase
	 *          if {@code true}, letter case will be ignored when the {@link #match(CharSequence)} method matches the
	 *          target pattern against input text.
	 */

	public SimpleWildcardPatternMatcher(
		String	pattern,
		Mode	mode,
		boolean	ignoreCase)
	{
		// Validate arguments
		if (pattern == null)
			throw new IllegalArgumentException("Null pattern");
		if (mode == null)
			throw new IllegalArgumentException("Null mode");

		// Initialise instance variables
		this.pattern = pattern;
		this.mode = mode;
		regexPattern = Pattern.compile(wildcardPatternToRegex(pattern, mode),
									   ignoreCase ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a matcher whose {@link #match(CharSequence)} method returns {@code true} if
	 * the specified target pattern matches any part of the input text.  Matching is case-sensitive.
	 *
	 * @param  pattern
	 *           the target pattern that will be matched against input text by the {@link #match(CharSequence)} method.
	 * @return a new instance of a case-sensitive matcher whose matching mode is 'ANY'.
	 */

	public static SimpleWildcardPatternMatcher anywhere(
		String	pattern)
	{
		return new SimpleWildcardPatternMatcher(pattern, Mode.ANYWHERE, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a matcher whose {@link #match(CharSequence)} method returns {@code true} if
	 * the specified target pattern matches any part of the input text.  Matching is case-insensitive.
	 *
	 * @param  pattern
	 *           the target pattern that will be matched against input text by the {@link #match(CharSequence)} method.
	 * @return a new instance of a case-insensitive matcher whose matching mode is 'ANY'.
	 */

	public static SimpleWildcardPatternMatcher anywhereIgnoreCase(
		String	pattern)
	{
		return new SimpleWildcardPatternMatcher(pattern, Mode.ANYWHERE, true);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a matcher whose {@link #match(CharSequence)} method returns {@code true} if
	 * the specified target pattern matches the start of the input text.  Matching is case-sensitive.
	 *
	 * @param  pattern
	 *           the target pattern that will be matched against input text by the {@link #match(CharSequence)} method.
	 * @return a new instance of a case-sensitive matcher whose matching mode is 'PREFIX'.
	 */

	public static SimpleWildcardPatternMatcher start(
		String	pattern)
	{
		return new SimpleWildcardPatternMatcher(pattern, Mode.START, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a matcher whose {@link #match(CharSequence)} method returns {@code true} if
	 * the specified target pattern matches the start of the input text.  Matching is case-insensitive.
	 *
	 * @param  pattern
	 *           the target pattern that will be matched against input text by the {@link #match(CharSequence)} method.
	 * @return a new instance of a case-insensitive matcher whose matching mode is 'PREFIX'.
	 */

	public static SimpleWildcardPatternMatcher startIgnoreCase(
		String	pattern)
	{
		return new SimpleWildcardPatternMatcher(pattern, Mode.START, true);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a matcher whose {@link #match(CharSequence)} method returns {@code true} if
	 * the specified target pattern matches all of the input text.  Matching is case-sensitive.
	 *
	 * @param  pattern
	 *           the target pattern that will be matched against input text by the {@link #match(CharSequence)} method.
	 * @return a new instance of a case-sensitive matcher whose matching mode is 'ALL'.
	 */

	public static SimpleWildcardPatternMatcher all(
		String	pattern)
	{
		return new SimpleWildcardPatternMatcher(pattern, Mode.ALL, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a matcher whose {@link #match(CharSequence)} method returns {@code true} if
	 * the specified target pattern matches all of the input text.  Matching is case-insensitive.
	 *
	 * @param  pattern
	 *           the target pattern that will be matched against input text by the {@link #match(CharSequence)} method.
	 * @return a new instance of a case-insensitive matcher whose matching mode is 'ALL'.
	 */

	public static SimpleWildcardPatternMatcher allIgnoreCase(
		String	pattern)
	{
		return new SimpleWildcardPatternMatcher(pattern, Mode.ALL, true);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a regular expression that is equivalent to the specified wildcard pattern and matching mode.
	 *
	 * @param  pattern
	 *           the target pattern for which a regular expression will be created.
	 * @param  mode
	 *           the way in which {@code pattern} will be matched against input text.
	 * @return a regular expression that is equivalent to {@code pattern} and {@code mode}.
	 */

	public static String wildcardPatternToRegex(
		String	pattern,
		Mode	mode)
	{
		// Allocate buffer for output pattern
		int length = pattern.length();
		StringBuilder buffer = new StringBuilder(length * 2);

		// Convert pattern
		boolean star = false;
		int index = 0;
		while (index < length)
		{
			// Match from start of text
			if (mode == Mode.START)
			{
				if (index == 0)
					buffer.append('^');
			}

			// Get next character from input pattern
			char ch = pattern.charAt(index++);

			// Case: star
			if (ch == MULTIPLE_WILDCARD_CHAR)
				star = true;

			// Case: not star
			else
			{
				// Replace sequence of stars with 'match any character' and reluctant quantifier
				if (star)
				{
					buffer.append(RELUCTANT_MATCH_ANY);
					star = false;
				}

				// Append character
				buffer.append((ch == SINGLE_WILDCARD_CHAR) ? DOT_CHAR : RegexUtils.escape(ch));
			}
		}

		// Replace pending sequence of stars with 'match any character' and reluctant quantifier
		if (star)
			buffer.append(RELUCTANT_MATCH_ANY);

		// Return output pattern
		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the target pattern.
	 *
	 * @return the target pattern.
	 */

	public String getPattern()
	{
		return pattern;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the regular-expression matcher.
	 *
	 * @return the regular-expression matcher.
	 */

	public Matcher getMatcher()
	{
		return matcher;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if an occurrence of the target pattern is found in the specified text.
	 *
	 * @param  text
	 *           the text that will be searched for an occurrence of the target pattern.
	 * @return {@code true} if an occurrence of the target pattern is found in {@code text}.
	 */

	public boolean match(
		CharSequence	text)
	{
		// No match if text is null
		if (text == null)
			return false;

		// Create matcher for input text
		matcher = regexPattern.matcher(text);

		// Search for match according to mode
		return (mode == Mode.ALL) ? matcher.matches() : matcher.find();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
