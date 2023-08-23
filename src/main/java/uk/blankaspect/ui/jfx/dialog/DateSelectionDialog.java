/*====================================================================*\

DateSelectionDialog.java

Class: date-selection dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.time.LocalDate;

import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Side;

import javafx.scene.Scene;

import javafx.scene.control.Button;

import javafx.scene.input.KeyEvent;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.container.DialogButtonPane;

import uk.blankaspect.ui.jfx.date.DateSelectionPane;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: DATE-SELECTION DIALOG


/**
 * This class implements an undecorated modal dialog that contains a {@linkplain DateSelectionPane pane} from which a
 * calendar date may be selected.
 */

public class DateSelectionDialog
	extends Stage
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The padding around a button. */
	private static final	Insets	BUTTON_PADDING	= new Insets(1.0, 8.0, 1.0, 8.0);

	/** The padding around the button pane. */
	private static final	Insets	BUTTON_PANE_PADDING	= new Insets(3.0, 6.0, 3.0, 6.0);

	/** The object that is used as the value of the <i>group</i> property of a button, to equalise button widths. */
	private static final	Object	BUTTON_GROUP	= new Object();

	/** Miscellaneous strings. */
	private static final	String	OK_STR		= "OK";
	private static final	String	CANCEL_STR	= "Cancel";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.BORDER,
			CssSelector.builder()
						.cls(StyleClass.DATE_SELECTION_DIALOG)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.BUTTON_PANE_BORDER,
			CssSelector.builder()
						.cls(StyleClass.DATE_SELECTION_DIALOG)
						.desc(StyleClass.BUTTON_PANE)
						.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.DATE_SELECTION_DIALOG)
									.desc(StyleClass.BUTTON_PANE)
									.build())
						.borders(Side.TOP)
						.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	BUTTON_PANE				= StyleConstants.CLASS_PREFIX + "button-pane";
		String	DATE_SELECTION_DIALOG	= StyleConstants.CLASS_PREFIX + "date-selection-dialog";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	BORDER				= PREFIX + "border";
		String	BUTTON_PANE_BORDER	= PREFIX + "buttonPane.border";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The result of this dialog. */
	private	LocalDate	result;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(DateSelectionDialog.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a date-selection dialog.
	 *
	 * @param owner
	 *          the owner of the dialog.  If {@code null}, the dialog will have no owner.
	 * @param date
	 *          the initial date that will be selected in the date-selection pane.
	 * @param showAdjacentMonths
	 *          if {@code true}, the days of adjacent months will be shown in the pages of the date-selection pane.
	 */

	public DateSelectionDialog(
		Window		owner,
		LocalDate	date,
		boolean		showAdjacentMonths)
	{
		// Call superclass constructor
		super(StageStyle.TRANSPARENT);

		// Set properties
		initModality(Modality.APPLICATION_MODAL);
		initOwner(owner);
		setResizable(false);

		// Create date-selection pane
		DateSelectionPane dateSelectionPane = new DateSelectionPane(date, showAdjacentMonths);
		VBox.setVgrow(dateSelectionPane, Priority.ALWAYS);

		// Create button pane
		DialogButtonPane buttonPane = new DialogButtonPane(6.0);
		buttonPane.setPadding(BUTTON_PANE_PADDING);
		buttonPane.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.BUTTON_PANE_BORDER), Side.TOP));
		buttonPane.getStyleClass().add(StyleClass.BUTTON_PANE);

		// Create button: OK
		Button okButton = new Button(OK_STR);
		okButton.getProperties().put(DialogButtonPane.BUTTON_GROUP_KEY, BUTTON_GROUP);
		okButton.setPadding(BUTTON_PADDING);
		okButton.setOnAction(event ->
		{
			result = dateSelectionPane.getDate();
			hide();
		});
		buttonPane.addButton(okButton, HPos.RIGHT);

		// Create button: cancel
		Button cancelButton = new Button(CANCEL_STR);
		cancelButton.getProperties().put(DialogButtonPane.BUTTON_GROUP_KEY, BUTTON_GROUP);
		cancelButton.setPadding(BUTTON_PADDING);
		cancelButton.setOnAction(event -> hide());
		buttonPane.addButton(cancelButton, HPos.RIGHT);

		// Update spacing of button groups
		buttonPane.updateButtonSpacing();

		// Create main pane
		VBox mainPane = new VBox(dateSelectionPane, buttonPane);
		mainPane.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.BORDER)));
		mainPane.getStyleClass().add(StyleClass.DATE_SELECTION_DIALOG);

		// Create scene and set it on this window
		setScene(new Scene(mainPane));

		// Add style sheet to scene
		StyleManager.INSTANCE.addStyleSheet(getScene());

		// Update UI after window is displayed
		addEventHandler(WindowEvent.WINDOW_SHOWN, event ->
		{
			// Equalise widths of groups of buttons
			double extraWidth = buttonPane.equaliseButtonWidths();

			// Increase width of window to accommodate extra width of buttons
			if (extraWidth > 0.0)
				setWidth(getWidth() + extraWidth);
		});

		// Fire 'OK' button when mouse is double-clicked on cell in date-selection pane
		dateSelectionPane.setAcceptAction(() -> okButton.fire());

		// Add handler for 'key pressed' events
		addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			switch (event.getCode())
			{
				case ESCAPE:
					cancelButton.fire();
					event.consume();
					break;

				case ENTER:
					okButton.fire();
					event.consume();
					break;

				default:
					// do nothing
					break;
			}
		});
	}

	//------------------------------------------------------------------

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
	 * Returns the result of this dialog.
	 *
	 * @return the date that was selected in this dialog, or {@code null} if the dialog was cancelled.
	 */

	public LocalDate getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
