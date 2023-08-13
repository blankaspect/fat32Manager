/*====================================================================*\

Volume.cc

Class: volume.

\*====================================================================*/


// INCLUDES


#ifndef _DRIVEIO_VOLUME_H_
	#include "Volume.h"
#endif
#ifndef _DRIVEIO_PARTITION_INFO_H_
	#include "PartitionInfo.h"
#endif

#include <errno.h>
#include <fcntl.h>
#include <unistd.h>

#include <linux/fs.h>
#include <sys/ioctl.h>

//----------------------------------------------------------------------


// CLASS: VOLUME


////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

// Minimum size of buffer for volume information
const int	MIN_INFO_BUFFER_SIZE	= sizeof(VolumeInfo);

// Pathnames of directories
namespace DirPathname
{
	const std::string	DEVICE	("/dev");
}

// Error messages
namespace ErrorMsg
{
	const std::wstring	VOLUME_IS_OPEN
						(L"The volume is already open.");

	const std::wstring	VOLUME_IS_NOT_OPEN
						(L"The volume is not open.");

	const std::wstring	FAILED_TO_OPEN_VOLUME
						(L"Failed to open the volume.");

	const std::wstring	FAILED_TO_CLOSE_VOLUME
						(L"Failed to close the volume.");

	const std::wstring	BUFFER_FOR_VOLUME_INFO_TOO_SMALL
						(L"The size of the buffer for information about the volume must be at least "
							+ std::to_wstring(MIN_INFO_BUFFER_SIZE) + L" bytes.");

	const std::wstring	FAILED_TO_READ_VOLUME_INFO
						(L"Failed to read information about the volume.");

	const std::wstring	SEEK_POSITION_NOT_SECTOR_ALIGNED
						(L"The seek position is not aligned with the start of a sector.");

	const std::wstring	SEEK_ERROR
						(L"An error occurred when setting the seek position.");

	const std::wstring	LENGTH_NOT_INTEGRAL_MULTIPLE_OF_SECTOR_LENGTH
						(L"The length is not an integral multiple of the sector length.");

	const std::wstring	ERROR_READING_VOLUME
						(L"An error occurred when reading the volume.");

	const std::wstring	ERROR_WRITING_VOLUME
						(L"An error occurred when writing the volume.");
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

Volume::Volume(
	const std::string&	name) :

