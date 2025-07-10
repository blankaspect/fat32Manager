/*====================================================================*\

IElementFacade.java

Interface: simple interface for JSON-XML elements.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.jsonxml;

//----------------------------------------------------------------------


// IMPORTS


import org.w3c.dom.Element;

//----------------------------------------------------------------------


// INTERFACE: SIMPLE INTERFACE FOR JSON-XML ELEMENTS


/**
 * This interface defines methods that can be used to create JSON-XML elements and to access the attributes of JSON-XML
 * elements.
 */

public interface IElementFacade
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a JSON-XML element whose name is the specified name or is derived from it
	 * in some way (for example, by applying a namespace prefix to it).
	 *
	 * @param  name
	 *           the name from which the name of the new JSON-XML element will be derived.
	 * @return a new instance of a JSON-XML element whose name is derived from {@code name}.
	 */

	Element createElement(
		String	name);

	//------------------------------------------------------------------

	/**
	 * Returns the value of a named attribute of the specified element.  The name of the attribute is the specified name
	 * or is derived from it in some way (for example, by applying a namespace prefix to it).
	 *
	 * @param  element
	 *           the JSON-XML element whose specified attribute is of interest.
	 * @param  name
	 *           the name from which the name of the target attribute will be derived.
	 * @return the value of the attribute of {@code element} whose name is derived from {@code name}.
	 */

	String getAttribute(
		Element	element,
		String	name);

	//------------------------------------------------------------------

	/**
	 * Sets the value of a named attribute of the specified element.  The name of the attribute is the specified name or
	 * is derived from it in some way (for example, by applying a namespace prefix to it).
	 *
	 * @param element
	 *          the JSON-XML element whose specified attribute is of interest.
	 * @param name
	 *          the name from which the name of the target attribute will be derived.
	 * @param value
	 *          the value to which the target attribute will be set.
	 */

	void setAttribute(
		Element	element,
		String	name,
		String	value);

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
