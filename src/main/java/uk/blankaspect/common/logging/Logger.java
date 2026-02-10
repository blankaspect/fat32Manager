/*====================================================================*\

Logger.java

Class: logger.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.logging;

//----------------------------------------------------------------------


// IMPORTS


import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import uk.blankaspect.common.exception2.ExceptionUtils;
import uk.blankaspect.common.exception2.UnexpectedRuntimeException;

import uk.blankaspect.common.filesystem.PathUtils;

import uk.blankaspect.common.stack.StackUtils;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// CLASS: LOGGER


/**
 * This class implements a simple logging facility that writes text to a file.  The single instance of this class is
 * accessed through its {@link #INSTANCE} field.
 * <p>
 * The methods for writing text are of two kinds:
 * </p>
 * <ul>
 *   <li>low-level methods for writing unformatted text and line feeds, and</li>
 *   <li>methods for writing <i>messages</i> that may have have optional basic formatting.</li>
 * </ul>
 * <p>
 * The writing of messages to the log is controlled by the conventional mechanism of predefined {@linkplain LogLevel
 * logging levels}: a level, referred to as the <i>threshold</i>, is set on the logger, and a message is logged only if
 * its logging level is greater than or equal to the threshold.  The logging level of a message may be set explicitly
 * with the {@code log} methods (eg, {@link #log(LogLevel, CharSequence, Throwable)}) or implicitly by one of the
 * methods whose logging level can be inferred from its name (eg, {@link #debug(CharSequence, Throwable)}).
 * </p>
 * <p>
 * A message may be formatted with the following five optional elements:
 * </p>
 * <ul>
 *   <li>a <i>prefix</i>,</li>
 *   <li>a <i>suffix</i>,</li>
 *   <li>three <i>fields</i>: a timestamp, the logging level and the source-file location.</li>
 * </ul>
 * <p>
 * The presence of each of the five elements is controlled independently.  If they are present, the prefix and suffix
 * are each followed by a line feed (U+000A), so they occupy separate lines from the body of the message.  The fields
 * precede the body of the message and may be either on a separate line from it or on the same line, separated by a
 * space, according to the state of a {@linkplain #setNewLineAfterFields(boolean) flag}.  The three fields always appear
 * in the order given above.
 * </p>
 * <p>
 * The formatting elements are encapsulated as a set of {@linkplain Params parameters}.  The current parameters may be
 * set individually or collectively.  To allow the global formatting of messages to be overridden locally, the current
 * set of parameters may be pushed on a stack with {@link #saveParams()} and restored with {@link #restoreParams()}.
 * </p>
 * <p>
 * The location of the log file is specified when the {@linkplain #open(Path, int) file is opened}.  The second
 * parameter of the {@code open} method controls the number of lines of a previous log file at the same location that
 * are retained when the file is reopened.  A log file may be closed explicitly with {@link #close()}.  The static
 * initialiser of this class adds a {@linkplain Runtime#addShutdownHook(Thread) shutdown hook} to close any open log
 * file when the Java VM terminates.
 * </p>
 * <p>
 * Text is written to a log file via a {@linkplain BufferedWriter buffered output stream}, which is flushed after a
 * line feed is written with {@link #writeEol()}.  The message-writing methods flush the output stream at each of the
 * following points, if the respective elements are present:
 * </p>
 * <ul>
 *   <li>after the prefix,</li>
 *   <li>after the fields and message text,</li>
 *   <li>after the stack trace of the exception,</li>
 *   <li>after the suffix.</li>
 * </ul>
 */

