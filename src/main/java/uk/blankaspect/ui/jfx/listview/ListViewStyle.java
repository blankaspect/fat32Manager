/*====================================================================*\

ListViewStyle.java

Class: style information for a list view.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.listview;

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
					.desc(FxStyleClass.LIST_CELL)
							.pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED, FxPseudoClass.EVEN)
					.build(),
			CssSelector.builder()
					.cls(StyleClass.LIST_VIEW).pseudo(FxPseudoClass.FOCUSED)
					.desc(FxStyleClass.LIST_CELL)
							.pseudo(FxPseudoClass.FILLED, FxPseudoClass.SELECTED, FxPseudoClass.ODD)
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
					.desc(FxStyleClass.LIST_CELL)
							.pseudo(FxPseudoClass.FILLED, FxPseudoClass.FOCUSED, FxPseudoClass.EVEN)
					.build(),
			ColourKey.CELL_BACKGROUND_FOCUSED,
			ColourKey.CELL_BACKGROUND_EVEN
		),
		focusedCellRuleSet(
			CssSelector.builder()
					.cls(StyleClass.LIST_VIEW).pseudo(FxPseudoClass.FOCUSED)
					.desc(FxStyleClass.LIST_CELL)
							.pseudo(FxPseudoClass.FILLED, FxPseudoClass.FOCUSED, FxPseudoClass.ODD)
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
