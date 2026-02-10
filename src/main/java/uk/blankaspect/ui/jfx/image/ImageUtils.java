/*====================================================================*\

ImageUtils.java

Class: image-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.image;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.List;

import java.util.function.UnaryOperator;

import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.scene.paint.Color;

import javax.imageio.ImageIO;

import uk.blankaspect.common.collection.ArraySet;

import uk.blankaspect.common.exception2.BaseException;

import uk.blankaspect.ui.jfx.geometry.Corner;

//----------------------------------------------------------------------


// CLASS: IMAGE-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain Image images}.
 */

public class ImageUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	PNG_FORMAT_NAME	= "png";

	private interface ErrorMsg
	{
		String	IMAGE_FORMAT_NOT_SUPPORTED =
				"This implementation of Java does not support the '%s' image format.";

		String	FAILED_TO_CONVERT_IMAGE =
				"Failed to convert image between internal types.";

		String	FAILED_TO_ENCODE_IMAGE_AS_PNG =
				"Failed to encode the image as a PNG file.";
	}

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
	 * Overlays one specified image on another and returns the resulting composite image.  The two images are aligned at
	 * the specified corner, and the size of the composite image is the size of the base image.
	 *
	 * @param  baseImage
	 *           the base image on which {@code overlay} will be overlaid.
	 * @param  overlay
	 *           the image that will be overlaid on {@code baseImage}.
	 * @param  corner
	 *           the corner at which {@code baseImage} and {@code overlay} will be aligned.
	 * @return the composite image formed by overlaying {@code overlay} on {@code baseImage}.
	 */

	public static Image overlayImage(
		Image	baseImage,
		Image	overlay,
		Corner	corner)
	{
		// Initialise widths and heights
		int baseImageWidth = (int)baseImage.getWidth();
		int baseImageHeight = (int)baseImage.getHeight();
		int overlayWidth = (int)overlay.getWidth();
		int overlayHeight = (int)overlay.getHeight();
		int regionWidth = Math.min(baseImageWidth, overlayWidth);
		int regionHeight = Math.min(baseImageHeight, overlayHeight);

		// Calculate x and y offsets of overlay
		int inX = 0;
		int inY = 0;
		int outX = 0;
		int outY = 0;
		switch (corner)
		{
			case TOP_LEFT:
				// do nothing
				break;

			case TOP_RIGHT:
				if (overlayWidth > baseImageWidth)
					inX = overlayWidth - baseImageWidth;
				else
					outX = baseImageWidth - overlayWidth;
				break;

			case BOTTOM_LEFT:
				if (overlayHeight > baseImageHeight)
					inY = overlayHeight - baseImageHeight;
				else
					outY = baseImageHeight - overlayHeight;
				break;

			case BOTTOM_RIGHT:
				if (overlayWidth > baseImageWidth)
					inX = overlayWidth - baseImageWidth;
				else
					outX = baseImageWidth - overlayWidth;

				if (overlayHeight > baseImageHeight)
					inY = overlayHeight - baseImageHeight;
				else
					outY = baseImageHeight - overlayHeight;
				break;
		}

		// Get pixel data of base image
		int[] pixelBuffer = new int[baseImageWidth * baseImageHeight];
		baseImage.getPixelReader().getPixels(0, 0, baseImageWidth, baseImageHeight, PixelFormat.getIntArgbInstance(),
											 pixelBuffer, 0, baseImageWidth);

		// Copy base image to output image
		WritableImage outImage = new WritableImage(baseImageWidth, baseImageHeight);
		outImage.getPixelWriter().setPixels(0, 0, baseImageWidth, baseImageHeight, PixelFormat.getIntArgbInstance(),
											pixelBuffer, 0, baseImageWidth);

		// Get pixel data of overlay
		overlay.getPixelReader().getPixels(inX, inY, regionWidth, regionHeight, PixelFormat.getIntArgbInstance(),
										   pixelBuffer, 0, baseImageWidth);

		// Overwrite region of output image with overlay
		outImage.getPixelWriter().setPixels(outX, outY, regionWidth, regionHeight, PixelFormat.getIntArgbInstance(),
											pixelBuffer, 0, baseImageWidth);

		// Return output image
		return outImage;
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

	/**
	 * Encodes the specified image as a PNG file and returns the contents of the file as an array of bytes.
	 *
	 * @param  image
	 *           the image that will be encoded.
	 * @return a byte array containing {@code image} encoded as a PNG file.
	 * @throws BaseException
	 *           if
	 *           <ul>
	 *             <li>
	 *               {@code image} could not be converted to an intermediate {@link BufferedImage}, or
	 *             </li>
	 *             <li>
	 *               the {@link ImageIO#write(java.awt.image.RenderedImage, String, java.io.OutputStream)
	 *               ImageIO.write(&hellip;)} method does not support the writing of PNG files, or
	 *             </li>
	 *             <li>
	 *               an error occurred when encoding the image as a PNG file.
	 *             </li>
	 *           </ul>
	 */

	public static byte[] imageToPngData(
		Image	image)
		throws BaseException
	{
		// Convert JavaFX image to AWT image
		BufferedImage outImage = SwingFXUtils.fromFXImage(image, null);
		if (outImage == null)
			throw new BaseException(ErrorMsg.FAILED_TO_CONVERT_IMAGE);

		// Convert image to byte array of PNG file data
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try
		{
			if (!ImageIO.write(outImage, PNG_FORMAT_NAME, outStream))
				throw new BaseException(ErrorMsg.IMAGE_FORMAT_NOT_SUPPORTED, PNG_FORMAT_NAME);
		}
		catch (IOException e)
		{
			throw new BaseException(ErrorMsg.FAILED_TO_ENCODE_IMAGE_AS_PNG, e);
		}
		return outStream.toByteArray();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
