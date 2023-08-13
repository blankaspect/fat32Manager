/*====================================================================*\

DateFieldPane.java

Class: date-field pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.date;

//----------------------------------------------------------------------


// IMPORTS


import java.io.ByteArrayInputStream;

import java.time.DateTimeException;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import javafx.geometry.Pos;

import javafx.scene.control.TextField;

import javafx.scene.image.Image;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.HBox;

import uk.blankaspect.ui.jfx.button.ImageButton;

import uk.blankaspect.ui.jfx.dialog.DateSelectionDialog;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.textfield.DateField;

import uk.blankaspect.ui.jfx.window.WindowUtils;

//----------------------------------------------------------------------


// CLASS: DATE-FIELD PANE


/**
 * This class implements a JavaFX pane that contains
 * <ul>
 *   <li>a {@linkplain DateField date field}: a text field in which a calendar date may be edited, and</li>
 *   <li>a button that causes a {@linkplain DateSelectionDialog date-selection dialog} to be displayed.</li>
 * </ul>
 * The background colour of the date field changes according to the content of the field; it is
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

public class DateFieldPane
	extends HBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The horizontal gap between adjacent components of the pane. */
	private static final	double	H_GAP	= 2.0;

	/** The image that is used for the button that invokes the date-selection dialog. */
	private static final	Image	CALENDAR_IMAGE	= new Image(new ByteArrayInputStream(ImageData.CALENDAR));

	/** Miscellaneous strings. */
	private static final	String	SELECT_DATE_STR	= "Select date";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The date field. */
	private	DateField	dateField;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a date-field pane that accepts the specified separators between components of a date in
	 * the date field.
	 *
	 * @param separators
	 *          the characters that will be allowed between components of a date in the date field.  At least one
	 *          separator must be specified.
	 */

	public DateFieldPane(
		char...	separators)
	{
		// Call alternative constructor
		this(new String(separators));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a date-field pane that accepts the specified separators between components of a date in
	 * the date field.
	 *
	 * @param separators
	 *          a string whose characters will be allowed between components of a date in the date field.  At least one
	 *          separator must be specified.
	 */

	public DateFieldPane(
		String	separators)
	{
		// Validate arguments
		if (separators.isEmpty())
			throw new IllegalArgumentException("No separators");

		// Set attributes
		setSpacing(H_GAP);
		setAlignment(Pos.CENTER);

		// Create date field and add it to this pane
		dateField = new DateField(separators);
		getChildren().add(dateField);

		// Create 'select date' button and add it to this pane
		ImageButton selectDateButton = new ImageButton(themedImage(CALENDAR_IMAGE), SELECT_DATE_STR);
		getChildren().add(selectDateButton);

		// Display date-selection dialog when button is pressed
		selectDateButton.setOnAction(event ->
		{
			// Get date from date field
			LocalDate date = LocalDate.now();
			if (!dateField.getText().isEmpty())
			{
				try
				{
					date = getDate();
				}
				catch (DateTimeException e)
				{
					// ignore
				}
			}

			// Create date-selection dialog
			DateSelectionDialog dateSelectionDialog = new DateSelectionDialog(SceneUtils.getWindow(this), date, true);

			// Display date-selection dialog relative to button
			WindowUtils.showAtRelativeLocation(dateSelectionDialog, selectDateButton);

			// Update date from result of dialog
			date = dateSelectionDialog.getResult();
			if (date != null)
				setDate(date);
		});

		// Display date-selection dialog when Ctrl+space is pressed on date field
		dateField.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if ((event.getCode() == KeyCode.SPACE) && event.isControlDown())
			{
				selectDateButton.fire();
				event.consume();
			}
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	private static Image themedImage(
		Image	image)
	{
		return StyleManager.INSTANCE.notUsingStyleSheet() ? image : StyleManager.INSTANCE.getTheme().processImage(image);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the date from the text field of this pane.
	 *
	 * @return the date from the text field of this pane, or {@code null} if the text field is empty.
	 * @throws DateTimeException
	 *           if the text field does not contain a valid date.
	 */

	public LocalDate getDate()
	{
		return dateField.getDate();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the content of the text field of this pane to a string representation of the specified value.  The string
	 * representation has the form <i>yyyySmmSdd</i>, where
	 * <ul>
	 *   <li><i>yyyy</i> is the year,</li>
	 *   <li><i>mm</i> is the month,</li>
	 *   <li><i>dd</i> is the day of the month, and</li>
	 *   <li><i>S</i> is the first separator that was specified in the constructor.</li>
	 * </ul>
	 *
	 * @param  date
	 *           the date whose string representation will be set as the content of the text field.
	 * @throws DateTimeException
	 *           if the year of <i>date</i> is outside the range 1600..3999 or <i>date</i> is otherwise invalid.
	 */

	public void setDate(
		LocalDate	date)
	{
		dateField.setDate(date);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the content of the date field represents a valid date.
	 *
	 * @return {@code true} if the content of the date field represents a valid date.
	 */

	public boolean isDateValid()
	{
		return dateField.isDateValid();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the date field of this pane.
	 *
	 * @return the date field of this pane.
	 */

	public TextField getDateField()
	{
		return dateField;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the date formatter that is used by {@link #setDate(LocalDate)}.
	 *
	 * @return the date formatter that is used by {@link #setDate(LocalDate)}.
	 */

	public DateTimeFormatter getFormatter()
	{
		return dateField.getFormatter();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Image data
////////////////////////////////////////////////////////////////////////

	private interface ImageData
	{
		byte[]	CALENDAR	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x11, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xF0, (byte)0x31, (byte)0x94,
			(byte)0x5F, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x42, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0x63, (byte)0xD8, (byte)0xB0, (byte)0x61, (byte)0xC3,
			(byte)0x7F, (byte)0x4A, (byte)0x31, (byte)0x03, (byte)0x88, (byte)0xB8, (byte)0x70, (byte)0xE1,
			(byte)0x02, (byte)0xD9, (byte)0x18, (byte)0xC5, (byte)0x10, (byte)0x06, (byte)0x06, (byte)0x06,
			(byte)0xB2, (byte)0x68, (byte)0xEA, (byte)0xBA, (byte)0x84, (byte)0x12, (byte)0x40, (byte)0x94,
			(byte)0x21, (byte)0x0E, (byte)0x0E, (byte)0x0E, (byte)0x78, (byte)0x69, (byte)0xA2, (byte)0x0C,
			(byte)0x21, (byte)0x04, (byte)0x50, (byte)0x0C, (byte)0x41, (byte)0xB7, (byte)0x81, (byte)0x58,
			(byte)0x7A, (byte)0xD4, (byte)0x25, (byte)0x98, (byte)0xF4, (byte)0xC0, (byte)0xB9, (byte)0x04,
			(byte)0x1D, (byte)0x50, (byte)0xD7, (byte)0x25, (byte)0x94, (byte)0x62, (byte)0x00, (byte)0xC7,
			(byte)0xA6, (byte)0x95, (byte)0x89, (byte)0x31, (byte)0x86, (byte)0x7B, (byte)0x96, (byte)0x00,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45, (byte)0x4E, (byte)0x44, (byte)0xAE,
			(byte)0x42, (byte)0x60, (byte)0x82
		};
	}

	//==================================================================

}

//----------------------------------------------------------------------
