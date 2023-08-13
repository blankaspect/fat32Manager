/*====================================================================*\

NullNode.java

Class: null node.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.basictree;

//----------------------------------------------------------------------


// CLASS: NULL NODE


/**
 * This class implements a {@linkplain AbstractNode node} that represents a null value.
 */

public class NullNode
	extends AbstractNode
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The string representation of a null node. */
	public static final	String	VALUE	= "null";

	/** The type of a null node. */
	public static final	NodeType	TYPE	= new NodeType(NodeType.ANY, NullNode.class);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a null node that has no parent.
	 */

	public NullNode()
	{
		// Call alternative constructor
		this(null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a null node that has the specified parent.
	 *
	 * @param parent
	 *          the parent of the null node.
	 */

	public NullNode(
		AbstractNode	parent)
	{
		// Call superclass constructor
		super(parent);
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
	 * For a null node, this method always returns {@code false}.
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
	 * Returns {@code true} if the specified object is an instance of {@code NullNode}.
	 *
	 * @param  obj
	 *           the object with which this null node will be compared.
	 * @return {@code true} if {@code obj} is an instance of {@code NullNode}; {@code false} otherwise.
	 */

	@Override
	public boolean equals(
		Object	obj)
	{
		return (obj instanceof NullNode);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the hash code of this null node.
	 *
	 * @return the hash code of this null node.
	 */

	@Override
	public int hashCode()
	{
		return 1;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a copy of this null node that has no parent.
	 *
	 * @return a copy of this null node that has no parent.
	 */

	@Override
	public NullNode clone()
	{
		return (NullNode)super.clone();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of this null node.
	 *
	 * @return a string representation of this null node.
	 */

	@Override
	public String toString()
	{
		return VALUE;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
