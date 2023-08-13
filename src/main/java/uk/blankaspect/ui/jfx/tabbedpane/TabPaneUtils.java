/*====================================================================*\

TabPaneUtils.java

Class: tabbed-pane-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.tabbedpane;

//----------------------------------------------------------------------


// IMPORTS


import javafx.geometry.Insets;
import javafx.geometry.Side;

import javafx.scene.control.TabPane;

import javafx.scene.layout.Region;

import javafx.scene.paint.Color;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.StyleSelector;

//----------------------------------------------------------------------


// CLASS: TABBED-PANE-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain TabPane tabbed panes}.
 */

public class TabPaneUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private TabPaneUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Sets the padding of the header area of the specified tabbed pane to the specified values.
	 * <p>
	 * <b>NOTE</b>:<br/>
	 * This method requires that the skin of the tabbed pane be initialised, which can be achieved by calling the method
	 * from a listener on {@link TabPane#skinProperty()}.
	 * </p>
	 *
	 * @param tabPane
	 *          the tabbed pane on whose header area the padding will be set.
	 * @param padding
	 *          the padding that will be set on the header area of <i>tabPane</i>.
	 */

	public static void setHeaderAreaPadding(TabPane tabPane,
											Insets  padding)
	{
		((Region)tabPane.lookup(StyleSelector.TAB_HEADER_AREA)).setPadding(padding);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the padding at the top of the header area of the specified tabbed pane to zero.
	 * <p>
	 * <b>NOTE</b>:<br/>
	 * This method requires that the skin of the tabbed pane be initialised, which can be achieved by calling the method
	 * from a listener on {@link TabPane#skinProperty()}.
	 * </p>
	 *
	 * @param tabPane
	 *          the tabbed pane whose header area will have its top padding removed.
	 */

	public static void removeHeaderAreaTopPadding(TabPane tabPane)
	{
		Region headerArea = (Region)tabPane.lookup(StyleSelector.TAB_HEADER_AREA);
		Insets padding = headerArea.getPadding();
		headerArea.setPadding(new Insets(0.0, padding.getRight(), padding.getBottom(), padding.getLeft()));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background of the header area of the specified tabbed pane to the specified colour.
	 * <p>
	 * <b>NOTE</b>:<br/>
	 * This method requires that the skin of the tabbed pane be initialised, which can be achieved by calling the method
	 * from a listener on {@link TabPane#skinProperty()}.
	 * </p>
	 *
	 * @param tabPane
	 *          the tabbed pane whose header area will have its background colour set.
	 * @param colour
	 *          the colour to which the background of the header area of <i>tabPane</i> will be set.
	 */

	public static void setHeaderBackground(TabPane tabPane,
										   Color   colour)
	{
		((Region)tabPane.lookup(StyleSelector.TAB_HEADER_BACKGROUND))
															.setBackground(SceneUtils.createColouredBackground(colour));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the bottom border of the header area of the specified tabbed pane to the specified colour.
	 * <p>
	 * <b>NOTE</b>:<br/>
	 * This method requires that the skin of the tabbed pane be initialised.  This can be achieved by calling the method
	 * from a listener on {@link TabPane#skinProperty()}.
	 * </p>
	 *
	 * @param tabPane
	 *          the tabbed pane whose header area will have its background colour set.
	 * @param colour
	 *          the colour to which the bottom border of the header area of {@code tabPane} will be set.
	 */

	public static void setHeaderBottomBorder(TabPane tabPane,
											 Color   colour)
	{
		((Region)tabPane.lookup(StyleSelector.TAB_HEADER_BACKGROUND))
														.setBorder(SceneUtils.createSolidBorder(colour, Side.BOTTOM));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
