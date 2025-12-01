/*====================================================================*\

JsonXmlUtils.java

Class: JSON-XML utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.jsonxml;

//----------------------------------------------------------------------


// IMPORTS


import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.blankaspect.common.basictree.NullNode;
import uk.blankaspect.common.basictree.StringNode;

import uk.blankaspect.common.json.JsonConstants;

//----------------------------------------------------------------------


// CLASS: JSON-XML UTILITY METHODS


/**
 * This class provides utility methods that are related to JSON-XML.
 */

public class JsonXmlUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Miscellaneous strings. */
	private static final	String	UNEXPECTED_EXCEPTION_STR	= "Unexpected exception";

	/** Names of JSON-XML attributes. */
	private interface AttrName
	{
		String	NAME	= "name";
		String	VALUE	= "value";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private JsonXmlUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of the <i>name</i> attribute of the specified XML element, which corresponds to the name of a
	 * member of a JSON object.
	 *
	 * @param  element
	 *           the target element.
	 * @return the value of the <i>name</i> attribute of {@code element}, or {@code null} if the element does not have
	 *         such an attribute.
	 */

	public static String getName(
		Element	element)
	{
		return getName(null, element);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of the <i>name</i> attribute of the specified XML element, which corresponds to the name of a
	 * member of a JSON object.  If an {@linkplain IElementFacade element facade} is supplied, it is used to access the
	 * attribute.  An instance of {@link SimpleElementFacade} may be used to apply a namespace prefix to the name of the
	 * attribute.
	 *
	 * @param  elementFacade
	 *           the facade through which the <i>name</i> attribute will be accessed.  If it is {@code null}, the
	 *           <i>name</i> attribute is accessed by calling {@link Element#getAttribute(String)
	 *           getAttribute(AttrName.NAME)} on {@code element}.
	 * @param  element
	 *           the target element.
	 * @return the value of the <i>name</i> attribute of {@code element}, or {@code null} if the element does not have
	 *         such an attribute.
	 */

	public static String getName(
		IElementFacade	elementFacade,
		Element			element)
	{
		return (elementFacade == null)
						? element.hasAttribute(AttrName.NAME)
								? element.getAttribute(AttrName.NAME)
								: null
						: elementFacade.getAttribute(element, AttrName.NAME);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the value of the <i>name</i> attribute of the specified XML element, which corresponds to the name of a
	 * member of a JSON object.
	 *
	 * @param element
	 *          the target element.
	 * @param name
	 *          the value to which the <i>name</i> attribute will be set.
	 */

	public static void setName(
		Element	element,
		String	name)
	{
		setName(null, element, name);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the value of the <i>name</i> attribute of the specified XML element, which corresponds to the name of a
	 * member of a JSON object.  If an {@linkplain IElementFacade element facade} is supplied, it is used to access the
	 * attribute.  An instance of {@link SimpleElementFacade} may be used to apply a namespace prefix to the name of the
	 * attribute.
	 *
	 * @param elementFacade
	 *          the facade through which the <i>name</i> attribute will be accessed.  If it is {@code null}, the
	 *          <i>name</i> attribute is accessed by calling {@link Element#setAttribute(String, String)
	 *          setAttribute(AttrName.NAME, name)} on {@code element}.
	 * @param element
	 *          the target element.
	 * @param name
	 *          the value to which the <i>name</i> attribute will be set.
	 */

	public static void setName(
		IElementFacade	elementFacade,
		Element			element,
		String			name)
	{
		if (elementFacade == null)
			element.setAttribute(AttrName.NAME, name);
		else
			elementFacade.setAttribute(element, AttrName.NAME, name);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of the <i>value</i> attribute of the specified XML element, which corresponds to a JSON value.
	 *
	 * @param  element
	 *           the target element.
	 * @return the value of the <i>value</i> attribute of {@code element}, or {@code null} if the element does not have
	 *         such an attribute.
	 */

	public static String getValue(
		Element	element)
	{
		return getValue(null, element);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of the <i>value</i> attribute of the specified XML element, which corresponds to a JSON value.
	 * If an {@linkplain IElementFacade element facade} is supplied, it is used to access the attribute.  An instance of
	 * {@link SimpleElementFacade} may be used to apply a namespace prefix to the name of the attribute.
	 *
	 * @param  elementFacade
	 *           the facade through which the <i>value</i> attribute will be accessed.  If it is {@code null}, the
	 *           <i>value</i> attribute is accessed by calling {@link Element#getAttribute(String)
	 *           getAttribute(AttrName.VALUE)} on {@code element}.
	 * @param  element
	 *           the target element.
	 * @return the value of the <i>value</i> attribute of {@code element}, or {@code null} if the element does not have
	 *         such an attribute.
	 */

	public static String getValue(
		IElementFacade	elementFacade,
		Element			element)
	{
		return (elementFacade == null)
						? element.hasAttribute(AttrName.VALUE)
								? element.getAttribute(AttrName.VALUE)
								: null
						: elementFacade.getAttribute(element, AttrName.VALUE);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the value of the <i>value</i> attribute of the specified XML element, which corresponds to a JSON value.
	 *
	 * @param element
	 *          the target element.
	 * @param value
	 *          the value to which the <i>value</i> attribute will be set.
	 */

	public static void setValue(
		Element	element,
		String	value)
	{
		setValue(null, element, value);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the value of the <i>value</i> attribute of the specified XML element, which corresponds to a JSON value.  If
	 * an {@linkplain IElementFacade element facade} is supplied, it is used to access the attribute.  An instance of
	 * {@link SimpleElementFacade} may be used to apply a namespace prefix to the name of the attribute.
	 *
	 * @param elementFacade
	 *          the facade through which the <i>value</i> attribute will be accessed.  If it is {@code null}, the
	 *          <i>value</i> attribute is accessed by calling {@link Element#setAttribute(String, String)
	 *          setAttribute(AttrName.VALUE, name)} on {@code element}.
	 * @param element
	 *          the target element.
	 * @param value
	 *          the value to which the <i>value</i> attribute will be set.
	 */

	public static void setValue(
		IElementFacade	elementFacade,
		Element			element,
		String			value)
	{
		if (elementFacade == null)
			element.setAttribute(AttrName.VALUE, value);
		else
			elementFacade.setAttribute(element, AttrName.VALUE, value);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of the <i>name</i> attribute of the specified XML element, ignoring any namespace prefix of the
	 * attribute.  The <i>name</i> attribute of a JSON-XML element corresponds to the name of a member of a JSON object.
	 *
	 * @param  element
	 *           the target element.
	 * @return the value of the <i>name</i> attribute of {@code element}, ignoring any namespace prefix of the
	 *         attribute, or {@code null} if the element does not have such an attribute.
	 */

	public static String getNameIgnoreNS(
		Element	element)
	{
		return getAttrIgnoreNS(element, AttrName.NAME);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of the <i>value</i> attribute of the specified XML element, ignoring any namespace prefix of
	 * the attribute.  The <i>value</i> attribute of a JSON-XML element corresponds to a JSON value.
	 *
	 * @param  element
	 *           the target element.
	 * @return the value of the <i>value</i> attribute of {@code element}, ignoring any namespace prefix of the
	 *         attribute, or {@code null} if the element does not have such an attribute.
	 */

	public static String getValueIgnoreNS(
		Element	element)
	{
		return getAttrIgnoreNS(element, AttrName.VALUE);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of the attribute of the specified XML element with the specified attribute name, ignoring any
	 * namespace prefix of the attribute.
	 *
	 * @param  element
	 *           the target element.
	 * @param  attrName
	 *           the name of the target attribute, without a namespace prefix.
	 * @return the value of the attribute of {@code element} that has the name {@code attrName}, ignoring any namespace
	 *         prefix of the attribute, or {@code null} if the element does not have such an attribute.
	 */

	public static String getAttrIgnoreNS(
		Element	element,
		String	attrName)
	{
		if (attrName != null)
		{
			NamedNodeMap attrs = element.getAttributes();
			int numAttrs = attrs.getLength();
			for (int i = 0; i < numAttrs; i++)
			{
				Node attr = attrs.item(i);
				if (attrName.equals((attr.getPrefix() == null) ? attr.getNodeName() : attr.getLocalName()))
					return attr.getNodeValue();
			}
		}
		return null;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a new instance of an {@linkplain IElementFacade element facade} that is associated with the owner
	 * document of the specified XML element.
	 *
	 * @param  element
	 *           the element with whose owner document the new element facade will be associated.
	 * @return a element facade that is associated with the owner document of {@code element}.
	 * @throws IllegalStateException
	 *           if {@code element} has no owner document.
	 */

	public static IElementFacade elementFacade(
		Element	element)
	{
		Document document = element.getOwnerDocument();
		if (document == null)
			throw new IllegalStateException("Element has no owner document");
		return new SimpleElementFacade(document);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the parent element of the specified element.
	 *
	 * @param  element
	 *           the target element.
	 * @return the element that is the parent of {@code element}, or {@code null} if the element does not have a parent
	 *         or its parent node is not an {@link Element}.
	 */

	public static Element parent(
		Element	element)
	{
		return (element.getParentNode() instanceof Element parent) ? parent : null;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a count of the child {@linkplain Node nodes} of the specified XML element that are JSON-XML elements.
	 * Any child node that is not a JSON-XML element is omitted from the count.
	 *
	 * @param  element
	 *           the target element.
	 * @return a count of the JSON-XML elements that are children of {@code element}.
	 */

	public static int countChildren(
		Element	element)
	{
		int count = 0;
		NodeList childNodes = element.getChildNodes();
		int numChildNodes = childNodes.getLength();
		for (int i = 0; i < numChildNodes; i++)
		{
			if ((childNodes.item(i) instanceof Element child) && ElementKind.anyMatch(child))
				++count;
		}
		return count;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a count of the descendant {@linkplain Node nodes} of the specified XML element that are JSON-XML
	 * elements.  Any descendant node that is not a JSON-XML element is omitted from the count.
	 *
	 * @param  element
	 *           the target element.
	 * @return a count of the JSON-XML elements that are descendants of {@code element}.
	 */

	public static int countDescendants(
		Element	element)
	{
		int count = 0;
		NodeList childNodes = element.getChildNodes();
		int numChildNodes = childNodes.getLength();
		for (int i = 0; i < numChildNodes; i++)
		{
			if ((childNodes.item(i) instanceof Element child) && ElementKind.anyMatch(child))
			{
				++count;
				count += countDescendants(child);
			}
		}
		return count;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the child {@linkplain Node nodes} of the specified XML element that are JSON-XML elements.  Any
	 * child node that is not a JSON-XML element is omitted from the list.
	 *
	 * @param  element
	 *           the target element.
	 * @return a modifiable list of the JSON-XML elements that are children of {@code element}.
	 */

	public static List<Element> children(
		Element	element)
	{
		List<Element> children = new ArrayList<>();
		NodeList childNodes = element.getChildNodes();
		int numChildNodes = childNodes.getLength();
		for (int i = 0; i < numChildNodes; i++)
		{
			if ((childNodes.item(i) instanceof Element child) && ElementKind.anyMatch(child))
				children.add(child);
		}
		return children;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the element at the specified index in the sequence of JSON-XML elements that are children of the
	 * specified XML element.  Any child {@linkplain Node node} of the specified element that is not a JSON-XML element
	 * is ignored.
	 *
	 * @param  element
	 *           the target element.
	 * @param  index
	 *           the index of the desired element in the sequence of JSON-XML children of {@code element}.
	 * @return the element at {@code index} in the sequence of JSON-XML elements that are children of {@code element},
	 *         or {@code null} if {@code index} is out of bounds.
	 */

	public static Element child(
		Element	element,
		int		index)
	{
		int count = 0;
		NodeList childNodes = element.getChildNodes();
		int numChildNodes = childNodes.getLength();
		for (int i = 0; i < numChildNodes; i++)
		{
			if ((childNodes.item(i) instanceof Element child) && ElementKind.anyMatch(child))
			{
				if (count == index)
					return child;
				++count;
			}
		}
		return null;
	}

	//------------------------------------------------------------------

	/**
	 * Returns an iterator over the JSON-XML elements that are children of the specified XML element.  The iterator
	 * ignores any child {@linkplain Node node} of the specified element that is not a JSON-XML element.
	 * <p>
	 * The returned iterator does not support the {@code remove} operation.  The behaviour of an iterator is undefined
	 * if the list of children of the specified element is modified structurally while an iteration is in progress.
	 * </p>
	 *
	 * @param  element
	 *           the target element.
	 * @return an iterator over the JSON-XML elements that are children of {@code element}.
	 */

	public static Iterator<Element> childIterator(
		Element	element)
	{
		return new Iterator<>()
		{
			int	index;

			@Override
			public boolean hasNext()
			{
				NodeList childNodes = element.getChildNodes();
				int numChildNodes = childNodes.getLength();
				for (int i = index; i < numChildNodes; i++)
				{
					if ((childNodes.item(i) instanceof Element child) && ElementKind.anyMatch(child))
					{
						index = i;
						return true;
					}
				}
				index = numChildNodes;
				return false;
			}

			@Override
			public Element next()
			{
				NodeList childNodes = element.getChildNodes();
				int numChildNodes = childNodes.getLength();
				for (int i = index; i < numChildNodes; i++)
				{
					if ((childNodes.item(i) instanceof Element child) && ElementKind.anyMatch(child))
					{
						index = i + 1;
						return child;
					}
				}
				index = numChildNodes;
				throw new NoSuchElementException();
			}
		};
	}

	//------------------------------------------------------------------

	/**
	 * Removes all the descendant {@linkplain Node nodes} of the specified XML element that are not JSON-XML elements.
	 *
	 * @param  element
	 *           the target element.
	 * @return a list of the non-JSON-XML descendants of {@code element} for which an error occurred when trying to
	 *         remove them from their parent.
	 */

	public static List<Node> clean(
		Element	element)
	{
		List<Node> notRemoved = new ArrayList<>();
		NodeList childNodes = element.getChildNodes();
		for (int i = childNodes.getLength() - 1; i >= 0; i--)
		{
			Node childNode = childNodes.item(i);
			if ((childNode instanceof Element child) && ElementKind.anyMatch(child))
				notRemoved.addAll(clean(child));
			else
			{
				try
				{
					element.removeChild(childNode);
				}
				catch (DOMException e)
				{
					notRemoved.add(childNode);
				}
			}
		}
		return notRemoved;
	}

	//------------------------------------------------------------------

	/**
	 * Performs a deep comparison of the specified pair of JSON-XML elements and returns {@code true} if the elements
	 * are equal to each other.  If either of the elements has a <i>name</i> attribute, the attribute is ignored when
	 * comparing the elements.
	 *
	 * @param  element1
	 *           the first element.
	 * @param  element2
	 *           the second element.
	 * @return {@code true} if {@code element1} and {@code element2} are deeply equal to each other or if both elements
	 *         are {@code null}; {@code false} otherwise.
	 * @throws ElementKind.UnexpectedKindException
	 *           if either {@code element1} or {@code element2} is not a JSON-XML element.
	 */

	public static boolean equals(
		Element	element1,
		Element	element2)
	{
		return equals(null, element1, null, element2, false);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a deep comparison of the specified pair of JSON-XML elements and returns {@code true} if the elements
	 * are equal to each other.  If either of the elements has a <i>name</i> attribute, the attribute is ignored when
	 * comparing the elements.
	 * <p>
	 * If an {@linkplain IElementFacade element facade} is supplied for an element, it will be used to access the
	 * attributes of the element and its descendants.  An instance of {@link SimpleElementFacade} may be used to apply a
	 * namespace prefix to the name of an attribute.
	 * </p>
	 *
	 * @param  elementFacade1
	 *           the facade through which the attributes of the first element and its descendants will be accessed.  If
	 *           it is {@code null}, attributes are accessed by calling {@link Element#getAttribute(String)
	 *           getAttribute(String)} on an element.
	 * @param  element1
	 *           the first element.
	 * @param  elementFacade2
	 *           the facade through which the attributes of the second element and its descendants will be accessed.  If
	 *           it is {@code null}, attributes are accessed by calling {@link Element#getAttribute(String)
	 *           getAttribute(String)} on an element.
	 * @param  element2
	 *           the second element.
	 * @return {@code true} if {@code element1} and {@code element2} are deeply equal to each other or if both elements
	 *         are {@code null}; {@code false} otherwise.
	 * @throws ElementKind.UnexpectedKindException
	 *           if either {@code element1} or {@code element2} is not a JSON-XML element.
	 */

	public static boolean equals(
		IElementFacade	elementFacade1,
		Element			element1,
		IElementFacade	elementFacade2,
		Element			element2)
	{
		return equals(elementFacade1, element1, elementFacade2, element2, false);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a deep comparison of the specified pair of JSON-XML elements and returns {@code true} if the elements
	 * are equal to each other.
	 * <p>
	 * If an {@linkplain IElementFacade element facade} is supplied for an element, it will be used to access the
	 * attributes of the element and its descendants.  An instance of {@link SimpleElementFacade} may be used to apply a
	 * namespace prefix to the name of an attribute.
	 * </p>
	 *
	 * @param  elementFacade1
	 *           the facade through which the attributes of the first element and its descendants will be accessed.  If
	 *           it is {@code null}, attributes are accessed by calling {@link Element#getAttribute(String)
	 *           getAttribute(String)} on an element.
	 * @param  element1
	 *           the first element.
	 * @param  elementFacade2
	 *           the facade through which the attributes of the second element and its descendants will be accessed.  If
	 *           it is {@code null}, attributes are accessed by calling {@link Element#getAttribute(String)
	 *           getAttribute(String)} on an element.
	 * @param  element2
	 *           the second element.
	 * @param  includeNameAttr
	 *           if {@code true}, the <i>name</i> attributes of {@code element1} and {@code element2} will be included
	 *           in the comparison of the elements.
	 * @return {@code true} if {@code element1} and {@code element2} are deeply equal to each other or if both elements
	 *         are {@code null}; {@code false} otherwise.
	 * @throws ElementKind.UnexpectedKindException
	 *           if either {@code element1} or {@code element2} is not a JSON-XML element.
	 */

	public static boolean equals(
		IElementFacade	elementFacade1,
		Element			element1,
		IElementFacade	elementFacade2,
		Element			element2,
		boolean			includeNameAttr)
	{
		// Test elements for identity
		if (element1 == element2)
			return true;

		// Test for null element
		if ((element1 == null) || (element2 == null))
			return false;

		// Compare kinds of elements, throwing an exception if either element is not a JSON-XML element
		ElementKind elementKind = ElementKind.ofThrow(element1);
		if (elementKind != ElementKind.ofThrow(element2))
			return false;

		// Compare namespace prefixes of elements
		if (!Objects.equals(element1.getPrefix(), element2.getPrefix()))
			return false;

		// Case: compound element
		if (elementKind.isCompound())
		{
			List<Element> children1 = children(element1);
			List<Element> children2 = children(element2);
			int numChildren = children1.size();
			if (numChildren != children2.size())
				return false;
			for (int i = 0; i < numChildren; i++)
			{
				if (!equals(elementFacade1, children1.get(i), elementFacade2, children2.get(i), true))
					return false;
			}
			return !includeNameAttr
					|| Objects.equals(getName(elementFacade1, element1), getName(elementFacade2, element2));
		}

		// Case: simple element
		return Objects.equals(getValue(elementFacade1, element1), getValue(elementFacade2, element2))
					&& (!includeNameAttr
							|| Objects.equals(getName(elementFacade1, element1), getName(elementFacade2, element2)));

	}

	//------------------------------------------------------------------

	/**
	 * Performs a deep comparison of the specified pair of JSON-XML elements and returns {@code true} if the elements
	 * are equal to each other.  When comparing the <i>name</i> and <i>value</i> attributes of JSON-XML elements, any
	 * namespace prefix of an attribute is ignored.  If either of the elements has a <i>name</i> attribute, the
	 * attribute is ignored when comparing the elements.
	 *
	 * @param  element1
	 *           the first element.
	 * @param  element2
	 *           the second element.
	 * @return {@code true} if {@code element1} and {@code element2} are deeply equal to each other or if both elements
	 *         are {@code null}; {@code false} otherwise.
	 * @throws ElementKind.UnexpectedKindException
	 *           if either {@code element1} or {@code element2} is not a JSON-XML element.
	 */

	public static boolean equalsIgnoreNS(
		Element	element1,
		Element	element2)
	{
		return equalsIgnoreNS(element1, element2, false);
	}

	//------------------------------------------------------------------

	/**
	 * Performs a deep comparison of the specified pair of JSON-XML elements and returns {@code true} if the elements
	 * are equal to each other.  When comparing the <i>name</i> and <i>value</i> attributes of JSON-XML elements, any
	 * namespace prefix of an attribute is ignored.
	 *
	 * @param  element1
	 *           the first element.
	 * @param  element2
	 *           the second element.
	 * @param  includeNameAttr
	 *           if {@code true}, the <i>name</i> attributes of {@code element1} and {@code element2} will be included
	 *           in the comparison of the elements.
	 * @return {@code true} if {@code element1} and {@code element2} are deeply equal to each other or if both elements
	 *         are {@code null}; {@code false} otherwise.
	 * @throws ElementKind.UnexpectedKindException
	 *           if either {@code element1} or {@code element2} is not a JSON-XML element.
	 */

	public static boolean equalsIgnoreNS(
		Element	element1,
		Element	element2,
		boolean	includeNameAttr)
	{
		// Test elements for identity
		if (element1 == element2)
			return true;

		// Test for null element
		if ((element1 == null) || (element2 == null))
			return false;

		// Compare kinds of elements, throwing an exception if either element is not a JSON-XML element
		ElementKind elementKind = ElementKind.ofThrow(element1);
		if (elementKind != ElementKind.ofThrow(element2))
			return false;

		// Case: compound element
		if (elementKind.isCompound())
		{
			List<Element> children1 = children(element1);
			List<Element> children2 = children(element2);
			int numChildren = children1.size();
			if (numChildren != children2.size())
				return false;
			for (int i = 0; i < numChildren; i++)
			{
				if (!equalsIgnoreNS(children1.get(i), children2.get(i), true))
					return false;
			}
			return !includeNameAttr
					|| Objects.equals(getNameIgnoreNS(element1), getNameIgnoreNS(element2));
		}

		// Case: simple element
		return Objects.equals(getValueIgnoreNS(element1), getValueIgnoreNS(element2))
					&& (!includeNameAttr
							|| Objects.equals(getNameIgnoreNS(element1), getNameIgnoreNS(element2)));

	}

	//------------------------------------------------------------------

	/**
	 * Returns a deep hash code for the specified XML element.
	 *
	 * @param  element
	 *           the target element.
	 * @return a hash code for {@code element}.
	 */

	public static int hashCode(
		Element	element)
	{
		return hashCode(null, element, false);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a deep hash code for the specified XML element.  If an {@linkplain IElementFacade element facade} is
	 * supplied, it will be used to access the attributes of JSON-XML elements.  An instance of {@link
	 * SimpleElementFacade} may be used to apply a namespace prefix to the name of the attribute.
	 *
	 * @param  elementFacade
	 *           the facade through which the attributes of JSON-XML elements will be accessed.  If it is {@code null},
	 *           attributes are accessed by calling {@link Element#getAttribute(String) getAttribute(String)} on an
	 *           element.
	 * @param  element
	 *           the target element.
	 * @return a hash code for {@code element}.
	 */

	public static int hashCode(
		IElementFacade	elementFacade,
		Element			element)
	{
		return hashCode(elementFacade, element, false);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a deep hash code for the specified XML element.  If an {@linkplain IElementFacade element facade} is
	 * supplied, it will be used to access the attributes of JSON-XML elements.  An instance of {@link
	 * SimpleElementFacade} may be used to apply a namespace prefix to the name of the attribute.
	 *
	 * @param  elementFacade
	 *           the facade through which the attributes of JSON-XML elements will be accessed.  If it is {@code null},
	 *           attributes are accessed by calling {@link Element#getAttribute(String) getAttribute(String)} on an
	 *           element.
	 * @param  element
	 *           the target element.
	 * @param  includeNameAttr
	 *           if {@code true}, the <i>name</i> attribute of {@code element} will be included in the calculation of
	 *           the hash code.
	 * @return a hash code for {@code element}.
	 */

	public static int hashCode(
		IElementFacade	elementFacade,
		Element			element,
		boolean			includeNameAttr)
	{
		int code = 0;
		ElementKind elementKind = ElementKind.of(element);
		if (elementKind != null)
		{
			// Initialise for kind of element
			code = elementKind.key().hashCode();

			// Case: compound element
			if (elementKind.isCompound())
			{
				for (Element child : children(element))
					code = 31 * code + hashCode(elementFacade, child, true);

				if (includeNameAttr)
					code ^= Objects.hashCode(getName(elementFacade, element));
			}

			// Case: simple element
			else
			{
				code = Objects.hashCode(getValue(elementFacade, element));
				if (includeNameAttr)
					code ^= Objects.hashCode(getName(elementFacade, element));
			}
		}
		return code;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a deep copy of the specified JSON-XML element.  The specified facades are used to create
	 * destination elements and to access attributes of source and destination elements.
	 *
	 * @param  sourceElementFacade
	 *           the facade through which the attributes of source JSON-XML elements will be accessed.  If it is {@code
	 *           null}, attributes are accessed by calling {@link Element#getAttribute(String) getAttribute(String)} on
	 *           a source element.
	 * @param  destElementFacade
	 *           the facade through which destination JSON-XML elements will be created and the attributes of
	 *           destination elements will be accessed.  It must not be {@code null}.
	 * @param  element
	 *           the element for which a copy is desired.
	 * @return a deep copy of {@code element}, if it is a JSON-XML element; otherwise, {@code null}.
	 */

	public static Element copy(
		IElementFacade	sourceElementFacade,
		IElementFacade	destElementFacade,
		Element			element)
	{
		Element copy = null;
		ElementKind elementKind = ElementKind.of(element);
		if (elementKind != null)
		{
			// Create copy of element
			copy = elementKind.createElement(destElementFacade);

			// Copy 'name' attribute
			String name = getName(sourceElementFacade, element);
			if (name != null)
				setName(destElementFacade, copy, name);

			// Case: compound element
			if (elementKind.isCompound())
			{
				// Copy descendants
				for (Element child : children(element))
					copy.appendChild(copy(sourceElementFacade, destElementFacade, child));
			}

			// Case: simple element
			else
			{
				// Copy 'value' attribute
				String value = getValue(sourceElementFacade, element);
				if (value != null)
					setValue(destElementFacade, copy, value);
			}
		}
		return copy;
	}

	//------------------------------------------------------------------

	/**
	 * Converts the tree of JSON-XML elements whose root is the specified XML element to JSON text and writes the text
	 * to the specified character stream.
	 *
	 * @param  element
	 *           the root of the tree of JSON-XML elements that will be written as JSON text.
	 * @param  printableAsciiOnly
	 *           if {@code true}, JSON string values and the names of the members of JSON objects will be escaped so
	 *           that they contain only printable characters from the US-ASCII character encoding (ie, characters in the
	 *           range U+0020 to U+007E inclusive).
	 * @param  writer
	 *           the character stream to which the JSON text will be written.
	 * @throws IOException
	 *           if an error occurs when writing the JSON text to the character stream.
	 */

	public static void writeJson(
		Element	element,
		boolean	printableAsciiOnly,
		Writer	writer)
		throws IOException
	{
		writeJson(null, element, printableAsciiOnly, writer);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the tree of JSON-XML elements whose root is the specified XML element to JSON text and writes the text
	 * to the specified character stream.
	 * <p>
	 * If an {@linkplain IElementFacade element facade} is supplied, it is used to access the attributes of the JSON-XML
	 * elements.  An instance of {@link SimpleElementFacade} may be used to apply a namespace prefix to the names of
	 * attributes.
	 * </p>
	 *
	 * @param  elementFacade
	 *           the facade through which the attributes of JSON-XML elements will be accessed.  If it is {@code null},
	 *           the attributes will be accessed directly through the elements.
	 * @param  element
	 *           the root of the tree of JSON-XML elements that will be written as JSON text.
	 * @param  printableAsciiOnly
	 *           if {@code true}, JSON string values and the names of the members of JSON objects will be escaped so
	 *           that they contain only printable characters from the US-ASCII character encoding (ie, characters in the
	 *           range U+0020 to U+007E inclusive).
	 * @param  writer
	 *           the character stream to which the JSON text will be written.
	 * @throws IOException
	 *           if an error occurs when writing the JSON text to the character stream.
	 */

	public static void writeJson(
		IElementFacade	elementFacade,
		Element			element,
		boolean			printableAsciiOnly,
		Writer			writer)
		throws IOException
	{
		ElementKind elementKind = ElementKind.of(element);
		if (elementKind != null)
		{
			switch (elementKind)
			{
				case NULL:
					writer.write(NullNode.VALUE);
					break;

				case BOOLEAN, NUMBER:
					writer.write(getValue(elementFacade, element));
					break;

				case STRING:
					writer.write(StringNode.escapeAndQuote(getValue(elementFacade, element), printableAsciiOnly));
					break;

				case ARRAY:
				{
					writer.write(JsonConstants.ARRAY_START_CHAR);
					writer.write(' ');
					Iterator<Element> it = childIterator(element);
					while (it.hasNext())
					{
						writeJson(elementFacade, it.next(), printableAsciiOnly, writer);
						if (it.hasNext())
							writer.write(JsonConstants.ARRAY_ELEMENT_SEPARATOR_CHAR);
						writer.write(' ');
					}
					writer.write(JsonConstants.ARRAY_END_CHAR);
					break;
				}

				case OBJECT:
				{
					writer.write(JsonConstants.OBJECT_START_CHAR);
					writer.write(' ');
					Iterator<Element> it = childIterator(element);
					while (it.hasNext())
					{
						Element child = it.next();
						writer.write(StringNode.escapeAndQuote(getName(elementFacade, child), printableAsciiOnly));
						writer.write(JsonConstants.OBJECT_NAME_VALUE_SEPARATOR_CHAR);
						writer.write(' ');
						writeJson(elementFacade, child, printableAsciiOnly, writer);
						if (it.hasNext())
							writer.write(JsonConstants.OBJECT_MEMBER_SEPARATOR_CHAR);
						writer.write(' ');
					}
					writer.write(JsonConstants.OBJECT_END_CHAR);
					break;
				}
			}
		}
	}

	//------------------------------------------------------------------

	/**
	 * Converts the tree of JSON-XML elements whose root is the specified XML element to JSON text and returns the text.
	 *
	 * @param  element
	 *           the root of the tree of JSON-XML elements that will be converted to JSON text.
	 * @param  printableAsciiOnly
	 *           if {@code true}, JSON string values and the names of the members of JSON objects will be escaped so
	 *           that they contain only printable characters from the US-ASCII character encoding (ie, characters in the
	 *           range U+0020 to U+007E inclusive).
	 * @return the result of converting the tree of JSON-XML elements whose root is {@code element} to JSON text.
	 */

	public static String toJsonText(
		Element	element,
		boolean	printableAsciiOnly)
	{
		return toJsonText(null, element, printableAsciiOnly);
	}

	//------------------------------------------------------------------

	/**
	 * Converts the tree of JSON-XML elements whose root is the specified XML element to JSON text and returns the text.
	 * <p>
	 * If an {@linkplain IElementFacade element facade} is supplied, it is used to access the attributes of the JSON-XML
	 * elements.  An instance of {@link SimpleElementFacade} may be used to apply a namespace prefix to the names of
	 * attributes.
	 * </p>
	 *
	 * @param  elementFacade
	 *           the facade through which the attributes of JSON-XML elements will be accessed.  If it is {@code null},
	 *           the attributes will be accessed through the elements.
	 * @param  element
	 *           the root of the tree of JSON-XML elements that will be converted to JSON text.
	 * @param  printableAsciiOnly
	 *           if {@code true}, JSON string values and the names of the members of JSON objects will be escaped so
	 *           that they contain only printable characters from the US-ASCII character encoding (ie, characters in the
	 *           range U+0020 to U+007E inclusive).
	 * @return the result of converting the tree of JSON-XML elements whose root is {@code element} to JSON text.
	 */

	public static String toJsonText(
		IElementFacade	elementFacade,
		Element			element,
		boolean			printableAsciiOnly)
	{
		try (CharArrayWriter writer = new CharArrayWriter(40 * (1 + countDescendants(element))))
		{
			writeJson(elementFacade, element, printableAsciiOnly, writer);
			return writer.toString();
		}
		catch (IOException e)
		{
			throw new RuntimeException(UNEXPECTED_EXCEPTION_STR, e);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Generates a textual representation of the tree of JSON-XML elements whose root is the specified XML element and
	 * writes the text to the specified character stream.
	 *
	 * @param  element
	 *           the root of the tree of JSON-XML elements for which a textual representation will be generated.
	 * @param  indent
	 *           the number of spaces by which the element at the root of XML tree will be indented.
	 * @param  indentIncrement
	 *           the number of spaces by which the indentation of each additional level of the XML tree below the root
	 *           will be increased.
	 * @param  writer
	 *           the character stream to which the text will be written.
	 * @throws IOException
	 *           if an error occurs when writing the text to the character stream.
	 */

	public static void writeXml(
		Element	element,
		int		indent,
		int		indentIncrement,
		Writer	writer)
		throws IOException
	{
		// Write indent
		for (int i = 0; i < indent; i++)
			writer.write(' ');

		// Write start tag
		String elementName = element.getNodeName();
		writer.write('<');
		writer.write(elementName);

		// Get attributes of element
		NamedNodeMap attrs = element.getAttributes();

		// Write attributes
		for (int i = 0; i < attrs.getLength(); i++)
		{
			// Write space before attribute
			writer.write(' ');

			// Write name of attribute
			Node node = attrs.item(i);
			writer.write(node.getNodeName());

			// Write separator between name and value
			writer.write('=');

			// Write value of attribute, quoted and escaped
			String value = node.getNodeValue();
			writer.write('"');
			for (int j = 0; j < value.length(); j++)
			{
				char ch = value.charAt(j);
				switch (ch)
				{
					case '<'  -> writer.write("&lt;");
					case '>'  -> writer.write("&gt;");
					case '\'' -> writer.write("&apos;");
					case '"'  -> writer.write("&quot;");
					case '&'  -> writer.write("&amp;");
					default   -> writer.write(ch);
				}
			}
			writer.write('"');
		}

		// Get child elements of element
		List<Element> children = new ArrayList<>();
		NodeList childNodes = element.getChildNodes();
		int numChildNodes = childNodes.getLength();
		for (int i = 0; i < numChildNodes; i++)
		{
			if (childNodes.item(i) instanceof Element child)
				children.add(child);
		}

		// If element has no children, write end of start tag and line feed ...
		if (children.isEmpty())
			writer.write("/>\n");

		// ... otherwise, write child elements and end tag
		else
		{
			// Write end of start tag and line feed
			writer.write(">\n");

			// Write child elements
			for (Element child : children)
				writeXml(child, indent + indentIncrement, indentIncrement, writer);

			// Write indent
			for (int i = 0; i < indent; i++)
				writer.write(' ');

			// Write end tag
			writer.write("</");
			writer.write(elementName);
			writer.write(">\n");
		}
	}

	//------------------------------------------------------------------

	/**
	 * Generates a textual representation of the tree of JSON-XML elements whose root is the specified XML element and
	 * returns the text.
	 *
	 * @param  element
	 *           the root of the tree of JSON-XML elements for which a textual representation will be generated.
	 * @param  indent
	 *           the number of spaces by which the element at the root of XML tree will be indented.
	 * @param  indentIncrement
	 *           the number of spaces by which the indentation of each additional level of the XML tree below the root
	 *           will be increased.
	 * @return a textual representation of the tree of JSON-XML elements whose root is {@code element}.
	 */

	public static String toXmlText(
		Element	element,
		int		indent,
		int		indentIncrement)
	{
		try (CharArrayWriter writer = new CharArrayWriter(4096))
		{
			writeXml(element, indent, indentIncrement, writer);
			return writer.toString();
		}
		catch (IOException e)
		{
			throw new RuntimeException(UNEXPECTED_EXCEPTION_STR, e);
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
