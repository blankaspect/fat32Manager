/*====================================================================*\

PaneStyle.java

Class: style information for a pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.container;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: STYLE INFORMATION FOR A PANE


public class PaneStyle
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.PANE)
					.build()
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	PANE	= StyleConstants.CLASS_PREFIX + "pane";
	}

	/** Keys of colours that are used in colour properties. */
	public interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	PANE_BORDER	= PREFIX + "pane.border";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(PaneStyle.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private PaneStyle()
	{
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
