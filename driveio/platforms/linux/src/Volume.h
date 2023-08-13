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
#ifndef _DRIVEIO_STR_CONV_H_
	#include "StrConv.h"
#endif

#ifndef _Included_uk_blankaspect_driveio_DriveIO
	#include "uk_blankaspect_driveio_DriveIO.h"
#endif

#include <cstring>

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

namespace rwMode
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


typedef struct __attribute__ ((packed))
{
	int		_bytesPerSector;
	SInt64	_startSector;
	SInt64	_numSectors;
	short	_mediumKind;
} VolumeInfo;

//----------------------------------------------------------------------


// CLASS: VOLUME


class Volume
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

private:

	int			_fileDescriptor;
	std::string	_name;
	int			_bytesPerSector;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

public:

	Volume(
		const std::string&	name);

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

	static void getInfo(
		const std::string&	name,
		VolumeInfo*			infoBufferPtr);

	//------------------------------------------------------------------

	static void getInfo(
		const std::string&	name,
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
		return (_fileDescriptor >= 0);
	}

	//------------------------------------------------------------------

	void open(
		int	accessMode);

	//------------------------------------------------------------------

	void close();

	//------------------------------------------------------------------

	void seek(
		SInt64	position) const;

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

		std::string	_volumeName;
		int			_sysErrorNum;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

	public:

		VolumeException(
			const std::wstring&	message,
			const std::string&	volumeName) :

			VolumeException(message, volumeName, 0)
		{
		}

		//--------------------------------------------------------------

		VolumeException(
			const std::wstring&	message,
			int					sysErrorNum) :

			VolumeException(message, "", sysErrorNum)
		{
		}

		//--------------------------------------------------------------

		VolumeException(
			const std::wstring&	message,
			const std::string&	volumeName,
			int					sysErrorNum) :

			Exception   (message),
			_volumeName (volumeName),
			_sysErrorNum(sysErrorNum)
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
				str.append(StrConv::strToWstr(_volumeName));
				str.append(L"\n");
			}

			// Append message
			str.append(getMessage());

			// Append system error message
			if (_sysErrorNum != 0)
			{
				str += L"\n";
				str += StrConv::strToWstr(std::string(::strerror(_sysErrorNum)));
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
