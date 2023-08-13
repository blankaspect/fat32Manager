/*====================================================================*\

MessageDialog.java

Class: message dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.stage.Window;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

//----------------------------------------------------------------------


// CLASS: MESSAGE DIALOG


/**
 * This class implements a dialog in which a message can be displayed alongside an optional icon.  The dialog can have
 * one or more buttons.
 *
 * @see MessageIcon32
 */

public class MessageDialog
	extends SimpleModalDialog<Integer>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The regular expression that is used to split the message into parts that have a {@linkplain #MESSAGE_GAP
		vertical gap} between their labels. */
	public static final		String	MESSAGE_SEPARATOR	= "\u000B";		// vertical tab

	/** The vertical gap between adjacent message labels. */
	private static final	double	MESSAGE_GAP	= 4.0;

	/** The gap between the icon and the text. */
	private static final	double	ICON_TEXT_GAP	= 10.0;

	/** The padding around the content pane. */
	private static final	Insets	CONTENT_PANE_PADDING			= new Insets(10.0);

	/** The padding around the content pane if there is no icon. */
	private static final	Insets	CONTENT_PANE_PADDING_NO_ICON	= new Insets(12.0);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The index of the button that was selected. */
	private	Integer			result;

	/** A list of the message labels. */
	private	List<Label>		messageLabels;

	/** A list of the buttons. */
	private	List<Button>	buttons;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a dialog with the specified owner, title, optional icon, message and buttons.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 * @param locator
	 *          the function that returns the location of the dialog, which may be {@code null}.
	 * @param buttons
	 *          information about the buttons of the dialog.
	 */

	public MessageDialog(
		Window								owner,
		String								title,
		Node								icon,
		String								message,
		ILocator							locator,
		Collection<? extends ButtonInfo>	buttons)
	{
		// Call superclass constructor
		super(owner, null, null, title, 1, locator, null);

		// Validate arguments
		if (buttons.isEmpty())
			throw new IllegalArgumentException("No buttons");

		// Initialise instance variables
		result = -1;

		// Create labels for messages
		messageLabels = new ArrayList<>();
		for (String message0 : message.split(MESSAGE_SEPARATOR))
		{
			Label label = new Label(message0);
			messageLabels.add(label);
		}

		// Create pane for message labels
		VBox messagePane = new VBox(MESSAGE_GAP);
		messagePane.setAlignment(Pos.CENTER_LEFT);
		messagePane.getChildren().addAll(messageLabels);
		HBox.setHgrow(messagePane, Priority.ALWAYS);

		// Create pane for icon and message labels
		HBox contentPane = new HBox(ICON_TEXT_GAP);
		contentPane.setAlignment((icon == null) ? Pos.CENTER : Pos.CENTER_LEFT);
		if (icon != null)
			contentPane.getChildren().add(icon);
		contentPane.getChildren().add(messagePane);

		// Add content pane to content
		addContent(contentPane);

		// Set padding around content pane
		getContentPane().setPadding((icon == null) ? CONTENT_PANE_PADDING_NO_ICON : CONTENT_PANE_PADDING);

		// Create buttons and add them to button pane
		this.buttons = new ArrayList<>();
		Button ctrlEnterButton = null;
		for (ButtonInfo buttonInfo : buttons)
		{
			// Create button
			Button button = new Button(buttonInfo.getText());
			button.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
			button.setOnAction(event ->
			{
				result = this.buttons.indexOf(button);
				hide();
			});
			this.buttons.add(button);

			// Add button to button pane
			addButton(button, buttonInfo.getPosition());

			// Set button that is fired on Ctrl+Enter
			if ((ctrlEnterButton == null) && buttonInfo.isFireOnCtrlEnter())
				ctrlEnterButton = button;
		}

		// Close dialog if Escape key is pressed
		setRequestCloseOnEscape();

		// Fire button if Ctrl+Enter is pressed
		if (ctrlEnterButton != null)
		{
			Button button = ctrlEnterButton;
			addEventFilter(KeyEvent.KEY_PRESSED, event ->
			{
				if ((event.getCode() == KeyCode.ENTER) && event.isControlDown())
					button.fire();
			});
		}
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, optional icon, message and buttons.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 * @param locator
	 *          the function that returns the location of the dialog, which may be {@code null}.
	 * @param buttons
	 *          information about the buttons of the dialog.
	 */

	public MessageDialog(
		Window			owner,
		String			title,
		Node			icon,
		String			message,
		ILocator		locator,
		ButtonInfo...	buttons)
	{
		// Call alternative constructor
		this(owner, title, icon, message, locator, Arrays.asList(buttons));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a dialog with the specified owner, title, optional icon, message and buttons, displays
	 * the dialog and returns the index of the selected button.
	 *
	 * @param  owner
	 *           the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  icon
	 *           the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param  message
	 *           the message that will be displayed in the dialog.
	 * @param  buttons
	 *           information about the buttons of the dialog.
	 * @return the index of the selected button, or -1 if no button was selected.
	 */

	public static int show(
		Window								owner,
		String								title,
		Node								icon,
		String								message,
		Collection<? extends ButtonInfo>	buttons)
	{
		return new MessageDialog(owner, title, icon, message, null, buttons).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, optional icon, message and buttons, displays
	 * the dialog and returns the index of the selected button.
	 *
	 * @param  owner
	 *           the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  icon
	 *           the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param  message
	 *           the message that will be displayed in the dialog.
	 * @param  buttons
	 *           information about the buttons of the dialog.
	 * @return the index of the selected button, or -1 if no button was selected.
	 */

	public static int show(
		Window			owner,
		String			title,
		Node			icon,
		String			message,
		ButtonInfo...	buttons)
	{
		return new MessageDialog(owner, title, icon, message, null, buttons).showDialog();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected Integer getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an unmodifiable list of the message labels of this dialog.
	 *
	 * @return an unmodifiable list of the message labels of this dialog.
	 */

	public List<Label> getMessageLabels()
	{
		return Collections.unmodifiableList(messageLabels);
	}

	//------------------------------------------------------------------

	/**
	 * Returns an unmodifiable list of the buttons of this dialog.
	 *
	 * @return an unmodifiable list of the buttons of this dialog.
	 */

	public List<Button> getButtons()
	{
		return Collections.unmodifiableList(buttons);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
