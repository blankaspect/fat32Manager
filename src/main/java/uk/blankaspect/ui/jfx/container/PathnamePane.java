/*====================================================================*\

PathnamePane.java

Class: pathname pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.container;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Group;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.button.Buttons;
import uk.blankaspect.ui.jfx.button.GraphicButton;

import uk.blankaspect.ui.jfx.icon.Icons;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: PATHNAME PANE


/**
 * This class implements a pane that contains:
 * <ul>
 *   <li>a text field for entering a pathname,</li>
 *   <li>a button containing an ellipsis, which may be used, for example, to invoke a location chooser, and</li>
 *   <li>an optional button for clearing the text field.</li>
 * </ul>
 */

public class PathnamePane
	extends HBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The gap between the text field and the button. */
	private static final	double	GAP	= 8.0;

	/** The padding around the ellipsis button. */
	private static final	Insets	ELLIPSIS_BUTTON_PADDING	= new Insets(2.0, 8.0, 2.0, 8.0);

	/** Miscellaneous strings. */
	private static final	String	ELLIPSIS_STR	= "...";
	private static final	String	CLEAR_FIELD_STR	= "Clear field";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.CLEAR_BUTTON_DISC,
			CssSelector.builder()
					.cls(StyleClass.PATHNAME_PANE)
					.desc(StyleClass.CLEAR_BUTTON)
					.desc(Icons.StyleClass.CLEAR01_DISC)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.CLEAR_BUTTON_CROSS,
			CssSelector.builder()
					.cls(StyleClass.PATHNAME_PANE)
					.desc(StyleClass.CLEAR_BUTTON)
					.desc(Icons.StyleClass.CLEAR01_CROSS)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	CLEAR_BUTTON	= StyleConstants.CLASS_PREFIX + "clear-button";
		String	PATHNAME_PANE	= StyleConstants.CLASS_PREFIX + "pathname-pane";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	CLEAR_BUTTON_CROSS	= PREFIX + "clearButton.cross";
		String	CLEAR_BUTTON_DISC	= PREFIX + "clearButton.disc";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The ellipsis button. */
	private	Button	ellipsisButton;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(PathnamePane.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a pathname pane containing the specified text field.
	 *
	 * @param pathnameField
	 *          the text field for a pathname that will be added to this pane.
	 */

	public PathnamePane(
		TextField	pathnameField)
	{
		// Call alternative constructor
		this(pathnameField, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a pathname pane containing the specified text field and an optional <i>clear</i>
	 * button.
	 *
	 * @param pathnameField
	 *          the text field for a pathname that will be added to this pane.
	 * @param hasClearButton
	 *          if {@code true}, the pane will have a button to the right of the ellipsis button to clear the text
	 *          field.
	 */

	public PathnamePane(
		TextField	pathnameField,
		boolean		hasClearButton)
	{
		// Call superclass constructor
		super(GAP);

		// Set properties
		setAlignment(Pos.CENTER_LEFT);
		getStyleClass().add(StyleClass.PATHNAME_PANE);

		// Set properties of pathname field
		HBox.setHgrow(pathnameField, Priority.ALWAYS);

		// Create button: ellipsis
		ellipsisButton = Buttons.hNoShrink(ELLIPSIS_STR);
		ellipsisButton.setPadding(ELLIPSIS_BUTTON_PADDING);
		ellipsisButton.prefHeightProperty().bind(pathnameField.heightProperty());

		// Add children to this pane
		getChildren().addAll(pathnameField, ellipsisButton);

		// Create button: clear
		if (hasClearButton)
		{
			// Create button
			Group clearIcon = Icons.clear01(getColour(ColourKey.CLEAR_BUTTON_DISC),
											getColour(ColourKey.CLEAR_BUTTON_CROSS));
			GraphicButton clearButton = new GraphicButton(clearIcon, CLEAR_FIELD_STR);
			clearButton.setOnAction(event ->
			{
				Platform.runLater(() ->
				{
					pathnameField.clear();
					pathnameField.requestFocus();
				});
			});
			clearButton.getStyleClass().add(StyleClass.CLEAR_BUTTON);
			setMargin(clearButton, new Insets(0.0, 0.0, 0.0, -4.0));

			// Add button to this pane
			getChildren().add(clearButton);
		}
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a pathname pane containing the specified text field.
	 *
	 * @param pathnameField
	 *          the text field for a pathname that will be added to this pane.
	 * @param actionHandler
	 *          the action-event handler that will be set on the button.
	 */

	public PathnamePane(
		TextField					pathnameField,
		EventHandler<ActionEvent>	actionHandler)
	{
		// Call alternative constructor
		this(pathnameField, false);

		// Set action-event handler on ellipsis button
		ellipsisButton.addEventHandler(ActionEvent.ACTION, actionHandler);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a pathname pane containing the specified text field and an optional <i>clear</i>
	 * button.
	 *
	 * @param pathnameField
	 *          the text field for a pathname that will be added to this pane.
	 * @param hasClearButton
	 *          if {@code true}, the pane will have a button to the right of the ellipsis button to clear the text
	 *          field.
	 * @param actionHandler
	 *          the action-event handler that will be set on the button.
	 */

	public PathnamePane(
		TextField					pathnameField,
		boolean						hasClearButton,
		EventHandler<ActionEvent>	actionHandler)
	{
		// Call alternative constructor
		this(pathnameField, hasClearButton);

		// Set action-event handler on ellipsis button
		ellipsisButton.addEventHandler(ActionEvent.ACTION, actionHandler);
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
	 * Returns the ellipsis button of this pathname pane.
	 *
	 * @return the ellipsis button of this pathname pane.
	 */

	public Button getButton()
	{
		return ellipsisButton;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
