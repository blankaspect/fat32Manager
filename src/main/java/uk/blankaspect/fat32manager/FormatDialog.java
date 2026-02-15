/*====================================================================*\

FormatDialog.java

Class: format dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;
import java.util.SplittableRandom;

import java.util.function.UnaryOperator;

import javafx.collections.FXCollections;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import javafx.scene.paint.Color;

import javafx.stage.Window;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IFunction0;
import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.number.NumberUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.dialog.ConfirmationDialog;
import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

import uk.blankaspect.ui.jfx.label.Labels;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;
import uk.blankaspect.ui.jfx.spinner.SpinnerFactory;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.TextUtils;

import uk.blankaspect.ui.jfx.textfield.FilterFactory;

//----------------------------------------------------------------------


// CLASS: FORMAT DIALOG


/**
 * This class implements a modal dialog in which the parameters for formatting a volume may be input.
 */

public class FormatDialog
	extends SimpleModalDialog<FormatDialog.Result>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default number of sectors per cluster. */
	private static final	Integer	DEFAULT_SECTORS_PER_CLUSTER	= 8;

	/** The horizontal gap between adjacent columns of the control pane. */
	private static final	double	CONTROL_PANE_H_GAP	= 6.0;

	/** The vertical gap between adjacent rows of the control pane. */
	private static final	double	CONTROL_PANE_V_GAP	= 6.0;

	/** The number of columns of the <i>volume ID</i> field. */
	private static final	int		VOLUME_ID_FIELD_NUM_COLUMNS		= 8;

	/** The number of columns of the <i>volume label</i> field. */
	private static final	int		VOLUME_LABEL_FIELD_NUM_COLUMNS	= 11;

	/** The number of columns of the <i>formatter name</i> field. */
	private static final	int		FORMATTER_NAME_FIELD_NUM_COLUMNS	= 8;

	/** The number of digits of the <i>number of reserved sectors</i> spinner. */
	private static final	int		MIN_NUM_RESERVED_SECTORS_SPINNER_NUM_DIGITS	= 10;

	/** The margins around a check box. */
	private static final	Insets	CHECK_BOX_MARGINS	= new Insets(2.0, 0.0, 2.0, 0.0);

	/** The padding around an information label. */
	private static final	Insets	INFO_LABEL_PADDING	= new Insets(2.0, 6.0, 2.0, 6.0);

	/** Miscellaneous strings. */
	private static final	String	VOLUME_ID_STR					= "Volume ID";
	private static final	String	VOLUME_LABEL_STR				= "Volume label";
	private static final	String	FORMATTER_NAME_STR				= "Formatter name";
	private static final	String	MIN_NUM_RESERVED_SECTORS_STR	= "Minimum number of reserved sectors";
	private static final	String	CLUSTER_ALIGNMENT_STR			= "Cluster alignment";
	private static final	String	ALIGN_FATS_TO_CLUSTERS_STR		= "Align FATs to clusters";
	private static final	String	SECTORS_PER_CLUSTER_STR			= "Sectors per cluster";
	private static final	String	BYTES_STR						= " bytes";
	private static final	String	FORMAT_STR						= "Format";
	private static final	String	CONFIRM_FORMAT_STR				=
			"Formatting will erase the contents of the volume.\nDo you want to proceed?";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.INFO_LABEL_TEXT,
			CssSelector.builder()
					.cls(StyleClass.FORMAT_DIALOG_ROOT)
					.desc(StyleClass.INFO_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.INFO_LABEL_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.FORMAT_DIALOG_ROOT)
					.desc(StyleClass.INFO_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.INFO_LABEL_BORDER,
			CssSelector.builder()
					.cls(StyleClass.FORMAT_DIALOG_ROOT)
					.desc(StyleClass.INFO_LABEL)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	FORMAT_DIALOG_ROOT	= StyleConstants.APP_CLASS_PREFIX + "format-dialog-root";

		String	INFO_LABEL	= StyleConstants.CLASS_PREFIX + "info-label";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	INFO_LABEL_BACKGROUND	= PREFIX + "infoLabel.background";
		String	INFO_LABEL_BORDER		= PREFIX + "infoLabel.border";
		String	INFO_LABEL_TEXT			= PREFIX + "infoLabel.text";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	FormatParams		params	= new FormatParams();
	private static	SplittableRandom	prng	= new SplittableRandom();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Result	result;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(FormatDialog.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a modal dialog in which the parameters for formatting a volume may be input.
	 *
	 * @param owner
	 *          the window that will own this dialog, or {@code null} for a top-level dialog that has no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param bytesPerSector
	 *          the size of a sector of the volume.
	 * @param numSectors
	 *          the number of sectors of the volume.
	 */

	private FormatDialog(
		Window	owner,
		String	title,
		int		bytesPerSector,
		int		numSectors)
	{
		// Call superclass constructor
		super(owner, MethodHandles.lookup().lookupClass().getName(), null, title);

		// Set style class on root node of scene graph
		getScene().getRoot().getStyleClass().add(StyleClass.FORMAT_DIALOG_ROOT);

		// Create control pane
		GridPane controlPane = new GridPane();
		controlPane.setHgap(CONTROL_PANE_H_GAP);
		controlPane.setVgap(CONTROL_PANE_V_GAP);
		controlPane.setAlignment(Pos.CENTER);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
		column.setHgrow(Priority.NEVER);
		controlPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		column.setHgrow(Priority.ALWAYS);
		controlPane.getColumnConstraints().add(column);

		// Initialise row index
		int row = 0;

		// Create pane: volume ID
		VolumeIdPane volumeIdPane = new VolumeIdPane();
		GridPane.setFillWidth(volumeIdPane, false);
		controlPane.addRow(row++, new Label(VOLUME_ID_STR), volumeIdPane);

		// Create field: volume label
		TextField volumeLabelField = new TextField();
		volumeLabelField.setPrefColumnCount(VOLUME_LABEL_FIELD_NUM_COLUMNS);
		volumeLabelField.setTextFormatter(
				new TextFormatter<>(FilterFactory.createFilter(VOLUME_LABEL_FIELD_NUM_COLUMNS, (ch, index, text) ->
						Fat32Volume.isValidVolumeLabelChar(ch) ? Character.toString(ch).toUpperCase() : "")));
		GridPane.setFillWidth(volumeLabelField, false);
		controlPane.addRow(row++, new Label(VOLUME_LABEL_STR), volumeLabelField);

		// Create field: formatter name
		TextField formatterNameField = new TextField(params.getFormatterName());
		formatterNameField.setPrefColumnCount(FORMATTER_NAME_FIELD_NUM_COLUMNS);
		formatterNameField.setTextFormatter(
				new TextFormatter<>(FilterFactory.createFilter(FORMATTER_NAME_FIELD_NUM_COLUMNS, (ch, index, text) ->
						Fat32Volume.isValidVolumeLabelChar(ch) ? Character.toString(ch) : "")));
		GridPane.setFillWidth(formatterNameField, false);
		controlPane.addRow(row++, new Label(FORMATTER_NAME_STR), formatterNameField);

		// Create spinner: minimum number of reserved sectors
		Spinner<Integer> minNumReservedSectorsSpinner =
				SpinnerFactory.integerSpinner(Fat32Volume.FORMAT_MIN_NUM_RESERVED_SECTORS, numSectors / 2,
											  params.getMinNumReservedSectors(),
											  MIN_NUM_RESERVED_SECTORS_SPINNER_NUM_DIGITS);
		controlPane.addRow(row++, new Label(MIN_NUM_RESERVED_SECTORS_STR), minNumReservedSectorsSpinner);

		// Create function to get minimum number of reserved sectors
		IFunction0<Integer> getMinNumReservedSectors = minNumReservedSectorsSpinner::getValue;

		// Create choice box: cluster alignment
		CollectionSpinner<ClusterAlignment> clusterAlignmentSpinner =
				CollectionSpinner.leftRightH(HPos.LEFT, true, ClusterAlignment.class, params.getClusterAlignment(),
											 null, null);
		controlPane.addRow(row++, new Label(CLUSTER_ALIGNMENT_STR), clusterAlignmentSpinner);

		// Create function to get cluster alignment
		IFunction0<ClusterAlignment> getClusterAlignment = clusterAlignmentSpinner::getItem;

		// Create check box: align FATs
		CheckBox alignFatsCheckBox = new CheckBox(ALIGN_FATS_TO_CLUSTERS_STR);
		alignFatsCheckBox.disableProperty()
				.bind(clusterAlignmentSpinner.itemProperty().isEqualTo(ClusterAlignment.NONE));
		alignFatsCheckBox.setSelected(params.isAlignFatsToClusters());
		GridPane.setMargin(alignFatsCheckBox, CHECK_BOX_MARGINS);
		controlPane.add(alignFatsCheckBox, 1, row++);

		// Create label: sectors per cluster
		Label sectorsPerClusterLabel = Labels.hNoShrink();
		sectorsPerClusterLabel.setAlignment(Pos.CENTER_RIGHT);
		sectorsPerClusterLabel.setPadding(INFO_LABEL_PADDING);
		sectorsPerClusterLabel.setTextFill(getColour(ColourKey.INFO_LABEL_TEXT));
		sectorsPerClusterLabel.setBackground(
				SceneUtils.createColouredBackground(getColour(ColourKey.INFO_LABEL_BACKGROUND)));
		sectorsPerClusterLabel.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.INFO_LABEL_BORDER)));

		// Create spinner: sectors per cluster
		CollectionSpinner<Integer> sectorsPerClusterSpinner =
				CollectionSpinner.leftRightH(HPos.CENTER, false, List.of(), 0, null, null);
		sectorsPerClusterSpinner.itemProperty().addListener((observable, oldSectorsPerCluster, sectorsPerCluster) ->
		{
			sectorsPerClusterLabel.setText((sectorsPerCluster == null)
													? null
													: sectorsPerCluster * bytesPerSector + BYTES_STR);
		});

		// Create pane: sectors per cluster
		HBox sectorsPerClusterPane = new HBox(CONTROL_PANE_H_GAP, sectorsPerClusterSpinner, sectorsPerClusterLabel);
		sectorsPerClusterPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.addRow(row++, new Label(SECTORS_PER_CLUSTER_STR), sectorsPerClusterPane);

		// Create function to get sectors per cluster
		IFunction0<Integer> getSectorsPerCluster = sectorsPerClusterSpinner::getItem;

		// Create procedure to update list of sectors per cluster
		IProcedure0 updateSectorsPerCluster = () ->
		{
			// Get list of valid sectors per cluster
			Integer sectorsPerCluster = getSectorsPerCluster.invoke();
			List<Integer> validSectorsPerCluster =
					Fat32Volume.getValidSectorsPerCluster(bytesPerSector, numSectors, getMinNumReservedSectors.invoke(),
														  getClusterAlignment.invoke(), alignFatsCheckBox.isSelected());

			// Update spinner
			String prototypeText = "0".repeat(NumberUtils.getNumDecDigitsInt(Fat32Volume.MAX_SECTORS_PER_CLUSTER) + 1);
			sectorsPerClusterSpinner.setItems(FXCollections.observableList(validSectorsPerCluster), prototypeText,
											  null);
			sectorsPerClusterSpinner.setItem(validSectorsPerCluster.contains(sectorsPerCluster)
															? sectorsPerCluster
															: DEFAULT_SECTORS_PER_CLUSTER);

			// Update minimum width of label
			int spc = validSectorsPerCluster.isEmpty()
							? 1
							: validSectorsPerCluster.get(validSectorsPerCluster.size() - 1);
			String text = spc * bytesPerSector + BYTES_STR;
			Insets insets = sectorsPerClusterLabel.getInsets();
			sectorsPerClusterLabel.setMinWidth(
					Math.ceil(TextUtils.textWidth(text) + insets.getLeft() + insets.getRight()));
		};

		// Initialise list of sectors per cluster
		updateSectorsPerCluster.invoke();

		// Update list of sectors per cluster when cluster alignment or number of reserved sectors changes
		minNumReservedSectorsSpinner.valueProperty().addListener(observable -> updateSectorsPerCluster.invoke());
		clusterAlignmentSpinner.itemProperty().addListener(observable -> updateSectorsPerCluster.invoke());
		alignFatsCheckBox.selectedProperty().addListener(observable -> updateSectorsPerCluster.invoke());

		// Add control pane to content pane
		addContent(controlPane);

		// Create button: format
		Button formatButton = Buttons.hNoShrink(FORMAT_STR);
		formatButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		formatButton.setOnAction(event ->
		{
			// Display dialog to confirm format
			if (ConfirmationDialog.show(this, title, MessageIcon32.WARNING.get(), CONFIRM_FORMAT_STR, FORMAT_STR))
			{
				String text = volumeIdPane.getText();
				int volumeId = text.isEmpty() ? prng.nextInt() : NumberUtils.parseIntHex(text);
				result = new Result(volumeId, volumeLabelField.getText(), formatterNameField.getText(),
									bytesPerSector, getMinNumReservedSectors.invoke(), getSectorsPerCluster.invoke(),
									getClusterAlignment.invoke(), alignFatsCheckBox.isSelected());
			}

			// Hide dialog
			hide();
		});
		addButton(formatButton, HPos.RIGHT);

		// Create procedure to update 'format' button
		IProcedure0 updateFormatButton = () ->
		{
			int length = volumeIdPane.field1.getLength() + volumeIdPane.field2.getLength();
			formatButton.setDisable((length > 0) && (length < VOLUME_ID_FIELD_NUM_COLUMNS));
		};

		// Update 'format' button when volume ID changes
		volumeIdPane.field1.textProperty().addListener(observable -> updateFormatButton.invoke());
		volumeIdPane.field2.textProperty().addListener(observable -> updateFormatButton.invoke());

		// Update 'format' button
		updateFormatButton.invoke();

		// Create button: cancel
		Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
		cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		cancelButton.setOnAction(event -> requestClose());
		addButton(cancelButton, HPos.RIGHT);

		// Fire 'cancel' button if Escape key is pressed
		setKeyFireButton(cancelButton, null);

		// Save state when dialog is closed
		setOnHiding(event -> params = new FormatParams(bytesPerSector, getMinNumReservedSectors.invoke(),
													   getClusterAlignment.invoke(), alignFatsCheckBox.isSelected(),
													   formatterNameField.getText()));

		// Request focus on volume label
		volumeLabelField.requestFocus();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static FormatParams getParams()
	{
		return params;
	}

	//------------------------------------------------------------------

	public static void setParams(
		FormatParams	params)
	{
		FormatDialog.params = params;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a modal dialog in which the parameters for formatting a volume may be entered, displays
	 * the dialog and returns the result.
	 *
	 * @param  owner
	 *           the window that will own this dialog, or {@code null} for a top-level dialog that has no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  bytesPerSector
	 *           the size of a sector of the volume.
	 * @param  numSectors
	 *           the number of sectors of the volume.
	 * @return the result of the dialog, or {@code null} if the dialog was cancelled.
	 */

	public static Result show(
		Window	owner,
		String	title,
		int		bytesPerSector,
		int		numSectors)
	{
		return new FormatDialog(owner, title, bytesPerSector, numSectors).showDialog();
	}

	//------------------------------------------------------------------

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
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected Result getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: RESULT


	public static class Result
		extends FormatParams
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int		volumeId;
		private	String	volumeLabel;
		private	int		sectorsPerCluster;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Result(
			int					volumeId,
			String				volumeLabel,
			String				formatterName,
			int					bytesPerSector,
			int					numReservedSectors,
			int					sectorsPerCluster,
			ClusterAlignment	clusterAlignment,
			boolean				alignFats)
		{
			// Call superclass constructor
			super(bytesPerSector, numReservedSectors, clusterAlignment, alignFats, formatterName);

			// Initialise instance variables
			this.volumeId = volumeId;
			this.volumeLabel = volumeLabel;
			this.sectorsPerCluster = sectorsPerCluster;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getVolumeId()
		{
			return volumeId;
		}

		//--------------------------------------------------------------

		public String getVolumeLabel()
		{
			return volumeLabel;
		}

		//--------------------------------------------------------------

		public int getSectorsPerCluster()
		{
			return sectorsPerCluster;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: VOLUME-IDENTIFIER PANE


	private static class VolumeIdPane
		extends HBox
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	FIELD_NUM_COLUMNS	= 4;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	TextField	field1;
		private	TextField	field2;
		private	boolean		editing;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private VolumeIdPane()
		{
			// Set properties
			setSpacing(2.0);
			setAlignment(Pos.CENTER_LEFT);

			// Create filter
			UnaryOperator<TextFormatter.Change> filter = FilterFactory.hexInteger(FIELD_NUM_COLUMNS);

			// Create field 1
			field1 = new TextField()
			{
				@Override
				public void replaceSelection(String replacement)
				{
					// Get text of field
					String text = getText();

					// Replace null with empty string
					if (text == null)
						text = "";

					// Get selection
					IndexRange range = getSelection();

					// Initialise output buffer
					StringBuilder buffer = new StringBuilder(32);

					// Append text before selection
					buffer.append(text.substring(0, range.getStart()));

					// Append filtered replacement
					for (int i = 0; i < replacement.length(); i++)
					{
						String str = FilterFactory.HEX_CHAR_FILTER.apply(replacement.charAt(i), i, replacement);
						if (!StringUtils.isNullOrEmpty(str))
							buffer.append(str);
					}

					// Append text after selection
					buffer.append(text.substring(range.getEnd()));

					// Get output text
					text = buffer.toString();

					// Get length of output text
					int length = text.length();
					if (length <= FIELD_NUM_COLUMNS)
					{
						field2.clear();
						setText(text);
						if (length == FIELD_NUM_COLUMNS)
							field2.requestFocus();
					}
					else
					{
						setText(text.substring(0, FIELD_NUM_COLUMNS));

						field2.setText(text.substring(FIELD_NUM_COLUMNS));
						field2.requestFocus();
						field2.deselect();
						field2.end();
					}
				}
			};
			field1.setPrefColumnCount(FIELD_NUM_COLUMNS);
			field1.setTextFormatter(new TextFormatter<>(filter));

			// Create field 2
			field2 = new TextField();
			field2.setPrefColumnCount(FIELD_NUM_COLUMNS);
			field2.setTextFormatter(new TextFormatter<>(filter));

			// Create procedure to move focus to field 1
			IProcedure0 moveToField1 = () ->
			{
				editing = true;
				field1.requestFocus();
				field1.deselect();
				field1.end();
				editing = false;
			};

			// Create procedure to move focus to field 2
			IProcedure0 moveToField2 = () ->
			{
				editing = true;
				field2.requestFocus();
				field2.deselect();
				field2.home();
				editing = false;
			};

			// Handle change in position of caret of field 1
			field1.caretPositionProperty().addListener((observable, oldPosition, position) ->
			{
				int pos = position.intValue();
				if (!editing && (pos == FIELD_NUM_COLUMNS) && (pos == field1.getAnchor()))
					moveToField2.invoke();
			});

			// Handle 'key pressed' events on field 1
			field1.addEventHandler(KeyEvent.KEY_PRESSED, event ->
			{
				if ((field1.getCaretPosition() == FIELD_NUM_COLUMNS) && (event.getCode() == KeyCode.RIGHT))
					moveToField2.invoke();
			});

			// Handle 'key typed' events on field 1
			field1.addEventHandler(KeyEvent.KEY_TYPED, event ->
			{
				if (field1.getCaretPosition() == FIELD_NUM_COLUMNS)
				{
					String charStr = event.getCharacter();
					if ((charStr.length() == 1)
							&& !FilterFactory.HEX_CHAR_FILTER.apply(charStr.charAt(0), 0, charStr).isEmpty())
					{
						moveToField2.invoke();
						field2.insertText(0, charStr);
						event.consume();
					}
				}
			});

			// Handle change in position of caret of field 2
			field2.caretPositionProperty().addListener((observable, oldPosition, position) ->
			{
				int pos = position.intValue();
				if (!editing && (pos == 0) && (pos == field2.getAnchor())
						&& (field1.getLength() < FIELD_NUM_COLUMNS) && (field2.getLength() == 0))
					moveToField1.invoke();
			});

			// Handle 'key pressed' events on field 2
			field2.addEventHandler(KeyEvent.KEY_PRESSED, event ->
			{
				if (field2.getCaretPosition() == 0)
				{
					KeyCode keyCode = event.getCode();
					if ((keyCode == KeyCode.BACK_SPACE) || (keyCode == KeyCode.LEFT))
						moveToField1.invoke();
				}
			});

			// Add children
			getChildren().addAll(field1, Labels.hNoShrink("\u2013"), field2);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private String getText()
		{
			// Get text from field 1
			String text1 = field1.getText();
			if (text1 == null)
				text1 = "";

			// Get text from field 2
			String text2 = field2.getText();
			if (text2 == null)
				text2 = "";

			// Return combined text
			return text1 + text2;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
