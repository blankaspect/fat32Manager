/*====================================================================*\

MapNode.java

Class: map node.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.basictree;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.function.Function;
import java.util.function.Predicate;

//----------------------------------------------------------------------


// CLASS: MAP NODE


/**
 * This class implements a {@linkplain AbstractNode node} that contains a collection of key&ndash;value pairs whose keys
 * are strings and whose values are {@linkplain AbstractNode node}s.  In the documentation of this class,
 * <i>key&ndash;value pair</i> is sometimes abbreviated to <i>KV pair</i> or just <i>pair</i>.
 * <p>
 * A map node preserves the order of the key&ndash;value pairs that are added to it; that is, an iterator over the
 * collection of KV pairs will traverse the pairs in the order in which their <i>keys</i> were added to the map node.
 * (The same is true of iterators over the collections of keys ({@link #getKeys()}) or values ({@link #getChildren()}).)
 * If a KV pair is added to a map node that already contains a KV pair whose key is equal to the key of the new pair,
 * the value of the new pair will replace the old value in the map without affecting the order of the KV pairs.
 * </p>
 * <p>
 * A map node may be created with an initial collection of key&ndash;value pairs.  A map node is mutable: KV pairs may
 * be added to and removed from a map node after its creation.
 * </p>
 * <p>
 * The default string representation of a map node begins with a '{' (U+007B) and ends with a '}' (U+007D).  The key of
 * a KV pair is escaped and enclosed in quotation marks in the same way as the value of a {@linkplain StringNode string
 * node}.  The key and value of a KV pair are separated with a ':' (U+003A).  Adjacent KV pairs are separated with a ','
 * (U+002C).
 * </p>
 */

