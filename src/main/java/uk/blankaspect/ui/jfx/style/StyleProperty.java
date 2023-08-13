/*====================================================================*\

StyleProperty.java

Interface: style-property constants.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.control.CheckBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToolBar;

import javafx.scene.layout.Region;

//----------------------------------------------------------------------


// INTERFACE: STYLE-PROPERTY CONSTANTS


/**
 * This interface defines constants for the names of some of the CSS properties of JavaFX nodes.
 */

public interface StyleProperty
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The background colour of the viewport of a {@link ScrollPane}. */
	String	BACKGROUND			= "-fx-background";

	/** The background insets of the bar of a {@link ProgressBar}. */
	String	BACKGROUND_INSETS	= "-fx-background-insets";

	/** The colour of the body of a control. */
	String	BODY_COLOUR			= "-fx-body-color";

	/** The optional animation that accompanies the closing of a tab in a {@link TabPane}. */
	String	CLOSE_TAB_ANIMATION	= "-fx-close-tab-animation";

	/** The colour of the inner border of a control. */
	String	INNER_BORDER		= "-fx-inner-border";

	/** The padding around the label of a {@link CheckBox} or a {@link RadioButton}. */
	String	LABEL_PADDING		= "-fx-label-padding";

	/** The optional animation that accompanies the opening of a tab in a {@link TabPane}. */
	String	OPEN_TAB_ANIMATION	= "-fx-open-tab-animation";

	/** The colour of the outer border of a control. */
	String	OUTER_BORDER		= "-fx-outer-border";

	/** The padding around a {@link Region}. */
	String	PADDING				= "-fx-padding";

	/** The text fill of a {@link Labeled} or {@link TextInputControl}. */
	String	TEXT_FILL			= "-fx-text-fill";

	/** The spacing between buttons in a {@link ToolBar}. */
	String	TOOLBAR_SPACING		= "-fx-spacing";

}

//----------------------------------------------------------------------
