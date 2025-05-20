package src.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;

import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import src.ADT.Board;
import src.Algorithm.AStar;
import src.Algorithm.GBFS;
import src.Algorithm.UCS;
import src.Algorithm.Algorithm;
import src.IO.Input;

/**
 * JavaFX GUI application for the Rush Hour Puzzle Solver.
 */
public class GUI extends Application 
{
    private Button aStarButton;
    private Button gbfsButton;
    private Button ucsButton;
    private Button distanceButton;
    private Button blockingButton;
    private Button loadButton;
    private Button solveButton;
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    private Button saveTxtButton;
    
    private Slider speedSlider;
    private Label fileNameLabel;
    private TextArea previewArea;
    private VBox mainContent;
    private Text stepCountText;
    
    private long searchTime;
    private int nodesExplored;
    private int currentStep;
    private int totalSteps;
    private Board board;
    private Input inputParser;
    private List<int[]> moves;
    private Pane boardPane;
    private Animation animation;

    private String selectedAlgorithm = "";
    private String selectedHeuristic = "";
    private String inputFilePath = "";
    private String fileName = "-";

    private boolean isPlaying = false;
    private boolean isPaused = false;

    /**
     * Start the JavaFX application.
     * 
     * @param stage The primary stage for the application
     */
    @Override
    public void start(Stage stage) 
    {
        // Load Poly font
        try 
        {
            Font polyFont = Font.loadFont(getClass().getResourceAsStream("/fonts/Poly-Regular.ttf"), 14);
            if (polyFont == null) 
            {
                System.out.println("Failed to load Poly font, path: /fonts/Poly-Regular.ttf");
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error loading font: " + e.getMessage());
            e.printStackTrace();
        }

        HBox mainLayout = new HBox();
        mainLayout.setStyle("-fx-background-color: white;");

        VBox sidebarWrapper = new VBox();
        sidebarWrapper.setAlignment(Pos.CENTER);
        sidebarWrapper.setPrefWidth(400);
        VBox.setVgrow(sidebarWrapper, Priority.ALWAYS);

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(25));
        sidebar.setStyle("-fx-background-color: white;");
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setMaxHeight(Region.USE_PREF_SIZE);

        // Title section
        VBox titleSection = new VBox(5);
        titleSection.setAlignment(Pos.CENTER);
        
        Label title = new Label("Rush Hour Puzzle Solver");
        title.setFont(Font.font("Poly", 28));
        
        titleSection.getChildren().addAll(title);
        VBox.setMargin(titleSection, new Insets(0, 0, 10, 0));

        // Preview section
        Label previewLabel = new Label("~ Preview ~");
        previewLabel.setFont(Font.font("Poly", 18));
        previewLabel.setAlignment(Pos.CENTER);
        
        previewArea = new TextArea();
        previewArea.setPrefHeight(220);
        previewArea.setPrefWidth(350);
        previewArea.setEditable(false);
        previewArea.setWrapText(true);
        previewArea.setFont(Font.font("Monospace", 12));
        previewArea.setStyle(
            "-fx-control-inner-background: white;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 1px;" +
            "-fx-focus-color: transparent;" +
            "-fx-faint-focus-color: transparent;"
        );

        // File loading section
        HBox fileLoadSection = new HBox(10);
        fileLoadSection.setAlignment(Pos.CENTER);
        
        loadButton = new Button("Load .txt");
        loadButton.setStyle(createButtonStyle(false));
        
        fileNameLabel = new Label(fileName);
        fileNameLabel.setFont(Font.font("Poly", 14));
        
        fileLoadSection.getChildren().addAll(loadButton, fileNameLabel);
        VBox.setMargin(fileLoadSection, new Insets(10, 0, 0, 0));
        
        // Add separator after file loading section
        Separator fileSeparator = new Separator();
        fileSeparator.setStyle("-fx-background-color: #CCCCCC;");
        VBox.setMargin(fileSeparator, new Insets(5, 0, 10, 0));

        // Algorithm section
        Label algorithmLabel = new Label("~ Algorithm ~");
        algorithmLabel.setFont(Font.font("Poly", 16));
        algorithmLabel.setAlignment(Pos.CENTER);
        
        HBox algorithmButtons = new HBox(10);
        algorithmButtons.setAlignment(Pos.CENTER);
        
        aStarButton = new Button("A*");
        gbfsButton = new Button("GBFS");
        ucsButton = new Button("UCS");
        
        aStarButton.setStyle(createButtonStyle(false));
        gbfsButton.setStyle(createButtonStyle(false));
        ucsButton.setStyle(createButtonStyle(false));
        
        // Set size and font for algorithm buttons
        aStarButton.setPrefWidth(70);
        aStarButton.setPrefHeight(40);
        aStarButton.setFont(Font.font("Poly", 12));
        
        gbfsButton.setPrefWidth(100);
        gbfsButton.setPrefHeight(40);
        gbfsButton.setFont(Font.font("Poly", 12));
        
        ucsButton.setPrefWidth(100);
        ucsButton.setPrefHeight(40);
        ucsButton.setFont(Font.font("Poly", 12));

        // Disable algorithm buttons initially
        aStarButton.setDisable(true);
        gbfsButton.setDisable(true);
        ucsButton.setDisable(true);

        algorithmButtons.getChildren().addAll(aStarButton, gbfsButton, ucsButton);
        VBox.setMargin(algorithmButtons, new Insets(0, 0, 15, 0));

        // Heuristic section
        Label heuristicLabel = new Label("~ Heuristics ~");
        heuristicLabel.setFont(Font.font("Poly", 16));
        heuristicLabel.setAlignment(Pos.CENTER);
        
        HBox heuristicButtons = new HBox(10);
        heuristicButtons.setAlignment(Pos.CENTER);
        
        distanceButton = new Button("Distance");
        blockingButton = new Button("Blocking");
        
        distanceButton.setStyle(createButtonStyle(false));
        blockingButton.setStyle(createButtonStyle(false));
        distanceButton.setDisable(true);
        blockingButton.setDisable(true);
        
        heuristicButtons.getChildren().addAll(distanceButton, blockingButton);

        // Add separator after heuristic section
        Separator heuristicSeparator = new Separator();
        heuristicSeparator.setStyle("-fx-background-color: #CCCCCC;");
        VBox.setMargin(heuristicSeparator, new Insets(10, 0, 10, 0));

        // Solve button
        solveButton = new Button("Solve");
        solveButton.setPrefWidth(350);
        solveButton.setStyle(createButtonStyle(false));
        solveButton.setDisable(true);

        // Add all sections to sidebar
        sidebar.getChildren().addAll(
            titleSection,
            previewLabel,
            previewArea,
            fileLoadSection,
            fileSeparator,
            algorithmLabel,
            algorithmButtons,
            heuristicLabel,
            heuristicButtons,
            heuristicSeparator,
            solveButton
        );

        sidebarWrapper.getChildren().add(sidebar);

        Separator separator = new Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        separator.setStyle("-fx-background-color: black;");

        mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPrefWidth(Region.USE_COMPUTED_SIZE);
        mainContent.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(mainContent, Priority.ALWAYS);

        // Initialize animation
        boardPane = new Pane();
        boardPane.setPrefSize(600, 600);
        boardPane.setStyle("-fx-background-color: white;");
        
        // Move counter text
        stepCountText = new Text("Move: 0 / 0");
        stepCountText.setFont(Font.font("Poly", 18));
        
        animation = new Animation();

        setupWelcomeView();

        // Bottom control section
        HBox bottomControls = new HBox(10);
        bottomControls.setPadding(new Insets(20));
        bottomControls.setAlignment(Pos.CENTER);
        bottomControls.setMaxWidth(Double.MAX_VALUE);

        // Speed slider section
        VBox speedSection = new VBox(5);
        speedSection.setAlignment(Pos.CENTER);
        speedSection.setPrefWidth(300);

        Label speedLabel = new Label("Speed:");
        speedLabel.setFont(Font.font("Poly", 14));
        speedLabel.setAlignment(Pos.CENTER);

        speedSlider = new Slider(0.1, 3.0, 1.0);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setMinorTickCount(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setDisable(true);
        speedSlider.setMaxWidth(250);

        // Simple styling to match the gray theme and Poly font
        speedSlider.setStyle(
            "-fx-control-inner-background: #E0E0E0;" +
            "-fx-accent: #808080;" +     
            "-fx-font-family: 'Poly';" +
            "-fx-disabled-opacity: 0.6;"      // Same as your buttons
        );

        speedSection.getChildren().addAll(speedLabel, speedSlider);

        // Playback controls section with larger buttons
        HBox playbackControls = new HBox(15);
        playbackControls.setAlignment(Pos.CENTER);

        playButton = new Button("▶");
        pauseButton = new Button("⏸");
        stopButton = new Button("⏹");

        // Set size and font for playback buttons
        playButton.setPrefWidth(60);
        playButton.setPrefHeight(40);
        playButton.setFont(Font.font("Poly", 8));

        pauseButton.setPrefWidth(60);
        pauseButton.setPrefHeight(40);
        pauseButton.setFont(Font.font("Poly", 8));

        stopButton.setPrefWidth(60);
        stopButton.setPrefHeight(40);
        stopButton.setFont(Font.font("Poly", 8));

        playButton.setStyle(createButtonStyle(false));
        pauseButton.setStyle(createButtonStyle(false));
        stopButton.setStyle(createButtonStyle(false));

        playButton.setDisable(true);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);

        playbackControls.getChildren().addAll(playButton, pauseButton, stopButton);

        // Save buttons section with larger buttons
        HBox saveControls = new HBox(15);
        saveControls.setAlignment(Pos.CENTER);

        saveTxtButton = new Button("Save .txt");

        saveTxtButton.setPrefWidth(120);
        saveTxtButton.setPrefHeight(40);
        saveTxtButton.setFont(Font.font("Poly", 16));
        saveTxtButton.setStyle(createButtonStyle(false));
        saveTxtButton.setDisable(true);

        saveControls.getChildren().addAll(saveTxtButton);

        // Add all sections to bottom controls with fixed spacing
        HBox.setHgrow(speedSection, Priority.ALWAYS);
        HBox.setHgrow(playbackControls, Priority.ALWAYS);
        HBox.setHgrow(saveControls, Priority.ALWAYS);
        bottomControls.getChildren().addAll(speedSection, playbackControls, saveControls);

        // Add the bottom controls to the main layout
        VBox rightSection = new VBox();
        rightSection.setAlignment(Pos.CENTER);
        rightSection.getChildren().addAll(mainContent, bottomControls);
        VBox.setVgrow(mainContent, Priority.ALWAYS);

        // Ensure the right section takes up all available space
        HBox.setHgrow(rightSection, Priority.ALWAYS);
        mainLayout.getChildren().addAll(sidebarWrapper, separator, rightSection);

        Scene scene = new Scene(mainLayout);
        
        // Add app icon
        try 
        {
            Image icon = new Image(getClass().getResourceAsStream("/images/icon.png"));
            stage.getIcons().add(icon);
        } 
        catch (Exception e) 
        {
            System.err.println("Error loading icon: " + e.getMessage());
        }
        
        stage.setTitle("Rush Hour Puzzle Solver");
        stage.setMaximized(true);

        scene.setOnKeyPressed(event -> 
        {
            switch (event.getCode()) 
            {
                case F11:
                    stage.setFullScreen(!stage.isFullScreen());
                    break;
                case ENTER:
                    if (event.isAltDown()) 
                    {
                        stage.setFullScreen(!stage.isFullScreen());
                    }
                    break;
                default:
                    break;
            }
        });

        stage.setScene(scene);
        stage.show();

        setupEventHandlers();
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() 
        {
            @Override
            public void handle(WindowEvent e) 
            {
                File currentDir = new File(System.getProperty("user.dir"));
                File file = new File(currentDir.getParentFile().getParentFile() + "/test/" + "temp" + "-output.png");
        
                if (file.exists()) file.delete();
                System.exit(0);
            }
        });
    }

    /**
     * Set up the welcome view.
     */
    private void setupWelcomeView() 
    {
        mainContent.getChildren().clear();
    
        Label welcomeLabel = new Label("Made by Love ~ Acep Villagers");
        welcomeLabel.setFont(Font.font("Poly", 32));
        welcomeLabel.setAlignment(Pos.CENTER);
        
        try 
        {
            Image mainImage = new Image(getClass().getResourceAsStream("/images/main.png"));
            if (mainImage.isError()) 
            {
                System.out.println("Error loading image: " + mainImage.getException().getMessage());
            }
            ImageView mainImageView = new ImageView(mainImage);
            mainImageView.setFitWidth(300);
            mainImageView.setPreserveRatio(true);
            
            mainContent.getChildren().addAll(mainImageView, welcomeLabel);
            
        } 
        catch (Exception e) 
        {
            System.err.println("Error loading main image: " + e.getMessage());
            e.printStackTrace();
            mainContent.getChildren().add(welcomeLabel);
        }
    }

    /**
     * Set up the results view.
     */
