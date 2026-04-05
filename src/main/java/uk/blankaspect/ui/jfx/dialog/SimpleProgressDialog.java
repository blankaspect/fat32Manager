/*====================================================================*\

SimpleProgressDialog.java

Class: simple progress dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Map;

import javafx.concurrent.Task;

import javafx.event.ActionEvent;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;

import javafx.scene.Scene;

import javafx.scene.control.Button;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.exec.ExecUtils;

import uk.blankaspect.ui.jfx.label.CompoundPathnameLabel;

import uk.blankaspect.ui.jfx.progress.SimpleProgressBar;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.window.WindowDims;

//----------------------------------------------------------------------


// CLASS: SIMPLE PROGRESS DIALOG


/**
 * This class implements a modal dialog that displays the progress of a {@linkplain Task task} with a message and a
 * progress bar that are bound to properties of the task.  The dialog may optionally have a <i>cancel</i> button.
 */

public class SimpleProgressDialog
	extends Stage
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default width of the control pane. */
	private static final	double	DEFAULT_CONTROL_PANE_WIDTH	= 480.0;

	/** The padding around a button. */
	private static final	Insets	BUTTON_PADDING	= new Insets(3.0, 12.0, 3.0, 12.0);

	/** The padding around the main pane. */
	private static final	Insets	MAIN_PANE_PADDING	= new Insets(8.0, 10.0, 8.0, 10.0);

	/** The padding around the main pane if there is no <i>cancel</i> button. */
	private static final	Insets	MAIN_PANE_PADDING_NO_CANCEL	= new Insets(8.0, 10.0, 10.0, 10.0);

	/** The gap between the message label and the progress bar. */
	private static final	double	GAP	= 8.0;

	/** A map from system-property keys to the default values of the corresponding delays (in milliseconds) in the
		<i>WINDOW_SHOWN</i> event handler of the window of the dialog. */
	private static final	Map<String, Integer>	WINDOW_DELAYS	= Map.of
	(
		SystemPropertyKey.WINDOW_DELAY_SIZE,     100,
		SystemPropertyKey.WINDOW_DELAY_LOCATION,  25,
		SystemPropertyKey.WINDOW_DELAY_OPACITY,   25
	);

	/** The minimum width of the window of the dialog. */
	private static final	double	MIN_WIDTH	= 120.0;

	/** Miscellaneous strings. */
	private static final	String	CANCEL_STR	= "Cancel";

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

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	WINDOW_DELAY_LOCATION	= "blankaspect.ui.jfx.simpleProgressDialog.windowDelay.location";
		String	WINDOW_DELAY_OPACITY	= "blankaspect.ui.jfx.simpleProgressDialog.windowDelay.opacity";
		String	WINDOW_DELAY_SIZE		= "blankaspect.ui.jfx.simpleProgressDialog.windowDelay.size";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** Flag: if {@code true}, this dialog will be displayed when the state of {@link #task} is {@code SCHEDULED}. */
	private	boolean					showDialog;

	/** The maximum number of lines of the message. */
	private	int						maxNumMessageLines;

	/** The label for the message. */
	private	CompoundPathnameLabel	messageLabel;

	/** The <i>cancel</i> button. */
	private	Button					cancelButton;

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
		// Initialise instance variables
		showDialog = true;
		maxNumMessageLines = 1;

		// Set properties
		initModality(Modality.APPLICATION_MODAL);
		initOwner(owner);

		// Bind title to title of task
		titleProperty().bind(task.titleProperty());

		// Create label for message
		messageLabel = new CompoundPathnameLabel("");
		messageLabel.textProperty().bind(task.messageProperty());
		VBox.setVgrow(messageLabel, Priority.ALWAYS);

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

					// Allow height of window to increase
					setMaxHeight(Double.MAX_VALUE);

					// Resize dialog
					sizeToScene();

					// Prevent height of window from changing
					double height = getHeight();
					setMinHeight(height);
					setMaxHeight(height);
				}
			}
		});

		// Create progress bar
		SimpleProgressBar progressBar = new SimpleProgressBar();
		progressBar.progressProperty().bind(task.progressProperty());

		// Create control pane
		VBox controlPane = new VBox(GAP, messageLabel, progressBar);
		controlPane.setPrefWidth(width);
		controlPane.setAlignment(Pos.CENTER_LEFT);

		// Initialise main pane
		Pane mainPane = null;

		// Case: no 'cancel' button
		if (cancelMode == CancelMode.NONE)
		{
			// Ignore dialog's 'close' button
			addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, WindowEvent::consume);

			// Set control pane as main pane
			mainPane = controlPane;
			mainPane.setPadding(MAIN_PANE_PADDING_NO_CANCEL);
		}

		// Case: 'cancel' button
		else
		{
			// Create 'cancel' button
			cancelButton = Buttons.hNoShrink(CANCEL_STR);
			cancelButton.setPadding(BUTTON_PADDING);

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

			// Create button pane
			HBox buttonPane = new HBox(cancelButton);
			buttonPane.setAlignment(Pos.CENTER_RIGHT);

			// Create content pane
			mainPane = new VBox(8.0, controlPane, buttonPane);
			mainPane.setPadding(MAIN_PANE_PADDING);
		}

		// Set scene
		setScene(new Scene(mainPane));
		sizeToScene();

		// Add style sheet to scene
		StyleManager.INSTANCE.addStyleSheet(getScene());

		// When window is shown, set its location after a delay
		addEventHandler(WindowEvent.WINDOW_SHOWN, event ->
		{
			// Get dimensions of window
			WindowDims dims = new WindowDims(this);

			// Set size of window after a delay
			ExecUtils.afterDelay(getDelay(SystemPropertyKey.WINDOW_DELAY_SIZE), () ->
			{
				// Update dimensions
				dims.update(false);

				// Temporarily set minimum dimensions to prevent window from shrinking (Linux/GNOME)
				dims.setMin(MIN_WIDTH, 0.0);

				// Set location of window after a delay
				ExecUtils.afterDelay(getDelay(SystemPropertyKey.WINDOW_DELAY_LOCATION), () ->
				{
					// Locate window relative to owner
					Point2D location = SceneUtils.getRelativeLocation(getWidth(), getHeight(), owner.getX(),
																	  owner.getY(), owner.getWidth(),
																	  owner.getHeight());

					// Set location of window
					setX(location.getX());
					setY(location.getY());

					// Perform remaining initialisation after a delay
					ExecUtils.afterDelay(getDelay(SystemPropertyKey.WINDOW_DELAY_OPACITY), () ->
					{
						// Set minimum width of window
						setMinWidth(MIN_WIDTH);

						// Prevent height of window from changing
						double height = getHeight();
						setMinHeight(height);
						setMaxHeight(height);

						// Make window visible
						setOpacity(1.0);

						// Allow subclasses to complete the initialisation of the dialog after the window is shown
						onWindowShown();
					});
				});
			});
		});

		// Show and hide dialog when state of task changes
		task.stateProperty().addListener((observable, oldState, state) ->
		{
			switch (state)
			{
				case CANCELLED, FAILED, SUCCEEDED:
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
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the delay (in milliseconds) that is defined the system property with the specified key.
	 *
	 * @param  key
	 *           the key of the system property.
	 * @return the delay (in milliseconds) that is defined the system property whose key is {@code key}, or a default
	 *         value if there is no such property or the property value is not a valid integer.
	 */

	private static int getDelay(
		String	key)
	{
		int delay = WINDOW_DELAYS.get(key);
		String value = System.getProperty(key);
		if (value != null)
		{
			try
			{
				delay = Integer.parseInt(value);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
		return delay;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the message label of this dialog.
	 *
	 * @return the message label of this dialog.
	 */

	public CompoundPathnameLabel getMessageLabel()
	{
		return messageLabel;
	}

	//------------------------------------------------------------------

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

	/**
	 * This method is called by the {@linkplain #SimpleProgressDialog(Window, Task, CancelMode, double) primary
	 * constructor} at the end of the {@code WindowEvent.WINDOW_SHOWN} event handler.  It may be overridden by
	 * subclasses to complete the initialisation of the dialog after the window is shown.
	 */

	protected void onWindowShown()
	{
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
