/*====================================================================*\

ListNode.java

Class: list node.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.basictree;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.stream.Stream;

//----------------------------------------------------------------------


// CLASS: LIST NODE


/**
 * This class implements a {@linkplain AbstractNode node} that contains a sequence of elements that are {@linkplain
 * AbstractNode nodes}.  The elements may be of different types (eg, a mixture of {@linkplain StringNode string nodes}
 * and {@linkplain ListNode list nodes}).
 * <p>
 * A list node may be created with an initial collection of elements, and elements may be added to a list node after its
 * creation, but elements cannot be removed from a list node.
 * </p>
 * <p>
 * The default string representation of a list node begins with a '[' (U+005B) and ends with a ']' (U+005D).  Adjacent
 * elements are separated with a ',' (U+002C).
 * </p>
 */

public class ListNode
	extends AbstractNode
	implements Iterable<AbstractNode>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The character that denotes the start of the string representation of a list node. */
	public static final	char	START_CHAR	= '[';

	/** The character that denotes the end of the string representation of a list node. */
	public static final	char	END_CHAR	= ']';

	/** The character that separates adjacent elements in the string representation of a list node. */
	public static final	char	ELEMENT_SEPARATOR_CHAR	= ',';

	/** The type of a list node. */
	public static final	NodeType	TYPE	= new NodeType(NodeType.ANY, ListNode.class);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** A list of the elements of this list node. */
	private	ArrayList<AbstractNode>	elements;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a list node that has no parent and initially contains no elements.
	 */

	public ListNode()
	{
		// Call alternative constructor
		this((AbstractNode)null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a list node that has the specified parent and initially contains no elements.
	 *
	 * @param parent
	 *          the parent of the list node.
	 */

	public ListNode(
		AbstractNode	parent)
	{
		// Call superclass constructor
		super(parent);

		// Initialise instance variables
		elements = new ArrayList<>();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a list node that has no parent and initially contains the specified elements.
	 *
	 * @param elements
	 *          the initial elements of the list node.
	 */

	public ListNode(
		Iterable<? extends AbstractNode>	elements)
	{
		// Call alternative constructor
		this(null, elements);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a list node that has the specified parent and initially contains the specified
	 * elements.
	 *
	 * @param parent
	 *          the parent of the list node.
	 * @param elements
	 *          the initial elements of the list node.
	 */

	public ListNode(
		AbstractNode	parent,
		AbstractNode...	elements)
	{
		// Call alternative constructor
		this(parent, List.of(elements));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a list node that has the specified parent and initially contains the specified
	 * elements.
	 *
	 * @param parent
	 *          the parent of the list node.
	 * @param elements
	 *          the initial elements of the list node.
	 */

	public ListNode(
		AbstractNode						parent,
		Iterable<? extends AbstractNode>	elements)
	{
		// Call alternative constructor
		this(parent);

		// Initialise instance variables
		setElements(elements);
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
	 * For a list node, this method always returns {@code true}.
	 *
	 * @return {@code true}.
	 */

	@Override
	public boolean isContainer()
	{
		return true;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the elements of this list node.  The returned list may be modified without affecting this list
	 * node, but modifying the elements of the list (for example, changing the parent of a node) <i>will</i> affect this
	 * list node.
	 *
	 * @return a list of the elements of this list node.
	 * @see    #getElements()
	 */

	@Override
	public List<AbstractNode> getChildren()
	{
		return new ArrayList<>(elements);
	}

	//------------------------------------------------------------------

	/**
	 * Returns an iterator over the elements of this list node.
	 *
	 * @return an iterator over the elements of this list node.
	 */

	@Override
	public Iterator<AbstractNode> iterator()
	{
		return elements.iterator();
	}

	//------------------------------------------------------------------
	/**
	 * Returns {@code true} if the specified object is an instance of {@code ListNode} <i>and</i> this list node
	 * contains the same number of elements as the other list node <i>and</i> each element in this list node is equal
	 * to the element at the same index in the other list node.
	 *
	 * @param  obj
	 *           the object with which this list node will be compared.
	 * @return {@code true} if {@code obj} is an instance of {@code ListNode} <i>and</i> this list node contains the
	 *         same number of elements as the other list node <i>and</i> each element in this list node is equal to the
	 *         element at the same index in the other list node; {@code false} otherwise.
	 */

	@Override
	public boolean equals(
		Object	obj)
	{
		if (this == obj)
			return true;

		return (obj instanceof ListNode other) && (elements.size() == other.elements.size())
				&& elements.equals(other.elements);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the hash code of this list node, which is the hash code of its elements.
	 *
	 * @return the hash code of this list node.
	 */

	@Override
	public int hashCode()
	{
		return elements.hashCode();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a deep copy of this list node that has no parent.
	 *
	 * @return a deep copy of this list node that has no parent.
	 */

	@Override
	public ListNode clone()
	{
		// Create copy of this list node
		ListNode copy = (ListNode)super.clone();

		// Copy elements
		copy.elements = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			AbstractNode value = element.clone();
			copy.elements.add(value);
			value.setParent(copy);
		}

		// Return copy
		return copy;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of this list node.
	 *
	 * @return a string representation of this list node.
	 */

	@Override
	public String toString()
	{
		return toString(true);
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public String toString(
		boolean	printableAsciiOnly)
	{
		StringBuilder buffer = new StringBuilder(128);
		buffer.append(START_CHAR);
		int numElements = elements.size();
		for (int i = 0; i < numElements; i++)
		{
			if (i > 0)
				buffer.append(ELEMENT_SEPARATOR_CHAR);
			buffer.append(' ');
			buffer.append(elements.get(i).toString(printableAsciiOnly));
		}
		buffer.append(' ');
		buffer.append(END_CHAR);
		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if this list node contains no elements.
	 *
	 * @return {@code true} if this list node contains no elements; {@code false} otherwise.
	 */

	public boolean isEmpty()
	{
		return elements.isEmpty();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of elements that this list node contains.
	 *
	 * @return the number of elements that this list node contains.
	 */

	public int getNumElements()
	{
		return elements.size();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element of this list node at the specified index.
	 *
	 * @param  index
	 *           the index of the required element.
	 * @return the element of this list node at {@code index}.
	 * @throws IndexOutOfBoundsException
	 *           if ({@code index} &lt; 0) or ({@code index} &gt;= {@link #getNumElements()}).
	 */

	public AbstractNode get(
		int	index)
	{
		return elements.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element of this list node at the specified index as a {@linkplain NullNode null node}.
	 *
	 * @param  index
	 *           the index of the required element.
	 * @return the element of this list node at {@code index}, cast to a {@link NullNode}.
	 * @throws IndexOutOfBoundsException
	 *           if ({@code index} &lt; 0) or ({@code index} &gt;= {@link #getNumElements()}).
	 * @throws ClassCastException
	 *           if the element at {@code index} is not an instance of {@link NullNode}.
	 */

	public NullNode getNull(
		int	index)
	{
		return (NullNode)elements.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element of this list node at the specified index as a {@linkplain BooleanNode Boolean node}.
	 *
	 * @param  index
	 *           the index of the required element.
	 * @return the element of this list node at {@code index}, cast to a {@link BooleanNode}.
	 * @throws IndexOutOfBoundsException
	 *           if ({@code index} &lt; 0) or ({@code index} &gt;= {@link #getNumElements()}).
	 * @throws ClassCastException
	 *           if the element at {@code index} is not an instance of {@link BooleanNode}.
	 */

	public BooleanNode getBoolean(
		int	index)
	{
		return (BooleanNode)elements.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element of this list node at the specified index as an {@linkplain IntNode 'int' node}.
	 *
	 * @param  index
	 *           the index of the required element.
	 * @return the element of this list node at {@code index}, cast to a {@link IntNode}.
	 * @throws IndexOutOfBoundsException
	 *           if ({@code index} &lt; 0) or ({@code index} &gt;= {@link #getNumElements()}).
	 * @throws ClassCastException
	 *           if the element at {@code index} is not an instance of {@link IntNode}.
	 */

	public IntNode getInt(
		int	index)
	{
		return (IntNode)elements.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element of this list node at the specified index as a {@linkplain LongNode 'long' node}.
	 *
	 * @param  index
	 *           the index of the required element.
	 * @return the element of this list node at {@code index}, cast to a {@link LongNode}.
	 * @throws IndexOutOfBoundsException
	 *           if ({@code index} &lt; 0) or ({@code index} &gt;= {@link #getNumElements()}).
	 * @throws ClassCastException
	 *           if the element at {@code index} is not an instance of {@link LongNode}.
	 */

	public LongNode getLong(int index)
	{
		return (LongNode)elements.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element of this list node at the specified index as a {@linkplain DoubleNode 'double' node}.
	 *
	 * @param  index
	 *           the index of the required element.
	 * @return the element of this list node at {@code index}, cast to a {@link DoubleNode}.
	 * @throws IndexOutOfBoundsException
	 *           if ({@code index} &lt; 0) or ({@code index} &gt;= {@link #getNumElements()}).
	 * @throws ClassCastException
	 *           if the element at {@code index} is not an instance of {@link DoubleNode}.
	 */

	public DoubleNode getDouble(
		int	index)
	{
		return (DoubleNode)elements.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element of this list node at the specified index as a {@linkplain StringNode string node}.
	 *
	 * @param  index
	 *           the index of the required element.
	 * @return the element of this list node at {@code index}, cast to a {@link StringNode}.
	 * @throws IndexOutOfBoundsException
	 *           if ({@code index} &lt; 0) or ({@code index} &gt;= {@link #getNumElements()}).
	 * @throws ClassCastException
	 *           if the element at {@code index} is not an instance of {@link StringNode}.
	 */

	public StringNode getString(
		int	index)
	{
		return (StringNode)elements.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element of this list node at the specified index as a {@linkplain ListNode list node}.
	 *
	 * @param  index
	 *           the index of the required element.
	 * @return the element of this list node at {@code index}, cast to a {@link ListNode}.
	 * @throws IndexOutOfBoundsException
	 *           if ({@code index} &lt; 0) or ({@code index} &gt;= {@link #getNumElements()}).
	 * @throws ClassCastException
	 *           if the element at {@code index} is not an instance of {@link ListNode}.
	 */

	public ListNode getList(
		int	index)
	{
		return (ListNode)elements.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element of this list node at the specified index as a {@linkplain MapNode map node}.
	 *
	 * @param  index
	 *           the index of the required element.
	 * @return the element of this list node at {@code index}, cast to a {@link MapNode}.
	 * @throws IndexOutOfBoundsException
	 *           if ({@code index} &lt; 0) or ({@code index} &gt;= {@link #getNumElements()}).
	 * @throws ClassCastException
	 *           if the element at {@code index} is not an instance of {@link MapNode}.
	 */

	public MapNode getMap(
		int	index)
	{
		return (MapNode)elements.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Returns an unmodifiable list of the elements of this list node.  Although the returned list cannot be modified,
	 * its elements <i>can</i> be modified, and doing so (for example, changing the parent of a node) will affect this
	 * list node.
	 *
	 * @return an unmodifiable list of the elements of this list node.
	 * @see    #getChildren()
	 */

	public List<AbstractNode> getElements()
	{
		return Collections.unmodifiableList(elements);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if any of the elements of this list node is of the specified type or a subtype of the
	 * specified type.
	 *
	 * @param  type
	 *           the target type of the elements.
	 * @return {@code true} if the type of any of the elements of this list node is {@code type} or a subtype of {@code
	 *         type}.
	 */

	public boolean any(
		NodeType	type)
	{
		for (AbstractNode element : elements)
		{
			if (element.getType().is(type))
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if all the elements of this list node are of the specified type or a subtype of the
	 * specified type.
	 *
	 * @param  type
	 *           the target type of the elements.
	 * @return {@code true} if the type of all the elements of this list node is {@code type} or a subtype of {@code
	 *         type}.
	 */

	public boolean all(
		NodeType	type)
	{
		for (AbstractNode element : elements)
		{
			if (!element.getType().is(type))
				return false;
		}
		return true;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the elements of this list node that are {@linkplain NullNode null nodes}.
	 *
	 * @return a list of the elements of this list node that are null nodes.
	 */

	public List<NullNode> nullNodes()
	{
		List<NullNode> nodes = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			if (element instanceof NullNode nullNode)
				nodes.add(nullNode);
		}
		return nodes;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a stream of the elements of this list node that are {@linkplain NullNode null nodes}.
	 *
	 * @return a stream of the elements of this list node that are null nodes.
	 */

	public Stream<NullNode> nullStream()
	{
		return nullNodes().stream();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the elements of this list node that are {@linkplain BooleanNode Boolean nodes}.
	 *
	 * @return a list of the elements of this list node that are Boolean nodes.
	 */

	public List<BooleanNode> booleanNodes()
	{
		List<BooleanNode> nodes = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			if (element instanceof BooleanNode booleanNode)
				nodes.add(booleanNode);
		}
		return nodes;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a stream of the elements of this list node that are {@linkplain BooleanNode Boolean nodes}.
	 *
	 * @return a stream of the elements of this list node that are Boolean nodes.
	 */

	public Stream<BooleanNode> booleanStream()
	{
		return booleanNodes().stream();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as an array of {@code boolean}s.
	 *
	 * @return an array of the underlying {@code boolean} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not a {@linkplain BooleanNode Boolean node}.
	 */

	public boolean[] getBooleanArray()
	{
		boolean[] values = new boolean[elements.size()];
		for (int i = 0; i < values.length; i++)
		{
			// Get element
			AbstractNode element = elements.get(i);

			// Check for element of the required type
			if (!(element instanceof BooleanNode booleanNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to array
			values[i] = booleanNode.getValue();
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as a list of {@code Boolean}s.
	 *
	 * @return a list of the underlying {@code Boolean} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not a {@linkplain BooleanNode Boolean node}.
	 */

	public List<Boolean> getBooleanList()
	{
		List<Boolean> values = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			// Check for element of the required type
			if (!(element instanceof BooleanNode booleanNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to list
			values.add(booleanNode.getValue());
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the elements of this list node that are {@linkplain IntNode 'int' nodes}.
	 *
	 * @return a list of the elements of this list node that are 'int' nodes.
	 */

	public List<IntNode> intNodes()
	{
		List<IntNode> nodes = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			if (element instanceof IntNode intNode)
				nodes.add(intNode);
		}
		return nodes;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a stream of the elements of this list node that are {@linkplain IntNode 'int' nodes}.
	 *
	 * @return a stream of the elements of this list node that are 'int' nodes.
	 */

	public Stream<IntNode> intStream()
	{
		return intNodes().stream();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as an array of {@code int}s.
	 *
	 * @return an array of the underlying {@code int} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not an {@linkplain IntNode 'int' node}.
	 */

	public int[] getIntArray()
	{
		int[] values = new int[elements.size()];
		for (int i = 0; i < values.length; i++)
		{
			// Get element
			AbstractNode element = elements.get(i);

			// Check for element of the required type
			if (!(element instanceof IntNode intNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to array
			values[i] = intNode.getValue();
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as a list of {@code Integer}s.
	 *
	 * @return a list of the underlying {@code Integer} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not an {@linkplain IntNode 'int' node}.
	 */

	public List<Integer> getIntList()
	{
		List<Integer> values = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			// Check for element of the required type
			if (!(element instanceof IntNode intNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to list
			values.add(intNode.getValue());
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the elements of this list node that are {@linkplain LongNode 'long' nodes}.
	 *
	 * @return a list of the elements of this list node that are 'long' nodes.
	 */

	public List<LongNode> longNodes()
	{
		List<LongNode> nodes = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			if (element instanceof LongNode longNode)
				nodes.add(longNode);
		}
		return nodes;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a stream of the elements of this list node that are {@linkplain LongNode 'long' nodes}.
	 *
	 * @return a stream of the elements of this list node that are 'long' nodes.
	 */

	public Stream<LongNode> longStream()
	{
		return longNodes().stream();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as an array of {@code long}s.
	 *
	 * @return an array of the underlying {@code long} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not a {@linkplain LongNode 'long' node}.
	 */

	public long[] getLongArray()
	{
		long[] values = new long[elements.size()];
		for (int i = 0; i < values.length; i++)
		{
			// Get element
			AbstractNode element = elements.get(i);

			// Check for element of the required type
			if (!(element instanceof LongNode longNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to array
			values[i] = longNode.getValue();
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as a list of {@code Long}s.
	 *
	 * @return a list of the underlying {@code Long} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not a {@linkplain LongNode 'long' node}.
	 */

	public List<Long> getLongList()
	{
		List<Long> values = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			// Check for element of the required type
			if (!(element instanceof LongNode longNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to list
			values.add(longNode.getValue());
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as an array of {@code long}s.  The elements of
	 * this list may be either {@linkplain IntNode 'int' nodes} or {@linkplain LongNode 'long' nodes}.
	 *
	 * @return an array of the underlying {@code long} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is neither an {@linkplain IntNode 'int' node} nor a
	 *           {@linkplain LongNode 'long' node}.
	 */

	public long[] getIntOrLongArray()
	{
		long[] values = new long[elements.size()];
		for (int i = 0; i < values.length; i++)
		{
			// Get element
			AbstractNode element = elements.get(i);

			// Add value of element to array
			if (element instanceof IntNode intNode)
				values[i] = intNode.getValue();
			else if (element instanceof LongNode longNode)
				values[i] = longNode.getValue();
			else
				throw new NodeTypeException(element.getType());
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as a list of {@code Long}s.  The elements of this
	 * list may be either {@linkplain IntNode 'int' nodes} or {@linkplain LongNode 'long' nodes}.
	 *
	 * @return a list of the underlying {@code Long} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is neither an {@linkplain IntNode 'int' node} nor a
	 *           {@linkplain LongNode 'long' node}.
	 */

	public List<Long> getIntOrLongList()
	{
		List<Long> values = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			// Add value of element to list
			if (element instanceof IntNode intNode)
				values.add((long)intNode.getValue());
			else if (element instanceof LongNode longNode)
				values.add(longNode.getValue());
			else
				throw new NodeTypeException(element.getType());
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the elements of this list node that are {@linkplain DoubleNode 'double' nodes}.
	 *
	 * @return a list of the elements of this list node that are 'double' nodes.
	 */

	public List<DoubleNode> doubleNodes()
	{
		List<DoubleNode> nodes = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			if (element instanceof DoubleNode doubleNode)
				nodes.add(doubleNode);
		}
		return nodes;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a stream of the elements of this list node that are {@linkplain DoubleNode 'double' nodes}.
	 *
	 * @return a stream of the elements of this list node that are 'double' nodes.
	 */

	public Stream<DoubleNode> doubleStream()
	{
		return doubleNodes().stream();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as an array of {@code double}s.
	 *
	 * @return an array of the underlying {@code double} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not a {@linkplain DoubleNode 'double' node}.
	 */

	public double[] getDoubleArray()
	{
		double[] values = new double[elements.size()];
		for (int i = 0; i < values.length; i++)
		{
			// Get element
			AbstractNode element = elements.get(i);

			// Check for element of the required type
			if (!(element instanceof DoubleNode doubleNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to array
			values[i] = doubleNode.getValue();
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as a list of {@code Double}s.
	 *
	 * @return a list of the underlying {@code Double} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not a {@linkplain DoubleNode 'double' node}.
	 */

	public List<Double> getDoubleList()
	{
		List<Double> values = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			// Check for element of the required type
			if (!(element instanceof DoubleNode doubleNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to list
			values.add(doubleNode.getValue());
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as an array of {@code double}s.  The elements of
	 * this list may be either {@linkplain IntNode 'int' nodes}, {@linkplain LongNode 'long' nodes} or {@linkplain
	 * DoubleNode 'double' nodes}.
	 *
	 * @return an array of the underlying {@code double} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not an {@linkplain IntNode 'int' node}, a {@linkplain
	 *           LongNode 'long' node} or a {@linkplain DoubleNode 'double' node}.
	 */

	public double[] getNumberArray()
	{
		double[] values = new double[elements.size()];
		for (int i = 0; i < values.length; i++)
		{
			// Get element
			AbstractNode element = elements.get(i);

			// Add value of element to array
			if (element instanceof IntNode intNode)
				values[i] = intNode.getValue();
			else if (element instanceof LongNode longNode)
				values[i] = longNode.getValue();
			else if (element instanceof DoubleNode doubleNode)
				values[i] = doubleNode.getValue();
			else
				throw new NodeTypeException(element.getType());
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as a list of {@code Double}s.  The elements of
	 * this list may be {@linkplain IntNode 'int' nodes}, {@linkplain LongNode 'long' nodes} or {@linkplain DoubleNode
	 * 'double' nodes}.
	 *
	 * @return a list of the underlying {@code Double} values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not an {@linkplain IntNode 'int' node}, a {@linkplain
	 *           LongNode 'long' node} or a {@linkplain DoubleNode 'double' node}.
	 */

	public List<Double> getNumberList()
	{
		List<Double> values = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			// Add value of element to list
			if (element instanceof IntNode intNode)
				values.add((double)intNode.getValue());
			else if (element instanceof LongNode longNode)
				values.add((double)longNode.getValue());
			else if (element instanceof DoubleNode doubleNode)
				values.add(doubleNode.getValue());
			else
				throw new NodeTypeException(element.getType());
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the elements of this list node that are {@linkplain StringNode string nodes}.
	 *
	 * @return a list of the elements of this list node that are string nodes.
	 */

	public List<StringNode> stringNodes()
	{
		List<StringNode> nodes = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			if (element instanceof StringNode stringNode)
				nodes.add(stringNode);
		}
		return nodes;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a stream of the elements of this list node that are {@linkplain StringNode string nodes}.
	 *
	 * @return a stream of the elements of this list node that are string nodes.
	 */

	public Stream<StringNode> stringStream()
	{
		return stringNodes().stream();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as an array of strings.
	 *
	 * @return an array of the underlying string values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not a {@linkplain StringNode string node}.
	 */

	public String[] getStringArray()
	{
		String[] values = new String[elements.size()];
		for (int i = 0; i < values.length; i++)
		{
			// Get element
			AbstractNode element = elements.get(i);

			// Check for element of the required type
			if (!(element instanceof StringNode stringNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to array
			values[i] = stringNode.getValue();
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying values of the elements of this list node as a list of strings.
	 *
	 * @return a list of the underlying string values of the elements of this list node.
	 * @throws NodeTypeException
	 *           if any of the elements of this list node is not a {@linkplain StringNode string node}.
	 */

	public List<String> getStringList()
	{
		List<String> values = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			// Check for element of the required type
			if (!(element instanceof StringNode stringNode))
				throw new NodeTypeException(element.getType());

			// Add value of element to list
			values.add(stringNode.getValue());
		}
		return values;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the elements of this list node that are {@linkplain ListNode list nodes}.
	 *
	 * @return a list of the elements of this list node that are list nodes.
	 */

	public List<ListNode> listNodes()
	{
		List<ListNode> nodes = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			if (element instanceof ListNode listNode)
				nodes.add(listNode);
		}
		return nodes;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a stream of the elements of this list node that are {@linkplain ListNode list nodes}.
	 *
	 * @return a stream of the elements of this list node that are list nodes.
	 */

	public Stream<ListNode> listStream()
	{
		return listNodes().stream();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the elements of this list node that are {@linkplain MapNode map nodes}.
	 *
	 * @return a list of the elements of this list node that are map nodes.
	 */

	public List<MapNode> mapNodes()
	{
		List<MapNode> nodes = new ArrayList<>();
		for (AbstractNode element : elements)
		{
			if (element instanceof MapNode mapNode)
				nodes.add(mapNode);
		}
		return nodes;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a stream of the elements of this list node that are {@linkplain MapNode map nodes}.
	 *
	 * @return a stream of the elements of this list node that are map nodes.
	 */

	public Stream<MapNode> mapStream()
	{
		return mapNodes().stream();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the index of the specified node in the list of the elements of this list node.  The node is compared for
	 * identity, not equality, with each element of the list until a match is found or all elements have been compared.
	 *
	 * @param  node
	 *           the node whose index is required.
	 * @return the index of {@code node} in the list of the elements of this list node, or -1 if {@code node} is not an
	 *         element of this list node.
	 */

	public int indexOf(
		AbstractNode	node)
	{
		int numElements = elements.size();
		for (int i = 0; i < numElements; i++)
		{
			if (elements.get(i) == node)
				return i;
		}
		return -1;
	}

	//------------------------------------------------------------------

	/**
	 * Removes all the elements of this list node.
	 */

	public void clear()
	{
		elements.clear();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the elements of this list node to the specified nodes.
	 *
	 * @param nodes
	 *          the nodes to which the elements of this list node will be set.
	 */

	public void setElements(
		AbstractNode...	nodes)
	{
		elements.clear();
		addElements(nodes);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the elements of this list node to the specified nodes.
	 *
	 * @param nodes
	 *          the nodes to which the elements of this list node will be set.
	 */

	public void setElements(
		Iterable<? extends AbstractNode>	nodes)
	{
		elements.clear();
		addElements(nodes);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified node to the end of the list of elements of this list node.
	 *
	 * @param  node
	 *           the node that will be added to the end of the list of elements of this list node.
	 * @throws IllegalArgumentException
	 *           if {@code node} is {@code null}.
	 */

	public void add(
		AbstractNode	node)
	{
		// Validate argument
		if (node == null)
			throw new IllegalArgumentException("Null value");

		// Add element to list
		elements.add(node);

		// Set parent of new element
		node.setParent(this);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified nodes to the end of the list of elements of this list node.  The nodes are added in the order
	 * of the arguments.
	 *
	 * @param nodes
	 *          the nodes that will be added to the end of the list of elements of this list node.
	 */

	public void addElements(
		AbstractNode...	nodes)
	{
		for (AbstractNode node : nodes)
			add(node);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified nodes to the end of the list of elements of this list node.  The nodes are added in the order
	 * in which they are returned by their iterator.
	 *
	 * @param nodes
	 *          the nodes that will be added to the list of elements of this list node.
	 */

	public void addElements(
		Iterable<? extends AbstractNode>	nodes)
	{
		for (AbstractNode node : nodes)
			add(node);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain NullNode null node}, adds it to the end of the list of elements of this
	 * list node and returns it.
	 *
	 * @return the null node that was created and added to the elements of this list node.
	 */

	public NullNode addNull()
	{
		NullNode node = new NullNode();
		add(node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain BooleanNode Boolean node} with the specified value, adds it to the end of
	 * of the list of elements of this list node and returns it.
	 *
	 * @param  value
	 *           the value of the Boolean node that will be created and added to the elements of this list node.
	 * @return the Boolean node that was created from {@code value} and added to the elements of this list node.
	 */

	public BooleanNode addBoolean(
		boolean	value)
	{
		BooleanNode node = new BooleanNode(value);
		add(node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain BooleanNode Boolean node} for each of the specified values and adds the
	 * nodes to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which Boolean nodes will be created and added to the elements of this list node.
	 */

	public void addBooleans(
		boolean...	values)
	{
		for (boolean value : values)
			add(new BooleanNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain BooleanNode Boolean node} for each of the specified values and adds the
	 * nodes to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which Boolean nodes will be created and added to the elements of this list node.
	 */

	public void addBooleans(
		Iterable<Boolean>	values)
	{
		for (Boolean value : values)
			add(new BooleanNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an {@linkplain IntNode 'int' node} with the specified value, adds it to the end of the
	 * list of elements of this list node and returns it.
	 *
	 * @param  value
	 *           the value of the 'int' node that will be created and added to the elements of this list node.
	 * @return the 'int' node that was created from {@code value} and added to the elements of this list node.
	 */

	public IntNode addInt(
		int	value)
	{
		IntNode node = new IntNode(value);
		add(node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an {@linkplain IntNode 'int' node} for each of the specified values and adds the nodes
	 * to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which 'int' nodes will be created and added to the elements of this list node.
	 */

	public void addInts(
		int...	values)
	{
		for (int value : values)
			add(new IntNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an {@linkplain IntNode 'int' node} for each of the specified values and adds the nodes
	 * to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which 'int' nodes will be created and added to the elements of this list node.
	 */

	public void addInts(
		Iterable<Integer>	values)
	{
		for (Integer value : values)
			add(new IntNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain LongNode 'long' node} with the specified value, adds it to the end of the
	 * list of elements of this list node and returns it.
	 *
	 * @param  value
	 *           the value of the 'long' node that will be created and added to the elements of this list node.
	 * @return the 'long' node that was created from {@code value} and added to the elements of this list node.
	 */

	public LongNode addLong(
		long	value)
	{
		LongNode node = new LongNode(value);
		add(node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain LongNode 'long' node} for each of the specified values and adds the nodes
	 * to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which 'long' nodes will be created and added to the elements of this list node.
	 */

	public void addLongs(
		long...	values)
	{
		for (long value : values)
			add(new LongNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain LongNode 'long' node} for each of the specified values and adds the nodes
	 * to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which 'long' nodes will be created and added to the elements of this list node.
	 */

	public void addLongs(
		Iterable<Long>	values)
	{
		for (Long value : values)
			add(new LongNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain DoubleNode 'double' node} with the specified value, adds it to the end of
	 * the list of elements of this list node and returns it.
	 *
	 * @param  value
	 *           the value of the 'double' node that will be created and added to the elements of this list node.
	 * @return the 'double' node that was created from {@code value} and added to the elements of this list node.
	 */

	public DoubleNode addDouble(
		double	value)
	{
		DoubleNode node = new DoubleNode(value);
		add(node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain DoubleNode 'double' node} for each of the specified values and adds the
	 * nodes to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which 'double' nodes will be created and added to the elements of this list node.
	 */

	public void addDoubles(
		double...	values)
	{
		for (double value : values)
			add(new DoubleNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain DoubleNode 'double' node} for each of the specified values and adds the
	 * nodes to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which 'double' nodes will be created and added to the elements of this list node.
	 */

	public void addDoubles(
		Iterable<Double>	values)
	{
		for (Double value : values)
			add(new DoubleNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain StringNode string node} with the specified value, adds it to the end of
	 * the list of elements of this list node and returns it.
	 *
	 * @param  value
	 *           the value of the string node that will be created and added to the elements of this list node.
	 * @return the string node that was created from {@code value} and added to the elements of this list node.
	 */

	public StringNode addString(
		String	value)
	{
		StringNode node = new StringNode(value);
		add(node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain StringNode string node} for each of the specified values and adds the
	 * nodes to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which string nodes will be created and added to the elements of this list node.
	 */

	public void addStrings(
		String...	values)
	{
		for (String value : values)
			add(new StringNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain StringNode string node} for each of the specified values and adds the
	 * nodes to the end of the list of elements of this list node, preserving the order of the values.
	 *
	 * @param values
	 *          the values for which string nodes will be created and added to the elements of this list node.
	 */

	public void addStrings(
		Iterable<String>	values)
	{
		for (String value : values)
			add(new StringNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} with the specified elements, adds it to the end of
	 * the list of elements of this list node and returns it.
	 *
	 * @param  elements
	 *           the elements of the list node that will be created and added to the elements of this list node.
	 * @return the list node that was created from {@code elements} and added to the elements of this list node.
	 */

	public ListNode addList(
		AbstractNode...	elements)
	{
		ListNode node = new ListNode(List.of(elements));
		add(node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} with the specified elements, adds it to the end of
	 * the list of elements of this list node and returns it.
	 *
	 * @param  elements
	 *           the elements of the list node that will be created and added to the elements of this list node.
	 * @return the list node that was created from {@code elements} and added to the elements of this list node.
	 */

	public ListNode addList(
		Iterable<? extends AbstractNode>	elements)
	{
		ListNode node = new ListNode(elements);
		add(node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain MapNode map node} with the specified key&ndash;value pairs, adds it to
	 * the end of the list of elements of this list node and returns it.
	 *
	 * @param  pairs
	 *           the key&ndash;value pairs of the map node that will be created and added to the elements of this list
	 *           node.
	 * @return the map node that was created from {@code pairs} and added to the elements of this list node.
	 */

	public MapNode addMap(
		Map<String, AbstractNode>	pairs)
	{
		MapNode node = new MapNode(pairs);
		add(node);
		return node;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
