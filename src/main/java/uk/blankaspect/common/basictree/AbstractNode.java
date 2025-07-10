/*====================================================================*\

AbstractNode.java

Class: abstract node.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.basictree;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Collections;
import java.util.List;

import uk.blankaspect.common.tree.ITreeNode;
import uk.blankaspect.common.tree.TreeUtils;

//----------------------------------------------------------------------


// CLASS: ABSTRACT NODE


/**
 * <p style="margin-bottom: 0.25em;">
 * This is the abstract base class of a node of a tree.  It is extended by concrete classes of the {@code
 * uk.blankaspect.common.basictree} package that implement
 * </p>
 * <ul style="margin-top: 0.25em;">
 *   <li>
 *     a node that represents a null value,
 *   </li>
 *   <li>
 *     nodes that contain instances of fundamental Java types ({@code boolean}, {@code int}, {@code long}, {@code
 *     double} and {@code String}), and
 *   </li>
 *   <li>
 *     nodes that contain other nodes (a list and a map).
 *   </li>
 * </ul>
 * <p>
 * This class implements {@link ITreeNode} to allow the methods of {@link TreeUtils} to be used on a tree of {@code
 * AbstractNode}s.
 * </p>
 */

public abstract class AbstractNode
	implements ITreeNode<AbstractNode>, Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The parent of this node. */
	private	AbstractNode	parent;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a node with the specified parent.
	 *
	 * @param parent
	 *          the parent of the node.
	 */

	protected AbstractNode(
		AbstractNode	parent)
	{
		// Initialise instance variables
		this.parent = parent;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Abstract methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the type of this node.
	 *
	 * @return the type of this node.
	 */

	public abstract NodeType getType();

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this node can contain other nodes.
	 *
	 * @return {@code true} if this node can contain other nodes; {@code false} otherwise.
	 */

	public abstract boolean isContainer();

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ITreeNode interface
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the parent of this node.
	 *
	 * @return the parent of this node, or {@code null} if this node has no parent.
	 */

	@Override
	public AbstractNode getParent()
	{
		return parent;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the children of this node.
	 *
	 * @return a list of the children of this node.
	 */

	@Override
	public List<AbstractNode> getChildren()
	{
		return Collections.emptyList();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a copy of this node that has no parent.
	 *
	 * @return a copy of this node that has no parent.
	 */

	@Override
	public AbstractNode clone()
	{
		try
		{
			// Create copy of this node
			AbstractNode copy = (AbstractNode)super.clone();

			// Clear parent
			copy.parent = null;

			// Return copy
			return copy;
		}
		catch (CloneNotSupportedException e)
		{
			throw new RuntimeException("Unexpected exception", e);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if this node is the root of the tree to which it belongs.  A node is deemed to be the root
	 * of its tree if it has no parent.
	 *
	 * @return {@code true} if this node is the root of the tree to which it belongs.
	 */

	public boolean isRoot()
	{
		return (parent == null);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the parent of this node to the specified node.
	 *
	 * @param parent
	 *          the node to which the parent of this node will be set, which should be {@code null} if this node has no
	 *          parent.
	 */

	public void setParent(
		AbstractNode	parent)
	{
		this.parent = parent;
	}

	//------------------------------------------------------------------

	/**
	 * If this node is an element of a {@linkplain ListNode list node}, returns the index of the element in the list
	 * node's list of elements.
	 *
	 * @return if this node is an element of a list node, the index of the element; otherwise, -1.
	 */

	public int getListIndex()
	{
		if (parent instanceof ListNode listNode)
		{
			List<AbstractNode> elements = listNode.getElements();
			for (int i = 0; i < elements.size(); i++)
			{
				if (elements.get(i) == this)
					return i;
			}
		}
		return -1;
	}

	//------------------------------------------------------------------

	/**
	 * If this node is the value of a key&ndash;value pair of a {@linkplain MapNode map node}, returns the key that is
	 * associated with the value.
	 *
	 * @return if this node is the value of a key&ndash;value pair of a map node, the key of the KV pair; otherwise,
	 *         {@code null}.
	 */

	public String getMapKey()
	{
		if (parent instanceof MapNode mapNode)
		{
			for (String name : mapNode.getKeys())
			{
				if (mapNode.get(name) == this)
					return name;
			}
		}
		return null;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of this node whose characters may optionally be escaped where necessary so that
	 * the returned string contains only printable characters from the US-ASCII character encoding.
	 *
	 * @param  printableAsciiOnly
	 *           if {@code true}, the characters of the string representation will be escaped where necessary so that
	 *           the returned string contains only printable characters from the US-ASCII character encoding (ie,
	 *           characters in the range U+0020 to U+007E inclusive).
	 * @return a string representation of this node.
	 */

	public String toString(
		boolean	printableAsciiOnly)
	{
		return toString();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
