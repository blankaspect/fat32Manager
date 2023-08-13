/*====================================================================*\

StrConv.cc

Class: string-conversion methods.

\*====================================================================*/


// INCLUDES


#ifndef _DRIVEIOTEST_STR_CONV_H_
	#include "StrConv.h"
#endif

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

static const	UInt32	LOWER_BOUND1	= 0x80;
static const	UInt32	UPPER_BOUND1	= 0xBF;

static const	UInt32	LOWER_BOUND2	= 0xA0;
static const	UInt32	UPPER_BOUND2	= 0x9F;

static const	UInt32	LOWER_BOUND3	= 0x90;
static const	UInt32	UPPER_BOUND3	= 0x8F;

static const	UInt32	MIN_HIGH_SURROGATE	= 0xD800;

static const	UInt32	MIN_LOW_SURROGATE	= 0xDC00;
static const	UInt32	MAX_LOW_SURROGATE	= 0xDFFF;

static const	UInt32	REPLACEMENT_CHAR	= 0xFFFD;

static const	UInt32	PLANE1_MIN_VALUE	= 0x10000;

static const	UInt32	MAX_VALUE	= 0x10FFFF;

static const	UInt32	MIN_VALUES[]	=
{
	1 << 7,		// 2-byte sequence
	1 << 11,	// 3-byte sequence
	1 << 16		// 4-byte sequence
};

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

std::u16string StrConv::utf8ToUtf16(
	const std::string&	str)
{
	// Initialise output string
	std::u16string outStr;

	// Initialise decoder variables
	UInt32 outCode = 0;
	UInt32 lowerBound = LOWER_BOUND1;
	UInt32 upperBound = UPPER_BOUND1;
	int numBytesExpected = 0;
	int numBytesProcessed = 0;

	// Convert string
	int inLength = str.length();
	int index = 0;
	while (index < inLength)
	{
		// Get next input byte
		UInt32 inByte = str[index++] & 0xFF;

		// Case: start of byte sequence
		if (numBytesExpected == 0)
		{
			// Case: 1-byte sequence
			if (inByte < 0x80)
				outStr.push_back(inByte);

			// Case: invalid character
			else if (inByte < 0xC2)
				outStr.push_back(REPLACEMENT_CHAR);

			// Case: 2-byte sequence
			else if (inByte < 0xE0)
			{
				// Initialise output code
				outCode = inByte & 0x1F;

				// One more byte expected
				numBytesExpected = 1;
			}

			// Case: 3-byte sequence
			else if (inByte < 0xF0)
			{
				// Initialise output code
				outCode = inByte & 0x0F;

				// Adjust bounds of next byte
				if (inByte == 0xE0)
					lowerBound = LOWER_BOUND2;
				else if (inByte == 0xED)
					upperBound = UPPER_BOUND2;

				// Two more bytes expected
				numBytesExpected = 2;
			}

			// Case: 4-byte sequence
			else if (inByte < 0xF5)
			{
				// Initialise output code
				outCode = inByte & 0x07;

				// Adjust bounds of next byte
				if (inByte == 0xF0)
					lowerBound = LOWER_BOUND3;
				else if (inByte == 0xF4)
					upperBound = UPPER_BOUND3;

				// Three more bytes expected
				numBytesExpected = 3;
			}

			// Case: invalid character
			else
				outStr.push_back(REPLACEMENT_CHAR);
		}

		// Case: byte sequence, byte out of bounds
		else if ((inByte < lowerBound) || (inByte > upperBound))
		{
			// Reset decoder variables
			outCode = 0;
			lowerBound = LOWER_BOUND1;
			upperBound = UPPER_BOUND1;
			numBytesExpected = 0;
			numBytesProcessed = 0;

			// Put byte back in input sequence
			--index;

			// Append replacement character to output string
			outStr.push_back(REPLACEMENT_CHAR);
		}

		// Case: byte sequence, valid byte
		else
		{
			// Reset bounds
			lowerBound = LOWER_BOUND1;
			upperBound = UPPER_BOUND1;

			// Add input byte to output code
			outCode <<= 6;
			outCode |= (inByte & 0x3F);

			// If byte sequence is complete, append output code to output string
			if (++numBytesProcessed >= numBytesExpected)
			{
				// Append output code to output string
				if (outCode < PLANE1_MIN_VALUE)
					outStr.push_back(outCode);
				else
				{
					UInt32 offset = outCode - PLANE1_MIN_VALUE;
					outStr.push_back(MIN_HIGH_SURROGATE + (offset >> 10));
					outStr.push_back(MIN_LOW_SURROGATE + (offset & 0x3FF));
				}

				// Reset decoder variables
				outCode = 0;
				numBytesExpected = 0;
				numBytesProcessed = 0;
			}
		}
	}

	// If partial byte sequence at end of input sequence, append replacement character to output string
	if (numBytesExpected > 0)
		outStr.push_back(REPLACEMENT_CHAR);

	// Return output string
	return outStr;
}

//----------------------------------------------------------------------

