/*====================================================================*\

Utils.java

Class: utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.text.DecimalFormat;

import javafx.stage.Window;

import uk.blankaspect.common.exception2.BaseException;

import uk.blankaspect.common.logging.Logger;

import uk.blankaspect.common.os.OsUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.clipboard.ClipboardUtils;

import uk.blankaspect.ui.jfx.dialog.ErrorDialog;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS


public class Utils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The formatter that is applied to integer values to group digits in threes. */
	public static final		DecimalFormat	INTEGER_FORMATTER;

	/** The initial part of the pathname of the root directory of a removable medium under Linux. */
	private static final	String	LINUX_MEDIA_PATHNAME_PREFIX	= "/media/%s/";

	/** The initial part of pathname of a device under Linux. */
	private static final	String	LINUX_DEVICE_PATHNAME_PREFIX	= "/dev/";

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		INTEGER_FORMATTER = new DecimalFormat();
		INTEGER_FORMATTER.setGroupingSize(3);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private Utils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static String bytesToString(
		byte[]	buffer,
		int		offset,
		int		length)
	{
		// Validate arguments
		if (buffer == null)
			throw new IllegalArgumentException("Null buffer");
		if ((offset < 0) || (offset > buffer.length))
			throw new IllegalArgumentException("Offset out of bounds: " + offset);
		if ((length < 0) || (length > buffer.length - offset))
			throw new IllegalArgumentException("Length out of bounds: " + length);

		// Convert bytes to characters
		char[] chars = new char[length];
		for (int i = 0; i < length; i++)
			chars[i] = (char)(buffer[offset + i] & 0xFF);

		// Create string from characters and return it
		return new String(chars);
	}

	//------------------------------------------------------------------

	public static byte[] stringToBytes(
		String	str,
		int		length)
	{
		byte[] buffer = new byte[(length == 0) ? str.length() : length];
		stringToBytes(str, buffer, 0, length);
		return buffer;
	}

	//------------------------------------------------------------------

	public static void stringToBytes(
		String	str,
		byte[]	buffer,
		int		offset,
		int		length)
	{
		String str0 = (length == 0) ? str : StringUtils.padAfter((str == null) ? "" : str, length);
		System.arraycopy(str0.getBytes(StandardCharsets.US_ASCII), 0, buffer, offset, str0.length());
	}

	//------------------------------------------------------------------

	public static String formatDecimal(
		long	value)
	{
		return INTEGER_FORMATTER.format(value);
	}

	//------------------------------------------------------------------

	public static Throwable getException(
		Throwable	exception)
	{
		return (exception instanceof WrappedVolumeException) ? exception.getCause() : exception;
	}

	//------------------------------------------------------------------

	public static void showErrorMessage(
		Window	window,
		String	title,
		String	message)
	{
		// Log error
		Logger.INSTANCE.error(message);

		// Display error dialog
		ErrorDialog.show(window, title, message);
	}

	//------------------------------------------------------------------

	public static void showErrorMessage(
		Window		window,
		String		title,
		Throwable	exception)
	{
		// Get exception
		exception = getException(exception);

		// Log error
		Logger.INSTANCE.error(title, exception);

		// Display error dialog
		ErrorDialog.show(window, title, exception);
	}

	//------------------------------------------------------------------

	public static void showErrorMessage(
		Window		window,
		String		title,
		String		message,
		Throwable	exception)
	{
		// Get exception
		exception = getException(exception);

		// Log error
		Logger.INSTANCE.error(message, exception);

		// Display error dialog
		ErrorDialog.show(window, title, message, exception);
	}

	//------------------------------------------------------------------

	public static void copyToClipboard(
		Window	window,
		String	title,
		String	text)
	{
		try
		{
			ClipboardUtils.putTextThrow(text);
		}
		catch (BaseException e)
		{
			ErrorDialog.show(window, title, e);
		}
	}

	//------------------------------------------------------------------

	public static String volumeDisplayName(
		Fat32Volume	volume)
	{
		return volumeDisplayName(volume.getName());
	}

	//------------------------------------------------------------------

	public static String volumeDisplayName(
		String	name)
	{
		if (name == null)
			return null;

		if (OsUtils.isWindows())
		{
			for (FileStore fileStore : FileSystems.getDefault().getFileStores())
			{
				String text = fileStore.toString();
				int index = text.lastIndexOf('(');
				if ((index >= 0) && text.substring(index + 1).startsWith(name))
				{
					String fileStoreName = fileStore.name();
					if (!StringUtils.isNullOrEmpty(fileStoreName))
						return fileStoreName + " (" + name + ")";
				}
			}
		}
		else
		{
			String mediaPathnamePrefix = String.format(LINUX_MEDIA_PATHNAME_PREFIX, System.getProperty("user.name"));
			for (FileStore fileStore : FileSystems.getDefault().getFileStores())
			{
				String fileStoreName = fileStore.name();
				if (fileStoreName.startsWith(LINUX_DEVICE_PATHNAME_PREFIX))
				{
					if (name.equals(fileStoreName.substring(LINUX_DEVICE_PATHNAME_PREFIX.length())))
					{
						String text = fileStore.toString();
						if (text.startsWith(mediaPathnamePrefix))
						{
							int index = text.lastIndexOf('(');
							if (index >= 0)
							{
								String pathname = text.substring(0, index).strip();
								if (Files.isDirectory(Path.of(pathname), LinkOption.NOFOLLOW_LINKS))
									return pathname.substring(mediaPathnamePrefix.length()) + " (" + name + ")";
							}
						}
					}
				}
			}
		}
		return name;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
