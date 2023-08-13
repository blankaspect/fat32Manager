/*====================================================================*\

ListViewStyle.java

Class: style information for a list view

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.listview;

//----------------------------------------------------------------------


// IMPORTS


import java.util.List;

import javafx.geometry.Side;

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


// CLASS: STYLE INFORMATION FOR A LIST VIEW


public class ListViewStyle
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
						.cls(StyleClass.LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_EMPTY,
			CssSelector.builder()
						.cls(StyleClass.LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.EMPTY)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_EVEN,
			CssSelector.builder()
						.cls(StyleClass.LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.EVEN)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_ODD,
			CssSelector.builder()
						.cls(StyleClass.LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.ODD)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_SELECTED,
			CssSelector.builder()
						.cls(StyleClass.LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_SELECTED_FOCUSED,
			CssSelector.builder()
						.cls(StyleClass.LIST_VIEW).pseudo(FxPseudoClass.FOCUSED)
						.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED, FxPseudoClass.EVEN)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.LIST_VIEW).pseudo(FxPseudoClass.FOCUSED)
						.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED, FxPseudoClass.ODD)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.CELL_BORDER,
			CssSelector.builder()
						.cls(StyleClass.LIST_VIEW)
						.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.FILLED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT,
			CssSelector.builder()
						.cls(StyleClass.CELL_LABEL)
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
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.LIST_VIEW)
									.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.EMPTY)
									.build())
						.emptyBorder()
						.build(),
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.LIST_VIEW)
									.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.FILLED)
									.build())
						.borders(Side.BOTTOM)
						.build(),
		focusedCellRuleSet(
			CssSelector.builder()
					.cls(StyleClass.LIST_VIEW).pseudo(FxPseudoClass.FOCUSED)
					.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.FOCUSED, FxPseudoClass.EVEN)
					.build(),
			ColourKey.CELL_BACKGROUND_FOCUSED,
			ColourKey.CELL_BACKGROUND_EVEN
		),
		focusedCellRuleSet(
			CssSelector.builder()
					.cls(StyleClass.LIST_VIEW).pseudo(FxPseudoClass.FOCUSED)
					.desc(FxStyleClass.LIST_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.FOCUSED, FxPseudoClass.ODD)
					.build(),
			ColourKey.CELL_BACKGROUND_FOCUSED,
			ColourKey.CELL_BACKGROUND_ODD
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	CELL_LABEL			= StyleConstants.CLASS_PREFIX + "list-cell-label";
		String	CELL_POPUP_LABEL	= StyleConstants.CLASS_PREFIX + "list-cell-popup-label";
		String	LIST_VIEW			= StyleConstants.CLASS_PREFIX + "list-view";
	}

	/** Keys of colours that are used in colour properties. */
	public interface ColourKey
	{
		String	CELL_BACKGROUND_EMPTY				= "listView.cell.background.empty";
		String	CELL_BACKGROUND_EVEN				= "listView.cell.background.even";
		String	CELL_BACKGROUND_ODD					= "listView.cell.background.odd";
		String	CELL_BACKGROUND_FOCUSED				= "listView.cell.background.focused";
		String	CELL_BACKGROUND_SELECTED			= "listView.cell.background.selected";
		String	CELL_BACKGROUND_SELECTED_FOCUSED	= "listView.cell.background.selected.focused";
		String	CELL_BORDER							= "listView.cell.border";
		String	CELL_POPUP_BACKGROUND				= "listView.cell.popup.background";
		String	CELL_POPUP_BORDER					= "listView.cell.popup.border";
		String	CELL_POPUP_TEXT						= "listView.cell.popup.text";
		String	CELL_TEXT							= "listView.cell.text";
		String	CELL_TEXT_SELECTED					= "listView.cell.text.selected";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(ListViewStyle.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ListViewStyle()
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
				.property(FxProperty.BACKGROUND_INSETS, "0 0 1 0, 1 1 2 1")
				.build();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
