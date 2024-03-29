/*====================================================================*\

Utils.h

Class: utility methods.

\*====================================================================*/

#ifndef _DRIVEIO_UTILS_H_
#define _DRIVEIO_UTILS_H_

//----------------------------------------------------------------------


// INCLUDES


#ifndef _DRIVEIO_TYPES_H_
	#include "types.h"
#endif

#include <string>

//----------------------------------------------------------------------


// CLASS: UTILITIES


class Utils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

private:

	Utils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

public:

	static const std::wstring trim(
		std::wstring&	str);

	//------------------------------------------------------------------

};

//----------------------------------------------------------------------

#endif	// _DRIVEIO_UTILS_H_

//----------------------------------------------------------------------
