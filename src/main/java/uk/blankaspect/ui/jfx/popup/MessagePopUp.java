/*====================================================================*\

MessagePopUp.java

Class: pop-up window containing an icon and a message.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Label;

import javafx.scene.layout.HBox;

import javafx.scene.paint.Color;

import javafx.stage.Popup;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.image.MessageIcon24;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: POP-UP WINDOW CONTAINING AN ICON AND A MESSAGE


public class MessagePopUp
	extends Popup
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The padding around the content pane. */
	private static final	Insets	CONTENT_PANE_PADDING	= new Insets(2.0, 6.0, 2.0, 4.0);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.TEXT,
			CssSelector.builder()
					.cls(StyleClass.MESSAGE_POPUP)
					.desc(FxStyleClass.LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.MESSAGE_POPUP)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.BORDER,
			CssSelector.builder()
					.cls(StyleClass.MESSAGE_POPUP)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	MESSAGE_POPUP	= StyleConstants.CLASS_PREFIX + "message-popup";
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
		StyleManager.INSTANCE.register(MessagePopUp.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a pop-up window containing the specified icon and message.
	 *
	 * @param icon
	 *          the icon that will be displayed in the pop-up window.
	 * @param message
	 *          the message that will be displayed in the pop-up window.
	 */

	public MessagePopUp(
		MessageIcon24	icon,
		String			message)
	{
		// Set properties
		setAutoHide(true);

		// Create label for message
		Label messageLabel = new Label(message);
		messageLabel.setTextFill(getColour(ColourKey.TEXT));

		// Create content pane
		HBox contentPane = new HBox(6.0, icon.get(), messageLabel);
		contentPane.setAlignment(Pos.CENTER_LEFT);
		contentPane.setPadding(CONTENT_PANE_PADDING);
		contentPane.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.BACKGROUND)));
		contentPane.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.BORDER)));
		contentPane.getStyleClass().add(StyleClass.MESSAGE_POPUP);

		// Set content of pop-up
		getContent().add(contentPane);
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
