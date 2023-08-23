/*====================================================================*\

AbstractTitledPane.java

Class: abstract titled pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.container;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.Node;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: ABSTRACT TITLED PANE


/**
 * This abstract class implements a pane that has a border, a title on the left side of the header, and a {@linkplain
 * BorderPane content pane}.  Content may be set in any of the five {@linkplain AbstractHeaderPane.ContentPosition
 * positions} of the content pane.
 */

public abstract class AbstractTitledPane
	extends AbstractHeaderPane
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.TITLE_BACKGROUND,
			CssSelector.builder()
						.cls(StyleClass.TITLED_HEADER_PANE)
						.desc(StyleClass.TITLE)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.TITLE_BORDER,
			CssSelector.builder()
						.cls(StyleClass.TITLED_HEADER_PANE)
						.desc(StyleClass.TITLE)
						.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.TITLED_HEADER_PANE)
									.desc(StyleClass.TITLE)
									.build())
						.borders(Side.RIGHT, Side.BOTTOM)
						.build()
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	TITLED_HEADER_PANE	= StyleConstants.CLASS_PREFIX + "titled-header-pane";
		String	TITLE				= StyleConstants.CLASS_PREFIX + "title";
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

	/** The background colour of the title. */
	private	Color	titleBackgroundColour;

	/** The border colour of the title. */
	private	Color	titleBorderColour;

	/** The title container. */
	private	HBox	title;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(AbstractTitledPane.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a titled pane.
	 */

	protected AbstractTitledPane()
	{
		// Initialise instance variables
		titleBackgroundColour = getColour(ColourKey.TITLE_BACKGROUND);
		titleBorderColour = getColour(ColourKey.TITLE_BORDER);

		// Create title
		title = new HBox();
		title.setMaxWidth(HBox.USE_PREF_SIZE);
		title.setAlignment(Pos.CENTER_LEFT);
		title.getStyleClass().add(StyleClass.TITLE);

		// Add title and filler to header
		getHeader().getChildren().add(title);

		// Set properties
		setHeaderPadding(Insets.EMPTY);
		getStyleClass().add(StyleClass.TITLED_HEADER_PANE);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a titled pane with the specified content.  The content is set in the centre of the
	 * content pane.
	 *
	 * @param content
	 *          the content of the pane.
	 */

	protected AbstractTitledPane(Node content)
	{
		// Call alternative constructor
		this();

		// Set content
		setContent(ContentPosition.CENTER, content);
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
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the background colour of the title.
	 *
	 * @return the background colour of the title.
	 */

	public Color getTitleBackgroundColour()
	{
		return titleBackgroundColour;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background colour of the title to the specified value.
	 *
	 * @param colour
	 *          the value to which the background colour of the title will be set.
	 */

	public void setTitleBackgroundColour(Color colour)
	{
		// Update instance variable
		titleBackgroundColour = colour;

		// Update header
		updateHeader();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the border colour of the title.
	 *
	 * @return the border colour of the title.
	 */

	public Color getTitleBorderColour()
	{
		return titleBorderColour;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the border colour of the title to the specified value.
	 *
	 * @param colour
	 *          the value to which the border colour of the title will be set.
	 */

	public void setTitleBorderColour(Color colour)
	{
		// Update instance variable
		titleBorderColour = colour;

		// Update header
		updateHeader();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the title container of this pane.
	 *
	 * @return the title container of this pane.
	 */

	public HBox getTitle()
	{
		return title;
	}

	//------------------------------------------------------------------

	/**
	 * Updates the title.
	 */

	protected void updateTitle()
	{
		// Update background colour and border colour of title
		title.setBackground(SceneUtils.createColouredBackground(titleBackgroundColour));
		title.setBorder(SceneUtils.createSolidBorder(titleBorderColour, Side.RIGHT, Side.BOTTOM));
	}

	//------------------------------------------------------------------

	/**
	 * Updates the header.
	 */

	@Override
	protected void updateHeader()
	{
		// Call superclass method
		super.updateHeader();

		// Update title
		updateTitle();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
