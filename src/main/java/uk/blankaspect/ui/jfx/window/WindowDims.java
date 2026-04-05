/*====================================================================*\

WindowDims.java

Class: dimensions of a window.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.window;

//----------------------------------------------------------------------


// IMPORTS


import javafx.stage.Stage;

//----------------------------------------------------------------------


// CLASS: DIMENSIONS OF A WINDOW


public class WindowDims
{

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The window. */
	private	Stage	window;

	/** The width of the window. */
	private	double	w;

	/** The height of the window. */
	private	double	h;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public WindowDims(
		Stage	window)
	{
		// Initialise instance variables
		this.window = window;
		w = window.getWidth();
		h = window.getHeight();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public double w()
	{
		return w;
	}

	//------------------------------------------------------------------

	public double h()
	{
		return h;
	}

	//------------------------------------------------------------------

	public void update(
		boolean	always)
	{
		double w0 = window.getWidth();
		if (always || (w < w0))
			w = w0;

		double h0 = window.getHeight();
		if (always || (h < h0))
			h = h0;
	}

	//------------------------------------------------------------------

	public void setMin(
		double	minWidth,
		double	minHeight)
	{
		window.setMinWidth(Math.max(minWidth, w));
		window.setMinHeight(Math.max(minHeight, h));
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
