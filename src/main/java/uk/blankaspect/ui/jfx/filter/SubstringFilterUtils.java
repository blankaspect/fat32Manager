/*====================================================================*\

SubstringFilterUtils.java

Class: substring-filter-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.filter;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import java.util.regex.Matcher;

import javafx.scene.text.Font;

import uk.blankaspect.common.matcher.OrderedCharacterMatcher;
import uk.blankaspect.common.matcher.SimpleWildcardPatternMatcher;

import uk.blankaspect.ui.jfx.font.FontUtils;

import uk.blankaspect.ui.jfx.text.Text2;

//----------------------------------------------------------------------


// CLASS: SUBSTRING-FILTER-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to substring filters.
 */

public class SubstringFilterUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A decorator for a text node that sets a bold font on highlighted text.*/
	public static final		IDecorator	BOLD_DECORATOR	= (textNode, highlighted) ->
			textNode.setFont(highlighted ? FontUtils.boldFont() : Font.getDefault());

	/** The character matcher that is used by {@link #createTextNodes(SubstringFilterPane.FilterMode, String, String,
		IDecorator)}. */
	private static final	OrderedCharacterMatcher	CHARACTER_MATCHER	= new OrderedCharacterMatcher();

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private SubstringFilterUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a list of {@linkplain Text2 text nodes} for the specified text.  The text nodes correspond to
	 * substrings of the text that match the specified filter mode.
	 *
	 * @param  filterMode
	 *           the mode of the filter that will be applied to <i>text</i>.
	 * @param  filter
	 *           the filter that will be applied to <i>text</i>.
	 * @param  text
	 *           the text that will be filtered.
	 * @param  normaliser
	 *           the function that will normalise <i>text</i> before the filter is applied to it.
	 * @param  decorator
	 *           the decorator that will be applied to each text node.  If it is {@code null}, it will be ignored.
	 * @return a list of {@linkplain Text2 text nodes} that correspond to normal and highlighted substrings of
	 *         <i>text</i>.
	 */

	public static List<Text2> createTextNodes(
		SubstringFilterPane.FilterMode	filterMode,
		String							filter,
		String							text,
		INormaliser						normaliser,
		IDecorator						decorator)
	{
		// Initialise list of text nodes
		List<Text2> textNodes = null;

		// Create text nodes
		if (!filter.isEmpty())
		{
			switch (filterMode)
			{
				case FRAGMENTED:
				{
					List<BitSet> matches = CHARACTER_MATCHER
												.setSource(normaliser.normalise(text))
												.setTarget(filter)
												.findMatches(1);
					if (!matches.isEmpty())
						textNodes = createTextNodes(text, matches.get(0), decorator);
					break;
				}

				case WILDCARD_ANYWHERE:
				{
					SimpleWildcardPatternMatcher matcher = SimpleWildcardPatternMatcher.anywhereIgnoreCase(filter);
					if (matcher.match(text))
					{
						Matcher regexMatcher = matcher.getMatcher();
						BitSet bits = new BitSet();
						bits.set(regexMatcher.start(), regexMatcher.end());
						textNodes = createTextNodes(text, bits, decorator);
					}
					break;
				}

				case WILDCARD_START:
				{
					SimpleWildcardPatternMatcher matcher = SimpleWildcardPatternMatcher.startIgnoreCase(filter);
					if (matcher.match(text))
					{
						Matcher regexMatcher = matcher.getMatcher();
						BitSet bits = new BitSet();
						bits.set(regexMatcher.start(), regexMatcher.end());
						textNodes = createTextNodes(text, bits, decorator);
					}
					break;
				}

				case WILDCARD_ALL:
				{
					SimpleWildcardPatternMatcher matcher = SimpleWildcardPatternMatcher.allIgnoreCase(filter);
					if (matcher.match(text))
					{
						Matcher regexMatcher = matcher.getMatcher();
						BitSet bits = new BitSet();
						bits.set(regexMatcher.start(), regexMatcher.end());
						textNodes = createTextNodes(text, bits, decorator);
					}
					break;
				}
			}
		}

		// Return text nodes
		return textNodes;
	}

	//------------------------------------------------------------------

	/**
	 * Creates {@linkplain Text2 text nodes} for the specified text with the specified highlighted characters.
	 *
	 * @param  text
	 *           the text for which text nodes will be created.
	 * @param  flags
	 *           a bit array in which each set bit indicates that the corresponding character in <i>text</i> is
	 *           highlighted.
	 * @param  decorator
	 *           the decorator that will be applied to each text node.  If it is {@code null}, it will be ignored.
	 * @return a list of {@linkplain Text2 text nodes} for <i>text</i> and <i>flags</i>.
	 */

	public static List<Text2> createTextNodes(
		String		text,
		BitSet		flags,
		IDecorator	decorator)
	{
		List<Text2> textNodes = new ArrayList<>();
		boolean highlighted = false;
		int startIndex = 0;
		int index = 0;
		while (index < text.length())
		{
			if (flags.get(index) != highlighted)
			{
				if (index > startIndex)
				{
					Text2 textNode = Text2.createCentred(text.substring(startIndex, index));
					if (decorator != null)
						decorator.apply(textNode, highlighted);
					textNodes.add(textNode);
				}
				highlighted = !highlighted;
				startIndex = index;
			}
			++index;
		}
		if (index > startIndex)
		{
			Text2 textNode = Text2.createCentred(text.substring(startIndex, index));
			if (decorator != null)
				decorator.apply(textNode, highlighted);
			textNodes.add(textNode);
		}
		return textNodes;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: TEXT NORMALISER


	/**
	 * This interface defines a function that normalises text before a filter is applied to it.
	 */

	@FunctionalInterface
	public interface INormaliser
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Normalises the specified text and returns the result.
		 *
		 * @param  text
		 *           the text that will be normalised.
		 * @return the result of normalising <i>text</i>.
		 */

		String normalise(
			String	text);

		//--------------------------------------------------------------

	}

	//==================================================================


	// INTERFACE: TEXT-NODE DECORATOR


	/**
	 * This functional interface defines the method that must be implemented by a decorator of a {@linkplain Text2 text
	 * node} that is created by {@link SubstringFilterUtils#createTextNodes(SubstringFilterPane.FilterMode, String,
	 * String, IDecorator)}.
	 */

	@FunctionalInterface
	public interface IDecorator
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Applies this decorator to the specified text node.
		 *
		 * @param textNode
		 *          the text node to which this decorator will be applied.
		 * @param highlighted
		 *          if {@code true}, the text node is highlighted.
		 */

		void apply(
			Text2	textNode,
			boolean	highlighted);

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
