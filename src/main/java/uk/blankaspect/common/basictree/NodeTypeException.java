/*====================================================================*\

NodeTypeException.java

Class: node-type exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.basictree;

//----------------------------------------------------------------------


// CLASS: NODE-TYPE EXCEPTION


/**
 * This class implements an unchecked exception that is associated with a {@linkplain NodeType node type}.
 */

public class NodeTypeException
	extends RuntimeException
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The node type that is associated with this exception. */
	private	NodeType	nodeType;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an exception that is associated with the specified node type.
	 *
	 * @param nodeType
	 *          the node type that will be associated with this exception.
	 */

	public NodeTypeException(
		NodeType	nodeType)
	{
		// Call superclass constructor
		super(nodeType.toString());

		// Initialise instance variable
		this.nodeType = nodeType;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the node type that is associated with this exception.
	 *
	 * @return the node type that is associated with this exception.
	 */

	public NodeType getNodeType()
	{
		return nodeType;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
