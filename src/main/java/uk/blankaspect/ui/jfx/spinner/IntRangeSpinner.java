/*====================================================================*\

IntRangeSpinner.java

Class: integer-range spinner.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.spinner;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import java.util.function.IntFunction;

import java.util.stream.IntStream;

import javafx.animation.AnimationTimer;

import javafx.beans.InvalidationListener;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import javafx.collections.FXCollections;

import javafx.css.PseudoClass;

import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;

import javafx.scene.Group;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.paint.Color;

import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;

import javafx.stage.Popup;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IFunction2;
import uk.blankaspect.common.function.IProcedure0;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.ui.jfx.listview.ListViewUtils;

import uk.blankaspect.ui.jfx.shape.Shapes;
import uk.blankaspect.ui.jfx.shape.ShapeUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.Text2;
import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: INTEGER-RANGE SPINNER


/**
 * This class implements a non-editable spinner that allows the user to select a value from a range of {@code int}s.
 * The user can move forwards and backwards through the values in the range by means of a pair of buttons, or a value
 * may be selected from a drop-down list that is displayed when the primary mouse button is clicked on the text box of
 * the spinner.  The two buttons may be positioned in the following ways:
 * <ul>
 *   <li>
 *     Both buttons are on the left of the text box.
 *   </li>
 *   <li>
 *     Both buttons are on the right of the text box.
 *   </li>
 *   <li>
 *     The <i>decrement</i> button is on the left of the text box and the <i>increment</i> button is on the right.
 *   </li>
 * </ul>
 */

