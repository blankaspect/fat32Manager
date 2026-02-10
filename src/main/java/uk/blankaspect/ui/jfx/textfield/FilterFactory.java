/*====================================================================*\

FilterFactory.java

Class: factory for filters for text-input controls.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.textfield;

//----------------------------------------------------------------------


// IMPORTS


import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// CLASS: FACTORY FOR FILTERS FOR TEXT-INPUT CONTROLS


/**
 * This class provides factory methods that create filters for JavaFX {@linkplain TextInputControl text-input controls}.
 * The type of a filter is {@code UnaryOperator<TextFormatter.Change>}, which is intended to be used to create a {@link
 * TextFormatter} that is set on a text-input control.
 */

public class FilterFactory
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** A character filter for decimal digits. */
	public static final	ICharFilter	DEC_CHAR_FILTER;

	/** A character filter for hexadecimal digits. */
	public static final	ICharFilter	HEX_CHAR_FILTER;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		DEC_CHAR_FILTER	= (ch, index, text) -> isDecimalDigit(ch) ? Character.toString(ch) : "";

		HEX_CHAR_FILTER	= (ch, index, text) ->
		{
			Character outChar = null;
			if (isDecimalDigit(ch))
				outChar = ch;
			else if ((ch >= 'A') && (ch <= 'F'))
				outChar = ch;
			else if ((ch >= 'a') && (ch <= 'f'))
				outChar = Character.toUpperCase(ch);
			return (outChar == null) ? "" : outChar.toString();
		};
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private FilterFactory()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the specified character is in the range U+0030 to U+0039 inclusive.
	 *
	 * @param  ch
	 *           the character of interest.
	 * @return {@code true} if {@code ch} is in the range U+0030 to U+0039 inclusive.
	 */

	public static boolean isDecimalDigit(
		char	ch)
	{
		return (ch >= '0') && (ch <= '9');
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a filter for a text-input control that applies the specified filter to each character of a
	 * change (addition or replacement) that is made to the content of the control.
	 *
	 * @param  charFilter
	 *           the filter that will be applied to each character of a change that is made to the content of a
	 *           text-input control.
	 * @return a filter that applies {@code charFilter} to a change that is made to the content of a text-input control.
	 */

	public static UnaryOperator<TextFormatter.Change> createFilter(
		ICharFilter	charFilter)
	{
		return createFilter(0, charFilter);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a filter for a text-input control that applies the specified filter to each character of a
	 * change (addition or replacement) that is made to the content of the control.  The length of the content of the
	 * control may optionally be limited to the specified value.
	 *
	 * @param  maxLength
	 *           the maximum length of the content of the text-input control, ignored if not greater than 0.
	 * @param  charFilter
	 *           the filter that will be applied to each character of a change that is made to the content of a
	 *           text-input control.
	 * @return a filter that applies {@code charFilter} to a change that is made to the content of a text-input control.
	 */

	public static UnaryOperator<TextFormatter.Change> createFilter(
		int			maxLength,
		ICharFilter	charFilter)
	{
		return change ->
		{
			// Apply the filter only if the change is an addition or a replacement
			if (change.isReplaced() || change.isAdded())
			{
				// Get the start and end of the change
				int start = change.getRangeStart();
				int end = change.getRangeEnd();

				// Get the control text
				String controlText = change.getControlText();

				// Get the control text without the change
				String suffix = controlText.substring(end);
				String text = (start < end) ? controlText.substring(0, start) + suffix : controlText;

				// Get the text of the change
				String inText = change.getText();
				String outText = inText;

				// Apply the filter to the text of the change
				if (charFilter != null)
				{
					int inLength = inText.length();
					StringBuilder buffer = new StringBuilder(2 * inLength);
					for (int i = 0; i < inLength; i++)
					{
						String str = charFilter.apply(inText.charAt(i), start + i, text);
						if (!StringUtils.isNullOrEmpty(str))
							buffer.append(str);
					}
					outText = buffer.toString();
				}

				// Get the filtered text and its length
				int outLength = outText.length();

				// If a maximum length is specified and the change would cause the length of the text to exceed this
				// maximum, update the change and truncate the text ...
				if ((maxLength > 0) && (text.length() + outLength > maxLength))
				{
					// Set the change to include the text after the change
					change.setText((outText + suffix).substring(0, maxLength - start));
					change.setRange(start, Math.min(maxLength, controlText.length()));

					// Set the anchor and caret to the end of the change
					end = Math.min(start + outLength, maxLength);
					change.selectRange(end, end);
				}

				// ... otherwise, if the filter has changed the text, update the change with the filtered text
				else if (!outText.equals(inText))
				{
					// If the filtered text is empty, restore the previous text and selection ...
					if (outLength == 0)
					{
						// Restore the previous text
						change.setText(controlText.substring(start, end));

						// Restore the previous anchor and caret position
						change.selectRange(change.getControlAnchor(), change.getControlCaretPosition());
					}

					// ... otherwise, update the change with the filtered text
					else
					{
						// Set text of change to filtered text
						change.setText(outText);

						// Set the anchor and caret position to the end of the change
						end = start + outLength;
						change.selectRange(end, end);
					}
				}
			}
			return change;
		};
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a filter for a text-input control that limits the length of the content of the control to the
	 * specified value.
	 *
	 * @param  maxLength
	 *           the maximum length of the content of the text-input control, ignored if not greater than 0.
	 * @return a filter that limits the length of the content of the control to {@code maxLength}.
	 */

	public static UnaryOperator<TextFormatter.Change> lengthLimiter(
		int	maxLength)
	{
		return createFilter(maxLength, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a filter that can be applied to a text-input control to limit the characters of the text to
	 * decimal digits and an optional leading minus sign.
	 *
	 * @param  signed
	 *           if {@code true}, the filter will accept a minus sign as the first character of the text.
	 * @return a filter that can be applied to a text-input control to limit the characters of the text to decimal
	 *         digits and an optional leading minus sign.
	 */

	public static UnaryOperator<TextFormatter.Change> decInteger(
		boolean	signed)
	{
		return decInteger(Integer.MAX_VALUE, signed);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a filter that can be applied to a text-input control to limit the characters of the text to
	 * decimal digits and an optional leading minus sign.
	 *
	 * @param  maxLength
	 *           the maximum length of the content of the text-input control, ignored if not greater than 0.
	 * @param  signed
	 *           if {@code true}, the filter will accept a minus sign as the first character of the text.
	 * @return a filter that can be applied to a text-input control to limit the characters of the text to decimal
	 *         digits and an optional leading minus sign.
	 */

	public static UnaryOperator<TextFormatter.Change> decInteger(
		int		maxLength,
		boolean	signed)
	{
		return createFilter(maxLength, (ch, index, text) ->
		{
			Character outChar = null;
			if (ch == '-')
			{
				if (signed && (index == 0) && (text.indexOf(ch) < 0))
					outChar = ch;
			}
			else if (isDecimalDigit(ch))
				outChar = ch;
			return (outChar == null) ? "" : outChar.toString();
		});
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a filter that can be applied to a text-input control to limit the characters of the text to
	 * decimal digits and no more than the specified number of specified separator characters.
	 *
	 * @param  maxNumDigits
	 *           the maximum number of decimal digits.
	 * @param  maxNumSeparators
	 *           the maximum number of separators.
	 * @param  separators
	 *           the allowed separator characters.
	 * @return a filter that can be applied to a text-input control to limit the characters of the text to decimal
	 *         digits and no more than {@code maxNumSeparators} characters from {@code separators}.
	 * @throws IllegalArgumentException
	 *           if {@code separators} contains a decimal-digit character.
	 */

	public static UnaryOperator<TextFormatter.Change> decInteger(
		int		maxNumDigits,
		int		maxNumSeparators,
		char...	separators)
	{
		return decInteger(maxNumDigits, maxNumSeparators, new String(separators));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a filter that can be applied to a text-input control to limit the characters of the text to
	 * decimal digits and no more than the specified number of specified separator characters.
	 *
	 * @param  maxNumDigits
	 *           the maximum number of decimal digits.
	 * @param  maxNumSeparators
	 *           the maximum number of separators.
	 * @param  separators
	 *           the allowed separator characters.
	 * @return a filter that can be applied to a text-input control to limit the characters of the text to decimal
	 *         digits and no more than {@code maxNumSeparators} characters from {@code separators}.
	 * @throws IllegalArgumentException
	 *           if {@code separators} contains a decimal-digit character.
	 */

	public static UnaryOperator<TextFormatter.Change> decInteger(
		int		maxNumDigits,
		int		maxNumSeparators,
		String	separators)
	{
		// Test whether any separator is decimal-digit character
		for (int i = 0; i < separators.length(); i++)
		{
			char ch = separators.charAt(i);
			if (isDecimalDigit(ch))
				throw new IllegalArgumentException("Separator is decimal digit");
		}

		// Create filter and return it
		return createFilter((ch, index, text) ->
		{
			Character outChar = null;
			if (isDecimalDigit(ch))
			{
				int numDigits = 0;
				for (int i = 0; i < text.length(); i++)
				{
					if (isDecimalDigit(text.charAt(i)))
						++numDigits;
				}
				if (numDigits < maxNumDigits)
					outChar = ch;
			}
			else if (separators.indexOf(ch) >= 0)
			{
				if (index > 0)
				{
					int numSeparators = 0;
					for (int i = 0; i < text.length(); i++)
					{
						if (separators.indexOf(text.charAt(i)) >= 0)
							++numSeparators;
					}
					if (numSeparators < maxNumSeparators)
						outChar = ch;
				}
			}
			return (outChar == null) ? "" : outChar.toString();
		});
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a filter that can be applied to a text-input control to limit the characters of the text to
	 * hexadecimal digits.
	 *
	 * @param  maxNumDigits
	 *           the maximum number of hexadecimal digits.
	 * @return a filter that can be applied to a text-input control to limit the characters of the text to hexadecimal
	 *         digits.
	 */

	public static UnaryOperator<TextFormatter.Change> hexInteger(
		int	maxNumDigits)
	{
		return createFilter(maxNumDigits, HEX_CHAR_FILTER);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a filter that can be applied to a text-input control to limit the characters of the text to
	 * decimal digits, an optional leading minus sign and a single radix point ('.').
	 *
	 * @param  signed
	 *           if {@code true}, the filter will accept a minus sign as the first character of the text.
	 * @return a filter that can be applied to a text-input control to limit the characters of the text to decimal
	 *         digits, an optional leading minus sign and a single radix point ('.').
	 */

	public static UnaryOperator<TextFormatter.Change> floatingPoint(
		boolean	signed)
	{
		return createFilter((ch, index, text) ->
		{
			Character outChar = null;
			if (ch == '-')
			{
				if (signed && (index == 0) && (text.indexOf(ch) < 0))
					outChar = ch;
			}
			else if (ch == '.')
			{
				if (text.indexOf(ch) < 0)
					outChar = ch;
			}
			else if (isDecimalDigit(ch))
				outChar = ch;
			return (outChar == null) ? "" : outChar.toString();
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: CHARACTER FILTER


	/**
	 * This functional interface defines the method that must be implemented by a filter that is applied by the {@link
	 * FilterFactory#createFilter(ICharFilter)} or {@link FilterFactory#createFilter(int, ICharFilter)} methods.
	 */

	@FunctionalInterface
	public interface ICharFilter
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Applies this character filter to the specified character at the specified index within the specified text
		 * and returns the resulting text.
		 *
		 * @param  ch
		 *           the character to which this filter will be applied.
		 * @param  index
		 *           the index of the character in the text that contains it.
		 * @param  text
		 *           the text that contains {@code ch}.
		 * @return the text that results from applying this filter to {@code ch}.
		 */

		String apply(
			char	ch,
			int		index,
			String	text);

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
