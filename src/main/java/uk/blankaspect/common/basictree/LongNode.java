/*====================================================================*\

LongNode.java

Class: long node.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.basictree;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: LONG NODE


/**
 * This class implements a {@linkplain AbstractNode node} that contains a long integer.
 */

public class LongNode
	extends AbstractNode
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The type of a 'long' node. */
	public static final	NodeType	TYPE	= new NodeType(NodeType.ANY, LongNode.class);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The value of this 'long' node. */
	private	long	value;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a 'long' node that has no parent and has the specified value.
	 *
	 * @param value
	 *          the value of the 'long' node.
	 */

	public LongNode(
		long	value)
	{
		// Call alternative constructor
		this(null, value);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a 'long' node that has the specified parent and value.
	 *
	 * @param parent
	 *          the parent of the 'long' node.
	 * @param value
	 *          the value of the 'long' node.
	 */

	public LongNode(
		AbstractNode	parent,
		long			value)
	{
		// Call superclass constructor
		super(parent);

		// Initialise instance variables
		this.value = value;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a list of 'long' nodes for the specified values, preserving the order of the elements, and returns the
	 * list, which may be used to construct a {@linkplain ListNode list node}.
	 *
	 * @param  values
	 *           the values for which 'long' nodes will be created.
	 * @return a list of 'long' nodes whose underlying values are {@code values}.
	 */

	public static List<LongNode> valuesToNodes(
		long...	values)
	{
		List<LongNode> outValues = new ArrayList<>();
		for (long value : values)
			outValues.add(new LongNode(value));
		return outValues;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a list of 'long' nodes for the specified values, preserving the order of the elements, and returns the
	 * list, which may be used to construct a {@linkplain ListNode list node}.
	 *
	 * @param  values
	 *           the values for which 'long' nodes will be created.
	 * @return a list of 'long' nodes whose underlying values are {@code values}.
	 */

	public static List<LongNode> valuesToNodes(
		Iterable<Long>	values)
	{
		List<LongNode> outValues = new ArrayList<>();
		for (Long value : values)
			outValues.add(new LongNode(value));
		return outValues;
	}

	//------------------------------------------------------------------

	/**
	 * Returns an array whose elements are the values of the specified {@linkplain LongNode 'long' nodes}, with the
	 * order of the elements preserved.
	 *
	 * @param  nodes
	 *           the 'long' nodes whose values will be extracted into an array.
	 * @return an array whose elements are the values of {@code nodes}.
	 */

	public static long[] nodesToArray(
		Collection<? extends LongNode>	nodes)
	{
		int numNodes = nodes.size();
		long[] values = new long[numNodes];
		int index = 0;
		for (LongNode node : nodes)
			values[index++] = node.getValue();
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list whose elements are the values of the specified {@linkplain LongNode 'long' nodes}, with the order
	 * of the elements preserved.
	 *
	 * @param  nodes
	 *           the 'long' nodes whose values will be extracted into a list.
	 * @return a list whose elements are the values of {@code nodes}.
	 */

	public static List<Long> nodesToList(
		Iterable<? extends LongNode>	nodes)
	{
		List<Long> values = new ArrayList<>();
		for (LongNode node : nodes)
			values.add(node.getValue());
		return values;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * @return {@link #TYPE}.
	 */

	@Override
	public NodeType getType()
	{
		return TYPE;
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 * For a 'long' node, this method always returns {@code false}.
	 *
	 * @return {@code false}.
	 */

	@Override
	public boolean isContainer()
	{
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified object is an instance of {@code LongNode} <i>and</i> it has the same value
	 * as this 'long' node.
	 *
	 * @param  obj
	 *           the object with which this 'long' node will be compared.
	 * @return {@code true} if {@code obj} is an instance of {@code LongNode} <i>and</i> it has the same value as this
	 *         'long' node; {@code false} otherwise.
	 */

	@Override
	public boolean equals(
		Object	obj)
	{
		return (obj == this) || ((obj instanceof LongNode other) && (value == other.value));
	}

	//------------------------------------------------------------------

	/**
	 * Returns the hash code of this 'long' node.
	 *
	 * @return the hash code of this 'long' node.
	 */

	@Override
	public int hashCode()
	{
		return Long.hashCode(value);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a copy of this 'long' node that has no parent.
	 *
	 * @return a copy of this 'long' node that has no parent.
	 */

	@Override
	public LongNode clone()
	{
		return (LongNode)super.clone();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of this 'long' node.
	 *
	 * @return a string representation of this 'long' node.
	 */

	@Override
	public String toString()
	{
		return Long.toString(value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of this 'long' node.
	 *
	 * @return the value of this 'long' node.
	 */

	public long getValue()
	{
		return value;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
