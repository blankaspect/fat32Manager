/*====================================================================*\

DialogState.java

Class: state of a simple dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import javafx.geometry.Point2D;

import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.ui.jfx.window.WindowState;

//----------------------------------------------------------------------


// CLASS: STATE OF A SIMPLE DIALOG


/**
 * This class implements a means of serialising the location, size and visibility of a {@linkplain SimpleDialog dialog}
 * as a {@link MapNode}.
 */

public class DialogState
	extends WindowState
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a dialog state.
	 *
	 * @param hidden
	 *          if {@code true}, the dialog is initially hidden.
	 * @param resizable
	 *          if {@code true}, the dialog is resizable.
	 */

	public DialogState(
		boolean	hidden,
		boolean	resizable)
	{
		// Call superclass constructor
		super(hidden, resizable);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog state for the specified dialog.
	 *
	 * @param dialog
	 *          the dialog with whose location, size and content size the dialog state will be initialised.
	 */

	public DialogState(
		SimpleDialog	dialog)
	{
		// Call superclass constructor
		super(dialog);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a locator function that returns a location from this dialog state.
	 *
	 * @return a locator function that returns a location from this dialog state, or {@code null} if the location is
	 *         {@code null}.
	 */

	public SimpleDialog.ILocator locator()
	{
		Point2D location = getLocation();
		return (location == null) ? null : (width, height) -> location;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
