/*====================================================================*\

Volume.h

Class: volume.

\*====================================================================*/

#ifndef _DRIVEIO_VOLUME_H_
#define _DRIVEIO_VOLUME_H_

//----------------------------------------------------------------------


// INCLUDES


#ifndef _DRIVEIO_EXCEPTION_H_
	#include "Exception.h"
#endif
#ifndef _DRIVEIO_UTILS_H_
	#include "Utils.h"
#endif

#ifndef _Included_uk_blankaspect_driveio_DriveIO
	#include "uk_blankaspect_driveio_DriveIO.h"
#endif

#include <windows.h>

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

namespace access
{
	enum
	{
		READ		= uk_blankaspect_driveio_DriveIO_ACCESS_READ,
		WRITE		= uk_blankaspect_driveio_DriveIO_ACCESS_WRITE,
		READ_WRITE	= uk_blankaspect_driveio_DriveIO_ACCESS_READ_WRITE
	};
}

namespace mediumKind
{
	enum
	{
		UNKNOWN		= uk_blankaspect_driveio_DriveIO_MEDIUM_UNKNOWN,
		REMOVABLE	= uk_blankaspect_driveio_DriveIO_MEDIUM_REMOVABLE,
		FIXED		= uk_blankaspect_driveio_DriveIO_MEDIUM_FIXED
	};
}

////////////////////////////////////////////////////////////////////////
//  Type definitions
////////////////////////////////////////////////////////////////////////


// STRUCTURE: VOLUME INFORMATION


#pragma pack(push, 1)
typedef struct
{
	int		_bytesPerSector;
	__int64	_startSector;
	__int64	_numSectors;
	short	_mediumKind;
} VolumeInfo;
#pragma pack(pop)

//----------------------------------------------------------------------


// CLASS: VOLUME


class Volume
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

private:

	HANDLE			_handle;
	std::wstring	_name;
	int				_bytesPerSector;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

public:

	Volume(
		const std::wstring&	name);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Destructors
////////////////////////////////////////////////////////////////////////

public:

	virtual ~Volume();

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

public:

	static UInt32 getVolumes();

	//------------------------------------------------------------------

	static std::wstring volumeIndexToString(
		int	volumeIndex);

	//------------------------------------------------------------------

	static bool isVolumeMounted(
		const std::wstring&	name);

	//------------------------------------------------------------------

	static void getInfo(
		const std::wstring&	name,
		VolumeInfo*			infoBufferPtr);

	//------------------------------------------------------------------

	static void getInfo(
		const std::wstring&	name,
		void*				buffer,
		int					bufferLength);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

public:

	int getBytesPerSector() const
	{
		return _bytesPerSector;
	}

	//------------------------------------------------------------------

	bool isOpen() const
	{
		return (_handle != INVALID_HANDLE_VALUE);
	}

	//------------------------------------------------------------------

	void open(
		int	accessMode);

	//------------------------------------------------------------------

	void close();

	//------------------------------------------------------------------

	void seek(
		__int64	position) const;

	//------------------------------------------------------------------

	void read(
		void*	buffer,
		int		length);

	//------------------------------------------------------------------

	void write(
		void*	data,
		int		length);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes
////////////////////////////////////////////////////////////////////////

private:


	// CLASS: VOLUME EXCEPTION


	class VolumeException : public Exception
	{

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

	private:

		std::wstring	_volumeName;
		int				_sysErrorNum;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

	public:

		VolumeException(
			const std::wstring&	message,
			const std::wstring&	volumeName) :

			VolumeException(message, volumeName, false)
		{
		}

		//--------------------------------------------------------------

		VolumeException(
			const std::wstring&	message,
			bool				sysError) :

			VolumeException(message, L"", sysError)
		{
		}

		//--------------------------------------------------------------

		VolumeException(
			const std::wstring&	message,
			const std::wstring&	volumeName,
			bool				sysError) :

			Exception   (message),
			_volumeName (volumeName),
			_sysErrorNum(sysError ? ::GetLastError() : 0)
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

	public:

		virtual std::wstring toString() const
		{
			// Initialise string
			std::wstring str;

			// Append volume name
			if (_volumeName.length() > 0)
			{
				str.append(L"Volume: ");
				str.append(_volumeName);
				str.append(L"\n");
			}

			// Append message
			str.append(getMessage());

			// Append system error message
			if (_sysErrorNum != 0)
			{
				wchar_t* msgBufPtr;
				::FormatMessage(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM
									| FORMAT_MESSAGE_IGNORE_INSERTS | FORMAT_MESSAGE_MAX_WIDTH_MASK,
								0, _sysErrorNum, 0, reinterpret_cast<LPTSTR>(&msgBufPtr), 0, 0);
				std::wstring sysMessage(msgBufPtr);
				::LocalFree(msgBufPtr);
				str.append(L"\n- " + Utils::trim(sysMessage));
			}

			// Return string
			return str;
		}

		//--------------------------------------------------------------

	};

	//==================================================================

};

//----------------------------------------------------------------------

#endif	// _DRIVEIO_VOLUME_H_

//----------------------------------------------------------------------
