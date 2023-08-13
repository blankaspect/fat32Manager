/*====================================================================*\

Volume.cc

Class: volume.

\*====================================================================*/


// INCLUDES


#ifndef _DRIVEIO_VOLUME_H_
	#include "Volume.h"
#endif

//----------------------------------------------------------------------


// CLASS: VOLUME


////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

// Minimum size of buffer for volume information
const	int	MIN_INFO_BUFFER_SIZE	= sizeof(VolumeInfo);

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

	const std::wstring	FAILED_TO_LOCK_VOLUME
						(L"Failed to lock the volume.");

	const std::wstring	BUFFER_FOR_VOLUME_INFO_TOO_SMALL
						(L"The size of the buffer for information about the volume must be at least "
							+ std::to_wstring(MIN_INFO_BUFFER_SIZE) + L" bytes.");

	const std::wstring	FAILED_TO_GET_VOLUME_INFO
						(L"Failed to get information about the volume.");

	const std::wstring	FAILED_TO_GET_PARTITION_INFO
						(L"Failed to get partition information for the volume.");

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

	const std::wstring	FAILED_TO_GET_AVAILABLE_VOLUMES
						(L"Failed to get a list of available volumes.");

	const std::wstring	NOT_ENOUGH_MEMORY
						(L"There is not enough memory to perform the current operation.");
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

Volume::Volume(
	const std::wstring&	name) :

	_handle(INVALID_HANDLE_VALUE),
	_name  (name)
{
}

//----------------------------------------------------------------------

