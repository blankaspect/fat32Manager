/*====================================================================*\

SceneUtils.java

Class: scene-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.scene;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import java.util.function.Function;
import java.util.function.Predicate;

import javafx.application.Platform;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;

import javafx.scene.image.Image;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Region;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import javafx.stage.Screen;
import javafx.stage.Window;

import uk.blankaspect.common.geometry.VHPos;

//----------------------------------------------------------------------


// CLASS: SCENE-RELATED UTILITY METHODS


public class SceneUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private SceneUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the specified node is the ancestor of the specified target node.
	 *
	 * @param  node
	 *           the node that is the prospective ancestor of {@code target}.
	 * @param  target
	 *           the node that is the prospective descendant of {@code node}.
	 * @return {@code true} if {@code node} is the ancestor of the {@code target}.
	 */

	public static boolean isAncestor(
		Node	node,
		Node	target)
	{
		Node node0 = target.getParent();
		while (node0 != null)
		{
			if (node0 == target)
				return true;
			node0 = node0.getParent();
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the depth of the specified node.  If the node has been added to a scene graph, this will be the number of
	 * levels below the root node of the scene; otherwise, it will be zero.
	 *
	 * @param  node
	 *           the node whose depth is desired.
	 * @return the depth of {@code node}.
	 */

	public static int getDepth(
		Node	node)
	{
		int depth = 0;
		while (true)
		{
			Node parent = node.getParent();
			if (parent == null)
				break;
			node = parent;
			++depth;
		}
		return depth;
	}

	//------------------------------------------------------------------

	/**
	 * Performs a depth-first traversal of a scene graph, starting from the specified root node and applying the
	 * specified action to each node that is visited.  The root node may optionally be omitted from having the action
	 * applied to it.
	 *
	 * @param  root
	 *           the node at which the traversal will start.
	 * @param  preorder
	 *           if {@code true}, each node will be visited <i>before</i> its descendants; otherwise, each node will be
	 *           visited <i>after</i> its descendants.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will have {@code action} applied to it.
	 * @param  action
	 *           the action that will be applied to each node of the scene graph that is visited.
	 * @return {@code false} if the traversal of the scene graph was terminated by {@code action}, possibly before all
	 *         nodes were visited; {@code true} if the traversal of all the items of the tree was completed.
	 */

	public static boolean visitDepthFirst(
		Node					root,
		boolean					preorder,
		boolean					includeRoot,
		Function<Node, Boolean>	action)
	{
		// Initialise stack
		Deque<Node> stack = new ArrayDeque<>();

		// Push root onto stack
		stack.addFirst(root);

		// Case: preorder
		if (preorder)
		{
			// While there are nodes on stack, visit them
			while (!stack.isEmpty())
			{
				// Pop node from stack
				Node node = stack.removeFirst();

				// Visit node
				if (((node != root) || includeRoot) && !action.apply(node))
					return false;

				// Push children onto stack
				if (node instanceof Parent parent)
				{
					List<Node> children = parent.getChildrenUnmodifiable();
					for (int i = children.size() - 1; i >= 0; i--)
						stack.addFirst(children.get(i));
				}
			}
		}

		// Case: postorder
		else
		{
			// Initialise list of pending nodes
			List<Node> pendingNodes = new ArrayList<>();

			// While there are nodes on stack, visit them
			while (!stack.isEmpty())
			{
				// Pop node from stack
				Node node = stack.removeFirst();

				// Assume visit is not pending
				boolean pending = false;

				// If node is not excluded, visit it if visit is pending
				if ((node != root) || includeRoot)
				{
					// Test whether visit is pending
					for (int i = pendingNodes.size() - 1; i >= 0; i--)
					{
						if (pendingNodes.get(i) == node)
						{
							pending = true;
							pendingNodes.remove(i);
							break;
						}
					}

					// If visit is pending, visit node ...
					if (pending)
					{
						if (!action.apply(node))
							return false;
					}

					// ... otherwise, push node back onto stack
					else
					{
						// Add node to pending nodes
						pendingNodes.add(node);

						// Push node onto stack
						stack.addFirst(node);
					}
				}

				// If visit was not pending, push children onto stack
				if (!pending && (node instanceof Parent parent))
				{
					List<Node> children = parent.getChildrenUnmodifiable();
					for (int i = children.size() - 1; i >= 0; i--)
						stack.addFirst(children.get(i));
				}
			}
		}

		// Indicate traversal completed
		return true;
	}

	//------------------------------------------------------------------

	/**
	 * Performs a breadth-first traversal of a scene graph, starting from the specified root node and applying the
	 * specified action to each node that is visited.  The root node may optionally be omitted from having the action
	 * applied to it.
	 *
	 * @param  root
	 *           the node at which the traversal will start.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will have {@code action} applied to it.
	 * @param  action
	 *           the action that will be applied to each node of the scene graph that is visited.
	 * @return {@code false} if the traversal of the scene graph was terminated by {@code action}, possibly before all
	 *         nodes were visited; {@code true} if the traversal of all the items of the tree was completed.
	 */

	public static boolean visitBreadthFirst(
		Node					root,
		boolean					includeRoot,
		Function<Node, Boolean>	action)
	{
		// Initialise queue
		Deque<Node> queue = new ArrayDeque<>(32);

		// Add root to queue
		queue.addLast(root);

		// While there are nodes to visit ...
		while (!queue.isEmpty())
		{
			// Get next node from queue
			Node node = queue.removeFirst();

			// Visit node
			if (((node != root) || includeRoot) && !action.apply(node))
				return false;

			// Add children of node to queue
			if (node instanceof Parent parent)
			{
				for (Node child : parent.getChildrenUnmodifiable())
					queue.addLast(child);
			}
		}

		// Indicate traversal completed
		return true;
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a scene graph from the specified node to the root node, applying the specified action to each node that
	 * is visited, including the root.
	 *
	 * @param  startNode
	 *           the node from which the ascent will start.
	 * @param  action
	 *           the action that will be applied to each node of the scene graph that is visited.
	 * @return {@code false} if the ascent of the scene graph was terminated by {@code action}, possibly before all
	 *         nodes were visited; {@code true} if the ascent was completed.
	 */

	public static boolean visitAscending(
		Node					startNode,
		Function<Node, Boolean>	action)
	{
		return visitAscending(startNode, null, false, action);
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a scene graph from the specified node to the specified end node, applying the specified action to each
	 * node that is visited.  The end node may optionally be visited.
	 *
	 * @param  startNode
	 *           the node from which the ascent will start.
	 * @param  endNode
	 *           the node at which the ascent will end.  {@code endNode} is visited if {@code includeEnd} is {@code
	 *           true}. If {@code endNode} is {@code null}, {@code startNode} and all its ancestors will be visited.
	 * @param  includeEnd
	 *           if {@code true}, {@code endNode} will have {@code action} applied to it.
	 * @param  action
	 *           the action that will be applied to each node of the scene graph that is visited.
	 * @return {@code false} if the ascent of the scene graph was terminated by {@code action}, possibly before all
	 *         nodes were visited; {@code true} if the ascent was completed.
	 */

	public static boolean visitAscending(
		Node					startNode,
		Node					endNode,
		boolean					includeEnd,
		Function<Node, Boolean>	action)
	{
		// Initialise current node
		Node node = startNode;

		// Ascend tree
		while (node != null)
		{
			// Visit node
			if ((includeEnd || (node != endNode)) && !action.apply(node))
				return false;

			// Test for end of ascent
			if (node == endNode)
				break;

			// Ascend tree
			node = node.getParent();
		}

		// Indicate ascent completed
		return true;
	}

	//------------------------------------------------------------------

	/**
	 * Performs a depth-first search of a scene graph, starting from the specified root node and applying the specified
	 * test to each node that is visited until a node is found that satisfies the test.  If such a node is found, the
	 * search terminates immediately and the matching node is returned.  The root node may optionally be included in the
	 * search.
	 *
	 * @param  root
	 *           the node at which the search will start.
	 * @param  preorder
	 *           if {@code true}, each node will be visited <i>before</i> its descendants; otherwise, each node will be
	 *           visited <i>after</i> its descendants.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will be included in the search.
	 * @param  test
	 *           the test that will be applied to each node of the scene graph that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 * @see    #searchBreadthFirst(Node, boolean, Predicate)
	 */

	public static Node searchDepthFirst(
		Node			root,
		boolean			preorder,
		boolean			includeRoot,
		Predicate<Node>	test)
	{
		// Initialise result
		Node result = null;

		// Initialise stack
		Deque<Node> stack = new ArrayDeque<>();

		// Push root onto stack
		stack.addFirst(root);

		// Case: preorder
		if (preorder)
		{
			// While there are nodes on stack, visit them
			while (!stack.isEmpty())
			{
				// Pop node from stack
				Node node = stack.removeFirst();

				// Test node
				if (((node != root) || includeRoot) && test.test(node))
				{
					result = node;
					break;
				}

				// Push children onto stack
				if (root instanceof Parent parent)
				{
					List<Node> children = parent.getChildrenUnmodifiable();
					for (int i = children.size() - 1; i >= 0; i--)
						stack.addFirst(children.get(i));
				}
			}
		}

		// Case: postorder
		else
		{
			// Initialise list of pending nodes
			List<Node> pendingNodes = new ArrayList<>();

			// While there are nodes on stack, visit them
			while (!stack.isEmpty())
			{
				// Pop node from stack
				Node node = stack.removeFirst();

				// Assume visit is not pending
				boolean pending = false;

				// If node is not excluded, visit it if visit is pending
				if ((node != root) || includeRoot)
				{
					// Test whether visit is pending
					for (int i = pendingNodes.size() - 1; i >= 0; i--)
					{
						if (pendingNodes.get(i) == node)
						{
							pending = true;
							pendingNodes.remove(i);
							break;
						}
					}

					// If visit is pending, test node ...
					if (pending)
					{
						if (test.test(node))
						{
							result = node;
							break;
						}
					}

					// ... otherwise, push node back onto stack
					else
					{
						// Add node to pending nodes
						pendingNodes.add(node);

						// Push node onto stack
						stack.addFirst(node);
					}
				}

				// If visit was not pending, push children onto stack
				if (!pending && (root instanceof Parent parent))
				{
					List<Node> children = parent.getChildrenUnmodifiable();
					for (int i = children.size() - 1; i >= 0; i--)
						stack.addFirst(children.get(i));
				}
			}
		}

		// Return result
		return result;
	}

	//------------------------------------------------------------------

	/**
	 * Performs a breadth-first search of a scene graph, starting from the specified root node and applying the
	 * specified test to each node that is visited until a node is found that satisfies the test.  If such a node is
	 * found, the search terminates immediately and the matching node is returned.  The root node may optionally be
	 * included in the search.
	 *
	 * @param  root
	 *           the node at which the search will start.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will be included in the search.
	 * @param  test
	 *           the test that will be applied to each node of the scene graph that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfied the test.
	 * @see    #searchDepthFirst(Node, boolean, boolean, Predicate)
	 */

	public static Node searchBreadthFirst(
		Node			root,
		boolean			includeRoot,
		Predicate<Node>	test)
	{
		// Initialise result
		Node result = null;

		// Initialise queue
		Deque<Node> queue = new ArrayDeque<>(32);

		// Add root to queue
		queue.addLast(root);

		// While there are nodes to visit ...
		while (!queue.isEmpty())
		{
			// Get next node from queue
			Node node = queue.removeFirst();

			// Test node
			if (((node != root) || includeRoot) && test.test(node))
			{
				result = node;
				break;
			}

			// Add children of node to queue
			if (node instanceof Parent parent)
			{
				for (Node child : parent.getChildrenUnmodifiable())
					queue.addLast(child);
			}
		}

		// Return result
		return result;
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a scene graph from the specified start node to the root node, applying the specified test to each node
	 * that is visited until a node is found that satisfies the test.  If such a node is found, the search terminates
	 * immediately and the matching node is returned.  The root is included in the search.
	 *
	 * @param  startNode
	 *           the node from which the search will start.
	 * @param  test
	 *           the test that will be applied to each node that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 * @see    #searchAscending(Node, Node, boolean, Predicate)
	 */

	public static Node searchAscending(
		Node			startNode,
		Predicate<Node>	test)
	{
		return searchAscending(startNode, null, false, test);
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a scene graph from the specified start node to the specified end node, applying the specified test to
	 * each node that is visited until a node is found that satisfies the test.  If such a node is found, the search
	 * terminates immediately and the node is returned.  The end node may optionally be included in the search.
	 *
	 * @param  startNode
	 *           the node from which the search will start.
	 * @param  endNode
	 *           the node at which the search will end.  {@code endNode} is included in the search if {@code includeEnd}
	 *           is {@code true}.  If {@code endNode} is {@code null}, {@code startNode} and all its ancestors will be
	 *           included in the search.
	 * @param  includeEnd
	 *           if {@code true}, {@code endNode} will have {@code test} applied to it.
	 * @param  test
	 *           the test that will be applied to each node that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 * @see    #searchAscending(Node, Predicate)
	 */

	public static Node searchAscending(
		Node			startNode,
		Node			endNode,
		boolean			includeEnd,
		Predicate<Node>	test)
	{
		// Initialise result
		Node result = null;

		// Perform search
		Node node = startNode;
		while (node != null)
		{
			// Test node
			if ((includeEnd || (node != endNode)) && test.test(node))
			{
				result = node;
				break;
			}

			// Test for end of ascent
			if (node == endNode)
				break;

			// Ascend the scene grpah
			node = node.getParent();
		}

		// Return result
		return result;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the window that contains the specified node.
	 *
	 * @param  node
	 *           the node whose window is desired.
	 * @return the {@linkplain Window window} that contains {@code node}, or {@code null} if {@code node} is {@code
	 *         null} or {@code node} is not contained by a scene.
	 */

	public static Window getWindow(
		Node	node)
	{
		Window window = null;
		if (node != null)
		{
			Scene scene = node.getScene();
			window = (scene == null) ? null : scene.getWindow();
		}
		return window;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified node is contained in a window and the window is showing.
	 *
	 * @param  node
	 *           the node of interest.
	 * @return {@code true} if {@code node} is contained in a window and the window is showing.
	 */

	public static boolean isShowing(
		Node	node)
	{
		Window window = getWindow(node);
		return (window != null) && window.isShowing();
	}

	//------------------------------------------------------------------

	/**
	 * If this method is running on the Java FX application thread, the specified procedure is run immediately;
	 * otherwise, the procedure is run on the Java FX application thread by calling {@link Platform#runLater(Runnable)}.
	 *
	 * @param runnable
	 *          the procedure that will be run on the Java FX application thread.
	 */

	public static void runOnFxApplicationThread(
		Runnable	runnable)
	{
		if (Platform.isFxApplicationThread())
			runnable.run();
		else
			Platform.runLater(runnable);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the background colour of the specified region or, if the region does not have a background, the
	 * background colour of its nearest ancestor.  The background colour is the colour of the first {@linkplain
	 * BackgroundFill fill} of the background of the region or its ancestor that is not fully transparent.
	 *
	 * @param  region
	 *           the region whose background colour is desired.
	 * @return the colour of the first fill of the background of the region or, if the region does not have a
	 *         background, its nearest ancestor that is not fully transparent, or {@code null} if the background of the
	 *         region or its ancestor does not have such a colour fill.
	 */

	public static Color getBackgroundColour(
		Region	region)
	{
		// Initialise colour
		Color[] backgroundColour = new Color[1];

		// Find first background colour of region or its ancestors that is not fully transparent
		searchAscending(region, node ->
		{
			if (node instanceof Region region0)
			{
				Background background = region0.getBackground();
				if (background != null)
				{
					for (BackgroundFill fill : background.getFills())
					{
						Paint paint = fill.getFill();
						if (paint instanceof Color colour)
						{
							if (colour.getOpacity() > 0.0)
							{
								backgroundColour[0] = colour;
								return true;
							}
						}
					}
				}
			}
			return false;
		});

		// Return background colour
		return backgroundColour[0];
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a background of the specified colour.
	 *
	 * @param  colour
	 *           the colour of the background.
	 * @return a {@linkplain Background background} whose colour is {@code colour}.
	 */

	public static Background createColouredBackground(
		Color	colour)
	{
		return new Background(new BackgroundFill(colour, null, null));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a two-part coloured background with the specified colours and insets.
	 *
	 * @param  colour1
	 *           the colour of the first part of the background.
	 * @param  insets1
	 *           the insets of the first part of the background.
	 * @param  colour2
	 *           the colour of the second part of the background.
	 * @param  insets2
	 *           the insets of the second part of the background.
	 * @return a two-part coloured {@linkplain Background background} with the specified colours and insets.
	 */

	public static Background createColouredBackground(
		Color	colour1,
		Insets	insets1,
		Color	colour2,
		Insets	insets2)
	{
		return new Background(new BackgroundFill(colour1, null, insets1), new BackgroundFill(colour2, null, insets2));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a solid one-pixel-wide border of the specified colour.
	 *
	 * @param  colour
	 *           the colour of the border.
	 * @return a solid one-pixel-wide {@linkplain Border border} whose colour is {@code colour}.
	 */

	public static Border createSolidBorder(
		Color	colour)
	{
		return new Border(new BorderStroke(colour, BorderStrokeStyle.SOLID, null, new BorderWidths(1.0)));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a solid one-pixel-wide border of the specified colour and with the specified insets.
	 *
	 * @param  colour
	 *           the colour of the border.
	 * @param  insets
	 *           the insets of the border.
	 * @return a solid one-pixel-wide {@linkplain Border border} whose colour is {@code colour} and whose insets are
	 *         {@code insets}.
	 */

	public static Border createSolidBorder(
		Color	colour,
		Insets	insets)
	{
		return new Border(new BorderStroke(colour, BorderStrokeStyle.SOLID, null, new BorderWidths(1.0), insets));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a solid border of the specified colour and with the specified set of widths.
	 *
	 * @param  colour
	 *           the colour of the border.
	 * @param  widths
	 *           the widths of the components of the border.
	 * @return a solid one-pixel-wide {@linkplain Border border} whose colour is {@code colour} and whose widths are
	 *         {@code widths}.
	 */

	public static Border createSolidBorder(
		Color			colour,
		BorderWidths	widths)
	{
		return new Border(new BorderStroke(colour, BorderStrokeStyle.SOLID, null, widths));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a solid one-pixel-wide border of the specified colour on the specified sides.
	 *
	 * @param  colour
	 *           the colour of the border.
	 * @param  sides
	 *           the sides of the border.
	 * @return a solid one-pixel-wide {@linkplain Border border} whose colour is {@code colour} on {@code sides}.
	 */

	public static Border createSolidBorder(
		Color	colour,
		Side...	sides)
	{
		return createSolidBorder(colour, 1.0, sides);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a solid one-pixel-wide border of the specified colour on the specified sides.
	 *
	 * @param  colour
	 *           the colour of the border.
	 * @param  sides
	 *           the sides of the border.
	 * @return a solid one-pixel-wide {@linkplain Border border} whose colour is {@code colour} on {@code sides}.
	 */

	public static Border createSolidBorder(
		Color			colour,
		Iterable<Side>	sides)
	{
		return createSolidBorder(colour, 1.0, sides);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a solid border of the specified colour and width on the specified sides.
	 *
	 * @param  colour
	 *           the colour of the border.
	 * @param  width
	 *           the width of the border.
	 * @param  sides
	 *           the sides of the border.
	 * @return a solid {@linkplain Border border} whose colour and width are {@code colour} and {@code width} on
	 *         {@code sides}.
	 */

	public static Border createSolidBorder(
		Color	colour,
		double	width,
		Side...	sides)
	{
		return createSolidBorder(colour, width, Arrays.asList(sides));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a solid border of the specified colour and width on the specified sides.
	 *
	 * @param  colour
	 *           the colour of the border.
	 * @param  width
	 *           the width of the border.
	 * @param  sides
	 *           the sides of the border.
	 * @return a solid {@linkplain Border border} whose colour and width are {@code colour} and {@code width} on
	 *         {@code sides}.
	 */

	public static Border createSolidBorder(
		Color			colour,
		double			width,
		Iterable<Side>	sides)
	{
		double top = 0.0;
		double bottom = 0.0;
		double left = 0.0;
		double right = 0.0;
		for (Side side : sides)
		{
			switch (side)
			{
				case TOP:
					top = width;
					break;

				case BOTTOM:
					bottom = width;
					break;

				case LEFT:
					left = width;
					break;

				case RIGHT:
					right = width;
					break;
			}
		}
		return createSolidBorder(colour, new BorderWidths(top, right, bottom, left));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a solid two-pixel-wide border consisting of a one-pixel-wide outer border and a
	 * one-pixel-wide inner border of the specified colours.
	 *
	 * @param  outerColour
	 *           the colour of the outer border.
	 * @param  innerColour
	 *           the colour of the inner border.
	 * @return a solid two-pixel-wide border consisting of a one-pixel-wide outer border whose colour is {@code
	 *         outerColour} and a one-pixel-wide inner border whose colour is {@code innerColour}.
	 */

	public static Border createSolidBorder(
		Color	outerColour,
		Color	innerColour)
	{
		return new Border(new BorderStroke(outerColour, BorderStrokeStyle.SOLID, null, null),
						  new BorderStroke(innerColour, BorderStrokeStyle.SOLID, null, null, new Insets(1.0)));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a one-pixel-wide border that consists of a dotted black border over a solid white border.
	 *
	 * @return a one-pixel-wide border that consists of a dotted black border over a solid white border.
	 */

	public static Border createFocusBorder()
	{
		return createFocusBorder(Insets.EMPTY);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a one-pixel-wide border that consists of a dotted black border over a solid white border.
	 *
	 * @param  insets
	 *           the insets of the border.
	 * @return a one-pixel-wide border that consists of a dotted black border over a solid white border.
	 */

	public static Border createFocusBorder(
		Insets	insets)
	{
		return new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, null, insets),
						  new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, null, insets));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a two-pixel-wide border that comprises a one-pixel-wide outer border and a one-pixel-wide
	 * inner border.  The outer border consists of a dotted black border over a solid white border; the inner border is
	 * a solid border of the specified colour; .
	 *
	 * @param  innerColour
	 *           the colour of the inner border.
	 * @return a two-pixel-wide border as described above.
	 */

	public static Border createOuterFocusBorder(
		Color	innerColour)
	{
		return new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, null),
						  new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, null),
						  new BorderStroke(innerColour, BorderStrokeStyle.SOLID, null, null, new Insets(1.0)));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a two-pixel-wide border that comprises a one-pixel-wide outer border and a one-pixel-wide
	 * inner border.  The outer border is a solid border of the specified colour; the inner border consists of a dotted
	 * black border over a solid white border.
	 *
	 * @param  outerColour
	 *           the colour of the outer border.
	 * @return a two-pixel-wide border as described above.
	 */

	public static Border createInnerFocusBorder(
		Color	outerColour)
	{
		return new Border(new BorderStroke(outerColour, BorderStrokeStyle.SOLID, null, null),
						  new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, null, new Insets(1.0)),
						  new BorderStroke(Color.BLACK, BorderStrokeStyle.DOTTED, null, null, new Insets(1.0)));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a snapshot of the specified node that has a transparent fill.  This method throws an
	 * exception if it is called on a thread other than the JavaFX application thread.
	 *
	 * @param  node
	 *           the node of which a snapshot will be created.
	 * @return a snapshot of {@code node} that has a transparent fill.
	 * @throws IllegalStateException
	 *           if this method is called on a thread other than the JavaFX application thread.
	 */

	public static Image createTransparentSnapshot(
		Node	node)
	{
		SnapshotParameters params = new SnapshotParameters();
		params.setFill(Color.TRANSPARENT);
		return node.snapshot(params, null);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of a target object with the specified location and size within the specified bounds such
	 * that the change to the location of the target is minimised and the top left corner of the target is within the
	 * bounds.
	 *
	 * @param  x
	 *           the <i>x</i> coordinate of the target.
	 * @param  y
	 *           the <i>y</i> coordinate of the target.
	 * @param  width
	 *           the width of the target.
	 * @param  height
	 *           the height of the target.
	 * @param  bounds
	 *           the bounds within which the target will be located.
	 * @return the location of the target within {@code bounds}.
	 */

	public static Point2D getLocationWithinBounds(
		double		x,
		double		y,
		double		width,
		double		height,
		Rectangle2D	bounds)
	{
		return getLocationWithinBounds(x, y, width, height, bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(),
									   bounds.getMaxY());
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of a target object with the specified location and size within the specified bounds such
	 * that the change to the location of the target is minimised and the top left corner of the target is within the
	 * bounds.
	 *
	 * @param  x
	 *           the <i>x</i> coordinate of the target.
	 * @param  y
	 *           the <i>y</i> coordinate of the target.
	 * @param  width
	 *           the width of the target.
	 * @param  height
	 *           the height of the target.
	 * @param  minX
	 *           the minimum <i>x</i> coordinate of the bounds within which the target will be located.
	 * @param  minY
	 *           the minimum <i>y</i> coordinate of the bounds within which the target will be located.
	 * @param  maxX
	 *           the maximum <i>x</i> coordinate of the bounds within which the target will be located.
	 * @param  maxY
	 *           the maximum <i>y</i> coordinate of the bounds within which the target will be located.
	 * @return the location of the target within the specified bounds.
	 */

	public static Point2D getLocationWithinBounds(
		double	x,
		double	y,
		double	width,
		double	height,
		double	minX,
		double	minY,
		double	maxX,
		double	maxY)
	{
		return new Point2D(Math.max(minX, Math.min(x, maxX - width)), Math.max(minY, Math.min(y, maxY - height)));
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of a target object of the specified width and height relative to another object (the
	 * <i>locator</i>) that has the specified location.  The location is initially calculated so that the top left
	 * corner of the target coincides with the location of the locator, then adjusted to ensure that as much as possible
	 * of the target lies within the visual bounds of the screen that contains the location of the locator.
	 *
	 * @param  width
	 *           the width of the target.
	 * @param  height
	 *           the height of the target.
	 * @param  locatorX
	 *           the <i>x</i> coordinate of the object relative to which the target will be located.
	 * @param  locatorY
	 *           the <i>y</i> coordinate of the object relative to which the target will be located.
	 * @return the location of the target relative to the locator, ensuring that as much as possible of the target lies
	 *         within the visual bounds of the screen that contains the location of the locator.
	 */

	public static Point2D getRelativeLocation(
		double	width,
		double	height,
		double	locatorX,
		double	locatorY)
	{
		// Initialise location
		Point2D location = null;

		// Determine relative location within visual bounds of screen that contains locator
		for (Screen screen : Screen.getScreens())
		{
			// Get visual bounds of screen
			Rectangle2D bounds = screen.getVisualBounds();

			// If locator is within visual bounds of screen, determine location of target within those bounds
			if (bounds.contains(locatorX, locatorY))
			{
				location = getLocationWithinBounds(locatorX, locatorY, width, height, bounds);
				break;
			}
		}

		// Return location
		return (location == null) ? new Point2D(0.0, 0.0) : location;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of a target object of the specified width and height relative to another object (the
	 * <i>locator</i>) that has the specified location and size.  The location is initially calculated so that the
	 * centre of the target coincides with the centre of the locator, then adjusted to ensure that as much as possible
	 * of the target lies within the visual bounds of the screen that contains the location of the locator.
	 *
	 * @param  width
	 *           the width of the target.
	 * @param  height
	 *           the height of the target.
	 * @param  locatorX
	 *           the <i>x</i> coordinate of the object relative to which the target will be located.
	 * @param  locatorY
	 *           the <i>y</i> coordinate of the object relative to which the target will be located.
	 * @param  locatorWidth
	 *           the width of the object relative to which the target will be located.
	 * @param  locatorHeight
	 *           the height of the object relative to which the target will be located.
	 * @return the location of the target relative to the locator, ensuring that as much as possible of the target lies
	 *         within the visual bounds of the screen that contains the location of the locator.
	 */

	public static Point2D getRelativeLocation(
		double	width,
		double	height,
		double	locatorX,
		double	locatorY,
		double	locatorWidth,
		double	locatorHeight)
	{
		// Initialise location
		Point2D location = null;

		// Get preferred coordinates of target
		double targetX = locatorX + 0.5 * (locatorWidth - width);
		double targetY = locatorY + 0.5 * (locatorHeight - height);

		// Find screen that has maximal intersection with locator
		double maxIntersection = 0.0;
		Screen maxIntersectionScreen = null;
		for (Screen screen : Screen.getScreens())
		{
			// Get visual bounds of screen
			Rectangle2D bounds = screen.getVisualBounds();

			// Get extent of horizontal intersection between screen and locator
			double dx = Math.min(bounds.getMaxX(), locatorX + locatorWidth) - Math.max(bounds.getMinX(), locatorX);

			// Get extent of vertical intersection between screen and locator
			double dy = Math.min(bounds.getMaxY(), locatorY + locatorHeight) - Math.max(bounds.getMinY(), locatorY);

			// Calculate area of intersection between screen and locator
			double intersection = ((dx > 0) && (dy > 0)) ? dx * dy : 0.0;

			// Update maximum intersection
			if (maxIntersection < intersection)
			{
				maxIntersection = intersection;
				maxIntersectionScreen = screen;
			}
		}

		// Determine location of target within visual bounds of screen that has maximal intersection with locator
		if (maxIntersectionScreen != null)
		{
			location = getLocationWithinBounds(targetX, targetY, width, height,
											   maxIntersectionScreen.getVisualBounds());
		}

		// Return location
		return (location == null) ? new Point2D(0.0, 0.0) : location;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the screen location of a target object of the specified width and height relative to the specified
	 * <i>locator</i> node by aligning the specified reference position of the target with the specified reference
	 * position of the locator node.
	 *
	 * @param  width
	 *           the width of the target.
	 * @param  height
	 *           the height of the target.
	 * @param  pos
	 *           the reference position of the target, which will be aligned with {@code locatorPos}.
	 * @param  locator
	 *           the node relative to which the target will be located.
	 * @param  locatorPos
	 *           the reference position of {@code locator}, which will be aligned with {@code pos}.
	 * @return the location of the target relative to the locator, aligning {@code pos} with {@code locatorPos}.
	 */

	public static Point2D getRelativeLocation(
		double	width,
		double	height,
		VHPos	pos,
		Node	locator,
		VHPos	locatorPos)
	{
		// Get bounds of locator
		Bounds locatorBounds = locator.localToScreen(locator.getLayoutBounds());
		if (locatorBounds == null)
			locatorBounds = new BoundingBox(0.0, 0.0, 0.0, 0.0);

		// Return location of target
		return getRelativeLocation(width, height, pos, locatorBounds.getMinX(), locatorBounds.getMinY(),
								   locatorBounds.getWidth(), locatorBounds.getHeight(), locatorPos);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of a target object of the specified width and height relative to another object (the
	 * <i>locator</i>) that has the specified location and size by aligning the specified reference position of the
	 * target with the specified reference position of the locator.
	 *
	 * @param  width
	 *           the width of the target.
	 * @param  height
	 *           the height of the target.
	 * @param  pos
	 *           the reference position of the target, which will be aligned with {@code locatorPos}.
	 * @param  locatorX
	 *           the <i>x</i> coordinate of the object relative to which the target will be located.
	 * @param  locatorY
	 *           the <i>y</i> coordinate of the object relative to which the target will be located.
	 * @param  locatorWidth
	 *           the width of the object relative to which the target will be located.
	 * @param  locatorHeight
	 *           the height of the object relative to which the target will be located.
	 * @param  locatorPos
	 *           the reference position of {@code locator}, which will be aligned with {@code pos}.
	 * @return the location of the target relative to the locator, aligning {@code pos} with {@code locatorPos}.
	 */

	public static Point2D getRelativeLocation(
		double	width,
		double	height,
		VHPos	pos,
		double	locatorX,
		double	locatorY,
		double	locatorWidth,
		double	locatorHeight,
		VHPos	locatorPos)
	{
		// Initialise x and y coordinates
		double x = 0.0;
		double y = 0.0;

		// Get x coordinate of locator
		double x1 = locatorX;
		double x3 = locatorX + locatorWidth;
		double x2 = 0.5 * (x1 + x3);
		switch (locatorPos.getH())
		{
			case LEFT:
				x = x1;
				break;

			case CENTRE:
				x = x2;
				break;

			case RIGHT:
				x = x3;
				break;
		}

		// Get y coordinate of locator
		double y1 = locatorY;
		double y3 = locatorY + locatorHeight;
		double y2 = 0.5 * (y1 + y3);
		switch (locatorPos.getV())
		{
			case TOP:
				y = y1;
				break;

			case CENTRE:
				y = y2;
				break;

			case BOTTOM:
				y = y3;
				break;
		}

		// Adjust x coordinate for position of target
		switch (pos.getH())
		{
			case LEFT:
				break;

			case CENTRE:
				x -= 0.5 * width;
				break;

			case RIGHT:
				x -= width;
				break;
		}

		// Adjust y coordinate for position of target
		switch (pos.getV())
		{
			case TOP:
				break;

			case CENTRE:
				y -= 0.5 * height;
				break;

			case BOTTOM:
				y -= height;
				break;
		}

		// Return location of target
		return new Point2D(x, y);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified location is within the visual bounds of any of the screens.
	 *
	 * @param  location
	 *           the location of interest.
	 * @return {@code true} if {@code location} is within the visual bounds of any of the screens.
	 */

	public static boolean isWithinScreen(
		Point2D	location)
	{
		return isWithinScreen(location.getX(), location.getY(), Insets.EMPTY);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified location is within the specified margins of the visual bounds of any of the
	 * screens.
	 *
	 * @param  location
	 *           the location of interest.
	 * @param  margins
	 *           the margins that will be applied to the visual bounds of each screen.
	 * @return {@code true} if {@code location} is within the visual bounds of any of the screens after {@code margins}
	 *         are applied to the visual bounds of each screen.
	 */

	public static boolean isWithinScreen(
		Point2D	location,
		Insets	margins)
	{
		return isWithinScreen(location.getX(), location.getY(), margins);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the location with the specified coordinates is within the visual bounds of any of the
	 * screens.
	 *
	 * @param  x
	 *           the <i>x</i> coordinate of the location of interest.
	 * @param  y
	 *           the <i>y</i> coordinate of the location of interest.
	 * @return {@code true} if the location whose coordinates are {@code x} and {@code y} is within the visual bounds of
	 *         any of the screens.
	 */

	public static boolean isWithinScreen(
		double	x,
		double	y)
	{
		return isWithinScreen(x, y, Insets.EMPTY);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the location with the specified coordinates is within the specified margins of the visual
	 * bounds of any of the screens.
	 *
	 * @param  x
	 *           the <i>x</i> coordinate of the location of interest.
	 * @param  y
	 *           the <i>y</i> coordinate of the location of interest.
	 * @param  margins
	 *           the margins that will be applied to the visual bounds of each screen.
	 * @return {@code true} if the location whose coordinates are {@code x} and {@code y} is within the visual bounds of
	 *         any of the screens after {@code margins} are applied to the visual bounds of each screen.
	 */

	public static boolean isWithinScreen(
		double	x,
		double	y,
		Insets	margins)
	{
		for (Screen screen : Screen.getScreens())
		{
			Rectangle2D bounds = screen.getVisualBounds();
			Rectangle2D adjustedBounds = new Rectangle2D(
				bounds.getMinX() + margins.getLeft(),
				bounds.getMinY() + margins.getTop(),
				bounds.getWidth() - margins.getLeft() - margins.getRight(),
				bounds.getHeight() - margins.getTop() - margins.getBottom()
			);
			if (adjustedBounds.contains(x, y))
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value, <i>dx</i>, whose absolute value is the smallest such that, when <i>dx</i> is added to each of
	 * the two specified <i>x</i> coordinates, the resulting coordinates are both within the horizontal extent of the
	 * screens.  If no value of <i>dx</i> can satisfy this condition for both <i>x</i> coordinates, <i>dx</i> is the
	 * value that satisfies the condition for the smaller <i>x</i> coordinate.
	 *
	 * @param  x1
	 *           the smaller <i>x</i> coordinate.
	 * @param  x2
	 *           the larger <i>x</i> coordinate.
	 * @return the value, <i>dx</i>, that satisfies at least one of the conditions set out above.
	 * @throws IllegalArgumentException
	 *           if {@code x1} is greater than {@code x2}.
	 */

	public static double deltaXWithinScreen(
		double	x1,
		double	x2)
	{
		// Validate arguments
		if (x1 > x2)
			throw new IllegalArgumentException("Coordinates out of order");

		// Initialise delta x
		double dx = 0.0;

		// Get screens
		List<Screen> screens = Screen.getScreens();

		// If there are screens, calculate delta x
		if (!screens.isEmpty())
		{
			double minX = Double.MAX_VALUE;
			double maxX = -Double.MAX_VALUE;
			for (Screen screen : screens)
			{
				Rectangle2D bounds = screen.getVisualBounds();
				double x = bounds.getMinX();
				if (minX > x)
					minX = x;
				x = bounds.getMaxX();
				if (maxX < x)
					maxX = x;
			}
			if (x1 < minX)
				dx = minX - x1;
			else if (x2 > maxX)
				dx = maxX - x2;
		}

		// Return delta x
		return dx;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value, <i>dy</i>, whose absolute value is the smallest such that, when <i>dy</i> is added to each of
	 * the two specified <i>y</i> coordinates, the resulting coordinates are both within the vertical extent of the
	 * screens.  If no value of <i>dy</i> can satisfy this condition for both <i>y</i> coordinates, <i>dy</i> is the
	 * value that satisfies the condition for the smaller <i>y</i> coordinate.
	 *
	 * @param  y1
	 *           the smaller <i>y</i> coordinate.
	 * @param  y2
	 *           the larger <i>y</i> coordinate.
	 * @return the value, <i>dy</i>, that satisfies at least one of the conditions set out above.
	 * @throws IllegalArgumentException
	 *           if {@code y1} is greater than {@code y2}.
	 */

	public static double deltaYWithinScreen(
		double	y1,
		double	y2)
	{
		// Validate arguments
		if (y1 > y2)
			throw new IllegalArgumentException("Coordinates out of order");

		// Initialise delta y
		double dy = 0.0;

		// Get screens
		List<Screen> screens = Screen.getScreens();

		// If there are screens, calculate delta y
		if (!screens.isEmpty())
		{
			double minY = Double.MAX_VALUE;
			double maxY = -Double.MAX_VALUE;
			for (Screen screen : screens)
			{
				Rectangle2D bounds = screen.getVisualBounds();
				double y = bounds.getMinY();
				if (minY > y)
					minY = y;
				y = bounds.getMaxY();
				if (maxY < y)
					maxY = y;
			}
			if (y1 < minY)
				dy = minY - y1;
			else if (y2 > maxY)
				dy = maxY - y2;
		}

		// Return delta y
		return dy;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of the top left corner of a rectangle with the specified width and height that would centre
	 * the rectangle in the primary screen.
	 *
	 * @param  width
	 *           the width of the rectangle of interest.
	 * @param  height
	 *           the height of the rectangle of interest.
	 * @return the location of the top left corner of a rectangle whose dimensions are {@code width} and {@code height}
	 *         that would centre the rectangle in the primary screen.
	 */

	public static Point2D centreInScreen(
		double	width,
		double	height)
	{
		return centreInScreen(width, height, Screen.getPrimary());
	}

	//------------------------------------------------------------------

	/**
	 * Returns the location of the top left corner of a rectangle with the specified width and height that would centre
	 * the rectangle in the specified screen.
	 *
	 * @param  width
	 *           the width of the rectangle of interest.
	 * @param  height
	 *           the height of the rectangle of interest.
	 * @param  screen
	 *           the screen in which the rectangle will be centred.
	 * @return the location of the top left corner of a rectangle whose dimensions are {@code width} and {@code height}
	 *         that would centre the rectangle in {@code screen}.
	 */

	public static Point2D centreInScreen(
		double	width,
		double	height,
		Screen	screen)
	{
		// Initialise location
		Point2D location = null;

		// Determine location within visual bounds of screen
		if (screen != null)
		{
			Rectangle2D screenBounds = screen.getVisualBounds();
			double x = screenBounds.getMinX() + 0.5 * (screenBounds.getWidth() - width);
			double y = screenBounds.getMinY() + 0.5 * (screenBounds.getHeight() - height);
			location = getLocationWithinBounds(x, y, width, height, screenBounds);
		}

		// Return location
		return (location == null) ? new Point2D(0.0, 0.0) : location;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the subtree of a scene graph starting from the specified root node.
	 *
	 * @param  root
	 *           the node at the root of the subtree whose string representation is desired.
	 * @param  indentIncrement
	 *           the number of spaces by which the string representation of each node will be indented relative to its
	 *           parent.
	 * @return a string representation of the subtree whose root is {@code root}.
	 */

	public static String treeToString(
		Node	root,
		int		indentIncrement)
	{
		StringBuilder buffer = new StringBuilder(256);
		int baseDepth = getDepth(root);
		visitDepthFirst(root, true, true, node ->
		{
			// Append linefeed
			if (!buffer.isEmpty())
				buffer.append('\n');

			// Append indent
			int indent = (getDepth(node) - baseDepth) * indentIncrement;
			for (int i = 0; i < indent; i++)
				buffer.append(' ');

			// Append string representation of node
			buffer.append(node);

			return true;
		});
		return buffer.toString();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
