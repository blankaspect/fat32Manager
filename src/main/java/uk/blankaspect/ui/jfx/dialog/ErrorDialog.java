/*====================================================================*\

ErrorDialog.java

Class: error dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import javafx.stage.Window;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

//----------------------------------------------------------------------


// CLASS: ERROR DIALOG


/**
 * This class provides methods that display a dialog in which the detail message and cause of a {@link Throwable} are
 * displayed alongside an {@linkplain MessageIcon32#ERROR error icon}.
 *
 * @see ExceptionDialog
 * @see WarningDialog
 * @see MessageIcon32
 */

public class ErrorDialog
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ErrorDialog()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and displays a new instance of a dialog with the specified owner and message.  The dialog will have an
	 * {@linkplain MessageIcon32#ERROR error icon}.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 */

	public static void show(
		Window	owner,
		String	title,
		String	message)
	{
		new ExceptionDialog(owner, title, MessageIcon32.ERROR, message, null).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and displays a new instance of a dialog with the specified owner and title, for the specified instance of
	 * {@link Throwable}.  The dialog will have an {@linkplain MessageIcon32#ERROR error icon}.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param throwable
	 *          the instance of {@code Throwable} whose detail message and cause will be displayed in the dialog.
	 */

	public static void show(
		Window		owner,
		String		title,
		Throwable	throwable)
	{
		new ExceptionDialog(owner, title, MessageIcon32.ERROR, throwable).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and displays a new instance of a dialog with the specified owner, title and message, and with the chain
	 * of causes whose first element is the specified instance of {@link Throwable}.  The dialog will have an
	 * {@linkplain MessageIcon32#ERROR error icon}.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 * @param cause
	 *          the first element of the chain of causes that will be displayed in the dialog.
	 */

	public static void show(
		Window		owner,
		String		title,
		String		message,
		Throwable	cause)
	{
		new ExceptionDialog(owner, title, MessageIcon32.ERROR, message, cause).showDialog();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
