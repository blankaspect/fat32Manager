/*====================================================================*\

Preferences.java

Class: user preferences.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: USER PREFERENCES


public class Preferences
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String	themeId;
	private	int		columnHeaderPopUpDelay;
	private	boolean	showSpecialDirectories;
	private	int		formatMinNumSectors;
	private	boolean	formatRemovableMediaOnly;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Preferences()
	{
		// Initialise instance variables
		themeId = StyleManager.DEFAULT_THEME_ID;
		columnHeaderPopUpDelay = DirectoryTableView.DEFAULT_HEADER_CELL_POP_UP_DELAY;
		formatMinNumSectors = FormatParams.DEFAULT_MIN_NUM_SECTORS;
	}

	//------------------------------------------------------------------

	public Preferences(
		String	themeId,
		int		columnHeaderPopUpDelay,
		boolean	showSpecialDirectories,
		int		formatMinNumSectors,
		boolean	formatRemovableMediaOnly)
	{
		// Initialise instance variables
		this.themeId = themeId;
		this.columnHeaderPopUpDelay = columnHeaderPopUpDelay;
		this.showSpecialDirectories = showSpecialDirectories;
		this.formatMinNumSectors = formatMinNumSectors;
		this.formatRemovableMediaOnly = formatRemovableMediaOnly;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getThemeId()
	{
		return themeId;
	}

	//------------------------------------------------------------------

	public void setThemeId(
		String	id)
	{
		themeId = id;
	}

	//------------------------------------------------------------------

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
