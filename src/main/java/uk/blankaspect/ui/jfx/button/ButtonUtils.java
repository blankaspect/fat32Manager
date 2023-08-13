/*====================================================================*\

ButtonUtils.java

Class: button-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.button;

//----------------------------------------------------------------------


// IMPORTS


import javafx.geometry.Insets;

import javafx.scene.control.ButtonBase;

import javafx.scene.paint.Color;

import uk.blankaspect.common.tuple.StrKVPair;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

import uk.blankaspect.ui.jfx.style.StyleProperty;
import uk.blankaspect.ui.jfx.style.StyleUtils;

//----------------------------------------------------------------------


// CLASS: BUTTON-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain ButtonBase button-like controls}.
 */

public class ButtonUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The suffix that denotes pixel units in the value of a style property. */
	private static final	String	PIXEL_UNIT_SUFFIX	= "px";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ButtonUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the gap between the actuator and the label of the specified button to the specified value.
	 *
	 * @param button
	 *          the button whose actuator&ndash;label gap will be set.
	 * @param gap
	 *          the gap between the actuator and the label
	 */

	public static void setActuatorLabelGap(
		ButtonBase	button,
		double		gap)
	{
		// Get padding of button label
		Insets padding = button.getLabelPadding();

		// Create style property for padding of button label
		StringBuilder buffer = new StringBuilder(64);
		buffer.append(padding.getTop());
		buffer.append(PIXEL_UNIT_SUFFIX);
		buffer.append(' ');
		buffer.append(padding.getRight());
		buffer.append(PIXEL_UNIT_SUFFIX);
		buffer.append(' ');
		buffer.append(padding.getBottom());
		buffer.append(PIXEL_UNIT_SUFFIX);
		buffer.append(' ');
		buffer.append(gap);
		buffer.append(PIXEL_UNIT_SUFFIX);

		// Set padding of button label through its style property
		StyleUtils.setProperty(button, StyleProperty.LABEL_PADDING, buffer.toString());
	}

	//------------------------------------------------------------------

	/**
	 * Sets the colour of the background and inner border of the specified button to the specified value.
	 *
	 * @param button
	 *          the target button.
	 * @param colour
	 *          the value to which the colour of the background and inner border of {@code button} will be set.
	 */

	public static void setBackgroundColour(
		ButtonBase button,
		Color      colour)
	{
		String colourStr = ColourUtils.colourToHexString(colour);
		StyleUtils.setProperties(button, StrKVPair.of(StyleProperty.BODY_COLOUR, colourStr),
								 StrKVPair.of(StyleProperty.INNER_BORDER, colourStr));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
