/*====================================================================*\

FxStyleClass.java

Interface: JavaFX CSS style-class constants.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;

import javafx.scene.control.skin.VirtualFlow;

//----------------------------------------------------------------------


// INTERFACE: JAVAFX CSS STYLE-CLASS CONSTANTS


/**
 * This interface defines constants for some of the CSS style classes of JavaFX.
 */

public interface FxStyleClass
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The arrow shape of various components. */
	String	ARROW						= "arrow";

	/** A {@link ChoiceBox}. */
	String	CHOICE_BOX					= "choice-box";

	/** A {@link ColorPicker}. */
	String	COLOR_PICKER				= "color-picker";

	/** The label of a {@link ColorPicker}. */
	String	COLOR_PICKER_LABEL			= "color-picker-label";

	/** The column header of a {@link TableView} or a {@link TreeTableView}. */
	String	COLUMN_HEADER				= "column-header";

	/** The background of the column headers of a {@link TableView} or a {@link TreeTableView}. */
	String	COLUMN_HEADER_BACKGROUND	= "column-header-background";

	/** The content of a {@link TextArea} or other components. */
	String	CONTENT						= "content";

	/** A {@link ContextMenu}. */
	String	CONTEXT_MENU				= "context-menu";

	/** The filler of a {@link TableView}. */
	String	FILLER						= "filler";

	/** The headers region of a {@link TabPane}. */
	String	HEADERS_REGION				= "headers-region";

	/** The label of a control or container. */
	String	LABEL						= "label";

	/** A cell of a {@link ListView}. */
	String	LIST_CELL					= "list-cell";

	/** A {@link ListView}. */
	String	LIST_VIEW					= "list-view";

	/** A {@link MenuButton}. */
	String	MENU_BUTTON					= "menu-button";

	/** A {@link ProgressBar}. */
	String	PROGRESS_BAR				= "progress-bar";

	/** A scroll bar of a {@link ScrollPane}. */
	String	SCROLL_BAR					= "scroll-bar";

	/** A divider of a {@link SplitPane}. */
	String	SPLIT_PANE_DIVIDER			= "split-pane-divider";

	/** A tab of a {@link TabPane}. */
	String	TAB							= "tab";

	/** The tab-header area of a {@link TabPane}. */
	String	TAB_HEADER_AREA				= "tab-header-area";

	/** The background of the tab-header area of a {@link TabPane}. */
	String	TAB_HEADER_BACKGROUND		= "tab-header-background";

	/** The label of a tab of a {@link TabPane}. */
	String  TAB_LABEL					= "tab-label";

	/** A {@link TabPane}. */
	String	TAB_PANE					= "tab-pane";

	/** A cell of a {@link TableView}. */
	String	TABLE_CELL					= "table-cell";

	/** A cell of a row of a {@link TableView}. */
	String	TABLE_ROW_CELL				= "table-row-cell";

	/** The text node of a control or container. */
	String	TEXT						= "text";

	/** A {@link TextArea}. */
	String	TEXT_AREA					= "text-area";

	/** A {@link TitledPane}. */
	String	TITLED_PANE					= "titled-pane";

	/** A cell of a {@link TreeView}. */
	String	TREE_CELL					= "tree-cell";

	/** A disclosure node of a {@link TreeView}. */
	String	TREE_DISCLOSURE_NODE		= "tree-disclosure-node";

	/** The viewport of a {@link ScrollPane}. */
	String	VIEWPORT					= "viewport";

	/** A {@link VirtualFlow}. */
	String	VIRTUAL_FLOW				= "virtual-flow";

}

//----------------------------------------------------------------------
