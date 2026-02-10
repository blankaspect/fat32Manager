/*====================================================================*\

Fat32Volume.java

Class: FAT32 volume.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import java.util.function.Predicate;

import uk.blankaspect.common.function.IFunction1;
import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.common.map.InsertionOrderStringMap;

import uk.blankaspect.common.number.DiscretisedValue;
import uk.blankaspect.common.number.LinearDiscretiser;
import uk.blankaspect.common.number.NumberCodec;
import uk.blankaspect.common.number.NumberUtils;

import uk.blankaspect.common.string.StringUtils;

import uk.blankaspect.common.task.ITaskStatus;

import uk.blankaspect.driveio.IVolumeAccessor;
import uk.blankaspect.driveio.Volume;
import uk.blankaspect.driveio.VolumeException;

//----------------------------------------------------------------------


// CLASS: FAT32 VOLUME


public class Fat32Volume
	extends Volume
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		int		MIN_SECTORS_PER_CLUSTER	= 1;
	public static final		int		MAX_SECTORS_PER_CLUSTER	= 128;

	private static final	int		MIN_SECTOR_SIZE	= 512;
	private static final	int		NUM_SUPPORTED_SECTOR_SIZES	= 4;

	private static final	int		NUM_BOOT_SECTORS	= 3;

	private static final	int		BOOT_SECTOR_JUMP_OFFSET	= 0x00;

	private static final	int		BOOT_SECTOR_JUMP1_OFFSET	= BOOT_SECTOR_JUMP_OFFSET;
	private static final	int		BOOT_SECTOR_JUMP1_LENGTH	= 1;
	private static final	int		BOOT_SECTOR_JUMP1			= 0xEB;		// jmp <disp8>

	private static final	int		BOOT_SECTOR_JUMP3_OFFSET	= 0x02;
	private static final	int		BOOT_SECTOR_JUMP3_LENGTH	= 1;
	private static final	int		BOOT_SECTOR_JUMP3			= 0x90;		// nop

	private static final	int		BOOT_SECTOR_FORMATTER_NAME_OFFSET	= 0x03;
	private static final	int		BOOT_SECTOR_FORMATTER_NAME_LENGTH	= 8;

	private static final	int		BOOT_SECTOR_SIGNATURE_OFFSET	= 0x1FE;
	private static final	int		BOOT_SECTOR_SIGNATURE_LENGTH	= 2;
	private static final	int		BOOT_SECTOR_SIGNATURE			= 0xAA55;

	private static final	int		BPB_BYTES_PER_SECTOR_OFFSET	= 0x0B;
	private static final	int		BPB_BYTES_PER_SECTOR_LENGTH	= 2;

	private static final	int		BPB_SECTORS_PER_CLUSTER_OFFSET	= 0x0D;
	private static final	int		BPB_SECTORS_PER_CLUSTER_LENGTH	= 1;

	private static final	int		BPB_NUM_RESERVED_SECTORS_OFFSET	= 0x0E;
	private static final	int		BPB_NUM_RESERVED_SECTORS_LENGTH	= 2;

	private static final	int		BPB_NUM_FATS_OFFSET	= 0x10;
	private static final	int		BPB_NUM_FATS_LENGTH	= 1;

	private static final	int		BPB_MAX_NUM_ROOT_DIR_ENTRIES_OFFSET	= 0x11;
	private static final	int		BPB_MAX_NUM_ROOT_DIR_ENTRIES_LENGTH	= 2;
	private static final	int		BPB_MAX_NUM_ROOT_DIR_ENTRIES		= 0;

	private static final	int		BPB_NUM_SECTORS_OFFSET	= 0x13;
	private static final	int		BPB_NUM_SECTORS_LENGTH	= 2;

	private static final	int		BPB_MEDIA_DESCRIPTOR_OFFSET	= 0x15;
	private static final	int		BPB_MEDIA_DESCRIPTOR_LENGTH	= 1;
	private static final	int		BPB_MEDIA_DESCRIPTOR		= 0xF8;
	private static final	int		BPB_MIN_MEDIA_DESCRIPTOR	= 0xF0;
	private static final	int		BPB_MAX_MEDIA_DESCRIPTOR	= 0xFF;

	private static final	int		BPB_SECTORS_PER_FAT_OFFSET	= 0x16;
	private static final	int		BPB_SECTORS_PER_FAT_LENGTH	= 2;
	private static final	int		BPB_SECTORS_PER_FAT			= 0;

	private static final	int		BPB_SECTORS_PER_TRACK_OFFSET	= 0x18;
	private static final	int		BPB_SECTORS_PER_TRACK_LENGTH	= 2;

	private static final	int		BPB_NUM_DISK_HEADS_OFFSET	= 0x1A;
	private static final	int		BPB_NUM_DISK_HEADS_LENGTH	= 2;

	private static final	int		BPB_NUM_HIDDEN_SECTORS_OFFSET	= 0x1C;
	private static final	int		BPB_NUM_HIDDEN_SECTORS_LENGTH	= 4;

	private static final	int		BPB_NUM_SECTORS_EX_OFFSET	= 0x20;
	private static final	int		BPB_NUM_SECTORS_EX_LENGTH	= 4;

	private static final	int		EBPB_SECTORS_PER_FAT_OFFSET	= 0x24;
	private static final	int		EBPB_SECTORS_PER_FAT_LENGTH	= 4;

	private static final	int		EBPB_VERSION_OFFSET	= 0x2A;
	private static final	int		EBPB_VERSION_LENGTH	= 2;
	private static final	int		EBPB_VERSION		= 0;

	private static final	int		EBPB_ROOT_DIR_CLUSTER_INDEX_OFFSET	= 0x2C;
	private static final	int		EBPB_ROOT_DIR_CLUSTER_INDEX_LENGTH	= 4;

	private static final	int		EBPB_FS_INFO_SECTOR_INDEX_OFFSET	= 0x30;
	private static final	int		EBPB_FS_INFO_SECTOR_INDEX_LENGTH	= 2;

	private static final	int		EBPB_BOOT_SECTOR_COPY_INDEX_OFFSET	= 0x32;
	private static final	int		EBPB_BOOT_SECTOR_COPY_INDEX_LENGTH	= 2;

	private static final	int		EBPB_SIGNATURE_OFFSET	= 0x42;
	private static final	int		EBPB_SIGNATURE_LENGTH	= 1;
	private static final	int		EBPB_SIGNATURE			= 0x29;

	private static final	int		EBPB_VOLUME_ID_OFFSET	= 0x43;
	private static final	int		EBPB_VOLUME_ID_LENGTH	= 4;

	private static final	int		EBPB_VOLUME_LABEL_OFFSET	= 0x47;
	private static final	int		EBPB_VOLUME_LABEL_LENGTH	= 11;

	private static final	int		EBPB_FILE_SYSTEM_NAME_OFFSET	= 0x52;
	private static final	int		EBPB_FILE_SYSTEM_NAME_LENGTH	= 8;

	private static final	int		BOOT_CODE_OFFSET	= EBPB_FILE_SYSTEM_NAME_OFFSET + EBPB_FILE_SYSTEM_NAME_LENGTH;

	private static final	String	FILE_SYSTEM_NAME	= "FAT32";

	private static final	int		FS_INFO_SIGNATURE1_OFFSET	= 0x00;
	private static final	int		FS_INFO_SIGNATURE1_LENGTH	= 4;
	private static final	String	FS_INFO_SIGNATURE1			= "RRaA";

	private static final	int		FS_INFO_SIGNATURE2_OFFSET	= 0x1E4;
	private static final	int		FS_INFO_SIGNATURE2_LENGTH	= 4;
	private static final	String	FS_INFO_SIGNATURE2			= "rrAa";

	private static final	int		FS_INFO_NUM_FREE_CLUSTERS_OFFSET	= 0x1E8;
	private static final	int		FS_INFO_NUM_FREE_CLUSTERS_LENGTH	= 4;

	private static final	int		FS_INFO_LAST_ALLOC_CLUSTER_OFFSET	= 0x1EC;
	private static final	int		FS_INFO_LAST_ALLOC_CLUSTER_LENGTH	= 4;

	public static final		int		FORMAT_MAX_NUM_SECTORS			= 1 << 30;	// 1,073,741,824
	public static final		int		FORMAT_MIN_NUM_RESERVED_SECTORS	= 16;
	public static final		int		FORMAT_NUM_FATS					= 2;
	private static final	int		FORMAT_SECTORS_PER_TRACK		= 63;	//XXX
	private static final	int		FORMAT_NUM_DISK_HEADS			= 255;	//XXX
	private static final	int		FORMAT_ROOT_DIR_CLUSTER_INDEX	= 2;
	private static final	int		FORMAT_FS_INFO_SECTOR_INDEX		= 1;
	private static final	int		FORMAT_BOOT_SECTOR_COPY_INDEX	= 6;

	private static final	byte[]	JUMP_CODE	=
	{
		(byte)BOOT_SECTOR_JUMP1,
		(byte)BOOT_CODE_OFFSET - 2,
		(byte)BOOT_SECTOR_JUMP3
	};

	private static final	byte[]	BOOT_CODE	=
	{
		/*
		005A  0E			push   cs
		005B  1F			pop    ds
		005C  E8 00 00		call   loc_005F
						loc_005F:
		005F  58			pop    ax
		0060  05 1E 00		add    ax,1Eh
		0063  8B F0			mov    si,ax
						loc_0065:
		0065  AC			lods   al,BYTE PTR ds:[si]
		0066  22 C0			and    al,al
		0068  74 0B			jz     loc_0075
		006A  56			push   si
		006B  B4 0E			mov    ah,0Eh
		006D  BB 07 00		mov    bx,07h
		0070  CD 10			int    10h
		0072  5E			pop    si
		0073  EB F0			jmp    loc_0065
						loc_0075:
		0075  32 E4			xor    ah,ah
		0077  CD 16			int    16h
		0079  CD 19			int    19h
						loc_007B:
		007B  EB FE			jmp    loc_007B
		007D
		*/
		(byte)0x0E, (byte)0x1F, (byte)0xE8, (byte)0x00, (byte)0x00, (byte)0x58, (byte)0x05, (byte)0x1E,
		(byte)0x00, (byte)0x8B, (byte)0xF0, (byte)0xAC, (byte)0x22, (byte)0xC0, (byte)0x74, (byte)0x0B,
		(byte)0x56, (byte)0xB4, (byte)0x0E, (byte)0xBB, (byte)0x07, (byte)0x00, (byte)0xCD, (byte)0x10,
		(byte)0x5E, (byte)0xEB, (byte)0xF0, (byte)0x32, (byte)0xE4, (byte)0xCD, (byte)0x16, (byte)0xCD,
		(byte)0x19, (byte)0xEB, (byte)0xFE,
	};

	private static final	String	BOOT_CODE_MESSAGE	= "This volume is not bootable.\r\n"
															+ "Press any key to reboot.\r\n";

	private static final	byte[]	FAT_ENTRIES	=
	{
		(byte)0xF8, (byte)0xFF, (byte)0xFF, (byte)0x0F,
		(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x0F,
		(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x0F
	};

	private static final	char	MIN_VOLUME_LABEL_CHAR		= '\u0021';
	private static final	char	MAX_VOLUME_LABEL_CHAR		= '\u007E';
	private static final	String	INVALID_VOLUME_LABEL_CHARS	= "\"*+,./:;<=>?[\\]|";

	private static final	String	VOLUME_STR						= "Volume";
	private static final	String	VOLUME_LABEL_STR				= "Volume label";
	private static final	String	ID_STR							= "ID";
	private static final	String	MEDIA_DESCRIPTOR_STR			= "Media descriptor";
	private static final	String	FORMATTER_NAME_STR				= "Formatter name";
	private static final	String	BYTES_PER_SECTOR_STR			= "Bytes per sector";
	private static final	String	SECTORS_PER_CLUSTER_STR			= "Sectors per cluster";
	private static final	String	NUM_SECTORS_STR					= "Number of sectors";
	private static final	String	NUM_HIDDEN_SECTORS_STR			= "Number of hidden sectors";
	private static final	String	NUM_RESERVED_SECTORS_STR		= "Number of reserved sectors";
	private static final	String	NUM_FATS_STR					= "Number of FATs";
	private static final	String	SECTORS_PER_FAT_STR				= "Sectors per FAT";
	private static final	String	BOOT_SECTOR_COPY_INDEX_STR		= "Index of copy of boot sector";
	private static final	String	ROOT_DIR_CLUSTER_INDEX_STR		= "Cluster index of root directory";
	private static final	String	NUM_CLUSTERS_STR				= "Number of clusters";
	private static final	String	NUM_UNUSED_CLUSTERS_STR			= "Number of unused clusters";
	private static final	String	VALIDATING_STR					= "Validating";
	private static final	String	COUNTING_UNUSED_CLUSTERS_STR	= "Counting unused clusters";
	private static final	String	ERASING_UNUSED_CLUSTERS_STR		= "Erasing unused clusters";
	private static final	String	VOLUME_NOT_INITIALISED_STR		= "Volume not initialised";
	private static final	String	DEFRAGMENTING_STR				= "Defragmenting";
	private static final	String	UPDATING_PARENT_DIRECTORY_STR	= "Updating parent directory";
	private static final	String	UPDATING_FATS_STR				= "Updating FATs";
	private static final	String	OPENING_STR						= "Opening";
	private static final	String	FORMATTING_STR					= "Formatting";
	private static final	String	READING_BOOT_SECTOR_STR			= "Reading boot sector";
	private static final	String	WRITING_BOOT_SECTOR_STR			= "Writing boot sector";
	private static final	String	COPY_STR						= " copy";
	private static final	String	UPDATING_ROOT_DIR_STR			= "Updating root directory";
	private static final	String	SECTOR_INDEX_OUT_OF_BOUNDS_STR	= "Sector index out of bounds: ";
	private static final	String	CLUSTER_INDEX_OUT_OF_BOUNDS_STR	= "Cluster index out of bounds: ";
	private static final	String	NUM_SECTORS_OUT_OF_BOUNDS_STR	= "Number of sectors out of bounds: ";

	public enum DefragStatus
	{
		NOT_FRAGMENTED,
		NOT_ENOUGH_SPACE,
		SUCCESS,
		CANCEL
	}

	private interface ErrorMsg
	{
		String	VOLUME_NOT_MOUNTED =
				"The volume is not mounted.";

		String	FILE_SYSTEM_NOT_FAT32 =
				"The file system is not recognised as FAT32.";

		String	INVALID_BYTES_PER_SECTOR =
				"The number of bytes per sector (%d) is not valid.";

		String	INCONSISTENT_SECTOR_PARAMETERS =
				"The sector parameters of the volume are inconsistent.";

		String	TOO_FEW_SECTORS_PER_FAT =
				"The number of sectors per FAT (%d) is less than required.";

		String	FAT_ENTRIES_DIFFER =
				"The entries of FATs 0 and %d differ at index %d.";

		String	UNEXPECTED_FAT_ENTRY =
				"A FAT entry has an unexpected value.";

		String	UNEXPECTED_DIRECTORY_ENTRY =
				"A directory entry has an unexpected value.";

		String	FAILED_TO_VERIFY_LAST_SECTOR =
				"Failed to verify the last sector of the volume.";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String			volumeLabel;
	private	int				id;
	private	int				mediaDescriptor;
	private	String			formatterName;
	private	int				bytesPerSector;
	private	int				sectorsPerCluster;
	private	long			numSectors;
	private	int				numHiddenSectors;
	private	int				numReservedSectors;
	private	int				numFats;
	private	int				sectorsPerFat;
	private	int				bootSectorCopyIndex;
	private	int				rootDirClusterIndex;
	private	int				rootDirNumClusters;
	private	Fat32Fat		fat;
	private	Fat32Directory	rootDir;
	private	boolean			fixDirEntryDatesTimes;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Fat32Volume(
		String			name,
		IVolumeAccessor	accessor)
	{
		// Call superclass constructor
		super(name, accessor);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static List<Integer> getValidSectorsPerCluster(
		int					bytesPerSector,
		int					numSectors,
		int					numReservedSectors,
		ClusterAlignment	clusterAlignment,
		boolean				alignFats)
	{
		// Initialise list of sectors per cluster
		List<Integer> validSectorsPerCluster = new ArrayList<>();

		// Populate list of sectors per cluster
		for (int sectorsPerCluster = MIN_SECTORS_PER_CLUSTER; sectorsPerCluster <= MAX_SECTORS_PER_CLUSTER;
				sectorsPerCluster *= 2)
		{
			FormatParams params = getFormatParams(bytesPerSector, numSectors, numReservedSectors, sectorsPerCluster,
												  clusterAlignment, alignFats);
			if (params != null)
			{
				int numEntries = (numSectors - params.minNumReservedSectors - FORMAT_NUM_FATS * params.sectorsPerFat)
										/ sectorsPerCluster + Fat32Fat.MIN_CLUSTER_INDEX;
				if (numEntries <= Fat32Fat.MAX_CLUSTER_INDEX)
					validSectorsPerCluster.add(sectorsPerCluster);
			}
		}

		// Return list of sectors per cluster
		return validSectorsPerCluster;
	}

	//------------------------------------------------------------------

	public static boolean isValidVolumeLabelChar(
		char	ch)
	{
		return (ch >= MIN_VOLUME_LABEL_CHAR) && (ch <= MAX_VOLUME_LABEL_CHAR)
				&& (INVALID_VOLUME_LABEL_CHARS.indexOf(ch) < 0);
	}

	//------------------------------------------------------------------

	public static boolean isSupportedSectorSize(
		int	size)
	{
		int sectorSize = MIN_SECTOR_SIZE;
		for (int i = 0; i < NUM_SUPPORTED_SECTOR_SIZES; i++)
		{
			if (size == sectorSize)
				return true;
			sectorSize <<= 1;
		}
		return false;
	}

	//------------------------------------------------------------------

	public static Params readVolumeParams(
		String			volumeName,
		IVolumeAccessor volumeAccessor)
		throws VolumeException
	{
		// Validate argument
		if (volumeName == null)
			throw new IllegalArgumentException("Null volume name");

		// Test for volume
		if (!volumeAccessor.getVolumeNames().contains(volumeName))
			throw new VolumeException(getErrorMessage(ErrorMsg.VOLUME_NOT_MOUNTED, volumeName));

		// Read volume parameters from BIOS parameter block in sector 0
		Params params = null;
		Volume volume = new Volume(volumeName, volumeAccessor);
		try
		{
			// Open volume for reading
			volume.open(Access.READ);

			// Read first sector of volume
			byte[] paramBlock = new byte[MIN_SECTOR_SIZE];
			volume.read(paramBlock);

			// Test for FAT32
			if (!isFat32(paramBlock))
				throw new VolumeException(getErrorMessage(ErrorMsg.FILE_SYSTEM_NOT_FAT32, volumeName));

			// Decode media descriptor
			int mediaDescriptor = NumberCodec.bytesToUIntLE(paramBlock, BPB_MEDIA_DESCRIPTOR_OFFSET,
															BPB_MEDIA_DESCRIPTOR_LENGTH);

			// Decode bytes per sector
			int bytesPerSector = NumberCodec.bytesToUIntLE(paramBlock, BPB_BYTES_PER_SECTOR_OFFSET,
														   BPB_BYTES_PER_SECTOR_LENGTH);

			// Check that bytes per sector is power of two
			if (Integer.bitCount(bytesPerSector) != 1)
			{
				throw new VolumeException(getErrorMessage(ErrorMsg.INVALID_BYTES_PER_SECTOR, volumeName),
										  bytesPerSector);
			}

			// Decode sectors per cluster
			int sectorsPerCluster = NumberCodec.bytesToUIntLE(paramBlock, BPB_SECTORS_PER_CLUSTER_OFFSET,
															  BPB_SECTORS_PER_CLUSTER_LENGTH);

			// Decode number of reserved sectors
			int numReservedSectors = NumberCodec.bytesToUIntLE(paramBlock, BPB_NUM_RESERVED_SECTORS_OFFSET,
															   BPB_NUM_RESERVED_SECTORS_LENGTH);

			// Decode number of hidden sectors
			int numHiddenSectors = NumberCodec.bytesToUIntLE(paramBlock, BPB_NUM_HIDDEN_SECTORS_OFFSET,
															 BPB_NUM_HIDDEN_SECTORS_LENGTH);

			// Decode number of sectors
			long numSectors = NumberCodec.bytesToULongLE(paramBlock, BPB_NUM_SECTORS_OFFSET, BPB_NUM_SECTORS_LENGTH);
			if (numSectors == 0)
				numSectors = NumberCodec.bytesToULongLE(paramBlock, BPB_NUM_SECTORS_EX_OFFSET,
														BPB_NUM_SECTORS_EX_LENGTH);

			// Decode number of FATs
			int numFats = NumberCodec.bytesToUIntLE(paramBlock, BPB_NUM_FATS_OFFSET, BPB_NUM_FATS_LENGTH);

			// Decode sectors per FAT
			int sectorsPerFat = NumberCodec.bytesToUIntLE(paramBlock, BPB_SECTORS_PER_FAT_OFFSET,
														  BPB_SECTORS_PER_FAT_LENGTH);
			if (sectorsPerFat == 0)
				sectorsPerFat = NumberCodec.bytesToUIntLE(paramBlock, EBPB_SECTORS_PER_FAT_OFFSET,
														  EBPB_SECTORS_PER_FAT_LENGTH);

			// Decode index of copy of boot sector
			int bootSectorCopyIndex = NumberCodec.bytesToUIntLE(paramBlock, EBPB_BOOT_SECTOR_COPY_INDEX_OFFSET,
																EBPB_BOOT_SECTOR_COPY_INDEX_LENGTH);

			// Validate parameters
			if ((numSectors < numReservedSectors + numFats * sectorsPerFat)
					|| (numReservedSectors < bootSectorCopyIndex + NUM_BOOT_SECTORS))
				throw new VolumeException(getErrorMessage(ErrorMsg.INCONSISTENT_SECTOR_PARAMETERS, volumeName));

			// Decode cluster index of root directory
			int rootDirClusterIndex = NumberCodec.bytesToUIntLE(paramBlock, EBPB_ROOT_DIR_CLUSTER_INDEX_OFFSET,
																EBPB_ROOT_DIR_CLUSTER_INDEX_LENGTH);

			// Decode volume ID
			int id = NumberCodec.bytesToUIntLE(paramBlock, EBPB_VOLUME_ID_OFFSET, EBPB_VOLUME_ID_LENGTH);

			// Decode volume label
			String volumeLabel = Utils.bytesToString(paramBlock, EBPB_VOLUME_LABEL_OFFSET, EBPB_VOLUME_LABEL_LENGTH);

			// Decode formatter name
			String formatterName = Utils.bytesToString(paramBlock, BOOT_SECTOR_FORMATTER_NAME_OFFSET,
													   BOOT_SECTOR_FORMATTER_NAME_LENGTH);

			// Create volume parameters
			params = new Params(volumeLabel, id, mediaDescriptor, formatterName, bytesPerSector, sectorsPerCluster,
								numSectors, numHiddenSectors, numReservedSectors, numFats, sectorsPerFat,
								bootSectorCopyIndex, rootDirClusterIndex);
		}
		finally
		{
			// Close volume
			if (volume.isOpen())
				volume.close();
		}

		// Return parameters
		return params;
	}

	//------------------------------------------------------------------

	public static FormatParams getFormatParams(
		int					bytesPerSector,
		int					numSectors,
		int					minNumReservedSectors,
		int					sectorsPerCluster,
		ClusterAlignment	clusterAlignment,
		boolean				alignFats)
	{
		// Initialise format parameters
		FormatParams params = null;

		// Calculate FAT entries per sector
		int fatEntriesPerSector = bytesPerSector / Fat32Fat.ENTRY_SIZE;

		// Calculate format parameters
		switch (clusterAlignment)
		{
			case NONE:
			{
				// Create a function to calculate the number of FAT entries
				IFunction1<Integer, Integer> getNumEntries = sectorsPerFat ->
						(numSectors - minNumReservedSectors - FORMAT_NUM_FATS * sectorsPerFat) / sectorsPerCluster
								+ Fat32Fat.MIN_CLUSTER_INDEX;

				// Create a function to test whether the sectors per FAT can accommodate all FAT entries
				Predicate<Integer> isValid = sectorsPerFat ->
						(getNumEntries.invoke(sectorsPerFat) <= sectorsPerFat * fatEntriesPerSector);

				// Repeatedly double the sectors per FAT until the FAT can accommodate all entries
				int sectorsPerFat = 1;
				while (sectorsPerFat > 0)
				{
					if (isValid.test(sectorsPerFat))
						break;
					sectorsPerFat *= 2;
				}

				// If the sectors per FAT has not overflowed ...
				if (sectorsPerFat > 0)
				{
					// Find the lower bound of the sectors per FAT by binary search
					if (sectorsPerFat > 1)
					{
						int upper = sectorsPerFat;
						int lower = upper / 2;
						while (lower < upper)
						{
							int median = (lower + upper) / 2;
							if (isValid.test(median))
								upper = median;
							else
								lower = median + 1;
						}
						sectorsPerFat = lower;
					}

					// Set parameters
					params = new FormatParams(minNumReservedSectors, sectorsPerFat);
				}
				break;
			}

			case INTEGRAL_MULTIPLE:
			case POWER_OF_TWO_MULTIPLE:
			{
				// Adjust minimum number of reserved sectors to align FATs
				int minNumReservedSectors0 = alignFats
												? NumberUtils.roundUpInt(minNumReservedSectors, sectorsPerCluster)
												: minNumReservedSectors;

				// Create a function to calculate the number of FAT entries
				IFunction1<Integer, Integer> getNumEntries = numHeaderSectors ->
						(numSectors - numHeaderSectors) / sectorsPerCluster + Fat32Fat.MIN_CLUSTER_INDEX;

				// Create a function to test whether the number of header sectors gives enough sectors per FAT to
				// accommodate all FAT entries
				Predicate<Integer> isValid = numHeaderSectors ->
				{
					int sectorsPerFat = (numHeaderSectors - minNumReservedSectors0) / FORMAT_NUM_FATS;
					return (getNumEntries.invoke(numHeaderSectors) <= sectorsPerFat * fatEntriesPerSector);
				};

				// Repeatedly double the number of header sectors (reserved sectors and FATs) until the FAT can
				// accommodate all entries
				int numHeaderSectors = sectorsPerCluster;
				while (numHeaderSectors > 0)
				{
					if (isValid.test(numHeaderSectors))
						break;
					numHeaderSectors *= 2;
				}

				// If the number of header sectors has not overflowed ...
				if (numHeaderSectors > 0)
				{
					// Calculate the number of sectors by which to align clusters
					int numAlignmentSectors = alignFats ? FORMAT_NUM_FATS * sectorsPerCluster : sectorsPerCluster;

					// If clusters are to be aligned on an integral multiple of sectors per cluster, find the lower
					// bound of the number of header sectors by binary search
					if ((clusterAlignment == ClusterAlignment.INTEGRAL_MULTIPLE)
							&& (numHeaderSectors > numAlignmentSectors))
					{
						int upper = numHeaderSectors / numAlignmentSectors;
						int lower = upper / 2;
						while (lower < upper)
						{
							int median = (lower + upper) / 2;
							if (isValid.test(median * numAlignmentSectors))
								upper = median;
							else
								lower = median + 1;
						}
						numHeaderSectors = lower * numAlignmentSectors;
					}

					// Get sectors per FAT
					int sectorsPerFat = (getNumEntries.invoke(numHeaderSectors) + fatEntriesPerSector - 1)
														/ fatEntriesPerSector;
					if (alignFats)
						sectorsPerFat = NumberUtils.roundUpInt(sectorsPerFat, sectorsPerCluster);

					// Set parameters
					params = new FormatParams(numHeaderSectors - FORMAT_NUM_FATS * sectorsPerFat, sectorsPerFat);
				}
				break;
			}
		}

		// Return format parameters
		return params;
	}

	//------------------------------------------------------------------

	public static void format(
		String			name,
		int				volumeId,
		String			volumeLabel,
		String			formatterName,
		int				bytesPerSector,
		int				startSector,
		int				numSectors,
		int				numReservedSectors,
		int				sectorsPerCluster,
		int				sectorsPerFat,
		IVolumeAccessor	accessor,
		ITaskStatus		taskStatus)
		throws VolumeException
	{
		final	int	BUFFER_NUM_SECTORS	= 128;

		// Get number of available sectors
		int numHeaderSectors = numReservedSectors + FORMAT_NUM_FATS * sectorsPerFat;
		int numClusters = (numSectors - numHeaderSectors) / sectorsPerCluster;
		int numAvailableSectors = numHeaderSectors + numClusters * sectorsPerCluster;

		// Allocate buffer for sector data
		byte[] buffer = new byte[BUFFER_NUM_SECTORS * bytesPerSector];

		// Create volume
		Fat32Volume volume = new Fat32Volume(name, accessor);

		// Write sectors
		try
		{
			// Update task message; set indeterminate progress
			taskStatus.setMessage(OPENING_STR + " " + name);
			taskStatus.setProgress(-1.0);

			// Open volume for writing
			volume.open(Access.WRITE);

			// Update task message; reset progress
			taskStatus.setMessage(FORMATTING_STR + " " + name);
			taskStatus.setProgress(0.0);

			// Initialise loop variables
			int sectorsWritten = 0;
			int sectorsRemaining = 0;
			int numSectorsToWrite = numReservedSectors + FORMAT_NUM_FATS * sectorsPerFat + sectorsPerCluster;
			int maxProgress = numSectorsToWrite + 4;
			int index = 0;

			// Write boot sectors and other reserved sectors, FATs and root directory
			while (index < numSectorsToWrite)
			{
				// Test whether task has been cancelled
				if (taskStatus.isCancelled())
					break;

				// Section: boot sectors and other reserved sectors
				if (index == 0)
				{
					// Create boot sectors
					int offset = 0;
					createBootSector1(bytesPerSector, startSector, numAvailableSectors, numReservedSectors,
									  sectorsPerCluster, sectorsPerFat, volumeId, volumeLabel, formatterName, buffer,
									  offset);
					offset += bytesPerSector;
					createBootSector2(buffer, offset);
					offset += bytesPerSector;
					createBootSector3(buffer, offset);
					offset += bytesPerSector;

					// Create copy of boot sectors
					System.arraycopy(buffer, 0, buffer, FORMAT_BOOT_SECTOR_COPY_INDEX * bytesPerSector, offset);

					// Initialise sector counts
					sectorsWritten = 0;
					sectorsRemaining = numReservedSectors;
				}

				// Section: first FAT
				else if (index == numReservedSectors)
				{
					// Clear previous section from buffer
					int offset = 0;
					int length = NUM_BOOT_SECTORS * bytesPerSector;
					Arrays.fill(buffer, offset, offset + length, (byte)0);
					offset = FORMAT_BOOT_SECTOR_COPY_INDEX * bytesPerSector;
					Arrays.fill(buffer, offset, offset + length, (byte)0);

					// Create FAT sector
					createFatSector(buffer, 0);

					// Initialise sector counts
					sectorsWritten = 0;
					sectorsRemaining = sectorsPerFat;
				}

				// Section: second FAT
				else if (index == numReservedSectors + sectorsPerFat)
				{
					// Clear previous section from buffer
					Arrays.fill(buffer, 0, bytesPerSector, (byte)0);

					// Create FAT sector
					createFatSector(buffer, 0);

					// Initialise sector counts
					sectorsWritten = 0;
					sectorsRemaining = sectorsPerFat;
				}

				// Section: root directory
				else if (index == numReservedSectors + FORMAT_NUM_FATS * sectorsPerFat)
				{
					// Clear previous section from buffer
					Arrays.fill(buffer, 0, bytesPerSector, (byte)0);

					// Create root directory
					if (!StringUtils.isNullOrEmpty(volumeLabel))
						createRootDirectorySector(volumeLabel, buffer, 0);

					// Initialise sector counts
					sectorsWritten = 0;
					sectorsRemaining = sectorsPerCluster;
				}

				// Clear first sector of buffer before writing second block of section
				if (sectorsWritten == BUFFER_NUM_SECTORS)
					Arrays.fill(buffer, 0, bytesPerSector, (byte)0);

				// Get number of sectors to write
				int blockNumSectors = Math.min(sectorsRemaining, BUFFER_NUM_SECTORS);

				// Write sectors
				volume.write(buffer, 0, blockNumSectors * bytesPerSector);

				// Update sector counts and index
				sectorsWritten += blockNumSectors;
				sectorsRemaining -= blockNumSectors;
				index += blockNumSectors;

				// Update progress
				taskStatus.setProgress((double)index / (double)maxProgress);
			}

			// Close volume
			volume.close();

			// Open volume for reading and writing
			volume.open(Access.READ_WRITE);

			// Get index of last sector of last cluster
			int lastSectorIndex = numAvailableSectors - 1;

			// Initialise sector data values
			byte[] fillValues = { (byte)0xAA, (byte)0x00 };

			// Write last sector, read it and verify it
			for (int i = 0; i < 2; i++)
			{
				// Fill buffer
				byte fillValue = fillValues[i];
				Arrays.fill(buffer, 0, bytesPerSector, fillValue);

				// Write last sector
				volume.seek((long)lastSectorIndex * bytesPerSector);
				volume.write(buffer, 0, bytesPerSector);

				// Update progress
				taskStatus.setProgress((double)++index / (double)maxProgress);

				// Read last sector
				volume.seek((long)lastSectorIndex * bytesPerSector);
				volume.read(buffer, 0, bytesPerSector);

				// Verify data
				for (int j = 0; j < bytesPerSector; j++)
				{
					if (buffer[j] != fillValue)
						throw new VolumeException(ErrorMsg.FAILED_TO_VERIFY_LAST_SECTOR);
				}

				// Update progress
				taskStatus.setProgress((double)++index / (double)maxProgress);
			}
		}
		finally
		{
			// Close volume
			if (volume.isOpen())
				volume.close();
		}
	}

	//------------------------------------------------------------------

	private static boolean isFat32(
		byte[]	paramBlock)
	{
		return isEqual(paramBlock, BOOT_SECTOR_JUMP1_OFFSET, BOOT_SECTOR_JUMP1_LENGTH, BOOT_SECTOR_JUMP1)
				&& isEqual(paramBlock, BOOT_SECTOR_JUMP3_OFFSET, BOOT_SECTOR_JUMP3_LENGTH, BOOT_SECTOR_JUMP3)
				&& isEqual(paramBlock, BOOT_SECTOR_SIGNATURE_OFFSET, BOOT_SECTOR_SIGNATURE_LENGTH,
						   BOOT_SECTOR_SIGNATURE)
				&& isEqual(paramBlock, BPB_MAX_NUM_ROOT_DIR_ENTRIES_OFFSET, BPB_MAX_NUM_ROOT_DIR_ENTRIES_LENGTH,
						   BPB_MAX_NUM_ROOT_DIR_ENTRIES)
				&& isIn(paramBlock, BPB_MEDIA_DESCRIPTOR_OFFSET, BPB_MEDIA_DESCRIPTOR_LENGTH, BPB_MIN_MEDIA_DESCRIPTOR,
						BPB_MAX_MEDIA_DESCRIPTOR)
				&& isEqual(paramBlock, BPB_SECTORS_PER_FAT_OFFSET, BPB_SECTORS_PER_FAT_LENGTH, BPB_SECTORS_PER_FAT)
				&& isEqual(paramBlock, EBPB_VERSION_OFFSET, EBPB_VERSION_LENGTH, EBPB_VERSION)
				&& isEqual(paramBlock, EBPB_SIGNATURE_OFFSET, EBPB_SIGNATURE_LENGTH, EBPB_SIGNATURE)
				&& Arrays.equals(paramBlock, EBPB_FILE_SYSTEM_NAME_OFFSET,
								 EBPB_FILE_SYSTEM_NAME_OFFSET + EBPB_FILE_SYSTEM_NAME_LENGTH,
								 Utils.stringToBytes(FILE_SYSTEM_NAME, EBPB_FILE_SYSTEM_NAME_LENGTH), 0,
								 EBPB_FILE_SYSTEM_NAME_LENGTH);
	}

	//------------------------------------------------------------------

	private static boolean isEqual(
		byte[]	data,
		int		offset,
		int		length,
		int		value)
	{
		return (length == 1) ? (data[offset] == (byte)value)
							 : (NumberCodec.bytesToUIntLE(data, offset, length) == value);
	}

	//------------------------------------------------------------------

	private static boolean isIn(
		byte[]	data,
		int		offset,
		int		length,
		int		minValue,
		int		maxValue)
	{
		int value = NumberCodec.bytesToUIntLE(data, offset, length);
		return (value >= minValue) && (value <= maxValue);
	}

	//------------------------------------------------------------------

	private static String getErrorMessage(
		String	message,
		String	volumeName)
	{
		return VOLUME_STR + ": " + volumeName + "\n" + message;
	}

	//------------------------------------------------------------------

	private static String idToString(
		int	id)
	{
		return NumberUtils.uIntToHexStringUpper(id >>> 16, 4, '0') + "-" + NumberUtils.uIntToHexStringUpper(id, 4, '0');
	}

	//------------------------------------------------------------------

	private static String mediaDescriptorToString(
		int	value)
	{
		return "0x" + NumberUtils.uIntToHexStringUpper(value, 2, '0');
	}

	//------------------------------------------------------------------

	private static void createBootSector1(
		int		bytesPerSector,
		int		startSector,
		int		numSectors,
		int		numReservedSectors,
		int		sectorsPerCluster,
		int		sectorsPerFat,
		int		volumeId,
		String	volumeLabel,
		String	formatterName,
		byte[]	buffer,
		int		offset)
	{
		// JMP instruction
		System.arraycopy(JUMP_CODE, 0, buffer, offset + BOOT_SECTOR_JUMP_OFFSET, JUMP_CODE.length);

		// Formatter name
		Utils.stringToBytes(formatterName, buffer, offset + BOOT_SECTOR_FORMATTER_NAME_OFFSET,
							BOOT_SECTOR_FORMATTER_NAME_LENGTH);

		// Bytes per sector
		NumberCodec.uIntToBytesLE(bytesPerSector, buffer, offset + BPB_BYTES_PER_SECTOR_OFFSET,
								  BPB_BYTES_PER_SECTOR_LENGTH);

		// Sectors per cluster
		NumberCodec.uIntToBytesLE(sectorsPerCluster, buffer, offset + BPB_SECTORS_PER_CLUSTER_OFFSET,
								  BPB_SECTORS_PER_CLUSTER_LENGTH);

		// Number of reserved sectors
		NumberCodec.uIntToBytesLE(numReservedSectors, buffer, offset + BPB_NUM_RESERVED_SECTORS_OFFSET,
								  BPB_NUM_RESERVED_SECTORS_LENGTH);

		// Number of FATs
		NumberCodec.uIntToBytesLE(FORMAT_NUM_FATS, buffer, offset + BPB_NUM_FATS_OFFSET, BPB_NUM_FATS_LENGTH);

		// Media descriptor
		NumberCodec.uIntToBytesLE(BPB_MEDIA_DESCRIPTOR, buffer, offset + BPB_MEDIA_DESCRIPTOR_OFFSET,
								  BPB_MEDIA_DESCRIPTOR_LENGTH);

		// Sectors per track
		NumberCodec.uIntToBytesLE(FORMAT_SECTORS_PER_TRACK, buffer, offset + BPB_SECTORS_PER_TRACK_OFFSET,
								  BPB_SECTORS_PER_TRACK_LENGTH);

		// Number of disk heads
		NumberCodec.uIntToBytesLE(FORMAT_NUM_DISK_HEADS, buffer, offset + BPB_NUM_DISK_HEADS_OFFSET,
								  BPB_NUM_DISK_HEADS_LENGTH);

		// Number of hidden sectors
		NumberCodec.uIntToBytesLE(startSector, buffer, offset + BPB_NUM_HIDDEN_SECTORS_OFFSET,
								  BPB_NUM_HIDDEN_SECTORS_LENGTH);

		// Number of sectors
		NumberCodec.uIntToBytesLE(numSectors, buffer, offset + BPB_NUM_SECTORS_EX_OFFSET, BPB_NUM_SECTORS_EX_LENGTH);

		// Sectors per FAT
		NumberCodec.uIntToBytesLE(sectorsPerFat, buffer, offset + EBPB_SECTORS_PER_FAT_OFFSET,
								  EBPB_SECTORS_PER_FAT_LENGTH);

		// Index of first root-directory cluster
		NumberCodec.uIntToBytesLE(FORMAT_ROOT_DIR_CLUSTER_INDEX, buffer, offset + EBPB_ROOT_DIR_CLUSTER_INDEX_OFFSET,
								  EBPB_ROOT_DIR_CLUSTER_INDEX_LENGTH);

		// Index of FS information sector
		NumberCodec.uIntToBytesLE(FORMAT_FS_INFO_SECTOR_INDEX, buffer, offset + EBPB_FS_INFO_SECTOR_INDEX_OFFSET,
								  EBPB_FS_INFO_SECTOR_INDEX_LENGTH);

		// Index of copy of boot sector
		NumberCodec.uIntToBytesLE(FORMAT_BOOT_SECTOR_COPY_INDEX, buffer, offset + EBPB_BOOT_SECTOR_COPY_INDEX_OFFSET,
								  EBPB_BOOT_SECTOR_COPY_INDEX_LENGTH);

		// Extended BPB signature
		NumberCodec.uIntToBytesLE(EBPB_SIGNATURE, buffer, offset + EBPB_SIGNATURE_OFFSET, EBPB_SIGNATURE_LENGTH);

		// Volume ID
		NumberCodec.uIntToBytesLE(volumeId, buffer, offset + EBPB_VOLUME_ID_OFFSET, EBPB_VOLUME_ID_LENGTH);

		// Volume label
		Utils.stringToBytes(volumeLabel, buffer, offset + EBPB_VOLUME_LABEL_OFFSET, EBPB_VOLUME_LABEL_LENGTH);

		// File-system name
		Utils.stringToBytes(FILE_SYSTEM_NAME, buffer, offset + EBPB_FILE_SYSTEM_NAME_OFFSET,
							EBPB_FILE_SYSTEM_NAME_LENGTH);

		// Boot code
		System.arraycopy(BOOT_CODE, 0, buffer, offset + BOOT_CODE_OFFSET, BOOT_CODE.length);

		// Boot-code message
		Utils.stringToBytes(BOOT_CODE_MESSAGE, buffer, offset + BOOT_CODE_OFFSET + BOOT_CODE.length, 0);

		// Boot-sector signature
		NumberCodec.uIntToBytesLE(BOOT_SECTOR_SIGNATURE, buffer, offset + BOOT_SECTOR_SIGNATURE_OFFSET,
								  BOOT_SECTOR_SIGNATURE_LENGTH);
	}

	//------------------------------------------------------------------

	private static void createBootSector2(
		byte[]	buffer,
		int		offset)
	{
		// Signature 1
		Utils.stringToBytes(FS_INFO_SIGNATURE1, buffer, offset + FS_INFO_SIGNATURE1_OFFSET, FS_INFO_SIGNATURE1_LENGTH);

		// Signature 2
		Utils.stringToBytes(FS_INFO_SIGNATURE2, buffer, offset + FS_INFO_SIGNATURE2_OFFSET, FS_INFO_SIGNATURE2_LENGTH);

		// Number of free clusters
		NumberCodec.intToBytesLE(-1, buffer, offset + FS_INFO_NUM_FREE_CLUSTERS_OFFSET,
								 FS_INFO_NUM_FREE_CLUSTERS_LENGTH);

		// Last allocated cluster
		NumberCodec.intToBytesLE(-1, buffer, offset + FS_INFO_LAST_ALLOC_CLUSTER_OFFSET,
								 FS_INFO_LAST_ALLOC_CLUSTER_LENGTH);

		// Boot-sector signature
		NumberCodec.uIntToBytesLE(BOOT_SECTOR_SIGNATURE, buffer, offset + BOOT_SECTOR_SIGNATURE_OFFSET,
								  BOOT_SECTOR_SIGNATURE_LENGTH);
	}

	//------------------------------------------------------------------

	private static void createBootSector3(
		byte[]	buffer,
		int		offset)
	{
		// Boot-sector signature
		NumberCodec.uIntToBytesLE(BOOT_SECTOR_SIGNATURE, buffer, offset + BOOT_SECTOR_SIGNATURE_OFFSET,
								  BOOT_SECTOR_SIGNATURE_LENGTH);
	}

	//------------------------------------------------------------------

	private static void createFatSector(
		byte[]	buffer,
		int		offset)
	{
		// FAT entries
		System.arraycopy(FAT_ENTRIES, 0, buffer, offset, FAT_ENTRIES.length);
	}

	//------------------------------------------------------------------

	private static void createRootDirectorySector(
		String	volumeLabel,
		byte[]	buffer,
		int		offset)
	{
		// Volume label
		Utils.stringToBytes(volumeLabel, buffer, offset + Fat32Directory.SHORT_NAME_OFFSET,
							Fat32Directory.VOLUME_LABEL_LENGTH);

		// Attributes
		Fat32Directory.setAttributes(EnumSet.of(Fat32Directory.Attr.VOLUME_LABEL), buffer, offset);

		// Modification time
		Fat32Directory.setLastModificationTime(LocalDateTime.now(), buffer, offset);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public void init()
		throws VolumeException
	{
		try
		{
			// Reset bytes per sector to indicate invalid parameters
			bytesPerSector = 0;

			// Read volume parameters from BIOS parameter block
			Params params = readVolumeParams(getName(), getAccessor());

			// Open volume for reading
			open(Access.READ);

			// Update instance variables
			volumeLabel = params.volumeLabel;
			id = params.id;
			mediaDescriptor = params.mediaDescriptor;
			formatterName = params.formatterName;
			bytesPerSector = params.bytesPerSector;
			sectorsPerCluster = params.sectorsPerCluster;
			numSectors = params.numSectors;
			numHiddenSectors = params.numHiddenSectors;
			numReservedSectors = params.numReservedSectors;
			numFats = params.numFats;
			sectorsPerFat = params.sectorsPerFat;
			bootSectorCopyIndex = params.bootSectorCopyIndex;
			rootDirClusterIndex = params.rootDirClusterIndex;

			// Validate sectors per FAT
			int entriesPerFat = getMaxClusterIndex() + 1;
			if (entriesPerFat > sectorsPerFat * bytesPerSector / Fat32Fat.ENTRY_SIZE)
				throw new VolumeException(ErrorMsg.TOO_FEW_SECTORS_PER_FAT, sectorsPerFat);

			// Initialise first FAT
			fat = new Fat32Fat(this);
			fat.init(0);

			// Initialise remaining FATs and compare their entries with those of first FAT
			int fatLength = fat.getLength();
			for (int i = 1; i < numFats; i++)
			{
				// Read FAT
				Fat32Fat fat = new Fat32Fat(this);
				fat.init(i);

				// Compare its entries with those of first FAT
				for (int j = 0; j < fatLength; j++)
				{
					if (fat.get(j) != this.fat.get(j))
						throw new VolumeException(ErrorMsg.FAT_ENTRIES_DIFFER, i, j);
				}
			}

			// Close volume
			close();

			// Set number of clusters in root directory
			rootDirNumClusters = fat.clusterCount(rootDirClusterIndex);

			// Initialise root directory
			rootDir = new Fat32Directory(this, null, null);
		}
		finally
		{
			// Close volume
			if (isOpen())
				close();
		}
	}

	//------------------------------------------------------------------

	public boolean isInitialised()
	{
		return (bytesPerSector > 0);
	}

	//------------------------------------------------------------------

	public int getBytesPerSector()
	{
		return bytesPerSector;
	}

	//------------------------------------------------------------------

	public int getSectorsPerCluster()
	{
		return sectorsPerCluster;
	}

	//------------------------------------------------------------------

	public int getBytesPerCluster()
	{
		return sectorsPerCluster * bytesPerSector;
	}

	//------------------------------------------------------------------

	public long getNumSectors()
	{
		return numSectors;
	}

	//------------------------------------------------------------------

	public int getNumReservedSectors()
	{
		return numReservedSectors;
	}

	//------------------------------------------------------------------

	public int getNumFats()
	{
		return numFats;
	}

	//------------------------------------------------------------------

	public int getSectorsPerFat()
	{
		return sectorsPerFat;
	}

	//------------------------------------------------------------------

	public int getRootDirClusterIndex()
	{
		return rootDirClusterIndex;
	}

	//------------------------------------------------------------------

	public int getRootDirNumClusters()
	{
		return rootDirNumClusters;
	}

	//------------------------------------------------------------------

	public int getId()
	{
		return id;
	}

	//------------------------------------------------------------------

	public String getVolumeLabel()
	{
		return volumeLabel;
	}

	//------------------------------------------------------------------

	public Fat32Fat getFat()
	{
		return fat;
	}

	//------------------------------------------------------------------

	public Fat32Directory getRootDir()
	{
		return rootDir;
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

	public long clusterIndexToSectorIndex(
		int	clusterIndex)
	{
		// Validate argument
		if (clusterIndex < Fat32Fat.MIN_CLUSTER_INDEX)
			throw new IllegalArgumentException(CLUSTER_INDEX_OUT_OF_BOUNDS_STR + clusterIndex);

		// Convert cluster index to sector index and return result
		return (numReservedSectors + numFats * sectorsPerFat)
						+ (long)(clusterIndex - Fat32Fat.MIN_CLUSTER_INDEX) * sectorsPerCluster;
	}

	//------------------------------------------------------------------

	public ClusterIndex sectorIndexToClusterIndex(
		long	sectorIndex)
	{
		long index = sectorIndex - numReservedSectors - numFats * sectorsPerFat;
		return (index < 0) ? null
						   : new ClusterIndex(Fat32Fat.MIN_CLUSTER_INDEX + (int)(index / sectorsPerCluster),
											  (int)(index % sectorsPerCluster));
	}

	//------------------------------------------------------------------

	public int getNumClusters()
	{
		return (int)((numSectors - numReservedSectors - numFats * sectorsPerFat) / sectorsPerCluster);
	}

	//------------------------------------------------------------------

	public int getMaxClusterIndex()
	{
		return Fat32Fat.MIN_CLUSTER_INDEX + getNumClusters() - 1;
	}

	//------------------------------------------------------------------

	public int getNumUnusedClusters()
	{
		// Check that volume has been initialised
		if (bytesPerSector == 0)
			throw new IllegalStateException(VOLUME_NOT_INITIALISED_STR);

		// Calculate number of unused clusters and return result
		return fat.getNumUnusedClusters();
	}

	//------------------------------------------------------------------

	public Map<String, String> getProperties()
	{
		return InsertionOrderStringMap
				.create()
				.integerFormatter(Utils.INTEGER_FORMATTER)
				.add(VOLUME_LABEL_STR,           volumeLabel)
				.add(ID_STR,                     idToString(id))
				.add(MEDIA_DESCRIPTOR_STR,       mediaDescriptorToString(mediaDescriptor))
				.add(FORMATTER_NAME_STR,         formatterName)
				.add(BYTES_PER_SECTOR_STR,       bytesPerSector)
				.add(SECTORS_PER_CLUSTER_STR,    sectorsPerCluster)
				.add(NUM_SECTORS_STR,            numSectors)
				.add(NUM_HIDDEN_SECTORS_STR,     numHiddenSectors)
				.add(NUM_RESERVED_SECTORS_STR,   numReservedSectors)
				.add(NUM_FATS_STR,               numFats)
				.add(SECTORS_PER_FAT_STR,        sectorsPerFat)
				.add(BOOT_SECTOR_COPY_INDEX_STR, bootSectorCopyIndex)
				.add(ROOT_DIR_CLUSTER_INDEX_STR, rootDirClusterIndex)
				.add(NUM_CLUSTERS_STR,           getNumClusters())
				.add(NUM_UNUSED_CLUSTERS_STR,    getNumUnusedClusters());
	}

	//------------------------------------------------------------------

	public void seekSector(
		long	index)
		throws VolumeException
	{
		// Check that volume has been initialised
		if (bytesPerSector == 0)
			throw new IllegalStateException(VOLUME_NOT_INITIALISED_STR);

		// Validate argument
		if ((index < 0) || (index >= numSectors))
			throw new IllegalArgumentException(SECTOR_INDEX_OUT_OF_BOUNDS_STR + index);

		// Seek sector
		seek(index * bytesPerSector);
	}

	//------------------------------------------------------------------

	public byte[] readSector(
		long	index)
		throws VolumeException
	{
		// Validate argument
		if ((index < 0) || (index >= numSectors))
			throw new IllegalArgumentException(SECTOR_INDEX_OUT_OF_BOUNDS_STR + index);

		// Check that volume has been initialised
		if (bytesPerSector == 0)
			throw new IllegalStateException(VOLUME_NOT_INITIALISED_STR);

		// Read sector
		try
		{
			// Allocate buffer for sector
			byte[] buffer = new byte[bytesPerSector];

			// Open volume for reading
			open(Access.READ);

			// Read sector
			seekSector(index);
			read(buffer);

			// Return sector data
			return buffer;
		}
		finally
		{
			// Close volume
			if (isOpen())
				close();
		}
	}

	//------------------------------------------------------------------

	public byte[] readSectors(
		long	index,
		int		numSectors)
		throws VolumeException
	{
		// Validate arguments
		if ((index < 0) || (index >= this.numSectors))
			throw new IllegalArgumentException(SECTOR_INDEX_OUT_OF_BOUNDS_STR + index);
		if ((numSectors < 0) || (numSectors > this.numSectors - index))
			throw new IllegalArgumentException(NUM_SECTORS_OUT_OF_BOUNDS_STR + numSectors);

		// Check that volume has been initialised
		if (bytesPerSector == 0)
			throw new IllegalStateException(VOLUME_NOT_INITIALISED_STR);

		// Read sectors
		try
		{
			// Allocate buffer for sectors
			byte[] buffer = new byte[numSectors * bytesPerSector];

			// Open volume for reading
			open(Access.READ);

			// Read sectors
			seekSector(index);
			read(buffer);

			// Return sector data
			return buffer;
		}
		finally
		{
			// Close volume
			if (isOpen())
				close();
		}
	}

	//------------------------------------------------------------------

	public byte[] readCluster(
		int	index)
		throws VolumeException
	{
		// Validate argument
		if ((index < Fat32Fat.MIN_CLUSTER_INDEX) || (index > getMaxClusterIndex()))
			throw new IllegalArgumentException(CLUSTER_INDEX_OUT_OF_BOUNDS_STR + index);

		// Check that volume has been initialised
		if (bytesPerSector == 0)
			throw new IllegalStateException(VOLUME_NOT_INITIALISED_STR);

		// Read sectors and return data
		long sectorIndex = clusterIndexToSectorIndex(index);
		return (sectorsPerCluster == 1) ? readSector(sectorIndex) : readSectors(sectorIndex, sectorsPerCluster);
	}

	//------------------------------------------------------------------

	public void readSectors(
		byte[]	buffer,
		int		offset,
		int		numSectors)
		throws VolumeException
	{
		// Check that volume has been initialised
		if (bytesPerSector == 0)
			throw new IllegalStateException(VOLUME_NOT_INITIALISED_STR);

		// Validate arguments
		if (buffer == null)
			throw new IllegalArgumentException("Null buffer");
		if ((offset < 0) || (offset > buffer.length))
			throw new IllegalArgumentException("Offset out of bounds: " + offset);

		int length = numSectors * bytesPerSector;
		if ((numSectors < 0) || (length >= buffer.length - offset))
			throw new IllegalArgumentException(NUM_SECTORS_OUT_OF_BOUNDS_STR + numSectors);

		// Read sectors
		read(buffer, offset, length);
	}

	//------------------------------------------------------------------

	public void updateVolumeLabel(
		String			volumeLabel,
		LocalDateTime	modTime,
		ITaskStatus		taskStatus)
		throws VolumeException
	{
		final	double	TOTAL_PROGRESS	= 6.0;

		// Pad volume label
		String paddedLabel = StringUtils.padAfter((volumeLabel == null) ? "" : volumeLabel, EBPB_VOLUME_LABEL_LENGTH);

		// Convert padded label to bytes
		byte[] labelData = paddedLabel.getBytes(StandardCharsets.US_ASCII);

		// Update volume label in boot sector, copy of boot sector and root directory
		try
		{
			// Initialise progress
			int progress = 0;

			// Update message and progress
			taskStatus.setMessage(READING_BOOT_SECTOR_STR);
			taskStatus.setProgress((double)progress / TOTAL_PROGRESS);

			// Open volume for reading and writing
			open(Access.READ_WRITE);

			// Allocate buffer for sector
			byte[] buffer = new byte[bytesPerSector];

			// Read boot sector
			seekSector(0);
			read(buffer);

			// Set volume label in boot sector
			System.arraycopy(labelData, 0, buffer, EBPB_VOLUME_LABEL_OFFSET, EBPB_VOLUME_LABEL_LENGTH);

			// Update message and progress
			taskStatus.setMessage(WRITING_BOOT_SECTOR_STR);
			taskStatus.setProgress((double)++progress / TOTAL_PROGRESS);

			// Write boot sector
			seekSector(0);
			write(buffer);

			// Update instance variable
			this.volumeLabel = paddedLabel;

			// Update message and progress
			taskStatus.setMessage(READING_BOOT_SECTOR_STR + COPY_STR);
			taskStatus.setProgress((double)++progress / TOTAL_PROGRESS);

			// Read copy of boot sector
			seekSector(bootSectorCopyIndex);
			read(buffer);

			// Set volume label in boot sector
			System.arraycopy(labelData, 0, buffer, EBPB_VOLUME_LABEL_OFFSET, EBPB_VOLUME_LABEL_LENGTH);

			// Update message and progress
			taskStatus.setMessage(WRITING_BOOT_SECTOR_STR + COPY_STR);
			taskStatus.setProgress((double)++progress / TOTAL_PROGRESS);

			// Write copy of boot sector
			seekSector(bootSectorCopyIndex);
			write(buffer);

			// Close volume
			close();

			// Update message and progress
			taskStatus.setMessage(UPDATING_ROOT_DIR_STR);
			taskStatus.setProgress((double)++progress / TOTAL_PROGRESS);

			// Update volume label in root directory
			rootDir.updateVolumeLabel(volumeLabel, modTime);

			// Update progress
			taskStatus.setProgress(1.0);
		}
		finally
		{
			// Close volume
			if (isOpen())
				close();
		}
	}

	//------------------------------------------------------------------

	public int validateClusterChains(
		List<Fat32Directory.Entry>	entries,
		List<InvalidCluster>		invalidClusters,
		ITaskStatus					taskStatus)
	{
		// Initialise number of clusters
		int numClusters = 0;

		// Validate directory entries
		for (Fat32Directory.Entry entry : entries)
		{
			// Test whether task has been cancelled
			if (taskStatus.isCancelled())
				break;

			// If entry is file or regular directory, process it
			if (entry.isFile() || entry.isRegularDirectory())
			{
				// Get pathname of entry
				String pathname = entry.getPathname();

				// Update message with pathname of file or directory
				taskStatus.setSpacedMessage(VALIDATING_STR, pathname);

				// Get index of first cluster of file or directory
				int index = entry.getClusterIndex();

				// If file or directory is not empty, process its clusters
				if (index != 0)
				{
					// Get chain of indices
					int prevIndex = -1;
					while (!Fat32Fat.isEndOfChain(index))
					{
						// Test for invalid cluster index
						if ((index < Fat32Fat.MIN_CLUSTER_INDEX) || Fat32Fat.isBadCluster(index))
						{
							invalidClusters.add(new InvalidCluster(pathname, prevIndex, index));
							break;
						}

						// Increment number of clusters
						++numClusters;

						// Get index of next cluster
						prevIndex = index;
						index = fat.get(index);
					}
				}
			}
		}

		// Return number of clusters
		return numClusters;
	}

	//------------------------------------------------------------------

	public int validateClusterChains(
		Fat32Directory			directory,
		List<InvalidCluster>	invalidClusters,
		ITaskStatus				taskStatus)
	{
		// Validate clusters of directory entries
		int numClusters = validateClusterChains(directory.getEntries(), invalidClusters, taskStatus);

		// Process subdirectories
		for (Fat32Directory subdirectory : directory.getChildren())
		{
			// Test whether task has been cancelled
			if (taskStatus.isCancelled())
				break;

			// Process subdirectory
			if (subdirectory.getEntryInParent().isRegularDirectory())
				numClusters += validateClusterChains(subdirectory, invalidClusters, taskStatus);
		}

		// Return number of clusters
		return numClusters;
	}

	//------------------------------------------------------------------

	public void eraseUnusedClusters(
		byte		fillerValue,
		ITaskStatus	taskStatus)
		throws VolumeException
	{
		// Check that volume has been initialised
		if (bytesPerSector == 0)
			throw new IllegalStateException(VOLUME_NOT_INITIALISED_STR);

		// Update task message; set indeterminate progress
		taskStatus.setMessage(COUNTING_UNUSED_CLUSTERS_STR);
		taskStatus.setProgress(-1.0);

		// Get number of unused clusters
		int numUnusedClusters = fat.getNumUnusedClusters();

		// Allocate array for cluster data and fill it with filler value
		byte[] data = new byte[bytesPerSector * sectorsPerCluster];
		Arrays.fill(data, fillerValue);

		// Write filler value to unused clusters
		try
		{
			// Update task message; reset progress
			taskStatus.setMessage(ERASING_UNUSED_CLUSTERS_STR);
			taskStatus.setProgress(0.0);

			// Open volume for writing
			open(Access.WRITE);

			// Fill unused clusters with filler value
			int numClustersProcessed = 0;
			DiscretisedValue discreteProgress = new DiscretisedValue(new LinearDiscretiser(400));
			for (int i = 0; i < fat.getLength(); i++)
			{
				// Test whether task has been cancelled
				if (taskStatus.isCancelled())
					break;

				// If cluster is unused, write filler value to its sectors
				if (fat.get(i) == 0)
				{
					// Write filler value to cluster
					seekSector(clusterIndexToSectorIndex(i));
					write(data);

					// Increment number of clusters processed
					++numClustersProcessed;

					// Update progress
					double progress = (double)numClustersProcessed / (double)numUnusedClusters;
					if ((progress == 1.0) || discreteProgress.updateChanged(progress))
						taskStatus.setProgress(progress);
				}
			}
		}
		finally
		{
			// Close volume
			if (isOpen())
				close();
		}
	}

	//------------------------------------------------------------------

	public boolean isEntryFragmented(
		Fat32Directory.Entry	entry)
		throws VolumeException
	{
		// Test for empty file
		if (entry.isFile() && (entry.getFileLength() == 0))
			return false;

		// Search cluster chain for non-consecutive indices
		int prevIndex = -1;
		Fat32Fat.IndexIterator it = fat.indexIterator(entry.getClusterIndex());
		while (it.hasNext())
		{
			int index = it.next();
			if ((prevIndex >= 0) && (index - prevIndex != 1))
				return true;
			prevIndex = index;
		}

		// Cluster indices are consecutive
		return false;
	}

	//------------------------------------------------------------------

	public DefragStatus defragmentFile(
		Fat32Directory.Entry	entry,
		ITaskStatus				taskStatus)
		throws VolumeException
	{
		return defragmentFile(entry, 0, entry.getNumClusters(), taskStatus);
	}

	//------------------------------------------------------------------

	public DefragStatus defragmentFile(
		Fat32Directory.Entry	entry,
		int						numClustersProcessed,
		int						totalNumClusters,
		ITaskStatus				taskStatus)
		throws VolumeException
	{
		// Check that entry is a file
		if (!entry.isFile())
			throw new IllegalArgumentException("Entry is not a file");

		// Test whether file is fragmented
		if (!isEntryFragmented(entry))
			return DefragStatus.NOT_FRAGMENTED;

		// Test whether there is space to defragment file without moving its first cluster
		int destIndex = 0;
		int sourceIndex = entry.getClusterIndex();
		int numClusters = entry.getNumClusters();
		if (sourceIndex + numClusters <= fat.getLength())
		{
			destIndex = sourceIndex;
			boolean findUnused = false;
			for (int i = 0; i < numClusters; i++)
			{
				int index = fat.get(sourceIndex + i);
				if (findUnused)
				{
					if (index != 0)
					{
						destIndex = 0;
						break;
					}
				}
				else if (index != sourceIndex + i + 1)
					findUnused = true;
			}
		}

		// Find sequence of unused clusters that can accommodate file
		if (destIndex == 0)
			destIndex = fat.findUnusedClusters(numClusters);
		if (destIndex == 0)
			return DefragStatus.NOT_ENOUGH_SPACE;

		// Calculate progress at start of operation and difference between progress at start and end of operation
		double startProgress = (double)numClustersProcessed / (double)totalNumClusters;
		double deltaProgress =
				(double)(numClustersProcessed + entry.getNumClusters()) / (double)totalNumClusters - startProgress;

		// Calculate total number of sectors that will be read and written
		int numDirectorySectors = fat.clusterCount(entry.getDirectory().getClusterIndex()) * sectorsPerCluster;
		long totalNumRWSectors = 2 * (numClusters * sectorsPerCluster + numDirectorySectors + numFats * sectorsPerFat);
		long[] numRWSectors = { 0 };

		// Create procedure to update progress
		IProcedure1<Integer> updateProgress = deltaSectors ->
		{
			numRWSectors[0] += deltaSectors;
			taskStatus.setProgress(startProgress + ((double)numRWSectors[0] / (double)totalNumRWSectors)
					* deltaProgress);
		};

		// Update task message and progress
		String pathname = entry.getPathname();
		taskStatus.setMessage(pathname + "\n" + DEFRAGMENTING_STR);
		updateProgress.invoke(0);

		// Defragment file
		try
		{
			// Open volume for reading and writing
			open(Volume.Access.READ_WRITE);

			// Allocate buffer for cluster
			int bytesPerCluster = sectorsPerCluster * bytesPerSector;
			byte[] buffer = new byte[bytesPerCluster];

			// Copy clusters
			int[] sourceIndices = new int[numClusters];
			int copyIndex = 0;
			while (copyIndex < numClusters)
			{
				// Add source index to array
				sourceIndices[copyIndex] = sourceIndex;

				// Test whether task has been cancelled
				if (taskStatus.isCancelled())
					break;

				// If source and destination clusters are the same, skip I/O ...
				if (sourceIndex == destIndex + copyIndex)
					updateProgress.invoke(2 * sectorsPerCluster);

				// ... otherwise, copy cluster
				else
				{
					// Read cluster from source
					seekSector(clusterIndexToSectorIndex(sourceIndex));
					read(buffer);

					// Update progress
					updateProgress.invoke(sectorsPerCluster);

					// Write cluster to destination
					seekSector(clusterIndexToSectorIndex(destIndex + copyIndex));
					write(buffer);

					// Update progress
					updateProgress.invoke(sectorsPerCluster);
				}

				// Get next source index
				sourceIndex = fat.get(sourceIndex);

				// Increment copy index
				++copyIndex;
			}

			// If no clusters were copied or source and destination clusters are the same, skip I/O ...
			if ((copyIndex == 0) || (sourceIndices[0] == destIndex))
				updateProgress.invoke(2 * numDirectorySectors);

			// ... otherwise, update cluster index in directory entry
			else
			{
				// Update message
				taskStatus.setMessage(pathname + "\n" + UPDATING_PARENT_DIRECTORY_STR);

				// Read clusters of directory
				Fat32Directory directory = entry.getDirectory();
				buffer = directory.readData(false);

				// Update progress
				updateProgress.invoke(numDirectorySectors);

				// Get offset to directory entry in cluster data
				int offset = (entry.getIndex() + entry.getLength() - 1) * Fat32Directory.Entry.SIZE;

				// Test cluster index of entry in clusters of directory
				if (Fat32Directory.getClusterIndex(buffer, offset) != sourceIndices[0])
					throw new VolumeException(ErrorMsg.UNEXPECTED_DIRECTORY_ENTRY);

				// Update cluster index of entry in clusters of directory
				Fat32Directory.setClusterIndex(destIndex, buffer, offset);

				// Write clusters of directory
				directory.writeData(buffer, 0, false);

				// Update progress
				updateProgress.invoke(numDirectorySectors);

				// Set cluster index on directory entry
				entry.setClusterIndex(destIndex);
			}

			// If no clusters were copied, skip I/O ...
			if (copyIndex == 0)
				updateProgress.invoke(2 * numFats * sectorsPerFat);

			// ... otherwise, update entries in FATs
			else
			{
				// Update message
				taskStatus.setMessage(pathname + "\n" + UPDATING_FATS_STR);

				// Update FAT entries
				int destEndIndex = destIndex + copyIndex;
				for (int fatIndex = 0; fatIndex < numFats; fatIndex++)
				{
					// Read sectors of FAT
					buffer = fat.read(fatIndex);

					// Update progress
					updateProgress.invoke(sectorsPerFat);

					// Initialise 'last FAT' flag
					boolean lastFat = (fatIndex == numFats - 1);

					// Update entries for old clusters
					for (int i = 0; i < copyIndex; i++)
					{
						// Get index of cluster
						int index = sourceIndices[i];

						// If old cluster does not conflict with new cluster, update FAT
						if ((index < destIndex) || (index >= destEndIndex))
						{
							// Get offset of FAT entry
							int offset = index * Fat32Fat.ENTRY_SIZE;

							// Test current value of FAT entry
							if (Fat32Fat.getIndex(buffer, offset) != fat.get(index))
								throw new VolumeException(ErrorMsg.UNEXPECTED_FAT_ENTRY);

							// Set new value in FAT
							Fat32Fat.setIndex(0, buffer, offset);

							// Update FAT array
							if (lastFat)
								fat.set(index, 0);
						}
					}

					// Update entries for new clusters
					for (int i = 0; i < copyIndex; i++)
					{
						// Get index of cluster
						int index = destIndex + i;

						// Get offset of FAT entry
						int offset = index * Fat32Fat.ENTRY_SIZE;

						// Test current value of FAT entry
						if (Fat32Fat.getIndex(buffer, offset) != fat.get(index))
							throw new VolumeException(ErrorMsg.UNEXPECTED_FAT_ENTRY);

						// Get new value of FAT entry
						int value = (i == numClusters - 1)
												? Fat32Fat.MAX_END_OF_CHAIN_INDEX
												: (i == copyIndex - 1)
														? sourceIndices[i + 1]
														: index + 1;

						// Set new value in FAT
						Fat32Fat.setIndex(value, buffer, offset);

						// Update FAT array
						if (lastFat)
							fat.set(index, value);
					}

					// Seek sector
					seekSector(numReservedSectors + fatIndex * sectorsPerFat);

					// Write sectors of FAT
					write(buffer);

					// Update progress
					updateProgress.invoke(sectorsPerFat);
				}
			}

			// Return result
			return (copyIndex < numClusters) ? DefragStatus.CANCEL : DefragStatus.SUCCESS;
		}
		finally
		{
			// Close volume
			if (isOpen())
				close();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: CLUSTER INDEX


	public record ClusterIndex(
		int	clusterIndex,
		int	sectorIndex)
	{ }

	//==================================================================


	// RECORD: INVALID CLUSTER


	public record InvalidCluster(
		String	pathname,
		int		entryIndex,
		int		entryValue)
	{

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			return pathname + ": " + entryIndex + ", " + entryValue;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// RECORD: VOLUME PARAMETERS


	public record Params(
		String	volumeLabel,
		int		id,
		int		mediaDescriptor,
		String	formatterName,
		int		bytesPerSector,
		int		sectorsPerCluster,
		long	numSectors,
		int		numHiddenSectors,
		int		numReservedSectors,
		int		numFats,
		int		sectorsPerFat,
		int		bootSectorCopyIndex,
		int		rootDirClusterIndex)
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	String	EQUALS_STR	= " = ";

	////////////////////////////////////////////////////////////////////
	//  Instance methods : overriding methods
	////////////////////////////////////////////////////////////////////

		@Override
		public String toString()
		{
			StringBuilder buffer = new StringBuilder(256);

			buffer.append(VOLUME_LABEL_STR);
			buffer.append(EQUALS_STR);
			buffer.append('"');
			buffer.append(volumeLabel);
			buffer.append('"');
			buffer.append('\n');

			buffer.append(ID_STR);
			buffer.append(EQUALS_STR);
			buffer.append(idToString(id));
			buffer.append('\n');

			buffer.append(MEDIA_DESCRIPTOR_STR);
			buffer.append(EQUALS_STR);
			buffer.append(mediaDescriptorToString(mediaDescriptor));
			buffer.append('\n');

			buffer.append(FORMATTER_NAME_STR);
			buffer.append(EQUALS_STR);
			buffer.append('"');
			buffer.append(formatterName);
			buffer.append('"');
			buffer.append('\n');

			buffer.append(BYTES_PER_SECTOR_STR);
			buffer.append(EQUALS_STR);
			buffer.append(bytesPerSector);
			buffer.append('\n');

			buffer.append(SECTORS_PER_CLUSTER_STR);
			buffer.append(EQUALS_STR);
			buffer.append(sectorsPerCluster);
			buffer.append('\n');

			buffer.append(NUM_SECTORS_STR);
			buffer.append(EQUALS_STR);
			buffer.append(numSectors);
			buffer.append('\n');

			buffer.append(NUM_HIDDEN_SECTORS_STR);
			buffer.append(EQUALS_STR);
			buffer.append(numHiddenSectors);
			buffer.append('\n');

			buffer.append(NUM_RESERVED_SECTORS_STR);
			buffer.append(EQUALS_STR);
			buffer.append(numReservedSectors);
			buffer.append('\n');

			buffer.append(NUM_FATS_STR);
			buffer.append(EQUALS_STR);
			buffer.append(numFats);
			buffer.append('\n');

			buffer.append(SECTORS_PER_FAT_STR);
			buffer.append(EQUALS_STR);
			buffer.append(sectorsPerFat);
			buffer.append('\n');

			buffer.append(BOOT_SECTOR_COPY_INDEX_STR);
			buffer.append(EQUALS_STR);
			buffer.append(bootSectorCopyIndex);
			buffer.append('\n');

			buffer.append(ROOT_DIR_CLUSTER_INDEX_STR);
			buffer.append(EQUALS_STR);
			buffer.append(rootDirClusterIndex);
			buffer.append('\n');

			return buffer.toString();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int bytesPerCluster()
		{
			return sectorsPerCluster * bytesPerSector;
		}

		//--------------------------------------------------------------

	}

	//==================================================================


	// RECORD: FORMAT PARAMETERS


	public record FormatParams(
		int	minNumReservedSectors,
		int	sectorsPerFat)
	{ }

	//==================================================================

}

//----------------------------------------------------------------------
