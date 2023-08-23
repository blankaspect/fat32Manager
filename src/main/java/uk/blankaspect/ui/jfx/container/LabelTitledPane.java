/*====================================================================*\

LabelTitledPane.java

Class: label titled pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.container;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.Arrays;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Side;

import javafx.scene.Node;

import javafx.scene.control.Label;

import javafx.scene.layout.BorderPane;

import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;
import uk.blankaspect.ui.jfx.style.StyleUtils;

//----------------------------------------------------------------------


// CLASS: LABEL TITLED PANE


/**
 * This class implements a pane with a border and, in the top left corner, a title label.  Content may be set in any of
 * the five {@linkplain AbstractHeaderPane.ContentPosition positions} of the {@linkplain BorderPane content pane}.
 */

public class LabelTitledPane
	extends AbstractTitledPane
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The padding around the title. */
	private static final	Insets	TITLE_PADDING	= new Insets(1.0, 5.0, 1.0, 5.0);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.TITLE_BACKGROUND,
			CssSelector.builder()
						.cls(StyleClass.LABEL_TITLED_PANE)
						.desc(AbstractTitledPane.StyleClass.TITLE)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.TITLE_BORDER,
			CssSelector.builder()
						.cls(StyleClass.LABEL_TITLED_PANE)
						.desc(AbstractTitledPane.StyleClass.TITLE)
						.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.LABEL_TITLED_PANE)
									.desc(AbstractTitledPane.StyleClass.TITLE)
									.build())
						.borders(Side.RIGHT, Side.BOTTOM)
						.build()
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	LABEL_TITLED_PANE	= StyleConstants.CLASS_PREFIX + "label-titled-pane";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	TITLE_BACKGROUND	= PREFIX + "title.background";
		String	TITLE_BORDER		= PREFIX + "title.border";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The label. */
	private	Label	label;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(LabelTitledPane.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a titled pane with the specified text.
	 *
	 * @param text
	 *          the text of the titled pane.
	 */

	public LabelTitledPane(
		String	text)
	{
		// Create label
		label = new Label(text);
		label.setMaxWidth(Label.USE_PREF_SIZE);

		// Add label to title
		getTitle().getChildren().add(label);

		// Set properties
		setTitleBackgroundColour(getColour(ColourKey.TITLE_BACKGROUND));
		setTitleBorderColour(getColour(ColourKey.TITLE_BORDER));
		getTitle().setPadding(TITLE_PADDING);
		StyleUtils.replaceStyleClass(this, AbstractTitledPane.StyleClass.TITLED_HEADER_PANE, StyleClass.LABEL_TITLED_PANE);

		// Update title, header and content pane
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a titled pane with the specified text and content.  The content is set in the centre of
	 * the content pane.
	 *
	 * @param text
	 *          the text of the titled pane.
	 * @param content
	 *          the content of the titled pane.
	 */

	public LabelTitledPane(
		String	text,
		Node	content)
	{
		// Call alternative constructor
		this(text);

		// Set content
		setContent(ContentPosition.CENTER, content);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a label for the title of a titled pane with the specified text and borders.
	 *
	 * @param  text
	 *           the text of the label.
	 * @param  borders
	 *           the sides of the border of the label.
	 * @return a label for the title of a titled pane.
	 */

	public static Label createTitleLabel(
		String	text,
		Side...	borders)
	{
		return createTitleLabel(text, Arrays.asList(borders));
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a label for the title of a titled pane with the specified text and borders.
	 *
	 * @param  text
	 *           the text of the label.
	 * @param  borders
	 *           the sides of the border of the label.
	 * @return a label for the title of a titled pane.
	 */

	public static Label createTitleLabel(
		String			text,
		Iterable<Side>	borders)
	{
		Label label = new Label(text);
		label.setMaxWidth(Label.USE_PREF_SIZE);
		label.setPadding(TITLE_PADDING);
		label.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.TITLE_BACKGROUND)));
		label.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.TITLE_BORDER), borders));
		return label;
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
	 * Returns the label of this pane.
	 *
	 * @return the label of this pane.
	 */

	public Label getLabel()
	{
		return label;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
