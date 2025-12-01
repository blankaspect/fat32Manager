/*====================================================================*\

Shapes.java

Class: factory methods for shapes.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.shape;

//----------------------------------------------------------------------


// IMPORTS


import java.util.EnumMap;
import java.util.Map;

import javafx.geometry.Bounds;

import javafx.scene.Group;

import javafx.scene.paint.Color;

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import javafx.scene.text.Font;

import uk.blankaspect.common.geometry.HDirection;
import uk.blankaspect.common.geometry.VHDirection;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: FACTORY METHODS FOR SHAPES


/**
 * This class contains factory methods for some {@linkplain Shape shapes}.
 */

public class Shapes
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The width and height of <i>arrowhead 01</i>. */
	private static final	double	ARROWHEAD01_SIZE	= 48.0;

	/** The coordinates of the vertices of <i>arrowhead 01 up</i>. */
	private static final	double[]	ARROWHEAD01_UP_COORDS	=
	{
		 10.0, 33.0,
		 24.0, 15.0,
		 38.0, 33.0
	};

	/** The coordinates of the vertices of <i>arrowhead 01 down</i>. */
	private static final	double[]	ARROWHEAD01_DOWN_COORDS	=
	{
		 10.0, 15.0,
		 24.0, 33.0,
		 38.0, 15.0
	};

	/** The coordinates of the vertices of <i>arrowhead 01 left</i>. */
	private static final	double[]	ARROWHEAD01_LEFT_COORDS	=
	{
		 15.0, 24.0,
		 33.0, 10.0,
		 33.0, 38.0
	};

	/** The coordinates of the vertices of <i>arrowhead 01 right</i>. */
	private static final	double[]	ARROWHEAD01_RIGHT_COORDS	=
	{
		 15.0, 10.0,
		 15.0, 38.0,
		 33.0, 24.0
	};

	/** A map of the coordinates of the vertices of <i>arrowhead 01</i>. */
	private static final	Map<VHDirection, double[]>	ARROWHEAD01_COORDS	= new EnumMap<>(Map.of
	(
		VHDirection.UP,    ARROWHEAD01_UP_COORDS,
		VHDirection.DOWN,  ARROWHEAD01_DOWN_COORDS,
		VHDirection.LEFT,  ARROWHEAD01_LEFT_COORDS,
		VHDirection.RIGHT, ARROWHEAD01_RIGHT_COORDS
	));

	/** The width and height of <i>plus 01</i> in relation to its {@linkplain #PLUS01_COORDS coordinates}. */
	private static final	double	PLUS01_SIZE	= 48.0;

	/** The stroke width of <i>plus 01</i>. */
	private static final	double	PLUS01_STROKE_WIDTH	= 6.0;

	/** The coordinates of the vertices of <i>plus 01</i>. */
	private static final	double[]	PLUS01_COORDS	=
	{
		24.0,  9.0,
		24.0, 39.0,
		 9.0, 24.0,
		39.0, 24.0
	};

	/** The width and height of <i>cross 01</i> in relation to its {@linkplain #CROSS01_COORDS coordinates}. */
	private static final	double	CROSS01_SIZE	= 48.0;

	/** The stroke width of <i>cross 01</i>. */
	private static final	double	CROSS01_STROKE_WIDTH	= 6.0;

	/** The coordinates of the vertices of <i>cross 01</i>. */
	private static final	double[]	CROSS01_COORDS	=
	{
		13.0, 13.0,
		35.0, 35.0,
		35.0, 13.0,
		13.0, 35.0
	};

	/** The width and height of <i>tick 01</i> in relation to its {@linkplain #TICK01_COORDS coordinates}. */
	private static final	double	TICK01_SIZE	= 48.0;

	/** The stroke width of <i>tick 01</i>. */
	private static final	double	TICK01_STROKE_WIDTH	= 6.0;

	/** The coordinates of the endpoints of the line segments of <i>cross 01</i>. */
	private static final	double[]	TICK01_COORDS	=
	{
		12.0, 26.0,
		20.0, 37.0,
		36.0, 11.0
	};

	/** The height of <i>angle 01</i>. */
	private static final	double	ANGLE01_HEIGHT	= 12.0;

	/** The factor by which the height of a specified font is multiplied to give the height of <i>angle 01</i>. */
	private static final	double	ANGLE01_FONT_HEIGHT_FACTOR	= 0.75;

	/** The stroke width of <i>angle 01</i>. */
	private static final	double	ANGLE01_STROKE_WIDTH	= 2.0;

	/** The difference between the <i>x</i> coordinates of adjacent vertices of <i>angle 01</i>. */
	private static final	double	ANGLE01_DELTA_X	= 4.0;

	/** The difference between the <i>y</i> coordinates of adjacent vertices of <i>angle 01</i>. */
	private static final	double	ANGLE01_DELTA_Y	= 4.0;

	/** The <i>y</i> coordinate of the topmost vertex of <i>angle 01</i>. */
	private static final	double	ANGLE01_Y1	= 2.0;

	/** The <i>x</i> coordinate of the leftmost vertex of a single left <i>angle 01</i>. */
	private static final	double	ANGLE01_SINGLE_LEFT_X1	= 9.0;

	/** The <i>x</i> coordinate of the leftmost vertex of a single right <i>angle 01</i>. */
	private static final	double	ANGLE01_SINGLE_RIGHT_X1	= 5.0;

	/** The <i>x</i> coordinate of the leftmost vertex of a double left <i>angle 01</i>. */
	private static final	double	ANGLE01_DOUBLE_LEFT_X1	= 6.0;

	/** The <i>x</i> coordinate of the leftmost vertex of a double right <i>angle 01</i>. */
	private static final	double	ANGLE01_DOUBLE_RIGHT_X1	= 2.0;

	/** The difference between the <i>x</i> coordinates of the corresponding vertices of the components of <i>angle
		01</i>. */
	private static final	double	ANGLE01_DOUBLE_DELTA_X	= 6.0;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private Shapes()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns an arrowhead shape that points in the specified direction and whose logical size is the
	 * height of the {@linkplain Font#getDefault() default font}.
	 *
	 * @param  direction
	 *           the direction of the arrowhead.
	 * @return an arrowhead shape that points in {@code direction} and whose logical size is the height of the default
	 *         font.
	 */

	public static Polygon arrowhead01(
		VHDirection	direction)
	{
		return arrowhead01(direction, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns an arrowhead shape that points in the specified direction and whose logical size is the
	 * height of the specified font.
	 *
	 * @param  direction
	 *           the direction of the arrowhead.
	 * @param  font
	 *           the font whose height will be used as the logical size of the arrowhead.
	 * @return an arrowhead shape that points in {@code direction} and whose logical size is the height of {@code font}.
	 */

	public static Polygon arrowhead01(
		VHDirection	direction,
		Font		font)
	{
		return arrowhead01(direction, TextUtils.textHeight(font));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns an arrowhead shape of the specified logical size that points in the specified direction.
	 *
	 * @param  direction
	 *           the direction of the arrowhead.
	 * @param  size
	 *           the logical size of the arrowhead.
	 * @return an arrowhead shape that points in {@code direction} and whose logical size is {@code size}.
	 */

	public static Polygon arrowhead01(
		VHDirection	direction,
		double		size)
	{
		// Scale coordinates of shape
		double factor = size / ARROWHEAD01_SIZE;
		double[] coords = ARROWHEAD01_COORDS.get(direction);
		double[] scaledCoords = new double[coords.length];
		for (int i = 0; i < scaledCoords.length; i++)
			scaledCoords[i] = coords[i] * factor;

		// Create and return shape
		return new Polygon(scaledCoords);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a plus-sign shape whose logical size is the height of the {@linkplain Font#getDefault()
	 * default font}.  The shape is formed from two line segments of equal length, one vertical and one horizontal, that
	 * intersect at their midpoints.
	 *
	 * @return a plus-sign shape whose logical size is the height of the default font.
	 */

	public static Path plus01()
	{
		return plus01(null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a plus-sign shape whose logical size is the height of the specified font.  The shape is
	 * formed from two line segments of equal length, one vertical and one horizontal, that intersect at their
	 * midpoints.
	 *
	 * @param  font
	 *           the font whose height will be used as the logical size of the shape.
	 * @return a plus-sign shape whose logical size is the height of {@code font}.
	 */

	public static Path plus01(
		Font	font)
	{
		return plus01(TextUtils.textHeight(font));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a plus-sign shape of the specified logical size.  The shape is formed from two line segments
	 * of equal length, one vertical and one horizontal, that intersect at their midpoints.
	 *
	 * @param  size
	 *           the logical size of the shape.
	 * @return a plus-sign shape whose logical size is {@code size}.
	 */

	public static Path plus01(
		double	size)
	{
		// Scale coordinates of shape
		double factor = size / PLUS01_SIZE;
		double[] coords = new double[PLUS01_COORDS.length];
		for (int i = 0; i < coords.length; i++)
			coords[i] = PLUS01_COORDS[i] * factor;

		// Create shape
		int index = 0;
		Path shape = new Path
		(
			new MoveTo(coords[index++], coords[index++]),
			new LineTo(coords[index++], coords[index++]),
			new MoveTo(coords[index++], coords[index++]),
			new LineTo(coords[index++], coords[index++])
		);
		shape.setStrokeWidth(PLUS01_STROKE_WIDTH * factor);
		shape.setStrokeLineCap(StrokeLineCap.BUTT);

		// Return shape
		return shape;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a cross shape whose logical size is the height of the {@linkplain Font#getDefault() default
	 * font}.  The cross is formed from two line segments of equal length that are the diagonals of a square whose sides
	 * are vertical and horizontal.
	 *
	 * @return a cross shape whose logical size is the height of the default font.
	 */

	public static Path cross01()
	{
		return cross01(null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a cross shape whose logical size is the height of the specified font.  The cross is formed
	 * from two line segments of equal length that are the diagonals of a square whose sides are vertical and
	 * horizontal.
	 *
	 * @param  font
	 *           the font whose height will be used as the logical size of the shape.
	 * @return a cross shape whose logical size is the height of {@code font}.
	 */

	public static Path cross01(
		Font	font)
	{
		return cross01(TextUtils.textHeight(font));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a cross shape of the specified logical size.  The cross is formed from two line segments of
	 * equal length that are the diagonals of a square whose sides are vertical and horizontal.
	 *
	 * @param  size
	 *           the logical size of the shape.
	 * @return a cross shape whose logical size is {@code size}.
	 */

	public static Path cross01(
		double	size)
	{
		// Scale coordinates of shape
		double factor = size / CROSS01_SIZE;
		double[] coords = new double[CROSS01_COORDS.length];
		for (int i = 0; i < coords.length; i++)
			coords[i] = CROSS01_COORDS[i] * factor;

		// Create shape
		int index = 0;
		Path shape = new Path
		(
			new MoveTo(coords[index++], coords[index++]),
			new LineTo(coords[index++], coords[index++]),
			new MoveTo(coords[index++], coords[index++]),
			new LineTo(coords[index++], coords[index++])
		);
		shape.setStrokeWidth(CROSS01_STROKE_WIDTH * factor);
		shape.setStrokeLineCap(StrokeLineCap.ROUND);

		// Return shape
		return shape;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a tick shape whose logical size is the height of the {@linkplain Font#getDefault() default
	 * font}.  The tick is formed from two line segments of unequal length that are joined at an endpoint.
	 *
	 * @return a tick shape whose logical size is the height of the default font.
	 */

	public static Polyline tick01()
	{
		return tick01(null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a tick shape whose logical size is the height of the specified font.  The tick is formed from
	 * two line segments of unequal length that are joined at an endpoint.
	 *
	 * @param  font
	 *           the font whose height will be used as the logical size of the tick.
	 * @return a tick shape whose logical size is the height of {@code font}.
	 */

	public static Polyline tick01(
		Font	font)
	{
		return tick01(TextUtils.textHeight(font));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a tick shape of the specified logical size.  The tick is formed from two line segments of
	 * unequal length that are joined at an endpoint.
	 *
	 * @param  size
	 *           the logical size of the tick.
	 * @return a tick shape whose logical size is {@code size}.
	 */

	public static Polyline tick01(
		double	size)
	{
		// Scale coordinates of shape
		double factor = size / TICK01_SIZE;
		double[] coords = new double[TICK01_COORDS.length];
		for (int i = 0; i < coords.length; i++)
			coords[i] = TICK01_COORDS[i] * factor;

		// Create shape
		Polyline shape = new Polyline(coords);
		shape.setStrokeWidth(TICK01_STROKE_WIDTH * factor);
		shape.setStrokeLineCap(StrokeLineCap.ROUND);
		shape.setStrokeLineJoin(StrokeLineJoin.ROUND);

		// Return shape
		return shape;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a shape consisting of a pair of joined line segments that point in the specified horizontal
	 * direction.  The shapes have a similar form to Unicode glyphs U+27E8 and U+27E9.  The height of the shape is the
	 * height of the {@linkplain Font#getDefault() default font} multiplied by a {@linkplain #ANGLE01_FONT_HEIGHT_FACTOR
	 * factor}.
	 *
	 * @param  direction
	 *           the horizontal direction in which the pair of joined line segments that constitute the shape point.
	 * @return a shape consisting of two joined line segments that point in the specified horizontal direction.
	 */

	public static Polyline angle01Single(
		HDirection	direction)
	{
		return angle01Single(direction, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a shape consisting of a pair of joined line segments that point in the specified horizontal
	 * direction.  The shapes have a similar form to Unicode glyphs U+27E8 and U+27E9.  The height of the shape is the
	 * height of the specified font multiplied by a {@linkplain #ANGLE01_FONT_HEIGHT_FACTOR factor}.
	 *
	 * @param  direction
	 *           the horizontal direction in which the pair of joined line segments that constitute the shape point.
	 * @param  font
	 *           the font whose height determines the height of the shape.
	 * @return a shape consisting of two joined line segments that point in the specified horizontal direction.
	 */

	public static Polyline angle01Single(
		HDirection	direction,
		Font		font)
	{
		return angle01Single(direction, TextUtils.textHeight(font) * ANGLE01_FONT_HEIGHT_FACTOR);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a shape of the specified height consisting of a pair of joined line segments that point in
	 * the specified horizontal direction.  The shapes have a similar form to Unicode glyphs U+27E8 and U+27E9.
	 *
	 * @param  direction
	 *           the horizontal direction in which the pair of joined line segments that constitute the shape point.
	 * @param  height
	 *           the height of the shape.
	 * @return a shape consisting of two joined line segments that point in the specified horizontal direction.
	 */

	public static Polyline angle01Single(
		HDirection	direction,
		double		height)
	{
		// Scale coordinates of shape
		double factor = height / ANGLE01_HEIGHT;
		double x1 = 0.0;
		double x2 = 0.0;
		switch (direction)
		{
			case LEFT:
			{
				x1 = ANGLE01_SINGLE_LEFT_X1 * factor;
				x2 = x1 - ANGLE01_DELTA_X * factor;
				break;
			}

			case RIGHT:
			{
				x1 = ANGLE01_SINGLE_RIGHT_X1 * factor;
				x2 = x1 + ANGLE01_DELTA_X * factor;
				break;
			}
		}
		double[] coords =
		{
			x1, ANGLE01_Y1 * factor,
			x2, (ANGLE01_Y1 + ANGLE01_DELTA_Y) * factor,
			x1, (ANGLE01_Y1 + 2.0 * ANGLE01_DELTA_Y) * factor
		};

		// Create shape
		Polyline shape = new Polyline(coords);
		shape.setStrokeWidth(ANGLE01_STROKE_WIDTH * factor);
		shape.setStrokeLineCap(StrokeLineCap.ROUND);

		// Return shape
		return shape;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a shape formed of two components separated horizontally, each component consisting of a pair
	 * of joined line segments that point in the specified horizontal direction.  The shapes have a similar form to
	 * Unicode glyphs U+27EA and U+27EB.  The height of the shape is the height of the {@linkplain Font#getDefault()
	 * default font} multiplied by a {@linkplain #ANGLE01_FONT_HEIGHT_FACTOR factor}.
	 *
	 * @param  direction
	 *           the horizontal direction in which the two pairs of joined line segments that constitute the shape
	 *           point.
	 * @return a shape consisting of two pairs of joined line segments that point in the specified horizontal direction.
	 */

	public static Path angle01Double(
		HDirection	direction)
	{
		return angle01Double(direction, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a shape formed of two components separated horizontally, each component consisting of a pair
	 * of joined line segments that point in the specified horizontal direction.  The shapes have a similar form to
	 * Unicode glyphs U+27EA and U+27EB.  The height of the shape is the height of the specified font multiplied by a
	 * {@linkplain #ANGLE01_FONT_HEIGHT_FACTOR factor}.
	 *
	 * @param  direction
	 *           the horizontal direction in which the two pairs of joined line segments that constitute the shape
	 *           point.
	 * @param  font
	 *           the font whose height determines the height of the shape.
	 * @return a shape consisting of two pairs of joined line segments that point in the specified horizontal direction.
	 */

	public static Path angle01Double(
		HDirection	direction,
		Font		font)
	{
		return angle01Double(direction, TextUtils.textHeight(font) * ANGLE01_FONT_HEIGHT_FACTOR);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a shape of the specified height formed of two components separated horizontally, each
	 * component consisting of a pair of joined line segments that point in the specified horizontal direction.  The
	 * shapes have a similar form to Unicode glyphs U+27EA and U+27EB.
	 *
	 * @param  direction
	 *           the horizontal direction in which the two pairs of joined line segments that constitute the shape
	 *           point.
	 * @param  height
	 *           the height of the shape.
	 * @return a shape consisting of two pairs of joined line segments that point in the specified horizontal direction.
	 */

	public static Path angle01Double(
		HDirection	direction,
		double		height)
	{
		// Scale coordinates of shape
		double factor = height / ANGLE01_HEIGHT;
		double x1 = 0.0;
		double x2 = 0.0;
		switch (direction)
		{
			case LEFT:
			{
				x1 = ANGLE01_DOUBLE_LEFT_X1 * factor;
				x2 = x1 - ANGLE01_DELTA_X * factor;
				break;
			}

			case RIGHT:
			{
				x1 = ANGLE01_DOUBLE_RIGHT_X1 * factor;
				x2 = x1 + ANGLE01_DELTA_X * factor;
				break;
			}
		}

		// Create shape
		Path shape = new Path();
		for (int i = 0; i < 2; i++)
		{
			shape.getElements().add(new MoveTo(x1, ANGLE01_Y1 * factor));
			shape.getElements().add(new LineTo(x2, (ANGLE01_Y1 + ANGLE01_DELTA_Y) * factor));
			shape.getElements().add(new LineTo(x1, (ANGLE01_Y1 + 2.0 * ANGLE01_DELTA_Y) * factor));

			x1 += ANGLE01_DOUBLE_DELTA_X * factor;
			x2 += ANGLE01_DOUBLE_DELTA_X * factor;
		}
		shape.setStrokeWidth(ANGLE01_STROKE_WIDTH * factor);
		shape.setStrokeLineCap(StrokeLineCap.ROUND);

		// Return shape
		return shape;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a <i>tile</i> for the specified shape.  A tile is a {@link Group} that contains a transparent
	 * {@link Rectangle} and, above it, the specified shape.  Each dimensions of the rectangle is the minimum integral
	 * value that is greater than or equal to the corresponding dimension of the shape.
	 *
	 * @param  shape
	 *           the shape that will be the upper component of the tile.
	 * @return a {@link Group} that contains a transparent bounding {@link Rectangle} below {@code shape}.
	 */

	public static Group tile(
		Shape	shape)
	{
		return tile(shape, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a <i>tile</i> for the specified shape.  A tile is a {@link Group} that contains a transparent
	 * {@link Rectangle} and, above it, the specified shape.  If the {@code square} option is specified, the width and
	 * height of the rectangle are the minimum integral value that is greater than or equal to the larger dimension of
	 * the shape; otherwise, each dimensions of the rectangle is the minimum integral value that is greater than or
	 * equal to the corresponding dimension of the shape.
	 *
	 * @param  shape
	 *           the shape that will be the upper component of the tile.
	 * @param  square
	 *           if {@code true}, the width and height of the {@code Rectangle} that determines the dimensions of the
	 *           tile will be equal.
	 * @return a {@link Group} that contains a transparent bounding {@link Rectangle} below {@code shape}.
	 */

	public static Group tile(
		Shape	shape,
		boolean	square)
	{
		// Get dimensions of shape
		double shapeWidth = shape.getLayoutBounds().getWidth();
		double shapeHeight = shape.getLayoutBounds().getHeight();

		// Calculate dimensions of bounding rectangle
		double width = Math.ceil(shapeWidth);
		double height = Math.ceil(shapeHeight);

		// If square tile was requested, equalise dimensions
		if (square)
			width = height = Math.max(width, height);

		// Centre shape in bounding rectangle
		shape.relocate(0.5 * (width - shapeWidth), 0.5 * (height - shapeHeight));

		// Create and return tile
		return new Group(new Rectangle(width, height, Color.TRANSPARENT), shape);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a <i>tile</i> for the specified shape.  A tile is a {@link Group} that contains a transparent
	 * {@link Rectangle} of the specified size (width and height) and, above it, the specified shape.
	 *
	 * @param  shape
	 *           the shape that will be the upper component of the tile.
	 * @param  size
	 *           the width and height of the {@code Rectangle} that is the lower component of the tile.
	 * @return a {@link Group} that contains a transparent {@link Rectangle} whose width and height are {@code size},
	 *         with {@code shape} above it.
	 */

	public static Group tile(
		Shape	shape,
		double	size)
	{
		return tile(shape, size, size);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a <i>tile</i> for the specified shape.  A tile is a {@link Group} that contains a transparent
	 * {@link Rectangle} of the specified width and height and, above it, the specified shape.
	 *
	 * @param  shape
	 *           the shape that will be the upper component of the tile.
	 * @param  width
	 *           the width of the {@code Rectangle} that is the lower component of the tile.
	 * @param  height
	 *           the height of the {@code Rectangle} that is the lower component of the tile.
	 * @return a {@link Group} that contains a transparent {@link Rectangle} whose dimensions are {@code width} and
	 *         {@code height}, with {@code shape} above it.
	 */

	public static Group tile(
		Shape	shape,
		double	width,
		double	height)
	{
		// Get bounds of shape
		Bounds bounds = shape.getLayoutBounds();

		// Centre shape in bounding rectangle
		shape.relocate(0.5 * (width - bounds.getWidth()), 0.5 * (height - bounds.getHeight()));

		// Create and return tile
		return new Group(new Rectangle(width, height, Color.TRANSPARENT), shape);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
