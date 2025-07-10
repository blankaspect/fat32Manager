/*====================================================================*\

DialogButtonPane.java

Class: dialog button pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.container;

//----------------------------------------------------------------------


// IMPORTS


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Node;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

//----------------------------------------------------------------------


// CLASS: DIALOG BUTTON PANE


/**
 * This class implements a pane in which the buttons of a dialog are arranged horizontally.
 * <p>
 * A button pane is divided horizontally into three zones: left, centre and right.  There is an extra gap between
 * adjacent zones.  When you {@linkplain #addButton(Region, HPos) add a button} to the button pane, you choose the zone
 * in which it will appear.
 * </p>
 * <p>
 * The widths of groups of buttons in a button pane may be equalised by setting the preferred width of each button in
 * the group to the width of the widest button.  A button is a member of a group if it has a property whose key is
 * {@link #BUTTON_GROUP_KEY}.  The group is identified by the value of the property.
 * </p>
 */

public class DialogButtonPane
	extends HBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The key of the <i>group</i> property of a button that is used when equalising button widths. */
	public static final		String	BUTTON_GROUP_KEY	= DialogButtonPane.class.getSimpleName() + ".buttonGroup";

	/** The default gap between buttons. */
	public static final		double	DEFAULT_BUTTON_GAP	= 8.0;

	/** The default padding around a button pane. */
	private static final	Insets	DEFAULT_PADDING	= new Insets(6.0, 10.0, 6.0, 10.0);

	/** The object that is used as the value of the user-data property of a filler between groups of buttons. */
	private static final	Object	FILLER	= new Object();

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The number of buttons in the left zone of this button pane. */
	private	int	numButtonsLeft;

	/** The number of buttons in the central zone of this button pane. */
	private	int	numButtonsCentre;

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a button pane with the default gap between buttons.
	 */

	public DialogButtonPane()
	{
		// Call alternative constructor
		this(DEFAULT_BUTTON_GAP);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a button pane with the specified gap between buttons.
	 *
	 * @param buttonGap
	 *          the gap between the buttons of a button pane.
	 */

	public DialogButtonPane(
		double	buttonGap)
	{
		// Call superclass constructor
		super(buttonGap);

		// Set properties
		setAlignment(Pos.CENTER);
		setPadding(DEFAULT_PADDING);
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Adds the specified button to the specified horizontal zone of this button pane.
	 *
	 * @param button
	 *          the button that will be added to this button pane.
	 * @param position
	 *          the horizontal zone in which the button will appear.
	 */

	public void addButton(
		Region	button,
		HPos	position)
	{
		// Add button to button pane
		switch (position)
		{
			case LEFT:
				getChildren().add(numButtonsLeft++, button);
				break;

			case CENTER:
				getChildren().add(numButtonsLeft + numButtonsCentre++, button);
				break;

			case RIGHT:
				getChildren().add(button);
				break;
		}
	}

	//------------------------------------------------------------------

	/**
	 * Adds horizontal fillers to the child list of this button pane to separate the groups of buttons.
	 */

	public void updateButtonSpacing()
	{
		// Remove current fillers
		Iterator<Node> it = getChildren().iterator();
		while (it.hasNext())
		{
			if (it.next().getUserData() == FILLER)
				it.remove();
		}

		// Add filler
		addFiller(numButtonsLeft);
		if (numButtonsCentre > 0)
			addFiller(numButtonsLeft + numButtonsCentre + 1);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the total increase in the the widths of all the buttons in this button pane as a result of equalising
	 * their widths.  The widths of the members of groups of buttons in this button pane may optionally be equalised by
	 * setting the preferred width of each button in the group to the width of the widest button.  A button group is
	 * denoted by a property with the key {@link #BUTTON_GROUP_KEY}.
	 *
	 * @param  apply
	 *           if {@code true}, the widths of the members of groups of buttons in this button pane will be equalised.
	 * @return the total increase in the the widths of all the buttons in this button pane as a result of equalising
	 *         their widths.
	 */

	public double equaliseButtonWidths(
		boolean	apply)
	{
		// Create list of fillers
		List<Region> fillers = new ArrayList<>();
		for (Node child : getChildren())
		{
			if ((child instanceof Region region) && (region.getUserData() == FILLER))
				fillers.add(region);
		}

		// Create map of groups of buttons
		Map<Object, List<Region>> groups = new HashMap<>();
		for (Node child : getChildren())
		{
			if (child instanceof Region region)
			{
				Object groupKey = child.getProperties().get(BUTTON_GROUP_KEY);
				if (groupKey != null)
				{
					List<Region> buttons = groups.get(groupKey);
					if (buttons == null)
					{
						buttons = new ArrayList<>();
						groups.put(groupKey, buttons);
					}
					buttons.add(region);
				}
			}
		}

		// Initialise extra width of buttons
		double extraWidth = 0.0;

		// For each group of buttons, set width of each button to width of widest button
		for (Object groupKey : groups.keySet())
		{
			// Get group of buttons
			List<Region> buttons = groups.get(groupKey);

			// Process group if it contains more than one button
			if (buttons.size() > 1)
			{
				// Find width of widest button
				double maxWidth = 0.0;
				for (Region button : buttons)
				{
					double width = button.getWidth();
					if (maxWidth < width)
						maxWidth = width;
				}

				// Set width of each button to width of widest button
				for (Region button : buttons)
				{
					if (apply)
						button.setPrefWidth(maxWidth);
					extraWidth += maxWidth - button.getWidth();
				}
			}
		}

		// Reduce extra button width by width of fillers
		for (Region filler : fillers)
			extraWidth -= filler.getWidth();

		// Return extra width of buttons
		return extraWidth;
	}

	//------------------------------------------------------------------

	/**
	 * Adds a horizontal filler to the child list of this button pane at the specified index.
	 *
	 * @param index
	 *          the index at which the filler will be added to the child list of this button pane.
	 */

	private void addFiller(
		int	index)
	{
		Region filler = new Region();
		filler.setUserData(FILLER);
		HBox.setHgrow(filler, Priority.ALWAYS);
		getChildren().add(index, filler);
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
