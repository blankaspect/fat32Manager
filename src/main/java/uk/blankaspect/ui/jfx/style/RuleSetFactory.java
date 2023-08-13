/*====================================================================*\

RuleSetFactory.java

Class: CSS rule-set factory.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssRuleSet;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

//----------------------------------------------------------------------


// CLASS: CSS RULE-SET FACTORY


public class RuleSetFactory
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	VALUE_SEPARATOR	= StyleConstants.VALUE_SEPARATOR;

	private interface BorderStyle
	{
		String	DOTTED	= "dotted";
		String	SOLID	= "solid";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private RuleSetFactory()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static CssRuleSet doubleSolidBorder(
		String	selector,
		Color	outerColour,
		Color	innerColour)
	{
		return RuleSetBuilder.create()
				.selector(selector)
				.property(FxProperty.BORDER_COLOUR,
						  ColourUtils.colourToCssRgbaString(outerColour) + VALUE_SEPARATOR
							+ ColourUtils.colourToCssRgbaString(innerColour))
				.borderInsets(0, 1)
				.build();
	}

	//------------------------------------------------------------------

	public static CssRuleSet doubleSolidBorder(
		String	selector,
		String	outerColourKey,
		Color	innerColour)
	{
		return RuleSetBuilder.create()
				.selector(selector)
				.property(FxProperty.BORDER_COLOUR,
						  StyleConstants.COLOUR_KEY_PREFIX + outerColourKey + VALUE_SEPARATOR
							+ ColourUtils.colourToCssRgbaString(innerColour))
				.borderInsets(0, 1)
				.build();
	}

	//------------------------------------------------------------------

	public static CssRuleSet doubleSolidBorder(
		String	selector,
		Color	outerColour,
		String	innerColourKey)
	{
		return RuleSetBuilder.create()
				.selector(selector)
				.property(FxProperty.BORDER_COLOUR,
						  ColourUtils.colourToCssRgbaString(outerColour) + VALUE_SEPARATOR
							+ StyleConstants.COLOUR_KEY_PREFIX + innerColourKey)
				.borderInsets(0, 1)
				.build();
	}

	//------------------------------------------------------------------

	public static CssRuleSet focusBorder(
		String	selector)
	{
		return RuleSetBuilder.create()
				.selector(selector)
				.property(FxProperty.BORDER_COLOUR,
						  ColourUtils.colourToCssRgbaString(Color.WHITE) + VALUE_SEPARATOR
							+ ColourUtils.colourToCssRgbaString(Color.BLACK))
				.property(FxProperty.BORDER_STYLE,
						  BorderStyle.SOLID + VALUE_SEPARATOR + BorderStyle.DOTTED)
				.borderInsets(0, 0)
				.build();
	}

	//------------------------------------------------------------------

	public static CssRuleSet outerFocusBorder(
		String	selector,
		String	colourKey)
	{
		return RuleSetBuilder.create()
				.selector(selector)
				.property(FxProperty.BORDER_COLOUR,
						  ColourUtils.colourToCssRgbaString(Color.WHITE) + VALUE_SEPARATOR
							+ ColourUtils.colourToCssRgbaString(Color.BLACK) + VALUE_SEPARATOR
							+ StyleConstants.COLOUR_KEY_PREFIX + colourKey)
				.property(FxProperty.BORDER_STYLE,
						  BorderStyle.SOLID + VALUE_SEPARATOR + BorderStyle.DOTTED + VALUE_SEPARATOR + BorderStyle.SOLID)
				.borderInsets(0, 0, 1)
				.build();
	}

	//------------------------------------------------------------------

	public static CssRuleSet innerFocusBorder(
		String	selector,
		String	colourKey)
	{
		return RuleSetBuilder.create()
				.selector(selector)
				.property(FxProperty.BORDER_COLOUR,
						  StyleConstants.COLOUR_KEY_PREFIX + colourKey
							+ ColourUtils.colourToCssRgbaString(Color.WHITE) + VALUE_SEPARATOR
							+ ColourUtils.colourToCssRgbaString(Color.BLACK) + VALUE_SEPARATOR)
				.property(FxProperty.BORDER_STYLE,
						  BorderStyle.SOLID + VALUE_SEPARATOR + BorderStyle.SOLID + VALUE_SEPARATOR + BorderStyle.DOTTED)
				.borderInsets(0, 1, 1)
				.build();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
