/*====================================================================*\

uk_blankaspect_driveio_DriveIO.cc

Java native interface routines: drive I/O

\*====================================================================*/


// INCLUDES


#ifndef _DRIVEIO_UTILS_H_
	#include "Utils.h"
#endif
#ifndef _DRIVEIO_VOLUME_H_
	#include "Volume.h"
#endif

#ifndef _Included_uk_blankaspect_driveio_DriveIO
	#include "uk_blankaspect_driveio_DriveIO.h"
#endif

//----------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

// Error messages
namespace ErrorMsg
{
	const std::wstring	NO_VOLUME_IS_OPEN
						(L"No volume is open.");
}

////////////////////////////////////////////////////////////////////////
//  Internal variables
////////////////////////////////////////////////////////////////////////

static	Volume*			volumePtr;
static	std::wstring	errorStr;

////////////////////////////////////////////////////////////////////////
//
//	JAVA NATIVE INTERFACE ROUTINES : DRIVE I/O
//
////////////////////////////////////////////////////////////////////////

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_init(
	JNIEnv*	envPtr,
	jclass	cls)
{
	return uk_blankaspect_driveio_DriveIO_LIBRARY_ID;
}

//----------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_getVolumeNames(
	JNIEnv*		envPtr,
	jclass		cls,
	jcharArray	buffer)
{
	// Initialise the number of volumes
	int numVolumes = 0;

	// Get the names of volumes in the buffer
	try
	{
		// Get a bit array of available volumes
		UInt32 volumes = Volume::getVolumes();

		// Get the length of the name buffer
		jsize bufferLength = envPtr->GetArrayLength(buffer);

		// Get a pointer to the name buffer
		jchar* bufferPtr = envPtr->GetCharArrayElements(buffer, NULL);

		// Add the names of volumes to the buffer
		int index = 0;
		int offset = 0;
		while (volumes != 0)
		{
			// If the volume is available ...
			if (volumes & 1)
			{
				// Get the name of the volume
				std::wstring name = Volume::volumeIndexToString(index);

				// Get the offset to the end of the volume name if it were added to the buffer
				int endOffset = offset + name.length() + 1;

				// If the name would extend beyond the end of the buffer, stop
				if (endOffset > bufferLength)
					break;

				// Copy the volume name to the buffer
				::wcscpy(reinterpret_cast<wchar_t*>(&bufferPtr[offset]), name.data());

				// Increment the offset to the buffer
				offset = endOffset;

				// Increment the number of volumes
				++numVolumes;
			}

			// Increment the volume index
			++index;

			// Shift the bit array of volumes
			volumes >>= 1;
		}

		// Release the name buffer
		envPtr->ReleaseCharArrayElements(buffer, bufferPtr, 0);
	}
	catch (const Exception& e)
	{
		errorStr = e.toString();
	}

	// Return the number of volumes
	return numVolumes;
}

//----------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_getVolumeInfo(
	JNIEnv*		envPtr,
	jclass		cls,
	jcharArray	volumeName,
	jbyteArray	buffer)
{
	// Get the name of the volume
	jsize nameLength = envPtr->GetArrayLength(volumeName);
	jchar* namePtr = envPtr->GetCharArrayElements(volumeName, NULL);
	std::wstring volumeName0 = std::wstring(reinterpret_cast<wchar_t*>(namePtr), nameLength);
	envPtr->ReleaseCharArrayElements(volumeName, namePtr, 0);

	// Get volume information
	jsize bufferLength = envPtr->GetArrayLength(buffer);
	jbyte* bufferPtr = envPtr->GetByteArrayElements(buffer, NULL);
	try
	{
		Volume::getInfo(volumeName0, bufferPtr, bufferLength);
		envPtr->ReleaseByteArrayElements(buffer, bufferPtr, 0);
		return uk_blankaspect_driveio_DriveIO_RESULT_SUCCESS;
	}
	catch (const Exception& e)
	{
		envPtr->ReleaseByteArrayElements(buffer, bufferPtr, JNI_ABORT);
		errorStr = e.toString();
		return uk_blankaspect_driveio_DriveIO_RESULT_ERROR;
	}
}

//----------------------------------------------------------------------

JNIEXPORT jboolean JNICALL Java_uk_blankaspect_driveio_DriveIO_isVolumeMounted(
	JNIEnv*		envPtr,
	jclass		cls,
	jcharArray	volumeName)
{
	// Get the name of the volume
	jsize nameLength = envPtr->GetArrayLength(volumeName);
	jchar* namePtr = envPtr->GetCharArrayElements(volumeName, NULL);
	std::wstring volumeName0 = std::wstring(reinterpret_cast<wchar_t*>(namePtr), nameLength);
	envPtr->ReleaseCharArrayElements(volumeName, namePtr, 0);

	// Test whether the volume is mounted
	return Volume::isVolumeMounted(volumeName0);
}

//----------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_getVolumeMountPoint(
	JNIEnv*		envPtr,
	jclass		cls,
	jcharArray	volumeName,
	jcharArray	buffer)
{
	// Get the name of the volume
	jsize nameLength = envPtr->GetArrayLength(volumeName);
	jchar* namePtr = envPtr->GetCharArrayElements(volumeName, NULL);
	std::wstring volumeName0 = std::wstring(reinterpret_cast<wchar_t*>(namePtr), nameLength);
	envPtr->ReleaseCharArrayElements(volumeName, namePtr, 0);

	// Get the length of the buffer for the mount point
	jsize bufferLength = envPtr->GetArrayLength(buffer);

	// Get a pointer to the buffer for the mount point
	jchar* bufferPtr = envPtr->GetCharArrayElements(buffer, NULL);

	// Get the length of the string that will be copied to the buffer
	int length = Volume::isVolumeMounted(volumeName0) ? volumeName0.length() : 0;
	if (length > bufferLength)
		length = bufferLength;

	// If the volume is mounted, copy its name to the buffer
	if (length > 0)
		::wcsncpy(reinterpret_cast<wchar_t*>(bufferPtr), volumeName0.data(), length);

	// Release the buffer
	envPtr->ReleaseCharArrayElements(buffer, bufferPtr, 0);

	// Return the the length of the mount point
	return length;
}

