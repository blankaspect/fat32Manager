/*====================================================================*\

TabPane2.java

Class: JavaFX tabbed pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.tabbedpane;

//----------------------------------------------------------------------


// IMPORTS


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.collections.ListChangeListener;

import javafx.event.Event;
import javafx.event.EventType;

import javafx.geometry.Bounds;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import javafx.scene.image.Image;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import javafx.scene.transform.Scale;

import javafx.util.Duration;

import uk.blankaspect.common.tuple.StrKVPair;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.StyleProperty;
import uk.blankaspect.ui.jfx.style.StyleSelector;
import uk.blankaspect.ui.jfx.style.StyleUtils;

import uk.blankaspect.ui.jfx.tooltip.TooltipDecorator;

//----------------------------------------------------------------------


// CLASS: JAVAFX TABBED PANE


/**
 * This class extends {@link TabPane} with the following abilities, both of which require a tab to have an {@linkplain
 * Tab#getId() identifier}:
 * <ul>
 *   <li>The order of tabs may be changed by dragging and dropping a tab.</li>
 *   <li>The <i>close</i> button of a tab may be hidden unless the mouse cursor is within the tab.</li>
 * </ul>
 */

public class TabPane2
	extends TabPane
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The prefix of property keys. */
	private static final	String	PROPERTY_KEY_PREFIX	= TabPane2.class.getCanonicalName() + ".";

	/** CSS classes. */
	public static final		String	STYLE_CLASS_TAB	= "tab";

	/** CSS property values. */
	private static final	String	NONE_STR	= "none";

	/** The style selector of the <i>close</i> button of a tab. */
	private static final	String	TAB_CLOSE_BUTTON_STYLE_SELECTOR	= ".tab-close-button";

	/** The default delay in milliseconds from when a tab receives a DRAG_ENTERED event to the selection of the tab. */
	private static final	double	DEFAULT_DRAG_SELECTION_DELAY	= 500.0;

	/** Keys of properties. */
	public interface PropertyKey
	{
		/**
		 * Tab node property
		 * Value type: any
		 * Setting this property prevents a tab node from acting as the source of a drag.
		 */
		String	NOT_DRAG_SOURCE		= PROPERTY_KEY_PREFIX + "notDragSource";

		/**
		 * Tab node property
		 * Value type: any
		 * Setting this property prevents a tab node from acting as the target of a drag.
		 */
		String	NOT_DRAG_TARGET		= PROPERTY_KEY_PREFIX + "notDragTarget";

		/**
		 * Tab property
		 * Value type: Boolean
		 * If this property is set, the <i>close</i> button of a tab will be hidden unless the mouse cursor is within
		 * the tab.  The initial value of the property must be {@code false}.
		 */
		String	HIDE_CLOSE_BUTTON	= PROPERTY_KEY_PREFIX + "hideCloseButton";

		/**
		 * Tab property
		 * Value type: String
		 * The value of this property is the text of the tooltip of a tab.
		 */
		String	TOOLTIP				= PROPERTY_KEY_PREFIX + "tooltip";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The tab node that is the source of a drag action. */
	private	Node		dragSource;

	/** The delay from when a tab receives a DRAG_ENTERED event to the selection of the tab.  If the delay is negative,
		the DRAG_ENTERED event is ignored. */
	private	double		dragSelectionDelay;

	/** The timeline that implements a delay in selecting a tab when a drag enters it. */
	private	Timeline	dragSelectionTimeline;

	/** The maximum width of the snapshot of the content of a dragged tab that is used as the drag image. */
	private	double		maxDragImageWidth;

	/** The maximum height of the snapshot of the content of a dragged tab that is used as the drag image. */
	private	double		maxDragImageHeight;

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: TABBED-PANE EVENT


	/**
	 * This class implements a JavaFX event that is associated with a {@linkplain TabPane2 tabbed pane}.
	 */

	public static class TabPaneEvent
		extends Event
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** This type of event is fired when the order of the tabs of a {@link TabPane2} changes. */
		public static final EventType<TabPaneEvent>	TAB_ORDER_CHANGED	= new EventType<>("TAB_ORDER_CHANGED");

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The tabbed pane with which this event is associated. */
		private	TabPane2	tabPane;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates an event of the specified type that is associated with the specified tabbed pane.
		 *
		 * @param eventType
		 *          the type of event that will be created.
		 * @param tabPane
		 *         the tabbed pane with which the event will be associated.
		 */

		public TabPaneEvent(EventType<TabPaneEvent> eventType,
							TabPane2                tabPane)
		{
			// Call superclass constructor
			super(eventType);

			// Initialise instance fields
			this.tabPane = tabPane;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the tabbed pane with which this event is associated.
		 *
		 * @return the tabbed pane with which this event is associated.
		 */

		public TabPane2 getTabPane()
		{
			return tabPane;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a tabbed pane with no tabs.
	 */

	public TabPane2()
	{
		// Call alternative constructor
		this((Tab[])null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a tabbed pane with the specified tabs.
	 *
	 * @param tabs
	 *          the tabs that will be added to the tabbed pane.
	 */

	public TabPane2(Tab... tabs)
	{
		// Initialise instance fields
		dragSelectionDelay = DEFAULT_DRAG_SELECTION_DELAY;

		// Disable animation when tabs are opened and closed
		StyleUtils.setProperties(this, StrKVPair.of(StyleProperty.OPEN_TAB_ANIMATION, NONE_STR),
								 StrKVPair.of(StyleProperty.CLOSE_TAB_ANIMATION, NONE_STR));

		// When drag enters this pane, set event handlers on tab nodes to handle dragging and dropping
		addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, mouseEvent ->
		{
			if (dragSource == null)
				setEventHandlers();
		});

		// Select tab when drag enters it
		addEventHandler(DragEvent.DRAG_ENTERED_TARGET, event ->
		{
			// If drag selection is enabled and event target is a node ...
			if ((dragSelectionDelay >= 0.0) && (event.getTarget() instanceof Node node))
			{
				// Get ID of target node
				String id = node.getId();

				// If event target is a tab of this tabbed pane, select the tab after a delay
				if ((id != null) && node.getStyleClass().contains(STYLE_CLASS_TAB) && isNearestTabPaneAncestorOf(node))
				{
					// Stop any pending selection
					stopPendingDragSelection();

					// Select tab after a delay
					getTabs().stream()
								.filter(tab -> id.equals(tab.getId()) && (getSelectionModel().getSelectedItem() != tab))
								.findFirst()
								.ifPresent(tab ->
								{
									dragSelectionTimeline =
												new Timeline(new KeyFrame(Duration.millis(dragSelectionDelay), event0 ->
												{
													getSelectionModel().select(tab);
													dragSelectionTimeline = null;
												}));
									dragSelectionTimeline.play();
								});
				}
			}
		});

		// Stop any pending selection of tab when drag leaves a tab
		addEventHandler(DragEvent.DRAG_EXITED_TARGET, event ->
		{
			if ((event.getTarget() instanceof Node node) && node.getStyleClass().contains(STYLE_CLASS_TAB))
				stopPendingDragSelection();
		});

		// Updates the "hide 'close' button" state of tabs when the list of tabs changes
		getTabs().addListener((ListChangeListener<Tab>) change ->
		{
			// Reset "hide 'close' button" property of tabs that were removed from list
			while (change.next())
			{
				if (change.wasRemoved())
				{
					for (Tab tab : change.getRemoved())
					{
						if (tab.getProperties().containsKey(PropertyKey.HIDE_CLOSE_BUTTON))
							tab.getProperties().put(PropertyKey.HIDE_CLOSE_BUTTON, false);
					}
				}
			}

			// Update "hide 'close' button" state of tabs
			updateTabs();
		});

		// Updates the "hide 'close' button" state of tabs when tabbed pane acquires a skin
		skinProperty().addListener((observable, oldSkin, skin) ->
		{
			if (skin != null)
				updateTabs();
		});

		// Add tabs
		if (tabs != null)
			getTabs().addAll(tabs);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the specified tab node.
	 *
	 * @param  tabNode
	 *           the node whose index is required.
	 * @return the index of <i>tabNode</i>, or -1 if <i>tabNode</i> was not found in its parent's list of children.
	 */

	public static int getTabNodeIndex(Node tabNode)
	{
		int index = -1;
		for (Node child : tabNode.getParent().getChildrenUnmodifiable())
		{
			if (child.getStyleClass().contains(STYLE_CLASS_TAB))
				++index;
			if (child == tabNode)
				break;
		}
		return index;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the specified tab to the end of the list of tabs.  If the tab's properties contain the key {@link
	 * #HIDE_CLOSE_BUTTON_PROPERTY_KEY}, the <i>close</i> button of the tab will be hidden unless the mouse cursor is
	 * within the tab.
	 *
	 * @param tab
	 *          the tab that will be added to the list of tabs.
	 */

	public void addTab(Tab tab)
	{
		// Add tab to list
		getTabs().add(tab);

		// Update "hide 'close' button" state of tabs
		updateTabs();
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified tab to the list of tabs at the specified index.  If the tab's properties contain the key
	 * {@link #HIDE_CLOSE_BUTTON_PROPERTY_KEY}, the <i>close</i> button of the tab will be hidden unless the mouse
	 * cursor is within the tab.
	 *
	 * @param index
	 *          the index at which <i>tab</i> will be added to the list of tabs.
	 * @param tab
	 *          the tab that will be added to the list of tabs.
	 */

	public void addTab(int index,
					   Tab tab)
	{
		// Add tab to list
		getTabs().add(index, tab);

		// Update "hide 'close' button" state of tabs
		updateTabs();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the tab node that corresponds to the specified tab.
	 *
	 * @param  tab
	 *           the tab whose tab node is required.
	 * @return the tab node that corresponds to <i>tab</i>, or {@code null} if there is no such node.
	 * @throws IllegalStateException
	 *           if <i>tab</i> does not have an identifier.
	 */

	public Node findTabNode(Tab tab)
	{
		// Get the ID of the tab
		String id = tab.getId();

		// Validate the ID
		if (id == null)
			throw new IllegalStateException("No tab ID");

		// Find the tab node whose ID matches that of the tab
		return (getSkin() == null)
						? null
						: lookupAll(StyleSelector.TAB).stream()
														.filter(tabNode -> id.equals(tabNode.getId())
																				&& isNearestTabPaneAncestorOf(tabNode))
														.findFirst()
														.orElse(null);
	}

	//------------------------------------------------------------------

	/**
	 * Updates the <i>hide 'close' button</i> state of the tabs of this tabbed pane.  If a tab's properties contain the
	 * key {@link #HIDE_CLOSE_BUTTON_PROPERTY_KEY}, the <i>close</i> button of the tab will be hidden unless the mouse
	 * cursor is within the tab.
	 */

	public void updateTabs()
	{
		for (Tab tab : getTabs())
		{
			// Get "hide 'close' button" property
			Object value = tab.getProperties().get(PropertyKey.HIDE_CLOSE_BUTTON);

			// Set mouse-event handlers on the tab if they haven't already been set
			if ((value instanceof Boolean booleanValue) && !booleanValue)
			{
				// Find tab node
				Node tabNode = findTabNode(tab);

				// Hide 'close' button of tab when mouse cursor is outside tab; set tooltip of tab
				if (tabNode != null)
				{
					// Get 'close' button
					Node buttonNode = tabNode.lookup(TAB_CLOSE_BUTTON_STYLE_SELECTOR);

					// Hide 'close' button
					buttonNode.setOpacity(0.0);

					// Set mouse-event handlers hide 'close' button when mouse cursor is outside tab
					tabNode.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> buttonNode.setOpacity(1.0));
					tabNode.addEventHandler(MouseEvent.MOUSE_EXITED, event -> buttonNode.setOpacity(0.0));

					// Update "hide 'close' button" property
					tab.getProperties().put(PropertyKey.HIDE_CLOSE_BUTTON, true);

					// Set tooltip on tab node
					String tooltipText = (String)tab.getProperties().get(PropertyKey.TOOLTIP);
					if (tooltipText != null)
						TooltipDecorator.addTooltip(tabNode, tooltipText);
				}
			}
		}
	}

	//------------------------------------------------------------------

	/**
	 * Sets the delay from when a tab receives a DRAG_ENTERED event to the selection of the tab.
	 *
	 * @param delay
	 *          the delay (in milliseconds) from when a tab receives a DRAG_ENTERED event to the selection of the tab.
	 *          If this is negative, the DRAG_ENTERED event is ignored.
	 */

	public void setDragSelectionDelay(double delay)
	{
		// Stop any pending selection
		stopPendingDragSelection();

		// Set instance field
		dragSelectionDelay = delay;
	}

	//------------------------------------------------------------------

	/**
	 * Sets both the maximum width and the maximum height of the image of a dragged tab to the specified value.  If the
	 * size is zero or negative, an image of the tab will be used instead of an image of the content of the tab.
	 *
	 * @param maxSize
	 *          the value to which the maximum width and maximum height of the image of a dragged tab will be set.
	 */

	public void setMaxDragImageSize(double maxSize)
	{
		maxDragImageWidth = maxSize;
		maxDragImageHeight = maxSize;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the maximum width and the maximum height of the image of a dragged tab to the specified values.  If either
	 * value is zero or negative, an image of the tab will be used instead of an image of the content of the tab.
	 *
	 * @param maxWidth
	 *          the value to which the maximum width of the image of a dragged tab will be set.
	 * @param maxHeight
	 *          the value to which the maximum height of the image of a dragged tab will be set.
	 */

	public void setMaxDragImageSize(double maxWidth,
									double maxHeight)
	{
		maxDragImageWidth = maxWidth;
		maxDragImageHeight = maxHeight;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the tab that corresponds to the specified tab node.
	 *
	 * @param  tabNode
	 *           the tab node whose corresponding tab is required.
	 * @return the tab that corresponds to <i>tabNode</i>.
	 */

	private Tab getTab(Node tabNode)
	{
		return getTabs().get(getTabNodeIndex(tabNode));
	}

	//------------------------------------------------------------------

	/**
	 * Moves the tab that corresponds to the specified tab node in response to the specified drag event.
	 *
	 * @param tabNode
	 *          the tab node whose corresponding tab will be moved.
	 * @param event
	 *          the drag event that is the cause of the movement.
	 */

	private void moveTab(Node      tabNode,
						 DragEvent event)
	{
		// Get source index and target index
		int sourceIndex = getTabNodeIndex(dragSource);
		int targetIndex = getTabNodeIndex(tabNode);
		if (targetIndex > sourceIndex)
			--targetIndex;

		// Determine new index of drag source
		int index = (event.getX() < 0.5 * tabNode.getLayoutBounds().getWidth()) ? targetIndex : targetIndex + 1;

		// If tab index has changed, move tabs from one side of the drag source to the other side
		if (index != sourceIndex)
		{
			// Move tabs from before the drag source to after it
			if (index < sourceIndex)
			{
				for (int i = sourceIndex; i > index; i--)
					addTab(i, getTabs().remove(i - 1));
			}

			// Move tabs from after the drag source to before it
			else
			{
				for (int i = sourceIndex; i < index; i++)
					addTab(i, getTabs().remove(i + 1));
			}

			// Set event handlers on new tab nodes
			setEventHandlers();

			// Select tab
			getSelectionModel().select(index);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this tabbed pane is the nearest ancestor of the specified node that is an instance of
	 * {@code TabPane2}.
	 *
	 * @param  node
	 *           the node of interest.
	 * @return {@code true} if this tabbed pane is the nearest ancestor of <i>node</i> that is an instance of {@code
	 *         TabPane2}.
	 */

	private boolean isNearestTabPaneAncestorOf(Node node)
	{
		return (SceneUtils.searchAscending(node, node0 -> node0 instanceof TabPane2) == this);
	}

	//------------------------------------------------------------------

	/**
	 * Stops any pending selection of a tab that has received a DRAG_ENTERED event.
	 */

	private void stopPendingDragSelection()
	{
		if (dragSelectionTimeline != null)
		{
			dragSelectionTimeline.stop();
			dragSelectionTimeline = null;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Sets the DRAG_DETECTED mouse-event handler and the drag-event handlers on the tab nodes of this tabbed pane.
	 */

	private void setEventHandlers()
	{
		lookupAll(StyleSelector.TAB).stream()
									.filter(tabNode -> (tabNode != dragSource) && isNearestTabPaneAncestorOf(tabNode))
									.forEach(tabNode ->
		{
			// Handle DRAG_DETECTED event
			tabNode.setOnDragDetected(event ->
			{
				if ((event.getButton() == MouseButton.PRIMARY) && (dragSource == null)
						&& !getTab(tabNode).getProperties().containsKey(PropertyKey.NOT_DRAG_SOURCE))
				{
					// Set drag source
					dragSource = tabNode;

					// Start drag-and-drop
					Dragboard dragboard = tabNode.startDragAndDrop(TransferMode.MOVE);

					// If maximum size of drag image has been set, use scaled snapshot of tab content as drag image
					Image image = null;
					if ((maxDragImageWidth > 0.0) && (maxDragImageHeight > 0.0))
					{
						// Get content of tab
						Node tabContent = getTab(tabNode).getContent();

						// If tab has content, use a scaled snapshot of it
						if (tabContent != null)
						{
							Bounds bounds = tabContent.getLayoutBounds();
							double width = bounds.getWidth();
							double height = bounds.getHeight();
							if ((width > 0.0) && (height > 0.0))
							{
								double scale = Math.min(maxDragImageWidth / width, maxDragImageHeight / height);
								SnapshotParameters params = new SnapshotParameters();
								params.setTransform(Scale.scale(scale, scale));
								image = tabContent.snapshot(params, null);
							}
						}
					}

					// If there is no snapshot of tab content, use snapshot of tab node as drag image
					if (image == null)
						image = tabNode.snapshot(null, null);

					// Set the image that will represent the source of the drag
					dragboard.setDragView(image);

					// Put dummy content on dragboard
					ClipboardContent clipboardContent = new ClipboardContent();
					clipboardContent.putString("");
					dragboard.setContent(clipboardContent);

					// Consume event
					event.consume();
				}
			});

			// Handle DRAG_OVER event
			tabNode.setOnDragOver(event ->
			{
				// If drag source is being dragged over another tab node ...
				if ((dragSource != null) && (dragSource != tabNode))
				{
					// If tab node can act as drag target ...
					if (!getTab(tabNode).getProperties().containsKey(PropertyKey.NOT_DRAG_TARGET))
					{
						// Accept drag
						event.acceptTransferModes(TransferMode.MOVE);

						// Move tab
						moveTab(tabNode, event);
					}
				}

				// Consume event
				event.consume();
			});

			// Handle DRAG_DROPPED event
			tabNode.setOnDragDropped(event ->
			{
				// If drag source is from this tabbed pane, move tab
				if (dragSource != null)
					moveTab(tabNode, event);

				// Indicate that drag is complete
				event.setDropCompleted(true);

				// Consume event
				event.consume();
			});

			// Handle DRAG_DONE event
			tabNode.setOnDragDone(event ->
			{
				// Clear drag source
				dragSource = null;

				// Fire 'tab order changed' event
				fireEvent(new TabPaneEvent(TabPaneEvent.TAB_ORDER_CHANGED, this));

				// Consume event
				event.consume();
			});
		});
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
