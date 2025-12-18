/*====================================================================*\

MessageDialog.java

Class: message dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
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

import uk.blankaspect.common.message.MessageConstants;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.button.Buttons;

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
	 *          the title of the dialog, which may be {@code null}.
	 * @param icon
	 *          the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 * @param locator
	 *          the function that returns the location of the dialog, which may be {@code null}.
	 * @param buttonInfos
	 *          information about the buttons of the dialog.
	 */

	public MessageDialog(
		Window								owner,
		String								title,
		Node								icon,
		String								message,
		ILocator							locator,
		Collection<? extends ButtonInfo>	buttonInfos)
	{
		// Call superclass constructor
		super(owner, null, null, title, 1, locator, null);

		// Validate arguments
		if (message == null)
			throw new IllegalArgumentException("Null message");
		if (buttonInfos == null)
			throw new IllegalArgumentException("Null buttons");
		if (buttonInfos.isEmpty())
			throw new IllegalArgumentException("No buttons");

		// Initialise instance variables
		result = -1;

		// Create labels for messages
		messageLabels = new ArrayList<>();
		for (String msg : StringUtils.split(message, MessageConstants.LABEL_SEPARATOR_CHAR, true))
			messageLabels.add(new Label(msg));

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
		buttons = new ArrayList<>();
		Button ctrlEnterButton = null;
		for (ButtonInfo buttonInfo : buttonInfos)
		{
			// Create button
			Button button = Buttons.hNoShrink(buttonInfo.getText());
			button.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
			button.setOnAction(event ->
			{
				result = buttons.indexOf(button);
				hide();
			});
			buttons.add(button);

			// Add button to button pane
			addButton(button, buttonInfo.getPosition());

			// Set button that is fired on Ctrl+Enter
			if ((ctrlEnterButton == null) && buttonInfo.isFireOnCtrlEnter())
				ctrlEnterButton = button;
		}

		// Fire 'request to close window' event if Escape is pressed
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
	 * Creates a new instance of a dialog with the specified owner, title, optional icon, message and buttons.  The
	 * first button is the default button.
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
	 * @param buttonInfos
	 *          information about the buttons of the dialog.
	 */

	public MessageDialog(
		Window			owner,
		String			title,
		Node			icon,
		String			message,
		ILocator		locator,
		ButtonInfo...	buttonInfos)
	{
		// Call alternative constructor
		this(owner, title, icon, message, locator, List.of(buttonInfos));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a builder for a message dialog.
	 *
	 * @return a new instance of a builder for a message dialog.
	 */

	public static Builder builder()
	{
		return new Builder();
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

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: BUILDER FOR A MESSAGE DIALOG


	/**
	 * This class implements a builder for a {@linkplain MessageDialog message dialog}.
	 */

	public static class Builder
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The owner of the dialog. */
		private	Window				owner;

		/** The title of the dialog. */
		private	String				title;

		/** The icon of the dialog. */
		private	Node				icon;

		/** The message that will be displayed in the dialog. */
		private	String				message;

		/** The function that returns the location of the dialog. */
		private	ILocator			locator;

		/** A list of information about the buttons of the dialog. */
		private	List<ButtonInfo>	buttonInfos;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a builder for a {@linkplain MessageDialog message dialog}.
		 */

		private Builder()
		{
			// Initialise instance variables
			buttonInfos = new ArrayList<>();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Sets the owner of the dialog to the specified value.
		 *
		 * @param  owner
		 *           the owner of the dialog.
		 * @return this builder.
		 */

		public Builder owner(
			Window	owner)
		{
			// Update instance variable
			this.owner = owner;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the title of the dialog to the specified value.
		 *
		 * @param  title
		 *           the title of the dialog.
		 * @return this builder.
		 */

		public Builder title(
			String	title)
		{
			// Update instance variable
			this.title = title;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the icon of the dialog to the specified value.
		 *
		 * @param  icon
		 *           the icon of the dialog.
		 * @return this builder.
		 */

		public Builder icon(
			Node	icon)
		{
			// Update instance variable
			this.icon = icon;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the message that will be displayed in the dialog to the specified value.
		 *
		 * @param  message
		 *           the message that will be displayed in the dialog.
		 * @return this builder.
		 */

		public Builder message(
			String	message)
		{
			// Update instance variable
			this.message = message;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the function that returns the location of the dialog to the specified value.
		 *
		 * @param  locator
		 *           the function that returns the location of the dialog.
		 * @return this builder.
		 */

		public Builder locator(
			ILocator	locator)
		{
			// Update instance variable
			this.locator = locator;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates an item of button information for a left-positioned button with the specified text and adds it to the
		 * list of information about the buttons of the dialog.
		 *
		 * @param  text
		 *           the text of the button.
		 * @return this builder.
		 */

		public Builder addLeft(
			String	text)
		{
			// Append button info to list
			buttonInfos.add(ButtonInfo.left(text));

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates an item of button information for a horizontally centred button with the specified text and adds it
		 * to the list of information about the buttons of the dialog.
		 *
		 * @param  text
		 *           the text of the button.
		 * @return this builder.
		 */

		public Builder addCentre(
			String	text)
		{
			// Append button info to list
			buttonInfos.add(ButtonInfo.centre(text));

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates an item of button information for a right-positioned button with the specified text and adds it to
		 * the list of information about the buttons of the dialog.
		 *
		 * @param  text
		 *           the text of the button.
		 * @return this builder.
		 */

		public Builder addRight(
			String	text)
		{
			// Append button info to list
			buttonInfos.add(ButtonInfo.right(text));

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Adds the specified items of button information to the list of information about the buttons of the dialog.
		 *
		 * @param  buttonInfos
		 *           the items that will be added to the list of button information.
		 * @return this builder.
		 */

		public Builder addButtons(
			Collection<? extends ButtonInfo>	buttonInfos)
		{
			// Append items to list
			this.buttonInfos.addAll(buttonInfos);

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Adds the specified items of button information to the list of information about the buttons of the dialog.
		 *
		 * @param  buttonInfos
		 *           the items that will be added to the list of button information.
		 * @return this builder.
		 */

		public Builder addButtons(
			ButtonInfo...	buttonInfos)
		{
			// Append items to list
			Collections.addAll(this.buttonInfos, buttonInfos);

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates and returns a new instance of a message dialog that is initialised from the state of this builder.
		 *
		 * @return a new instance of a message dialog.
		 */

		public MessageDialog build()
		{
			return new MessageDialog(owner, title, icon, message, locator, buttonInfos);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
