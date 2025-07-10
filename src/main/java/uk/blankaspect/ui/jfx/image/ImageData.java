/*====================================================================*\

ImageData.java

Record: image data.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.image;

//----------------------------------------------------------------------


// IMPORTS


import java.io.ByteArrayInputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.function.UnaryOperator;

import javafx.scene.image.Image;

import uk.blankaspect.common.exception2.ExceptionUtils;

import uk.blankaspect.common.thread.ThreadUtils;

//----------------------------------------------------------------------


// RECORD: IMAGE DATA


public record ImageData(
	String	id,
	String	typeId,
	byte[]	data)
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Miscellaneous strings. */
	private static final	String	NULL_DATA_STR				= "Null image data";
	private static final	String	NULL_ID_STR					= "Null ID";
	private static final	String	NULL_KEY_STR				= "Null key";
	private static final	String	NULL_PROCESSOR_STR			= "Null processor";
	private static final	String	CONFLICTING_IMAGE_ID_STR	= "Conflicting image ID: ";
	private static final	String	FAILED_TO_FIND_IMAGE_STR	=
			"Failed to find the image with ID '%s'; substituting the default 16x16 image.";

	/** The default image. */
	private static final	Image	DEFAULT_IMAGE	= new Image(new ByteArrayInputStream(ImgData.DEFAULT));

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** The cache of images in which an image is associated with an identifier. */
	private static	List<ImageData>						imageDatas	= new ArrayList<>();

	/** The cache of images in which an image is associated with an identifier. */
	private static	Map<String, Image>					images		= new HashMap<>();

	/** A map from image-type identifiers to image-processing functions. */
	private static	Map<String, UnaryOperator<Image>>	processors	= new HashMap<>();

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static ImageData of(
		String	id,
		byte...	data)
	{
		return of(id, null, data);
	}

	//------------------------------------------------------------------

	public static ImageData of(
		String	id,
		String	typeId,
		byte...	data)
	{
		// Validate arguments
		if (id == null)
			throw new IllegalArgumentException(NULL_ID_STR);
		if (data == null)
			throw new IllegalArgumentException(NULL_DATA_STR);

		// Create new instance of image data and return it
		return new ImageData(id, typeId, data);
	}

	//------------------------------------------------------------------

	public static ImageData add(
		String	id,
		byte...	data)
	{
		return add(id, null, data);
	}

	//------------------------------------------------------------------

	public static ImageData add(
		String	id,
		String	typeId,
		byte...	data)
	{
		// Create new instance of image data
		ImageData imageData = of(id, typeId, data);

		// Insert image data into list
		int index = Collections.binarySearch(imageDatas, imageData, Comparator.comparing(ImageData::id));
		if (index < 0)
			imageDatas.add(-index - 1, imageData);
		else
		{
			System.err.println(CONFLICTING_IMAGE_ID_STR + id);
			System.err.println(ThreadUtils.getStackTraceString(null, 1, 5));
		}

		// Return new instance of image data
		return imageData;
	}

	//------------------------------------------------------------------

	public static ImageData remove(
		String	id)
	{
		// Validate argument
		if (id == null)
			throw new IllegalArgumentException(NULL_ID_STR);

		// Search list of image data by ID
		int index = findImageData(id);

		// If image data was found, remove it from list
		ImageData imageData = (index < 0) ? null : imageDatas.remove(index);

		// Remove corresponding image from cache
		images.remove(id);

		// Return removed image data
		return imageData;
	}

	//------------------------------------------------------------------

	public static Image image(
		String	id)
	{
		return image(id, true);
	}

	//------------------------------------------------------------------

	public static Image image(
		String	id,
		boolean	defaultIfNotFound)
	{
		// Validate arguments
		if (id == null)
			throw new IllegalArgumentException(NULL_ID_STR);

		// Get image from cache
		Image image = images.get(id);

		// If image is not in cache, search for image data, create image from it and add image to cache
		if (image == null)
		{
			// Search list of image data by ID
			int index = findImageData(id);

			// If image data was found, create image from it and add image to cache
			if (index >= 0)
			{
				try
				{
					image = addImage(imageDatas.get(index));
				}
				catch (Exception e)
				{
					ExceptionUtils.printStderrLocated(e);
				}
			}
		}

		// If image is not in cache or in list of image data, substitute default image
		if ((image == null) && defaultIfNotFound)
		{
			// Use default image
			image = DEFAULT_IMAGE;

			// Report failure
			System.err.println(String.format(FAILED_TO_FIND_IMAGE_STR, id));
			System.err.println(ThreadUtils.getStackTraceString(null, 1, 20));
		}

		// Return image
		return image;
	}

	//------------------------------------------------------------------

	public static void updateImages()
	{
		// Clear image cache
		images.clear();

		// Repopulate image cache from image data
		for (ImageData imageData : imageDatas)
		{
			try
			{
				addImage(imageData);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	//------------------------------------------------------------------

	public static UnaryOperator<Image> addProcessor(
		String					key,
		UnaryOperator<Image>	processor)
	{
		// Validate arguments
		if (key == null)
			throw new IllegalArgumentException(NULL_KEY_STR);
		if (processor == null)
			throw new IllegalArgumentException(NULL_PROCESSOR_STR);

		// Add image-processing function to map
		return processors.put(key, processor);
	}

	//------------------------------------------------------------------

	public static UnaryOperator<Image> removeProcessor(
		String	key)
	{
		// Validate arguments
		if (key == null)
			throw new IllegalArgumentException(NULL_KEY_STR);

		// Remove image-processing function from map and return it
		return processors.remove(key);
	}

	//------------------------------------------------------------------

	private static int findImageData(
		String	id)
	{
		return Collections.binarySearch(imageDatas, new ImageData(id, null, null), Comparator.comparing(ImageData::id));
	}

	//------------------------------------------------------------------

	private static Image addImage(
		ImageData	imageData)
		throws Exception
	{
		// Create image from image data
		Image image = new Image(new ByteArrayInputStream(imageData.data));
		if (image.isError())
			throw image.getException();

		// Find an image-processing function for the image type and process the image
		if (imageData.typeId != null)
		{
			UnaryOperator<Image> processor = processors.get(imageData.typeId);
			if (processor != null)
				image = processor.apply(image);
		}

		// Add image to cache
		images.put(imageData.id, image);

		// Return image
		return image;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Image data
////////////////////////////////////////////////////////////////////////

	/**
	 * PNG image data.
	 */

	private interface ImgData
	{
		/** Default image. */
		byte[]	DEFAULT	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x1F, (byte)0xF3, (byte)0xFF,
			(byte)0x61, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x4F, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x95, (byte)0x93, (byte)0x4B, (byte)0x4B, (byte)0xC3,
			(byte)0x50, (byte)0x10, (byte)0x85, (byte)0x2F, (byte)0x54, (byte)0xB7, (byte)0xDA, (byte)0xFE,
			(byte)0x02, (byte)0xAB, (byte)0x2B, (byte)0x37, (byte)0xE2, (byte)0xD6, (byte)0x5F, (byte)0x25,
			(byte)0xB5, (byte)0xDD, (byte)0xD8, (byte)0xEE, (byte)0xA4, (byte)0x52, (byte)0xB0, (byte)0x9B,
			(byte)0xBA, (byte)0x12, (byte)0x71, (byte)0xEB, (byte)0xDE, (byte)0x8D, (byte)0x0F, (byte)0x6C,
			(byte)0xA5, (byte)0xA0, (byte)0x68, (byte)0x7D, (byte)0xAE, (byte)0x04, (byte)0x23, (byte)0x3E,
			(byte)0xB0, (byte)0xC6, (byte)0x8B, (byte)0xB1, (byte)0xA8, (byte)0x28, (byte)0x69, (byte)0x5A,
			(byte)0xD2, (byte)0xA4, (byte)0x79, (byte)0x8C, (byte)0x99, (byte)0x59, (byte)0xC4, (byte)0xE6,
			(byte)0x9A, (byte)0x90, (byte)0x7A, (byte)0x60, (byte)0x36, (byte)0x73, (byte)0xCE, (byte)0x7C,
			(byte)0x19, (byte)0xC8, (byte)0x1D, (byte)0xF6, (byte)0x9C, (byte)0x61, (byte)0x69, (byte)0x5E,
			(byte)0x48, (byte)0x5E, (byte)0xBC, (byte)0x2C, (byte)0x24, (byte)0x6C, (byte)0x39, (byte)0xC3,
			(byte)0x60, (byte)0x98, (byte)0xC2, (byte)0x2C, (byte)0xCF, (byte)0xA7, (byte)0xCE, (byte)0x9B,
			(byte)0x39, (byte)0x36, (byte)0xC1, (byte)0x78, (byte)0x3E, (byte)0x79, (byte)0xA9, (byte)0x1E,
			(byte)0x94, (byte)0x1C, (byte)0xD7, (byte)0x36, (byte)0x61, (byte)0x58, (byte)0x61, (byte)0x56,
			(byte)0xAD, (byte)0x2D, (byte)0x3B, (byte)0x08, (byte)0x61, (byte)0x48, (byte)0x0B, (byte)0x1B,
			(byte)0xEE, (byte)0xB7, (byte)0x24, (byte)0xD0, (byte)0x4E, (byte)0xD6, (byte)0x40, (byte)0x6B,
			(byte)0xAC, (byte)0x43, (byte)0xFF, (byte)0xE3, (byte)0x5E, (byte)0xB4, (byte)0xC1, (byte)0xB5,
			(byte)0x0C, (byte)0xDA, (byte)0x84, (byte)0xE1, (byte)0x4A, (byte)0xA2, (byte)0xD4, (byte)0xFD,
			(byte)0x25, (byte)0x90, (byte)0xB3, (byte)0x89, (byte)0xDF, (byte)0x95, (byte)0x73, (byte)0xA3,
			(byte)0xA0, (byte)0x1D, (byte)0xAD, (byte)0x8A, (byte)0x31, (byte)0xF2, (byte)0xFE, (byte)0x00,
			(byte)0x8C, (byte)0x66, (byte)0x83, (byte)0x8C, (byte)0x56, (byte)0x65, (byte)0x0E, (byte)0x7A,
			(byte)0x0F, (byte)0x75, (byte)0xD0, (byte)0xEF, (byte)0xAA, (byte)0xF0, (byte)0x56, (byte)0x9E,
			(byte)0x25, (byte)0xA0, (byte)0xA9, (byte)0x5C, (byte)0x07, (byte)0xB2, (byte)0xA1, (byte)0x00,
			(byte)0x5C, (byte)0x5B, (byte)0xCE, (byte)0x8E, (byte)0x80, (byte)0xF5, (byte)0xF9, (byte)0xE8,
			(byte)0xF7, (byte)0x8C, (byte)0xA7, (byte)0x63, (byte)0x0A, (byte)0xB7, (byte)0x0F, (byte)0x2B,
			(byte)0x03, (byte)0xC9, (byte)0x08, (byte)0x00, (byte)0xB8, (byte)0x2E, (byte)0xD8, (byte)0x9D,
			(byte)0xF7, (byte)0x40, (byte)0x0B, (byte)0xD7, (byte)0xC7, (byte)0x5C, (byte)0xF7, (byte)0x6A,
			(byte)0x33, (byte)0xD0, (byte)0x0F, (byte)0x07, (byte)0x0C, (byte)0xCA, (byte)0xB1, (byte)0xA1,
			(byte)0x5D, (byte)0x2F, (byte)0xD3, (byte)0xFA, (byte)0xAF, (byte)0xC5, (byte)0x29, (byte)0x70,
			(byte)0xCD, (byte)0x6E, (byte)0xC0, (byte)0x8E, (byte)0x05, (byte)0x7C, (byte)0x6D, (byte)0xCD,
			(byte)0x53, (byte)0x48, (byte)0x29, (byte)0x4D, (byte)0x83, (byte)0xF5, (byte)0x2D, (byte)0x8B,
			(byte)0x76, (byte)0x3C, (byte)0x40, (byte)0x59, (byte)0x99, (byte)0xF1, (byte)0xBE, (byte)0x3C,
			(byte)0xE9, (byte)0xFD, (byte)0xB2, (byte)0x9E, (byte)0x68, (byte)0x91, (byte)0x62, (byte)0x01,
			(byte)0x9D, (byte)0xD3, (byte)0x0D, (byte)0xAA, (byte)0x28, (byte)0xC5, (byte)0x02, (byte)0xF4,
			(byte)0x9B, (byte)0x6D, (byte)0xD0, (byte)0xA5, (byte)0x1D, (byte)0xB1, (byte)0xED, (byte)0x2B,
			(byte)0x16, (byte)0xC0, (byte)0x17, (byte)0xC7, (byte)0xBC, (byte)0x1A, (byte)0x17, (byte)0xDB,
			(byte)0xBE, (byte)0x08, (byte)0x10, (byte)0xF5, (byte)0x94, (byte)0x51, (byte)0xBA, (byte)0xB4,
			(byte)0x0B, (byte)0xFA, (byte)0xED, (byte)0x9E, (byte)0xD8, (byte)0x26, (byte)0xF9, (byte)0x4F,
			(byte)0x19, (byte)0x2F, (byte)0x11, (byte)0x0F, (byte)0x23, (byte)0x0A, (byte)0x12, (byte)0x26,
			(byte)0x3A, (byte)0xA6, (byte)0x6A, (byte)0xD1, (byte)0xE6, (byte)0x85, (byte)0xD4, (byte)0x19,
			(byte)0xC3, (byte)0x93, (byte)0xC4, (byte)0xAB, (byte)0xFA, (byte)0xF7, (byte)0x39, (byte)0x7B,
			(byte)0xC3, (byte)0x38, (byte)0xFB, (byte)0x03, (byte)0xEF, (byte)0x1F, (byte)0x63, (byte)0xFD,
			(byte)0xA2, (byte)0x02, (byte)0xF3, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};
	}

	//==================================================================

}

//----------------------------------------------------------------------
