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

import java.io.File;
import java.util.Arrays;

import src.ADT.Board;

/**
 * JavaFX GUI application for the Rush Hour Puzzle Solver.
 */
public class GUI extends Application 
{
    private Button findButton;
    private Button savePngButton;
    private Button saveTxtButton;
    private TextField testCaseInput;
    private ListView<String> testCasesList;
    private ListView<String> outputFilesList;
    private TextArea previewArea;
    private VBox mainContent;
    private String fileName;
    private long searchTime;
    private int nodesExplored;
    private Board board;

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
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: white;");
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setMaxHeight(Region.USE_PREF_SIZE);

        VBox titleSection = new VBox(5);
        titleSection.setAlignment(Pos.CENTER);
        
        Label title = new Label("Rush Hour Puzzle Solver");
        title.setFont(Font.font("Poly", 24));
        
        Label copyrightLabel = new Label("~ rzi - 13523004 ~");
        copyrightLabel.setFont(Font.font("Poly", 12));
        copyrightLabel.setStyle("-fx-text-fill: #808080;");
        
        titleSection.getChildren().addAll(title, copyrightLabel);
        VBox.setMargin(titleSection, new Insets(0, 0, 10, 0));

        HBox inputSection = new HBox(15);
        inputSection.setAlignment(Pos.CENTER);
        
