/*====================================================================*\

DateField.java

Class: date field.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.textfield;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.time.DateTimeException;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import java.util.List;

import javafx.css.PseudoClass;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.common.regex.RegexUtils;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

import uk.blankaspect.ui.jfx.date.DateSelectionPane;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;
import uk.blankaspect.ui.jfx.style.StyleUtils;

//----------------------------------------------------------------------


// CLASS: DATE FIELD


/**
 * This class implements a JavaFX text field in which a calendar date may be edited.  The background colour of the field
 * changes according to the content of the field; it is
 * <ul>
 *   <li><i>white</i> if the field is empty,</li>
 *   <li><i>green</i> if the field contains a valid date, or</li>
 *   <li><i>red</i> if the field contains an invalid date.</li>
 * </ul>
 * <p>
 * The field expects the date to have the form <i>&lt;year&gt;</i> <i>&lt;month&gt;</i> <i>&lt;day of the month&gt;</i>,
 * with configurable separators between the components of the date.  The year must have four digits; the month and day
 * may have either one or two digits.  The year must be in the range 1600 to 3999 inclusive.  The month and day of the
 * month are one-based (ie, the first month and day of the month are 1).
 * </p>
 */

public class DateField
	extends TextField
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The number of columns of the field. */
	private static final	int		NUM_COLUMNS	= 7;

	/** The pattern of a date. */
	private static final	String	DATE_PATTERN	= "uuuu%sMM%sdd";

	/** Miscellaneous strings. */
	private static final	String	PROMPT_STR				= "yyyy%smm%sdd";
	private static final	String	MALFORMED_DATE_STR		= "Malformed date";
	private static final	String	YEAR_OUT_OF_BOUNDS_STR	= "Year out of bounds: ";

	/** The pseudo-class that is associated with the <i>invalid</i> state. */
	private static final	PseudoClass	INVALID_PSEUDO_CLASS	= PseudoClass.getPseudoClass(PseudoClassKey.INVALID);

	/** The pseudo-class that is associated with the <i>valid</i> state. */
	private static final	PseudoClass	VALID_PSEUDO_CLASS		= PseudoClass.getPseudoClass(PseudoClassKey.VALID);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.CONTROL_INNER_BACKGROUND,
			ColourKey.BACKGROUND_INVALID,
			CssSelector.builder()
						.cls(StyleClass.DATE_FIELD).pseudo(PseudoClassKey.INVALID)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.CONTROL_INNER_BACKGROUND,
			ColourKey.BACKGROUND_VALID,
			CssSelector.builder()
						.cls(StyleClass.DATE_FIELD).pseudo(PseudoClassKey.VALID)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.HIGHLIGHT_FILL,
			ColourKey.HIGHLIGHT_FOCUSED,
			CssSelector.builder()
						.cls(StyleClass.DATE_FIELD).pseudo(FxPseudoClass.FOCUSED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.HIGHLIGHT_TEXT_FILL,
			ColourKey.HIGHLIGHT_TEXT_FOCUSED,
			CssSelector.builder()
						.cls(StyleClass.DATE_FIELD).pseudo(FxPseudoClass.FOCUSED)
						.build()
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	DATE_FIELD	= StyleConstants.CLASS_PREFIX + "date-field";
	}

	/** Keys of CSS pseudo-classes. */
	private interface PseudoClassKey
	{
		String	INVALID	= "invalid";
		String	VALID	= "valid";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	BACKGROUND_INVALID		= PREFIX + "background.invalid";
		String	BACKGROUND_VALID		= PREFIX + "background.valid";
		String	HIGHLIGHT_FOCUSED		= PREFIX + "highlight.focused";
		String	HIGHLIGHT_TEXT_FOCUSED	= PREFIX + "highlight.text.focused";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The regular expression that is used for searching for separators in the text of this field. */
	private	String				separatorRegex;

	/** The date formatter that is used by {@link #setDate(LocalDate)}. */
	private	DateTimeFormatter	formatter;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(DateField.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a date field that accepts the specified separators between components of a date.
	 *
	 * @param separators
	 *          the characters that will be allowed between components of a date.  At least one separator must be
	 *          specified.
	 */

	public DateField(
		char...	separators)
	{
		// Call alternative constructor
		this(new String(separators));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a date field that accepts the specified separators between components of a date.
	 *
	 * @param separators
	 *          a string whose characters will be allowed between components of a date.  At least one separator must be
	 *          specified.
	 */

	public DateField(
		String	separators)
	{
		// Validate arguments
		if (separators.isEmpty())
			throw new IllegalArgumentException("No separators");

		// Create regular expression for separators
		StringBuilder buffer = new StringBuilder();
		buffer.append('(');
		for (int i = 0; i < separators.length(); i++)
		{
			if (i > 0)
				buffer.append('|');
			buffer.append(RegexUtils.escape(separators.charAt(i)));
		}
		buffer.append(')');
		separatorRegex = buffer.toString();

		// Initialise date formatter
		char separator = separators.charAt(0);
		formatter = DateTimeFormatter.ofPattern(String.format(DATE_PATTERN, separator, separator));

		// Set properties
		setPrefColumnCount(NUM_COLUMNS);
		setTextFormatter(new TextFormatter<>(FilterFactory.decInteger(8, 2, separators)));
		setPromptText(String.format(PROMPT_STR, separator, separator));
		getStyleClass().add(StyleClass.DATE_FIELD);

		// Create procedure to set background colour
		IProcedure1<Color> setBackgroundColour = colour ->
		{
			StyleUtils.setProperty(this, FxProperty.CONTROL_INNER_BACKGROUND.getName(),
								   ColourUtils.colourToHexString(colour));
		};

		// Update background colour when content of field changes
		textProperty().addListener(observable ->
		{
			try
			{
				if (getDate() == null)
				{
					pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
					pseudoClassStateChanged(VALID_PSEUDO_CLASS, false);

					setStyle(null);
				}
				else
				{
					pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
					pseudoClassStateChanged(VALID_PSEUDO_CLASS, true);

					setBackgroundColour.invoke(getColour(ColourKey.BACKGROUND_VALID));
				}
			}
			catch (DateTimeException e)
			{
				pseudoClassStateChanged(INVALID_PSEUDO_CLASS, true);
				pseudoClassStateChanged(VALID_PSEUDO_CLASS, false);

				setBackgroundColour.invoke(getColour(ColourKey.BACKGROUND_INVALID));
			}
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the colour that is associated with the specified key in the colour map of the selected theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the selected theme of the style
	 *         manager, or {@link StyleManager#DEFAULT_COLOUR} if there is no such colour.
	 */

	private static Color getColour(
		String	key)
	{
		return StyleManager.INSTANCE.getColourOrDefault(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the date from this field.
	 *
	 * @return the date from this field, or {@code null} if this field is empty.
	 * @throws DateTimeException
	 *           if this field does not contain a valid date.
	 */

	public LocalDate getDate()
	{
		// Initialise date
		LocalDate date = null;

		// Get text
		String text = getText();

		// If text is not empty, parse it
		if (!text.isEmpty())
		{
			// Test number of date components
			String[] strs = text.split(separatorRegex, -1);
			if (strs.length != 3)
				throw new DateTimeException(MALFORMED_DATE_STR);

			// Test for empty components
			for (int i = 0; i < strs.length; i++)
			{
				if (strs[i].isEmpty())
					throw new DateTimeException(MALFORMED_DATE_STR);
			}

			// Validate year
			int year = Integer.parseInt(strs[0]);
			if ((year < DateSelectionPane.MIN_YEAR) || (year > DateSelectionPane.MAX_YEAR))
				throw new DateTimeException(YEAR_OUT_OF_BOUNDS_STR + year);

			// Create date from components
			date = LocalDate.of(year, Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
		}

		// Return date
		return date;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the content of this field to a string representation of the specified value.  The string representation has
	 * the form <i>yyyySmmSdd</i>, where
	 * <ul>
	 *   <li><i>yyyy</i> is the year,</li>
	 *   <li><i>mm</i> is the month,</li>
	 *   <li><i>dd</i> is the day of the month, and</li>
	 *   <li><i>S</i> is the first separator that was specified in the constructor.</li>
	 * </ul>
	 *
	 * @param  date
	 *           the date whose string representation will be set as the content of this field.
	 * @throws DateTimeException
	 *           if the year of <i>date</i> is outside the range 1600..3999 or <i>date</i> is otherwise invalid.
	 */

	public void setDate(
		LocalDate	date)
	{
		// Validate year
		int year = date.getYear();
		if ((year < DateSelectionPane.MIN_YEAR) || (year > DateSelectionPane.MAX_YEAR))
			throw new DateTimeException(YEAR_OUT_OF_BOUNDS_STR + year);

		// Set text of text field to formatted date
		setText(formatter.format(date));
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the content of this field represents a valid date.
	 *
	 * @return {@code true} if the content of this field represents a valid date.
	 */

	public boolean isDateValid()
	{
		try
		{
			return (getDate() != null);
		}
		catch (DateTimeException e)
		{
			return false;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns the date formatter that is used by {@link #setDate(LocalDate)}.
	 *
	 * @return the date formatter that is used by {@link #setDate(LocalDate)}.
	 */

	public DateTimeFormatter getFormatter()
	{
		return formatter;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
