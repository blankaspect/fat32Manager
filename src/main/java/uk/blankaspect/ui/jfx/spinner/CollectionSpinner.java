/*====================================================================*\

CollectionSpinner.java

Class: collection spinner.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.spinner;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import java.util.function.Function;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;

//----------------------------------------------------------------------


// CLASS: COLLECTION SPINNER


/**
 * This class implements a non-editable spinner that allows the user to select an item from a {@linkplain Collection
 * collection}.  The user can move forwards and backwards through the items of the collection by means of a pair of
 * buttons, or an item may be selected from a drop-down list that is displayed when the primary mouse button is clicked
 * on the text box of the spinner.  The two buttons may be positioned in the following ways:
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
 *
 * @param <T>
 *          the type of the items of the collection.
 */

public class CollectionSpinner<T>
	extends IntRangeSpinner
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	String	DEFAULT_EMPTY_ITEMS_TEXT	= "No items";

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** A list of the items that may be selected. */
	private	List<T>					items;

	/** The currently selected item. */
	private	SimpleObjectProperty<T>	item;

	/** The function that returns the index of a given item.  It is used by {@link #setItem(Object)}. */
	private	Function<T, Integer>	indexer;

	/** The function that creates a string representation of a given item. */
	private	Function<T, String>		converter;

	/** The text that is displayed in a spinner whose collection of items is empty. */
	private	String					emptyItemsText;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a collection spinner.
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
	 *              if {@code true}, the buttons cycle through the items of the collection: when the first item is
	 *              selected, the <i>decrement</i> button selects the last item; when the last item is selected, the
	 *              <i>increment</i> button selects the first item.
	 *            </li>
	 *            <li>
	 *              if {@code false}, the first and last items of the collection are end stops: the <i>decrement</i>
	 *              button is disabled when the first item is selected and the <i>increment</i> button is disabled when
	 *              the last item is selected.
	 *            </li>
	 *          </ul>
	 */

	public CollectionSpinner(
		ButtonPos	buttonPos,
		Orientation	arrowOrientation,
		HPos		textAlignment,
		boolean		cyclic)
	{
		// Call superclass constructor
		super(buttonPos, arrowOrientation, textAlignment, cyclic);

		// Initialise instance variables
		items = new ArrayList<>();
		item = new SimpleObjectProperty<>();
		indexer = items::indexOf;
		converter = Object::toString;
		emptyItemsText = DEFAULT_EMPTY_ITEMS_TEXT;

		// Update item when value changes
		valueProperty().addListener((observable, oldValue, value) ->
		{
			int index = value.intValue();
			item.set(((index >= 0) && (index < items.size())) ? items.get(index) : null);
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates and returns a new instance of a spinner for the specified items.  Both buttons of the spinner are on the
	 * left of the text box, and the arrowhead icons of the buttons point left and right.
	 *
	 * @param  <T>
	 *           the type of the items of the collection.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the items of the collection: when the first item is
	 *               selected, the <i>decrement</i> button selects the last item; when the last item is selected, the
	 *               <i>increment</i> button selects the first item.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last items of the collection are end stops: the <i>decrement</i>
	 *               button is disabled when the first item is selected and the <i>increment</i> button is disabled when
	 *               the last item is selected.
	 *             </li>
	 *           </ul>
	 * @param  items
	 *           the items that may be selected in the spinner.
	 * @param  initialItem
	 *           the item that is initially selected.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of {@code items}, using the string
	 *           representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given item that will be displayed in the text
	 *           box.  If it is {@code null}, the default converter, {@link Object#toString()}, will be used.
	 * @return a new instance of a spinner in which both buttons are on the left of the text box, and the arrowhead
	 *         icons of the buttons point left and right.
	 */

	public static <T> CollectionSpinner<T> leftH(
		HPos					textAlignment,
		boolean					cyclic,
		Collection<? extends T>	items,
		T						initialItem,
		String					prototypeText,
		Function<T, String>		converter)
	{
		CollectionSpinner<T> spinner =
				new CollectionSpinner<T>(ButtonPos.LEFT, Orientation.HORIZONTAL, textAlignment, cyclic)
						.items(items, prototypeText, converter);
		return (initialItem == null) ? spinner : spinner.item(initialItem);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the constants of the specified {@code enum} type.  Both
	 * buttons of the spinner are on the left of the text box, and the arrowhead icons of the buttons point left and
	 * right.
	 *
	 * @param  <E>
	 *           the type of the elements of the enumeration.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the {@code enum} constants: when the first constant is
	 *               selected, the <i>decrement</i> button selects the last constant; when the last constant is
	 *               selected, the <i>increment</i> button selects the first constant.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last {@code enum} constants are end stops: the <i>decrement</i>
	 *               button is disabled when the first constant is selected and the <i>increment</i> button is disabled
	 *               when the last constant is selected.
	 *             </li>
	 *           </ul>
	 * @param  cls
	 *           the class of the {@code enum}.
	 * @param  initialConstant
	 *           the initial {@code enum} constant.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the {@code enum} constants, using the
	 *           string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given {@code enum} constant that will be
	 *           displayed in the text box.  If it is {@code null}, the default converter, {@link Object#toString()},
	 *           will be used.
	 * @return a new instance of a spinner in which both buttons are on the left of the text box, and the arrowhead
	 *         icons of the buttons point left and right.
	 */

	public static <E> CollectionSpinner<E> leftH(
		HPos				textAlignment,
		boolean				cyclic,
		Class<E>			cls,
		E					initialConstant,
		String				prototypeText,
		Function<E, String>	converter)
	{
		CollectionSpinner<E> spinner =
				new CollectionSpinner<E>(ButtonPos.LEFT, Orientation.HORIZONTAL, textAlignment, cyclic)
						.items(getEnumConstants(cls), prototypeText, converter);
		return (initialConstant == null) ? spinner : spinner.item(initialConstant);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified items.  Both buttons of the spinner are on the
	 * left of the text box, and the arrowhead icons of the buttons point down and up.
	 *
	 * @param  <T>
	 *           the type of the items of the collection.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the items of the collection: when the first item is
	 *               selected, the <i>decrement</i> button selects the last item; when the last item is selected, the
	 *               <i>increment</i> button selects the first item.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last items of the collection are end stops: the <i>decrement</i>
	 *               button is disabled when the first item is selected and the <i>increment</i> button is disabled when
	 *               the last item is selected.
	 *             </li>
	 *           </ul>
	 * @param  items
	 *           the items that may be selected in the spinner.
	 * @param  initialItem
	 *           the item that is initially selected.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of {@code items}, using the string
	 *           representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given item that will be displayed in the text
	 *           box.  If it is {@code null}, the default converter, {@link Object#toString()}, will be used.
	 * @return a new instance of a spinner in which both buttons are on the left of the text box, and the arrowhead
	 *         icons of the buttons point down and up.
	 */

	public static <T> CollectionSpinner<T> leftV(
		HPos					textAlignment,
		boolean					cyclic,
		Collection<? extends T>	items,
		T						initialItem,
		String					prototypeText,
		Function<T, String>		converter)
	{
		CollectionSpinner<T> spinner =
				new CollectionSpinner<T>(ButtonPos.LEFT, Orientation.VERTICAL, textAlignment, cyclic)
						.items(items, prototypeText, converter);
		return (initialItem == null) ? spinner : spinner.item(initialItem);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the constants of the specified {@code enum} type.  Both
	 * buttons of the spinner are on the left of the text box, and the arrowhead icons of the buttons point down and up.
	 *
	 * @param  <E>
	 *           the type of the elements of the enumeration.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the {@code enum} constants: when the first constant is
	 *               selected, the <i>decrement</i> button selects the last constant; when the last constant is
	 *               selected, the <i>increment</i> button selects the first constant.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last {@code enum} constants are end stops: the <i>decrement</i>
	 *               button is disabled when the first constant is selected and the <i>increment</i> button is disabled
	 *               when the last constant is selected.
	 *             </li>
	 *           </ul>
	 * @param  cls
	 *           the class of the {@code enum}.
	 * @param  initialConstant
	 *           the initial {@code enum} constant.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the {@code enum} constants, using the
	 *           string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given {@code enum} constant that will be
	 *           displayed in the text box.  If it is {@code null}, the default converter, {@link Object#toString()},
	 *           will be used.
	 * @return a new instance of a spinner in which both buttons are on the left of the text box, and the arrowhead
	 *         icons of the buttons point down and up.
	 */

	public static <E> CollectionSpinner<E> leftV(
		HPos				textAlignment,
		boolean				cyclic,
		Class<E>			cls,
		E					initialConstant,
		String				prototypeText,
		Function<E, String>	converter)
	{
		CollectionSpinner<E> spinner =
				new CollectionSpinner<E>(ButtonPos.LEFT, Orientation.VERTICAL, textAlignment, cyclic)
						.items(getEnumConstants(cls), prototypeText, converter);
		return (initialConstant == null) ? spinner : spinner.item(initialConstant);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified items.  Both buttons of the spinner are on the
	 * right of the text box, and the arrowhead icons of the buttons point left and right.
	 *
	 * @param  <T>
	 *           the type of the items of the collection.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the items of the collection: when the first item is
	 *               selected, the <i>decrement</i> button selects the last item; when the last item is selected, the
	 *               <i>increment</i> button selects the first item.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last items of the collection are end stops: the <i>decrement</i>
	 *               button is disabled when the first item is selected and the <i>increment</i> button is disabled when
	 *               the last item is selected.
	 *             </li>
	 *           </ul>
	 * @param  items
	 *           the items that may be selected in the spinner.
	 * @param  initialItem
	 *           the item that is initially selected.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of {@code items}, using the string
	 *           representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given item that will be displayed in the text
	 *           box.  If it is {@code null}, the default converter, {@link Object#toString()}, will be used.
	 * @return a new instance of a spinner in which both buttons are on the right of the text box, and the arrowhead
	 *         icons of the buttons point left and right.
	 */

	public static <T> CollectionSpinner<T> rightH(
		HPos					textAlignment,
		boolean					cyclic,
		Collection<? extends T>	items,
		T						initialItem,
		String					prototypeText,
		Function<T, String>		converter)
	{
		CollectionSpinner<T> spinner =
				new CollectionSpinner<T>(ButtonPos.RIGHT, Orientation.HORIZONTAL, textAlignment, cyclic)
						.items(items, prototypeText, converter);
		return (initialItem == null) ? spinner : spinner.item(initialItem);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the constants of the specified {@code enum} type.  Both
	 * buttons of the spinner are on the right of the text box, and the arrowhead icons of the buttons point left and
	 * right.
	 *
	 * @param  <E>
	 *           the type of the elements of the enumeration.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the {@code enum} constants: when the first constant is
	 *               selected, the <i>decrement</i> button selects the last constant; when the last constant is
	 *               selected, the <i>increment</i> button selects the first constant.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last {@code enum} constants are end stops: the <i>decrement</i>
	 *               button is disabled when the first constant is selected and the <i>increment</i> button is disabled
	 *               when the last constant is selected.
	 *             </li>
	 *           </ul>
	 * @param  cls
	 *           the class of the {@code enum}.
	 * @param  initialConstant
	 *           the initial {@code enum} constant.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the {@code enum} constants, using the
	 *           string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given {@code enum} constant that will be
	 *           displayed in the text box.  If it is {@code null}, the default converter, {@link Object#toString()},
	 *           will be used.
	 * @return a new instance of a spinner in which both buttons are on the right of the text box, and the arrowhead
	 *         icons of the buttons point left and right.
	 */

	public static <E> CollectionSpinner<E> rightH(
		HPos				textAlignment,
		boolean				cyclic,
		Class<E>			cls,
		E					initialConstant,
		String				prototypeText,
		Function<E, String>	converter)
	{
		CollectionSpinner<E> spinner =
				new CollectionSpinner<E>(ButtonPos.RIGHT, Orientation.HORIZONTAL, textAlignment, cyclic)
						.items(getEnumConstants(cls), prototypeText, converter);
		return (initialConstant == null) ? spinner : spinner.item(initialConstant);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified items.  Both buttons of the spinner are on the
	 * right of the text box, and the arrowhead icons of the buttons point down and up.
	 *
	 * @param  <T>
	 *           the type of the items of the collection.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the items of the collection: when the first item is
	 *               selected, the <i>decrement</i> button selects the last item; when the last item is selected, the
	 *               <i>increment</i> button selects the first item.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last items of the collection are end stops: the <i>decrement</i>
	 *               button is disabled when the first item is selected and the <i>increment</i> button is disabled when
	 *               the last item is selected.
	 *             </li>
	 *           </ul>
	 * @param  items
	 *           the items that may be selected in the spinner.
	 * @param  initialItem
	 *           the item that is initially selected.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of {@code items}, using the string
	 *           representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given item that will be displayed in the text
	 *           box.  If it is {@code null}, the default converter, {@link Object#toString()}, will be used.
	 * @return a new instance of a spinner in which both buttons are on the right of the text box, and the arrowhead
	 *         icons of the buttons point down and up.
	 */

	public static <T> CollectionSpinner<T> rightV(
		HPos					textAlignment,
		boolean					cyclic,
		Collection<? extends T>	items,
		T						initialItem,
		String					prototypeText,
		Function<T, String>		converter)
	{
		CollectionSpinner<T> spinner =
				new CollectionSpinner<T>(ButtonPos.RIGHT, Orientation.VERTICAL, textAlignment, cyclic)
						.items(items, prototypeText, converter);
		return (initialItem == null) ? spinner : spinner.item(initialItem);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the constants of the specified {@code enum} type.  Both
	 * buttons of the spinner are on the right of the text box, and the arrowhead icons of the buttons point down and
	 * up.
	 *
	 * @param  <E>
	 *           the type of the elements of the enumeration.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the {@code enum} constants: when the first constant is
	 *               selected, the <i>decrement</i> button selects the last constant; when the last constant is
	 *               selected, the <i>increment</i> button selects the first constant.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last {@code enum} constants are end stops: the <i>decrement</i>
	 *               button is disabled when the first constant is selected and the <i>increment</i> button is disabled
	 *               when the last constant is selected.
	 *             </li>
	 *           </ul>
	 * @param  cls
	 *           the class of the {@code enum}.
	 * @param  initialConstant
	 *           the initial {@code enum} constant.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the {@code enum} constants, using the
	 *           string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given {@code enum} constant that will be
	 *           displayed in the text box.  If it is {@code null}, the default converter, {@link Object#toString()},
	 *           will be used.
	 * @return a new instance of a spinner in which both buttons are on the right of the text box, and the arrowhead
	 *         icons of the buttons point down and up.
	 */

	public static <E> CollectionSpinner<E> rightV(
		HPos				textAlignment,
		boolean				cyclic,
		Class<E>			cls,
		E					initialConstant,
		String				prototypeText,
		Function<E, String>	converter)
	{
		CollectionSpinner<E> spinner =
				new CollectionSpinner<E>(ButtonPos.RIGHT, Orientation.VERTICAL, textAlignment, cyclic)
						.items(getEnumConstants(cls), prototypeText, converter);
		return (initialConstant == null) ? spinner : spinner.item(initialConstant);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified items.  The <i>decrement</i> button is on the
	 * left of the text box, the <i>increment</i> button is on the right of the text box, and the arrowhead icons of the
	 * buttons point left and right.
	 *
	 * @param  <T>
	 *           the type of the items of the collection.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the items of the collection: when the first item is
	 *               selected, the <i>decrement</i> button selects the last item; when the last item is selected, the
	 *               <i>increment</i> button selects the first item.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last items of the collection are end stops: the <i>decrement</i>
	 *               button is disabled when the first item is selected and the <i>increment</i> button is disabled when
	 *               the last item is selected.
	 *             </li>
	 *           </ul>
	 * @param  items
	 *           the items that may be selected in the spinner.
	 * @param  initialItem
	 *           the item that is initially selected.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of {@code items}, using the string
	 *           representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given item that will be displayed in the text
	 *           box.  If it is {@code null}, the default converter, {@link Object#toString()}, will be used.
	 * @return a new instance of a spinner in which the <i>decrement</i> button is on the left of the text box, the
	 *         <i>increment</i> button is on the right of the text box, and the arrowhead icons of the buttons point
	 *         left and right.
	 */

	public static <T> CollectionSpinner<T> leftRightH(
		HPos					textAlignment,
		boolean					cyclic,
		Collection<? extends T>	items,
		T						initialItem,
		String					prototypeText,
		Function<T, String>		converter)
	{
		CollectionSpinner<T> spinner =
				new CollectionSpinner<T>(ButtonPos.LEFT_RIGHT, Orientation.HORIZONTAL, textAlignment, cyclic)
						.items(items, prototypeText, converter);
		return (initialItem == null) ? spinner : spinner.item(initialItem);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the constants of the specified {@code enum} type.  The
	 * <i>decrement</i> button is on the left of the text box, the <i>increment</i> button is on the right of the text
	 * box, and the arrowhead icons of the buttons point left and right.
	 *
	 * @param  <E>
	 *           the type of the elements of the enumeration.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the {@code enum} constants: when the first constant is
	 *               selected, the <i>decrement</i> button selects the last constant; when the last constant is
	 *               selected, the <i>increment</i> button selects the first constant.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last {@code enum} constants are end stops: the <i>decrement</i>
	 *               button is disabled when the first constant is selected and the <i>increment</i> button is disabled
	 *               when the last constant is selected.
	 *             </li>
	 *           </ul>
	 * @param  cls
	 *           the class of the {@code enum}.
	 * @param  initialConstant
	 *           the initial {@code enum} constant.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the {@code enum} constants, using the
	 *           string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given {@code enum} constant that will be
	 *           displayed in the text box.  If it is {@code null}, the default converter, {@link Object#toString()},
	 *           will be used.
	 * @return a new instance of a spinner in which the <i>decrement</i> button is on the left of the text box, the
	 *         <i>increment</i> button is on the right of the text box, and the arrowhead icons of the buttons point
	 *         left and right.
	 */

	public static <E> CollectionSpinner<E> leftRightH(
		HPos				textAlignment,
		boolean				cyclic,
		Class<E>			cls,
		E					initialConstant,
		String				prototypeText,
		Function<E, String>	converter)
	{
		CollectionSpinner<E> spinner =
				new CollectionSpinner<E>(ButtonPos.LEFT_RIGHT, Orientation.HORIZONTAL, textAlignment, cyclic)
						.items(getEnumConstants(cls), prototypeText, converter);
		return (initialConstant == null) ? spinner : spinner.item(initialConstant);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the specified items.  The <i>decrement</i> button is on the
	 * left of the text box, the <i>increment</i> button is on the right of the text box, and the arrowhead icons of the
	 * buttons point down and up.
	 *
	 * @param  <T>
	 *           the type of the items of the collection.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the items of the collection: when the first item is
	 *               selected, the <i>decrement</i> button selects the last item; when the last item is selected, the
	 *               <i>increment</i> button selects the first item.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last items of the collection are end stops: the <i>decrement</i>
	 *               button is disabled when the first item is selected and the <i>increment</i> button is disabled when
	 *               the last item is selected.
	 *             </li>
	 *           </ul>
	 * @param  items
	 *           the items that may be selected in the spinner.
	 * @param  initialItem
	 *           the item that is initially selected.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of {@code items}, using the string
	 *           representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given item that will be displayed in the text
	 *           box.  If it is {@code null}, the default converter, {@link Object#toString()}, will be used.
	 * @return a new instance of a spinner in which the <i>decrement</i> button is on the left of the text box, the
	 *         <i>increment</i> button is on the right of the text box, and the arrowhead icons of the buttons point
	 *         down and up.
	 */

	public static <T> CollectionSpinner<T> leftRightV(
		HPos					textAlignment,
		boolean					cyclic,
		Collection<? extends T>	items,
		T						initialItem,
		String					prototypeText,
		Function<T, String>		converter)
	{
		CollectionSpinner<T> spinner =
				new CollectionSpinner<T>(ButtonPos.LEFT_RIGHT, Orientation.VERTICAL, textAlignment, cyclic)
						.items(items, prototypeText, converter);
		return (initialItem == null) ? spinner : spinner.item(initialItem);
	}

	//------------------------------------------------------------------

	/**
	 * Creates and returns a new instance of a spinner for the constants of the specified {@code enum} type.  The
	 * <i>decrement</i> button is on the left of the text box, the <i>increment</i> button is on the right of the text
	 * box, and the arrowhead icons of the buttons point down and up.
	 *
	 * @param  <E>
	 *           the type of the elements of the enumeration.
	 * @param  textAlignment
	 *           the horizontal alignment of the text in the text box.
	 * @param  cyclic
	 *           <ul>
	 *             <li>
	 *               if {@code true}, the buttons cycle through the {@code enum} constants: when the first constant is
	 *               selected, the <i>decrement</i> button selects the last constant; when the last constant is
	 *               selected, the <i>increment</i> button selects the first constant.
	 *             </li>
	 *             <li>
	 *               if {@code false}, the first and last {@code enum} constants are end stops: the <i>decrement</i>
	 *               button is disabled when the first constant is selected and the <i>increment</i> button is disabled
	 *               when the last constant is selected.
	 *             </li>
	 *           </ul>
	 * @param  cls
	 *           the class of the {@code enum}.
	 * @param  initialConstant
	 *           the initial {@code enum} constant.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of the {@code enum} constants, using the
	 *           string representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given {@code enum} constant that will be
	 *           displayed in the text box.  If it is {@code null}, the default converter, {@link Object#toString()},
	 *           will be used.
	 * @return a new instance of a spinner in which the <i>decrement</i> button is on the left of the text box, the
	 *         <i>increment</i> button is on the right of the text box, and the arrowhead icons of the buttons point
	 *         down and up.
	 */

	public static <E> CollectionSpinner<E> leftRightV(
		HPos				textAlignment,
		boolean				cyclic,
		Class<E>			cls,
		E					initialConstant,
		String				prototypeText,
		Function<E, String>	converter)
	{
		CollectionSpinner<E> spinner =
				new CollectionSpinner<E>(ButtonPos.LEFT_RIGHT, Orientation.VERTICAL, textAlignment, cyclic)
						.items(getEnumConstants(cls), prototypeText, converter);
		return (initialConstant == null) ? spinner : spinner.item(initialConstant);
	}

	//------------------------------------------------------------------

	/**
	 * Returns an unmodifiable list of the enumeration constants of the specified class, which is expected to be an
	 * {@code enum} class.
	 *
	 * @param  <E>
	 *           the type of the elements of the enumeration.
	 * @param  cls
	 *           the class whose enumeration constants are desired.
	 * @return an unmodifiable list of the enumeration constants of {@code cls}, which is empty if {@code cls} does not
	 *         denote an {@code enum} class.
	 */

	private static <E> List<E> getEnumConstants(
		Class<E>	cls)
	{
		E[] enumConstants = cls.getEnumConstants();
		return (enumConstants == null) ? Collections.emptyList() : List.of(enumConstants);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the item that is currently selected in this spinner.
	 *
	 * @return the item that is currently selected in this spinner, or {@code null} if the list of items is empty.
	 */

	public T getItem()
	{
		return items.isEmpty() ? null : item.get();
	}

	//------------------------------------------------------------------

	/**
	 * Selects the specified item in this spinner.
	 *
	 * @param item
	 *          the item that will be selected.
	 */

	public void setItem(
		T	item)
	{
		// Set value
		int oldValue = value();
		setValue(indexer.apply(item));

		// If value hasn't changed, set item
		if (value() == oldValue)
			this.item.set(item);
	}

	//------------------------------------------------------------------

	/**
	 * Selects the specified item in this spinner.
	 *
	 * @param  item
	 *           the item that will be selected.
	 * @return this spinner.
	 */

	public CollectionSpinner<T> item(
		T	item)
	{
		// Select item
		setItem(item);

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the item that is currently selected in this spinner as a read-only property.
	 *
	 * @return the item that is currently selected in this spinner as a read-only property.
	 */

	public ReadOnlyObjectProperty<T> itemProperty()
	{
		return item;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the items that may be selected by this spinner to the specified collection.  The default converter, {@link
	 * Object#toString(int)}, will be used to produce the string representation of an item that is displayed in the text
	 * box.
	 *
	 * @param items
	 *          the items that may be selected by this spinner.
	 */

	public void setItems(
		Collection<? extends T>	items)
	{
		setItems(items, null, T::toString);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the items that may be selected by this spinner to the specified collection.
	 *
	 * @param items
	 *          the items that may be selected by this spinner.
	 * @param prototypeText
	 *          the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *          the spinner is based on the widest string representation of {@code items}, using the string
	 *          representations that are returned by {@code converter}.
	 * @param converter
	 *          the function that returns the string representation of a given item that will be displayed in the text
	 *          box.  If it is {@code null}, the current converter will be used, or, if no converter has been set, the
	 *          default converter, {@link Object#toString(int)}.
	 */

	public void setItems(
		Collection<? extends T>	items,
		String					prototypeText,
		Function<T, String>		converter)
	{
		// Validate argument
		if (items == null)
			throw new IllegalArgumentException("Null items");

		// Update instance variables
		this.items.clear();
		this.items.addAll(items);
		if (converter != null)
			this.converter = converter;

		// Set index range
		if (items.isEmpty())
		{
			// Set range
			setEmptyRange(emptyItemsText, prototypeText);

			// Invalidate item
			item.set(null);
		}
		else
		{
			// Set range
			setRange(0, items.size() - 1, prototypeText, index -> this.converter.apply(this.items.get(index)));

			// Set item
			item.set(this.items.get(value()));
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns an unmodifiable list of the items that may be selected by this spinner.
	 *
	 * @return an unmodifiable list of the items that may be selected by this spinner.
	 */

	public List<T> items()
	{
		return Collections.unmodifiableList(items);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the items that may be selected by this spinner to the specified collection.
	 *
	 * @param  items
	 *           the items that may be selected by this spinner.
	 * @return this spinner.
	 */

	public CollectionSpinner<T> items(
		Collection<? extends T>	items)
	{
		return items(items, null, T::toString);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the items that may be selected by this spinner to the specified collection.
	 *
	 * @param  items
	 *           the items that may be selected by this spinner.
	 * @param  prototypeText
	 *           the text that will be used to determine the width of the spinner.  If it is {@code null}, the width of
	 *           the spinner is based on the widest string representation of {@code items}, using the string
	 *           representations that are returned by {@code converter}.
	 * @param  converter
	 *           the function that returns the string representation of a given item that will be displayed in the text
	 *           box.  If it is {@code null}, the default converter, {@link Object#toString()}, will be used.
	 * @return this spinner.
	 */

	public CollectionSpinner<T> items(
		Collection<? extends T>	items,
		String					prototypeText,
		Function<T, String>		converter)
	{
		// Set items
		setItems(items, prototypeText, converter);

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if this spinner has no items that may be selected.
	 *
	 * @return {@code true} if this spinner has no items that may be selected.
	 */

	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the function that returns the index of a given item to the specified value.  The function is used by {@link
	 * #setItem(Object)}.
	 *
	 * @param indexer
	 *          the function that will be used find the index of a given item.
	 */

	public void setIndexer(
		Function<T, Integer>	indexer)
	{
		// Validate argument
		if (indexer == null)
			throw new IllegalArgumentException("Null indexer");

		// Update instance variable
		this.indexer = indexer;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the function that returns the index of a given item to the specified value.  The function is used by {@link
	 * #setItem(Object)}.
	 *
	 * @param  indexer
	 *           the function that will be used find the index of a given item.
	 * @return this spinner.
	 */

	public CollectionSpinner<T> indexer(
		Function<T, Integer>	indexer)
	{
		// Set indexer
		setIndexer(indexer);

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the function that returns the index of a given item to use a test for identity when comparing items (ie,
	 * <i>item1</i> == <i>item2</i>).  The function is used by {@link #setItem(Object)}.
	 */

	public void setIdentityIndexer()
	{
		setIndexer(item ->
		{
			for (int i = 0, numItems = items.size(); i < numItems; i++)
			{
				if (items.get(i) == item)
					return i;
			}
			return -1;
		});
	}

	//------------------------------------------------------------------

	/**
	 * Sets the function that returns the index of a given item to use a test for identity when comparing items (ie,
	 * <i>item1</i> == <i>item2</i>).  The function is used by {@link #setItem(Object)}.
	 *
	 * @return this spinner.
	 */

	public CollectionSpinner<T> identityIndexer()
	{
		// Set identity indexer
		setIdentityIndexer();

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the text that is displayed in a spinner whose collection of items is empty.  The text that is set by this
	 * method will take effect the next time an empty collection of items is set with {@link #setItems(Collection)},
	 * {@link #setItems(Collection, String, Function)}, {@link #items(Collection)} or {@link #items(Collection, String,
	 * Function)}.
	 *
	 * @param text
	 *          the text that will be displayed in a spinner whose collection of items is empty.
	 */

	public void setEmptyItemsText(
		String	text)
	{
		// Validate argument
		if (text == null)
			throw new IllegalArgumentException("Null text");

		// Update instance variable
		emptyItemsText = text;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the text that is displayed in a spinner whose collection of items is empty.  The text that is set by this
	 * method will take effect the next time an empty collection of items is set with {@link #setItems(Collection)},
	 * {@link #setItems(Collection, String, Function)}, {@link #items(Collection)} or {@link #items(Collection, String,
	 * Function)}.
	 *
	 * @param  text
	 *           the text that will be displayed in a spinner whose collection of items is empty.
	 * @return this spinner.
	 */

	public CollectionSpinner<T> emptyItemsText(
		String	text)
	{
		// Set 'empty items' text
		setEmptyItemsText(text);

		// Return this spinner
		return this;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