std::u32string StrConv::utf8ToUtf32(
	const std::string&	str)
{
	// Initialise output string
	std::u32string outStr;

	// Initialise decoder variables
	UInt32 outCode = 0;
	UInt32 lowerBound = LOWER_BOUND1;
	UInt32 upperBound = UPPER_BOUND1;
	int numBytesExpected = 0;
	int numBytesProcessed = 0;

	// Convert string
	int inLength = str.length();
	int index = 0;
	while (index < inLength)
	{
		// Get next input byte
		UInt32 inByte = str[index++] & 0xFF;

		// Case: start of byte sequence
		if (numBytesExpected == 0)
		{
			// Case: 1-byte sequence
			if (inByte < 0x80)
				outStr.push_back(inByte);

			// Case: invalid character
			else if (inByte < 0xC2)
				outStr.push_back(REPLACEMENT_CHAR);

			// Case: 2-byte sequence
			else if (inByte < 0xE0)
			{
				// Initialise output code
				outCode = inByte & 0x1F;

				// One more byte expected
				numBytesExpected = 1;
			}

			// Case: 3-byte sequence
			else if (inByte < 0xF0)
			{
				// Initialise output code
				outCode = inByte & 0x0F;

				// Adjust bounds of next byte
				if (inByte == 0xE0)
					lowerBound = LOWER_BOUND2;
				else if (inByte == 0xED)
					upperBound = UPPER_BOUND2;

				// Two more bytes expected
				numBytesExpected = 2;
			}

			// Case: 4-byte sequence
			else if (inByte < 0xF5)
			{
				// Initialise output code
				outCode = inByte & 0x07;

				// Adjust bounds of next byte
				if (inByte == 0xF0)
					lowerBound = LOWER_BOUND3;
				else if (inByte == 0xF4)
					upperBound = UPPER_BOUND3;

				// Three more bytes expected
				numBytesExpected = 3;
			}

			// Case: invalid character
			else
				outStr.push_back(REPLACEMENT_CHAR);
		}

		// Case: byte sequence, byte out of bounds
		else if ((inByte < lowerBound) || (inByte > upperBound))
		{
			// Reset decoder variables
			outCode = 0;
			lowerBound = LOWER_BOUND1;
			upperBound = UPPER_BOUND1;
			numBytesExpected = 0;
			numBytesProcessed = 0;

			// Put byte back in input sequence
			--index;

			// Append replacement character to output string
			outStr.push_back(REPLACEMENT_CHAR);
		}

		// Case: byte sequence, valid byte
		else
		{
			// Reset bounds
			lowerBound = LOWER_BOUND1;
			upperBound = UPPER_BOUND1;

			// Add input byte to output code
			outCode <<= 6;
			outCode |= (inByte & 0x3F);

			// If byte sequence is complete, append output code to output string
			if (++numBytesProcessed >= numBytesExpected)
			{
				// Append output code to output string
				outStr.push_back(outCode);

				// Reset decoder variables
				outCode = 0;
				numBytesExpected = 0;
				numBytesProcessed = 0;
			}
		}
	}

	// If partial byte sequence at end of input sequence, append replacement character to output string
	if (numBytesExpected > 0)
		outStr.push_back(REPLACEMENT_CHAR);

	// Return output string
	return outStr;
}

//----------------------------------------------------------------------

std::string StrConv::utf16ToUtf8(
	const std::u16string&	str)
{
	// Initialise output string
	std::string outStr;

	// Convert string
	int inLength = str.length();
	int index = 0;
	while (index < inLength)
	{
		// Get next input code
		UInt32 inCode = str[index++];

		// Replace code that is out of bounds
		if (inCode > MAX_VALUE)
			inCode = REPLACEMENT_CHAR;

		// Test for surrogate
		else if ((inCode >= MIN_HIGH_SURROGATE) && (inCode < MIN_LOW_SURROGATE))
		{
			if (index < inLength)
			{
				UInt32 inCode0 = str[index];
				if ((inCode0 >= MIN_LOW_SURROGATE) && (inCode0 <= MAX_LOW_SURROGATE))
				{
					++index;
					inCode = PLANE1_MIN_VALUE + ((inCode - MIN_HIGH_SURROGATE) << 10 | (inCode0 - MIN_LOW_SURROGATE));
				}
				else
					inCode = REPLACEMENT_CHAR;
			}
			else
				inCode = REPLACEMENT_CHAR;
		}

		// Case: append 1-byte sequence
		if (inCode < MIN_VALUES[0])
			outStr.push_back(inCode & 0x7F);

		// Case: append 2-byte sequence
		else if (inCode < MIN_VALUES[1])
		{
			outStr.push_back(0xC0 | (inCode >> 6));
			outStr.push_back(0x80 | (inCode & 0x3F));
		}

		// Case: append 3-byte sequence
		else if (inCode < MIN_VALUES[2])
		{
			outStr.push_back(0xE0 | (inCode >> 12));
			outStr.push_back(0x80 | (inCode >> 6 & 0x3F));
			outStr.push_back(0x80 | (inCode      & 0x3F));
		}

		// Case: append 4-byte sequence
		else
		{
			outStr.push_back(0xF0 | (inCode >> 18));
			outStr.push_back(0x80 | (inCode >> 12 & 0x3F));
			outStr.push_back(0x80 | (inCode >> 6  & 0x3F));
			outStr.push_back(0x80 | (inCode       & 0x3F));
		}
	}

	// Return output string
	return outStr;
}