	_fileDescriptor(-1),
	_name          (name)
{
}

//----------------------------------------------------------------------

Volume::~Volume()
{
	if (_fileDescriptor >= 0)
		::close(_fileDescriptor);
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

void Volume::getInfo(
	const std::string&	name,
	VolumeInfo*			infoBufferPtr)
{
	// Get bytes per sector
	infoBufferPtr->_bytesPerSector = PartitionInfo::getBytesPerSector(name);

	// Get partition information
	PartitionInfo partInfo = PartitionInfo::createInfo(name);

	// Set remaining items of volume information
	infoBufferPtr->_startSector = partInfo.getStartSector();
	infoBufferPtr->_numSectors = partInfo.getNumSectors();
	infoBufferPtr->_mediumKind = partInfo.getMediumKind();
}

//----------------------------------------------------------------------

void Volume::getInfo(
	const std::string&	name,
	void*				buffer,
	int					bufferLength)
{
	// Test buffer length
	if (bufferLength < MIN_INFO_BUFFER_SIZE)
		throw Exception(ErrorMsg::BUFFER_FOR_VOLUME_INFO_TOO_SMALL);

	// Get information
	getInfo(name, reinterpret_cast<VolumeInfo*>(buffer));
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

void Volume::open(
	int	accessMode)
{
	// Test whether volume is open
	if (_fileDescriptor >= 0)
		throw VolumeException(ErrorMsg::VOLUME_IS_OPEN, _name);

	// Get access mode
	int accessMode0 = O_CLOEXEC | O_NONBLOCK;
	switch (accessMode)
	{
		case rwMode::READ:
			accessMode0 |= O_RDONLY;
			break;

		case rwMode::WRITE:
			accessMode0 |= O_WRONLY;
			break;

		case rwMode::READ_WRITE:
			accessMode0 |= O_RDWR;
			break;
	}

	// Open volume and read bytes per sector
	int fd = -1;
	try
	{
		// Open volume
		const std::string pathname = DirPathname::DEVICE + "/" + _name;
		fd = ::open(pathname.c_str(), accessMode0);
		if (fd < 0)
			throw VolumeException(ErrorMsg::FAILED_TO_OPEN_VOLUME, _name, errno);

		// Read bytes per sector
		UInt64 sectorSize = 0;
		if (::ioctl(fd, BLKSSZGET, &sectorSize) < 0)
			throw VolumeException(ErrorMsg::FAILED_TO_READ_VOLUME_INFO, _name, errno);

		// Update instance variables
		_fileDescriptor = fd;
		_bytesPerSector = sectorSize;
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

void Volume::close()
{
	// Test whether volume is open
	if (_fileDescriptor < 0)
		throw VolumeException(ErrorMsg::VOLUME_IS_NOT_OPEN, _name);

	// Invalidate file descriptor
	int fd = _fileDescriptor;
	_fileDescriptor = -1;

	// Close volume
	if (::close(fd) < 0)
		throw VolumeException(ErrorMsg::FAILED_TO_CLOSE_VOLUME, _name, errno);
}

//----------------------------------------------------------------------

void Volume::seek(
	SInt64	offset) const
{
	// Test whether volume is open
	if (_fileDescriptor < 0)
		throw VolumeException(ErrorMsg::VOLUME_IS_NOT_OPEN, _name);

	// Validate arguments
	if (offset % _bytesPerSector != 0)
		throw VolumeException(ErrorMsg::SEEK_POSITION_NOT_SECTOR_ALIGNED, _name);

	// Set file position
	off_t result = ::lseek(_fileDescriptor, offset, SEEK_SET);
	if (result < 0)
		throw VolumeException(ErrorMsg::SEEK_ERROR, _name, errno);
}

//----------------------------------------------------------------------

void Volume::read(
	void*	buffer,
	int		length)
{
	// Test whether volume is open
	if (_fileDescriptor < 0)
		throw VolumeException(ErrorMsg::VOLUME_IS_NOT_OPEN, _name);

	// Validate arguments
	if (length % _bytesPerSector != 0)
		throw VolumeException(ErrorMsg::LENGTH_NOT_INTEGRAL_MULTIPLE_OF_SECTOR_LENGTH, _name);

	// Read data from volume into buffer
	SInt64 readLength = ::read(_fileDescriptor, buffer, length);
	if (readLength < 0)
		throw VolumeException(ErrorMsg::ERROR_READING_VOLUME, _name, errno);
	if (readLength != length)
		throw VolumeException(ErrorMsg::ERROR_READING_VOLUME, _name);
}

//----------------------------------------------------------------------

void Volume::write(
	void*	data,
	int		length)
{
	// Test whether volume is open
	if (_fileDescriptor < 0)
		throw VolumeException(ErrorMsg::VOLUME_IS_NOT_OPEN, _name);

	// Validate arguments
	if (length % _bytesPerSector != 0)
		throw VolumeException(ErrorMsg::LENGTH_NOT_INTEGRAL_MULTIPLE_OF_SECTOR_LENGTH, _name);

	// Write data to volume
	SInt64 writeLength = ::write(_fileDescriptor, data, length);
	if (writeLength < 0)
		throw VolumeException(ErrorMsg::ERROR_WRITING_VOLUME, _name, errno);
	if (writeLength != length)
		throw VolumeException(ErrorMsg::ERROR_WRITING_VOLUME, _name);
}

//----------------------------------------------------------------------
