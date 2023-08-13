/*====================================================================*\

ConfirmationDialog.java

Class: confirmation dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import javafx.geometry.HPos;

import javafx.scene.Node;

import javafx.stage.Window;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

//----------------------------------------------------------------------


// CLASS: NOTIFICATION DIALOG


/**
 * This class provides methods that display a dialog in which a message can be displayed alongside an optional icon.
 * The dialog has one button, whose text may be specified.  If no button text is specified, the button will have the
 * text "OK".
 *
 * @see MessageDialog
 * @see MessageIcon32
 */

public class NotificationDialog
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Miscellaneous strings. */
	private static final	String	OK_STR	= "OK";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private NotificationDialog()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a dialog with the specified owner, title, optional icon and message, and with "OK" as
	 * the text of the <i>accept</i> button, and displays the dialog.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 */

	public static void show(
		Window	owner,
		String	title,
		Node	icon,
		String	message)
	{
		MessageDialog.show(owner, title, icon, message, new ButtonInfo(OK_STR, HPos.RIGHT));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, optional icon, message and text of the
	 * <i>accept</i> button, and displays the dialog.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 * @param acceptText
	 *          the text of the <i>accept</i> button.
	 */

	public static void show(
		Window	owner,
		String	title,
		Node	icon,
		String	message,
		String	acceptText)
	{
		MessageDialog.show(owner, title, icon, message, new ButtonInfo(acceptText, HPos.RIGHT));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
