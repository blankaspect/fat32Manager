/*====================================================================*\

TreeTraversal.java

Class: tree-traversal utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.treetraversal;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

//----------------------------------------------------------------------


// CLASS: TREE-TRAVERSAL UTILITY METHODS


/**
 * This class contains utility methods that relate to the traversal of a tree.
 */

public class TreeTraversal
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private TreeTraversal()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the root node of the tree to which the specified node belongs.  The root node is assumed to be the first
	 * ancestor of the specified node whose parent is {@code null}.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose root ancestor is desired.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @return the root node of the tree to which {@code node} belongs.
	 */

	public static <T> T getRoot(
		T				node,
		Function<T, T>	parentMapper)
	{
		while (true)
		{
			T parent = parentMapper.apply(node);
			if (parent == null)
				return node;
			node = parent;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the siblings of the specified node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose siblings are desired.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @param  childListMapper
	 *           the function that returns a list of the children of a given node.
	 * @return a list of the siblings of {@code node}.
	 */

	public static <T> List<T> getSiblings(
		T						node,
		Function<T, T>			parentMapper,
		Function<T, List<T>>	childListMapper)
	{
		T parent = parentMapper.apply(node);
		return (parent == null) ? Collections.emptyList()
								: childListMapper.apply(parent).stream()
										.filter(child -> (child != node))
										.map(child -> child)
										.toList();
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified node is an ancestor of the specified target node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node that is a potential ancestor of {@code target}.
	 * @param  target
	 *           the node that is a potential descendant of {@code node}.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @return {@code true} if {@code node} is an ancestor of {@code target}.
	 */

	public static <T> boolean isAncestor(
		T				node,
		T				target,
		Function<T, T>	parentMapper)
	{
		return isAncestor(node, target, false, parentMapper);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified node is an ancestor of the specified target node or, optionally, if the
	 * node is identical to the target.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node that is a potential ancestor of {@code target} or (if {@code testForIdentity} is {@code true})
	 *           is potentially identical to {@code target}.
	 * @param  target
	 *           the node that is a potential descendant of {@code node} or (if {@code testForIdentity} is {@code true})
	 *           is potentially identical to {@code node}.
	 * @param  testForIdentity
	 *           if {@code true}, {@code node} and {@code target} will be tested for identity as well as for an
	 *           ancestor&ndash;descendant relationship.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @return {@code true} if
	 * 		   <ul>
	 *           <li>{@code node} is the ancestor of {@code target}, or</li>
	 *           <li>{@code testForIdentity} is {@code true} and {@code node} is identical to {@code target}.</li>
	 * 		   </ul>
	 */

	public static <T> boolean isAncestor(
		T				node,
		T				target,
		boolean			testForIdentity,
		Function<T, T>	parentMapper)
	{
		T target0 = testForIdentity ? target : parentMapper.apply(target);
		while (target0 != null)
		{
			if (node == target0)
				return true;
			target0 = parentMapper.apply(target0);
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the depth of the specified node (ie, the number of levels below the root node of the tree to which the
	 * specified node belongs).
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose depth is desired.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @return the depth of {@code node}.
	 * @see    #getDepth(Object, Object, Function)
	 */

	public static <T> int getDepth(
		T				node,
		Function<T, T>	parentMapper)
	{
		int depth = 0;
		while (true)
		{
			// Get parent of node
			T parent = parentMapper.apply(node);

			// If no parent, stop
			if (parent == null)
				break;

			// Ascend tree
			node = parent;
			++depth;
		}
		return depth;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of levels of the specified node below the specified root node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the root of the tree node whose depth is desired.
	 * @param  node
	 *           the node whose depth below {@code root} is desired.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @return the depth of {@code node} below {@code root}, or -1 if {@code root} is not an ancestor of {@code node}.
	 * @see    #getDepth(Object, Function)
	 */

	public static <T> int getDepth(
		T				root,
		T				node,
		Function<T, T>	parentMapper)
	{
		int depth = 0;
		while (depth >= 0)
		{
			// Test for root
			if (node == root)
				break;

			// Get parent of node
			T parent = parentMapper.apply(node);

			// If no parent, invalidate depth ...
			if (parent == null)
				depth = -1;

			// ... otherwise, ascend tree
			else
			{
				node = parent;
				++depth;
			}
		}
		return depth;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the path to the specified node from the root of the tree to which it belongs.  The path is a list of
	 * nodes that includes the root and the target node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose path is desired.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @return a list of nodes that denotes the path to {@code node} from the root of the tree to which it belongs.
	 * @see    #getPath(Object, Object, Function)
	 */

	public static <T> List<T> getPath(
		T				node,
		Function<T, T>	parentMapper)
	{
		// Initialise list of nodes
		LinkedList<T> path = new LinkedList<>();

		// Add target node and its ancestors to list
		visitAscending(node, parentMapper, node0 ->
		{
			path.addFirst(node0);
			return true;
		});

		// Return list of nodes
		return path;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the path from the specified root node to the specified node, which is assumed to be a descendant of the
	 * specified root.  The path is a list of nodes that includes the root and the target node.
	 * <p>
	 * If the specified root node is not an ancestor of the target node, the path will be a list of nodes from the root
	 * of the tree to which the target node belongs, which will be the first element of the list.
	 * </p>
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at which the path will start, unless it is not an ancestor of {@code node}.
	 * @param  node
	 *           the node whose path is desired.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @return a list of nodes that denotes the path from {@code root} to {@code node}, or, if {@code root} is not an
	 *         ancestor of {@code node}, the path to {@code node} from the root of the tree to which it belongs.
	 * @see    #getPath(Object, Function)
	 */

	public static <T> List<T> getPath(
		T				root,
		T				node,
		Function<T, T>	parentMapper)
	{
		// Initialise list of nodes
		LinkedList<T> path = new LinkedList<>();

		// Add target node and its ancestors to list
		visitAscending(node, root, true, parentMapper, node0 ->
		{
			path.addFirst(node0);
			return true;
		});

		// Return list of nodes
		return path;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of indices to the specified node from the root of the tree to which it belongs.  Each element of
	 * the list is the sum of the specified base index and the index of the corresponding node in its parent's list of
	 * children, or -1 if the node is not a child of its parent.  The first element of the list is a child of the root
	 * node.  The list is empty if the target node is a root node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose indices are desired.
	 * @param  baseIndex
	 *           the base index that is added to each valid index in the list of indices.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @param  childListMapper
	 *           the function that returns a list of the children of a given node.
	 * @return a list of nodes that denotes the path to {@code node} from the root of the tree to which it belongs.
	 * @see    #getIndices(Object, Object, boolean, int, Function, Function)
	 */

	public static <T> List<Integer> getIndices(
		T						node,
		int						baseIndex,
		Function<T, T>			parentMapper,
		Function<T, List<T>>	childListMapper)
	{
		// Initialise list of indices
		LinkedList<Integer> indices = new LinkedList<>();

		// Add indices of target node and its ancestors to list
		visitAscending(node, null, false, parentMapper, node0 ->
		{
			T parent = parentMapper.apply(node0);
			if (parent != null)
			{
				int index = childListMapper.apply(parent).indexOf(node0);
				indices.addFirst((index < 0) ? -1 : baseIndex + index);
			}
			return true;
		});

		// Return list of indices
		return indices;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of indices of the path from the specified root node to the specified node, which is assumed to be
	 * a descendant of the specified root.  Each element of the list is the sum of the specified base index and the
	 * index of the corresponding node in its parent's list of children, or -1 if the node has no parent or is not a
	 * child of its parent.  The list of indices may optionally include the index of the root node.
	 * <p>
	 * If the specified root node is not an ancestor of the target node, the list of indices will start at
	 * </p>
	 * <ul>
	 *   <li>
	 *     the root of the tree to which the target node belongs, if {@code includeRoot} is {@code true}, or
	 *   </li>
	 *   <li>
	 *     the ancestor of the target node that is a child of the root of the tree to which the target node belongs, if
	 *     {@code includeRoot} is {@code false}.
	 *   </li>
	 * </ul>
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at which the indices will start, unless it is not an ancestor of {@code node}.
	 * @param  node
	 *           the node whose indices are desired.
	 * @param  includeRoot
	 *           the index of {@code root} will be included in the list of indices.
	 * @param  baseIndex
	 *           the base index that is added to each valid index in the list of indices.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @param  childListMapper
	 *           the function that returns a list of the children of a given node.
	 * @return a list of nodes that denotes the path from {@code root} to {@code node}.
	 * @see    #getIndices(Object, int, Function, Function)
	 */

	public static <T> List<Integer> getIndices(
		T						root,
		T						node,
		boolean					includeRoot,
		int						baseIndex,
		Function<T, T>			parentMapper,
		Function<T, List<T>>	childListMapper)
	{
		// Initialise list of indices
		LinkedList<Integer> indices = new LinkedList<>();

		// Add indices of target node and its ancestors to list
		visitAscending(node, root, includeRoot, parentMapper, node0 ->
		{
			T parent = parentMapper.apply(node0);
			int index = (parent == null) ? -1 : childListMapper.apply(parent).indexOf(node0);
			indices.addFirst((index < 0) ? -1 : baseIndex + index);
			return true;
		});

		// Return list of indices
		return indices;
	}

	//------------------------------------------------------------------

	/**
	 * Searches a tree, starting from the specified node, for a node whose path from the root matches the specified path
	 * descriptor.  The first element of the path descriptor is tested against the root node with the specified matcher:
	 * if there is no match, the search terminates immediately; otherwise, the root node becomes the current node, and
	 * the search proceeds by descending the tree, testing successive elements against the children of the current node
	 * until either no children match or the last element of the path descriptor is matched.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  <U>
	 *           the type of the elements of the path.
	 * @param  root
	 *           the node at which the search will start.
	 * @param  childListMapper
	 *           the function that returns a list of the children of a given node.
	 * @param  path
	 *           a path descriptor whose elements are tested sequentially against {@code root} and its descendants with
	 *           {@code matcher}.
	 * @param  matcher
	 *           the matcher that tests a node against an element of {@code path}.
	 * @return the node that matches {@code path}, or {@code null} if no matching node is found.
	 */

	public static <T, U> T findNode(
		T						root,
		Function<T, List<T>>	childListMapper,
		Iterable<U>				path,
		BiPredicate<T, U>		matcher)
	{
		T result = null;
		Iterator<U> it = path.iterator();
		if (it.hasNext() && matcher.test(root, it.next()))
		{
			result = root;
			while (it.hasNext())
			{
				U target = it.next();
				result = childListMapper.apply(result).stream()
						.filter(child -> matcher.test(child, target))
						.findFirst()
						.orElse(null);
				if (result == null)
					break;
			}
		}
		return result;
	}

	//------------------------------------------------------------------

	/**
	 * Performs a depth-first traversal of a tree, starting from the specified root node and applying the specified
	 * action to each node that is visited.  The root node may optionally be visited.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at the root of a tree, each of whose nodes will have {@code action} applied to it.
	 * @param  preorder
	 *           if {@code true}, each node will be visited <i>before</i> its descendants; otherwise, each node will be
	 *           visited <i>after</i> its descendants.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will have {@code action} applied to it.
	 * @param  childListMapper
	 *           the function that returns a list of the children of a given node.
	 * @param  action
	 *           the action that will be applied to each node of the tree that is visited.  If the action returns {@code
	 *           false}, the traversal of the tree will be terminated.
	 * @return {@code false} if the traversal of the tree was terminated by {@code action}, possibly before all nodes
	 *         were visited; {@code true} otherwise.
	 */

	public static <T> boolean visitDepthFirst(
		T						root,
		boolean					preorder,
		boolean					includeRoot,
		Function<T, List<T>>	childListMapper,
		Function<T, Boolean>	action)
	{
		// Initialise stack
		Deque<T> stack = new ArrayDeque<>();

		// Push root onto stack
		stack.addFirst(root);

		// Case: preorder
		if (preorder)
		{
			// While there are nodes on stack, visit them
			while (!stack.isEmpty())
			{
				// Pop node from stack
				T node = stack.removeFirst();

				// Visit node
				if (((node != root) || includeRoot) && !action.apply(node))
					return false;

				// Push children onto stack
				List<T> children = childListMapper.apply(node);
				for (int i = children.size() - 1; i >= 0; i--)
					stack.addFirst(children.get(i));
			}
		}

		// Case: postorder
		else
		{
			// Initialise list of pending nodes
			List<T> pendingNodes = new ArrayList<>();

			// While there are nodes on stack, visit them
			while (!stack.isEmpty())
			{
				// Pop node from stack
				T node = stack.removeFirst();

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
				if (!pending)
				{
					List<T> children = childListMapper.apply(node);
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
	 * Performs a breadth-first traversal of a tree, starting from the specified root node and applying the specified
	 * action to each node that is visited.  The root node may optionally be visited.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at the root of a tree, each of whose nodes will have {@code action} applied to it.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will have {@code action} applied to it.
	 * @param  childListMapper
	 *           the function that returns a list of the children of a given node.
	 * @param  action
	 *           the action that will be applied to each node of the tree that is visited.  If the action returns {@code
	 *           false}, the traversal of the tree will be terminated.
	 * @return {@code false} if the traversal of the tree was terminated by {@code action}, possibly before all nodes
	 *         were visited; {@code true} otherwise.
	 */

	public static <T> boolean visitBreadthFirst(
		T						root,
		boolean					includeRoot,
		Function<T, List<T>>	childListMapper,
		Function<T, Boolean>	action)
	{
		// Initialise queue
		Deque<T> queue = new ArrayDeque<>(32);

		// Add root to queue
		queue.addLast(root);

		// While there are nodes to visit ...
		while (!queue.isEmpty())
		{
			// Get next node from queue
			T node = queue.removeFirst();

			// Visit node
			if (((node != root) || includeRoot) && !action.apply(node))
				return false;

			// Add children of node to queue
			for (T child : childListMapper.apply(node))
				queue.addLast(child);
		}

		// Indicate traversal completed
		return true;
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a tree from the specified node to the root node, applying the specified action to each node that is
	 * visited, including the root.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  startNode
	 *           the node from which the ascent will start.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @param  action
	 *           the action that will be applied to each node of the tree that is visited.  If the action returns {@code
	 *           false}, the ascent of the tree will be terminated.
	 * @return {@code false} if the ascent of the tree was terminated by {@code action}, possibly before all nodes were
	 *         visited; {@code true} otherwise.
	 */

	public static <T> boolean visitAscending(
		T						startNode,
		Function<T, T>			parentMapper,
		Function<T, Boolean>	action)
	{
		return visitAscending(startNode, null, false, parentMapper, action);
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a tree from the specified node to the specified end node, applying the specified action to each node that
	 * is visited.  The end node may optionally be visited.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  startNode
	 *           the node from which the ascent will start.
	 * @param  endNode
	 *           the node at which the ascent will end.  {@code endNode} is visited if {@code includeEnd} is {@code
	 *           true}.  If {@code endNode} is {@code null}, {@code startNode} and all its ancestors will be visited.
	 * @param  includeEnd
	 *           if {@code true}, {@code endNode} will have {@code action} applied to it.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @param  action
	 *           the action that will be applied to each node of the tree that is visited.  If the action returns {@code
	 *           false}, the ascent of the tree will be terminated.
	 * @return {@code false} if the ascent of the tree was terminated by {@code action}, possibly before all nodes were
	 *         visited; {@code true} otherwise.
	 */

	public static <T> boolean visitAscending(
		T						startNode,
		T						endNode,
		boolean					includeEnd,
		Function<T, T>			parentMapper,
		Function<T, Boolean>	action)
	{
		// Initialise current node
		T node = startNode;

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
			node = parentMapper.apply(node);
		}

		// Indicate ascent completed
		return true;
	}

	//------------------------------------------------------------------

	/**
	 * Performs a depth-first search of a tree, starting from the specified root node and applying the specified test
	 * to each node that is visited until a node is found that satisfies the test.  If such a node is found, the search
	 * terminates immediately and the matching node is returned.  The root node may optionally be included in the
	 * search.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at which the search will start.
	 * @param  preorder
	 *           if {@code true}, each node will be visited <i>before</i> its descendants; otherwise, each node will be
	 *           visited <i>after</i> its descendants.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will be included in the search.
	 * @param  childListMapper
	 *           the function that returns a list of the children of a given node.
	 * @param  test
	 *           the test that will be applied to each node of the tree that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 */

	public static <T> T searchDepthFirst(
		T						root,
		boolean					preorder,
		boolean					includeRoot,
		Function<T, List<T>>	childListMapper,
		Predicate<T>			test)
	{
		// Initialise result
		T result = null;

		// Initialise stack
		Deque<T> stack = new ArrayDeque<>();

		// Push root onto stack
		stack.addFirst(root);

		// Case: preorder
		if (preorder)
		{
			// While there are nodes on stack, visit them
			while (!stack.isEmpty())
			{
				// Pop node from stack
				T node = stack.removeFirst();

				// Test node
				if (((node != root) || includeRoot) && test.test(node))
				{
					result = node;
					break;
				}

				// Push children onto stack
				List<T> children = childListMapper.apply(node);
				for (int i = children.size() - 1; i >= 0; i--)
					stack.addFirst(children.get(i));
			}
		}

		// Case: postorder
		else
		{
			// Initialise list of pending nodes
			List<T> pendingNodes = new ArrayList<>();

			// While there are nodes on stack, visit them
			while (!stack.isEmpty())
			{
				// Pop node from stack
				T node = stack.removeFirst();

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
				if (!pending)
				{
					List<T> children = childListMapper.apply(node);
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
	 * Performs a breadth-first search of a tree, starting from the specified root node and applying the specified test
	 * to each node that is visited until a node is found that satisfies the test.  If such a node is found, the search
	 * terminates immediately and the matching node is returned.  The root node may optionally be included in the
	 * search.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at which the search will start.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will be included in the search.
	 * @param  childListMapper
	 *           the function that returns a list of the children of a given node.
	 * @param  test
	 *           the test that will be applied to each node of the tree that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 */

	public static <T> T searchBreadthFirst(
		T						root,
		boolean					includeRoot,
		Function<T, List<T>>	childListMapper,
		Predicate<T>			test)
	{
		// Initialise result
		T result = null;

		// Initialise queue
		Deque<T> queue = new ArrayDeque<>(32);

		// Add root to queue
		queue.addLast(root);

		// While there are nodes to visit ...
		while (!queue.isEmpty())
		{
			// Get next node from queue
			T node = queue.removeFirst();

			// Test node
			if (((node != root) || includeRoot) && test.test(node))
			{
				result = node;
				break;
			}

			// Add children of node to queue
			for (T child : childListMapper.apply(node))
				queue.addLast(child);
		}

		// Return result
		return result;
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a tree from the specified start node to the root node, applying the specified test to each node that is
	 * visited until a node is found that satisfies the test.  If such a node is found, the search terminates
	 * immediately and the matching node is returned.  The root is included in the search.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  startNode
	 *           the node from which the search will start.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @param  test
	 *           the test that will be applied to each node of the tree that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 */

	public static <T> T searchAscending(
		T				startNode,
		Function<T, T>	parentMapper,
		Predicate<T>	test)
	{
		return searchAscending(startNode, null, false, parentMapper, test);
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a tree from the specified start node to the specified end node, applying the specified test to each node
	 * that is visited until a node is found that satisfies the test.  If such a node is found, the search terminates
	 * immediately and the matching node is returned.  The end node may optionally be included in the search.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  startNode
	 *           the node from which the search will start.
	 * @param  endNode
	 *           the node at which the search will end.  {@code endNode} is included in the search if {@code includeEnd}
	 *           is {@code true}.  If {@code endNode} is {@code null}, {@code startNode} and all its ancestors will be
	 *           included in the search.
	 * @param  includeEnd
	 *           if {@code true}, {@code endNode} will have {@code test} applied to it.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @param  test
	 *           the test that will be applied to each node of the tree that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 */

	public static <T> T searchAscending(
		T				startNode,
		T				endNode,
		boolean			includeEnd,
		Function<T, T>	parentMapper,
		Predicate<T>	test)
	{
		// Initialise result
		T result = null;

		// Perform search
		T node = startNode;
		while (node != null)
		{
			// Test node
			if (((node != endNode) || includeEnd) && test.test(node))
			{
				result = node;
				break;
			}

			// Test for end of ascent
			if (node == endNode)
				break;

			// Ascend the tree
			node = parentMapper.apply(node);
		}

		// Return result
		return result;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the tree whose root is the specified node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at the root of the tree.
	 * @param  indentIncrement
	 *           the number of spaces by which the indent of a line of text will be incremented for each level of the
	 *           tree below {@code root}.
	 * @param  parentMapper
	 *           the function that returns the parent of a given node.
	 * @param  childListMapper
	 *           the function that returns a list of the children of a given node.
	 * @param  converter
	 *           the function that will convert each node to its string representation.
	 * @return a string representation of the tree whose root is {@code root}.
	 */

	public static <T> String treeToString(
		T						root,
		int						indentIncrement,
		Function<T, T>			parentMapper,
		Function<T, List<T>>	childListMapper,
		Function<T, String>		converter)
	{
		StringBuilder buffer = new StringBuilder(256);
		visitDepthFirst(root, true, true, childListMapper, node ->
		{
			// Get depth of node below root
			int depth = getDepth(root, node, parentMapper);

			// Append linefeed if node is not root
			if (depth > 0)
				buffer.append('\n');

			// Append indent
			int indent = depth * indentIncrement;
			for (int i = 0; i < indent; i++)
				buffer.append(' ');

			// Append string representation of node
			buffer.append(converter.apply(node));

			// Continue to traverse tree
			return true;
		});
		return buffer.toString();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
