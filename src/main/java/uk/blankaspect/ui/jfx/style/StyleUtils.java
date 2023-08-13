/*====================================================================*\

StyleUtils.java

Class: utility methods for CSS styles.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.paint.Color;

import uk.blankaspect.common.tuple.StrKVPair;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS FOR CSS STYLES


/**
 * This class contains utility methods that relate to CSS styles.
 */

public class StyleUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The set of JavaFX CSS properties that are colour properties. */
	private static final	Set<FxProperty>	COLOUR_PROPERTIES	= EnumSet.of
	(
		FxProperty.BACKGROUND_COLOUR,
		FxProperty.BORDER_COLOUR,
		FxProperty.FILL,
		FxProperty.STROKE,
		FxProperty.TEXT_FILL
	);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private StyleUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the specified CSS property of the specified node to the specified value.
	 *
	 * @param node
	 *          the node whose CSS property will be set.
	 * @param name
	 *          the name of the property that will be set.
	 * @param value
	 *          the value to which the property will be set.
	 */

	public static void setProperty(
		Node	node,
		String	name,
		String	value)
	{
		setProperties(node, Collections.singletonList(StrKVPair.of(name, value)));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the specified CSS properties of the specified node to the specified values.
	 *
	 * @param node
	 *          the node whose CSS properties will be set.
	 * @param properties
	 *          the properties that will be set, and the values to which they will be set.
	 */

	public static void setProperties(
		Node			node,
		StrKVPair...	properties)
	{
		setProperties(node, Arrays.asList(properties));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the specified CSS properties of the specified node to the specified values.
	 *
	 * @param node
	 *          the node whose CSS properties will be set.
	 * @param properties
	 *          the properties that will be set, and the values to which they will be set.
	 */

	public static void setProperties(
		Node				node,
		Iterable<StrKVPair>	properties)
	{
		StringBuilder buffer = new StringBuilder(256);
		for (StrKVPair property : properties)
		{
			buffer.append(property.key());
			buffer.append(':');
			buffer.append(property.value());
			buffer.append(';');
		}
		node.setStyle(buffer.toString());
	}

	//------------------------------------------------------------------

	/**
	 * Sets the specified CSS colour property of the specified node to the specified value.
	 * <p>
	 * If the opacity of the colour is less than 1, the string representation of the colour will be in CSS RGBA
	 * format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgba(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>,</b>
	 * &lt;<i>opacity</i>&gt;<b>)</b><br>otherwise, it will be in CSS RGB format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgb(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>)</b>
	 * </p>
	 *
	 * @param node
	 *          the node whose CSS colour property will be set.
	 * @param name
	 *          the name of the property that will be set.
	 * @param colour
	 *          the value to which the property will be set.
	 */

	public static void setColour(
		Node	node,
		String	name,
		Color	colour)
	{
		setProperty(node, name, ColourUtils.colourToCssRgbaString(colour));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>-fx-fill</i> CSS property of the specified node to the specified value.
	 * <p>
	 * If the opacity of the colour is less than 1, the string representation of the colour will be in CSS RGBA
	 * format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgba(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>,</b>
	 * &lt;<i>opacity</i>&gt;<b>)</b><br>otherwise, it will be in CSS RGB format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgb(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>)</b>
	 * </p>
	 *
	 * @param node
	 *          the node whose <i>-fx-fill</i> CSS property will be set.
	 * @param colour
	 *          the value to which the property will be set.
	 */

	public static void setFillColour(
		Node	node,
		Color	colour)
	{
		setProperty(node, FxProperty.FILL.getName(), ColourUtils.colourToCssRgbaString(colour));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>-fx-text-fill</i> CSS property of the specified node to the specified value.
	 * <p>
	 * If the opacity of the colour is less than 1, the string representation of the colour will be in CSS RGBA
	 * format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgba(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>,</b>
	 * &lt;<i>opacity</i>&gt;<b>)</b><br>otherwise, it will be in CSS RGB format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgb(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>)</b>
	 * </p>
	 *
	 * @param node
	 *          the node whose <i>-fx-text-fill</i> CSS property will be set.
	 * @param colour
	 *          the value to which the property will be set.
	 */

	public static void setTextFillColour(
		Node	node,
		Color	colour)
	{
		setProperty(node, FxProperty.TEXT_FILL.getName(), ColourUtils.colourToCssRgbaString(colour));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>-fx-stroke</i> CSS property of the specified node to the specified value.
	 * <p>
	 * If the opacity of the colour is less than 1, the string representation of the colour will be in CSS RGBA
	 * format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgba(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>,</b>
	 * &lt;<i>opacity</i>&gt;<b>)</b><br>otherwise, it will be in CSS RGB format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgb(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>)</b>
	 * </p>
	 *
	 * @param node
	 *          the node whose <i>-fx-stroke</i> CSS property will be set.
	 * @param colour
	 *          the value to which the property will be set.
	 */

	public static void setStrokeColour(
		Node	node,
		Color	colour)
	{
		setProperty(node, FxProperty.STROKE.getName(), ColourUtils.colourToCssRgbaString(colour));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>-fx-background-color</i> CSS property of the specified node to the specified value.
	 * <p>
	 * If the opacity of the colour is less than 1, the string representation of the colour will be in CSS RGBA
	 * format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgba(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>,</b>
	 * &lt;<i>opacity</i>&gt;<b>)</b><br>otherwise, it will be in CSS RGB format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgb(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>)</b>
	 * </p>
	 *
	 * @param node
	 *          the node whose <i>-fx-background-color</i> CSS property will be set.
	 * @param colour
	 *          the value to which the property will be set.
	 */

	public static void setBackgroundColour(
		Node	node,
		Color	colour)
	{
		setProperty(node, FxProperty.BACKGROUND_COLOUR.getName(), ColourUtils.colourToCssRgbaString(colour));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>-fx-border-color</i> CSS property of the specified node to the specified value.
	 * <p>
	 * If the opacity of the colour is less than 1, the string representation of the colour will be in CSS RGBA
	 * format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgba(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>,</b>
	 * &lt;<i>opacity</i>&gt;<b>)</b><br>otherwise, it will be in CSS RGB format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgb(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>)</b>
	 * </p>
	 *
	 * @param node
	 *          the node whose <i>-fx-border-color</i> CSS property will be set.
	 * @param colour
	 *          the value to which the property will be set.
	 */

	public static void setBorderColour(
		Node	node,
		Color	colour)
	{
		setProperty(node, FxProperty.BORDER_COLOUR.getName(), ColourUtils.colourToCssRgbaString(colour));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the specified CSS colour properties of the specified node to the specified values.
	 * <p>
	 * If the opacity of the colour is less than 1, the string representation of the colour will be in CSS RGBA
	 * format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgba(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>,</b>
	 * &lt;<i>opacity</i>&gt;<b>)</b><br>otherwise, it will be in CSS RGB format:<br>
	 * &nbsp;&nbsp;&nbsp;<b>rgb(</b>&lt;<i>red</i>&gt;<b>,</b> &lt;<i>green</i>&gt;<b>,</b> &lt;<i>blue</i>&gt;<b>)</b>
	 * </p>
	 *
	 * @param  node
	 *           the node whose CSS colour properties will be set.
	 * @param  colours
	 *           a map from JavaFX CSS properties to colours.  All the keys of the map must be colour properties.
	 * @throws IllegalArgumentException
	 *           if any of the properties that are the keys of {@code colours} is not a colour property.
	 */

	public static void setColours(
		Node					node,
		Map<FxProperty, Color>	colours)
	{
		List<StrKVPair>properties = new ArrayList<>();
		for (Map.Entry<FxProperty, Color> entry : colours.entrySet())
		{
			FxProperty property = entry.getKey();
			if (!COLOUR_PROPERTIES.contains(property))
				throw new IllegalArgumentException(property + " is not a colour property");
			properties.add(StrKVPair.of(property.getName(), ColourUtils.colourToCssRgbaString(entry.getValue())));
		}

		setProperties(node, properties);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified style class to or removes the style class from the list of style classes of the specified
	 * node according to the specified flag.  If the <i>add</i> flag is set, the specified style class is not added to
	 * the node's list of style classes if the list already contains the style class.
	 * <p>
	 * This method is intended to be used in a case where alternative states of a node are indicated by the presence or
	 * absence of the target style class; in such a case, the <i>add</i> flag may be used to toggle between the states.
	 * </p>
	 *
	 * @param node
	 *          the node to or from whose list of style classes {@code styleClass} will be added or removed.
	 * @param styleClass
	 *          the style class that will be added to or removed from the list of style classes of {@code node}.
	 * @param add
	 *          if {@code true}, {@code styleClass} will be added to the list of style classes of {@code node} if the
	 *          list does not already contain the style class; if {@code false}, {@code styleClass} will be removed from
	 *          the list of style classes of {@code node} if the list contains the style class.
	 */

	public static void addRemoveStyleClass(
		Node	node,
		String	styleClass,
		boolean	add)
	{
		List<String> styleClasses = node.getStyleClass();
		if (add)
		{
			if (!styleClasses.contains(styleClass))
				styleClasses.add(styleClass);
		}
		else
			styleClasses.remove(styleClass);
	}

	//------------------------------------------------------------------

	/**
	 * Removes the first specified style class from the list of style classes of the specified node and adds the second
	 * specified style class to the node's list of style classes if the list does not already contain it.
	 *
	 * @param node
	 *          the node whose list of style classes is the target of this method.
	 * @param oldStyleClass
	 *          the style class that will be removed from the list of style classes of {@code node}.
	 * @param newStyleClass
	 *          the style class that will be added to the list of style classes of {@code node} if the list does not
	 *          already contain it.
	 */

	public static void replaceStyleClass(
		Node	node,
		String	oldStyleClass,
		String	newStyleClass)
	{
		// Get list of style classes of node
		List<String> styleClasses = node.getStyleClass();

		// Remove old style class from list
		styleClasses.remove(oldStyleClass);

		// Add new style class to list if it not already present
		if (!styleClasses.contains(newStyleClass))
			styleClasses.add(newStyleClass);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the style class of the specified subclass of {@link Node}.  The style class is the canonical name of the
	 * class with each '.' replaced by '-'.
	 *
	 * @param  cls
	 *           the class whose style class is required.
	 * @return the style class of {@code cls}.
	 */

	public static String getStyleClass(
		Class<? extends Node>	cls)
	{
		return cls.getCanonicalName().replace('.', '-');
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the tree of style classes rooted at the specified node.  Each node in the tree
	 * is represented by a line of text with the following format:
	 * <p>
	 * <code>
	 * &nbsp;&nbsp;&lt;<i>node-class</i>&gt; [<b>(#</b>&lt;<i>node-id</i>&gt;<b>)</b>]<b>:</b>
	 * <b>[</b>[&lt;<i>style-class</i>&gt;[<b>,</b> &lt;<i>style-class</i>&gt;...]]<b>]</b>
	 * </code>
	 * </p>
	 *
	 * @param  node
	 *           the node at the root of the tree.
	 * @param  indentIncrement
	 *           the number of spaces by which the indent of a line of text will be incremented for each level of the
	 *           tree below <i>node</i>.
	 * @return a string representation of the tree of style classes rooted at <i>node</i>.
	 */

	public static String getStyleClassTree(
		Node	node,
		int		indentIncrement)
	{
		StringBuilder buffer = new StringBuilder(256);
		appendStyleClassTree(buffer, node, 0, indentIncrement);
		return buffer.toString();
	}

	//------------------------------------------------------------------

	/**
	 * Appends a string representation of the tree of style classes rooted at the specified node to the specified text
	 * buffer.
	 *
	 * @param buffer
	 *          the buffer to which the string representation of the tree will be appended.
	 * @param node
	 *          the node at the root of the tree.
	 * @param indent
	 *          the number of spaces that will be prefixed to each line of text at the root level.
	 * @param indentIncrement
	 *          the number of spaces by which the indent of a line of text will be incremented for each level of the
	 *          tree below <i>node</i>.
	 */

	private static void appendStyleClassTree(
		StringBuilder	buffer,
		Node			node,
		int				indent,
		int				indentIncrement)
	{
		// Append linefeed
		if (!buffer.isEmpty())
			buffer.append('\n');

		// Append indent
		for (int i = 0; i < indent; i++)
			buffer.append(' ');

		// Append class name
		Class<? extends Node> cls = node.getClass();
		String className = cls.getSimpleName();
		if (className.isEmpty())
			className = cls.getSuperclass().getSimpleName();
		buffer.append(className);

		// Append node ID
		String id = node.getId();
		if (id != null)
		{
			buffer.append(" (#");
			buffer.append(id);
			buffer.append(')');
		}

		// Append list of style classes
		buffer.append(": [");
		Iterator<String> it = node.getStyleClass().iterator();
		while (it.hasNext())
		{
			buffer.append(it.next());
			if (it.hasNext())
				buffer.append(", ");
		}
		buffer.append(']');

		// Append style classes of child nodes
		if (node instanceof Parent parent)
		{
			for (Node child : parent.getChildrenUnmodifiable())
				appendStyleClassTree(buffer, child, indent + indentIncrement, indentIncrement);
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
