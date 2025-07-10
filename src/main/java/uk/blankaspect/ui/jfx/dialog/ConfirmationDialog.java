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


// CLASS: CONFIRMATION DIALOG


/**
 * This class provides methods that display a dialog in which a message can be displayed alongside an optional icon.
 * The dialog has two buttons, whose text may be specified.  If no button text is specified, the first button (the
 * <i>accept</i> button) will have the text "OK" and the second button (the <i>cancel</i> button) will have the text
 * "Cancel".
 *
 * @see MessageDialog
 * @see MessageIcon32
 */

public class ConfirmationDialog
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Miscellaneous strings. */
	private static final	String	OK_STR		= "OK";
	private static final	String	CANCEL_STR	= "Cancel";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ConfirmationDialog()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a dialog with the specified owner, title, optional icon and message, and with "OK" and
	 * "Cancel" as the text for the <i>accept</i> button and <i>cancel</i> button respectively, displays the dialog and
	 * returns the result from the dialog.
	 *
	 * @param  owner
	 *           the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  icon
	 *           the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param  message
	 *           the message that will be displayed in the dialog.
	 * @return {@code true} if the dialog was closed by the <i>accept</i> button, {@code false} otherwise.
	 */

	public static boolean show(
		Window	owner,
		String	title,
		Node	icon,
		String	message)
	{
		return (MessageDialog.show(owner, title, icon, message, ButtonInfo.of(HPos.RIGHT, OK_STR, CANCEL_STR)) == 0);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, optional icon, message and text for the
	 * <i>accept</i> button, with "Cancel" as the text for the <i>cancel</i> button, displays the dialog and returns the
	 * result from the dialog.
	 *
	 * @param  owner
	 *           the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  icon
	 *           the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param  message
	 *           the message that will be displayed in the dialog.
	 * @param  acceptText
	 *           the text of the <i>accept</i> button.
	 * @return {@code true} if the dialog was closed by the <i>accept</i> button, {@code false} otherwise.
	 */

	public static boolean show(
		Window	owner,
		String	title,
		Node	icon,
		String	message,
		String	acceptText)
	{
		return (MessageDialog.show(owner, title, icon, message,
								   ButtonInfo.of(HPos.RIGHT, acceptText, CANCEL_STR)) == 0);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, optional icon, message, text for the
	 * <i>accept</i> button and text for the <i>cancel</i> button, displays the dialog and returns the result from the
	 * dialog.
	 *
	 * @param  owner
	 *           the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  icon
	 *           the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param  message
	 *           the message that will be displayed in the dialog.
	 * @param  acceptText
	 *           the text of the <i>accept</i> button.
	 * @param  cancelText
	 *           the text of the <i>cancel</i> button.
	 * @return {@code true} if the dialog was closed by the <i>accept</i> button, {@code false} otherwise.
	 */

	public static boolean show(
		Window	owner,
		String	title,
		Node	icon,
		String	message,
		String	acceptText,
		String	cancelText)
	{
		return (MessageDialog.show(owner, title, icon, message,
								   ButtonInfo.of(HPos.RIGHT, acceptText, cancelText)) == 0);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
