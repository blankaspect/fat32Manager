/*====================================================================*\

Theme.java

Class: 'light' theme.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style.themes.light;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.image.Image;

import uk.blankaspect.ui.jfx.style.AbstractTheme;

//----------------------------------------------------------------------


// CLASS: THEME


public class Theme
	extends AbstractTheme
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	ID	= "light";

	private static final	String	NAME	= "Light";

	private static final	double	BRIGHTNESS_DELTA	= -0.035;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Theme()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String getId()
	{
		return ID;
	}

	//------------------------------------------------------------------

	@Override
	public String getName()
	{
		return NAME;
	}

	//------------------------------------------------------------------

	@Override
	public double getBrightnessDelta1()
	{
		return BRIGHTNESS_DELTA;
	}

	//------------------------------------------------------------------

	@Override
	public Image processImage(
		Image	image)
	{
		return image;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
