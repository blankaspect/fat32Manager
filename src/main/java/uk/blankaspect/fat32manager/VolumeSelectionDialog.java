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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import javafx.stage.Window;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;

//----------------------------------------------------------------------


// CLASS: VOLUME-SELECTION DIALOG


/**
 * This class implements a modal dialog in which a volume may be selected by name.
 */

public class VolumeSelectionDialog
	extends SimpleModalDialog<VolumeSelectionDialog.Result>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The horizontal gap between adjacent columns of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The vertical gap between adjacent rows of the control pane. */
	private static final	double	CONTROL_PANE_V_GAP	= 10.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(4.0, 8.0, 4.0, 8.0);

	/** Miscellaneous strings. */
	private static final	String	VOLUME_STR			= "Volume";
	private static final	String	UNBUFFERED_IO_STR	= "Unbuffered I/O";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	boolean	unbufferedIO;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Result	result;

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

		// Create control pane
		GridPane controlPane = new GridPane();
		controlPane.setHgap(CONTROL_PANE_H_GAP);
		controlPane.setVgap(CONTROL_PANE_V_GAP);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
		column.setHgrow(Priority.NEVER);
		controlPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		column.setHgrow(Priority.ALWAYS);
		controlPane.getColumnConstraints().add(column);

		// Initialise row index
		int row = 0;

		// Spinner: volume
		CollectionSpinner<String> volumeSpinner =
				CollectionSpinner.leftRightH(HPos.CENTER, true, volumeNames, volumeName, null,
											 Utils::volumeDisplayName);
		controlPane.addRow(row++, new Label(VOLUME_STR), volumeSpinner);

		// Check box: unbuffered I/O
		CheckBox unbufferedIOCheckBox = new CheckBox(UNBUFFERED_IO_STR);
		unbufferedIOCheckBox.setSelected(unbufferedIO);
		controlPane.add(unbufferedIOCheckBox, 1, row++);

		// Add control pane to content pane
		addContent(controlPane);

		// Button: OK
		Button okButton = Buttons.hNoShrink(OK_STR);
		okButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		okButton.setOnAction(event ->
		{
			unbufferedIO = unbufferedIOCheckBox.isSelected();
			result = new Result(volumeSpinner.getItem(), unbufferedIO);
			hide();
		});
		addButton(okButton, HPos.RIGHT);

		// Button: cancel
		Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
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

	public static Result show(
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
	protected Result getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: RESULT OF DIALOG


	public record Result(
		String	volumeName,
		boolean	unbufferedIO)
	{ }

	//==================================================================

}

//----------------------------------------------------------------------
