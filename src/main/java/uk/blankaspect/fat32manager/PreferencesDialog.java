/*====================================================================*\

PreferencesDialog.java

Class: preferences dialog.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.List;
import java.util.Objects;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;

import javafx.stage.Window;

import uk.blankaspect.common.css.CssRuleSet;
import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IProcedure1;

import uk.blankaspect.ui.jfx.button.Buttons;

import uk.blankaspect.ui.jfx.container.PaneStyle;

import uk.blankaspect.ui.jfx.control.ControlUtils;

import uk.blankaspect.ui.jfx.dialog.SimpleModalDialog;

import uk.blankaspect.ui.jfx.label.Labels;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.spinner.CollectionSpinner;
import uk.blankaspect.ui.jfx.spinner.SpinnerFactory;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.RuleSetBuilder;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.tabbedpane.TabPaneUtils;

//----------------------------------------------------------------------


// CLASS: PREFERENCES DIALOG


public class PreferencesDialog
	extends SimpleModalDialog<Preferences>
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	int		MIN_COLUMN_HEADER_POP_UP_DELAY	= 0;
	private static final	int		MAX_COLUMN_HEADER_POP_UP_DELAY	= 5000;

	private static final	int		MIN_MIN_NUM_SECTORS	= 0;
	private static final	int		MAX_MIN_NUM_SECTORS	= Fat32Volume.FORMAT_MAX_NUM_SECTORS;

	private static final	int		COLUMN_HEADER_POP_UP_DELAY_SPINNER_NUM_DIGITS	= 4;
	private static final	int		MIN_NUM_SECTORS_SPINNER_NUM_DIGITS	= 10;

	private static final	double	CONTROL_H_GAP	= 6.0;
	private static final	double	CONTROL_V_GAP	= 6.0;

	private static final	Insets	CONTROL_PANE_PADDING	= new Insets(12.0);

	private static final	Insets	TABBED_PANE_HEADER_PADDING	= new Insets(2.0, 2.0, 0.0, 2.0);

	private static final	double	MIN_TAB_WIDTH	= 64.0;

	private static final	String	PREFERENCES_STR					= "Preferences";
	private static final	String	THEME_STR						= "Theme";
	private static final	String	COLUMN_HEADER_POP_UP_DELAY_STR	= "Table-column header pop-up delay";
	private static final	String	MS_STR							= "ms";
	private static final	String	SHOW_SPECIAL_DIRECTORIES_STR	= "Show special directories ( '.' and '..' )";
	private static final	String	FIX_INVALID_DATES_TIMES_STR		= "Fix invalid dates and times";
	private static final	String	REMOVABLE_ONLY_STR				= "Removable media only";
	private static final	String	MIN_NUM_SECTORS_STR				= "Minimum number of sectors";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			PaneStyle.ColourKey.PANE_BORDER,
			CssSelector.builder()
					.cls(StyleClass.PREFERENCES_DIALOG_ROOT)
					.desc(StyleClass.TABBED_PANE)
					.build()
		)
	);

	/** CSS rule sets. */
	private static final	List<CssRuleSet>	RULE_SETS	= List.of
	(
		RuleSetBuilder.create()
				.selector(CssSelector.builder()
						.cls(StyleClass.PREFERENCES_DIALOG_ROOT)
						.desc(StyleClass.TABBED_PANE)
						.build())
				.borders(Side.BOTTOM)
				.build()
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	PREFERENCES_DIALOG_ROOT	= StyleConstants.APP_CLASS_PREFIX + "preferences-dialog-root";

		String	TABBED_PANE	= StyleConstants.CLASS_PREFIX + "tabbed-pane";
	}

