/*====================================================================*\

DirectoryPane.java

Class: directory pane.

\*====================================================================*/


// PACKAGE


package uk.blankaspect.fat32manager;

//----------------------------------------------------------------------


// IMPORTS


import java.lang.invoke.MethodHandles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.application.Platform;

import javafx.beans.property.SimpleObjectProperty;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.Group;
import javafx.scene.Node;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.shape.Shape;

import javafx.stage.Window;

import uk.blankaspect.common.css.CssSelector;

import uk.blankaspect.common.function.IFunction1;
import uk.blankaspect.common.function.IFunction2;

import uk.blankaspect.common.geometry.VHDirection;

import uk.blankaspect.ui.jfx.button.GraphicButton;
import uk.blankaspect.ui.jfx.button.ImageDataButton;

import uk.blankaspect.ui.jfx.filler.FillerUtils;

import uk.blankaspect.ui.jfx.image.ImageData;

import uk.blankaspect.ui.jfx.scene.SceneUtils;

import uk.blankaspect.ui.jfx.shape.Shapes;

import uk.blankaspect.ui.jfx.style.ColourProperty;
import uk.blankaspect.ui.jfx.style.FxProperty;
import uk.blankaspect.ui.jfx.style.FxPseudoClass;
import uk.blankaspect.ui.jfx.style.StyleConstants;
import uk.blankaspect.ui.jfx.style.StyleManager;

import uk.blankaspect.ui.jfx.text.Text2;
import uk.blankaspect.ui.jfx.text.TextUtils;

//----------------------------------------------------------------------


// CLASS: DIRECTORY PANE


public class DirectoryPane
	extends VBox
{

////////////////////////////////////////////////////////////////////////
//  Constants
////////////////////////////////////////////////////////////////////////

	private static final	double	TABLE_VIEW_HEIGHT	= 506.0;

	private static final	double	BUTTON_PANE_SPACING	= 2.0;

	private static final	double	TOP_PANE_SPACING	= 4.0;
	private static final	Insets	TOP_PANE_PADDING	= new Insets(3.0, 6.0, 3.0, 4.0);

	/** Miscellaneous strings. */
	private static final	String	BACK_TO_STR				= "Back to ";
	private static final	String	FORWARD_TO_STR			= "Forward to ";
	private static final	String	REFRESH_DIRECTORY_STR	= "Refresh directory";
	private static final	String	SHOW_PATHNAME_FIELD_STR	= "Show pathname field";
	private static final	String	SHOW_DIRECTORY_BAR_STR	= "Show directory bar";

	/** CSS colour properties. */
	private static final	List<ColourProperty>	COLOUR_PROPERTIES	= List.of
	(
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.DIRECTORY_BAR_TEXT,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_PANE)
					.desc(StyleClass.DIRECTORY_BAR_TEXT)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BACKGROUND_COLOUR,
			ColourKey.DIRECTORY_BAR_BACKGROUND,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.BORDER_COLOUR,
			ColourKey.DIRECTORY_BAR_BORDER,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.DIRECTORY_BAR_BUTTON_BACKGROUND_HOVERED,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.desc(GraphicButton.StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.HOVERED)
					.desc(GraphicButton.StyleClass.INNER_VIEW)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.STROKE,
			ColourKey.DIRECTORY_BAR_BUTTON_BORDER_HOVERED,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.desc(GraphicButton.StyleClass.GRAPHIC_BUTTON).pseudo(FxPseudoClass.HOVERED)
					.desc(GraphicButton.StyleClass.INNER_VIEW)
					.build()
		),
		ColourProperty.of
		(
			FxProperty.FILL,
			ColourKey.DIRECTORY_BAR_ARROWHEAD,
			CssSelector.builder()
					.cls(StyleClass.DIRECTORY_PANE)
					.desc(StyleClass.DIRECTORY_BAR)
					.desc(StyleClass.ARROWHEAD)
					.build()
		)
	);

	/** CSS style classes. */
	private interface StyleClass
	{
		String	DIRECTORY_PANE	= StyleConstants.APP_CLASS_PREFIX + "directory-pane";

		String	ARROWHEAD			= StyleConstants.CLASS_PREFIX + "arrowhead";
		String	DIRECTORY_BAR		= StyleConstants.CLASS_PREFIX + "directory-bar";
		String	DIRECTORY_BAR_TEXT	= StyleConstants.CLASS_PREFIX + "directory-bar-text";
	}

	/** Keys of colours that are used in colour properties. */
	private interface ColourKey
	{
		String	PREFIX	= StyleManager.colourKeyPrefix(MethodHandles.lookup().lookupClass().getEnclosingClass());

		String	DIRECTORY_BAR_ARROWHEAD					= PREFIX + "directoryBar.arrowhead";
		String	DIRECTORY_BAR_BACKGROUND				= PREFIX + "directoryBar.background";
		String	DIRECTORY_BAR_BORDER					= PREFIX + "directoryBar.border";
		String	DIRECTORY_BAR_BUTTON_BACKGROUND_HOVERED	= PREFIX + "directoryBar.button.background.hovered";
		String	DIRECTORY_BAR_BUTTON_BORDER_HOVERED		= PREFIX + "directoryBar.button.border.hovered";
		String	DIRECTORY_BAR_TEXT						= PREFIX + "directoryBar.text";
	}