        testCaseInput = new TextField();
        testCaseInput.setPromptText("Type test case .txt");
        testCaseInput.setPrefWidth(220);
        testCaseInput.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 20;" +
            "-fx-border-radius: 20;" +
            "-fx-padding: 8;" +
            "-fx-font-family: 'Poly';" +
            "-fx-prompt-text-fill: #757575;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 0);"
        );
        
        findButton = new Button("Find!");
        findButton.setPrefWidth(80);
        findButton.setStyle(
            "-fx-background-color: #E0E0E0;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8;" +
            "-fx-font-family: 'Poly';" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 10, 0, 0, 0);"
        );
        
        inputSection.getChildren().addAll(testCaseInput, findButton);

        HBox listsContainer = new HBox(20);
        listsContainer.setAlignment(Pos.CENTER);

        VBox testCasesColumn = new VBox(5);
        testCasesColumn.setAlignment(Pos.CENTER);
        testCasesColumn.setSpacing(10);
        
        Label testCasesLabel = new Label("~ Available Test Cases ~");
        testCasesLabel.setFont(Font.font("Poly", 14));
        testCasesLabel.setAlignment(Pos.CENTER);
        
        testCasesList = new ListView<>();
        testCasesList.setPrefHeight(170);
        testCasesList.setPrefWidth(170);
        testCasesList.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 1px;" +
            "-fx-font-family: 'Poly';"
        );
        
        testCasesColumn.getChildren().addAll(testCasesLabel, testCasesList);

        VBox outputColumn = new VBox(5);
        outputColumn.setAlignment(Pos.CENTER);
        outputColumn.setSpacing(10);
        
        Label outputLabel = new Label("~ Output ~");
        outputLabel.setFont(Font.font("Poly", 14));
        outputLabel.setAlignment(Pos.CENTER);
        
        outputFilesList = new ListView<>();
        outputFilesList.setPrefHeight(170);
        outputFilesList.setPrefWidth(170);
        outputFilesList.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 1px;" +
            "-fx-font-family: 'Poly';"
        );
        
        outputColumn.getChildren().addAll(outputLabel, outputFilesList);
        listsContainer.getChildren().addAll(testCasesColumn, outputColumn);

        Label previewLabel = new Label("~ Preview ~");
        previewLabel.setFont(Font.font("Poly", 14));
        previewLabel.setAlignment(Pos.CENTER);
        
        previewArea = new TextArea();
        previewArea.setPrefHeight(170);
        previewArea.setPrefWidth(320);
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

        Label instructionLabel = new Label("Manually add Test Case with *.txt file in the ~/test directory. \nThen, restart the App or click \"Find!\" button to refresh the list.");
        instructionLabel.setFont(Font.font("Poly", 11));
        instructionLabel.setWrapText(true);
        instructionLabel.setAlignment(Pos.CENTER);
        instructionLabel.setTextAlignment(TextAlignment.CENTER);
        instructionLabel.setPrefWidth(350);

        HBox saveButtons = new HBox(15);
        saveButtons.setAlignment(Pos.CENTER);
        
        String buttonStyle = 
            "-fx-background-color: #808080;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5;" +
            "-fx-padding: 8 20;" +
            "-fx-font-family: 'Poly';";
            
        saveTxtButton = new Button("Save .txt");
        savePngButton = new Button("Save .png");
        
        saveTxtButton.setStyle(buttonStyle);
        savePngButton.setStyle(buttonStyle);
        saveTxtButton.setDisable(true);
        savePngButton.setDisable(true);
        
        saveButtons.getChildren().addAll(saveTxtButton, savePngButton);

        sidebar.getChildren().addAll(
            titleSection,
            inputSection,
            listsContainer,
            previewLabel,
            previewArea,
            instructionLabel,
            saveButtons
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

        mainLayout.getChildren().addAll(sidebarWrapper, separator, mainContent);

        Scene scene = new Scene(mainLayout);

        // add icon
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
        
        loadTestCases();
        
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
            
            Label timeLabel = new Label("Searching Time: " + searchTime + " ms");
            timeLabel.setFont(Font.font("Poly", 14));
            
            Label casesLabel = new Label("Nodes Explored: " + nodesExplored);
            casesLabel.setFont(Font.font("Poly", 14));
            
            VBox statsContainer = new VBox(10);
            statsContainer.setAlignment(Pos.CENTER);
            statsContainer.getChildren().addAll(timeLabel, casesLabel);
            
            mainContent.getChildren().addAll(resultsLabel, imageContainer, statsContainer);
            
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
        
        saveTxtButton.setDisable(false);
        savePngButton.setDisable(true);

        board = new Board(0, 0, 0, errorMessage, null);
    }

    /**
     * Load test cases from the ~/test directory.
     */
    private void loadTestCases() 
    {
        try 
        {
            File currentDir = new File(System.getProperty("user.dir"));
            File testDir = new File(currentDir.getParentFile().getParentFile() + "/test");
            File[] files = testDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) 
            {
                Arrays.sort(files);
                for (File file : files) 
                {
                    String fileName = file.getName();
                    if (fileName.endsWith("-output.txt")) 
                    {
                        outputFilesList.getItems().add(fileName);
                    } 
                    else 
                    {
                        testCasesList.getItems().add(fileName);
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            System.err.println("Error loading test cases: " + e.getMessage());
        }
    }

    /**
     * Set up event handlers for the GUI.
     */
    private void setupEventHandlers() 
    {
        findButton.setOnAction(e -> 
        {
            String selected = testCaseInput.getText();
            if (selected.isEmpty()) 
            {
                fileName = "invalid-file-1";
                showErrorView("Filename cannot be empty.");
                return;
            }
            
            if (selected.endsWith("-output")) {
                showErrorView("Output files cannot be processed");
                return;
            }
            
            handleFindButtonClick();
        });
    
        testCasesList.setOnMouseClicked(e -> 
        {
            String selected = testCasesList.getSelectionModel().getSelectedItem();
            if (selected != null) 
            {
                testCaseInput.setText(selected.replace(".txt", ""));
                loadPreview(selected);
            }
        });
    
        outputFilesList.setOnMouseClicked(e -> 
        {
            String selected = outputFilesList.getSelectionModel().getSelectedItem();
            if (selected != null) 
            {
                testCaseInput.setText(selected.replace(".txt", ""));
                loadPreview(selected);
            }
        });
    
        saveTxtButton.setOnAction(e -> handleSaveTxtClick());
        savePngButton.setOnAction(e -> handleSavePngClick());
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
     * Handle the "Find!" button click event.
     */
    private void handleFindButtonClick() 
    {

    }

    /**
     * Handle the "Save .txt" button click event.
     */
    private void handleSaveTxtClick() 
    {
        try 
        {
            OutputGUI output = new OutputGUI(fileName, board, searchTime, nodesExplored);
            File txtFile = output.getOutputTextFile();

            if (txtFile.exists()) 
            {
                if(output.shouldOverwrite(txtFile))
                {
                    txtFile.delete();
                    output.saveToText();
                    showInfo("Successfully saved solution to " + fileName + "-output.txt");
                }
            }
            else
            {
                output.saveToText();
                showInfo("Successfully saved solution to " + fileName + "-output.txt");
            }
            clearTestCases();
            loadTestCases();
        } 
        catch (Exception e) 
        {
            showAlert("Error saving text file: " + e.getMessage());
        }
    }

    /**
     * Handle the "Save .png" button click event.
     */
    private void handleSavePngClick() 
    {
        try 
        {
            OutputGUI output = new OutputGUI(fileName, board, searchTime, nodesExplored);
            File pngFile = output.getOutputImageFile();

            if (pngFile.exists())
            {
                if(output.shouldOverwrite(pngFile))
                {   
                    pngFile.delete();
                    output.saveToImage();
                    showInfo("Successfully saved image to " + fileName + "-output.png");
                }
            }
            else
            {
                output.saveToImage();
                showInfo("Successfully saved image to " + fileName + "-output.png");
            }
            clearTestCases();
            loadTestCases();
        } 
        catch (Exception e) 
        {
            showAlert("Error saving image file: " + e.getMessage());
        }
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
     * Clear the test cases list.
     */
    private void clearTestCases() 
    {
        testCasesList.getItems().clear();
        outputFilesList.getItems().clear();
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