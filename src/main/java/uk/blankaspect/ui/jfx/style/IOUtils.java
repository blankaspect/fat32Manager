/*====================================================================*\

IOUtils.java

Class: utility methods for input and output.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.nio.channels.FileChannel;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;

import uk.blankaspect.common.bytechannel.ChannelUtils;

import uk.blankaspect.common.exception2.FileException;

import uk.blankaspect.common.filesystem.FilenameUtils;
import uk.blankaspect.common.filesystem.PathUtils;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS FOR INPUT AND OUTPUT


public class IOUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FAILED_TO_OPEN_FILE =
				"Failed to open the file.";

		String	FAILED_TO_CLOSE_FILE =
				"Failed to close the file.";

		String	FAILED_TO_LOCK_FILE =
				"Failed to lock the file.";

		String	FAILED_TO_READ_FILE_ATTRIBUTES =
				"Failed to read the attributes of the file.";

		String	FAILED_TO_CREATE_DIRECTORY =
				"Failed to create the directory.";

		String	FAILED_TO_CREATE_TEMPORARY_FILE =
				"Failed to create a temporary file.";

		String	FAILED_TO_DELETE_FILE =
				"Failed to delete the existing file.";

		String	FAILED_TO_RENAME_FILE =
				"Temporary file: %s\nFailed to rename the temporary file to the specified filename.";

		String	ERROR_WRITING_FILE =
				"An error occurred when writing the file.";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private IOUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void writeTextFile(
		Path	file,
		String	text)
		throws FileException
	{
		// Initialise variables
		FileChannel channel = null;
		Path tempFile = null;
		boolean oldFileDeleted = false;

		// Write file
		try
		{
			// Read file permissions of an existing file
			FileAttribute<?>[] attrs = {};
			if (Files.exists(file, LinkOption.NOFOLLOW_LINKS))
			{
				try
				{
					PosixFileAttributes posixAttrs =
							Files.readAttributes(file, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
					attrs = new FileAttribute<?>[] { PosixFilePermissions.asFileAttribute(posixAttrs.permissions()) };
				}
				catch (UnsupportedOperationException e)
				{
					// ignore
				}
				catch (Exception e)
				{
					throw new FileException(ErrorMsg.FAILED_TO_READ_FILE_ATTRIBUTES, e, file);
				}
			}

			// Create parent directory
			Path directory = PathUtils.absParent(file);
			try
			{
				Files.createDirectories(directory);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CREATE_DIRECTORY, e, directory);
			}

			// Create temporary file
			try
			{
				tempFile = FilenameUtils.tempLocation(file);
				Files.createFile(tempFile, attrs);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CREATE_TEMPORARY_FILE, e, tempFile);
			}

			// Open channel for writing
			try
			{
				channel = FileChannel.open(tempFile, StandardOpenOption.WRITE);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_OPEN_FILE, e, tempFile);
			}

			// Lock channel
			try
			{
				if (channel.tryLock() == null)
					throw new FileException(ErrorMsg.FAILED_TO_LOCK_FILE, tempFile);
			}
			catch (IOException e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_LOCK_FILE, e, tempFile);
			}

			// Write text to channel
			try
			{
				ChannelUtils.write(channel, text.getBytes(StandardCharsets.UTF_8));
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.ERROR_WRITING_FILE, e, tempFile);
			}

			// Close channel
			try
			{
				channel.close();
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_CLOSE_FILE, e, tempFile);
			}
			finally
			{
				channel = null;
			}

			// Delete any existing file
			try
			{
				Files.deleteIfExists(file);
				oldFileDeleted = true;
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_DELETE_FILE, e, file);
			}

			// Rename temporary file
			try
			{
				Files.move(tempFile, file, StandardCopyOption.ATOMIC_MOVE);
			}
			catch (Exception e)
			{
				throw new FileException(ErrorMsg.FAILED_TO_RENAME_FILE, e, file, PathUtils.abs(tempFile));
			}
		}
		catch (FileException e)
		{
			// Close channel
			if (channel != null)
			{
				try
				{
					channel.close();
				}
				catch (Exception e0)
				{
					// ignore
				}
			}

			// Delete temporary file
			if (!oldFileDeleted && (tempFile != null))
			{
				try
				{
					Files.deleteIfExists(tempFile);
				}
				catch (Exception e0)
				{
					// ignore
				}
			}

			// Rethrow exception
			throw e;
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
