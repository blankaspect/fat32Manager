/*====================================================================*\

TextAreaDialog.java

Class: text-area dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import javafx.geometry.HPos;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import javafx.stage.Window;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

//----------------------------------------------------------------------


// CLASS: TEXT-AREA DIALOG


public class TextAreaDialog
	extends SimpleModalDialog<Void>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	double	TEXT_AREA_WIDTH		= 480.0;
	private static final	double	TEXT_AREA_HEIGHT	= 320.0;

	private static final	KeyCombination	KEY_COMBO_COPY	=
			new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);

	private static final	String	COPY_STR	= "Copy";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private TextAreaDialog(
		Window	window,
		String	keySuffix,
		String	title,
		String	initialText)
	{
		// Call superclass constructor
		super(window, MethodHandles.lookup().lookupClass().getName() + "-" + keySuffix, title);

		// Set properties
		setResizable(true);

		// Create text area
		TextArea textArea = new TextArea(initialText);
		textArea.setPrefSize(TEXT_AREA_WIDTH, TEXT_AREA_HEIGHT);
		textArea.setEditable(false);

		// Set text area as content
		setContent(textArea);

		// Create button: copy
		Button copyButton = Buttons.hNoShrink(COPY_STR);
		copyButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		copyButton.setOnAction(event ->
		{
			// Get text that is selected in text area
			String sourceText = textArea.getSelectedText();

			// If no text is selected, use entire text
			if (sourceText.isEmpty())
				sourceText = textArea.getText() + "\n";

			// Put text on clipboard
			Utils.copyToClipboard(this, COPY_STR, sourceText);
		});
		addButton(copyButton, HPos.LEFT);

		// Create button: close
		Button closeButton = Buttons.hNoShrink(CLOSE_STR);
		closeButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		closeButton.setOnAction(event -> requestClose());
		addButton(closeButton, HPos.RIGHT);

		// Fire 'close' button if Escape key is pressed
		setKeyFireButton(closeButton, null);

		// Fire 'copy' button if Ctrl+C is pressed
		addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if (KEY_COMBO_COPY.match(event) && (!textArea.isFocused() || (textArea.getSelection().getLength() == 0)))
			{
				copyButton.fire();
				event.consume();
			}
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void show(
		Window	window,
		String	keySuffix,
		String	title,
		String	initialText)
	{
		new TextAreaDialog(window, keySuffix, title, initialText).showDialog();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
