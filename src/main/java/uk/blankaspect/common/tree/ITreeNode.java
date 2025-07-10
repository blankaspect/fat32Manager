/*====================================================================*\

ITreeNode.java

Interface: tree node.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.tree;

//----------------------------------------------------------------------


// IMPORTS


import java.util.List;

//----------------------------------------------------------------------


// INTERFACE: TREE NODE


/**
 * This interface defines the methods that must be implemented by a node of a tree that can be used with the methods of
 * {@link TreeUtils}.
 *
 * @param <T>
 *          the type of the node: a subtype of {@code ITreeNode}.
 */

public interface ITreeNode<T extends ITreeNode<T>>
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the parent of this node.
	 *
	 * @return the parent of this node.
	 */

	T getParent();

	//------------------------------------------------------------------

	/**
	 * Returns a list of the children of this node.
	 *
	 * @return a list of the children of this node.
	 */

	List<T> getChildren();

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this node is a leaf node (ie, it has no children).
	 *
	 * @return {@code true} if this node is a leaf node.
	 */

	default boolean isLeaf()
	{
		return getChildren().isEmpty();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
