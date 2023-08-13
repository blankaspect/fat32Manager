/*====================================================================*\

PartitionInfo.cc

Class: partition information.

\*====================================================================*/


// INCLUDES


#ifndef _DRIVEIO_PARTITION_INFO_H_
	#include "PartitionInfo.h"
#endif
#ifndef _DRIVEIO_FILE_EXCEPTION_H_
	#include "FileException.h"
#endif
#ifndef _DRIVEIO_STR_CONV_H_
	#include "StrConv.h"
#endif
#ifndef _DRIVEIO_VOLUME_H_
	#include "Volume.h"
#endif

#include <algorithm>
#include <cstring>
#include <iostream>

#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <mntent.h>
#include <unistd.h>

#include <linux/fs.h>
#include <sys/ioctl.h>
#include <sys/stat.h>

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

const long	MAX_FILE_LENGTH	= 1 << 16;

// Pathnames of directories
namespace DirPathname
{
	const std::string	DEVICE		("/dev");
	const std::string	BLOCK_CLASS	("/sys/class/block");
}

// Pathnames of files
namespace FilePathname
{
	const std::string	MOUNTS	("/proc/mounts");
}

// Filenames
namespace Filename
{
	const std::string	ALIGNMENT_OFFSET	("alignment_offset");
	const std::string	NUM_SECTORS			("size");
	const std::string	PARTITION			("partition");
	const std::string	READ_ONLY			("ro");
	const std::string	REMOVABLE			("removable");
	const std::string	START_SECTOR		("start");
}

// Error messages
namespace ErrorMsg
{
	const std::wstring	FAILED_TO_OPEN_DEVICE
						(L"Failed to open the device.");

	const std::wstring	FAILED_TO_CLOSE_DEVICE
						(L"Failed to close the device.");

	const std::wstring	FAILED_TO_READ_DEVICE_INFO
						(L"Failed to read information about the device.");

	const std::wstring	NOT_A_FILE
						(L"The pathname does not denote a regular file.");

	const std::wstring	FAILED_TO_OPEN_FILE
						(L"Failed to open the file.");

	const std::wstring	FAILED_TO_CLOSE_FILE
						(L"Failed to close the file.");

	const std::wstring	ERROR_READING_FILE
						(L"An error occurred when reading the file.");

	const std::wstring	FILE_IS_TOO_LONG
						(L"The file is too long to be read.");

	const std::wstring	NO_PARTITION_ID
						(L"No partition ID was found.");

	const std::wstring	INVALID_PARTITION_ID
						(L"The partition ID is invalid.");

	const std::wstring	NO_PARTITION_SIZE
						(L"No partition size was found.");

	const std::wstring	INVALID_PARTITION_SIZE
						(L"The partition size is invalid.");

	const std::wstring	NO_PARTITION_START_SECTOR
						(L"No partition start sector was found.");

	const std::wstring	INVALID_PARTITION_START_SECTOR
						(L"The partition start sector is invalid.");

	const std::wstring	NO_PARTITION_READ_ONLY_FLAG
						(L"No partition read-only flag was found.");

	const std::wstring	INVALID_PARTITION_READ_ONLY_FLAG
						(L"The partition read-only flag is invalid.");

	const std::wstring	NO_PARTITION_ALIGNMENT_OFFSET
						(L"No partition alignment offset was found.");

	const std::wstring	INVALID_PARTITION_ALIGNMENT_OFFSET
						(L"The partition alignment offset is invalid.");

	const std::wstring	INVALID_REMOVABLE_FLAG
						(L"The 'removable' flag is invalid.");
}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

