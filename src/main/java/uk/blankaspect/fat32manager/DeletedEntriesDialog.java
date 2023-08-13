/*====================================================================*\

DeletedEntriesDialog.java

Class: deleted directory-entries dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;

import javafx.geometry.HPos;
import javafx.geometry.Point2D;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;

import javafx.stage.Window;

import uk.blankaspect.common.basictree.AbstractNode;
import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.driveio.VolumeException;

import uk.blankaspect.fat32manager.Fat32Directory.Entry;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.window.WindowState;

//----------------------------------------------------------------------


// CLASS: DELETED DIRECTORY-ENTRIES DIALOG


public class DeletedEntriesDialog
	extends SimpleModalDialog<Void>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The preferred height of the table view. */
	private static final	double	TABLE_VIEW_HEIGHT	= 506.0;

	/** Miscellaneous strings. */
	private static final	String	DELETED_ENTRIES_STR	= "Deleted entries";
	private static final	String	VIEW_CLUSTER_STR	= "View cluster";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	State	state	= new State();

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public DeletedEntriesDialog(
		Window						owner,
		Fat32Directory				directory0,
		List<Fat32Directory.Entry>	entries)
	{
		// Call superclass constructor
		super(owner, DELETED_ENTRIES_STR + " - " + directory0.getPathname(), state.getLocator(), state.getSize());

		// Set properties
		setResizable(true);

		// Create directory table view
		DirectoryTableView tableView = new DirectoryTableView()
		{
			@Override
			public Fat32Directory getDirectory()
			{
				return directory0;
			}

			@Override
			protected void onCellDoubleClicked(
				Entry	entry)
			{
				if (entry != null)
				{
					Fat32Volume volume = directory0.getVolume();
					int index = entry.getClusterIndex();
					if ((index >= Fat32Fat.MIN_CLUSTER_INDEX) && (index <= volume.getMaxClusterIndex()))
					{
						try
						{
							SectorClusterViewDialog.showCluster(getWindow(), volume,
																volume.getFat().indexFinder(entry.getClusterIndex(), -1));
						}
						catch (VolumeException e)
						{
							Utils.showErrorMessage(getWindow(), VIEW_CLUSTER_STR, e);
						}
					}
				}
			}
		};
		tableView.setPrefHeight(TABLE_VIEW_HEIGHT);
		tableView.setColumns(state.columnWidths);
		tableView.setHeaderMode(DirectoryTableView.HeaderMode.DELETED);
		tableView.setItems(FXCollections.observableList(entries));

		// Set table view as content of dialog
		setContent(tableView);

		// Create button: close
		Button closeButton = new Button(CLOSE_STR);
		closeButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		closeButton.setOnAction(event -> requestClose());
		addButton(closeButton, HPos.RIGHT);

		// Fire 'close' button if Escape key is pressed
		setKeyFireButton(closeButton, null);

		// Save state of window when dialog is closed
		setOnHiding(event ->
		{
			state.restoreAndUpdate(this, true);
			state.updateColumnWidths(tableView);
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static MapNode encodeState()
	{
		return state.encodeTree();
	}

	//------------------------------------------------------------------

	public static void decodeState(
		MapNode	mapNode)
	{
		state.decodeTree(mapNode);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: STATE


	public static class State
		extends WindowState
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** Keys of properties. */
		private interface PropertyKey
		{
			String	COLUMN_WIDTHS	= "columnWidths";
			String	VIEW			= "view";
		}

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	Map<String, Double>	columnWidths;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private State()
		{
			// Call superclass constructor
			super(true, true);

			// Initialise instance variables
			columnWidths = new LinkedHashMap<>();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Encodes this dialog state as a tree of {@linkplain AbstractNode nodes} and returns the root node.
		 *
		 * @return the root node of the tree of {@linkplain AbstractNode nodes} that encodes this dialog state.
		 */

		@Override
		public MapNode encodeTree()
		{
			// Call superclass method
			MapNode rootNode = super.encodeTree();

			// Create view node
			MapNode viewNode = rootNode.addMap(PropertyKey.VIEW);

			// Encode widths of columns of directory table
			MapNode columnsNode = new MapNode();
			for (Map.Entry<String, Double> columnWidth : columnWidths.entrySet())
				columnsNode.addDouble(columnWidth.getKey(), columnWidth.getValue());
			if (!columnsNode.isEmpty())
				viewNode.add(PropertyKey.COLUMN_WIDTHS, columnsNode);

			// Return root node
			return rootNode;
		}

		//--------------------------------------------------------------

		/**
		 * Decodes this dialog state from the tree of {@linkplain AbstractNode nodes} whose root is the specified node.
		 *
		 * @param rootNode
		 *          the root of the tree of {@linkplain AbstractNode nodes} from which this dialog state will be
		 *          decoded.
		 */

		@Override
		public void decodeTree(
			MapNode	rootNode)
		{
			// Call superclass method
			super.decodeTree(rootNode);

			// Decode view properties
			String key = PropertyKey.VIEW;
			if (rootNode.hasMap(key))
			{
				// Get view node
				MapNode viewNode = rootNode.getMapNode(key);

				// Decode widths of columns of directory table
				columnWidths.clear();
				key = PropertyKey.COLUMN_WIDTHS;
				if (viewNode.hasMap(key))
				{
					MapNode columnsNode = viewNode.getMapNode(key);
					for (String key0 : columnsNode.getKeys())
						columnWidths.put(key0, columnsNode.getDouble(key0));
				}
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns a locator function that returns the location from this dialog state.
		 *
		 * @return a locator function that returns the location from this dialog state, or {@code null} if the location
		 *         is {@code null}.
		 */

		private ILocator getLocator()
		{
			Point2D location = getLocation();
			return (location == null) ? null : (width, height) -> location;
		}

		//--------------------------------------------------------------

		private void updateColumnWidths(
			DirectoryTableView	tableView)
		{
			columnWidths.clear();
			for (TableColumn<Fat32Directory.Entry, ?> column : tableView.getColumns())
				columnWidths.put(column.getId(), column.getWidth());
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
