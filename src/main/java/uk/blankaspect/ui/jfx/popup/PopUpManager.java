/*====================================================================*\

PopUpManager.java

Class: pop-up manager.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.input.MouseEvent;

import javafx.stage.Popup;
import javafx.stage.Window;

import javafx.util.Duration;

import uk.blankaspect.common.function.IProcedure0;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.ui.jfx.event.MouseEventKind;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

//----------------------------------------------------------------------


// CLASS: POP-UP MANAGER


/**
 * This class provides a means of managing the creation and display of pop-up windows.
 */

public class PopUpManager
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The delay (in milliseconds) before a pop-up window is displayed. */
	private	int											delay;

	/** A map of mouse-event handlers that are set on the root node of a pop-up window. */
	private Map<String, List<MouseEventKind.Handler>>	mouseEventHandlers;

	/** The decorator that will be applied to a pop-up after the content is added to the pop-up. */
	private	IProcedure1<Popup>							popUpDecorator;

	/** A map of pop-up windows that are associated with nodes. */
	private	Map<Node, MapEntry>							popUps;

	/** The event handler that tracks the location of the mouse in the scene that contains the target of the pop-up
		window. */
	private	EventHandler<MouseEvent>					mouseTracker;

	/** The event that provides location information to the locator of the pop-up window. */
	private	MouseEvent									locationEvent;

	/** The scene that contains the target of the pop-up window. */
	private	Scene										targetScene;

	/** A list of managers of event handlers. */
	private	List<EventHandlerManager<?>>				eventHandlerManagers;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a manager of pop-up windows.
	 */

	public PopUpManager()
	{
		// Initialise instance variables
		mouseEventHandlers = new HashMap<>();
		popUps = new HashMap<>();
		mouseTracker = event -> locationEvent = event;
		eventHandlerManagers = Collections.emptyList();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the delay (in milliseconds) before a pop-up window is displayed to the specified value.  If it is negative,
	 * no pop-up window is displayed.
	 *
	 * @param delay
	 *          the delay (in milliseconds) before a pop-up window is displayed.
	 */

	public void setDelay(
		int	delay)
	{
		this.delay = delay;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the decorator that will be applied to a pop-up after the content is added to the pop-up.
	 *
	 * @param decorator
	 *          the decorator that will be applied to a pop-up after the content is added to the pop-up.  If it is
	 *          {@code null}, no decorator will be applied.
	 */

	public void setPopUpDecorator(
		IProcedure1<Popup>	decorator)
	{
		this.popUpDecorator = decorator;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the pop-up window that is associated with the specified node in this pop-up manager's map of pop-up
	 * windows.
	 *
	 * @param  node
	 *           the node whose associated pop-up window is required.
	 * @return the pop-up window that is associated with <i>node</i> in this pop-up manager's map of pop-up windows, or
	 *         {@code null} if the map does not contain the key <i>node</i>.
	 */

	public Popup getPopUp(
		Node	node)
	{
		MapEntry entry = popUps.get(node);
		return (entry == null) ? null : entry.popUp;
	}

	//------------------------------------------------------------------

	/**
	 * If a pop-up window for the specified target node is not already displayed, creates and displays a pop-up window
	 * that is associated with the target node.  The pop-up has the specified content and is displayed at the location
	 * that is provided by the specified locator.
	 *
	 * @param target
	 *          the node with which the pop-up window will be associated.
	 * @param content
	 *          the content of the pop-up.
	 * @param triggerEvent
	 *          the mouse event that triggered the pop-up.
	 * @param locator
	 *          the provider of the screen location of the pop-up, given the layout bounds of the content and a locator
	 *          function.
	 */

	public void showPopUp(
		Node			target,
		Node			content,
		MouseEvent		triggerEvent,
		IPopUpLocator	locator)
	{
		if (!isPopUpActivated(target) && (delay >= 0))
		{
			// Initialise location event and scene of pop-up target
			if (triggerEvent != null)
				locationEvent = triggerEvent;
			targetScene = null;

			// Create pop-up and add content to it
			Popup popUp = new Popup();
			popUp.getContent().add(content);

			// Apply decorator to pop-up
			if (popUpDecorator != null)
				popUpDecorator.invoke(popUp);

			// Add pop-up to map
			MapEntry entry = new MapEntry(popUp);
			popUps.put(target, entry);

			// Create procedure for displaying pop-up
			IProcedure0 show = () ->
			{
				// Remove mouse tracker
				removeMouseTracker();

				// If there is no location event, remove pop-up from map ...
				if (locationEvent == null)
					popUps.remove(target);

				// ... otherwise, display pop-up
				else
				{
					// Get window that contains pop-up target
					Window window = SceneUtils.getWindow(target);

					// If there is a window, display pop-up
					if (window != null)
					{
						// Add mouse-event handlers to root node
						Node popUpRoot = popUp.getScene().getRoot();
						popUpRoot.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> entry.mouseWithinPopup = true);
						popUpRoot.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
						{
							entry.mouseWithinPopup = false;
							hidePopUp(target);
						});
						popUpRoot.addEventHandler(MouseEvent.MOUSE_MOVED, event ->
						{
							Bounds bounds = target.localToScreen(target.getLayoutBounds());
							if ((bounds == null) || !bounds.contains(event.getScreenX(), event.getScreenY()))
							{
								entry.mouseWithinPopup = false;
								hidePopUp(target);
							}
						});

						// Add external mouse-event handlers to root node
						for (List<MouseEventKind.Handler> handlers : mouseEventHandlers.values())
						{
							for (MouseEventKind.Handler handler : handlers)
								handler.addHandler(popUpRoot);
						}

						// Hide pop-up if it loses focus (required for Linux)
						popUp.focusedProperty().addListener((observable, oldFocused, focused) ->
						{
							if (!focused)
								hidePopUp(target);
						});

						// Display pop-up
						popUp.show(window);

						// Set location of pop-up
						Point2D location = locator.getLocation(content.getLayoutBounds(),
															   () -> new Point2D(locationEvent.getScreenX(),
																				 locationEvent.getScreenY()));
						popUp.setX(location.getX());
						popUp.setY(location.getY());
					}
				}
			};

			// If no delay, display pop-up immediately ...
			if (delay == 0)
				show.invoke();

			// ... otherwise, display pop-up after a delay
			else
			{
				// Track mouse location in scene that contains pop-up target
				targetScene = target.getScene();
				if (targetScene != null)
					targetScene.addEventFilter(MouseEvent.MOUSE_MOVED, mouseTracker);

				// Create timer to display pop-up after a delay
				entry.delayTimer = new Timeline(new KeyFrame(Duration.millis((double)delay), event -> show.invoke()));

				// Start timer to display pop-up
				entry.delayTimer.play();
			}
		}
	}

	//------------------------------------------------------------------

	/**
	 * Hides the pop-up window that is associated with the node that is the source of the specified mouse event if the
	 * mouse cursor is not within the pop-up.
	 *
	 * @param event
	 *          the mouse event whose source is the node that is associated with the pop-up window that will be hidden.
	 */

	public void hidePopUp(
		MouseEvent	event)
	{
		Object source = event.getSource();
		if (source instanceof Node node)
			hidePopUp(node);
	}

	//------------------------------------------------------------------

	/**
	 * Hides the pop-up window that is associated with the specified node if the mouse cursor is not within the pop-up.
	 *
	 * @param node
	 *          the node whose associated pop-up window will be hidden.
	 */

	public void hidePopUp(
		Node	node)
	{
		MapEntry entry = popUps.get(node);
		if ((entry != null) && !entry.mouseWithinPopup)
		{
			// Stop delay timer
			if (entry.delayTimer != null)
				entry.delayTimer.stop();

			// Hide pop-up
			entry.popUp.hide();

			// Remove entry from map
			popUps.remove(node);

			// Remove mouse tracker
			removeMouseTracker();
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if a pop-up window that is associated with the node that is the source of the specified
	 * mouse event has been activated.
	 *
	 * @param  event
	 *           the mouse event whose source is the node that is associated with the pop-up window of interest.
	 * @return {@code true} if a pop-up window that is associated with the node that is the source of <i>event</i> has
	 *         been activated.
	 */

	public boolean isPopUpActivated(
		MouseEvent	event)
	{
		Object source = event.getSource();
		return (source instanceof Node node) && isPopUpActivated(node);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if a pop-up window that is associated with the specified node has been activated.
	 *
	 * @param  node
	 *           the node whose associated pop-up window is of interest.
	 * @return {@code true} if a pop-up window that is associated with <i>node</i> has been activated.
	 */

	public boolean isPopUpActivated(
		Node	node)
	{
		return popUps.containsKey(node);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified handler for the specified kind of mouse event to this pop-up manager's map of mouse-event
	 * handlers that are set on the root node of a pop-up window.  The handler is added to the map under the specified
	 * key.
	 * <p>
	 * The handler is also added to the root node of each pop-up window in this pop-up manager's {@linkplain #popUps map
	 * of pop-up windows}.
	 * </p>
	 *
	 * @param key
	 *          the key with which <i>handler</i> will be added to the map of mouse-event handlers.
	 * @param eventKind
	 *          the kind of mouse event that will be handled by <i>handler</i>.
	 * @param handler
	 *          the handler for <i>eventKind</i> mouse events.
	 */

	public void addMouseEventHandler(
		String						key,
		MouseEventKind				eventKind,
		EventHandler<MouseEvent>	handler)
	{
		// Get list of mouse-event handlers from map
		List<MouseEventKind.Handler> handlers = mouseEventHandlers.get(key);

		// If map doesn't contain a list for the key, create one and add it to the map
		if (handlers == null)
		{
			handlers = new ArrayList<>();
			mouseEventHandlers.put(key, handlers);
		}

		// Add mouse-event handler to list
		handlers.add(new MouseEventKind.Handler(eventKind, handler));

		// Add mouse-event handler to pop-ups
		popUps.values().forEach(entry -> eventKind.addHandler(entry.popUp.getScene().getRoot(), handler));
	}

	//------------------------------------------------------------------

	/**
	 * Removes all the mouse-event handlers from this pop-up manager's map of mouse-event handlers that are associated
	 * with the specified key in the map.
	 * <p>
	 * The handlers are also removed from the root node of each pop-up window in this pop-up manager's {@linkplain
	 * #popUps map of pop-up windows}.
	 * </p>
	 *
	 * @param key
	 *          the key whose associated event handlers will be removed from the map of mouse-event handlers.
	 */

	public void removeMouseEventHandlers(
		String	key)
	{
		if (mouseEventHandlers.containsKey(key))
		{
			// Remove mouse-event handlers from pop-ups
			popUps.values()
					.forEach(entry -> mouseEventHandlers.get(key)
					.forEach(handler -> handler.removeHandler(entry.popUp.getScene().getRoot())));

			// Remove mouse-event handlers from map
			mouseEventHandlers.remove(key);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns the node that is associated with the specified pop-up window in this pop-up manager's map of pop-up
	 * windows.
	 *
	 * @param  popUp
	 *           the pop-up window whose associated node is required.
	 * @return the node that is associated with <i>popUp</i> in this pop-up manager's map of pop-up windows, or {@code
	 *         null} if the map does not contain the value <i>popUp</i>.
	 */

	public Node findNode(
		Popup	popUp)
	{
		return popUps.entrySet().stream()
								.filter(entry -> entry.getValue().popUp == popUp)
								.map(entry -> entry.getKey())
								.findFirst()
								.orElse(null);
	}

	//------------------------------------------------------------------

	/**
	 * Adds event handlers to the specified target node to show and hide a pop-up window.  The event handlers can be
	 * removed with {@link #removeEventHandlers(Node)}.
	 *
	 * @param target
	 *          the node to which the event handlers will be added.
	 * @param targetPos
	 *          the reference position of {@code target}, which is aligned with {@code popupPos} when the pop-up is
	 *          displayed.
	 * @param popupPos
	 *          the reference position of the label of the pop-up, which is aligned with {@code targetPos} when the
	 *          pop-up is displayed.
	 * @param showPopUp
	 *          the procedure that shows the pop-up window.
	 * @see   #removeEventHandlers(Node)
	 */

	public void addEventHandlers(
		Node					target,
		VHPos					targetPos,
		VHPos					popupPos,
		IProcedure1<MouseEvent>	showPopUp)
	{
		// Create mouse-event handler
		EventHandler<MouseEvent> mouseEventHandler = event ->
		{
			// Show pop-up
			showPopUp.invoke(event);

			// Consume event
			event.consume();
		};

		// Add mouse-event handlers to show and hide pop-up
		addEventHandlerManager(target, MouseEvent.MOUSE_ENTERED, mouseEventHandler).addHandler();
		if ((targetPos == null)
				|| !(targetPos.getV().isOpposite(popupPos.getV()) || targetPos.getH().isOpposite(popupPos.getH())))
			addEventHandlerManager(target, MouseEvent.MOUSE_MOVED, mouseEventHandler).addHandler();
		addEventHandlerManager(target, MouseEvent.MOUSE_EXITED, this::hidePopUp).addHandler();

		// Add event handler to update pop-up when its content has changed
		addEventHandlerManager(target, PopUpEvent.CONTENT_CHANGED, event ->
		{
			// If pop-up is activated for target, hide and show pop-up
			if (isPopUpActivated(target))
			{
				// Hide pop-up
				hidePopUp(target);

				// Show pop-up
				showPopUp.invoke(null);
			}

			// Consume event
			event.consume();
		})
		.addHandler();
	}

	//------------------------------------------------------------------

	/**
	 * Removes the event handlers that were added to the specified target node with {@link #addEventHandlers(Node,
	 * VHPos, VHPos, IProcedure1)}, and removes the manager of each removed event handler from the list of event-handler
	 * managers.
	 *
	 * @param target
	 *          the node from which the event handlers will be removed.
	 * @see   #addEventHandlers(Node, VHPos, VHPos, IProcedure1)
	 */

	public void removeEventHandlers(
		Node	target)
	{
		// Remove event handlers from target; remove manager of each removed event handler from list
		for (EventHandlerManager<?> manager : new ArrayList<>(eventHandlerManagers))
		{
			if (manager.getNode() == target)
			{
				// Remove event handlers
				for (EventHandlerManager.HandlerKind handlerKind : manager.getHandlerKinds())
					manager.removeAs(handlerKind);

				// Remove manager from list
				eventHandlerManagers.remove(manager);
			}
		}

		// Replace modifiable empty list with unmodifiable list
		if (eventHandlerManagers.isEmpty())
			eventHandlerManagers = Collections.emptyList();
	}

	//------------------------------------------------------------------

	/**
	 * Adds a manager for the specified event handler to the list of managers, and returns the manager.
	 *
	 * @param  <T>
	 *           the type of the event that the event handler can handle.
	 * @param  node
	 *           the node to which {@code handler} may be added and removed through the manager.
	 * @param  eventType
	 *           the type of event that {@code handler} can handle.
	 * @param  handler
	 *           the event handler.
	 * @return a manager for {@code handler}.
	 */

	private <T extends Event> EventHandlerManager<T> addEventHandlerManager(
		Node			node,
		EventType<T>	eventType,
		EventHandler<T>	handler)
	{
		// Replace unmodifiable empty list with modifiable list
		if (eventHandlerManagers.isEmpty())
			eventHandlerManagers = new ArrayList<>();

		// Create event-handler manager and add it to list
		EventHandlerManager<T> manager = new EventHandlerManager<>(node, eventType, handler);
		eventHandlerManagers.add(manager);

		// Return event-handler manager
		return manager;
	}

	//------------------------------------------------------------------

	/**
	 * Removes the mouse-tracking event handler from the scene that contains the target of the pop-up window.
	 */

	private void removeMouseTracker()
	{
		if (targetScene != null)
		{
			targetScene.removeEventFilter(MouseEvent.MOUSE_MOVED, mouseTracker);
			targetScene = null;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: POP-UP MAP ENTRY


	/**
	 * This class implements an entry in the {@linkplain PopUpManager#popUps map from nodes to pop-up windows}.
	 */

	private static class MapEntry
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The pop-up window. */
		private	Popup		popUp;

		/** Flag: if {@code true}, the mouse cursor is within the root node of the pop-up window. */
		private	boolean		mouseWithinPopup;

		/** The timer that implements a delay before displaying the pop-up. */
		private	Timeline	delayTimer;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an entry for the {@linkplain PopUpManager#popUps map from nodes to pop-up windows}.
		 *
		 * @param popUp
		 *          the pop-up window of the entry.
		 */

		private MapEntry(
			Popup	popUp)
		{
			// Initialise instance variables
			this.popUp = popUp;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
