/*====================================================================*\

IVolumeAccessor.java

Interface: methods for accessing a volume.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.driveio;

//----------------------------------------------------------------------


// IMPORTS


import java.util.List;

//----------------------------------------------------------------------


// INTERFACE: METHODS FOR ACCESSING A VOLUME


public interface IVolumeAccessor
{

////////////////////////////////////////////////////////////////////////
//  Methods
////////////////////////////////////////////////////////////////////////

	void init()
		throws VolumeException;

	//------------------------------------------------------------------

	List<String> getVolumeNames()
		throws VolumeException;

	//------------------------------------------------------------------

	void getVolumeInfo(
		String	volumeName,
		byte[]	buffer)
		throws VolumeException;

	//------------------------------------------------------------------

	boolean isVolumeMounted(
		String	volumeName);

	//------------------------------------------------------------------

	String getVolumeMountPoint(
		String	volumeName);

	//------------------------------------------------------------------

	boolean isVolumeOpen();

	//------------------------------------------------------------------

	void openVolume(
		String	volumeName,
		int		accessFlags)
		throws VolumeException;

	//------------------------------------------------------------------

	void closeVolume()
		throws VolumeException;

	//------------------------------------------------------------------

	void seekVolume(
		long	position)
		throws VolumeException;

	//------------------------------------------------------------------

	void readVolume(
		byte[]	buffer,
		int		offset,
		int		length)
		throws VolumeException;

	//------------------------------------------------------------------

	void writeVolume(
		byte[]	data,
		int		offset,
		int		length)
		throws VolumeException;

	//------------------------------------------------------------------

	String getErrorMessage();

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
