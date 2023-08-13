/*====================================================================*

FontCache.java

Class: font cache.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.font;

//----------------------------------------------------------------------


// IMPORTS


import java.io.InputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedList;

import javafx.scene.text.Font;

import uk.blankaspect.common.exception2.ExceptionUtils;

import uk.blankaspect.common.resource.ResourceUtils;

//----------------------------------------------------------------------


// CLASS: FONT CACHE


/**
 * This class implements a means of loading named fonts from the resources of registered classes, and caching those
 * fonts so that they are loaded only once.
 */

public class FontCache
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The directory that contains font files. */
	private static final	String	DIRECTORY	= "../../fonts/";

	/** The default filename extension of a font file. */
	private static final	String	DEFAULT_FILENAME_EXTENSION	= ".ttf";

	/** The separator between the filename and the size of a font. */
	private static final	String	SEPARATOR	= ";";

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** A list of pathnames of directories that contain fonts. */
	private static	LinkedList<String>		directories	= new LinkedList<>();

	/** The cache of fonts in which a font is associated with a key composed of the name of the file from which it was
		loaded and the size of the font. */
	private static	HashMap<String, Font>	fonts		= new HashMap<>();

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Add the font directory of this class to the list
		addDirectory(FontCache.class, DIRECTORY);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private FontCache()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns a {@linkplain Font font} of the specified size for the file with the specified filename.  If the filename
	 * does not have an extension (ie, it is a filename stem), the default extension ({@code .ttf}) will be appended to
	 * the filename.
	 * <p>
	 * If the named font was returned by a previous call to this method, its cached copy will be returned by subsequent
	 * calls; otherwise, the directories that have been registered with {@link #addDirectory(Class, String)} are
	 * searched for the first occurrence of the named file in the reverse order of registration (ie, the last directory
	 * to be added is searched first).  If the named file is found in any of the directories, the associated class
	 * loader attempts to load it and to create a font from it; if successful, the font is added to the cache.
	 * </p>
	 * <p>
	 * If a font with the specified filename is not found in the cache or in any of the directories, the default font is
	 * returned.
	 * </p>
	 *
	 * @param  filename
	 *           the filename or filename stem of the required font file.
	 * @param  size
	 *           the size of the required font.
	 * @return the font that corresponds to the specified filename, or the default font if the specified font was not
	 *         found by the class loader.
	 */

	public static Font getFont(
		String	filename,
		double	size)
	{
		// Append default filename extension to a filename that doesn't have an extension
		if (filename.indexOf('.') < 0)
			filename += DEFAULT_FILENAME_EXTENSION;

		// Get font from cache
		String key = filename + SEPARATOR + size;
		Font font = fonts.get(key);

		// If font is not in cache, try to load font from resource file
		if (font == null)
		{
			for (String directory : directories)
			{
				// Get pathname of font file
				String pathname = directory;
				pathname += pathname.endsWith("/") ? filename : "/" + filename;

				// Open input stream on resource
				InputStream inStream = FontCache.class.getResourceAsStream(pathname);

				// If resource was found, create font from it and add font to cache
				if (inStream != null)
				{
					// Create font
					font = Font.loadFont(inStream, size);

					// Add font to cache
					if (font != null)
						fonts.put(key, font);

					// Close input stream
					try
					{
						inStream.close();
					}
					catch (IOException e)
					{
						ExceptionUtils.printStderrLocated(e);
					}
					break;
				}
			}
		}

		// If font is not in cache and its resource was not found, use default font
		if (font == null)
			font = Font.font(size);

		// Return font
		return font;
	}

	//------------------------------------------------------------------

	/**
	 * Registers the specified class and the pathname of the directory that contains fonts for the class.  The directory
	 * will be included in the search of uncached fonts that is performed by {@link #getImage(String)}.  Directories are
	 * searched in reverse order of registration (ie, the last directory to be added by this method is searched first).
	 *
	 * @param cls
	 *          the class whose fonts are located in the directory denoted by {@code pathname}.
	 * @param pathname
	 *          the pathname of the directory that contains fonts for {@code cls}.
	 */

	public static void addDirectory(
		Class<?>	cls,
		String		pathname)
	{
		directories.addFirst(ResourceUtils.normalisedPathname(cls, pathname));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
