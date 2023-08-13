/*====================================================================*\

MultiTextLabeller.java

Class: multiple-text decorator for label.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.label;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Map;

import javafx.scene.Node;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;

import javafx.scene.layout.StackPane;

import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.StyleUtils;

import uk.blankaspect.ui.jfx.text.Text2;

//----------------------------------------------------------------------


// CLASS: MULTIPLE-TEXT DECORATOR FOR LABEL


public class MultiTextLabeller<T>
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Labeled		target;
	private	StackPane	container;
	private	String		selectedText;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public MultiTextLabeller(
		Labeled			target,
		Map<T, String>	texts)
	{
		// Validate argument
		if (target == null)
			throw new IllegalArgumentException("Null target");

		// Update instance variable
		this.target = target;

		// Set properties of target
		target.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

		// Create label for each text
		container = new StackPane();
		for (Map.Entry<T, String> entry : texts.entrySet())
		{
			Text2 label = Text2.createCentred(entry.getValue());
			label.setUserData(entry.getKey());
			label.setVisible(false);
			label.getStyleClass().add(FxStyleClass.TEXT);
			StyleUtils.setProperty(label, FxProperty.FILL.getName(), FxProperty.TEXT_BASE_COLOR.getName());
			container.getChildren().add(label);
		}

		// Set container of labels as graphic of target
		target.setGraphic(container);
	}

	//------------------------------------------------------------------

	public MultiTextLabeller(
		Labeled			target,
		Map<T, String>	texts,
		T				key)
	{
		// Call alternative constructor
		this(target, texts);

		// Select initial text
		selectText(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Labeled getTarget()
	{
		return target;
	}

	//------------------------------------------------------------------

	public String getSelectedText()
	{
		return selectedText;
	}

	//------------------------------------------------------------------

	public void selectText(
		T	key)
	{
		// Invalidate selected text
		selectedText = null;

		// Hide all labels
		for (Node child : container.getChildren())
			child.setVisible(false);

		// Show first label whose key matches the target key
		if (key != null)
		{
			for (Node child : container.getChildren())
			{
				if (key.equals(child.getUserData()))
				{
					child.setVisible(true);
					selectedText = ((Text2)child).getText();
					break;
				}
			}
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
