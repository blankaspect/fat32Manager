/*====================================================================*\

TimeField.java

Class: time field.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.textfield;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.time.DateTimeException;
import java.time.LocalTime;

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

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;
import uk.blankaspect.ui.jfx.style.StyleUtils;

//----------------------------------------------------------------------


// CLASS: TIME FIELD


/**
 * This class implements a JavaFX text field in which a 24-hour clock time may be edited.
 * The background colour of the text field changes according to the content of the field; it is
 * <ul>
 *   <li><i>white</i> if the field is empty,</li>
 *   <li><i>green</i> if the field contains a valid time, or</li>
 *   <li><i>red</i> if the field contains an invalid time.</li>
 * </ul>
 * <p>
 * The field expects the time to have the form <i>&lt;hour&gt;</i> <i>&lt;minute&gt;</i> <i>&lt;second&gt;</i>, with
 * configurable separators between the components of the time.  Each component may have either one or two digits.  The
 * hour must be in the range 0 to 23 inclusive.
 * </p>
 */

public class TimeField
	extends TextField
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The number of columns of the field. */
	private static final	int		NUM_COLUMNS	= 5;

	/** The pattern of a time. */
	private static final	String	TIME_PATTERN	= "HH%smm%sss";

	/** Miscellaneous strings. */
	private static final	String	PROMPT_STR			= "hh%smm%sss";
	private static final	String	MALFORMED_TIME_STR	= "Malformed time";

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
					.cls(StyleClass.TIME_FIELD).pseudo(PseudoClassKey.INVALID)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.CONTROL_INNER_BACKGROUND,
			ColourKey.BACKGROUND_VALID,
			CssSelector.builder()
					.cls(StyleClass.TIME_FIELD).pseudo(PseudoClassKey.VALID)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.HIGHLIGHT_FILL,
			ColourKey.HIGHLIGHT_FOCUSED,
			CssSelector.builder()
					.cls(StyleClass.TIME_FIELD).pseudo(FxPseudoClass.FOCUSED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.HIGHLIGHT_TEXT_FILL,
			ColourKey.HIGHLIGHT_TEXT_FOCUSED,
			CssSelector.builder()
					.cls(StyleClass.TIME_FIELD).pseudo(FxPseudoClass.FOCUSED)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	TIME_FIELD	= StyleConstants.CLASS_PREFIX + "time-field";
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

	/** The time formatter that is used by {@link #setTime(LocalTime)}. */
	private	DateTimeFormatter	formatter;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(TimeField.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a time field that accepts the specified separators between components of a time.
	 *
	 * @param separators
	 *          the characters that will be allowed between components of a time.  At least one separator must be
	 *          specified.
	 */

	public TimeField(
		char...	separators)
	{
		// Call alternative constructor
		this(new String(separators));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a time field that accepts the specified separators between components of a time.
	 *
	 * @param separators
	 *          a string whose characters will be allowed between components of a time.  At least one separator must be
	 *          specified.
	 */

	public TimeField(
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

		// Initialise time formatter
		String separator = separators.substring(0, 1);
		formatter = DateTimeFormatter.ofPattern(String.format(TIME_PATTERN, separator, separator));

		// Set properties
		setPrefColumnCount(NUM_COLUMNS);
		setTextFormatter(new TextFormatter<>(FilterFactory.decInteger(8, 2, separators)));
		setPromptText(String.format(PROMPT_STR, separator, separator));
		getStyleClass().add(StyleClass.TIME_FIELD);

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
				if (getTime() == null)
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
	 * Returns the colour that is associated with the specified key in the colour map of the current theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the current theme of the style
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
	 * Returns the time from this field.
	 *
	 * @return the time from this field, or {@code null} if this field is empty.
	 * @throws DateTimeException
	 *           if this field does not contain a valid time.
	 */

	public LocalTime getTime()
	{
		// Initialise time
		LocalTime time = null;

		// Get text
		String text = getText();

		// If text is not empty, parse it
		if (!text.isEmpty())
		{
			// Test number of time components
			String[] strs = text.split(separatorRegex, -1);
			if ((strs.length < 2) || (strs.length > 3))
				throw new DateTimeException(MALFORMED_TIME_STR);

			// Test for empty components
			for (int i = 0; i < strs.length; i++)
			{
				if (strs[i].isEmpty())
					throw new DateTimeException(MALFORMED_TIME_STR);
			}

			// Create time from components
			time = LocalTime.of(Integer.parseInt(strs[0]), Integer.parseInt(strs[1]),
								(strs.length < 3) ? 0 : Integer.parseInt(strs[2]));
		}

		// Return time
		return time;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the content of this field to a string representation of the specified value.  The string representation has
	 * the form <i>hhSmmSss</i>, where
	 * <ul>
	 *   <li><i>hh</i> is the hour in the range 0..23</li>
	 *   <li><i>mm</i> is the minute,</li>
	 *   <li><i>ss</i> is second, and</li>
	 *   <li><i>S</i> is the first separator that was specified in the constructor.</li>
	 * </ul>
	 *
	 * @param  time
	 *           the time whose string representation will be set as the content of this field.
	 * @throws DateTimeException
	 *           if <i>time</i> is invalid.
	 */

	public void setTime(
		LocalTime	time)
	{
		setText(formatter.format(time));
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the content of this field represents a valid time.
	 *
	 * @return {@code true} if the content of this field represents a valid time.
	 */

	public boolean isTimeValid()
	{
		try
		{
			return (getTime() != null);
		}
		catch (DateTimeException e)
		{
			return false;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns the time formatter that is used by {@link #setTime(LocalTime)}.
	 *
	 * @return the time formatter that is used by {@link #setTime(LocalTime)}.
	 */

	public DateTimeFormatter getFormatter()
	{
		return formatter;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
