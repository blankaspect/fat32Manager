/*====================================================================*\

FileException.cc

Class: file exception.

\*====================================================================*/


// INCLUDES


#ifndef _DRIVEIO_FILE_EXCEPTION_H_
	#include "FileException.h"
#endif

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

const wchar_t*	LOCATION_STR	= L"Location: ";

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

FileException::FileException(
	const std::wstring&	message,
	const std::wstring&	pathname) :

	Exception(message),
	_pathname(pathname)
{
}

//----------------------------------------------------------------------

FileException::FileException(
	const std::wstring&	message,
	const std::wstring&	pathname,
	int					sysErrorNum) :

	Exception(message, sysErrorNum),
	_pathname(pathname)
{
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

std::wstring FileException::toString() const
{
	std::wstring str(LOCATION_STR);
	str += _pathname;
	str += L"\n";
	str += Exception::toString();
	return str;
}

//----------------------------------------------------------------------
