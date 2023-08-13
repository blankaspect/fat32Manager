/*====================================================================*\

MessageIcon24.java

Enumeration: message icon, 24x24.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.image;

//----------------------------------------------------------------------


// IMPORTS


import java.io.ByteArrayInputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import uk.blankaspect.ui.common.image.MessageIconImageData;

//----------------------------------------------------------------------


// ENUMERATION: MESSAGE ICON, 24x24


/**
 * This is an enumeration of icons that are intended to accompany messages.
 */

public enum MessageIcon24
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * Alert icon
	 */
	ALERT
	(
		MessageIconImageData.ALERT_24
	),

	/**
	 * Error icon
	 */
	ERROR
	(
		MessageIconImageData.ERROR_24
	),

	/**
	 * Information icon
	 */
	INFORMATION
	(
		MessageIconImageData.INFORMATION_24
	),

	/**
	 * Question icon
	 */
	QUESTION
	(
		MessageIconImageData.QUESTION_24
	),

	/**
	 * Warning icon
	 */
	WARNING
	(
		MessageIconImageData.WARNING_24
	);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The image of the icon. */
	private	Image	image;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an enumeration constant for a message icon.
	 *
	 * @param imageData
	 *          the data of the image of the icon.
	 */

	private MessageIcon24(
		byte[]	imageData)
	{
		// Initialise instance variables
		image = new Image(new ByteArrayInputStream(imageData));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns this icon as an {@link ImageView}.
	 *
	 * @return this icon as an {@link ImageView}.
	 */

	public ImageView get()
	{
		return ImageUtils.createSmoothImageView(image);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the image of this icon.
	 *
	 * @return the image of this icon.
	 */

	public Image getImage()
	{
		return image;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
