/*====================================================================*\

ToggleSwitch.java

Class: toggle switch.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.button;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.css.PseudoClass;

import javafx.event.ActionEvent;

import javafx.scene.Group;

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
import javafx.scene.layout.Region;

import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.shape.ShapeUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: TOGGLE SWITCH


public class ToggleSwitch
	extends ButtonBase
	implements Toggle
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The pseudo-class that is associated with the <i>selected</i> state. */
	private static final	PseudoClass	PSEUDO_CLASS_SELECTED	= PseudoClass.getPseudoClass(FxPseudoClass.SELECTED);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TRACK,
			CssSelector.builder()
					.cls(StyleClass.TOGGLE_SWITCH)
					.desc(StyleClass.TRACK)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TRACK_DISABLED,
			CssSelector.builder()
					.cls(StyleClass.TOGGLE_SWITCH).pseudo(FxPseudoClass.DISABLED)
					.desc(StyleClass.TRACK)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TRACK_DISABLED,
			CssSelector.builder()
					.cls(StyleClass.TOGGLE_SWITCH).pseudo(FxPseudoClass.DISABLED).pseudo(FxPseudoClass.SELECTED)
					.desc(StyleClass.TRACK)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TRACK_SELECTED,
			CssSelector.builder()
					.cls(StyleClass.TOGGLE_SWITCH).pseudo(FxPseudoClass.SELECTED)
					.desc(StyleClass.TRACK)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.SLIDER,
			CssSelector.builder()
					.cls(StyleClass.TOGGLE_SWITCH)
					.desc(StyleClass.SLIDER)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.SLIDER_DISABLED,
			CssSelector.builder()
					.cls(StyleClass.TOGGLE_SWITCH).pseudo(FxPseudoClass.DISABLED)
					.desc(StyleClass.SLIDER)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.SLIDER_DISABLED,
			CssSelector.builder()
					.cls(StyleClass.TOGGLE_SWITCH).pseudo(FxPseudoClass.DISABLED).pseudo(FxPseudoClass.SELECTED)
					.desc(StyleClass.SLIDER)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.SLIDER_SELECTED,
			CssSelector.builder()
					.cls(StyleClass.TOGGLE_SWITCH).pseudo(FxPseudoClass.SELECTED)
					.desc(StyleClass.SLIDER)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	SLIDER			= StyleConstants.CLASS_PREFIX + "slider";
		String	TOGGLE_SWITCH	= StyleConstants.CLASS_PREFIX + "toggle-switch";
		String	TRACK			= StyleConstants.CLASS_PREFIX + "track";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	SLIDER			= PREFIX + "slider";
		String	SLIDER_DISABLED	= PREFIX + "slider.disabled";
		String	SLIDER_SELECTED	= PREFIX + "slider.selected";
		String	TRACK			= PREFIX + "track";
		String	TRACK_DISABLED	= PREFIX + "track.disabled";
		String	TRACK_SELECTED	= PREFIX + "track.selected";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The node that provides the skin for this toggle switch. */
	private View    							view;

	/** Flag: if {@code true}, this toggle switch behaves as a radio button. */
	private	boolean								radioButton;

	/** Flag: if {@code true}, this toggle switch is selected. */
	private	SimpleBooleanProperty				selected;

	/** The toggle group to which this toggle switch belongs. */
	private	SimpleObjectProperty<ToggleGroup>	toggleGroup;

	/** The colour of the track. */
	private	Color								trackColour;

	/** The colour of the track when this toggle switch is selected. */
	private	Color								trackColourSelected;

	/** The colour of the slider. */
	private	Color								sliderColour;

	/** The colour of the slider when this toggle switch is selected. */
	private	Color								sliderColourSelected;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(ToggleSwitch.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a toggle switch.
	 */

	public ToggleSwitch()
	{
		// Initialise instance variables
		view = new View();
		selected = new SimpleBooleanProperty(false);
		toggleGroup = new SimpleObjectProperty<>();
		trackColour = getColour(ColourKey.TRACK);
		trackColourSelected = getColour(ColourKey.TRACK_SELECTED);
		sliderColour = getColour(ColourKey.SLIDER);
		sliderColourSelected = getColour(ColourKey.SLIDER_SELECTED);

		// Set properties
		setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		setBackground(Background.EMPTY);
		setBorder(Border.EMPTY);
		setPickOnBounds(true);
		getStyleClass().add(StyleClass.TOGGLE_SWITCH);

		// Update view when toggle switch is disabled or enabled
		disabledProperty().addListener(observable -> update());

		// Update view when toggle switch gains or loses focus
		focusedProperty().addListener(observable -> update());

		// Update view when 'selected' state of toggle switch changes
		selected.addListener(observable ->
		{
			pseudoClassStateChanged(PSEUDO_CLASS_SELECTED, isSelected());
			update();
		});

		// Update view when preferred width of toggle switch changes
		prefWidthProperty().addListener(observable -> update());

		// Update view when preferred height of toggle switch changes
		prefHeightProperty().addListener(observable -> update());

		// If toggle switch is focus-traversable, request focus when mouse button is pressed on it
		addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
		{
			if (isFocusTraversable())
				requestFocus();
		});

		// Cause toggle switch to fire when primary mouse button is clicked on it
		addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
		{
			if (event.getButton() == MouseButton.PRIMARY)
			{
				// Fire toggle switch
				fire();

				// Consume event
				event.consume();
			}
		});

		// Cause toggle switch to fire when space key is pressed
		addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if (event.getCode() == KeyCode.SPACE)
			{
				// Fire toggle switch
				fire();

				// Consume event
				event.consume();
			}
		});

		// Draw toggle switch
		update();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

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
	 * Fires this toggle switch.  If the component is not disabled, the following actions are performed:
	 * <ul>
	 *   <li>
	 *     If the toggle switch is not a radio button that is currently selected, its <i>selected</i> state is toggled.
	 *   </li>
	 *   <li>
	 *     An {@link ActionEvent} with the event type {@code ACTION} is fired.
	 *   </li>
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
	protected Skin<ToggleSwitch> createDefaultSkin()
	{
		return new Skin<>()
		{
			@Override
			public ToggleSwitch getSkinnable()
			{
				return ToggleSwitch.this;
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
	 * Sets the <i>radio button</i> attribute of this toggle switch.  If the <i>radio button</i> attribute is set and
	 * the toggle switch is in the <i>selected</i> state, the switch will not be deselected when it is fired.
	 *
	 * @param radioButton
	 *          the value to which the <i>radio button</i> attribute of this toggle switch will be set.
	 */

	public void setRadioButton(
		boolean	radioButton)
	{
		this.radioButton = radioButton;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the colour of the track of this toggle switch.
	 *
	 * @return the colour of the track of this toggle switch.
	 */

	public Color getTrackColour()
	{
		return trackColour;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the colour of the track of this toggle switch to the specified value.
	 *
	 * @param colour
	 *          the value to which the colour of the track of this toggle switch will be set.  If it is {@code null},
	 *          the default colour will be restored.
	 */

	public void setTrackColour(
		Color	colour)
	{
		if (!trackColour.equals(colour))
		{
			// Update instance variable
			trackColour = (colour == null) ? getColour(ColourKey.TRACK) : colour;

			// Update view
			update();
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns the colour of the track of this toggle switch when the switch is selected.
	 *
	 * @return the colour of the track of this toggle switch when the switch is selected.
	 */

	public Color getTrackColourSelected()
	{
		return trackColourSelected;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the colour of the track of this toggle switch when the switch is selected to the specified value.
	 *
	 * @param colour
	 *          the value to which the colour of the track of this toggle switch when the switch is selected will be
	 *          set.  If it is {@code null}, the default colour will be restored.
	 */

	public void setTrackColourSelected(
		Color	colour)
	{
		if (!trackColourSelected.equals(colour))
		{
			// Update instance variable
			trackColourSelected = (colour == null) ? getColour(ColourKey.TRACK_SELECTED) : colour;

			// Update view
			update();
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns the colour of the slider of this toggle switch.
	 *
	 * @return the colour of the slider of this toggle switch.
	 */

	public Color getSliderColour()
	{
		return sliderColour;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the colour of the slider of this toggle switch to the specified value.
	 *
	 * @param colour
	 *          the value to which the colour of the slider of this toggle switch will be set.  If it is {@code null},
	 *          the default colour will be restored.
	 */

	public void setSliderColour(
		Color	colour)
	{
		if (!sliderColour.equals(colour))
		{
			// Update instance variable
			sliderColour = (colour == null) ? getColour(ColourKey.SLIDER) : colour;

			// Update view
			update();
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns the colour of the slider of this toggle switch when the switch is selected.
	 *
	 * @return the colour of the slider of this toggle switch when the switch is selected.
	 */

	public Color getSliderColourSelected()
	{
		return sliderColourSelected;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the colour of the slider of this toggle switch when the switch is selected to the specified value.
	 *
	 * @param colour
	 *          the value to which the colour of the slider of this toggle switch when the switch is selected will be
	 *          set.  If it is {@code null}, the default colour will be restored.
	 */

	public void setSliderColourSelected(
		Color	colour)
	{
		if (!sliderColourSelected.equals(colour))
		{
			// Update instance variable
			sliderColourSelected = (colour == null) ? getColour(ColourKey.SLIDER_SELECTED) : colour;

			// Update view
			update();
		}
	}

	//------------------------------------------------------------------

	/**
	 * Sets the toggle group to which this toggle switch belongs and sets the <i>radio-button</i> attribute of this
	 * switch to the specified values.
	 *
	 * @param toggleGroup
	 *          the toggle group to which this toggle switch will belong.  If {@code toggleGroup} is {@code null}, this
	 *          switch will be removed from the toggle group to which it currently belongs.
	 * @param radioButton
	 *          the value to which the <i>radio-button</i> attribute of this toggle switch will be set.  This parameter
	 *          will be ignored if {@code toggleGroup} is {@code null}.
	 * @see   #setToggleGroup(ToggleGroup)
	 * @see   #setRadioButton(boolean)
	 */

	public void setToggleGroup(
		ToggleGroup	toggleGroup,
		boolean		radioButton)
	{
		// Remove toggle switch from its current toggle group
		ToggleGroup oldToggleGroup = getToggleGroup();
		if (oldToggleGroup != null)
		{
			// Deselect toggle switch
			if (oldToggleGroup.getSelectedToggle() == this)
				setSelected(false);

			// Remove toggle switch from toggle group
			oldToggleGroup.getToggles().remove(this);
		}

		// Update 'radio button' flag
		if (toggleGroup != null)
			this.radioButton = radioButton;

		// Set toggle-group property
		this.toggleGroup.set(toggleGroup);
	}

	//------------------------------------------------------------------

	/**
	 * Toggles the <i>selected</i> state of this toggle switch (ie, if the switch is selected, it will be deselected,
	 * and vice versa).
	 *
	 * @see #isSelected()
	 * @see #setSelected(boolean)
	 * @see #selectedProperty()
	 */

	public void toggleSelected()
	{
		selected.set(!selected.get());
	}

	//------------------------------------------------------------------

	/**
	 * Updates the view of this toggle switch.  A subclass that provides its own skin for this switch may override this
	 * method to redraw the skin in response to changes to the state of the switch.
	 */

	protected void update()
	{
		SceneUtils.runOnFxApplicationThread(view::update);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: VIEW OF TOGGLE SWITCH


	/**
	 * This class implements a node that provides the default skin for the enclosing toggle switch.
	 */

	private class View
		extends Group
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The radius of the rounded ends of the track of the toggle switch. */
		private static final	double	TRACK_END_RADIUS	= 6.5;

		/** The extent of the track (ie, the distance between the two positions of the slider). */
		private static final	double	TRACK_EXTENT	= 2.0 * TRACK_END_RADIUS + 2.0;

		/** The radius of the slider of the toggle switch. */
		private static final	double	SLIDER_RADIUS	= 8.5;

		/** The margin around the toggle switch to allow for the focus-indicator border. */
		private static final	double	MARGIN	= 2.0;

		/** The width of the toggle switch. */
		private static final	double	WIDTH	= 2.0 * MARGIN + 2.0 * SLIDER_RADIUS + TRACK_EXTENT;

		/** The height of the toggle switch. */
		private static final	double	HEIGHT	= 2.0 * MARGIN + 2.0 * SLIDER_RADIUS;

		/** The opacity of the view of a disabled toggle switch. */
		private static final	double	DISABLED_OPACITY	= 0.4;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a view for the enclosing toggle switch.
		 */

		private View()
		{
			// Set properties
			setMouseTransparent(true);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Updates this view.
		 */

		private void update()
		{
			// Get toggle switch
			ToggleSwitch toggleSwitch = ToggleSwitch.this;

			// Get colours
			boolean disabled = toggleSwitch.isDisabled();
			boolean selected = isSelected();
			Color trackColour = disabled
									? getColour(ColourKey.TRACK_DISABLED)
									: selected
											? trackColourSelected
											: toggleSwitch.trackColour;
			Color sliderColour = disabled
									? getColour(ColourKey.SLIDER_DISABLED)
									: selected
											? sliderColourSelected
											: toggleSwitch.sliderColour;

			// Remove all children
			getChildren().clear();

			// If toggle switch has focus, create background and focus-indicator border ...
			if (toggleSwitch.isFocused())
				getChildren().addAll(ShapeUtils.createFocusBorder(WIDTH, HEIGHT));

			// ... otherwise, create background
			else
				getChildren().add(new Rectangle(WIDTH, HEIGHT, Color.TRANSPARENT));

			// Create track
			double x = MARGIN + SLIDER_RADIUS;
			double y = MARGIN + SLIDER_RADIUS - TRACK_END_RADIUS;
			Rectangle track = new Rectangle(x, y, TRACK_EXTENT, 2.0 * TRACK_END_RADIUS);
			track.setFill(trackColour);
			track.getStyleClass().add(StyleClass.TRACK);
			getChildren().add(track);

			// Create rounded end of track
			x = MARGIN + (selected ? SLIDER_RADIUS : SLIDER_RADIUS + TRACK_EXTENT);
			Circle trackEnd = new Circle(x, MARGIN + SLIDER_RADIUS, TRACK_END_RADIUS);
			trackEnd.setFill(trackColour);
			trackEnd.getStyleClass().add(StyleClass.TRACK);
			getChildren().add(trackEnd);

			// Create slider
			x = MARGIN + (selected ? SLIDER_RADIUS + TRACK_EXTENT : SLIDER_RADIUS);
			y = MARGIN + SLIDER_RADIUS;
			Circle slider = new Circle(x, y, SLIDER_RADIUS);
			slider.setFill(sliderColour);
			slider.getStyleClass().add(StyleClass.SLIDER);
			getChildren().add(slider);

			// If toggle switch is disabled, reduce opacity of this view
			setOpacity(disabled ? DISABLED_OPACITY : 1.0);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
