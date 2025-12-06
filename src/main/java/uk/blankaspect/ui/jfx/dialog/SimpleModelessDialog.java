/*====================================================================*\

SimpleModelessDialog.java

Class: simple modeless dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import javafx.geometry.Dimension2D;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

//----------------------------------------------------------------------


// CLASS: SIMPLE MODELESS DIALOG


/**
 * This is the abstract base class of a modeless dialog that is based on a JavaFX {@link Stage} rather than a {@link
 * javafx.scene.control.Dialog}.
 */

public abstract class SimpleModelessDialog
	extends SimpleDialog
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a modeless dialog with the specified owner.
	 *
	 * @param owner
	 *          the owner of the dialog, or {@code null} for a dialog with no owner.
	 * @param boundsKey
	 *          the key with which the dialog will be associated in the map of locations and sizes.  If the map contains
	 *          an entry for the specified key, the location and size of the dialog will be set to the associated values
	 *          when the dialog is displayed.  If the key is {@code null}, it will be ignored.
	 */

	protected SimpleModelessDialog(
		Window	owner,
		String	boundsKey)
	{
		// Call alternative constructor
		this(owner, boundsKey, boundsKey, null, 1, null, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a modeless dialog with the specified owner and title.
	 *
	 * @param owner
	 *          the owner of the dialog, or {@code null} for a dialog with no owner.
	 * @param boundsKey
	 *          the key with which the dialog will be associated in the map of locations and sizes.  If the map contains
	 *          an entry for the specified key, the location and size of the dialog will be set to the associated values
	 *          when the dialog is displayed.  If the key is {@code null}, it will be ignored.
	 * @param title
	 *          the title of the dialog, which may be {@code null}.
	 */

	protected SimpleModelessDialog(
		Window	owner,
		String	boundsKey,
		String	title)
	{
		// Call alternative constructor
		this(owner, boundsKey, boundsKey, title, 1, null, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a modeless dialog with the specified owner and title.
	 *
	 * @param owner
	 *          the owner of the dialog, or {@code null} for a dialog with no owner.
	 * @param locationKey
	 *          the key with which the dialog will be associated in the map of locations.  If the map contains an entry
	 *          for the specified key, the location of the dialog will be set to the associated value when the dialog is
	 *          displayed.  If the key is {@code null}, it will be ignored.
	 * @param sizeKey
	 *          the key with which the dialog will be associated in the map of sizes.  If the map contains an entry for
	 *          the specified key, the size of the dialog will be set to the associated value when the dialog is
	 *          displayed.  If the key is {@code null}, it will be ignored.
	 * @param title
	 *          the title of the dialog, which may be {@code null}.
	 */

	protected SimpleModelessDialog(
		Window	owner,
		String	locationKey,
		String	sizeKey,
		String	title)
	{
		// Call alternative constructor
		this(owner, locationKey, sizeKey, title, 1, null, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a modeless dialog with the specified owner, title, locator and size.
	 *
	 * @param owner
	 *          the owner of the dialog, or {@code null} for a dialog with no owner.
	 * @param title
	 *          the title of the dialog, which may be {@code null}.
	 * @param locator
	 *          the function that returns the location of the dialog, which may be {@code null}.
	 * @param size
	 *          the size of the dialog, which may be {@code null}.
	 */

	protected SimpleModelessDialog(
		Window		owner,
		String		title,
		ILocator	locator,
		Dimension2D	size)
	{
		// Call superclass constructor
		this(owner, null, null, title, 1, locator, size);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a modeless dialog with the specified owner, title and locator.
	 *
	 * @param owner
	 *          the owner of the dialog, or {@code null} for a dialog with no owner.
	 * @param locationKey
	 *          the key with which the dialog will be associated in the map of locations.  If the map contains an entry
	 *          for the specified key, the location of the dialog will be set to the associated value when the dialog is
	 *          displayed.  If the key is {@code null}, it will be ignored.
	 * @param sizeKey
	 *          the key with which the dialog will be associated in the map of sizes.  If the map contains an entry for
	 *          the specified key, the size of the dialog will be set to the associated value when the dialog is
	 *          displayed.  If the key is {@code null}, it will be ignored.
	 * @param title
	 *          the title of the dialog, which may be {@code null}.
	 * @param numButtonPanes
	 *          the number of button panes.
	 * @param locator
	 *          the function that returns the location of the dialog, which may be {@code null}.
	 */

	protected SimpleModelessDialog(
		Window		owner,
		String		locationKey,
		String		sizeKey,
		String		title,
		int			numButtonPanes,
		ILocator	locator,
		Dimension2D	size)
	{
		// Call superclass constructor
		super(Modality.NONE, owner, locationKey, sizeKey, title, numButtonPanes, locator, size);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