//----------------------------------------------------------------------

JNIEXPORT jboolean JNICALL Java_uk_blankaspect_driveio_DriveIO_isVolumeOpen(
	JNIEnv*	envPtr,
	jclass	cls)
{
	return volumePtr && volumePtr->isOpen();
}

//----------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_openVolume(
	JNIEnv*		envPtr,
	jclass		cls,
	jcharArray	volumeName,
	jint		accessMode)
{
	// Get the name of the volume
	jsize nameLength = envPtr->GetArrayLength(volumeName);
	jchar* namePtr = envPtr->GetCharArrayElements(volumeName, NULL);
	std::wstring volumeName0 = std::wstring(reinterpret_cast<wchar_t*>(namePtr), nameLength);
	envPtr->ReleaseCharArrayElements(volumeName, namePtr, 0);

	// Close the current volume
	delete volumePtr;

	// Instantiate a new volume
	volumePtr = new Volume(volumeName0);

	// Open the volume
	try
	{
		volumePtr->open(accessMode);
		return uk_blankaspect_driveio_DriveIO_RESULT_SUCCESS;
	}
	catch (const Exception& e)
	{
		errorStr = e.toString();
		return uk_blankaspect_driveio_DriveIO_RESULT_ERROR;
	}
}

//----------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_closeVolume(
	JNIEnv*	envPtr,
	jclass	cls )
{
	try
	{
		// Test whether a volume is open
		if (!volumePtr)
			throw Exception(ErrorMsg::NO_VOLUME_IS_OPEN);

		// Invalidate the volume pointer
		Volume* volumePtr0 = volumePtr;
		volumePtr = 0;

		// Close the volume
		volumePtr0->close();
		return uk_blankaspect_driveio_DriveIO_RESULT_SUCCESS;
	}
	catch (const Exception& e)
	{
		errorStr = e.toString();
		return uk_blankaspect_driveio_DriveIO_RESULT_ERROR;
	}
}

//----------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_seekVolume(
	JNIEnv*	envPtr,
	jclass	cls,
	jlong	position )
{
	try
	{
		// Test whether a volume is open
		if (!volumePtr)
			throw Exception(ErrorMsg::NO_VOLUME_IS_OPEN);

		// Seek the position in the volume
		volumePtr->seek(position);
		return uk_blankaspect_driveio_DriveIO_RESULT_SUCCESS;
	}
	catch (const Exception& e)
	{
		errorStr = e.toString();
		return uk_blankaspect_driveio_DriveIO_RESULT_ERROR;
	}
}

//----------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_readVolume(
	JNIEnv*		envPtr,
	jclass		cls,
	jbyteArray	buffer,
	jint		offset,
	jint		length)
{
	jbyte* bufferPtr = envPtr->GetByteArrayElements(buffer, NULL);
	try
	{
		// Test whether a volume is open
		if (!volumePtr)
			throw Exception(ErrorMsg::NO_VOLUME_IS_OPEN);

		// Read from the volume
		volumePtr->read(bufferPtr + offset, length);
		envPtr->ReleaseByteArrayElements(buffer, bufferPtr, 0);
		return uk_blankaspect_driveio_DriveIO_RESULT_SUCCESS;
	}
	catch (const Exception& e)
	{
		envPtr->ReleaseByteArrayElements(buffer, bufferPtr, JNI_ABORT);
		errorStr = e.toString();
		return uk_blankaspect_driveio_DriveIO_RESULT_ERROR;
	}
}

//----------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_writeVolume(
	JNIEnv*		envPtr,
	jclass		cls,
	jbyteArray	data,
	jint		offset,
	jint		length)
{
	jbyte* dataPtr = envPtr->GetByteArrayElements(data, NULL);
	try
	{
		// Test whether a volume is open
		if (!volumePtr)
			throw Exception(ErrorMsg::NO_VOLUME_IS_OPEN);

		// Write to the volume
		volumePtr->write(dataPtr + offset, length);
		envPtr->ReleaseByteArrayElements(data, dataPtr, 0);
		return uk_blankaspect_driveio_DriveIO_RESULT_SUCCESS;
	}
	catch (const Exception& e)
	{
		envPtr->ReleaseByteArrayElements(data, dataPtr, JNI_ABORT);
		errorStr = e.toString();
		return uk_blankaspect_driveio_DriveIO_RESULT_ERROR;
	}
}

//----------------------------------------------------------------------

JNIEXPORT jint JNICALL Java_uk_blankaspect_driveio_DriveIO_getErrorMessage(
	JNIEnv*		envPtr,
	jclass		cls,
	jcharArray	buffer)
{
	// Get the length of the buffer for the error message
	jsize bufferLength = envPtr->GetArrayLength(buffer);

	// Get a pointer to the buffer for the error message
	jchar* bufferPtr = envPtr->GetCharArrayElements(buffer, NULL);

	// Get the length of the string that will be copied to the buffer
	int length = errorStr.length();
	if (length > bufferLength)
		length = bufferLength;

	// Copy the error message to the buffer
	::wcsncpy(reinterpret_cast<wchar_t*>(bufferPtr), errorStr.data(), length);

	// Release the buffer
	envPtr->ReleaseCharArrayElements(buffer, bufferPtr, 0);

	// Return the the length of the error message
	return length;
}

//----------------------------------------------------------------------
