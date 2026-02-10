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
	 * Creates and returns a button-information item for a button with the specified text.  The button is positioned on
	 * the left of its container.
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
	 * Creates and returns a list of button-information items for buttons with the specified texts.  All the buttons are
	 * positioned on the left of their container.
	 *
	 * @param  texts
	 *           the text of the buttons.
	 * @return a list of button-information items.
	 */

	public static List<ButtonInfo> allLeft(
		String...	texts)
	{
		return ButtonInfo.of(HPos.LEFT, texts);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a button-information item for a button with the specified text.  The button is positioned
	 * horizontally in the centre of its container.
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
	 * Creates and returns a list of button-information items for buttons with the specified texts.  All the buttons are
	 * positioned horizontally in the centre of their container.
	 *
	 * @param  texts
	 *           the text of the buttons.
	 * @return a list of button-information items.
	 */

	public static List<ButtonInfo> allCentre(
		String...	texts)
	{
		return ButtonInfo.of(HPos.CENTER, texts);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a button-information item for a button with the specified text.  The button is positioned on
	 * the right of its container.
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
	 * Creates and returns a list of button-information items for buttons with the specified texts.  All the buttons are
	 * positioned on the right of their container.
	 *
	 * @param  texts
	 *           the text of the buttons.
	 * @return a list of button-information items.
	 */

	public static List<ButtonInfo> allRight(
		String...	texts)
	{
		return ButtonInfo.of(HPos.RIGHT, texts);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a list of button-information items for buttons with the specified texts.  All the buttons
	 * have the specified horizontal position in their container.
	 *
	 * @param  position
	 *           the horizontal position of all the buttons.
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
