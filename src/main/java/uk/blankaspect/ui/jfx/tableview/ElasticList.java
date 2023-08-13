/*====================================================================*\

ElasticList.java

Class: elastic list of items for a list view or table view.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.tableview;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.collections.transformation.SortedList;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

//----------------------------------------------------------------------


// CLASS: ELASTIC LIST OF ITEMS FOR A LIST VIEW OR TABLE VIEW


/**
 * This class implements a list of items for a JavaFX {@linkplain ListView list view} or {@linkplain TableView table
 * view}.  An instance of this class consists of two underlying lists:
 * <ol>
 *   <li>
 *     A base list of unsorted items (the inner list).
 *   <li>
 *   <li>
 *     A {@linkplain SortedList sorted list} of the items of the base list (the outer list).
 *   <li>
 * </ol>
 * The nesting of the lists allows the unsorted order of items to be restored when the sorting of the columns of a table
 * view is removed.
 */

public class ElasticList<T>
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The underlying unsorted list of the items that are represented in the table view. */
	private	ObservableList<T>	baseList;

	/** The underlying sorted list of the items that are represented in the table view. */
	private	SortedList<T>		sortedList;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a list of items for the specified list view.
	 *
	 * @param listView
	 *          the list view to which this list will be applied.
	 */

	public ElasticList(
		ListView<T>	listView)
	{
		// Validate arguments
		if (listView == null)
			throw new IllegalArgumentException("Null list view");

		// Initialise instance variables
		baseList = FXCollections.observableArrayList();
		sortedList = new SortedList<>(baseList);

		// Set items on list view
		listView.setItems(sortedList);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a list of items for the specified table view.
	 *
	 * @param tableView
	 *          the table view to which this list will be applied.
	 */

	public ElasticList(
		TableView<T>	tableView)
	{
		// Validate arguments
		if (tableView == null)
			throw new IllegalArgumentException("Null table view");

		// Initialise instance variables
		baseList = FXCollections.observableArrayList();
		sortedList = new SortedList<>(baseList);

		// Set items on table view
		tableView.setItems(sortedList);

		// Bind comparator of sorted list to comparator of table view
		sortedList.comparatorProperty().bind(tableView.comparatorProperty());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of items in this list.
	 *
	 * @return the number of items in this list.
	 */

	public int getNumItems()
	{
		return baseList.size();
	}

	//------------------------------------------------------------------

	/**
	 * Returns a modifiable copy of this list in which the items are in unsorted order.
	 *
	 * @return a modifiable copy of this list in which the items are in unsorted order.
	 */

	public List<T> getItems()
	{
		return new ArrayList<>(baseList);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the index of the specified item in the unsorted list of items.  The target item is compared to the items
	 * in the unsorted list using reference equality ({@code item1 == item2}), not object equality ({@code
	 * item1.equals(item2)}).
	 *
	 * @param  item
	 *           the target item.
	 * @return the index of {@code item} in the unsorted list of items, or -1 if the item was not found in the list.
	 */

	public int indexOf(
		T	item)
	{
		int numItems = baseList.size();
		for (int i = 0; i < numItems; i++)
		{
			if (baseList.get(i) == item)
				return i;
		}
		return -1;
	}

	//------------------------------------------------------------------

	/**
	 * Adds the specified item to the end of this list.
	 *
	 * @param item
	 *          the item that will be added to the end of this list.
	 */

	public void add(
		T	item)
	{
		baseList.add(item);
	}

	//------------------------------------------------------------------

	/**
	 * Inserts the specified item into this list at the specified index.  The index relates to the sorted list.
	 *
	 * @param index
	 *          the index at which {@code item} will be inserted into this list.
	 * @param item
	 *          the item that will be inserted into this list.
	 */

	public void add(
		int	index,
		T	item)
	{
		baseList.add(sortedList.getSourceIndex(index), item);
	}

	//------------------------------------------------------------------

	/**
	 * Removes the item at the specified index from this list.  The index relates to the sorted list.
	 *
	 * @param  index
	 *           the index of the item that will be removed from this list.
	 * @return the item that was removed from this list.
	 */

	public T remove(
		int	index)
	{
		return baseList.remove(sortedList.getSourceIndex(index));
	}

	//------------------------------------------------------------------

	/**
	 * Removes the specified item from this list.
	 *
	 * @param  item
	 *           the item that will be removed from this list.
	 * @return {@code true} if this list contained {@code item}.
	 */

	public boolean remove(
		T	item)
	{
		return baseList.remove(item);
	}

	//------------------------------------------------------------------

	/**
	 * Replaces the item at the specified index in this list with specified item.  The index relates to the sorted list.
	 *
	 * @param index
	 *          the index of the item that will be replaced by {@code item}.
	 * @param item
	 *          the item that will replace the item at {@code index} in this list.
	 */

	public void set(
		int	index,
		T	item)
	{
		baseList.set(sortedList.getSourceIndex(index), item);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the comparison function that is applied to the sorted list.
	 *
	 * @return the comparison function that is applied to the sorted list, or {@code null} if no comparator is applied.
	 */

	public Comparator<? super T> getComparator()
	{
		return sortedList.getComparator();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the specified comparison function on the sorted list.  This method cannot be used when this list is used
	 * with a table view because the comparator of the sorted list is bound to the comparator of the table view.
	 *
	 * @param comparator
	 *          the comparison function that will be set on the sorted list.  If it is {@code null}, no comparator will
	 *          be applied.
	 */

	public void setComparator(
		Comparator<? super T>	comparator)
	{
		sortedList.setComparator(comparator);
	}

	//------------------------------------------------------------------

	/**
	 * Updates this list with the specified items.
	 *
	 * @param items
	 *          the items that will be set on this list.
	 */

	public void update(
		Collection<? extends T>	items)
	{
		baseList.setAll(items);
	}

	//------------------------------------------------------------------

	/**
	 * Updates this list with the specified items.
	 *
	 * @param items
	 *          the items that will be set on this list.
	 */

	@SuppressWarnings("unchecked")
	public void update(
		T...	items)
	{
		baseList.setAll(items);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
