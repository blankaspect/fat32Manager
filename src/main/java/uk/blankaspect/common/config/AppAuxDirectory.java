/*====================================================================*\

AppAuxDirectory.java

Class: auxiliary directory of an application.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.common.config;

//----------------------------------------------------------------------


// IMPORTS


import java.io.File;

import java.lang.invoke.MethodHandles;

import java.net.URI;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import uk.blankaspect.common.filesystem.PathnameUtils;

import uk.blankaspect.common.function.IFunction1;

import uk.blankaspect.common.logging.Logger;

//----------------------------------------------------------------------


// CLASS: AUXILIARY DIRECTORY OF AN APPLICATION


public class AppAuxDirectory
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	public enum Mode
	{
		NONE,
		IMPLICIT,
		EXPLICIT
	}

	private static final	String	UNIX_AUX_DIR_PREFIX	= ".";

	private static final	String	URI_FILE_SCHEME	= "file";

	private static final	String	DIRECTORY_NAME	= "blankaspect";

	private interface SystemPropertyKey
	{
		String	APP_AUX_DIR	= "blankaspect.app.auxDir";
		String	USER_HOME	= "user.home";
	}

	private interface EnvVariableName
	{
		String	WINDOWS_APP_DATA_DIR	= "APPDATA";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private AppAuxDirectory()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Directory getDirectory(
		String	appKey)
	{
		return getDirectory(appKey, null);
	}

	//------------------------------------------------------------------

	public static Directory getDirectory(
		String		appKey,
		Class<?>	cls)
	{
		// Validate arguments
		if (appKey == null)
			throw new IllegalArgumentException("Null app key");

		// Create function to fix separators of pathname
		IFunction1<String, String> fixSeparators = pathname -> pathname.replace('/', File.separatorChar);

		// Initialise result
		Path location = null;
		Mode mode = Mode.NONE;

		// Determine location of auxiliary directory
		try
		{
			// Get system property
			String pathname = System.getProperty(SystemPropertyKey.APP_AUX_DIR);

			// Case: system property is not defined
			if (pathname == null)
			{
				// Case: Windows platform
				if (File.separatorChar == '\\')
				{
					pathname = System.getenv(EnvVariableName.WINDOWS_APP_DATA_DIR);
					if (pathname != null)
						location = Path.of(fixSeparators.invoke(pathname)).resolve(DIRECTORY_NAME).resolve(appKey);
				}

				// Case: Unix-like platform
				else
				{
					pathname = System.getProperty(SystemPropertyKey.USER_HOME);
					if (pathname != null)
					{
						location = Path.of(fixSeparators.invoke(pathname))
													.resolve(UNIX_AUX_DIR_PREFIX + DIRECTORY_NAME).resolve(appKey);
					}
				}
			}

			// Case: system property is empty
			else if (pathname.isEmpty())
			{
				try
				{
					Class<?> class0 = (cls == null) ? MethodHandles.lookup().lookupClass() : cls;
					URI uri = class0.getProtectionDomain().getCodeSource().getLocation().toURI();
					if (URI_FILE_SCHEME.equals(uri.getScheme()))
					{
						location = Path.of(uri).toAbsolutePath().getParent();
						mode = Mode.IMPLICIT;
					}
				}
				catch (Exception e)
				{
					Logger.INSTANCE.error(e);

					e.printStackTrace();
				}
			}

			// Case: system property is not empty
			else
			{
				location = Path.of(PathnameUtils.parsePathname(fixSeparators.invoke(pathname)));
				mode = Mode.EXPLICIT;
			}
		}
		catch (InvalidPathException e)
		{
			Logger.INSTANCE.error(e);

			e.printStackTrace();
		}

		// Return result
		return (location == null) ? null : new Directory(location.normalize(), mode);
	}

	//------------------------------------------------------------------

	public static Path resolve(
		String	appKey,
		String	name)
	{
		return resolve(appKey, null, name);
	}

	//------------------------------------------------------------------

	public static Path resolve(
		String		appKey,
		Class<?>	cls,
		String		name)
	{
		// Validate arguments
		if (name == null)
			throw new IllegalArgumentException("Null name");

		// Get location of auxiliary directory
		Directory directory = getDirectory(appKey, cls);

		// Resolve name against location of auxiliary directory and return result
		return (directory == null) ? Path.of(name) : directory.location.resolve(name);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: DIRECTORY


	public record Directory(
		Path	location,
		Mode	mode)
	{ }

	//==================================================================

}

//----------------------------------------------------------------------
