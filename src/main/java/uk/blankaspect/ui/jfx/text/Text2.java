/*====================================================================*\

Text2.java

Class: extended text node.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.text;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.Group;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

//----------------------------------------------------------------------


// CLASS: EXTENDED TEXT NODE


/**
 * This class extends {@link Text} by setting the {@linkplain FontSmoothingType font-smoothing type} to {@code LCD}.
 */

public class Text2
	extends Text
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an empty text node.
	 */

	public Text2()
	{
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a text node with the specified text.
	 *
	 * @param text
	 *          the text of the text node.
	 */

	public Text2(
		String	text)
	{
		// Set text
		setText(text);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a text node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text is the specified value.
	 *
	 * @param  text
	 *           the text for which a text node will be created.
	 * @return a text node whose bounds type is {@code LOGICAL_VERTICAL_CENTER} for {@code text}.
	 */

	public static Text2 createCentred(
		String	text)
	{
		Text2 textNode = new Text2(text);
		textNode.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
		return textNode;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text2} node whose text is set to the specified value, and returns the width of the
	 * {@linkplain Node#getLayoutBounds() layout bounds} of the node.
	 *
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width of {@code text}.
	 */

	public static double textWidth(
		String	text)
	{
		return textWidth(null, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text2} node whose text is set to the specified value, and returns the width of the
	 * {@linkplain Node#getLayoutBounds() layout bounds} of the node when it is rendered in the specified font.
	 *
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width of {@code text} when it is rendered in {@code font}.
	 */

	public static double textWidth(
		Font	font,
		String	text)
	{
		double width = 0.0;
		if ((text != null) && !text.isEmpty())
		{
			Text2 node = new Text2(text);
			if (font != null)
				node.setFont(font);
			width = node.getLayoutBounds().getWidth();
		}
		return width;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text2} node whose text is "M" and whose bounds type is {@link
	 * TextBoundsType#LOGICAL_VERTICAL_CENTER}, and returns the height of the {@linkplain Node#getLayoutBounds() layout
	 * bounds} of the node.
	 *
	 * @return the height of a {@link Text2} node whose text is "M".
	 */

	public static double textHeight()
	{
		return textHeight(null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text2} node whose text is "M" and whose bounds type is {@link
	 * TextBoundsType#LOGICAL_VERTICAL_CENTER}, and returns the height of the {@linkplain Node#getLayoutBounds() layout
	 * bounds} of the node when it is rendered in the specified font.
	 *
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @return the height of a {@link Text2} node whose text is "M" when it is rendered in {@code font}.
	 */

	public static double textHeight(
		Font	font)
	{
		Text2 node = Text2.createCentred("M");
		if (font != null)
			node.setFont(font);
		return node.getLayoutBounds().getHeight();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a tile (a {@link Group}) that contains a text node that is centred on a transparent
	 * rectangle.  The dimensions of the tile are the corresponding dimensions of the text node, each rounded up to the
	 * nearest integer.
	 *
	 * @param  text
	 *           the text for which a tile will be created.
	 * @return the tile that was created for {@code text}.
	 */

	public static Group createTile(
		String	text)
	{
		return createTile(text, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a tile (a {@link Group}) that contains a text node of the specified colour, centred on a
	 * transparent rectangle.  The dimensions of the tile are the corresponding dimensions of the text node, each
	 * rounded up to the nearest integer.
	 *
	 * @param  text
	 *           the text for which a tile will be created.
	 * @param  colour
	 *           the colour of the text, which may be {@code null}.
	 * @return the tile that was created for {@code text}.
	 */

	public static Group createTile(
		String	text,
		Color	colour)
	{
		// Create text node
		Text2 textNode = new Text2(text);
		textNode.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
		if (colour != null)
			textNode.setFill(colour);

		// Get dimensions of text node
		double textWidth = textNode.getLayoutBounds().getWidth();
		double textHeight = textNode.getLayoutBounds().getHeight();

		// Calculate dimensions of bounding rectangle
		double width = Math.ceil(textWidth);
		double height = Math.ceil(textHeight);

		// Centre text node in bounding rectangle
		textNode.relocate(0.5 * (width - textWidth), 0.5 * (height - textHeight));

		// Create bounding rectangle
		Rectangle rect = new Rectangle(width, height, Color.TRANSPARENT);

		// Return tile
		return new Group(rect, textNode);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the width of this text node.
	 *
	 * @return the width of this text node.
	 */

	public double getWidth()
	{
		return getLayoutBounds().getWidth();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the height of this text node.
	 *
	 * @return the height of this text node.
	 */

	public double getHeight()
	{
		return getLayoutBounds().getHeight();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
