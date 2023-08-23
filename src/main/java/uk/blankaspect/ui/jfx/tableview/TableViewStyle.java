/*====================================================================*\

TableViewStyle.java

Class: style information for a table view

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.tableview;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

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


// CLASS: STYLE INFORMATION FOR A TABLE VIEW


public class TableViewStyle
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
						.cls(StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.TABLE_CELL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT_SELECTED,
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.SELECTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_EMPTY,
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.TABLE_ROW_CELL)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.EMPTY)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.ROW_SELECTION)
						.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.FOCUSED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_EVEN,
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.EVEN)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_ODD,
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.ODD)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_SELECTED,
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.ROW_SELECTION)
						.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.SELECTED)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.CELL_SELECTION)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_SELECTED_FOCUSED,
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.ROW_SELECTION)
						.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.SELECTED, FxPseudoClass.EVEN)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.ROW_SELECTION)
						.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.SELECTED, FxPseudoClass.ODD)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.CELL_SELECTION)
						.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.EVEN)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED, FxPseudoClass.FOCUSED)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.CELL_SELECTION)
						.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.ODD)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED, FxPseudoClass.FOCUSED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.CELL_BORDER,
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT,
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW)
						.desc(StyleClass.CELL_LABEL)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT_SELECTED,
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.SELECTED)
						.desc(StyleClass.CELL_LABEL)
						.build(),
			CssSelector.builder()
						.cls(StyleClass.TABLE_VIEW)
						.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.SELECTED)
						.desc(StyleClass.CELL_LABEL)
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
									.cls(StyleClass.TABLE_VIEW)
									.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.EMPTY)
									.build())
						.emptyBorder()
						.build(),
		RuleSetBuilder.create()
						.selector(CssSelector.builder()
									.cls(StyleClass.TABLE_VIEW)
									.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED)
									.build())
						.borders(Side.RIGHT, Side.BOTTOM)
						.build(),
		focusedCellRuleSetRowSelection(
			CssSelector.builder()
					.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.ROW_SELECTION)
					.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.EVEN)
					.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED)
					.build(),
			ColourKey.CELL_BACKGROUND_FOCUSED,
			ColourKey.CELL_BACKGROUND_EVEN
		),
		focusedCellRuleSetRowSelection(
			CssSelector.builder()
					.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.ROW_SELECTION)
					.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.ODD)
					.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED)
					.build(),
			ColourKey.CELL_BACKGROUND_FOCUSED,
			ColourKey.CELL_BACKGROUND_ODD
		),
		focusedCellRuleSetCellSelection(
			CssSelector.builder()
					.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.CELL_SELECTION)
					.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.EVEN)
					.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.FOCUSED)
					.build(),
			ColourKey.CELL_BACKGROUND_FOCUSED,
			ColourKey.CELL_BACKGROUND_EVEN
		),
		focusedCellRuleSetCellSelection(
			CssSelector.builder()
					.cls(StyleClass.TABLE_VIEW).pseudo(FxPseudoClass.FOCUSED, FxPseudoClass.CELL_SELECTION)
					.desc(FxStyleClass.TABLE_ROW_CELL).pseudo(FxPseudoClass.ODD)
					.desc(FxStyleClass.TABLE_CELL).pseudo(FxPseudoClass.FILLED, FxPseudoClass.FOCUSED)
					.build(),
			ColourKey.CELL_BACKGROUND_FOCUSED,
			ColourKey.CELL_BACKGROUND_ODD
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	CELL_LABEL			= StyleConstants.CLASS_PREFIX + "table-cell-label";
		String	CELL_POPUP_LABEL	= StyleConstants.CLASS_PREFIX + "table-cell-popup-label";
		String	TABLE_VIEW			= StyleConstants.CLASS_PREFIX + "table-view";
	}

	/** Keys of colours that are used in colour properties. */
	public interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	CELL_BACKGROUND_EMPTY				= PREFIX + "cell.background.empty";
		String	CELL_BACKGROUND_EVEN				= PREFIX + "cell.background.even";
		String	CELL_BACKGROUND_ODD					= PREFIX + "cell.background.odd";
		String	CELL_BACKGROUND_FOCUSED				= PREFIX + "cell.background.focused";
		String	CELL_BACKGROUND_SELECTED			= PREFIX + "cell.background.selected";
		String	CELL_BACKGROUND_SELECTED_FOCUSED	= PREFIX + "cell.background.selected.focused";
		String	CELL_BORDER							= PREFIX + "cell.border";
		String	CELL_POPUP_BACKGROUND				= PREFIX + "cell.popup.background";
		String	CELL_POPUP_BORDER					= PREFIX + "cell.popup.border";
		String	CELL_POPUP_TEXT						= PREFIX + "cell.popup.text";
		String	CELL_TEXT							= PREFIX + "cell.text";
		String	CELL_TEXT_SELECTED					= PREFIX + "cell.text.selected";
		String	HEADER_CELL_BACKGROUND				= PREFIX + "header.cell.background";
		String	HEADER_CELL_BORDER					= PREFIX + "header.cell.border";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(TableViewStyle.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private TableViewStyle()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static CssRuleSet focusedCellRuleSetRowSelection(
		String	selector,
		String	outerColourKey,
		String	innerColourKey)
	{
		return focusedCellRuleSetSelection(selector, outerColourKey, innerColourKey, "1 1 2 0");
	}

	//------------------------------------------------------------------

	public static CssRuleSet focusedCellRuleSetCellSelection(
		String	selector,
		String	outerColourKey,
		String	innerColourKey)
	{
		return focusedCellRuleSetSelection(selector, outerColourKey, innerColourKey, "1 2 2 1");
	}

	//------------------------------------------------------------------

	private static CssRuleSet focusedCellRuleSetSelection(
		String	selector,
		String	outerColourKey,
		String	innerColourKey,
		String	innerInsets)
	{
		return RuleSetBuilder.create()
				.selector(selector)
				.property(FxProperty.BACKGROUND_COLOUR,
						  StyleConstants.COLOUR_KEY_PREFIX + outerColourKey + StyleConstants.VALUE_SEPARATOR
							+ StyleConstants.COLOUR_KEY_PREFIX + innerColourKey)
				.property(FxProperty.BACKGROUND_INSETS, "0 1 1 0, " + innerInsets)
				.build();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
