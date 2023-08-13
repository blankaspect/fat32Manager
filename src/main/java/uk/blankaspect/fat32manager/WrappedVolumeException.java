/*====================================================================*\

WrappedVolumeException.java

Class: wrapped volume exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import uk.blankaspect.driveio.VolumeException;

//----------------------------------------------------------------------


// CLASS: WRAPPED VOLUME EXCEPTION


public class WrappedVolumeException
	extends RuntimeException
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public WrappedVolumeException(
		VolumeException	exception)
	{
		// Call superclass constructor
		super(exception);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
