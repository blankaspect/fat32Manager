/*====================================================================*\

ActionLabelPopUp.java

Class: pop-up window containing a label that can perform an action.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import javafx.geometry.Insets;

import javafx.scene.Node;

import javafx.scene.control.Label;

import javafx.scene.input.MouseButton;

import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;

import javafx.stage.Popup;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: POP-UP WINDOW CONTAINING A LABEL THAT CAN PERFORM AN ACTION


/**
 * The class implements a pop-up window that contains a label with the specified text and graphic.  The specified action
 * is performed when the primary mouse button is clicked on the label.
 */

public class ActionLabelPopUp
	extends Popup
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The vertical padding of the label. */
	private static final	double	V_PADDING	= 4.0;

	/** The horizontal padding of the label. */
	private static final	double	H_PADDING	= 8.0;

	/** The left padding of a label that has a graphic. */
	private static final	double	LEFT_PADDING_GRAPHIC	= 4.0;

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.TEXT,
			CssSelector.builder()
					.cls(StyleClass.ACTION_LABEL_POPUP)
					.desc(FxStyleClass.LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.ACTION_LABEL_POPUP)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.BORDER,
			CssSelector.builder()
					.cls(StyleClass.ACTION_LABEL_POPUP)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	ACTION_LABEL_POPUP	= StyleConstants.CLASS_PREFIX + "action-label-popup";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	BACKGROUND	= PREFIX + "background";
		String	BORDER		= PREFIX + "border";
		String	TEXT		= PREFIX + "text";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(ActionLabelPopUp.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a pop-up window that contains a label with the specified text and graphic.  The
	 * specified action is performed when the primary mouse button is clicked on the label.  The pop-up is hidden when
	 * the mouse button is clicked, immediately before the action is performed.
	 *
	 * @param text
	 *          the text of the label, which may be {@code null}.
	 * @param graphic
	 *          the graphic of the label, which may be {@code null}.
	 * @param action
	 *          the action that will be performed, which may be {@code null}.
	 */

	public ActionLabelPopUp(
		String		text,
		Node		graphic,
		Runnable	action)
	{
		// Set properties
		setAutoHide(true);

		// Create label
		Label label = new Label(text, graphic);
		label.setPadding(new Insets(V_PADDING, H_PADDING, V_PADDING,
									(graphic == null) ? H_PADDING : LEFT_PADDING_GRAPHIC));
		label.setTextFill(getColour(ColourKey.TEXT));
		if (action != null)
		{
			label.setOnMouseClicked(event ->
			{
				if (event.getButton() == MouseButton.PRIMARY)
				{
					// Hide pop-up
					hide();

					// Perform action
					action.run();
				}
			});
		}

		// Create content pane
		StackPane pane = new StackPane(label);
		pane.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.BACKGROUND)));
		pane.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.BORDER)));
		pane.getStyleClass().add(StyleClass.ACTION_LABEL_POPUP);

		// Set content of pop-up
		getContent().add(pane);
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

}

//----------------------------------------------------------------------
