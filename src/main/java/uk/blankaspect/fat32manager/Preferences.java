/*====================================================================*\

Preferences.java

Class: user preferences.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// CLASS: USER PREFERENCES


public class Preferences
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	int		columnHeaderPopUpDelay;
	private	boolean	showSpecialDirectories;
	private	boolean	fixDirEntryDatesTimes;
	private	int		formatMinNumSectors;
	private	boolean	formatRemovableMediaOnly;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Preferences()
	{
		// Initialise instance variables
		columnHeaderPopUpDelay = DirectoryTableView.DEFAULT_HEADER_CELL_POP_UP_DELAY;
		formatMinNumSectors = FormatParams.DEFAULT_MIN_NUM_SECTORS;
	}

	//------------------------------------------------------------------

	public Preferences(
		int		columnHeaderPopUpDelay,
		boolean	showSpecialDirectories,
		boolean	fixDirEntryDatesTimes,
		int		formatMinNumSectors,
		boolean	formatRemovableMediaOnly)
	{
		// Initialise instance variables
		this.columnHeaderPopUpDelay = columnHeaderPopUpDelay;
		this.showSpecialDirectories = showSpecialDirectories;
		this.fixDirEntryDatesTimes = fixDirEntryDatesTimes;
		this.formatMinNumSectors = formatMinNumSectors;
		this.formatRemovableMediaOnly = formatRemovableMediaOnly;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getColumnHeaderPopUpDelay()
	{
		return columnHeaderPopUpDelay;
	}

	//------------------------------------------------------------------

	public void setColumnHeaderPopUpDelay(
		int	delay)
	{
		columnHeaderPopUpDelay = delay;
	}

	//------------------------------------------------------------------

	public boolean isShowSpecialDirectories()
	{
		return showSpecialDirectories;
	}

	//------------------------------------------------------------------

	public void setShowSpecialDirectories(
		boolean	showSpecialDirectories)
	{
		this.showSpecialDirectories = showSpecialDirectories;
	}

	//------------------------------------------------------------------

	public boolean isFixDirEntryDatesTimes()
	{
		return fixDirEntryDatesTimes;
	}

	//------------------------------------------------------------------

	public void setFixDirEntryDatesTimes(
		boolean	fixDirEntryDatesTimes)
	{
		this.fixDirEntryDatesTimes = fixDirEntryDatesTimes;
	}

	//------------------------------------------------------------------

	public int getFormatMinNumSectors()
	{
		return formatMinNumSectors;
	}

	//------------------------------------------------------------------

	public boolean isFormatRemovableMediaOnly()
	{
		return formatRemovableMediaOnly;
	}

	//------------------------------------------------------------------

	public void update(
		FormatParams	formatParams)
	{
		formatMinNumSectors = formatParams.getMinNumSectors();
		formatRemovableMediaOnly = formatParams.isRemovableMediaOnly();
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
