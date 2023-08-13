/*====================================================================*\

ModifierKey.java

Enumeration: keys that modify key events and mouse events.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.event;

//----------------------------------------------------------------------


// IMPORTS


import java.util.EnumSet;

import java.util.function.Predicate;

import java.util.stream.Stream;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

//----------------------------------------------------------------------


// ENUMERATION: KEYS THAT MODIFY KEY EVENTS AND MOUSE EVENTS


/**
 * This is an enumeration of the modifier keys for which there are corresponding methods in {@link KeyEvent} and {@link
 * MouseEvent}.
 */

public enum ModifierKey
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	CONTROL
	(
		KeyEvent::isControlDown,
		MouseEvent::isControlDown
	),

	SHIFT
	(
		KeyEvent::isShiftDown,
		MouseEvent::isShiftDown
	),

	ALT
	(
		KeyEvent::isAltDown,
		MouseEvent::isAltDown
	),

	META
	(
		KeyEvent::isMetaDown,
		MouseEvent::isMetaDown
	),

	SHORTCUT
	(
		KeyEvent::isShortcutDown,
		MouseEvent::isShortcutDown
	);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The method that is called on a {@linkplain KeyEvent key event} to test for this modifier. */
	private	Predicate<KeyEvent>		keyEventTest;

	/** The method that is called on a {@linkplain MouseEvent mouse event} to test for this modifier. */
	private	Predicate<MouseEvent>	mouseEventTest;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an enumeration constant for a modifier key.
	 *
	 * @param keyEventTest
				the method that is called on a key event to test for the modifier.
	 * @param mouseEventTest
				the method that is called on a mouse event to test for the modifier.
	 */

	private ModifierKey(
		Predicate<KeyEvent>		keyEventTest,
		Predicate<MouseEvent>	mouseEventTest)
	{
		// Initialise instance variables
		this.keyEventTest = keyEventTest;
		this.mouseEventTest = mouseEventTest;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a set of modifier keys that are down in the specified key event.
	 *
	 * @param  event
				 the key event whose modifier keys are desired.
	 * @return a set of modifier keys that are down in {@code event}.
	 */

	public static EnumSet<ModifierKey> forKeyEvent(
		KeyEvent	event)
	{
		EnumSet<ModifierKey> modifiers = EnumSet.noneOf(ModifierKey.class);
		Stream.of(values())
				.filter(modifier -> modifier.keyEventTest.test(event))
				.findFirst()
				.ifPresent(modifier -> modifiers.add(modifier));
		return modifiers;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a set of modifier keys that are down in the specified mouse event.
	 *
	 * @param  event
				 the mouse event whose modifier keys are desired.
	 * @return a set of modifier keys that are down in {@code event}.
	 */

	public static EnumSet<ModifierKey> forMouseEvent(
		MouseEvent	event)
	{
		EnumSet<ModifierKey> modifiers = EnumSet.noneOf(ModifierKey.class);
		Stream.of(values())
				.filter(modifier -> modifier.mouseEventTest.test(event))
				.findFirst()
				.ifPresent(modifier -> modifiers.add(modifier));
		return modifiers;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
