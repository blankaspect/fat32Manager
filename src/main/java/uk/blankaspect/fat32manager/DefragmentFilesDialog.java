/*====================================================================*\

DefragmentFilesDialog.java

Class: 'defragment files' dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import javafx.scene.layout.HBox;

import javafx.stage.Window;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

//----------------------------------------------------------------------


// CLASS: 'DEFRAGMENT FILES' DIALOG


public class DefragmentFilesDialog
	extends SimpleModalDialog<Boolean>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The horizontal spacing between adjacent components of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(4.0, 8.0, 4.0, 8.0);

	/** Miscellaneous strings. */
	private static final	String	INCLUDE_SUBDIRS_STR	= "Include subdirectories";
	private static final	String	DEFRAGMENT_STR		= "Defragment";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	boolean	recursive;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Boolean	result;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private DefragmentFilesDialog(
		Window	owner,
		String	title)
	{
		// Call superclass constructor
		super(owner, MethodHandles.lookup().lookupClass().getName(), null, title);

		// Create 'recursive' check box
		CheckBox recursiveCheckBox = new CheckBox(INCLUDE_SUBDIRS_STR);
		recursiveCheckBox.setSelected(recursive);

		// Create control pane
		HBox controlPane = new HBox(CONTROL_PANE_H_GAP, recursiveCheckBox);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);

		// Add control pane to content pane
		addContent(controlPane);

		// Create button: defragment
		Button defragmentButton = Buttons.hNoShrink(DEFRAGMENT_STR);
		defragmentButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		defragmentButton.setOnAction(event ->
		{
			result = recursiveCheckBox.isSelected();
			hide();
		});
		addButton(defragmentButton, HPos.RIGHT);

		// Create button: cancel
		Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
		cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		cancelButton.setOnAction(event -> requestClose());
		addButton(cancelButton, HPos.RIGHT);

		// Fire 'cancel' button if Escape key is pressed
		setKeyFireButton(cancelButton, null);

		// Save state when dialog is closed
		setOnHiding(event -> recursive = recursiveCheckBox.isSelected());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Boolean show(
		Window	owner,
		String	title)
	{
		return new DefragmentFilesDialog(owner, title).showDialog();
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
