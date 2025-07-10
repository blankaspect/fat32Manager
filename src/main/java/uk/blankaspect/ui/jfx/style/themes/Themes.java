/*====================================================================*\

Themes.java

Class: registry of themes.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.style.themes;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.List;

import uk.blankaspect.common.exception2.BaseException;

import uk.blankaspect.ui.jfx.style.AbstractTheme;

//----------------------------------------------------------------------


// CLASS: REGISTRY OF THEMES


public class Themes
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The classes of available themes. */
	private static final	List<Class<? extends AbstractTheme>>	CLASSES	= List.of
	(
		uk.blankaspect.ui.jfx.style.themes.dark.Theme.class,
		uk.blankaspect.ui.jfx.style.themes.light.Theme.class
	);

	/** Error messages. */
	private interface ErrorMsg
	{
		String	FAILED_TO_INSTANTIATE_CLASS	= "Class: %s\nFailed to create an instance of the class.";
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private Themes()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a list containing a new instance of each available theme.
	 *
	 * @return a list containing a new instance of each available theme.
	 * @throws BaseException
	 *           if an error occurs when instantiating a theme.
	 */

	public static List<AbstractTheme> instances()
		throws BaseException
	{
		// Initialise list of themes
		List<AbstractTheme> themes = new ArrayList<>();

		// Instantiate themes
		for (Class<? extends AbstractTheme> cls : CLASSES)
		{
			// Create instance of theme
			AbstractTheme theme = null;
			try
			{
				theme = (AbstractTheme)cls.getDeclaredConstructor().newInstance();
			}
			catch (Throwable e)
			{
				throw new BaseException(ErrorMsg.FAILED_TO_INSTANTIATE_CLASS, e, cls.getName());
			}

			// Add theme to list
			themes.add(theme);
		}

		// Return list of themes
		return themes;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
