/*====================================================================*\

Utils.cc

Class: utility methods.

\*====================================================================*/


// INCLUDES


#ifndef _DRIVEIO_UTILS_H_
	#include "Utils.h"
#endif

//----------------------------------------------------------------------

const std::wstring Utils::trim(
	std::wstring&	str)
{
	static const	wchar_t	WHITESPACE_CHARS[]	= L"\t\n\r ";

	std::wstring outStr = std::wstring(str);
	int pos = outStr.find_first_not_of(WHITESPACE_CHARS);
	outStr.erase(0, pos);
	pos = outStr.find_last_not_of(WHITESPACE_CHARS);
	if (pos == std::wstring::npos)
		outStr.erase();
	else
		outStr.erase(++pos);
	return outStr;
}

//----------------------------------------------------------------------
