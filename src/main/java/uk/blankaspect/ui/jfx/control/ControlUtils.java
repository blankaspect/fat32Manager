/*====================================================================*\

ControlUtils.java

Class: utility methods that relate to JavaFX controls.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.control;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.control.Control;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS THAT RELATE TO JAVAFX CONTROLS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain Control controls}.
 */

public class ControlUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ControlUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Performs the specified action when the skin property is set on the specified control.
	 *
	 * @param control
	 *          the target control.
	 * @param action
	 *          the action that will be performed when the skin property is set on {@code control}.
	 */

	public static void onSkin(
		Control		control,
		Runnable	action)
	{
		control.skinProperty().addListener((observable, oldSkin, skin) ->
		{
			if (skin != null)
				action.run();
		});
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
