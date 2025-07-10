/*====================================================================*\

InvalidClusterDialog.java

Class: invalid-cluster dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;

import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;

import javafx.stage.Window;

import uk.blankaspect.common.comparator.CompoundStringComparator;

import uk.blankaspect.common.text.Tabulator;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleUtils;

import uk.blankaspect.ui.jfx.tableview.SimpleTableView;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: INVALID-CLUSTER DIALOG


public class InvalidClusterDialog
	extends SimpleModalDialog<Void>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The preferred height of the table view. */
	private static final	double	TABLE_VIEW_HEIGHT	= 312.0;

	/** The padding around the content pane. */
	private static final	Insets	CONTENT_PANE_PADDING	= new Insets(2.0, 2.0, 0.0, 2.0);

	/** The padding around a button. */
	private static final	Insets	BUTTON_PADDING	= new Insets(3.0, 20.0, 3.0, 20.0);

	/** Miscellaneous strings. */
	private static final	String	PATHNAME_STR	= "Pathname";
	private static final	String	INDEX_STR		= "Index";
	private static final	String	VALUE_STR		= "Value";
	private static final	String	COPY_STR		= "Copy";

	/** Identifiers of the columns of the table view. */
	private interface ColumnId
	{
		String	INDEX		= "index";
		String	PATHNAME	= "pathname";
		String	VALUE		= "value";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private InvalidClusterDialog(
		Window								owner,
		String								title,
		List<Fat32Volume.InvalidCluster>	invalidClusters)
	{
		// Call superclass method
		super(owner, MethodHandles.lookup().lookupClass().getName(), title);

		// Set properties
		setResizable(true);

		// Create table view
		List<SimpleTableView.IColumn<Fat32Volume.InvalidCluster, ?>> columns =
				List.of(pathnameColumn(), indexColumn(), valueColumn());
		SimpleTableView<Fat32Volume.InvalidCluster> tableView = new SimpleTableView<>(columns);
		tableView.setPrefHeight(TABLE_VIEW_HEIGHT);
		tableView.setItems(invalidClusters);

		// Add table view to content
		addContent(tableView);

		// Adjust padding around content pane
		getContentPane().setPadding(CONTENT_PANE_PADDING);

		// Remove border from content pane
		StyleUtils.setProperty(getContentPane(), FxProperty.BORDER_WIDTH.getName(), "0");

		// Create button: copy
		Button copyButton = Buttons.hNoShrink(COPY_STR);
		copyButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		copyButton.setOnAction(event ->
		{
			// Get number of columns
			int numColumns = columns.size();

			// Create array of flags for right-aligned columns
			boolean[] rightAligned = new boolean[numColumns];
			for (int i = 0; i < numColumns; i++)
				rightAligned[i] = (columns.get(i).getHAlignment() == HPos.RIGHT);

			// Create array of gaps between columns
			int[] gaps = new int[numColumns - 1];
			Arrays.fill(gaps, 2);

			// Initialise list of rows of table
			List<String[]> rows = new ArrayList<>();

			// Add row for header
			String[] fields = new String[numColumns];
			for (int i = 0; i < numColumns; i++)
				fields[i] = columns.get(i).getTitle();
			rows.add(fields);

			// Add rows for invalid clusters
			for (Fat32Volume.InvalidCluster invalidCluster : invalidClusters)
			{
				fields = new String[numColumns];
				int index = 0;
				fields[index++] = invalidCluster.pathname();
				fields[index++] = Utils.formatDecimal(invalidCluster.entryIndex());
				fields[index++] = Utils.formatDecimal(invalidCluster.entryValue());
				rows.add(fields);
			}

			// Tabulate rows
			Tabulator.Result table = Tabulator.tabulate(numColumns, rightAligned, gaps, rows);
			String text = table.text();
			int index = text.indexOf('\n') + 1;
			text = text.substring(0, index) + "-".repeat(table.maxLineLength()) + "\n" + text.substring(index);

			// Copy tabulated text to clipboard
			Utils.copyToClipboard(this, COPY_STR, text);
		});
		addButton(copyButton, HPos.LEFT);

		// Create button: close
		Button closeButton = Buttons.hNoShrink(CLOSE_STR);
		closeButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		closeButton.setOnAction(event -> requestClose());
		addButton(closeButton, HPos.RIGHT);

		// Fire 'close' button if Escape key is pressed
		setKeyFireButton(closeButton, null);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a modal window in which information about invalid clusters is displayed.
	 *
	 * @param  owner
	 *           the window that will own this window, or {@code null} if the window has no owner.
	 * @param  title
	 *           the title of the window.
	 * @param  invalidClusters
	 *           a list of the invalid clusters that will be displayed.
	 */

	public static void show(
		Window								owner,
		String								title,
		List<Fat32Volume.InvalidCluster>	invalidClusters)
	{
		new InvalidClusterDialog(owner, title, invalidClusters).showDialog();
	}

	//------------------------------------------------------------------

	private static SimpleTableView.IColumn<Fat32Volume.InvalidCluster, String> pathnameColumn()
	{
		return new SimpleTableView.IColumn<>()
		{
			@Override
			public String getId()
			{
				return ColumnId.PATHNAME;
			}

			@Override
			public String getTitle()
			{
				return PATHNAME_STR;
			}

			@Override
			public double getPrefWidth()
			{
				return TextUtils.textHeightCeil(25.0);
			}

			@Override
			public TableColumn<Fat32Volume.InvalidCluster, String> createColumn(
				SimpleTableView<Fat32Volume.InvalidCluster>	tableView)
			{
				TableColumn<Fat32Volume.InvalidCluster, String> column =
						SimpleTableView.IColumn.super.createColumn(tableView);
				column.setComparator(CompoundStringComparator.ignoreCase(Fat32Directory.NAME_SEPARATOR_CHAR));
				return column;
			}

			@Override
			public String getValue(
				Fat32Volume.InvalidCluster	item)
			{
				return item.pathname();
			}

			@Override
			public String getText(
				String	pathname)
			{
				return pathname;
			}
		};
	}

	//------------------------------------------------------------------

	private static SimpleTableView.IColumn<Fat32Volume.InvalidCluster, Integer> indexColumn()
	{
		return new SimpleTableView.IColumn<>()
		{
			@Override
			public String getId()
			{
				return ColumnId.INDEX;
			}

			@Override
			public String getTitle()
			{
				return INDEX_STR;
			}

			@Override
			public HPos getHAlignment()
			{
				return HPos.RIGHT;
			}

			@Override
			public double getPrefWidth()
			{
				return TextUtils.textWidthCeil(Utils.formatDecimal(0xFFFF_FFFFL));
			}

			@Override
			public Integer getValue(
				Fat32Volume.InvalidCluster	item)
			{
				return item.entryIndex();
			}

			@Override
			public String getText(
				Integer	index)
			{
				return Utils.formatDecimal(index);
			}
		};
	}

	//------------------------------------------------------------------

	private static SimpleTableView.IColumn<Fat32Volume.InvalidCluster, Integer> valueColumn()
	{
		return new SimpleTableView.IColumn<>()
		{
			@Override
			public String getId()
			{
				return ColumnId.VALUE;
			}

			@Override
			public String getTitle()
			{
				return VALUE_STR;
			}

			@Override
			public HPos getHAlignment()
			{
				return HPos.RIGHT;
			}

			@Override
			public double getPrefWidth()
			{
				return TextUtils.textWidthCeil(Utils.formatDecimal(0xFFFF_FFFFL));
			}

			@Override
			public Integer getValue(
				Fat32Volume.InvalidCluster	item)
			{
				return item.entryValue();
			}

			@Override
			public String getText(
				Integer	value)
			{
				return Utils.formatDecimal(value);
			}
		};
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	protected Insets getButtonPadding()
	{
		return BUTTON_PADDING;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
