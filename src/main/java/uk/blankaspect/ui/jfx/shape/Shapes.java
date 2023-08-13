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
import javafx.scene.Node;

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
	private static final	Map<VHDirection, double[]>	ARROWHEAD01_COORDS;

	/** The width and height of <i>cross 01</i>. */
	private static final	double	CROSS01_SIZE	= 48.0;

	/** The stroke width of <i>cross 01</i>. */
	private static final	double	CROSS01_STROKE_WIDTH	= 6.0;

	/** The coordinates of the vertices of <i>cross 01</i>. */
	private static final	double[]	CROSS01_COORDS	=
	{
		 5.0,  5.0,
		27.0, 27.0,
		27.0,  5.0,
		 5.0, 27.0
	};

	/** The width and height of <i>tick 01</i>. */
	private static final	double	TICK01_SIZE	= 48.0;

	/** The stroke width of <i>tick 01</i>. */
	private static final	double	TICK01_STROKE_WIDTH	= 6.0;

	/** The coordinates of the endpoints of the line segments of <i>cross 01</i>. */
	private static final	double[]	TICK01_COORDS	=
	{
		 4.0, 18.0,
		12.0, 29.0,
		28.0,  3.0
	};

	/** The height of <i>angle 01</i>. */
	private static final	double	ANGLE01_HEIGHT	= 12.0;

//XXX
	private static final	double	ANGLE01_FONT_HEIGHT_FACTOR	= 0.75;

	/** The stroke width of <i>angle 01</i>. */
	private static final	double	ANGLE01_STROKE_WIDTH	= 2.0;

//XXX
	private static final	double	ANGLE01_DELTA_X	= 4.0;
	private static final	double	ANGLE01_DELTA_Y	= 4.0;
	private static final	double	ANGLE01_Y1		= 2.0;

	private static final	double	ANGLE01_SINGLE_LEFT_X1	= 9.0;
	private static final	double	ANGLE01_SINGLE_RIGHT_X1	= 5.0;

	private static final	double	ANGLE01_DOUBLE_LEFT_X1	= 6.0;
	private static final	double	ANGLE01_DOUBLE_RIGHT_X1	= 2.0;
	private static final	double	ANGLE01_DOUBLE_DELTA_X	= 6.0;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		ARROWHEAD01_COORDS = new EnumMap<>(VHDirection.class);
		ARROWHEAD01_COORDS.put(VHDirection.UP,    ARROWHEAD01_UP_COORDS);
		ARROWHEAD01_COORDS.put(VHDirection.DOWN,  ARROWHEAD01_DOWN_COORDS);
		ARROWHEAD01_COORDS.put(VHDirection.LEFT,  ARROWHEAD01_LEFT_COORDS);
		ARROWHEAD01_COORDS.put(VHDirection.RIGHT, ARROWHEAD01_RIGHT_COORDS);
	}

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
	 *           the font whose height will be used as the logical size of the cross.
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
	 *           the logical size of the cross.
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

//XXX
	public static Polyline angle01Single(
		HDirection	direction)
	{
		return angle01Single(direction, null);
	}

	//------------------------------------------------------------------

//XXX
	public static Polyline angle01Single(
		HDirection	direction,
		Font		font)
	{
		return angle01Single(direction, TextUtils.textHeight(font) * ANGLE01_FONT_HEIGHT_FACTOR);
	}

	//------------------------------------------------------------------

//XXX
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

//XXX
	public static Path angle01Double(
		HDirection	direction)
	{
		return angle01Double(direction, null);
	}

	//------------------------------------------------------------------

//XXX
	public static Path angle01Double(
		HDirection	direction,
		Font		font)
	{
		return angle01Double(direction, TextUtils.textHeight(font) * ANGLE01_FONT_HEIGHT_FACTOR);
	}

	//------------------------------------------------------------------

//XXX
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
	 * Creates and returns a <i>tile</i> for the specified node.  A tile is a {@link Group} that contains a transparent
	 * {@link Rectangle} and, above it, the specified node.  Each dimensions of the rectangle is the minimum integer
	 * value that is greater than or equal to the corresponding dimension of the node.
	 *
	 * @param  node
	 *           the node that will be the upper component of the tile.
	 * @return a {@link Group} that contains a transparent bounding {@link Rectangle} below {@code node}.
	 */

	public static Group tile(
		Node	node)
	{
		return tile(node, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a <i>tile</i> for the specified node.  A tile is a {@link Group} that contains a transparent
	 * {@link Rectangle} and, above it, the specified node.  If the {@code square} option is specified, the width and
	 * height of the rectangle are the minimum integer value that is greater than or equal to the larger dimension of
	 * the node; otherwise, each dimensions of the rectangle is the minimum integer value that is greater than or equal
	 * to the corresponding dimension of the node.
	 *
	 * @param  node
	 *           the node that will be the upper component of the tile.
	 * @param  square
	 *           if {@code true}, the width and height of the {@code Rectangle} that determines the dimensions of the
	 *           tile will be equal.
	 * @return a {@link Group} that contains a transparent bounding {@link Rectangle} below {@code node}.
	 */

	public static Group tile(
		Node	node,
		boolean	square)
	{
		// Get dimensions of node
		double nodeWidth = node.getLayoutBounds().getWidth();
		double nodeHeight = node.getLayoutBounds().getHeight();

		// Calculate dimensions of bounding rectangle
		double width = Math.ceil(nodeWidth);
		double height = Math.ceil(nodeHeight);

		// If square tile was requested, equalise dimensions
		if (square)
			width = height = Math.max(width, height);

		// Centre node in bounding rectangle
		node.relocate(0.5 * (width - nodeWidth), 0.5 * (height - nodeHeight));

		// Create and return tile
		return new Group(new Rectangle(width, height, Color.TRANSPARENT), node);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a <i>tile</i> for the specified node.  A tile is a {@link Group} that contains a transparent
	 * {@link Rectangle} of the specified size (width and height) and, above it, the specified node.
	 *
	 * @param  node
	 *           the node that will be the upper component of the tile.
	 * @param  size
	 *           the width and height of the {@code Rectangle} that is the lower component of the tile.
	 * @return a {@link Group} that contains a transparent {@link Rectangle} whose width and height are {@code size},
	 *         with {@code node} above it.
	 */

	public static Group tile(
		Node	node,
		double	size)
	{
		return tile(node, size, size);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a <i>tile</i> for the specified node.  A tile is a {@link Group} that contains a transparent
	 * {@link Rectangle} of the specified width and height and, above it, the specified node.
	 *
	 * @param  node
	 *           the node that will be the upper component of the tile.
	 * @param  width
	 *           the width of the {@code Rectangle} that is the lower component of the tile.
	 * @param  height
	 *           the height of the {@code Rectangle} that is the lower component of the tile.
	 * @return a {@link Group} that contains a transparent {@link Rectangle} whose dimensions are {@code width} and
	 *         {@code height}, with {@code node} above it.
	 */

	public static Group tile(
		Node	node,
		double	width,
		double	height)
	{
		// Get bounds of node
		Bounds bounds = node.getLayoutBounds();

		// Centre node in bounding rectangle
		node.relocate(0.5 * (width - bounds.getWidth()), 0.5 * (height - bounds.getHeight()));

		// Create and return tile
		return new Group(new Rectangle(width, height, Color.TRANSPARENT), node);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
