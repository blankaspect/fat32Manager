/*====================================================================*\

ListViewUtils.java

Class: list-view-related utilities.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.listview;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.Node;

import javafx.scene.control.ListView;

import javafx.scene.control.skin.VirtualFlow;

import uk.blankaspect.ui.jfx.style.StyleSelector;

//----------------------------------------------------------------------


// CLASS: LIST-VIEW-RELATED UTILITIES


/**
 * This class contains utility methods that relate to JavaFX {@linkplain ListView list views}.
 */

public class ListViewUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private ListViewUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the number of cells that are expected to be fully visible in the viewport of the specified list view,
	 * based on the mean height of the cells in the {@linkplain VirtualFlow cell container} of the list view.  The
	 * return value may be used, for example, to adjust the argument of a call to {@link ListView#scrollTo(int)} so that
	 * the target cell is near the vertical centre of the viewport.
	 *
	 * @param  listView
	 *           the list view of interest.
	 * @return the number of cells that are expected to be fully visible in the viewport of {@code listView}.  It will
	 *         be 0 if the list view does not have a cell container of the {@linkplain VirtualFlow expected type}.
	 */

	public static int getNumViewableCells(
		ListView<?>	listView)
	{
		int numCells = 0;
		Node node = listView.lookup(StyleSelector.VIRTUAL_FLOW);
		if (node instanceof VirtualFlow<?> container)
		{
			numCells = container.getCellCount();
			if (numCells > 0)
			{
				double totalCellHeight = 0.0;
				for (int i = 0; i < numCells; i++)
					totalCellHeight += container.getCell(i).getHeight();
				double meanCellHeight = totalCellHeight / numCells;
				numCells = (int)(container.getHeight() / meanCellHeight);
			}
		}
		return numCells;
	}

	//------------------------------------------------------------------

	/**
	 * Scrolls the specified list view so that the item at the specified index is near the vertical centre of the
	 * viewport of the list view.
	 *
	 * @param listView
	 *          the list view that will be scrolled.
	 * @param index
	 *          the index of the item to which {@code listView} will be scrolled.
	 */

	public static void scrollToCentred(
		ListView<?>	listView,
		int			index)
	{
		if (index >= 0)
		{
			int numCells = getNumViewableCells(listView);
			if (numCells > 2)
				index -= (numCells - 1) / 2;
			listView.scrollTo(Math.max(0, index));
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