public class IntRangeSpinner
	extends Group
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The horizontal inset of the text box. */
	private static final	double	TEXT_BOX_H_INSET	= 6.0;

	/** The vertical inset of the text box. */
	private static final	double	TEXT_BOX_V_INSET	= 3.0;

	/** The preferred number of rows of the list view. */
	private static final	int		LIST_VIEW_NUM_ROWS	= 10;

	/** The gap between the text and the graphic of a cell of the list view. */
	private static final	double	LIST_VIEW_CELL_GRAPHIC_TEXT_GAP	= 6.0;

	/** The padding at the top and bottom of a cell of the list view. */
	private static final	double	LIST_VIEW_CELL_VERTICAL_PADDING	= 3.0;

	/** The logical size of a <i>tick</i> icon. */
	private static final	double	LIST_VIEW_TICK_ICON_SIZE	= 0.85 * TextUtils.textHeight();

	/** The opacity of a component when it is disabled. */
	private static final	double	DISABLED_OPACITY	= 0.4;

	/** A map from button-icon orientation to button information. */
	private static final	Map<Orientation, ButtonInfo>	BUTTON_INFOS	= new EnumMap<>(Map.of
	(
		Orientation.HORIZONTAL, new ButtonInfo(0.55, 0.35, 6.0),
		Orientation.VERTICAL,   new ButtonInfo(0.4,  0.6,  5.0)
	));

	/** Miscellaneous strings. */
	private static final	String	MIN_MAX_OUT_OF_ORDER_STR	= "Minimum and maximum values out of order";

	/** The positions of the buttons relative to the text box. */
	public enum ButtonPos
	{
		LEFT,
		RIGHT,
		LEFT_RIGHT
	}

	/** The pseudo-class that is associated with the <i>empty</i> state. */
	private static final	PseudoClass	EMPTY_PSEUDO_CLASS		= PseudoClass.getPseudoClass(FxPseudoClass.EMPTY);

	/** The pseudo-class that is associated with the <i>pressed</i> state. */
	private static final	PseudoClass	PRESSED_PSEUDO_CLASS	= PseudoClass.getPseudoClass(FxPseudoClass.PRESSED);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.FRAME,
			CssSelector.builder()
					.cls(StyleClass.INT_RANGE_SPINNER)
					.desc(StyleClass.FRAME)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TEXT_BOX_TEXT,
			CssSelector.builder()
					.cls(StyleClass.INT_RANGE_SPINNER)
					.desc(StyleClass.TEXT)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TEXT_BOX_TEXT_EMPTY,
			CssSelector.builder()
					.cls(StyleClass.INT_RANGE_SPINNER)
					.desc(StyleClass.TEXT).pseudo(FxPseudoClass.EMPTY)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TEXT_BOX_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.INT_RANGE_SPINNER)
					.desc(StyleClass.TEXT_BOX)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.TEXT_BOX_BACKGROUND_EMPTY,
			CssSelector.builder()
					.cls(StyleClass.INT_RANGE_SPINNER)
					.desc(StyleClass.TEXT_BOX).pseudo(FxPseudoClass.EMPTY)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BUTTON_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.INT_RANGE_SPINNER)
					.desc(StyleClass.BUTTON)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BUTTON_BACKGROUND_PRESSED,
			CssSelector.builder()
					.cls(StyleClass.INT_RANGE_SPINNER)
					.desc(StyleClass.BUTTON).pseudo(FxPseudoClass.PRESSED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BUTTON_ICON,
			CssSelector.builder()
					.cls(StyleClass.INT_RANGE_SPINNER)
					.desc(StyleClass.ARROWHEAD)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.LIST_VIEW_TICK,
			CssSelector.builder()
					.cls(StyleClass.LIST_VIEW_TICK)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	INT_RANGE_SPINNER	= StyleConstants.CLASS_PREFIX + "int-range-spinner";

		String	ARROWHEAD			= StyleConstants.CLASS_PREFIX + "arrowhead";
		String	BUTTON				= StyleConstants.CLASS_PREFIX + "button";
		String	FRAME				= StyleConstants.CLASS_PREFIX + "frame";
		String	LIST_VIEW_TICK		= INT_RANGE_SPINNER + "-list-view-tick";
		String	TEXT				= StyleConstants.CLASS_PREFIX + "text";
		String	TEXT_BOX			= StyleConstants.CLASS_PREFIX + "text-box";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	BUTTON_BACKGROUND			= PREFIX + "button.background";
		String	BUTTON_BACKGROUND_PRESSED	= PREFIX + "button.background.pressed";
		String	BUTTON_ICON					= PREFIX + "button.icon";
		String	FRAME						= PREFIX + "frame";
		String	LIST_VIEW_TICK				= PREFIX + "listView.tick";
		String	TEXT_BOX_BACKGROUND			= PREFIX + "textBox.background";
		String	TEXT_BOX_BACKGROUND_EMPTY	= PREFIX + "textBox.background.empty";
		String	TEXT_BOX_TEXT				= PREFIX + "textBox.text";
		String	TEXT_BOX_TEXT_EMPTY			= PREFIX + "textBox.text.empty";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The position of the buttons relative to the text box. */
	private	ButtonPos				buttonPos;

	/** The orientation of the button icons. */
	private	Orientation				arrowOrientation;

	/** The horizontal alignment of the text in the text box. */
	private	HPos					textAlignment;

	/** Flag: if {@code true}, the buttons cycle through the range of values. */
	private	boolean					cyclic;

	/** The minimum value. */
	private	int						minValue;

	/** The maximum value. */
	private	int						maxValue;

	/** The current value. */
	private	SimpleIntegerProperty	value;

	/** The procedure that updates the UI when {@link #value} changes. */
	private	IProcedure1<Integer>	valueUpdater;

	/** The function that creates a string representation of a value. */
	private	IntFunction<String>		converter;

	/** The background colour of the text box. */
	private	Color					textBoxBackgroundColour;

	/** The text colour of this spinner. */
	private	Color					textColour;

	/** The background node of the {@linkplain #textBox text box}. */
	private	Rectangle				textBoxBackground;

	/** The text node of the {@linkplain #textBox text box}. */
	private	Text2					textNode;

	/** The text box. */
	private	Group					textBox;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(IntRangeSpinner.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of an integer-range spinner.
	 *
	 * @param buttonPos
	 *          the position of the spinner's buttons relative to the text box.
	 * @param arrowOrientation
	 *          the orientation of the button icons.
	 * @param textAlignment
	 *          the horizontal alignment of the text in the text box.
	 * @param cyclic
	 *          <ul>
	 *            <li>
	 *              if {@code true}, the buttons cycle through the range of values: when the minimum value is selected,
	 *              the <i>decrement</i> button selects the maximum value; when the maximum value is selected, the
	 *              <i>increment</i> button selects the minimum value.
	 *            </li>
	 *            <li>
	 *              if {@code false}, the minimum and maximum values are end stops: the <i>decrement</i> button is
	 *              disabled when the minimum value is selected and the <i>increment</i> button is disabled when the
	 *              maximum value is selected.
	 *            </li>
	 *          </ul>
	 */

	public IntRangeSpinner(
		ButtonPos	buttonPos,
		Orientation	arrowOrientation,
		HPos		textAlignment,
		boolean		cyclic)
	{
		// Validate arguments
		if (buttonPos == null)
			throw new IllegalArgumentException("Null button position");
		if (arrowOrientation == null)
			throw new IllegalArgumentException("Null arrow orientation");
		if (textAlignment == null)
			throw new IllegalArgumentException("Null text alignment");

		// Initialise instance variables
		this.buttonPos = buttonPos;
		this.arrowOrientation = arrowOrientation;
		this.textAlignment = textAlignment;
		this.cyclic = cyclic;
		value = new SimpleIntegerProperty();
		converter = Integer::toString;
		textBoxBackgroundColour = getColour(ColourKey.TEXT_BOX_BACKGROUND);
		textColour = getColour(ColourKey.TEXT_BOX_TEXT);

		// Set properties
		getStyleClass().add(StyleClass.INT_RANGE_SPINNER);

		// Update text and buttons when value changes
		value.addListener((observable, oldValue, newValue) ->
		{
			if (valueUpdater != null)
				valueUpdater.invoke(newValue.intValue());
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a spinner for the specified integer range.  Both buttons of the spinner are
	 * on the left of the text box, and the arrowhead icons of the buttons point left and right.
	 *
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the range of values: when the minimum value is selected,
	 *               the <i>decrement</i> button selects the maximum value; when the maximum value is selected, the
	 *               <i>increment</i> button selects the minimum value.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the minimum and maximum values are end stops: the <i>decrement</i> button is
	 *               disabled when the minimum value is selected and the <i>increment</i> button is disabled when the
	 *               maximum value is selected.
	 *             </li>
	 *           </ul>
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the values in the range [{@code minValue}
	 *           .. {@code maxValue}], using the string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given value that will be displayed in the text
	 *           box.  If it is {@code null}, the current converter will be used, or, if no converter has been set, the
	 *           default converter, {@link Integer#toString(int)}.
	 * @return a new instance of a spinner in which both buttons are on the left of the text box, and the arrowhead
	 *         icons of the buttons point left and right.
	 */

	public static IntRangeSpinner leftH(
		HPos				textAlignment,
		boolean				cyclic,
		int					minValue,
		int					maxValue,
		int					initialValue,
		String				prototypeText,
		IntFunction<String>	converter)
	{
		return new IntRangeSpinner(ButtonPos.LEFT, Orientation.HORIZONTAL, textAlignment, cyclic)
						.range(minValue, maxValue, prototypeText, converter)
						.value(initialValue);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified integer range.  Both buttons of the spinner are
	 * on the left of the text box, and the arrowhead icons of the buttons point down and up.
	 *
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the range of values: when the minimum value is selected,
	 *               the <i>decrement</i> button selects the maximum value; when the maximum value is selected, the
	 *               <i>increment</i> button selects the minimum value.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the minimum and maximum values are end stops: the <i>decrement</i> button is
	 *               disabled when the minimum value is selected and the <i>increment</i> button is disabled when the
	 *               maximum value is selected.
	 *             </li>
	 *           </ul>
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the values in the range [{@code minValue}
	 *           .. {@code maxValue}], using the string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given value that will be displayed in the text
	 *           box.  If it is {@code null}, the current converter will be used, or, if no converter has been set, the
	 *           default converter, {@link Integer#toString(int)}.
	 * @return a new instance of a spinner in which both buttons are on the left of the text box, and the arrowhead
	 *         icons of the buttons point down and up.
	 */

	public static IntRangeSpinner leftV(
		HPos				textAlignment,
		boolean				cyclic,
		int					minValue,
		int					maxValue,
		int					initialValue,
		String				prototypeText,
		IntFunction<String>	converter)
	{
		return new IntRangeSpinner(ButtonPos.LEFT, Orientation.VERTICAL, textAlignment, cyclic)
						.range(minValue, maxValue, prototypeText, converter)
						.value(initialValue);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified integer range.  Both buttons of the spinner are
	 * on the right of the text box, and the arrowhead icons of the buttons point left and right.
	 *
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the range of values: when the minimum value is selected,
	 *               the <i>decrement</i> button selects the maximum value; when the maximum value is selected, the
	 *               <i>increment</i> button selects the minimum value.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the minimum and maximum values are end stops: the <i>decrement</i> button is
	 *               disabled when the minimum value is selected and the <i>increment</i> button is disabled when the
	 *               maximum value is selected.
	 *             </li>
	 *           </ul>
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the values in the range [{@code minValue}
	 *           .. {@code maxValue}], using the string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given value that will be displayed in the text
	 *           box.  If it is {@code null}, the current converter will be used, or, if no converter has been set, the
	 *           default converter, {@link Integer#toString(int)}.
	 * @return a new instance of a spinner in which both buttons are on the right of the text box, and the arrowhead
	 *         icons of the buttons point left and right.
	 */

	public static IntRangeSpinner rightH(
		HPos				textAlignment,
		boolean				cyclic,
		int					minValue,
		int					maxValue,
		int					initialValue,
		String				prototypeText,
		IntFunction<String>	converter)
	{
		return new IntRangeSpinner(ButtonPos.RIGHT, Orientation.HORIZONTAL, textAlignment, cyclic)
						.range(minValue, maxValue, prototypeText, converter)
						.value(initialValue);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified integer range.  Both buttons of the spinner are
	 * on the right of the text box, and the arrowhead icons of the buttons point down and up.
	 *
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the range of values: when the minimum value is selected,
	 *               the <i>decrement</i> button selects the maximum value; when the maximum value is selected, the
	 *               <i>increment</i> button selects the minimum value.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the minimum and maximum values are end stops: the <i>decrement</i> button is
	 *               disabled when the minimum value is selected and the <i>increment</i> button is disabled when the
	 *               maximum value is selected.
	 *             </li>
	 *           </ul>
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the values in the range [{@code minValue}
	 *           .. {@code maxValue}], using the string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given value that will be displayed in the text
	 *           box.  If it is {@code null}, the current converter will be used, or, if no converter has been set, the
	 *           default converter, {@link Integer#toString(int)}.
	 * @return a new instance of a spinner in which both buttons are on the right of the text box, and the arrowhead
	 *         icons of the buttons point down and up.
	 */

	public static IntRangeSpinner rightV(
		HPos				textAlignment,
		boolean				cyclic,
		int					minValue,
		int					maxValue,
		int					initialValue,
		String				prototypeText,
		IntFunction<String>	converter)
	{
		return new IntRangeSpinner(ButtonPos.RIGHT, Orientation.VERTICAL, textAlignment, cyclic)
						.range(minValue, maxValue, prototypeText, converter)
						.value(initialValue);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified integer range.  The <i>decrement</i> button is
	 * on the left of the text box, the <i>increment</i> button is on the right of the text box, and the arrowhead icons
	 * of the buttons point left and right.
	 *
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the range of values: when the minimum value is selected,
	 *               the <i>decrement</i> button selects the maximum value; when the maximum value is selected, the
	 *               <i>increment</i> button selects the minimum value.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the minimum and maximum values are end stops: the <i>decrement</i> button is
	 *               disabled when the minimum value is selected and the <i>increment</i> button is disabled when the
	 *               maximum value is selected.
	 *             </li>
	 *           </ul>
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the values in the range [{@code minValue}
	 *           .. {@code maxValue}], using the string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given value that will be displayed in the text
	 *           box.  If it is {@code null}, the current converter will be used, or, if no converter has been set, the
	 *           default converter, {@link Integer#toString(int)}.
	 * @return a new instance of a spinner in which the <i>decrement</i> button is on the left of the text box, the
	 *         <i>increment</i> button is on the right of the text box, and the arrowhead icons of the buttons point
	 *         left and right.
	 */

	public static IntRangeSpinner leftRightH(
		HPos				textAlignment,
		boolean				cyclic,
		int					minValue,
		int					maxValue,
		int					initialValue,
		String				prototypeText,
		IntFunction<String>	converter)
	{
		return new IntRangeSpinner(ButtonPos.LEFT_RIGHT, Orientation.HORIZONTAL, textAlignment, cyclic)
						.range(minValue, maxValue, prototypeText, converter)
						.value(initialValue);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified integer range.  The <i>decrement</i> button is
	 * on the left of the text box, the <i>increment</i> button is on the right of the text box, and the arrowhead icons
	 * of the buttons point down and up.
	 *
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the range of values: when the minimum value is selected,
	 *               the <i>decrement</i> button selects the maximum value; when the maximum value is selected, the
	 *               <i>increment</i> button selects the minimum value.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the minimum and maximum values are end stops: the <i>decrement</i> button is
	 *               disabled when the minimum value is selected and the <i>increment</i> button is disabled when the
	 *               maximum value is selected.
	 *             </li>
	 *           </ul>
	 * @param  minValue
	 *           the minimum value of the spinner.
	 * @param  maxValue
	 *           the maximum value of the spinner.
	 * @param  initialValue
	 *           the initial value of the spinner.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the values in the range [{@code minValue}
	 *           .. {@code maxValue}], using the string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given value that will be displayed in the text
	 *           box.  If it is {@code null}, the current converter will be used, or, if no converter has been set, the
	 *           default converter, {@link Integer#toString(int)}.
	 * @return a new instance of a spinner in which the <i>decrement</i> button is on the left of the text box, the
	 *         <i>increment</i> button is on the right of the text box, and the arrowhead icons of the buttons point
	 *         down and up.
	 */

	public static IntRangeSpinner leftRightV(
		HPos				textAlignment,
		boolean				cyclic,
		int					minValue,
		int					maxValue,
		int					initialValue,
		String				prototypeText,
		IntFunction<String>	converter)
	{
		return new IntRangeSpinner(ButtonPos.LEFT_RIGHT, Orientation.VERTICAL, textAlignment, cyclic)
						.range(minValue, maxValue, prototypeText, converter)
						.value(initialValue);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the colour that is associated with the specified key in the colour map of the current theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the current theme of the style
	 *         manager, or {@link StyleManager#DEFAULT_COLOUR} if there is no such colour.
	 */

	private static Color getColour(
		String	key)
	{
		return StyleManager.INSTANCE.getColourOrDefault(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public void requestFocus()
	{
		textBox.requestFocus();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the current value of this spinner.
	 *
	 * @return the current value of this spinner.
	 */

	public int getValue()
	{
		return value.get();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the current value of this spinner to the specified value.
	 *
	 * @param value
	 *          the value to which the current value of this spinner will be set.
	 */

	public void setValue(
		int	value)
	{
		this.value.set(Math.min(Math.max(minValue, value), maxValue));
	}

	//------------------------------------------------------------------

	/**
	 * Sets the current value of this spinner to the specified value.
	 *
	 * @param  value
	 *           the value to which the current value of this spinner will be set.
	 * @return this spinner.
	 */

	public IntRangeSpinner value(
		int	value)
	{
		// Update value
		setValue(value);

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the value of this spinner as a read-only property.
	 *
	 * @return the value of this spinner as a read-only property.
	 */

	public ReadOnlyIntegerProperty valueProperty()
	{
		return value;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the minimum and maximum values of this spinner.  This method recreates the tree of JavaFX nodes of which
	 * this spinner is the root.  The current converter will be used to create a string representation of a value of the
	 * spinner, and the widest of those string representations will determine the width of the spinner.
	 *
	 * @param minValue
	 *          the minimum value.
	 * @param maxValue
	 *          the maximum value.
	 */

	public void setRange(
		int	minValue,
		int	maxValue)
	{
		setRange(minValue, maxValue, null, null);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the minimum and maximum values of this spinner.  This method recreates the tree of JavaFX nodes of which
	 * this spinner is the root.
	 *
	 * @param minValue
	 *          the minimum value.
	 * @param maxValue
	 *          the maximum value.
	 * @param prototypeText
	 *          the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *          the spinner is based on the widest string representation of the values in the range [{@code minValue} ..
	 *          {@code maxValue}], using the string representations that are returned by {@code converter}.
	 * @param converter
	 *          the function that returns the string representation of a given value that will be displayed in the text
	 *          box.  If it is {@code null}, the current converter will be used, or, if no converter has been set, the
	 *          default converter, {@link Integer#toString(int)}.
	 */

	public void setRange(
		int					minValue,
		int					maxValue,
		String				prototypeText,
		IntFunction<String>	converter)
	{
		// Validate arguments
		if (minValue > maxValue)
			throw new IllegalArgumentException(MIN_MAX_OUT_OF_ORDER_STR);

		// Update instance variables
		this.minValue = minValue;
		this.maxValue = maxValue;
		if (converter != null)
			this.converter = converter;

		// Determine width of text
		double textWidth = 0.0;
		if (prototypeText == null)
		{
			for (int value = maxValue; value >= minValue; value--)
			{
				double width = TextUtils.textWidth(this.converter.apply(value));
				if (textWidth < width)
					textWidth = width;
			}
		}
		else
			textWidth = TextUtils.textWidth(prototypeText);

		// Create text node
		textNode = Text2.createCentred("");
		textNode.setFill(textColour);
		textNode.getStyleClass().add(StyleClass.TEXT);

		// Create background of text box
		double textHeight = textNode.getHeight();
		double boxWidth = Math.ceil(textWidth + 2.0 * TEXT_BOX_H_INSET);
		double boxHeight = Math.ceil(textHeight + 2.0 * TEXT_BOX_V_INSET);
		textBoxBackground = new Rectangle(boxWidth, boxHeight, textBoxBackgroundColour);
		textBoxBackground.setStrokeWidth(0.0);
		textBoxBackground.getStyleClass().add(StyleClass.TEXT_BOX);

		// Create text box
		textBox = new Group(textBoxBackground, textNode);
		textBox.setFocusTraversable(true);

		// Set initial position of text within text box
		textNode.relocate(TEXT_BOX_H_INSET, TEXT_BOX_V_INSET);

		// Create focus-indicator border of button
		List<Rectangle> textBoxFocusedBorder = ShapeUtils.createFocusBorder(boxWidth, boxHeight);

		// Create pop-up for list view
		Popup popUp = new Popup();
		popUp.setAutoHide(true);

		// Create list view
		double cellHeight = TextUtils.textHeight() + 2.0 * LIST_VIEW_CELL_VERTICAL_PADDING + 1.0;
		ListView<String> listView = new ListView<>();
		listView.setFixedCellSize(cellHeight);
		listView.setCellFactory(listView0 ->
		{
			// Create cell
			ListCell<String> cell = new ListCell<>()
			{
				Group	marker;
				Shape	blank;

				// Constructor
				{
					// Initialise instance variables
					Shape tickIcon = Shapes.tick01(LIST_VIEW_TICK_ICON_SIZE);
					tickIcon.setStroke(getColour(ColourKey.LIST_VIEW_TICK));
					tickIcon.getStyleClass().add(StyleClass.LIST_VIEW_TICK);
					marker = Shapes.tile(tickIcon);
					Bounds bounds = marker.getLayoutBounds();
					blank = new Rectangle(bounds.getWidth(), bounds.getHeight(), Color.TRANSPARENT);

					// Set properties
					setGraphicTextGap(LIST_VIEW_CELL_GRAPHIC_TEXT_GAP);
				}

				@Override
				protected void updateItem(
					String	item,
					boolean	empty)
				{
					// Call superclass method
					super.updateItem(item, empty);

					// Set graphic
					setGraphic((empty || (getIndex() != getValue() - minValue)) ? blank : marker);

					// Set text
					setText(empty ? null : item);
				}
			};

			// Set 'selected' pseudo-class of cell if it is selected or mouse is hovering over it
			cell.getPseudoClassStates().addListener((InvalidationListener) observable ->
			{
				boolean selected =
						!cell.isEmpty() && (listView.getSelectionModel().getSelectedIndex() == cell.getIndex());
				boolean hovered =
						cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass(FxPseudoClass.HOVERED));
				cell.pseudoClassStateChanged(PseudoClass.getPseudoClass(FxPseudoClass.SELECTED), selected || hovered);
			});

			// Return cell
			return cell;
		});

		// Set items on list view
		List<String> items = IntStream.range(minValue, maxValue + 1).mapToObj(this.converter).toList();
		listView.setItems(FXCollections.observableList(items));

		// Create procedure to update value of spinner
		IProcedure0 updateValue = () ->
		{
			// Update value of spinner if item was selected in list view
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index >= 0)
				setValue(minValue + index);

			// Hide pop-up
			popUp.hide();
		};

		// Handle 'key pressed' event on list view
		listView.setOnKeyPressed(event ->
		{
			// Update value
			if (event.getCode() == KeyCode.ENTER)
				updateValue.invoke();

			// Hide pop-up
			else if (event.getCode() == KeyCode.ESCAPE)
				popUp.hide();

			// Consume event
			event.consume();
		});

		// Update value if mouse is clicked on list view
		listView.setOnMouseClicked(event ->
		{
			if (event.getButton() == MouseButton.PRIMARY)
			{
				// Update value
				updateValue.invoke();

				// Consume event
				event.consume();
			}
		});

		// Set content of pop-up
		popUp.getContent().add(listView);

		// Create procedure to display list view in pop-up window
		IProcedure0 showListView = () ->
		{
			// Get screen bounds of this spinner
			Bounds bounds = localToScreen(getLayoutBounds());

			// Set preferred size of list view
			int numValues = maxValue - minValue + 1;
			int numRows = Math.min(Math.max(1, numValues), LIST_VIEW_NUM_ROWS);
			double height = (double)numRows * cellHeight + 2.0;
			listView.setPrefSize(bounds.getWidth() - 2.0, height);

			// Redraw cells of list view
			listView.refresh();

			// Display pop-up
			popUp.show(this, bounds.getMinX(), bounds.getMaxY() - 1.0);

			// Select spinner value in list view
			int index = getValue() - minValue;
			listView.getSelectionModel().clearAndSelect(index);
			if (numRows < numValues)
				ListViewUtils.scrollToCentred(listView, index);
		};

		// Get dimensions of arrowhead
		ButtonInfo buttonInfo = BUTTON_INFOS.get(arrowOrientation);
		double arrowHeight = textHeight * buttonInfo.heightFactor;
		double arrowWidth = Math.rint(buttonInfo.widthFactor * textHeight);

		// Create left arrowhead
		Polygon leftArrow = null;
		switch (arrowOrientation)
		{
			case HORIZONTAL:
				leftArrow = new Polygon(arrowWidth, 0.0, arrowWidth, arrowHeight, 0.0, 0.5 * arrowHeight);
				break;

			case VERTICAL:
				leftArrow = new Polygon(0.0, 0.0, arrowWidth, 0.0, 0.5 * arrowWidth, arrowHeight);
				break;
		}
		leftArrow.setFill(getColour(ColourKey.BUTTON_ICON));
		leftArrow.getStyleClass().add(StyleClass.ARROWHEAD);

		// Create right arrowhead
		Polygon rightArrow = null;
		switch (arrowOrientation)
		{
			case HORIZONTAL:
				rightArrow = new Polygon(0.0, 0.0, 0.0, arrowHeight, arrowWidth, 0.5 * arrowHeight);
				break;

			case VERTICAL:
				rightArrow = new Polygon(0.0, arrowHeight, arrowWidth, arrowHeight, 0.5 * arrowWidth, 0.0);
				break;
		}
		rightArrow.setFill(getColour(ColourKey.BUTTON_ICON));
		rightArrow.getStyleClass().add(StyleClass.ARROWHEAD);

		// Get dimensions of button
		double buttonWidth = arrowWidth + 2.0 * buttonInfo.horizontalInset;
		double buttonHeight = boxHeight;

		// Create factory for button
		IFunction2<Group, Polygon, Integer> buttonFactory = (arrow, increment) ->
		{
			// Create background of button
			Rectangle background = new Rectangle(buttonWidth, buttonHeight, getColour(ColourKey.BUTTON_BACKGROUND));
			background.setStrokeWidth(0.0);
			background.getStyleClass().add(StyleClass.BUTTON);

			// Create focus-indicator border of button
			List<Rectangle> focusedBorder = ShapeUtils.createFocusBorder(buttonWidth, buttonHeight);

			// Create button
			Group button = new Group(background, arrow);
			button.setFocusTraversable(true);
			arrow.relocate(buttonInfo.horizontalInset, 0.5 * (buttonHeight - arrowHeight));

			// Create timer to update value of spinner after interval
			UpdateTimer updateTimer = new UpdateTimer(increment);

			// Create procedure to start updating value of spinner
			IProcedure0 startUpdatingValue = () ->
			{
				// Stop updating value
				updateTimer.stop();

				// Highlight background and border of button
				background.pseudoClassStateChanged(PRESSED_PSEUDO_CLASS, true);
				background.setFill(getColour(ColourKey.BUTTON_BACKGROUND_PRESSED));

				// Start timer to update value
				updateTimer.start();
			};

			// Create procedure to stop updating value of spinner
			IProcedure0 stopUpdatingValue = () ->
			{
				// Stop updating value
				updateTimer.stop();

				// Restore normal background and border of button
				background.pseudoClassStateChanged(PRESSED_PSEUDO_CLASS, false);
				background.setFill(getColour(ColourKey.BUTTON_BACKGROUND));
			};

			// Handle 'mouse pressed' event
			button.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
			{
				// If pop-up is showing, hide it ...
				if (popUp.isShowing())
					popUp.hide();

				// ... otherwise, if primary button pressed, start timer to update value
				else if (event.getButton() == MouseButton.PRIMARY)
				{
					// Start updating value
					startUpdatingValue.invoke();

					// Consume event
					event.consume();
				}

				// Request focus
				button.requestFocus();
			});

			// Handle 'mouse released' event
			button.addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
			{
				if (event.getButton() == MouseButton.PRIMARY)
				{
					// Stop updating value
					stopUpdatingValue.invoke();

					// Consume event
					event.consume();
				}
			});

			// Handle 'key pressed' event
			button.addEventHandler(KeyEvent.KEY_PRESSED, event ->
			{
				if (event.getCode() == KeyCode.SPACE)
				{
					// If Ctrl is pressed, show list-view pop-up ...
					if (event.isControlDown())
					{
						// Stop updating value
						stopUpdatingValue.invoke();

						// Show list-view pop-up
						showListView.invoke();
					}

					// ... otherwise, start updating value
					else if (!button.isDisabled() && !updateTimer.running)
						startUpdatingValue.invoke();

					// Consume event
					event.consume();
				}
			});

			// Handle 'key released' event
			button.addEventHandler(KeyEvent.KEY_RELEASED, event ->
			{
				if (event.getCode() == KeyCode.SPACE)
				{
					// Stop updating value
					stopUpdatingValue.invoke();

					// Consume event
					event.consume();
				}
			});

			// Add or remove focus-indicator border when button gains or loses focus
			button.focusedProperty().addListener((observable, oldFocused, focused) ->
			{
				// If focused, add focus-indicator border ...
				if (focused)
					button.getChildren().addAll(1, focusedBorder);

				// ... otherwise, remove focus-indicator border
				else
				{
					// Stop updating value
					stopUpdatingValue.invoke();

					// Remove focus-indicator border
					button.getChildren().removeAll(focusedBorder);
				}
			});

			// Update button when it is enabled or disabled
			button.disabledProperty().addListener((observable, oldDisabled, disabled) ->
			{
				// If button is disabled, stop updating value
				if (disabled)
					stopUpdatingValue.invoke();

				// Update opacity
				button.setOpacity(disabled ? DISABLED_OPACITY : 1.0);
			});

			// Return button
			return button;
		};

		// Create frame
		Rectangle frame = new Rectangle(boxWidth + 2.0 * buttonWidth + 4.0, boxHeight + 2.0, Color.TRANSPARENT);
		frame.setStroke(getColour(ColourKey.FRAME));
		frame.setStrokeType(StrokeType.INSIDE);
		frame.getStyleClass().add(StyleClass.FRAME);

		// Create left button
		Group leftButton = buttonFactory.invoke(leftArrow, -1);

		// Create right button
		Group rightButton = buttonFactory.invoke(rightArrow, 1);

		// Create first separator
		Line separator1 = new Line(0.0, 1.0, 0.0, boxHeight + 1.0);
		separator1.setStroke(getColour(ColourKey.FRAME));
		separator1.setStrokeLineCap(StrokeLineCap.BUTT);
		separator1.getStyleClass().add(StyleClass.FRAME);

		// Create second separator
		Line separator2 = new Line(0.0, 1.0, 0.0, boxHeight + 1.0);
		separator2.setStroke(getColour(ColourKey.FRAME));
		separator2.setStrokeLineCap(StrokeLineCap.BUTT);
		separator2.getStyleClass().add(StyleClass.FRAME);

		// Add children
		getChildren().setAll(frame, separator1, separator2);
		switch (buttonPos)
		{
			case LEFT:
				getChildren().addAll(leftButton, rightButton, textBox);
				break;

			case RIGHT:
				getChildren().addAll(textBox, leftButton, rightButton);
				break;

			case LEFT_RIGHT:
				getChildren().addAll(leftButton, textBox, rightButton);
				break;
		}

		// Set location of children
		double x = 1.0;
		switch (buttonPos)
		{
			case LEFT:
				leftButton.relocate(x, 1.0);
				x += buttonWidth;

				separator1.setStartX(x + 0.5);
				separator1.setEndX(x + 0.5);
				x += 1.0;

				rightButton.relocate(x, 1.0);
				x += buttonWidth;

				separator2.setStartX(x + 0.5);
				separator2.setEndX(x + 0.5);
				x += 1.0;

				textBox.relocate(x, 1.0);
				break;

			case RIGHT:
				textBox.relocate(x, 1.0);
				x += boxWidth;

				separator1.setStartX(x + 0.5);
				separator1.setEndX(x + 0.5);
				x += 1.0;

				leftButton.relocate(x, 1.0);
				x += buttonWidth;

				separator2.setStartX(x + 0.5);
				separator2.setEndX(x + 0.5);
				x += 1.0;

				rightButton.relocate(x, 1.0);
				break;

			case LEFT_RIGHT:
				leftButton.relocate(x, 1.0);
				x += buttonWidth;

				separator1.setStartX(x + 0.5);
				separator1.setEndX(x + 0.5);
				x += 1.0;

				textBox.relocate(x, 1.0);
				x += boxWidth;

				separator2.setStartX(x + 0.5);
				separator2.setEndX(x + 0.5);
				x += 1.0;

				rightButton.relocate(x, 1.0);
				break;
		}

		// Show or hide list-view pop-up when primary mouse button is clicked on text box
		textBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
		{
			// If list-view pop-up is hidden, request focus on text box
			if (!popUp.isShowing())
				textBox.requestFocus();

			// Show or hide list-view pop-up
			if (event.getButton() == MouseButton.PRIMARY)
			{
				// Show or hide pop-up
				if (popUp.isShowing())
					popUp.hide();
				else
					showListView.invoke();

				// Consume event
				event.consume();
			}
		});

		// Show or hide list-view pop-up when space key is pressed on text box
		textBox.addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			if (event.getCode() == KeyCode.SPACE)
			{
				// Show or hide pop-up
				if (popUp.isShowing())
					popUp.hide();
				else
					showListView.invoke();

				// Consume event
				event.consume();
			}
		});

		// Add or remove focus-indicator border when text box gains or loses focus
		textBox.focusedProperty().addListener((observable, oldFocused, focused) ->
		{
			// If focused, add focus-indicator border ...
			if (focused)
				textBox.getChildren().addAll(1, textBoxFocusedBorder);

			// ... otherwise, remove focus-indicator border
			else
				textBox.getChildren().removeAll(textBoxFocusedBorder);
		});

		// Update opacity of spinner when it is enabled or disabled
		disabledProperty().addListener((observable, oldDisabled, disabled) ->
				setOpacity(disabled ? DISABLED_OPACITY : 1.0));

		// Update procedure to update UI when value of spinner changes
		valueUpdater = value ->
		{
			// Update text
			textNode.setText(this.converter.apply(value));

			// Set location of text within text box
			switch (textAlignment)
			{
				case LEFT:
					// do nothing
					break;

				case CENTER:
					textNode.setLayoutX(0.5 * (boxWidth - textNode.getWidth()));
					break;

				case RIGHT:
					textNode.setLayoutX(boxWidth - textNode.getWidth() - TEXT_BOX_H_INSET);
					break;
			}

			// Update buttons
			if (!cyclic)
			{
				leftButton.setDisable(value == minValue);
				rightButton.setDisable(value == maxValue);
			}
		};

		// Set initial value
		if (getValue() == minValue)
			valueUpdater.invoke(minValue);
		else
			value.set(minValue);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the minimum and maximum values of this spinner.  This method recreates the tree of JavaFX nodes of which
	 * this spinner is the root.  The current converter will be used to create a string representation of a value of the
	 * spinner, and the widest of those string representations will determine the width of the spinner.
	 *
	 * @param  minValue
	 *           the minimum value.
	 * @param  maxValue
	 *           the maximum value.
	 * @return this spinner.
	 */

	public IntRangeSpinner range(
		int	minValue,
		int	maxValue)
	{
		return range(minValue, maxValue, null, null);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the minimum and maximum values of this spinner.  This method recreates the tree of JavaFX nodes of which
	 * this spinner is the root.
	 *
	 * @param  minValue
	 *           the minimum value.
	 * @param  maxValue
	 *           the maximum value.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the values in the range [{@code minValue}
	 *           .. {@code maxValue}], using the string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given value that will be displayed in the text
	 *           box.  If it is {@code null}, the current converter will be used, or, if no converter has been set, the
	 *           default converter, {@link Integer#toString(int)}.
	 * @return this spinner.
	 */

	public IntRangeSpinner range(
		int					minValue,
		int					maxValue,
		String				prototypeText,
		IntFunction<String>	converter)
	{
		// Set range
		setRange(minValue, maxValue, prototypeText, converter);

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background colour of the text box of this spinner to the specified value.
	 *
	 * @param  colour
	 *           the value to which the background colour of the text box of this spinner will be set.
	 * @return this spinner.
	 */

	public IntRangeSpinner textBoxBackgroundColour(
		Color	colour)
	{
		// Update instance variable
		textBoxBackgroundColour = colour;

		// Update UI
		textBoxBackground.setFill(colour);

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the text colour of this spinner to the specified value.
	 *
	 * @param  colour
	 *           the value to which the text colour of this spinner will be set.
	 * @return this spinner.
	 */

	public IntRangeSpinner textColour(
		Color	colour)
	{
		// Update instance variable
		textColour = colour;

		// Update UI
		textNode.setFill(colour);

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Sets an empty range on this spinner.  The spinner has no buttons: it consists of a text box that contains the
	 * specified text, and its value is fixed at zero.  This method is intended to be used only by subclasses.
	 *
	 * @param text
	 *          the text that will be displayed in the spinner.
	 */

	protected void setEmptyRange(
		String	text)
	{
		// Update instance variables
		minValue = maxValue = 0;

		// Create text node
		textNode = Text2.createCentred(text);
		textNode.setFill(getColour(ColourKey.TEXT_BOX_TEXT_EMPTY));
		textNode.getStyleClass().add(StyleClass.TEXT);
		textNode.pseudoClassStateChanged(EMPTY_PSEUDO_CLASS, true);

		// Create background of text box
		double boxWidth = Math.ceil(textNode.getWidth() + 2.0 * TEXT_BOX_H_INSET);
		double boxHeight = Math.ceil(textNode.getHeight() + 2.0 * TEXT_BOX_V_INSET);
		textBoxBackground = new Rectangle(boxWidth, boxHeight, getColour(ColourKey.TEXT_BOX_BACKGROUND_EMPTY));
		textBoxBackground.setStrokeWidth(0.0);
		textBoxBackground.getStyleClass().add(StyleClass.TEXT_BOX);
		textBoxBackground.pseudoClassStateChanged(EMPTY_PSEUDO_CLASS, true);

		// Create text box
		Group textBox = new Group(textBoxBackground, textNode);
		textBox.setLayoutX(1.0);
		textBox.setLayoutY(1.0);

		// Set initial position of text within text box
		textNode.relocate(TEXT_BOX_H_INSET, TEXT_BOX_V_INSET);

		// Create frame
		Rectangle frame = new Rectangle(boxWidth + 2.0, boxHeight + 2.0, Color.TRANSPARENT);
		frame.setStroke(getColour(ColourKey.FRAME));
		frame.setStrokeType(StrokeType.INSIDE);
		frame.getStyleClass().add(StyleClass.FRAME);

		// Set children
		getChildren().setAll(frame, textBox);

		// Set initial value
		value.set(minValue);
	}

	//------------------------------------------------------------------

	/**
	 * Sets an empty range on this spinner.  The spinner has no buttons: it consists of a text box that contains the
	 * specified text, and its value is fixed at zero.  This method is intended to be used only by subclasses.
	 *
	 * @param  text
	 *           the text that will be displayed in the spinner.
	 * @return this spinner.
	 */

	protected IntRangeSpinner emptyRange(
		String	text)
	{
		// Set range
		setEmptyRange(text);

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: BUTTON INFORMATION


	/**
	 * This record encapsulates information about the buttons of a spinner.
	 *
	 * @param heightFactor
	 *          the factor by which the height of the text in the text box is multiplied to yield the height of the
	 *          arrowhead icon of a button.
	 * @param widthFactor
	 *          the factor by which the height of the text in the text box is multiplied to yield the width of the
	 *          arrowhead icon of a button.
	 * @param horizontalInset
	 *          the amount by which the arrowhead icon of a button is inset from the left and right sides of the button.
	 */

	private record ButtonInfo(
		double	heightFactor,
		double	widthFactor,
		double	horizontalInset)
	{ }

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: TIMER FOR UPDATING VALUE OF SPINNER


	private class UpdateTimer
		extends AnimationTimer
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The time (in nanoseconds) by which the interval between updates of the spinner value is reduced after each
			update, until the {@linkplain #MIN_INTERVAL minimum interval} is reached. */
		private static final	long	DELTA_INTERVAL	= 75_000_000;

		/** The maximum interval (in nanoseconds) between updates of the spinner value. */
		private static final	long	MAX_INTERVAL	= 425_000_000;

		/** The minimum interval (in nanoseconds) between updates of the spinner value. */
		private static final	long	MIN_INTERVAL	= MAX_INTERVAL - 4 * DELTA_INTERVAL;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int		increment;
		private	long	updateTime;
		private	long	interval;
		private	boolean	running;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private UpdateTimer(
			int	increment)
		{
			// Initialise instance variables
			this.increment = increment;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public void start()
		{
			// Reset update time and interval
			updateTime = 0;
			interval = MAX_INTERVAL;

			// Set 'running' flag
			running = true;

			// Call superclass method
			super.start();
		}

		//--------------------------------------------------------------

		@Override
		public void stop()
		{
			// Call superclass method
			super.stop();

			// Clear 'running' flag
			running = false;
		}

		//--------------------------------------------------------------

		@Override
		public void handle(
			long	time)
		{
			// If update interval has elapsed, update value and schedule next update
			if (time >= updateTime)
			{
				// Update value
				int value = getValue() + increment;
				if (cyclic)
				{
					if (value < minValue)
						value = maxValue;
					else if (value > maxValue)
						value = minValue;
				}
				setValue(value);

				// Schedule next update
				updateTime = time + interval;

				// Decrement interval between updates
				if (interval > MIN_INTERVAL)
					interval -= DELTA_INTERVAL;
			}
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
