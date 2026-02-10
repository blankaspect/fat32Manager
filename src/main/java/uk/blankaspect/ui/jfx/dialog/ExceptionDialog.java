/*====================================================================*\

ExceptionDialog.java

Class: dialog for displaying a representation of an exception.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;

import javafx.scene.Group;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.shape.Shape;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.exception2.BaseException;
import uk.blankaspect.common.exception2.ExceptionUtils;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.geometry.VHDirection;

import uk.blankaspect.common.message.MessageConstants;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.clipboard.ClipboardUtils;

import uk.blankaspect.ui.jfx.font.Fonts;

import uk.blankaspect.ui.jfx.image.MessageIcon32;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.shape.Shapes;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.RuleSetFactory;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;
import uk.blankaspect.ui.jfx.style.StyleSelector;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: DIALOG FOR DISPLAYING A REPRESENTATION OF AN EXCEPTION


/**
 * This class implements a dialog in which a message and the chain of causes of a {@link Throwable} may be displayed
 * alongside an icon.
 *
 * @see ErrorDialog
 * @see WarningDialog
 * @see MessageIcon32
 */

public class ExceptionDialog
	extends SimpleModalDialog<Integer>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default interval (in milliseconds) after the <i>details</i> window was hidden during which 'mouse released'
		events on the <i>details</i> button are suppressed to prevent the button from firing and thereby causing the
		<i>details</i> window to be shown again, immediately. */
	private static final	int		DEFAULT_DETAILS_WINDOW_DEBOUNCE_INTERVAL = 200;

	/** The vertical gap between adjacent message labels. */
	private static final	double	MESSAGE_GAP	= 4.0;

	/** The gap between the icon and the text. */
	private static final	double	ICON_TEXT_GAP	= 10.0;

	/** The minimum width of a message label. */
	private static final	double	MIN_MESSAGE_LABEL_WIDTH	= 240.0;

	/** The maximum width of a message label. */
	private static final	double	MAX_MESSAGE_LABEL_WIDTH	= 640.0;

	/** The padding around the content pane. */
	private static final	Insets	CONTENT_PANE_PADDING	= new Insets(4.0, 2.0, 4.0, 2.0);

	/** The padding around the <i>details</i> button. */
	private static final	Insets	DETAILS_BUTTON_PADDING	= new Insets(2.0, 6.0, 2.0, 6.0);

	/** The separator between stack traces. */
	private static final	String	STACK_TRACE_SEPARATOR	= "-".repeat(64) + "\n";

	/** Miscellaneous strings. */
	private static final	String	DETAILS_STR	= "Details";
	private static final	String	COPY_STR	= "Copy";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			Color.TRANSPARENT,
			CssSelector.builder()
					.cls(StyleClass.EXCEPTION_DIALOG_ROOT)
					.desc(StyleClass.DETAILS_BUTTON)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.DETAILS_BUTTON_BACKGROUND_FOCUSED,
			CssSelector.builder()
					.cls(StyleClass.EXCEPTION_DIALOG_ROOT)
					.desc(StyleClass.DETAILS_BUTTON).pseudo(FxPseudoClass.FOCUSED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.DETAILS_BUTTON_BORDER,
			CssSelector.builder()
					.cls(StyleClass.EXCEPTION_DIALOG_ROOT)
					.desc(StyleClass.DETAILS_BUTTON)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.DETAILS_BUTTON_ARROWHEAD,
			CssSelector.builder()
					.cls(StyleClass.EXCEPTION_DIALOG_ROOT)
					.desc(StyleClass.DETAILS_BUTTON)
					.desc(StyleClass.ARROWHEAD)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.DETAILS_AREA_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.DETAILS_AREA)
					.desc(FxStyleClass.CONTENT)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetFactory.focusBorder
		(
			CssSelector.builder()
					.cls(StyleClass.EXCEPTION_DIALOG_ROOT)
					.desc(StyleClass.DETAILS_BUTTON).pseudo(FxPseudoClass.FOCUSED)
					.build()
		),
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.DETAILS_AREA)
						.desc(FxStyleClass.TEXT)
						.build())
				.grayFontSmoothing()
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	EXCEPTION_DIALOG_ROOT	= StyleConstants.CLASS_PREFIX + "exception-dialog-root";

		String	ARROWHEAD			= StyleConstants.CLASS_PREFIX + "arrowhead";
		String	DETAILS_AREA		= EXCEPTION_DIALOG_ROOT + "-details-area";
		String	DETAILS_BUTTON		= StyleConstants.CLASS_PREFIX + "details-button";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	DETAILS_AREA_BACKGROUND				= PREFIX + "detailsArea.background";
		String	DETAILS_BUTTON_ARROWHEAD			= PREFIX + "detailsButton.arrowhead";
		String	DETAILS_BUTTON_BACKGROUND_FOCUSED	= PREFIX + "detailsButton.background.focused";
		String	DETAILS_BUTTON_BORDER				= PREFIX + "detailsButton.border";
	}

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	DETAILS_WINDOW_DEBOUNCE_INTERVAL =
				"blankaspect.ui.jfx.exceptionDialog.detailsWindowDebounceInterval";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** The interval (in milliseconds) after the <i>details</i> window was hidden during which 'mouse released' events
		on the <i>details</i> button are suppressed. */
	private static	int	detailsWindowDebounceInterval	= DEFAULT_DETAILS_WINDOW_DEBOUNCE_INTERVAL;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The index of the button that was selected. */
	private	Integer			result;

	/** The last time that the {@linkplain #detailsWindow <i>details</i> window} was hidden. */
	private	long			detailsWindowHiddenTime;

	/** A list of the message labels. */
	private	List<Label>		messageLabels;

	/** A list of the buttons. */
	private	List<Button>	buttons;

	/** The button that shows and hides the {@linkplain #detailsWindow <i>details</i> window}. */
	private	ToggleButton	detailsButton;

	/** The window in which the stack traces of an exception and its chain of causes are displayed. */
	private	DetailsWindow	detailsWindow;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(ExceptionDialog.class, COLOUR_PROPERTIES, RULE_SETS);

		// Get the debounce interval of the 'details' window from a system property
		String value = System.getProperty(SystemPropertyKey.DETAILS_WINDOW_DEBOUNCE_INTERVAL);
		if (value != null)
		{
			try
			{
				detailsWindowDebounceInterval = Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a dialog with the specified owner, title and icon, for the specified exception, and
	 * with a <i>close</i> button.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.
	 * @param exception
	 *          the exception whose detail message and chain of causes will be displayed in the dialog.
	 */

	public ExceptionDialog(
		Window			owner,
		String			title,
		MessageIcon32	icon,
		Throwable		exception)
	{
		// Call alternative constructor
		this(owner, title, icon, exception.getMessage(), exception.getCause());
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, icon and message, with a chain of causes
	 * whose first element is the specified exception, and with a <i>close</i> button.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 * @param cause
	 *          the exception that is the first element of the chain of causes that will be displayed in the dialog.
	 */

	public ExceptionDialog(
		Window			owner,
		String			title,
		MessageIcon32	icon,
		String			message,
		Throwable		cause)
	{
		// Call alternative constructor
		this(owner, title, icon, message, cause, new ButtonInfo(CLOSE_STR, HPos.RIGHT));
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, icon and buttons, for the specified
	 * exception.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.
	 * @param exception
	 *          the exception whose detail message and chain of causes will be displayed in the dialog.
	 * @param buttonInfos
	 *          information about the buttons of the dialog.
	 */

	public ExceptionDialog(
		Window								owner,
		String								title,
		MessageIcon32						icon,
		Throwable							exception,
		Collection<? extends ButtonInfo>	buttonInfos)
	{
		// Call alternative constructor
		this(owner, title, icon, exception.getMessage(), exception.getCause(), buttonInfos);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, icon and buttons, for the specified
	 * exception.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.
	 * @param exception
	 *          the exception whose detail message and chain of causes will be displayed in the dialog.
	 * @param buttonInfos
	 *          information about the buttons of the dialog.
	 */

	public ExceptionDialog(
		Window			owner,
		String			title,
		MessageIcon32	icon,
		Throwable		exception,
		ButtonInfo...	buttonInfos)
	{
		// Call alternative constructor
		this(owner, title, icon, exception.getMessage(), exception.getCause(), buttonInfos);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, icon, message and buttons, and with a chain
	 * of causes whose first element is the specified exception.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 * @param cause
	 *          the exception that is the first element of the chain of causes that will be displayed in the dialog.
	 * @param buttonInfos
	 *          information about the buttons of the dialog.
	 */

	public ExceptionDialog(
		Window								owner,
		String								title,
		MessageIcon32						icon,
		String								message,
		Throwable							cause,
		Collection<? extends ButtonInfo>	buttonInfos)
	{
		// Call superclass constructor
		super(owner, null, title);

		// Validate arguments
		if (buttonInfos == null)
			throw new IllegalArgumentException("Null buttons");
		if (buttonInfos.isEmpty())
			throw new IllegalArgumentException("No buttons");

		// Initialise instance variables
		result = -1;

		// Set style class on root node of scene graph
		getScene().getRoot().getStyleClass().add(StyleClass.EXCEPTION_DIALOG_ROOT);

		// Create list of string representations of stack traces of chain of causes
		List<String> causes = ExceptionUtils.getStackTraceStrings(cause);

		// Create text for copying to clipboard
		String text = ((message == null) ? "" : message.replace(MessageConstants.LABEL_SEPARATOR_CHAR, '\n') + "\n")
						+ (causes.isEmpty() ? "" : STACK_TRACE_SEPARATOR + String.join(STACK_TRACE_SEPARATOR, causes));

		// Create labels for messages
		messageLabels = new ArrayList<>();
		if (!StringUtils.isNullOrEmpty(message))
		{
			for (String msg : StringUtils.split(message, MessageConstants.LABEL_SEPARATOR_CHAR, true))
			{
				Label label = new Label(msg);
				label.setMinWidth(MIN_MESSAGE_LABEL_WIDTH);
				label.setMaxWidth(MAX_MESSAGE_LABEL_WIDTH);
				label.setWrapText(true);
				messageLabels.add(label);
			}
		}

		// Create pane for message labels
		VBox messagePane = new VBox(MESSAGE_GAP);
		messagePane.setAlignment(Pos.CENTER_LEFT);
		messagePane.getChildren().addAll(messageLabels);
		HBox.setHgrow(messagePane, Priority.ALWAYS);

		// Create pane for icon and message labels
		HBox contentPane = new HBox(ICON_TEXT_GAP, icon.get(), messagePane);
		contentPane.setAlignment(Pos.CENTER_LEFT);
		contentPane.setPadding(CONTENT_PANE_PADDING);

		// Add content pane
		addContent(contentPane);

		// Create button to show causes in a separate window
		if (!causes.isEmpty())
		{
			// Create up arrowhead for 'details' button
			double arrowheadSize = (double)((int)TextUtils.textHeight() / 4 * 4);
			Shape upArrowhead = Shapes.arrowhead01(VHDirection.UP, arrowheadSize);
			upArrowhead.setFill(getColour(ColourKey.DETAILS_BUTTON_ARROWHEAD));
			upArrowhead.getStyleClass().add(StyleClass.ARROWHEAD);
			Group upIcon = Shapes.tile(upArrowhead, arrowheadSize);

			// Create down arrowhead for 'details' button
			Shape downArrowhead = Shapes.arrowhead01(VHDirection.DOWN, arrowheadSize);
			downArrowhead.setFill(getColour(ColourKey.DETAILS_BUTTON_ARROWHEAD));
			downArrowhead.getStyleClass().add(StyleClass.ARROWHEAD);
			Group downIcon = Shapes.tile(downArrowhead, arrowheadSize);

			// Create 'details' button
			detailsButton = new ToggleButton(DETAILS_STR, upIcon);
			detailsButton.setContentDisplay(ContentDisplay.RIGHT);
			detailsButton.setPadding(DETAILS_BUTTON_PADDING);
			detailsButton.getStyleClass().add(StyleClass.DETAILS_BUTTON);
			detailsButton.selectedProperty().addListener((observable, oldSelected, selected) ->
			{
				if (selected)
				{
					if (detailsWindow == null)
					{
						detailsButton.setGraphic(downIcon);
						detailsWindow = new DetailsWindow(causes);
						detailsWindow.show();
					}
				}
				else
				{
					if (detailsWindow != null)
					{
						detailsWindow.hide();
						detailsWindow = null;
						detailsWindowHiddenTime = System.currentTimeMillis();
						detailsButton.setGraphic(upIcon);
					}
				}
			});

			// This is a WORKAROUND for a problem that has been observed with JavaFX on Linux/GNOME (specifically, with
			// JavaFX 21.0.3 on Ubuntu 24.04.3 LTS, though it may be present with other versions of JavaFX and on other
			// platforms).
			// - The problem :
			//   The 'details' window is closed by a listener on its 'focused' property when the window loses focus.  If
			//   a 'mouse pressed' event occurs on the 'details' button while the 'details' window is shown, the window
			//   loses focus and is hidden, as intended.  However, the accompanying 'mouse released' event then fires
			//   the 'details' button, thereby causing the 'details' window to be shown again, immediately.
			// - The solution :
			//   A 'mouse released' event on the 'details' button is suppressed if it occurs too soon after the
			//   'details' window was hidden, thereby preventing the button from firing.
			//
			// The problem is not present on Windows because of a difference in the way in which JavaFX handles a 'mouse
			// pressed' event on the 'details' button.  On Windows, if such an event occurs while the 'details' window
			// is shown, it causes the 'details' window to lose focus, but, unlike on Linux/GNOME, it doesn't cause the
			// 'details' button to fire.
			detailsButton.addEventFilter(MouseEvent.MOUSE_RELEASED, event ->
			{
				if (System.currentTimeMillis() - detailsWindowHiddenTime < detailsWindowDebounceInterval)
					event.consume();
			});

			// Create procedure to update 'details' button
			IProcedure0 updateDetailsButton = () ->
			{
				boolean focused = detailsButton.isFocused();
				detailsButton.setBackground(focused
						? SceneUtils.createColouredBackground(getColour(ColourKey.DETAILS_BUTTON_BACKGROUND_FOCUSED))
						: null);
				detailsButton.setBorder(focused
						? SceneUtils.createFocusBorder()
						: SceneUtils.createSolidBorder(getColour(ColourKey.DETAILS_BUTTON_BORDER)));
			};

			// Update 'details' button when it gains or loses focus
			if (StyleManager.INSTANCE.notUsingStyleSheet())
			{
				detailsButton.focusedProperty().addListener(observable -> updateDetailsButton.invoke());
				updateDetailsButton.invoke();
			}

			// Add button to button pane
			addButton(detailsButton, HPos.LEFT, false);
		}

		// Create button: copy
		Button copyButton = Buttons.hNoShrink(COPY_STR);
		copyButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		copyButton.setOnAction(event ->
		{
			try
			{
				ClipboardUtils.putTextThrow(text);
			}
			catch (BaseException e)
			{
				NotificationDialog.show(this, COPY_STR, MessageIcon32.ERROR.get(), e.getMessage());
			}
		});
		addButton(copyButton, HPos.LEFT);

		// Create buttons and add them to button pane
		buttons = new ArrayList<>();
		Button ctrlEnterButton = null;
		for (ButtonInfo buttonInfo : buttonInfos)
		{
			// Create button
			Button button = Buttons.hNoShrink(buttonInfo.getText());
			button.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP2);
			button.setOnAction(event ->
			{
				result = buttons.indexOf(button);
				hide();
			});
			buttons.add(button);

			// Add button to button pane
			addButton(button, buttonInfo.getPosition());

			// Set button that is fired on Ctrl+Enter
			if ((ctrlEnterButton == null) && buttonInfo.isFireOnCtrlEnter())
				ctrlEnterButton = button;
		}

		// Fire button if Ctrl+Enter is pressed
		if (ctrlEnterButton != null)
		{
			Button button = ctrlEnterButton;
			addEventFilter(KeyEvent.KEY_PRESSED, event ->
			{
				if ((event.getCode() == KeyCode.ENTER) && event.isControlDown())
					button.fire();
			});
		}

		// Hide 'details' window or fire request to close window if Escape key is pressed
		addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if (event.getCode() == KeyCode.ESCAPE)
			{
				// If 'details' button is selected, hide 'details' window ...
				if ((detailsButton != null) && detailsButton.isSelected())
					hideDetailsWindow();

				// ... otherwise, fire 'request to close window' event
				else
					requestClose();

				// Consume event
				event.consume();
			}
		});
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, icon, message and buttons, and with a chain
	 * of causes whose first element is the specified exception.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 * @param cause
	 *          the exception that is the first element of the chain of causes that will be displayed in the dialog.
	 * @param buttonInfos
	 *          information about the buttons of the dialog.
	 */

	public ExceptionDialog(
		Window			owner,
		String			title,
		MessageIcon32	icon,
		String			message,
		Throwable		cause,
		ButtonInfo...	buttonInfos)
	{
		// Call alternative constructor
		this(owner, title, icon, message, cause, List.of(buttonInfos));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a builder for an exception dialog.
	 *
	 * @return a new instance of a builder for an exception dialog.
	 */

	public static Builder builder()
	{
		return new Builder();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and displays a new instance of a dialog with the specified owner, title and icon, for the specified
	 * exception, and with a <i>close</i> button.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param exception
	 *          the exception whose detail message and chain of causes will be displayed in the dialog.
	 */

	public static void show(
		Window			owner,
		String			title,
		MessageIcon32	icon,
		Throwable		exception)
	{
		new ExceptionDialog(owner, title, icon, exception).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates and displays a new instance of a dialog with the specified owner, title, icon and message, with a chain
	 * of causes whose first element is the specified exception, and with a <i>close</i> button.
	 *
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param title
	 *          the title of the dialog.
	 * @param icon
	 *          the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param message
	 *          the message that will be displayed in the dialog.
	 * @param cause
	 *          the exception that is the first element of the chain of causes that will be displayed in the dialog.
	 */

	public static void show(
		Window			owner,
		String			title,
		MessageIcon32	icon,
		String			message,
		Throwable		cause)
	{
		new ExceptionDialog(owner, title, icon, message, cause).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, icon and buttons, for the specified
	 * exception, displays the dialog and returns the index of the selected button.
	 *
	 * @param  owner
	 *           the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  icon
	 *           the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param  exception
	 *           the exception whose detail message and chain of causes will be displayed in the dialog.
	 * @param  buttonInfos
	 *           information about the buttons of the dialog.
	 * @return the index of the selected button, or -1 if no button was selected.
	 */

	public static int show(
		Window								owner,
		String								title,
		MessageIcon32						icon,
		Throwable							exception,
		Collection<? extends ButtonInfo>	buttonInfos)
	{
		return new ExceptionDialog(owner, title, icon, exception, buttonInfos).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, icon and buttons, for the specified
	 * exception, displays the dialog and returns the index of the selected button.
	 *
	 * @param  owner
	 *           the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  icon
	 *           the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param  buttonInfos
	 *           information about the buttons of the dialog.
	 * @return the index of the selected button, or -1 if no button was selected.
	 */

	public static int show(
		Window			owner,
		String			title,
		MessageIcon32	icon,
		Throwable		exception,
		ButtonInfo...	buttonInfos)
	{
		return new ExceptionDialog(owner, title, icon, exception, buttonInfos).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, icon, message and buttons, and with a chain
	 * of causes whose first element is the specified exception, displays the dialog and returns the index of the
	 * selected button.
	 *
	 * @param  owner
	 *           the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  icon
	 *           the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param  message
	 *           the message that will be displayed in the dialog.
	 * @param  cause
	 *           the exception that is the first element of the chain of causes that will be displayed in the dialog.
	 * @param  buttonInfos
	 *           information about the buttons of the dialog.
	 * @return the index of the selected button, or -1 if no button was selected.
	 */

	public static int show(
		Window								owner,
		String								title,
		MessageIcon32						icon,
		String								message,
		Throwable							cause,
		Collection<? extends ButtonInfo>	buttonInfos)
	{
		return new ExceptionDialog(owner, title, icon, message, cause, buttonInfos).showDialog();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a dialog with the specified owner, title, icon, message and buttons, and with a chain
	 * of causes whose first element is the specified exception, displays the dialog and returns the index of the
	 * selected button.
	 *
	 * @param  owner
	 *           the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param  title
	 *           the title of the dialog.
	 * @param  icon
	 *           the icon of the dialog.  If it is {@code null}, the dialog will have no icon.
	 * @param  message
	 *           the message that will be displayed in the dialog.
	 * @param  cause
	 *           the exception that is the first element of the chain of causes that will be displayed in the dialog.
	 * @param  buttonInfos
	 *           information about the buttons of the dialog.
	 * @return the index of the selected button, or -1 if no button was selected.
	 */

	public static int show(
		Window			owner,
		String			title,
		MessageIcon32	icon,
		String			message,
		Throwable		cause,
		ButtonInfo...	buttonInfos)
	{
		return new ExceptionDialog(owner, title, icon, message, cause, buttonInfos).showDialog();
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

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected Integer getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an unmodifiable list of the message labels of this dialog.
	 *
	 * @return an unmodifiable list of the message labels of this dialog.
	 */

	public List<Label> getMessageLabels()
	{
		return Collections.unmodifiableList(messageLabels);
	}

	//------------------------------------------------------------------

	/**
	 * Returns an unmodifiable list of the buttons of this dialog.
	 *
	 * @return an unmodifiable list of the buttons of this dialog.
	 */

	public List<Button> getButtons()
	{
		return Collections.unmodifiableList(buttons);
	}

	//------------------------------------------------------------------

	/**
	 * Hides the <i>details</i> window.
	 */

	private void hideDetailsWindow()
	{
		detailsButton.setSelected(false);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: BUILDER FOR AN EXCEPTION DIALOG


	/**
	 * This class implements a builder for an {@linkplain ExceptionDialog exception dialog}.
	 */

	public static class Builder
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The owner of the dialog. */
		private	Window				owner;

		/** The title of the dialog. */
		private	String				title;

		/** The icon of the dialog. */
		private	MessageIcon32		icon;

		/** The message that will be displayed in the dialog. */
		private	String				message;

		/** The exception that is the first element of the chain of causes that will be displayed in the dialog. */
		private	Throwable			cause;

		/** A list of information about the buttons of the dialog. */
		private	List<ButtonInfo>	buttonInfos;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a builder for an {@linkplain ExceptionDialog exception dialog}.
		 */

		private Builder()
		{
			// Initialise instance variables
			buttonInfos = new ArrayList<>();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Sets the owner of the dialog to the specified value.
		 *
		 * @param  owner
		 *           the owner of the dialog.
		 * @return this builder.
		 */

		public Builder owner(
			Window	owner)
		{
			// Update instance variable
			this.owner = owner;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the title of the dialog to the specified value.
		 *
		 * @param  title
		 *           the title of the dialog.
		 * @return this builder.
		 */

		public Builder title(
			String	title)
		{
			// Update instance variable
			this.title = title;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the icon of the dialog to the specified value.
		 *
		 * @param  icon
		 *           the icon of the dialog.
		 * @return this builder.
		 */

		public Builder icon(
			MessageIcon32	icon)
		{
			// Update instance variable
			this.icon = icon;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the message that will be displayed in the dialog to the specified value.
		 *
		 * @param  message
		 *           the message that will be displayed in the dialog.
		 * @return this builder.
		 */

		public Builder message(
			String	message)
		{
			// Update instance variable
			this.message = message;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Sets the exception that is the first element of the chain of causes that will be displayed in the dialog.
		 *
		 * @param  cause
		 *           the exception that is the first element of the chain of causes that will be displayed in the
		 *           dialog.
		 * @return this builder.
		 */

		public Builder cause(
			Throwable	cause)
		{
			// Update instance variable
			this.cause = cause;

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates an item of button information for a left-positioned button with the specified text and adds it to the
		 * list of information about the buttons of the dialog.
		 *
		 * @param  text
		 *           the text of the button.
		 * @return this builder.
		 */

		public Builder addLeft(
			String	text)
		{
			// Append button info to list
			buttonInfos.add(ButtonInfo.left(text));

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates an item of button information for a horizontally centred button with the specified text and adds it
		 * to the list of information about the buttons of the dialog.
		 *
		 * @param  text
		 *           the text of the button.
		 * @return this builder.
		 */

		public Builder addCentre(
			String	text)
		{
			// Append button info to list
			buttonInfos.add(ButtonInfo.centre(text));

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates an item of button information for a right-positioned button with the specified text and adds it to
		 * the list of information about the buttons of the dialog.
		 *
		 * @param  text
		 *           the text of the button.
		 * @return this builder.
		 */

		public Builder addRight(
			String	text)
		{
			// Append button info to list
			buttonInfos.add(ButtonInfo.right(text));

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Adds the specified items of button information to the list of information about the buttons of the dialog.
		 *
		 * @param  buttonInfos
		 *           the items that will be added to the list of button information.
		 * @return this builder.
		 */

		public Builder addButtons(
			Collection<? extends ButtonInfo>	buttonInfos)
		{
			// Append items to list
			this.buttonInfos.addAll(buttonInfos);

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Adds the specified items of button information to the list of information about the buttons of the dialog.
		 *
		 * @param  buttonInfos
		 *           the items that will be added to the list of button information.
		 * @return this builder.
		 */

		public Builder addButtons(
			ButtonInfo...	buttonInfos)
		{
			// Append items to list
			Collections.addAll(this.buttonInfos, buttonInfos);

			// Return this builder
			return this;
		}

		//--------------------------------------------------------------

		/**
		 * Creates and returns a new instance of an exception dialog that is initialised from the state of this builder.
		 *
		 * @return a new instance of an exception dialog.
		 */

		public ExceptionDialog build()
		{
			return new ExceptionDialog(owner, title, icon, message, cause, buttonInfos);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: DETAILS WINDOW


	/**
	 * This class implements an undecorated window in which stack traces of an exception and its chain of causes are
	 * displayed.
	 */

	private class DetailsWindow
		extends Stage
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The preferred width of the text area. */
		private static final	double	TEXT_AREA_WIDTH		= 480.0;

		/** The preferred height of the text area. */
		private static final	double	TEXT_AREA_HEIGHT	= 240.0;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a window in which the specified stack traces of an exception and its chain of
		 * causes are displayed.
		 *
		 * @param stackTraces
		 *          the stack traces that will be displayed in the window.
		 */

		private DetailsWindow(
			Collection<String>	stackTraces)
		{
			// Set properties
			initModality(Modality.NONE);
			initOwner(ExceptionDialog.this);
			initStyle(StageStyle.UNDECORATED);

			// Create text area for stack traces
			TextArea textArea = new TextArea();
			textArea.setEditable(false);
			textArea.setPrefSize(TEXT_AREA_WIDTH, TEXT_AREA_HEIGHT);
			textArea.setFont(Fonts.monoFontSmaller());
			for (String stackTrace : stackTraces)
			{
				if (textArea.getLength() > 0)
					textArea.appendText(STACK_TRACE_SEPARATOR);
				textArea.appendText(stackTrace.replace("\n\t", "\n    "));
			}
			textArea.home();
			textArea.getStyleClass().add(StyleClass.DETAILS_AREA);

			// Create scene and set it on this window
			setScene(new Scene(textArea));

			// Add style sheet to scene
			StyleManager.INSTANCE.addStyleSheet(getScene());

			// Hide window if it loses focus
			focusedProperty().addListener((observable, oldFocused, focused) ->
			{
				if (!focused)
					hideDetailsWindow();
			});

			// Hide window if mouse button is pressed on window with Control key down
			addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
			{
				if (event.isControlDown())
					hideDetailsWindow();
			});

			// Hide window if Escape key is pressed
			addEventFilter(KeyEvent.KEY_PRESSED, event ->
			{
				if (event.getCode() == KeyCode.ESCAPE)
					hideDetailsWindow();
			});

			// When window is shown, set background colour of text area and request focus on text area
			setOnShown(event ->
			{
				// Set background colour of text area
				if (StyleManager.INSTANCE.notUsingStyleSheet()
						&& (textArea.lookup(StyleSelector.TEXT_AREA_CONTENT) instanceof Region region))
				{
					region.setBackground(SceneUtils.createColouredBackground(
							getColour(ColourKey.DETAILS_AREA_BACKGROUND)));
				}

				// Set location of window
				Window owner = getOwner();
				Rectangle2D rect =
						SceneUtils.findScreenBounds(owner.getX(), owner.getY(), owner.getWidth(), owner.getHeight());
				if (rect == null)
					rect = Rectangle2D.EMPTY;
				Bounds bounds = detailsButton.localToScreen(detailsButton.getLayoutBounds());
				setX(Math.max(0.0, Math.min(rect.getMaxX() - getWidth(), bounds.getMinX())));
				setY(Math.max(0.0, Math.min(rect.getMaxY() - getHeight(), bounds.getMaxY())));

				// Request focus on text area
				textArea.requestFocus();
			});
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
