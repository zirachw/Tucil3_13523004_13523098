package src.GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Arrays;

import src.ADT.Board;

/**
 * JavaFX GUI application for the Rush Hour Puzzle Solver.
 */
public class GUI extends Application 
{
    // Algorithm buttons
    private Button aStarButton;
    private Button fringeButton;
    private Button gbfsButton;
    private Button ucsButton;
    
    // Heuristic buttons
    private Button distanceButton;
    private Button blockingButton;
    
    // Control buttons
    private Button loadButton;
    private Button solveButton;
    
    // Animation control buttons
    private Button playButton;
    private Button pauseButton;
    private Button stopButton;
    
    // Save button
    private Button saveTxtButton;
    
    // Speed slider
    private Slider speedSlider;
    
    // Text elements
    private Label fileNameLabel;
    private TextArea previewArea;
    private VBox mainContent;
    
    // State variables
    private String fileName = "~";
    private long searchTime;
    private int nodesExplored;
    private int currentStep;
    private int totalSteps;
    private Board board;
    
    // Algorithm and heuristic selection
    private String selectedAlgorithm = "";
    private String selectedHeuristic = "";
    
    // Animation state
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
        sidebarWrapper.setPrefWidth(400); // Increased sidebar width
        VBox.setVgrow(sidebarWrapper, Priority.ALWAYS);

        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(25)); // Increased padding
        sidebar.setStyle("-fx-background-color: white;");
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setMaxHeight(Region.USE_PREF_SIZE);

        // Title section
        VBox titleSection = new VBox(5);
        titleSection.setAlignment(Pos.CENTER);
        
        Label title = new Label("Rush Hour Puzzle Solver");
        title.setFont(Font.font("Poly", 28)); // Increased font size
        
        titleSection.getChildren().addAll(title);
        VBox.setMargin(titleSection, new Insets(0, 0, 10, 0)); // Increased margin

        // Preview section
        Label previewLabel = new Label("~ Preview ~");
        previewLabel.setFont(Font.font("Poly", 18)); // Increased font size
        previewLabel.setAlignment(Pos.CENTER);
        
        previewArea = new TextArea();
        previewArea.setPrefHeight(220);
        previewArea.setPrefWidth(350); // Increased width
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
        algorithmLabel.setFont(Font.font("Poly", 16)); // Increased font size
        algorithmLabel.setAlignment(Pos.CENTER);
        
        HBox algorithmButtons = new HBox(10); // Increased spacing
        algorithmButtons.setAlignment(Pos.CENTER);
        
        aStarButton = new Button("A*");
        fringeButton = new Button("Fringe");
        gbfsButton = new Button("GBFS");
        ucsButton = new Button("UCS");
        
        aStarButton.setStyle(createButtonStyle(false));
        fringeButton.setStyle(createButtonStyle(false));
        gbfsButton.setStyle(createButtonStyle(false));
        ucsButton.setStyle(createButtonStyle(false));
        
        // Set size and font for algorithm buttons
        aStarButton.setPrefWidth(70);
        aStarButton.setPrefHeight(40);
        aStarButton.setFont(Font.font("Poly", 12));
        
        fringeButton.setPrefWidth(100);
        fringeButton.setPrefHeight(40);
        fringeButton.setFont(Font.font("Poly", 12));
        
        gbfsButton.setPrefWidth(100);
        gbfsButton.setPrefHeight(40);
        gbfsButton.setFont(Font.font("Poly", 12));
        
        ucsButton.setPrefWidth(100);
        ucsButton.setPrefHeight(40);
        ucsButton.setFont(Font.font("Poly", 12));

        // Disable algorithm buttons initially
        aStarButton.setDisable(true);
        fringeButton.setDisable(true);
        gbfsButton.setDisable(true);
        ucsButton.setDisable(true);

        algorithmButtons.getChildren().addAll(aStarButton, fringeButton, gbfsButton, ucsButton);
        VBox.setMargin(algorithmButtons, new Insets(0, 0, 15, 0));

        // Heuristic section
        Label heuristicLabel = new Label("~ Heuristics ~");
        heuristicLabel.setFont(Font.font("Poly", 16)); // Increased font size
        heuristicLabel.setAlignment(Pos.CENTER);
        
        HBox heuristicButtons = new HBox(10); // Increased spacing
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

        setupWelcomeView();        

        // Create the bottom control section with center alignment
        HBox bottomControls = new HBox(20);
        bottomControls.setPadding(new Insets(20));
        bottomControls.setAlignment(Pos.CENTER); // Centers children horizontally in the HBox
        bottomControls.setMaxWidth(Double.MAX_VALUE); // Allow HBox to expand to full width of parent

        // Speed slider section
        VBox speedSection = new VBox(5);
        speedSection.setAlignment(Pos.CENTER);
        speedSection.setPrefWidth(300);

        Label speedLabel = new Label("Speed:");
        speedLabel.setFont(Font.font("Poly", 14)); // Increased font size
        speedLabel.setAlignment(Pos.CENTER); // Center the label text

        speedSlider = new Slider(0.1, 3.0, 1.0);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setMinorTickCount(1);
        speedSlider.setSnapToTicks(true);
        speedSlider.setDisable(true);
        speedSlider.setMaxWidth(250); // Constrain slider width

        // Simple styling to match the gray theme and Poly font
        speedSlider.setStyle(
            "-fx-control-inner-background: #E0E0E0;" +
            "-fx-accent: #808080;" +          // Color of the filled track (same as selected buttons)
            "-fx-font-family: 'Poly';" +
            "-fx-disabled-opacity: 0.6;"      // Same as your buttons
        );

        speedSection.getChildren().addAll(speedLabel, speedSlider);

        // Playback controls section with larger buttons
        HBox playbackControls = new HBox(15); // Increased spacing
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
        HBox saveControls = new HBox(15); // Increased spacing
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
        rightSection.setAlignment(Pos.CENTER); // Center align VBox contents
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
        
        Label resultsLabel = new Label("~ Results ~");
        resultsLabel.setFont(Font.font("Poly", 32));
        resultsLabel.setAlignment(Pos.CENTER);
        
        try 
        {
            File currentDir = new File(System.getProperty("user.dir"));
            File outputImage = new File(currentDir.getParentFile().getParentFile() + "/test/" + "temp" + "-output.png");
            Image image = new Image(outputImage.toURI().toString());
            ImageView solutionImage = new ImageView(image);
            
            solutionImage.setFitWidth(400);
            solutionImage.setFitHeight(400);
            solutionImage.setPreserveRatio(true);
            
            VBox imageContainer = new VBox(solutionImage);
            imageContainer.setPadding(new Insets(20));
            imageContainer.setAlignment(Pos.CENTER);
            
            Label stepLabel = new Label("Move: " + currentStep + "/" + totalSteps);
            stepLabel.setFont(Font.font("Poly", 14));
            
            Label nodesLabel = new Label("Nodes Explored: " + nodesExplored);
            nodesLabel.setFont(Font.font("Poly", 14));
            
            Label timeLabel = new Label("Searching Time: " + searchTime + " ms");
            timeLabel.setFont(Font.font("Poly", 14));
            
            VBox statsContainer = new VBox(10);
            statsContainer.setAlignment(Pos.CENTER);
            statsContainer.getChildren().addAll(stepLabel, nodesLabel, timeLabel);
            
            mainContent.getChildren().addAll(resultsLabel, imageContainer, statsContainer);
            
            // Enable animation controls and save buttons
            speedSlider.setDisable(false);
            playButton.setDisable(false);
            pauseButton.setDisable(true);
            stopButton.setDisable(false);
            saveTxtButton.setDisable(false);
            
        } 
        catch (Exception e) 
        {
            System.err.println("Error loading solution image: " + e.getMessage());
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
        
        Label resultsLabel = new Label("~ Results ~");
        resultsLabel.setFont(Font.font("Poly", 32));
        resultsLabel.setAlignment(Pos.CENTER);
        
        Label errorLabel = new Label(errorMessage);
        errorLabel.setFont(Font.font("Poly", 14));
        errorLabel.setTextFill(javafx.scene.paint.Color.RED);
        errorLabel.setAlignment(Pos.CENTER);
        errorLabel.setWrapText(true);
        
        VBox errorContainer = new VBox(20);
        errorContainer.setAlignment(Pos.CENTER);
        errorContainer.getChildren().addAll(resultsLabel, errorLabel);
        
        mainContent.getChildren().add(errorContainer);
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
        fringeButton.setOnAction(e -> handleAlgorithmSelection("Fringe"));
        gbfsButton.setOnAction(e -> handleAlgorithmSelection("GBFS"));
        ucsButton.setOnAction(e -> handleAlgorithmSelection("UCS"));
        
        // Heuristic button handlers
        distanceButton.setOnAction(e -> handleHeuristicSelection("Distance"));
        blockingButton.setOnAction(e -> handleHeuristicSelection("Blocking"));
        
        // Solve button handler
        solveButton.setOnAction(e -> handleSolveButtonClick());
        
        // Animation control handlers
        playButton.setOnAction(e -> handlePlayButtonClick());
        pauseButton.setOnAction(e -> handlePauseButtonClick());
        stopButton.setOnAction(e -> handleStopButtonClick());
        
        // Save button handlers
        saveTxtButton.setOnAction(e -> handleSaveTxtClick());
    }
    
    /**
     * Handle the Load button click event.
     */
    private void handleLoadButtonClick() 
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open TXT File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        
        // Set initial directory to the test folder
        try {
            File currentDir = new File(System.getProperty("user.dir"));
            File testDir = new File(currentDir.getParentFile().getParentFile() + "/test");
            fileChooser.setInitialDirectory(testDir);
        } catch (Exception e) {
            System.err.println("Error setting initial directory: " + e.getMessage());
        }
        
        Stage stage = (Stage) loadButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            if (filePath.toLowerCase().endsWith(".txt")) {
                fileName = selectedFile.getName();
                fileNameLabel.setText(fileName);
                loadPreview(fileName);
                
                // Reset UI state for new file
                resetUIState();
                enableAlgorithmButtons();
            } else {
                showErrorView("Not .txt file loaded");
            }
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
        
        // Reset button styles
        aStarButton.setStyle(createButtonStyle(false));
        fringeButton.setStyle(createButtonStyle(false));
        gbfsButton.setStyle(createButtonStyle(false));
        ucsButton.setStyle(createButtonStyle(false));
        
        // Highlight selected button
        switch (algorithm) {
            case "A*":
                aStarButton.setStyle(createButtonStyle(true));
                break;
            case "Fringe":
                fringeButton.setStyle(createButtonStyle(true));
                break;
            case "GBFS":
                gbfsButton.setStyle(createButtonStyle(true));
                break;
            case "UCS":
                ucsButton.setStyle(createButtonStyle(true));
                break;
        }
        
        // Enable or disable heuristic buttons based on algorithm
        if (algorithm.equals("UCS")) {
            distanceButton.setDisable(true);
            blockingButton.setDisable(true);
            
            // Reset heuristic selection
            selectedHeuristic = "";
            distanceButton.setStyle(createButtonStyle(false));
            blockingButton.setStyle(createButtonStyle(false));
            
            // Enable solve button directly for UCS
            solveButton.setDisable(false);
        } else {
            distanceButton.setDisable(false);
            blockingButton.setDisable(false);
            
            // Check if heuristic is already selected
            solveButton.setDisable(selectedHeuristic.isEmpty());
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
        switch (heuristic) {
            case "Distance":
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
     */
    private void handleSolveButtonClick() 
    {
        // This will be implemented later
        // For now, just simulate the state change
        
        currentStep = 1;
        totalSteps = 32; // Example value
        nodesExplored = 69; // Example value
        searchTime = 2; // Example value in ms
        
        // Setup results view with the solution
        setupResultsView();
    }
    
    /**
     * Handle the Play button click event.
     */
    private void handlePlayButtonClick() 
    {
        isPlaying = true;
        isPaused = false;
        
        // Update button states
        playButton.setDisable(true);
        pauseButton.setDisable(false);
        stopButton.setDisable(false);
        speedSlider.setDisable(true);
        
        // Animation playback logic will be implemented later
    }
    
    /**
     * Handle the Pause button click event.
     */
    private void handlePauseButtonClick() 
    {
        isPlaying = false;
        isPaused = true;
        
        // Update button states
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        stopButton.setDisable(false);
        speedSlider.setDisable(false);
        
        // Animation pause logic will be implemented later
    }
    
    /**
     * Handle the Stop button click event.
     */
    private void handleStopButtonClick() 
    {
        isPlaying = false;
        isPaused = false;
        
        // Update button states
        playButton.setDisable(false);
        pauseButton.setDisable(true);
        stopButton.setDisable(false);
        speedSlider.setDisable(false);
        
        // Animation stop logic will be implemented later
    }
    
    /**
     * Handle the Save GIF button click event.
     */
    private void handleSaveGifClick() 
    {
        // This will be implemented later
        showInfo("Successfully saved GIF to " + fileName + "-output.gif");
    }
    
    /**
     * Handle the Save TXT button click event.
     */
    private void handleSaveTxtClick() 
    {
        // This will be implemented later
        showInfo("Successfully saved solution to " + fileName + "-output.txt");
    }

    /**
     * Load the preview of the selected test case.
     * 
     * @param fileName Name of the test case file
     */
    private void loadPreview(String fileName) 
    {
        try 
        {
            File currentDir = new File(System.getProperty("user.dir"));
            File file = new File(currentDir.getParentFile().getParentFile() + "/test/" + fileName);
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
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
        // Reset algorithm selection
        selectedAlgorithm = "";
        aStarButton.setStyle(createButtonStyle(false));
        fringeButton.setStyle(createButtonStyle(false));
        gbfsButton.setStyle(createButtonStyle(false));
        ucsButton.setStyle(createButtonStyle(false));
        
        // Reset heuristic selection
        selectedHeuristic = "";
        distanceButton.setStyle(createButtonStyle(false));
        blockingButton.setStyle(createButtonStyle(false));
        distanceButton.setDisable(true);
        blockingButton.setDisable(true);
        
        // Disable solve button
        solveButton.setDisable(true);
        
        // Disable animation controls
        speedSlider.setDisable(true);
        playButton.setDisable(true);
        pauseButton.setDisable(true);
        stopButton.setDisable(true);
        
        // Disable save buttons
        saveTxtButton.setDisable(true);
        
        // Reset animation state
        isPlaying = false;
        isPaused = false;
        
        // Reset the main content
        setupWelcomeView();
    }
    
    /**
     * Enable algorithm buttons.
     */
    private void enableAlgorithmButtons() 
    {
        aStarButton.setDisable(false);
        fringeButton.setDisable(false);
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
     * Show an alert dialog with the provided message.
     * 
     * @param message Alert message
     */
    private void showAlert(String message) 
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show an information dialog with the provided message.
     * 
     * @param message Information message
     */
    private void showInfo(String message) 
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
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