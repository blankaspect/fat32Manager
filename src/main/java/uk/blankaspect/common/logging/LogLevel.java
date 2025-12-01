/*====================================================================*\

LogLevel.java

Enumeration: logging levels.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.logging;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Arrays;

//----------------------------------------------------------------------


// ENUMERATION: LOGGING LEVELS


/**
 * This is an enumeration of the logging levels that are used by {@link Logger}.
 */

public enum LogLevel
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * Detailed information.
	 */
	TRACE,

	/**
	 * Debugging information.
	 */
	DEBUG,

	/**
	 * Information.
	 */
	INFO,

	/**
	 * A warning.
	 */
	WARNING,

	/**
	 * An error.
	 */
	ERROR,

	/**
	 * No logging.
	 */
	OFF;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a logging level.
	 */

	private LogLevel()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the logging level that is associated with the specified key.
	 *
	 * @param  key
	 *           the key whose associated logging level is required.
	 * @return the logging level that is associated with {@code key}, or {@code null} if there is no such logging level.
	 */

	public static LogLevel forKey(String key)
	{
		return Arrays.stream(values()).filter(value -> value.getKey().equals(key)).findFirst().orElse(null);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the key that is associated with this logging level.
	 *
	 * @return the key that is associated with this logging level.
	 */

	public String getKey()
	{
		return name().toLowerCase();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