////////////////////////////////////////////////////////////////////////
//  Instance variables
////////////////////////////////////////////////////////////////////////

	private	HBox					controlPane;
	private	MainDirectoryTableView	tableView;

////////////////////////////////////////////////////////////////////////
//  Static initialiser
////////////////////////////////////////////////////////////////////////

	static
	{
		// Register the style properties of this class with the style manager
		StyleManager.INSTANCE.register(DirectoryPane.class, COLOUR_PROPERTIES);
	}

////////////////////////////////////////////////////////////////////////
//  Constructors
////////////////////////////////////////////////////////////////////////

	public DirectoryPane()
	{
		// Set properties
		getStyleClass().add(StyleClass.DIRECTORY_PANE);

		// Create button: previous directory
		ImageDataButton previousButton = Images.imageButton(Images.ImageId.ARROW_LEFT);
		previousButton.setDisable(true);
		previousButton.setOnAction(event -> tableView.openPreviousDirectory());

		// Create button: next directory
		ImageDataButton nextButton = Images.imageButton(Images.ImageId.ARROW_RIGHT);
		nextButton.setDisable(true);
		nextButton.setOnAction(event -> tableView.openNextDirectory());

		// Create button: open parent directory
		ImageDataButton openParentButton = Images.imageButton(Images.ImageId.ARROW_UP);
		openParentButton.setDisable(true);
		openParentButton.setOnAction(event -> tableView.openParentDirectory());

		// Create button: refresh
		ImageDataButton refreshButton = Images.imageButton(Images.ImageId.REFRESH, REFRESH_DIRECTORY_STR);
		refreshButton.setOnAction(event -> tableView.refreshDirectory());

		// Create left button pane
		HBox leftButtonPane =
				new HBox(BUTTON_PANE_SPACING, previousButton, nextButton, openParentButton, refreshButton);
		leftButtonPane.setAlignment(Pos.CENTER_LEFT);

		// Create directory bar
		DirectoryBar directoryBar = new DirectoryBar();
		directoryBar.directory.addListener((observable, oldDirectory, directory) -> tableView.openDirectory(directory));

		// Create pathname field
		TextField pathnameField = new TextField();
		pathnameField.setVisible(false);
		pathnameField.setOnAction(event -> tableView.openDirectory(pathnameField.getText()));

		// Create navigation pane
		StackPane navigationPane = new StackPane(directoryBar, pathnameField);
		HBox.setHgrow(navigationPane, Priority.ALWAYS);

		// Button: navigation mode
		ImageDataButton navigationModeButton = Images.imageButton(Images.ImageId.PENCIL, SHOW_PATHNAME_FIELD_STR);
		navigationModeButton.setOnAction(event ->
		{
			if (directoryBar.isVisible())
			{
				directoryBar.setVisible(false);
				pathnameField.setVisible(true);
				pathnameField.requestFocus();
				pathnameField.selectAll();
				navigationModeButton.setImage(ImageData.image(Images.ImageId.BUTTON_BAR));
				navigationModeButton.setTooltipText(SHOW_DIRECTORY_BAR_STR);
			}
			else
			{
				directoryBar.setVisible(true);
				pathnameField.setVisible(false);
				navigationModeButton.setImage(ImageData.image(Images.ImageId.PENCIL));
				navigationModeButton.setTooltipText(SHOW_PATHNAME_FIELD_STR);
			}
		});

		// Create right button pane
		HBox rightButtonPane = new HBox(BUTTON_PANE_SPACING, navigationModeButton);
		rightButtonPane.setAlignment(Pos.CENTER_LEFT);

		// Create pane for navigation pane and button panes
		controlPane = new HBox(TOP_PANE_SPACING, leftButtonPane, navigationPane, rightButtonPane);
		controlPane.setPadding(TOP_PANE_PADDING);

		// Create directory table view
		tableView = new MainDirectoryTableView();
		tableView.setPrefHeight(TABLE_VIEW_HEIGHT);
		VBox.setVgrow(tableView, Priority.ALWAYS);

		// Add children to this pane
		getChildren().addAll(controlPane, tableView);

		// Update 'open parent directory' button and pathname when directory changes
		tableView.addDirectoryChangedListener(observable ->
		{
			// Get directory
			Fat32Directory directory = tableView.getDirectory();

			// Update 'open parent directory' button
			String text = (directory == null) ? null : tableView.getOpenParentDirectoryCommand();
			openParentButton.setTooltipText(text);
			openParentButton.setDisable(text == null);

			// Update directory bar
			directoryBar.directory.set(directory);

			// Update pathname field
			String pathname = (directory == null) ? "" : directory.getPathname();
			pathnameField.setText(pathname);
			Platform.runLater(() ->
			{
				pathnameField.end();
				pathnameField.deselect();
			});
		});

		// Update buttons when table-view history changes
		tableView.addHistoryChangedListener(observable ->
		{
			// Get history
			MainDirectoryTableView.History history = tableView.getHistory();

			// Update 'previous directory' button
			previousButton.setDisable(!history.hasPrevious());
			previousButton.setTooltipText(MainDirectoryTableView.getQuotedName(history.getPrevious(), BACK_TO_STR));

			// Update 'next directory' button
			nextButton.setDisable(!history.hasNext());
			nextButton.setTooltipText(MainDirectoryTableView.getQuotedName(history.getNext(), FORWARD_TO_STR));
		});

		// Open previous directory when mouse 'back' button is pressed; open next directory when mouse 'forward' button
		// is pressed
		addEventFilter(MouseEvent.MOUSE_PRESSED, event ->
		{
			switch (event.getButton())
			{
				case BACK:
					// Open previous directory
					tableView.openPreviousDirectory();

					// Consume event
					event.consume();
					break;

				case FORWARD:
					// Open next directory
					tableView.openNextDirectory();

					// Consume event
					event.consume();
					break;

				default:
					// do nothing
					break;
			}
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
//  Instance methods
////////////////////////////////////////////////////////////////////////

	public HBox getControlPane()
	{
		return controlPane;
	}

	//------------------------------------------------------------------

	public MainDirectoryTableView getTableView()
	{
		return tableView;
	}

	//------------------------------------------------------------------

////////////////////////////////////////////////////////////////////////
//  Member classes : non-inner classes
////////////////////////////////////////////////////////////////////////


	// CLASS: DIRECTORY BAR


	private static class DirectoryBar
		extends HBox
	{

	////////////////////////////////////////////////////////////////////
	//  Constants
	////////////////////////////////////////////////////////////////////

		private static final	Object	ELEMENT_NODE_KEY	= new Object();

		private static final	String	SEPARATOR	= "/";

		private static final	double	ARROWHEAD_HEIGHT_FACTOR	= 1.125;

		private static final	String	ROOT_DIR_DISPLAY_NAME	= "\u00ABROOT\u00BB";

		private static final	String	COPY_PATHNAME_STR	= "Copy pathname";

	////////////////////////////////////////////////////////////////////
	//  Instance variables
	////////////////////////////////////////////////////////////////////

		private	SimpleObjectProperty<Fat32Directory>	directory;
		private	int										elementIndex;
		private	GraphicButton							previousElementButton;
		private	GraphicButton							nextElementButton;

	////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////

		private DirectoryBar()
		{
			// Initialise instance variables
			directory = new SimpleObjectProperty<>();
			directory.addListener(observable -> update());

			// Set properties
			setAlignment(Pos.CENTER_LEFT);
			setBackground(SceneUtils.createColouredBackground(getColour(ColourKey.DIRECTORY_BAR_BACKGROUND)));
			setBorder(SceneUtils.createSolidBorder(getColour(ColourKey.DIRECTORY_BAR_BORDER)));
			getStyleClass().add(StyleClass.DIRECTORY_BAR);

			// Calculate size of icon of navigation buttons
			double textHeight = TextUtils.textHeight();
			double arrowheadHeight = Math.rint(ARROWHEAD_HEIGHT_FACTOR * textHeight);

			// Create factory for navigation button
			IFunction1<GraphicButton, Shape> navigationButtonFactory = arrowhead ->
			{
				// Set properties of icon
				arrowhead.setFill(getColour(ColourKey.DIRECTORY_BAR_ARROWHEAD));
				arrowhead.getStyleClass().add(StyleClass.ARROWHEAD);

				// Create button
				GraphicButton button = new GraphicButton(Shapes.tile(arrowhead, textHeight));
				button.setBackgroundColour(getColour(ColourKey.DIRECTORY_BAR_BUTTON_BACKGROUND_HOVERED),
										   GraphicButton.State.HOVERED);
				button.setBorderColour(getColour(ColourKey.DIRECTORY_BAR_BUTTON_BORDER_HOVERED),
									   GraphicButton.State.HOVERED);
				return button;
			};

			// Button: previous element
			Shape arrowhead = Shapes.arrowhead01(VHDirection.LEFT, arrowheadHeight);
			previousElementButton = navigationButtonFactory.invoke(arrowhead);
			previousElementButton.setDisable(true);
			previousElementButton.setOnAction(event ->
			{
				if (elementIndex > 0)
				{
					--elementIndex;
					update();
				}
			});
			getChildren().add(previousElementButton);

			// Filler
			getChildren().add(FillerUtils.hBoxFiller(0.0));

			// Button: next element
			arrowhead = Shapes.arrowhead01(VHDirection.RIGHT, arrowheadHeight);
			nextElementButton = navigationButtonFactory.invoke(arrowhead);
			nextElementButton.setDisable(true);
			nextElementButton.setOnAction(event ->
			{
				Fat32Directory dir = directory.get();
				int numElements = (dir == null) ? 0 : dir.getPath().size();
				if (elementIndex < numElements - 1)
				{
					++elementIndex;
					update();
				}
			});
			getChildren().add(nextElementButton);

			// Set minimum width of this container
			setMinWidth(2.0 + getButtonMaxX(previousElementButton) - getButtonMaxX(nextElementButton));

			// Update this container when its layout bounds change
			layoutBoundsProperty().addListener(observable -> update());
		}

		//--------------------------------------------------------------

		private static double getButtonMaxX(
			GraphicButton	button)
		{
			return button.getGraphic().getLayoutBounds().getMaxX() + 2.0 * GraphicButton.BORDER_WIDTH
						+ GraphicButton.DEFAULT_PADDING.getLeft() + GraphicButton.DEFAULT_PADDING.getRight();
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Instance methods
	////////////////////////////////////////////////////////////////////

		private void update()
		{
			// Initialise list of element-node information
			List<NodeInfo> nodeInfos = new ArrayList<>();

			// Create element nodes and populate list of element-node information
			Fat32Directory directory = this.directory.get();
			if (directory != null)
			{
				// Create factory for button for element of pathname
				IFunction2<GraphicButton, Fat32Directory, String> buttonFactory = (location, text) ->
				{
					// Create graphic for button
					Group textGroup = Text2.createTile(text, getColour(ColourKey.DIRECTORY_BAR_TEXT));
					textGroup.getChildren().get(1).getStyleClass().add(StyleClass.DIRECTORY_BAR_TEXT);

					// Create button
					GraphicButton button = new GraphicButton(textGroup);
					button.getProperties().put(ELEMENT_NODE_KEY, "");
					button.setBackgroundColour(getColour(ColourKey.DIRECTORY_BAR_BUTTON_BACKGROUND_HOVERED),
											   GraphicButton.State.HOVERED);
					button.setBorderColour(getColour(ColourKey.DIRECTORY_BAR_BUTTON_BORDER_HOVERED),
										   GraphicButton.State.HOVERED);
					button.setOnAction(event -> this.directory.set(location));

					// Display context menu on request
					button.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event ->
					{
						// Get window
						Window window = SceneUtils.getWindow(this);

						// Create context menu
						ContextMenu menu = new ContextMenu();

						// Menu item: copy pathname
						MenuItem menuItem =
								new MenuItem(COPY_PATHNAME_STR, Images.icon(Images.ImageId.COPY));
						menuItem.setOnAction(event0 ->
								Utils.copyToClipboard(window, COPY_PATHNAME_STR, location.getPathname()));
						menu.getItems().add(menuItem);

						// Display context menu at location of event
						menu.show(window, event.getScreenX(), event.getScreenY());
					});

					// Return button
					return button;
				};

				// Get path from root
				List<Fat32Directory> path = directory.getPath();

				// Create button for root and add it to list
				GraphicButton button = buttonFactory.invoke(path.get(0), ROOT_DIR_DISPLAY_NAME);
				nodeInfos.add(new NodeInfo(button));

				// Create buttons for remaining elements of path
				for (int i = 1; i < path.size(); i++)
				{
					// Update directory
					Fat32Directory dir = path.get(i);

					// Create separator and add it to list
					Group separator = Text2.createTile(SEPARATOR, getColour(ColourKey.DIRECTORY_BAR_TEXT));
					separator.getChildren().get(1).getStyleClass().add(StyleClass.DIRECTORY_BAR_TEXT);
					separator.getProperties().put(ELEMENT_NODE_KEY, "");
					nodeInfos.add(new NodeInfo(separator));

					// Create button and add it to list
					button = buttonFactory.invoke(dir, dir.getName());
					nodeInfos.add(new NodeInfo(button));
				}
			}

			// Adjust element index
			int numElementNodes = nodeInfos.size();
			int numElements = numElementNodes / 2 + 1;
			elementIndex = Math.min(Math.max(0, elementIndex), numElements - 1);

			// Update navigation buttons
			previousElementButton.setDisable(elementIndex <= 0);
			nextElementButton.setDisable(elementIndex >= numElements - 1);

			// Remove all old element nodes
			Iterator<Node> it = getChildren().iterator();
			while (it.hasNext())
			{
				if (it.next().getProperties().containsKey(ELEMENT_NODE_KEY))
					it.remove();
			}

			// Insert new element nodes
			if (numElementNodes > 0)
			{
				// Get available width of this container
				double width = getWidth() - getButtonMaxX(previousElementButton) - getButtonMaxX(nextElementButton);

				// Insert element nodes
				int index = 1;
				double maxX = 0.0;
				int i0 = 2 * elementIndex - 1;
				for (int i = i0; i < numElementNodes; i += 2)
				{
					// Increment maximum x coordinate
					if (i > i0)
						maxX += nodeInfos.get(i).maxX;
					maxX += nodeInfos.get(i + 1).maxX;

					// If maximum x coordinate exceeds width, stop
					if (maxX > width)
						break;

					// Insert separator and element node
					if (i > i0)
						getChildren().add(index++, nodeInfos.get(i).node);
					getChildren().add(index++, nodeInfos.get(i + 1).node);
				}
			}
		}

		//--------------------------------------------------------------

	////////////////////////////////////////////////////////////////////
	//  Member classes : non-inner classes
	////////////////////////////////////////////////////////////////////


		// CLASS: ELEMENT-NODE INFORMATION


		private static class NodeInfo
		{

		////////////////////////////////////////////////////////////////
		//  Instance variables
		////////////////////////////////////////////////////////////////

			private	Node	node;
			private	double	maxX;

		////////////////////////////////////////////////////////////////
		//  Constructors
		////////////////////////////////////////////////////////////////

			private NodeInfo(
				Node	node)
			{
				// Initialise instance variables
				this.node = node;
				maxX = (node instanceof GraphicButton button)
										? getButtonMaxX(button)
										: node.getLayoutBounds().getMaxX();
			}

			//----------------------------------------------------------

		}

		//==============================================================

	}

	//==================================================================

}

//----------------------------------------------------------------------
