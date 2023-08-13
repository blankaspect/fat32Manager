/*====================================================================*\

IDiscretiser.java

Interface: discretiser.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.number;

//----------------------------------------------------------------------


// INTERFACE: DISCRETISER


/**
 * This functional interface defines the method that must be implemented by a discretiser that maps continuous values to
 * discrete values.
 */

@FunctionalInterface
public interface IDiscretiser
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a discrete value for the specified continuous value.
	 *
	 * @param  value
	 *           the continuous value for which a discrete value is desired.
	 * @return a discrete value for {@code value}.
	 */

	int discretise(double value);

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
