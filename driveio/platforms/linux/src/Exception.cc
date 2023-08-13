/*====================================================================*\

Exception.cc

Class: exception.

\*====================================================================*/


// INCLUDES


#ifndef _DRIVEIO_EXCEPTION_H_
	#include "Exception.h"
#endif
#ifndef _DRIVEIO_STR_CONV_H_
	#include "StrConv.h"
#endif

#include <cstring>

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

Exception::Exception(
	const std::wstring&	message) :

	_message(message),
	_fatal  (false)
{
}

//----------------------------------------------------------------------

Exception::Exception(
	const std::wstring&	message,
	int					sysErrorNum) :

	_message    (message),
	_sysErrorNum(sysErrorNum),
	_fatal      (false)
{
}

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

std::wstring Exception::toString() const
{
	std::wstring str(_message);
	if (_sysErrorNum != 0)
	{
		str += L"\n";
		str += StrConv::strToWstr(std::string(::strerror(_sysErrorNum)));
	}
	return str;
}

//----------------------------------------------------------------------
