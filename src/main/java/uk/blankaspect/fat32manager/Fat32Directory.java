/*====================================================================*\

Fat32Directory.java

Class: FAT32 directory.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import java.util.stream.Collectors;

import uk.blankaspect.common.bitarray.BitUtils;

import uk.blankaspect.common.logging.Logger;

import uk.blankaspect.common.number.NumberCodec;
import uk.blankaspect.common.number.NumberUtils;

import uk.blankaspect.common.stack.StackUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.task.ICancellable;

import uk.blankaspect.common.tree.ITreeNode;

import uk.blankaspect.driveio.Volume;
import uk.blankaspect.driveio.VolumeException;

import uk.blankaspect.ui.jfx.font.FontUtils;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: FAT32 DIRECTORY


public class Fat32Directory
	implements ITreeNode<Fat32Directory>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		char	NAME_SEPARATOR_CHAR	= '/';
	public static final		String	NAME_SEPARATOR		= Character.toString(NAME_SEPARATOR_CHAR);

	public static final		char	EXTENSION_SEPARATOR_CHAR	= '.';
	public static final		String	EXTENSION_SEPARATOR			= Character.toString(EXTENSION_SEPARATOR_CHAR);

	public static final		String	SPECIAL_DIRECTORY_NAME_THIS		= ".";
	public static final		String	SPECIAL_DIRECTORY_NAME_PARENT	= "..";

	public static final		DateTimeFormatter	CREATION_TIME_FORMATTER	=
			DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss.SS");
	public static final		DateTimeFormatter	LAST_MODIFICATION_TIME_FORMATTER	=
			DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
	public static final		DateTimeFormatter	ACCESS_DATE_FORMATTER	=
			DateTimeFormatter.ofPattern("uuuu-MM-dd");

	public static final		int		SHORT_NAME_OFFSET	= 0x00;
	private static final	int		SHORT_NAME_LENGTH	= 8;

	private static final	int		SHORT_NAME_EXTENSION_OFFSET	= 0x08;
	private static final	int		SHORT_NAME_EXTENSION_LENGTH	= 3;

	public static final		int		VOLUME_LABEL_LENGTH	= SHORT_NAME_LENGTH + SHORT_NAME_EXTENSION_LENGTH;

	private static final	int		ATTRIBUTES_OFFSET	= 0x0B;
	private static final	int		ATTRIBUTES_LENGTH	= 1;

	private static final	int		NAME_CASE_OFFSET	= 0x0C;
	private static final	int		NAME_CASE_LENGTH	= 1;

	private static final	int		CREATION_TIME_CS_OFFSET	= 0x0D;
	private static final	int		CREATION_TIME_CS_LENGTH	= 1;

	private static final	int		CREATION_TIME_OFFSET	= 0x0E;
	private static final	int		CREATION_TIME_LENGTH	= 2;

	private static final	int		CREATION_DATE_OFFSET	= 0x10;
	private static final	int		CREATION_DATE_LENGTH	= 2;

	private static final	int		ACCESS_DATE_OFFSET	= 0x12;
	private static final	int		ACCESS_DATE_LENGTH	= 2;

	private static final	int		LAST_MODIFICATION_TIME_OFFSET	= 0x16;
	private static final	int		LAST_MODIFICATION_TIME_LENGTH	= 2;

	private static final	int		LAST_MODIFICATION_DATE_OFFSET	= 0x18;
	private static final	int		LAST_MODIFICATION_DATE_LENGTH	= 2;

	private static final	int		CLUSTER_INDEX_LOW_OFFSET	= 0x1A;
	private static final	int		CLUSTER_INDEX_LOW_LENGTH	= 2;

	private static final	int		CLUSTER_INDEX_HIGH_OFFSET	= 0x14;
	private static final	int		CLUSTER_INDEX_HIGH_LENGTH	= 2;

	private static final	int		FILE_LENGTH_OFFSET	= 0x1C;
	private static final	int		FILE_LENGTH_LENGTH	= 4;

	private static final	int		END_OF_DIRECTORY			= 0x00;
	private static final	int		DELETED_ENTRY				= 0xE5;
	private static final	int		DELETED_ENTRY_PLACEHOLDER	= 0x05;

	private static final	int		LFN_ATTRS				= 0x0F;
	private static final	int		LFN_FIRST_ENTRY_MASK	= 1 << 6;
	private static final	int		LFN_INDEX_MASK			= 0x1F;

	private static final	int		LFN_CHARS_PER_ENTRY	= 13;

	private static final	char	LFN_END_CHAR	= '\0';

	private static final	int		LFN_CHARS1_OFFSET	= 0x01;
	private static final	int		LFN_CHARS1_LENGTH	= 10;

	private static final	int		LFN_CHARS2_OFFSET	= 0x0E;
	private static final	int		LFN_CHARS2_LENGTH	= 12;

	private static final	int		LFN_CHARS3_OFFSET	= 0x1C;
	private static final	int		LFN_CHARS3_LENGTH	= 4;

	private static final	int		LFN_TYPE_OFFSET	= 0x0C;
	private static final	int		LFN_TYPE_LENGTH	= 1;
	private static final	int		LFN_TYPE		= 0;

	private static final	int		LFN_CHECKSUM_OFFSET	= 0x0D;
	private static final	int		LFN_CHECKSUM_LENGTH	= 1;

	private static final	int		LFN_MIN_INDEX	= 1;
	private static final	int		LFN_MAX_INDEX	= 20;

	private static final	int		LFN_STEM_CASE_MASK		= 1 << 3;
	private static final	int		LFN_EXTENSION_CASE_MASK	= 1 << 4;

	private static final	int		DAY_FIELD_SHIFT		= 0;
	private static final	int		DAY_FIELD_LENGTH	= 5;

	private static final	int		MIN_DAY	= 1;
	private static final	int		MAX_DAY	= 31;

	private static final	int		MIN_MAX_DAY	= 28;

	private static final	int		MONTH_FIELD_SHIFT	= DAY_FIELD_SHIFT + DAY_FIELD_LENGTH;
	private static final	int		MONTH_FIELD_LENGTH	= 4;

	private static final	int		MIN_MONTH	= 1;
	private static final	int		MAX_MONTH	= 12;

	private static final	int		YEAR_FIELD_SHIFT	= MONTH_FIELD_SHIFT + MONTH_FIELD_LENGTH;
	private static final	int		YEAR_FIELD_LENGTH	= 7;

	private static final	int		BASE_YEAR	= 1980;

	private static final	int		MIN_CENTISECOND	= 0;
	private static final	int		MAX_CENTISECOND	= 199;

	private static final	int		SECOND_FIELD_SHIFT	= 0;
	private static final	int		SECOND_FIELD_LENGTH	= 5;

	private static final	int		MIN_SECOND	= 0;
	private static final	int		MAX_SECOND	= 58;

	private static final	int		MINUTE_FIELD_SHIFT	= SECOND_FIELD_SHIFT + SECOND_FIELD_LENGTH;
	private static final	int		MINUTE_FIELD_LENGTH	= 6;

	private static final	int		MIN_MINUTE	= 0;
	private static final	int		MAX_MINUTE	= 59;

	private static final	int		HOUR_FIELD_SHIFT	= MINUTE_FIELD_SHIFT + MINUTE_FIELD_LENGTH;
	private static final	int		HOUR_FIELD_LENGTH	= 5;

	private static final	int		MIN_HOUR	= 0;
	private static final	int		MAX_HOUR	= 23;

	private static final	String	ENTRY_KIND_STR					= "Entry kind";
	private static final	String	PATHNAME_STR					= "Pathname";
	private static final	String	CREATION_DATE_TIME_STR			= "creation date or time";
	private static final	String	LAST_MODIFICATION_DATE_TIME_STR	= "last modification date or time";
	private static final	String	ACCESS_DATE_STR					= "access date";
	private static final	String	ERRORS_READING_CHILDREN_STR		= "errors reading children of";

	private interface ErrorMsg
	{
		String	INVALID_DIRECTORY_ENTRY =
				"Directory: %s\nThe directory entry at index %d is not valid.";

		String	INVALID_DIRECTORY_ENTRY_FIELD =
				"The %s of the directory entry is not valid: %s";

		String	LFN_INDEX_OUT_OF_SEQUENCE =
				"The long-filename index is out of sequence.";

		String	LFN_INDEX_OUT_OF_BOUNDS =
				"The long-filename index is out of bounds.";

		String	FIRST_ENTRY_OF_LFN_EXPECTED =
				"The first entry of a long filename was expected.";

		String	UNRECOGNISED_LFN_TYPE =
				"The long-filename entry type is not recognised.";

		String	INCORRECT_SFN_CHECKSUM =
				"The checksum of the short filename is incorrect.";

		String	CLUSTER_INDEX_OUT_OF_BOUNDS =
				"The cluster index %d is out of bounds.";

		String	CLUSTER_INDEX_ZERO_EXPECTED =
				"Cluster index %d: an index of 0 was expected.";

		String	INCONSISTENT_FILE_LENGTH =
				"File: %s\nThe file length is inconsistent with the number of clusters.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Fat32Volume				volume;
	private	Fat32Directory			parent;
	private	Entry					entryInParent;
	private	int						clusterIndex;
	private	List<Fat32Directory>	children;
	private	List<Entry>				entries;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Fat32Directory(
		Fat32Volume		volume,
		Fat32Directory	parent,
		Entry			entryInParent)
	{
		// Validate arguments
		if (volume == null)
			throw new IllegalArgumentException("Null volume");

		// Initialise instance variables
		this.volume = volume;
		this.parent = parent;
		this.entryInParent = entryInParent;
		clusterIndex = (entryInParent == null) ? volume.getRootDirClusterIndex() : entryInParent.getClusterIndex();
		entries = new ArrayList<>();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static int getClusterIndex(
		byte[]	data,
		int		offset)
	{
		int clusterIndex = NumberCodec.bytesToUIntLE(data, offset + CLUSTER_INDEX_LOW_OFFSET, CLUSTER_INDEX_LOW_LENGTH);
		clusterIndex |= NumberCodec.bytesToUIntLE(data, offset + CLUSTER_INDEX_HIGH_OFFSET,
												  CLUSTER_INDEX_HIGH_LENGTH) << 16;
		return clusterIndex;
	}

	//------------------------------------------------------------------

	public static void setClusterIndex(
		int		clusterIndex,
		byte[]	buffer,
		int		offset)
	{
		NumberCodec.uIntToBytesLE(clusterIndex, buffer, offset + CLUSTER_INDEX_LOW_OFFSET, CLUSTER_INDEX_LOW_LENGTH);
		NumberCodec.uIntToBytesLE(clusterIndex >>> 16, buffer, offset + CLUSTER_INDEX_HIGH_OFFSET,
								  CLUSTER_INDEX_HIGH_LENGTH);
	}

	//------------------------------------------------------------------

	public static void setAttributes(
		Collection<Attr>	attrs,
		byte[]				buffer,
		int					offset)
	{
		NumberCodec.uIntToBytesLE(Attr.attrsToBitArray(attrs), buffer, offset + ATTRIBUTES_OFFSET, ATTRIBUTES_LENGTH);
	}

	//------------------------------------------------------------------

	public static void setLastModificationTime(
		LocalDateTime	dateTime,
		byte[]			buffer,
		int				offset)
	{
		// Set date
		int date = 0;
		date = BitUtils.setBitField(date, dateTime.getDayOfMonth(), DAY_FIELD_SHIFT, DAY_FIELD_LENGTH);
		date = BitUtils.setBitField(date, dateTime.getMonthValue(), MONTH_FIELD_SHIFT, MONTH_FIELD_LENGTH);
		date = BitUtils.setBitField(date, dateTime.getYear() - BASE_YEAR, YEAR_FIELD_SHIFT, YEAR_FIELD_LENGTH);
		NumberCodec.uIntToBytesLE(date, buffer, offset + LAST_MODIFICATION_DATE_OFFSET, LAST_MODIFICATION_DATE_LENGTH);

		// Set time
		int time = 0;
		time = BitUtils.setBitField(time, dateTime.getSecond() / 2, SECOND_FIELD_SHIFT, SECOND_FIELD_LENGTH);
		time = BitUtils.setBitField(time, dateTime.getMinute(), MINUTE_FIELD_SHIFT, MINUTE_FIELD_LENGTH);
		time = BitUtils.setBitField(time, dateTime.getHour(), HOUR_FIELD_SHIFT, HOUR_FIELD_LENGTH);
		NumberCodec.uIntToBytesLE(time, buffer, offset + LAST_MODIFICATION_TIME_OFFSET, LAST_MODIFICATION_TIME_LENGTH);
	}

	//------------------------------------------------------------------

	private static String getShortName(
		byte[]	buffer,
		int		offset,
		int		length)
	{
		while (length > 0)
		{
			if (buffer[offset + --length] != ' ')
			{
				++length;
				break;
			}
		}
		return Utils.bytesToString(buffer, offset, length);
	}

	//------------------------------------------------------------------

	private static int getLfnChars(
		byte[]	inBuffer,
		int		inOffset,
		int		length,
		char[]	outBuffer,
		int		outOffset)
	{
		for (int i = 0; i < length; i += Character.BYTES)
			outBuffer[outOffset++] = (char)NumberCodec.bytesToUIntLE(inBuffer, inOffset + i, Character.BYTES);
		return outOffset;
	}

	//------------------------------------------------------------------

	private static String dateToString(
		int	year,
		int	month,
		int	day)
	{
		return String.format("%04d-%02d-%02d", year, month, day);
	}

	//------------------------------------------------------------------

	private static String timeToString(
		int	hour,
		int	minute,
		int	second)
	{
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	//------------------------------------------------------------------

	private static String dateTimeToString(
		int	year,
		int	month,
		int	day,
		int	hour,
		int	minute,
		int	second)
	{
		return dateToString(year, month, day) + " " + timeToString(hour, minute, second);
	}

	//------------------------------------------------------------------

	private static void addEntryMessage(
		Entry			entry,
		String			message,
		List<String>	messages)
	{
		if (messages != null)
		{
			// Initialise buffer
			StringBuilder buffer = new StringBuilder(256);

			// Append pathname of entry
			String pathname = entry.getPathname();
			if (pathname != null)
			{
				buffer.append(PATHNAME_STR);
				buffer.append(": ");
				buffer.append(pathname);
				buffer.append('\n');
			}

			// Append kind of entry
			buffer.append(ENTRY_KIND_STR);
			buffer.append(": ");
			buffer.append(entry.getKind().text.toLowerCase());
			buffer.append('\n');

			// Append message
			buffer.append(message);

			// Add compound message to list
			messages.add(buffer.toString());
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : ITreeNode interface
////////////////////////////////////////////////////////////////////////

	@Override
	public Fat32Directory getParent()
	{
		return parent;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a list of the subdirectories of this directory.
	 *
	 * @return a list of the subdirectories of this directory.
	 * @throws WrappedVolumeException
	 *           if an error occurs when reading the children of this directory.
	 */

	@Override
	public List<Fat32Directory> getChildren()
	{
		// If list of children is uninitialised, read children
		if (children == null)
		{
			List<String> messages = new ArrayList<>();
			try
			{
				readChildren(messages);
			}
			catch (VolumeException e)
			{
				throw new WrappedVolumeException(e);
			}
			finally
			{
				if (!messages.isEmpty())
				{
					StackWalker.StackFrame sf = StackUtils.stackFrame();
					String prefix = StringUtils.getSuffixAfterLast(sf.getClassName(), '.') + "." + sf.getMethodName()
										+ " : " + ERRORS_READING_CHILDREN_STR + " " + getPathname() + "\n----\n";
					Logger.INSTANCE.error(messages.stream().collect(Collectors.joining("\n----\n", prefix, "")));
				}
			}
		}

		// Return list of children
		return children;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(
		Object	obj)
	{
		if (this == obj)
			return true;

		return (obj instanceof Fat32Directory other) && getPathname().equals(other.getPathname());
	}

	//------------------------------------------------------------------

	@Override
	public int hashCode()
	{
		return getPathname().hashCode();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public Fat32Volume getVolume()
	{
		return volume;
	}

	//------------------------------------------------------------------

	public Entry getEntryInParent()
	{
		return entryInParent;
	}

	//------------------------------------------------------------------

	public int getNumEntries()
	{
		return entries.size();
	}

	//------------------------------------------------------------------

	public Entry getEntry(
		int	index)
	{
		return entries.get(index);
	}

	//------------------------------------------------------------------

	public List<Entry> getEntries()
	{
		return Collections.unmodifiableList(entries);
	}

	//------------------------------------------------------------------

	public Entry findEntry(
		String	name)
	{
		for (Entry entry : entries)
		{
			if (name.equals(entry.getName()))
				return entry;
		}
		return null;
	}

	//------------------------------------------------------------------

	public Fat32Directory getDirectory(
		Entry	entry)
	{
		Fat32Directory directory = null;
		if (entry.name.equals(SPECIAL_DIRECTORY_NAME_THIS))
			directory = this;
		else if (entry.name.equals(SPECIAL_DIRECTORY_NAME_PARENT))
			directory = getParent();
		else
			directory = getSubdirectory(entry);
		return directory;
	}

	//------------------------------------------------------------------

	public Fat32Directory getSubdirectory(
		Entry	entry)
	{
		for (Fat32Directory child : getChildren())
		{
			if (child.entryInParent == entry)
				return child;
		}
		return null;
	}

	//------------------------------------------------------------------

	public int getClusterIndex()
	{
		return clusterIndex;
	}

	//------------------------------------------------------------------

	public void setClusterIndex(
		int	index)
	{
		clusterIndex = index;
	}

	//------------------------------------------------------------------

	public long getSectorIndex()
	{
		return volume.clusterIndexToSectorIndex(clusterIndex);
	}

	//------------------------------------------------------------------

	public boolean isRoot()
	{
		return (parent == null);
	}

	//------------------------------------------------------------------

	public String getName()
	{
		return (entryInParent == null) ? "" : entryInParent.getName();
	}

	//------------------------------------------------------------------

	public List<Fat32Directory> getPath()
	{
		// Initialise list of directories
		LinkedList<Fat32Directory> elements = new LinkedList<>();

		// Collect directories, ascending from this directory to root
		Fat32Directory directory = this;
		while (directory != null)
		{
			elements.addFirst(directory);
			directory = directory.parent;
		}

		// Return list of directories
		return elements;
	}

	//------------------------------------------------------------------

	public String getPathname()
	{
		// If this is root directory, return name separator
		if (parent == null)
			return NAME_SEPARATOR;

		// Collect names of directories, ascending from this directory to root
		List<String> names = new ArrayList<>();
		Fat32Directory directory = this;
		while (directory != null)
		{
			names.add(directory.getName());
			directory = directory.parent;
		}

		// Concatenate names of directories below root
		StringBuilder buffer = new StringBuilder(256);
		for (int i = names.size() - 2; i >= 0; i--)
		{
			buffer.append(NAME_SEPARATOR_CHAR);
			buffer.append(names.get(i));
		}

		// Return pathname
		return buffer.toString();
	}

	//------------------------------------------------------------------

	public void read(
		List<String>	messages)
		throws VolumeException
	{
		// Read directory clusters
		byte[] buffer = readData();

		// Invalidate list of children
		children = null;

		// Parse entries
		parse(buffer, buffer.length / Entry.SIZE, messages);
	}

	//------------------------------------------------------------------

	public byte[] readData()
		throws VolumeException
	{
		return readData(true);
	}

	//------------------------------------------------------------------

	public byte[] readData(
		boolean	openClose)
		throws VolumeException
	{
		// Get FAT
		Fat32Fat fat = volume.getFat();

		// Allocate buffer for directory
		int bytesPerCluster = volume.getBytesPerCluster();
		byte[] buffer = new byte[fat.clusterCount(clusterIndex) * bytesPerCluster];

		// Open volume for reading
		if (openClose)
			volume.open(Volume.Access.READ);

		// Read directory
		try
		{
			int offset = 0;
			Fat32Fat.IndexIterator it = fat.indexIterator(clusterIndex);
			while (it.hasNext())
			{
				// Seek cluster
				volume.seekSector(volume.clusterIndexToSectorIndex(it.next()));

				// Read cluster
				volume.read(buffer, offset, bytesPerCluster);
				offset += bytesPerCluster;
			}
		}
		finally
		{
			// Close volume
			if (openClose)
				volume.close();
		}

		// Return buffer
		return buffer;
	}

	//------------------------------------------------------------------

	public void readChildren(
		List<String>	messages)
		throws VolumeException
	{
		// Initialise list of children
		children = new ArrayList<>();

		// Read subdirectories
		for (Entry entry : entries)
		{
			if (entry.isRegularDirectory())
			{
				// Create directory
				Fat32Directory directory = new Fat32Directory(volume, this, entry);

				// Read directory
				directory.read(messages);

				// Add directory to list of children
				children.add(directory);
			}
		}
	}

	//------------------------------------------------------------------

	public void readTree(
		List<String>	messages,
		ICancellable	taskStatus)
		throws VolumeException
	{
		if (!taskStatus.isCancelled())
		{
			// Read this directory
			read(messages);

			// Initialise list of children
			children = new ArrayList<>();

			// Read subdirectories
			for (Entry entry : entries)
			{
				// Test whether task has been cancelled
				if (taskStatus.isCancelled())
					break;

				// If entry is regular directory, read directory and its subdirectories
				if (entry.isRegularDirectory())
				{
					// Create directory
					Fat32Directory directory = new Fat32Directory(volume, this, entry);

					// Add directory to list of children
					children.add(directory);

					// Read directory and its subdirectories
					directory.readTree(messages, taskStatus);
				}
			}
		}
	}

	//------------------------------------------------------------------

	public void writeData(
		byte[]	data,
		int		offset)
		throws VolumeException
	{
		writeData(data, offset, true);
	}

	//------------------------------------------------------------------

	public void writeData(
		byte[]	data,
		int		offset,
		boolean	openClose)
		throws VolumeException
	{
		// Validate arguments
		if (data == null)
			throw new IllegalArgumentException("Null data");
		if ((offset < 0) || (offset > data.length))
			throw new IllegalArgumentException("Offset out of bounds: " + offset);

		// Get FAT
		Fat32Fat fat = volume.getFat();

		// Test for enough data
		int bytesPerCluster = volume.getBytesPerCluster();
		if (fat.clusterCount(clusterIndex) * bytesPerCluster > data.length - offset)
			throw new IllegalArgumentException("Not enough data");

		// Open volume for writing
		if (openClose)
			volume.open(Volume.Access.WRITE);

		// Write directory
		try
		{
			Fat32Fat.IndexIterator it = fat.indexIterator(clusterIndex);
			while (it.hasNext())
			{
				// Seek cluster
				volume.seekSector(volume.clusterIndexToSectorIndex(it.next()));

				// Write cluster
				volume.write(data, offset, bytesPerCluster);
				offset += bytesPerCluster;
			}
		}
		finally
		{
			// Close volume
			if (openClose)
				volume.close();
		}
	}

	//------------------------------------------------------------------

	public void sortByName(
		boolean	ignoreCase)
		throws VolumeException
	{
		// Read directory clusters
		byte[] inData = readData();

		// Sort entries by name
		entries.sort(ignoreCase ? Entry.NAME_IGNORE_CASE_COMPARATOR : Entry.NAME_COMPARATOR);

		// Invalidate list of children
		children = null;

		// Allocate buffer for new directory entries
		byte[] outData = new byte[inData.length];

		// Copy entry data in new order
		int index = 0;
		for (Entry entry : entries)
		{
			System.arraycopy(inData, entry.index * Entry.SIZE, outData, index * Entry.SIZE, entry.length * Entry.SIZE);
			entry.index = index;
			index += entry.length;
		}

		// Write directory clusters
		writeData(outData, 0);
	}

	//------------------------------------------------------------------

	public int eraseDeletedEntries()
		throws VolumeException
	{
		// Get list of indices of deleted entries
		List<Integer> indices = findDeletedEntries(null).stream().map(Entry::getIndex).toList();

		// If there are deleted entries, erase them
		if (!indices.isEmpty())
		{
			// Read directory clusters
			byte[] inData = readData();

			// Allocate buffer for new directory entries
			byte[] outData = new byte[inData.length];

			// Copy entries, omitting deleted entries
			int index = 0;
			for (Entry entry : entries)
			{
				if (!indices.contains(entry.index))
				{
					System.arraycopy(inData, entry.index * Entry.SIZE, outData, index * Entry.SIZE,
									 entry.length * Entry.SIZE);
					entry.index = index;
					index += entry.length;
				}
			}

			// Write directory clusters
			writeData(outData, 0);
		}

		// Return count of erased entries
		return indices.size();
	}

	//------------------------------------------------------------------

	public void updateVolumeLabel(
		String			volumeLabel,
		LocalDateTime	lastModificationTime)
		throws VolumeException
	{
		int offset = 0;
		for (int i = 0; i < getNumEntries(); i++)
		{
			// Get entry
			Entry entry = entries.get(i);

			// If entry is volume label, update it
			if (entry.isVolumeLabel())
			{
				// Update name
				entry.shortName = volumeLabel;
				entry.name = volumeLabel;

				// Update last modification time
				if (lastModificationTime != null)
					entry.lastModificationTime = lastModificationTime;

				// Read directory clusters
				byte[] data = readData();

				// Update volume label
				Utils.stringToBytes(volumeLabel, data, offset + SHORT_NAME_OFFSET, VOLUME_LABEL_LENGTH);

				// Update last modification time
				if (lastModificationTime != null)
					setLastModificationTime(lastModificationTime, data, offset);

				// Write directory clusters
				writeData(data, 0);

				// End of search
				break;
			}

			// Increment offset
			offset += entry.getLength();
		}
	}

	//------------------------------------------------------------------

	public List<Entry> findDeletedEntries(
		List<String>	messages)
		throws VolumeException
	{
		// Read directory clusters
		byte[] buffer = readData();

		// Initialise list of deleted entries
		List<Entry> deletedEntries = new ArrayList<>();

		// Collect deleted entries
		int numEntries = buffer.length / Entry.SIZE;
		int index = 0;
		try
		{
			Fat32Fat fat = volume.getFat();
			Deque<String> filenameParts = new ArrayDeque<>();
			char[] lfnChars = new char[LFN_CHARS_PER_ENTRY];
			while (index < numEntries)
			{
				// Calculate offset of entry within buffer
				int offset = index * Entry.SIZE;

				// Get first byte of entry
				int b = buffer[offset] & 0xFF;

				// If no more entries, stop
				if (b == END_OF_DIRECTORY)
					break;

				// If entry is deleted ...
				if (b == DELETED_ENTRY)
				{
					// Get attributes
					int attrs = NumberCodec.bytesToUIntLE(buffer, offset + ATTRIBUTES_OFFSET, ATTRIBUTES_LENGTH);

					// Case: long-filename entry
					if (attrs == LFN_ATTRS)
					{
						// Test type of long-filename entry
						if (NumberCodec.bytesToUIntLE(buffer, offset + LFN_TYPE_OFFSET, LFN_TYPE_LENGTH) != LFN_TYPE)
							throw new EntryException(ErrorMsg.UNRECOGNISED_LFN_TYPE);

						// Get characters of long filename
						int lfnOffset = 0;
						lfnOffset = getLfnChars(buffer, offset + LFN_CHARS1_OFFSET, LFN_CHARS1_LENGTH, lfnChars,
												lfnOffset);
						lfnOffset = getLfnChars(buffer, offset + LFN_CHARS2_OFFSET, LFN_CHARS2_LENGTH, lfnChars,
												lfnOffset);
						lfnOffset = getLfnChars(buffer, offset + LFN_CHARS3_OFFSET, LFN_CHARS3_LENGTH, lfnChars,
												lfnOffset);
						String str = new String(lfnChars);

						// Find end of long filename
						int i = str.indexOf(LFN_END_CHAR);
						if (i >= 0)
							str = str.substring(0, i);

						// Add part of long filename to list
						filenameParts.addFirst(str);
					}

					// Case: normal entry (ie, not long filename)
					else
					{
						// Initialise entry
						Entry entry = new Entry(this);

						// Get number of long-filename entries
						int lfnNumEntries = filenameParts.size();

						// Set length and index on entry
						entry.length = lfnNumEntries + 1;
						entry.index = index - lfnNumEntries;

						// Set attributes on entry
						entry.setAttributes(attrs);

						// If there is a long filename, set it on entry
						if (lfnNumEntries > 0)
						{
							entry.name = filenameParts.stream().collect(Collectors.joining());
							filenameParts.clear();
						}

						// Get short-filename stem
						String snStem = "?" + getShortName(buffer, offset + SHORT_NAME_OFFSET + 1,
														   SHORT_NAME_LENGTH - 1);

						// Get short-filename extension
						String snExtension = getShortName(buffer, offset + SHORT_NAME_EXTENSION_OFFSET,
														  SHORT_NAME_EXTENSION_LENGTH);

						// Set short filename on entry
						String separator = entry.isVolumeLabel() ? "" : EXTENSION_SEPARATOR;
						entry.shortName = snExtension.isEmpty() ? snStem : snStem + separator + snExtension;

						// If there is no explicit long filename, set long filename from short filename
						if (entry.name == null)
						{
							int nameCase = NumberCodec.bytesToUIntLE(buffer, offset + NAME_CASE_OFFSET,
																	 NAME_CASE_LENGTH);
							entry.name = ((nameCase & LFN_STEM_CASE_MASK) == 0) ? snStem : snStem.toLowerCase();
							if (!snExtension.isEmpty())
							{
								entry.name += separator + (((nameCase & LFN_EXTENSION_CASE_MASK) == 0)
																   ? snExtension
																   : snExtension.toLowerCase());
							}
						}

						// Get file length and set it on entry
						if (entry.isFile())
						{
							entry.fileLength = NumberCodec.bytesToUIntLE(buffer, offset + FILE_LENGTH_OFFSET,
																		 FILE_LENGTH_LENGTH);
						}

						// Case: entry is volume label
						if (entry.isVolumeLabel())
							decodeLastModificationDateTime(buffer, offset, entry, messages);

						// Case: entry is not volume label
						else
						{
							// Decode cluster index
							try
							{
								decodeClusterIndex(buffer, offset, fat, entry);
							}
							catch (EntryException e)
							{
								addEntryMessage(entry, e.getMessage(), messages);
							}

							// Set sector index
							if (entry.clusterIndex > 0)
								entry.sectorIndex = volume.clusterIndexToSectorIndex(entry.clusterIndex);

							// Decode creation date and time
							decodeCreationDateTime(buffer, offset, entry, messages);

							// Decode last modification date and time
							decodeLastModificationDateTime(buffer, offset, entry, messages);

							// Decode access date
							decodeAccessDate(buffer, offset, entry, messages);
						}

						// Add entry to list
						deletedEntries.add(entry);
					}
				}

				// Increment entry index
				++index;
			}
		}
		catch (EntryException e)
		{
			throw new VolumeException(ErrorMsg.INVALID_DIRECTORY_ENTRY, e, getPathname(), index);
		}

		// Return list of deleted entries
		return deletedEntries;
	}

	//------------------------------------------------------------------

	private void parse(
		byte[]			buffer,
		int				numEntries,
		List<String>	messages)
		throws VolumeException
	{
		// Clear entries
		entries.clear();

		// Parse entries
		int index = 0;
		try
		{
			Fat32Fat fat = volume.getFat();
			Deque<String> filenameParts = new ArrayDeque<>();
			boolean longFilename = false;
			int lfnIndex = 0;
			char[] lfnChars = new char[LFN_CHARS_PER_ENTRY];
			while (index < numEntries)
			{
				// Calculate offset of entry within buffer
				int offset = index * Entry.SIZE;

				// Get first byte of entry
				int b = buffer[offset] & 0xFF;

				// If no more entries, stop
				if (b == END_OF_DIRECTORY)
					break;

				// Case: long-filename entry
				if (longFilename)
				{
					// Validate index of long-filename entry
					if ((b & LFN_INDEX_MASK) != lfnIndex)
						throw new EntryException(ErrorMsg.LFN_INDEX_OUT_OF_SEQUENCE);

					// Test type of long-filename entry
					if (NumberCodec.bytesToUIntLE(buffer, offset + LFN_TYPE_OFFSET, LFN_TYPE_LENGTH) != LFN_TYPE)
						throw new EntryException(ErrorMsg.UNRECOGNISED_LFN_TYPE);

					// Get characters of long filename
					int lfnOffset = 0;
					lfnOffset = getLfnChars(buffer, offset + LFN_CHARS1_OFFSET, LFN_CHARS1_LENGTH, lfnChars, lfnOffset);
					lfnOffset = getLfnChars(buffer, offset + LFN_CHARS2_OFFSET, LFN_CHARS2_LENGTH, lfnChars, lfnOffset);
					lfnOffset = getLfnChars(buffer, offset + LFN_CHARS3_OFFSET, LFN_CHARS3_LENGTH, lfnChars, lfnOffset);
					String str = new String(lfnChars);

					// Find end of long filename
					int i = str.indexOf(LFN_END_CHAR);
					if (i >= 0)
						str = str.substring(0, i);

					// Add part of long filename to list
					filenameParts.addFirst(str);

					// Decrement index of long-filename entry
					if (--lfnIndex == 0)
						longFilename = false;
				}

				// Case: normal entry (ie, not long filename)
				else if (b != DELETED_ENTRY)
				{
					// Get attributes
					int attrs = NumberCodec.bytesToUIntLE(buffer, offset + ATTRIBUTES_OFFSET, ATTRIBUTES_LENGTH);

					// Long-filename entry ...
					if (attrs == LFN_ATTRS)
					{
						if ((b & LFN_FIRST_ENTRY_MASK) == 0)
							throw new EntryException(ErrorMsg.FIRST_ENTRY_OF_LFN_EXPECTED);

						lfnIndex = b & LFN_INDEX_MASK;
						if ((lfnIndex < LFN_MIN_INDEX) || (lfnIndex > LFN_MAX_INDEX))
							throw new EntryException(ErrorMsg.LFN_INDEX_OUT_OF_BOUNDS);

						longFilename = true;
						--index;
					}

					// Normal entry (ie, not long filename) ...
					else
					{
						// Initialise entry
						Entry entry = new Entry(this);

						// Get number of long-filename entries
						int lfnNumEntries = filenameParts.size();

						// Set length and index on entry
						entry.length = lfnNumEntries + 1;
						entry.index = index - lfnNumEntries;

						// Set attributes on entry
						entry.setAttributes(attrs);

						// If there is a long filename, set it on entry and verify checksum of short filename
						if (lfnNumEntries > 0)
						{
							// Get long filename and set it on entry
							entry.name = filenameParts.stream().collect(Collectors.joining());
							filenameParts.clear();

							// Calculate checksum of short filename
							int checksum = 0;
							int j = offset + SHORT_NAME_OFFSET;
							for (int i = 0; i < SHORT_NAME_LENGTH + SHORT_NAME_EXTENSION_LENGTH; i++)
								checksum = (((checksum << 7) & 0x80) | ((checksum >> 1) & 0x7F)) + buffer[j++];
							checksum &= 0xFF;

							// Verify checksum in each LFN entry
							int currIndex = index;
							index -= lfnNumEntries;
							while (index < currIndex)
							{
								if (NumberCodec.bytesToUIntLE(buffer, index * Entry.SIZE + LFN_CHECKSUM_OFFSET,
															  LFN_CHECKSUM_LENGTH) != checksum)
									throw new EntryException(ErrorMsg.INCORRECT_SFN_CHECKSUM);
								++index;
							}
						}

						// Get short-filename stem
						int snOffset = offset + SHORT_NAME_OFFSET;
						if (b == DELETED_ENTRY_PLACEHOLDER)
							buffer[snOffset] = (byte)DELETED_ENTRY;
						String snStem = getShortName(buffer, snOffset, SHORT_NAME_LENGTH);
						if (b == DELETED_ENTRY_PLACEHOLDER)
							buffer[snOffset] = (byte)DELETED_ENTRY_PLACEHOLDER;

						// Get short-filename extension
						snOffset = offset + SHORT_NAME_EXTENSION_OFFSET;
						String snExtension = getShortName(buffer, snOffset, SHORT_NAME_EXTENSION_LENGTH);

						// Set short filename on entry
						String separator = entry.isVolumeLabel() ? "" : EXTENSION_SEPARATOR;
						entry.shortName = snExtension.isEmpty() ? snStem : snStem + separator + snExtension;

						// If there is no explicit long filename, set long filename from short filename
						if (entry.name == null)
						{
							int nameCase = NumberCodec.bytesToUIntLE(buffer, offset + NAME_CASE_OFFSET,
																	 NAME_CASE_LENGTH);
							entry.name = ((nameCase & LFN_STEM_CASE_MASK) == 0) ? snStem : snStem.toLowerCase();
							if (!snExtension.isEmpty())
							{
								entry.name += separator + (((nameCase & LFN_EXTENSION_CASE_MASK) == 0)
																				? snExtension
																				: snExtension.toLowerCase());
							}
						}

						// Get file length and set it on entry
						if (entry.isFile())
						{
							entry.fileLength = NumberCodec.bytesToUIntLE(buffer, offset + FILE_LENGTH_OFFSET,
																		 FILE_LENGTH_LENGTH);
						}

						// Case: entry is volume label
						if (entry.isVolumeLabel())
							decodeLastModificationDateTime(buffer, offset, entry, messages);

						// Case: entry is not volume label
						else
						{
							// Decode cluster index
							decodeClusterIndex(buffer, offset, fat, entry);

							// Set number of clusters and sector index
							if (entry.clusterIndex > 0)
							{
								// Count number of clusters
								int numClusters = fat.clusterCount(entry.clusterIndex);

								// If entry is file, validate file length against number of clusters
								if (entry.isFile())
								{
									if (numClusters != NumberUtils.roundUpQuotientLong(entry.getFileLength(),
																					   volume.getBytesPerCluster()))
										throw new EntryException(ErrorMsg.INCONSISTENT_FILE_LENGTH, entry.getName());
								}

								// Set number of clusters on entry
								entry.numClusters = numClusters;

								// Set sector index on entry
								entry.sectorIndex = volume.clusterIndexToSectorIndex(entry.clusterIndex);
							}

							// Decode creation date and time
							decodeCreationDateTime(buffer, offset, entry, messages);

							// Decode last modification date and time
							decodeLastModificationDateTime(buffer, offset, entry, messages);

							// Decode access date
							decodeAccessDate(buffer, offset, entry, messages);
						}

						// Add entry to list
						entries.add(entry);
					}
				}

				// Increment entry index
				++index;
			}
		}
		catch (EntryException e)
		{
			throw new VolumeException(ErrorMsg.INVALID_DIRECTORY_ENTRY, e, getPathname(), index);
		}
		finally
		{
			// Close volume
			if (volume.isOpen())
				volume.close();
		}
	}

	//------------------------------------------------------------------

	private void decodeClusterIndex(
		byte[]		buffer,
		int			offset,
		Fat32Fat	fat,
		Entry		entry)
		throws EntryException, VolumeException
	{
		// Get cluster index
		int clusterIndex = getClusterIndex(buffer, offset);

		// Validate cluster index
		if ((entry.isFile() && (entry.fileLength == 0))
				|| (entry.name.equals(SPECIAL_DIRECTORY_NAME_PARENT) && (parent != null) && parent.isRoot()))
		{
			if (clusterIndex != 0)
				throw new EntryException(ErrorMsg.CLUSTER_INDEX_ZERO_EXPECTED, clusterIndex);
		}
		else if ((clusterIndex < volume.getRootDirClusterIndex()) || (clusterIndex > volume.getMaxClusterIndex()))
			throw new EntryException(ErrorMsg.CLUSTER_INDEX_OUT_OF_BOUNDS, clusterIndex);

		// Set cluster index on entry
		entry.clusterIndex = clusterIndex;
	}

	//------------------------------------------------------------------

	private void decodeCreationDateTime(
		byte[]			buffer,
		int				offset,
		Entry			entry,
		List<String>	messages)
	{
		// Decode date components
		int date = NumberCodec.bytesToUIntLE(buffer, offset + CREATION_DATE_OFFSET, CREATION_DATE_LENGTH);
		int day = BitUtils.getBitField(date, DAY_FIELD_SHIFT, DAY_FIELD_LENGTH);
		int month = BitUtils.getBitField(date, MONTH_FIELD_SHIFT, MONTH_FIELD_LENGTH);
		int year = BASE_YEAR + BitUtils.getBitField(date, YEAR_FIELD_SHIFT, YEAR_FIELD_LENGTH);

		// Decode time components
		int centisecond = NumberCodec.bytesToUIntLE(buffer, offset + CREATION_TIME_CS_OFFSET, CREATION_TIME_CS_LENGTH);
		int time = NumberCodec.bytesToUIntLE(buffer, offset + CREATION_TIME_OFFSET, CREATION_TIME_LENGTH);
		int second = BitUtils.getBitField(time, SECOND_FIELD_SHIFT, SECOND_FIELD_LENGTH) * 2;
		int minute = BitUtils.getBitField(time, MINUTE_FIELD_SHIFT, MINUTE_FIELD_LENGTH);
		int hour = BitUtils.getBitField(time, HOUR_FIELD_SHIFT, HOUR_FIELD_LENGTH);

		// Set date and time on entry
		try
		{
			entry.creationTime = getDateTime(year, month, day, hour, minute, second, centisecond);
		}
		catch (DateTimeException e)
		{
			addEntryMessage(entry,
							String.format(ErrorMsg.INVALID_DIRECTORY_ENTRY_FIELD, CREATION_DATE_TIME_STR,
										  dateTimeToString(year, month, day, hour, minute, second)),
							messages);
		}
	}

	//------------------------------------------------------------------

	private void decodeLastModificationDateTime(
		byte[]			buffer,
		int				offset,
		Entry			entry,
		List<String>	messages)
	{
		// Decode date components
		int date = NumberCodec.bytesToUIntLE(buffer, offset + LAST_MODIFICATION_DATE_OFFSET,
											 LAST_MODIFICATION_DATE_LENGTH);
		int day = BitUtils.getBitField(date, DAY_FIELD_SHIFT, DAY_FIELD_LENGTH);
		int month = BitUtils.getBitField(date, MONTH_FIELD_SHIFT, MONTH_FIELD_LENGTH);
		int year = BASE_YEAR + BitUtils.getBitField(date, YEAR_FIELD_SHIFT, YEAR_FIELD_LENGTH);

		// Decode time components
		int time = NumberCodec.bytesToUIntLE(buffer, offset + LAST_MODIFICATION_TIME_OFFSET,
											 LAST_MODIFICATION_TIME_LENGTH);
		int second = BitUtils.getBitField(time, SECOND_FIELD_SHIFT, SECOND_FIELD_LENGTH) * 2;
		int minute = BitUtils.getBitField(time, MINUTE_FIELD_SHIFT, MINUTE_FIELD_LENGTH);
		int hour = BitUtils.getBitField(time, HOUR_FIELD_SHIFT, HOUR_FIELD_LENGTH);

		// Set date and time on entry
		try
		{
			entry.lastModificationTime = getDateTime(year, month, day, hour, minute, second, 0);
		}
		catch (DateTimeException e)
		{
			addEntryMessage(entry,
							String.format(ErrorMsg.INVALID_DIRECTORY_ENTRY_FIELD, LAST_MODIFICATION_DATE_TIME_STR,
										  dateTimeToString(year, month, day, hour, minute, second)),
							messages);
		}
	}

	//------------------------------------------------------------------

	private void decodeAccessDate(
		byte[]			buffer,
		int				offset,
		Entry			entry,
		List<String>	messages)
	{
		// Decode date
		int date = NumberCodec.bytesToUIntLE(buffer, offset + ACCESS_DATE_OFFSET, ACCESS_DATE_LENGTH);
		if (date != 0)
		{
			// Decode date components
			int day = BitUtils.getBitField(date, DAY_FIELD_SHIFT, DAY_FIELD_LENGTH);
			int month = BitUtils.getBitField(date, MONTH_FIELD_SHIFT, MONTH_FIELD_LENGTH);
			int year = BASE_YEAR + BitUtils.getBitField(date, YEAR_FIELD_SHIFT, YEAR_FIELD_LENGTH);

			// Set date on entry
			try
			{
				entry.accessDate = getDate(year, month, day);
			}
			catch (DateTimeException e)
			{
				addEntryMessage(entry,
								String.format(ErrorMsg.INVALID_DIRECTORY_ENTRY_FIELD, ACCESS_DATE_STR,
											  dateToString(year, month, day)),
								messages);
			}
		}
	}

	//------------------------------------------------------------------

	private LocalDate getDate(
		int	year,
		int	month,
		int	day)
	{
		// Case: convert date unchanged
		if (!volume.isFixDirEntryDatesTimes())
			return LocalDate.of(year, month, day);

		// Clamp month
		if (month < MIN_MONTH)
			month = MIN_MONTH;
		else if (month > MAX_MONTH)
			month = MAX_MONTH;

		// Clamp day
		if (day < MIN_DAY)
			day = MIN_DAY;
		else if (day > MAX_DAY)
			day = MAX_DAY;

		// Repeatedly try to convert date; decrement day after each failure
		while (true)
		{
			try
			{
				return LocalDate.of(year, month, day);
			}
			catch (DateTimeException e)
			{
				if (--day < MIN_MAX_DAY)
					throw e;
			}
		}
	}

	//------------------------------------------------------------------

	private LocalDateTime getDateTime(
		int	year,
		int	month,
		int	day,
		int	hour,
		int	minute,
		int	second,
		int centisecond)
	{
		// Case: convert date and time unchanged
		if (!volume.isFixDirEntryDatesTimes())
		{
			second += centisecond / 100;
			return LocalDateTime.of(year, month, day, hour, minute, second, (centisecond % 100) * 10_000_000);
		}

		// Clamp month
		if (month < MIN_MONTH)
			month = MIN_MONTH;
		else if (month > MAX_MONTH)
			month = MAX_MONTH;

		// Clamp day
		if (day < MIN_DAY)
			day = MIN_DAY;
		else if (day > MAX_DAY)
			day = MAX_DAY;

		// Clamp hour
		if (hour < MIN_HOUR)
			hour = MIN_HOUR;
		else if (hour > MAX_HOUR)
			hour = MAX_HOUR;

		// Clamp minute
		if (minute < MIN_MINUTE)
			minute = MIN_MINUTE;
		else if (minute > MAX_MINUTE)
			minute = MAX_MINUTE;

		// Clamp second
		if (second < MIN_SECOND)
			second = MIN_SECOND;
		else if (second > MAX_SECOND)
			second = MAX_SECOND;

		// Clamp centisecond
		if (centisecond < MIN_CENTISECOND)
			centisecond = MIN_CENTISECOND;
		else if (centisecond > MAX_CENTISECOND)
			centisecond = MAX_CENTISECOND;

		// Adjust second from centisecond
		second += centisecond / 100;

		// Convert centisecond to nanosecond
		int nanosecond = (centisecond % 100) * 10_000_000;

		// Repeatedly try to convert date and time; decrement day after each failure
		while (true)
		{
			try
			{
				return LocalDateTime.of(year, month, day, hour, minute, second, nanosecond);
			}
			catch (DateTimeException e)
			{
				if (--day < MIN_MAX_DAY)
					throw e;
			}
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: ENTRY ATTRIBUTES


	public enum Attr
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		READ_ONLY,
		HIDDEN,
		SYSTEM,
		VOLUME_LABEL,
		DIRECTORY,
		ARCHIVE;

		public static final	Attr[]	DISPLAYED_ATTRS	=
		{
			DIRECTORY,
			VOLUME_LABEL,
			SYSTEM,
			HIDDEN,
			READ_ONLY
		};

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Attr()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		public static String attrsToString(
			Collection<Attr>	attrs)
		{
			char[] chars = new char[values().length];
			Arrays.fill(chars, '.');
			for (int i = 0; i < chars.length; i++)
			{
				Attr attr = values()[i];
				if (attrs.contains(attr))
					chars[chars.length - i - 1] = attr.getIdChar();
			}
			return new String(chars);
		}

		//--------------------------------------------------------------

		public static int attrsToBitArray(
			Collection<Attr>	attrs)
		{
			int bits = 0;
			for (Attr attr : Attr.values())
			{
				if (attrs.contains(attr))
					bits |= 1 << attr.ordinal();
			}
			return bits;
		}

		//--------------------------------------------------------------

		public static double getMaxIdCharWidth()
		{
			double maxWidth = 0.0;
			for (Attr attr : DISPLAYED_ATTRS)
			{
				double width = TextUtils.textWidth(FontUtils.boldFont(), Character.toString(attr.getIdChar()));
				if (maxWidth < width)
					maxWidth = width;
			}
			return Math.ceil(maxWidth);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public String getKey()
		{
			return StringUtils.toCamelCase(name());
		}

		//--------------------------------------------------------------

		public char getIdChar()
		{
			return name().charAt(0);
		}

		//--------------------------------------------------------------

		private int getMask()
		{
			return 1 << ordinal();
		}

		//--------------------------------------------------------------

	}

	//==================================================================

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: DIRECTORY ENTRY


	public static class Entry
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		public static final		Comparator<Entry>	NAME_COMPARATOR	=
				Comparator.comparing(Entry::getKind).thenComparing(Entry::getName);

		public static final		Comparator<Entry>	NAME_IGNORE_CASE_COMPARATOR	=
				Comparator.comparing(Entry::getKind).thenComparing(Entry::getName, String.CASE_INSENSITIVE_ORDER);

		public static final		int		SIZE	= 32;

		private static final	String	NAME_STR					= "name";
		private static final	String	PATHNAME_STR				= "pathname";
		private static final	String	KIND_STR					= "kind";
		private static final	String	ATTRIBUTES_STR				= "attributes";
		private static final	String	LENGTH_STR					= "length";
		private static final	String	CREATION_TIME_STR			= "creation time";
		private static final	String	LAST_MODIFICATION_TIME_STR	= "last modification time";
		private static final	String	ACCESS_DATE_STR				= "access date";
		private static final	String	CLUSTER_INDEX_STR			= "cluster index";
		private static final	String	NUM_CLUSTERS_STR			= "number of clusters";
		private static final	String	SECTOR_INDEX_STR			= "sector index";

		private static final	String	EQUALS_STR	= " = ";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	Fat32Directory	directory;
		private	int				index;
		private	int				length;
		private	String			name;
		private	String			shortName;
		private	EnumSet<Attr>	attributes;
		private	long			fileLength;
		private	LocalDateTime	creationTime;
		private	LocalDateTime	lastModificationTime;
		private	LocalDate		accessDate;
		private	int				clusterIndex;
		private	int				numClusters;
		private	long			sectorIndex;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Entry(
			Fat32Directory	directory)
		{
			// Initialise instance variables
			this.directory = directory;
			attributes = EnumSet.noneOf(Attr.class);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			StringBuilder buffer = new StringBuilder(256);

			buffer.append(NAME_STR);
			buffer.append(EQUALS_STR);
			buffer.append(getName());
			buffer.append('\n');

			String pathname = getPathname();
			if (pathname != null)
			{
				buffer.append(PATHNAME_STR);
				buffer.append(EQUALS_STR);
				buffer.append(pathname);
				buffer.append('\n');
			}

			buffer.append(KIND_STR);
			buffer.append(EQUALS_STR);
			buffer.append(getKind().text.toLowerCase());
			buffer.append('\n');

			buffer.append(ATTRIBUTES_STR);
			buffer.append(EQUALS_STR);
			buffer.append(getAttributeString());
			buffer.append('\n');

			if (isFile())
			{
				buffer.append(LENGTH_STR);
				buffer.append(EQUALS_STR);
				buffer.append(fileLength);
				buffer.append('\n');
			}

			if (creationTime != null)
			{
				buffer.append(CREATION_TIME_STR);
				buffer.append(EQUALS_STR);
				buffer.append(getCreationTimeString());
				buffer.append('\n');
			}

			if (lastModificationTime != null)
			{
				buffer.append(LAST_MODIFICATION_TIME_STR);
				buffer.append(EQUALS_STR);
				buffer.append(getLastModificationTimeString());
				buffer.append('\n');
			}

			if (accessDate != null)
			{
				buffer.append(ACCESS_DATE_STR);
				buffer.append(EQUALS_STR);
				buffer.append(getAccessDateString());
				buffer.append('\n');
			}

			if (clusterIndex != 0)
			{
				buffer.append(CLUSTER_INDEX_STR);
				buffer.append(EQUALS_STR);
				buffer.append(clusterIndex);
				buffer.append('\n');

				buffer.append(NUM_CLUSTERS_STR);
				buffer.append(EQUALS_STR);
				buffer.append(numClusters);
				buffer.append('\n');

				buffer.append(SECTOR_INDEX_STR);
				buffer.append(EQUALS_STR);
				buffer.append(sectorIndex);
				buffer.append('\n');
			}

			return buffer.toString();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public Fat32Directory getDirectory()
		{
			return directory;
		}

		//--------------------------------------------------------------

		public int getIndex()
		{
			return index;
		}

		//--------------------------------------------------------------

		public int getLength()
		{
			return length;
		}

		//--------------------------------------------------------------

		public String getName()
		{
			return (name == null) ? shortName : name;
		}

		//--------------------------------------------------------------

		public String getPathname()
		{
			String pathname = null;
			if (!isVolumeLabel())
			{
				pathname = directory.getPathname();
				String name = getName();
				pathname = pathname.endsWith(NAME_SEPARATOR) ? pathname + name : pathname + NAME_SEPARATOR + name;
			}
			return pathname;
		}

		//--------------------------------------------------------------

		public boolean isDirectory()
		{
			return attributes.contains(Attr.DIRECTORY);
		}

		//--------------------------------------------------------------

		public boolean isRegularDirectory()
		{
			return attributes.contains(Attr.DIRECTORY)
						&& !(name.equals(SPECIAL_DIRECTORY_NAME_THIS) || name.equals(SPECIAL_DIRECTORY_NAME_PARENT));
		}

		//--------------------------------------------------------------

		public boolean isSpecialDirectory()
		{
			return attributes.contains(Attr.DIRECTORY)
						&& (name.equals(SPECIAL_DIRECTORY_NAME_THIS) || name.equals(SPECIAL_DIRECTORY_NAME_PARENT));
		}

		//--------------------------------------------------------------

		public boolean isVolumeLabel()
		{
			return attributes.contains(Attr.VOLUME_LABEL);
		}

		//--------------------------------------------------------------

		public boolean isFile()
		{
			return !(attributes.contains(Attr.DIRECTORY) || attributes.contains(Attr.VOLUME_LABEL));
		}

		//--------------------------------------------------------------

		public Kind getKind()
		{
			return attributes.contains(Attr.VOLUME_LABEL)
							? Kind.VOLUME_LABEL
							: attributes.contains(Attr.DIRECTORY)
									? (name.equals(SPECIAL_DIRECTORY_NAME_THIS)
												|| name.equals(SPECIAL_DIRECTORY_NAME_PARENT))
											? Kind.SPECIAL_DIRECTORY
											: Kind.DIRECTORY
									: Kind.FILE;
		}

		//--------------------------------------------------------------

		public boolean hasAttribute(
			Attr	attr)
		{
			return attributes.contains(attr);
		}

		//--------------------------------------------------------------

		public EnumSet<Attr> getAttributes()
		{
			return attributes;
		}

		//--------------------------------------------------------------

		public void setAttributes(
			int	bits)
		{
			attributes.clear();
			for (Attr attr : Attr.values())
			{
				if ((bits & attr.getMask()) != 0)
					attributes.add(attr);
			}
		}

		//--------------------------------------------------------------

		public long getFileLength()
		{
			return fileLength;
		}

		//--------------------------------------------------------------

		public LocalDateTime getCreationTime()
		{
			return creationTime;
		}

		//--------------------------------------------------------------

		public LocalDateTime getLastModificationTime()
		{
			return lastModificationTime;
		}

		//--------------------------------------------------------------

		public LocalDate getAccessDate()
		{
			return accessDate;
		}

		//--------------------------------------------------------------

		public int getClusterIndex()
		{
			return clusterIndex;
		}

		//--------------------------------------------------------------

		public void setClusterIndex(
			int	index)
		{
			// Update cluster index
			clusterIndex = index;

			// Update sector index
			sectorIndex = directory.getVolume().clusterIndexToSectorIndex(index);
		}

		//--------------------------------------------------------------

		public int getNumClusters()
		{
			return numClusters;
		}

		//--------------------------------------------------------------

		public long getSectorIndex()
		{
			return sectorIndex;
		}

		//--------------------------------------------------------------

		public String getAttributeString()
		{
			return Attr.attrsToString(attributes);
		}

		//--------------------------------------------------------------

		public String getCreationTimeString()
		{
			return (creationTime == null) ? "" : CREATION_TIME_FORMATTER.format(creationTime);
		}

		//--------------------------------------------------------------

		public String getLastModificationTimeString()
		{
			return (lastModificationTime == null) ? "" : LAST_MODIFICATION_TIME_FORMATTER.format(lastModificationTime);
		}

		//--------------------------------------------------------------

		public String getAccessDateString()
		{
			return (accessDate == null) ? "" : ACCESS_DATE_FORMATTER.format(accessDate);
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Enumerated types
	////////////////////////////////////////////////////////////////////


		// ENUMERATION: ENTRY KIND


		public enum Kind
		{

		////////////////////////////////////////////////////////////////
		//  Constants
		////////////////////////////////////////////////////////////////

			VOLUME_LABEL
			(
				"Volume label"
			),

			SPECIAL_DIRECTORY
			(
				"Special directory"
			),

			DIRECTORY
			(
				"Directory"
			),

			FILE
			(
				"File"
			);

		////////////////////////////////////////////////////////////////
		//  Instance variables
		////////////////////////////////////////////////////////////////

			private	String	text;

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private Kind(
				String	text)
			{
				// Initialise instance variables
				this.text = text;
			}

			//----------------------------------------------------------

		////////////////////////////////////////////////////////////////
		//  Instance methods : overriding methods
		////////////////////////////////////////////////////////////////

			@Override
			public String toString()
			{
				return text;
			}

			//----------------------------------------------------------

		}

		//==============================================================

	}

	//==================================================================


	// CLASS: DIRECTORY-ENTRY EXCEPTION


	private static class EntryException
		extends Exception
	{

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private EntryException(
			String		message,
			Object...	replacements)
		{
			// Call superclass constructor
			super(String.format(message, replacements));
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
