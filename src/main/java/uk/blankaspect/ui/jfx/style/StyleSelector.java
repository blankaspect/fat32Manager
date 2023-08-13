/*====================================================================*\

StyleSelector.java

Interface: style-selector constants.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;

import javafx.scene.control.skin.VirtualFlow;

//----------------------------------------------------------------------


// INTERFACE: STYLE-SELECTOR CONSTANTS


/**
 * This interface defines constants for some of the CSS selectors of JavaFX.
 */

public interface StyleSelector
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The label of a {@link ChoiceBox}. */
	String	CHOICE_BOX_LABEL				= ".choice-box > .label";

	/** A {@link ColorPicker}. */
	String	COLOR_PICKER					= ".color-picker";

	/** The label of a {@link ColorPicker}. */
	String	COLOR_PICKER_LABEL				= ".color-picker-label";

	/** The column header of a {@link TableView}. */
	String	COLUMN_HEADER					= ".column-header";

	/** The background of the column headers of a {@link TableView}. */
	String	COLUMN_HEADER_BACKGROUND		= ".column-header-background";

	/** The label of a column header of a {@link TableView}. */
	String	COLUMN_HEADER_LABEL				= ".column-header > .label";

	/** The filler of a {@link TableView}. */
	String	FILLER							= ".filler";

	/** The label of a control or container. */
	String	LABEL							= ".label";

	/** The horizontal scroll bar of a {@link ListView}. */
	String	LIST_VIEW_HORIZONTAL_SCROLLBAR	= ".list-view > .virtual-flow > .scroll-bar:horizontal";

	/** The vertical scroll bar of a {@link ListView}. */
	String	LIST_VIEW_VERTICAL_SCROLLBAR	= ".list-view > .virtual-flow > .scroll-bar:vertical";

	/** The label of a {@link MenuButton}. */
	String	MENU_BUTTON_LABEL				= ".menu-button > .label";

	/** The bar of a {@link ProgressBar}. */
	String	PROGRESS_BAR_BAR				= ".progress-bar > .bar";

	/** The scroll bar of a {@link ScrollPane}. */
	String	SCROLL_BAR						= ".scroll-bar";

	/** The viewport of a {@link ScrollPane}. */
	String	SCROLL_PANE_VIEWPORT			= ".scroll-pane > .viewport";

	/** A divider of a {@link SplitPane}. */
	String	SPLIT_PANE_DIVIDER				= ".split-pane-divider";

	/** A tab of a {@link TabPane}. */
	String	TAB								= ".tab-pane > .tab-header-area > .headers-region > .tab";

	/** The container of a tab of a {@link TabPane}. */
	String  TAB_CONTAINER					= ".tab-pane > .tab-header-area > .headers-region > .tab > .tab-container";

	/** The tab-header area of a {@link TabPane}. */
	String	TAB_HEADER_AREA					= ".tab-pane > .tab-header-area";

	/** The background of the tab-header area of a {@link TabPane}. */
	String	TAB_HEADER_BACKGROUND			= ".tab-pane > .tab-header-area > .tab-header-background";

	/** The headers region of a {@link TabPane}. */
	String	TAB_HEADERS_REGION				= ".tab-pane > .tab-header-area > .headers-region";

	/** The label of a tab of a {@link TabPane}. */
	String  TAB_LABEL						= ".tab-pane > .tab-header-area > .headers-region > .tab > .tab-container > .tab-label";

	/** The text node of a control or container. */
	String	TEXT							= ".text";

	/** The content of a {@link TextArea}. */
	String	TEXT_AREA_CONTENT				= ".text-area .content";

	/** The title of a {@link TitledPane}. */
	String	TITLED_PANE_TITLE				= ".titled-pane > .title";

	/** The disclosure node of a cell of a {@link TreeView}. */
	String	TREE_CELL_DISCLOSURE_NODE		= ".tree-cell > .tree-disclosure-node";

	/** The disclosure arrow of a cell of a {@link TreeView}. */
	String	TREE_CELL_DISCLOSURE_ARROW		= ".tree-cell > .tree-disclosure-node > .arrow";

	/** A {@link VirtualFlow}. */
	String	VIRTUAL_FLOW					= ".virtual-flow";

}

//----------------------------------------------------------------------
