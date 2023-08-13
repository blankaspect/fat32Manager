/*====================================================================*\

WindowState.java

Class: state of JavaFX window.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.window;

//----------------------------------------------------------------------


// IMPORTS


import java.util.List;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import javafx.stage.Stage;

import uk.blankaspect.common.basictree.AbstractNode;
import uk.blankaspect.common.basictree.IntNode;
import uk.blankaspect.common.basictree.MapNode;

import uk.blankaspect.common.exception.UnexpectedRuntimeException;

//----------------------------------------------------------------------


// CLASS: STATE OF JAVAFX WINDOW


/**
 * This class implements a means of serialising the location, size and visibility of a {@linkplain Stage window} as a
 * {@link MapNode}.
 */

public class WindowState
	implements Cloneable
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** Keys of properties. */
	private interface PropertyKey
	{
		String	HIDDEN		= "hidden";
		String	LOCATION	= "location";
		String	MAXIMISED	= "maximised";
		String	SIZE		= "size";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The location of the window. */
	private	Point2D		location;

	/** The size of the window. */
	private	Dimension2D	size;

	/** Flag: if {@code true}, the window is not visible. */
	private	boolean		hidden;

	/** Flag: if {@code true}, the window is resizable. */
	private	boolean		resizable;

	/** Flag: if {@code true}, the window is maximised. */
	private	boolean		maximised;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a window state.
	 *
	 * @param hidden
	 *          if {@code true}, the window is initially hidden.
	 * @param resizable
	 *          if {@code true}, the window is resizable.
	 */

	public WindowState(
		boolean	hidden,
		boolean	resizable)
	{
		// Initialise instance variables
		this.hidden = hidden;
		this.resizable = resizable;
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a window state for the specified window.
	 *
	 * @param window
	 *          the window with whose location and size the window state will be initialised.
	 */

	public WindowState(
		Stage	window)
	{
		if (window != null)
		{
			// Update location and size
			updateBounds(window);

			// Update 'resizable' flag
			resizable = window.isResizable();
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public WindowState clone()
	{
		try
		{
			WindowState copy = (WindowState)super.clone();
			if (location != null)
				copy.location = new Point2D(location.getX(), location.getY());
			if (size != null)
				copy.size = new Dimension2D(size.getWidth(), size.getHeight());
			return copy;
		}
		catch (CloneNotSupportedException e)
		{
			throw new UnexpectedRuntimeException(e);
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the location of the window.
	 *
	 * @return the location of the window.
	 */

	public Point2D getLocation()
	{
		return location;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the location to the specified coordinates.
	 *
	 * @param x
	 *          the <i>x</i> coordinate.
	 * @param y
	 *          the <i>y</i> coordinate.
	 */

	public void setLocation(
		double	x,
		double	y)
	{
		location = new Point2D(x, y);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the size of the window.
	 *
	 * @return the size of the window.
	 */

	public Dimension2D getSize()
	{
		return size;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the size to the specified dimensions.
	 *
	 * @param width
	 *          the width.
	 * @param height
	 *          the height.
	 */

	public void setSize(
		double	width,
		double	height)
	{
		size = new Dimension2D(width, height);
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the window is not visible.
	 *
	 * @return {@code true} if the window is not visible.
	 */

	public boolean isHidden()
	{
		return hidden;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>hidden</i> state of the window to the specified value.
	 *
	 * @param hidden
	 *          the value to which the <i>hidden</i> state of the window will be set.
	 */

	public void setHidden(
		boolean	hidden)
	{
		this.hidden = hidden;
	}

	//------------------------------------------------------------------

	/**
	 * Returns {@code true} if the window is maximised.
	 *
	 * @return {@code true} if the window is maximised.
	 */

	public boolean isMaximised()
	{
		return maximised;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the <i>maximised</i> state of the window to the specified value.
	 *
	 * @param maximised
	 *          the value to which the <i>maximised</i> state of the window will be set.
	 */

	public void setMaximised(
		boolean	maximised)
	{
		this.maximised = maximised;
	}

	//------------------------------------------------------------------

	/**
	 * Updates this window state with the location and size of the specified window.
	 *
	 * @param window
	 *          the window with whose location and size this window state will be updated.
	 */

	public void updateBounds(
		Stage	window)
	{
		if (!window.isIconified())
		{
			setLocation(window.getX(), window.getY());
			setSize(window.getWidth(), window.getHeight());
		}
	}

	//------------------------------------------------------------------

	/**
	 * Uniconifies and unmaximises the specified window, updates this window state with the location and size of the
	 * window, updates its <i>maximised</i> state, and sets its <i>hidden</i> state to {@code false}.
	 *
	 * @param window
	 *          the window with whose location and size this window state will be updated.
	 */

	public void restoreAndUpdate(
		Stage	window)
	{
		restoreAndUpdate(window, false);
	}

	//------------------------------------------------------------------

	/**
	 * Uniconifies and unmaximises the specified window, updates this window state with the location and size of the
	 * window, updates its <i>maximised</i> state, and sets its <i>hidden</i> state to the specified value.
	 *
	 * @param window
	 *          the window with whose location and size this window state will be updated.
	 * @param hidden
	 *          the value to which the <i>hidden</i> state of the window will be set.
	 */

	public void restoreAndUpdate(
		Stage	window,
		boolean	hidden)
	{
		// Uniconify window
		if (window.isIconified())
			window.setIconified(false);

		// Unmaximise window
		boolean maximised = window.isMaximized();
		if (maximised)
			window.setMaximized(false);

		// Update location and size
		updateBounds(window);

		// Update 'hidden' flag
		this.hidden = hidden;

		// Update 'maximised' flag
		this.maximised = maximised;
	}

	//------------------------------------------------------------------

	/**
	 * Encodes this window state as a tree of {@linkplain AbstractNode nodes} and returns the root node.
	 *
	 * @return the root node of the tree of {@linkplain AbstractNode nodes} that encodes this window state.
	 */

	public MapNode encodeTree()
	{
		// Create root node
		MapNode rootNode = new MapNode();

		// Encode location
		if (location != null)
			rootNode.addInts(PropertyKey.LOCATION, (int)location.getX(), (int)location.getY());

		// Encode size
		if (resizable && (size != null))
			rootNode.addInts(PropertyKey.SIZE, (int)size.getWidth(), (int)size.getHeight());

		// Encode 'hidden' flag
		if (hidden)
			rootNode.addBoolean(PropertyKey.HIDDEN, true);

		// Encode 'maximised' flag
		if (maximised)
			rootNode.addBoolean(PropertyKey.MAXIMISED, true);

		// Return root node
		return rootNode;
	}

	//------------------------------------------------------------------

	/**
	 * Decodes this window state from the tree of {@linkplain AbstractNode nodes} whose root is the specified node.
	 *
	 * @param rootNode
	 *          the root of the tree of {@linkplain AbstractNode nodes} from which this window state will be decoded.
	 */

	public void decodeTree(
		MapNode	rootNode)
	{
		// Decode location
		location = null;
		String key = PropertyKey.LOCATION;
		if (rootNode.hasList(key))
		{
			List<IntNode> nodes = rootNode.getListNode(key).intNodes();
			if (nodes.size() >= 2)
				location = new Point2D((double)nodes.get(0).getValue(), (double)nodes.get(1).getValue());
		}

		// Decode size
		size = null;
		key = PropertyKey.SIZE;
		if (resizable && rootNode.hasList(key))
		{
			List<IntNode> nodes = rootNode.getListNode(key).intNodes();
			if (nodes.size() >= 2)
			{
				int width = nodes.get(0).getValue();
				int height = nodes.get(1).getValue();
				if ((width > 0) && (height > 0))
					size = new Dimension2D((double)width, (double)height);
			}
		}

		// Decode 'hidden' flag
		hidden = rootNode.getBoolean(PropertyKey.HIDDEN, false);

		// Decode 'maximised' flag
		maximised = rootNode.getBoolean(PropertyKey.MAXIMISED, false);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
