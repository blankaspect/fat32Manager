/*====================================================================*\

Fat32Fat.java

Class: FAT32 file-allocation table.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import uk.blankaspect.common.number.NumberCodec;

import uk.blankaspect.driveio.VolumeException;

//----------------------------------------------------------------------


// CLASS: FAT32 FILE-ALLOCATION TABLE


public class Fat32Fat
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int		ENTRY_SIZE	= 4;

	public static final		int		MIN_CLUSTER_INDEX	= 2;
	public static final		int		MAX_CLUSTER_INDEX	= 0x0FFFFFF6;

	public static final		int		BAD_CLUSTER_INDEX	= 0x0FFFFFF7;

	public static final		int		MIN_END_OF_CHAIN_INDEX	= 0x0FFFFFF8;
	public static final		int		MAX_END_OF_CHAIN_INDEX	= 0x0FFFFFFF;

	private static final	int		CLUSTER_INDEX_MASK	= 0x0FFFFFFF;

	private static final	String	FAT_NOT_INITIALISED_STR			= "FAT not initialised";
	private static final	String	START_INDEX_OUT_OF_BOUNDS_STR	= "Start index out of bounds: ";

	private interface ErrorMsg
	{
		String	INVALID_CLUSTER_INDEX	= "The cluster chain that starts at index %d includes an invalid cluster index at %s.";
		String	BAD_CLUSTER				= "The cluster chain includes a bad cluster at %s.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Fat32Volume	volume;
	private	int[]		entries;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Fat32Fat(
		Fat32Volume	volume)
	{
		// Validate arguments
		if (volume == null)
			throw new IllegalArgumentException("Null volume");

		// Validate volume
		if (!volume.isInitialised())
			throw new IllegalStateException("Volume not initialised");

		// Initialise instance variables
		this.volume = volume;
	}

	//------------------------------------------------------------------

	/**
	 * For use in unit tests.
	 */

	@SuppressWarnings("unused")
	private Fat32Fat()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean isEndOfChain(
		int	index)
	{
		return (index >= MIN_END_OF_CHAIN_INDEX) && (index <= MAX_END_OF_CHAIN_INDEX);
	}

	//------------------------------------------------------------------

	public static boolean isBadCluster(
		int	index)
	{
		return (index == BAD_CLUSTER_INDEX);
	}

	//------------------------------------------------------------------

	public static int getIndex(
		byte[]	data,
		int		offset)
	{
		return NumberCodec.bytesToUIntLE(data, offset, ENTRY_SIZE) & CLUSTER_INDEX_MASK;
	}

	//------------------------------------------------------------------

	public static void setIndex(
		int		index,
		byte[]	buffer,
		int		offset)
	{
		NumberCodec.uIntToBytesLE(index, buffer, offset, ENTRY_SIZE);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getLength()
	{
		return entries.length;
	}

	//------------------------------------------------------------------

	public int get(
		int	index)
	{
		return entries[index];
	}

	//------------------------------------------------------------------

	public void set(
		int	index,
		int	value)
	{
		entries[index] = value;
	}

	//------------------------------------------------------------------

	public byte[] read(
		int	index)
		throws VolumeException
	{
		// Seek sector
		int numSectors = volume.getSectorsPerFat();
		volume.seekSector(volume.getNumReservedSectors() + index * numSectors);

		// Allocate buffer
		byte[] buffer = new byte[numSectors * volume.getBytesPerSector()];

		// Read sectors
		volume.read(buffer);

		// Return buffer
		return buffer;
	}

	//------------------------------------------------------------------

	public void init(
		int	index)
		throws VolumeException
	{
		// Read sectors of FAT from volume
		byte[] data = read(index);

		// Initialise entries
		int numEntries = volume.getMaxClusterIndex() + 1;
		entries = new int[numEntries];
		int offset = 0;
		for (int i = 0; i < numEntries; i++)
		{
			entries[i] = NumberCodec.bytesToUIntLE(data, offset, ENTRY_SIZE) & CLUSTER_INDEX_MASK;
			offset += ENTRY_SIZE;
		}
	}

	//------------------------------------------------------------------

	public IndexIterator indexIterator(
		int	startIndex)
	{
		// Check that FAT has been initialised
		if (entries == null)
			throw new IllegalStateException(FAT_NOT_INITIALISED_STR);

		// Create new iterator and return it
		return new IndexIterator(startIndex);
	}

	//------------------------------------------------------------------

	public IndexFinder indexFinder(
		int	startIndex,
		int	numClusters)
		throws VolumeException
	{
		// Check that FAT has been initialised
		if (entries == null)
			throw new IllegalStateException(FAT_NOT_INITIALISED_STR);

		// Create new index finder and return it
		return new IndexFinder(startIndex, numClusters);
	}

	//------------------------------------------------------------------

	public IndexFinder indexFinder(
		Fat32Directory.Entry	entry)
		throws VolumeException
	{
		return indexFinder(entry.getClusterIndex(), entry.getNumClusters());
	}

	//------------------------------------------------------------------

	public int getIndex(
		int	startIndex,
		int	distance)
		throws VolumeException
	{
		// Validate argument
		if (distance < 0)
			throw new IllegalArgumentException("Distance out of bounds: " + distance);

		// Check that FAT has been initialised
		if (entries == null)
			throw new IllegalStateException(FAT_NOT_INITIALISED_STR);

		// Follow chain until distance from start index is reached
		int offset = 0;
		IndexIterator it = indexIterator(startIndex);
		while (it.hasNext())
		{
			int index = it.next();
			if (offset == distance)
				return index;
			++offset;
		}

		// Indicate premature end of chain
		return -1;
	}

	//------------------------------------------------------------------

	public int clusterCount(
		int	startIndex)
		throws VolumeException
	{
		// Check that FAT has been initialised
		if (entries == null)
			throw new IllegalStateException(FAT_NOT_INITIALISED_STR);

		// Initialise count
		int count = 0;

		// Count clusters
		IndexIterator it = indexIterator(startIndex);
		while (it.hasNext())
		{
			it.next();
			++count;
		}

		// Return number of clusters
		return count;
	}

	//------------------------------------------------------------------

	public int getNumUnusedClusters()
	{
		// Check that FAT has been initialised
		if (entries == null)
			throw new IllegalStateException(FAT_NOT_INITIALISED_STR);

		// Count unused clusters
		int numUnusedClusters = 0;
		for (int i = 0; i < entries.length; i++)
		{
			if (entries[i] == 0)
				++numUnusedClusters;
		}

		// Return number of unused clusters
		return numUnusedClusters;
	}

	//------------------------------------------------------------------

	public int findUnusedCluster(
		int	startIndex)
	{
		// Check that FAT has been initialised
		if (entries == null)
			throw new IllegalStateException(FAT_NOT_INITIALISED_STR);

		// Search from start index towards last cluster
		for (int i = startIndex; i < entries.length; i++)
		{
			if (entries[i] == 0)
				return i;
		}

		// Search from start index towards first cluster
		for (int i = startIndex - 1; i >= MIN_CLUSTER_INDEX; i--)
		{
			if (entries[i] == 0)
				return i;
		}

		// Indicate no unused cluster found
		return 0;
	}

	//------------------------------------------------------------------

	public int findUnusedClusters(
		int	length)
	{
		// Validate argument
		if (length <= 0)
			throw new IllegalArgumentException("Length out of bounds: " + length);

		// Check that FAT has been initialised
		if (entries == null)
			throw new IllegalStateException(FAT_NOT_INITIALISED_STR);

		// Search for sequence of unused clusters
		int index = MIN_CLUSTER_INDEX;
		int endIndex = entries.length - length;
		while (index <= endIndex)
		{
			if (entries[index++] == 0)
			{
				int startIndex = index - 1;
				int endIndex0 = startIndex + length;
				while (index < endIndex0)
				{
					if (entries[index] != 0)
						break;
					++index;
				}
				if (index == endIndex0)
					return startIndex;
				++index;
			}
		}

		// No sequence of unused clusters of desired length was found
		return 0;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: INDEX ITERATOR


	public class IndexIterator
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	START_OF_CHAIN_STR	= "the start of the chain";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int	startIndex;
		private	int	index;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private IndexIterator(
			int	startIndex)
		{
			// Validate arguments
			if ((startIndex < MIN_CLUSTER_INDEX) || (startIndex >= entries.length))
				throw new IllegalArgumentException(START_INDEX_OUT_OF_BOUNDS_STR + startIndex);

			// Initialise instance variables
			this.startIndex = startIndex;
			index = -1;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		private static String indexToString(
			int	index)
		{
			return (index < 0) ? START_OF_CHAIN_STR : Integer.toString(index);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public boolean hasNext()
		{
			return (index < 0) || !Fat32Fat.isEndOfChain(entries[index]);
		}

		//--------------------------------------------------------------

		public Integer next()
			throws VolumeException
		{
			// Get index of next cluster
			int prevIndex = index;
			index = (index < 0) ? startIndex : entries[index];

			// Test for invalid cluster index
			if (index < MIN_CLUSTER_INDEX)
				throw new VolumeException(ErrorMsg.INVALID_CLUSTER_INDEX, startIndex, indexToString(prevIndex));

			// Test for bad cluster
			if (index == BAD_CLUSTER_INDEX)
				throw new VolumeException(ErrorMsg.BAD_CLUSTER, indexToString(prevIndex));

			// Return index
			return index;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// CLASS: INDEX FINDER


	public class IndexFinder
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int	MAX_NUM_INDICES_SHIFT	= 16;

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int		startIndex;
		private	int		maxChainIndex;
		private	int		shift;
		private	int		mask;
		private	int[]	indices;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private IndexFinder(
			int	startIndex,
			int	numClusters)
			throws VolumeException
		{
			// Validate arguments
			if ((startIndex < MIN_CLUSTER_INDEX) || (startIndex >= entries.length))
				throw new IllegalArgumentException(START_INDEX_OUT_OF_BOUNDS_STR + startIndex);

			// Initialise instance variables
			this.startIndex = startIndex;

			// If number of clusters was not supplied, count clusters
			if (numClusters < 0)
				numClusters = clusterCount(startIndex);
			maxChainIndex = numClusters - 1;

			// Calculate right shift for mapping cluster index to base index
			shift = 0;
			if (numClusters > 0)
			{
				int temp = (numClusters - 1) >>> MAX_NUM_INDICES_SHIFT;
				while (temp != 0)
				{
					++shift;
					temp >>>= 1;
				}
			}

			// Calculate mask for mapping cluster index to base index
			int numIndices = numClusters >>> shift;
			mask = (1 << shift) - 1;
			if ((numClusters & mask) != 0)
				++numIndices;

			// Create array of base indices
			indices = new int[numIndices];
			IndexIterator it = indexIterator(startIndex);
			int i = 0;
			int j = 0;
			if (shift > 0)
			{
				while (it.hasNext())
				{
					int index = it.next();
					if ((j++ & mask) == 0)
						indices[i++] = index;
				}
			}
			else
			{
				while (it.hasNext())
					indices[i++] = it.next();
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getStartIndex()
		{
			return startIndex;
		}

		//--------------------------------------------------------------

		public int getMaxChainIndex()
		{
			return maxChainIndex;
		}

		//--------------------------------------------------------------

		public int find(
			int	index)
			throws VolumeException
		{
			// Validate argument
			if ((index < 0) || (index > maxChainIndex))
				throw new IllegalArgumentException("Index out of bounds: " + index);

			// Return cluster index
			return (shift > 0) ? getIndex(indices[index >>> shift], index & mask) : indices[index];
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
