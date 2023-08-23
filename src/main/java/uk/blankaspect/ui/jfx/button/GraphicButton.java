/*====================================================================*\

GraphicButton.java

Class: graphic button.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.button;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.EnumSet;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.css.PseudoClass;

import javafx.event.ActionEvent;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;

import javafx.scene.Group;
import javafx.scene.Node;

import javafx.scene.control.ButtonBase;
import javafx.scene.control.Skin;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Background;
import javafx.scene.layout.Border;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IProcedure0;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.ui.jfx.event.ModifierKey;

import uk.blankaspect.ui.jfx.popup.LabelPopUpManager;
import uk.blankaspect.ui.jfx.popup.PopUpEvent;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.shape.ShapeUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.tooltip.TooltipDecorator;

//----------------------------------------------------------------------


// CLASS: GRAPHIC BUTTON


/**
 * This class implements a button that contains a graphic (a JavaFX {@link Node}) but no text.  A button is rendered
 * through its {@linkplain Skin skin}; the default skin provides a background and a composite border that change
 * according to the button's state.
 * <p>
 * Buttons have the following attributes and properties:
 * </p>
 * <ul>
 *   <li>a <i>selectable</i> attribute that enables a latching <i>selected</i> property;</li>
 *   <li>a <i>toggle group</i> property that allows a button to be used as a <b>toggle button</b>;</li>
 *   <li>a <i>radio button</i> attribute that, in conjunction with the <i>toggle group</i> property, allows a button to
 *       serve as a <b>radio button</b>.</li>
 * </ul>
 * <p>
 * Through its attributes and properties, a button can be configured to operate in the following modes:
 * </p>
 * <dl>
 *   <dt>Command button</dt>
 *   <dd>
 *     If the button's <i>selectable</i> attribute is not set, it can be used to issue a command when it is fired.
 *   </dd>
 *   <dt>Selectable button</dt>
 *   <dd>
 *     If the button's <i>selectable</i> attribute is set, its <i>selected</i> state is toggled when the button is
 *     fired.  When a selectable button is selected, it is displayed with a darkened background and border unless the
 *     highlighting is disabled with {@link #setHighlightIfSelected(boolean)}.
 *   </dd>
 *   <dt>Toggle button</dt>
 *   <dd>
 *     A <i>selectable button</i> may belong to a {@link ToggleGroup}.  Only one button in a toggle group may be
 *     selected at a time.  The toggle group can be set with {@link #setToggleGroup(ToggleGroup)}, which implicitly sets
 *     the button's <i>selectable</i> attribute.
 *   </dd>
 *   <dt>Radio button</dt>
 *   <dd>
 *     A <i>selectable button</i> may be a <i>radio button</i>: if a radio button is in the <i>selected</i> state, it
 *     will not be deselected when it is fired.  The <i>radio button</i> attribute can be set with {@link
 *     #setRadioButton(boolean)}.  To be useful, a radio button must belong to a {@link ToggleGroup}; for convenience,
 *     the toggle group and <i>radio button</i> attribute can both be set with {@link #setToggleGroup(ToggleGroup,
 *     boolean)}, which also implicitly sets the button's <i>selectable</i> attribute.
 *   </dd>
 * </dl>
 */

