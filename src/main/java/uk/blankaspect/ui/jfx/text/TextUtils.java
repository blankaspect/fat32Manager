/*====================================================================*\

TextUtils.java

Class: text-node-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.text;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.Group;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

//----------------------------------------------------------------------


// CLASS: TEXT-NODE-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain Text text nodes}.
 */

public class TextUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private TextUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the specified string is {@code null} or empty.
	 *
	 * @param  str
	 *           the string that will be tested.
	 * @return {@code true} if {@code str} is {@code null} or empty.
	 */

	public static boolean isNullOrEmpty(
		String	str)
	{
		return (str == null) || str.isEmpty();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text has the specified value, and returns the width of the {@linkplain
	 * Text#getLayoutBounds() layout bounds} of the node.
	 *
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width of {@code text}.
	 */

	public static double textWidth(
		String	text)
	{
		return textWidth(null, null, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text has the specified value, and returns the width of the {@linkplain
	 * Text#getLayoutBounds() layout bounds} of the node when it is rendered in the specified font.
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
		return textWidth(null, font, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type and text have the specified values, and returns the width
	 * of the {@linkplain Text#getLayoutBounds() layout bounds} of the node when it is rendered in the specified font.
	 *
	 * @param  boundsType
	 *           the type of the bounds that are returned by the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width of {@code text} when it is rendered in {@code font}.
	 */

	public static double textWidth(
		TextBoundsType	boundsType,
		Font			font,
		String			text)
	{
		double width = 0.0;
		if (!isNullOrEmpty(text))
		{
			Text node = new Text(text);
			node.setBoundsType((boundsType == null) ? TextBoundsType.LOGICAL_VERTICAL_CENTER : boundsType);
			if (font != null)
				node.setFont(font);
			width = node.getLayoutBounds().getWidth();
		}
		return width;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text has the specified value, and returns the width of the {@linkplain
	 * Text#getLayoutBounds() layout bounds} of the node, rounded up to the nearest integer.
	 *
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width of {@code text}, rounded up to the nearest integer.
	 */

	public static double textWidthCeil(
		String	text)
	{
		return textWidthCeil(null, null, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text has the specified value, and returns the width of the {@linkplain
	 * Text#getLayoutBounds() layout bounds} of the node when it is rendered in the specified font, rounded up to the
	 * nearest integer.
	 *
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width of {@code text} when it is rendered in {@code font}, rounded up to the nearest integer.
	 */

	public static double textWidthCeil(
		Font	font,
		String	text)
	{
		return textWidthCeil(null, font, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type and text have the specified values, and returns the width
	 * of the {@linkplain Text#getLayoutBounds() layout bounds} of the node when it is rendered in the specified font,
	 * rounded up to the nearest integer.
	 *
	 * @param  boundsType
	 *           the type of the bounds that are returned by the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width of {@code text} when it is rendered in {@code font}, rounded up to the nearest integer.
	 */

	public static double textWidthCeil(
		TextBoundsType	boundsType,
		Font			font,
		String			text)
	{
		return Math.ceil(textWidth(boundsType, font, text));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text is "M", and returns the height of the {@linkplain Text#getLayoutBounds()
	 * layout bounds} of the node.
	 *
	 * @return the height of a {@link Text} node whose text is "M".
	 */

	public static double textHeight()
	{
		return textHeight("M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text has the specified value, and returns the height of the {@linkplain
	 * Text#getLayoutBounds() layout bounds} of the node.
	 *
	 * @param  text
	 *           the text whose height is desired.
	 * @return the height of {@code text}.
	 */

	public static double textHeight(
		String	text)
	{
		return textHeight(null, null, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text is "M", and returns the height of the {@linkplain Text#getLayoutBounds()
	 * layout bounds} of the node when it is rendered in the specified font.
	 *
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @return the height of {@code text} when it is rendered in {@code font}.
	 */

	public static double textHeight(
		Font font)
	{
		return textHeight(null, font, "M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose text is "M" and whose bounds type has the specified value, and
	 * returns the height of the {@linkplain Text#getLayoutBounds() layout bounds} of the node when it is rendered in
	 * the specified font.
	 *
	 * @param  boundsType
	 *           the type of the bounds that are returned by the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @return the height of {@code text} when it is rendered in {@code font}.
	 */

	public static double textHeight(
		TextBoundsType	boundsType,
		Font			font)
	{
		return textHeight(boundsType, font, "M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose text and bounds type have the specified values, and returns the
	 * height of the {@linkplain Text#getLayoutBounds() layout bounds} of the node when it is rendered in the specified
	 * font.
	 *
	 * @param  boundsType
	 *           the type of the bounds that are returned by the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose height is desired.
	 * @return the height of {@code text} when it is rendered in {@code font}.
	 */

	public static double textHeight(
		TextBoundsType	boundsType,
		Font			font,
		String			text)
	{
		double width = 0.0;
		if (!isNullOrEmpty(text))
		{
			Text node = new Text(text);
			node.setBoundsType((boundsType == null) ? TextBoundsType.LOGICAL_VERTICAL_CENTER : boundsType);
			if (font != null)
				node.setFont(font);
			width = node.getLayoutBounds().getHeight();
		}
		return width;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text is "M", and returns the height of the {@linkplain Text#getLayoutBounds()
	 * layout bounds} of the node, rounded up to the nearest integer.
	 *
	 * @return the height of a {@link Text} node whose text is "M", rounded up to the nearest integer.
	 */

	public static double textHeightCeil()
	{
		return textHeightCeil("M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text has the specified value, and returns the height of the {@linkplain
	 * Text#getLayoutBounds() layout bounds} of the node, rounded up to the nearest integer.
	 *
	 * @param  text
	 *           the text whose height is desired.
	 * @return the height of {@code text}, rounded up to the nearest integer.
	 */

	public static double textHeightCeil(
		String	text)
	{
		return textHeightCeil(null, null, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text is "M", and returns the height of the {@linkplain Text#getLayoutBounds()
	 * layout bounds} of the node when it is rendered in the specified font, rounded up to the nearest integer.
	 *
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @return the height of {@code text} when it is rendered in {@code font}, rounded up to the nearest integer.
	 */

	public static double textHeightCeil(
		Font font)
	{
		return textHeightCeil(null, font, "M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose text is "M" and whose bounds type has the specified value, and
	 * returns the height of the {@linkplain Text#getLayoutBounds() layout bounds} of the node when it is rendered in
	 * the specified font, rounded up to the nearest integer.
	 *
	 * @param  boundsType
	 *           the type of the bounds that are returned by the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @return the height of {@code text} when it is rendered in {@code font}, rounded up to the nearest integer.
	 */

	public static double textHeightCeil(
		TextBoundsType	boundsType,
		Font			font)
	{
		return textHeightCeil(boundsType, font, "M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose text and bounds type have the specified values, and returns the
	 * height of the {@linkplain Text#getLayoutBounds() layout bounds} of the node when it is rendered in the specified
	 * font, rounded up to the nearest integer.
	 *
	 * @param  boundsType
	 *           the type of the bounds that are returned by the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary text node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose height is desired.
	 * @return the height of {@code text} when it is rendered in {@code font}, rounded up to the nearest integer.
	 */

	public static double textHeightCeil(
		TextBoundsType	boundsType,
		Font			font,
		String			text)
	{
		return Math.ceil(textHeight(boundsType, font, text));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Group} that contains the specified {@linkplain Text text nodes}, and returns it.
	 *
	 * @param  textNodes
	 *           the text nodes for which a group will be created.
	 * @return a {@link Group} that contains {@code textNodes}.
	 */

	public static Group createGroup(
		Iterable<? extends Text>	textNodes)
	{
		// Create group
		Group group = new Group();

		// Add text nodes to group
		double x = 0.0;
		for (Text text : textNodes)
		{
			// Set x coordinate of text node
			text.setLayoutX(x);

			// Add text node to group
			group.getChildren().add(text);

			// Increment x coordinate
			x += text.getLayoutBounds().getWidth();
		}

		// Return group
		return group;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