public class MapNode
	extends AbstractNode
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The character that denotes the start of the string representation of a map node. */
	public static final	char	START_CHAR	= '{';

	/** The character that denotes the end of the string representation of a map node. */
	public static final	char	END_CHAR	= '}';

	/** The character that separates the key and value of a KV pair in the string representation of a map node. */
	public static final	char	KEY_VALUE_SEPARATOR_CHAR	= ':';

	/** The character that separates adjacent KV pairs in the string representation of a map node. */
	public static final	char	PAIR_SEPARATOR_CHAR	= ',';

	/** The type of a map node. */
	public static final	NodeType	TYPE	= new NodeType(NodeType.ANY, MapNode.class);

	/** Miscellaneous strings. */
	private static final	String	NULL_KEY_STR	= "Null key";
	private static final	String	NULL_VALUE_STR	= "Null value";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** A map of the key&ndash;value pairs of this map node. */
	private	LinkedHashMap<String, AbstractNode>	pairs;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a map node that has no parent and initially contains no key&ndash;value pairs.
	 */

	public MapNode()
	{
		// Call alternative constructor
		this((AbstractNode)null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a map node that has the specified parent and initially contains no key&ndash;value
	 * pairs.
	 *
	 * @param parent
	 *          the parent of the map node.
	 */

	public MapNode(
		AbstractNode	parent)
	{
		// Call superclass constructor
		super(parent);

		// Initialise instance variables
		pairs = new LinkedHashMap<>();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a map node that has no parent and initially contains the specified key&ndash;value
	 * pairs.
	 *
	 * @param pairs
	 *          the initial key&ndash;value pairs of the map node.
	 */

	public MapNode(
		Pair...	pairs)
	{
		// Call alternative constructor
		this(null, List.of(pairs));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a map node that has no parent and initially contains the specified key&ndash;value
	 * pairs.
	 *
	 * @param pairs
	 *          the initial key&ndash;value pairs of the map node.
	 */

	public MapNode(
		Iterable<? extends Pair>	pairs)
	{
		// Call alternative constructor
		this(null, pairs);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a map node that has no parent and initially contains the specified key&ndash;value
	 * pairs.
	 *
	 * @param pairs
	 *          the initial key&ndash;value pairs of the map node.
	 */

	public MapNode(
		Map<String, AbstractNode>	pairs)
	{
		// Call alternative constructor
		this(null, pairs);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a map node that has the specified parent and initially contains the specified
	 * key&ndash;value pairs.
	 *
	 * @param parent
	 *          the parent of the map node.
	 * @param pairs
	 *          the initial key&ndash;value pairs of the map node.
	 */

	public MapNode(
		AbstractNode	parent,
		Pair...			pairs)
	{
		// Call alternative constructor
		this(parent, List.of(pairs));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a map node that has the specified parent and initially contains the specified
	 * key&ndash;value pairs.
	 *
	 * @param parent
	 *          the parent of the map node.
	 * @param pairs
	 *          the initial key&ndash;value pairs of the map node.
	 */

	public MapNode(
		AbstractNode				parent,
		Iterable<? extends Pair>	pairs)
	{
		// Call alternative constructor
		this(parent);

		// Initialise instance variables
		addPairs(pairs);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a map node that has the specified parent and initially contains the specified
	 * key&ndash;value pairs.
	 *
	 * @param parent
	 *          the parent of the map node.
	 * @param pairs
	 *          the initial key&ndash;value pairs of the map node.
	 */

	public MapNode(
		AbstractNode				parent,
		Map<String, AbstractNode>	pairs)
	{
		// Call alternative constructor
		this(parent);

		// Initialise instance variables
		addPairs(pairs);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a {@linkplain BooleanNode Boolean node} for the specified value, creates a {@linkplain Pair
	 * key&ndash;value pair} whose key is the specified key and whose value is the Boolean node, and returns the KV
	 * pair.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair.
	 * @param  value
	 *           the value of the Boolean node.
	 * @return a key&ndash;value pair whose key is {@code key} and whose value is a new Boolean node whose value is
	 *         {@code value}.
	 */

	public static Pair pair(
		String	key,
		boolean	value)
	{
		return new Pair(key, new BooleanNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates an {@linkplain IntNode 'int' node} for the specified value, creates a {@linkplain Pair key&ndash;value
	 * pair} whose key is the specified key and whose value is the 'int' node, and returns the KV pair.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair.
	 * @param  value
	 *           the value of the 'int' node.
	 * @return a key&ndash;value pair whose key is {@code key} and whose value is a new 'int' node whose value is
	 *         {@code value}.
	 */

	public static Pair pair(
		String	key,
		int		value)
	{
		return new Pair(key, new IntNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@linkplain LongNode 'long' node} for the specified value, creates a {@linkplain Pair key&ndash;value
	 * pair} whose key is the specified key and whose value is the 'long' node, and returns the KV pair.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair.
	 * @param  value
	 *           the value of the 'long' node.
	 * @return a key&ndash;value pair whose key is {@code key} and whose value is a new 'long' node whose value is
	 *         {@code value}.
	 */

	public static Pair pair(
		String	key,
		long	value)
	{
		return new Pair(key, new LongNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@linkplain DoubleNode 'double' node} for the specified value, creates a {@linkplain Pair
	 * key&ndash;value pair} whose key is the specified key and whose value is the 'double' node, and returns the KV
	 * pair.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair.
	 * @param  value
	 *           the value of the 'double' node.
	 * @return a key&ndash;value pair whose key is {@code key} and whose value is a new 'double' node whose value is
	 *         {@code value}.
	 */

	public static Pair pair(
		String	key,
		double	value)
	{
		return new Pair(key, new DoubleNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@linkplain StringNode string node} for the specified value, creates a {@linkplain Pair key&ndash;value
	 * pair} whose key is the specified key and whose value is the string node, and returns the KV pair.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair.
	 * @param  value
	 *           the value of the string node.
	 * @return a key&ndash;value pair whose key is {@code key} and whose value is a new string node whose value is
	 *         {@code value}.
	 */

	public static Pair pair(
		String	key,
		String	value)
	{
		return new Pair(key, new StringNode(value));
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the specified key for a map node.
	 *
	 * @param  key
	 *           the key whose string representation is desired.
	 * @return the string representation of {@code key}.
	 */

	public static String keyToString(
		CharSequence	key)
	{
		return keyToString(key, true);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the specified key for a map node.
	 *
	 * @param  key
	 *           the key whose string representation is desired.
	 * @param  printableAsciiOnly
	 *           if {@code true}, the characters of {@code key} will be escaped where necessary so that the returned
	 *           string contains only printable characters from the US-ASCII character encoding (ie, characters in the
	 *           range U+0020 to U+007E inclusive).
	 * @return the string representation of {@code key}.
	 */

	public static String keyToString(
		CharSequence	key,
		boolean			printableAsciiOnly)
	{
		return StringNode.escapeAndQuote(key, printableAsciiOnly);
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
	 * For a map node, this method always returns {@code true}.
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
	 * Returns a list of the values of the key&ndash;value pairs of this map node.  The list may be modified without
	 * affecting this map node, but modifying the elements of the list (for example, changing the parent of a node)
	 * <i>will</i> affect this map node.
	 *
	 * @return a list of the values of the key&ndash;value pairs of this map node.
	 * @see    #getKeys()
	 * @see    #getPairs()
	 * @see    #getPairList()
	 */

	@Override
	public List<AbstractNode> getChildren()
	{
		return new ArrayList<>(pairs.values());
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified object is an instance of {@code MapNode} <i>and</i> this map node contains
	 * the same number of key&ndash;value pairs as the other map node <i>and</i> for each KV pair in this map node,
	 * <i>kv1</i>, there is a KV pair in the other map node, <i>kv2</i>, for which the keys of <i>kv1</i> and
	 * <i>kv2</i> are equal and the values of <i>kv1</i> and <i>kv2</i> are equal.
	 *
	 * @param  obj
	 *           the object with which this map node will be compared.
	 * @return {@code true} if {@code obj} is an instance of {@code MapNode} <i>and</i> this map node contains the same
	 *         number of key&ndash;value pairs as the other map node <i>and</i> for each KV pair in this map node,
	 *         <i>kv1</i>, there is a KV pair in the other map node, <i>kv2</i>, for which the keys of <i>kv1</i> and
	 *         <i>kv2</i> are equal and the values of <i>kv1</i> and <i>kv2</i> are equal; {@code false} otherwise.
	 */

	@Override
	public boolean equals(
		Object	obj)
	{
		if (this == obj)
			return true;

		return (obj instanceof MapNode other) && (pairs.size() == other.pairs.size()) && pairs.equals(other.pairs);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the hash code of this map node, which is the hash code of its key&ndash;value pairs.
	 *
	 * @return the hash code of this map node.
	 */

	@Override
	public int hashCode()
	{
		return pairs.hashCode();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a deep copy of this map node that has no parent.
	 *
	 * @return a deep copy of this map node that has no parent.
	 */

	@Override
	public MapNode clone()
	{
		// Create copy of this map node
		MapNode copy = (MapNode)super.clone();

		// Copy KV pairs
		copy.pairs = new LinkedHashMap<>();
		for (Map.Entry<String, AbstractNode> pair : pairs.entrySet())
		{
			AbstractNode value = pair.getValue().clone();
			copy.pairs.put(pair.getKey(), value);
			value.setParent(copy);
		}

		// Return copy
		return copy;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of this map node.
	 *
	 * @return a string representation of this map node.
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
		buffer.append(' ');
		Iterator<Map.Entry<String, AbstractNode>> it = pairs.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<String, AbstractNode> pair = it.next();
			buffer.append(keyToString(pair.getKey(), printableAsciiOnly));
			buffer.append(KEY_VALUE_SEPARATOR_CHAR);
			buffer.append(' ');
			buffer.append(pair.getValue().toString(printableAsciiOnly));
			if (it.hasNext())
				buffer.append(PAIR_SEPARATOR_CHAR);
			buffer.append(' ');
		}
		buffer.append(END_CHAR);
		return buffer.toString();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if this map node contains no key&ndash;value pairs.
	 *
	 * @return {@code true} if this map node contains no key&ndash;value pairs; {@code false} otherwise.
	 */

	public boolean isEmpty()
	{
		return pairs.isEmpty();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of key&ndash;value pairs that this map node contains.
	 *
	 * @return the number of key&ndash;value pairs that this map node contains.
	 */

	public int getNumPairs()
	{
		return pairs.size();
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair with the specified key.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key}; {@code false}
	 *         otherwise.
	 */

	public boolean hasKey(
		String	key)
	{
		return pairs.containsKey(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair whose key is the specified key and whose
	 * value is a {@linkplain NullNode null node}.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key} and whose value is
	 *         a {@linkplain NullNode null node}; {@code false} otherwise.
	 */

	public boolean hasNull(
		String	key)
	{
		return pairs.get(key) instanceof NullNode;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair whose key is the specified key and whose
	 * value is a {@linkplain BooleanNode Boolean node}.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key} and whose value is
	 *         a {@linkplain BooleanNode Boolean node}; {@code false} otherwise.
	 */

	public boolean hasBoolean(
		String	key)
	{
		return pairs.get(key) instanceof BooleanNode;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair whose key is the specified key and whose
	 * value is an {@linkplain IntNode 'int' node}.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key} and whose value is
	 *         an {@linkplain IntNode 'int' node}; {@code false} otherwise.
	 */

	public boolean hasInt(
		String	key)
	{
		return pairs.get(key) instanceof IntNode;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair whose key is the specified key and whose
	 * value is a {@linkplain LongNode 'long' node}.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key} and whose value is
	 *         a {@linkplain LongNode 'long' node}; {@code false} otherwise.
	 */

	public boolean hasLong(
		String	key)
	{
		return pairs.get(key) instanceof LongNode;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair whose key is the specified key and whose
	 * value is either an {@linkplain IntNode 'int' node} or a {@linkplain LongNode 'long' node}.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key} and whose value is
	 *         either an {@linkplain IntNode 'int' node} or a {@linkplain LongNode 'long' node}; {@code false}
	 *         otherwise.
	 */

	public boolean hasIntOrLong(
		String	key)
	{
		AbstractNode value = pairs.get(key);
		return (value instanceof IntNode) || (value instanceof LongNode);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair whose key is the specified key and whose
	 * value is a {@linkplain DoubleNode 'double' node}.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key} and whose value is
	 *         a {@linkplain DoubleNode 'double' node}; {@code false} otherwise.
	 */

	public boolean hasDouble(
		String	key)
	{
		return pairs.get(key) instanceof DoubleNode;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair whose key is the specified key and whose
	 * value is a {@linkplain StringNode string node}.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key} and whose value is
	 *         a {@linkplain StringNode string node}; {@code false} otherwise.
	 */

	public boolean hasString(
		String	key)
	{
		return pairs.get(key) instanceof StringNode;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair whose key is the specified key and whose
	 * value is a {@linkplain ListNode list node}.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key} and whose value is
	 *         a {@linkplain ListNode list node}; {@code false} otherwise.
	 */

	public boolean hasList(
		String	key)
	{
		return pairs.get(key) instanceof ListNode;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this map node contains a key&ndash;value pair whose key is the specified key and whose
	 * value is a {@linkplain MapNode map node}.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair of interest.
	 * @return {@code true} if this map node contains a key&ndash;value pair whose key is {@code key} and whose value is
	 *         a {@linkplain MapNode map node}; {@code false} otherwise.
	 */

	public boolean hasMap(
		String	key)
	{
		return pairs.get(key) instanceof MapNode;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of the key&ndash;value pair of this map node with the specified key.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose value is desired.
	 * @return the value of the key&ndash;value pair of this map node whose key is {@code key}, or {@code null} if this
	 *         map node does not contain a KV pair with such a key.
	 */

	public AbstractNode get(
		String	key)
	{
		return pairs.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain NullNode null node} that is associated with the specified key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose value is desired.
	 * @return the null node that is associated with {@code key} in this map node, or {@code null} if this map node does
	 *         not contain a KV pair with such a key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           NullNode}.
	 */

	public NullNode getNullNode(
		String	key)
	{
		return (NullNode)pairs.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain BooleanNode Boolean node} that is associated with the specified key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose value is desired.
	 * @return the Boolean node that is associated with {@code key} in this map node, or {@code null} if this map node
	 *         does not contain a KV pair with such a key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           BooleanNode}.
	 */

	public BooleanNode getBooleanNode(
		String	key)
	{
		return (BooleanNode)pairs.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain BooleanNode Boolean node} that is associated with the specified
	 * key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @return the underlying value of the Boolean node that is associated with {@code key} in this map node.
	 * @throws NullPointerException
	 *           if this map node does not contain a KV pair with the specified key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           BooleanNode}.
	 */

	public boolean getBoolean(
		String	key)
	{
		return ((BooleanNode)pairs.get(key)).getValue();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain BooleanNode Boolean node} that is associated with the specified
	 * key in this map node.  If this map node does not contain a key&ndash;value pair with such a key, or if the
	 * associated value is not a Boolean node, the specified default value is returned instead.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @param  defaultValue
	 *           the value that will be returned if this map node does not contain a key&ndash;value pair whose key is
	 *           {@code key} or the value of the pair is not a Boolean node.
	 * @return the underlying value of the Boolean node that is associated with {@code key} in this map node, or
	 *         {@code defaultValue} if there is no such node.
	 */

	public boolean getBoolean(
		String	key,
		boolean	defaultValue)
	{
		AbstractNode value = pairs.get(key);
		return (value instanceof BooleanNode booleanNode) ? booleanNode.getValue() : defaultValue;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain IntNode 'int' node} that is associated with the specified key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose value is desired.
	 * @return the 'int' node that is associated with {@code key} in this map node, or {@code null} if this map node
	 *         does not contain a KV pair with such a key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           IntNode}.
	 */

	public IntNode getIntNode(
		String	key)
	{
		return (IntNode)pairs.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain IntNode 'int' node} that is associated with the specified key
	 * in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @return the underlying value of the 'int' node that is associated with {@code key} in this map node.
	 * @throws NullPointerException
	 *           if this map node does not contain a KV pair with the specified key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           IntNode}.
	 */

	public int getInt(
		String	key)
	{
		return ((IntNode)pairs.get(key)).getValue();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain IntNode 'int' node} that is associated with the specified key
	 * in this map node.  If this map node does not contain a key&ndash;value pair with such a key, or if the associated
	 * value is not an 'int' node, the specified default value is returned instead.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @param  defaultValue
	 *           the value that will be returned if this map node does not contain a key&ndash;value pair whose key is
	 *           {@code key} or the value of the pair is not an 'int' node.
	 * @return the underlying value of the 'int' node that is associated with {@code key} in this map node, or
	 *         {@code defaultValue} if there is no such node.
	 */

	public int getInt(
		String	key,
		int		defaultValue)
	{
		AbstractNode value = pairs.get(key);
		return (value instanceof IntNode intNode) ? intNode.getValue() : defaultValue;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain LongNode 'long' node} that is associated with the specified key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose value is desired.
	 * @return the 'long' node that is associated with {@code key} in this map node, or {@code null} if this map node
	 *         does not contain a KV pair with such a key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           LongNode}.
	 */

	public LongNode getLongNode(
		String	key)
	{
		return (LongNode)pairs.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain LongNode 'long' node} that is associated with the specified key
	 * in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @return the underlying value of the 'long' node that is associated with {@code key} in this map node.
	 * @throws NullPointerException
	 *           if this map node does not contain a KV pair with the specified key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           LongNode}.
	 */

	public long getLong(
		String	key)
	{
		return ((LongNode)pairs.get(key)).getValue();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain LongNode 'long' node} that is associated with the specified key
	 * in this map node.  If this map node does not contain a key&ndash;value pair with such a key, or if the associated
	 * value is not a 'long' node, the specified default value is returned instead.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @param  defaultValue
	 *           the value that will be returned if this map node does not contain a key&ndash;value pair whose key is
	 *           {@code key} or the value of the pair is not a 'long' node.
	 * @return the underlying value of the 'long' node that is associated with {@code key} in this map node, or
	 *         {@code defaultValue} if there is no such node.
	 */

	public long getLong(
		String	key,
		long	defaultValue)
	{
		AbstractNode value = pairs.get(key);
		return (value instanceof LongNode longNode) ? longNode.getValue() : defaultValue;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain IntNode 'int' node} or {@linkplain LongNode 'long' node} that is
	 * associated with the specified key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @return the underlying value of the 'int' node or 'long' node that is associated with {@code key} in this map
	 *         node.
	 * @throws NullPointerException
	 *           if this map node does not contain a KV pair with the specified key.
	 * @throws NodeTypeException
	 *           if this map node contains a KV pair with the specified key and its value is neither an instance of
	 *           {@link IntNode} nor an instance of {@link LongNode}.
	 */

	public long getIntOrLong(
		String	key)
	{
		AbstractNode value = pairs.get(key);
		if (value instanceof IntNode intNode)
			return intNode.getValue();
		if (value instanceof LongNode longNode)
			return longNode.getValue();
		throw new NodeTypeException(value.getType());
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain IntNode 'int' node} or {@linkplain LongNode 'long' node} that is
	 * associated with the specified key in this map node.  If this map node does not contain a key&ndash;value pair
	 * with such a key, or if the associated value is neither an 'int' node nor a 'long' node, the specified default
	 * value is returned instead.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @param  defaultValue
	 *           the value that will be returned if this map node does not contain a key&ndash;value pair whose key is
	 *           {@code key} or the value of the pair is neither an 'int' node nor a 'long' node.
	 * @return the underlying value of the 'int' node or 'long' node that is associated with {@code key} in this map
	 *         node, or {@code defaultValue} if there is no such node.
	 */

	public long getIntOrLong(
		String	key,
		long	defaultValue)
	{
		AbstractNode value = pairs.get(key);
		if (value instanceof IntNode intNode)
			return intNode.getValue();
		if (value instanceof LongNode longNode)
			return longNode.getValue();
		return defaultValue;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain DoubleNode 'double' node} that is associated with the specified key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose value is desired.
	 * @return the 'double' node that is associated with {@code key} in this map node, or {@code null} if this map node
	 *         does not contain a KV pair with such a key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           DoubleNode}.
	 */

	public DoubleNode getDoubleNode(
		String	key)
	{
		return (DoubleNode)pairs.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain DoubleNode 'double' node} that is associated with the specified
	 * key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @return the underlying value of the 'double' node that is associated with {@code key} in this map node.
	 * @throws NullPointerException
	 *           if this map node does not contain a KV pair with the specified key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           DoubleNode}.
	 */

	public double getDouble(
		String	key)
	{
		return ((DoubleNode)pairs.get(key)).getValue();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain DoubleNode 'double' node} that is associated with the specified
	 * key in this map node.  If this map node does not contain a key&ndash;value pair with such a key, or if the
	 * associated value is not a 'double' node, the specified default value is returned instead.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @param  defaultValue
	 *           the value that will be returned if this map node does not contain a key&ndash;value pair whose key is
	 *           {@code key} or the value of the pair is not a 'double' node.
	 * @return the underlying value of the 'double' node that is associated with {@code key} in this map node, or
	 *         {@code defaultValue} if there is no such node.
	 */

	public double getDouble(
		String	key,
		double	defaultValue)
	{
		AbstractNode value = pairs.get(key);
		return (value instanceof DoubleNode doubleNode) ? doubleNode.getValue() : defaultValue;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain StringNode string node} that is associated with the specified key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose value is desired.
	 * @return the string node that is associated with {@code key} in this map node, or {@code null} if this map node
	 *         does not contain a KV pair with such a key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           StringNode}.
	 */

	public StringNode getStringNode(
		String	key)
	{
		return (StringNode)pairs.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain StringNode string node} that is associated with the specified key
	 * in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @return the underlying value of the string node that is associated with {@code key} in this map node.
	 * @throws NullPointerException
	 *           if this map node does not contain a KV pair with the specified key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           StringNode}.
	 */

	public String getString(
		String	key)
	{
		return ((StringNode)pairs.get(key)).getValue();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the underlying value of the {@linkplain StringNode string node} that is associated with the specified key
	 * in this map node.  If this map node does not contain a key&ndash;value pair with such a key, or if the associated
	 * value is not a string node, the specified default value is returned instead.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is desired.
	 * @param  defaultValue
	 *           the value that will be returned if this map node does not contain a key&ndash;value pair whose key is
	 *           {@code key} or the value of the pair is not a string node.
	 * @return the underlying value of the string node that is associated with {@code key} in this map node, or
	 *         {@code defaultValue} if there is no such node.
	 */

	public String getString(
		String	key,
		String	defaultValue)
	{
		AbstractNode value = pairs.get(key);
		return (value instanceof StringNode stringNode) ? stringNode.getValue() : defaultValue;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain ListNode list node} that is associated with the specified key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose value is desired.
	 * @return the list node that is associated with {@code key} in this map node, or {@code null} if this map node does
	 *         not contain a KV pair with such a key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           ListNode}.
	 */

	public ListNode getListNode(
		String	key)
	{
		return (ListNode)pairs.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain MapNode map node} that is associated with the specified key in this map node.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair whose value is desired.
	 * @return the map node that is associated with {@code key} in this map node, or {@code null} if this map node does
	 *         not contain a KV pair with such a key.
	 * @throws ClassCastException
	 *           if this map node contains a KV pair with the specified key and its value is not an instance of {@link
	 *           MapNode}.
	 */

	public MapNode getMapNode(
		String	key)
	{
		return (MapNode)pairs.get(key);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the constant of the specified enumeration type whose name matches the underlying value of the {@linkplain
	 * StringNode string node} that is associated with the specified key in this map node.  If this map node does not
	 * contain a key&ndash;value pair with such a key, or if the associated value is not a string node, or if the value
	 * of the string node does not match the name of any of the enumeration constants, the specified default value is
	 * returned instead.
	 *
	 * @param  <E>
	 *           the enumeration type.
	 * @param  cls
	 *           the class of the enumeration type.
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is required to match the name of an
	 *           enumeration constant of {@code cls}.
	 * @param  defaultValue
	 *           the value that will be returned if this map node does not contain a key&ndash;value pair whose key is
	 *           {@code key} or the value of the pair is not a string node or the value of the string node does not
	 *           match the name of any of the enumeration constants.
	 * @return the enumeration constant of {@code cls} whose name matches the the underlying value of the string node
	 *         that is associated with {@code key} in this map node, or {@code defaultValue} if there is no such node or
	 *         there is no matching enumeration constant.
	 */

	public <E extends Enum<E>> E getEnumValue(
		Class<E>	cls,
		String		key,
		E			defaultValue)
	{
		return getEnumValue(cls, key, E::name, defaultValue);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the constant of the specified enumeration type that, after the specified converter has been applied to
	 * it, matches the underlying value of the {@linkplain StringNode string node} that is associated with the specified
	 * key in this map node.  If this map node does not contain a key&ndash;value pair with such a key, or if the
	 * associated value is not a string node, or if the value of the string node does not match any of the converted
	 * enumeration constants, the specified default value is returned instead.
	 *
	 * @param  <E>
	 *           the enumeration type.
	 * @param  cls
	 *           the class of the enumeration type.
	 * @param  key
	 *           the key of the key&ndash;value pair whose underlying value is required to match an enumeration constant
	 *           of {@code cls} after {@code converter} has been applied to it.
	 * @param  converter
	 *           the function that converts the enumeration constants of {@code cls} to strings.
	 * @param  defaultValue
	 *           the value that will be returned if this map node does not contain a key&ndash;value pair whose key is
	 *           {@code key} or the value of the pair is not a string node or the value of the string node does not
	 *           match any of the converted enumeration constants.
	 * @return the enumeration constant of {@code cls} that, after {@code converter} has been applied to it, matches the
	 *         the underlying value of the string node that is associated with {@code key} in this map node, or
	 *         {@code defaultValue} if there is no such node or there is no matching enumeration constant.
	 */

	public <E extends Enum<E>> E getEnumValue(
		Class<E>			cls,
		String				key,
		Function<E, String>	converter,
		E					defaultValue)
	{
		if (hasString(key))
		{
			String str = getStringNode(key).getValue();
			for (E value : cls.getEnumConstants())
			{
				if (converter.apply(value).equals(str))
					return value;
			}
		}
		return defaultValue;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the keys of the key&ndash;value pairs of this map node.  The keys are in the order in which
	 * their associated KV pairs were added to this map node.  The list is independent of this map node, so it may be
	 * modified without affecting this node.
	 *
	 * @return a list of the keys of the key&ndash;value pairs of this map node.
	 * @see    #getChildren()
	 * @see    #getPairList()
	 * @see    #getPairs()
	 */

	public List<String> getKeys()
	{
		return new ArrayList<>(pairs.keySet());
	}

	//------------------------------------------------------------------

	/**
	 * Returns an unmodifiable map of the key&ndash;value pairs of this map node.  Iterating over the {@linkplain
	 * Map#entrySet() entries} of the map will traverse the KV pairs in the order in which they were added to this map
	 * node.  Although the returned map cannot be modified, the values of its KV pairs <i>can</i> be modified, and doing
	 * so (for example, changing the parent of a node) will affect this map node.
	 *
	 * @return an unmodifiable map of the key&ndash;value pairs of this map node.
	 * @see    #getPairList()
	 * @see    #getChildren()
	 * @see    #getKeys()
	 */

	public Map<String, AbstractNode> getPairs()
	{
		return Collections.unmodifiableMap(pairs);
	}

	//------------------------------------------------------------------

	/**
	 * Returns an iterator over the key&ndash;value pairs of this map node.
	 *
	 * @return an iterator over the key&ndash;value pairs of this map node.
	 */

	public Iterator<Map.Entry<String, AbstractNode>> getPairIterator()
	{
		return pairs.entrySet().iterator();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the key&ndash;value pairs of this map node.  Iterating over the list will traverse the KV pairs
	 * in the order in which they were added to this map node.  Modifying the value of a KV pair (for example, changing
	 * the parent of a node) will affect this map node.
	 *
	 * @return a list of the key&ndash;value pairs of this map node.
	 * @see    #getPairs()
	 * @see    #getChildren()
	 * @see    #getKeys()
	 */

	public List<Pair> getPairList()
	{
		List<Pair> pairs = new ArrayList<>();
		for (Map.Entry<String, AbstractNode> entry : this.pairs.entrySet())
			pairs.add(new Pair(entry.getKey(), entry.getValue()));
		return pairs;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the key that is associated with the specified value in this map node.  Values are tested for identity,
	 * not for equality; that is, two values, {@code v1} and {@code v2}, match if and only if {@code v1 == v2}.
	 *
	 * @param  value
	 *           the value whose associated key is desired.
	 * @return the key that is associated with {@code value} in this map node, or {@code null} if there is no such key.
	 */

	public String findKey(
		AbstractNode	value)
	{
		for (Map.Entry<String, AbstractNode> entry : pairs.entrySet())
		{
			if (entry.getValue() == value)
				return entry.getKey();
		}
		return null;
	}

	//------------------------------------------------------------------

	/**
	 * Removes all the key&ndash;value pairs of this map node.
	 */

	public void clear()
	{
		pairs.clear();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the key&ndash;value pairs of this map node to the specified pairs of keys and {@linkplain AbstractNode
	 * values}.
	 *
	 * @param pairs
	 *          the pairs of keys and values to which the key&ndash;value pairs of this map node will be set.
	 */

	public void setPairs(
		Map<String, AbstractNode>	pairs)
	{
		this.pairs.clear();
		addPairs(pairs);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified key&ndash;value pair to this map node.  If this map node already contains a KV pair with the
	 * key of the new pair, the new pair will replace the existing pair without affecting the order of the pairs;
	 * otherwise, the specified pair will be added to the end of the collection of pairs.
	 *
	 * @param pair
	 *          the key&ndash;value pair that will be added to this map node.
	 * @throws IllegalArgumentException
	 *          if {@code pair} is {@code null}.
	 */

	public void add(
		Pair	pair)
	{
		add(pair.key, pair.value);
	}

	//------------------------------------------------------------------

	/**
	 * Adds a key&ndash;value pair with the specified key and value to this map node.  If this map node already contains
	 * a KV pair with the specified key, the specified value will replace the value of the existing pair without
	 * affecting the order of the pairs; otherwise, a new KV pair will be added to the end of the collection of KV
	 * pairs.
	 *
	 * @param  key
	 *           the key of the key&ndash;value pair.
	 * @param  value
	 *           the value of the key&ndash;value pair.
	 * @throws IllegalArgumentException
	 *           if {@code key} is {@code null} or {@code value} is {@code null}.
	 */

	public void add(
		String			key,
		AbstractNode	value)
	{
		// Validate arguments
		if (key == null)
			throw new IllegalArgumentException(NULL_KEY_STR);
		if (value == null)
			throw new IllegalArgumentException(NULL_VALUE_STR);

		// Add pair to map
		pairs.put(key, value);

		// Set this node as parent of new value
		value.setParent(this);
	}

	//------------------------------------------------------------------

	/**
	 * <p style="margin-bottom: 0.25em;">
	 * Adds a key&ndash;value pair with the specified key and value to this map node if it does not already contain a KV
	 * pair with the specified key.
	 * </p>
	 * <ul>
	 *   <li>
	 *     If this node already contains a KV pair with the specified key, a {@link ClassCastException} is thrown if the
	 *     type of the existing value is different from the type of the specified value.  If the types of the existing
	 *     value and the specified value are the same, the existing value is returned.  This node is not modified.
	 *   </li>
	 *   <li>
	 *     If this node does not contain a KV pair with the specified key, a new KV pair will be added to the end of the
	 *     collection of KV pairs.
	 *   </li>
	 * </ul>
	 *
	 * @param  <T>
	 *           the type of the value that will be added to this map node if it does not already contain a
	 *           key&ndash;value pair with the specified key.
	 * @param  key
	 *           the key of the key&ndash;value pair.
	 * @param  value
	 *           the value of the key&ndash;value pair.
	 * @return the value that is associated with {@code key} in this node's collection of KV pairs.
	 * @throws IllegalArgumentException
	 *           if {@code key} is {@code null} or {@code value} is {@code null}.
	 * @throws ClassCastException
	 *           if this map node already contains an entry for {@code key} but the type of the associated value is
	 *           different from the type of {@code value}.
	 */

	public <T extends AbstractNode> T addIfAbsent(
		String	key,
		T		value)
	{
		// Validate arguments
		if (key == null)
			throw new IllegalArgumentException(NULL_KEY_STR);
		if (value == null)
			throw new IllegalArgumentException(NULL_VALUE_STR);

		// If it does not already contain the key, add KV pair to map; otherwise, get existing value
		@SuppressWarnings("unchecked")
		T outValue = (T)pairs.putIfAbsent(key, value);
		if (outValue == null)
			outValue = value;

		// Set this node as parent of value in map
		outValue.setParent(this);

		// Return value
		return outValue;
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified key&ndash;value pairs to the key&ndash;value pairs of this map node.  For each of the new
	 * pairs, if this node already contains a KV pair with the key of the new pair, the value of the new pair will
	 * replace the value of the existing pair without affecting the order of the pairs; otherwise, the new KV pair will
	 * be added to the end of the collection of pairs.  The KV pairs are added in the order in which they are traversed
	 * by their iterator.
	 *
	 * @param pairs
	 *          the key&ndash;value pairs that will be added to the key&ndash;value pairs of this map node.
	 */

	public void addPairs(
		Pair...	pairs)
	{
		for (Pair pair : pairs)
			add(pair);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified key&ndash;value pairs to the key&ndash;value pairs of this map node.  For each of the new
	 * pairs, if this node already contains a KV pair with the key of the new pair, the value of the new pair will
	 * replace the value of the existing pair without affecting the order of the pairs; otherwise, the new KV pair will
	 * be added to the end of the collection of pairs.  The KV pairs are added in the order in which they are traversed
	 * by their iterator.
	 *
	 * @param pairs
	 *          the key&ndash;value pairs that will be added to the key&ndash;value pairs of this map node.
	 */

	public void addPairs(
		Iterable<? extends Pair>	pairs)
	{
		for (Pair pair : pairs)
			add(pair);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified pairs of keys and {@linkplain AbstractNode values} to the key&ndash;value pairs of this map
	 * node.  For each of the new pairs, if this node already contains a KV pair with the key of the new pair, the value
	 * of the new pair will replace the value of the existing pair without affecting the order of the pairs; otherwise,
	 * the new KV pair will be added to the end of the collection of pairs.  The KV pairs are added in the order in
	 * which they are traversed by the iterator over the entries of the input map.
	 *
	 * @param pairs
	 *          the pairs of keys and values that will be added to the key&ndash;value pairs of this map node.
	 */

	public void addPairs(
		Map<String, AbstractNode>	pairs)
	{
		for (Map.Entry<String, AbstractNode> pair : pairs.entrySet())
			add(pair.getKey(), pair.getValue());
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain NullNode null node}, adds it to this map node as a key&ndash;value pair
	 * with the specified key and returns it.  If this map node already contains a KV pair with the specified key, the
	 * new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a new
	 * KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new null node will be associated.
	 * @return the null node that was created and added to this map node as a key&ndash;value pair whose key is
	 *         {@code key}.
	 */

	public NullNode addNull(
		String	key)
	{
		NullNode node = new NullNode();
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain BooleanNode Boolean node}, adds it to this map node as a key&ndash;value
	 * pair with the specified key and returns it.  If this map node already contains a KV pair with the specified key,
	 * the new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a
	 * new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new Boolean node will be associated.
	 * @param  value
	 *           the value of the new Boolean node.
	 * @return the Boolean node that was created and added to this map node as a key&ndash;value pair whose key is
	 *         {@code key}.
	 */

	public BooleanNode addBoolean(
		String	key,
		boolean	value)
	{
		BooleanNode node = new BooleanNode(value);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * BooleanNode Boolean nodes} with the specified values, adds the list node to this map node as a key&ndash;value
	 * pair with the specified key and returns it.  If this map node already contains a KV pair with the specified key,
	 * the new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a
	 * new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing Boolean nodes will be created.
	 * @return the new list node that contains the Boolean nodes that were created from {@code values} and that was
	 *         added to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addBooleans(
		String		key,
		boolean...	values)
	{
		ListNode node = new ListNode();
		node.addBooleans(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * BooleanNode Boolean nodes} with the specified values, adds the list node to this map node as a key&ndash;value
	 * pair with the specified key and returns it.  If this map node already contains a KV pair with the specified key,
	 * the new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a
	 * new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing Boolean nodes will be created.
	 * @return the new list node that contains the Boolean nodes that were created from {@code values} and that was
	 *         added to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addBooleans(
		String				key,
		Iterable<Boolean>	values)
	{
		ListNode node = new ListNode();
		node.addBooleans(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of an {@linkplain IntNode 'int' node}, adds it to this map node as a key&ndash;value pair
	 * with the specified key and returns it.  If this map node already contains a KV pair with the specified key, the
	 * new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a new
	 * KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new 'int' node will be associated.
	 * @param  value
	 *           the value of the new 'int' node.
	 * @return the 'int' node that was created and added to this map node as a key&ndash;value pair whose key is
	 *         {@code key}.
	 */

	public IntNode addInt(
		String	key,
		int		value)
	{
		IntNode node = new IntNode(value);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * IntNode 'int' nodes} with the specified values, adds the list node to this map node as a key&ndash;value pair
	 * with the specified key and returns it.  If this map node already contains a KV pair with the specified key, the
	 * new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a new
	 * KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing 'int' nodes will be created.
	 * @return the new list node that contains the 'int' nodes that were created from {@code values} and that was added
	 *         to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addInts(
		String	key,
		int...	values)
	{
		ListNode node = new ListNode();
		node.addInts(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * IntNode 'int' nodes} with the specified values, adds the list node to this map node as a key&ndash;value pair
	 * with the specified key and returns it.  If this map node already contains a KV pair with the specified key, the
	 * new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a new
	 * KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing 'int' nodes will be created.
	 * @return the new list node that contains the 'int' nodes that were created from {@code values} and that was added
	 *         to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addInts(
		String				key,
		Iterable<Integer>	values)
	{
		ListNode node = new ListNode();
		node.addInts(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain LongNode 'long' node}, adds it to this map node as a key&ndash;value
	 * pair with the specified key and returns it.  If this map node already contains a KV pair with the specified key,
	 * the new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a
	 * new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new 'long' node will be associated.
	 * @param  value
	 *           the value of the new 'long' node.
	 * @return the 'long' node that was created and added to this map node as a key&ndash;value pair whose key is
	 *         {@code key}.
	 */

	public LongNode addLong(
		String	key,
		long	value)
	{
		LongNode node = new LongNode(value);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * LongNode 'long' nodes} with the specified values, adds the list node to this map node as a key&ndash;value pair
	 * with the specified key and returns it.  If this map node already contains a KV pair with the specified key, the
	 * new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a new
	 * KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing 'long' nodes will be created.
	 * @return the new list node that contains the 'long' nodes that were created from {@code values} and that was added
	 *         to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addLongs(
		String	key,
		long...	values)
	{
		ListNode node = new ListNode();
		node.addLongs(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * LongNode 'long' nodes} with the specified values, adds the list node to this map node as a key&ndash;value pair
	 * with the specified key and returns it.  If this map node already contains a KV pair with the specified key, the
	 * new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a new
	 * KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing 'long' nodes will be created.
	 * @return the new list node that contains the 'long' nodes that were created from {@code values} and that was added
	 *         to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addLongs(
		String			key,
		Iterable<Long>	values)
	{
		ListNode node = new ListNode();
		node.addLongs(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain DoubleNode 'double' node}, adds it to this map node as a key&ndash;value
	 * pair with the specified key and returns it.  If this map node already contains a KV pair with the specified key,
	 * the new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a
	 * new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new 'double' node will be associated.
	 * @param  value
	 *           the value of the new 'double' node.
	 * @return the 'double' node that was created and added to this map node as a key&ndash;value pair whose key is
	 *         {@code key}.
	 */

	public DoubleNode addDouble(
		String	key,
		double	value)
	{
		DoubleNode node = new DoubleNode(value);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * DoubleNode 'double' nodes} with the specified values, adds the list node to this map node as a key&ndash;value
	 * pair with the specified key and returns it.  If this map node already contains a KV pair with the specified key,
	 * the new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a
	 * new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing 'double' nodes will be created.
	 * @return the new list node that contains the 'double' nodes that were created from {@code values} and that was
	 *         added to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addDoubles(
		String		key,
		double...	values)
	{
		ListNode node = new ListNode();
		node.addDoubles(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * DoubleNode 'double' nodes} with the specified values, adds the list node to this map node as a key&ndash;value
	 * pair with the specified key and returns it.  If this map node already contains a KV pair with the specified key,
	 * the new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a
	 * new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing 'double' nodes will be created.
	 * @return the new list node that contains the 'double' nodes that were created from {@code values} and that was
	 *         added to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addDoubles(
		String				key,
		Iterable<Double>	values)
	{
		ListNode node = new ListNode();
		node.addDoubles(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain StringNode string node}, adds it to this map node as a key&ndash;value
	 * pair with the specified key and returns it.  If this map node already contains a KV pair with the specified key,
	 * the new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a
	 * new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new string node will be associated.
	 * @param  value
	 *           the value of the new string node.
	 * @return the string node that was created and added to this map node as a key&ndash;value pair whose key is
	 *         {@code key}.
	 */

	public StringNode addString(
		String	key,
		String	value)
	{
		StringNode node = new StringNode(value);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * StringNode string nodes} with the specified values, adds the list node to this map node as a key&ndash;value pair
	 * with the specified key and returns it.  If this map node already contains a KV pair with the specified key, the
	 * new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a new
	 * KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing string nodes will be created.
	 * @return the new list node that contains the string nodes that were created from {@code values} and that was added
	 *         to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addStrings(
		String		key,
		String...	values)
	{
		ListNode node = new ListNode();
		node.addStrings(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} whose elements are new instances of {@linkplain
	 * StringNode string nodes} with the specified values, adds the list node to this map node as a key&ndash;value pair
	 * with the specified key and returns it.  If this map node already contains a KV pair with the specified key, the
	 * new node will replace the value of the existing pair without affecting the order of the pairs; otherwise, a new
	 * KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  values
	 *           the values for which a list node containing string nodes will be created.
	 * @return the new list node that contains the string nodes that were created from {@code values} and that was added
	 *         to this map node as a key&ndash;value pair whose key is {@code key}.
	 */

	public ListNode addStrings(
		String				key,
		Iterable<String>	values)
	{
		ListNode node = new ListNode();
		node.addStrings(values);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} that contains the specified elements, adds it to this
	 * map node as a key&ndash;value pair with the specified key and returns it.  If this map node already contains a KV
	 * pair with the specified key, the new node will replace the value of the existing pair without affecting the order
	 * of the pairs; otherwise, a new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  elements
	 *           the elements of the new list node.
	 * @return the new list node that contains {@code elements} and that was added to this map node as a key&ndash;value
	 *         pair whose key is {@code key}.
	 */

	public ListNode addList(
		String			key,
		AbstractNode...	elements)
	{
		ListNode node = new ListNode(List.of(elements));
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain ListNode list node} that contains the specified elements, adds it to this
	 * map node as a key&ndash;value pair with the specified key and returns it.  If this map node already contains a KV
	 * pair with the specified key, the new node will replace the value of the existing pair without affecting the order
	 * of the pairs; otherwise, a new KV pair will be added to the end of the collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new list node will be associated.
	 * @param  elements
	 *           the elements of the new list node.
	 * @return the new list node that contains {@code elements} and that was added to this map node as a key&ndash;value
	 *         pair whose key is {@code key}.
	 */

	public ListNode addList(
		String								key,
		Iterable<? extends AbstractNode>	elements)
	{
		ListNode node = new ListNode(elements);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain MapNode map node} that contains the specified pairs of keys and values,
	 * adds the new map node to this map node as a key&ndash;value pair with the specified key and returns it.  If this
	 * map node already contains a KV pair with the specified key, the new node will replace the value of the existing
	 * pair without affecting the order of the pairs; otherwise, a new KV pair will be added to the end of the
	 * collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new map node will be associated.
	 * @param  pairs
	 *           the key&ndash;value pairs of the new map node.
	 * @return the new map node that contains {@code pairs} and that was added to this map node as a key&ndash;value
	 *         pair whose key is {@code key}.
	 */

	public MapNode addMap(
		String	key,
		Pair...	pairs)
	{
		MapNode node = new MapNode(pairs);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain MapNode map node} that contains the specified pairs of keys and values,
	 * adds the new map node to this map node as a key&ndash;value pair with the specified key and returns it.  If this
	 * map node already contains a KV pair with the specified key, the new node will replace the value of the existing
	 * pair without affecting the order of the pairs; otherwise, a new KV pair will be added to the end of the
	 * collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new map node will be associated.
	 * @param  pairs
	 *           the key&ndash;value pairs of the new map node.
	 * @return the new map node that contains {@code pairs} and that was added to this map node as a key&ndash;value
	 *         pair whose key is {@code key}.
	 */

	public MapNode addMap(
		String						key,
		Iterable<? extends Pair>	pairs)
	{
		MapNode node = new MapNode(pairs);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a {@linkplain MapNode map node} that contains the specified pairs of keys and values,
	 * adds the new map node to this map node as a key&ndash;value pair with the specified key and returns it.  If this
	 * map node already contains a KV pair with the specified key, the new node will replace the value of the existing
	 * pair without affecting the order of the pairs; otherwise, a new KV pair will be added to the end of the
	 * collection of pairs.
	 *
	 * @param  key
	 *           the key with which the new map node will be associated.
	 * @param  pairs
	 *           the key&ndash;value pairs of the new map node.
	 * @return the new map node that contains {@code pairs} and that was added to this map node as a key&ndash;value
	 *         pair whose key is {@code key}.
	 */

	public MapNode addMap(
		String						key,
		Map<String, AbstractNode>	pairs)
	{
		MapNode node = new MapNode(pairs);
		add(key, node);
		return node;
	}

	//------------------------------------------------------------------

	/**
	 * Removes the key&ndash;value pairs with the specified keys from this map node.  For each of the specified keys, if
	 * the key is not associated with a KV pair of this map node, it is ignored.
	 *
	 * @param keys
	 *          the keys of the key&ndash;value pairs that will be removed from this map node.
	 */

	public void remove(
		String...	keys)
	{
		for (String key : keys)
			pairs.remove(key);
	}

	//------------------------------------------------------------------

	/**
	 * Removes the key&ndash;value pairs with the specified keys from this map node.  For each of the specified keys, if
	 * the key is not associated with a KV pair of this map node, it is ignored.
	 *
	 * @param keys
	 *          the keys of the key&ndash;value pairs that will be removed from this map node.
	 */

	public void remove(
		Iterable<String>	keys)
	{
		for (String key : keys)
			pairs.remove(key);
	}

	//------------------------------------------------------------------

	/**
	 * Removes from this map node the key&ndash;value pairs whose keys are accepted by the specified filter.
	 *
	 * @param keyFilter
	 *          the function that selects the keys of the key&ndash;value pairs that will be removed from this map node.
	 */

	public void remove(
		Predicate<String>	keyFilter)
	{
		for (String key : new ArrayList<>(pairs.keySet()))
		{
			if (keyFilter.test(key))
				pairs.remove(key);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Removes all key&ndash;value pairs from this map node except for those with the specified keys.
	 *
	 * @param keys
	 *          the keys of the key&ndash;value pairs that will be retained by this map node.
	 */

	public void retain(
		String...	keys)
	{
		retain(List.of(keys));
	}

	//------------------------------------------------------------------

	/**
	 * Removes all key&ndash;value pairs from this map node except for those with the specified keys.
	 *
	 * @param keys
	 *          the keys of the key&ndash;value pairs that will be retained by this map node.
	 */

	public void retain(
		Collection<String>	keys)
	{
		for (String key : new ArrayList<>(pairs.keySet()))
		{
			if (!keys.contains(key))
				pairs.remove(key);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Removes all key&ndash;value pairs from this map node except for those whose keys are accepted by the specified
	 * filter.
	 *
	 * @param keyFilter
	 *          the function that selects the keys of the key&ndash;value pairs that will be retained by this map node.
	 */

	public void retain(
		Predicate<String>	keyFilter)
	{
		for (String key : new ArrayList<>(pairs.keySet()))
		{
			if (!keyFilter.test(key))
				pairs.remove(key);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Sorts the key&ndash;value pairs of this map node into ascending order of their keys by applying the {@link
	 * String#compareTo(String)} method to the keys.
	 */

	public void sort()
	{
		sort(null);
	}

	//------------------------------------------------------------------

	/**
	 * Sorts the key&ndash;value pairs of this map node by applying the specified comparator to their keys.
	 *
	 * @param keyComparator
	 *          the comparator that will be applied to the keys of this map node to determine the order of the
	 *          key&ndash;value pairs.  If it is {@code null}, the keys will be sorted by applying the {@link
	 *          String#compareTo(String)} method to them.
	 */

	public void sort(
		Comparator<String>	keyComparator)
	{
		// Create a list of the keys
		List<String> keys = new ArrayList<>(pairs.keySet());

		// Sort the keys
		keys.sort(keyComparator);

		// Create a copy of the KV pairs
		Map<String, AbstractNode> pairsCopy = new HashMap<>(pairs);

		// Clear the map of KV pairs and copy the pairs back into it in the order of the sorted keys
		pairs.clear();
		for (String key : keys)
			pairs.put(key, pairsCopy.get(key));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: KEY-VALUE PAIR


	/**
	 * This class encapsulates a key&ndash;value pair of a {@linkplain MapNode map node}.  The key is a string and the
	 * value is a {@linkplain AbstractNode node}.
	 */

	public static class Pair
		implements Cloneable
	{

	////////////////////////////////////////////////////////////////////
	//  Class variables
	////////////////////////////////////////////////////////////////////

		/** The function that converts the key of a key&ndash;value pair to its string representation for {@link
			#toString()}. */
		private static	Function<CharSequence, String>	keyConverter	= StringNode::escapeAndQuote;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The key of this key&ndash;value pair. */
		private	String			key;

		/** The value of this key&ndash;value pair. */
		private	AbstractNode	value;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a key&ndash;value pair of a {@linkplain MapNode map node}.
		 *
		 * @param  key
		 *           the key of the key&ndash;value pair.
		 * @param  value
		 *           the value of the key&ndash;value pair.
		 * @throws IllegalArgumentException
		 *           if {@code key} is {@code null} or {@code value} is {@code null}.
		 */

		public Pair(
			String			key,
			AbstractNode	value)
		{
			// Validate arguments
			if (key == null)
				throw new IllegalArgumentException("Null key");
			if (value == null)
				throw new IllegalArgumentException("Null value");

			// Initialise instance variables
			this.key = key;
			this.value = value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Sets the function that converts the key of a key&ndash;value pair to its string representation for the {@link
		 * #toString()} method.  The default converter is the method {@link StringNode#escapeAndQuote(CharSequence)}.
		 *
		 * @param converter  the function that converts the key of a key&ndash;value pair to a string representation.
		 */

		public static void setKeyConverter(
			Function<CharSequence, String>	converter)
		{
			keyConverter = converter;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns {@code true} if the specified object is an instance of {@code Pair} <i>and</i> the keys and values of
		 * the two pairs are equal to each other.
		 *
		 * @param  obj
		 *           the object with which this KV pair will be compared.
		 * @return {@code true} if {@code obj} is an instance of {@code Pair} <i>and</i> the keys and values of the two
		 *         pairs are equal to each other; {@code false} otherwise.
		 */

		@Override
		public boolean equals(
			Object	obj)
		{
			if (this == obj)
				return true;

			return (obj instanceof Pair other) && key.equals(other.key) && value.equals(other.value);
		}

		//--------------------------------------------------------------

		/**
		 * Returns the hash code of this key&ndash;value pair.
		 *
		 * @return the hash code of this key&ndash;value pair.
		 */

		@Override
		public int hashCode()
		{
			return 31 * key.hashCode() + value.hashCode();
		}

		//--------------------------------------------------------------

		/**
		 * Creates and returns a copy of this key&ndash;value pair.
		 *
		 * @return a copy of this key&ndash;value pair.
		 */

		@Override
		public Pair clone()
		{
			try
			{
				// Create copy of this pair
				Pair copy = (Pair)super.clone();

				// Create copy of value
				copy.value = value.clone();

				// Return copy
				return copy;
			}
			catch (CloneNotSupportedException e)
			{
				throw new RuntimeException("Unexpected exception", e);
			}
		}

		//--------------------------------------------------------------

		/**
		 * Returns a string representation of this pair.
		 *
		 * @return a string representation of this pair.
		 */

		@Override
		public String toString()
		{
			return keyConverter.apply(key) + KEY_VALUE_SEPARATOR_CHAR + ' ' + value;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the key of this key&ndash;value pair.
		 *
		 * @return the key of this key&ndash;value pair.
		 */

		public String getKey()
		{
			return key;
		}

		//--------------------------------------------------------------

		/**
		 * Returns the value of this key&ndash;value pair.
		 *
		 * @return the value of this key&ndash;value pair.
		 */

		public AbstractNode getValue()
		{
			return value;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
