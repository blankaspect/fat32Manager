/*====================================================================*\

Exception.cc

Class: exception.

\*====================================================================*/


// INCLUDES


#ifndef _DRIVEIO_EXCEPTION_H_
	#include "Exception.h"
#endif

//----------------------------------------------------------------------

Exception::Exception(
	const std::wstring&	message) :

	_message(message),
	_fatal	(false)
{
}

//----------------------------------------------------------------------
