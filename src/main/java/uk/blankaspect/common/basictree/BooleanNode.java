/*====================================================================*\

BooleanNode.java

Class: Boolean node.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.basictree;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//----------------------------------------------------------------------


// CLASS: BOOLEAN NODE


/**
 * This class implements a {@linkplain AbstractNode node} that contains a Boolean value.
 */

public class BooleanNode
	extends AbstractNode
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The string representation of a Boolean node whose value is <i>false</i>. */
	public static final	String	VALUE_FALSE	= "false";

	/** The string representation of a Boolean node whose value is <i>true</i>. */
	public static final	String	VALUE_TRUE	= "true";

	/** The type of a Boolean node. */
	public static final	NodeType	TYPE	= new NodeType(NodeType.ANY, BooleanNode.class);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The value of this Boolean node. */
	private	boolean	value;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a Boolean node that has no parent and has the specified value.
	 *
	 * @param value
	 *          the value of the Boolean node.
	 */

	public BooleanNode(
		boolean	value)
	{
		// Call alternative constructor
		this(null, value);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a Boolean node that has the specified parent and value.
	 *
	 * @param parent
	 *          the parent of the Boolean node.
	 * @param value
	 *          the value of the Boolean node.
	 */

	public BooleanNode(
		AbstractNode	parent,
		boolean			value)
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
	 * Creates a list of Boolean nodes for the specified values, preserving the order of the elements, and returns the
	 * list, which may be used to construct a {@linkplain ListNode list node}.
	 *
	 * @param  values
	 *           the values for which Boolean nodes will be created.
	 * @return a list of Boolean nodes whose underlying values are {@code values}.
	 */

	public static List<BooleanNode> valuesToNodes(
		boolean...	values)
	{
		List<BooleanNode> outValues = new ArrayList<>();
		for (boolean value : values)
			outValues.add(new BooleanNode(value));
		return outValues;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a list of Boolean nodes for the specified values, preserving the order of the elements, and returns the
	 * list, which may be used to construct a {@linkplain ListNode list node}.
	 *
	 * @param  values
	 *           the values for which Boolean nodes will be created.
	 * @return a list of Boolean nodes whose underlying values are {@code values}.
	 */

	public static List<BooleanNode> valuesToNodes(
		Iterable<Boolean>	values)
	{
		List<BooleanNode> outValues = new ArrayList<>();
		for (Boolean value : values)
			outValues.add(new BooleanNode(value));
		return outValues;
	}

	//------------------------------------------------------------------

	/**
	 * Returns an array whose elements are the values of the specified {@linkplain BooleanNode Boolean nodes}, with the
	 * order of the elements preserved.
	 *
	 * @param  nodes
	 *           the Boolean nodes whose values will be extracted into an array.
	 * @return an array whose elements are the values of {@code nodes}.
	 */

	public static boolean[] nodesToArray(
		Collection<? extends BooleanNode>	nodes)
	{
		int numNodes = nodes.size();
		boolean[] values = new boolean[numNodes];
		int index = 0;
		for (BooleanNode node : nodes)
			values[index++] = node.getValue();
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list whose elements are the values of the specified {@linkplain BooleanNode Boolean nodes}, with the
	 * order of the elements preserved.
	 *
	 * @param  nodes
	 *           the Boolean nodes whose values will be extracted into a list.
	 * @return a list whose elements are the values of {@code nodes}.
	 */

	public static List<Boolean> nodesToList(
		Iterable<? extends BooleanNode>	nodes)
	{
		List<Boolean> values = new ArrayList<>();
		for (BooleanNode node : nodes)
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
	 * For a Boolean node, this method always returns {@code false}.
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
	 * Returns {@code true} if the specified object is an instance of {@code BooleanNode} <i>and</i> it has the same
	 * value as this Boolean node.
	 *
	 * @param  obj
	 *           the object with which this Boolean node will be compared.
	 * @return {@code true} if {@code obj} is an instance of {@code BooleanNode} <i>and</i> it has the same value as
	 *         this Boolean node ; {@code false} otherwise.
	 */

	@Override
	public boolean equals(
		Object	obj)
	{
		return (this == obj) || ((obj instanceof BooleanNode other) && (value == other.value));
	}

	//------------------------------------------------------------------

	/**
	 * Returns the hash code of this Boolean node.
	 *
	 * @return the hash code of this Boolean node.
	 */

	@Override
	public int hashCode()
	{
		return Boolean.hashCode(value);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a copy of this Boolean node that has no parent.
	 *
	 * @return a copy of this Boolean node that has no parent.
	 */

	@Override
	public BooleanNode clone()
	{
		return (BooleanNode)super.clone();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of this Boolean node.
	 *
	 * @return a string representation of this Boolean node.
	 */

	@Override
	public String toString()
	{
		return value ? VALUE_TRUE : VALUE_FALSE;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of this Boolean node.
	 *
	 * @return the value of this Boolean node.
	 */

	public boolean getValue()
	{
		return value;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
