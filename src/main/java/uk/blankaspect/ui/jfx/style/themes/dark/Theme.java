/*====================================================================*\

Theme.java

Class: 'dark' theme.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style.themes.dark;

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

	public static final		String	ID	= "dark";

	private static final	String	NAME	= "Dark";

	private static final	Color	DEFAULT_MONO_IMAGE_COLOUR	= Color.WHITE;

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
