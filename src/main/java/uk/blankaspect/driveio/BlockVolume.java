/*====================================================================*\

BlockVolume.java

Class: block volume.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.driveio;

//----------------------------------------------------------------------


// IMPORTS


import uk.blankaspect.common.number.NumberCodec;

//----------------------------------------------------------------------


// CLASS: BLOCK VOLUME


public class BlockVolume
	extends Volume
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	int		bytesPerSector;
	private	long	startSector;
	private	long	numSectors;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public BlockVolume(
		String			name,
		IVolumeAccessor	accessor)
	{
		// Call superclass constructor
		super(name, accessor);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public int getBytesPerSector()
	{
		return bytesPerSector;
	}

	//------------------------------------------------------------------

	public long getStartSector()
	{
		return startSector;
	}

	//------------------------------------------------------------------

	public long getNumSectors()
	{
		return numSectors;
	}

	//------------------------------------------------------------------

	public void updateInfo()
		throws VolumeException
	{
		// Get volume information
		byte[] buffer = new byte[2 * Long.BYTES + Integer.BYTES + Short.BYTES];
		getInfo(buffer);

		// Update instance variables
		int offset = 0;

		int length = Integer.BYTES;
		bytesPerSector = NumberCodec.bytesToUIntLE(buffer, offset, length);
		offset += length;

		length = Long.BYTES;
		startSector = NumberCodec.bytesToULongLE(buffer, offset, length);
		offset += length;

		length = Long.BYTES;
		numSectors = NumberCodec.bytesToULongLE(buffer, offset, length);
		offset += length;

		length = Short.BYTES;
		int index = NumberCodec.bytesToUIntLE(buffer, offset, length);
		setMediumKind(((index >= 0) && (index < MediumKind.values().length)) ? MediumKind.values()[index]
																			 : MediumKind.UNKNOWN);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
