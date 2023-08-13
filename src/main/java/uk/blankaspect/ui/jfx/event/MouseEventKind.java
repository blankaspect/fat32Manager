/*====================================================================*\

MouseEventKind.java

Enumeration: mouse-event kind.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.event;

//----------------------------------------------------------------------


// IMPORTS


import javafx.event.EventHandler;
import javafx.event.EventType;

import javafx.scene.Node;

import javafx.scene.input.MouseEvent;

//----------------------------------------------------------------------


// ENUMERATION: MOUSE-EVENT KIND


/**
 * This is an enumeration of kinds of mouse events.  Its {@link Handler} member class is a convenience class that
 * associates a kind of mouse event with a handler for that kind of event.
 */

public enum MouseEventKind
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * Corresponds to a {@link MouseEvent#MOUSE_ENTERED MOUSE_ENTERED} event.
	 */
	ENTERED
	(
		"entered",
		MouseEvent.MOUSE_ENTERED
	),

	/**
	 * Corresponds to a {@link MouseEvent#MOUSE_EXITED MOUSE_EXITED} event.
	 */
	EXITED
	(
		"exited",
		MouseEvent.MOUSE_EXITED
	),

	/**
	 * Corresponds to a {@link MouseEvent#MOUSE_MOVED MOUSE_MOVED} event.
	 */
	MOVED
	(
		"moved",
		MouseEvent.MOUSE_MOVED
	),

	/**
	 * Corresponds to a {@link MouseEvent#MOUSE_PRESSED MOUSE_PRESSED} event.
	 */
	PRESSED
	(
		"pressed",
		MouseEvent.MOUSE_PRESSED
	),

	/**
	 * Corresponds to a {@link MouseEvent#MOUSE_RELEASED MOUSE_RELEASED} event.
	 */
	RELEASED
	(
		"released",
		MouseEvent.MOUSE_RELEASED
	),

	/**
	 * Corresponds to a {@link MouseEvent#MOUSE_CLICKED MOUSE_CLICKED} event.
	 */
	CLICKED
	(
		"clicked",
		MouseEvent.MOUSE_CLICKED
	),

	/**
	 * Corresponds to a {@link MouseEvent#MOUSE_DRAGGED MOUSE_DRAGGED} event.
	 */
	DRAGGED
	(
		"dragged",
		MouseEvent.MOUSE_DRAGGED
	),

	/**
	 * Corresponds to a {@link MouseEvent#DRAG_DETECTED DRAG_DETECTED} event.
	 */
	DRAG_DETECTED
	(
		"dragDetected",
		MouseEvent.DRAG_DETECTED
	);

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: EVENT HANDLER


	/**
	 * This class associates a kind of mouse event with a handler for that kind of event.
	 */

	public static class Handler
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates an instance of an association between the specified kind of mouse event and the specified handler.
		 *
		 * @param eventKind  the kind of mouse event with which {@code handler} will be associated.
		 * @param handler    the handler that will be associated with {@code eventKind}.
		 */

		public Handler(MouseEventKind           eventKind,
					   EventHandler<MouseEvent> handler)
		{
			// Initialise instance variables
			this.eventKind = eventKind;
			this.handler = handler;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the {@linkplain MouseEventKind kind of mouse event} with which this object is associated.
		 *
		 * @return the kind of mouse event with which this object is associated.
		 */

		public MouseEventKind getEventKind()
		{
			return eventKind;
		}

		//--------------------------------------------------------------

		/**
		 * Returns the mouse-event handler with which this object is associated.
		 *
		 * @return the mouse-event handler with which this object is associated.
		 */

		public EventHandler<MouseEvent> getHandler()
		{
			return handler;
		}

		//--------------------------------------------------------------

		/**
		 * Adds the mouse-event handler with which this object is associated to the specified node.
		 *
		 * @param node  the node to which this handler will be added.
		 */

		public void addHandler(Node node)
		{
			node.addEventHandler(eventKind.eventType, handler);
		}

		//--------------------------------------------------------------

		/**
		 * Removes the mouse-event handler with which this object is associated from the specified node.
		 *
		 * @param node  the node from which this handler will be removed.
		 */

		public void removeHandler(Node node)
		{
			node.removeEventHandler(eventKind.eventType, handler);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The kind of mouse event with which {@link #handler} is associated. */
		private	MouseEventKind				eventKind;

		/** The event handler for {@link #eventKind}. */
		private	EventHandler<MouseEvent>	handler;

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an enumeration constant for a kind of mouse event that has the specified key and corresponds to the
	 * specified event type.
	 *
	 * @param key        the key associated with the enumeration constant.
	 * @param eventType  the type of mouse event that corresponds to the enumeration constant.
	 */

	private MouseEventKind(String                key,
						   EventType<MouseEvent> eventType)
	{
		// Initialise instance variables
		this.key = key;
		this.eventType = eventType;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the key for this kind of mouse event.
	 *
	 * @return the key for this kind of mouse event.
	 */

	public String getKey()
	{
		return key;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the type of key event that corresponds to this kind of mouse event.
	 *
	 * @return the type of key event that corresponds to this kind of mouse event.
	 */

	public EventType<MouseEvent> getEventType()
	{
		return eventType;
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified event handler for this kind of mouse event to the specified node.
	 *
	 * @param node     the node to which {@code handler} will be added.
	 * @param handler  the event handler for this kind of mouse event that will be added to {@code node}.
	 */

	//------------------------------------------------------------------

	public void addHandler(Node                     node,
						   EventHandler<MouseEvent> handler)
	{
		node.addEventHandler(eventType, handler);
	}

	//------------------------------------------------------------------

	/**
	 * Removes the specified event handler for this kind of mouse event from the specified node.
	 *
	 * @param node     the node from which {@code handler} will be removed.
	 * @param handler  the event handler for this kind of mouse event that will be removed from {@code node}.
	 */

	public void removeHandler(Node                     node,
							  EventHandler<MouseEvent> handler)
	{
		node.removeEventHandler(eventType, handler);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The key associated with this enumeration constant. */
	private	String					key;

	/** The type of mouse event that corresponds to this enumeration constant. */
	private	EventType<MouseEvent>	eventType;

}

//----------------------------------------------------------------------
