/*====================================================================*\

Theme.java

Class: 'dark' theme.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style.themes.dark;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.image.Image;

import javafx.scene.paint.Color;

import uk.blankaspect.common.math.ExpLog;

import uk.blankaspect.ui.jfx.image.ImageUtils;

import uk.blankaspect.ui.jfx.style.AbstractTheme;

//----------------------------------------------------------------------


// CLASS: THEME


public class Theme
	extends AbstractTheme
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	ID	= "dark";

	private static final	String	NAME	= "Dark";

	private static final	double	BRIGHTNESS_DELTA	= 0.08;

	private static final	double	BRIGHTNESS_BASE		= 0.2;
	private static final	double	BRIGHTNESS_PARAM	= 0.65;

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
		image = ImageUtils.processPixelColours(image, colour ->
		{
			double hue = colour.getHue();
			if (hue == 0.0)
			{
				double brightness =
						Math.min(BRIGHTNESS_BASE + ExpLog.b_01_10(BRIGHTNESS_PARAM).apply(colour.getBrightness()), 1.0);
				return Color.hsb(hue, colour.getSaturation(), brightness, colour.getOpacity());
			}
			return colour;
		});
		return image;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
