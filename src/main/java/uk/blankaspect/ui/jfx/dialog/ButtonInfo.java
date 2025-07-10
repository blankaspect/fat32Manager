/*====================================================================*\

ButtonInfo.java

Class: button information.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.List;

import javafx.geometry.HPos;

//----------------------------------------------------------------------


// CLASS: BUTTON INFORMATION


/**
 * This class encapsulates information about a button of a {@linkplain MessageDialog message dialog} or an {@linkplain
 * ExceptionDialog exception dialog}.
 */

public class ButtonInfo
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The text of the button. */
	private	String	text;

	/** The horizontal position of the button. */
	private	HPos	position;

	/** Flag: if {@code true}, the button is fired when the Ctrl+Enter key combination is pressed. */
	private	boolean	fireOnCtrlEnter;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of button information.
	 *
	 * @param text
	 *          the text of the button.
	 * @param position
	 *          the horizontal position of the button.
	 */

	public ButtonInfo(
		String	text,
		HPos	position)
	{
		// Call alternative constructor
		this(text, position, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of button information.
	 *
	 * @param text
	 *          the text of the button.
	 * @param position
	 *          the horizontal position of the button.
	 * @param fireOnCtrlEnter
	 *          if {@code true}, the button will be fired when the Ctrl+Enter key combination is pressed.
	 */

	public ButtonInfo(
		String	text,
		HPos	position,
		boolean	fireOnCtrlEnter)
	{
		// Initialise instance variables
		this.text = text;
		this.position = position;
		this.fireOnCtrlEnter = fireOnCtrlEnter;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a button-information item for a button with the specified text that is positioned on the
	 * left.
	 *
	 * @param  text
	 *           the text of the button.
	 * @return a button-information item for a left-positioned button whose text is {@code text}.
	 */

	public static ButtonInfo left(
		String	text)
	{
		return new ButtonInfo(text, HPos.LEFT);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a button-information item for a button with the specified text that is horizontally
	 * positioned in the centre.
	 *
	 * @param  text
	 *           the text of the button.
	 * @return a button-information item for a horizontally centred button whose text is {@code text}.
	 */

	public static ButtonInfo centre(
		String	text)
	{
		return new ButtonInfo(text, HPos.CENTER);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a button-information item for a button with the specified text that is positioned on the
	 * right.
	 *
	 * @param  text
	 *           the text of the button.
	 * @return a button-information item for a right-positioned button whose text is {@code text}.
	 */

	public static ButtonInfo right(
		String	text)
	{
		return new ButtonInfo(text, HPos.RIGHT);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a list of button-information items, with the specified texts for the buttons.  All items have
	 * the specified horizontal position.
	 *
	 * @param  position
	 *           the horizontal position of each button.
	 * @param  texts
	 *           the text of the buttons.
	 * @return a list of button-information items.
	 */

	public static List<ButtonInfo> of(
		HPos		position,
		String...	texts)
	{
		List<ButtonInfo> buttonInfos = new ArrayList<>();
		for (String text : texts)
			buttonInfos.add(new ButtonInfo(text, position));
		return buttonInfos;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the text of the button.
	 *
	 * @return the text of the button.
	 */

	public String getText()
	{
		return text;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the horizontal position of the button.
	 *
	 * @return the horizontal position of the button.
	 */

	public HPos getPosition()
	{
		return position;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the button is fired when the Ctrl+Enter key combination is pressed.
	 *
	 * @return {@code true} if the button is fired when the Ctrl+Enter key combination is pressed.
	 */

	public boolean isFireOnCtrlEnter()
	{
		return fireOnCtrlEnter;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
