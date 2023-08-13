/*====================================================================*\

EraseUnusedClustersDialog.java

Class: 'erase unused clusters' dialog.

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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import javafx.scene.layout.HBox;

import javafx.stage.Window;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.textfield.FilterFactory;

//----------------------------------------------------------------------


// CLASS: 'ERASE UNUSED CLUSTERS' DIALOG


public class EraseUnusedClustersDialog
	extends SimpleModalDialog<Integer>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The spacing between adjacent children of the control pane. */
	private static final	double	CONTROL_PANE_GAP	= 6.0;

	/** The padding around the control pane. */
	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(2.0, 24.0, 2.0, 24.0);

	/** The number of columns of the <i>filler</i> field. */
	private static final	int		FILLER_FIELD_NUM_COLUMNS	= 2;

	/** Miscellaneous strings. */
	private static final	String	FILLER_VALUE_STR	= "Filler value";
	private static final	String	ERASE_STR			= "Erase";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	String	fillerText;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Integer	result;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private EraseUnusedClustersDialog(
		Window	owner,
		String	title)
	{
		// Call superclass constructor
		super(owner, MethodHandles.lookup().lookupClass().getName(), null, title);

		// Create 'filler' text field
		TextField fillerField = new TextField(fillerText);
		fillerField.setPrefColumnCount(FILLER_FIELD_NUM_COLUMNS);
		fillerField.setTextFormatter(new TextFormatter<>(FilterFactory.hexInteger(FILLER_FIELD_NUM_COLUMNS)));

		// Create control pane
		HBox controlPane = new HBox(CONTROL_PANE_GAP, new Label(FILLER_VALUE_STR), fillerField);
		controlPane.setAlignment(Pos.CENTER);
		controlPane.setPadding(CONTROL_PANE_PADDING);

		// Add control pane to content pane
		addContent(controlPane);

		// Create button: erase
		Button eraseButton = new Button(ERASE_STR);
		eraseButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		eraseButton.setOnAction(event ->
		{
			result = Integer.parseInt(fillerField.getText(), 16);
			hide();
		});
		addButton(eraseButton, HPos.LEFT);

		// Create procedure to disable 'erase' button if content of 'filler' field is invalid
		IProcedure0 updateEraseButton = () ->
		{
			String text = fillerField.getText();
			eraseButton.setDisable((text == null) || (text.length() != FILLER_FIELD_NUM_COLUMNS));
		};

		// Disable 'erase' button if content of 'filler' field is invalid
		fillerField.textProperty().addListener(observable -> updateEraseButton.invoke());

		// Update 'erase' button
		updateEraseButton.invoke();

		// Create button: cancel
		Button cancelButton = new Button(CANCEL_STR);
		cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		cancelButton.setOnAction(event -> requestClose());
		addButton(cancelButton, HPos.RIGHT);

		// Fire 'cancel' button if Escape key is pressed
		setKeyFireButton(cancelButton, null);

		// Request focus on text field
		fillerField.requestFocus();
		fillerField.selectAll();

		// Save state when dialog is closed
		setOnHiding(event -> fillerText = fillerField.getText());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Integer show(
		Window	owner,
		String	title)
	{
		return new EraseUnusedClustersDialog(owner, title).showDialog();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected Integer getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
