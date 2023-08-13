/*====================================================================*\

LoggerUtils.java

Class: logger-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.logging;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.file.Path;

import uk.blankaspect.common.exception2.ExceptionUtils;

//----------------------------------------------------------------------


// CLASS: LOGGER-RELATED UTILITY METHODS


/**
 * This class provides utility methods that relate to a {@link Logger}.
 */

public class LoggerUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The separator between the keys of fields in a system property. */
	private static final	String	FIELD_SEPARATOR	= ",";

	/** The prefix of Java system properties. */
	private static final	String	SYSTEM_PROPERTY_PREFIX	= "blankaspect.log.";

	/** Keys of Java system properties. */
	private interface SystemPropertyKey
	{
		String	FIELDS					= SYSTEM_PROPERTY_PREFIX + "fields";
		String	FILE					= SYSTEM_PROPERTY_PREFIX + "file";
		String	NEW_LINE_AFTER_FIELDS	= SYSTEM_PROPERTY_PREFIX + "newLineAfterFields";
		String	NUM_RETAINED_LINES		= SYSTEM_PROPERTY_PREFIX + "numRetainedLines";
		String	PREFIX					= SYSTEM_PROPERTY_PREFIX + "prefix";
		String	SUFFIX					= SYSTEM_PROPERTY_PREFIX + "suffix";
		String	THRESHOLD				= SYSTEM_PROPERTY_PREFIX + "threshold";
	}

	/** Error messages. */
	private interface ErrorMsg
	{
		String	INVALID_SYSTEM_PROPERTY	= "Key: %s\nValue: %s\nThe value of the system property is invalid.";
		String	FAILED_TO_OPEN_LOG_FILE	= "Failed to open the log file: ";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private LoggerUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Initialises the single instance of {@link Logger} and, if a file is specified and the logging threshold is not
	 * {@linkplain LogLevel#OFF OFF}, opens the log file.  The logger is initialised with values that are specified in
	 * the following system properties:
	 * <ul>
	 *   <li>org.lhasalimited.log.threshold</li>
	 *   <li>org.lhasalimited.log.prefix</li>
	 *   <li>org.lhasalimited.log.suffix</li>
	 *   <li>org.lhasalimited.log.fields</li>
	 *   <li>org.lhasalimited.log.newLineAfterFields</li>
	 * </ul>
	 * <p>
	 * The arguments of {@link Logger#open(Path, int)} are specified by the following system properties:
	 * </p>
	 * <ul>
	 *   <li>org.lhasalimited.log.file</li>
	 *   <li>org.lhasalimited.log.numRetainedLines</li>
	 * </ul>
	 * <p>
	 * If there is no system property for a value, the specified default value is used instead.
	 * </p>
	 *
	 * @param defaultThreshold
	 *          the default logging threshold, ignored if it is {@code null}.
	 * @param defaultParams
	 *          the default message-format parameters, ignored if it is {@code null}.
	 * @param defaultFile
	 *          the default location of the log file.
	 * @param defaultNumRetainedLines
	 *          the default number of lines of a previous log file that will be retained.
	 */

	public static void openLogger(
		LogLevel		defaultThreshold,
		Logger.Params	defaultParams,
		Path			defaultFile,
		int				defaultNumRetainedLines)
	{
		// Get logger
		Logger logger = Logger.INSTANCE;

		// Threshold
		String key = SystemPropertyKey.THRESHOLD;
		String value = System.getProperty(key);
		if (value == null)
		{
			if (defaultThreshold != null)
				logger.setThreshold(defaultThreshold);
		}
		else
		{
			LogLevel threshold = LogLevel.forKey(value);
			if (threshold == null)
				ExceptionUtils.printStderrLocated(String.format(ErrorMsg.INVALID_SYSTEM_PROPERTY, key, value));
			else
				logger.setThreshold(threshold);
		}

		// Prefix
		key = SystemPropertyKey.PREFIX;
		value = System.getProperty(key);
		if (value == null)
		{
			if (defaultParams != null)
				logger.setPrefix(defaultParams.getPrefix());
		}
		else
			logger.setPrefix(value);

		// Suffix
		key = SystemPropertyKey.SUFFIX;
		value = System.getProperty(key);
		if (value == null)
		{
			if (defaultParams != null)
				logger.setSuffix(defaultParams.getSuffix());
		}
		else
			logger.setSuffix(value);

		// Fields
		key = SystemPropertyKey.FIELDS;
		value = System.getProperty(key);
		if (value == null)
		{
			if (defaultParams != null)
				logger.setFields(defaultParams.getFields());
		}
		else
		{
			logger.clearFields();
			String message = String.format(ErrorMsg.INVALID_SYSTEM_PROPERTY, key, value);
			String[] strs = value.split(FIELD_SEPARATOR);
			for (String str : strs)
			{
				Logger.Field field = Logger.Field.forKey(str.trim());
				if (field == null)
				{
					if (message != null)
					{
						ExceptionUtils.printStderrLocated(message);
						message = null;
					}
				}
				else
					logger.addField(field);
			}
		}

		// New line after fields
		key = SystemPropertyKey.NEW_LINE_AFTER_FIELDS;
		value = System.getProperty(key);
		if (value == null)
		{
			if (defaultParams != null)
				logger.setNewLineAfterFields(defaultParams.isNewLineAfterFields());
		}
		else
			logger.setNewLineAfterFields(Boolean.parseBoolean(value));

		// File
		key = SystemPropertyKey.FILE;
		value = System.getProperty(key);
		Path file = (value == null) ? defaultFile : Path.of(value);

		// Number of lines to retain
		int numRetainedLines = defaultNumRetainedLines;
		key = SystemPropertyKey.NUM_RETAINED_LINES;
		value = System.getProperty(key);
		if (value != null)
		{
			try
			{
				numRetainedLines = Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				ExceptionUtils.printStderrLocated(String.format(ErrorMsg.INVALID_SYSTEM_PROPERTY, key, value));
			}
		}

		// Open file
		if ((file != null) && (logger.getThreshold() != LogLevel.OFF))
		{
			try
			{
				logger.open(file, numRetainedLines);
			}
			catch (Logger.LoggerException e)
			{
				ExceptionUtils.printStderrLocated(ErrorMsg.FAILED_TO_OPEN_LOG_FILE + file.toAbsolutePath());
			}
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
