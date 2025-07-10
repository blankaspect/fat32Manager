/*====================================================================*\

AbstractHeaderPane.java

Class: abstract header pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.container;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Node;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: ABSTRACT HEADER PANE


/**
 * This abstract class implements a pane that has a border, a header that extends across the width of the pane, and a
 * {@linkplain BorderPane content pane}.  Content may be set in any of the five {@linkplain
 * AbstractHeaderPane.ContentPosition positions} of the content pane.
 */

public abstract class AbstractHeaderPane
	extends VBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default padding around the header. */
	private static final	Insets	DEFAULT_HEADER_PADDING	= new Insets(1.0, 5.0, 1.0, 5.0);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.BORDER,
			CssSelector.builder()
					.cls(StyleClass.HEADER_PANE)
					.build()
		)
	);

	/**
	 * The positions of the content of a {@link AbstractHeaderPane}.
	 */
	public enum ContentPosition
	{
		TOP,
		BOTTOM,
		LEFT,
		RIGHT,
		CENTER
	}

	/** CSS style classes. */
	private interface StyleClass
	{
		String	HEADER_PANE	= StyleConstants.CLASS_PREFIX + "header-pane";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	BORDER	= PREFIX + "border";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The padding around the header. */
	private	Insets		headerPadding;

	/** The header container. */
	private	HBox		header;

	/** The content pane. */
	private	BorderPane	contentPane;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(AbstractHeaderPane.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a header pane.
	 */

	protected AbstractHeaderPane()
	{
		// Initialise instance variables
		headerPadding = DEFAULT_HEADER_PADDING;

		// Set properties
		setAlignment(Pos.TOP_CENTER);
		setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.BORDER)));
		getStyleClass().add(StyleClass.HEADER_PANE);

		// Create header
		header = new HBox();
		header.setAlignment(Pos.CENTER_LEFT);
		header.setPadding(headerPadding);

		// Create content pane
		contentPane = new BorderPane();
		VBox.setVgrow(contentPane, Priority.ALWAYS);

		// Add children to this pane
		getChildren().addAll(header, contentPane);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a header pane with the specified content.  The content is set in the centre of the
	 * content pane.
	 *
	 * @param content
	 *          the content of the pane.
	 */

	protected AbstractHeaderPane(
		Node	content)
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
	 * Returns the padding around the header.
	 *
	 * @return the padding around the header.
	 */

	public Insets getHeaderPadding()
	{
		return headerPadding;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the padding around the header to the specified value.
	 *
	 * @param padding
	 *          the value to which the padding around the header will be set.
	 */

	public void setHeaderPadding(
		Insets	padding)
	{
		// Update instance variable
		headerPadding = padding;

		// Update header
		updateHeader();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the header container of this pane.
	 *
	 * @return the header container of this pane.
	 */

	public HBox getHeader()
	{
		return header;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the content pane of this pane.
	 *
	 * @return the content pane of this pane.
	 */

	public BorderPane getContentPane()
	{
		return contentPane;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the content at the specified position of this pane.
	 *
	 * @param  position
	 *           the position whose content is required.
	 * @return the content of this pane at {@code position}.
	 */

	public Node getContent(
		ContentPosition	position)
	{
		// Validate argument
		if (position == null)
			throw new IllegalArgumentException("Null position");

		// Return content at specified position
		switch (position)
		{
		case TOP:
			return contentPane.getTop();

		case BOTTOM:
			return contentPane.getBottom();

		case LEFT:
			return contentPane.getLeft();

		case RIGHT:
			return contentPane.getRight();

		case CENTER:
			return contentPane.getCenter();
		}
		return null;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the specified node as the content of this pane at the specified position.
	 *
	 * @param position
	 *          the position at which {@code content} will be set.
	 * @param content
	 *          the node that will be set as the content of this pane.
	 */

	public void setContent(
		ContentPosition	position,
		Node			content)
	{
		// Validate arguments
		if (position == null)
			throw new IllegalArgumentException("Null position");
		if (content == null)
			throw new IllegalArgumentException("Null content");

		// Set content at specified position
		switch (position)
		{
		case TOP:
			contentPane.setTop(content);
			break;

		case BOTTOM:
			contentPane.setBottom(content);
			break;

		case LEFT:
			contentPane.setLeft(content);
			break;

		case RIGHT:
			contentPane.setRight(content);
			break;

		case CENTER:
			contentPane.setCenter(content);
			break;
		}

		// Update header and content pane
		update();
	}

	//------------------------------------------------------------------

	/**
	 * Updates the header.
	 */

	protected void updateHeader()
	{
		header.setPadding(headerPadding);
	}

	//------------------------------------------------------------------

	/**
	 * Updates the header and content pane.
	 */

	protected void update()
	{
		updateHeader();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
