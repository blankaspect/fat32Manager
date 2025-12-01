/*====================================================================*\

DirectoryMatcher.java

Class: directory-location matcher.

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


// CLASS: DIRECTORY-LOCATION MATCHER


public class DirectoryMatcher
	extends LocationMatcher
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public static final	DirectoryMatcher	ANY_DIRECTORY	= new DirectoryMatcher("Any directory", location -> true);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public DirectoryMatcher(
		String			description,
		Predicate<Path>	matcher)
	{
		// Call superclass constructor
		super(description, matcher);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	@Override
	public MatcherScope getScope()
	{
		return MatcherScope.DIRECTORIES;
	}

	//------------------------------------------------------------------

	@Override
	public boolean matches(
		Path	location)
	{
		return (location != null) && Files.isDirectory(location, LinkOption.NOFOLLOW_LINKS)
				&& super.matches(location);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
