/*====================================================================*\

ImageDataButton.java

Class: image-data button.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.button;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.stage.Window;

import uk.blankaspect.ui.jfx.image.ImageData;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

//----------------------------------------------------------------------


// CLASS: IMAGE-DATA BUTTON


public class ImageDataButton
	extends ImageButton
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String	imageId;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public ImageDataButton(
		String	imageId)
	{
		// Call alternative constructor
		this(imageId, null);
	}

	//------------------------------------------------------------------

	public ImageDataButton(
		String	imageId,
		String	tooltipText)
	{
		// Call superclass constructor
		super(ImageData.image(imageId), tooltipText);

		// Initialise instance variables
		this.imageId = imageId;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void updateButtons()
	{
		for (Window window : Window.getWindows())
		{
			Scene scene = window.getScene();
			if (scene != null)
				updateButtons(scene.getRoot());
		}
	}

	//------------------------------------------------------------------

	public static void updateButtons(
		Scene	scene)
	{
		updateButtons(scene.getRoot());
	}

	//------------------------------------------------------------------

	public static void updateButtons(
		Node	root)
	{
		SceneUtils.visitDepthFirst(root, true, true, node ->
		{
			if (node instanceof ImageDataButton button)
				button.updateImage();
			return true;
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void updateImage()
	{
		setImage(ImageData.image(imageId));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
