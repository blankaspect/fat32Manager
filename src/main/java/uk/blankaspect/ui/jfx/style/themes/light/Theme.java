/*====================================================================*\

Theme.java

Class: 'light' theme.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style.themes.light;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.paint.Color;

import uk.blankaspect.ui.jfx.style.AbstractTheme;

//----------------------------------------------------------------------


// CLASS: THEME


public class Theme
	extends AbstractTheme
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		String	ID	= "light";

	private static final	String	NAME	= "Light";

	private static final	Color	DEFAULT_MONO_IMAGE_COLOUR	= Color.grayRgb(48);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Theme()
	{
		// Set properties
		setMonoImageColour(DEFAULT_MONO_IMAGE_COLOUR);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String id()
	{
		return ID;
	}

	//------------------------------------------------------------------

	@Override
	public String name()
	{
		return NAME;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
