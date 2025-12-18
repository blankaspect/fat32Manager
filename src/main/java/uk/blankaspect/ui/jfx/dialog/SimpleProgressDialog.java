/*====================================================================*\

SimpleProgressDialog.java

Class: simple progress dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import javafx.concurrent.Task;

import javafx.event.ActionEvent;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Button;

import javafx.scene.layout.VBox;

import javafx.stage.Window;
import javafx.stage.WindowEvent;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.label.CompoundPathnameLabel;

import uk.blankaspect.ui.jfx.progress.SimpleProgressBar;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleUtils;

import uk.blankaspect.ui.jfx.window.WindowUtils;

//----------------------------------------------------------------------


// CLASS: SIMPLE PROGRESS DIALOG


/**
 * This class implements a dialog that displays the progress of a {@linkplain Task task} with a message and a progress
 * bar that are bound to properties of the task.  The dialog may optionally have a <i>cancel</i> button.
 */

public class SimpleProgressDialog
	extends SimpleModalDialog<Void>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default width of the control pane. */
	private static final	double	DEFAULT_CONTROL_PANE_WIDTH	= 480.0;

	/** The padding around the content pane. */
	private static final	Insets	CONTENT_PANE_PADDING	= new Insets(8.0, 8.0, 4.0, 8.0);

	/** The gap between the message label and the progress bar. */
	private static final	double	GAP	= 8.0;

	/** The width of the progress bar. */
	private static final	double	PROGRESS_BAR_WIDTH	= 24.0;

	/** The height of the progress bar. */
	private static final	double	PROGRESS_BAR_HEIGHT	= 12.0;

	/**
	 * The cancellation mode.
	 */
	public enum CancelMode
	{
		/**
		 * The dialog does not have a <i>cancel</i> button.
		 */
		NONE,

		/**
		 * The dialog has a <i>cancel</i> button.  When the button is fired or the dialog is closed, {@link
		 * Task#cancel(boolean)} is called with {@code false} as its argument.
		 */
		NO_INTERRUPT,

		/**
		 * The dialog has a <i>cancel</i> button.  When the button is fired or the dialog is closed, {@link
		 * Task#cancel(boolean)} is called with {@code true} as its argument.
		 */
		INTERRUPT
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** Flag: if {@code true}, this dialog will be displayed when the state of {@link #task} is {@code SCHEDULED}. */
	private	boolean	showDialog;

	/** The <i>cancel</i> button. */
	private	Button	cancelButton;

	/** The maximum number of lines of the message. */
	private	int		maxNumMessageLines;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a progress dialog for the specified {@linkplain Task task}.  The dialog does not have a
	 * <i>cancel</i> button.
	 *
	 * @param task
	 *          the task whose progress will be displayed in the dialog.
	 */

	public SimpleProgressDialog(
		Task<?>	task)
	{
		// Call alternative constructor
		this(null, task, CancelMode.NONE, DEFAULT_CONTROL_PANE_WIDTH);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a progress dialog for the specified {@linkplain Task task} and with the specified
	 * width.  The dialog does not have a <i>cancel</i> button.
	 *
	 * @param task
	 *          the task whose progress will be displayed in the dialog.
	 * @param width
	 *          the preferred width of the dialog pane.
	 */

	public SimpleProgressDialog(
		Task<?>	task,
		double	width)
	{
		// Call alternative constructor
		this(null, task, CancelMode.NONE, width);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a progress dialog for the specified {@linkplain Task task} and with the specified
	 * owner.  The dialog does not have a <i>cancel</i> button.
	 *
	 * @param owner
	 *          the owner of the dialog, which may be {@code null}.
	 * @param task
	 *          the task whose progress will be displayed in the dialog.
	 */

	public SimpleProgressDialog(
		Window	owner,
		Task<?>	task)
	{
		// Call alternative constructor
		this(owner, task, CancelMode.NONE, DEFAULT_CONTROL_PANE_WIDTH);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a progress dialog for the specified {@linkplain Task task} and with the specified owner
	 * and width.  The dialog does not have a <i>cancel</i> button.
	 *
	 * @param owner
	 *          the owner of the dialog, which may be {@code null}.
	 * @param task
	 *          the task whose progress will be displayed in the dialog.
	 * @param width
	 *          the preferred width of the dialog pane.
	 */

	public SimpleProgressDialog(
		Window	owner,
		Task<?>	task,
		double	width)
	{
		// Call alternative constructor
		this(owner, task, CancelMode.NONE, width);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a progress dialog for the specified {@linkplain Task task} and with the specified
	 * owner.  The dialog may have an optional <i>cancel</i> button.
	 *
	 * @param owner
	 *          the owner of the dialog, which may be {@code null}.
	 * @param task
	 *          the task whose progress will be displayed in the dialog.
	 * @param cancelMode
	 *          the cancellation mode.
	 */

	public SimpleProgressDialog(
		Window		owner,
		Task<?>		task,
		CancelMode	cancelMode)
	{
		// Call alternative constructor
		this(owner, task, cancelMode, DEFAULT_CONTROL_PANE_WIDTH);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a progress dialog for the specified {@linkplain Task task} and with the specified owner
	 * and width.  The dialog may have an optional <i>cancel</i> button.
	 *
	 * @param owner
	 *          the owner of the dialog, which may be {@code null}.
	 * @param task
	 *          the task whose progress will be displayed in the dialog.
	 * @param cancelMode
	 *          the cancellation mode.
	 * @param width
	 *          the preferred width of the control pane.
	 */

	public SimpleProgressDialog(
		Window		owner,
		Task<?>		task,
		CancelMode	cancelMode,
		double		width)
	{
		// Call superclass constructor
		super(owner, null);

		// Initialise instance variables
		showDialog = true;

		// Allow dialog to be resized
		setResizable(true);

		// Bind title to title of task
		titleProperty().bind(task.titleProperty());

		// Create label for message
		CompoundPathnameLabel messageLabel = new CompoundPathnameLabel();
		messageLabel.textProperty().bind(task.messageProperty());

		// Resize dialog if number of lines of message increases
		messageLabel.textProperty().addListener((observable, oldText, text) ->
		{
			if (isShowing())
			{
				// Count number of lines of message
				int numLines = StringUtils.isNullOrEmpty(text)
											? 0
											: (int)text.chars().filter(ch -> ch == '\n').count() + 1;

				// If number of lines has increased, resize dialog
				if (maxNumMessageLines < numLines)
				{
					// Update instance variable
					maxNumMessageLines = numLines;

					// Resize dialog
					sizeToScene();
				}
			}
		});

		// Create progress bar
		SimpleProgressBar progressBar = new SimpleProgressBar(PROGRESS_BAR_WIDTH, PROGRESS_BAR_HEIGHT);
		progressBar.progressProperty().bind(task.progressProperty());

		// Create control pane
		VBox controlPane = new VBox(GAP, messageLabel, progressBar);
		controlPane.setAlignment(Pos.CENTER_LEFT);
		controlPane.setPrefWidth(width);

		// Add control pane to content pane
		addContent(controlPane);

		// Adjust padding around content pane
		getContentPane().setPadding(CONTENT_PANE_PADDING);

		// Remove border from content pane
		StyleUtils.setProperty(getContentPane(), FxProperty.BORDER_WIDTH.getName(), "0");

		// Case: no 'cancel' button
		if (cancelMode == CancelMode.NONE)
		{
			// Remove padding from button pane
			getButtonPane(0).setPadding(Insets.EMPTY);

			// Ignore dialog's 'close' button
			addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, WindowEvent::consume);
		}

		// Case: 'cancel' button
		else
		{
			// Create 'cancel' button
			cancelButton = Buttons.hNoShrink(CANCEL_STR);
			addButton(cancelButton, HPos.RIGHT);

			// Create procedure to cancel task
			IProcedure0 cancel = () ->
			{
				// Disable 'cancel' button
				cancelButton.setDisable(true);

				// Cancel task
				task.cancel(cancelMode == CancelMode.INTERRUPT);
			};

			// Cancel task if 'cancel' button is pressed
			cancelButton.addEventHandler(ActionEvent.ACTION, event -> cancel.invoke());

			// Cancel task if dialog is closed
			addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> cancel.invoke());
		}

		// Resize dialog to scene
		sizeToScene();

		// Show and hide dialog when state of task changes
		task.stateProperty().addListener((observable, oldState, state) ->
		{
			switch (state)
			{
				case CANCELLED:
				case FAILED:
				case SUCCEEDED:
					showDialog = false;
					SceneUtils.runOnFxApplicationThread(() ->
					{
						// Unbind UI components from task
						titleProperty().unbind();
						messageLabel.textProperty().unbind();
						progressBar.progressProperty().unbind();

						// Hide dialog
						hide();
					});
					break;

				case SCHEDULED:
					if (showDialog)
					{
						showDialog = false;
						SceneUtils.runOnFxApplicationThread(this::show);
					}
					break;

				default:
					break;
			}
		});

		// When dialog is closed, disable progress bar to stop indeterminate-progress timer
		addEventHandler(WindowEvent.WINDOW_HIDING, event -> progressBar.setDisable(true));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents the height of this dialog from changing.
	 */

	@Override
	protected void onWindowShown()
	{
		// Call superclass method
		super.onWindowShown();

		// Prevent height of window from changing
		WindowUtils.preventHeightChange(this);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the <i>cancel</i> button of this dialog.
	 *
	 * @return the <i>cancel</i> button, or {@code null} if this dialog does not have one.
	 */

	public Button getCancelButton()
	{
		return cancelButton;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
