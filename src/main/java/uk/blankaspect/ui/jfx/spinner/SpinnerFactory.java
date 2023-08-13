/*====================================================================*\

SpinnerFactory.java

Class: spinner factory.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.spinner;

//----------------------------------------------------------------------


// IMPORTS


import java.text.DecimalFormat;

import javafx.beans.value.ChangeListener;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import uk.blankaspect.ui.jfx.textfield.TextFieldUtils;

//----------------------------------------------------------------------


// CLASS: SPINNER FACTORY


public class SpinnerFactory
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The key with which a focus listener is associated in the properties of a spinner by the factory methods of this
		class. */
	public static final		Object	FOCUS_LISTENER_KEY	= new Object();

	/** The minimum number of columns of a spinner's editor that are occupied by the arrow buttons. */
	private static final	int		MIN_ARROW_BUTTON_NUM_COLUMNS		= 1;

	/** The maximum number of columns of a spinner's editor that are occupied by the arrow buttons. */
	private static final	int		MAX_ARROW_BUTTON_NUM_COLUMNS		= 32;

	/** The default number of columns of a spinner's editor that are occupied by the arrow buttons. */
	private static final	int		DEFAULT_ARROW_BUTTON_NUM_COLUMNS	= 2;

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** The number of columns of a spinner's editor that are occupied by the arrow buttons. */
	private static	int	arrowButtonNumColumns	= DEFAULT_ARROW_BUTTON_NUM_COLUMNS;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private SpinnerFactory()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the number of columns of a spinner's editor that are occupied by the arrow buttons.
	 *
	 * @param numColumns
	 *          the number of columns of a spinner's editor that are occupied by the arrow buttons.
	 */

	public static void setArrowButtonNumColumns(
		int	numColumns)
	{
		// Validate arguments
		if ((numColumns < MIN_ARROW_BUTTON_NUM_COLUMNS) || (numColumns > MAX_ARROW_BUTTON_NUM_COLUMNS))
			throw new IllegalArgumentException("Number of columns out of bounds: " + numColumns);

		// Update class variable
		arrowButtonNumColumns = numColumns;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns an editable spinner for integer values.  The spinner sets its value from the content of its
	 * text field ({@link Spinner#getEditor()}) when it loses keyboard focus.
	 *
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  numDigits
	 *           the number of digits that the editor of the spinner must accommodate.
	 * @return an editable spinner for integer values.
	 */

	public static Spinner<Integer> integerSpinner(
		int	minValue,
		int	maxValue,
		int	initialValue,
		int	numDigits)
	{
		return integerSpinner(minValue, maxValue, initialValue, 1, numDigits);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns an editable spinner for {@code Integer} values.  The spinner sets its value from the content
	 * of its text field ({@link Spinner#getEditor()}) when it loses keyboard focus.
	 *
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  step
	 *           the step between successive values of the spinner.
	 * @param  numDigits
	 *           the number of digits that the editor of the spinner must accommodate.
	 * @return an editable spinner for integer values.
	 */

	public static Spinner<Integer> integerSpinner(
		int	minValue,
		int	maxValue,
		int	initialValue,
		int	step,
		int	numDigits)
	{
		// Create value factory that clamps the value that is returned by its converter's 'fromString' method
		SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
				new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, initialValue, step);
		SpinnerUtils.setIntegerClampingConverter(valueFactory);

		// Create spinner
		Spinner<Integer> spinner = new Spinner<>(valueFactory);

		// Get 'signed' flag
		boolean signed = (minValue < 0);

		// Set properties
		String prototypeStr = "0".repeat(numDigits);
		if (signed)
			prototypeStr += "-";
		int numColumns = TextFieldUtils.getNumColumns(spinner.getEditor(), prototypeStr) + arrowButtonNumColumns;
		spinner.getEditor().setPrefColumnCount(numColumns);
		spinner.setEditable(true);

		// Set filter on editor
		SpinnerUtils.setIntegerFilter(spinner, signed);

		// Set filters and handlers for key events
		setKeyEventProcessors(spinner);

		// Create listener to set spinner value to content of editor when focus is lost
		ChangeListener<Boolean> focusListener = (observable, oldFocused, focused) ->
		{
			if (!focused)
				SpinnerUtils.updateIntegerValue(spinner);
		};

		// Add listener to focus property
		spinner.focusedProperty().addListener(focusListener);

		// Add focus listener to properties of spinner
		spinner.getProperties().put(FOCUS_LISTENER_KEY, focusListener);

		// Return spinner
		return spinner;
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns an editable spinner for {@code Double} values.  The spinner sets its value from the content
	 * of its text field ({@link Spinner#getEditor()}) when it loses keyboard focus.
	 *
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  step
	 *           the step between successive values of the spinner.
	 * @param  numDigits
	 *           the number of digits that the editor of the spinner must accommodate.
	 * @return an editable spinner for double values.
	 */

	public static Spinner<Double> doubleSpinner(
		double	minValue,
		double	maxValue,
		double	initialValue,
		double	step,
		int		numDigits)
	{
		return doubleSpinner(minValue, maxValue, initialValue, step, numDigits, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns an editable spinner for {@code Double} values.  The spinner sets its value from the content
	 * of its text field ({@link Spinner#getEditor()}) when it loses keyboard focus.
	 *
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  step
	 *           the step between successive values of the spinner.
	 * @param  numDigits
	 *           the number of digits that the editor of the spinner must accommodate.
	 * @param  formatter
	 *           the formatter that will provide the string representation of the value in the text field of the
	 *           spinner.  If it is {@code null}, a default formatter will be used.
	 * @return an editable spinner for double values.
	 */

	public static Spinner<Double> doubleSpinner(
		double			minValue,
		double			maxValue,
		double			initialValue,
		double			step,
		int				numDigits,
		DecimalFormat	formatter)
	{
		// Create value factory that clamps the value that is returned by its converter's 'fromString' method
		SpinnerValueFactory.DoubleSpinnerValueFactory valueFactory =
				new SpinnerValueFactory.DoubleSpinnerValueFactory(minValue, maxValue, initialValue, step);
		SpinnerUtils.setDoubleClampingConverter(valueFactory, formatter);

		// Create spinner
		Spinner<Double> spinner = new Spinner<>(valueFactory);

		// Get 'signed' flag
		boolean signed = (minValue < 0.0);

		// Set properties
		String prototypeStr = "0".repeat(numDigits) + ".";
		if (signed)
			prototypeStr += "-";
		int numColumns = TextFieldUtils.getNumColumns(spinner.getEditor(), prototypeStr) + arrowButtonNumColumns;
		spinner.getEditor().setPrefColumnCount(numColumns);
		spinner.setEditable(true);

		// Set filter on editor
		SpinnerUtils.setDoubleFilter(spinner, signed);

		// Set filters and handlers for key events
		setKeyEventProcessors(spinner);

		// Create listener to set spinner value to content of editor when focus is lost
		ChangeListener<Boolean> focusListener = (observable, oldFocused, focused) ->
		{
			if (!focused)
				SpinnerUtils.updateDoubleValue(spinner);
		};

		// Add listener to focus property
		spinner.focusedProperty().addListener(focusListener);

		// Add focus listener to properties of spinner
		spinner.getProperties().put(FOCUS_LISTENER_KEY, focusListener);

		// Return spinner
		return spinner;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the listener that was set on the focus property of the specified spinner by a factory method of this
	 * class.
	 *
	 * @param  spinner
	 *           the spinner whose focus listener is required.
	 * @return if the spinner was created by one of the factory methods of this class, the listener that was set on the
	 *         focus property of the specified spinner; otherwise, {@code null}.
	 */

	@SuppressWarnings("unchecked")
	public static ChangeListener<Boolean> getFocusListener(
		Spinner<?>	spinner)
	{
		return (ChangeListener<Boolean>)spinner.getProperties().get(FOCUS_LISTENER_KEY);
	}

	//------------------------------------------------------------------

	/**
	 * Sets a filter and a handler for key events on the specified spinner.  The event filter causes the F10 key to be
	 * ignored; the event handler increments and decrements the spinner value by 10 steps when, respectively, the
	 * Ctrl+Up and Ctrl+Down key combinations are pressed.
	 *
	 * @param spinner
	 *          the spinner on which the key-event filter and handler will be set.
	 */

	private static void setKeyEventProcessors(
		Spinner<?>	spinner)
	{
		// Ignore F10 key
		spinner.addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if (event.getCode() == KeyCode.F10)
				event.consume();
		});

		// Increment/decrement spinner by 10 when Ctrl+Up/Ctrl+Down pressed
		spinner.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if (event.isControlDown())
			{
				int numSteps = 10;
				switch (event.getCode())
				{
					case UP:
						spinner.increment(numSteps);
						break;

					case DOWN:
						spinner.decrement(numSteps);
						break;

					default:
						// do nothing
						break;
				}
			}
		});
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
