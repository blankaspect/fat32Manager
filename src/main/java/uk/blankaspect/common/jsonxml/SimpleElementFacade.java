/*====================================================================*\

SimpleElementFacade.java

Class: implementation of a simple interface for JSON-XML elements.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.jsonxml;

//----------------------------------------------------------------------


// IMPORTS


import java.util.function.Predicate;

import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import uk.blankaspect.common.exception2.BaseException;

//----------------------------------------------------------------------


// CLASS: IMPLEMENTATION OF A SIMPLE INTERFACE FOR JSON-XML ELEMENTS


/**
 * This class provides methods that can be used to create JSON-XML elements and to access the attributes of JSON-XML
 * elements.  The JSON-XML elements that are created by this class must be associated with an {@linkplain Document XML
 * document}.  Some of the constructors of this class create an XML document for this purpose; the remaining
 * constructors expect an XML document to be supplied as an argument.
 * <p>
 * An instance of this class may be used to apply a namespace prefix to the name of a JSON-XML element and to the name
 * of an attribute of a JSON-XML element.
 * </p>
 */

public class SimpleElementFacade
	implements IElementFacade
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The required features of a DOM implementation. */
	private static final	String	XML_DOM_FEATURES	= "XML 3.0";

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FAILED_TO_GET_DOM_IMPLEMENTATION =
				"Failed to get an implementation of XML DOM Level 3.";

		String	FAILED_TO_CREATE_DOCUMENT =
				"Failed to create an XML document.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The namespace URI that is associated with JSON-XML elements and attributes. */
	private	String		namespaceUri;

	/** The namespace prefix that is applied to the names of JSON-XML elements and attributes. */
	private	String		namespacePrefix;

	/** The XML document that is associated with the elements and attributes that are created by the methods of this
		facade. */
	private	Document	document;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a facade that provides methods for creating a JSON-XML element and accessing the
	 * attributes of a JSON-XML element.  No namespace URI is associated with the JSON-XML elements and attributes.
	 * <p>
	 * The JSON-XML elements and attributes are associated with an XML document that is created by this constructor.
	 * The document element of the document has no namespace URI and the specified qualified name.
	 * </p>
	 *
	 * @param  documentElementName
	 *           the qualified name of the document element of the XML document that is created.
	 * @throws BaseException
	 *           if an error occurs when creating the XML document.
	 */

	public SimpleElementFacade(
		String	documentElementName)
		throws BaseException
	{
		// Call alternative constructor
		this(null, null, documentElementName);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a facade that provides methods for creating a JSON-XML element and accessing the
	 * attributes of a JSON-XML element.  The specified namespace URI is associated with the JSON-XML elements and
	 * attributes, and the specified namespace prefix is applied to the names of the JSON-XML elements and attributes.
	 * <p>
	 * The JSON-XML elements and attributes are associated with an XML document that is created by this constructor.
	 * The document element of the document has the specified namespace URI and the specified qualified name.
	 * </p>
	 *
	 * @param  namespaceUri
	 *           the namespace URI that will be associated with JSON-XML elements and attributes.  It will also be set
	 *           on the document element of the XML document that is created.
	 * @param  namespacePrefix
	 *           the namespace prefix that will be applied to the names of JSON-XML elements and attributes.  If the
	 *           prefix is {@code null} or empty or it contains only whitespace characters, no prefix will be applied.
	 * @param  documentElementName
	 *           the qualified name of the document element of the XML document that is created.
	 * @throws BaseException
	 *           if an error occurs when creating the XML document.
	 * @throws IllegalArgumentException
	 *         if
	 *         <ul>
	 *           <li>
	 *             {@code documentElementName} is {@code null}, or
	 *           </li>
	 *           <li>
	 *             {@code namespaceUri} is {@code null} and {@code namespacePrefix} is not {@code null} and {@code
	 *             namespacePrefix} is not empty and {@code namespacePrefix} does not contain only {@linkplain
	 *             Character#isWhitespace(int) whitespace}, or
	 *           </li>
	 *           <li>
	 *             {@code namespaceUri} is not {@code null} and any of the following is {@code true}:
	 *             <ul>
	 *               <li>
	 *                 {@code namespacePrefix} is {@code null},
	 *               </li>
	 *               <li>
	 *                 {@code namespacePrefix} is empty,
	 *               </li>
	 *               <li>
	 *                 {@code namespacePrefix} contains only {@linkplain Character#isWhitespace(int) whitespace}
	 *                 characters.
	 *               </li>
	 *             </ul>
	 *           </li>
	 *         </ul>
	 */

	public SimpleElementFacade(
		String	namespaceUri,
		String	namespacePrefix,
		String	documentElementName)
		throws BaseException
	{
		// Call alternative constructor
		this(namespaceUri, namespacePrefix, createDocument(namespaceUri, documentElementName));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a facade that provides methods for creating a JSON-XML element and accessing the
	 * attributes of a JSON-XML element.  No namespace URI is associated with the JSON-XML elements and attributes.
	 * <p>
	 * The JSON-XML elements and attributes are associated with the specified XML document.
	 * </p>
	 *
	 * @param document
	 *          the XML document with which JSON-XML elements and attributes will be associated.
	 */

	public SimpleElementFacade(
		Document	document)
	{
		// Call alternative constructor
		this(null, null, document);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a facade that provides methods for creating a JSON-XML element and accessing the
	 * attributes of a JSON-XML element.  The specified namespace URI is associated with the JSON-XML elements and
	 * attributes, and the specified namespace prefix is applied to the names of the JSON-XML elements and attributes.
	 * <p>
	 * The JSON-XML elements and attributes are associated with the specified XML document.
	 * </p>
	 *
	 * @param namespaceUri
	 *          the namespace URI that will be associated with JSON-XML elements and attributes.
	 * @param namespacePrefix
	 *          the namespace prefix that will be applied to the names of JSON-XML elements and attributes.  If the
	 *          prefix is {@code null} or empty or it contains only whitespace characters, no prefix will be applied.
	 * @param document
	 *          the XML document with which JSON-XML elements and attributes will be associated.
	 * @throws IllegalArgumentException
	 *         if
	 *         <ul>
	 *           <li>
	 *             {@code document} is {@code null}, or
	 *           </li>
	 *           <li>
	 *             {@code namespaceUri} is {@code null} and {@code namespacePrefix} is not {@code null} and {@code
	 *             namespacePrefix} is not empty and {@code namespacePrefix} does not contain only {@linkplain
	 *             Character#isWhitespace(int) whitespace}, or
	 *           </li>
	 *           <li>
	 *             {@code namespaceUri} is not {@code null} and any of the following is {@code true}:
	 *             <ul>
	 *               <li>
	 *                 {@code namespacePrefix} is {@code null},
	 *               </li>
	 *               <li>
	 *                 {@code namespacePrefix} is empty,
	 *               </li>
	 *               <li>
	 *                 {@code namespacePrefix} contains only {@linkplain Character#isWhitespace(int) whitespace}
	 *                 characters.
	 *               </li>
	 *             </ul>
	 *           </li>
	 *         </ul>
	 */

	public SimpleElementFacade(
		String		namespaceUri,
		String		namespacePrefix,
		Document	document)
	{
		// Create test for null or blank string
		Predicate<String> isNullOrBlank = str -> (str == null) || str.isBlank();

		// Validate arguments
		if ((namespaceUri == null) && !isNullOrBlank.test(namespacePrefix))
			throw new IllegalArgumentException("No namespace URI");
		if ((namespaceUri != null) && isNullOrBlank.test(namespacePrefix))
			throw new IllegalArgumentException("No namespace prefix");
		if (document == null)
			throw new IllegalArgumentException("Null document");

		// Initialise instance variables
		this.namespaceUri = namespaceUri;
		this.namespacePrefix = isNullOrBlank.test(namespacePrefix) ? null : namespacePrefix;
		this.document = document;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a XML document whose document element has the specified namespace URI and
	 * the specified qualified name.
	 *
	 * @param  namespaceUri
	 *           the namespace URI of the document element of the XML document, which may be {@code null}.
	 * @param  documentElementName
	 *           the qualified name of the document element of the XML document, which may be {@code null}.
	 * @return a new instance of a XML document.  The namespace URI of the document element is {@code namespaceUri} and
	 *         the qualified name of the document element is {@code documentElementName}.
	 * @throws BaseException
	 *           if an error occurs when creating the XML document.
	 */

	public static Document createDocument(
		String	namespaceUri,
		String	documentElementName)
		throws BaseException
	{
		// Get implementation of XML DOM
		DOMImplementation domImpl = null;
		try
		{
			domImpl = DOMImplementationRegistry.newInstance().getDOMImplementation(XML_DOM_FEATURES);
		}
		catch (Exception e)
		{
			throw new BaseException(ErrorMsg.FAILED_TO_GET_DOM_IMPLEMENTATION, e);
		}

		// Create XML document and return it
		try
		{
			return domImpl.createDocument(namespaceUri, documentElementName, null);
		}
		catch (Exception e)
		{
			throw new BaseException(ErrorMsg.FAILED_TO_CREATE_DOCUMENT, e);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : IElementFacade interface
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public Element createElement(
		String	name)
	{
		return (namespaceUri == null)
						? document.createElement(name)
						: document.createElementNS(namespaceUri, qualifiedName(name));
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public String getAttribute(
		Element	element,
		String	name)
	{
		return (namespaceUri == null)
						? element.hasAttribute(name)
								? element.getAttribute(name)
								: null
						: element.hasAttributeNS(namespaceUri, name)
								? element.getAttributeNS(namespaceUri, name)
								: null;
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void setAttribute(
		Element	element,
		String	name,
		String	value)
	{
		if (namespaceUri == null)
			element.setAttribute(name, value);
		else
			element.setAttributeNS(namespaceUri, qualifiedName(name), value);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the XML document that is associated with the elements and attributes that are created by the methods of
	 * this facade.
	 *
	 * @return the XML document that is associated with the elements and attributes that are created by the methods of
	 *         this facade.
	 */

	public Document getDocument()
	{
		return document;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a qualified name for the specified local name.  If this facade is associated with a namespace prefix, the
	 * prefix is applied to the specified name, separated by a colon; otherwise, the specified name is returned
	 * unchanged.
	 *
	 * @param  name
	 *           the local name.
	 * @return the qualified name for {@code name}.
	 */

	private String qualifiedName(
		String	name)
	{
		return (namespacePrefix == null) ? name : namespacePrefix + ":" + name;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
