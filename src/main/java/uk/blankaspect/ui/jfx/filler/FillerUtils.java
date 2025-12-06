/*====================================================================*\

FillerUtils.java

Class: filler-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.filler;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

//----------------------------------------------------------------------


// CLASS: FILLER-RELATED UTILITY METHODS


public class FillerUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private FillerUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Region filler(
		double	minWidth,
		double	minHeight)
	{
		Region filler = new Region();
		filler.setMinSize(minWidth, minHeight);
		return filler;
	}

	//------------------------------------------------------------------

	public static Region hBoxFiller(
		double	minWidth)
	{
		Region filler = new Region();
		filler.setMinWidth(minWidth);
		HBox.setHgrow(filler, Priority.ALWAYS);
		return filler;
	}

	//------------------------------------------------------------------

	public static Region vBoxFiller(
		double	minHeight)
	{
		Region filler = new Region();
		filler.setMinHeight(minHeight);
		VBox.setVgrow(filler, Priority.ALWAYS);
		return filler;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
