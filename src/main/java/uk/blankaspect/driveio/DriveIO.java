/*====================================================================*\

DriveIO.java

Class: drive I/O native methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.driveio;

//----------------------------------------------------------------------


// IMPORTS


import java.io.IOException;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;

import uk.blankaspect.common.exception2.LocationException;

import uk.blankaspect.common.logging.Logger;

import uk.blankaspect.common.resource.ResourceUtils;

//----------------------------------------------------------------------


// CLASS: DRIVE I/O NATIVE METHODS


public class DriveIO
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final		String	NAME	= "driveio";

	public static final		DriveIO.Accessor	ACCESSOR	= new DriveIO.Accessor();

	public static final		int		ACCESS_READ			= 1 << 0;
	public static final		int		ACCESS_WRITE		= 1 << 1;
	public static final		int		ACCESS_READ_WRITE	= ACCESS_READ | ACCESS_WRITE;

	public static final		int		MEDIUM_UNKNOWN		= 0;
	public static final		int		MEDIUM_REMOVABLE	= 1;
	public static final		int		MEDIUM_FIXED		= 2;

	private static final	int		RESULT_SUCCESS	= 0;
	private static final	int		RESULT_FAILURE	= 1;
	private static final	int		RESULT_ERROR	= 2;

	private static final	int		LIBRARY_ID	= 0xF19D3742;

	private static final	String	LOADING_LIBRARY_STR	= "Loading library from ";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	boolean	loaded;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		String message = "Volume.MediumKind constant";
		if (MEDIUM_UNKNOWN != Volume.MediumKind.UNKNOWN.ordinal())
			throw new AssertionError(message);
		if (MEDIUM_REMOVABLE != Volume.MediumKind.REMOVABLE.ordinal())
			throw new AssertionError(message);
		if (MEDIUM_FIXED != Volume.MediumKind.FIXED.ordinal())
			throw new AssertionError(message);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private DriveIO()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static boolean initLibrary(
		Path	libDirectory)
		throws LocationException, UnsatisfiedLinkError
	{
		if (!loaded)
		{
			// Get name of library
			String libName = System.mapLibraryName(NAME);

			// Get expected location of library
			Path libLocation = libDirectory.resolve(libName);

			// Log location of library
			Logger.INSTANCE.info(LOADING_LIBRARY_STR + libLocation.toString());

			// Load library
			System.load(libLocation.toString());

			// Indicate library is loaded
			loaded = true;
		}

		// Test ID of library and return result
		return (init() == LIBRARY_ID);
	}

	//------------------------------------------------------------------

	public static boolean initLibrary(
		String	sourceDirPathname,
		Path	libDirectory)
		throws LocationException, UnsatisfiedLinkError
	{
		if (!loaded)
		{
			// Get name of library
			String libName = System.mapLibraryName(NAME);

			// Get pathname of library resource
			String resourcePathname = (sourceDirPathname == null)
												? libName
												: sourceDirPathname.endsWith("/")
														? sourceDirPathname + libName
														: sourceDirPathname + '/' + libName;

			// Get expected location of library
			Path libLocation = libDirectory.resolve(libName);

			// Test whether library exists at expected location and, if so, whether its timestamp is the same as that
			// of the library resource
			boolean copy = true;
			if (Files.exists(libLocation, LinkOption.NOFOLLOW_LINKS))
			{
				URL url = ClassLoader.getSystemClassLoader().getResource(resourcePathname);
				if (url != null)
				{
					try
					{
						long timestamp = url.openConnection().getLastModified();
						copy = (timestamp == 0)
									|| (timestamp != Files.getLastModifiedTime(libLocation,
																			   LinkOption.NOFOLLOW_LINKS).toMillis());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}

			// If library does not exist at expected location, or if its timestamp is different from that of the library
			// resource, copy resource to file system
			if (copy)
				ResourceUtils.copyResource(null, resourcePathname, libLocation);

			// Log location of library
			Logger.INSTANCE.info(LOADING_LIBRARY_STR + libLocation.toString());

			// Load library
			System.load(libLocation.toString());

			// Indicate library is loaded
			loaded = true;
		}

		// Test ID of library and return result
		return (init() == LIBRARY_ID);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods : native
////////////////////////////////////////////////////////////////////////

	private static native int init();

	//------------------------------------------------------------------

	private static native int getVolumeNames(
		char[]	buffer);

	//------------------------------------------------------------------

	private static native int getVolumeInfo(
		char[]	volumeName,
		byte[]	buffer);

	//------------------------------------------------------------------

	private static native boolean isVolumeMounted(
		char[]	volumeName);

	//------------------------------------------------------------------

	private static native int getVolumeMountPoint(
		char[]	volumeName,
		char[]	buffer);

	//------------------------------------------------------------------

	private static native boolean isVolumeOpen();

	//------------------------------------------------------------------

	private static native int openVolume(
		char[]	volumeName,
		int		accessFlags);

	//------------------------------------------------------------------

	private static native int closeVolume();

	//------------------------------------------------------------------

	private static native int seekVolume(
		long	position);

	//------------------------------------------------------------------

	private static native int readVolume(
		byte[]	buffer,
		int		offset,
		int		length);

	//------------------------------------------------------------------

	private static native int writeVolume(
		byte[]	data,
		int		offset,
		int		length);

	//------------------------------------------------------------------

	private static native int getErrorMessage(
		char[]	buffer);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: VOLUME-ACCESS METHODS


	public static class Accessor
		implements IVolumeAccessor
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	int		VOLUME_NAME_BUFFER_LENGTH	= 1024;

		private static final	int		MAX_MOUNT_POINT_LENGTH	= 1024;

		private static final	int		MAX_ERROR_STR_LENGTH	= 1024;

		private static final	String	UNEXPECTED_LIBRARY_ID_STR	= "Unexpected library ID: 0x%X";

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Accessor()
		{
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods : IVolumeAccessor interface
	////////////////////////////////////////////////////////////////////

		@Override
		public void init()
			throws VolumeException
		{
			int id = DriveIO.init();
			if (id != LIBRARY_ID)
				throw new VolumeException(String.format(UNEXPECTED_LIBRARY_ID_STR, id));
		}

		//--------------------------------------------------------------

		@Override
		public List<String> getVolumeNames()
			throws VolumeException
		{
			// Get names of volumes
			char[] buffer = new char[VOLUME_NAME_BUFFER_LENGTH];
			int numVolumes = DriveIO.getVolumeNames(buffer);
			if (numVolumes == 0)
				throw new VolumeException(getErrorMessage());

			// Convert volume names to array of strings
			List<String> volumeNames = new ArrayList<>();
			int index = 0;
			for (int i = 0; i < numVolumes; i++)
			{
				int startIndex = index;
				while (true)
				{
					char ch = buffer[index++];
					if (ch == '\0')
						break;
				}
				volumeNames.add(new String(buffer, startIndex, index - startIndex - 1));
			}

			// Return volume names
			return volumeNames;
		}

		//--------------------------------------------------------------

		@Override
		public void getVolumeInfo(
			String	volumeName,
			byte[]	buffer)
			throws VolumeException
		{
			if (DriveIO.getVolumeInfo(volumeName.toCharArray(), buffer) != RESULT_SUCCESS)
				throw new VolumeException(getErrorMessage());
		}

		//--------------------------------------------------------------

		@Override
		public boolean isVolumeMounted(
			String	volumeName)
		{
			return DriveIO.isVolumeMounted(volumeName.toCharArray());
		}

		//--------------------------------------------------------------

		@Override
		public String getVolumeMountPoint(
			String	volumeName)
		{
			char[] buffer = new char[MAX_MOUNT_POINT_LENGTH];
			int length = DriveIO.getVolumeMountPoint(volumeName.toCharArray(), buffer);
			return new String(buffer, 0, length);
		}

		//--------------------------------------------------------------

		@Override
		public boolean isVolumeOpen()
		{
			return DriveIO.isVolumeOpen();
		}

		//--------------------------------------------------------------

		@Override
		public void openVolume(
			String	volumeName,
			int		accessFlags)
			throws VolumeException
		{
			if (DriveIO.openVolume(volumeName.toCharArray(), accessFlags) != RESULT_SUCCESS)
				throw new VolumeException(getErrorMessage());
		}

		//--------------------------------------------------------------

		@Override
		public void closeVolume()
			throws VolumeException
		{
			if (DriveIO.closeVolume() != RESULT_SUCCESS)
				throw new VolumeException(getErrorMessage());
		}

		//--------------------------------------------------------------

		@Override
		public void seekVolume(
			long	position)
			throws VolumeException
		{
			if (DriveIO.seekVolume(position) != RESULT_SUCCESS)
				throw new VolumeException(getErrorMessage());
		}

		//--------------------------------------------------------------

		@Override
		public void readVolume(
			byte[]	buffer,
			int		offset,
			int		length)
			throws VolumeException
		{
			// Validate arguments
			if (buffer == null)
				throw new IllegalArgumentException("Null buffer");
			if ((offset < 0) || (offset > buffer.length))
				throw new IllegalArgumentException("Offset out of bounds: " + offset);
			if ((length < 0) || (length > buffer.length - offset))
				throw new IllegalArgumentException("Length out of bounds: " + length);

			// Read from volume
			if (DriveIO.readVolume(buffer, offset, length) != RESULT_SUCCESS)
				throw new VolumeException(getErrorMessage());
		}

		//--------------------------------------------------------------

		@Override
		public void writeVolume(
			byte[]	data,
			int		offset,
			int		length)
			throws VolumeException
		{
			// Validate arguments
			if (data == null)
				throw new IllegalArgumentException("Null data");
			if ((offset < 0) || (offset > data.length))
				throw new IllegalArgumentException("Offset out of bounds: " + offset);
			if ((length < 0) || (length > data.length - offset))
				throw new IllegalArgumentException("Length out of bounds: " + length);

			// Write to volume
			if (DriveIO.writeVolume(data, offset, length) != RESULT_SUCCESS)
				throw new VolumeException(getErrorMessage());
		}

		//--------------------------------------------------------------

		@Override
		public String getErrorMessage()
		{
			char[] buffer = new char[MAX_ERROR_STR_LENGTH];
			int length = DriveIO.getErrorMessage(buffer);
			return new String(buffer, 0, length);
		}

		//--------------------------------------------------------------

	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
