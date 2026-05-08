/*====================================================================*\

ImageUtils.java

Class: image-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.image;

//----------------------------------------------------------------------


// IMPORTS


import java.io.ByteArrayInputStream;

import java.util.List;

import java.util.function.UnaryOperator;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.scene.paint.Color;

import uk.blankaspect.common.collection.ArraySet;

//----------------------------------------------------------------------


// CLASS: IMAGE-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain Image images}.
 */

public class ImageUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ImageUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an {@linkplain ImageView image view} for the specified image, sets the <i>smooth</i> property of the
	 * image view and returns the image view.
	 *
	 * @param  image
	 *           the image for which an image view will be created.
	 * @return an image view for {@code image}, with its <i>smooth</i> property set.
	 */

	public static ImageView smoothImageView(
		Image	image)
	{
		ImageView imageView = new ImageView(image);
		imageView.setSmooth(true);
		return imageView;
	}

	//------------------------------------------------------------------

	/**
	 * Creates an {@linkplain ImageView image view} for an image that is created from the specified byte data, sets the
	 * <i>smooth</i> property of the image view and returns the image view.
	 *
	 * @param  data
	 *           the image data for which an image view will be created.
	 * @return an image view for the image that is created from {@code data}, with its <i>smooth</i> property set.
	 */

	public static ImageView smoothImageView(
		byte[]	data)
	{
		return smoothImageView(new Image(new ByteArrayInputStream(data)));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the hues of the pixels of the specified image, sorted in ascending order.
	 * @param  image
	 *           the image whose hues are desired.
	 * @return a list of the hues of the pixels of {@code image}, sorted in ascending order.
	 */

	public static List<Double> getHues(
		Image	image)
	{
		// Initialise list of hues
		List<Double> hues = new ArraySet<>();

		// Get dimensions of input image
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();

		// Get hues of pixels of image
		PixelReader reader = image.getPixelReader();
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
				hues.add(reader.getColor(x, y).getHue());
		}

		// Sort list of hues
		hues.sort(null);

		// Return list of hues
		return hues;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new image from the specified image.  The specified function is applied to the colour of each pixel of
	 * the input image, and the colour of the corresponding pixel of the output image is set to the resulting value.
	 *
	 * @param  image
	 *           the image whose pixels will be processed with {@code colourFunction}.
	 * @param  colourFunction
	 *           the function that will be applied to the colour of each pixel of {@code image} in order to produce the
	 *           colour of the corresponding pixel of the output image.
	 * @return a new image that is the result of applying {@code colourFunction} to the pixels of {@code image}.
	 */

	public static Image processPixelColours(
		Image					image,
		UnaryOperator<Color>	colourFunction)
	{
		// Get dimensions of input image
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();

		// Initialise output image
		WritableImage outImage = new WritableImage(width, height);

		// Process pixels of image
		if ((width > 0) && (height > 0))
		{
			PixelReader reader = image.getPixelReader();
			PixelWriter writer = outImage.getPixelWriter();
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
					writer.setColor(x, y, colourFunction.apply(reader.getColor(x, y)));
			}
		}

		// Return output image
		return outImage;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
