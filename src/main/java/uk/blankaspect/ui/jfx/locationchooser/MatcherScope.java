/*====================================================================*\

MatcherScope.java

Enumeration: the scope of a file-system location matcher.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.locationchooser;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import uk.blankaspect.common.string.StringUtils;

//----------------------------------------------------------------------


// ENUMERATION: THE SCOPE OF A FILE-SYSTEM LOCATION MATCHER


public enum MatcherScope
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	FILES
	(
		FileMatcher.ALL_FILES
	),

	DIRECTORIES
	(
		DirectoryMatcher.ALL_DIRECTORIES
	),

	FILES_AND_DIRECTORIES
	(
		LocationMatcher.ALL_FILES_AND_DIRECTORIES
	);

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	LocationMatcher	unconditionalMatcher;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private MatcherScope(
		LocationMatcher	unconditionalMatcher)
	{
		// Initialise instance variables
		this.unconditionalMatcher = unconditionalMatcher;
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

	public LocationMatcher getUnconditionalMatcher()
	{
		return unconditionalMatcher;
	}

	//------------------------------------------------------------------

	public boolean matchesFiles()
	{
		return (this == FILES) || (this == FILES_AND_DIRECTORIES);
	}

	//------------------------------------------------------------------

	public boolean matchesDirectories()
	{
		return (this == DIRECTORIES) || (this == FILES_AND_DIRECTORIES);
	}

	//------------------------------------------------------------------

	public boolean matches(
		Path	location)
	{
		return (matchesFiles() && Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS))
				|| (matchesDirectories() && Files.isDirectory(location, LinkOption.NOFOLLOW_LINKS));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
