/*====================================================================*\

Volume.java

Class: volume.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.driveio;

//----------------------------------------------------------------------


// CLASS: VOLUME


public class Volume
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public enum MediumKind
	{
		UNKNOWN,
		REMOVABLE,
		FIXED
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String			name;
	private	IVolumeAccessor	accessor;
	private	MediumKind		mediumKind;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public Volume(
		String			name,
		IVolumeAccessor	accessor)
	{
		// Initialise instance variables
		this.name = name;
		this.accessor = accessor;
		mediumKind = MediumKind.UNKNOWN;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getName()
	{
		return name;
	}

	//------------------------------------------------------------------

	public IVolumeAccessor getAccessor()
	{
		return accessor;
	}

	//------------------------------------------------------------------

	public MediumKind getMediumKind()
	{
		return mediumKind;
	}

	//------------------------------------------------------------------

	public void setMediumKind(
		MediumKind	mediumKind)
	{
		this.mediumKind = mediumKind;
	}

	//------------------------------------------------------------------

	public void getInfo(
		byte[]	buffer)
		throws VolumeException
	{
		// Validate argument
		if (buffer == null)
			throw new IllegalArgumentException("Null buffer");

		// Get volume info
		accessor.getVolumeInfo(name, buffer);
	}

	//------------------------------------------------------------------

	public synchronized boolean isOpen()
	{
		return accessor.isVolumeOpen();
	}

	//------------------------------------------------------------------

	public void open(
		Access	access)
		throws VolumeException
	{
		final	int		NUM_ATTEMPTS	= 10;
		final	long	INTERVAL		= 200;

		// Initialise exception
		VolumeException exception = null;

		// Make multiple attempts to open volume
		for (int i = 0; i < NUM_ATTEMPTS; i++)
		{
			// Wait until time for next attempt
			if (i > 0)
			{
				try
				{
					Thread.sleep(INTERVAL);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			// Open volume
			try
			{
				accessor.openVolume(name, access.flags);
				return;
			}
			catch (VolumeException e)
			{
				exception = e;
			}

			// Close volume
			accessor.closeVolume();
		}

		// Throw exception
		throw exception;
	}

	//------------------------------------------------------------------

	public void close()
		throws VolumeException
	{
		accessor.closeVolume();
	}

	//------------------------------------------------------------------

	public void seek(
		long	position)
		throws VolumeException
	{
		// Validate argument
		if (position < 0)
			throw new IllegalArgumentException("Invalid position");

		// Seek position
		accessor.seekVolume(position);
	}

	//------------------------------------------------------------------

	public void read(
		byte[]	buffer)
		throws VolumeException
	{
		read(buffer, 0, buffer.length);
	}

	//------------------------------------------------------------------

	public void read(
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

		// Read volume
		accessor.readVolume(buffer, offset, length);
	}

	//------------------------------------------------------------------

	public void write(
		byte[]	data)
		throws VolumeException
	{
		write(data, 0, data.length);
	}

	//------------------------------------------------------------------

	public void write(
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

		// Write volume
		accessor.writeVolume(data, offset, length);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: VOLUME ACCESS


	public enum Access
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		READ
		(
			DriveIO.ACCESS_READ
		),

		WRITE
		(
			DriveIO.ACCESS_WRITE
		),

		READ_WRITE
		(
			DriveIO.ACCESS_READ_WRITE
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	int	flags;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private Access(
			int	flags)
		{
			this.flags = flags;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		public int getFlags()
		{
			return flags;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
