/*====================================================================*\

NodeType.java

Class: node type.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.basictree;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.blankaspect.common.tree.ITreeNode;

//----------------------------------------------------------------------


// CLASS: NODE TYPE


/**
 * This class represents the type of a {@linkplain AbstractNode node}.  Node types form a hierarchy whose root is {@link
 * #ANY}, the node type of {@link AbstractNode}.  The hierarchy of node types mirrors the hierarchy of the classes with
 * which the node types are associated, so node types are likely to be useful only when it is inconvenient to work with
 * the classes of nodes.
 */

public final class NodeType
	implements ITreeNode<NodeType>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The root of the node-type hierarchy. */
	public static final	NodeType	ANY	= new NodeType(AbstractNode.class);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The parent type of this node type. */
	private	NodeType						parent;

	/** The child types of this node type. */
	private	List<NodeType>					children;

	/** The class of node with which this node type is associated. */
	private	Class<? extends AbstractNode>	nodeClass;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new node type that has the specified parent and is associated with the specified class of {@linkplain
	 * AbstractNode node}.
	 *
	 * @param  parent
	 *           the parent of the node type.
	 * @param  nodeClass
	 *           the class of node with which the node type will be associated.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code parent} is {@code null} or</li>
	 *             <li>{@code nodeClass} is {@code null} or</li>
	 *             <li>the node class of {@code parent} is not a superclass of {@code nodeClass} or</li>
	 *             <li>{@code parent} already has a child that is associated with {@code nodeClass}.</li>
	 *           </ul>
	 */

	public NodeType(
		NodeType						parent,
		Class<? extends AbstractNode>	nodeClass)
	{
		// Validate arguments
		if (parent == null)
			throw new IllegalArgumentException("Null parent");

		// Perform remaining initialisation
		init(parent, nodeClass);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new node type that is associated with the specified class of {@linkplain AbstractNode node}.  This
	 * constructor is used only to create the {@linkplain #ANY root node}.
	 *
	 * @param  nodeClass
	 *           the class of node with which the node type will be associated.
	 * @throws IllegalArgumentException
	 *           if {@code nodeClass} is {@code null}.
	 */

	private NodeType(
		Class<? extends AbstractNode>	nodeClass)
	{
		// Perform initialisation
		init(null, nodeClass);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ITreeNode interface
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the parent type of this node type.
	 *
	 * @return the parent type of this node type.
	 */

	@Override
	public NodeType getParent()
	{
		return parent;
	}

	//------------------------------------------------------------------

	/**
	 * Returns an unmodifiable list of the child types of this node type.
	 *
	 * @return an unmodifiable list of the child types of this node type.
	 */

	@Override
	public List<NodeType> getChildren()
	{
		return Collections.unmodifiableList(children);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a string representation of this node type, which is the name of the class of {@linkplain AbstractNode
	 * node} with which this node type is associated.
	 *
	 * @return a string representation of this node type.
	 */

	@Override
	public String toString()
	{
		return nodeClass.getName();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the class of {@linkplain AbstractNode node} with which this node type is associated.
	 *
	 * @return the class of node with which this node type is associated.
	 */

	public Class<? extends AbstractNode> getNodeClass()
	{
		return nodeClass;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this node type is identical to or a subtype (ie, descendant) of the specified node type.
	 *
	 * @param  nodeType
	 *           the node type with which this node type will be compared.
	 * @return {@code true} if this node type is identical to or a subtype of {@code nodeType}; {@code false} otherwise.
	 */

	public boolean is(
		NodeType	nodeType)
	{
		NodeType type = this;
		while (type != null)
		{
			if (type == nodeType)
				return true;
			type = type.parent;
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this node type is identical to or a subtype (ie, descendant) of any of the specified node
	 * types.
	 *
	 * @param  nodeTypes
	 *           the node types with which this node type will be compared.
	 * @return {@code true} if this node type is identical to or a subtype of any of {@code nodeTypes}; {@code false}
	 *         otherwise.
	 */

	public boolean isAnyOf(
		NodeType...	nodeTypes)
	{
		for (NodeType nodeType : nodeTypes)
		{
			if (is(nodeType))
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this node type is identical to or a subtype (ie, descendant) of any of the specified node
	 * types.
	 *
	 * @param  nodeTypes
	 *           the node types with which this node type will be compared.
	 * @return {@code true} if this node type is identical to or a subtype of any of {@code nodeTypes}; {@code false}
	 *         otherwise.
	 */

	public boolean isAnyOf(
		Iterable<NodeType>	nodeTypes)
	{
		for (NodeType nodeType : nodeTypes)
		{
			if (is(nodeType))
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified node type to the list of child types of this node type.
	 *
	 * @param  nodeType
	 *           the node type that will be added to the list of child types of this node type.
	 * @throws IllegalArgumentException
	 *           this node type already has a child that is associated with the node class of {@code nodeType}.
	 */

	private void addChild(
		NodeType	nodeType)
	{
		for (NodeType child : children)
		{
			if (nodeType.nodeClass == child.nodeClass)
			{
				throw new IllegalArgumentException("The node type for " + this + " already has a child type for "
														+ nodeType);
			}
		}
		children.add(nodeType);
	}

	//------------------------------------------------------------------

	/**
	 * Initialises this node type, associating it with the specified class of {@linkplain AbstractNode node}.
	 *
	 * @param  parent
	 *           the parent of this node type.
	 * @param  nodeClass
	 *           the class of node with which this node type will be associated.
	 * @throws IllegalArgumentException
	 *           if
	 *           <ul>
	 *             <li>{@code parent} is {@code null} or</li>
	 *             <li>{@code nodeClass} is {@code null} or</li>
	 *             <li>{@code parent} is not {@code null} and the node class of {@code parent} is not a superclass of
	 *                 {@code nodeClass} or</li>
	 *             <li>{@code parent} is not {@code null} and {@code parent} already has a child that is associated with
	 *                 {@code nodeClass}.</li>
	 *           </ul>
	 */

	private void init(
		NodeType						parent,
		Class<? extends AbstractNode>	nodeClass)
	{
		// Validate arguments
		if (nodeClass == null)
			throw new IllegalArgumentException("Null node class");

		// Initialise instance variables
		this.parent = parent;
		children = new ArrayList<>();
		this.nodeClass = nodeClass;

		// If this node type is not the root ...
		if (parent != null)
		{
			// Check that the node class of the parent is a superclass of the node class of this node type
			boolean found = false;
			Class<?> superclass = nodeClass.getSuperclass();
			while (superclass != null)
			{
				if (parent.nodeClass == superclass)
				{
					found = true;
					break;
				}
				superclass = superclass.getSuperclass();
			}
			if (!found)
			{
				throw new IllegalArgumentException("The node class of the parent type is not a superclass of "
														+ nodeClass);
			}

			// Add this node type to its parent
			parent.addChild(this);
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
