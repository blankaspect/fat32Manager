/*====================================================================*\

SimpleProgressBar.java

Class: simple progress bar.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.progress;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;

import javafx.geometry.Insets;

import javafx.scene.Group;
import javafx.scene.Node;

import javafx.scene.layout.Region;

import javafx.scene.paint.Color;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import javafx.util.Duration;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: SIMPLE PROGRESS BAR


/**
 * This class implements a simple progress bar.  When the progress bar is no longer used, it should be disabled to stop
 * the timer for the indeterminate-progress indicator.
 */

public class SimpleProgressBar
	extends Region
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The width of the border of a progress bar. */
	private static final	double	BORDER_WIDTH	= 1.0;

	/** The default width of the frame of a progress bar. */
	private static final	double	DEFAULT_FRAME_WIDTH		= 400.0;

	/** The default height of the frame of a progress bar. */
	private static final	double	DEFAULT_FRAME_HEIGHT	= 12.0;

	/** The increment of the offset of the pattern of the indicator of indeterminate progress. */
	private static final	double	PATTERN_OFFSET_INCREMENT	= 0.5;

	/** The opacity of a disabled progress bar. */
	private static final	double	DISABLED_OPACITY	= 0.4;

	/** The interval (in milliseconds) between successive updates of the indeterminate-progress indicator. */
	private static final	double	BAR_UPDATE_INTERVAL	= 40.0;

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.SIMPLE_PROGRESS_BAR)
					.desc(StyleClass.FRAME)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.BORDER,
			CssSelector.builder()
					.cls(StyleClass.SIMPLE_PROGRESS_BAR)
					.desc(StyleClass.FRAME)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.INDICATOR,
			CssSelector.builder()
					.cls(StyleClass.SIMPLE_PROGRESS_BAR)
					.desc(StyleClass.INDICATOR)
					.build()
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	FRAME				= StyleConstants.CLASS_PREFIX + "frame";
		String	INDICATOR			= StyleConstants.CLASS_PREFIX + "indicator";
		String	SIMPLE_PROGRESS_BAR	= StyleConstants.CLASS_PREFIX + "simple-progress-bar";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	BACKGROUND	= PREFIX + "background";
		String	BORDER		= PREFIX + "border";
		String	INDICATOR	= PREFIX + "indicator";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The background colour of this progress bar. */
	private	Color					backgroundColour;

	/** The border colour of this progress bar. */
	private	Color					borderColour;

	/** The colour of the indicator of this progress bar. */
	private	Color					indicatorColour;

	/** The offset of the pattern of the indicator of indeterminate progress. */
	private	double					patternOffset;

	/** The progress value of this progress bar. */
	private	SimpleDoubleProperty	progress;

	/** The listener that is set on {@link #progress}. */
	private	ChangeListener<Number>	progressListener;

	/** The frame of the progress bar. */
	private	Rectangle				frame;

	/** The indicator of the progress bar. */
	private	Rectangle				indicator;

	/** The container for the shapes that form the indicator of indeterminate progress. */
	private	Group					indicatorShapes;

	/** The pool of polygons that may be used for the indicator of indeterminate progress. */
	private	Deque<Polygon>			polygonPool;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(SimpleProgressBar.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a simple progress bar.
	 */

	public SimpleProgressBar()
	{
		// Initialise instance variables
		backgroundColour = getColour(ColourKey.BACKGROUND);
		borderColour = getColour(ColourKey.BORDER);
		indicatorColour = getColour(ColourKey.INDICATOR);
		progress = new SimpleDoubleProperty(0.0);

		// Set properties
		getStyleClass().add(StyleClass.SIMPLE_PROGRESS_BAR);

		// Create frame
		frame = new Rectangle(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
		frame.setFill(backgroundColour);
		frame.setStroke(borderColour);
		frame.setStrokeWidth(BORDER_WIDTH);
		frame.setStrokeType(StrokeType.INSIDE);
		frame.getStyleClass().add(StyleClass.FRAME);

		// Create indicator
		indicator = new Rectangle();
		indicator.setFill(indicatorColour);
		indicator.setMouseTransparent(true);
		indicator.getStyleClass().add(StyleClass.INDICATOR);

		// Create container for shapes that form indicator of indeterminate progress
		indicatorShapes = new Group();
		indicatorShapes.setVisible(false);

		// Add children to this container
		getChildren().setAll(frame, indicator, indicatorShapes);

		// Create timer that animates indicator of indeterminate progress
		Timeline indeterminateProgressTimer =
				new Timeline(new KeyFrame(Duration.millis(BAR_UPDATE_INTERVAL), event -> updateIndicator()));
		indeterminateProgressTimer.setCycleCount(Animation.INDEFINITE);

		// Create listener that starts and stops animation of indicator of indeterminate progress
		progressListener = (observable, oldProgress, newProgress) ->
		{
			// If indeterminate progress, start animation of indicator ...
			double progress = newProgress.doubleValue();
			if (progress < 0.0)
			{
				if (!isDisabled())
					indeterminateProgressTimer.play();
			}

			// ... otherwise, update progress bar
			else
			{
				// Stop animation of indeterminate progress
				if (indeterminateProgressTimer.getStatus() == Animation.Status.RUNNING)
					indeterminateProgressTimer.stop();

				// Redraw indicator
				updateIndicator();
			}
		};

		// Update bar when progress changes
		progress.addListener(new WeakChangeListener<>(progressListener));

		// Reduce opacity of progress bar if disabled
		disabledProperty().addListener((observable, oldDisabled, disabled) ->
		{
			// Stop animation of indeterminate-progress indicator
			if (disabled)
				indeterminateProgressTimer.stop();

			// Update opacity of progress bar
			setOpacity(disabled ? DISABLED_OPACITY : 1.0);
		});
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the colour that is associated with the specified key in the colour map of the current theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the current theme of the style
	 *         manager, or {@link StyleManager#DEFAULT_COLOUR} if there is no such colour.
	 */

	private static Color getColour(
		String	key)
	{
		return StyleManager.INSTANCE.getColourOrDefault(key);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected double computeMinHeight(
		double	width)
	{
		Insets insets = getInsets();
		return snapSizeY(snapSpaceY(insets.getTop()) + snapSpaceY(insets.getBottom()) + frame.getHeight());
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected void layoutChildren()
	{
		// Get the insets of the progress bar
		Insets insets = getInsets();
		double top = snapSpaceY(insets.getTop());
		double bottom = snapSpaceY(insets.getBottom());
		double left = snapSpaceX(insets.getLeft());
		double right = snapSpaceX(insets.getRight());

		// Calculate the available width
		double width = snapSizeX(getWidth() - left - right);

		// Calculate the available height
		double height = snapSizeY(getHeight() - top - bottom);

		// Set the dimensions of the frame
		frame.setWidth(width);
		frame.setHeight(height);

		// Set the location of the frame
		double x = left;
		double y = top;
		frame.relocate(x, y);

		// Set the height of the indicator
		indicator.setHeight(indicatorHeight());

		// Set the location of the indicator
		x += BORDER_WIDTH;
		y += BORDER_WIDTH;
		indicator.relocate(x, y);

		// Set the location of the container for the shapes of the indeterminate-progress indicator
		indicatorShapes.relocate(x, y);

		// Update the indicator
		updateIndicator();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the value of the progress of this progress bar.
	 *
	 * @return the value of the progress of this progress bar.
	 */

	public double progress()
	{
		return progress.get();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the progress of this progress bar to the specified value.  If the progress is indeterminate, the value
	 * should be negative; otherwise, the value should be between 0 and 1 inclusive.
	 *
	 * @param  progress
	 *           the value to which the progress of this progress bar will be set.
	 * @return this progress bar.
	 */

	public SimpleProgressBar progress(
		double	progress)
	{
		// Update instance variable
		this.progress.set(progress);

		// Return this progress bar
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the progress value of this progress bar as a property.
	 *
	 * @return the progress value of this progress bar as a property.
	 */

	public DoubleProperty progressProperty()
	{
		return progress;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the content of this progress bar (ie, the bar itself, without any padding).
	 *
	 * @return the content of this progress bar (ie, the bar itself, without any padding).
	 */

	public Rectangle content()
	{
		return frame;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background colour of this progress bar to the specified value.
	 *
	 * @param  colour
	 *           the value to which the background colour of this progress bar will be set.  If it is {@code null}, the
	 *           default background colour will be used.
	 * @return this progress bar.
	 */

	public SimpleProgressBar backgroundColour(
		Color	colour)
	{
		// Replace null colour with default colour
		if (colour == null)
			colour = getColour(ColourKey.BACKGROUND);

		// Update instance variable and redraw progress bar
		if (!colour.equals(backgroundColour))
		{
			// Update instance variable
			backgroundColour = colour;

			// Update frame
			frame.setFill(colour);
		}

		// Return this progress bar
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the border colour of this progress bar to the specified value.
	 *
	 * @param  colour
	 *           the value to which the border colour of this progress bar will be set.  If it is {@code null}, the
	 *           default border colour will be used.
	 * @return this progress bar.
	 */

	public SimpleProgressBar borderColour(
		Color	colour)
	{
		// Replace null colour with default colour
		if (colour == null)
			colour = getColour(ColourKey.BORDER);

		// Update instance variable and redraw progress bar
		if (!colour.equals(borderColour))
		{
			// Update instance variable
			borderColour = colour;

			// Update frame
			frame.setStroke(colour);
		}

		// Return this progress bar
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the colour of the indicator of this progress bar to the specified value.
	 *
	 * @param  colour
	 *           the value to which the colour of the indicator of this progress bar will be set.  If it is {@code
	 *           null}, the default indicator colour will be used.
	 * @return this progress bar.
	 */

	public SimpleProgressBar indicatorColour(
		Color	colour)
	{
		// Replace null colour with default colour
		if (colour == null)
			colour = getColour(ColourKey.INDICATOR);

		// Update instance variable and redraw progress bar
		if (!colour.equals(indicatorColour))
		{
			// Update instance variable
			indicatorColour = colour;

			// Redraw indicator
			updateIndicator();
		}

		// Return this progress bar
		return this;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the width of the indicator of this progress bar.
	 *
	 * @return the width of the indicator of this progress bar.
	 */

	private double indicatorWidth()
	{
		return Math.max(0.0, frame.getWidth() - 2.0 * BORDER_WIDTH);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the height of the indicator of this progress bar.
	 *
	 * @return the height of the indicator of this progress bar.
	 */

	private double indicatorHeight()
	{
		return Math.max(0.0, frame.getHeight() - 2.0 * BORDER_WIDTH);
	}

	//------------------------------------------------------------------

	/**
	 * Draws the indicator of this progress bar.
	 */

	private void updateIndicator()
	{
		double height = indicator.getHeight();
		if (height > 0.0)
		{
			// Get value of progress
			double progress = progress();

			// Case: indeterminate progress
			if (progress < 0.0)
			{
				indicator.setVisible(false);
				updateIndicatorShapes();
				indicatorShapes.setVisible(true);
				patternOffset = (patternOffset + PATTERN_OFFSET_INCREMENT) % (2.0 * height);
			}

			// Case: determinate progress
			else
			{
				indicatorShapes.setVisible(false);
				indicator.setWidth(Math.min(Math.max(0.0, progress), 1.0) * indicatorWidth());
				indicator.setFill(indicatorColour);
				indicator.setVisible(true);
			}
		}
	}

	//------------------------------------------------------------------

	/**
	 * Creates the shapes that form the indicator of indeterminate progress.
	 */

	private void updateIndicatorShapes()
	{
		// Remove all indicator shapes
		List<Node> children = new ArrayList<>(indicatorShapes.getChildren());
		indicatorShapes.getChildren().clear();

		// Add polygons to pool
		if (polygonPool == null)
			polygonPool = new ArrayDeque<>();
		for (Node child : children)
		{
			if (child instanceof Polygon polygon)
			{
				polygon.getPoints().clear();
				polygonPool.addLast(polygon);
			}
		}

		// Get width and height of indicator
		double w = indicatorWidth();
		double h = indicatorHeight();

		// Get width of pattern
		double patternWidth = 2.0 * h;

		// Initialise array of vertices: four vertices for unclipped polygon and four vertices for clipped polygon
		Vertex[] vertices = new Vertex[8];

		// Initialise x coordinate of shape
		double x = patternOffset;
		if (x > 0.0)
			x -= patternWidth;

		// Create shapes
		while (x < w)
		{
			// Initialise x coordinates of unclipped polygon
			double x0 = x;
			double x1 = x + h;
			double x2 = x + patternWidth;

			// Initialise vertices of unclipped polygon
			vertices[0] = Vertex.of(x0, h);
			vertices[1] = null;
			vertices[2] = Vertex.of(x1, 0.0);
			vertices[3] = null;
			vertices[4] = Vertex.of(x2, 0.0);
			vertices[5] = null;
			vertices[6] = Vertex.of(x1, h);
			vertices[7] = null;

			// Initialise vertices of polygon whose left part is clipped
			if (x1 < 0.0)
			{
				vertices[0] = null;
				vertices[2] = null;
				vertices[3] = Vertex.of(0.0, 0.0);
				vertices[5] = Vertex.of(0.0, x2);
				vertices[6] = null;

			}
			else if (x0 < 0.0)
			{
				vertices[0] = null;
				vertices[1] = Vertex.of(0.0, x1);
				vertices[7] = Vertex.of(0.0, h);
			}

			// Initialise vertices of polygon whose right part is clipped
			if (x1 > w)
			{
				vertices[1] = Vertex.of(w, x1 - w);
				vertices[2] = null;
				vertices[4] = null;
				vertices[6] = null;
				vertices[7] = Vertex.of(w, h);
			}
			else if (x2 > w)
			{
				vertices[3] = Vertex.of(w, 0.0);
				vertices[4] = null;
				vertices[5] = Vertex.of(w, x2 - w);
			}

			// Get polygon from pool; create new polygon if pool is empty
			Polygon polygon = polygonPool.pollLast();
			if (polygon == null)
			{
				polygon = new Polygon();
				polygon.setMouseTransparent(true);
			}
			polygon.setFill(indicatorColour);

			// Add vertices to polygon
			for (int i = 0; i < vertices.length; i++)
			{
				Vertex vertex = vertices[i];
				if (vertex != null)
				{
					polygon.getPoints().add(vertex.x);
					polygon.getPoints().add(vertex.y);
				}
			}

			// Add shape to container
			indicatorShapes.getChildren().add(polygon);

			// Increment x coordinate of shape
			x += patternWidth;
		}
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member records
////////////////////////////////////////////////////////////////////////


	// RECORD: VERTEX


	/**
	 * This record encapsulates a vertex of a polygon.
	 *
	 * @param x
	 *          the x coordinate.
	 * @param y
	 *          the y coordinate.
	 */

	private record Vertex(
		double	x,
		double	y)
	{

	////////////////////////////////////////////////////////////////////
	//  Class methods
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates and returns a new instance of a vertex with the specified coordinates.
		 *
		 * @param  x
		 *           the x coordinate.
		 * @param  y
		 *           the y coordinate.
		 * @return a new instance of a vertex.
		 */

		private static Vertex of(
			double	x,
			double	y)
		{
			return new Vertex(x, y);
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
