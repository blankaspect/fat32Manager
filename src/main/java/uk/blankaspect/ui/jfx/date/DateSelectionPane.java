/*====================================================================*\

DateSelectionPane.java

Class: date-selection pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.ui.jfx.date;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.time.LocalDate;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javafx.application.Platform;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.Node;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.shape.Shape;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.date.DateUtils;

import uk.blankaspect.common.function.IFunction2;
import uk.blankaspect.common.function.IProcedure0;

import uk.blankaspect.common.geometry.HDirection;
import uk.blankaspect.common.geometry.VHPos;

import uk.blankaspect.common.misc.ModernCalendar;

import uk.blankaspect.ui.jfx.button.Buttons;
import uk.blankaspect.ui.jfx.button.GraphicButton;

import uk.blankaspect.ui.jfx.container.DialogButtonPane;
import uk.blankaspect.ui.jfx.container.PaneStyle;

import uk.blankaspect.ui.jfx.exec.ExecUtils;

import uk.blankaspect.ui.jfx.font.FontUtils;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.shape.Shapes;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;
import uk.blankaspect.ui.jfx.spinner.SpinnerFactory;
import uk.blankaspect.ui.jfx.spinner.SpinnerUtils;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.TextUtils;

import uk.blankaspect.ui.jfx.tooltip.TooltipDecorator;

//----------------------------------------------------------------------


// CLASS: DATE-SELECTION PANE


/**
 * This class implements a JavaFX pane in which a date can be selected from a calendar.  The pane has the conventional
 * form of a month-per-page matrix.
 */

