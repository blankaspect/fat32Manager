/*====================================================================*\

Icons.java

Class: factory methods for icons.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.icon;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.Group;

import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

import uk.blankaspect.ui.jfx.style.StyleConstants;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: FACTORY METHODS FOR ICONS


/**
 * This class contains factory methods for icons.
 */

public class Icons
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The factor by which the height of the default font is multiplied to give the default size of the <i>clear01</i>
		icon. */
	public static final		double	CLEAR01_DEFAULT_SIZE_FACTOR	= 0.8;

	/** The factor by which the size of the <i>clear01</i> icon is multiplied to give the radius of the background
		disc. */
	private static final	double	CLEAR01_RADIUS_FACTOR	= 0.5;

	/** The factor by which the size of the <i>clear01</i> icon is multiplied to give a coordinate of the cross of the
		icon. */
	private static final	double	CLEAR01_CROSS_SIZE_FACTOR	= 0.4 * CLEAR01_RADIUS_FACTOR;

	/** The factor by which the size of the <i>clear01</i> icon is multiplied to give the width of the strokes of the
		cross. */
	private static final	double	CLEAR01_CROSS_STROKE_WIDTH_FACTOR	= 0.3 * CLEAR01_RADIUS_FACTOR;

	/** The factor by which the height of the default font is multiplied to give the default height of the
		<i>clear02</i> icon. */
	public static final		double	CLEAR02_DEFAULT_HEIGHT_FACTOR	= 0.8;

	/** The factor by which the height of the <i>clear02</i> icon is multiplied to give the <i>a</i> dimension. */
	private static final	double	CLEAR02_A_FACTOR	= 0.5;

	/** The factor by which the height of the <i>clear02</i> icon is multiplied to give the <i>b</i> dimension. */
	private static final	double	CLEAR02_B_FACTOR	= 0.85;

	/** The factor by which the height of the <i>clear02</i> icon is multiplied to give the <i>d</i> dimension. */
	private static final	double	CLEAR02_D_FACTOR	= 0.7 * CLEAR02_A_FACTOR;

	/** The factor by which the height of the <i>clear02</i> icon is multiplied to give a coordinate of the cross of the
		icon. */
	private static final	double	CLEAR02_CROSS_SIZE_FACTOR	= 0.4 * CLEAR02_A_FACTOR;

	/** The factor by which the height of the <i>clear02</i> icon is multiplied to give the width of the strokes of the
		cross. */
	private static final	double	CLEAR02_CROSS_STROKE_WIDTH_FACTOR	= 0.3 * CLEAR02_A_FACTOR;

	/** CSS style classes. */
	public interface StyleClass
	{
		String	CLEAR01_BACKGROUND	= StyleConstants.CLASS_PREFIX + "icons-clear01-background";
		String	CLEAR01_FOREGROUND	= StyleConstants.CLASS_PREFIX + "icons-clear01-foreground";
		String	CLEAR02_BACKGROUND	= StyleConstants.CLASS_PREFIX + "icons-clear02-background";
		String	CLEAR02_FOREGROUND	= StyleConstants.CLASS_PREFIX + "icons-clear02-foreground";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private Icons()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Group clear01(
		Color	backgroundColour,
		Color	foregroundColour)
	{
		return clear01(backgroundColour, foregroundColour, CLEAR01_DEFAULT_SIZE_FACTOR, CLEAR01_DEFAULT_SIZE_FACTOR);
	}

	//------------------------------------------------------------------

	public static Group clear01(
		Color	backgroundColour,
		Color	foregroundColour,
		double	sizeFactor)
	{
		return clear01(backgroundColour, foregroundColour, sizeFactor, sizeFactor);
	}

	//------------------------------------------------------------------

	public static Group clear01(
		Color	backgroundColour,
		Color	foregroundColour,
		double	sizeFactor,
		double	tileSizeFactor)
	{
		// Validate arguments
		if (sizeFactor > tileSizeFactor)
			throw new IllegalArgumentException("Size factor is greater than tile-size factor");

		// Calculate size of icon from height of default font
		double textHeight = TextUtils.textHeight();
		double size = sizeFactor * textHeight;

		// Create disc
		double radius = CLEAR01_RADIUS_FACTOR * size;
		Circle disc = new Circle(radius, backgroundColour);
		disc.getStyleClass().add(StyleClass.CLEAR01_BACKGROUND);

		// Create cross
		double coord = CLEAR01_CROSS_SIZE_FACTOR * size;
		Path cross = new Path
		(
			new MoveTo(-coord, -coord),
			new LineTo(coord, coord),
			new MoveTo(-coord, coord),
			new LineTo(coord, -coord)
		);
		cross.setStroke(foregroundColour);
		cross.setStrokeWidth(CLEAR01_CROSS_STROKE_WIDTH_FACTOR * size);
		cross.setStrokeLineCap(StrokeLineCap.ROUND);
		cross.getStyleClass().add(StyleClass.CLEAR01_FOREGROUND);

		// Create background rectangle
		double tileSize = Math.ceil(tileSizeFactor * textHeight);
		double tileOffset = -0.5 * tileSize;
		Rectangle background = new Rectangle(tileOffset, tileOffset, tileSize, tileSize);
		background.setFill(Color.TRANSPARENT);

		// Create and return icon
		return new Group(background, disc, cross);
	}

	//------------------------------------------------------------------

	public static Group clear02(
		Color	backgroundColour,
		Color	foregroundColour)
	{
		return clear02(backgroundColour, foregroundColour, CLEAR02_DEFAULT_HEIGHT_FACTOR,
					   CLEAR02_DEFAULT_HEIGHT_FACTOR);
	}

	//------------------------------------------------------------------

	public static Group clear02(
		Color	backgroundColour,
		Color	foregroundColour,
		double	sizeFactor)
	{
		return clear02(backgroundColour, foregroundColour, sizeFactor, sizeFactor);
	}

	//------------------------------------------------------------------

	public static Group clear02(
		Color	backgroundColour,
		Color	foregroundColour,
		double	heightFactor,
		double	tileHeightFactor)
	{
		// Validate arguments
		if (heightFactor > tileHeightFactor)
			throw new IllegalArgumentException("Height factor is greater than tile-height factor");

		// Calculate height of icon from height of default font
		double textHeight = TextUtils.textHeight();
		double height = heightFactor * textHeight;

		// Create polygon
		double a = CLEAR02_A_FACTOR * height;
		double b = CLEAR02_B_FACTOR * height;
		double c = b - a;
		double d = CLEAR02_D_FACTOR * height;
		double x1 = -(c + d);
		double x2 = -c;
		double x3 = a;
		double y1 = -a;
		double y2 = 0.0;
		double y3 = a;
		Polygon polygon = new Polygon(x1, y2, x2, y1, x3, y1, x3, y3, x2, y3);
		polygon.getStyleClass().add(StyleClass.CLEAR02_BACKGROUND);

		// Create cross
		double coord = CLEAR02_CROSS_SIZE_FACTOR * height;
		Path cross = new Path
		(
			new MoveTo(-coord, -coord),
			new LineTo(coord, coord),
			new MoveTo(-coord, coord),
			new LineTo(coord, -coord)
		);
		cross.setStroke(foregroundColour);
		cross.setStrokeWidth(CLEAR02_CROSS_STROKE_WIDTH_FACTOR * height);
		cross.setStrokeLineCap(StrokeLineCap.ROUND);
		cross.getStyleClass().add(StyleClass.CLEAR02_FOREGROUND);

		// Create background rectangle
		double h = tileHeightFactor * textHeight;
		double tileHeight = Math.ceil(h);
		double tileWidth = Math.ceil(h / (2.0 * a) * (b + d));
		double tileYOffset = -0.5 * tileHeight;
		double tileXOffset = -tileWidth - tileYOffset;
		Rectangle background = new Rectangle(tileXOffset, tileYOffset, tileWidth, tileHeight);
		background.setFill(Color.TRANSPARENT);

		// Create and return icon
		return new Group(background, polygon, cross);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
