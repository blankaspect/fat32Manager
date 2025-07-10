/*====================================================================*\

ITextLine.java

Interface: line of text.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.text;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Comparator;
import java.util.List;

//----------------------------------------------------------------------


// INTERFACE: LINE OF TEXT


/**
 * This interface defines the methods that must be implemented by a class that represents a line of text.
 */

public interface ITextLine
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A comparator that compares two lines by the offset from the start of the text to the start of the line. */
	Comparator<ITextLine>	OFFSET_COMPARATOR	= Comparator.comparingInt(ITextLine::offset);

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the text of this line.
	 *
	 * @return the text of this line.
	 */

	String text();

	//------------------------------------------------------------------

	/**
	 * Returns the offset from the start of the text to the start of this line.
	 *
	 * @return the offset from the start of the text to the start of this line.
	 */

	int offset();

	//------------------------------------------------------------------

	/**
	 * Returns the offset from the start of the text to the exclusive end of this line (that is, the character
	 * immediately after the end of this line).
	 *
	 * @return the offset from the start of the text to the exclusive end of this line.
	 */

	default int endOffset()
	{
		return offset() + length();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the length of this line.
	 *
	 * @return the length of this line.
	 */

	default int length()
	{
		int length = 0;
		for (ITextSpan span : spans())
			length += span.length();
		return length;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the spans of text that are associated with this line.
	 *
	 * @return a list of the spans of text that are associated with this line.
	 */

	List<? extends ITextSpan> spans();

	//------------------------------------------------------------------

	/**
	 * Returns the number of spans of text that are associated with this line.
	 *
	 * @return the number of spans of text that are associated with this line.
	 */

	default int numSpans()
	{
		return spans().size();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
