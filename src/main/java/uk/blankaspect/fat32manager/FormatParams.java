/*====================================================================*\

FormatParams.java

Class: format parameters.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import uk.blankaspect.common.basictree.MapNode;

//----------------------------------------------------------------------


// CLASS: FORMAT PARAMETERS


public class FormatParams
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The default size of a sector in bytes. */
	public static final		int		DEFAULT_SECTOR_SIZE	= 512;

	/** The default minimum number of sectors. */
	public static final		int		DEFAULT_MIN_NUM_SECTORS	= 1 << 16;	// 65536

	/** The default minimum number of reserved sectors. */
	private static final	int		DEFAULT_MIN_NUM_RESERVED_SECTORS	= 32;

	/** The default formatter name. */
	private static final	String	DEFAULT_FORMATTER_NAME	= "FAT32MGR";

	/** The default cluster alignment. */
	private static final	ClusterAlignment	DEFAULT_CLUSTER_ALIGNMENT	= ClusterAlignment.INTEGRAL_MULTIPLE;

	private interface PropertyKey
	{
		String	ALIGN_FATS_TO_CLUSTERS		= "alignFatsToClusters";
		String	CLUSTER_ALIGNMENT			= "clusterAlignment";
		String	FORMATTER_NAME				= "formatterName";
		String	MIN_NUM_RESERVED_SECTORS	= "minNumReservedSectors";
		String	MIN_NUM_SECTORS				= "minNumSectors";
		String	REMOVABLE_MEDIA_ONLY		= "removableMediaOnly";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	int					bytesPerSector;
	private	int					minNumSectors;
	private	int					minNumReservedSectors;
	private	ClusterAlignment	clusterAlignment;
	private	boolean				alignFatsToClusters;
	private	boolean				removableMediaOnly;
	private	String				formatterName;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FormatParams()
	{
		// Initialise instance variables
		bytesPerSector = DEFAULT_SECTOR_SIZE;
		minNumSectors = DEFAULT_MIN_NUM_SECTORS;
		minNumReservedSectors = DEFAULT_MIN_NUM_RESERVED_SECTORS;
		clusterAlignment = DEFAULT_CLUSTER_ALIGNMENT;
		alignFatsToClusters = true;
		formatterName = DEFAULT_FORMATTER_NAME;
	}

	//------------------------------------------------------------------

	public FormatParams(
		int					bytesPerSector,
		int					minNumReservedSectors,
		ClusterAlignment	clusterAlignment,
		boolean				alignFatsToClusters,
		String				formatterName)
	{
		// Initialise instance variables
		this.bytesPerSector = bytesPerSector;
		this.minNumReservedSectors = minNumReservedSectors;
		this.clusterAlignment = clusterAlignment;
		this.alignFatsToClusters = alignFatsToClusters;
		this.formatterName = formatterName;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getBytesPerSector()
	{
		return bytesPerSector;
	}

	//------------------------------------------------------------------

	public int getMinNumSectors()
	{
		return minNumSectors;
	}

	//------------------------------------------------------------------

	public void setMinNumSectors(
		int	minNumSectors)
	{
		this.minNumSectors = minNumSectors;
	}

	//------------------------------------------------------------------

	public int getMinNumReservedSectors()
	{
		return minNumReservedSectors;
	}

	//------------------------------------------------------------------

	public ClusterAlignment getClusterAlignment()
	{
		return clusterAlignment;
	}

	//------------------------------------------------------------------

	public boolean isAlignFatsToClusters()
	{
		return alignFatsToClusters;
	}

	//------------------------------------------------------------------

	public boolean isRemovableMediaOnly()
	{
		return removableMediaOnly;
	}

	//------------------------------------------------------------------

	public void setRemovableMediaOnly(
		boolean	removableOnly)
	{
		removableMediaOnly = removableOnly;
	}

	//------------------------------------------------------------------

	public String getFormatterName()
	{
		return formatterName;
	}

	//------------------------------------------------------------------

	public void update(
		Preferences	preferences)
	{
		minNumSectors = preferences.getFormatMinNumSectors();
		removableMediaOnly = preferences.isFormatRemovableMediaOnly();
	}

	//------------------------------------------------------------------

	public MapNode encode()
	{
		// Create root node
		MapNode rootNode = new MapNode();

		// Encode minimum number of sectors
		rootNode.addInt(PropertyKey.MIN_NUM_SECTORS, minNumSectors);

		// Encode minimum number of reserved sectors
		rootNode.addInt(PropertyKey.MIN_NUM_RESERVED_SECTORS, minNumReservedSectors);

		// Encode cluster alignment
		rootNode.addString(PropertyKey.CLUSTER_ALIGNMENT, clusterAlignment.getKey());

		// Encode 'align FATs to clusters' flag
		rootNode.addBoolean(PropertyKey.ALIGN_FATS_TO_CLUSTERS, alignFatsToClusters);

		// Encode 'removable media only' flag
		rootNode.addBoolean(PropertyKey.REMOVABLE_MEDIA_ONLY, removableMediaOnly);

		// Encode formatter name
		rootNode.addString(PropertyKey.FORMATTER_NAME, (formatterName == null) ? "" : formatterName);

		// Return root node
		return rootNode;
	}

	//------------------------------------------------------------------

	public void decode(
		MapNode	rootNode)
	{
		// Decode minimum number of sectors
		minNumSectors = rootNode.getInt(PropertyKey.MIN_NUM_SECTORS, DEFAULT_MIN_NUM_SECTORS);

		// Decode minimum number of reserved sectors
		minNumReservedSectors = rootNode.getInt(PropertyKey.MIN_NUM_RESERVED_SECTORS, DEFAULT_MIN_NUM_RESERVED_SECTORS);

		// Decode cluster alignment
		clusterAlignment = ClusterAlignment.forKey(rootNode.getString(PropertyKey.CLUSTER_ALIGNMENT, null));
		if (clusterAlignment == null)
			clusterAlignment = DEFAULT_CLUSTER_ALIGNMENT;

		// Decode 'align FATs to clusters' flag
		alignFatsToClusters = rootNode.getBoolean(PropertyKey.ALIGN_FATS_TO_CLUSTERS, false);

		// Decode 'removable media only' flag
		removableMediaOnly = rootNode.getBoolean(PropertyKey.REMOVABLE_MEDIA_ONLY, false);

		// Decode formatter name
		formatterName = rootNode.getString(PropertyKey.FORMATTER_NAME, DEFAULT_FORMATTER_NAME);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
