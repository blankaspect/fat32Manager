/*====================================================================*\

PopUpUtils.java

Class: utility methods for pop-up windows.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import javafx.geometry.Point2D;

import javafx.scene.Node;

import uk.blankaspect.common.function.IFunction0;

import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

//----------------------------------------------------------------------


// CLASS: UTILITY METHODS FOR POP-UP WINDOWS


/**
 * This class contains utility methods related to {@linkplain Popup pop-up windows}.
 */

public class PopUpUtils
{

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Prevents this class from being instantiated externally.
	 */

	private PopUpUtils()
	{
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a locator function for pop-up window that aligns the specified reference position of the pop-up with the
	 * specified reference position of the target node.
	 *
	 * @param target
	 *          the target node with which the pop-up is associated.
	 * @param targetPos
	 *          the reference position of {@code target}, which is aligned with {@code popUpPos} when the pop-up is
	 *          displayed.
	 * @param popUpPos
	 *          the reference position of the content of the pop-up, which is aligned with {@code targetPos} when the
	 *          pop-up is displayed.
	 */

	public static IPopUpLocator createLocator(
		Node	target,
		VHPos	targetPos,
		VHPos	popUpPos)
	{
		return createLocator(target, targetPos, popUpPos, 0.0, 0.0);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a locator function for pop-up window that aligns the specified reference position of the pop-up with the
	 * specified reference position of the target node and adds the specified offsets to the <i>x</i> and <i>y</i>
	 * coordinates of the resulting location.
	 *
	 * @param target
	 *          the target node with which the pop-up is associated.
	 * @param targetPos
	 *          the reference position of {@code target}, which is aligned with {@code popUpPos} when the pop-up is
	 *          displayed.  If it is {@code null}, the base location will be the one that is returned by the locator.
	 * @param popUpPos
	 *          the reference position of the content of the pop-up, which is aligned with {@code targetPos} when the
	 *          pop-up is displayed.
	 * @param xOffset
	 *          the value that will be added to the <i>x</i> coordinate of the location of the pop-up that is calculated
	 *          from {@code targetPos} and {@code popUpPos}.  If it is {@code NaN}, the <i>x</i> coordinate of the
	 *          pop-up will be adjusted to keep the pop-up within the horizontal extent of the screens.
	 * @param yOffset
	 *          the value that will be added to the <i>y</i> coordinate of the location of the pop-up that is calculated
	 *          from {@code targetPos} and {@code popUpPos}.  If it is {@code NaN}, the <i>y</i> coordinate of the
	 *          pop-up will be adjusted to keep the pop-up within the vertical extent of the screens.
	 */

	public static IPopUpLocator createLocator(
		Node	target,
		VHPos	targetPos,
		VHPos	popUpPos,
		double	xOffset,
		double	yOffset)
	{
		return (contentBounds, locator) ->
		{
			// Calculate base location of pop-up
			Point2D location = (targetPos == null) ? locator.invoke()
												   : SceneUtils.getRelativeLocation(contentBounds.getWidth(),
																					contentBounds.getHeight(),
																					popUpPos, target, targetPos);

			// Adjust x coordinate of pop-up
			double x = location.getX();
			x += Double.isNaN(xOffset) ? SceneUtils.deltaXWithinScreen(x, x + contentBounds.getWidth()) : xOffset;

			// Adjust y coordinate of pop-up
			double y = location.getY();
			y += Double.isNaN(yOffset) ? SceneUtils.deltaYWithinScreen(y, y + contentBounds.getHeight()) : yOffset;

			// Return location of pop-up
			return new Point2D(x, y);
		};
	}

	//------------------------------------------------------------------

	/**
	 * Creates a pop-up window for the specified target node and content node using an instance of {@link PopUpManager}
	 * that is created by this method.  The pop-up is located by aligning the specified reference position of the pop-up
	 * with the specified reference position of the target node.
	 *
	 * @param  target
	 *           the target node that will trigger the display of the pop-up.
	 * @param  content
	 *           the content of the pop-up.
	 * @param  targetPos
	 *           the reference position of {@code target}, which is aligned with {@code popUpPos} when the pop-up is
	 *           displayed.
	 * @param  popUpPos
	 *           the reference position of the label of the pop-up, which is aligned with {@code targetPos} when the
	 *           pop-up is displayed.
	 * @return the pop-up manager.
	 */

	public static PopUpManager createPopUp(
		Node	target,
		Node	content,
		VHPos	targetPos,
		VHPos	popUpPos)
	{
		return createPopUp(new PopUpManager(), target, content, targetPos, popUpPos, 0.0, 0.0);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a pop-up window for the specified target node and content node using the specified instance of {@link
	 * PopUpManager}.  The pop-up is located by aligning the specified reference position of the pop-up with the
	 * specified reference position of the target node and adding the specified offsets to the <i>x</i> and <i>y</i>
	 * coordinates of the resulting location.
	 *
	 * @param  popUpManager
	 *           the object that will manage the creation and display of the pop-up window.
	 * @param  target
	 *           the target node that will trigger the display of the pop-up.
	 * @param  content
	 *           the content of the pop-up.
	 * @param  targetPos
	 *           the reference position of {@code target}, which is aligned with {@code popUpPos} when the pop-up is
	 *           displayed.
	 * @param  popUpPos
	 *           the reference position of the label of the pop-up, which is aligned with {@code targetPos} when the
	 *           pop-up is displayed.
	 * @param  xOffset
	 *           the value that will be added to the <i>x</i> coordinate of the location of the pop-up that is
	 *           calculated from {@code targetPos} and {@code popUpPos}.  If it is {@code NaN}, the <i>x</i> coordinate
	 *           of the pop-up will be adjusted to keep the pop-up within the horizontal extent of the screens.
	 * @param  yOffset
	 *           the value that will be added to the <i>y</i> coordinate of the location of the pop-up that is
	 *           calculated from {@code targetPos} and {@code popUpPos}.  If it is {@code NaN}, the <i>y</i> coordinate
	 *           of the pop-up will be adjusted to keep the pop-up within the vertical extent of the screens.
	 * @return the pop-up manager.
	 */

	public static PopUpManager createPopUp(
		PopUpManager	popUpManager,
		Node			target,
		Node			content,
		VHPos			targetPos,
		VHPos			popUpPos,
		double			xOffset,
		double			yOffset)
	{
		// Add event handlers to show and hide pop-up
		popUpManager.addEventHandlers(target, targetPos, popUpPos, event ->
		{
			popUpManager.showPopUp(target, content, event,
								   createLocator(target, targetPos, popUpPos, xOffset, yOffset));
		});

		// Return pop-up manager
		return popUpManager;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a pop-up window for the specified target node using the specified instance of {@link LabelPopUpManager}.
	 * The pop-up window contains a label whose text and graphic that are provided by the specified sources.  The pop-up
	 * is located by aligning the specified reference position of the pop-up with the specified reference position of
	 * the target node and adding the specified offsets to the <i>x</i> and <i>y</i> coordinates of the resulting
	 * location.
	 *
	 * @param  popUpManager
	 *           the object that will manage the creation and display of the pop-up window.  If it is {@code null}, a
	 *           new instance of a {@linkplain LabelPopUpManager label pop-up manager} will be created.
	 * @param  target
	 *           the target node that will trigger the display of the pop-up.
	 * @param  targetPos
	 *           the reference position of {@code target}, which is aligned with {@code popUpPos} when the pop-up is
	 *           displayed.
	 * @param  popUpPos
	 *           the reference position of the label of the pop-up, which is aligned with {@code targetPos} when the
	 *           pop-up is displayed.
	 * @param  xOffset
	 *           the value that will be added to the <i>x</i> coordinate of the location of the pop-up that is
	 *           calculated from {@code targetPos} and {@code popUpPos}.
	 * @param  yOffset
	 *           the value that will be added to the <i>y</i> coordinate of the location of the pop-up that is
	 *           calculated from {@code targetPos} and {@code popUpPos}.
	 * @param  textSource
	 *           the source of the text of the label of the pop-up, which may be {@code null}.
	 * @param  graphicSource
	 *           the source of the graphic of the label of the pop-up, which may be {@code null}.
	 * @return the pop-up manager.
	 */

	public static LabelPopUpManager createPopUp(
		LabelPopUpManager	popUpManager,
		Node				target,
		VHPos				targetPos,
		VHPos				popUpPos,
		double				xOffset,
		double				yOffset,
		IFunction0<String>	textSource,
		IFunction0<Node>	graphicSource)
	{
		// Add event handlers to show and hide pop-up
		popUpManager.addEventHandlers(target, targetPos, popUpPos, event ->
		{
			String text = (textSource == null) ? null : textSource.invoke();
			Node graphic = (graphicSource == null) ? null : graphicSource.invoke();
			if ((text != null) || (graphic != null))
			{
				popUpManager.showPopUp(target, text, graphic, event,
									   createLocator(target, targetPos, popUpPos, xOffset, yOffset));
			}
		});

		// Return pop-up manager
		return popUpManager;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
