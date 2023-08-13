/*====================================================================*\

DateUtils.java

Class: date-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.date;

//----------------------------------------------------------------------


// IMPORTS


import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import uk.blankaspect.common.misc.ModernCalendar;

//----------------------------------------------------------------------


// CLASS: DATE-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to calendar dates.
 */

public class DateUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The number of names of months. */
	public static final	int	NUM_MONTH_NAMES	= 12;

	/** The number of names of days of the week. */
	public static final	int	NUM_DAY_NAMES	= 7;

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** The names of months, initialised for the default locale. */
	private static	List<String>	monthNames	= getMonthNames(Locale.getDefault());

	/** The names of days of the week, initialised for the default locale. */
	private static	List<String>	dayNames	= getDayNames(Locale.getDefault());

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private DateUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an unmodifiable list of the names of months.  The list is initialised with the names of months for the
	 * default locale, but it can be overwritten with {@link #setMonthNames(Collection)}.
	 *
	 * @return an unmodifiable list of the names of months.
	 * @see    #setMonthNames(Collection)
	 */

	public static List<String> getMonthNames()
	{
		return Collections.unmodifiableList(monthNames);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the names of months for the specified locale.
	 *
	 * @param  locale
	 *           the locale for which names of months are required.
	 * @return a list of the names of months for {@code locale}.
	 */

	public static List<String> getMonthNames(
		Locale	locale)
	{
		return getMonthNames(locale, 0);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the names of months for the specified locale.  The length of each name will not exceed the
	 * specified maximum value.
	 *
	 * @param  locale
	 *           the locale for which names of months are required.
	 * @param  maxLength
	 *           the maximum length of an element of the list of names.  If this is zero or negative, names will not be
	 *           truncated.
	 * @return a list of the names of months for {@code locale}.
	 */

	public static List<String> getMonthNames(
		Locale	locale,
		int		maxLength)
	{
		List<String> names = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("MMMM", locale);
		Calendar date = new ModernCalendar(1970, 0, 1);
		for (int i = 0; i < NUM_MONTH_NAMES; i++)
		{
			String str = format.format(date.getTime());
			if ((maxLength > 0) && (maxLength < str.length()))
				str = str.substring(0, maxLength);
			names.add(str);
			date.add(Calendar.MONTH, 1);
		}
		return names;
	}

	//------------------------------------------------------------------

	/**
	 * Returns an unmodifiable list of the names of days of the week.  The list is initialised with the names of days of
	 * the week for the default locale, but it can be overwritten with {@link #setDayNames(Collection)}.
	 *
	 * @return an unmodifiable list of the names of days of the week.
	 * @see    #setDayNames(Collection)
	 */

	public static List<String> getDayNames()
	{
		return Collections.unmodifiableList(dayNames);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the names of days of the week for the specified locale.
	 *
	 * @param  locale
	 *           the locale for which names of days of the week are required.
	 * @return a list of the names of days of the week for {@code locale}.
	 */

	public static List<String> getDayNames(Locale locale)
	{
		return getDayNames(locale, 0);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the names of days of the week for the specified locale.  The length of each name will not
	 * exceed the specified maximum value.
	 *
	 * @param  locale
	 *           the locale for which names of days of the week are required.
	 * @param  maxLength
	 *           the maximum length of an element of the list of names.  If this is zero or negative, names will not be
	 *           truncated.
	 * @return a list of the names of days of the week for {@code locale}.
	 */

	public static List<String> getDayNames(Locale locale,
										   int    maxLength)
	{
		List<String> names = new ArrayList<>();
		SimpleDateFormat format = new SimpleDateFormat("EEEE", locale);
		Calendar date = new ModernCalendar(1970, 0, 4);
		for (int i = 0; i < NUM_DAY_NAMES; i++)
		{
			String str = format.format(date.getTime());
			if ((maxLength > 0) && (maxLength < str.length()))
				str = str.substring(0, maxLength);
			names.add(str);
			date.add(Calendar.DAY_OF_MONTH, 1);
		}
		return names;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the names of months to the specified values.  The names are returned by {@link #getMonthNames()}.
	 *
	 * @param  names
	 *           the values to which the names of the month will be set.
	 * @throws IllegalArgumentException
	 *           if {@code names} is {@code null} or the size of {@code names} is not 12.
	 * @see    #getMonthNames()
	 */

	public static void setMonthNames(List<String> names)
	{
		// Validate argument
		if ((names == null) || (names.size() != NUM_MONTH_NAMES))
			throw new IllegalArgumentException();

		// Set names of months
		monthNames.clear();
		monthNames.addAll(names);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the names of days of the week to the specified values.  The names are returned by {@link #getDayNames()}.
	 *
	 * @param  names
	 *           the values to which the names of the days of the week will be set.
	 * @throws IllegalArgumentException
	 *           if {@code names} is {@code null} or the size of {@code names} is not 7.
	 * @see    #getDayNames()
	 */

	public static void setDayNames(List<String> names)
	{
		// Validate argument
		if ((names == null) || (names.size() != NUM_DAY_NAMES))
			throw new IllegalArgumentException();

		// Set names of days of the week
		dayNames.clear();
		dayNames.addAll(names);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
