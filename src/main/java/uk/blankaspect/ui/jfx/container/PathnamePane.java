/*====================================================================*\

PathnamePane.java

Class: pathname pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.container;

//----------------------------------------------------------------------


// IMPORTS


import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import uk.blankaspect.ui.jfx.button.Buttons;

//----------------------------------------------------------------------


// CLASS: PATHNAME PANE


/**
 * This class implements a pane containing a text field for entering a pathname and a button containing an ellipsis,
 * which may be used, for example, to invoke a file chooser.
 */

public class PathnamePane
	extends HBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The gap between the text field and the button. */
	private static final	double	GAP	= 8.0;

	/** The padding around the button. */
	private static final	Insets	BUTTON_PADDING	= new Insets(2.0, 8.0, 2.0, 8.0);

	/** Miscellaneous strings. */
	private static final	String	ELLIPSIS_STR	= "...";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The button. */
	private	Button	button;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a pathname pane containing the specified text field.
	 *
	 * @param pathnameField
	 *          the text field for a pathname that will be added to this pane.
	 */

	public PathnamePane(
		TextField	pathnameField)
	{
		// Call superclass constructor
		super(GAP);

		// Set properties
		setAlignment(Pos.CENTER_LEFT);

		// Set properties of pathname field
		HBox.setHgrow(pathnameField, Priority.ALWAYS);

		// Create button
		button = Buttons.hNoShrink(ELLIPSIS_STR);
		button.setPadding(BUTTON_PADDING);
		button.prefHeightProperty().bind(pathnameField.heightProperty());

		// Add children to this pane
		getChildren().addAll(pathnameField, button);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a pathname pane containing the specified text field.
	 *
	 * @param pathnameField
	 *          the text field for a pathname that will be added to this pane.
	 * @param actionHandler
	 *          the action-event handler that will be set on the button.
	 */

	public PathnamePane(
		TextField					pathnameField,
		EventHandler<ActionEvent>	actionHandler)
	{
		// Call alternative constructor
		this(pathnameField);

		// Set action-event handler on button
		button.addEventHandler(ActionEvent.ACTION, actionHandler);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the button of this pathname pane.
	 *
	 * @return the button of this pathname pane.
	 */

	public Button getButton()
	{
		return button;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
