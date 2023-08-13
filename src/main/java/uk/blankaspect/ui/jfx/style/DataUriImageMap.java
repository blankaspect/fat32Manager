/*====================================================================*\

DataUriImageMap.java

Class: map of images encoded as data-scheme URIs.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

import uk.blankaspect.common.exception2.BaseException;

import uk.blankaspect.ui.jfx.image.ImageUtils;

//----------------------------------------------------------------------


// CLASS: MAP OF IMAGES ENCODED AS DATA-SCHEME URIS


public class DataUriImageMap
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		DataUriImageMap	INSTANCE	= new DataUriImageMap();

	private static final	String	PNG_DATA_URI_PREFIX	= "data:image/png;base64,";

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
		// Encode image as PNG file
		byte[] data = ImageUtils.imageToPngData(image);

		// Encode byte data as Base64, append to data-scheme URI prefix and add result to map
		return put(key, PNG_DATA_URI_PREFIX + Base64.getEncoder().encodeToString(data));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
