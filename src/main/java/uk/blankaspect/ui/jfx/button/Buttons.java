/*====================================================================*\

Buttons.java

Class: factory methods for buttons.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.button;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.control.Button;

import javafx.scene.layout.Region;

//----------------------------------------------------------------------


// CLASS: FACTORY METHODS FOR BUTTONS


public class Buttons
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private Buttons()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Button hNoShrink()
	{
		Button button = new Button();
		button.setMinWidth(Region.USE_PREF_SIZE);
		return button;
	}

	//------------------------------------------------------------------

	public static Button hNoShrink(
		String	text)
	{
		Button button = new Button(text);
		button.setMinWidth(Region.USE_PREF_SIZE);
		return button;
	}

	//------------------------------------------------------------------

	public static Button hExpansive(
		String	text)
	{
		Button button = new Button(text);
		button.setMaxWidth(Double.MAX_VALUE);
		return button;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
