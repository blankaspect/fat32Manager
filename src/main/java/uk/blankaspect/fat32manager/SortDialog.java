/*====================================================================*\

SortDialog.java

Class: sort dialog.

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

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.stage.Window;

import uk.blankaspect.common.os.OsUtils;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

//----------------------------------------------------------------------


// CLASS: SORT DIALOG


public class SortDialog
	extends SimpleModalDialog<SortDialog.Result>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The spacing between adjacent children of the control pane. */
	private static final	double	CONTROL_PANE_GAP	= 8.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(2.0, 8.0, 2.0, 8.0);

	/** Miscellaneous strings. */
	private static final	String	PREVIEW_STR			= "Preview only";
	private static final	String	INCLUDE_SUBDIRS_STR	= "Include subdirectories";
	private static final	String	IGNORE_CASE_STR		= "Ignore case";
	private static final	String	SORT_STR			= "Sort";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	boolean	preview		= true;
	private static	boolean	recursive;
	private static	boolean	ignoreCase	= OsUtils.isWindows();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Result	result;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private SortDialog(
		Window	owner,
		String	title)
	{
		// Call superclass constructor
		super(owner, MethodHandles.lookup().lookupClass().getName(), null, title);

		// Create 'preview' check box
		CheckBox previewCheckBox = new CheckBox(PREVIEW_STR);
		previewCheckBox.setSelected(preview);

		// Create 'recursive' check box
		CheckBox recursiveCheckBox = new CheckBox(INCLUDE_SUBDIRS_STR);
		recursiveCheckBox.disableProperty().bind(previewCheckBox.selectedProperty());
		recursiveCheckBox.setSelected(recursive);

		// Create 'ignore case' check box
		CheckBox ignoreCaseCheckBox = new CheckBox(IGNORE_CASE_STR);
		ignoreCaseCheckBox.setSelected(ignoreCase);

		// Create control pane
		VBox controlPane = new VBox(CONTROL_PANE_GAP, previewCheckBox, recursiveCheckBox, ignoreCaseCheckBox);
		controlPane.setMaxWidth(Region.USE_PREF_SIZE);
		controlPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.setPadding(CONTROL_PANE_PADDING);

		// Add control pane to content pane
		addContent(controlPane);

		// Create button: sort
		Button sortButton = Buttons.hNoShrink(SORT_STR);
		sortButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		sortButton.setOnAction(event ->
		{
			result = new Result(previewCheckBox.isSelected(), recursiveCheckBox.isSelected(),
								ignoreCaseCheckBox.isSelected());
			hide();
		});
		addButton(sortButton, HPos.RIGHT);

		// Create button: cancel
		Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
		cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		cancelButton.setOnAction(event -> requestClose());
		addButton(cancelButton, HPos.RIGHT);

		// Fire 'cancel' button if Escape key is pressed
		setKeyFireButton(cancelButton, null);

		// Request focus on 'preview' check box when dialog is shown
		setOnShown(event -> previewCheckBox.requestFocus());

		// Save state when dialog is closed
		setOnHiding(event ->
		{
			preview = previewCheckBox.isSelected();
			recursive = recursiveCheckBox.isSelected();
			ignoreCase = ignoreCaseCheckBox.isSelected();
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Result show(
		Window	owner,
		String	title)
	{
		return new SortDialog(owner, title).showDialog();
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


	// RECORD: RESULT


	public record Result(
		boolean	preview,
		boolean	recursive,
		boolean	ignoreCase)
	{ }

	//==================================================================

}

//----------------------------------------------------------------------
