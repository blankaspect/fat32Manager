/*====================================================================*\

SpinnerUtils.java

Class: spinner-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.spinner;

//----------------------------------------------------------------------


// IMPORTS


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import java.util.Locale;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import javafx.util.StringConverter;

import uk.blankaspect.ui.jfx.textfield.FilterFactory;

//----------------------------------------------------------------------


// CLASS: SPINNER-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain Spinner spinners}.
 */

public class SpinnerUtils
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Formatter for converting a floating-point number to a string without an exponent. */
	private static final	DecimalFormat	FORMATTER;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		FORMATTER = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		FORMATTER.setMaximumFractionDigits(100);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private SpinnerUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the value of the specified integer spinner to the content of the spinner's editor.
	 *
	 * @param spinner
	 *          the target spinner.
	 */

	public static void updateIntegerValue(
		Spinner<Integer>	spinner)
	{
		TextField editor = spinner.getEditor();
		SpinnerValueFactory<Integer> valueFactory = spinner.getValueFactory();
		try
		{
			valueFactory.setValue(Integer.parseInt(editor.getText()));
		}
		catch (NumberFormatException e)
		{
			editor.setText(valueFactory.getConverter().toString(spinner.getValue()));
		}
	}

	//------------------------------------------------------------------

	/**
	 * Sets the value of the specified double spinner to the content of the spinner's editor.
	 *
	 * @param spinner
	 *          the target spinner.
	 */

	public static void updateDoubleValue(
		Spinner<Double>	spinner)
	{
		TextField editor = spinner.getEditor();
		SpinnerValueFactory<Double> valueFactory = spinner.getValueFactory();
		try
		{
			valueFactory.setValue(Double.parseDouble(editor.getText()));
		}
		catch (NullPointerException | NumberFormatException e)
		{
			editor.setText(valueFactory.getConverter().toString(spinner.getValue()));
		}
	}

	//------------------------------------------------------------------

	/**
	 * Sets a filter on the editor of the specified integer spinner to filter out characters other than decimal digits
	 * and, if signed, a minus sign.
	 *
	 * @param spinner
	 *          the spinner on whose editor a filter will be set.
	 * @param signed
	 *          if {@code true}, the filter will allow a single leading minus sign.
	 */

	public static void setIntegerFilter(
		Spinner<Integer>	spinner,
		boolean				signed)
	{
		spinner.getEditor().setTextFormatter(new TextFormatter<>(FilterFactory.decInteger(signed)));
	}

	//------------------------------------------------------------------

	/**
	 * Sets a filter on the editor of the specified double spinner to filter out characters other than decimal digits,
	 * a radix point and, if signed, a minus sign.
	 *
	 * @param spinner
	 *          the spinner on whose editor a filter will be set.
	 * @param signed
	 *          if {@code true}, the filter will allow a single leading minus sign.
	 */

	public static void setDoubleFilter(
		Spinner<Double>	spinner,
		boolean			signed)
	{
		spinner.getEditor().setTextFormatter(new TextFormatter<>(FilterFactory.floatingPoint(signed)));
	}

	//------------------------------------------------------------------

	/**
	 * Sets a converter on the value factory of the specified spinner to clamp the value that is returned by the
	 * converter's {@link StringConverter#fromString(String) fromString(String)} method to the range whose lower and
	 * upper bounds are {@link SpinnerValueFactory.IntegerSpinnerValueFactory#getMin()} and {@link
	 * SpinnerValueFactory.IntegerSpinnerValueFactory#getMax()} respectively.
	 *
	 * @param spinner
	 *          the spinner on whose value factory a clamping converter will be set.
	 */

	public static void setIntegerClampingConverter(
		Spinner<Integer>	spinner)
	{
		SpinnerValueFactory<Integer> factory = spinner.getValueFactory();
		if (factory instanceof SpinnerValueFactory.IntegerSpinnerValueFactory integerFactory)
			setIntegerClampingConverter(integerFactory);
	}

	//------------------------------------------------------------------

	/**
	 * Sets a converter on the specified value factory to clamp the value that is returned by the converter's {@link
	 * StringConverter#fromString(String) fromString(String)} method to the range whose lower and upper bounds are
	 * {@link SpinnerValueFactory.IntegerSpinnerValueFactory#getMin()} and {@link
	 * SpinnerValueFactory.IntegerSpinnerValueFactory#getMax()} respectively.
	 *
	 * @param valueFactory
	 *          the value factory on which a clamping converter will be set.
	 */

	public static void setIntegerClampingConverter(
		SpinnerValueFactory.IntegerSpinnerValueFactory	valueFactory)
	{
		valueFactory.setConverter(new StringConverter<>()
		{
			@Override
			public String toString(
				Integer	value)
			{
				return (value == null) ? "" : value.toString();
			}

			@Override
			public Integer fromString(
				String	string)
			{
				try
				{
					return Math.min(Math.max(valueFactory.getMin(), Integer.parseInt(string)), valueFactory.getMax());
				}
				catch (NumberFormatException e)
				{
					return valueFactory.getValue();
				}
			}
		});
	}

	//------------------------------------------------------------------

	/**
	 * Sets a converter on the value factory of the specified spinner to clamp the value that is returned by the
	 * converter's {@link StringConverter#fromString(String) fromString(String)} method to the range whose lower and
	 * upper bounds are {@link SpinnerValueFactory.DoubleSpinnerValueFactory#getMin()} and {@link
	 * SpinnerValueFactory.DoubleSpinnerValueFactory#getMax()} respectively.
	 *
	 * @param spinner
	 *          the spinner on whose value factory a clamping converter will be set.
	 */

	public static void setDoubleClampingConverter(
		Spinner<Double>	spinner)
	{
		SpinnerValueFactory<Double> factory = spinner.getValueFactory();
		if (factory instanceof SpinnerValueFactory.DoubleSpinnerValueFactory doubleFactory)
			setDoubleClampingConverter(doubleFactory, null);
	}

	//------------------------------------------------------------------

	/**
	 * Sets a converter on the value factory of the specified spinner to clamp the value that is returned by the
	 * converter's {@link StringConverter#fromString(String) fromString(String)} method to the range whose lower and
	 * upper bounds are {@link SpinnerValueFactory.DoubleSpinnerValueFactory#getMin()} and {@link
	 * SpinnerValueFactory.DoubleSpinnerValueFactory#getMax()} respectively.
	 *
	 * @param spinner
	 *          the spinner on whose value factory a clamping converter will be set.
	 * @param formatter
	 *          the formatter that will provide the string representation of the value.  If it is {@code null}, the
	 *          default formatter will be used.
	 */

	public static void setDoubleClampingConverter(
		Spinner<Double>	spinner,
		DecimalFormat	formatter)
	{
		SpinnerValueFactory<Double> factory = spinner.getValueFactory();
		if (factory instanceof SpinnerValueFactory.DoubleSpinnerValueFactory doubleFactory)
			setDoubleClampingConverter(doubleFactory, formatter);
	}

	//------------------------------------------------------------------

	/**
	 * Sets a converter on the specified value factory to clamp the value that is returned by the converter's {@link
	 * StringConverter#fromString(String) fromString(String)} method to the range whose lower and upper bounds are
	 * {@link SpinnerValueFactory.DoubleSpinnerValueFactory#getMin()} and {@link
	 * SpinnerValueFactory.DoubleSpinnerValueFactory#getMax()} respectively.
	 *
	 * @param valueFactory
	 *          the value factory on which a clamping converter will be set.
	 */

	public static void setDoubleClampingConverter(
		SpinnerValueFactory.DoubleSpinnerValueFactory	valueFactory)
	{
		setDoubleClampingConverter(valueFactory, null);
	}

	//------------------------------------------------------------------

	/**
	 * Sets a converter on the specified value factory to clamp the value that is returned by the converter's {@link
	 * StringConverter#fromString(String) fromString(String)} method to the range whose lower and upper bounds are
	 * {@link SpinnerValueFactory.DoubleSpinnerValueFactory#getMin()} and {@link
	 * SpinnerValueFactory.DoubleSpinnerValueFactory#getMax()} respectively.
	 *
	 * @param valueFactory
	 *          the value factory on which a clamping converter will be set.
	 * @param formatter
	 *          the formatter that will provide the string representation of the value.  If it is {@code null}, the
	 *          default formatter will be used.
	 */

	public static void setDoubleClampingConverter(
		SpinnerValueFactory.DoubleSpinnerValueFactory	valueFactory,
		DecimalFormat									formatter)
	{
		valueFactory.setConverter(new StringConverter<>()
		{
			@Override
			public String toString(
				Double	value)
			{
				return (value == null) ? "" : ((formatter == null) ? FORMATTER : formatter).format(value);
			}

			@Override
			public Double fromString(
				String	string)
			{
				try
				{
					return Math.min(Math.max(valueFactory.getMin(), Double.parseDouble(string)), valueFactory.getMax());
				}
				catch (NullPointerException | NumberFormatException e)
				{
					return valueFactory.getValue();
				}
			}
		});
	}

	//------------------------------------------------------------------
}

//----------------------------------------------------------------------
