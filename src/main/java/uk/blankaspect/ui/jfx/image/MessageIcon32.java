/*====================================================================*\

MessageIcon32.java

Enumeration: message icon, 32x32.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.image;

//----------------------------------------------------------------------


// IMPORTS


import java.io.ByteArrayInputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import uk.blankaspect.ui.common.image.MessageIconImageData;

import uk.blankaspect.ui.jfx.dialog.ConfirmationDialog;
import uk.blankaspect.ui.jfx.dialog.NotificationDialog;

//----------------------------------------------------------------------


// ENUMERATION: MESSAGE ICON, 32x32


/**
 * This is an enumeration of icons that are intended to accompany messages in, for example, dialogs such as {@link
 * ConfirmationDialog} and {@link NotificationDialog}.
 */

public enum MessageIcon32
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * Alert icon
	 */
	ALERT
	(
		MessageIconImageData.ALERT_32
	),

	/**
	 * Error icon
	 */
	ERROR
	(
		MessageIconImageData.ERROR_32
	),

	/**
	 * Information icon
	 */
	INFORMATION
	(
		MessageIconImageData.INFORMATION_32
	),

	/**
	 * Question icon
	 */
	QUESTION
	(
		MessageIconImageData.QUESTION_32
	),

	/**
	 * Warning icon
	 */
	WARNING
	(
		MessageIconImageData.WARNING_32
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

	private MessageIcon32(
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
		return ImageUtils.smoothImageView(image);
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
