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

	/** The factor by which the default text height is multiplied to give the width and height of the <i>clear01</i>
		icon. */
	private static final	double	CLEAR01_DEFAULT_SIZE_FACTOR	= 0.8;

	/** The factor by which the radius of the disc of the <i>clear01</i> icon is multiplied to give a coordinate of
		the cross of the icon. */
	private static final	double	CLEAR01_CROSS_SIZE_FACTOR	= 0.4;

	/** The factor by which the radius of the disc of the <i>clear01</i> icon is multiplied to give the width of the
		strokes of the cross. */
	private static final	double	CLEAR01_CROSS_STROKE_WIDTH_FACTOR	= 0.3;

	/** CSS style classes. */
	public interface StyleClass
	{
		String	CLEAR01_CROSS	= StyleConstants.CLASS_PREFIX + "icons-clear01-cross";
		String	CLEAR01_DISC	= StyleConstants.CLASS_PREFIX + "icons-clear01-disc";
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
		Color	discColour,
		Color	crossColour)
	{
		return clear01(discColour, crossColour, CLEAR01_DEFAULT_SIZE_FACTOR);
	}

	//------------------------------------------------------------------

	public static Group clear01(
		Color	discColour,
		Color	crossColour,
		double	sizeFactor)
	{
		// Calculate size of icon from height of default font
		double size = sizeFactor * TextUtils.textHeight();

		// Create disc
		double radius = 0.5 * size;
		Circle disc = new Circle(radius, discColour);
		disc.getStyleClass().add(StyleClass.CLEAR01_DISC);

		// Create cross
		double coord = CLEAR01_CROSS_SIZE_FACTOR * radius;
		Path cross = new Path
		(
			new MoveTo(-coord, -coord),
			new LineTo(coord, coord),
			new MoveTo(-coord, coord),
			new LineTo(coord, -coord)
		);
		cross.setStroke(crossColour);
		cross.setStrokeWidth(radius * CLEAR01_CROSS_STROKE_WIDTH_FACTOR);
		cross.setStrokeLineCap(StrokeLineCap.ROUND);
		cross.getStyleClass().add(StyleClass.CLEAR01_CROSS);

		// Create background rectangle
		double tileSize = Math.ceil(size);
		double tileOffset = -0.5 * tileSize;
		Rectangle background = new Rectangle(tileOffset, tileOffset, tileSize, tileSize);
		background.setFill(Color.TRANSPARENT);

		// Create and return icon
		return new Group(background, disc, cross);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