Volume::~Volume()
{
	if (_handle != INVALID_HANDLE_VALUE)
		::CloseHandle(_handle);
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

UInt32 Volume::getVolumes()
{
	UInt32 drives = ::GetLogicalDrives();
	if (drives == 0)
		throw VolumeException(ErrorMsg::FAILED_TO_GET_AVAILABLE_VOLUMES, true);
	return drives;
}

//----------------------------------------------------------------------

std::wstring Volume::volumeIndexToString(
	int	volumeIndex)
{
	std::wstring str(L"A:");
	str[0] += volumeIndex;
	return str;
}

//----------------------------------------------------------------------

bool Volume::isVolumeMounted(
	const std::wstring&	name)
{
	// Initialise result of search
	bool found = false;

	// Search available volumes for target name
	try
	{
		// Get a bit array of available volumes
		UInt32 volumes = getVolumes();

		// Search for volume whose name matches target name
		int index = 0;
		int offset = 0;
		while (volumes != 0)
		{
			// If the volume is available, compare its name with the target name
			if ((volumes & 1) && (name == volumeIndexToString(index)))
			{
				// Get information about the volume to test whether it is mounted rather than 'available'
				found = (::GetVolumeInformation(name.c_str(), NULL, 0, NULL, NULL, NULL, NULL, 0) == TRUE)
							|| (::GetLastError() != ERROR_NOT_READY);
				break;
			}

			// Increment the volume index
			++index;

			// Shift the bit array of volumes
			volumes >>= 1;
		}
	}
	catch (const Exception& e)
	{
		// ignore
	}

	// Return result of search
	return found;
}

//----------------------------------------------------------------------

void Volume::getInfo(
	const std::wstring&	name,
	VolumeInfo*			infoBufferPtr)
{
	// Open volume
	std::wstring pathname(L"\\\\.\\" + name);
	HANDLE handle = ::CreateFile(pathname.c_str(), GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, 0, NULL);
	if (handle == INVALID_HANDLE_VALUE)
		throw VolumeException(ErrorMsg::FAILED_TO_OPEN_VOLUME, name, true);

	// Get drive geometry
	DISK_GEOMETRY_EX geometry {};
	DWORD returned = 0;
	VolumeException* errorPtr = ::DeviceIoControl(handle, IOCTL_DISK_GET_DRIVE_GEOMETRY_EX, NULL, 0, &geometry,
												  sizeof(geometry), &returned, NULL)
											? NULL
											: &VolumeException(ErrorMsg::FAILED_TO_GET_VOLUME_INFO, name, true);

	// Get partition information
	PARTITION_INFORMATION_EX partitionInfo {};
	if (!errorPtr)
		errorPtr = ::DeviceIoControl(handle, IOCTL_DISK_GET_PARTITION_INFO_EX, NULL, 0, &partitionInfo,
									 sizeof(partitionInfo), &returned, NULL)
											? NULL
											: &VolumeException(ErrorMsg::FAILED_TO_GET_PARTITION_INFO, name, true);

	// Close volume
	if (!::CloseHandle(handle) && !errorPtr)
		throw VolumeException(ErrorMsg::FAILED_TO_CLOSE_VOLUME, name, true);

	// Throw deferred error
	if (errorPtr)
		throw *errorPtr;

	// Set volume information in buffer
	int bytesPerSector = geometry.Geometry.BytesPerSector;
	infoBufferPtr->_bytesPerSector = bytesPerSector;
	infoBufferPtr->_startSector = partitionInfo.StartingOffset.QuadPart / bytesPerSector;
	infoBufferPtr->_numSectors = partitionInfo.PartitionLength.QuadPart / bytesPerSector;
	switch (geometry.Geometry.MediaType)
	{
		case Unknown:
			infoBufferPtr->_mediumKind = mediumKind::UNKNOWN;
			break;

		case RemovableMedia:
			infoBufferPtr->_mediumKind = mediumKind::REMOVABLE;
			break;

		case FixedMedia:
			infoBufferPtr->_mediumKind = mediumKind::FIXED;
			break;

		default:
			infoBufferPtr->_mediumKind = ((geometry.Geometry.MediaType >= 0x01) && (geometry.Geometry.MediaType <= 0x19))
													? mediumKind::REMOVABLE
													: mediumKind::UNKNOWN;
			break;
	}
}

//----------------------------------------------------------------------

void Volume::getInfo(
	const std::wstring&	name,
	void*				buffer,
	int					bufferLength)
{
	// Test buffer length
	if (bufferLength < MIN_INFO_BUFFER_SIZE)
		throw VolumeException(ErrorMsg::BUFFER_FOR_VOLUME_INFO_TOO_SMALL, name);

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
	// Get volume information
	VolumeInfo info {};
	getInfo(_name, &info);

	// Get access mode and share mode
	int accessMode0 = 0;
	int shareMode = 0;
	switch (accessMode)
	{
		case access::READ:
			accessMode0 = GENERIC_READ;
			shareMode = FILE_SHARE_READ;
			break;

		case access::WRITE:
			accessMode0 = GENERIC_WRITE;
			shareMode = FILE_SHARE_WRITE;
			break;

		case access::READ_WRITE:
			accessMode0 = GENERIC_READ | GENERIC_WRITE;
			shareMode = FILE_SHARE_READ | FILE_SHARE_WRITE;
			break;
	}

	// Open volume
	std::wstring pathname(L"\\\\.\\" + _name);
	_handle = ::CreateFile(pathname.c_str(), accessMode0, shareMode, NULL, OPEN_EXISTING, 0, NULL);
	if (_handle == INVALID_HANDLE_VALUE)
		throw VolumeException(ErrorMsg::FAILED_TO_OPEN_VOLUME, _name, true);

	// Lock volume
	DWORD result;
	if (!::DeviceIoControl(_handle, FSCTL_LOCK_VOLUME, NULL, 0, NULL, 0, &result, NULL))
		throw VolumeException(ErrorMsg::FAILED_TO_LOCK_VOLUME, _name, true);

	// Update instance variables
	_bytesPerSector = info._bytesPerSector;
}

//----------------------------------------------------------------------

void Volume::close()
{
	// Test whether volume is open
	if (_handle == INVALID_HANDLE_VALUE)
		throw VolumeException(ErrorMsg::VOLUME_IS_NOT_OPEN, _name);

	// Invalidate handle
	HANDLE handle = _handle;
	_handle = INVALID_HANDLE_VALUE;

	// Close volume
	if (!::CloseHandle(handle))
		throw VolumeException(ErrorMsg::FAILED_TO_CLOSE_VOLUME, _name, true);
}

//----------------------------------------------------------------------

void Volume::seek(
	__int64	position) const
{
	// Test whether volume is open
	if (_handle == INVALID_HANDLE_VALUE)
		throw VolumeException(ErrorMsg::VOLUME_IS_NOT_OPEN, _name);

	// Validate arguments
	if (position % _bytesPerSector != 0)
		throw VolumeException(ErrorMsg::SEEK_POSITION_NOT_SECTOR_ALIGNED, _name);

	// Set file position
	LARGE_INTEGER position0;
	position0.QuadPart = position;
	DWORD result = ::SetFilePointer(_handle, position0.LowPart, &position0.HighPart, FILE_BEGIN);
	if (result == INVALID_SET_FILE_POINTER)
		throw VolumeException(ErrorMsg::SEEK_ERROR, _name, true);
}

//----------------------------------------------------------------------

void Volume::read(
	void*	buffer,
	int		length)
{
	// Test whether volume is open
	if (_handle == INVALID_HANDLE_VALUE)
		throw VolumeException(ErrorMsg::VOLUME_IS_NOT_OPEN, _name);

	// Validate arguments
	if (length % _bytesPerSector != 0)
		throw VolumeException(ErrorMsg::LENGTH_NOT_INTEGRAL_MULTIPLE_OF_SECTOR_LENGTH, _name);

	// Read data from volume into buffer
	DWORD readLength;
	if (!::ReadFile(_handle, buffer, length, &readLength, NULL))
		throw VolumeException(ErrorMsg::ERROR_READING_VOLUME, _name, true);
	if (readLength != length)
		throw VolumeException(ErrorMsg::ERROR_READING_VOLUME, _name);
}

//----------------------------------------------------------------------

void Volume::write(
	void*	data,
	int		length)
{
	// Test whether volume is open
	if (_handle == INVALID_HANDLE_VALUE)
		throw VolumeException(ErrorMsg::VOLUME_IS_NOT_OPEN, _name);

	// Validate arguments
	if (length % _bytesPerSector != 0)
		throw VolumeException(ErrorMsg::LENGTH_NOT_INTEGRAL_MULTIPLE_OF_SECTOR_LENGTH, _name);

	// Write data to volume
	DWORD writeLength;
	if (!::WriteFile(_handle, data, length, &writeLength, NULL))
		throw VolumeException(ErrorMsg::ERROR_WRITING_VOLUME, _name, true);
	if (writeLength != length)
		throw VolumeException(ErrorMsg::ERROR_WRITING_VOLUME, _name);
}

//----------------------------------------------------------------------
