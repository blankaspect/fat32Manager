/*====================================================================*\

CopyPopUp.java

Class: pop-up window containing a label that performs a 'copy' action.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.nio.file.Path;

import java.util.List;

import javafx.scene.image.Image;

import javafx.stage.Window;

import uk.blankaspect.common.exception2.BaseException;

import uk.blankaspect.common.function.IFunction0;

import uk.blankaspect.ui.jfx.clipboard.ClipboardUtils;

import uk.blankaspect.ui.jfx.dialog.ErrorDialog;

import uk.blankaspect.ui.jfx.image.ImageData;
import uk.blankaspect.ui.jfx.image.ImageUtils;

import uk.blankaspect.ui.jfx.style.AbstractTheme;

//----------------------------------------------------------------------


// CLASS: POP-UP WINDOW CONTAINING A LABEL THAT PERFORMS A 'COPY' ACTION


/**
 * The class implements a pop-up window that contains a label with a 'copy' icon and specified or default text.  A
 * specified action is performed when the primary mouse button is clicked on the label.
 * <p>
 * Static methods provide pop-ups for putting text, an image, a single file-system location or a collection of
 * file-system locations on the system clipboard.
 * </p>
 */

public class CopyPopUp
	extends ActionLabelPopUp
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Miscellaneous strings. */
	private static final	String	COPY_STR			= "Copy";
	private static final	String	COPY_TEXT_STR		= "Copy text";
	private static final	String	COPY_IMAGE_STR		= "Copy image";
	private static final	String	COPY_LOCATION_STR	= "Copy location";
	private static final	String	COPY_LOCATIONS_STR	= "Copy locations";

	/** Image identifiers. */
	private interface ImageId
	{
		String	PREFIX = MethodHandles.lookup().lookupClass().getEnclosingClass().getName() + ".";

		String	COPY	= PREFIX + "copy";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Create image from image data
		ImageData.add(ImageId.COPY, AbstractTheme.MONO_IMAGE_KEY, ImgData.COPY);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public CopyPopUp(
		Runnable	action)
	{
		// Call alternative constructor
		this(COPY_STR, action);
	}

	//------------------------------------------------------------------

	public CopyPopUp(
		String		text,
		Runnable	action)
	{
		// Call superclass constructor
		super(text, ImageUtils.smoothImageView(ImageData.image(ImageId.COPY)), action);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static CopyPopUp text(
		Window				owner,
		IFunction0<String>	textSource)
	{
		return text(owner, COPY_TEXT_STR, textSource);
	}

	//------------------------------------------------------------------

	public static CopyPopUp text(
		Window				owner,
		String				labelText,
		IFunction0<String>	textSource)
	{
		return new CopyPopUp(labelText, () ->
		{
			try
			{
				ClipboardUtils.putTextThrow(textSource.invoke());
			}
			catch (BaseException e)
			{
				ErrorDialog.show(owner, labelText, e);
			}
		});
	}

	//------------------------------------------------------------------

	public static CopyPopUp image(
		Window				owner,
		IFunction0<Image>	imageSource)
	{
		return image(owner, COPY_IMAGE_STR, imageSource);
	}

	//------------------------------------------------------------------

	public static CopyPopUp image(
		Window				owner,
		String				labelText,
		IFunction0<Image>	imageSource)
	{
		return new CopyPopUp(labelText, () ->
		{
			try
			{
				ClipboardUtils.putImageThrow(imageSource.invoke());
			}
			catch (BaseException e)
			{
				ErrorDialog.show(owner, labelText, e);
			}
		});
	}

	//------------------------------------------------------------------

	public static CopyPopUp location(
		Window	owner,
		Path	location)
	{
		return location(owner, COPY_LOCATION_STR, location);
	}

	//------------------------------------------------------------------

	public static CopyPopUp location(
		Window	owner,
		String	labelText,
		Path	location)
	{
		return locations(owner, labelText, () -> List.of(location));
	}

	//------------------------------------------------------------------

	public static CopyPopUp location(
		Window						owner,
		IFunction0<? extends Path>	locationSource)
	{
		return location(owner, COPY_LOCATION_STR, locationSource);
	}

	//------------------------------------------------------------------

	public static CopyPopUp location(
		Window						owner,
		String						labelText,
		IFunction0<? extends Path>	locationSource)
	{
		return locations(owner, labelText, () -> List.of(locationSource.invoke()));
	}

	//------------------------------------------------------------------

	public static CopyPopUp locations(
		Window	owner,
		Path...	locations)
	{
		return locations(owner, COPY_LOCATIONS_STR, locations);
	}

	//------------------------------------------------------------------

	public static CopyPopUp locations(
		Window	owner,
		String	labelText,
		Path...	locations)
	{
		return locations(owner, labelText, () -> List.of(locations));
	}

	//------------------------------------------------------------------

	public static CopyPopUp locations(
		Window									owner,
		IFunction0<Iterable<? extends Path>>	locationSource)
	{
		return locations(owner, COPY_LOCATIONS_STR, locationSource);
	}

	//------------------------------------------------------------------

	public static CopyPopUp locations(
		Window									owner,
		String									labelText,
		IFunction0<Iterable<? extends Path>>	locationSource)
	{
		return new CopyPopUp(labelText, () ->
		{
			try
			{
				ClipboardUtils.putLocationsAndTextThrow(locationSource.invoke());
			}
			catch (BaseException e)
			{
				ErrorDialog.show(owner, labelText, e);
			}
		});
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
		// File: mono/copy
		byte[]	COPY	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xB5, (byte)0xFA, (byte)0x37,
			(byte)0xEA, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x59, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xF8, (byte)0xC7, (byte)0x80, (byte)0x1F,
			(byte)0x22, (byte)0x33, (byte)0xDF, (byte)0x33, (byte)0xFC, (byte)0x07, (byte)0xC3, (byte)0xDF,
			(byte)0x0C, (byte)0x49, (byte)0xD8, (byte)0x15, (byte)0xFC, (byte)0x87, (byte)0xD1, (byte)0x0C,
			(byte)0x8F, (byte)0x19, (byte)0x32, (byte)0xF1, (byte)0x2B, (byte)0x50, (byte)0x45, (byte)0x28,
			(byte)0xC1, (byte)0xA6, (byte)0x00, (byte)0x62, (byte)0xD5, (byte)0x6F, (byte)0x14, (byte)0x05,
			(byte)0x98, (byte)0xF6, (byte)0xC3, (byte)0x95, (byte)0xA3, (byte)0x71, (byte)0xE1, (byte)0xF6,
			(byte)0xE3, (byte)0x56, (byte)0x00, (byte)0xB5, (byte)0x1F, (byte)0x97, (byte)0x02, (byte)0xB8,
			(byte)0xFD, (byte)0x38, (byte)0x14, (byte)0x20, (byte)0x78, (byte)0x04, (byte)0x14, (byte)0x00,
			(byte)0xE1, (byte)0x7B, (byte)0xBC, (byte)0x0A, (byte)0x90, (byte)0xD8, (byte)0x98, (byte)0x42,
			(byte)0x64, (byte)0x2A, (byte)0x80, (byte)0x05, (byte)0x14, (byte)0x04, (byte)0x42, (byte)0xED,
			(byte)0x47, (byte)0x52, (byte)0x80, (byte)0x1B, (byte)0x02, (byte)0x00, (byte)0xE5, (byte)0x32,
			(byte)0xF8, (byte)0x0C, (byte)0x4A, (byte)0x24, (byte)0x84, (byte)0xAC, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42,
			(byte)0x60, (byte)0x82
		};
	}

	//==================================================================

}

//----------------------------------------------------------------------
