/*====================================================================*\

Labels.java

Class: factory methods for labels.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.label;

//----------------------------------------------------------------------


// IMPORTS


import javafx.geometry.Pos;

import javafx.scene.control.Label;

import javafx.scene.layout.Region;

import javafx.scene.paint.Color;

import uk.blankaspect.ui.jfx.font.FontUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

//----------------------------------------------------------------------


// CLASS: FACTORY METHODS FOR LABELS


public class Labels
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private Labels()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Label hNoShrink()
	{
		Label label = new Label();
		label.setMinWidth(Region.USE_PREF_SIZE);
		return label;
	}

	//------------------------------------------------------------------

	public static Label hNoShrink(
		String	text)
	{
		Label label = new Label(text);
		label.setMinWidth(Region.USE_PREF_SIZE);
		return label;
	}

	//------------------------------------------------------------------

	public static Label expansive(
		String	text)
	{
		return expansive(text, 0.0, null, null);
	}

	//------------------------------------------------------------------

	public static Label expansive(
		String	text,
		double	fontSizeFactor)
	{
		return expansive(text, fontSizeFactor, null, null);
	}

	//------------------------------------------------------------------

	public static Label expansive(
		String	text,
		double	fontSizeFactor,
		Color	textColour,
		Color	backgroundColour)
	{
		Label label = new Label(text);
		label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		label.setAlignment(Pos.CENTER);
		if (fontSizeFactor > 0.0)
			label.setFont(FontUtils.defaultFont(fontSizeFactor));
		if (textColour != null)
			label.setTextFill(textColour);
		if (backgroundColour != null)
			label.setBackground(SceneUtils.createColouredBackground(backgroundColour));
		return label;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
