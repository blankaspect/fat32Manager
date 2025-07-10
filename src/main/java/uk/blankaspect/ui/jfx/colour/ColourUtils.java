/*====================================================================*\

ColourUtils.java

Class: JavaFX colour utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.colour;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.paint.Color;

import uk.blankaspect.common.colour.ColourConstants;

//----------------------------------------------------------------------


// CLASS: JAVAFX COLOUR UTILITY METHODS


/**
 * This class contains utility methods related to JavaFX {@linkplain Color colours}.
 */

public class ColourUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Miscellaneous strings. */
	private static final	String	FRACTION_OUT_OF_BOUNDS_STR	= "Fraction out of bounds: ";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ColourUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a copy of the specified colour.
	 *
	 * @param  colour
	 *           the colour for which a copy is required.
	 * @return a copy of {@code colour}.
	 */

	public static Color copy(
		Color	colour)
	{
		return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getOpacity());
	}

	//------------------------------------------------------------------

	/**
	 * Returns a fully opaque colour whose red, green and blue components are equal to those of the specified colour.
	 * If the specified colour is fully opaque, it is returned unchanged.
	 *
	 * @param  colour
	 *           the colour for which a corresponding fully opaque colour is required.
	 * @return fully opaque colour whose red, green and blue components are equal to those of {@code colour}.
	 */

	public static Color opaque(
		Color	colour)
	{
		return colour.isOpaque() ? colour : Color.color(colour.getRed(), colour.getGreen(), colour.getBlue());
	}

	//------------------------------------------------------------------

	/**
	 * Returns a colour whose red, green and blue components are equal to those of the specified colour and whose
	 * opacity is the specified value.
	 *
	 * @param  colour
	 *           the colour that will provide the red, green and blue components of the output colour.
	 * @param  opacity
	 *           the required opacity of the colour.
	 * @return a colour whose red, green and blue components are equal to those of {@code colour} and whose opacity is
	 *         {@code opacity}.
	 */

	public static Color applyOpacity(
		Color	colour,
		double	opacity)
	{
		return Color.color(colour.getRed(), colour.getGreen(), colour.getBlue(), opacity);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a colour that is derived from the specified colour by adding the specified value to the brightness
	 * component of the colour and clamping the result to the interval [0, 1].
	 *
	 * @param  colour
	 *           the colour from which the output colour will be derived.
	 * @param  delta
	 *           the value that will be added to the brightness component of {@code colour}.
	 * @return the colour that is the result of adding {@code delta} to the brightness component of {@code colour}.
	 */

	public static Color shiftBrightness(
		Color	colour,
		double	delta)
	{
		return Color.hsb(colour.getHue(), colour.getSaturation(),
						 Math.min(Math.max(0.0, colour.getBrightness() + delta), 1.0), colour.getOpacity());
	}

	//------------------------------------------------------------------

	/**
	 * Multiplies each of the red, green and blue components of the specified colour with the opacity of the colour and
	 * returns the colour that is composed of the resulting values.
	 *
	 * @param  colour
	 *           the colour whose red, green and blue components will be multiplied by the colour's opacity.
	 * @param  opaque
	 *           if {@code true}, the returned colour will be opaque; otherwise, it will have the opacity of
	 *           {@code colour}.
	 * @return the colour that results from multiplying each of the red, green and blue components of {@code colour}
	 *         with the opacity of {@code colour}.
	 */

	public static Color multiplyByAlpha(
		Color	colour,
		boolean	opaque)
	{
		double a = colour.getOpacity();
		if (a < 1.0)
		{
			double r = a * colour.getRed();
			double g = a * colour.getGreen();
			double b = a * colour.getBlue();
			colour = opaque ? Color.color(r, g, b) : Color.color(r, g, b, a);
		}
		return colour;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the colour that results from overlaying one specified colour on another using alpha compositing.
	 *
	 * @param  colour1
	 *           the colour that will be overlaid on {@code colour2}.
	 * @param  colour2
	 *           the colour over which {@code colour1} will be overlaid.
	 * @return the result of overlaying {@code colour1} on {@code colour2} using alpha compositing.
	 */

	public static Color overlay(
		Color	colour1,
		Color	colour2)
	{
		double a1 = colour1.getOpacity();
		double a2 = (1.0 - a1) * colour2.getOpacity();
		double a = a1 + a2;

		double r = (a1 * colour1.getRed()   + a2 * colour2.getRed())   / a;
		double g = (a1 * colour1.getGreen() + a2 * colour2.getGreen()) / a;
		double b = (a1 * colour1.getBlue()  + a2 * colour2.getBlue())  / a;
		return Color.color(r, g, b, a);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified normalised colour value &mdash; a value in the range 0.0 to 1.0 &mdash; to an integer in
	 * the range 0 to 255 and returns the result.
	 *
	 * @param  value
	 *           the value that will be converted.
	 * @return the integer value that corresponds to {@code value}.
	 */

	public static int colourValueToInt(
		double	value)
	{
		return (int)Math.round(value * (double)ColourConstants.MAX_RGB_COMPONENT_VALUE);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified colour to a 32-bit ARGB value and returns the result.  The ARGB components are represented
	 * by the following bit fields, with all components in the range 0..255:
	 * <ul>
	 *   <li>bits 24..31 : alpha</li>
	 *   <li>bits 16..23 : red</li>
	 *   <li>bits 8..15 : green</li>
	 *   <li>bits 0..7 : blue</li>
	 * </ul>
	 *
	 * @param  colour
	 *           the colour that will be converted.
	 * @return the 32-bit ARGB value that corresponds to {@code colour}.
	 */

	public static int colourToArgb(
		Color	colour)
	{
		return colourValueToInt(colour.getOpacity())  << 24
				| colourValueToInt(colour.getRed())   << 16
				| colourValueToInt(colour.getGreen()) << 8
				| colourValueToInt(colour.getBlue());
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified normalised colour value &mdash; a value in the range 0.0 to 1.0 &mdash; to an integer in
	 * the range 0 to 255 and returns a two-digit hexadecimal string representation of the integer.
	 *
	 * @param  value
	 *           the value that will be converted.
	 * @return two-digit hexadecimal string representation of {@code value}.
	 */

	public static String colourValueToHexString(
		double	value)
	{
		int intValue = colourValueToInt(value);
		return new String(new char[] { ColourConstants.HEX_DIGITS[(intValue >> 4) & 0x0F],
									   ColourConstants.HEX_DIGITS[intValue & 0x0F] });
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified colour to an eight-digit hexadecimal string representation in the form "#rrggbbaa" and
	 * returns the result.
	 *
	 * @param  colour
	 *           the colour that will be converted.
	 * @return an eight-digit hexadecimal string representation of {@code colour} in the form "#rrggbbaa".
	 */

	public static String colourToHexString(
		Color	colour)
	{
		StringBuilder buffer = new StringBuilder();
		buffer.append('#');
		buffer.append(colourValueToHexString(colour.getRed()));
		buffer.append(colourValueToHexString(colour.getGreen()));
		buffer.append(colourValueToHexString(colour.getBlue()));
		buffer.append(colourValueToHexString(colour.getOpacity()));
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified colour to a string representation of its RGB component values and its opacity (if less
	 * than 1) and returns the result.
	 *
	 * @param  colour
	 *           the colour that will be converted.
	 * @return the string representation of {@code colour}.
	 */

	public static String colourToRgbString(
		Color	colour)
	{
		// Initialise buffer
		StringBuilder buffer = new StringBuilder(32);

		// Append RGB component values
		buffer.append(colourValueToInt(colour.getRed()));
		buffer.append(ColourConstants.RGB_SEPARATOR);
		buffer.append(colourValueToInt(colour.getGreen()));
		buffer.append(ColourConstants.RGB_SEPARATOR);
		buffer.append(colourValueToInt(colour.getBlue()));

		// Append opacity
		double opacity = colour.getOpacity();
		if (opacity < 1.0)
		{
			buffer.append(ColourConstants.RGB_SEPARATOR);
			buffer.append(ColourConstants.OPACITY_FORMATTER.format(opacity));
		}

		// Return string
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified colour to its string representation in CSS RGB or RGBA format and returns the result.
	 * <p>
	 * If the opacity of the colour is less than 1, the string representation will be in RGBA format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgba(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>,</b>
	 * &lt;<i>opacity</i>&gt;<b>)</b><br>otherwise, it will be in RGB format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgb(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>)</b>
	 * </p>
	 *
	 * @param  colour
	 *           the colour that will be converted.
	 * @return the string representation of {@code colour} in CSS RGB or RGBA format.
	 */

	public static String colourToCssRgbaString(
		Color	colour)
	{
		return colourToCssRgbaString(colour, false);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the specified colour to its string representation in CSS RGB or RGBA format and returns the result.
	 * <p>
	 * If the {@code explicitAlpha} option is chosen or the opacity of the colour is less than 1, the string
	 * representation will be in RGBA format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgba(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>,</b>
	 * &lt;<i>opacity</i>&gt;<b>)</b><br>otherwise, it will be in RGB format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgb(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>)</b>
	 * </p>
	 *
	 * @param  colour
	 *           the colour that will be converted.
	 * @param  explicitAlpha
	 *           if {@code true}, the string representation will be in RGBA format.
	 * @return the string representation of {@code colour} in CSS RGB or RGBA format.
	 */

	public static String colourToCssRgbaString(
		Color	colour,
		boolean	explicitAlpha)
	{
		// Initialise buffer
		StringBuilder buffer = new StringBuilder(32);

		// Initialise 'has alpha' flag
		double opacity = colour.getOpacity();
		boolean hasAlpha = explicitAlpha || (opacity < 1.0);

		// Append RGB prefix
		buffer.append(hasAlpha ? ColourConstants.RGBA_PREFIX : ColourConstants.RGB_PREFIX);
		buffer.append('(');

		// Append RGB component values
		buffer.append(colourValueToInt(colour.getRed()));
		buffer.append(ColourConstants.RGB_SEPARATOR);
		buffer.append(colourValueToInt(colour.getGreen()));
		buffer.append(ColourConstants.RGB_SEPARATOR);
		buffer.append(colourValueToInt(colour.getBlue()));

		// Append opacity
		if (hasAlpha)
		{
			buffer.append(ColourConstants.RGB_SEPARATOR);
			buffer.append(ColourConstants.OPACITY_FORMATTER.format(opacity));
		}

		// Append suffix
		buffer.append(')');

		// Return string
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * <p style="margin-bottom: 0.25em;">
	 * Parses the specified string as a representation of a colour and returns the result.  The string is expected to
	 * consist of one, two, three or four comma-separated components:
	 * </p>
	 * <ol style="margin-top: 0.25em;">
	 *   <li>
	 *     <div>
	 *       <i>grey</i>
	 *     </div>
	 *     <div style="padding-left: 1em;">
	 *       where <i>grey</i> is an integer in the interval [0, 255] that is the value of the red, green and blue
	 *       components.  The opacity is 1.
	 *     </div>
	 *   </li>
	 *   <li>
	 *     <div>
	 *       <i>grey</i>, <i>opacity</i>
	 *     </div>
	 *     <div style="padding-left: 1em;">
	 *       where <i>grey</i> is an integer in the interval [0, 255] that is the value of the red, green and blue
	 *       components, and <i>opacity</i> is a floating-point value in the interval [0, 1].
	 *     </div>
	 *   </li>
	 *   <li>
	 *     <div>
	 *       <i>red</i>, <i>green</i>, <i>blue</i>
	 *     </div>
	 *     <div style="padding-left: 1em;">
	 *       where <i>red</i>, <i>green</i> and <i>blue</i> are integers in the interval [0, 255].  The opacity is 1.
	 *     </div>
	 *   </li>
	 *   <li>
	 *     <div>
	 *       <i>red</i>, <i>green</i>, <i>blue</i>, <i>opacity</i>
	 *     </div>
	 *     <div style="padding-left: 1em;">
	 *       where <i>red</i>, <i>green</i> and <i>blue</i> are integers in the interval [0, 255], and <i>opacity</i> is
	 *       a floating-point value in the interval [0, 1].
	 *     </div>
	 *   </li>
	 * </ol>
	 * <p>
	 * There may be optional whitespace on either side of each component.
	 * </p>
	 *
	 * @param  str
	 *           the string that will be parsed.
	 * @return the colour that is represented by {@code str}.
	 * @throws IllegalArgumentException
	 *           if the input string is not a valid representation of a colour.
	 */

	public static Color parseRgb(
		String	str)
	{
		// Split input string into components
		String[] strs = str.strip().split(ColourConstants.RGB_SEPARATOR_REGEX, -1);
		int numComponents = strs.length;
		if (numComponents > 4)
			throw new IllegalArgumentException("Malformed colour");

		// Parse RGB components
		int[] rgb = new int[3];
		int value = 0;
		int index = 0;
		while (index < rgb.length)
		{
			if ((index == 0) || (numComponents > 2))
			{
				String str0 = strs[index];
				try
				{
					value = Integer.parseInt(str0);
					if ((value < ColourConstants.MIN_RGB_COMPONENT_VALUE)
							|| (value > ColourConstants.MAX_RGB_COMPONENT_VALUE))
						throw new IllegalArgumentException("RGB component out of bounds: " + value);
				}
				catch (NumberFormatException e)
				{
					throw new IllegalArgumentException("Invalid RGB component: " + str0);
				}
			}
			rgb[index++] = value;
		}

		// Parse opacity
		double opacity = ColourConstants.MAX_OPACITY;
		if ((numComponents == 2) || (numComponents == 4))
		{
			String str0 = strs[numComponents - 1];
			try
			{
				opacity = Double.parseDouble(str0);
				if ((opacity < ColourConstants.MIN_OPACITY) || (opacity > ColourConstants.MAX_OPACITY))
					throw new IllegalArgumentException("Opacity out of bounds: " + opacity);
			}
			catch (NumberFormatException e)
			{
				throw new IllegalArgumentException("Invalid opacity:" + str0);
			}
		}

		// Return colour
		return Color.rgb(rgb[0], rgb[1], rgb[2], opacity);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a linear interpolation of the red, green and blue values of the two specified colours at the specified
	 * fraction of each value, and returns the resulting colour.
	 * <p>
	 * Each value (red, green, blue) is calculated thus:<br>
	 * <i>v</i> = <i>v1</i> + (<i>v2</i> - <i>v1</i>) * <i>fraction</i>
	 * </p>
	 *
	 * @param  colour1
	 *           the first colour.
	 * @param  colour2
	 *           the second colour.
	 * @param  fraction
	 *           the fraction between {@code colour1} and {@code colour2}, in the interval [0, 1].
	 * @return the colour that results from interpolating the red, green and blue values of {@code colour1} and {@code
	 *         colour2} at {@code fraction}.
	 */

	public static Color interpolateRgb(
		Color	colour1,
		Color	colour2,
		double	fraction)
	{
		// Validate arguments
		if ((fraction < 0.0) || (fraction > 1.0))
			throw new IllegalArgumentException(FRACTION_OUT_OF_BOUNDS_STR + fraction);

		// Calculate red
		double red1 = colour1.getRed();
		double red = red1 + (colour2.getRed() - red1) * fraction;

		// Calculate green
		double green1 = colour1.getGreen();
		double green = green1 + (colour2.getGreen() - green1) * fraction;

		// Calculate blue
		double blue1 = colour1.getBlue();
		double blue = blue1 + (colour2.getBlue() - blue1) * fraction;

		// Return colour
		return Color.color(red, green, blue);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a linear interpolation of the hue, saturation and brightness values of the two specified colours at the
	 * specified fraction of each value, and returns the resulting colour.
	 * <p>
	 * Each value (hue, saturation, brightness) is calculated thus:<br>
	 * <i>v</i> = <i>v1</i> + (<i>v2</i> - <i>v1</i>) * <i>fraction</i>
	 * </p>
	 *
	 * @param  colour1
	 *           the first colour.
	 * @param  colour2
	 *           the second colour.
	 * @param  fraction
	 *           the fraction between {@code colour1} and {@code colour2}, in the interval [0, 1].
	 * @return the colour that results from interpolating the hue, saturation and brightness of {@code colour1} and
	 *         {@code colour2} at {@code fraction}.
	 */

	public static Color interpolateHsb(
		Color	colour1,
		Color	colour2,
		double	fraction)
	{
		// Validate arguments
		if ((fraction < 0.0) || (fraction > 1.0))
			throw new IllegalArgumentException(FRACTION_OUT_OF_BOUNDS_STR + fraction);

		// Calculate hue
		double hue1 = colour1.getHue();
		double hue = hue1 + (colour2.getHue() - hue1) * fraction;
		while (hue < 0.0)
			hue += 360.0;
		while (hue >= 360.0)
			hue -= 360.0;

		// Calculate saturation
		double saturation1 = colour1.getSaturation();
		double saturation = saturation1 + (colour2.getSaturation() - saturation1) * fraction;

		// Calculate brightness
		double brightness1 = colour1.getBrightness();
		double brightness = brightness1 + (colour2.getBrightness() - brightness1) * fraction;

		// Return colour
		return Color.hsb(hue, saturation, brightness);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
