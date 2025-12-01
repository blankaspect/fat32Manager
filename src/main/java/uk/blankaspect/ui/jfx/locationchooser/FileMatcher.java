/*====================================================================*\

FileMatcher.java

Class: file-location matcher.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.locationchooser;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import java.util.function.Predicate;

//----------------------------------------------------------------------


// CLASS: FILE-LOCATION MATCHER


public class FileMatcher
	extends LocationMatcher
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	FileMatcher	ANY_FILE	= new FileMatcher("Any file", location -> true);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public FileMatcher(
		String			description,
		Predicate<Path>	matcher)
	{
		// Call superclass constructor
		super(description, matcher);
	}

	//------------------------------------------------------------------

	public FileMatcher(
		String				description,
		Iterable<String>	filenameSuffixes)
	{
		// Call superclass constructor
		super(description, filenameSuffixes);
	}

	//------------------------------------------------------------------

	public FileMatcher(
		String		description,
		String...	filenameSuffixes)
	{
		// Call superclass constructor
		super(description, filenameSuffixes);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public MatcherScope getScope()
	{
		return MatcherScope.FILES;
	}

	//------------------------------------------------------------------

	@Override
	public boolean matches(
		Path	location)
	{
		return (location != null) && Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS)
				&& super.matches(location);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
