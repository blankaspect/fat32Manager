/*====================================================================*\

WarningDialog.java

Class: warning dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import javafx.stage.Window;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

//----------------------------------------------------------------------


// CLASS: WARNING DIALOG


/**
 * This class provides methods that display a dialog in which the detail message and cause of a {@link Throwable} are
 * displayed alongside a {@linkplain MessageIcon32#WARNING warning icon}.
 *
 * @see ExceptionDialog
 * @see ErrorDialog
 * @see MessageIcon32
 */

public class WarningDialog
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private WarningDialog()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and displays a new instance of a dialog with the specified owner and message.  The dialog will have a
	 * {@linkplain MessageIcon32#WARNING warning icon}.
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
		new ExceptionDialog(owner, title, MessageIcon32.WARNING, message, null).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and display a new instance of a dialog with the specified owner and title, for the specified instance of
	 * {@link Throwable}.  The dialog will have a {@linkplain MessageIcon32#WARNING warning icon}.
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
		new ExceptionDialog(owner, title, MessageIcon32.WARNING, throwable).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and displays a new instance of a dialog with the specified owner, title and message, and with the chain
	 * of causes whose first element is the specified instance of {@link Throwable}.  The dialog will have a {@linkplain
	 * MessageIcon32#WARNING warning icon}.
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
		new ExceptionDialog(owner, title, MessageIcon32.WARNING, message, cause).showDialog();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
