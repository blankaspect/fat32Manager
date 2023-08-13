/*====================================================================*\

VolumeSelectionDialog.java

Class: volume-selection dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.Collection;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;

import javafx.stage.Window;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;

//----------------------------------------------------------------------


// CLASS: VOLUME-SELECTION DIALOG


/**
 * This class implements a modal dialog in which a volume may be selected by name.
 */

public class VolumeSelectionDialog
	extends SimpleModalDialog<String>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(0.0, 24.0, 0.0, 24.0);

	/** Miscellaneous strings. */
	private static final	String	VOLUME_STR	= "Volume";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String	result;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a modal dialog in which a volume may be selected by name.
	 *
	 * @param owner
	 *          the window that will own this dialog, or {@code null} for a top-level dialog that has no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param volumeNames
	 *          the names of the volumes from which a selection may be made.
	 * @param volumeName
	 *          the name of the volume that will be initially selected, which may be {@code null}.
	 */

	private VolumeSelectionDialog(
		Window				owner,
		String				title,
		Collection<String>	volumeNames,
		String				volumeName)
	{
		// Call superclass constructor
		super(owner, MethodHandles.lookup().lookupClass().getName(), null, title);

		// Create label for choice box
		Label label = new Label(VOLUME_STR);

		// Create spinner
		CollectionSpinner<String> volumeSpinner =
				CollectionSpinner.leftRightH(HPos.CENTER, true, volumeNames, volumeName, null, Utils::volumeDisplayName);

		// Create control pane
		HBox controlPane = new HBox(6.0, label, volumeSpinner);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);

		// Add control pane to content pane
		addContent(controlPane);

		// Create button: OK
		Button okButton = new Button(OK_STR);
		okButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		okButton.setOnAction(event ->
		{
			result = volumeSpinner.getItem();
			hide();
		});
		addButton(okButton, HPos.RIGHT);

		// Create button: cancel
		Button cancelButton = new Button(CANCEL_STR);
		cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		cancelButton.setOnAction(event -> requestClose());
		addButton(cancelButton, HPos.RIGHT);

		// Fire 'cancel' button if Escape key is pressed; fire 'OK' button if Ctrl+Enter is pressed
		setKeyFireButton(cancelButton, okButton);

		// Request focus on choice box when dialog is shown
		setOnShown(event -> volumeSpinner.requestFocus());

		// Apply new style sheet to scene
		applyStyleSheet();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a modal dialog in which a volume may be selected by name, displays the dialog, and
	 * returns the selected volume.
	 *
	 * @param  owner
	 *           the window that will own this dialog, or {@code null} for a top-level dialog that has no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  volumeNames
	 *           the names of the volumes from which a selection may be made.
	 * @param  volumeName
	 *           the name of the volume that will be initially selected, which may be {@code null}.
	 * @return the name of the selected volume, if the dialog was accepted; {@code null} otherwise.
	 */

	public static String show(
		Window				owner,
		String				title,
		Collection<String>	volumeNames,
		String				volumeName)
	{
		return new VolumeSelectionDialog(owner, title, volumeNames, volumeName).showDialog();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected String getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
