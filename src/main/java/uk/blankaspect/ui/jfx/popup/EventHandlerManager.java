/*====================================================================*\

EventHandlerManager.java

Class: manager of an event handler.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import java.util.EnumSet;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

import javafx.scene.Node;

//----------------------------------------------------------------------


// CLASS: MANAGER OF AN EVENT HANDLER


/**
 * This class implements a manager of an {@linkplain EventHandler event handler}.
 *
 * @param <T>
 *          the type of the event that the event handler can handle.
 */

public class EventHandlerManager<T extends Event>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * The kinds of event handler.
	 */

	public enum HandlerKind
	{
		/**
		 * An event handler.
		 */
		HANDLER,

		/**
		 * An event filter.
		 */
		FILTER
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The node to which the event handler may be added and removed through this manager. */
	private	Node					node;

	/** The type of event that {@linkplain #handler managed event handler} can handle. */
	private	EventType<T>			eventType;

	/** The managed event handler. */
	private	EventHandler<T>			handler;

	/** The kinds of handler that have been added to the target node through this manager. */
	private	EnumSet<HandlerKind>	handlerKinds;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a manager of an event handler.
	 *
	 * @param node
	 *          the node to which {@code handler} may be added and removed through the manager.
	 * @param eventType
	 *          the type of event that {@code handler} can handle.
	 * @param handler
	 *          the event handler.
	 */

	public EventHandlerManager(
		Node			node,
		EventType<T>	eventType,
		EventHandler<T>	handler)
	{
		// Initialise instance variables
		this.node = node;
		this.eventType = eventType;
		this.handler = handler;
		handlerKinds = EnumSet.noneOf(HandlerKind.class);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the node to which {@code handler} may be added and removed through this manager.
	 *
	 * @return the node to which {@code handler} may be added and removed through this manager.
	 */

	public Node getNode()
	{
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the type of event that the {@linkplain #getHandler() managed event handler} can handle.
	 *
	 * @return the type of event that the managed event handler can handle.
	 */

	public EventType<T> getEventType()
	{
		return eventType;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the managed event handler.
	 *
	 * @return the managed event handler.
	 */

	public EventHandler<T> getHandler()
	{
		return handler;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the kinds of handler that have been added to the target node through this manager.
	 *
	 * @return the kinds of handler that have been added to the target node through this manager.
	 */

	public EnumSet<HandlerKind> getHandlerKinds()
	{
		return handlerKinds;
	}

	//------------------------------------------------------------------

	/**
	 * Adds the {@linkplain #getHandler() managed event handler} to the {@linkplain #getNode() target node} as an event
	 * <i>handler</i>.
	 *
	 * @see Node#addEventHandler(EventType, EventHandler)
	 */

	public void addHandler()
	{
		if (!handlerKinds.contains(HandlerKind.HANDLER))
		{
			node.addEventHandler(eventType, handler);
			handlerKinds.add(HandlerKind.HANDLER);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Removes the {@linkplain #getHandler() managed event handler} from the {@linkplain #getNode() target node} if it
	 * was added to the node by {@link #addHandler()} or {@link #add(HandlerKind)}.
	 */

	public void removeHandler()
	{
		if (handlerKinds.contains(HandlerKind.HANDLER))
		{
			node.removeEventHandler(eventType, handler);
			handlerKinds.remove(HandlerKind.HANDLER);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Adds the {@linkplain #getHandler() managed event handler} to the {@linkplain #getNode() target node} as an event
	 * <i>filter</i>.
	 *
	 * @see Node#addEventFilter(EventType, EventHandler)
	 */

	public void addFilter()
	{
		if (!handlerKinds.contains(HandlerKind.FILTER))
		{
			node.addEventFilter(eventType, handler);
			handlerKinds.add(HandlerKind.FILTER);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Removes the {@linkplain #getHandler() managed event handler} from the {@linkplain #getNode() target node} if it
	 * was added to the node by {@link #addFilter()} or {@link #add(HandlerKind)}.
	 */

	public void removeFilter()
	{
		if (handlerKinds.contains(HandlerKind.FILTER))
		{
			node.removeEventFilter(eventType, handler);
			handlerKinds.remove(HandlerKind.FILTER);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Adds the {@linkplain #getHandler() managed event handler} to the {@linkplain #getNode() target node} as the
	 * specified kind of event handler.
	 *
	 * @param handlerKind
	 *          the kind of event handler as which the managed event handler will be added to the target node.
	 */

	public void addAs(
		HandlerKind	handlerKind)
	{
		switch (handlerKind)
		{
			case HANDLER:
				addHandler();
				break;

			case FILTER:
				addFilter();
				break;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Removes the {@linkplain #getHandler() managed event handler} from the {@linkplain #getNode() target node} if it
	 * was added to the node by {@link #addHandler()}, {@link #addFilter()} or {@link #add(HandlerKind)}.
	 *
	 * @param handlerKind
	 *          the kind of event handler as which the managed event handler will be added to the target node.
	 */

	public void removeAs(
		HandlerKind	handlerKind)
	{
		switch (handlerKind)
		{
			case HANDLER:
				removeHandler();
				break;

			case FILTER:
				removeFilter();
				break;
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
