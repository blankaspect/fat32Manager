/*====================================================================*\

ImageButton.java

Class: image button.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.button;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.image.Image;

import uk.blankaspect.ui.jfx.image.ImageUtils;

//----------------------------------------------------------------------


// CLASS: IMAGE BUTTON


/**
 * This class implements a button that contains an image but no text.
 * <p>
 * See the documentation of {@link GraphicButton} for a description of a button's attributes and properties.
 * </p>
 */

public class ImageButton
	extends GraphicButton
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The image of this button. */
	private	Image	image;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a button that contains the specified image.
	 *
	 * @param image
	 *          the image that will be the graphic content of the button, which may be {@code null}.
	 */

	public ImageButton(
		Image	image)
	{
		// Call alternative constructor
		this(image, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a button that contains the specified image and has the specified tooltip text.
	 *
	 * @param image
	 *          the image that will be the graphic content of the button, which may be {@code null}.
	 * @param tooltipText
	 *          the text of the tooltip for the button, which may be {@code null}.
	 */

	public ImageButton(
		Image	image,
		String	tooltipText)
	{
		// Call superclass constructor
		super(null, tooltipText);

		// Set properties
		if (image != null)
			setImage(image);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the image that is the graphic content of this button.
	 *
	 * @return the image that is the graphic content of this button.
	 */

	public Image getImage()
	{
		return image;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the image that is the graphic content of this button.
	 *
	 * @param image
	 *          the image that will be the graphic content of this button.
	 */

	public void setImage(
		Image	image)
	{
		// Update instance variable
		this.image = image;

		// Update graphic content
		setGraphic(ImageUtils.smoothImageView(image));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
