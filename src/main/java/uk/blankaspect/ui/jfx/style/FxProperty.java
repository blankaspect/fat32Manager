/*====================================================================*\

FxProperty.java

Enumeration: JavaFX CSS properties.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style;

//----------------------------------------------------------------------


// ENUMERATION: JAVAFX CSS PROPERTIES


public enum FxProperty
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	BACKGROUND_COLOUR
	(
		"background-color"
	),

	BACKGROUND_INSETS
	(
		"background-insets"
	),

	BACKGROUND_RADIUS
	(
		"background-radius"
	),

	BASE
	(
		"base"
	),

	BORDER_COLOUR
	(
		"border-color"
	),

	BORDER_INSETS
	(
		"border-insets"
	),

	BORDER_RADIUS
	(
		"border-radius"
	),

	BORDER_STYLE
	(
		"border-style"
	),

	BORDER_WIDTH
	(
		"border-width"
	),

	CONTROL_INNER_BACKGROUND
	(
		"control-inner-background"
	),

	FILL
	(
		"fill"
	),

	FONT_FAMILY
	(
		"font-family"
	),

	FONT_SIZE
	(
		"font-size"
	),

	FONT_SMOOTHING_TYPE
	(
		"font-smoothing-type"
	),

	FONT_STYLE
	(
		"font-style"
	),

	FONT_WEIGHT
	(
		"font-weight"
	),

	HIGHLIGHT_FILL
	(
		"highlight-fill"
	),

	HIGHLIGHT_TEXT_FILL
	(
		"highlight-text-fill"
	),

	OPACITY
	(
		"opacity"
	),

	PROMPT_TEXT_FILL
	(
		"prompt-text-fill"
	),

	STROKE
	(
		"stroke"
	),

	TAB_SIZE
	(
		"tab-size"
	),

	TEXT_BASE_COLOR
	(
		"text-base-color"
	),

	TEXT_FILL
	(
		"text-fill"
	),

	UNDERLINE
	(
		"underline"
	);

	public static final	String	NAME_PREFIX	= "-fx-";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String	name;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private FxProperty(
		String	nameSuffix)
	{
		// Initialise instance variables
		name = NAME_PREFIX + nameSuffix;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getName()
	{
		return name;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
