/*====================================================================*\

SimpleDialog.java

Class: simple dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.dialog;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.application.Platform;

import javafx.event.EventHandler;

import javafx.geometry.Dimension2D;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Side;

import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.ButtonBase;
import javafx.scene.control.Labeled;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.container.DialogButtonPane;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: SIMPLE DIALOG


/**
 * This is the abstract base class of a dialog that is based on a JavaFX {@link Stage} rather than a {@link
 * javafx.scene.control.Dialog}.
 * <p>
 * A dialog consists of components arranged vertically: a {@linkplain #getContent() content node} at the top and at
 * least one {@linkplain #getButtonPane() button pane} below it.  By default, the content node is a {@linkplain
 * #getContentPane() content pane} to which content may be added.
 * </p>
 * <p>
 * A button pane is divided horizontally into three zones: left, centre and right.  There is an extra gap between
 * adjacent zones.  When you {@linkplain #addButton(Region, HPos) add a button} to the button pane, you choose the zone
 * in which it will appear.
 * </p>
 * <p>
 * The widths of groups of buttons in each button pane may be equalised by setting the preferred width of each button in
 * the group to the width of the widest button.  Membership of a group is conferred on a button by its having a property
 * with the key {@link #BUTTON_GROUP_KEY}; the group is identified by the value of the property.
 * </p>
 */

