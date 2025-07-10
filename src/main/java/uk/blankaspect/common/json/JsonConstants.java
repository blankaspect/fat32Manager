/*====================================================================*\

JsonConstants.java

Interface: JSON-related constants.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.json;

//----------------------------------------------------------------------


// IMPORTS


import java.util.List;

import uk.blankaspect.common.basictree.BooleanNode;
import uk.blankaspect.common.basictree.DoubleNode;
import uk.blankaspect.common.basictree.IntNode;
import uk.blankaspect.common.basictree.ListNode;
import uk.blankaspect.common.basictree.LongNode;
import uk.blankaspect.common.basictree.MapNode;
import uk.blankaspect.common.basictree.NodeType;
import uk.blankaspect.common.basictree.NullNode;
import uk.blankaspect.common.basictree.StringNode;

//----------------------------------------------------------------------


// INTERFACE: JSON-RELATED CONSTANTS


/**
 * This interface defines constants that relate to JSON.
 */

public interface JsonConstants
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The character that denotes the start of a JSON array. */
	char	ARRAY_START_CHAR	= '[';

	/** The character that denotes the end of a JSON array. */
	char	ARRAY_END_CHAR		= ']';

	/** The character that separates adjacent elements of a JSON array. */
	char	ARRAY_ELEMENT_SEPARATOR_CHAR	= ',';

	/** The character that denotes the start of a JSON object. */
	char	OBJECT_START_CHAR	= '{';

	/** The character that denotes the end of a JSON object. */
	char	OBJECT_END_CHAR		= '}';

	/** The character that separates the name and value of a member of a JSON object. */
	char	OBJECT_NAME_VALUE_SEPARATOR_CHAR	= ':';

	/** The character that separates adjacent members of a JSON object. */
	char	OBJECT_MEMBER_SEPARATOR_CHAR	= ',';

	/** The types of nodes that represent JSON values. */
	List<NodeType>	NODE_TYPES	= List.of
	(
		NullNode.TYPE,
		BooleanNode.TYPE,
		IntNode.TYPE,
		LongNode.TYPE,
		DoubleNode.TYPE,
		StringNode.TYPE,
		ListNode.TYPE,
		MapNode.TYPE
	);

	/** The types of nodes that represent simple JSON values. */
	List<NodeType>	SIMPLE_NODE_TYPES	= List.of
	(
		NullNode.TYPE,
		BooleanNode.TYPE,
		IntNode.TYPE,
		LongNode.TYPE,
		DoubleNode.TYPE,
		StringNode.TYPE
	);

	/** The types of nodes that represent compound JSON values. */
	List<NodeType>	COMPOUND_NODE_TYPES	= List.of
	(
		ListNode.TYPE,
		MapNode.TYPE
	);

	/** The types of nodes that represent JSON numbers. */
	List<NodeType>	NUMBER_TYPES	= List.of
	(
		IntNode.TYPE,
		LongNode.TYPE,
		DoubleNode.TYPE
	);
}

//----------------------------------------------------------------------
