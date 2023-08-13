/*====================================================================*\

VolumeException.java

Class: volume exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.driveio;

//----------------------------------------------------------------------


// CLASS: VOLUME EXCEPTION


public class VolumeException
	extends Exception
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public VolumeException(
		String		message,
		Object...	replacements)
	{
		// Call superclass constructor
		super(String.format(message, replacements));
	}

	//------------------------------------------------------------------

	public VolumeException(
		String		message,
		Throwable	cause,
		Object...	replacements)
	{
		// Call superclass constructor
		super(String.format(message, replacements), cause);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
