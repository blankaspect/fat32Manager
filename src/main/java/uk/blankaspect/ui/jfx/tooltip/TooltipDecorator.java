/*====================================================================*\

TooltipDecorator.java

Class: tooltip decorator.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.tooltip;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import javafx.geometry.Insets;

import javafx.scene.Node;

import javafx.scene.control.Label;

import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IFunction0;

import uk.blankaspect.ui.jfx.popup.LabelPopUpManager;
import uk.blankaspect.ui.jfx.popup.PopUpUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: TOOLTIP DECORATOR


/**
 * This class provides methods for adding a tooltip to a specified JavaFX {@link Node}.
 */

public class TooltipDecorator
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default padding around a tooltip. */
	private static final	Insets	DEFAULT_PADDING	= new Insets(1.0, 6.0, 1.0, 6.0);

	/** The default delay (in milliseconds) before a tooltip pop-up is displayed. */
	private static final	int		DEFAULT_DELAY	= 500;

	/** The special value that indicates that the <i>x</i> coordinate of a tooltip pop-up should be adjusted to keep
		the pop-up within the horizontal extent of the screens. */
	private static final	double	X_OFFSET	= Double.NaN;

	/** The value that is added to the <i>y</i> coordinate of the triggering mouse event when determining the location
		of the tooltip. */
	private static final	double	Y_OFFSET	= 20.0;

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.TOOLTIP_TEXT,
			CssSelector.builder()
					.cls(StyleClass.TOOLTIP_DECORATOR_TOOLTIP)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.TOOLTIP_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.TOOLTIP_DECORATOR_TOOLTIP)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.TOOLTIP_BORDER,
			CssSelector.builder()
					.cls(StyleClass.TOOLTIP_DECORATOR_TOOLTIP)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	TOOLTIP_DECORATOR_TOOLTIP	= StyleConstants.CLASS_PREFIX + "tooltip-decorator-tooltip";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	TOOLTIP_BACKGROUND	= PREFIX + "tooltip.background";
		String	TOOLTIP_BORDER		= PREFIX + "tooltip.border";
		String	TOOLTIP_TEXT		= PREFIX + "tooltip.text";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(TooltipDecorator.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private TooltipDecorator()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets event handlers on the specified node to display a tooltip when the mouse enters the node.  The tooltip is a
	 * label containing text in a pop-up window, which is displayed below the location of the event that triggered the
	 * tooltip.
	 *
	 * @param  target
	 *           the node for which a tooltip will be displayed.
	 * @param  text
	 *           the text of the label of the tooltip, which may be {@code null}.
	 * @return the pop-up manager that manages the tooltip.
	 */

	public static LabelPopUpManager addTooltip(
		Node	target,
		String	text)
	{
		return addTooltip(target, () -> text);
	}

	//------------------------------------------------------------------

	/**
	 * Sets event handlers on the specified node to display a tooltip when the mouse enters the node.  The tooltip is a
	 * label containing text in a pop-up window, which is displayed below the location of the event that triggered the
	 * tooltip.
	 *
	 * @param  target
	 *           the node for which a tooltip will be displayed.
	 * @param  textSource
	 *           the source of the text of the label of the tooltip, which may be {@code null}.
	 * @return the pop-up manager that manages the tooltip.
	 */

	public static LabelPopUpManager addTooltip(
		Node				target,
		IFunction0<String>	textSource)
	{
		return addTooltip(target, getColour(ColourKey.TOOLTIP_TEXT), getColour(ColourKey.TOOLTIP_BACKGROUND),
						  getColour(ColourKey.TOOLTIP_BORDER), DEFAULT_PADDING, DEFAULT_DELAY, textSource);
	}

	//------------------------------------------------------------------

	/**
	 * Sets event handlers on the specified node to display a tooltip when the mouse enters the node.  The tooltip is a
	 * label containing text in a pop-up window, which is displayed below the location of the event that triggered the
	 * tooltip.  The <i>x</i> coordinate of the pop-up will be adjusted to keep the pop-up within the horizontal extent
	 * of the screens.
	 *
	 * @param  target
	 *           the node for which a tooltip will be displayed.
	 * @param  textColour
	 *           the text colour of the tooltip.
	 * @param  backgroundColour
	 *           the background colour of the tooltip.
	 * @param  borderColour
	 *           the border colour of the tooltip, or {@code null} for no border.
	 * @param  padding
	 *           the padding around the tooltip.
	 * @param  delay
	 *           the delay (in milliseconds) before the tooltip is displayed.
	 * @param  textSource
	 *           the source of the text of the label of the tooltip, which may be {@code null}.
	 * @return the pop-up manager that manages the tooltip.
	 */

	public static LabelPopUpManager addTooltip(
		Node				target,
		Color				textColour,
		Color				backgroundColour,
		Color				borderColour,
		Insets				padding,
		int					delay,
		IFunction0<String>	textSource)
	{
		// Create pop-up manager
		LabelPopUpManager popUpManager = new LabelPopUpManager((text, graphic) ->
		{
			Label label = new Label(text, graphic);
			label.setPadding(padding);
			label.setTextFill(textColour);
			label.setBackground(SceneUtils.createColouredBackground(backgroundColour));
			if (borderColour != null)
				label.setBorder(SceneUtils.createSolidBorder(borderColour));
			label.getStyleClass().add(StyleClass.TOOLTIP_DECORATOR_TOOLTIP);
			return label;
		});
		popUpManager.setDelay(delay);
		popUpManager.setPopUpDecorator(popUp -> popUp.setAutoFix(false));

		// Create pop-up
		PopUpUtils.createPopUp(popUpManager, target, null, null, X_OFFSET, Y_OFFSET, textSource, null);

		// Return pop-up manager
		return popUpManager;
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

}

//----------------------------------------------------------------------
