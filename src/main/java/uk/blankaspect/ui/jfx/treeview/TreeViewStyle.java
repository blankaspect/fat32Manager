/*====================================================================*\

TreeViewStyle.java

Class: style information for a tree view

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.treeview;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.FxStyleClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: STYLE INFORMATION FOR A TREE VIEW


public class TreeViewStyle
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT,
			CssSelector.builder()
						.cls(StyleClass.TREE_VIEW)
						.desc(FxStyleClass.TREE_CELL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT_SELECTED,
			CssSelector.builder()
						.cls(StyleClass.TREE_VIEW)
						.desc(FxStyleClass.TREE_CELL).pseudo(FxPseudoClass.SELECTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND,
			CssSelector.builder()
						.cls(StyleClass.TREE_VIEW)
						.desc(FxStyleClass.TREE_CELL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_SELECTED,
			CssSelector.builder()
						.cls(StyleClass.TREE_VIEW)
						.desc(FxStyleClass.TREE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_SELECTED_FOCUSED,
			CssSelector.builder()
						.cls(StyleClass.TREE_VIEW).pseudo(FxPseudoClass.FOCUSED)
						.desc(FxStyleClass.TREE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT,
			CssSelector.builder()
						.cls(StyleClass.TREE_VIEW)
						.desc(StyleClass.CELL_LABEL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT_SELECTED,
			CssSelector.builder()
						.cls(FxStyleClass.TREE_CELL).pseudo(FxPseudoClass.SELECTED)
						.desc(StyleClass.CELL_LABEL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_DISCLOSURE_ARROW,
			CssSelector.builder()
						.cls(StyleClass.TREE_VIEW)
						.desc(FxStyleClass.TREE_CELL)
						.child(FxStyleClass.TREE_DISCLOSURE_NODE)
						.child(FxStyleClass.ARROW)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_DISCLOSURE_ARROW_SELECTED,
			CssSelector.builder()
						.cls(StyleClass.TREE_VIEW)
						.desc(FxStyleClass.TREE_CELL).pseudo(FxPseudoClass.SELECTED)
						.child(FxStyleClass.TREE_DISCLOSURE_NODE)
						.child(FxStyleClass.ARROW)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_POPUP_TEXT,
			CssSelector.builder()
						.cls(StyleClass.CELL_POPUP_LABEL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_POPUP_BACKGROUND,
			CssSelector.builder()
						.cls(StyleClass.CELL_POPUP_LABEL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.CELL_POPUP_BORDER,
			CssSelector.builder()
						.cls(StyleClass.CELL_POPUP_LABEL)
						.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		focusedCellRuleSet(
			CssSelector.builder()
					.cls(StyleClass.TREE_VIEW).pseudo(FxPseudoClass.FOCUSED)
					.desc(FxStyleClass.TREE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.FOCUSED)
					.build(),
			ColourKey.CELL_BACKGROUND_FOCUSED,
			ColourKey.CELL_BACKGROUND
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	CELL_LABEL			= StyleConstants.CLASS_PREFIX + "tree-cell-label";
		String	CELL_POPUP_LABEL	= StyleConstants.CLASS_PREFIX + "tree-cell-popup-label";
		String	TREE_VIEW			= StyleConstants.CLASS_PREFIX + "tree-view";
	}

	/** Keys of colours that are used in colour properties. */
	public interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	CELL_BACKGROUND						= PREFIX + "cell.background";
		String	CELL_BACKGROUND_FOCUSED				= PREFIX + "cell.background.focused";
		String	CELL_BACKGROUND_SELECTED			= PREFIX + "cell.background.selected";
		String	CELL_BACKGROUND_SELECTED_FOCUSED	= PREFIX + "cell.background.selected.focused";
		String	CELL_DISCLOSURE_ARROW				= PREFIX + "cell.disclosureArrow";
		String	CELL_DISCLOSURE_ARROW_SELECTED		= PREFIX + "cell.disclosureArrow.selected";
		String	CELL_POPUP_BACKGROUND				= PREFIX + "cell.popup.background";
		String	CELL_POPUP_BORDER					= PREFIX + "cell.popup.border";
		String	CELL_POPUP_TEXT						= PREFIX + "cell.popup.text";
		String	CELL_TEXT							= PREFIX + "cell.text";
		String	CELL_TEXT_SELECTED					= PREFIX + "cell.text.selected";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(TreeViewStyle.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private TreeViewStyle()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static CssRuleSet focusedCellRuleSet(
		String	selector,
		String	outerColourKey,
		String	innerColourKey)
	{
		return RuleSetBuilder.create()
				.selector(selector)
				.property(FxProperty.BACKGROUND_COLOUR,
						  StyleConstants.COLOUR_KEY_PREFIX + outerColourKey + StyleConstants.VALUE_SEPARATOR
							+ StyleConstants.COLOUR_KEY_PREFIX + innerColourKey)
				.property(FxProperty.BACKGROUND_INSETS, "0, 1")
				.build();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
