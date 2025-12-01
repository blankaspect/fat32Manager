/*====================================================================*\

RuleSetBuilder.java

Class: CSS rule-set builder.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.geometry.Side;

import javafx.scene.text.FontWeight;

import uk.blankaspect.common.css.CssRuleSet;

//----------------------------------------------------------------------


// CLASS: CSS RULE-SET BUILDER


public class RuleSetBuilder
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	REPEATING_IMAGE_PATTERN_PROPERTY_VALUE	= "repeating-image-pattern(\"%s\")";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	CssRuleSet.Builder	builder;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private RuleSetBuilder()
	{
		// Initialise instance variables
		builder = CssRuleSet.builder();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static RuleSetBuilder create()
	{
		return new RuleSetBuilder();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public RuleSetBuilder selector(
		String	selector)
	{
		// Add selector to list
		builder.selector(selector);

		// Return this builder
		return this;
	}

	//------------------------------------------------------------------

	public RuleSetBuilder property(
		FxProperty	fxProperty,
		String		value)
	{
		// Validate arguments
		if (fxProperty == null)
			throw new IllegalArgumentException("Null FX property");

		// Add property to map
		builder.property(fxProperty.getName(), value);

		// Return this builder
		return this;
	}

	//------------------------------------------------------------------

	public RuleSetBuilder repeatingImageBackground(
		String	imageKey)
	{
		// Get data-scheme URI of image
		String uri = DataUriImageMap.INSTANCE.get(imageKey);

		// Add property to map; return this builder
		return (uri == null)
					? this
					: property(FxProperty.BACKGROUND_COLOUR,
							   String.format(REPEATING_IMAGE_PATTERN_PROPERTY_VALUE, uri));
	}

	//------------------------------------------------------------------

	public RuleSetBuilder emptyBorder()
	{
		// Add property to map; return this builder
		return property(FxProperty.BORDER_WIDTH, "0");
	}

	//------------------------------------------------------------------

	public RuleSetBuilder borders(
		Side...	sides)
	{
		// Convert sides to array of widths
		int[] widths = new int[4];
		for (Side side : sides)
		{
			switch (side)
			{
				case TOP:
					widths[0] = 1;
					break;

				case BOTTOM:
					widths[2] = 1;
					break;

				case LEFT:
					widths[3] = 1;
					break;

				case RIGHT:
					widths[1] = 1;
					break;
			}
		}

		// Convert widths to string; add property to map; return this builder
		return borderWidths(widths);
	}

	//------------------------------------------------------------------

	public RuleSetBuilder borderWidths(
		int...	widths)
	{
		// Convert widths to string
		String value = IntStream.of(widths).mapToObj(Integer::toString).collect(Collectors.joining(" "));

		// Add property to map; return this builder
		return property(FxProperty.BORDER_WIDTH, value);
	}

	//------------------------------------------------------------------

	public RuleSetBuilder borderInsets(
		int...	insets)
	{
		// Convert insets to string
		String value = IntStream.of(insets).mapToObj(Integer::toString).collect(Collectors.joining(", "));

		// Add property to map; return this builder
		return property(FxProperty.BORDER_INSETS, value);
	}

	//------------------------------------------------------------------

	public RuleSetBuilder fontFamily(
		String	name)
	{
		// Add property to map; return this builder
		return property(FxProperty.FONT_FAMILY, (name.indexOf(' ') < 0) ? name : "\"" + name + "\"");
	}

	//------------------------------------------------------------------

	public RuleSetBuilder fontSize(
		String	size)
	{
		// Add property to map; return this builder
		return property(FxProperty.FONT_SIZE, size);
	}

	//------------------------------------------------------------------

	public RuleSetBuilder fontWeight(
		FontWeight	weight)
	{
		// Add property to map; return this builder
		return property(FxProperty.FONT_WEIGHT, Integer.toString(weight.getWeight()));
	}

	//------------------------------------------------------------------

	public RuleSetBuilder boldFont()
	{
		// Add property to map; return this builder
		return property(FxProperty.FONT_WEIGHT, "bold");
	}

	//------------------------------------------------------------------

	public RuleSetBuilder italicFont()
	{
		// Add property to map; return this builder
		return property(FxProperty.FONT_STYLE, "italic");
	}

	//------------------------------------------------------------------

	public RuleSetBuilder grayFontSmoothing()
	{
		// Add property to map; return this builder
		return property(FxProperty.FONT_SMOOTHING_TYPE, "gray");
	}

	//------------------------------------------------------------------

	public RuleSetBuilder underlinedText()
	{
		// Add property to map; return this builder
		return property(FxProperty.UNDERLINE, Boolean.toString(true));
	}

	//------------------------------------------------------------------

	public CssRuleSet build()
	{
		return builder.build();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
