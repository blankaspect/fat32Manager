/*====================================================================*\

IFunction0.java

Interface: function with no parameters.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.function;

//----------------------------------------------------------------------


// INTERFACE: FUNCTION WITH NO PARAMETERS


/**
 * This functional interface defines the method that must be implemented by a function with no parameters.
 *
 * @param <R>
 *          the type of the return value.
 */

@FunctionalInterface
public interface IFunction0<R>
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Invokes this function.
	 */

	R invoke();

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
