/*====================================================================*\

PopUpEvent.java

Class: pop-up event.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import javafx.event.Event;
import javafx.event.EventType;

//----------------------------------------------------------------------


// CLASS: POP-UP EVENT


/**
 * This class implements an event that is related to pop-up windows.
 */

public class PopUpEvent
	extends Event
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** An event that signals that the content of a pop-up has changed. */
	public static final	EventType<PopUpEvent>	CONTENT_CHANGED	= new EventType<>("CONTENT_CHANGED");

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an event of the specified type.
	 *
	 * @param eventType
	 *          the type of the event.
	 */

	public PopUpEvent(EventType<PopUpEvent> eventType)
	{
		// Call superclass constructor
		super(eventType);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