private void setupResultsView()
{
    mainContent.getChildren().clear();
    
    // Use a VBox with explicit spacing for the entire content layout
    VBox contentLayout = new VBox();
    contentLayout.setAlignment(Pos.TOP_CENTER);
    contentLayout.setSpacing(20); // Good spacing between major sections
    
    // 1. Title section
    Label resultsLabel = new Label("~ Results ~");
    resultsLabel.setFont(Font.font("Poly", 24));
    resultsLabel.setAlignment(Pos.CENTER);

    VBox.setMargin(resultsLabel, new Insets(0, 0, 10, 0));
    
    try
    {
        // 3. Board container (centered)
        StackPane boardContainer = new StackPane();
        boardContainer.setAlignment(Pos.CENTER);
        
        // Your adaptive cell size calculation
        int maxWidth = 400;
        int maxHeight = 400;
        int cellWidthBased = (maxWidth - 20) / board.getCols();
        int cellHeightBased = (maxHeight - 20) / board.getRows();
        int cellSize = Math.min(cellWidthBased, cellHeightBased);
        cellSize = Math.max(cellSize, 20);
        
        // Set the cell size in the animation object
        animation.setCellSize(cellSize);
        
        // Calculate final dimensions
        double boardWidth = board.getCols() * cellSize + 20;
        double boardHeight = board.getRows() * cellSize + 20;
        
        boardPane.setMinSize(boardWidth, boardHeight);
        boardPane.setMaxSize(boardWidth, boardHeight);
        boardPane.setPrefSize(boardWidth, boardHeight);
        boardContainer.getChildren().add(boardPane);
        
        // 5. Stats container
        VBox statsContainer = new VBox(10); // 10px spacing between stat items
        statsContainer.setAlignment(Pos.CENTER);
        
        // Initialize step counter
        currentStep = 0;
        totalSteps = moves != null ? moves.size() : 0;
        stepCountText.setText("Move: " + currentStep + " / " + totalSteps);
        stepCountText.setFont(Font.font("Poly", 18));
        
        Label outLabel = new Label("Nodes Explored: " + nodesExplored + "    |    Searching Time: " + searchTime + " ms");
        outLabel.setFont(Font.font("Poly", 14));
        
        statsContainer.getChildren().addAll(stepCountText, outLabel);
        
        // Add all components to the main content layout in order with proper spacing
        contentLayout.getChildren().addAll(
            resultsLabel,
            boardContainer,
            statsContainer
        );
        
        // Add the content layout to the main content
        mainContent.getChildren().add(contentLayout);
        
        // Enable appropriate controls
        speedSlider.setDisable(false);
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        stopButton.setDisable(false);
        saveTxtButton.setDisable(false);
    } 
    catch (Exception e) 
    {
        System.err.println("Error displaying results: " + e.getMessage());
        showAlert("Error displaying results: " + e.getMessage());
    }
}

    /**
     * Show an error view with the provided error message.
     * 
     * @param errorMessage Error message
     */
    private void showErrorView(String errorMessage) 
    {
        mainContent.getChildren().clear();
        
        Label resultsLabel = new Label("~ Error ~");
        resultsLabel.setFont(Font.font("Poly", 32));
        resultsLabel.setAlignment(Pos.CENTER);
        
        Label errorLabel = new Label(errorMessage);
        errorLabel.setFont(Font.font("Poly", 14));
        errorLabel.setTextFill(javafx.scene.paint.Color.RED);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(600);
        
        VBox errorContainer = new VBox(20);
        errorContainer.setAlignment(Pos.CENTER);
        errorContainer.getChildren().addAll(resultsLabel, errorLabel);
        
        mainContent.getChildren().add(errorContainer);
        
        // Make sure animation controls are properly reset
        resetAnimationControls();
        
        // Keep solve button enabled if algorithm and heuristic are selected
        if (!selectedAlgorithm.isEmpty()) {
            if (selectedAlgorithm.equals("UCS") || !selectedHeuristic.isEmpty()) {
                solveButton.setDisable(false);
            }
        }
    }

    /**
     * Set up event handlers for the GUI.
     */
    private void setupEventHandlers() 
    {
        // Load button handler
        loadButton.setOnAction(e -> handleLoadButtonClick());
        
        // Algorithm button handlers
        aStarButton.setOnAction(e -> handleAlgorithmSelection("A*"));
        gbfsButton.setOnAction(e -> handleAlgorithmSelection("GBFS"));
        ucsButton.setOnAction(e -> handleAlgorithmSelection("UCS"));
        
        // Heuristic button handlers
        distanceButton.setOnAction(e -> handleHeuristicSelection("Manhattan"));
        blockingButton.setOnAction(e -> handleHeuristicSelection("Blocking"));
        
        // Solve button handler
        solveButton.setOnAction(e -> handleSolveButtonClick());
        
        // Animation control handlers
        playButton.setOnAction(e -> handlePlayButtonClick());
        pauseButton.setOnAction(e -> handlePauseButtonClick());
        stopButton.setOnAction(e -> handleStopButtonClick());
        
        // Save button handlers
        saveTxtButton.setOnAction(e -> handleSaveTxtClick());
        
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
        {
            if (animation != null && animation.getMovesAnimation() != null)
                animation.setSpeed(newVal.doubleValue());
        });
    }
    
    /**
     * Handle the Load button click event.
     */
    private void handleLoadButtonClick() 
    {
        File initialDirectory = null;
        try 
        {
            File currentDir = new File(System.getProperty("user.dir"));
            File testDir = new File(currentDir.getParentFile().getParentFile() + "/test");
            
            if (testDir.exists() && testDir.isDirectory()) initialDirectory = testDir;
            else initialDirectory = currentDir;
        } 
        catch (Exception e) 
        {
            System.err.println("Error setting initial directory: " + e.getMessage());
            initialDirectory = new File(System.getProperty("user.dir"));
        }
        
        File selectedFile = OutputGUI.showCustomFileDialog(
            loadButton.getScene().getWindow(),
            "Load Puzzle File",
            initialDirectory,
            null,
            false
        );
        
        if (selectedFile != null) 
        {
            String filePath = selectedFile.getAbsolutePath();
            
            if (filePath.toLowerCase().endsWith(".txt")) 
            {
                fileName = selectedFile.getName();
                fileNameLabel.setText(fileName);
                
                this.inputFilePath = filePath;
                loadPreview(filePath);
                
                resetUIState();
                enableAlgorithmButtons();
            } 
            else showAlert("Please select a .txt file");
        }
    }

    /**
     * Handle algorithm selection.
     * 
     * @param algorithm Selected algorithm
     */
    private void handleAlgorithmSelection(String algorithm) 
    {
        selectedAlgorithm = algorithm;
        
        aStarButton.setStyle(createButtonStyle(false));
        gbfsButton.setStyle(createButtonStyle(false));
        ucsButton.setStyle(createButtonStyle(false));
        
        switch (algorithm) 
        {
            case "A*":
                aStarButton.setStyle(createButtonStyle(true));
                break;
            case "GBFS":
                gbfsButton.setStyle(createButtonStyle(true));
                break;
            case "UCS":
                ucsButton.setStyle(createButtonStyle(true));
                break;
        }
        
        selectedHeuristic = "";
        distanceButton.setStyle(createButtonStyle(false));
        blockingButton.setStyle(createButtonStyle(false));
        
        if (algorithm.equals("UCS")) 
        {
            distanceButton.setDisable(true);
            blockingButton.setDisable(true);
            solveButton.setDisable(false);
        } 
        else 
        {
            distanceButton.setDisable(false);
            blockingButton.setDisable(false);
            solveButton.setDisable(true);
        }
    }
    
    /**
     * Handle heuristic selection.
     * 
     * @param heuristic Selected heuristic
     */
    private void handleHeuristicSelection(String heuristic) 
    {
        selectedHeuristic = heuristic;
        
        // Reset button styles
        distanceButton.setStyle(createButtonStyle(false));
        blockingButton.setStyle(createButtonStyle(false));
        
        // Highlight selected button
        switch (heuristic) 
        {
            case "Manhattan":
                distanceButton.setStyle(createButtonStyle(true));
                break;
            case "Blocking":
                blockingButton.setStyle(createButtonStyle(true));
                break;
        }
        
        // Enable solve button if algorithm is selected
        solveButton.setDisable(selectedAlgorithm.isEmpty());
    }

    /**
     * Handle the Solve button click event.
     * Fixed to ensure proper validation checks for input file format.
     */
    private void handleSolveButtonClick() 
    {
        solveButton.setDisable(true);
        
        isPlaying = false;
        isPaused = false;
        
        resetAnimationControls();
        
        // Use the stored input file path
        File file = new File(inputFilePath);
        
        ProgressIndicator progress = new ProgressIndicator();
        VBox loadingBox = new VBox(10);
        loadingBox.setAlignment(Pos.CENTER);
        
        Label loadingLabel = new Label("Loading and solving puzzle...");
        loadingLabel.setFont(Font.font("Poly", 14));
        
        loadingBox.getChildren().addAll(progress, loadingLabel);
        mainContent.getChildren().clear();
        mainContent.getChildren().add(loadingBox);
        
        Task<List<int[]>> solveTask = new Task<List<int[]>>() 
        {
            @Override
            protected List<int[]> call() throws Exception 
            {
                try 
                {
                    inputParser = new Input();
                    inputParser.validateFile(file);
                    
                    if (inputParser.hasError()) 
                    {
                        final String errorMsg = inputParser.getErrorMsg();
                        Platform.runLater(() -> 
                        {
                            board = new Board(0, 0, 0, 0, 0, "", errorMsg);
                            showErrorView(errorMsg);
                            solveButton.setDisable(false);
                        });
                        return null;
                    }
            
                    inputParser.readInput(file.getAbsolutePath());
                    
                    if (inputParser.hasError()) 
                    {
                        final String errorMsg = inputParser.getErrorMsg();
                        Platform.runLater(() -> 
                        {
                    
                            board = new Board(0, 0, 0, 0, 0, "", errorMsg);
                            showErrorView(errorMsg);
                            solveButton.setDisable(false);
                        });
                        return null;
                    }
                    
                    board = new Board(
                        inputParser.getRows(),
                        inputParser.getCols(),
                        inputParser.getNumCars(),
                        inputParser.getExitRow(),
                        inputParser.getExitCol(),
                        inputParser.getExitSide(),
                        null
                    );
                    
                    board.loadConfiguration(inputParser.getBoardConfig());
                    
                    if (board.hasError()) 
                    {
                        final String errorMsg = board.getErrorMsg();
                        Platform.runLater(() -> 
                        {
                            board = new Board(0, 0, 0, 0, 0, "", errorMsg);
                            showErrorView(errorMsg);
                            solveButton.setDisable(false);
                        });
                        return null;
                    }
                    
                    // Solve the puzzle using the selected algorithm
                    Algorithm algorithm = null;
                    List<int[]> solutionMoves = null;
                
                    switch (selectedAlgorithm) 
                    {
                        case "A*":
                            algorithm = new AStar(board);
                            solutionMoves = algorithm.solve(selectedHeuristic);
                            break;
                        case "GBFS":
                            algorithm = new GBFS(board);
                            solutionMoves = algorithm.solve(selectedHeuristic);
                            break;
                        case "UCS":
                            algorithm = new UCS(board);
                            solutionMoves = algorithm.solve("none");
                            break;
                    }
                    
                    if (algorithm != null) 
                    {
                        searchTime = algorithm.getExecutionTime();
                        nodesExplored = algorithm.getNodesExplored();
                    }
                    
                    return solutionMoves;
                }
                catch (Exception ex) 
                {
                    final String errorMsg = "Unexpected error: " + ex.getMessage();
                    Platform.runLater(() -> 
                    {
                        board = new Board(0, 0, 0, 0, 0, "", errorMsg);
                        showErrorView(errorMsg);
                        solveButton.setDisable(false);
                    });
                    ex.printStackTrace();
                    return null;
                }
            }
        };
        
        solveTask.setOnSucceeded(event -> 
        {
            moves = solveTask.getValue();
            
            if (moves != null && !moves.isEmpty()) 
            {
                totalSteps = moves.size();
                currentStep = 0;
                
                Board displayBoard = board.copy();
                displayBoard.setCurrentMovedCarIndex(null);
        
                animation.initialize(displayBoard, moves, stepCountText);

                setupResultsView();
                
                animation.drawBoard(displayBoard, boardPane);

                playButton.setDisable(false);
                stopButton.setDisable(false);
                speedSlider.setDisable(false);
                saveTxtButton.setDisable(false);
            } 
            else if (board != null && board.hasError()) 
            {
                showErrorView(board.getErrorMsg());
                saveTxtButton.setDisable(false);
            }
            else if (moves == null)
            {
                saveTxtButton.setDisable(false);
            }
            else 
            {
                showErrorView("No solution found after exploring " + nodesExplored + " nodes.");
                saveTxtButton.setDisable(false);
            }
            
            solveButton.setDisable(false);
        });
        
        solveTask.setOnFailed(event -> 
        {
            Throwable exception = solveTask.getException();
            showErrorView("Error solving puzzle: " + exception.getMessage());
            solveButton.setDisable(false);
        });
        
        new Thread(solveTask).start();
    }

    /**
     * Handle the Play button click event.
     */
    private void handlePlayButtonClick() 
    {
        if (animation != null) 
        {
            // Animation with current speed on first play
            if (animation.getMovesAnimation() == null || (!isPlaying && !isPaused)) 
            {
                animation.createMovesAnimation(board, moves, boardPane, speedSlider.getValue());
            }
            
            animation.play(speedSlider.getValue());
            isPlaying = true;
            isPaused = false;
            
            // Update button states
            playButton.setDisable(true);
            pauseButton.setDisable(false);
            stopButton.setDisable(false);
            
            // Disable speed slider during playback
            speedSlider.setDisable(true);
        }
    }
    
    /**
     * Handle the Pause button click event.
     */
    private void handlePauseButtonClick() 
    {
        if (animation != null) 
        {
            animation.pause();
            isPlaying = false;
            isPaused = true;
            
            // Update button states
            playButton.setDisable(false);
            pauseButton.setDisable(true);
            stopButton.setDisable(false);
            
            // Enable speed slider when paused
            speedSlider.setDisable(false);
        }
    }
    
    /**
     * Handle the Stop button click event.
     */
    private void handleStopButtonClick() 
    {
        if (animation != null) 
        {
            animation.stop();
            isPlaying = false;
            isPaused = false;
            currentStep = 0;
            
            playButton.setDisable(false);
            pauseButton.setDisable(true);
            stopButton.setDisable(false);
            
            speedSlider.setDisable(false);
            animation.drawBoard(board, boardPane);
        }
    }
    
    /**
     * Handle the Save TXT button click event with custom file dialog.
     */
    private void handleSaveTxtClick() 
    {
        try 
        {   
            String defaultFileName = fileName.replace(".txt", "") + "-output.txt";
            
            File initialDirectory = null;
            try 
            {
                File inputFile = new File(inputFilePath);
                initialDirectory = inputFile.getParentFile();
            } 
            catch (Exception e) 
            {
                try {
                    File currentDir = new File(System.getProperty("user.dir"));
                    File testDir = new File(currentDir.getParentFile().getParentFile() + "/test");
                    
                    if (testDir.exists() && testDir.isDirectory()) initialDirectory = testDir;
                    else initialDirectory = currentDir;
                } 
                catch (Exception ex) 
                {
                    System.err.println("Error setting initial save directory: " + ex.getMessage());
                    initialDirectory = new File(System.getProperty("user.dir"));
                }
            }
            
            // Show custom file save dialog
            File outputFile = OutputGUI.showCustomFileDialog(
                saveTxtButton.getScene().getWindow(),
                "Save Solution File",
                initialDirectory,
                defaultFileName,
                true 
            );
            
            if (outputFile != null) 
            {
                String outputPath = outputFile.getAbsolutePath();
                
                // If board has an error, save the error information
                if (board.hasError()) 
                {
                    OutputGUI output = new OutputGUI(outputPath, 
                                                    board, 
                                                    0,
                                                    0,
                                                    null);
                    
                    if (output.saveToText()) 
                    {
                        showInfo("Successfully saved error information to " + outputFile.getName());
                    }
                } 
                else if (moves != null && !moves.isEmpty()) 
                {
                    OutputGUI output = new OutputGUI(outputPath, 
                                                    board, 
                                                    searchTime, 
                                                    nodesExplored, 
                                                    moves);
                    
                    if (output.saveToText()) 
                    {
                        showInfo("Successfully saved solution to " + outputFile.getName());
                    }
                }
                else {
                    // No solution found case
                    OutputGUI output = new OutputGUI(outputPath, 
                                                    board, 
                                                    searchTime, 
                                                    nodesExplored, 
                                                    null);
                                                    
                    if (output.saveToText()) 
                    {
                        showInfo("Successfully saved to " + outputFile.getName());
                    }
                }
            }
        } 
        catch (IOException e) 
        {
            showAlert("Error saving solution: " + e.getMessage());
        }
    }

    /**
     * Load the preview of the selected test case.
     * 
     * @param filePath Path to the file to load
     */
    private void loadPreview(String filePath) 
    {
        try 
        {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            previewArea.setText(content);
            previewArea.positionCaret(0);
        } 
        catch (Exception ex) 
        {
            previewArea.setText("Error loading preview: " + ex.getMessage());
        }
    }
    
    /**
     * Reset the UI state for a new file.
     */
    private void resetUIState() 
    {
        // Reset algorithm and heuristic selection state
        selectedAlgorithm = "";
        aStarButton.setStyle(createButtonStyle(false));
        gbfsButton.setStyle(createButtonStyle(false));
        ucsButton.setStyle(createButtonStyle(false));
        
        selectedHeuristic = "";
        distanceButton.setStyle(createButtonStyle(false));
        blockingButton.setStyle(createButtonStyle(false));
        distanceButton.setDisable(true);
        blockingButton.setDisable(true);
        
        solveButton.setDisable(true);
        resetAnimationControls();
        setupWelcomeView();
        
        board = null;
        moves = null;
        searchTime = 0;
        nodesExplored = 0;
    }
    
    /**
     * Reset animation controls.
     */
    private void resetAnimationControls() 
    {
        // Stop any running animation
        if (animation != null)
            animation.stop();
        
        // Reset animation state
        isPlaying = false;
        isPaused = false;
        currentStep = 0;
        totalSteps = 0;
        
        // Update text display
        if (stepCountText != null) 
            stepCountText.setText("Move: 0 / 0");
    
        playButton.setDisable(true);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        speedSlider.setDisable(true);
        saveTxtButton.setDisable(true);
    }
    
    /**
     * Enable algorithm buttons.
     */
    private void enableAlgorithmButtons() 
    {
        aStarButton.setDisable(false);
        gbfsButton.setDisable(false);
        ucsButton.setDisable(false);
    }

    /**
     * Create a style string for buttons.
     * 
     * @param isSelected Whether the button is selected
     * @return Button style string
     */
    private String createButtonStyle(boolean isSelected) 
    {
        String backgroundColor = isSelected ? "#808080" : "#E0E0E0";
        String textColor = isSelected ? "white" : "black";
        
        return "-fx-background-color: " + backgroundColor + ";" +
               "-fx-text-fill: " + textColor + ";" +
               "-fx-background-radius: 5;" +
               "-fx-padding: 8 20;" +
               "-fx-font-family: 'Poly';";
    }
    
    /**
     * Show the alert dialog
     * 
     * @param message Alert message
     */
    private void showAlert(String message) 
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(null);
        alert.setHeaderText(null); 
        
        Label contentLabel = new Label(message);
        contentLabel.setFont(Font.font("Poly", 14));
        contentLabel.setWrapText(true);
        contentLabel.setPrefWidth(350);
        contentLabel.setMaxWidth(350);
        contentLabel.setPadding(new Insets(10, 5, 10, 5));
        
        alert.getDialogPane().setContent(contentLabel);
        alert.getDialogPane().getStyleClass().remove("header-panel");
        
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("custom-alert");
        
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setFont(Font.font("Poly", 12));
        okButton.setStyle(createButtonStyle(false));
        
        Stage alertStage = (Stage) dialogPane.getScene().getWindow();
        StackPane alertRoot = new StackPane();
        alertRoot.getChildren().add(dialogPane.getContent());
        
        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10, 5, 5, 5));
        buttonBar.getChildren().add(okButton);
        
        VBox alertLayout = new VBox(10);
        alertLayout.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 15px;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 1px;"
        );
        alertLayout.getChildren().addAll(alertRoot, buttonBar);
        
        Scene newScene = new Scene(alertLayout);
        newScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        alertStage.setScene(newScene);
        
        // Center the dialog on the screen
        alertStage.setOnShown(e -> 
        {
            Stage primaryStage = (Stage) mainContent.getScene().getWindow();
            
            double alertWidth = alertStage.getWidth();
            double alertHeight = alertStage.getHeight();
            
            double centerX = primaryStage.getX() + (primaryStage.getWidth() - alertWidth) / 2;
            double centerY = primaryStage.getY() + (primaryStage.getHeight() - alertHeight) / 2;
            
            alertStage.setX(centerX);
            alertStage.setY(centerY);
        });
        
        alert.showAndWait();
    }

    /**
     * Show the information dialog
     * 
     * @param message Information message
     */
    private void showInfo(String message) 
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        
        Label contentLabel = new Label(message);
        contentLabel.setFont(Font.font("Poly", 14));
        contentLabel.setWrapText(true);
        contentLabel.setPrefWidth(350);
        contentLabel.setMaxWidth(350);
        contentLabel.setPadding(new Insets(10, 5, 10, 5));
        
        alert.getDialogPane().setContent(contentLabel);
        alert.getDialogPane().getStyleClass().remove("header-panel");
        
        javafx.scene.control.DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("custom-alert");
        
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setFont(Font.font("Poly", 14));
        okButton.setStyle(createButtonStyle(false));
        
        Stage alertStage = (Stage) dialogPane.getScene().getWindow();
        alertStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        
        StackPane alertRoot = new StackPane();
        alertRoot.getChildren().add(dialogPane.getContent());
        
        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10, 5, 5, 5));
        buttonBar.getChildren().add(okButton);
        
        VBox alertLayout = new VBox(10);
        alertLayout.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 15px;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 1px;"
        );
        alertLayout.getChildren().addAll(alertRoot, buttonBar);
        
        Scene newScene = new Scene(alertLayout);
        newScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        alertStage.setScene(newScene);
        
        alertStage.setOnShown(e -> {
            Stage primaryStage = (Stage) mainContent.getScene().getWindow();
            
            double alertWidth = alertStage.getWidth();
            double alertHeight = alertStage.getHeight();
            
            double centerX = primaryStage.getX() + (primaryStage.getWidth() - alertWidth) / 2;
            double centerY = primaryStage.getY() + (primaryStage.getHeight() - alertHeight) / 2;
            
            alertStage.setX(centerX);
            alertStage.setY(centerY);
        });
        
        alert.showAndWait();
    }

    /**
     * Main method to launch the JavaFX application.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) 
    {
        launch();
    }
}