////////////////////////////////////////////////////////////////////////
//  Class variables
////////////////////////////////////////////////////////////////////////

	private static	int	selectedTabIndex;

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	Preferences	result;
	private	TabPane		tabPane;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(PreferencesDialog.class, COLOUR_PROPERTIES, RULE_SETS);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	private PreferencesDialog(
		Window		owner,
		Preferences	preferences)
	{
		// Call superclass constructor
		super(owner, MethodHandles.lookup().lookupClass().getName(), null, PREFERENCES_STR);

		// Set style class on root node of scene graph
		getScene().getRoot().getStyleClass().add(StyleClass.PREFERENCES_DIALOG_ROOT);

		// Create tabbed pane
		tabPane = new TabPane();
		tabPane.setBorder(SceneUtils.createSolidBorder(getColour(PaneStyle.ColourKey.PANE_BORDER), Side.BOTTOM));
		tabPane.setTabMinWidth(MIN_TAB_WIDTH);
		tabPane.getStyleClass().add(StyleClass.TABBED_PANE);

		// Set tabbed pane as content of dialog
		setContent(tabPane);

		// Set padding around header of tabbed pane
		ControlUtils.onSkin(tabPane, () -> TabPaneUtils.setHeaderAreaPadding(tabPane, TABBED_PANE_HEADER_PADDING));

		// Add tabs to tabbed pane
		for (TabId tabId : TabId.values())
			tabPane.getTabs().add(tabId.createTab());

		// Select tab
		tabPane.getSelectionModel().select(selectedTabIndex);


		//----  Tab: appearance

		// Create procedure to select theme
		StyleManager styleManager = StyleManager.INSTANCE;
		IProcedure1<String> selectTheme = id ->
		{
			if (id != null)
			{
				// Update theme
				styleManager.selectTheme(id);

				// Reapply style sheet to the scenes of all JavaFX windows
				styleManager.reapplyStylesheet();
			}
		};

		// Spinner: theme
		String themeId = styleManager.getThemeId();
		CollectionSpinner<String> themeSpinner =
				CollectionSpinner.leftRightH(HPos.CENTER, true, styleManager.getThemeIds(), themeId, null,
											 id -> styleManager.findTheme(id).name());
		themeSpinner.itemProperty().addListener((observable, oldId, id) -> selectTheme.invoke(id));

		// Pane: appearance
		HBox appearancePane = new HBox(CONTROL_H_GAP, Labels.hNoShrink(THEME_STR), themeSpinner);
		appearancePane.setAlignment(Pos.CENTER);
		appearancePane.setPadding(CONTROL_PANE_PADDING);

		// Set content of tab
		getTab(TabId.APPEARANCE).setContent(appearancePane);


		//----  Tab: view

		// Pane: view
		GridPane viewPane = new GridPane();
		viewPane.setHgap(CONTROL_H_GAP);
		viewPane.setVgap(CONTROL_V_GAP);
		viewPane.setAlignment(Pos.CENTER);
		viewPane.setPadding(CONTROL_PANE_PADDING);

		// Initialise column constraints
		ColumnConstraints column = new ColumnConstraints();
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
		viewPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		viewPane.getColumnConstraints().add(column);

		// Initialise row index
		int row = 0;

		// Spinner: column-header pop-up delay
		Spinner<Integer> columnHeaderPopUpDelaySpinner =
				SpinnerFactory.integerSpinner(MIN_COLUMN_HEADER_POP_UP_DELAY, MAX_COLUMN_HEADER_POP_UP_DELAY,
											  preferences.getColumnHeaderPopUpDelay(),
											  COLUMN_HEADER_POP_UP_DELAY_SPINNER_NUM_DIGITS);

		// Pane: column-header pop-up delay
		HBox columnHeaderPopUpDelayPane = new HBox(4.0, columnHeaderPopUpDelaySpinner, Labels.hNoShrink(MS_STR));
		columnHeaderPopUpDelayPane.setAlignment(Pos.CENTER_LEFT);
		viewPane.addRow(row++, new Label(COLUMN_HEADER_POP_UP_DELAY_STR), columnHeaderPopUpDelayPane);

		// Check box: show special directories
		CheckBox showSpecialDirectoriesCheckBox = new CheckBox(SHOW_SPECIAL_DIRECTORIES_STR);
		showSpecialDirectoriesCheckBox.setSelected(preferences.isShowSpecialDirectories());
		GridPane.setMargin(showSpecialDirectoriesCheckBox, new Insets(2.0, 0.0, 0.0, 0.0));
		viewPane.add(showSpecialDirectoriesCheckBox, 1, row++);

		// Set content of tab
		getTab(TabId.VIEW).setContent(viewPane);


		//----  Tab: directory entries

		// Check box: fix invalid dates and times of directory entries
		CheckBox fixDirEntryDatesTimesCheckBox = new CheckBox(FIX_INVALID_DATES_TIMES_STR);
		fixDirEntryDatesTimesCheckBox.setSelected(preferences.isFixDirEntryDatesTimes());

		// Pane: directory entries
		StackPane dirEntriesPane = new StackPane(fixDirEntryDatesTimesCheckBox);
		dirEntriesPane.setPadding(CONTROL_PANE_PADDING);

		// Set content of tab
		getTab(TabId.DIRECTORY_ENTRIES).setContent(dirEntriesPane);


		//----  Tab: format

		// Pane: format
		GridPane formatPane = new GridPane();
		formatPane.setHgap(CONTROL_H_GAP);
		formatPane.setVgap(CONTROL_V_GAP);
		formatPane.setAlignment(Pos.CENTER);
		formatPane.setPadding(CONTROL_PANE_PADDING);

		// Initialise column constraints
		column = new ColumnConstraints();
		column.setMinWidth(Region.USE_PREF_SIZE);
		column.setHalignment(HPos.RIGHT);
		formatPane.getColumnConstraints().add(column);

		column = new ColumnConstraints();
		column.setHalignment(HPos.LEFT);
		formatPane.getColumnConstraints().add(column);

		// Initialise row index
		row = 0;

		// Spinner: minimum number of sectors
		Spinner<Integer> minNumSectorsSpinner =
				SpinnerFactory.integerSpinner(MIN_MIN_NUM_SECTORS, MAX_MIN_NUM_SECTORS,
											  preferences.getFormatMinNumSectors(), MIN_NUM_SECTORS_SPINNER_NUM_DIGITS);
		formatPane.addRow(row++, new Label(MIN_NUM_SECTORS_STR), minNumSectorsSpinner);

		// Check box: removable media only
		CheckBox removableOnlyCheckBox = new CheckBox(REMOVABLE_ONLY_STR);
		removableOnlyCheckBox.setSelected(preferences.isFormatRemovableMediaOnly());
		GridPane.setMargin(removableOnlyCheckBox, new Insets(2.0, 0.0, 0.0, 0.0));
		formatPane.add(removableOnlyCheckBox, 1, row++);

		// Set content of tab
		getTab(TabId.FORMAT).setContent(formatPane);


		//----  Window

		// Create button: OK
		Button okButton = Buttons.hNoShrink(OK_STR);
		okButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		okButton.setOnAction(event ->
		{
			// Set result
			result = new Preferences(
				columnHeaderPopUpDelaySpinner.getValue(),
				showSpecialDirectoriesCheckBox.isSelected(),
				fixDirEntryDatesTimesCheckBox.isSelected(),
				minNumSectorsSpinner.getValue(),
				removableOnlyCheckBox.isSelected()
			);

			// Close dialog
			requestClose();
		});
		addButton(okButton, HPos.RIGHT);

		// Create button: cancel
		Button cancelButton = Buttons.hNoShrink(CANCEL_STR);
		cancelButton.getProperties().put(BUTTON_GROUP_KEY, BUTTON_GROUP1);
		cancelButton.setOnAction(event -> requestClose());
		addButton(cancelButton, HPos.RIGHT);

		// When window is closed, save index of selected tab and restore old theme
		setOnHiding(event ->
		{
			// Save index of selected tab
			selectedTabIndex = tabPane.getSelectionModel().getSelectedIndex();

			// If dialog was not accepted, restore old theme
			if ((result == null) && !Objects.equals(themeId, styleManager.getThemeId()))
				selectTheme.invoke(themeId);
		});

		// Apply new style sheet to scene
		applyStyleSheet();
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Class methods
////////////////////////////////////////////////////////////////////////

	public static Preferences show(
		Window		owner,
		Preferences	preferences)
	{
		return new PreferencesDialog(owner, preferences).showDialog();
	}

	//------------------------------------------------------------------

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

	@Override
	protected Preferences getResult()
	{
		return result;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Instance methods
////////////////////////////////////////////////////////////////////////

	private Tab getTab(
		TabId	tabId)
	{
		return tabPane.getTabs().get(tabId.ordinal());
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Enumerated types
////////////////////////////////////////////////////////////////////////


	// ENUMERATION: TAB IDENTIFIER


	private enum TabId
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		APPEARANCE
		(
			"Appearance"
		),

		VIEW
		(
			"View"
		),

		DIRECTORY_ENTRIES
		(
			"Directory entries"
		),

		FORMAT
		(
			"Format"
		);

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	String	text;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private TabId(
			String	text)
		{
			this.text = text;
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private Tab createTab()
		{
			Tab tab = new Tab(text);
			tab.setClosable(false);
			return tab;
		}

		//--------------------------------------------------------------

	}

	//==================================================================

}

//----------------------------------------------------------------------
