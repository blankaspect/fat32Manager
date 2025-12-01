/*====================================================================*\

LocationMatcher.java

Class: file-system location matcher.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.locationchooser;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.function.Predicate;
import java.util.function.Supplier;

//----------------------------------------------------------------------


// CLASS: FILE-SYSTEM LOCATION MATCHER


public class LocationMatcher
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	LocationMatcher	ANY_FILE_OR_DIRECTORY	=
			new LocationMatcher("Any file or directory", location -> true);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	String			description;
	private	List<String>	filenameSuffixes;
	private	Predicate<Path>	matcher;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public LocationMatcher(
		String			description,
		Predicate<Path>	matcher)
	{
		// Validate arguments
		if (description == null)
			throw new IllegalArgumentException("Null description");
		if (matcher == null)
			throw new IllegalArgumentException("Null matcher");

		// Initialise instance variables
		this.description = description;
		this.matcher = matcher;
	}

	//------------------------------------------------------------------

	public LocationMatcher(
		String				description,
		Iterable<String>	filenameSuffixes)
	{
		// Validate arguments
		if (description == null)
			throw new IllegalArgumentException("Null description");
		if (filenameSuffixes == null)
			throw new IllegalArgumentException("Null filename suffixes");
		int numSuffixes = 0;
		for (String suffix : filenameSuffixes)
		{
			if (suffix.isBlank())
				throw new IllegalArgumentException("A filename suffix is invalid");
			++numSuffixes;
		}
		if (numSuffixes == 0)
			throw new IllegalArgumentException("No filename suffixes");

		// Initialise instance variables
		this.description = description;
		this.filenameSuffixes = new ArrayList<>();
		for (String suffix : filenameSuffixes)
			this.filenameSuffixes.add(suffix);
		matcher = location ->
		{
			String filename = location.getFileName().toString();
			return this.filenameSuffixes.stream().anyMatch(suffix -> filename.endsWith(suffix));
		};
	}

	//------------------------------------------------------------------

	public LocationMatcher(
		String		description,
		String...	filenameSuffixes)
	{
		// Call alternative constructor
		this(description, List.of(filenameSuffixes));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Predicate<Path> suffixMatcher(
		Supplier<String>	suffixSource)
	{
		return location -> location.getFileName().toString().endsWith(suffixSource.get());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public MatcherScope getScope()
	{
		return MatcherScope.FILES_AND_DIRECTORIES;
	}

	//------------------------------------------------------------------

	public String getDescription()
	{
		return description;
	}

	//------------------------------------------------------------------

	public boolean hasFilenameSuffixes()
	{
		return (getNumFilenameSuffixes() > 0);
	}

	//------------------------------------------------------------------

	public int getNumFilenameSuffixes()
	{
		return (filenameSuffixes == null) ? 0 : filenameSuffixes.size();
	}

	//------------------------------------------------------------------

	public List<String> getFilenameSuffixes()
	{
		return (getNumFilenameSuffixes() > 0) ? Collections.unmodifiableList(filenameSuffixes) : List.of();
	}

	//------------------------------------------------------------------

	public Predicate<Path> getMatcher()
	{
		return matcher;
	}

	//------------------------------------------------------------------

	public boolean matches(
		Path	location)
	{
		return matcher.test(location);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
