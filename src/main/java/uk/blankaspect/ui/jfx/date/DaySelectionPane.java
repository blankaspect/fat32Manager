/*====================================================================*\

DaySelectionPane.java

Class: day-selection pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.date;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javafx.css.PseudoClass;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.Node;

import javafx.scene.control.Label;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import javafx.scene.paint.Color;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.date.DateUtils;

import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.ui.jfx.popup.LabelPopUpManager;
import uk.blankaspect.ui.jfx.popup.PopUpUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: DAY-SELECTION PANE


/**
 * This class implements a JavaFX pane in which the days of a calendar month are displayed, and from which a day may be
 * selected.
 */

public class DaySelectionPane
	extends GridPane
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The minimum index of a day of a month. */
	private static final	int		MIN_DAY	= 0;

	/** The minimum number of days in a month. */
	private static final	int		MIN_NUM_DAYS	= 28;

	/** The maximum number of days in a month. */
	private static final	int		MAX_NUM_DAYS	= 31;

	/** The minimum length of the name of a day of the week. */
	private static final	int		MIN_DAY_NAME_LENGTH	= 2;

	/** The number of days in a week. */
	private static final	int		NUM_DAYS_IN_WEEK	= 7;

	/** The horizontal padding of a day cell. */
	private static final	double	CELL_H_PADDING	= 2.0;

	/** The vertical padding of a day cell. */
	private static final	double	CELL_V_PADDING	= 1.0;

	/** The padding around a cell of the header. */
	private static final	Insets	HEADER_CELL_PADDING	=
			new Insets(CELL_V_PADDING, CELL_H_PADDING, CELL_V_PADDING, CELL_H_PADDING + 1.0);

	/** The padding around a pop-up window in which the full name of a day of the week is displayed. */
	private static final	Insets	POP_UP_PADDING	= new Insets(1.0, 4.0, 2.0, 4.0);

	/** The delay (in milliseconds) between the triggering of a day-of-the-week pop-up and its display. */
	private static final	int		POP_UP_DELAY	= 100;

	/** The number of columns of the day matrix. */
	private static final	int		NUM_COLUMNS	= NUM_DAYS_IN_WEEK;

	/** The prototype string of the name of a day of the week. */
	private static final	String	PROTOTYPE_DAY_STR	= "0".repeat(MIN_DAY_NAME_LENGTH);

	/** The pseudo-class that is associated with the <i>even</i> state. */
	private static final	PseudoClass	EVEN_PSEUDO_CLASS		= PseudoClass.getPseudoClass(FxPseudoClass.EVEN);

	/** The pseudo-class that is associated with the <i>invalid</i> state. */
	private static final	PseudoClass	INVALID_PSEUDO_CLASS	= PseudoClass.getPseudoClass(PseudoClassKey.INVALID);

	/** The pseudo-class that is associated with the <i>odd</i> state. */
	private static final	PseudoClass	ODD_PSEUDO_CLASS		= PseudoClass.getPseudoClass(FxPseudoClass.ODD);

	/** The pseudo-class that is associated with the <i>selected</i> state. */
	private static final	PseudoClass	SELECTED_PSEUDO_CLASS	= PseudoClass.getPseudoClass(FxPseudoClass.SELECTED);

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.BORDER,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.desc(StyleClass.CELL_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.CELL_TEXT_INVALID,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.desc(StyleClass.CELL_LABEL).pseudo(PseudoClassKey.INVALID)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_EVEN,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.desc(StyleClass.CELL_LABEL).pseudo(FxPseudoClass.EVEN)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_ODD,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.desc(StyleClass.CELL_LABEL).pseudo(FxPseudoClass.ODD)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_SELECTED,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.desc(StyleClass.CELL_LABEL).pseudo(FxPseudoClass.SELECTED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.CELL_BACKGROUND_SELECTED_FOCUSED,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE).pseudo(FxPseudoClass.FOCUSED)
					.desc(StyleClass.CELL_LABEL).pseudo(FxPseudoClass.SELECTED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.CELL_BORDER,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.desc(StyleClass.CELL_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.CELL_BORDER_SELECTED,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.desc(StyleClass.CELL_LABEL).pseudo(FxPseudoClass.SELECTED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.CELL_BORDER_SELECTED_FOCUSED,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE).pseudo(FxPseudoClass.FOCUSED)
					.desc(StyleClass.CELL_LABEL).pseudo(FxPseudoClass.SELECTED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.HEADER_CELL_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.desc(StyleClass.HEADER_CELL_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.HEADER_CELL_BORDER,
			CssSelector.builder()
					.cls(StyleClass.DAY_SELECTION_PANE)
					.desc(StyleClass.HEADER_CELL_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.TEXT_FILL,
			ColourKey.POPUP_TEXT,
			CssSelector.builder()
					.cls(StyleClass.CELL_POPUP_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.POPUP_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.CELL_POPUP_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.POPUP_BORDER,
			CssSelector.builder()
					.cls(StyleClass.CELL_POPUP_LABEL)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.DAY_SELECTION_PANE)
						.desc(StyleClass.HEADER_CELL_LABEL)
						.build())
				.borders(Side.RIGHT, Side.BOTTOM)
				.build()
	);

	/** CSS style classes. */
	public interface StyleClass
	{
		String	DAY_SELECTION_PANE	= StyleConstants.CLASS_PREFIX + "day-selection-pane";

		String	CELL_LABEL			= StyleConstants.CLASS_PREFIX + "cell-label";
		String	CELL_POPUP_LABEL	= DAY_SELECTION_PANE + "-cell-popup-label";
		String	HEADER_CELL_LABEL	= StyleConstants.CLASS_PREFIX + "header-cell-label";
	}

	/** Keys of CSS pseudo-classes. */
	private interface PseudoClassKey
	{
		String	INVALID	= "invalid";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	BORDER								= PREFIX + "border";
		String	CELL_BACKGROUND_EVEN				= PREFIX + "cell.background.even";
		String	CELL_BACKGROUND_ODD					= PREFIX + "cell.background.odd";
		String	CELL_BACKGROUND_SELECTED			= PREFIX + "cell.background.selected";
		String	CELL_BACKGROUND_SELECTED_FOCUSED	= PREFIX + "cell.background.selected.focused";
		String	CELL_BORDER							= PREFIX + "cell.border";
		String	CELL_BORDER_SELECTED				= PREFIX + "cell.border.selected";
		String	CELL_BORDER_SELECTED_FOCUSED		= PREFIX + "cell.border.selected.focused";
		String	CELL_TEXT							= PREFIX + "cell.text";
		String	CELL_TEXT_INVALID					= PREFIX + "cell.text.invalid";
		String	HEADER_CELL_BACKGROUND				= PREFIX + "header.cell.background";
		String	HEADER_CELL_BORDER					= PREFIX + "header.cell.border";
		String	POPUP_TEXT							= PREFIX + "popup.text";
		String	POPUP_BACKGROUND					= PREFIX + "popup.background";
		String	POPUP_BORDER						= PREFIX + "popup.border";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The offset of the first day of the month from the start of the week. */
	private	int				dayOffset;

	/** The number of days in the month. */
	private	int				numDays;

	/** The index of the selected day. */
	private	int				selectedDay;

	/** The number of days in the previous month. */
	private	int				prevMonthNumDays;

	/** The index of the first day of the week. */
	private	int				firstDayOfWeek;

	/** Flag: if {@code true}, the height of the pane can accommodate a month with the maximum number of days. */
	private	boolean			fullHeight;

	/** The number of rows of the matrix of day cells. */
	private	int				numRows;

	/** The names of days of the week. */
	private	List<String>	dayNames;

	/** The length of names of days of the week. */
	private	int				dayNameLength;

	/** The action that is performed when the mouse is double-clicked on a day cell. */
	private	IProcedure0		acceptAction;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(DaySelectionPane.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a day-selection pane with the specified parameters.
	 *
	 * @param  dayOffset
	 *           the offset of the first day of the month from the start of the week.
	 * @param  numDays
	 *           the number of days in the month.
	 * @param  selectedDay
	 *           the index of the selected day.
	 * @param  prevMonthNumDays
	 *           the number of days in the previous month.
	 * @param  firstDayOfWeek
	 *           the index of the first day of the week.
	 * @param  fullHeight
	 *           if {@code true}, the height of the pane will accommodate a month with the maximum number of days.
	 * @throws IllegalArgumentException
	 *           if <i>dayOffset</i>, <i>numDays</i> or <i>selectedDay</i> is out of bounds.
	 */

	public DaySelectionPane(
		int		dayOffset,
		int		numDays,
		int		selectedDay,
		int		prevMonthNumDays,
		int		firstDayOfWeek,
		boolean	fullHeight)
	{
		// Initialise instance variables
		init(dayOffset, numDays, selectedDay, prevMonthNumDays);
		this.firstDayOfWeek = firstDayOfWeek;
		this.fullHeight = fullHeight;

		// Create list of names of days of week
		dayNames = DateUtils.getDayNames(Locale.getDefault());
		int numDayNames = dayNames.size();

		// Get length of names of days of week
		dayNameLength = MIN_DAY_NAME_LENGTH - 1;
		boolean done = false;
		while (!done)
		{
			++dayNameLength;
			for (int i = 0; i < numDayNames - 1; i++)
			{
				String str = getDayString(i, dayNameLength);
				int j = i + 1;
				while (j < numDayNames)
				{
					if (str.equals(getDayString(j, dayNameLength)))
						break;
					++j;
				}
				if (j == numDayNames)
				{
					done = true;
					break;
				}
			}
		}

		// Get width of day numbers
		double dayStrWidth = TextUtils.textWidth(PROTOTYPE_DAY_STR);

		// Get maximum width of day names
		double maxStrWidth = 0.0;
		for (int i = 0; i < numDayNames; i++)
		{
			double strWidth = TextUtils.textWidth(getDayString(i, dayNameLength));
			if (maxStrWidth < strWidth)
				maxStrWidth = strWidth;
		}
		if (maxStrWidth < dayStrWidth)
			maxStrWidth = dayStrWidth;

		// Initialise remaining instance variables
		double columnWidth = Math.ceil(maxStrWidth + (2.0 * CELL_H_PADDING + 1.0) + 1.0);

		// Set properties
		setFocusTraversable(true);
		setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.BORDER)));
		getStyleClass().add(StyleClass.DAY_SELECTION_PANE);

		// Set column constraints
		for (int i = 0; i < NUM_COLUMNS; i++)
			getColumnConstraints().add(new ColumnConstraints(Region.USE_PREF_SIZE, columnWidth, Region.USE_PREF_SIZE));

		// Create header cells and add them to this pane
		for (int i = 0; i < NUM_COLUMNS; i++)
		{
			// Get text of label
			String text = getDayString(i, dayNameLength);

			// Create label
			Label label = new Label(text);

			// Set properties of label
			label.setMaxWidth(Double.MAX_VALUE);
			label.setAlignment(Pos.CENTER);
			label.setPadding(HEADER_CELL_PADDING);
			label.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.HEADER_CELL_BACKGROUND)));
			label.setBorder(SceneUtils.createSolidBorder(
					getColour(ColourKey.HEADER_CELL_BORDER), Side.RIGHT, Side.BOTTOM));
			label.getStyleClass().add(StyleClass.HEADER_CELL_LABEL);

			// Add label to this pane
			add(label, i, 0);

			// Create pop-up manager
			LabelPopUpManager popUpManager = new LabelPopUpManager((text0, graphic) ->
			{
				// Create label
				Label popUpLabel = new Label(text0);

				// Set properties of label
				popUpLabel.setPadding(POP_UP_PADDING);
				popUpLabel.setTextFill(getColour(ColourKey.POPUP_TEXT));
				popUpLabel.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.POPUP_BACKGROUND)));
				popUpLabel.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.POPUP_BORDER)));
				popUpLabel.getStyleClass().add(StyleClass.CELL_POPUP_LABEL);

				// Return label
				return popUpLabel;
			});
			popUpManager.setDelay(POP_UP_DELAY);

			// Add event handler to show pop-up
			int i0 = i;
			double textWidth = maxStrWidth;
			label.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
			{
				if (event.getButton() == MouseButton.PRIMARY)
				{
					popUpManager.showPopUp(label, getDayString(i0, 0), null, event,
										   PopUpUtils.createLocator(label, VHPos.CENTRE_LEFT, VHPos.CENTRE_LEFT,
																	0.5 * (textWidth - TextUtils.textWidth(text)) - 2.0,
																	0.0));
				}
			});

			// Add event handler to hide pop-up
			label.addEventHandler(MouseEvent.MOUSE_RELEASED, popUpManager::hidePopUp);
		}

		// Create cell padding
		Insets cellPadding = new Insets(CELL_V_PADDING,
										CELL_H_PADDING + 0.5 * Math.max(0.0, maxStrWidth - dayStrWidth - 1.0),
										CELL_V_PADDING, CELL_H_PADDING);

		// Add day labels
		int numRows = getPaneNumRows();
		for (int row = 0; row < numRows; row++)
		{
			for (int column = 0; column < NUM_COLUMNS; column++)
			{
				// Create label
				Label label = new Label();

				// Set properties of label
				boolean evenColumn = (column % 2 == 0);
				label.setMaxWidth(Double.MAX_VALUE);
				label.setAlignment(Pos.CENTER_RIGHT);
				label.setPadding(cellPadding);
				label.setBackground(SceneUtils.createColouredBackground(evenColumn
																	? getColour(ColourKey.CELL_BACKGROUND_EVEN)
																	: getColour(ColourKey.CELL_BACKGROUND_ODD)));
				label.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.CELL_BORDER)));
				label.pseudoClassStateChanged(EVEN_PSEUDO_CLASS, evenColumn);
				label.pseudoClassStateChanged(ODD_PSEUDO_CLASS, !evenColumn);
				label.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, false);
				label.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
				label.getStyleClass().add(StyleClass.CELL_LABEL);

				// Select day when mouse is pressed on cell
				label.addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
				{
					if (event.getButton() == MouseButton.PRIMARY)
					{
						Integer index = (Integer)label.getUserData();
						if (index != null)
							setSelection(index);
					}
				});

				// Perform 'accept' action when mouse is double-clicked on cell
				label.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
				{
					if ((event.getButton() == MouseButton.PRIMARY) && (event.getClickCount() == 2))
					{
						Integer index = (Integer)label.getUserData();
						if ((index != null) && (acceptAction != null))
							acceptAction.invoke();
					}
				});

				// Add label to this pane
				add(label, column, row + 1);
			}
		}

		// Add handler for 'key pressed' events
		addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			boolean consume = true;
			switch (event.getCode())
			{
				case UP:
					incrementSelectionRow(-1);
					break;

				case DOWN:
					incrementSelectionRow(1);
					break;

				case LEFT:
					incrementSelectionColumn(-1);
					break;

				case RIGHT:
					incrementSelectionColumn(1);
					break;

				case HOME:
					if (event.isControlDown())
						incrementSelectionRow(-numRows);
					else
						incrementSelectionColumn(-NUM_COLUMNS);
					break;

				case END:
					if (event.isControlDown())
						incrementSelectionRow(numRows);
					else
						incrementSelectionColumn(NUM_COLUMNS);
					break;

				default:
					consume = false;
					break;
			}

			if (consume)
				event.consume();
		});

		// Update selection when 'focused' state changes
		focusedProperty().addListener(observable -> setSelection(this.selectedDay));

		// Request focus when mouse is pressed on this pane
		addEventHandler(MouseEvent.MOUSE_PRESSED, event -> requestFocus());

		// Update selection when mouse is dragged on this pane
		addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->
		{
			if (event.getButton() == MouseButton.PRIMARY)
			{
				Point2D p = localToScene(event.getX(), event.getY());
				for (Node child : getChildren())
				{
					Integer index = (Integer)child.getUserData();
					if ((index != null) && child.localToScene(child.getLayoutBounds()).contains(p))
						setSelection(index);
				}
			}
		});

		// Update day cells
		updateDays();
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
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the index of the currently selected day.
	 *
	 * @return the zero-based index of the currently selected day, or -1 if no day is selected.
	 */

	public int getSelectedDay()
	{
		return selectedDay;
	}

	//------------------------------------------------------------------

	/**
	 * Sets the month that will be displayed in this pane according to the specified parameters.
	 *
	 * @param  dayOffset
	 *           the offset of the first day of the month from the start of the week.
	 * @param  numDays
	 *           the number of days in the month.
	 * @param  selectedDay
	 *           the index of the selected day.
	 * @param  prevMonthNumDays
	 *           the number of days in the previous month.
	 * @throws IllegalArgumentException
	 *           if <i>dayOffset</i>, <i>numDays</i> or <i>selectedDay</i> is out of bounds.
	 */

	public void setMonth(
		int	dayOffset,
		int	numDays,
		int	selectedDay,
		int	prevMonthNumDays)
	{
		// Clear selection
		setSelection(-1);

		// Update instance variables
		init(dayOffset, numDays, selectedDay, prevMonthNumDays);

		// Update day cells
		updateDays();
	}

	//------------------------------------------------------------------

	/**
	 * Sets the specified procedure as the action that will be performed when the mouse is double-clicked on a day in
	 * this pane.
	 *
	 * @param action
	 *          the action that will be performed when the mouse is double-clicked on a day in this pane.
	 */

	public void setAcceptAction(
		IProcedure0	action)
	{
		acceptAction = action;
	}

	//------------------------------------------------------------------

	/**
	 * Initialises this pane with the specified parameters.
	 *
	 * @param  dayOffset
	 *           the offset of the first day of the month from the start of the week.
	 * @param  numDays
	 *           the number of days in the month.
	 * @param  selectedDay
	 *           the index of the selected day.
	 * @param  prevMonthNumDays
	 *           the number of days in the previous month.
	 * @throws IllegalArgumentException
	 *           if <i>dayOffset</i>, <i>numDays</i> or <i>selectedDay</i> is out of bounds.
	 */

	private void init(
		int	dayOffset,
		int	numDays,
		int	selectedDay,
		int	prevMonthNumDays)
	{
		// Validate arguments
		if ((dayOffset < 0) || (dayOffset >= NUM_COLUMNS))
			throw new IllegalArgumentException("Day offset out of bounds: " + dayOffset);
		if ((numDays < MIN_NUM_DAYS) || (numDays > MAX_NUM_DAYS))
			throw new IllegalArgumentException("Number of days out of bounds: " + numDays);
		if (selectedDay >= numDays)
			throw new IllegalArgumentException("Selected day out of bounds: " + selectedDay);

		// Set instance variables
		this.dayOffset = ((prevMonthNumDays < 0) || (dayOffset >= 0)) ? dayOffset : dayOffset + NUM_COLUMNS;
		this.numDays = numDays;
		this.selectedDay = (selectedDay < 0) ? -1 : selectedDay;
		this.prevMonthNumDays = prevMonthNumDays;
		numRows = (this.dayOffset + numDays + NUM_COLUMNS - 1) / NUM_COLUMNS;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of rows that are displayed in this pane.
	 *
	 * @return the number of rows that are displayed in this pane.
	 */

	private int getPaneNumRows()
	{
		return fullHeight ? MAX_NUM_DAYS / NUM_COLUMNS + Math.min(MAX_NUM_DAYS % NUM_COLUMNS, 2) : numRows;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the name of the day of the week with the specified index.
	 *
	 * @param  index
	 *           the index of the day of the week.
	 * @param  maxLength
	 *           the maximum length of the name.
	 * @return the name of the day of the week whose index is <i>index</i>.
	 */

	private String getDayString(
		int	index,
		int	maxLength)
	{
		int i = firstDayOfWeek + index - Calendar.SUNDAY;
		if (i >= NUM_DAYS_IN_WEEK)
			i -= NUM_DAYS_IN_WEEK;
		String str = dayNames.get(i);
		if ((maxLength > 0) && (maxLength < str.length()))
			str = str.substring(0, maxLength);
		return str;
	}

	//------------------------------------------------------------------

	/**
	 * Increments the column of the selected day cell by the specified number.
	 *
	 * @param increment
	 *          the number by which the column of the selected day cell will be incremented.
	 */

	private void incrementSelectionColumn(
		int	increment)
	{
		int day = (selectedDay < 0) ? 0 : selectedDay;
		int column = Math.min(Math.max(0, getColumnForDay(day) + increment), NUM_COLUMNS - 1);
		day = rowColumnToDay(getRowForDay(day), column);
		day = Math.min(Math.max(MIN_DAY, day), numDays - 1);
		setSelection(day);
	}

	//------------------------------------------------------------------

	/**
	 * Increments the row of the selected day cell by the specified number.
	 *
	 * @param increment
	 *          the number by which the row of the selected day cell will be incremented.
	 */

	private void incrementSelectionRow(
		int	increment)
	{
		int day = (selectedDay < 0) ? 0 : selectedDay;
		int row = Math.min(Math.max(0, getRowForDay(day) + increment), numRows - 1);
		day = rowColumnToDay(row, getColumnForDay(day));
		while (day < MIN_DAY)
			day += NUM_COLUMNS;
		while (day >= numDays)
			day -= NUM_COLUMNS;
		setSelection(day);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the row index that corresponds to the specified day of the month.
	 *
	 * @param  day
	 *           the day of the month whose row index is required.
	 * @return the row index that corresponds to <i>day</i>.
	 */

	private int getRowForDay(
		int	day)
	{
		return (day + dayOffset) / NUM_COLUMNS;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the column index that corresponds to the specified day of the month.
	 *
	 * @param  day
	 *           the day of the month whose column index is required.
	 * @return the column index that corresponds to <i>day</i>.
	 */

	private int getColumnForDay(
		int	day)
	{
		return (day + dayOffset) % NUM_COLUMNS;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the index of the day of the month at the intersection of the specified row and column.
	 *
	 * @param  row
	 *           the row index.
	 * @param  column
	 *           the column index.
	 * @return the index of the day of the month at the intersection of <i>row</i> and <i>column</i>.
	 */

	private int rowColumnToDay(
		int	row,
		int	column)
	{
		return row * NUM_COLUMNS + column - dayOffset;
	}

	//------------------------------------------------------------------

	/**
	 * Updates the day cells of this pane.
	 */

	private void updateDays()
	{
		// Update day labels
		int numRows = getPaneNumRows();
		int startDay = (prevMonthNumDays < 0) ? 0 : -dayOffset;
		int endDay = (prevMonthNumDays < 0) ? numDays : startDay + numRows * NUM_COLUMNS;
		int day = -dayOffset;
		for (int row = 0; row < numRows; row++)
		{
			for (int column = 0; column < NUM_COLUMNS; column++)
			{
				Label label = (Label)getChildren().get((row + 1) * NUM_COLUMNS + column);
				if ((day >= startDay) && (day < endDay))
				{
					int dayIndex = (day < 0) ? day + prevMonthNumDays
											 : (day < numDays)
													? day
													: day - numDays;
					label.setText(Integer.toString(dayIndex + 1));
					boolean dayInMonth = ((day >= 0) && (day < numDays));
					label.pseudoClassStateChanged(INVALID_PSEUDO_CLASS, !dayInMonth);
					label.setTextFill(dayInMonth ? getColour(ColourKey.CELL_TEXT)
												 : getColour(ColourKey.CELL_TEXT_INVALID));
					label.setUserData(dayInMonth ? day : null);
				}
				else
				{
					label.setText(null);
					label.setUserData(null);
				}

				++day;
			}
		}

		// Update selected day
		setSelection(selectedDay);
	}

	//------------------------------------------------------------------

	/**
	 * Selects the day of the month with the specified index.
	 *
	 * @param day
	 *          the index of the day of the month that will be selected.  If this is negative, the selection will be
	 *          cleared.
	 */

	private void setSelection(
		int	day)
	{
		// Redraw current selection
		if (selectedDay >= 0)
		{
			boolean evenColumn = (getColumnForDay(selectedDay) % 2 == 0);
			Label label = (Label)getChildren().get(NUM_COLUMNS + dayOffset + selectedDay);
			label.pseudoClassStateChanged(EVEN_PSEUDO_CLASS, evenColumn);
			label.pseudoClassStateChanged(ODD_PSEUDO_CLASS, !evenColumn);
			label.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, false);
			label.setBackground(SceneUtils.createColouredBackground(evenColumn
																		? getColour(ColourKey.CELL_BACKGROUND_EVEN)
																		: getColour(ColourKey.CELL_BACKGROUND_ODD)));
			label.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.CELL_BORDER)));
		}

		// Update instance variable
		selectedDay = day;

		// Draw selection
		if (day >= 0)
		{
			int index = NUM_COLUMNS + dayOffset + day;
			Label label = (Label)getChildren().get(index);
			label.pseudoClassStateChanged(EVEN_PSEUDO_CLASS, false);
			label.pseudoClassStateChanged(ODD_PSEUDO_CLASS, false);
			label.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true);
			label.setBackground(SceneUtils.createColouredBackground(isFocused()
																? getColour(ColourKey.CELL_BACKGROUND_SELECTED_FOCUSED)
																: getColour(ColourKey.CELL_BACKGROUND_SELECTED)));
			label.setBorder(SceneUtils.createSolidBorder(isFocused()
																? getColour(ColourKey.CELL_BORDER_SELECTED_FOCUSED)
																: getColour(ColourKey.CELL_BORDER_SELECTED)));
		}
	}

	//------------------------------------------------------------------

}

//----------------------------------------------------------------------
