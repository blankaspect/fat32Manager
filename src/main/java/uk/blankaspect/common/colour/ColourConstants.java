/*====================================================================*\

ColourConstants.java

Interface: colour-related constants.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.colour;

//----------------------------------------------------------------------


// IMPORTS


import java.text.DecimalFormat;

//----------------------------------------------------------------------


// CLASS: COLOUR-RELATED CONSTANTS


/**
 * This interface defines some colour-related constants.
 */

public interface ColourConstants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The minimum value of an RGB component of a colour. */
	int		MIN_RGB_COMPONENT_VALUE	= 0;

	/** The maximum value of an RGB component of a colour. */
	int		MAX_RGB_COMPONENT_VALUE	= 255;

	/** The minimum value of the opacity of a colour. */
	double	MIN_OPACITY	= 0.0;

	/** The maximum value of the opacity of a colour. */
	double	MAX_OPACITY	= 1.0;

	/** The formatter of the alpha component of a colour in HTML or CSS RGBA format. */
	DecimalFormat	OPACITY_FORMATTER	= new DecimalFormat("0.0##");

	/** The mask of RGB components in a 32-bit ARGB representation of a colour. */
	int		RGB_MASK	= 0xFFFFFF;

	/** The prefix of the string representation of a colour in HTML or CSS RGB format. */
	String	RGB_PREFIX	= "rgb";

	/** The prefix of the string representation of a colour in HTML or CSS RGBA format. */
	String	RGBA_PREFIX	= "rgba";

	/** The separator between RGB components of an output string representation of a colour. */
	String	RGB_SEPARATOR	= ", ";

	/** The regular-expression pattern of the separator between RGB components of the input string representation of a
		colour. */
	String	RGB_SEPARATOR_REGEX	= "\\s*,\\s*";

	/** Upper-case hexadecimal-digit characters. */
	char[]	HEX_DIGITS	= "0123456789ABCDEF".toCharArray();

}

//----------------------------------------------------------------------
