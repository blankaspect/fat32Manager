/*====================================================================*\

PartitionInfo.h

Class: partition information.

\*====================================================================*/

#ifndef _DRIVEIO_PARTITION_INFO_H_
#define _DRIVEIO_PARTITION_INFO_H_

//----------------------------------------------------------------------


// INCLUDES


#ifndef _DRIVEIO_TYPES_H_
	#include "types.h"
#endif

//----------------------------------------------------------------------


// CLASS: PARTITION INFORMATION


class PartitionInfo
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

private:

	std::string	_name;
	int			_id;
	long		_startSector;
	long		_numSectors;
	bool		_readOnly;
	int			_alignmentOffset;
	int			_mediumKind;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

public:

	PartitionInfo(
		const std::string&	name);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

public:

	static bool isPartition(
		const std::string&	name);

	//------------------------------------------------------------------

	static void appendPartitionNames(
		StringVector&	names);

	//------------------------------------------------------------------

	static int getBytesPerSector(
		const std::string&	name);

	//------------------------------------------------------------------

	static PartitionInfo createInfo(
		const std::string&	name);

	//------------------------------------------------------------------

	static std::string findMountPoint(
		const std::string&	name);

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

public:

	const std::string& getName() const
	{
		return _name;
	}

	//------------------------------------------------------------------

	int getId() const
	{
		return _id;
	}

	//------------------------------------------------------------------

	long getNumSectors() const
	{
		return _numSectors;
	}

	//------------------------------------------------------------------

	long getStartSector() const
	{
		return _startSector;
	}

	//------------------------------------------------------------------

	bool isReadOnly() const
	{
		return _readOnly;
	}

	//------------------------------------------------------------------

	int getAlignmentOffset() const
	{
		return _alignmentOffset;
	}

	//------------------------------------------------------------------

	int getMediumKind() const
	{
		return _mediumKind;
	}

	//------------------------------------------------------------------

};

//----------------------------------------------------------------------

#endif	// _DRIVEIO_PARTITION_INFO_H_

//----------------------------------------------------------------------
