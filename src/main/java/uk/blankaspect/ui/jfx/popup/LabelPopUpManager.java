/*====================================================================*\

LabelPopUpManager.java

Class: label pop-up manager.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.Node;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

import javafx.scene.input.MouseEvent;

//----------------------------------------------------------------------


// CLASS: LABEL POP-UP MANAGER


/**
 * This class provides a means of managing the creation and display of a pop-up window that contains a label.
 */

public class LabelPopUpManager
	extends PopUpManager
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The factory that creates a label for this pop-up manager. */
	private	ILabelFactory	labelFactory;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a label pop-up manager whose label is created by the specified factory.
	 *
	 * @param labelFactory
	 *          the factory that will create the label of the pop-up.
	 */

	public LabelPopUpManager(
		ILabelFactory	labelFactory)
	{
		// Initialise instance variables
		this.labelFactory = labelFactory;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * If a pop-up window for the specified target node is not already displayed, creates and displays a pop-up window
	 * that is associated with the target node.  The content of the pop-up is a label with the specified text and
	 * graphic node, and it is displayed at the location that is provided by the specified locator.
	 *
	 * @param target
	 *          the node with which the pop-up window will be associated.
	 * @param text
	 *          the text of the label that will be the content of the pop-up. If it is {@code null}, the label will
	 *          have no text.
	 * @param graphic
	 *          the graphic node of the label that will be the content of the pop-up. If it is {@code null}, the label
	 *          will have no graphic node.
	 * @param event
	 *          the mouse event that triggered the pop-up.
	 * @param locator
	 *          the provider of the screen location of the pop-up, given the layout bounds of the content and a locator
	 *          function.
	 */

	public void showPopUp(
		Node			target,
		String			text,
		Node			graphic,
		MouseEvent		event,
		IPopUpLocator	locator)
	{
		if (!isPopUpActivated(target))
		{
			// Create label and set its properties
			Label label = labelFactory.createLabel(text, graphic);
			if (text == null)
				label.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
			else if (graphic == null)
				label.setContentDisplay(ContentDisplay.TEXT_ONLY);

			// Create and display pop-up window
			showPopUp(target, label, event, locator);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Sets the label factory of this pop-up manager to the specified value.
	 *
	 * @param factory
	 *          the label factory.  If it is {@code null}, the label factory will be set to a default factory that
	 *          applies the properties of this pop-up manager to the labels that it creates.
	 */

	public void setLabelFactory(
		ILabelFactory	factory)
	{
		// Validate argument
		if (factory == null)
			throw new IllegalArgumentException("Null factory");

		// Update instance variable
		labelFactory = factory;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////

	/**
	 * This functional interface defines the methods that must be implemented by a factory of labels for a {@link
	 * LabelPopUpManager}.
	 */

	@FunctionalInterface
	public interface ILabelFactory
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates and returns a new instance of a label with the specified text and graphic.
		 *
		 * @param  text
		 *           the text of the label.
		 * @param  graphic
		 *           the graphic of the label.
		 * @return a new instance of a label.
		 */

		Label createLabel(
			String	text,
			Node	graphic);

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
