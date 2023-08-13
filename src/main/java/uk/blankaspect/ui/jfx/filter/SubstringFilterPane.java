/*====================================================================*\

SubstringFilterPane.java

Class: substring-filter pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.filter;

//----------------------------------------------------------------------


// IMPORTS


import java.io.ByteArrayInputStream;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.event.ActionEvent;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Group;

import javafx.scene.control.TextField;

import javafx.scene.image.Image;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.button.GraphicButton;
import uk.blankaspect.ui.jfx.button.ImageButton;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.Text2;

//----------------------------------------------------------------------


// CLASS: SUBSTRING-FILTER PANE


/**
 * This class implements a pane containing a text field for a filter, an optional button for clearing the field and an
 * optional button for selecting the mode of the filter.
 */

public class SubstringFilterPane
	extends HBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/**
	 * The modes in which the filter of a {@link SubstringFilterPane} can operate.
	 */
	public enum FilterMode
	{
		FRAGMENTED,
		WILDCARD_ANYWHERE,
		WILDCARD_START,
		WILDCARD_ALL
	}

	/** The gap between adjacent children of a filter pane. */
	private static final	double	GAP	= 2.0;

	/** The image of the <i>clear field</i> button. */
	private static final	Image	CLEAR_FIELD_IMAGE	= new Image(new ByteArrayInputStream(ImageData.CLEAR));

	/** A map of the templates for filter modes. */
	private static final	Map<FilterMode, String>	FILTER_MODE_TEMPLATES;

	/** The extra gap for a <i>filter mode</i> button. */
	private static final	double	FILTER_MODE_BUTTON_EXTRA_GAP	= 2.0;

	/** Miscellaneous strings. */
	private static final	String	CLEAR_FIELD_STR	= "Clear field";
	private static final	String	FILTER_MODE_STR	= "Filter mode";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.FILTER_MODE_BUTTON_BACKGROUND,
			CssSelector.builder()
						.cls(StyleClass.SUBSTRING_FILTER_PANE)
						.desc(StyleClass.FILTER_MODE_BUTTON).pseudo(GraphicButton.PseudoClassKey.INACTIVE)
						.desc(GraphicButton.StyleClass.INNER_VIEW)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.FILTER_MODE_BUTTON_BORDER,
			CssSelector.builder()
						.cls(StyleClass.SUBSTRING_FILTER_PANE)
						.desc(StyleClass.FILTER_MODE_BUTTON).pseudo(GraphicButton.PseudoClassKey.INACTIVE)
						.desc(GraphicButton.StyleClass.INNER_VIEW)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.FILTER_MODE_BUTTON_TEXT,
			CssSelector.builder()
						.cls(StyleClass.FILTER_MODE_BUTTON_TEXT)
						.build()
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	SUBSTRING_FILTER_PANE	= StyleConstants.CLASS_PREFIX + "substring-filter-pane";

		String	FILTER_MODE_BUTTON		= StyleConstants.CLASS_PREFIX + "filter-mode-button";
		String	FILTER_MODE_BUTTON_TEXT	= SUBSTRING_FILTER_PANE + "-filter-mode-button-text";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	FILTER_MODE_BUTTON_BACKGROUND	= "substringFilterPane.filterModeButton.background";
		String	FILTER_MODE_BUTTON_BORDER		= "substringFilterPane.filterModeButton.border";
		String	FILTER_MODE_BUTTON_TEXT			= "substringFilterPane.filterModeButton.text";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The <i>filter mode</i> property of this pane. */
	private	SimpleObjectProperty<FilterMode>	filterMode;

	/** The text field of this pane. */
	private	TextField							textField;

	/** The <i>filter mode</i> button of this pane. */
	private	GraphicButton						filterModeButton;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Initialise filter-mode templates
		FILTER_MODE_TEMPLATES = new EnumMap<>(FilterMode.class);
		FILTER_MODE_TEMPLATES.put(FilterMode.FRAGMENTED,        "-a-b-");
		FILTER_MODE_TEMPLATES.put(FilterMode.WILDCARD_ANYWHERE, "\u2013a?*\u2013");
		FILTER_MODE_TEMPLATES.put(FilterMode.WILDCARD_START,    "a?*\u2014");
		FILTER_MODE_TEMPLATES.put(FilterMode.WILDCARD_ALL,      "a?*");

		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(SubstringFilterPane.class, COLOUR_PROPERTIES,
									   GraphicButton.class);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a filter pane containing a new instance of a text field and with the specified filter
	 * mode.
	 *
	 * @param filterMode
	 *          the initial filter mode of the <i>filter mode</i> button.
	 */

	public SubstringFilterPane(
		FilterMode	filterMode)
	{
		// Call alternative constructor
		this(null, filterMode, false, false);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a filter pane containing a new instance of a text field, an optional button for
	 * clearing the text field and an optional <i>filter mode</i> button.
	 *
	 * @param initialFilterMode
	 *          the initial filter mode of the pane, which can be changed only if the pane has a filter-mode button.
	 * @param hasClearButton
	 *          if {@code true}, the pane will have a button for clearing the text field.
	 * @param hasFilterModeButton
	 *          if {@code true}, the pane will have a button for changing the filter mode.
	 */

	public SubstringFilterPane(
		FilterMode	initialFilterMode,
		boolean		hasClearButton,
		boolean		hasFilterModeButton)
	{
		// Call alternative constructor
		this(null, initialFilterMode, hasClearButton, hasFilterModeButton);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a filter pane containing the specified text field, an optional button for clearing the
	 * text field and an optional <i>filter mode</i> button.
	 *
	 * @param filterField
	 *          the text field for a filter that will appear in this pane.  If it is {@code null}, a new instance of a
	 *          text field will be created.
	 * @param initialFilterMode
	 *          the initial filter mode of the pane, which can be changed only if the pane has a filter-mode button.
	 * @param hasClearButton
	 *          if {@code true}, the pane will have a button for clearing the text field.
	 * @param hasFilterModeButton
	 *          if {@code true}, the pane will have a button for changing the filter mode.
	 */

	public SubstringFilterPane(
		TextField	filterField,
		FilterMode	initialFilterMode,
		boolean		hasClearButton,
		boolean		hasFilterModeButton)
	{
		// Call superclass constructor
		super(GAP);

		// Validate arguments
		if (initialFilterMode == null)
			throw new IllegalArgumentException("Null initial filter mode");

		// Initialise instance variables
		filterMode = new SimpleObjectProperty<>();

		// Set properties
		setAlignment(Pos.CENTER_LEFT);
		getStyleClass().add(StyleClass.SUBSTRING_FILTER_PANE);

		// Create text field, if necessary
		textField = (filterField == null) ? new TextField() : filterField;
		HBox.setHgrow(textField, Priority.ALWAYS);

		// Add text field to this pane
		getChildren().add(textField);

		// Create 'clear field' button
		if (hasClearButton)
		{
			// Create button
			ImageButton clearFieldButton = new ImageButton(CLEAR_FIELD_IMAGE, CLEAR_FIELD_STR);
			clearFieldButton.setOnAction(event ->
			{
				textField.clear();
				Platform.runLater(() -> textField.requestFocus());
			});

			// Add button to this pane
			getChildren().add(clearFieldButton);
		}

		// Create 'filter mode' button
		if (hasFilterModeButton)
		{
			// Create components of graphic for each filter mode
			EnumMap<FilterMode, Text2> textNodes = new EnumMap<>(FilterMode.class);
			double maxWidth = 0.0;
			for (FilterMode filterMode : FILTER_MODE_TEMPLATES.keySet())
			{
				// Create text node
				Text2 textNode = Text2.createCentred(FILTER_MODE_TEMPLATES.get(filterMode));
				textNode.setFill(getColour(ColourKey.FILTER_MODE_BUTTON_TEXT));
				textNode.getStyleClass().add(StyleClass.FILTER_MODE_BUTTON_TEXT);
				textNodes.put(filterMode, textNode);

				// Update maximum width
				double width = textNode.getWidth();
				if (maxWidth < width)
					maxWidth = width;
			}

			// Centre graphic of each filter mode horizontally
			double buttonWidth = 2.0 * (Math.ceil(0.5 * maxWidth) + 1.0);
			for (FilterMode filterMode : textNodes.keySet())
			{
				Text2 textNode = textNodes.get(filterMode);
				textNode.relocate(Math.floor(0.5 * (buttonWidth - textNode.getWidth())), 0.0);
			}

			// Create graphic for button
			Group graphic = new Group(new Rectangle(buttonWidth, Text2.textHeight(), Color.TRANSPARENT),
									  textNodes.get(initialFilterMode));

			// Create button
			filterModeButton = new GraphicButton(graphic, FILTER_MODE_STR);
			filterModeButton.setPadding(Insets.EMPTY);
			filterModeButton.setBackgroundColour(getColour(ColourKey.FILTER_MODE_BUTTON_BACKGROUND));
			filterModeButton.setBorderColour(getColour(ColourKey.FILTER_MODE_BUTTON_BORDER));
			filterModeButton.getStyleClass().add(StyleClass.FILTER_MODE_BUTTON);
			if (!hasClearButton)
				setMargin(filterModeButton, new Insets(0.0, 0.0, 0.0, FILTER_MODE_BUTTON_EXTRA_GAP));

			// Update button when filter mode changes
			filterMode.addListener((observable, oldMode, mode) -> graphic.getChildren().set(1, textNodes.get(mode)));

			// Cycle through filter modes when button is pressed
			filterModeButton.addEventHandler(ActionEvent.ACTION, event -> nextFilterMode());

			// Add button to this pane
			getChildren().add(filterModeButton);
		}

		// Initialise filter mode
		filterMode.set(initialFilterMode);
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
		Color colour = StyleManager.INSTANCE.getColour(key);
		return (colour == null) ? StyleManager.DEFAULT_COLOUR : colour;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the text field of this pane.
	 *
	 * @return the text field of this pane.
	 */

	public TextField getTextField()
	{
		return textField;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the <i>filter mode</i> button of this pane.
	 *
	 * @return the <i>filter mode</i> button of this pane.
	 */

	public GraphicButton getFilterModeButton()
	{
		return filterModeButton;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the filter mode of this pane.
	 *
	 * @return the filter mode of this pane.
	 */

	public FilterMode getFilterMode()
	{
		return filterMode.get();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the filter mode of this pane as a read-only property.
	 *
	 * @return the filter mode of this pane as a read-only property.
	 */

	public ReadOnlyObjectProperty<FilterMode> filterModeProperty()
	{
		return filterMode;
	}

	//------------------------------------------------------------------

	/**
	 * Advances the filter mode to the next value in the cycle.
	 */

	protected void nextFilterMode()
	{
		filterMode.set(FilterMode.values()[(filterMode.get().ordinal() + 1) % FilterMode.values().length]);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Image data
////////////////////////////////////////////////////////////////////////

	private interface ImageData
	{
		byte[]	CLEAR	=
		{
			(byte)0x89, (byte)0x50, (byte)0x4E, (byte)0x47, (byte)0x0D, (byte)0x0A, (byte)0x1A, (byte)0x0A,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0D, (byte)0x49, (byte)0x48, (byte)0x44, (byte)0x52,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x10,
			(byte)0x08, (byte)0x06, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x1F, (byte)0xF3, (byte)0xFF,
			(byte)0x61, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFD, (byte)0x49, (byte)0x44, (byte)0x41,
			(byte)0x54, (byte)0x78, (byte)0x5E, (byte)0xAD, (byte)0x52, (byte)0x41, (byte)0x0E, (byte)0x82,
			(byte)0x30, (byte)0x10, (byte)0xF4, (byte)0x11, (byte)0xEA, (byte)0x6F, (byte)0x08, (byte)0xE5,
			(byte)0xC2, (byte)0x03, (byte)0x0C, (byte)0x3E, (byte)0x81, (byte)0x04, (byte)0x9E, (byte)0xA1,
			(byte)0xF8, (byte)0x32, (byte)0xEE, (byte)0xA2, (byte)0xFF, (byte)0xE0, (byte)0xA2, (byte)0x2D,
			(byte)0x70, (byte)0xB4, (byte)0xEE, (byte)0x14, (byte)0xB6, (byte)0x69, (byte)0x1B, (byte)0x88,
			(byte)0x98, (byte)0x30, (byte)0xC9, (byte)0x84, (byte)0xD2, (byte)0xDD, (byte)0x99, (byte)0xEE,
			(byte)0x6E, (byte)0xBB, (byte)0xDB, (byte)0x6D, (byte)0x8D, (byte)0x34, (byte)0x4D, (byte)0x8F,
			(byte)0x42, (byte)0x88, (byte)0x1B, (byte)0xF1, (byte)0x41, (byte)0xEC, (byte)0x26, (byte)0x36,
			(byte)0xC4, (byte)0x0A, (byte)0xB1, (byte)0x30, (byte)0xDF, (byte)0x43, (byte)0x1C, (byte)0xC7,
			(byte)0x67, (byte)0x4A, (byte)0x94, (byte)0x44, (byte)0xBD, (byte)0x40, (byte)0xC4, (byte)0xB2,
			(byte)0x50, (byte)0x67, (byte)0x30, (byte)0x89, (byte)0x3F, (byte)0x33, (byte)0xA2, (byte)0x90,
			(byte)0xC8, (byte)0xF1, (byte)0x4D, (byte)0xA6, (byte)0xB2, (byte)0xCD, (byte)0xC9, (byte)0x75,
			(byte)0x5D, (byte)0xEB, (byte)0xB6, (byte)0x6D, (byte)0x75, (byte)0x51, (byte)0x14, (byte)0x56,
			(byte)0x84, (byte)0x35, (byte)0xF6, (byte)0x10, (byte)0xC3, (byte)0x3F, (byte)0x1D, (byte)0xF6,
			(byte)0x8E, (byte)0xA2, (byte)0xE8, (byte)0x60, (byte)0x0D, (byte)0xC4, (byte)0xD8, (byte)0xB3,
			(byte)0x09, (byte)0x22, (byte)0x11, (byte)0x50, (byte)0x4A, (byte)0x19, (byte)0x21, (byte)0x88,
			(byte)0x35, (byte)0x80, (byte)0x18, (byte)0xE7, (byte)0x25, (byte)0x49, (byte)0x72, (byte)0x75,
			(byte)0x0D, (byte)0x9E, (byte)0x1C, (byte)0xC8, (byte)0xF3, (byte)0x5C, (byte)0x4B, (byte)0x29,
			(byte)0x8D, (byte)0x60, (byte)0x18, (byte)0x06, (byte)0xDD, (byte)0x75, (byte)0x9D, (byte)0x59,
			(byte)0xE3, (byte)0x5B, (byte)0x96, (byte)0xA5, (byte)0xDB, (byte)0x4A, (byte)0xE3, (byte)0x1A,
			(byte)0x28, (byte)0x27, (byte)0x60, (byte)0x4C, (byte)0xFA, (byte)0xBE, (byte)0x37, (byte)0x42,
			(byte)0x36, (byte)0x0A, (byte)0xC4, (byte)0xA0, (byte)0x74, (byte)0x0D, (byte)0xBC, (byte)0xC9,
			(byte)0xC3, (byte)0x80, (byte)0x4F, (byte)0x5E, (byte)0x32, (byte)0xC0, (byte)0x1C, (byte)0x5C,
			(byte)0x03, (byte)0xDC, (byte)0xF9, (byte)0x6C, (byte)0x0B, (byte)0x5C, (byte)0xC9, (byte)0xAF,
			(byte)0x16, (byte)0x2A, (byte)0x0E, (byte)0xAC, (byte)0x1D, (byte)0x22, (byte)0x55, (byte)0x70,
			(byte)0xB1, (byte)0x06, (byte)0xFF, (byte)0x5E, (byte)0x23, (byte)0xF1, (byte)0x45, (byte)0xDC,
			(byte)0x5B, (byte)0x03, (byte)0x80, (byte)0x36, (byte)0x32, (byte)0xB1, (byte)0xF2, (byte)0x21,
			(byte)0xD1, (byte)0x15, (byte)0x9E, (byte)0x3C, (byte)0x31, (byte)0x03, (byte)0x26, (byte)0x18,
			(byte)0xCE, (byte)0x8C, (byte)0xC8, (byte)0x9E, (byte)0xBC, (byte)0x28, (byte)0x66, (byte)0xE0,
			(byte)0x85, (byte)0xE1, (byte)0x91, (byte)0x90, (byte)0xD1, (byte)0x5D, (byte)0x8C, (byte)0xD7,
			(byte)0xAB, (byte)0xB0, (byte)0x46, (byte)0xCF, (byte)0x22, (byte)0x2C, (byte)0x7B, (byte)0x0B,
			(byte)0x7C, (byte)0x01, (byte)0x3D, (byte)0x89, (byte)0x73, (byte)0xCF, (byte)0x31, (byte)0x6C,
			(byte)0x9D, (byte)0xB3, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x49, (byte)0x45,
			(byte)0x4E, (byte)0x44, (byte)0xAE, (byte)0x42, (byte)0x60, (byte)0x82
		};
	}

	//==================================================================

}

//----------------------------------------------------------------------