//----------------------------------------------------------------------

std::u32string StrConv::utf16ToUtf32(
	const std::u16string&	str)
{
	// Initialise output string
	std::u32string outStr;

	// Convert string
	int inLength = str.length();
	int index = 0;
	while (index < inLength)
	{
		// Get next input code
		UInt32 inCode = str[index++];

		// Replace code that is out of bounds
		if (inCode > MAX_VALUE)
			inCode = REPLACEMENT_CHAR;

		// Test for surrogate
		else if ((inCode >= MIN_HIGH_SURROGATE) && (inCode < MIN_LOW_SURROGATE))
		{
			if (index < inLength)
			{
				UInt32 inCode0 = str[index];
				if ((inCode0 >= MIN_LOW_SURROGATE) && (inCode0 <= MAX_LOW_SURROGATE))
				{
					++index;
					inCode = PLANE1_MIN_VALUE + ((inCode - MIN_HIGH_SURROGATE) << 10 | (inCode0 - MIN_LOW_SURROGATE));
				}
				else
					inCode = REPLACEMENT_CHAR;
			}
			else
				inCode = REPLACEMENT_CHAR;
		}

		// Append code to output string
		outStr.push_back(inCode);
	}

	// Return output string
	return outStr;
}

//----------------------------------------------------------------------

std::string StrConv::utf32ToUtf8(
	const std::u32string&	str)
{
	// Initialise output string
	std::string outStr;

	// Convert string
	int inLength = str.length();
	for (int i = 0; i < inLength; ++i)
	{
		// Get next input code
		UInt32 inCode = str[i];

		// Replace code that is out of bounds
		if (inCode > MAX_VALUE)
			inCode = REPLACEMENT_CHAR;

		// Case: append 1-byte sequence
		if (inCode < MIN_VALUES[0])
			outStr.push_back(inCode & 0x7F);

		// Case: append 2-byte sequence
		else if (inCode < MIN_VALUES[1])
		{
			outStr.push_back(0xC0 | (inCode >> 6));
			outStr.push_back(0x80 | (inCode & 0x3F));
		}

		// Case: append 3-byte sequence
		else if (inCode < MIN_VALUES[2])
		{
			outStr.push_back(0xE0 | (inCode >> 12));
			outStr.push_back(0x80 | (inCode >> 6 & 0x3F));
			outStr.push_back(0x80 | (inCode      & 0x3F));
		}

		// Case: append 4-byte sequence
		else
		{
			outStr.push_back(0xF0 | (inCode >> 18));
			outStr.push_back(0x80 | (inCode >> 12 & 0x3F));
			outStr.push_back(0x80 | (inCode >> 6  & 0x3F));
			outStr.push_back(0x80 | (inCode       & 0x3F));
		}
	}

	// Return output string
	return outStr;
}

//----------------------------------------------------------------------

std::u16string StrConv::utf32ToUtf16(
	const std::u32string&	str)
{
	// Initialise output string
	std::u16string outStr;

	// Convert string
	int inLength = str.length();
	for (int i = 0; i < inLength; ++i)
	{
		// Get next input code
		UInt32 inCode = str[i];

		// Replace code that is out of bounds
		if (inCode > MAX_VALUE)
			inCode = REPLACEMENT_CHAR;

		// Append code(s) to output string
		if (inCode < PLANE1_MIN_VALUE)
			outStr.push_back(inCode);
		else
		{
			UInt32 offset = inCode - PLANE1_MIN_VALUE;
			outStr.push_back(MIN_HIGH_SURROGATE + (offset >> 10));
			outStr.push_back(MIN_LOW_SURROGATE + (offset & 0x3FF));
		}
	}

	// Return output string
	return outStr;
}

//----------------------------------------------------------------------

std::wstring StrConv::utf32ToWstr(
	const std::u32string&	str)
{
	// Test for 32-bit wide characters
	static_assert(sizeof(wchar_t) == sizeof(char32_t));

	// Convert string
	return std::wstring(reinterpret_cast<const wchar_t*>(str.data()), str.length());
}

//----------------------------------------------------------------------

std::u32string StrConv::wstrToUtf32(
	const std::wstring&	str)
{
	// Test for 32-bit wide characters
	static_assert(sizeof(wchar_t) == sizeof(char32_t));

	// Convert string
	return std::u32string(reinterpret_cast<const char32_t*>(str.data()), str.length());
}

//----------------------------------------------------------------------

std::wstring StrConv::strToWstr(
	const std::string&	str)
{
	return utf32ToWstr(utf8ToUtf32(str));
}

//----------------------------------------------------------------------

std::string StrConv::wstrToStr(
	const std::wstring&	str)
{
	return utf32ToUtf8(wstrToUtf32(str));
}

//----------------------------------------------------------------------