public abstract class SimpleDialog
	extends Stage
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The key of the <i>group</i> property of a button that is used when equalising button widths. */
	public static final		String	BUTTON_GROUP_KEY	= DialogButtonPane.BUTTON_GROUP_KEY;

	/** An object that may be used as the value of the <i>group</i> property of a button, to equalise button widths. */
	protected static final	Object	BUTTON_GROUP1	= new Object();

	/** An object that may be used as the value of the <i>group</i> property of a button, to equalise button widths. */
	protected static final	Object	BUTTON_GROUP2	= new Object();

	/** An object that may be used as the value of the <i>group</i> property of a button, to equalise button widths. */
	protected static final	Object	BUTTON_GROUP3	= new Object();

	/** The text of commonly used buttons. */
	protected static final	String	OK_STR		= "OK";
	protected static final	String	CANCEL_STR	= "Cancel";
	protected static final	String	APPLY_STR	= "Apply";
	protected static final	String	CLOSE_STR	= "Close";

	/** The default padding around the content pane. */
	private static final	Insets	DEFAULT_CONTENT_PANE_PADDING	= new Insets(6.0, 8.0, 6.0, 8.0);

	/** The default gap between buttons. */
	private static final	double	DEFAULT_BUTTON_GAP	= 8.0;

	/** The default padding around a button. */
	private static final	Insets	DEFAULT_BUTTON_PADDING	= new Insets(3.0, 8.0, 3.0, 8.0);

	/** The default minimum width of a button. */
	private static final	double	DEFAULT_MIN_BUTTON_WIDTH	= 3.0 * TextUtils.textHeight();

	/** The margins that are applied to the visual bounds of each screen when determining whether the saved location of
		the window is within a screen. */
	private static final	Insets	SCREEN_MARGINS	= new Insets(0.0, 32.0, 32.0, 0.0);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.CONTENT_PANE_BORDER,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_DIALOG)
						.desc(StyleClass.CONTENT_PANE)
						.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.SIMPLE_DIALOG)
									.desc(StyleClass.CONTENT_PANE)
									.build())
						.borders(Side.BOTTOM)
						.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	CONTENT_PANE	= StyleConstants.CLASS_PREFIX + "content-pane";
		String	SIMPLE_DIALOG	= StyleConstants.CLASS_PREFIX + "simple-dialog";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	CONTENT_PANE_BORDER	= PREFIX + "contentPane.border";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** A map of the locations of dialogs. */
	private static	Map<String, Point2D>		locations	= new HashMap<>();

	/** A map of the sizes of dialogs. */
	private static	Map<String, Dimension2D>	sizes		= new HashMap<>();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The minimum width of a button. */
	private	double					minButtonWidth;

	/** The main pane of this dialog. */
	private	VBox					mainPane;

	/** The content of this dialog, which is {@link #contentPane} by default. */
	private	Node					content;

	/** The content pane of this dialog. */
	private	StackPane				contentPane;

	/** A list of the button panes of this dialog. */
	private	List<DialogButtonPane>	buttonPanes;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(SimpleDialog.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a dialog with the specified modality, owner, title and locator.
	 *
	 * @param modality
	 *          the modality of the dialog.
	 * @param owner
	 *          the owner of the dialog.  If it is {@code null}, the dialog will have no owner.
	 * @param locationKey
	 *          the key with which the dialog will be associated in the map of locations.  If the map contains an entry
	 *          for the specified key, the location of the dialog will be set to the associated value when the dialog is
	 *          displayed.  If the key is {@code null}, it will be ignored.
	 * @param sizeKey
	 *          the key with which the dialog will be associated in the map of sizes.  If the map contains an entry for
	 *          the specified key, the size of the dialog will be set to the associated value when the dialog is
	 *          displayed.  If the key is {@code null}, it will be ignored.
	 * @param title
	 *          the title of the dialog, which may be {@code null}.
	 * @param numButtonPanes
	 *          the number of button panes.
	 * @param locator
	 *          the function that returns the location of the dialog, which may be {@code null}.
	 * @param size
	 *          the size of the dialog, which may be {@code null}.
	 */

	protected SimpleDialog(
		Modality	modality,
		Window		owner,
		String		locationKey,
		String		sizeKey,
		String		title,
		int			numButtonPanes,
		ILocator	locator,
		Dimension2D	size)
	{
		// Validate arguments
		if (numButtonPanes <= 0)
			throw new IllegalArgumentException("Number of button panes out of bounds: " + numButtonPanes);

		// Initialise instance variables
		minButtonWidth = DEFAULT_MIN_BUTTON_WIDTH;

		// Set properties
		initModality(modality);
		initOwner(owner);
		if (title != null)
			setTitle(title);
		setResizable(false);

		// Make window invisible until it is displayed
		setOpacity(0.0);

		// Set icons to those of owner
		if (owner instanceof Stage stage)
			getIcons().addAll(stage.getIcons());

		// Create content pane
		contentPane = new StackPane();
		contentPane.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.CONTENT_PANE_BORDER), Side.BOTTOM));
		contentPane.setPadding(DEFAULT_CONTENT_PANE_PADDING);
		contentPane.getStyleClass().add(StyleClass.CONTENT_PANE);
		VBox.setVgrow(contentPane, Priority.ALWAYS);
		content = contentPane;

		// Create main pane
		mainPane = new VBox(contentPane);
		mainPane.getStyleClass().add(StyleClass.SIMPLE_DIALOG);

		// Create button panes
		buttonPanes = new ArrayList<>();
		for (int i = 0; i < numButtonPanes; i++)
		{
			// Create button pane
			DialogButtonPane buttonPane = new DialogButtonPane(getButtonGap());

			// If button pane is not the first pane, remove top padding
			if (i > 0)
			{
				Insets padding = buttonPane.getPadding();
				padding = new Insets(0.0, padding.getRight(), padding.getBottom(), padding.getLeft());
				buttonPane.setPadding(padding);
			}

			// Add button pane to list
			buttonPanes.add(buttonPane);

			// Add button pane to main pane
			mainPane.getChildren().add(buttonPane);
		}

		// Create scene and set it on this window
		setScene(new Scene(mainPane));

		// Apply style sheet to scene
		applyStyleSheet();

		// Update UI before window is displayed
		boolean[] widthSet = { false };
		addEventHandler(WindowEvent.WINDOW_SHOWING, event ->
		{
			// Update spacing of button groups
			for (DialogButtonPane buttonPane : buttonPanes)
				buttonPane.updateButtonSpacing();

			// Set location of window to previous value
			if (locationKey != null)
			{
				Point2D location = locations.get(locationKey);
				if (location != null)
				{
					setX(location.getX());
					setY(location.getY());
				}
			}

			// If no size was provided, get previous size of window
			Dimension2D size0 = size;
			if ((size0 == null) && (sizeKey != null))
				size0 = sizes.get(sizeKey);

			// Set dimensions of window
			if (size0 != null)
			{
				// Set width
				double width = size0.getWidth();
				if (width > 0.0)
				{
					setWidth(width);
					widthSet[0] = true;
				}

				// Set height
				double height = size0.getHeight();
				if (height > 0.0)
					setHeight(height);
			}
		});

		// Update UI after window is displayed
		addEventHandler(WindowEvent.WINDOW_SHOWN, event ->
		{
			// If width has not been set, equalise widths of groups of buttons
			double extraWidth = 0.0;
			if (!widthSet[0])
			{
				// Equalise widths of groups of buttons
				for (DialogButtonPane buttonPane : buttonPanes)
				{
					double ew = buttonPane.equaliseButtonWidths();
					if (extraWidth < ew)
						extraWidth = ew;
				}

				// Increase width of window to accommodate extra width of buttons
				if (extraWidth > 0.0)
				{
					// Temporarily allow window to be resized
					boolean fixedSize = !isResizable();
					if (fixedSize)
						setResizable(true);

					// Increase width of window
					setWidth(getWidth() + extraWidth);

					// Prevent window from being resized
					if (fixedSize)
						setResizable(false);
				}
			}

			// Get dimensions of window
			double width = getWidth();
			double height = getHeight();

			// If there is no previous location of window, get location from locator ...
			Point2D location = (locationKey == null) ? null : locations.get(locationKey);
			if (location == null)
			{
				if (locator != null)
					location = locator.getLocation(width, height);
			}

			// ... otherwise, ensure that window rectangle intersects a screen
			else if (Screen.getScreensForRectangle(getX(), getY(), width, height).isEmpty())
			{
				// Remove location from map
				locations.remove(locationKey);

				// Invalidate location
				location = null;
			}

			// If there is a location, invalidate it if top centre of window is not within a screen
			if ((location != null)
					&& !SceneUtils.isWithinScreen(location.getX() + 0.5 * width, location.getY(), SCREEN_MARGINS))
				location = null;

			// If there is no location, locate window relative to owner
			if (location == null)
			{
				// If owner is showing, locate window relative to owner ...
				if ((owner != null) && owner.isShowing())
				{
					location = SceneUtils.getRelativeLocation(width, height, owner.getX(), owner.getY(),
															  owner.getWidth(), owner.getHeight());
				}

				// ... otherwise, if there is no screen, adjust x coordinate of window
				else if (Screen.getScreens().isEmpty())
					setX(getX() - 0.5 * extraWidth);
			}

			// If there is no location, centre window within primary screen
			if (location == null)
				location = SceneUtils.centreInScreen(width, height);

			// Set location of window
			setX(location.getX());
			setY(location.getY());

			// Make window visible
			Platform.runLater(() -> setOpacity(1.0));
		});

		// Save location and size of window when it is closed
		if ((locationKey != null) || (sizeKey != null))
		{
			addEventHandler(WindowEvent.WINDOW_HIDING, event ->
			{
				// Uniconify window
				if (isIconified())
					setIconified(false);

				// Unmaximise window
				if (isMaximized())
					setMaximized(false);

				// Save location of window
				if (locationKey != null)
					locations.put(locationKey, new Point2D(getX(), getY()));

				// Save size of window
				if (sizeKey != null)
					sizes.put(sizeKey, new Dimension2D(getWidth(), getHeight()));
			});
		}

		// If dialog has owner, request focus on it when dialog is closed
		if (owner != null)
			addEventHandler(WindowEvent.WINDOW_HIDDEN, event -> owner.requestFocus());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if the map of dialog locations contains the specified key.
	 *
	 * @param  key
	 *           the key of interest.
	 * @return {@code true} if the map of dialog locations contains {@code key}.
	 */

	public static boolean hasLocation(
		String	key)
	{
		return locations.containsKey(key);
	}

	//------------------------------------------------------------------

	/**
	 * Adds an entry for the specified key and location to the map of dialog locations.
	 *
	 * @param key
	 *          the key of the map entry.
	 * @param location
	 *          the location that will be associated with {@code key} in the map of dialog locations.
	 */

	public static void addLocation(
		String	key,
		Point2D	location)
	{
		locations.put(key, location);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the map of dialog sizes contains the specified key.
	 *
	 * @param  key
	 *           the key of interest.
	 * @return {@code true} if the map of dialog sizes contains {@code key}.
	 */

	public static boolean hasSize(
		String	key)
	{
		return sizes.containsKey(key);
	}

	//------------------------------------------------------------------

	/**
	 * Adds an entry for the specified key and size to the map of dialog sizes.
	 *
	 * @param key
	 *          the key of the map entry.
	 * @param size
	 *          the size that will be associated with {@code key} in the map of dialog sizes.
	 */

	public static void addSize(
		String		key,
		Dimension2D	size)
	{
		sizes.put(key, size);
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
	 * Returns the content of this dialog.  By default, this is the {@linkplain #getContentPane() content pane}.
	 *
	 * @return the content of this dialog.
	 */

	public Node getContent()
	{
		return content;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the main pane of this dialog.
	 *
	 * @return the main pane of this dialog.
	 */

	public VBox getMainPane()
	{
		return mainPane;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the content pane of this dialog.
	 *
	 * @return the content pane of this dialog.
	 */

	public StackPane getContentPane()
	{
		return contentPane;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the button pane with the specified index.
	 *
	 * @param  index
	 *           the index of the required buton pane.
	 * @return the button pane whose index is {@code index}.
	 */
	public DialogButtonPane getButtonPane(
		int	index)
	{
		return buttonPanes.get(index);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the content of this dialog to the specified node.  The node will replace the default {@linkplain
	 * #getContentPane() content pane}.
	 *
	 * @param  content
	 *           the node that will be set as the content of this dialog.
	 * @throws IllegalArgumentException
	 *           if {@code content} is {@code null}.
	 */

	public void setContent(
		Node	content)
	{
		// Validate argument
		if (content == null)
			throw new IllegalArgumentException("Null content");

		// Update instance variables
		this.content = content;
		if (contentPane != content)
			contentPane = null;

		// Set properties of content node
		VBox.setVgrow(content, Priority.ALWAYS);

		// Set content node and button panes as children of main pane
		mainPane.getChildren().setAll(content);
		mainPane.getChildren().addAll(buttonPanes);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified node to the content pane of this dialog.
	 *
	 * @param  node
	 *           the node that will be added to the content pane of this dialog.
	 * @throws IllegalStateException
	 *           if this dialog does not have a content pane because it has been replaced by {@link #setContent(Node)}.
	 */

	public void addContent(
		Node	node)
	{
		// Test for content pane
		if (contentPane == null)
			throw new IllegalStateException("No content pane");

		// Add node to content pane
		contentPane.getChildren().add(node);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified button to the specified horizontal zone of the first button pane of this dialog.  The padding
	 * that is returned by {@link #getButtonPadding()} will be set on the button.
	 *
	 * @param  button
	 *           the button that will be added to the first button pane.
	 * @param  position
	 *           the horizontal zone in which the button will appear.
	 * @throws IllegalStateException
	 *           if this method is called when the window is showing.
	 */

	public void addButton(
		Region	button,
		HPos	position)
	{
		addButton(0, button, position, true);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified button to the specified horizontal zone of the first button pane of this dialog.  The padding
	 * that is returned by {@link #getButtonPadding()} may optionally be set on the button.
	 *
	 * @param  button
	 *           the button that will be added to the first button pane.
	 * @param  position
	 *           the horizontal zone in which the button will appear.
	 * @param  setPadding
	 *           if {@code true}, the padding that is returned by {@link #getButtonPadding()} will be set on {@code
	 *           button}.
	 * @throws IllegalStateException
	 *           if this method is called when the window is showing.
	 */

	public void addButton(
		Region	button,
		HPos	position,
		boolean	setPadding)
	{
		addButton(0, button, position, setPadding);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified button to the specified horizontal zone of the specified button pane of this dialog.  The
	 * padding that is returned by {@link #getButtonPadding()} will be set on the button.
	 *
	 * @param  buttonPaneIndex
	 *           the index of the button pane to which {@code button} will be added.
	 * @param  button
	 *           the button that will be added to the button pane.
	 * @param  position
	 *           the horizontal zone in which the button will appear.
	 * @throws IllegalStateException
	 *           if this method is called when the window is showing.
	 */

	public void addButton(
		int		buttonPaneIndex,
		Region	button,
		HPos	position)
	{
		addButton(buttonPaneIndex, button, position, true);
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified button to the specified horizontal zone of the specified button pane of this dialog.  The
	 * padding that is returned by {@link #getButtonPadding()} may optionally be set on the button.
	 *
	 * @param  buttonPaneIndex
	 *           the index of the button pane to which {@code button} will be added.
	 * @param  button
	 *           the button that will be added to the button pane.
	 * @param  position
	 *           the horizontal zone in which the button will appear.
	 * @param  setPadding
	 *           if {@code true}, the padding that is returned by {@link #getButtonPadding()} will be set on {@code
	 *           button}.
	 * @throws IllegalStateException
	 *           if this method is called when the window is showing.
	 */

	public void addButton(
		int		buttonPaneIndex,
		Region	button,
		HPos	position,
		boolean	setPadding)
	{
		// Test whether window is showing
		if (isShowing())
			throw new IllegalStateException("Cannot add button when window is showing");

		// Set properties of button
		if (minButtonWidth >= 0.0)
			button.setMinWidth(minButtonWidth);
		if (setPadding)
			button.setPadding(getButtonPadding());

		// Add button to button pane
		buttonPanes.get(buttonPaneIndex).addButton(button, position);
	}

	//------------------------------------------------------------------

	/**
	 * Searches the button panes of this dialog for a button (an instance of {@link Labeled}) with the specified text,
	 * and returns the first such button that is found.
	 *
	 * @param  text
	 *           the text of the target button, which may be {@code null}.
	 * @return the child of the button panes that is an instance of {@link Labeled} and whose text is {@code text}.
	 */

	public Labeled findButton(
		String	text)
	{
		for (DialogButtonPane buttonPane : buttonPanes)
		{
			for (Node child : buttonPane.getChildren())
			{
				if ((child instanceof Labeled button) && Objects.equals(text, button.getText()))
					return button;
			}
		}
		return null;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the minimum width of a button to the specified value.  If the value is negative, no minimum width will be
	 * set on a button.  The default value is three times the height of text in the default font.
	 *
	 * @param width
	 *          the value to which the minimum width of a button will be set.  If it is negative, no minimum width will
	 *          be set on a button.
	 */

	public void setMinButtonWidth(
		double	width)
	{
		minButtonWidth = width;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the style sheet for the current theme on the scene of this dialog.
	 */

	public void applyStyleSheet()
	{
		StyleManager.INSTANCE.setStyleSheet(getScene());
	}

	//------------------------------------------------------------------

	/**
	 * Fires a <i>request to close window</i> event on this dialog.
	 */

	public void requestClose()
	{
		fireEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	//------------------------------------------------------------------

	/**
	 * Returns the gap between adjacent buttons in the button pane.  This is also the minimum extra gap between adjacent
	 * zones of the button pane.
	 *
	 * @return the gap between adjacent buttons in the button pane.
	 */

	protected double getButtonGap()
	{
		return DEFAULT_BUTTON_GAP;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the padding around the buttons in the button pane.
	 *
	 * @return the padding around the buttons in the button pane.
	 */

	protected Insets getButtonPadding()
	{
		return DEFAULT_BUTTON_PADDING;
	}

	//------------------------------------------------------------------

	/**
	 * Adds a <i>key pressed</i> event filter that fires a <i>request to close window</i> event on this dialog if the
	 * <i>Escape</i> key is pressed.
	 */

	protected void setRequestCloseOnEscape()
	{
		addEventFilter(KeyEvent.KEY_PRESSED, event ->
		{
			if (event.getCode() == KeyCode.ESCAPE)
			{
				// Fire 'request to close window' event
				requestClose();

				// Consume event
				event.consume();
			}
		});
	}

	//------------------------------------------------------------------

	/**
	 * Adds a <i>key pressed</i> event filter that fires the specified buttons if the corresponding key or key
	 * combination, <i>Escape</i> or <i>Ctrl+Enter</i>, is pressed.
	 *
	 * @param escButton
	 *          the button that will be fired if the <i>Escape</i> key is pressed; ignored if it is {@code null}.
	 * @param ctrlEnterButton
	 *          the button that will be fired if the <i>Ctrl+Enter</i> key combination is pressed; ignored if it is
	 *          {@code null}.
	 */

	protected void setKeyFireButton(
		ButtonBase	escButton,
		ButtonBase	ctrlEnterButton)
	{
		setKeyFireButton(escButton, ctrlEnterButton, true);
	}

	//------------------------------------------------------------------

	/**
	 * Adds a <i>key pressed</i> event filter or event handler that fires the specified buttons if the corresponding key
	 * or key combination, <i>Escape</i> or <i>Ctrl+Enter</i>, is pressed.
	 *
	 * @param escButton
	 *          the button that will be fired if the <i>Escape</i> key is pressed; ignored if it is {@code null}.
	 * @param ctrlEnterButton
	 *          the button that will be fired if the <i>Ctrl+Enter</i> key combination is pressed; ignored if it is
	 *          {@code null}.
	 * @param filter
	 *          if {@code true}, an event filter will be added; otherwise, an event handler will be added.
	 */

	protected void setKeyFireButton(
		ButtonBase	escButton,
		ButtonBase	ctrlEnterButton,
		boolean     filter)
	{
		// Create event handler
		EventHandler<KeyEvent> eventHandler = event ->
		{
			switch (event.getCode())
			{
				case ESCAPE:
					if (escButton != null)
					{
						// Fire button
						escButton.fire();

						// Consume event
						event.consume();
					}
					break;

				case ENTER:
					if ((ctrlEnterButton != null) && event.isControlDown())
					{
						// Fire button
						ctrlEnterButton.fire();

						// Consume event
						event.consume();
					}
					break;

				default:
					// do nothing
					break;
			}
		};

		// Add event filter or handler
		if (filter)
			addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
		else
			addEventHandler(KeyEvent.KEY_PRESSED, eventHandler);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: LOCATOR FUNCTION


	/**
	 * This functional interface defines a method that returns the location of a {@link SimpleDialog} given the width
	 * and height of the dialog.
	 */

	@FunctionalInterface
	public interface ILocator
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the location of a dialog with the specified width and height.
		 *
		 * @param  width
		 *           the width of the dialog.
		 * @param  height
		 *           the height of the dialog.
		 * @return the location of the dialog whose dimensions are {@code width} and {@code height}.
		 */

		Point2D getLocation(
			double	width,
			double	height);

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
