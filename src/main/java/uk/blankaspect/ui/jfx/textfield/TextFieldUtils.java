/*====================================================================*\

TextFieldUtils.java

Class: text-field-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.textfield;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.control.TextField;

import javafx.scene.text.Font;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: TEXT-FIELD-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain TextField text fields}.
 */

public class TextFieldUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	double	CARET_WIDTH	= 2.0;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private TextFieldUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int getNumColumns(
		TextField	field,
		char		ch,
		int			length)
	{
		return getNumColumns(field, Character.toString(ch).repeat(length));
	}

	//------------------------------------------------------------------

	public static int getNumColumns(
		TextField	field,
		String      prototypeText)
	{
		Font font = field.getFont();
		double width = TextUtils.textWidth(font, prototypeText) + CARET_WIDTH;
		return (int)Math.ceil(width / TextUtils.textWidth(font, "W"));
	}

	//------------------------------------------------------------------

	public static void setNumColumns(
		TextField	field,
		char		ch,
		int			length)
	{
		field.setPrefColumnCount(getNumColumns(field, ch, length));
	}

	//------------------------------------------------------------------

	public static void setNumColumns(
		TextField	field,
		String   	prototypeText)
	{
		field.setPrefColumnCount(getNumColumns(field, prototypeText));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
