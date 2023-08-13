/*====================================================================*\

FontUtils.java

Class: font-related utility methods.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.font;

//----------------------------------------------------------------------


// IMPORTS


import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

//----------------------------------------------------------------------


// CLASS: FONT-RELATED UTILITY METHODS


/**
 * This class contains utility methods that relate to JavaFX {@linkplain Font fonts}.
 */

public class FontUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private FontUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the size of the default JavaFX font.
	 *
	 * @return the size of the default JavaFX font.
	 */

	public static double getDefaultSize()
	{
		return Font.getDefault().getSize();
	}

	//------------------------------------------------------------------

	/**
	 * Returns the size of the default JavaFX font multiplied by the specified factor.
	 *
	 * @param  factor
	 *           the factor by which the size of the default JavaFX font will be multiplied.
	 * @return the size of the default JavaFX font multiplied by {@code factor}.
	 */

	public static double getSize(
		double	factor)
	{
		return Font.getDefault().getSize() * factor;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the {@linkplain Font#getDefault() default font} whose size is scaled by the specified factor.
	 *
	 * @param  sizeFactor
	 *           the factor by which the size of the default font will be scaled.
	 * @return the default font scaled by {@code sizeFactor}.
	 */

	public static Font defaultFont(
		double	sizeFactor)
	{
		Font font = Font.getDefault();
		return Font.font(font.getFamily(), font.getSize() * sizeFactor);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the bold variant of the {@linkplain Font#getDefault() default font}.
	 *
	 * @return the bold variant of the default font.
	 */

	public static Font boldFont()
	{
		return boldFont(Font.getDefault(), 1.0);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the bold variant of the specified font.
	 *
	 * @param  font
	 *           the font whose bold variant is required.
	 * @return the bold variant of {@code font}.
	 */

	public static Font boldFont(
		Font	font)
	{
		return boldFont(font, 1.0);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the bold variant of the {@linkplain Font#getDefault() default font} whose size is multiplied by the
	 * specified factor.
	 *
	 * @param  sizeFactor
	 *           the factor by which the size of the default font will be multiplied.
	 * @return the bold variant of the default font scaled by {@code sizeFactor}.
	 */

	public static Font boldFont(
		double	sizeFactor)
	{
		return boldFont(Font.getDefault(), sizeFactor);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the bold variant of the specified font whose size is multiplied by the specified factor.
	 *
	 * @param  font
	 *           the font whose bold variant is desired.
	 * @param  sizeFactor
	 *           the factor by which the size of {@code font} will be multiplied.
	 * @return the bold variant of {@code font} scaled by {@code sizeFactor}.
	 */

	public static Font boldFont(
		Font	font,
		double	sizeFactor)
	{
		return Font.font(font.getFamily(), FontWeight.BOLD, font.getSize() * sizeFactor);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the italic variant of the {@linkplain Font#getDefault() default font}.
	 *
	 * @return the italic variant of the default font.
	 */

	public static Font italicFont()
	{
		return italicFont(Font.getDefault(), 1.0);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the italic variant of the specified font.
	 *
	 * @param  font
	 *           the font whose italic variant is desired.
	 * @return the italic variant of {@code font}.
	 */

	public static Font italicFont(
		Font	font)
	{
		return italicFont(font, 1.0);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the italic variant of the {@linkplain Font#getDefault() default font} whose size is multiplied by the
	 * specified factor.
	 *
	 * @param  sizeFactor
	 *           the factor by which the size of the default font will be multiplied.
	 * @return the italic variant of the default font scaled by {@code sizeFactor}.
	 */

	public static Font italicFont(
		double	sizeFactor)
	{
		return italicFont(Font.getDefault(), sizeFactor);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the italic variant of the specified font whose size is multiplied by the specified factor.
	 *
	 * @param  font
	 *           the font whose italic variant is desired.
	 * @param  sizeFactor
	 *           the factor by which the size of {@code font} will be multiplied.
	 * @return the italic variant of {@code font} scaled by {@code sizeFactor}.
	 */

	public static Font italicFont(
		Font	font,
		double	sizeFactor)
	{
		return Font.font(font.getFamily(), FontPosture.ITALIC, font.getSize() * sizeFactor);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the bold italic variant of the {@linkplain Font#getDefault() default font}.
	 *
	 * @return the bold italic variant of the default font.
	 */

	public static Font boldItalicFont()
	{
		return boldItalicFont(Font.getDefault());
	}

	//------------------------------------------------------------------

	/**
	 * Returns the bold italic variant of the specified font.
	 *
	 * @param  font
	 *           the font whose bold italic variant is desired.
	 * @return the bold italic variant of {@code font}.
	 */

	public static Font boldItalicFont(
		Font	font)
	{
		return boldItalicFont(font, 1.0);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the bold italic variant of the {@linkplain Font#getDefault() default font} whose size is multiplied by
	 * the specified factor.
	 *
	 * @param  sizeFactor
	 *           the factor by which the size of the default font will be multiplied.
	 * @return the bold italic variant of the default font scaled by {@code sizeFactor}.
	 */

	public static Font boldItalicFont(
		double	sizeFactor)
	{
		return boldItalicFont(Font.getDefault(), sizeFactor);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the bold italic variant of the specified font whose size is multiplied by the specified factor.
	 *
	 * @param  font
	 *           the font whose italic variant is desired.
	 * @param  sizeFactor
	 *           the factor by which the size of {@code font} will be multiplied.
	 * @return the bold italic variant of {@code font} scaled by {@code sizeFactor}.
	 */

	public static Font boldItalicFont(
		Font	font,
		double	sizeFactor)
	{
		return Font.font(font.getFamily(), FontWeight.BOLD, FontPosture.ITALIC, font.getSize() * sizeFactor);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