public class GraphicButton
	extends ButtonBase
	implements Toggle
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The aggregate width of the various borders. */
	public static final		double	BORDER_WIDTH	= 3.0;

	/** The default padding around the graphic content of the button. */
	public static final		Insets	DEFAULT_PADDING	= new Insets(1.0);

	/** The default background colour of the button. */
	private static final	Color	DEFAULT_BACKGROUND_COLOUR	= Color.TRANSPARENT;

	/** The default border colour of the button. */
	private static final	Color	DEFAULT_BORDER_COLOUR	= Color.TRANSPARENT;

	/** The pseudo-class that is associated with the <i>highlighted</i> state. */
	private static final	PseudoClass	HIGHLIGHTED_PSEUDO_CLASS	= PseudoClass.getPseudoClass(PseudoClassKey.HIGHLIGHTED);

	/** The pseudo-class that is associated with the <i>inactive</i> state. */
	private static final	PseudoClass	INACTIVE_PSEUDO_CLASS		= PseudoClass.getPseudoClass(PseudoClassKey.INACTIVE);

	/** The pseudo-class that is associated with the <i>selected</i> state. */
	private static final	PseudoClass	SELECTED_PSEUDO_CLASS		= PseudoClass.getPseudoClass(FxPseudoClass.SELECTED);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BACKGROUND,
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(PseudoClassKey.INACTIVE)
						.desc(StyleClass.INNER_VIEW)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BACKGROUND_HOVERED,
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.HOVER)
						.desc(StyleClass.INNER_VIEW)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.SELECTED).pseudo(FxPseudoClass.HOVER)
						.desc(StyleClass.INNER_VIEW).pseudo(PseudoClassKey.HIGHLIGHTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BACKGROUND_ARMED,
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.ARMED)
						.desc(StyleClass.INNER_VIEW)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.SELECTED).pseudo(FxPseudoClass.ARMED)
						.desc(StyleClass.INNER_VIEW).pseudo(PseudoClassKey.HIGHLIGHTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BACKGROUND_SELECTED,
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.SELECTED)
						.desc(StyleClass.INNER_VIEW).pseudo(PseudoClassKey.HIGHLIGHTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.BORDER,
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON)
						.desc(StyleClass.INNER_VIEW)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.BORDER_HOVERED,
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.HOVER)
						.desc(StyleClass.INNER_VIEW)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.SELECTED).pseudo(FxPseudoClass.HOVER)
						.desc(StyleClass.INNER_VIEW).pseudo(PseudoClassKey.HIGHLIGHTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.BORDER_ARMED,
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.ARMED)
						.desc(StyleClass.INNER_VIEW)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.SELECTED).pseudo(FxPseudoClass.ARMED)
						.desc(StyleClass.INNER_VIEW).pseudo(PseudoClassKey.HIGHLIGHTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.BORDER_SELECTED,
			CssSelector.builder()
						.cls(StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.SELECTED)
						.desc(StyleClass.INNER_VIEW).pseudo(PseudoClassKey.HIGHLIGHTED)
						.build()
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	GRAPHIC_BUTTON	= StyleConstants.CLASS_PREFIX + "graphic-button";
		String	INNER_VIEW		= StyleConstants.CLASS_PREFIX + "inner-view";
		String	OUTER_VIEW		= StyleConstants.CLASS_PREFIX + "outer-view";
	}

	/** Keys of CSS pseudo-classes. */
	public interface PseudoClassKey
	{
		String	HIGHLIGHTED	= "highlighted";
		String	INACTIVE	= "inactive";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	BACKGROUND			= PREFIX + "background";
		String	BACKGROUND_ARMED	= PREFIX + "background.armed";
		String	BACKGROUND_HOVERED	= PREFIX + "background.hovered";
		String	BACKGROUND_SELECTED	= PREFIX + "background.selected";
		String	BORDER				= PREFIX + "border";
		String	BORDER_ARMED		= PREFIX + "border.armed";
		String	BORDER_HOVERED		= PREFIX + "border.hovered";
		String	BORDER_SELECTED		= PREFIX + "border.selected";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The node that provides the skin for this button. */
	private View    							view;

	/** Flag: if {@code true}, the mouse cursor is within this button. */
	private	boolean								mouseWithin;

	/** Flag: if {@code true}, the primary mouse button is pressed when the mouse cursor is within this button. */
	private	boolean								pressed;

	/** Flag: if {@code true}, this button can be selected. */
	private	boolean								selectable;

	/** Flag: if {@code true}, a background and border are drawn for this button if it is selected. */
	private	boolean								highlightIfSelected;

	/** Flag: if {@code true}, this button behaves as a radio button. */
	private	boolean								radioButton;

	/** Flag: if {@code true}, this button is selected. */
	private	SimpleBooleanProperty				selected;

	/** The toggle group to which this button belongs. */
	private	SimpleObjectProperty<ToggleGroup>	toggleGroup;

	/** The modifiers whose keys were down when this button was last fired. */
	private	EnumSet<ModifierKey>				modifiers;

	/** The tooltip text for this button. */
	private	String								tooltipText;

	/** The manager of the pop-up for the tooltip. */
	private	LabelPopUpManager					tooltipPopUpManager;

	/** The background colour of this button. */
	private	Color								backgroundColour;

	/** The border colour of this button. */
	private	Color								borderColour;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(GraphicButton.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a button that contains the specified graphic.
	 *
	 * @param graphic
	 *          the graphic that will be displayed in the button, which may be {@code null}.
	 */

	public GraphicButton(
		Node	graphic)
	{
		// Call alternative constructor
		this(graphic, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a button that contains the specified graphic and has the specified tooltip text.
	 *
	 * @param graphic
	 *          the graphic that will be displayed in the button, which may be {@code null}.
	 * @param tooltipText
	 *          the text of the tooltip for the button, which may be {@code null}.
	 */

	public GraphicButton(
		Node	graphic,
		String	tooltipText)
	{
		// Initialise instance variables
		view = new View();
		highlightIfSelected = true;
		selected = new SimpleBooleanProperty(false);
		toggleGroup = new SimpleObjectProperty<>();
		backgroundColour = DEFAULT_BACKGROUND_COLOUR;
		borderColour = DEFAULT_BORDER_COLOUR;

		// Set properties
		setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
		setPadding(DEFAULT_PADDING);
		setBackground(Background.EMPTY);
		setBorder(Border.EMPTY);
		if (graphic != null)
			setGraphic(graphic);
		if (tooltipText != null)
			setTooltipText(tooltipText);
		getStyleClass().add(StyleClass.GRAPHIC_BUTTON);

		// Create procedure to update 'inactive' pseudo-class state
		IProcedure0 updateInactiveState = () -> pseudoClassStateChanged(INACTIVE_PSEUDO_CLASS, !mouseWithin);

		// Create procedure to update 'hover' pseudo-class state
		IProcedure1<Boolean> updateHoverState = hovered ->
				pseudoClassStateChanged(PseudoClass.getPseudoClass(FxPseudoClass.HOVER), hovered);

		// Initialise 'inactive' pseudo-class state
		updateInactiveState.invoke();

		// Update button view
		update();

		// Update button view when graphic changes
		graphicProperty().addListener(observable -> update());

		// Update button view when padding changes
		paddingProperty().addListener(observable -> update());

		// Update button view when button is disabled or enabled
		disabledProperty().addListener(observable -> update());

		// Update button view when button gains or loses focus
		focusedProperty().addListener(observable -> update());

		// Update button view when button's 'selected' state changes
		selected.addListener(observable ->
		{
			// Update pseudo-class state
			pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, isSelected());

			// Update button view
			update();
		});

		// Update button view when preferred width of button changes
		prefWidthProperty().addListener(observable -> update());

		// Update button view when preferred height of button changes
		prefHeightProperty().addListener(observable -> update());

		// Update button view when mouse enters or leaves button
		addEventHandler(MouseEvent.MOUSE_ENTERED, event ->
		{
			// Update 'mouse within' flag
			mouseWithin = true;

			// Set 'armed' state
			if (pressed)
				arm();

			// Update pseudo-class states
			updateInactiveState.invoke();
			updateHoverState.invoke(!pressed);

			// Update button view
			update();
		});
		addEventHandler(MouseEvent.MOUSE_EXITED, event ->
		{
			// Update 'mouse within' flag
			mouseWithin = false;

			// Clear 'armed' state
			disarm();

			// Update pseudo-class states
			updateInactiveState.invoke();

			// Update button view
			update();
		});

		// Update state and button view when primary mouse button is pressed or released
		addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
		{
			if (event.getButton() == MouseButton.PRIMARY)
			{
				// Update 'pressed' flag
				pressed = true;

				// Set 'armed' state
				arm();

				// Update pseudo-class states
				updateHoverState.invoke(false);

				// Update button view
				update();

				// Consume event
				event.consume();
			}
		});
		addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
		{
			// Request focus
			if (mouseWithin)
				requestFocus();

			// If primary mouse button, update state and button view
			if (event.getButton() == MouseButton.PRIMARY)
			{
				// Update 'pressed' flag
				pressed = false;

				// Clear 'armed' state
				disarm();

				// Update pseudo-class states
				updateHoverState.invoke(mouseWithin);

				// Update button view
				update();

				// Consume event
				event.consume();
			}
		});

		// Fire button when primary mouse button is clicked on it
		addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
		{
			if (event.getButton() == MouseButton.PRIMARY)
			{
				// Update modifiers
				modifiers = ModifierKey.forMouseEvent(event);

				// Fire button
				fire();

				// Consume event
				event.consume();
			}
		});

		// Fire button when space key is pressed
		addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if (event.getCode() == KeyCode.SPACE)
			{
				// Update modifiers
				modifiers = ModifierKey.forKeyEvent(event);

				// Fire button
				fire();

				// Consume event
				event.consume();
			}
		});

		// Create pop-up to display tooltip
		tooltipPopUpManager = TooltipDecorator.addTooltip(this, () -> this.tooltipText);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

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
//  Instance methods : Toggle interface
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public ToggleGroup getToggleGroup()
	{
		return toggleGroup.get();
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 * @see #setToggleGroup(ToggleGroup, boolean)
	 */

	@Override
	public void setToggleGroup(
		ToggleGroup	toggleGroup)
	{
		setToggleGroup(toggleGroup, false);
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public ObjectProperty<ToggleGroup> toggleGroupProperty()
	{
		return toggleGroup;
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public boolean isSelected()
	{
		return selected.get();
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void setSelected(
		boolean	selected)
	{
		if (selectable)
			this.selected.set(selected);
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public BooleanProperty selectedProperty()
	{
		return selected;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Fires this button.  If the button is not disabled, the following actions are performed:
	 * <ul>
	 *   <li>If the button is selectable <b>and</b> it is not a radio button that is currently selected, its
	 *       <i>selected</i> state is toggled.</li>
	 *   <li>An {@link ActionEvent} with the event type {@code ACTION} is fired.</li>
	 * </ul>
	 */

	@Override
	public void fire()
	{
		if (!isDisabled())
		{
			if (!(radioButton && isSelected()))
				toggleSelected();
			fireEvent(new ActionEvent());
		}
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected Skin<GraphicButton> createDefaultSkin()
	{
		return new Skin<>()
		{
			@Override
			public GraphicButton getSkinnable()
			{
				return GraphicButton.this;
			}

			@Override
			public View getNode()
			{
				return view;
			}

			@Override
			public void dispose()
			{
				// do nothing
			}
		};
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a set of the modifiers whose keys were down when this button was last fired.
	 *
	 * @return a set of the modifiers whose keys were down when this button was last fired.
	 */

	public EnumSet<ModifierKey> getModifiers()
	{
		return modifiers;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the width and height of this button.
	 *
	 * @return the width and height of this button.
	 */

	public Dimension2D getSize()
	{
		// Get graphic
		Node graphic = getGraphic();

		// Get bounds of graphic
		Bounds bounds = (graphic == null) ? null : graphic.getLayoutBounds();

		// Get insets
		Insets insets = getInsets();

		// Calculate width
		double width = getPrefWidth();
		if (width == USE_COMPUTED_SIZE)
		{
			width = 2.0 * BORDER_WIDTH + insets.getLeft() + insets.getRight();
			if (bounds != null)
				width += bounds.getWidth();
		}

		// Calculate height
		double height = getPrefHeight();
		if (height == USE_COMPUTED_SIZE)
		{
			height = 2.0 * BORDER_WIDTH + insets.getTop() + insets.getBottom();
			if (bounds != null)
				height += bounds.getHeight();
		}

		// Return width and height
		return new Dimension2D(width, height);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the text that is displayed in the tooltip for this button.
	 *
	 * @param text
	 *          the text that will be displayed in the tooltip for this button.
	 */

	public void setTooltipText(
		String	text)
	{
		// Update instance variable
		tooltipText = text;

		// Fire event to notify listeners that content of pop-up has changed
		fireEvent(new PopUpEvent(PopUpEvent.CONTENT_CHANGED));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>selectable</i> attribute of this button.
	 *
	 * @param selectable
	 *          the value to which the <i>selectable</i> attribute of this button will be set.
	 */

	public void setSelectable(
		boolean	selectable)
	{
		this.selectable = selectable;
	}

	//------------------------------------------------------------------

	/**
	 * Enables or disables highlighting of this button if it is selected.
	 *
	 * @param highlight
	 *          if {@code true}, draw a border and background for this button if it is selected.
	 */

	public void setHighlightIfSelected(
		boolean	highlight)
	{
		highlightIfSelected = highlight;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>radio button</i> attribute of this button.  If the <i>radio button</i> attribute is set and the
	 * button is in the <i>selected</i> state, the button will not be deselected when it is fired.
	 *
	 * @param radioButton
	 *          the value to which the <i>radio button</i> attribute of this button will be set.
	 */

	public void setRadioButton(
		boolean	radioButton)
	{
		this.radioButton = radioButton;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the toggle group to which this button belongs and the <i>radio button</i> attribute of this button to the
	 * specified values.
	 *
	 * @param toggleGroup
	 *          the toggle group to which this button will belong.  If {@code toggleGroup} is {@code null}, this button
	 *          will be removed from the toggle group to which it currently belongs.  If {@code toggleGroup} is not
	 *          {@code null}, the <i>selectable</i> attribute of this button will be set.
	 * @param radioButton
	 *          the value to which the <i>radio button</i> attribute of this button will be set.  This parameter will be
	 *          ignored if {@code toggleGroup} is {@code null}.
	 * @see   #setToggleGroup(ToggleGroup)
	 * @see   #setRadioButton(boolean)
	 */

	public void setToggleGroup(
		ToggleGroup	toggleGroup,
		boolean		radioButton)
	{
		// Remove button from its current toggle group
		ToggleGroup oldToggleGroup = getToggleGroup();
		if (oldToggleGroup != null)
		{
			// Deselect button
			if (oldToggleGroup.getSelectedToggle() == this)
				setSelected(false);

			// Make button unselectable
			selectable = false;

			// Remove button from toggle group
			oldToggleGroup.getToggles().remove(this);
		}

		// Make button selectable
		if (toggleGroup != null)
		{
			selectable = true;
			this.radioButton = radioButton;
		}

		// Set 'toggle group' property
		this.toggleGroup.set(toggleGroup);
	}

	//------------------------------------------------------------------

	/**
	 * Toggles the <i>selected</i> state of this button (ie, if the button is selected, it will be deselected, and vice
	 * versa).
	 *
	 * @see #isSelected()
	 * @see #setSelected(boolean)
	 * @see #selectedProperty()
	 */

	public void toggleSelected()
	{
		if (selectable)
			selected.set(!selected.get());
	}

	//------------------------------------------------------------------

	/**
	 * Returns the manager of the pop-up for the tooltip of this button.
	 *
	 * @return the manager of the pop-up for the tooltip of this button.
	 */

	public LabelPopUpManager getTooltipPopupManager()
	{
		return tooltipPopUpManager;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background colour of this button to the specified value.
	 *
	 * @param colour
	 *          the value to which the background colour of this button will be set.  If it is {@code null}, the
	 *          background colour will be set to its default value (transparent).
	 */

	public void setBackgroundColour(
		Color	colour)
	{
		// Update instance variable
		backgroundColour = (colour == null) ? DEFAULT_BACKGROUND_COLOUR : colour;

		// Update button view
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the border colour of this button to the specified value.
	 *
	 * @param colour
	 *          the value to which the border colour of this button will be set.  If it is {@code null}, the border
	 *          colour will be set to its default value (transparent).
	 */

	public void setBorderColour(
		Color	colour)
	{
		// Update instance variable
		borderColour = (colour == null) ? DEFAULT_BORDER_COLOUR : colour;

		// Update button view
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Updates the view of this button.  A subclass that provides its own skin for this button may override this method
	 * to redraw the skin in response to changes to the button's state.
	 */

	protected void update()
	{
		SceneUtils.runOnFxApplicationThread(() -> view.update());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: BUTTON VIEW


	/**
	 * This class implements a node that provides the default skin for the enclosing instance of a button.
	 */

	private class View
		extends Group
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The opacity of the graphic of a disabled button. */
		private static final	double	DISABLED_OPACITY	= 0.4;

		/** The amount by which the focus-indicator border is inset. */
		private static final	double	FOCUSED_BORDER_INSET	= 1.0;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a view for the enclosing instance of a button.
		 */

		private View()
		{
			// Set properties
			setMouseTransparent(true);
			getStyleClass().add(StyleClass.OUTER_VIEW);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Updates this button view.
		 */

		private void update()
		{
			// Get graphic
			GraphicButton button = GraphicButton.this;
			Node graphic = button.getGraphic();

			// If button is disabled, reduce opacity of graphic
			if ((graphic != null) && button.isDisabled())
			{
				// Wrap graphic in group
				graphic = new Group(graphic);

				// Reduce opacity of graphic
				graphic.setOpacity(DISABLED_OPACITY);
			}

			// Get background colour
			Color backgroundColour = mouseWithin
										? pressed
												? getColour(ColourKey.BACKGROUND_ARMED)
												: getColour(ColourKey.BACKGROUND_HOVERED)
										: (button.isSelected() && highlightIfSelected)
												? getColour(ColourKey.BACKGROUND_SELECTED)
												: button.backgroundColour;

			// Get border colour
			Color borderColour = mouseWithin
										? pressed
												? getColour(ColourKey.BORDER_ARMED)
												: getColour(ColourKey.BORDER_HOVERED)
										: (button.isSelected() && highlightIfSelected)
												? getColour(ColourKey.BORDER_SELECTED)
												: button.borderColour;

			// Remove all children
			getChildren().clear();

			// Get dimensions of button
			Dimension2D size = getSize();
			double width = size.getWidth();
			double height = size.getHeight();

			// Create background and add it to children
			Rectangle rect = new Rectangle(width, height, backgroundColour);
			rect.setStroke(borderColour);
			rect.setStrokeType(StrokeType.INSIDE);
			rect.getStyleClass().add(StyleClass.INNER_VIEW);
			if (highlightIfSelected)
				rect.pseudoClassStateChanged(HIGHLIGHTED_PSEUDO_CLASS, true);
			getChildren().add(rect);

			// Create focus-indicator border for button and add it to children
			if (button.isFocused() && !mouseWithin && !pressed)
				getChildren().addAll(ShapeUtils.createFocusBorder(width, height, FOCUSED_BORDER_INSET));

			// Add graphic to children
			if (graphic != null)
			{
				// Get bounds of graphic
				Bounds bounds = graphic.getLayoutBounds();

				// Set location of graphic
				graphic.relocate(0.5 * (width - bounds.getWidth()), 0.5 * (height - bounds.getHeight()));

				// Add graphic to children
				getChildren().add(graphic);
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
