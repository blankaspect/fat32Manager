/*====================================================================*\

IOUtils.java

Class: utility methods related to file-system input/output operations.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.io;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import javafx.stage.Window;

import uk.blankaspect.common.exception2.FileException;

import uk.blankaspect.common.filesystem.PathUtils;

import uk.blankaspect.common.message.MessageConstants;

import uk.blankaspect.ui.jfx.dialog.ConfirmationDialog;
import uk.blankaspect.ui.jfx.dialog.ErrorDialog;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS RELATED TO FILE-SYSTEM INPUT/OUTPUT OPERATIONS


public class IOUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	TEST_FOR_FILE_STR		= "Test for file";
	private static final	String	TEST_FOR_DIRECTORY_STR	= "Test for directory";
	private static final	String	ALREADY_EXISTS_STR		= "The file already exists.\nDo you want to replace it?";
	private static final	String	REPLACE_FILE_STR		= "Replace file";
	private static final	String	REPLACE_STR				= "Replace";

	/**
	 * Error messages
	 */
	private interface ErrorMsg
	{
		String	FILE_DOES_NOT_EXIST			= "The file does not exist.";
		String	DIRECTORY_DOES_NOT_EXIST	= "The directory does not exist.";
		String	NOT_A_FILE					= "The location does not denote a regular file.";
		String	NOT_A_DIRECTORY				= "The location does not denote a directory.";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private IOUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean isExistingFile(
		Path	location,
		Window	owner,
		String	title)
	{
		try
		{
			if (!Files.exists(location, LinkOption.NOFOLLOW_LINKS))
				throw new FileException(ErrorMsg.FILE_DOES_NOT_EXIST, location);
			if (!Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS))
				throw new FileException(ErrorMsg.NOT_A_FILE, location);
			return true;
		}
		catch (FileException e)
		{
			ErrorDialog.show(owner, (title == null) ? TEST_FOR_FILE_STR : title, e);
			return false;
		}
	}

	//------------------------------------------------------------------

	public static boolean isExistingDirectory(
		Path	location,
		Window	owner,
		String	title)
	{
		try
		{
			if (!Files.exists(location, LinkOption.NOFOLLOW_LINKS))
				throw new FileException(ErrorMsg.DIRECTORY_DOES_NOT_EXIST, location);
			if (!Files.isDirectory(location, LinkOption.NOFOLLOW_LINKS))
				throw new FileException(ErrorMsg.NOT_A_DIRECTORY, location);
			return true;
		}
		catch (FileException e)
		{
			ErrorDialog.show(owner, (title == null) ? TEST_FOR_DIRECTORY_STR : title, e);
			return false;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if no file exists at the specified file-system location, or if the user chooses the
	 * <i>replace</i> option in the dialog that is displayed to confirm the replacement of the file.
	 * @param  file
	 *           the location of the file whose existence will be tested.
	 * @param  title
	 *           the title of the dialog in which the user is asked to confirm the replacement of {@code file}.  If it
	 *           is {@code null}, the title will be "Replace file".
	 * @return {@code true} if
	 * <ul>
	 *   <li>{@code file} does not exist, or</li>
	 *   <li>{@code file} exists and the user chooses the <i>replace</i> option in the dialog that is displayed to
	 *       confirm the replacement of the file.</li>
	 * </ul>
	 */

	public static boolean replaceExistingFile(
		Path	file,
		Window	owner,
		String	title)
	{
		// Test whether file exists
		if (!Files.exists(file, LinkOption.NOFOLLOW_LINKS))
			return true;

		// Display dialog to confirm replacement of file
		String message = PathUtils.abs(file) + MessageConstants.LABEL_SEPARATOR + ALREADY_EXISTS_STR;
		return ConfirmationDialog.show(owner, (title == null) ? REPLACE_FILE_STR : title, MessageIcon32.WARNING.get(),
									   message, REPLACE_STR);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