PartitionInfo::PartitionInfo(
	const std::string&	name) :

	_name      (name),
	_mediumKind(mediumKind::UNKNOWN)
{
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Internal methods
////////////////////////////////////////////////////////////////////////

static bool dirHasEntry(
	const std::string&	dirPathname,
	const std::string&	name,
	mode_t				modeMask)
{
	// Initialise result of search
	bool found = false;

	// Open directory
	DIR* dirPtr = ::opendir(dirPathname.c_str());

	// Search directory entries
	if (dirPtr)
	{
		struct dirent* entryPtr;
		struct stat statBuffer {};
		while (!found)
		{
			// Get next entry
			entryPtr = ::readdir(dirPtr);
			if (entryPtr == 0)
				break;

			// Test name and mode of entry
			if (::strcmp(name.c_str(), entryPtr->d_name) == 0)
			{
				std::string pathname = dirPathname + "/" + name;
				if ((::stat(pathname.c_str(), &statBuffer) == 0) && (statBuffer.st_mode & modeMask))
					found = true;
			}
		}

		// Close directory
		::closedir(dirPtr);
	}

	// Return result of search
	return found;
}

//----------------------------------------------------------------------

static int readSysFile(
	const std::string&	pathname,
	std::string&		result)
{
	int fd = -1;
	try
	{
		// Open file
		std::wstring wpathname = StrConv::strToWstr(pathname);
		fd = ::open(pathname.c_str(), O_RDONLY | O_CLOEXEC | O_NONBLOCK);
		if (fd < 0)
		{
			if (errno == ENOENT)
				return -1;
			throw FileException(ErrorMsg::FAILED_TO_OPEN_FILE, wpathname, errno);
		}

		// Get file information
		struct stat statBuffer {};
		if (::fstat(fd, &statBuffer) < 0)
			throw FileException(ErrorMsg::ERROR_READING_FILE, wpathname, errno);

		// Test for regular file
		if (!S_ISREG(statBuffer.st_mode))
			throw FileException(ErrorMsg::NOT_A_FILE, wpathname);

		// Test file length
		if (statBuffer.st_size > MAX_FILE_LENGTH)
			throw FileException(ErrorMsg::FILE_IS_TOO_LONG, wpathname);

		// Allocate buffer for file
		char buffer[statBuffer.st_size];

		// Read file
		ssize_t length = ::read(fd, buffer, statBuffer.st_size);
		if (length < 0)
			throw FileException(ErrorMsg::ERROR_READING_FILE, wpathname, errno);

		// Close file
		int fd0 = fd;
		fd = -1;
		if (::close(fd0) < 0)
			throw FileException(ErrorMsg::FAILED_TO_CLOSE_FILE, wpathname, errno);

		// Set result
		result.assign(buffer, length);

		// Return success code
		return 0;
	}
	catch (...)
	{
		// Close file
		if (fd >= 0)
			::close(fd);

		// Rethrow exception
		throw;
	}
}

//----------------------------------------------------------------------

static std::string findParent(
	const std::string&	name)
{
	// Initialise parent
	std::string parent;

	// Open directory
	DIR* dirPtr = ::opendir(DirPathname::BLOCK_CLASS.c_str());

	// Search directory entries
	if (dirPtr)
	{
		struct dirent* entryPtr;
		while (parent.empty())
		{
			// Get next entry
			entryPtr = ::readdir(dirPtr);
			if (entryPtr == 0)
				break;

			// Test whether entry contains a subdirectory whose name matches the target
			char* name0 = entryPtr->d_name;
			if (::strcmp(name0, ".") && ::strcmp(name0, "..") && ::strcmp(name0, name.c_str()))
			{
				if (dirHasEntry(DirPathname::BLOCK_CLASS + "/" + name0, name, S_IFDIR))
					parent.assign(name0);
			}
		}

		// Close directory
		::closedir(dirPtr);
	}

	// Return parent
	return parent;
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

bool PartitionInfo::isPartition(
	const std::string&	name)
{
	std::string pathname = DirPathname::BLOCK_CLASS + "/" + name + "/" + Filename::PARTITION;
	return (::access(pathname.c_str(), F_OK) == 0);
}

//----------------------------------------------------------------------

void PartitionInfo::appendPartitionNames(
	StringVector&	names)
{
	// Initialise temporary list of names
	StringVector tempNames;

	// Open directory
	DIR* dirPtr = ::opendir(DirPathname::BLOCK_CLASS.c_str());

	// Create list of names of block devices and partitions
	if (dirPtr)
	{
		struct dirent* entryPtr;
		while (true)
		{
			// Get next entry
			entryPtr = ::readdir(dirPtr);
			if (entryPtr == 0)
				break;

			// If not a special directory, add name of entry to list
			char* name = entryPtr->d_name;
			if (::strcmp(name, ".") && ::strcmp(name, ".."))
				tempNames.push_back(std::string(name));
		}

		// Sort list of names
		std::sort(tempNames.begin(), tempNames.end());

		// Close directory
		::closedir(dirPtr);
	}

	// Create list of names of partitions
	for (const std::string& name : tempNames)
	{
		if (isPartition(name))
			names.push_back(name);
	}
}

//----------------------------------------------------------------------

int PartitionInfo::getBytesPerSector(
	const std::string&	name)
{
	int fd = -1;
	try
	{
		// Open device
		const std::string pathname = DirPathname::DEVICE + "/" + name;
		fd = ::open(pathname.c_str(), O_RDONLY | O_CLOEXEC | O_NONBLOCK);
		if (fd < 0)
			throw FileException(ErrorMsg::FAILED_TO_OPEN_DEVICE, StrConv::strToWstr(pathname), errno);

		// Read sector size
		UInt64 sectorSize = 0;
		if (::ioctl(fd, BLKSSZGET, &sectorSize) < 0)
			throw FileException(ErrorMsg::FAILED_TO_READ_DEVICE_INFO, StrConv::strToWstr(pathname), errno);

		// Close device
		int fd0 = fd;
		fd = -1;
		if (::close(fd0) < 0)
			throw FileException(ErrorMsg::FAILED_TO_CLOSE_DEVICE, StrConv::strToWstr(pathname), errno);

		// Return sector size
		return sectorSize;
	}
	catch (...)
	{
		// Close device
		if (fd >= 0)
			::close(fd);

		// Rethrow exception
		throw;
	}
}

//----------------------------------------------------------------------

PartitionInfo PartitionInfo::createInfo(
	const std::string&	name)
{
	// Initialise partition info
	PartitionInfo partInfo(name);

	// Initialise pathname of partition directory
	std::string partitionDirPathname = DirPathname::BLOCK_CLASS + "/" + name + "/";

	// Initialise result of reading file
	std::string result;

	// Read ID
	std::string pathname = partitionDirPathname + Filename::PARTITION;
	if (readSysFile(pathname, result) < 0)
		throw FileException(ErrorMsg::NO_PARTITION_ID, StrConv::strToWstr(pathname));
	try
	{
		partInfo._id = std::stoi(result);
	}
	catch (...)
	{
		throw FileException(ErrorMsg::INVALID_PARTITION_ID, StrConv::strToWstr(pathname));
	}

	// Read start sector
	pathname = partitionDirPathname + Filename::START_SECTOR;
	if (readSysFile(pathname, result) < 0)
		throw FileException(ErrorMsg::NO_PARTITION_START_SECTOR, StrConv::strToWstr(pathname));
	try
	{
		partInfo._startSector = std::stol(result);
	}
	catch (...)
	{
		throw FileException(ErrorMsg::INVALID_PARTITION_START_SECTOR, StrConv::strToWstr(pathname));
	}

	// Read number of sectors
	pathname = partitionDirPathname + Filename::NUM_SECTORS;
	if (readSysFile(pathname, result) < 0)
		throw FileException(ErrorMsg::NO_PARTITION_SIZE, StrConv::strToWstr(pathname));
	try
	{
		partInfo._numSectors = std::stol(result);
	}
	catch (...)
	{
		throw FileException(ErrorMsg::INVALID_PARTITION_SIZE, StrConv::strToWstr(pathname));
	}

	// Read read-only flag
	pathname = partitionDirPathname + Filename::READ_ONLY;
	if (readSysFile(pathname, result) < 0)
		throw FileException(ErrorMsg::NO_PARTITION_READ_ONLY_FLAG, StrConv::strToWstr(pathname));
	try
	{
		partInfo._readOnly = std::stoi(result);
	}
	catch (...)
	{
		throw FileException(ErrorMsg::INVALID_PARTITION_READ_ONLY_FLAG, StrConv::strToWstr(pathname));
	}

	// Read alignment offset
	pathname = partitionDirPathname + Filename::ALIGNMENT_OFFSET;
	if (readSysFile(pathname, result) < 0)
		throw FileException(ErrorMsg::NO_PARTITION_ALIGNMENT_OFFSET, StrConv::strToWstr(pathname));
	try
	{
		partInfo._alignmentOffset = std::stoi(result);
	}
	catch (...)
	{
		throw FileException(ErrorMsg::INVALID_PARTITION_ALIGNMENT_OFFSET, StrConv::strToWstr(pathname));
	}

	// Read 'removable' flag
	pathname = partitionDirPathname + Filename::REMOVABLE;
	if (readSysFile(pathname, result) == 0)
	{
		try
		{
			partInfo._mediumKind = std::stoi(result) ? mediumKind::REMOVABLE : mediumKind::FIXED;
		}
		catch (...)
		{
			throw FileException(ErrorMsg::INVALID_REMOVABLE_FLAG, StrConv::strToWstr(pathname));
		}
	}
	else
	{
		const std::string& parent = findParent(name);
		if (!parent.empty())
		{
			pathname = DirPathname::BLOCK_CLASS + "/" + parent + "/" + Filename::REMOVABLE;
			if (readSysFile(pathname, result) == 0)
			{
				try
				{
					partInfo._mediumKind = std::stoi(result) ? mediumKind::REMOVABLE : mediumKind::FIXED;
				}
				catch (...)
				{
					throw FileException(ErrorMsg::INVALID_REMOVABLE_FLAG, StrConv::strToWstr(pathname));
				}
			}
		}
	}

	// Return partition info
	return partInfo;
}

//----------------------------------------------------------------------

std::string PartitionInfo::findMountPoint(
	const std::string&	name)
{
	// Initialise mount directory
	std::string mountDir;

	// Search table of mounted file systems for target
	FILE* filePtr;
	try
	{
		// Open stream on table of mounted file systems
		filePtr = ::setmntent(FilePathname::MOUNTS.c_str(), "r");
		if (filePtr == NULL)
			throw FileException(L"", StrConv::strToWstr(FilePathname::MOUNTS), errno);

		// Initialise name of target file system
		std::string targetName = DirPathname::DEVICE + "/" + name;

		// Search table entries for target file-system name
		struct mntent* entPtr;
		while (true)
		{
			// Get next entry
			entPtr = ::getmntent(filePtr);

			// If end of table, stop
			if (entPtr == NULL)
				break;

			// If name of mounted file system matches target name, stop searching
			if (::strcmp(entPtr->mnt_fsname, targetName.c_str()) == 0)
			{
				mountDir.assign(entPtr->mnt_dir);
				break;
			}
		}

		// Close stream
		::endmntent(filePtr);

		// Return mount directory
		return mountDir;
	}
	catch (...)
	{
		// Close stream
		if (filePtr)
			::endmntent(filePtr);

		// Rethrow exception
		throw;
	}
}

//----------------------------------------------------------------------
