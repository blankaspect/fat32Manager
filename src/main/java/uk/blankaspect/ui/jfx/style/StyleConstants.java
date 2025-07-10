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

	/** The prefix of {@code uk.blankaspect} style classes. */
	String	CLASS_PREFIX		= "-ba-";

	/** The prefix of {@code uk.blankaspect} style classes that belong to applications. */
	String	APP_CLASS_PREFIX	= CLASS_PREFIX + "-app-";

	/** The separator between the elements of a multi-element JavaFX property value of a CSS rule. */
	String	VALUE_SEPARATOR	= ", ";

	/** The prefix of a colour key in a JavaFX property value of a CSS rule. */
	char	COLOUR_KEY_PREFIX_CHAR	= '&';

	/** The prefix of a colour key in a JavaFX property value of a CSS rule. */
	String	COLOUR_KEY_PREFIX		= Character.toString(COLOUR_KEY_PREFIX_CHAR);

	/** Identifiers of nodes. */
	interface NodeId
	{
		String	APP_MAIN_ROOT	= "appMainRoot";
	}

	/** Keys of system properties. */
	interface SystemPropertyKey
	{
		String	NO_STYLE_SHEET	= "blankaspect.ui.jfx.noStyleSheet";
	}

}

//----------------------------------------------------------------------
