/*====================================================================*\

HatchedImageFactory.java

Class: factory for creating hatched JavaFX images.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.image;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.image.Image;

import javafx.scene.paint.Color;

import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

//----------------------------------------------------------------------


// CLASS: FACTORY FOR CREATING HATCHED JAVAFX IMAGES


/**
 * This class provides methods for creating images that contain repeating patterns of regularly spaced lines.
 */

public class HatchedImageFactory
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private HatchedImageFactory()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an image that contains a repeating pattern of regularly spaced diagonal lines of alternating colours.
	 * The lines are at an angle of &pi;/2 anticlockwise from the horizontal, and successive lines of the same colour
	 * are separated by the specified (horizontal and vertical) distance.
	 *
	 * @param  width
	 *           the width of the image.
	 * @param  height
	 *           the height of the image.
	 * @param  spacing
	 *           the (horizontal and vertical) distance between successive lines of the same colour.
	 * @param  colour
	 *           the first colour of the diagonal lines.
	 * @param  brightnessDelta
	 *           the value that will be added to the brightness component of {@code colour} to yield the second colour
	 *           of the diagonal lines.
	 * @return an image that contains a repeating pattern of regularly spaced diagonal lines of alternating colours.
	 */

	public static Image diagonal(
		int		width,
		int		height,
		double	spacing,
		Color	colour,
		double	brightnessDelta)
	{
		return diagonal(width, height, spacing, colour, ColourUtils.shiftBrightness(colour, brightnessDelta));
	}

	//------------------------------------------------------------------

	/**
	 * Returns an image that contains a repeating pattern of regularly spaced diagonal lines of the specified colours,
	 * alternating.  The lines are at an angle of &pi;/2 anticlockwise from the horizontal, and successive lines of the
	 * same colour are separated by the specified (horizontal and vertical) distance.
	 *
	 * @param  width
	 *           the width of the image.
	 * @param  height
	 *           the height of the image.
	 * @param  spacing
	 *           the (horizontal and vertical) distance between successive lines of the same colour.
	 * @param  colour1
	 *           the first colour of the diagonal lines.
	 * @param  colour2
	 *           the second colour of the diagonal lines.
	 * @return an image that contains a repeating pattern of regularly spaced diagonal lines of alternating colours,
	 *         {@code colour1} and {@code colour2}.
	 */

	public static Image diagonal(
		int		width,
		int		height,
		double	spacing,
		Color	colour1,
		Color	colour2)
	{
		// Create group; add background rectangle to group
		Group group = new Group(new Rectangle((double)width, (double)height, colour1));

		// Create diagonal lines and add them to group
		double lineWidth = spacing / Math.sqrt(2.0);
		int numSteps = (int)Math.ceil((double)Math.max(width, height) / spacing);
		for (int i = 0; i < numSteps; i++)
		{
			double x = ((double)i * 2.0 + 1.0) * spacing;

			Line line = new Line(x, 0.0, 0.0, x);
			line.setStrokeWidth(lineWidth);
			line.setStroke(colour2);
			group.getChildren().add(line);
		}

		// Clip group to background rectangle
		group.setClip(new Rectangle((double)width, (double)height));

		// Add group to temporary scene for snapshot
		new Scene(group);

		// Create snapshot of group and return it
		return group.snapshot(null, null);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
