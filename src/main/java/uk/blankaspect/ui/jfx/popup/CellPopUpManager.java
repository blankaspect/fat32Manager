/*====================================================================*\

CellPopUpManager.java

Class: cell pop-up manager.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.popup;

//----------------------------------------------------------------------


// IMPORTS


import java.util.Iterator;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.geometry.Point2D;

import javafx.scene.Node;

import javafx.scene.input.MouseEvent;

import javafx.stage.Popup;
import javafx.stage.Window;

import javafx.util.Duration;

import uk.blankaspect.common.os.OsUtils;

//----------------------------------------------------------------------


// CLASS: CELL POP-UP MANAGER


/**
 * This class provides a means of managing the creation and display of pop-up windows for JavaFX cells.
 */

public class CellPopUpManager
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Keys of system properties. */
	private interface SystemPropertyKey
	{
		String	DEACTIVATE_POP_UP_ON_MOUSE_EXITED =
				"blankaspect.ui.jfx.cellPopUpManager.deactivatePopUpOnMouseExited";
	}

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		if (OsUtils.isWindows())
			System.setProperty(SystemPropertyKey.DEACTIVATE_POP_UP_ON_MOUSE_EXITED, Boolean.toString(true));
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The delay (in milliseconds) before a pop-up window is displayed after it is activated. */
	private	int			delay;

	/** The system time at which a pop-up window was last activated. */
	private	long		activationTime;

	/** The timer that provides the delay between the activation of a pop-up window and its display. */
	private	Timeline	activationTimer;

	/** The current pop-up window. */
	private	Popup		popUp;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a manager for pop-up windows that are displayed for cells.
	 *
	 * @param delay
	 *          the delay (in milliseconds) before a pop-up window is displayed after it is activated.
	 */

	public CellPopUpManager(
		int	delay)
	{
		// Validate arguments
		if (delay < 0)
			throw new IllegalArgumentException("Delay out of bounds: " + delay);

		// Intialise instance variables
		this.delay = delay;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns {@code true} if a pop-up window for a cell should be deactivated when the cell receives a {@link
	 * MouseEvent#MOUSE_EXITED MOUSE_EXITED} event.
	 *
	 * @return {@code true} if a pop-up window for a cell should be deactivated when the cell receives a {@code
	 *         MOUSE_EXITED} event.
	 */

	public static boolean deactivatePopUpOnMouseExited()
	{
		return Boolean.getBoolean(SystemPropertyKey.DEACTIVATE_POP_UP_ON_MOUSE_EXITED);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the two specified cell identifiers match.
	 *
	 * @param  id1
	 *           the first cell identifier.
	 * @param  id2
	 *           the second cell identifier.
	 * @return {@code true} if {@code id1} and {@code id2} match.
	 */

	private static boolean cellIdsMatch(
		Object	id1,
		Object	id2)
	{
		if (((id1 instanceof Integer) && (id2 instanceof Integer))
				|| ((id1 instanceof Long) && (id2 instanceof Long))
				|| ((id1 instanceof String) && (id2 instanceof String)))
			return id1.equals(id2);
		return (id1 == id2);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Activates a pop-up window for the cell that is associated with the specified identifier.  When {@link #delay} has
	 * elapsed, the specified iterator is used to search a collection of cells for the cell whose {@linkplain
	 * ICell#getIdentifier() identifier} is identical to the specified identifier.  If a matching cell is found, a
	 * pop-up window containing the node that is returned by the cell's {@link ICell#getPopupContent()
	 * getPopupContent()} method is displayed at the location that is returned by the cell's {@link
	 * ICell#getPrefPopupLocation(Node) getPopupLocation(Node)} method.
	 *
	 * @param id
	 *          the identifier of the cell for which a pop-up window will be displayed.
	 * @param cellIterator
	 *          the iterator that will be used to search a collection of cells for the cell whose identifier is {@code
	 *          id}.
	 */

	public void activate(
		Object							id,
		Iterator<? extends ICell<?>>	cellIterator)
	{
		// Deactivate a pop-up that is pending or hide a pop-up that is displayed
		deactivate();

		// If an identifier was specified, start timer to display pop-up
		if (id != null)
		{
			// Update activation time
			activationTime = System.currentTimeMillis();

			// Create timer
			activationTimer = new Timeline(new KeyFrame(Duration.millis((double)delay), event ->
			{
				// Invalidate timer
				activationTimer = null;

				// Find activated cell and display a pop-up for it
				while (cellIterator.hasNext())
				{
					// Get next cell
					ICell<?> cell = cellIterator.next();

					// If cell matches activated cell, display pop-up for cell
					if (cellIdsMatch(id, cell.getIdentifier()))
					{
						// Get content of pop-up
						Node content = cell.getPopUpContent();

						// If there is content for pop-up ...
						if (content != null)
						{
							// Get window that contains cell
							Window window = cell.getWindow();

							// If cell is contained in a window ...
							if (window != null)
							{
								// Create pop-up and add content to it
								popUp = new Popup();
								popUp.getContent().add(content);

								// Display pop-up
								popUp.show(window);

								// Get location of pop-up
								Point2D location = cell.getPrefPopUpLocation(content);

								// Relocate pop-up
								if (location != null)
								{
									popUp.setX(location.getX());
									popUp.setY(location.getY());
								}
							}
						}
						break;
					}
				}
			}));

			// Start timer
			activationTimer.play();
		}
	}

	//------------------------------------------------------------------

	/**
	 * Deactivates any pending pop-up window and hides a pop-up window that is displayed.
	 *
	 * @return {@code true} if a pop-up window was displayed.
	 */

	public boolean deactivate()
	{
		// If a timer is running, stop it
		if (activationTimer != null)
		{
			// Stop timer
			activationTimer.stop();

			// Invalidate timer
			activationTimer = null;
		}

		// Initialise 'pop-up shown' flag
		boolean popUpShown = false;

		// If a pop-up is displayed, hide it
		if (popUp != null)
		{
			// Determine whether pop-up is showing
			popUpShown = popUp.isShowing();

			// Hide pop-up
			popUp.hide();

			// Invalidate instance variable
			popUp = null;
		}

		// Return 'pop-up shown' flag
		return popUpShown;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if a pop-up window has been activated but not yet displayed.
	 *
	 * @return {@code true} if a pop-up window has been activated but not yet displayed.
	 */

	public boolean isActivated()
	{
		return (System.currentTimeMillis() - activationTime < delay);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if a pop-up window is displayed.
	 *
	 * @return {@code true} if a pop-up window is displayed.
	 */

	public boolean isPopUpDisplayed()
	{
		return (popUp != null);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member interfaces
////////////////////////////////////////////////////////////////////////


	// INTERFACE: MANAGED CELL


	/**
	 * This interface defines the methods that must be implemented by a cell that is managed by a {@link
	 * CellPopUpManager}.
	 *
	 * @param <T>
	 *          the type of item that is represented by a cell.
	 */

	public interface ICell<T>
	{

	////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Returns the item that is associated with this cell.
		 *
		 * @return the item that is associated with this cell, or {@code null} if the cell is empty.
		 */

		T getItem();

		//--------------------------------------------------------------

		/**
		 * Returns the object that {@link CellPopupManager#activate(Object, Iterator)} will use to identify this cell
		 * when it searches a collection of cells for the cell whose pop-up window has been activated.  By default, this
		 * method calls {@link #getItem()} and returns the result.
		 *
		 * @return the object that a cell pop-up manager will use to identify this cell when it searches a collection of
		 *         cells for the cell whose pop-up window has been activated.
		 */

		default Object getIdentifier()
		{
			return getItem();
		}

		//--------------------------------------------------------------
		/**
		 * Returns the node that will be the content of a pop-up window for this cell.
		 *
		 * @return the node that will be the content of a pop-up window for this cell.  If {@code null}, no pop-up
		 *         window will be displayed.
		 */

		Node getPopUpContent();

		//--------------------------------------------------------------

		/**
		 * Returns the preferred location of a pop-up window that has the specified content.  This method is called
		 * immediately after the pop-up window is made visible.
		 *
		 * @param  content
		 *           the content of the pop-up window.
		 * @return the preferred location of a pop-up window that contains {@code content}.
		 */

		Point2D getPrefPopUpLocation(
			Node	content);

		//--------------------------------------------------------------

		/**
		 * Returns the window that will be the owner of any pop-up windows that are created for this cell.
		 *
		 * @return the window that will be the owner of any pop-up windows that are created for this cell.  If {@code
		 *         null}, no pop-up window will be displayed.
		 */

		Window getWindow();

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
