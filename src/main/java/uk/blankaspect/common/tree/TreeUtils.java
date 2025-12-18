/*====================================================================*\

TreeUtils.java

Class: tree-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.tree;

//----------------------------------------------------------------------


// IMPORTS


import java.util.List;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import uk.blankaspect.common.treetraversal.TreeTraversal;

//----------------------------------------------------------------------


// CLASS: TREE-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to trees whose nodes implement the {@link ITreeNode} interface.
 * <p>
 * Calls to methods of this class are forwarded to corresponding methods of the {@link TreeTraversal} class along with
 * functions that map a node to its parent or to a list of its children, as required.
 * </p>
 */

public class TreeUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private TreeUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the root node of the tree to which the specified {@linkplain ITreeNode node} belongs.  The root node is
	 * assumed to be the first ancestor of the specified node whose parent is {@code null}.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose root ancestor is desired.
	 * @return the root node of the tree to which {@code node} belongs.
	 */

	public static <T extends ITreeNode<T>> T getRoot(
		T	node)
	{
		return TreeTraversal.getRoot(node, ITreeNode::getParent);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the siblings of the specified {@linkplain ITreeNode node}.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose siblings are desired.
	 * @return a list of the siblings of {@code node}.
	 */

	public static <T extends ITreeNode<T>> List<T> getSiblings(
		T	node)
	{
		return TreeTraversal.getSiblings(node, ITreeNode::getParent, ITreeNode::getChildren);
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
	 * @return {@code true} if {@code node} is an ancestor of {@code target}.
	 */

	public static <T extends ITreeNode<T>> boolean isAncestor(
		T	node,
		T	target)
	{
		return isAncestor(node, target, false);
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
	 * @return {@code true} if
	 * 		   <ul>
	 *           <li>{@code node} is the ancestor of {@code target}, or</li>
	 *           <li>{@code testForIdentity} is {@code true} and {@code node} is identical to {@code target}.</li>
	 * 		   </ul>
	 */

	public static <T extends ITreeNode<T>> boolean isAncestor(
		T		node,
		T		target,
		boolean	testForIdentity)
	{
		return TreeTraversal.isAncestor(node, target, testForIdentity, ITreeNode::getParent);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the depth of the specified {@linkplain ITreeNode node} (ie, the number of levels below the root node of
	 * the tree to which the specified node belongs).
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose depth is desired.
	 * @return the depth of {@code node}.
	 * @see    #getDepth(ITreeNode, ITreeNode)
	 */

	public static <T extends ITreeNode<T>> int getDepth(
		T	node)
	{
		return TreeTraversal.getDepth(node, ITreeNode::getParent);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of levels of the specified {@linkplain ITreeNode node} below the specified root node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the root of the tree node whose depth is desired.
	 * @param  node
	 *           the node whose depth below {@code root} is desired.
	 * @return the depth of {@code node} below {@code root}, or -1 if {@code root} is not an ancestor of {@code node}.
	 * @see    #getDepth(ITreeNode)
	 */

	public static <T extends ITreeNode<T>> int getDepth(
		T	root,
		T	node)
	{
		return TreeTraversal.getDepth(root, node, ITreeNode::getParent);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the path to the specified {@linkplain ITreeNode node} from the root of the tree to which it belongs.  The
	 * path is a list of nodes that includes the root and the target node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose path is desired.
	 * @return a list of nodes that denotes the path to {@code node} from the root of the tree to which it belongs.
	 * @see    #getPath(ITreeNode, ITreeNode)
	 */

	public static <T extends ITreeNode<T>> List<T> getPath(
		T	node)
	{
		return TreeTraversal.getPath(node, ITreeNode::getParent);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the path from the specified root {@linkplain ITreeNode node} to the specified node, which is assumed to
	 * be a descendant of the specified root.  The path is a list of nodes that includes the root and the target node.
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
	 * @return a list of nodes that denotes the path from {@code root} to {@code node}, or, if {@code root} is not an
	 *         ancestor of {@code node}, the path to {@code node} from the root of the tree to which it belongs.
	 * @see    #getPath(ITreeNode)
	 */

	public static <T extends ITreeNode<T>> List<T> getPath(
		T	root,
		T	node)
	{
		return TreeTraversal.getPath(root, node, ITreeNode::getParent);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of indices to the specified {@linkplain ITreeNode node} from the root of the tree to which it
	 * belongs.  Each element of the list is the sum of the specified base index and the index of the corresponding node
	 * in its {@linkplain ITreeNode#getParent() parent}'s {@linkplain ITreeNode#getChildren() list of children}, or -1
	 * if the node is not a child of its parent.  The first element of the list is a child of the root node.  The list
	 * is empty if the target node is a root node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  node
	 *           the node whose indices are desired.
	 * @param  baseIndex
	 *           the base index that is added to each valid index in the list of indices.
	 * @return a list of nodes that denotes the path to {@code node} from the root of the tree to which it belongs.
	 * @see    #getIndices(ITreeNode, ITreeNode, boolean, int)
	 */

	public static <T extends ITreeNode<T>> List<Integer> getIndices(
		T	node,
		int	baseIndex)
	{
		return TreeTraversal.getIndices(node, baseIndex, ITreeNode::getParent, ITreeNode::getChildren);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of indices of the path from the specified root {@linkplain ITreeNode node} to the specified node,
	 * which is assumed to be a descendant of the specified root.  Each element of the list is the sum of the specified
	 * base index and the index of the corresponding node in its {@linkplain ITreeNode#getParent() parent}'s {@linkplain
	 * ITreeNode#getChildren() list of children}, or -1 if the node has no parent or is not a child of its parent.  The
	 * list of indices may optionally include the index of the root node.
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
	 * @return a list of nodes that denotes the path from {@code root} to {@code node}.
	 * @see    #getIndices(ITreeNode, int)
	 */

	public static <T extends ITreeNode<T>> List<Integer> getIndices(
		T		root,
		T		node,
		boolean	includeRoot,
		int		baseIndex)
	{
		return TreeTraversal.getIndices(root, node, includeRoot, baseIndex, ITreeNode::getParent,
										ITreeNode::getChildren);
	}

	//------------------------------------------------------------------

	/**
	 * Searches a tree of {@link ITreeNode}s, starting from the specified node, for a node whose path from the root
	 * matches the specified path descriptor.  The first element of the path descriptor is tested against the root node
	 * with the specified matcher: if there is no match, the search terminates immediately; otherwise, the root node
	 * becomes the current node, and the search proceeds by descending the tree, testing successive elements against the
	 * children of the current node until either no children match or the last element of the path descriptor is
	 * matched.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  <U>
	 *           the type of the elements of the path.
	 * @param  root
	 *           the node at which the search will start.
	 * @param  path
	 *           a path descriptor whose elements are tested sequentially against {@code root} and its descendants with
	 *           {@code matcher}.
	 * @param  matcher
	 *           the matcher that tests a node against an element of {@code path}.
	 * @return the node that matches {@code path}, or {@code null} if no matching node is found.
	 */

	public static <T extends ITreeNode<T>, U> T findNode(
		T					root,
		Iterable<U>			path,
		BiPredicate<T, U>	matcher)
	{
		return TreeTraversal.findNode(root, ITreeNode::getChildren, path, matcher);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a depth-first traversal of a tree of {@link ITreeNode}s, starting from the specified root node and
	 * applying the specified action to each node that is visited.  The root node may optionally be visited.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at the root of a tree of {@code ITreeNode}s, each of which will have {@code action} applied to
	 *           it.
	 * @param  preorder
	 *           if {@code true}, each node will be visited <i>before</i> its descendants; otherwise, each node will be
	 *           visited <i>after</i> its descendants.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will have {@code action} applied to it.
	 * @param  action
	 *           the action that will be applied to each node of the tree that is visited.  If the action returns {@code
	 *           false}, the traversal of the tree will be terminated.
	 * @return {@code false} if the traversal of the tree was terminated by {@code action}, possibly before all nodes
	 *         were visited; {@code true} otherwise.
	 */

	public static <T extends ITreeNode<T>> boolean visitDepthFirst(
		T						root,
		boolean					preorder,
		boolean					includeRoot,
		Function<T, Boolean>	action)
	{
		return TreeTraversal.visitDepthFirst(root, preorder, includeRoot, ITreeNode::getChildren, action);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a breadth-first traversal of a tree of {@link ITreeNode}s, starting from the specified root node and
	 * applying the specified action to each node that is visited.  The root node may optionally be visited.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at the root of a tree of {@code ITreeNode}s, each of which will have {@code action} applied to
	 *           it.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will have {@code action} applied to it.
	 * @param  action
	 *           the action that will be applied to each node of the tree that is visited.  If the action returns {@code
	 *           false}, the traversal of the tree will be terminated.
	 * @return {@code false} if the traversal of the tree was terminated by {@code action}, possibly before all nodes
	 *         were visited; {@code true} otherwise.
	 */

	public static <T extends ITreeNode<T>> boolean visitBreadthFirst(
		T						root,
		boolean					includeRoot,
		Function<T, Boolean>	action)
	{
		return TreeTraversal.visitBreadthFirst(root, includeRoot, ITreeNode::getChildren, action);
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a tree of {@link ITreeNode}s from the specified node to the root node, applying the specified action to
	 * each node that is visited, including the root.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  startNode
	 *           the node from which the ascent will start.
	 * @param  action
	 *           the action that will be applied to each node of the tree that is visited.  If the action returns {@code
	 *           false}, the ascent of the tree will be terminated.
	 * @return {@code false} if the ascent of the tree was terminated by {@code action}, possibly before all nodes were
	 *         visited; {@code true} otherwise.
	 */

	public static <T extends ITreeNode<T>> boolean visitAscending(
		T						startNode,
		Function<T, Boolean>	action)
	{
		return visitAscending(startNode, null, false, action);
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a tree of {@link ITreeNode}s from the specified node to the specified end node, applying the specified
	 * action to each node that is visited.  The end node may optionally be visited.
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
	 * @param  action
	 *           the action that will be applied to each node of the tree that is visited.  If the action returns {@code
	 *           false}, the ascent of the tree will be terminated.
	 * @return {@code false} if the ascent of the tree was terminated by {@code action}, possibly before all nodes were
	 *         visited; {@code true} otherwise.
	 */

	public static <T extends ITreeNode<T>> boolean visitAscending(
		T						startNode,
		T						endNode,
		boolean					includeEnd,
		Function<T, Boolean>	action)
	{
		return TreeTraversal.visitAscending(startNode, endNode, includeEnd, ITreeNode::getParent, action);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if and only if the specified {@link ITreeNode} or one of its ancestors satisfies the
	 * specified test.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  startNode
	 *           the node from which the ascent will start.
	 * @param  test
	 *           the test that will be applied to {@code startNode} and its ancestors.
	 * @return {@code true} if and only if {@code startNode} or one of its ancestors satisfies {@code test}.
	 */

	public static <T extends ITreeNode<T>> boolean testAscending(
		T				startNode,
		Predicate<T>	test)
	{
		return (searchAscending(startNode, test) != null);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a depth-first search of a tree of {@link ITreeNode}s, starting from the specified root node and applying
	 * the specified test to each node that is visited until a node is found that satisfies the test.  If such a node is
	 * found, the search terminates immediately and the matching node is returned.  The root node may optionally be
	 * included in the search.
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
	 * @param  test
	 *           the test that will be applied to each node of the tree that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 */

	public static <T extends ITreeNode<T>> T searchDepthFirst(
		T				root,
		boolean			preorder,
		boolean			includeRoot,
		Predicate<T>	test)
	{
		return TreeTraversal.searchDepthFirst(root, preorder, includeRoot, ITreeNode::getChildren, test);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a breadth-first search of a tree of {@link ITreeNode}s, starting from the specified root node and
	 * applying the specified test to each node that is visited until a node is found that satisfies the test.  If such
	 * a node is found, the search terminates immediately and the matching node is returned.  The root node may
	 * optionally be included in the search.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at which the search will start.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will be included in the search.
	 * @param  test
	 *           the test that will be applied to each node of the tree that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 */

	public static <T extends ITreeNode<T>> T searchBreadthFirst(
		T				root,
		boolean			includeRoot,
		Predicate<T>	test)
	{
		return TreeTraversal.searchBreadthFirst(root, includeRoot, ITreeNode::getChildren, test);
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a tree of {@link ITreeNode}s from the specified start node to the root node, applying the specified test
	 * to each node that is visited until a node is found that satisfies the test.  If such a node is found, the search
	 * terminates immediately and the matching node is returned.  The root is included in the search.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  startNode
	 *           the node from which the search will start.
	 * @param  test
	 *           the test that will be applied to each node of the tree that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 */

	public static <T extends ITreeNode<T>> T searchAscending(
		T				startNode,
		Predicate<T>	test)
	{
		return searchAscending(startNode, null, false, test);
	}

	//------------------------------------------------------------------

	/**
	 * Ascends a tree of {@link ITreeNode}s from the specified start node to the specified end node, applying the
	 * specified test to each node that is visited until a node is found that satisfies the test.  If such a node is
	 * found, the search terminates immediately and the matching node is returned.  The end node may optionally be
	 * included in the search.
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
	 * @param  test
	 *           the test that will be applied to each node of the tree that is visited until the test succeeds.
	 * @return the first node that returns {@code true} when {@code test} is applied to it, or {@code null} if no node
	 *         satisfies the test.
	 */

	public static <T extends ITreeNode<T>> T searchAscending(
		T				startNode,
		T				endNode,
		boolean			includeEnd,
		Predicate<T>	test)
	{
		return TreeTraversal.searchAscending(startNode, endNode, includeEnd, ITreeNode::getParent, test);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the tree of {@link ITreeNode}s whose root is the specified node.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at the root of the tree.
	 * @param  indentIncrement
	 *           the number of spaces by which the indent of a line of text will be incremented for each level of the
	 *           tree below {@code root}.
	 * @param  converter
	 *           the function that will convert each node to its string representation.
	 * @return a string representation of the tree whose root is {@code root}.
	 */

	public static <T extends ITreeNode<T>> String treeToString(
		T					root,
		int					indentIncrement,
		Function<T, String>	converter)
	{
		return treeToString(root, indentIncrement, true, converter);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the tree of {@link ITreeNode}s whose root is the specified node.  The
	 * inclusion of the root node in the string representation is optional.
	 *
	 * @param  <T>
	 *           the type of the nodes of the tree.
	 * @param  root
	 *           the node at the root of the tree.
	 * @param  indentIncrement
	 *           the number of spaces by which the indent of a line of text will be incremented for each level of the
	 *           tree below {@code root}.
	 * @param  includeRoot
	 *           if {@code true}, {@code root} will be included in the string representation.
	 * @param  converter
	 *           the function that will convert each node to its string representation.
	 * @return a string representation of the tree whose root is {@code root}.
	 */

	public static <T extends ITreeNode<T>> String treeToString(
		T					root,
		int					indentIncrement,
		boolean				includeRoot,
		Function<T, String>	converter)
	{
		return TreeTraversal.treeToString(root, indentIncrement, includeRoot, ITreeNode::getParent,
										  ITreeNode::getChildren, converter);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
