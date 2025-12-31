/*====================================================================*\

TextUtils.java

Class: text-node-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.text;

//----------------------------------------------------------------------


// IMPORTS


import java.util.List;

import javafx.scene.Group;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

import uk.blankaspect.common.css.CssUtils;

import uk.blankaspect.ui.jfx.style.FxProperty;

//----------------------------------------------------------------------


// CLASS: TEXT-NODE-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain Text text nodes}.
 */

public class TextUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A style sheet that, when appended to a suitable selector, may be used to apply {@linkplain
		FontSmoothingType#GRAY <i>gray</i> font smoothing} to a container of {@link Text} nodes. */
	private static final	String	GRAY_FONT_SMOOTHING_STYLE_SHEET	=
			".text { " + FxProperty.FONT_SMOOTHING_TYPE.getName() + ": gray; }";

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
	 * @return the width of a {@code Text} node that was created for {@code text}.
	 */

	public static double textWidth(
		String	text)
	{
		return textWidth(null, null, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose font and text have the specified values, and returns the width of the
	 * {@linkplain Text#getLayoutBounds() layout bounds} of the node.
	 *
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width of a {@code Text} node that was created for {@code text} with the specified font.
	 */

	public static double textWidth(
		Font	font,
		String	text)
	{
		return textWidth(null, font, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type, font and text have the specified values, and returns the
	 * width of the {@linkplain Text#getLayoutBounds() layout bounds} of the node.
	 *
	 * @param  boundsType
	 *           the type of the bounds that will be set on the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width of a {@code Text} node that was created for {@code text} with the specified bounds type and
	 *         font.
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
	 * @return the width, rounded up to the nearest integer, of a {@code Text} node that was created for {@code text}.
	 */

	public static double textWidthCeil(
		String	text)
	{
		return textWidthCeil(null, null, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose font and text have the specified values, and returns the width of the
	 * {@linkplain Text#getLayoutBounds() layout bounds} of the node, rounded up to the nearest integer.
	 *
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width, rounded up to the nearest integer, of a {@code Text} node that was created for {@code text}
	 *         with the specified font.
	 */

	public static double textWidthCeil(
		Font	font,
		String	text)
	{
		return textWidthCeil(null, font, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type, font and text have the specified values, and returns the
	 * width of the {@linkplain Text#getLayoutBounds() layout bounds} of the node, rounded up to the nearest integer.
	 *
	 * @param  boundsType
	 *           the type of the bounds that will be set on the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose width is desired.
	 * @return the width, rounded up to the nearest integer, of a {@code Text} node that was created for {@code text}
	 *         with the specified bounds type and font.
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
	 * @return the height of a {@link Text} node that was created for the text "M".
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
	 * @return the height of a {@code Text} node that was created for {@code text}.
	 */

	public static double textHeight(
		String	text)
	{
		return textHeight(null, null, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER}, whose font has the specified value and whose text is "M", and returns the height of the
	 * {@linkplain Text#getLayoutBounds() layout bounds} of the node.
	 *
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @return the height of a {@code Text} node that was created for the text "M" with the specified font.
	 */

	public static double textHeight(
		Font font)
	{
		return textHeight(null, font, "M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type and font have the specified values and whose text is "M",
	 * and returns the height of the {@linkplain Text#getLayoutBounds() layout bounds} of the node.
	 *
	 * @param  boundsType
	 *           the type of the bounds that will be set on the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @return the height of a {@code Text} node that was created for the text "M" with the specified bounds type and
	 *         font.
	 */

	public static double textHeight(
		TextBoundsType	boundsType,
		Font			font)
	{
		return textHeight(boundsType, font, "M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type, font and text have the specified values, and returns the
	 * height of the {@linkplain Text#getLayoutBounds() layout bounds} of the node.
	 *
	 * @param  boundsType
	 *           the type of the bounds that will be set on the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose height is desired.
	 * @return the height of a {@code Text} node that was created for {@code text} with the specified bounds type and
	 *         font.
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
	 * @return the height, rounded up to the nearest integer, of a {@code Text} node that was created for the text "M".
	 */

	public static double textHeightCeil()
	{
		return textHeightCeil("M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text is "M", and returns the height of the {@linkplain Text#getLayoutBounds()
	 * layout bounds} of the node multiplied by the specified factor and rounded up to the nearest integer.
	 *
	 * @param  factor
	 *           the factor by which the height of the prototype text will be multiplied before rounding up.
	 * @return the height of a {@code Text} node that was created for the text "M", multiplied by {@code factor} and
	 *         rounded up to the nearest integer.
	 */

	public static double textHeightCeil(
		double	factor)
	{
		return Math.ceil(factor * textHeight("M"));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER} and whose text has the specified value, and returns the height of the {@linkplain
	 * Text#getLayoutBounds() layout bounds} of the node, rounded up to the nearest integer.
	 *
	 * @param  text
	 *           the text whose height is desired.
	 * @return the height, rounded up to the nearest integer, of a {@code Text} node that was created for {@code text}.
	 */

	public static double textHeightCeil(
		String	text)
	{
		return textHeightCeil(null, null, text);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type is {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER
	 * LOGICAL_VERTICAL_CENTER}, whose font has the specified value and whose text is "M", and returns the height of the
	 * {@linkplain Text#getLayoutBounds() layout bounds} of the node, rounded up to the nearest integer.
	 *
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @return the height, rounded up to the nearest integer, of a {@code Text} node that was created for the text "M"
	 *         with the specified font.
	 */

	public static double textHeightCeil(
		Font font)
	{
		return textHeightCeil(null, font, "M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type and font have the specified values and whose text is "M",
	 * and returns the height of the {@linkplain Text#getLayoutBounds() layout bounds} of the node, rounded up to the
	 * nearest integer.
	 *
	 * @param  boundsType
	 *           the type of the bounds that will be set on the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @return the height, rounded up to the nearest integer, of a {@code Text} node that was created for the text "M"
	 *         with the specified bounds type and font.
	 */

	public static double textHeightCeil(
		TextBoundsType	boundsType,
		Font			font)
	{
		return textHeightCeil(boundsType, font, "M");
	}

	//------------------------------------------------------------------

	/**
	 * Creates a temporary {@link Text} node whose bounds type, font and text have the specified values, and returns the
	 * height of the {@linkplain Text#getLayoutBounds() layout bounds} of the node, rounded up to the nearest integer.
	 *
	 * @param  boundsType
	 *           the type of the bounds that will be set on the temporary {@code Text} node.  If it is {@code null},
	 *           the bounds type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the temporary {@code Text} node; ignored if it is {@code null}.
	 * @param  text
	 *           the text whose height is desired.
	 * @return the height, rounded up to the nearest integer, of a {@code Text} node that was created for {@code text}
	 *         with the specified bounds type and font.
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
	 * Creates a {@link Text} node for each of the specified strings and returns the width of the widest node.  It is
	 * equivalent to calling {@link #textWidth(String)} on each string and returning the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @return the width of the widest of the {@code Text} nodes that were created for {@code strs}.
	 */

	public static double maxWidth(
		Iterable<String>	strs)
	{
		return maxWidth(null, null, strs);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node with the specified font for each of the specified strings, and returns the width of
	 * the widest node.  It is equivalent to calling {@link #textWidth(Font, String)} on each string and returning the
	 * largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @param  font
	 *           the font that will be set on the {@code Text} nodes; ignored if it is {@code null}.
	 * @return the width of the widest of the {@code Text} nodes with the specified font that were created for {@code
	 *         strs}.
	 */

	public static double maxWidth(
		Font				font,
		Iterable<String>	strs)
	{
		return maxWidth(null, font, strs);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node with the specified bounds type and font for each of the specified strings, and
	 * returns the width of the widest node.  It is equivalent to calling {@link #textWidth(TextBoundsType, Font,
	 * String)} on each string and returning the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @param  boundsType
	 *           the type of the bounds that will be set on the {@code Text} nodes.  If it is {@code null}, the bounds
	 *           type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the {@code Text} nodes; ignored if it is {@code null}.
	 * @return the width of the widest of the {@code Text} nodes with the specified bounds type and font that were
	 *         created for {@code strs}.
	 */

	public static double maxWidth(
		TextBoundsType		boundsType,
		Font				font,
		Iterable<String>	strs)
	{
		double maxWidth = 0.0;
		for (String str : strs)
		{
			double width = textWidth(boundsType, font, str);
			if (maxWidth < width)
				maxWidth = width;
		}
		return maxWidth;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node for each of the specified strings and returns the width of the widest node.  It is
	 * equivalent to calling {@link #textWidth(String)} on each string and returning the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @return the width of the widest of the {@code Text} nodes that were created for {@code strs}.
	 */

	public static double maxWidth(
		String...	strs)
	{
		return maxWidth(null, null, strs);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node with the specified font for each of the specified strings, and returns the width of
	 * the widest node.  It is equivalent to calling {@link #textWidth(Font, String)} on each string and returning the
	 * largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @param  font
	 *           the font that will be set on the {@code Text} nodes; ignored if it is {@code null}.
	 * @return the width of the widest of the {@code Text} nodes with the specified font that were created for {@code
	 *         strs}.
	 */

	public static double maxWidth(
		Font		font,
		String...	strs)
	{
		return maxWidth(null, font, strs);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node with the specified bounds type and font for each of the specified strings, and
	 * returns the width of the widest node.  It is equivalent to calling {@link #textWidth(TextBoundsType, Font,
	 * String)} on each string and returning the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @param  boundsType
	 *           the type of the bounds that will be set on the {@code Text} nodes.  If it is {@code null}, the bounds
	 *           type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the {@code Text} nodes; ignored if it is {@code null}.
	 * @return the width of the widest of the {@code Text} nodes with the specified bounds type and font that were
	 *         created for {@code strs}.
	 */

	public static double maxWidth(
		TextBoundsType	boundsType,
		Font			font,
		String...		strs)
	{
		return maxWidth(boundsType, font, List.of(strs));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node for each of the specified strings and returns the width of the widest node, rounded
	 * up to the nearest integer.  It is equivalent to calling {@link #textWidth(String)} on each string and returning
	 * the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @return the width, rounded up to the nearest integer, of the widest of the {@code Text} nodes that were created
	 *         for {@code strs}.
	 */

	public static double maxWidthCeil(
		Iterable<String>	strs)
	{
		return Math.ceil(maxWidth(null, null, strs));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node with the specified font for each of the specified strings, and returns the width of
	 * the widest node, rounded up to the nearest integer.  It is equivalent to calling {@link #textWidth(Font, String)}
	 * on each string and returning the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @param  font
	 *           the font that will be set on the {@code Text} nodes; ignored if it is {@code null}.
	 * @return the width, rounded up to the nearest integer, of the widest of the {@code Text} nodes with the specified
	 *         font that were created for {@code strs}.
	 */

	public static double maxWidthCeil(
		Font				font,
		Iterable<String>	strs)
	{
		return Math.ceil(maxWidth(null, font, strs));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node with the specified bounds type and font for each of the specified strings, and
	 * returns the width of the widest node, rounded up to the nearest integer.  It is equivalent to calling {@link
	 * #textWidth(TextBoundsType, Font, String)} on each string and returning the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @param  boundsType
	 *           the type of the bounds that will be set on the {@code Text} nodes.  If it is {@code null}, the bounds
	 *           type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the {@code Text} nodes; ignored if it is {@code null}.
	 * @return the width, rounded up to the nearest integer, of the widest of the {@code Text} nodes with the specified
	 *         bounds type and font that were created for {@code strs}.
	 */

	public static double maxWidthCeil(
		TextBoundsType		boundsType,
		Font				font,
		Iterable<String>	strs)
	{
		return Math.ceil(maxWidth(boundsType, font, strs));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node for each of the specified strings and returns the width of the widest node, rounded
	 * up to the nearest integer.  It is equivalent to calling {@link #textWidth(String)} on each string and returning
	 * the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @return the width, rounded up to the nearest integer, of the widest of the {@code Text} nodes that were created
	 *         for {@code strs}.
	 */

	public static double maxWidthCeil(
		String...	strs)
	{
		return Math.ceil(maxWidth(null, null, strs));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node with the specified font for each of the specified strings, and returns the width of
	 * the widest node, rounded up to the nearest integer.  It is equivalent to calling {@link #textWidth(Font, String)}
	 * on each string and returning the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @param  font
	 *           the font that will be set on the {@code Text} nodes; ignored if it is {@code null}.
	 * @return the width, rounded up to the nearest integer, of the widest of the {@code Text} nodes with the specified
	 *         font that were created for {@code strs}.
	 */

	public static double maxWidthCeil(
		Font		font,
		String...	strs)
	{
		return Math.ceil(maxWidth(null, font, strs));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Text} node with the specified bounds type and font for each of the specified strings, and
	 * returns the width of the widest node, rounded up to the nearest integer.  It is equivalent to calling {@link
	 * #textWidth(TextBoundsType, Font, String)} on each string and returning the largest width.
	 *
	 * @param  strs
	 *           the strings whose maximum width will be determined.
	 * @param  boundsType
	 *           the type of the bounds that will be set on the {@code Text} nodes.  If it is {@code null}, the bounds
	 *           type will be {@linkplain TextBoundsType#LOGICAL_VERTICAL_CENTER LOGICAL_VERTICAL_CENTER}.
	 * @param  font
	 *           the font that will be set on the {@code Text} nodes; ignored if it is {@code null}.
	 * @return the width, rounded up to the nearest integer, of the widest of the {@code Text} nodes with the specified
	 *         bounds type and font that were created for {@code strs}.
	 */

	public static double maxWidthCeil(
		TextBoundsType	boundsType,
		Font			font,
		String...		strs)
	{
		return Math.ceil(maxWidth(boundsType, font, strs));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a {@link Group} that contains the specified {@linkplain Text text nodes} laid out in a horizontal
	 * sequence, and returns it.
	 *
	 * @param  textNodes
	 *           the {@code Text} nodes for which a group will be created.
	 * @return a {@link Group} that contains {@code textNodes} laid out in a horizontal sequence.
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

	/**
	 * Returns a data-scheme URI that encodes a style sheet that may be used to apply {@linkplain FontSmoothingType#GRAY
	 * <i>gray</i> font smoothing} to a container of {@link Text} nodes.
	 *
	 * @param  selector
	 *           the CSS selector that will be prefixed to the style sheet.  There will be a space between the prefix
	 *           and the remainder of the selector ({@code .text}).
	 * @return a data-scheme URI that encodes a style sheet that may be used to apply <i>gray</i> font smoothing to a
	 *         container of {@code Text} nodes.
	 */

	public static String grayFontSmoothingStyleSheet(
		String	selector)
	{
		return CssUtils.styleSheetToDataUri(selector + " " + GRAY_FONT_SMOOTHING_STYLE_SHEET);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
