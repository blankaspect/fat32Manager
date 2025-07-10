/*====================================================================*\

ITextSpan.java

Interface: span of text.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.text;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Comparator;
import java.util.List;

//----------------------------------------------------------------------


// INTERFACE: SPAN OF TEXT


/**
 * This interface defines the methods that must be implemented by a class that represents a span of text (that is, a
 * sequence of characters).
 */

public interface ITextSpan
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A comparator that compares two spans by the offset from the start of the text to the start of the span. */
	Comparator<ITextSpan>	OFFSET_COMPARATOR	= Comparator.comparingInt(ITextSpan::offset);

	/** A comparator that compares two spans: first, by the offset from the start of the text to the start of the span;
		then, if the offsets are equal, by the length of the span. */
	Comparator<ITextSpan>	OFFSET_LENGTH_COMPARATOR	=
			Comparator.comparingInt(ITextSpan::offset).thenComparingInt(ITextSpan::length);

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the offset from the start of the text to the start of this span.
	 *
	 * @return the offset from the start of the text to the start of this span.
	 */

	int offset();

	//------------------------------------------------------------------

	/**
	 * Returns the offset from the start of the text to the exclusive end of this span (that is, the character
	 * immediately after the end of this span).
	 *
	 * @return the offset from the start of the text to the exclusive end of this span.
	 */

	default int endOffset()
	{
		return offset() + length();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the length of this span.
	 *
	 * @return the length of this span.
	 */

	int length();

	//------------------------------------------------------------------

	/**
	 * Returns a list of the keys that are associated with this span.
	 *
	 * @return a list of the keys that are associated with this span.
	 */

	List<String> keys();

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
