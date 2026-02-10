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

import java.util.Iterator;
import java.util.List;

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
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static FileMatcher from(
		String				description,
		Iterable<String>	filenameSuffixes)
	{
		StringBuilder buffer = new StringBuilder(64);
		buffer.append(description);
		Iterator<String> it = filenameSuffixes.iterator();
		if (it.hasNext())
		{
			buffer.append("  (");
			while (it.hasNext())
			{
				buffer.append('*').append(it.next());
				if (it.hasNext())
					buffer.append(", ");
			}
			buffer.append(')');
		}
		return new FileMatcher(buffer.toString(), filenameSuffixes);
	}

	//------------------------------------------------------------------

	public static FileMatcher from(
		String		description,
		String...	filenameSuffixes)
	{
		return from(description, List.of(filenameSuffixes));
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
		return (location != null) && super.matches(location)
				&& (Files.notExists(location, LinkOption.NOFOLLOW_LINKS)
						|| Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS));
	}

	//------------------------------------------------------------------

	@Override
	public boolean matchesExists(
		Path	location)
	{
		return (location != null) && super.matches(location)
				&& Files.isRegularFile(location, LinkOption.NOFOLLOW_LINKS);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
