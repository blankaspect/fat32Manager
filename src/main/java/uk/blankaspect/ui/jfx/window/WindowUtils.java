/*====================================================================*\

WindowUtils.java

Class: window-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.window;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Platform;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import javafx.scene.Node;

import javafx.stage.Stage;
import javafx.stage.Window;

import javafx.util.Duration;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

//----------------------------------------------------------------------


// CLASS: WINDOW-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain Window windows}.
 */

public class WindowUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private WindowUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void preventHeightChange(
		Stage	window)
	{
		// Create code to be executed
		Runnable fixHeight = () ->
		{
			double height = Math.ceil(window.getHeight());
			window.setMinHeight(height);
			window.setMaxHeight(height);
		};

		// If platform is Windows, run immediately ...
		if (File.separatorChar == '\\')
			fixHeight.run();

		// ... otherwise, run after a delay of 100 ms
		else
			new Timeline(new KeyFrame(Duration.millis(100.0), event -> fixHeight.run())).play();
	}

	//------------------------------------------------------------------

	public static void showAtRelativeLocation(
		Stage	window,
		Node	locator)
	{
		// Make window invisible
		window.setOpacity(0.0);

		// Set location of window when window is shown
		window.setOnShown(event ->
		{
			// Get screen bounds of locator
			Bounds bounds = locator.localToScreen(locator.getLayoutBounds());

			// Get location of window relative to locator
			Point2D location = SceneUtils.getRelativeLocation(window.getWidth(), window.getHeight(), bounds.getMinX(),
															  bounds.getMinY());

			// Set location of window
			window.setX(location.getX());
			window.setY(location.getY());

			// Make window visible
			Platform.runLater(() -> window.setOpacity(1.0));
		});

		// Display window
		window.showAndWait();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
