/*====================================================================*\

Fonts.java

Class: fonts.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.font;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.text.Font;

//----------------------------------------------------------------------


// CLASS: FONTS


public class Fonts
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The name of the family of monospaced fonts. */
	public static final		String	MONO_FONT_FAMILY	= "Roboto Mono";

	/** The resource directory that contains fonts that are used by this class. */
	private static final	String	DIRECTORY	= "/uk/blankaspect/ui/common/fonts/";

	/** The filename stems of monospaced fonts. */
	private static final	String[]	MONO_FONT_FILENAMES	=
	{
		FontFilename.ROBOTO,
		FontFilename.ROBOTO_BOLD,
		FontFilename.ROBOTO_ITALIC,
		FontFilename.ROBOTO_BOLD_ITALIC
	};

	/** Sizes of monospaced fonts. */
	public interface MonoFontSize
	{
		double	DEFAULT	= 13.0;
		double	SMALLER	= 11.0;
	}

	/** The filename stems of fonts that are used in this class. */
	private interface FontFilename
	{
		String	ROBOTO				= "RobotoMono-Regular";
		String	ROBOTO_BOLD			= "RobotoMono-Bold";
		String	ROBOTO_ITALIC		= "RobotoMono-Italic";
		String	ROBOTO_BOLD_ITALIC	= "RobotoMono-BoldItalic";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	/** Flag: if {@code true}, the monospaced fonts have been loaded. */
	private static	boolean	monoFontsLoaded;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		FontCache.addDirectory(Fonts.class, DIRECTORY);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private Fonts()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Loads the monospaced fonts.
	 */

	public static void loadMonoFonts()
	{
		if (!monoFontsLoaded)
		{
			for (String filename : MONO_FONT_FILENAMES)
				FontCache.getFont(filename, MonoFontSize.DEFAULT);
			monoFontsLoaded = true;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Returns a regular monospaced font (Roboto Mono) of the {@linkplain MonoFontSize#DEFAULT default size}.
	 *
	 * @return a regular monospaced font of the default size.
	 */

	public static Font monoFont()
	{
		return monoFont(MonoFontSize.DEFAULT);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a regular monospaced font (Roboto Mono) of the specified size.
	 *
	 * @param  size
	 *           the desired size of the font.
	 * @return a regular monospaced font whose size is <i>size</i>.
	 */

	public static Font monoFont(
		double	size)
	{
		// Load mono fonts
		loadMonoFonts();

		// Return regular mono font
		return FontCache.getFont(FontFilename.ROBOTO, size);
	}

	//------------------------------------------------------------------

	/**
	 * Returns a regular monospaced font (Roboto Mono) that is {@linkplain MonoFontSize#SMALLER smaller} than the
	 * default size.
	 *
	 * @return a regular monospaced font that is smaller than the default size.
	 */

	public static Font monoFontSmaller()
	{
		return monoFont(MonoFontSize.SMALLER);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
