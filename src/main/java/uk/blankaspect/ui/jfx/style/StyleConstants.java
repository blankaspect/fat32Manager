/*====================================================================*\

StyleConstants.java

Interface: style constants.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// INTERFACE: STYLE CONSTANTS


public interface StyleConstants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	String	CLASS_PREFIX	= "-ba-";

	String	VALUE_SEPARATOR	= ", ";

	char	COLOUR_KEY_PREFIX_CHAR	= '&';
	String	COLOUR_KEY_PREFIX		= Character.toString(COLOUR_KEY_PREFIX_CHAR);

	/** Keys of system properties. */
	public interface SystemPropertyKey
	{
		String	NO_STYLE_SHEET	= "blankaspect.ui.jfx.noStyleSheet";
	}

}

//----------------------------------------------------------------------
