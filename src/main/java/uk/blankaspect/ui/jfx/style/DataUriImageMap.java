/*====================================================================*\

DataUriImageMap.java

Class: map of images encoded as data-scheme URIs.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javafx.embed.swing.SwingFXUtils;

import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import uk.blankaspect.common.exception2.BaseException;

//----------------------------------------------------------------------


// CLASS: MAP OF IMAGES ENCODED AS DATA-SCHEME URIS


public class DataUriImageMap
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The single instance of this class. */
	public static final		DataUriImageMap	INSTANCE	= new DataUriImageMap();

	/** The name of the PNG image format. */
	private static final	String	PNG_FORMAT_NAME	= "png";

	/** The data-scheme URI prefix of PNG-file data. */
	private static final	String	PNG_DATA_URI_PREFIX	= "data:image/png;base64,";

	/** Error messages. */
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
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Map<String, String>	imageMap;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private DataUriImageMap()
	{
		// Initialise instance variables
		imageMap = new HashMap<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static DataUriImageMap create()
	{
		return new DataUriImageMap();
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

	private static byte[] imageToPngData(
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

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String get(
		String	key)
	{
		return imageMap.get(key);
	}

	//------------------------------------------------------------------

	public String put(
		String	key,
		String	uri)
	{
		return imageMap.put(key, uri);
	}

	//------------------------------------------------------------------

	public String put(
		String	key,
		Image	image)
		throws BaseException
	{
		// Encode byte data as Base64, append to data-scheme URI prefix and add result to map
		return put(key, PNG_DATA_URI_PREFIX + Base64.getEncoder().encodeToString(imageToPngData(image)));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
