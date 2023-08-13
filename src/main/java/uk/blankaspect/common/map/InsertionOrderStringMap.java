/*====================================================================*\

InsertionOrderStringMap.java

Class: insertion-order map whose keys and values are strings.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.map;

//----------------------------------------------------------------------


// IMPORTS


import java.text.DecimalFormat;

import java.util.LinkedHashMap;

//----------------------------------------------------------------------


// CLASS: INSERTION-ORDER MAP WHOSE KEYS AND VALUES ARE STRINGS


public class InsertionOrderStringMap
	extends LinkedHashMap<String, String>
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	DecimalFormat	integerFormatter;
	private	DecimalFormat	floatingPointFormatter;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public InsertionOrderStringMap()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static InsertionOrderStringMap create()
	{
		return new InsertionOrderStringMap();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public InsertionOrderStringMap add(
		String	key,
		boolean	value)
	{
		// Add value to map
		put(key, Boolean.toString(value));

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

	public InsertionOrderStringMap add(
		String	key,
		char	value)
	{
		// Add value to map
		put(key, Character.toString(value));

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

	public InsertionOrderStringMap add(
		String	key,
		int		value)
	{
		// Add value to map
		put(key, (integerFormatter == null) ? Integer.toString(value) : integerFormatter.format(value));

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

	public InsertionOrderStringMap add(
		String	key,
		long	value)
	{
		// Add value to map
		put(key, (integerFormatter == null) ? Long.toString(value) : integerFormatter.format(value));

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

	public InsertionOrderStringMap add(
		String	key,
		float	value)
	{
		// Add value to map
		put(key, (floatingPointFormatter == null) ? Float.toString(value) : floatingPointFormatter.format(value));

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

	public InsertionOrderStringMap add(
		String	key,
		double	value)
	{
		// Add value to map
		put(key, (floatingPointFormatter == null) ? Double.toString(value) : floatingPointFormatter.format(value));

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

	public InsertionOrderStringMap add(
		String	key,
		String	value)
	{
		// Add value to map
		put(key, value);

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

	public InsertionOrderStringMap add(
		String	key,
		Object	value)
	{
		// Validate argument
		if (value == null)
			throw new IllegalArgumentException("Null value");

		// Add value to map
		put(key, value.toString());

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

	public InsertionOrderStringMap integerFormatter(
		DecimalFormat	formatter)
	{
		// Update instance variable
		integerFormatter = formatter;

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

	public InsertionOrderStringMap floatingPointFormatter(
		DecimalFormat	formatter)
	{
		// Update instance variable
		floatingPointFormatter = formatter;

		// Return this map
		return this;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
