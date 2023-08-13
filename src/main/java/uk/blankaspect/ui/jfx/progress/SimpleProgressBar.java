/*====================================================================*\

SimpleProgressBar.java

Class: simple progress bar.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.progress;

//----------------------------------------------------------------------


// IMPORTS


import java.nio.IntBuffer;

import java.util.List;

import javafx.animation.AnimationTimer;

import javafx.application.Platform;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import javafx.geometry.Insets;

import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javafx.scene.layout.Region;

import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

//----------------------------------------------------------------------


// CLASS: SIMPLE PROGRESS BAR


/**
 * This class implements a simple progress bar.
 */

public class SimpleProgressBar
	extends Region
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The width of the border of a progress bar. */
	private static final	double	BORDER_WIDTH	= 1.0;

	/** The minimum width of a progress bar. */
	private static final	double	MIN_WIDTH	= 2.0 * BORDER_WIDTH + 2.0;

	/** The minimum height of a progress bar. */
	private static final	double	MIN_HEIGHT	= 2.0 * BORDER_WIDTH + 2.0;

	/** The increment of the offset of the pattern of the indicator of indeterminate progress. */
	private static final	double	PATTERN_OFFSET_INCREMENT	= 0.75;

	/** The maximum value of an ARGB component of a colour. */
	private static final	double	MAX_ARGB_COMPONENT_VALUE	= 255.0;

	/** The opacity of a disabled progress bar. */
	private static final	double	DISABLED_OPACITY	= 0.4;

	/** The interval (in nanoseconds) between successive updates of the progress bar. */
	private static final	long	BAR_UPDATE_INTERVAL	= 25_000_000;

	/** The default margins of the bar of a progress bar. */
	private static final	Insets	DEFAULT_BAR_MARGINS	= Insets.EMPTY;

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.FRAME_BACKGROUND,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_PROGRESS_BAR)
						.desc(StyleClass.FRAME)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.FRAME_BORDER,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_PROGRESS_BAR)
						.desc(StyleClass.FRAME)
						.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.BAR,
			CssSelector.builder()
						.cls(StyleClass.SIMPLE_PROGRESS_BAR)
						.desc(StyleClass.BAR)
						.build()
		)
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	BAR					= StyleConstants.CLASS_PREFIX + "bar";
		String	FRAME				= StyleConstants.CLASS_PREFIX + "frame";
		String	SIMPLE_PROGRESS_BAR	= StyleConstants.CLASS_PREFIX + "simple-progress-bar";
	}

	/** Keys of colours that are used in colour properties. */
	public interface ColourKey
	{
		String	BAR					= "simpleProgressBar.bar";
		String	FRAME_BACKGROUND	= "simpleProgressBar.frame.background";
		String	FRAME_BORDER		= "simpleProgressBar.frame.border";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The width of this progress bar. */
	private	double					width;

	/** The height of this progress bar. */
	private	double					height;

	/** The colour of the bar of this progress bar. */
	private	Color					barColour;

	/** The margins of the bar of this progress bar. */
	private	Insets					barMargins;

	/** The height of the progress bar. */
	private	double					barHeight;

	/** The offset of the pattern of the indicator of indeterminate progress. */
	private	double					patternOffset;

	/** The progress value of this progress bar. */
	private	SimpleDoubleProperty	progress;

	/** The timer that animates the pattern of the indicator of indeterminate progress. */
	private	AnimationTimer			indeterminateTimer;

	/** The image of the pattern of the indicator of indeterminate progress. */
	private	WritableImage			image;

	/** The frame of the progress bar. */
	private	Rectangle				frame;

	/** The bar of the progress bar. */
	private	Rectangle				bar;

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
	 * Creates a new instance of a simple progress bar with the specified width and height.
	 *
	 * @param  width
	 *           the width of the progress bar.
	 * @param  height
	 *           the height of the progress bar.
	 * @throws IllegalArgumentException
	 *           if {@code width} is less than 4 or {@code height} is less than 4.
	 */

	public SimpleProgressBar(
		double	width,
		double	height)
	{
		// Validate arguments
		if (width < MIN_WIDTH)
			throw new IllegalArgumentException("Width must be not be less than " + MIN_WIDTH);
		if (height < MIN_HEIGHT)
			throw new IllegalArgumentException("Height must be not be less than " + MIN_HEIGHT);

		// Initialise instance variables
		this.width = width;
		this.height = height;
		barColour = getColour(ColourKey.BAR);
		barMargins = DEFAULT_BAR_MARGINS;
		progress = new SimpleDoubleProperty(0.0);

		// Set properties
		getStyleClass().add(StyleClass.SIMPLE_PROGRESS_BAR);

		// Create frame
		frame = new Rectangle(width, height);
		frame.setStrokeWidth(BORDER_WIDTH);
		frame.setStrokeType(StrokeType.INSIDE);
		frame.setFill(getColour(ColourKey.FRAME_BACKGROUND));
		frame.setStroke(getColour(ColourKey.FRAME_BORDER));
		frame.getStyleClass().add(StyleClass.FRAME);

		// Create bar
		bar = new Rectangle();
		bar.setFill(getColour(ColourKey.BAR));
		bar.setMouseTransparent(true);
		bar.getStyleClass().add(StyleClass.BAR);

		// Add children to this group
		getChildren().setAll(frame, bar);

		// Create timer that animates indicator of indeterminate progress
		indeterminateTimer = new AnimationTimer()
		{
			long	updateTime;

			@Override
			public void start()
			{
				// Reset update time
				updateTime = 0;

				// Call superclass method
				super.start();
			}

			@Override
			public void handle(long time)
			{
				// Initialise update time
				if (updateTime == 0)
					updateTime = time;

				// Update progress bar
				while (updateTime <= time)
				{
					updateBar();
					updateTime += BAR_UPDATE_INTERVAL;
				}
			}
		};

		// Update bar when progress changes
		progress.addListener((observable, oldProgress, newProgress) ->
		{
			// If indeterminate progress, start animation of indicator ...
			double progress = newProgress.doubleValue();
			if (progress < 0.0)
				indeterminateTimer.start();

			// ... otherwise, update progress bar
			else
			{
				// Stop animation of indeterminate progress
				indeterminateTimer.stop();

				// Update progress bar
				Platform.runLater(() -> updateBar());
			}
		});

		// Reduce opacity of progress bar if disabled
		disabledProperty().addListener((observable, oldDisabled, disabled) ->
				setOpacity(disabled ? DISABLED_OPACITY : 1.0));
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the colour that is associated with the specified key in the colour map of the selected theme of the
	 * {@linkplain StyleManager style manager}.
	 *
	 * @param  key
	 *           the key of the desired colour.
	 * @return the colour that is associated with {@code key} in the colour map of the selected theme of the style
	 *         manager, or {@link StyleManager#DEFAULT_COLOUR} if there is no such colour.
	 */

	private static Color getColour(
		String	key)
	{
		Color colour = StyleManager.INSTANCE.getColour(key);
		return (colour == null) ? StyleManager.DEFAULT_COLOUR : colour;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected double computeMinWidth(
		double	height)
	{
		Insets insets = getInsets();
		return width + insets.getLeft() + insets.getRight();
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected double computeMinHeight(
		double	width)
	{
		Insets insets = getInsets();
		return height + insets.getTop() + insets.getBottom();
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
		double top = insets.getTop();
		double bottom = insets.getBottom();
		double left = insets.getLeft();
		double right = insets.getRight();

		// Get the preferred width
		double prefWidth = getPrefWidth();
		if (prefWidth != USE_PREF_SIZE)
		{
			if (prefWidth < 0.0)
				prefWidth = Math.max(0.0, width - left - right);
		}

		// Get the preferred height
		double prefHeight = getPrefHeight();
		if (prefHeight != USE_PREF_SIZE)
		{
			if (prefHeight < 0.0)
				prefHeight = Math.max(0.0, height - top - bottom);
		}

		// Get the available width
		double width = Math.max(0.0, getWidth() - left - right);

		// Apply the minimum width
		double minWidth = getMinWidth();
		if (minWidth == USE_PREF_SIZE)
			minWidth = prefWidth;
		if (minWidth >= 0.0)
			width = Math.max(minWidth, width);

		// Apply the maximum width
		double maxWidth = getMaxWidth();
		if (maxWidth == USE_PREF_SIZE)
			maxWidth = prefWidth;
		else if (maxWidth < 0.0)
			maxWidth = Double.MAX_VALUE;
		width = Math.min(width, maxWidth);

		// Get the available height
		double height = Math.max(0.0, getHeight() - top - bottom);

		// Apply the minimum height
		double minHeight = getMinHeight();
		if (minHeight == USE_PREF_SIZE)
			minHeight = prefHeight;
		if (minHeight >= 0.0)
			height = Math.max(minHeight, height);

		// Apply the maximum height
		double maxHeight = getMaxHeight();
		if (maxHeight == USE_PREF_SIZE)
			maxHeight = prefHeight;
		else if (maxHeight < 0.0)
			maxHeight = Double.MAX_VALUE;
		height = Math.min(height, maxHeight);

		// Set the dimensions of the frame
		frame.setWidth(width);
		frame.setHeight(height);

		// Calculate the x coordinate of the frame
		double hInsets = left + right;
		double x = Math.min(((hInsets == 0.0) ? 0.5 : left / hInsets) * (getWidth() - width), left);

		// Calculate the y coordinate of the frame
		double vInsets = top + bottom;
		double y = Math.min(((vInsets == 0.0) ? 0.5 : top / vInsets) * (getHeight() - height), top);

		// Set the location of the frame
		frame.relocate(x, y);

		// Set the height and location of the bar
		double barX = BORDER_WIDTH + barMargins.getLeft();
		double barY = BORDER_WIDTH + barMargins.getTop();
		bar.setHeight(Math.max(0.0, height - barY - BORDER_WIDTH - barMargins.getBottom()));
		bar.relocate(x + barX, y + barY);

		// Update the image of the pattern of the indicator of indeterminate progress
		if (barHeight != bar.getHeight())
		{
			barHeight = bar.getHeight();
			updateImage();
		}

		// Update the bar
		updateBar();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the progress value of this progress bar.
	 *
	 * @return the progress value of this progress bar.
	 */

	public double getProgress()
	{
		return progress.get();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the progress of this progress bar to the specified value.  If the progress is indeterminate, the value
	 * should be negative; otherwise, the value should be between 0 and 1 inclusive.
	 *
	 * @param progress
	 *          the value to which the progress of this progress bar will be set.
	 */

	public void setProgress(
		double	progress)
	{
		this.progress.set(progress);
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

	public Rectangle getContent()
	{
		return frame;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the background colour of this progress bar to the specified value.
	 *
	 * @param colour
	 *          the value to which the background colour of this progress bar will be set.
	 */

	public void setBackgroundColour(
		Color	colour)
	{
		// Set fill colour of frame
		frame.setFill(colour);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the border colour of this progress bar to the specified value.
	 *
	 * @param colour
	 *          the value to which the border colour of this progress bar will be set.
	 */

	public void setBorderColour(
		Color	colour)
	{
		// Set stroke colour of frame
		frame.setStroke(colour);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the bar colour of this progress bar to the specified value.
	 *
	 * @param colour
	 *          the value to which the bar colour of this progress bar will be set.
	 */

	public void setBarColour(
		Color	colour)
	{
		// Validate arguments
		if (colour == null)
			throw new IllegalArgumentException("Null colour");

		// Set bar colour
		if (!colour.equals(barColour))
		{
			// Update instance variable
			barColour = colour;

			// Update image of pattern of indicator of indeterminate progress
			updateImage();

			// Update bar
			updateBar();
		}
	}

	//------------------------------------------------------------------

	/**
	 * Sets the margins of this progress bar to the specified value.
	 *
	 * @param margins
	 *          the value to which the margins of this progress bar will be set.
	 */

	public void setBarMargins(
		Insets	margins)
	{
		// Validate arguments
		if (margins == null)
			throw new IllegalArgumentException("Null margins");

		// Set bar margins
		if (!margins.equals(barMargins))
		{
			// Update instance variable
			barMargins = margins;

			// Redraw control
			requestLayout();
		}
	}

	//------------------------------------------------------------------

	/**
	 * Draws the bar of the progress bar.
	 */

	private void updateBar()
	{
		double barHeight = bar.getHeight();
		if (barHeight > 0.0)
		{
			double progress = getProgress();
			double barWidth = Math.max(0.0, frame.getWidth() - 2.0 * BORDER_WIDTH - barMargins.getLeft()
																							- barMargins.getRight());
			if ((progress < 0.0) && (image != null))
			{
				bar.setWidth(barWidth);
				double imageWidth = 2.0 * barHeight;
				bar.setFill(new ImagePattern(image, patternOffset, 0.0, imageWidth, barHeight, false));
				patternOffset = (patternOffset + PATTERN_OFFSET_INCREMENT) % imageWidth;
			}
			else
			{
				bar.setWidth(Math.min(Math.max(0.0, progress), 1.0) * barWidth);
				bar.setFill(barColour);
			}
		}
	}

	//------------------------------------------------------------------

	/**
	 * Updates the image of the pattern of the indicator of indeterminate progress.
	 */

	private void updateImage()
	{
		double barHeight = bar.getHeight();
		if (barHeight > 0.0)
		{
			// Get ARGB values of bar colour
			double red = barColour.getRed() * MAX_ARGB_COMPONENT_VALUE;
			double green = barColour.getGreen() * MAX_ARGB_COMPONENT_VALUE;
			double blue = barColour.getGreen() * MAX_ARGB_COMPONENT_VALUE;
			int argb0 = 128 << 24
							| (int)Math.round(0.5 * red) << 16
							| (int)Math.round(0.5 * green) << 8
							| (int)Math.round(0.5 * blue);
			int argb1 = 255 << 24
							| (int)Math.round(red) << 16
							| (int)Math.round(green) << 8
							| (int)Math.round(blue);

			// Fill buffer with ARGB values
			int height = (int)Math.ceil(barHeight);
			int[] buffer = new int[height + 1];
			int i = 0;
			buffer[i++] = argb0;
			while (i < height)
				buffer[i++] = argb1;
			buffer[i++] = argb0;

			// Create image
			int width = 2 * height;
			image = new WritableImage(width, height);

			// Set pixels of image
			PixelFormat<IntBuffer> format = PixelFormat.getIntArgbPreInstance();
			PixelWriter writer = image.getPixelWriter();
			int x = width - buffer.length;
			for (int y = 0; y < height; y++)
				writer.setPixels(x--, y, buffer.length, 1, format, buffer, 0, 0);
		}
		else
			image = null;
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
