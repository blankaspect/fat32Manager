/*====================================================================*\

types.h

Type definitions.

\*====================================================================*/

#ifndef _DRIVEIO_TYPES_H_
#define _DRIVEIO_TYPES_H_

//----------------------------------------------------------------------


// INCLUDES


#include <cassert>
#include <string>
#include <vector>

//----------------------------------------------------------------------


// TYPE DEFINITIONS


// Signed and unsigned integers
typedef unsigned char  UChar;
typedef unsigned short UShort;
typedef unsigned int   UInt;
typedef unsigned long  ULong;

typedef	unsigned char  UInt8;
typedef	signed char    SInt8;
static_assert(sizeof(UInt8) == 1);

typedef	unsigned short UInt16;
typedef	signed short   SInt16;
static_assert(sizeof(UInt16) == 2);

typedef	unsigned int   UInt32;
typedef	signed int     SInt32;
static_assert(sizeof(UInt32) == 4);

typedef	unsigned long  UInt64;
typedef	signed long    SInt64;
static_assert(sizeof(UInt64) == 8);

// Vectors
typedef std::vector<std::string>  StringVector;
typedef std::vector<std::wstring> WstringVector;

//----------------------------------------------------------------------

#endif	// _DRIVEIO_TYPES_H_

//----------------------------------------------------------------------
