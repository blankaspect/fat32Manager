/*====================================================================*\

MessageConstants.java

Interface: message-related constants.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.message;

//----------------------------------------------------------------------


// INTERFACE: MESSAGE-RELATED CONSTANTS


public interface MessageConstants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A character that may be used to separate adjacent parts of a message. */
	char	SEPARATOR_CHAR	= '\u200B';		// zero-width space;

	/** A string that may be used to separate adjacent parts of a message. */
	String	SEPARATOR		= Character.toString(SEPARATOR_CHAR);

	/** A space followed by a string that may be used to separate adjacent parts of a message. */
	String	SPACE_SEPARATOR	= " " + SEPARATOR;

	/** A character that may be used to separate adjacent parts of a message that are represented by separate labels. */
	char	LABEL_SEPARATOR_CHAR	= '\u000B';		// vertical tab

	/** A string that may be used to separate adjacent parts of a message that are represented by separate labels. */
	String	LABEL_SEPARATOR			= Character.toString(LABEL_SEPARATOR_CHAR);

}

//----------------------------------------------------------------------