public class DateSelectionPane
	extends VBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	/** The minimum year of the calendar. */
	public static final		int		MIN_YEAR	= 1600;

	/** The maximum year of the calendar. */
	public static final		int		MAX_YEAR	= 3999;

	/** The minimum month of a year. */
	private static final	int		MIN_MONTH	= 0;

	/** The maximum month of a year. */
	private static final	int		MAX_MONTH	= 11;

	/** The minimum day of a month. */
	private static final	int		MIN_DAY	= 0;

	/** The number of days in a week. */
	private static final	int		NUM_DAYS_IN_WEEK	= 7;

	/** The minimum length of the name of a month. */
	private static final	int		MIN_MONTH_NAME_LENGTH	= 3;

	/** The value by which the height of the default font is multiplied to give the preferred width of a navigation
		button. */
	private static final	double	NAVIGATION_BUTTON_WIDTH_FACTOR	= 1.4;

	/** The value by which the height of the default font is multiplied to give the preferred height of a navigation
		button. */
	private static final	double	NAVIGATION_BUTTON_HEIGHT_FACTOR	= 1.2;

	/** Miscellaneous strings. */
	private static final	String	PREVIOUS_MONTH_STR	= "Previous month (PageUp)";
	private static final	String	NEXT_MONTH_STR		= "Next month (PageDown)";
	private static final	String	PREVIOUS_YEAR_STR	= "Previous year (Ctrl+PageUp)";
	private static final	String	NEXT_YEAR_STR		= "Next year (Ctrl+PageDown)";
	private static final	String	EDIT_MONTH_YEAR_STR	= "Edit month and year (Ctrl+Space)";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.MONTH_LABEL_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.DATE_SELECTION_PANE)
					.desc(StyleClass.MONTH_LABEL)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.MONTH_LABEL_BACKGROUND_HOVERED,
			CssSelector.builder()
					.cls(StyleClass.DATE_SELECTION_PANE)
					.desc(StyleClass.MONTH_LABEL).pseudo(FxPseudoClass.HOVERED)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.DATE_SELECTION_PANE)
					.desc(StyleClass.NAVIGATION_PANE)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.NAVIGATION_BUTTON_ICON,
			CssSelector.builder()
					.cls(StyleClass.DATE_SELECTION_PANE)
					.desc(StyleClass.NAVIGATION_BUTTON)
					.desc(StyleClass.ICON)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.NAVIGATION_BUTTON_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.DATE_SELECTION_PANE)
					.desc(StyleClass.NAVIGATION_BUTTON)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.NAVIGATION_BUTTON_BORDER,
			CssSelector.builder()
					.cls(StyleClass.DATE_SELECTION_PANE)
					.desc(StyleClass.NAVIGATION_BUTTON)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.MONTH_YEAR_DIALOG_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.MONTH_YEAR_DIALOG)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.MONTH_YEAR_DIALOG_BORDER,
			CssSelector.builder()
					.cls(StyleClass.MONTH_YEAR_DIALOG)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.MONTH_YEAR_DIALOG_DIVIDER,
			CssSelector.builder()
					.cls(StyleClass.MONTH_YEAR_DIALOG)
					.desc(StyleClass.CONTROL_PANE)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(DaySelectionPane.StyleClass.DAY_SELECTION_PANE)
						.build())
				.emptyBorder()
				.build(),
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.MONTH_YEAR_DIALOG)
						.desc(StyleClass.CONTROL_PANE)
						.build())
				.borders(Side.BOTTOM)
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	CONTROL_PANE		= StyleConstants.CLASS_PREFIX + "control-pane";
		String	DATE_SELECTION_PANE	= StyleConstants.CLASS_PREFIX + "date-selection-pane";
		String	ICON				= StyleConstants.CLASS_PREFIX + "icon";
		String	MONTH_LABEL			= StyleConstants.CLASS_PREFIX + "month-label";
		String	MONTH_YEAR_DIALOG	= DATE_SELECTION_PANE + "-month-year-dialog";
		String	NAVIGATION_BUTTON	= StyleConstants.CLASS_PREFIX + "navigation-button";
		String	NAVIGATION_PANE		= StyleConstants.CLASS_PREFIX + "navigation-pane";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	MONTH_LABEL_BACKGROUND			= PREFIX + "monthLabel.background";
		String	MONTH_LABEL_BACKGROUND_HOVERED	= PREFIX + "monthLabel.background.hovered";
		String	MONTH_YEAR_DIALOG_BACKGROUND	= PREFIX + "monthYearDialog.background";
		String	MONTH_YEAR_DIALOG_BORDER		= PREFIX + "monthYearDialog.border";
		String	MONTH_YEAR_DIALOG_DIVIDER		= PREFIX + "monthYearDialog.divider";
		String	NAVIGATION_BUTTON_BACKGROUND	= PREFIX + "navigationButton.background";
		String	NAVIGATION_BUTTON_BORDER		= PREFIX + "navigationButton.border";
		String	NAVIGATION_BUTTON_ICON			= PREFIX + "navigationButton.icon";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	/** The calendar that is represented by this pane. */
	private	Calendar			calendar;

	/** The index of the first day of the week. */
	private	int					firstDayOfWeek;

	/** Flag: if {@code true}, the days of adjacent months are shown in a month page. */
	private	boolean				showAdjacentMonths;

	/** The names of months. */
	private	List<String>		monthNames;

	/** The lengths of the names of months. */
	private	int[]				monthNameLengths;

	/** The <i>previous month</i> button. */
	private	GraphicButton		previousMonthButton;

	/** The <i>previous year</i> button. */
	private	GraphicButton		previousYearButton;

	/** The <i>next month</i> button. */
	private	GraphicButton		nextMonthButton;

	/** The <i>next year</i> button. */
	private	GraphicButton		nextYearButton;

	/** The month label. */
	private	Label				monthLabel;

	/** The day-selection pane. */
	private	DaySelectionPane	daySelectionPane;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class and its dependencies with the style manager
		StyleManager.INSTANCE.register(DateSelectionPane.class, COLOUR_PROPERTIES, RULE_SETS,
									   PaneStyle.class);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a new instance of a date-selection pane that is initialised with the specified date.
	 *
	 * @param  date
	 *           the initial date.
	 * @param  showAdjacentMonths
	 *           if {@code true}, the days of adjacent months will be shown in a month page.
	 * @throws IllegalArgumentException
	 *           if a component of <i>date</i> is out of bounds.
	 */

	public DateSelectionPane(
		LocalDate	date,
		boolean		showAdjacentMonths)
	{
		// Call alternative constructor
		this(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth() - 1, 0, showAdjacentMonths);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a date-selection pane that is initialised with the specified date.
	 *
	 * @param  date
	 *           the initial date.
	 * @param  firstDayOfWeek
	 *           the index of the first day of the week (1..7).  If this is zero, the first day of the week for the
	 *           current locale will be used.
	 * @param  showAdjacentMonths
	 *           if {@code true}, the days of adjacent months will be shown in a month page.
	 * @throws IllegalArgumentException
	 *           if a component of <i>date</i> is out of bounds.
	 */

	public DateSelectionPane(
		LocalDate	date,
		int			firstDayOfWeek,
		boolean		showAdjacentMonths)
	{
		// Call alternative constructor
		this(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth() - 1, firstDayOfWeek, showAdjacentMonths);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a date-selection panel that is initialised with the specified date components.
	 *
	 * @param  year
	 *           the initial year.
	 * @param  month
	 *           the initial month.
	 * @param  day
	 *           the initial day of the month.
	 * @param  showAdjacentMonths
	 *           if {@code true}, the days of adjacent months will be shown in a month page.
	 * @throws IllegalArgumentException
	 *           if a date component is out of bounds.
	 */

	public DateSelectionPane(
		int		year,
		int		month,
		int		day,
		boolean	showAdjacentMonths)
	{
		// Call alternative constructor
		this(year, month, day, 0, showAdjacentMonths);
	}

	//------------------------------------------------------------------

	/**
	 * Creates a new instance of a date-selection panel that is initialised with the specified date components.
	 *
	 * @param  year
	 *           the initial year.
	 * @param  month
	 *           the initial month.
	 * @param  day
	 *           the initial day of the month.
	 * @param  firstDayOfWeek
	 *           the index of the first day of the week (1..7).  If this is zero, the first day of the week for the
	 *           current locale will be used.
	 * @param  showAdjacentMonths
	 *           if {@code true}, the days of adjacent months will be shown in a month page.
	 * @throws IllegalArgumentException
	 *           if a date component is out of bounds.
	 */

	public DateSelectionPane(
		int		year,
		int		month,
		int		day,
		int		firstDayOfWeek,
		boolean	showAdjacentMonths)
	{
		// Validate arguments
		if ((year < MIN_YEAR) || (year > MAX_YEAR))
			throw new IllegalArgumentException("Year out of bounds: " + year);
		if ((month < MIN_MONTH) || (month > MAX_MONTH))
			throw new IllegalArgumentException("Month out of bounds: " + month);
		if ((firstDayOfWeek < 0) || (firstDayOfWeek > Calendar.SATURDAY))
			throw new IllegalArgumentException("First day of week out of bounds: " + firstDayOfWeek);

		// Initialise instance variables
		calendar = new ModernCalendar(year, month, 1);
		if ((day < MIN_DAY) || (day >= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)))
			throw new IllegalArgumentException();
		this.firstDayOfWeek = (firstDayOfWeek == 0) ? calendar.getFirstDayOfWeek() : firstDayOfWeek;
		this.showAdjacentMonths = showAdjacentMonths;

		// Initialise list of names of months
		monthNames = DateUtils.getMonthNames(Locale.getDefault());
		int numMonths = monthNames.size();

		// Initialise array of lengths of names
		monthNameLengths = new int[numMonths];
		for (int i = 0; i < numMonths; i++)
		{
			int length = MIN_MONTH_NAME_LENGTH - 1;
			boolean done = false;
			while (!done)
			{
				++length;
				String str0 = monthNames.get(i);
				if (length < str0.length())
					str0 = str0.substring(0, length);
				int j = 0;
				while (j < numMonths)
				{
					if (i != j)
					{
						String str1 = monthNames.get(j);
						if (length < str1.length())
							str1 = str1.substring(0, length);
						if (str1.equals(str0))
							break;
					}
					++j;
				}
				if (j == numMonths)
					done = true;
			}
			monthNameLengths[i] = length;
		}

		// Set properties
		getStyleClass().add(StyleClass.DATE_SELECTION_PANE);

		// Create factory for navigation buttons
		IFunction2<GraphicButton, Shape, String> navigationButtonFactory = (icon, tooltipText) ->
		{
			// Set properties of icon
			icon.setStroke(getColour(ColourKey.NAVIGATION_BUTTON_ICON));
			icon.getStyleClass().add(StyleClass.ICON);

			// Create button
			GraphicButton button = new GraphicButton(Shapes.tile(icon), tooltipText);

			// Set properties of button
			double fontHeight = TextUtils.textHeight();
			double width = Math.rint(fontHeight * NAVIGATION_BUTTON_WIDTH_FACTOR);
			double height = Math.rint(fontHeight * NAVIGATION_BUTTON_HEIGHT_FACTOR);
			button.setPrefSize(width, height);
			button.setPadding(Insets.EMPTY);
			button.setBackgroundColour(getColour(ColourKey.NAVIGATION_BUTTON_BACKGROUND));
			button.setBorderColour(getColour(ColourKey.NAVIGATION_BUTTON_BORDER));
			button.getStyleClass().add(StyleClass.NAVIGATION_BUTTON);

			// Return button
			return button;
		};

		// Button: previous year
		previousYearButton = navigationButtonFactory.invoke(Shapes.angle01Double(HDirection.LEFT), PREVIOUS_YEAR_STR);
		previousYearButton.setOnAction(event -> onPreviousYear());

		// Button: previous month
		previousMonthButton = navigationButtonFactory.invoke(Shapes.angle01Single(HDirection.LEFT), PREVIOUS_MONTH_STR);
		previousMonthButton.setOnAction(event -> onPreviousMonth());
		HBox.setMargin(previousMonthButton, new Insets(0.0, 0.0, 0.0, 1.0));

		// Label: month and year
		monthLabel = new Label(getMonthString(year, month));
		monthLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		monthLabel.setFont(FontUtils.boldFont());
		monthLabel.setAlignment(Pos.CENTER);
		monthLabel.setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.MONTH_LABEL_BACKGROUND)));
		if (StyleManager.INSTANCE.notUsingStyleSheet())
		{
			monthLabel.addEventHandler(MouseEvent.MOUSE_ENTERED, event ->
			{
				monthLabel.setBackground(SceneUtils.createColouredBackground(
						getColour(ColourKey.MONTH_LABEL_BACKGROUND_HOVERED)));
			});
			monthLabel.addEventHandler(MouseEvent.MOUSE_EXITED, event ->
			{
				monthLabel.setBackground(SceneUtils.createColouredBackground(
						getColour(ColourKey.MONTH_LABEL_BACKGROUND)));
			});
		}
		monthLabel.getStyleClass().add(StyleClass.MONTH_LABEL);
		HBox.setHgrow(monthLabel, Priority.ALWAYS);
		monthLabel.setOnMouseClicked(event ->
		{
			if (event.getButton() == MouseButton.PRIMARY)
				onEditMonthYear();
		});
		TooltipDecorator.addTooltip(monthLabel, EDIT_MONTH_YEAR_STR);

		// Button: next month
		nextMonthButton = navigationButtonFactory.invoke(Shapes.angle01Single(HDirection.RIGHT), NEXT_MONTH_STR);
		nextMonthButton.setOnAction(event -> onNextMonth());
		HBox.setMargin(nextMonthButton, new Insets(0.0, 1.0, 0.0, 0.0));

		// Button: next year
		nextYearButton = navigationButtonFactory.invoke(Shapes.angle01Double(HDirection.RIGHT), NEXT_YEAR_STR);
		nextYearButton.setOnAction(event -> onNextYear());

		// Create navigation pane
		HBox navigationPane = new HBox(previousYearButton, previousMonthButton, monthLabel, nextMonthButton,
									   nextYearButton);
		navigationPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER), Side.BOTTOM));
		navigationPane.getStyleClass().add(StyleClass.NAVIGATION_PANE);

		// Create day-selection pane
		daySelectionPane = new DaySelectionPane(getDayOffset(), getDaysInMonth(), day,
												showAdjacentMonths ? getDaysInPrevMonth() : -1, this.firstDayOfWeek,
												true);
		daySelectionPane.setBorder(Border.EMPTY);

		// Add children to this pane
		getChildren().addAll(navigationPane, daySelectionPane);

		// Add handler for 'key pressed' events
		addEventHandler(KeyEvent.KEY_PRESSED, event ->
		{
			switch (event.getCode())
			{
				case PAGE_UP:
					if (event.isControlDown())
						onPreviousYear();
					else
						onPreviousMonth();
					break;

				case PAGE_DOWN:
					if (event.isControlDown())
						onNextYear();
					else
						onNextMonth();
					break;

				case SPACE:
					if (event.isControlDown())
						onEditMonthYear();
					break;

				default:
					// do nothing
					break;
			}
		});

		// Update buttons
		updateButtons();

		// Request focus on day-selection pane
		Platform.runLater(daySelectionPane::requestFocus);
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
	public void requestFocus()
	{
		daySelectionPane.requestFocus();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the date that is currently selected in the day-selection pane.
	 *
	 * @return the date that is currently selected in the day-selection pane.
	 */

	public LocalDate getDate()
	{
		int day = daySelectionPane.getSelectedDay();
		return (day < 0) ? null : LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, day + 1);
	}

	//------------------------------------------------------------------

	/**
	 * Sets the specified procedure as the action that will be performed when the mouse is double-clicked on a day in
	 * the day-selection pane.
	 *
	 * @param action
	 *          the action that will be performed when the mouse is double-clicked on a day in the day-selection pane.
	 */

	public void setAcceptAction(
		IProcedure0	action)
	{
		daySelectionPane.setAcceptAction(action);
	}

	//------------------------------------------------------------------

	/**
	 * Enables or disables the navigation buttons according to the current year and month.
	 */

	private void updateButtons()
	{
		previousMonthButton.setDisable((calendar.get(Calendar.MONTH) <= MIN_MONTH)
										&& (calendar.get(Calendar.YEAR) <= MIN_YEAR));
		previousYearButton.setDisable(calendar.get(Calendar.YEAR) <= MIN_YEAR);
		nextMonthButton.setDisable((calendar.get(Calendar.MONTH) >= MAX_MONTH)
									&& (calendar.get(Calendar.YEAR) >= MAX_YEAR));
		nextYearButton.setDisable(calendar.get(Calendar.YEAR) >= MAX_YEAR);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the offset of the first day of the current month from the start of the week.
	 *
	 * @return the offset of the first day of the current month from the start of the week.
	 */

	private int getDayOffset()
	{
		int offset = calendar.get(Calendar.DAY_OF_WEEK) - firstDayOfWeek;
		if (offset < 0)
			offset += NUM_DAYS_IN_WEEK;
		return offset;
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of days in the current month.
	 *
	 * @return the number of days in the current month.
	 */

	private int getDaysInMonth()
	{
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the number of days in the previous month.
	 *
	 * @return the number of days in the previous month.
	 */

	private int getDaysInPrevMonth()
	{
		ModernCalendar calendar = new ModernCalendar();
		calendar.setTimeInMillis(this.calendar.getTimeInMillis());
		calendar.roll(Calendar.MONTH, false);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	//------------------------------------------------------------------

	/**
	 * Returns the name of the month at the specified index.
	 *
	 * @param  index
	 *           the zero-based index of the month of interest.
	 * @return the name of the month at <i>index</i>
	 */

	private String getMonthString(
		int	index)
	{
		String str = monthNames.get(index);
		if (monthNameLengths[index] < str.length())
			str = str.substring(0, monthNameLengths[index]);
		return str;
	}

	//------------------------------------------------------------------

	/**
	 * Returns a string representation of the specified year and month.
	 *
	 * @param  year
	 *           the year.
	 * @param  month
	 *           the month.
	 * @return a string representation of the specified year and month.
	 */

	private String getMonthString(
		int	year,
		int	month)
	{
		return getMonthString(month) + " " + Integer.toString(year);
	}

	//------------------------------------------------------------------

	/**
	 * Displays the page for the specified year and month in this pane.
	 *
	 * @param year
	 *          the year.
	 * @param month
	 *          the month.
	 */

	private void setMonth(
		int	year,
		int	month)
	{
		calendar = new ModernCalendar(year, month, 1);
		int daysInMonth = getDaysInMonth();
		int selectedDay = Math.min(daySelectionPane.getSelectedDay(), daysInMonth - 1);
		daySelectionPane.setMonth(getDayOffset(), daysInMonth, selectedDay,
								  showAdjacentMonths ? getDaysInPrevMonth() : -1);
		monthLabel.setText(getMonthString(year, month));
		updateButtons();
	}

	//------------------------------------------------------------------

	/**
	 * Navigates to the previous month.
	 */

	private void onPreviousMonth()
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (month == MIN_MONTH)
		{
			if (year > MIN_YEAR)
				setMonth(year - 1, MAX_MONTH);
		}
		else
			setMonth(year, month - 1);
	}

	//------------------------------------------------------------------

	/**
	 * Navigates to the previous year.
	 */

	private void onPreviousYear()
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (year > MIN_YEAR)
			setMonth(year - 1, month);
	}

	//------------------------------------------------------------------

	/**
	 * Navigates to the next month.
	 */

	private void onNextMonth()
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (month == MAX_MONTH)
		{
			if (year < MAX_YEAR)
				setMonth(year + 1, MIN_MONTH);
		}
		else
			setMonth(year, month + 1);
	}

	//------------------------------------------------------------------

	/**
	 * Navigates to the next year.
	 */

	private void onNextYear()
	{
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		if (year < MAX_YEAR)
			setMonth(year + 1, month);
	}

	//------------------------------------------------------------------

	/**
	 * Displays a dialog in which the year and month may be selected.
	 */

	private void onEditMonthYear()
	{
		MonthYearDialog dialog = new MonthYearDialog(daySelectionPane, calendar.get(Calendar.YEAR),
													 calendar.get(Calendar.MONTH), monthNames);
		dialog.showAndWait();
		if (dialog.result != null)
			setMonth(dialog.result[0], dialog.result[1]);

		daySelectionPane.requestFocus();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: MONTH-YEAR DIALOG


	/**
	 * This class implements an undecorated modal dialog in which the month and year may be selected.
	 */

	private static class MonthYearDialog
		extends Stage
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		/** The number of digits of the year spinner. */
		private static final	int		YEAR_SPINNER_NUM_DIGITS	= 4;

		/** The gap between adjacent controls in the control pane. */
		private static final	double	CONTROL_PANE_GAP	= 3.0;

		/** The padding around the control pane. */
		private static final	Insets	CONTROL_PANE_PADDING	= new Insets(3.0);

		/** The gap between the buttons of the dialog. */
		private static final	double	BUTTON_GAP	= 6.0;

		/** The padding around a button. */
		private static final	Insets	BUTTON_PADDING	= new Insets(1.0, 8.0, 1.0, 8.0);

		/** The padding around the button pane. */
		private static final	Insets	BUTTON_PANE_PADDING	= new Insets(3.0);

		/** The object that is used as the value of the <i>group</i> property of a button, to equalise button widths. */
		private static final	Object	BUTTON_GROUP	= new Object();

		/** The delay (in milliseconds) before making the window visible by restoring its opacity. */
		private static final	int		WINDOW_VISIBLE_DELAY	= 50;

		/** Miscellaneous strings. */
		private static final	String	OK_STR		= "OK";
		private static final	String	CANCEL_STR	= "Cancel";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		/** The result of this dialog (year and month). */
		private	int[]	result;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		/**
		 * Creates a new instance of a month-and-year dialog.
		 *
		 * @param locator
		 *          the node relative to which the dialog will be located.
		 * @param year
		 *          the initial year.
		 * @param month
		 *          the initial month.
		 * @param monthStrs
		 *          the names of months.
		 */

		private MonthYearDialog(
			Node			locator,
			int				year,
			int				month,
			List<String>	monthStrs)
		{
			// Call superclass constructor
			super(StageStyle.TRANSPARENT);

			// Set properties
			initModality(Modality.APPLICATION_MODAL);
			initOwner(SceneUtils.getWindow(locator));
			setResizable(false);

			// Make window invisible until it is displayed
			setOpacity(0.0);

			// Spinner: months
			CollectionSpinner<String> monthSpinner =
					CollectionSpinner.leftRightH(HPos.CENTER, true, monthStrs, monthStrs.get(month), null, null);

			// Spinner: year
			Spinner<Integer> yearSpinner =
					SpinnerFactory.integerSpinner(MIN_YEAR, MAX_YEAR, year, YEAR_SPINNER_NUM_DIGITS);

			// Create control pane
			HBox controlPane = new HBox(CONTROL_PANE_GAP, monthSpinner, yearSpinner);
			controlPane.setAlignment(Pos.CENTER);
			controlPane.setPadding(CONTROL_PANE_PADDING);
			controlPane.setBorder(SceneUtils.createSolidBorder(
					getColour(ColourKey.MONTH_YEAR_DIALOG_DIVIDER), Side.BOTTOM));
			controlPane.getStyleClass().add(StyleClass.CONTROL_PANE);

			// Create button pane
			DialogButtonPane buttonPane = new DialogButtonPane(BUTTON_GAP);
			buttonPane.setPadding(BUTTON_PANE_PADDING);

			// Create button: OK
			Button okButton = Buttons.hNoShrink(OK_STR);
			okButton.getProperties().put(DialogButtonPane.BUTTON_GROUP_KEY, BUTTON_GROUP);
			okButton.setPadding(BUTTON_PADDING);
			okButton.setOnAction(event ->
			{
				result = new int[] { yearSpinner.getValue(), monthSpinner.getValue() };
				hide();
			});
			buttonPane.addButton(okButton, HPos.RIGHT);

			// Create button: cancel
			Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
			cancelButton.getProperties().put(DialogButtonPane.BUTTON_GROUP_KEY, BUTTON_GROUP);
			cancelButton.setPadding(BUTTON_PADDING);
			cancelButton.setOnAction(event -> hide());
			buttonPane.addButton(cancelButton, HPos.RIGHT);

			// Update spacing of button groups
			buttonPane.updateButtonSpacing();

			// Create main pane
			VBox mainPane = new VBox(controlPane, buttonPane);
			mainPane.setBackground(SceneUtils.createColouredBackground(
					getColour(ColourKey.MONTH_YEAR_DIALOG_BACKGROUND)));
			mainPane.setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.MONTH_YEAR_DIALOG_BORDER)));
			mainPane.getStyleClass().add(StyleClass.MONTH_YEAR_DIALOG);

			// Create scene
			Scene scene = new Scene(mainPane);

			// Add style sheet to scene
			StyleManager.INSTANCE.addStyleSheet(scene);

			// Set scene on this window
			setScene(scene);

			// Add handler for 'key pressed' events
			addEventFilter(KeyEvent.KEY_PRESSED, event ->
			{
				switch (event.getCode())
				{
					case ESCAPE:
						cancelButton.fire();
						event.consume();
						break;

					case ENTER:
						if (event.isControlDown())
						{
							SpinnerUtils.updateIntegerValue(yearSpinner);
							okButton.fire();
							event.consume();
						}
						break;

					default:
						// do nothing
						break;
				}
			});

			// Update UI after window is displayed
			addEventHandler(WindowEvent.WINDOW_SHOWN, event ->
			{
				// Equalise widths of groups of buttons
				double extraWidth = buttonPane.equaliseButtonWidths(true);

				// Increase width of window to accommodate extra width of buttons
				if (extraWidth > 0.0)
					setWidth(getWidth() + extraWidth);

				// Get location of window relative to locator
				Point2D location = SceneUtils.getRelativeLocation(getWidth(), getHeight(), VHPos.TOP_CENTRE, locator,
																  VHPos.TOP_CENTRE);

				// Set location of window
				setX(location.getX());
				setY(location.getY() - 1.0);

				// Make window visible after a delay
				ExecUtils.afterDelay(WINDOW_VISIBLE_DELAY, () -> setOpacity(1.0));
			});
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
