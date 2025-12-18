/*====================================================================*\

MessageListDialog.java

Class: message dialog that contains a list view.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.stage.Window;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.message.MessageConstants;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.clipboard.ClipboardUtils;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

import uk.blankaspect.ui.jfx.listview.SimpleTextListView;

//----------------------------------------------------------------------


// CLASS: MESSAGE DIALOG THAT CONTAINS A LIST VIEW


/**
 * This class implements a dialog in which a message can be displayed alongside an optional icon, and a collection of
 * items can be displayed in a list view below the message.  The dialog can have one or more buttons.
 *
 * @see MessageIcon32
 */

public class MessageListDialog
	extends SimpleModalDialog<Integer>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The vertical gap between adjacent message labels. */
	private static final	double	MESSAGE_GAP	= 4.0;

	/** The gap between the icon and the text. */
	private static final	double	ICON_TEXT_GAP	= 10.0;

	/** The gap between the message pane and the list view. */
	private static final	double	MESSAGE_LIST_VIEW_GAP	= 10.0;

	/** The preferred width of the list view. */
	private static final	double	LIST_VIEW_WIDTH		= 320.0;

	/** The preferred height of the list view. */
	private static final	double	LIST_VIEW_HEIGHT	= 120.0;

	/** The padding at the top and bottom of the label of a cell of the list view. */
	private static final	double	LIST_VIEW_CELL_VERTICAL_PADDING	= 1.0;

	/** The padding around the content pane. */
	private static final	Insets	CONTENT_PANE_PADDING			= new Insets(10.0);

	/** The padding around the content pane if there is no icon. */
	private static final	Insets	CONTENT_PANE_PADDING_NO_ICON	= new Insets(12.0);

	/** The padding around the content pane if there is a list view. */
	private static final	Insets	CONTENT_PANE_PADDING_LIST_VIEW	= new Insets(10.0, 10.0, 6.0, 10.0);

	/** The padding around the content pane if there is a list view but no icon. */
	private static final	Insets	CONTENT_PANE_PADDING_LIST_VIEW_NO_ICON	= new Insets(12.0, 12.0, 6.0, 12.0);

	/** The key combination that causes the items of the list view to be copied to the system clipboard. */
	private static final	KeyCombination	KEY_COMBO_COPY	=
			new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

	/** Miscellaneous strings. */
	private static final	String	COPY_STR		= "Copy";
	private static final	String	COPY_ITEMS_STR	= "Copy items";

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
	 * @param items
	 *          the items that will be displayed in a list view in the dialog.  If it is {@code null}, the dialog will
	 *          have no list view.
	 * @param locator
	 *          the function that returns the location of the dialog, which may be {@code null}.
	 * @param hasCopyButton
	 *          if {@code true}, the dialog will have a <i>Copy</i> button whose action is to copy {@code items} to the
	 *          system clipboard.
	 * @param buttons
	 *          information about the buttons of the dialog.
	 */

	public MessageListDialog(
		Window								owner,
		String								title,
		Node								icon,
		String								message,
		Collection<String>					items,
		ILocator							locator,
		boolean								hasCopyButton,
		Collection<? extends ButtonInfo>	buttons)
	{
		// Call superclass constructor
		super(owner, null, null, title, 1, locator, null);

		// Validate arguments
		if (buttons.isEmpty())
			throw new IllegalArgumentException("No buttons");

		// Initialise instance variables
		result = -1;

		// Set properties
		setResizable(true);

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
		HBox topPane = new HBox(ICON_TEXT_GAP);
		topPane.setAlignment((icon == null) ? Pos.CENTER : Pos.CENTER_LEFT);
		if (icon != null)
			topPane.getChildren().add(icon);
		topPane.getChildren().add(messagePane);

		// Create procedure to copy items to clipboard
		IProcedure0 copyItems = () ->
		{
			try
			{
				ClipboardUtils.putTextThrow(StringUtils.join('\n', items.size() > 1, items));
			}
			catch (Exception e)
			{
				ErrorDialog.show(this, COPY_ITEMS_STR, e);
			}
		};

		// Case: no list view
		if (items == null)
		{
			// Add top pane to content
			addContent(topPane);

			// Set padding around content pane
			getContentPane().setPadding((icon == null) ? CONTENT_PANE_PADDING_NO_ICON : CONTENT_PANE_PADDING);
		}

		// Case: list view
		else
		{
			// Create list view
			SimpleTextListView<String> listView = new SimpleTextListView<>(items, null);
			listView.setPrefSize(LIST_VIEW_WIDTH, LIST_VIEW_HEIGHT);
			listView.setCellVerticalPadding(LIST_VIEW_CELL_VERTICAL_PADDING);
			listView.setUniformCells(true);
			listView.addEventHandler(KeyEvent.KEY_PRESSED, event ->
			{
				if (KEY_COMBO_COPY.match(event))
					copyItems.invoke();
			});
			listView.setOnContextMenuRequested(event ->
			{
				// Create context menu
				ContextMenu menu = new ContextMenu();

				// Add menu item: copy items
				MenuItem menuItem = new MenuItem(COPY_ITEMS_STR);
				menuItem.setOnAction(event0 -> copyItems.invoke());
				menu.getItems().add(menuItem);

				// Display context menu
				menu.show(this, event.getScreenX(), event.getScreenY());
			});
			VBox.setVgrow(listView, Priority.ALWAYS);

			// Add top pane and list view to content
			addContent(new VBox(MESSAGE_LIST_VIEW_GAP, topPane, listView));

			// Set padding around content pane
			getContentPane().setPadding((icon == null) ? CONTENT_PANE_PADDING_LIST_VIEW_NO_ICON
													   : CONTENT_PANE_PADDING_LIST_VIEW);
		}

		// Add 'copy' button
		if (hasCopyButton)
		{
			// Create button
			Button button = Buttons.hNoShrink(COPY_STR);
			button.setOnAction(event -> copyItems.invoke());

			// Add button to button pane
			addButton(button, HPos.LEFT);
		}

		// Create buttons and add them to button pane
		this.buttons = new ArrayList<>();
		Button ctrlEnterButton = null;
		for (ButtonInfo buttonInfo : buttons)
		{
			// Create button
			Button button = Buttons.hNoShrink(buttonInfo.getText());
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

		// Apply new style sheet to scene
		applyStyleSheet();
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
	 * @param items
	 *          the items that will be displayed in a list view in the dialog.  If it is {@code null}, the dialog will
	 *          have no list view.
	 * @param locator
	 *          the function that returns the location of the dialog, which may be {@code null}.
	 * @param hasCopyButton
	 *          if {@code true}, the dialog will have a <i>Copy</i> button whose action is to copy {@code items} to the
	 *          system clipboard.
	 * @param buttons
	 *          information about the buttons of the dialog.
	 */

	public MessageListDialog(
		Window				owner,
		String				title,
		Node				icon,
		String				message,
		Collection<String>	items,
		ILocator			locator,
		boolean				hasCopyButton,
		ButtonInfo...		buttons)
	{
		// Call alternative constructor
		this(owner, title, icon, message, items, locator, hasCopyButton, List.of(buttons));
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
	 * @param  items
	 *           the items that will be displayed in a list view in the dialog.  If it is {@code null}, the dialog will
	 *           have no list view.
	 * @param  hasCopyButton
	 *           if {@code true}, the dialog will have a <i>Copy</i> button whose action is to copy {@code items} to the
	 *           system clipboard.
	 * @param  buttons
	 *           information about the buttons of the dialog.
	 * @return the index of the selected button, or -1 if no button was selected.
	 */

	public static int show(
		Window								owner,
		String								title,
		Node								icon,
		String								message,
		Collection<String>					items,
		boolean								hasCopyButton,
		Collection<? extends ButtonInfo>	buttons)
	{
		return new MessageListDialog(owner, title, icon, message, items, null, hasCopyButton, buttons).showDialog();
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
	 * @param  items
	 *           the items that will be displayed in a list view in the dialog.  If it is {@code null}, the dialog will
	 *           have no list view.
	 * @param  hasCopyButton
	 *           if {@code true}, the dialog will have a <i>Copy</i> button whose action is to copy {@code items} to the
	 *           system clipboard.
	 * @param  buttons
	 *           information about the buttons of the dialog.
	 * @return the index of the selected button, or -1 if no button was selected.
	 */

	public static int show(
		Window				owner,
		String				title,
		Node				icon,
		String				message,
		Collection<String>	items,
		boolean				hasCopyButton,
		ButtonInfo...		buttons)
	{
		return new MessageListDialog(owner, title, icon, message, items, null, hasCopyButton, buttons).showDialog();
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
