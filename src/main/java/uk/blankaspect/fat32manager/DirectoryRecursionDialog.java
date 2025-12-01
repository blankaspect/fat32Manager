/*====================================================================*\

DirectoryRecursionDialog.java

Class: directory-recursion dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.HPos;
import javafx.geometry.Insets;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import javafx.scene.layout.StackPane;

import javafx.stage.Window;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

//----------------------------------------------------------------------


// CLASS: DIRECTORY-RECURSION DIALOG


public class DirectoryRecursionDialog
	extends SimpleModalDialog<Boolean>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(4.0, 10.0, 4.0, 10.0);

	/** Miscellaneous strings. */
	private static final	String	INCLUDE_SUBDIRS_STR	= "Include subdirectories";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	Map<String, Boolean>	recursiveMap	= new HashMap<>();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Boolean	result;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private DirectoryRecursionDialog(
		Window	owner,
		String	keySuffix,
		String	title,
		String	acceptStr)
	{
		// Call superclass constructor
		super(owner, MethodHandles.lookup().lookupClass().getName() + "-" + keySuffix, null, title);

		// Create 'recursive' check box
		CheckBox recursiveCheckBox = new CheckBox(INCLUDE_SUBDIRS_STR);
		recursiveCheckBox.setSelected(recursiveMap.getOrDefault(keySuffix, false));

		// Create control pane
		StackPane controlPane = new StackPane(recursiveCheckBox);
		controlPane.setPadding(CONTROL_PANE_PADDING);

		// Add control pane to content pane
		addContent(controlPane);

		// Create button: accept
		Button acceptButton = Buttons.hNoShrink(acceptStr);
		acceptButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		acceptButton.setOnAction(event ->
		{
			result = recursiveCheckBox.isSelected();
			hide();
		});
		addButton(acceptButton, HPos.RIGHT);

		// Create button: cancel
		Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
		cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		cancelButton.setOnAction(event -> requestClose());
		addButton(cancelButton, HPos.RIGHT);

		// Fire 'cancel' button if Escape key is pressed
		setKeyFireButton(cancelButton, null);

		// Save state when dialog is closed
		setOnHiding(event -> recursiveMap.put(keySuffix, recursiveCheckBox.isSelected()));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Boolean show(
		Window	owner,
		String	keySuffix,
		String	title,
		String	acceptStr)
	{
		return new DirectoryRecursionDialog(owner, keySuffix, title, acceptStr).showDialog();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected Boolean getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
