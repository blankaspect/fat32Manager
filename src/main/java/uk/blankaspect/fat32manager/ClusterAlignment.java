/*====================================================================*\

ClusterAlignment.java

Enumeration: cluster alignment.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Arrays;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// ENUMERATION: CLUSTER ALIGNMENT


public enum ClusterAlignment
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	NONE
	(
		"None"
	),

	INTEGRAL_MULTIPLE
	(
		"Sectors per cluster multiplied by an integer"
	),

	POWER_OF_TWO_MULTIPLE
	(
		"Sectors per cluster multiplied by a power of 2"
	);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String	text;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private ClusterAlignment(
		String	text)
	{
		// Initialise instance variables
		this.text = text;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static ClusterAlignment forKey(
		String	key)
	{
		return Arrays.stream(values()).filter(value -> value.getKey().equals(key)).findFirst().orElse(null);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public String toString()
	{
		return text;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public String getKey()
	{
		return StringUtils.toCamelCase(name());
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
