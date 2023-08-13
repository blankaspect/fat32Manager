/*====================================================================*\

ChoiceBoxWidthStabiliser.java

Class: decorator that stabilises the width of a choice box.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.choicebox;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.control.ChoiceBox;

//----------------------------------------------------------------------


// CLASS: DECORATOR THAT STABILISES THE WIDTH OF A CHOICE BOX


public class ChoiceBoxWidthStabiliser
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	double	choiceBoxWidth;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private ChoiceBoxWidthStabiliser(
		ChoiceBox<?>	choiceBox)
	{
		choiceBox.widthProperty().addListener((observable, oldWidth, newWidth) ->
		{
			double width = newWidth.doubleValue();
			if (choiceBoxWidth < width)
			{
				choiceBoxWidth = width;
				choiceBox.setPrefWidth(width);
			}
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static void apply(
		ChoiceBox<?>	choiceBox)
	{
		new ChoiceBoxWidthStabiliser(choiceBox);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
