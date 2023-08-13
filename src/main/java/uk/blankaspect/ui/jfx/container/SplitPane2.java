/*====================================================================*\

SplitPane2.java

Class: split pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.container;

//----------------------------------------------------------------------


// IMPORTS


import javafx.application.Platform;

import javafx.beans.property.ReadOnlyProperty;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;

import javafx.collections.ListChangeListener;

import javafx.scene.Node;

import javafx.scene.control.SplitPane;

import uk.blankaspect.ui.jfx.style.StyleUtils;

//----------------------------------------------------------------------


// CLASS: SPLIT PANE


/**
 * This class extends {@link SplitPane} by preserving the divider positions when the layout bounds of the pane change.
 * <p>
 * For correct behaviour, divider positions must be set only with the methods {@link #setDividerPosition(int, double)}
 * and {@link #setDividerPositions(double...)}.
 * </p>
 */

public class SplitPane2
	extends SplitPane
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The style class of this class. */
	private static final	String	STYLE_CLASS	= StyleUtils.getStyleClass(SplitPane2.class);

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a split pane with no content.
	 */

	public SplitPane2()
	{
		// Add style class
		getStyleClass().add(STYLE_CLASS);

		// Update cached divider position when position of divider changes
		dividerPositionChangeListener = (observable, oldPosition, position) ->
		{
			if (!ignoreDividerPositionChange)
			{
				int index = getDividers().indexOf(((ReadOnlyProperty<?>)observable).getBean());
				if ((index >= 0) && (index < dividerPositions.length))
					dividerPositions[index] = position.doubleValue();
			}
		};

		// Manage listeners on position properties of dividers
		getDividers().addListener((ListChangeListener<Divider>) change ->
		{
			// Update cached divider positions
			updateDividerPositions();

			// Add and remove listeners
			while (change.next())
			{
				// Remove listener from each divider that was removed
				if (change.wasRemoved())
				{
					for (Divider divider : change.getRemoved())
						divider.positionProperty()
											.removeListener(new WeakChangeListener<>(dividerPositionChangeListener));
				}

				// Add listener to each divider that was added
				if (change.wasAdded())
				{
					for (Divider divider : change.getAddedSubList())
						divider.positionProperty()
											.addListener(new WeakChangeListener<>(dividerPositionChangeListener));
				}
			}
		});

		// Initialise cached divider positions
		updateDividerPositions();

		// Set divider positions from cached values when bounds of split pane change
		layoutBoundsProperty().addListener((observable, oldBounds, bounds) ->
		{
			Platform.runLater(() ->
			{
				ignoreDividerPositionChange = true;
				super.setDividerPositions(dividerPositions);
				super.layoutChildren();
				ignoreDividerPositionChange = false;
			});
		});
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a split pane with the specified items as its content, separated by dividers.
	 *
	 * @param items  the items that will be the content of the split pane.
	 */

	public SplitPane2(Node... items)
	{
		// Initialise split pane
		this();

		// Add items
		getItems().addAll(items);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods : overriding methods
////////////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void setDividerPosition(int    dividerIndex,
								   double position)
	{
		// Call superclass method
		super.setDividerPosition(dividerIndex, position);

		// Update cached divider positions
		updateDividerPositions();
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	public void setDividerPositions(double... positions)
	{
		// Call superclass method
		super.setDividerPositions(positions);

		// Update cached divider positions
		updateDividerPositions();
	}

	//------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */

	@Override
	protected void layoutChildren()
	{
		// Call superclass method, preventing cached divider positions from being updated
		ignoreDividerPositionChange = true;
		super.layoutChildren();
		ignoreDividerPositionChange = false;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Updates the cached divider positions with the current divider positions.
	 */

	private void updateDividerPositions()
	{
		dividerPositions = getDividerPositions().clone();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** Cached values of the divider positions. */
	private	double[]				dividerPositions;

	/** The listener that updates the {@linkplain #dividerPositions} cached divider positions} when a divider is
		moved. */
	private	ChangeListener<Number>	dividerPositionChangeListener;

	/** Flag: if {@code true}, {@link #dividerPositionChangeListener} ignores a change to a divider position. */
	private	boolean					ignoreDividerPositionChange;

}

//----------------------------------------------------------------------
