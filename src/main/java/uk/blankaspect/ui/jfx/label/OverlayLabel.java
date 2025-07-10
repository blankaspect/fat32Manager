/*====================================================================*\

OverlayLabel.java

Class: overlay label.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.label;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import javafx.event.EventHandler;

import javafx.geometry.Insets;

import javafx.scene.Node;

import javafx.scene.control.Label;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Border;

import javafx.scene.paint.Color;

import javafx.stage.Popup;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IFunction0;

import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.ui.jfx.colour.ColourUtils;

import uk.blankaspect.ui.jfx.popup.LabelPopUpManager;
import uk.blankaspect.ui.jfx.popup.PopUpUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: OVERLAY LABEL


/**
 * This class implements a {@linkplain Label label} that is overlaid with a pop-up window when a given mouse event
 * occurs.  The pop-up contains another label with either the full (ie, not truncated) text of the underlying label or,
 * optionally, some other specified text.  A flag controls whether the pop-up will always be displayed or whether it
 * will be displayed only if the text of the primary label is truncated.
 */

public class OverlayLabel
	extends Label
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default padding around the label of the pop-up. */
	private static final	Insets	DEFAULT_POP_UP_PADDING	= new Insets(2.0, 6.0, 2.0, 6.0);

	/** The default delay (in milliseconds) before the pop-up is displayed. */
	private static final	int		DEFAULT_POP_UP_DELAY	= 250;

	/**
	 * The mouse event that will cause the pop-up to be displayed.
	 */

	public enum Trigger
	{
		/**
		 * The mouse is hovered over the label.
		 */
		HOVERED,

		/**
		 * The primary mouse button is pressed on the label.
		 */
		PRIMARY_PRESSED,

		/**
		 * The secondary mouse button is pressed on the label.
		 */
		SECONDARY_PRESSED
	}

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.POPUP_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.OVERLAY_LABEL_POPUP)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.POPUP_BORDER,
			CssSelector.builder()
					.cls(StyleClass.OVERLAY_LABEL_POPUP)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	OVERLAY_LABEL_POPUP	= StyleConstants.CLASS_PREFIX + "overlay-label-popup";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	POPUP_BACKGROUND	= PREFIX + "popup.background";
		String	POPUP_BORDER		= PREFIX + "popup.border";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The function that provides the graphic of this label. */
	private	IFunction0<? extends Node>	graphicSource;

	/** The text that will be displayed in the pop-up.  If {@code null}, the text of this label is displayed. */
	private	String						popUpText;

	/** The padding around the label of the pop-up. */
	private	Insets						popUpPadding;

	/** The background colour of the label of the pop-up.  If {@code null}, the background colour of this label is
		used. */
	private	Color						popUpBackgroundColour;

	/** The border colour of the label of the pop-up. */
	private	Color						popUpBorderColour;

	/** The CSS style class of the label of the pop-up. */
	private	String						popUpStyleClass;

	/** The delay (in milliseconds) before the pop-up is displayed. */
	private	int							popUpDelay;

	/** Flag: if {@code true}, the preferred height of the label of the pop-up will be set to the height of this
		label. */
	private	boolean						popUpMatchHeight;

	/** Flag: if {@code true}, the pop-up will always be displayed; otherwise, the pop-up will be displayed only if the
		text of this label is truncated. */
	private	boolean						alwaysShowPopUp;

	/** Flag: if {@code true}, the event that caused the pop-up to be displayed will be consumed. */
	private	boolean						consumeEvent;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(OverlayLabel.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new label with the specified trigger and with no text or graphic.
	 *
	 * @param trigger
	 *          the kind of mouse event that will cause the pop-up to be displayed.
	 */

	public OverlayLabel(
		Trigger	trigger)
	{
		// Call alternative constructor
		this(trigger, null, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new label with the specified text.
	 *
	 * @param trigger
	 *          the kind of mouse event that will cause the pop-up to be displayed.
	 * @param text
	 *          the text of the label, which may be {@code null}.
	 */

	public OverlayLabel(
		Trigger	trigger,
		String	text)
	{
		// Call alternative constructor
		this(trigger, text, null);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new label with the specified text and graphic.
	 *
	 * @param trigger
	 *          the kind of mouse event that will cause the pop-up to be displayed.
	 * @param text
	 *          the text of the label, which may be {@code null}.
	 * @param graphicSource
	 *          the function that will provide the graphic of the label, which may be {@code null}.
	 */

	public OverlayLabel(
		Trigger						trigger,
		String						text,
		IFunction0<? extends Node>	graphicSource)
	{
		// Call superclass constructor
		super(text, (graphicSource == null) ? null : graphicSource.invoke());

		// Initialise instance variables
		this.graphicSource = graphicSource;
		popUpPadding = DEFAULT_POP_UP_PADDING;
		popUpBorderColour = getColour(ColourKey.POPUP_BORDER);
		popUpDelay = DEFAULT_POP_UP_DELAY;
		consumeEvent = true;

		// Create function to return pop-up padding
		IFunction0<Insets> getPopUpPadding = () -> (popUpPadding == null) ? getPadding() : popUpPadding;

		// Create pop-up manager
		LabelPopUpManager popUpManager = new LabelPopUpManager((text0, graphic) ->
		{
			// Create label
			Label label = new Label(text0, graphic);

			// Set properties of label
			label.setGraphicTextGap(getGraphicTextGap());
			label.setTextFill(getTextFill());
			label.setFont(getFont());
			label.setPadding(getPopUpPadding.invoke());
			label.setBorder((popUpBorderColour == null) ? Border.EMPTY
														: SceneUtils.createSolidBorder(popUpBorderColour));
			label.getStyleClass().add((popUpStyleClass == null) ? StyleClass.OVERLAY_LABEL_POPUP : popUpStyleClass);
			if (popUpMatchHeight)
				label.setPrefHeight(getHeight());

			// Set background colour of label
			Color backgroundColour = popUpBackgroundColour;
			if (backgroundColour == null)
			{
				backgroundColour = SceneUtils.getBackgroundColour(this);
				backgroundColour = (backgroundColour == null) ? getColour(ColourKey.POPUP_BACKGROUND)
															  : ColourUtils.opaque(backgroundColour);
			}
			label.setBackground(SceneUtils.createColouredBackground(backgroundColour));

			// Return label
			return label;
		});

		// Create event handler to show pop-up
		EventHandler<MouseEvent> showPopUp = event ->
		{
			// Determine whether event is trigger for pop-up
			boolean triggered = false;
			switch (trigger)
			{
				case HOVERED:
					triggered = true;
					break;

				case PRIMARY_PRESSED:
					triggered = (event.getButton() == MouseButton.PRIMARY);
					break;

				case SECONDARY_PRESSED:
					triggered = (event.getButton() == MouseButton.SECONDARY);
					break;
			}

			// Create and display pop-up
			if (triggered && (alwaysShowPopUp || (computePrefWidth(0.0) > getWidth())))
			{
				// Get insets
				Insets insets = getInsets();

				// Get pop-up padding
				Insets popUpPadding = getPopUpPadding.invoke();

				// Get horizontal alignment and x adjustment
				VHPos pos = VHPos.CENTRE_LEFT;
				double x = 0.0;
				switch (getAlignment().getHpos())
				{
					case LEFT:
						x = insets.getLeft() - popUpPadding.getLeft();
						if (popUpBorderColour != null)
							x -= 1.0;
						break;

					case CENTER:
						x = insets.getLeft() - popUpPadding.getLeft();
						break;

					case RIGHT:
						x = popUpPadding.getRight() - insets.getRight();
						if (popUpBorderColour != null)
							x += 1.0;
						pos = VHPos.CENTRE_RIGHT;
						break;
				}

				// Create and display pop-up
				popUpManager.setDelay(popUpDelay);
				popUpManager.showPopUp(this, (popUpText == null) ? getText() : popUpText,
									   (this.graphicSource == null) ? null : this.graphicSource.invoke(), event,
									   PopUpUtils.createLocator(this, pos, pos, x, 0.0));

				// Consume event
				if (consumeEvent)
					event.consume();
			}
		};

		// Add event handlers to show and hide pop-up
		switch (trigger)
		{
			case HOVERED:
				addEventHandler(MouseEvent.MOUSE_ENTERED, showPopUp);
				addEventHandler(MouseEvent.MOUSE_MOVED,   showPopUp);
				addEventHandler(MouseEvent.MOUSE_EXITED,  popUpManager::hidePopUp);
				break;

			case PRIMARY_PRESSED:
			case SECONDARY_PRESSED:
				addEventHandler(MouseEvent.MOUSE_PRESSED,  showPopUp);
				addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
				{
					// Get pop-up
					Popup popUp = popUpManager.getPopUp(this);

					// Determine whether pop-up is showing
					boolean popUpShowing = (popUp != null) && popUp.isShowing();

					// Hide pop-up
					popUpManager.hidePopUp(event);

					// If pop-up was showing, consume event
					if (popUpShowing)
						event.consume();
				});
				break;
		}
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
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the function that will provide the graphic of this label to the specified value.
	 *
	 * @param source
	 *          the function that will provide the graphic of this label, which may be {@code null}.
	 */

	public void setGraphicSource(
		IFunction0<? extends Node>	source)
	{
		graphicSource = source;
		setGraphic((source == null) ? null : source.invoke());
	}

	//------------------------------------------------------------------

	/**
	 * Updates the graphic of this label from the graphic source.
	 */

	public void updateGraphic()
	{
		if (graphicSource != null)
			setGraphic(graphicSource.invoke());
	}

	//------------------------------------------------------------------

	/**
	 * Sets the text of the label of the pop-up to the specified value.  If it is {@code null}, the text of this label
	 * will be displayed in the pop-up; otherwise, the <i>always show pop-up</i> flag will be set.
	 *
	 * @param text
	 *          the value to which the the text of the label of the pop-up will be set.
	 */

	public void setPopUpText(
		String	text)
	{
		popUpText = text;
		if (text != null)
			alwaysShowPopUp = true;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the padding around the label of the pop-up to the specified value.
	 *
	 * @param padding
	 *          the value to which the padding around the label of the pop-up will be set.
	 */

	public void setPopUpPadding(
		Insets	padding)
	{
		popUpPadding = padding;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background colour of the label of the pop-up to the specified value.  If it is {@code null}, the
	 * background colour will be obtained from this label or its ancestors.
	 *
	 * @param colour
	 *          the value to which the background colour of the label of the pop-up will be set.
	 */

	public void setPopUpBackgroundColour(
		Color	colour)
	{
		popUpBackgroundColour = colour;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the border colour of the label of the pop-up to the specified value.
	 *
	 * @param colour
	 *          the value to which the border colour of the label of the pop-up will be set.
	 */

	public void setPopUpBorderColour(
		Color	colour)
	{
		popUpBorderColour = colour;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the CSS style class of the label of the pop-up to the specified value.
	 *
	 * @param styleClass
	 *          the value to which the CSS style class of the label of the pop-up will be set.
	 */

	public void setPopUpStyleClass(
		String	styleClass)
	{
		popUpStyleClass = styleClass;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the delay before the pop-up is displayed to the specified value.
	 *
	 * @param delay
	 *          the delay (in milliseconds) before the pop-up is displayed.
	 */

	public void setPopUpDelay(
		int	delay)
	{
		popUpDelay = delay;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>pop-up match height</i> flag to the specified value.  If the flag is {@code true}, the preferred
	 * height of the label of the pop-up will be set to the height of this label.  The flag is {@code false} by default.
	 *
	 * @param matchHeight
	 *          if {@code true}, the preferred height of the label of the pop-up will be set to the height of this
	 *          label.
	 */

	public void setPopUpMatchHeight(
		boolean	matchHeight)
	{
		popUpMatchHeight = matchHeight;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>always show pop-up</i> flag to the specified value.  If the flag is {@code true}, the pop-up will
	 * always be displayed when the mouse is hovered over this label; otherwise, the pop-up will be displayed only if
	 * the text of this label is truncated.  The flag is {@code false} by default.
	 *
	 * @param alwaysShow
	 *          if {@code true}, the pop-up will always be displayed when the mouse is hovered over this label;
	 *          otherwise, the pop-up will be displayed only if the text of this label is truncated.
	 */

	public void setAlwaysShowPopUp(
		boolean	alwaysShow)
	{
		alwaysShowPopUp = alwaysShow;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>consume event</i> flag to the specified value.  If the flag is {@code true}, the event that caused
	 * the pop-up to be displayed will be consumed; otherwise, the event will be propagated to the underlying label.
	 * The flag is {@code true} by default.
	 *
	 * @param consume
	 *          if {@code true}, the event that caused the pop-up to be displayed will be consumed; otherwise, the event
	 *          will be propagated to the underlying label.
	 */

	public void setConsumeEvent(
		boolean	consume)
	{
		consumeEvent = consume;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: HOVER LABEL


	/**
	 * This class implements a {@linkplain Label label} that is overlaid with a pop-up window when the mouse is hovered
	 * over the label.  The pop-up contains another label with either the full (ie, not truncated) text of the
	 * underlying label or, optionally, some other specified text.  A flag controls whether the pop-up will always be
	 * displayed or whether it will be displayed only if the text of the principal label is truncated.
	 */

	public static class Hover
		extends OverlayLabel
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new label with no text or graphic.
		 */

		public Hover()
		{
			// Call alternative constructor
			this(null, null);
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new label with the specified text.
		 *
		 * @param text
		 *          the text of the label, which may be {@code null}.
		 */

		public Hover(
			String	text)
		{
			// Call alternative constructor
			this(text, null);
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new label with the specified text and graphic.
		 *
		 * @param text
		 *          the text of the label, which may be {@code null}.
		 * @param graphicSource
		 *          the function that will provide the graphic of the label, which may be {@code null}.
		 */

		public Hover(
			String						text,
			IFunction0<? extends Node>	graphicSource)
		{
			// Call superclass constructor
			super(Trigger.HOVERED, text, graphicSource);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: PRIMARY-BUTTON PRESS LABEL


	/**
	 * This class implements a {@linkplain Label label} that is overlaid with a pop-up window when the primary mouse
	 * button is pressed on the label.  The pop-up contains another label with either the full (ie, not truncated) text
	 * of the underlying label or, optionally, some other specified text.  A flag controls whether the pop-up will
	 * always be displayed or whether it will be displayed only if the text of the principal label is truncated.
	 */

	public static class Primary
		extends OverlayLabel
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new label with no text or graphic.
		 */

		public Primary()
		{
			// Call alternative constructor
			this(null, null);
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new label with the specified text.
		 *
		 * @param text
		 *          the text of the label, which may be {@code null}.
		 */

		public Primary(
			String	text)
		{
			// Call alternative constructor
			this(text, null);
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new label with the specified text and graphic.
		 *
		 * @param text
		 *          the text of the label, which may be {@code null}.
		 * @param graphicSource
		 *          the function that will provide the graphic of the label, which may be {@code null}.
		 */

		public Primary(
			String						text,
			IFunction0<? extends Node>	graphicSource)
		{
			// Call superclass constructor
			super(Trigger.PRIMARY_PRESSED, text, graphicSource);
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: SECONDARY-BUTTON PRESS LABEL


	/**
	 * This class implements a {@linkplain Label label} that is overlaid with a pop-up window when the secondary mouse
	 * button is pressed on the label.  The pop-up contains another label with either the full (ie, not truncated) text
	 * of the underlying label or, optionally, some other specified text.  A flag controls whether the pop-up will
	 * always be displayed or whether it will be displayed only if the text of the principal label is truncated.
	 */

	public static class Secondary
		extends OverlayLabel
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new label with no text or graphic.
		 */

		public Secondary()
		{
			// Call alternative constructor
			this(null, null);
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new label with the specified text.
		 *
		 * @param text
		 *          the text of the label, which may be {@code null}.
		 */

		public Secondary(
			String	text)
		{
			// Call alternative constructor
			this(text, null);
		}

		//--------------------------------------------------------------

		/**
		 * Creates a new label with the specified text and graphic.
		 *
		 * @param text
		 *          the text of the label, which may be {@code null}.
		 * @param graphicSource
		 *          the function that will provide the graphic of the label, which may be {@code null}.
		 */

		public Secondary(
			String						text,
			IFunction0<? extends Node>	graphicSource)
		{
			// Call superclass constructor
			super(Trigger.SECONDARY_PRESSED, text, graphicSource);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