public class Logger
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The single instance of {@code Logger}. */
	public static final		Logger	INSTANCE	= new Logger();

	/** The pattern with which a timestamp field is formatted. */
	private static final	DateTimeFormatter	TIMESTAMP_FORMATTER	=
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	/** The name of the thread that is run by the shutdown hook to close a log file. */
	private static final	String	CLOSE_THREAD_NAME	= "closeLogger";

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FILE_IS_OPEN =
				"A log file is already open.";

		String	FAILED_TO_OPEN_FILE =
				"Failed to open the log file.";

		String	FAILED_TO_CLOSE_FILE =
				"Failed to close the log file.";

		String	ERROR_READING_FILE =
				"An error occurred while reading the log file.";

		String	ERROR_WRITING_FILE =
				"An error occurred while writing the log file.";

		String	FILE_ACCESS_NOT_PERMITTED =
				"Access to the log file was not permitted.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The logging threshold to which the logging level of a message is compared. */
	private	LogLevel		threshold;

	/** The current parameters for formatting a message. */
	private	Params			params;

	/** The stack on which formatting parameters may be saved. */
	private	Deque<Params>	stack;

	/** The location of the log file. */
	private	Path			file;

	/** The output stream that is opened on the {@linkplain #file log file}. */
	private	BufferedWriter	outStream;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Close log file on shutdown
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			try
			{
				INSTANCE.close();
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		},
		CLOSE_THREAD_NAME));
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a logger.
	 */

	private Logger()
	{
		// Initialise instance variables
		threshold = LogLevel.INFO;
		params = new Params(null, null, EnumSet.noneOf(Field.class), true);
		stack = new ArrayDeque<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the logging threshold to which the logging level of a message is compared to determine whether the
	 * message is written to the log.
	 *
	 * @return the logging threshold.
	 */

	public LogLevel getThreshold()
	{
		return threshold;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the logging threshold to the specified value.  When one of the message-writing methods is called, the
	 * message is written to the log only if its logging level is greater than or equal to the threshold.
	 *
	 * @param threshold
	 *          the value to which the logging threshold will be set.
	 */

	public void setThreshold(
		LogLevel	threshold)
	{
		if (threshold == null)
			throw new IllegalArgumentException("Null threshold");

		this.threshold = threshold;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the prefix of a message.
	 *
	 * @return the prefix of a message.
	 */

	public String getPrefix()
	{
		return params.prefix;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the prefix of a message to the specified value.
	 *
	 * @param prefix
	 *          the value to which the prefix of a message will be set; {@code null} for no prefix.
	 */

	public void setPrefix(
		String	prefix)
	{
		params.prefix = prefix;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the suffix of a message.
	 *
	 * @return the suffix of a message.
	 */

	public String getSuffix()
	{
		return params.suffix;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the suffix of a message to the specified value.
	 *
	 * @param suffix
	 *          the value to which the suffix of a message will be set; {@code null} for no suffix.
	 */

	public void setSuffix(
		String	suffix)
	{
		params.suffix = suffix;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the fields of a message.
	 *
	 * @return the fields of a message.
	 */

	public Set<Field> getFields()
	{
		return params.fields.clone();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the fields of a message to the specified value.
	 *
	 * @param fields
	 *          the value to which the fields of a message will be set.
	 */

	public void setFields(
		Field...	fields)
	{
		// Validate argument
		if (fields == null)
			throw new IllegalArgumentException("Null fields");

		// Update current parameters
		params.fields.clear();
		for (Field field : fields)
			params.fields.add(field);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the fields of a message to the specified value.
	 *
	 * @param fields
	 *          the value to which the fields of a message will be set.
	 */

	public void setFields(
		Iterable<? extends Field> fields)
	{
		// Validate argument
		if (fields == null)
			throw new IllegalArgumentException("Null fields");

		// Update current parameters
		params.fields.clear();
		for (Field field : fields)
			params.fields.add(field);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the current set of fields contains the specified value.
	 *
	 * @param  field
	 *           the field of interest.
	 * @return {@code true} if the current set of fields contains {@code field}.
	 */

	public boolean hasField(
		Field	field)
	{
		return params.fields.contains(field);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified field to the current set of fields.
	 *
	 * @param field
	 *          the field that will be added to the current set of fields.
	 */

	public void addField(
		Field	field)
	{
		// Validate argument
		if (field == null)
			throw new IllegalArgumentException("Null field");

		// Update current parameters
		params.fields.add(field);
	}

	//------------------------------------------------------------------

	/**
	 * Removes the specified field from the current set of fields.
	 *
	 * @param field
	 *          the field that will be removed from the current set of fields.
	 */

	public void removeField(
		Field	field)
	{
		params.fields.remove(field);
	}

	//------------------------------------------------------------------

	/**
	 * Removes all elements from the current set of fields.
	 */

	public void clearFields()
	{
		params.fields.clear();
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if fields are separated from the body of the message by a line feed (U+000A) when a message
	 * is written to the log; {@code false} if they are separated by a space character (U+0020).
	 *
	 * @return {@code true} if fields are separated from the body of the message by a line feed (U+000A) when a message
	 *         is written to the log; {@code false} if they are separated by a space character (U+0020).
	 */

	public boolean isNewLineAfterFields()
	{
		return params.newLineAfterFields;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the value of the flag that controls whether fields are separated from the body of the message by a line feed
	 * (U+000A) or a space character (U+0020) when a message is written to the log.
	 *
	 * @param newLineAfterFields
	 *          if {@code true}, fields will be separated from the body of the message by a line feed (U+000A) when a
	 *          message is written to the log; otherwise they will be separated by a space character (U+0020).
	 */

	public void setNewLineAfterFields(
		boolean	newLineAfterFields)
	{
		params.newLineAfterFields = newLineAfterFields;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the current set of message-format parameters.
	 *
	 * @return the current set of message-format parameters.
	 */

	public Params getParams()
	{
		return params;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the message-format parameters to the specified value.
	 *
	 * @param params
	 *          the value to which the message-format parameters will be set.
	 */

	public void setParams(
		Params	params)
	{
		// Validate argument
		if (params == null)
			throw new IllegalArgumentException("Null parameters");

		// Update instance variable
		this.params = params;
	}

	//------------------------------------------------------------------

	/**
	 * Pushes the current set of message-format parameters to a stack (LIFO queue), from which they can be retrieved
	 * with {@link #restoreParams()}.
	 */

	public void saveParams()
	{
		stack.push(params.clone());
	}

	//------------------------------------------------------------------

	/**
	 * Restores the message-format parameters from the last value that was pushed to the stack (LIFO queue) with {@link
	 * #saveParams()}.  If the stack is empty, this method has no effect.
	 */

	public void restoreParams()
	{
		if (!stack.isEmpty())
			params = stack.pop();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of the log file.
	 *
	 * @return the location of the log file, or {@code null} if no log file is open.
	 */

	public Path getFile()
	{
		return file;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if a log file is open.
	 *
	 * @return {@code true} if a log file is open.
	 */

	public boolean isOpen()
	{
		return (outStream != null);
	}

	//------------------------------------------------------------------

	/**
	 * Opens a log file at the specified location.  If the location denotes an existing text file, the specified number
	 * of lines from the end of the file will be retained.
	 *
	 * @param  location
	 *           the location of the log file.
	 * @param  numRetainedLines
	 *           the number of lines from the end of an existing file that will be retained.  If this is 0 or negative,
	 *           an existing file will be overwritten.
	 * @throws LoggerException
	 *           if
	 *           <ul>
	 *             <li>a log file is already open, or</li>
	 *             <li>an error occurred when opening the log file, or</li>
	 *             <li>an error occurred when reading or writing an existing file while removing lines from it.</li>
	 *           </ul>
	 */

	public void open(
		Path	location,
		int		numRetainedLines)
		throws LoggerException
	{
		// Validate arguments
		if (location == null)
			throw new IllegalArgumentException("Null location");

		// Test whether log file is open
		if (isOpen())
			throw new LoggerException(ErrorMsg.FILE_IS_OPEN, null, location);

		// Initialise options for opening file
		StandardOpenOption[] options = {};

		// If necessary, remove lines from start of file
		if ((numRetainedLines > 0) && Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS))
		{
			// Read log file
			List<String> lines = null;
			try
			{
				lines = Files.readAllLines(location);
			}
			catch (IOException e)
			{
				throw new LoggerException(ErrorMsg.ERROR_READING_FILE, e, location);
			}
			catch (SecurityException e)
			{
				throw new LoggerException(ErrorMsg.FILE_ACCESS_NOT_PERMITTED, e, location);
			}

			// Get number of lines in log
			int numLines = lines.size();

			// If number of lines exceeds number of lines to keep, remove lines from start of file
			if (numLines > numRetainedLines)
			{
				// Remove lines from start of list
				lines = lines.subList(numLines - numRetainedLines, numLines);

				// Write remaining lines to file
				try
				{
					Files.writeString(location, StringUtils.join('\n', true, lines));
				}
				catch (IOException e)
				{
					throw new LoggerException(ErrorMsg.ERROR_WRITING_FILE, e, location);
				}
				catch (SecurityException e)
				{
					throw new LoggerException(ErrorMsg.FILE_ACCESS_NOT_PERMITTED, e, location);
				}
			}

			// Create option for appending to file
			options = new StandardOpenOption[] { StandardOpenOption.WRITE, StandardOpenOption.APPEND };
		}

		// Open output stream on file
		try
		{
			outStream = Files.newBufferedWriter(location, options);
			this.file = location;
		}
		catch (IOException e)
		{
			throw new LoggerException(ErrorMsg.FAILED_TO_OPEN_FILE, e, location);
		}
		catch (SecurityException e)
		{
			throw new LoggerException(ErrorMsg.FILE_ACCESS_NOT_PERMITTED, e, location);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Closes an open log file.  If no log file is open, this method has no effect.
	 *
	 * @throws LoggerException
	 *           if an error occurs when closing the log file.
	 */

	public void close()
	{
		if (isOpen())
		{
			// Invalidate instance variables
			Path file0 = file;
			file = null;

			BufferedWriter outStream0 = outStream;
			outStream = null;

			// Close file
			try
			{
				outStream0.close();
			}
			catch (IOException e)
			{
				throw new LoggerException(ErrorMsg.FAILED_TO_CLOSE_FILE, e, file0);
			}
		}
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text to the log if the logging threshold is equal to {@linkplain
	 * LogLevel#TRACE TRACE}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void trace(
		CharSequence	text)
	{
		write(LogLevel.TRACE, text, null);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing a stack trace of the specified exception to the log if the logging threshold is equal
	 * to {@linkplain LogLevel#TRACE TRACE}.
	 *
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void trace(
		Throwable	exception)
	{
		write(LogLevel.TRACE, null, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text and a stack trace of the specified exception to the log if the
	 * logging threshold is equal to {@linkplain LogLevel#TRACE TRACE}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void trace(
		CharSequence	text,
		Throwable		exception)
	{
		write(LogLevel.TRACE, text, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text to the log if the logging threshold is less than or equal to
	 * {@linkplain LogLevel#DEBUG DEBUG}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void debug(
		CharSequence	text)
	{
		write(LogLevel.DEBUG, text, null);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing a stack trace of the specified exception to the log if the logging threshold is less
	 * than or equal to {@linkplain LogLevel#DEBUG DEBUG}.
	 *
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void debug(
		Throwable	exception)
	{
		write(LogLevel.DEBUG, null, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text and a stack trace of the specified exception to the log if the
	 * logging threshold is less than or equal to {@linkplain LogLevel#DEBUG DEBUG}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void debug(
		CharSequence	text,
		Throwable		exception)
	{
		write(LogLevel.DEBUG, text, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text to the log if the logging threshold is less than or equal to
	 * {@linkplain LogLevel#INFO INFO}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void info(
		CharSequence	text)
	{
		write(LogLevel.INFO, text, null);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing a stack trace of the specified exception to the log if the logging threshold is less
	 * than or equal to {@linkplain LogLevel#INFO INFO}.
	 *
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void info(
		Throwable	exception)
	{
		write(LogLevel.INFO, null, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text and a stack trace of the specified exception to the log if the
	 * logging threshold is less than or equal to {@linkplain LogLevel#INFO INFO}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void info(
		CharSequence	text,
		Throwable		exception)
	{
		write(LogLevel.INFO, text, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text to the log if the logging threshold is less than or equal to
	 * {@linkplain LogLevel#WARNING WARNING}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void warning(
		CharSequence	text)
	{
		write(LogLevel.WARNING, text, null);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing a stack trace of the specified exception to the log if the logging threshold is less
	 * than or equal to {@linkplain LogLevel#WARNING WARNING}.
	 *
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void warning(
		Throwable	exception)
	{
		write(LogLevel.WARNING, null, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text and a stack trace of the specified exception to the log if the
	 * logging threshold is less than or equal to {@linkplain LogLevel#WARNING WARNING}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */
	public void warning(
		CharSequence	text,
		Throwable		exception)
	{
		write(LogLevel.WARNING, text, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text to the log if the logging threshold is less than or equal to
	 * {@linkplain LogLevel#ERROR ERROR}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void error(
		CharSequence	text)
	{
		write(LogLevel.ERROR, text, null);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing a stack trace of the specified exception to the log if the logging threshold is less
	 * than or equal to {@linkplain LogLevel#ERROR ERROR}.
	 *
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void error(
		Throwable	exception)
	{
		write(LogLevel.ERROR, null, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text and a stack trace of the specified exception to the log if the
	 * logging threshold is less than or equal to {@linkplain LogLevel#ERROR ERROR}.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void error(
		CharSequence	text,
		Throwable		exception)
	{
		write(LogLevel.ERROR, text, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text to the log if the specified level is greater than or equal to the
	 * logging threshold.
	 *
	 * @param  level
	 *           the logging level that will be compared to the logging threshold.
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void log(
		LogLevel		level,
		CharSequence	text)
	{
		write(level, text, null);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing a stack trace of the specified exception to the log if the specified level is greater
	 * than or equal to the logging threshold.
	 *
	 * @param  level
	 *           the logging level that will be compared to the logging threshold.
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void log(
		LogLevel	level,
		Throwable	exception)
	{
		write(level, null, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text and a stack trace of the specified exception to the log if the
	 * specified level is greater than or equal to the logging threshold.
	 *
	 * @param  level
	 *           the logging level that will be compared to the logging threshold.
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void log(
		LogLevel		level,
		CharSequence	text,
		Throwable		exception)
	{
		write(level, text, exception);
	}

	//------------------------------------------------------------------

	/**
	 * Writes the specified text to the log.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void write(
		CharSequence	text)
	{
		if (isOpen())
		{
			try
			{
				outStream.append(text);
			}
			catch (IOException e)
			{
				throw new LoggerException(ErrorMsg.ERROR_WRITING_FILE, e, file);
			}
		}
	}

	//------------------------------------------------------------------

	/**
	 * Writes the specified text followed by a line feed (U+000A) to the log.
	 *
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void writeLine(
		CharSequence	text)
	{
		write(text);
		writeEol();
	}

	//------------------------------------------------------------------

	/**
	 * Writes a line feed (U+000A) to the log and flushes the output stream.
	 *
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	public void writeEol()
	{
		// Write linefeed
		write("\n");

		// Flush output stream
		flush();
	}

	//------------------------------------------------------------------

	/**
	 * Writes the local date and time followed by an optional line feed (U+000A) to the log.
	 *
	 * @param newLine
	 *          if {@code true}, a line feed (U+000A) will be written to the log after the date and time.
	 */

	public void writeTime(
		boolean	newLine)
	{
		// Write local date and time
		write(TIMESTAMP_FORMATTER.format(LocalDateTime.now()));

		// Write line feed
		if (newLine)
			writeEol();
	}

	//------------------------------------------------------------------

	/**
	 * Writes a message containing the specified text and a stack trace of the specified exception to the log if the
	 * specified level is greater than or equal to the logging threshold.  The output stream is flushed at each of the
	 * following points, if the respective elements are present:
	 * <ul>
	 *   <li>after the prefix,</li>
	 *   <li>after the fields and message text,</li>
	 *   <li>after the stack trace of the exception,</li>
	 *   <li>after the suffix.</li>
	 * </ul>
	 *
	 * @param  level
	 *           the logging level that will be compared to the logging threshold.
	 * @param  text
	 *           the text of the message that will be written to the log.
	 * @param  exception
	 *           the exception for which a stack trace will be written to the log.
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	private void write(
		LogLevel		level,
		CharSequence	text,
		Throwable		exception)
	{
		if (isOpen() && (level != LogLevel.OFF) && (level.ordinal() >= threshold.ordinal()))
		{
			// Write prefix
			if (params.prefix != null)
				writeLine(params.prefix);

			// Initialise buffer for fields and text
			StringBuilder buffer = new StringBuilder(256);

			// Append fields
			if (!params.fields.isEmpty())
			{
				for (Field field : params.fields)
				{
					if (!buffer.isEmpty())
						buffer.append(' ');

					switch (field)
					{
						case TIMESTAMP:
							buffer.append('[');
							buffer.append(TIMESTAMP_FORMATTER.format(LocalDateTime.now()));
							buffer.append(']');
							break;

						case LEVEL:
							buffer.append(level);
							break;

						case SOURCE_LOCATION:
						{
							StackWalker.StackFrame sf = StackUtils.stackFrame(2);
							buffer.append('(');
							buffer.append(sf.getFileName());
							int lineNumber = sf.getLineNumber();
							if (lineNumber >= 0)
							{
								buffer.append(':');
								buffer.append(lineNumber);
							}
							buffer.append(')');
							break;
						}
					}
				}
			}

			// Append text
			if ((text != null) && (text.length() > 0))
			{
				if (!buffer.isEmpty())
					buffer.append(params.newLineAfterFields ? '\n' : ' ');
				buffer.append(text);
			}

			// Write fields and text
			if (!buffer.isEmpty())
				writeLine(buffer);

			// Write exception
			if (exception != null)
			{
				CharArrayWriter writer = new CharArrayWriter(1024);
				exception.printStackTrace(new PrintWriter(writer, true));
				write(writer.toString());
				flush();
			}

			// Write suffix
			if (params.suffix != null)
				writeLine(params.suffix);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Flushes the buffered output stream.
	 *
	 * @throws LoggerException
	 *           if an error occurs when writing to the log file.
	 */

	private void flush()
	{
		if (isOpen())
		{
			try
			{
				outStream.flush();
			}
			catch (IOException e)
			{
				throw new LoggerException(ErrorMsg.ERROR_WRITING_FILE, e, file);
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: FIELDS OF FORMATTED MESSAGE


	/**
	 * This is an enumeration of the optional fields of a formatted message.
	 */

	public enum Field
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/**
		 * The local time and date.
		 */
		TIMESTAMP
		(
			"timestamp"
		),

		/**
		 * The logging level.
		 */
		LEVEL
		(
			"level"
		),

		/**
		 * The location in a source file.
		 */
		SOURCE_LOCATION
		(
			"sourceLocation"
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The key that is associated with this field. */
		private	String	key;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a field.
		 *
		 * @param key
		 *          the key that will be associated with the field.
		 */

		private Field(
			String	key)
		{
			// Initialise instance variables
			this.key = key;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the field that is associated with the specified key.
		 *
		 * @param  key
		 *           the key whose associated field is required.
		 * @return the field that is associated with {@code key}, or {@code null} if there is no such field.
		 */

		public static Field forKey(
			String	key)
		{
			return Arrays.stream(values()).filter(value -> value.getKey().equals(key)).findFirst().orElse(null);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the key that is associated with this field.
		 *
		 * @return the key that is associated with this field.
		 */

		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: MESSAGE-FORMAT PARAMETERS


	/**
	 * This class encapsulates the formatting parameters of a message that is written to a log.
	 */

	public static class Params
		implements Cloneable
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The prefix that is written before any fields. */
		private	String			prefix;

		/** The suffix that is written after the message body. */
		private	String			suffix;

		/** A set of the fields that are written after any prefix and before the message body. */
		private	EnumSet<Field>	fields;

		/** Flag: if {@code true}, the fields and the message body are separated by a line feed instead of a space
			character. */
		private	boolean			newLineAfterFields;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a set of formatting parameters.
		 *
		 * @param prefix
		 *          the prefix that will be written before any fields.
		 * @param suffix
		 *          the suffix that is written after the message body.
		 * @param fields
		 *          the fields that will be written after any prefix and before the message body, which may be {@code
		 *          null}.
		 * @param newLineAfterFields
		 *          if {@code true}, the fields and the message body will be separated by a line feed instead of a space
		 *          character.
		 */

		public Params(
			String				prefix,
			String				suffix,
			Collection<Field>	fields,
			boolean				newLineAfterFields)
		{
			// Initialise instance variables
			this.prefix = prefix;
			this.suffix = suffix;
			this.fields = (fields == null) ? EnumSet.noneOf(Field.class) : EnumSet.copyOf(fields);
			this.newLineAfterFields = newLineAfterFields;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * {@inheritDoc}
		 */

		@Override
		public Params clone()
		{
			try
			{
				Params copy = (Params)super.clone();
				copy.fields = EnumSet.copyOf(fields);
				return copy;
			}
			catch (CloneNotSupportedException e)
			{
				throw new UnexpectedRuntimeException(e);
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the prefix that is written before any fields.
		 *
		 * @return the prefix that is written before any fields.
		 */

		public String getPrefix()
		{
			return prefix;
		}

		//--------------------------------------------------------------

		/**
		 * Returns the suffix that is written after the message body.
		 *
		 * @return the suffix that is written after the message body.
		 */

		public String getSuffix()
		{
			return suffix;
		}

		//--------------------------------------------------------------

		/**
		 * Returns a set of the fields that are written after any prefix and before the message body.
		 *
		 * @return a set of the fields that are written after any prefix and before the message body.
		 */

		public EnumSet<Field> getFields()
		{
			return fields.clone();
		}

		//--------------------------------------------------------------

		/**
		 * Returns {@code true} if the fields and the message body are separated by a line feed; {@code false} if they
		 * are separated by a space character.
		 *
		 * @return {@code true} if the fields and the message body are separated by a line feed; {@code false} if they
		 *         are separated by a space character.
		 */

		public boolean isNewLineAfterFields()
		{
			return newLineAfterFields;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: LOGGER EXCEPTION


	/**
	 * This class implements an unchecked exception that relates to a logger.
	 */

	public static class LoggerException
		extends RuntimeException
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** Miscellaneous strings. */
		private static final	String	LOCATION_STR	= "Location: ";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The location of the log file that is associated with this exception. */
		private	Path	file;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a logger-related exception.
		 *
		 * @param message
		 *          the detail message of the exception.
		 * @param replacements
		 *          the items whose string representations will replace placeholders in {@code message}.
		 */

		private LoggerException(
			String		message,
			Object...	replacements)
		{
			// Call superclass constructor
			super(String.format(message, replacements));
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new instance of a logger-related exception.
		 *
		 * @param message
		 *          the detail message of the exception.
		 * @param cause
		 *          the underlying cause of the exception.
		 * @param file
		 *          the location of the log file that will be associated with the exception.
		 */

		private LoggerException(
			String		message,
			Throwable	cause,
			Path		file)
		{
			// Call superclass constructor
			super(((file == null) ? "" : LOCATION_STR + PathUtils.abs(file) + "\n")
						+ message + ExceptionUtils.getCompositeCauseString(cause, "- "));

			// Initialise instance variables
			this.file = file;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the location of the log file that is associated with this exception.
		 *
		 * @return the location of the log file that is associated with this exception.
		 */

		public Path getFile()
		{
			return file;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
