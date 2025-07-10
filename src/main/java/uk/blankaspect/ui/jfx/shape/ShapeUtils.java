/*====================================================================*\

ShapeUtils.java

Class: shape-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.shape;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

//----------------------------------------------------------------------


// CLASS: SHAPE-RELATED UTILITY METHODS


/**
 * This class contains utility methods related to {@linkplain Shape shapes}.
 */

public class ShapeUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The colour of the first part of a two-part focus-indicator border. */
	private static final	Color	FOCUS_BORDER_COLOUR1	= Color.WHITE;

	/** The colour of the second part of a two-part focus-indicator border. */
	private static final	Color	FOCUS_BORDER_COLOUR2	= Color.BLACK;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ShapeUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void applyDash(
		Shape	shape,
		double	dashA,
		double	dashB,
		double	x1,
		double	y1,
		double	x2,
		double	y2)
	{
		shape.getStrokeDashArray().setAll(dashA, dashB);
		shape.setStrokeDashOffset(calculateDashOffset(dashA, dashB, x2 - x1, y2 - y1));
	}

	//------------------------------------------------------------------

	public static double calculateDashOffset(
		double	dashA,
		double	dashB,
		double	dx,
		double	dy)
	{
		final	double	ROUNDING_FACTOR	= 1000.0;

		double rem = Math.sqrt(dx * dx + dy * dy) % (dashA + dashB);
		double offset = Math.rint(0.5 * (dashA - rem) * ROUNDING_FACTOR) / ROUNDING_FACTOR;
		if (offset < 0.0)
			offset += 0.5 * (dashA + dashB);
		return offset;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a pair of rectangles of the specified dimensions.  Both rectangles have a transparent fill.
	 * The first rectangle has a solid white one-pixel-wide border; the second rectangle has a dashed black
	 * one-pixel-wide border.  The rectangles may be placed under a component to indicate that it has keyboard focus.
	 *
	 * @param  width
	 *           the width of the rectangles.
	 * @param  height
	 *           the height of the rectangles.
	 * @return a list of two rectangles.  The coordinates of the top left corner of each rectangle are (0, 0) and the
	 *         dimensions of each rectangle are {@code width} and {@code height}.  The fills and borders of the
	 *         rectangles are described above.
	 */

	public static List<Rectangle> createFocusBorder(
		double	width,
		double	height)
	{
		return createFocusBorder(width, height, 0.0);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a pair of rectangles whose dimensions are the specified values adjusted by the specified
	 * inset as described below.  Both rectangles have a transparent fill.  The first rectangle has a solid white
	 * one-pixel-wide border; the second rectangle has a dashed black one-pixel-wide border.  The rectangles may be
	 * placed under a component to indicate that it has keyboard focus.
	 *
	 * @param  width
	 *           the width of the rectangles before adjusting for {@code inset}.
	 * @param  height
	 *           the height of the rectangles before adjusting for {@code inset}.
	 * @param  inset
	 *           the amount by which the rectangles are inset from the specified dimensions.  The effect of the inset is
	 *           as follows:
	 *           <ul>
	 *             <li>
	 *               If {@code inset} is zero, the coordinates of the top left corner of each rectangle are (0, 0), and
	 *               the width and height of each rectangle are the specified values.
	 *             </li>
	 *             <li>
	 *               If {@code inset} is non-zero, the coordinates of the top left corner of each rectangle are ({@code
	 *               inset}, {@code inset}), and the width and height of each rectangle are the specified values minus
	 *               2 &times; {@code inset}.
	 *             </li>
	 *           </ul>
	 * @return a list of two rectangles.  The locations, dimensions, fills and borders of the rectangles are described
	 *         above.
	 */

	public static List<Rectangle> createFocusBorder(
		double	width,
		double	height,
		double	inset)
	{
		List<Rectangle> borders = new ArrayList<>();
		for (int i = 0; i < 2; i++)
		{
			Rectangle border = new Rectangle(width - 2.0 * inset, height - 2.0 * inset, Color.TRANSPARENT);
			border.setStroke((i == 0) ? FOCUS_BORDER_COLOUR1 : FOCUS_BORDER_COLOUR2);
			border.setStrokeType(StrokeType.INSIDE);
			border.setStrokeLineCap(StrokeLineCap.BUTT);
			if (i == 1)
				border.getStrokeDashArray().addAll(1.0, 1.0);
			if (inset != 0.0)
				border.relocate(inset, inset);
			borders.add(border);
		}
		return borders;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
