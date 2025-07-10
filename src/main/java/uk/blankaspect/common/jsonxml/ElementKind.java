/*====================================================================*\

ElementKind.java

Enumeration: kind of JSON-XML element.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.jsonxml;

//----------------------------------------------------------------------


// IMPORTS


import java.util.EnumSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

//----------------------------------------------------------------------


// ENUMERATION: KIND OF JSON-XML ELEMENT


/**
 * This is an enumeration of the kinds of element that may appear in a tree of JSON-XML.
 */

public enum ElementKind
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * Corresponds to a JSON null value.
	 */
	NULL,

	/**
	 * Corresponds to a JSON Boolean value.
	 */
	BOOLEAN,

	/**
	 * Corresponds to a JSON number.
	 */
	NUMBER,

	/**
	 * Corresponds to a JSON string.
	 */
	STRING,

	/**
	 * Corresponds to a JSON array.
	 */
	ARRAY,

	/**
	 * Corresponds to a JSON object.
	 */
	OBJECT;

	/** The kinds of element that correspond to simple JSON values. */
	public static final	Set<ElementKind>	SIMPLE_KINDS	= EnumSet.of(NULL, BOOLEAN, NUMBER, STRING);

	/** The kinds of element that correspond to compound JSON values. */
	public static final	Set<ElementKind>	COMPOUND_KINDS	= EnumSet.of(ARRAY, OBJECT);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The key of this kind of JSON-XML element. */
	private	String	key;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an enumeration constant for a kind of JSON-XML element.
	 */

	private ElementKind()
	{
		// Initialise instance variables
		key = name().toLowerCase();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the kind of JSON-XML element of the specified XML element.
	 *
	 * @param  element
	 *           the XML element of interest.
	 * @return the kind of JSON-XML element of {@code element}, or {@code null} if the element is not a JSON-XML
	 *         element.
	 */

	public static ElementKind of(
		Element	element)
	{
		String name = name(element);
		for (ElementKind kind : values())
		{
			if (kind.key.equals(name))
				return kind;
		}
		return null;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the kind of JSON-XML element of the specified XML element.  An exception is thrown if the element is not
	 * a JSON-XML element.
	 *
	 * @param  element
	 *           the XML element of interest.
	 * @return the kind of JSON-XML element of {@code element}.
	 * @throws UnexpectedKindException
	 *           if {@code element} is not a JSON-XML element.
	 */

	public static ElementKind ofThrow(
		Element	element)
	{
		String name = name(element);
		for (ElementKind kind : values())
		{
			if (kind.key.equals(name))
				return kind;
		}
		throw UnexpectedKindException.of(name);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified XML element matches one of the specified kinds of JSON-XML element.
	 *
	 * @param  element
	 *           the XML element of interest.
	 * @param  elementKinds
	 *           the kinds of JSON-XML element against which {@code element} will be tested.
	 * @return {@code true} if {@code element} matches one of {@code elementKinds}; {@code false} otherwise.
	 */

	public static boolean matches(
		Element			element,
		ElementKind...	elementKinds)
	{
		String name = name(element);
		for (ElementKind kind : elementKinds)
		{
			if (kind.key.equals(name))
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified XML element matches one of the specified kinds of JSON-XML element.
	 *
	 * @param  element
	 *           the XML element of interest.
	 * @param  elementKinds
	 *           the kinds of JSON-XML element against which {@code element} will be tested.
	 * @return {@code true} if {@code element} matches one of {@code elementKinds}; {@code false} otherwise.
	 */

	public static boolean matches(
		Element					element,
		Iterable<ElementKind>	elementKinds)
	{
		String name = name(element);
		for (ElementKind kind : elementKinds)
		{
			if (kind.key.equals(name))
				return true;
		}
		return false;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified XML element matches any kind of JSON-XML element.
	 *
	 * @param  element
	 *           the XML element of interest.
	 * @return {@code true} if {@code element} matches any kind of JSON-XML element; {@code false} otherwise.
	 */

	public static boolean anyMatch(
		Element	element)
	{
		return matches(element, values());
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified XML element matches a kind of JSON-XML element that corresponds to a simple
	 * JSON value (null, Boolean, number or string).
	 *
	 * @param  element
	 *           the XML element of interest.
	 * @return {@code true} if {@code element} matches a kind of JSON-XML element that corresponds to a simple JSON
	 *         value (null, Boolean, number or string); {@code false} otherwise.
	 */

	public static boolean isSimple(
		Element	element)
	{
		return matches(element, SIMPLE_KINDS);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified XML element matches a kind of JSON-XML element that corresponds to a
	 * compound JSON value (array or object).
	 *
	 * @param  element
	 *           the XML element of interest.
	 * @return {@code true} if {@code element} matches a kind of JSON-XML element that corresponds to a compound JSON
	 *         value (array or object); {@code false} otherwise.
	 */

	public static boolean isCompound(
		Element	element)
	{
		return matches(element, COMPOUND_KINDS);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the name of the specified element that is used when matching the element against a kind of JSON-XML
	 * element.  If the element has a {@linkplain Node#getLocalName() local name}, it is returned; otherwise, the
	 * {@linkplain Node#getNodeName() node name} of the element is returned.
	 *
	 * @param  element
	 *           the XML element of interest.
	 * @return the name of {@code element}.
	 */

	private static String name(
		Element	element)
	{
		String name = element.getLocalName();
		return (name == null) ? element.getNodeName() : name;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the key of this kind of JSON-XML element.
	 *
	 * @return the key of this kind of JSON-XML element.
	 */

	public String key()
	{
		return key;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this kind of JSON-XML element corresponds to a simple JSON value (null, Boolean, number
	 * or string).
	 *
	 * @return {@code true} if this kind of JSON-XML element corresponds to a simple JSON value (null, Boolean, number
	 *         or string); {@code false} otherwise.
	 */

	public boolean isSimple()
	{
		return SIMPLE_KINDS.contains(this);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this kind of JSON-XML element corresponds to a compound JSON value (array or object).
	 *
	 * @return {@code true} if this kind of JSON-XML element corresponds to a compound JSON value (array or object);
	 *         {@code false} otherwise.
	 */

	public boolean isCompound()
	{
		return COMPOUND_KINDS.contains(this);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the specified XML element matches this kind of JSON-XML element.
	 *
	 * @param  element
	 *           the XML element of interest.
	 * @return {@code true} if {@code element} matches this kind of JSON-XML element; {@code false} otherwise.
	 */

	public boolean matches(
		Element	element)
	{
		return key.equals(name(element));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a JSON-XML element of this kind using the specified facade.
	 *
	 * @param  elementFacade
	 *           the facade through which the JSON-XML element will be created.
	 * @return a new instance of a JSON-XML element whose name is derived from the key of this kind of element.
	 */

	public Element createElement(
		IElementFacade	elementFacade)
	{
		return elementFacade.createElement(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: 'UNEXPECTED KIND OF ELEMENT' EXCEPTION


	/**
	 * This class implements an unchecked exception that may be thrown if an XML element is encountered where a
	 * JSON-XML element is expected.
	 */

	public static class UnexpectedKindException
		extends RuntimeException
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of an exception with no detail message.
		 */

		public UnexpectedKindException()
		{
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new instance of an exception with the specified detail message.
		 *
		 * @param message
		 *          the detail message.
		 */

		public UnexpectedKindException(
			String	message)
		{
			// Call superclass constructor
			super(message);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates and returns a new instance of {@code UnexpectedKindException} whose detail message includes the
		 * specified name of an element of an unexpected kind.
		 *
		 * @param  name
		 *           the name of an element of an unexpected kind.
		 * @return a new instance of {@code UnexpectedKindException}.
		 */

		public static UnexpectedKindException of(
			String	name)
		{
			return new UnexpectedKindException("Unexpected kind of element: " + name);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
