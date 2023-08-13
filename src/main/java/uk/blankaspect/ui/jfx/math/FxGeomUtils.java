/*====================================================================*\

FxGeomUtils.java

Class: JavaFX geometry-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.math;

//----------------------------------------------------------------------


// IMPORTS


import java.util.stream.Stream;

import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;

//----------------------------------------------------------------------


// CLASS: JAVAFX GEOMETRY-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX geometry.
 */

public class FxGeomUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private FxGeomUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Converts the specified polar coordinates (radius and angle) to Cartesian coordinates (<i>x</i> and <i>y</i>) and
	 * returns the result.
	 *
	 * @param  radius
	 *           the radial coordinate.
	 * @param  angle
	 *           the angular coordinate.
	 * @return a new instance of a {@linkplain Point2D point} in the Cartesian coordinate system that corresponds to the
	 *         input polar coordinates.
	 */

	public static Point2D polarToCartesian(double radius,
										   double angle)
	{
		return new Point2D(radius * Math.cos(angle), radius * Math.sin(angle));
	}

	//------------------------------------------------------------------

	/**
	 * Returns the combined vertical and horizontal position that corresponds to the specified vertical and horizontal
	 * alignments.
	 *
	 * @param  verticalAlignment
	 *           the vertical alignment.
	 * @param  horizontalAlignment
	 *           the horizontal alignment.
	 * @return the combined vertical and horizontal position that corresponds to <i>verticalAlignment</i> and
	 *         <i>horizontalAlignment</i>.
	 */

	public static Pos getPos(VPos verticalAlignment,
							 HPos horizontalAlignment)
	{
		return Stream.of(Pos.values())
						.filter(pos -> (pos.getVpos() == verticalAlignment) && (pos.getHpos() == horizontalAlignment))
						.findFirst()
						.orElse(null);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
