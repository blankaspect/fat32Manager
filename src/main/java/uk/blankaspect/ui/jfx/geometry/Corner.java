/*====================================================================*\

Corner.java

Enumeration: corner.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.geometry;

//----------------------------------------------------------------------


// ENUMERATION: CORNER


/**
 * This is an enumeration of the corners of an axis-aligned rectangle.
 */

public enum Corner
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * The top left corner.
	 */
	TOP_LEFT,

	/**
	 * The top right corner.
	 */
	TOP_RIGHT,

	/**
	 * The bottom left corner.
	 */
	BOTTOM_LEFT,

	/**
	 * The bottom right corner.
	 */
	BOTTOM_RIGHT;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an enumeration constant.
	 */

	private Corner()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if this corner is incident with the top edge of an axis-aligned rectangle.
	 *
	 * @return {@code true} if this corner is incident with the top edge of an axis-aligned rectangle.
	 */

	public boolean isTop()
	{
		return (this == TOP_LEFT) || (this == TOP_RIGHT);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this corner is incident with the bottom edge of an axis-aligned rectangle.
	 *
	 * @return {@code true} if this corner is incident with the bottom edge of an axis-aligned rectangle.
	 */

	public boolean isBottom()
	{
		return (this == BOTTOM_LEFT) || (this == BOTTOM_RIGHT);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this corner is incident with the left edge of an axis-aligned rectangle.
	 *
	 * @return {@code true} if this corner is incident with the left edge of an axis-aligned rectangle.
	 */

	public boolean isLeft()
	{
		return (this == TOP_LEFT) || (this == BOTTOM_LEFT);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this corner is incident with the right edge of an axis-aligned rectangle.
	 *
	 * @return {@code true} if this corner is incident with the right edge of an axis-aligned rectangle.
	 */

	public boolean isRight()
	{
		return (this == TOP_RIGHT) || (this == BOTTOM_RIGHT);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
