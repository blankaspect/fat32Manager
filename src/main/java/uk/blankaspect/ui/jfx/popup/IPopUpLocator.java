/*====================================================================*\

IPopUpLocator.java

Interface: pop-up window locator.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import uk.blankaspect.common.function.IFunction0;

//----------------------------------------------------------------------


// INTERFACE: POP-UP WINDOW LOCATOR


/**
 * This functional interface defines the method that must be implemented by a provider of the location of a pop-up
 * window, given the layout bounds of the pop-up's content and a function that returns a location that may be used to
 * locate the pop-up.
 */

@FunctionalInterface
public interface IPopUpLocator
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the location of a pop-up window for the specified layout bounds of the pop-up's content.
	 *
	 * @param  contentBounds
	 *           the layout bounds of the content for which the location of the containing pop-up window is required.
	 * @param  locator
	 *           a function that returns a location that may be used to locate the pop-up.
	 * @return the location of a pop-up window for {@code contentBounds} and {@code locator}.
	 */

	Point2D getLocation(
		Bounds				contentBounds,
		IFunction0<Point2D>	locator);

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
