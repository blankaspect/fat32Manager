/*====================================================================*\

VolumeLabelDialog.java

Class: volume-label dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.time.LocalDateTime;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import javafx.stage.Window;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.ui.jfx.button.Buttons;
import uk.blankaspect.ui.jfx.button.ImageDataButton;

import uk.blankaspect.ui.jfx.date.DateFieldPane;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.label.Labels;

import uk.blankaspect.ui.jfx.textfield.FilterFactory;
import uk.blankaspect.ui.jfx.textfield.TimeField;

//----------------------------------------------------------------------


// CLASS: VOLUME-LABEL DIALOG


/**
 * This class implements a modal dialog in which a volume label may be edited.
 */

public class VolumeLabelDialog
	extends SimpleModalDialog<VolumeLabelDialog.Result>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The horizontal gap between adjacent columns of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The vertical gap between adjacent rows of the control pane. */
	private static final	double	CONTROL_PANE_V_GAP	= 6.0;

	/** The number of columns of the <i>volume label</i> field. */
	private static final	int		VOLUME_LABEL_FIELD_NUM_COLUMNS	= 11;

	private static final	String	DATE_SEPARATORS	= "-/.";
	private static final	String	TIME_SEPARATORS	= ":.";

	private static final	String	ZERO_TIME_PATTERN	= "00%s00%s00";

	/** Miscellaneous strings. */
	private static final	String	VOLUME_LABEL_STR			= "Volume label";
	private static final	String	USE_CURRENT_DATE_TIME_STR	= "Use current date and time";
	private static final	String	DATE_STR					= "Date";
	private static final	String	TIME_STR					= "Time";
	private static final	String	SET_TO_ZERO_STR				= "Set time to zero";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	boolean	useCurrentDateTime;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Result	result;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a modal dialog in which a volume label may be edited.
	 *
	 * @param owner
	 *          the window that will own this dialog, or {@code null} for a top-level dialog that has no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param volumeLabel
	 *          the initial volume label.
	 * @param modTime
	 *          the initial modification time.
	 */

	private VolumeLabelDialog(
		Window			owner,
		String			title,
		String			volumeLabel,
		LocalDateTime	modTime)
	{
		// Call superclass constructor
		super(owner, MethodHandles.lookup().lookupClass().getName(), null, title);

		// Create control pane
		GridPane controlPane = new GridPane();
		controlPane.setHgap(CONTROL_PANE_H_GAP);
		controlPane.setVgap(CONTROL_PANE_V_GAP);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
		column.setFillWidth(false);
		controlPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		column.setFillWidth(false);
		controlPane.getColumnConstraints().add(column);

		// Initialise row index
		int row = 0;

		// Create field: volume label
		TextField volumeLabelField = new TextField(volumeLabel);
		volumeLabelField.setPrefColumnCount(VOLUME_LABEL_FIELD_NUM_COLUMNS);
		volumeLabelField.setTextFormatter(new TextFormatter<>(FilterFactory.createFilter(VOLUME_LABEL_FIELD_NUM_COLUMNS,
																						 (ch, index, text) ->
				Fat32Volume.isValidVolumeLabelChar(ch) ? Character.toString(ch).toUpperCase() : "")));
		controlPane.addRow(row++, new Label(VOLUME_LABEL_STR), volumeLabelField);

		// Create check box: use current date and time
		CheckBox useCurrDateTimeCheckBox = new CheckBox(USE_CURRENT_DATE_TIME_STR);
		useCurrDateTimeCheckBox.setSelected(useCurrentDateTime);
		GridPane.setMargin(useCurrDateTimeCheckBox, new Insets(2.0, 0.0, 2.0, 0.0));
		controlPane.add(useCurrDateTimeCheckBox, 1, row++);

		// Create label: date
		Label dateLabel = new Label(DATE_STR);

		// Create pane: date
		DateFieldPane datePane = new DateFieldPane(DATE_SEPARATORS);
		if (modTime != null)
			datePane.setDate(modTime.toLocalDate());

		// Create field: time
		TimeField timeField = new TimeField(TIME_SEPARATORS);
		if (modTime != null)
			timeField.setTime(modTime.toLocalTime());

		// Create button: set to zero
		ImageDataButton setToZeroButton = Images.imageButton(Images.ImageId.SET_TO_ZERO, SET_TO_ZERO_STR);
		HBox.setMargin(setToZeroButton, new Insets(0.0, 0.0, 0.0, -6.0));
		setToZeroButton.setOnAction(event ->
		{
			char separator = TIME_SEPARATORS.charAt(0);
			timeField.setText(String.format(ZERO_TIME_PATTERN, separator, separator));
		});
		setToZeroButton.updateImage();

		// Create pane: time
		HBox timePane = new HBox(CONTROL_PANE_H_GAP, Labels.hNoShrink(TIME_STR), timeField, setToZeroButton);
		timePane.setAlignment(Pos.CENTER_LEFT);

		// Create pane: date and time
		HBox dateTimePane = new HBox(16.0, datePane, timePane);
		dateTimePane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, dateLabel, dateTimePane);

		// Add control pane to content pane
		addContent(controlPane);

		// Create button: OK
		Button okButton = Buttons.hNoShrink(OK_STR);
		okButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		okButton.setOnAction(event ->
		{
			LocalDateTime dateTime = useCurrDateTimeCheckBox.isSelected()
														? LocalDateTime.now()
														: LocalDateTime.of(datePane.getDate(), timeField.getTime());
			result = new Result(volumeLabelField.getText(), dateTime);
			hide();
		});
		addButton(okButton, HPos.RIGHT);

		// Create procedure to enable/disable date and time fields according to 'selected' state of 'use current date
		// and time' check box
		IProcedure0 updateDateTime = () ->
		{
			boolean disabled = useCurrDateTimeCheckBox.isSelected();
			dateLabel.setDisable(disabled);
			dateTimePane.setDisable(disabled);
		};

		// Create procedure to enable/disable 'OK' button according to validity of date and time
		IProcedure0 updateOkButton = () ->
		{
			boolean valid = useCurrDateTimeCheckBox.isSelected() || (datePane.isDateValid() && timeField.isTimeValid());
			okButton.setDisable(!valid);
		};

		// Enable/disable date and time fields and 'OK' button when 'selected' state of 'use current date and time'
		// check box changes
		useCurrDateTimeCheckBox.selectedProperty().addListener(observable ->
		{
			updateDateTime.invoke();
			updateOkButton.invoke();
		});

		// Enable/disable 'OK' button when date or time changes
		datePane.getDateField().textProperty().addListener(observable -> updateOkButton.invoke());
		timeField.textProperty().addListener(observable -> updateOkButton.invoke());

		// Enable/disable date and time fields according to 'selected' state of 'use current date and time' check box
		updateDateTime.invoke();

		// Enable/disable 'OK' button according to validity of date and time
		updateOkButton.invoke();

		// Create button: cancel
		Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
		cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		cancelButton.setOnAction(event -> requestClose());
		addButton(cancelButton, HPos.RIGHT);

		// Fire 'cancel' button if Escape key is pressed; fire 'OK' button if Ctrl+Enter is pressed
		setKeyFireButton(cancelButton, okButton);

		// Save state when dialog is closed
		setOnHiding(event -> useCurrentDateTime = useCurrDateTimeCheckBox.isSelected());

		// Apply new style sheet to scene
		applyStyleSheet();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a modal dialog in which a volume label may be edited, displays the dialog and returns
	 * the result.
	 *
	 * @param  owner
	 *           the window that will own this dialog, or {@code null} for a top-level dialog that has no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  volumeLabel
	 *           the initial volume label.
	 * @param  modTime
	 *           the initial modification time.
	 * @return the result of the dialog, or {@code null} if the dialog was cancelled.
	 */

	public static Result show(
		Window			owner,
		String			title,
		String			volumeLabel,
		LocalDateTime	modTime)
	{
		return new VolumeLabelDialog(owner, title, volumeLabel, modTime).showDialog();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected Result getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: RESULT


	public record Result(
		String			volumeLabel,
		LocalDateTime	modTime)
	{ }

	//==================================================================

}

//----------------------------------------------------------------------
