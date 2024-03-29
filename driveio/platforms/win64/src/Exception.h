/*====================================================================*\

Exception.h

Class: exception.

\*====================================================================*/

#ifndef _DRIVEIO_EXCEPTION_H_
#define _DRIVEIO_EXCEPTION_H_

//----------------------------------------------------------------------


// INCLUDES


#ifndef _DRIVEIO_TYPES_H_
	#include "types.h"
#endif

#include <string>

//----------------------------------------------------------------------


// CLASS: EXCEPTION


class Exception
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

private:

	const std::wstring&	_message;
	bool				_fatal;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

public:

	Exception(
		const std::wstring&	message);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

public:

	const std::wstring& getMessage() const
	{
		return _message;
	}

	//------------------------------------------------------------------

	bool isFatal() const
	{
		return _fatal;
	}

	//------------------------------------------------------------------

	virtual std::wstring toString() const
	{
		return _message;
	}

	//------------------------------------------------------------------

protected:

	void setFatal(
		bool	fatal)
	{
		_fatal = fatal;
	}

	//------------------------------------------------------------------

};

//----------------------------------------------------------------------

#endif	// _DRIVEIO_EXCEPTION_H_

//----------------------------------------------------------------------
