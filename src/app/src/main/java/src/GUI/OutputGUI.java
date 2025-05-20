package src.GUI;

import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.scene.text.Font;
import javafx.collections.FXCollections;
import src.ADT.Board;
import src.IO.Output;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * OutputGUI class to save the output to a text file with custom file chooser.
 */
public class OutputGUI 
{
    private String outputFilePath;
    private Board board;
    private long time;
    private int attempts;
    private List<int[]> moves;

    /**
     * Constructs an OutputGUI object with the provided parameters.
     *
     * @param outputFilePath Full path to output file or null to prompt for location
     * @param board Board object
     * @param time Time taken to solve the puzzle
     * @param attempts Number of iterations
     */
    public OutputGUI(String outputFilePath, Board board, long time, int attempts) 
    {
        this.outputFilePath = outputFilePath;
        this.board = board;
        this.time = time;
        this.attempts = attempts;
        this.moves = new ArrayList<>();
    }
    
    /**
     * Constructs an OutputGUI object with solution moves for better formatting
     *
     * @param outputFilePath Full path to output file or null to prompt for location
     * @param board Board object
     * @param time Time taken to solve the puzzle
     * @param attempts Number of iterations
     * @param moves List of solution moves
     */
    public OutputGUI(String outputFilePath, Board board, long time, int attempts, List<int[]> moves) 
    {
        this.outputFilePath = outputFilePath;
        this.board = board;
        this.time = time;
        this.attempts = attempts;
        this.moves = moves;
    }

    /**
     * Saves the output to a text file.
     *
     * @throws IOException if an I/O error occurs
     * @return true if save was successful, false if cancelled
     */
    public boolean saveToText() throws IOException 
    {
        File txtFile = new File(outputFilePath);
        if (!shouldOverwrite(txtFile)) return false;

        java.io.FileWriter writer = new java.io.FileWriter(txtFile);
        
        if (board.hasError()) writer.write(board.getErrorMsg());
        else 
        {
            writer.write("Nodes Explored: " + attempts + "\n");
            writer.write("Searching Time: " + time + " ms\n\n");
            
            writer.write("Initial board state:\n");
            writer.write(Output.boardToString(board) + "\n\n");
            
            if (moves != null && !moves.isEmpty()) 
            {
                writer.write("Solution:\n\n");
                
                Board resultBoard = board.copy();
                List<int[]> combinedMoves = Output.combineConsecutiveMoves(moves);
                
                for (int[] move : combinedMoves) 
                {
                    int pieceIndex = move[0];
                    int moveAmount = move[1];
                    
                    resultBoard = Output.applyMoveAmount(resultBoard, pieceIndex, moveAmount);
                    int orientation = resultBoard.getCars().get(pieceIndex).getOrientation();

                    String direction;
                    
                    if (orientation == 1 && moveAmount > 0) direction = "Right";
                    else if (orientation == 1 && moveAmount < 0) direction = "Left";
                    else if (orientation == 0 && moveAmount > 0) direction = "Down";
                    else direction = "Up";
                    
                    writer.write("Move piece " + resultBoard.getCars().get(pieceIndex).getId() + 
                                 " " + Math.abs(moveAmount) + " spaces " + direction + "\n");
                    
                    writer.write(Output.boardToString(resultBoard) + "\n\n");
                }
            } 
            else writer.write("No solution found.\n");
        }
        writer.close();
        return true;
    }

    /**
     * Prompts the user to confirm overwriting an existing file - with overlay removed.
     *
     * @param file File to overwrite
     * @return true if the file should be overwritten, false otherwise
     */
    public boolean shouldOverwrite(File file) 
    {
        if (!file.exists()) return true;
        
        Stage confirmStage = new Stage();
        confirmStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        
        final Stage[] ownerStage = new Stage[1];
        for (Window window : Stage.getWindows()) 
        {
            if (window instanceof Stage && window.isShowing()) 
            {
                ownerStage[0] = (Stage) window;
                confirmStage.initOwner(ownerStage[0]);
                break;
            }
        }
        
        Label contentLabel = new Label("'" + file.getName() + "' already exists. Overwrite?");
        contentLabel.setFont(Font.font("Poly", 14));
        contentLabel.setWrapText(true);
        contentLabel.setPrefWidth(350);
        contentLabel.setMaxWidth(350);
        contentLabel.setPadding(new Insets(10, 5, 10, 5));
        
        StackPane contentPane = new StackPane(contentLabel);
        
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        
        okButton.setFont(Font.font("Poly", 12));
        cancelButton.setFont(Font.font("Poly", 12));
        okButton.setStyle(createButtonStyle(false));
        cancelButton.setStyle(createButtonStyle(false));
        
        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        buttonBar.setPadding(new Insets(10, 5, 5, 5));
        buttonBar.getChildren().addAll(okButton, cancelButton);
        
        VBox dialogLayout = new VBox(10);
        dialogLayout.setStyle(
            "-fx-background-color: white;" +
            "-fx-padding: 15px;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 1px;" +
            "-fx-effect: dropshadow(three-pass-box, white, 10, 0.2, 0, 0);"
        );
        dialogLayout.getChildren().addAll(contentPane, buttonBar);
        
        Scene scene = new Scene(dialogLayout);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        confirmStage.setScene(scene);
        
        final boolean[] result = {false};
        
        okButton.setOnAction(e -> 
        {
            result[0] = true;
            confirmStage.close();
        });
        
        cancelButton.setOnAction(e -> 
        {
            result[0] = false;
            confirmStage.close();
        });
        
        confirmStage.setOnShown(e -> 
        {
            if (ownerStage[0] != null) 
            {
                Stage primaryStage = ownerStage[0];
                
                double dialogWidth = confirmStage.getWidth();
                double dialogHeight = confirmStage.getHeight();
                
                double centerX = primaryStage.getX() + (primaryStage.getWidth() - dialogWidth) / 2;
                double centerY = primaryStage.getY() + (primaryStage.getHeight() - dialogHeight) / 2;
                
                confirmStage.setX(centerX);
                confirmStage.setY(centerY);
                
                confirmStage.toFront();
            }
        });
        
        // Show and wait for result
        confirmStage.showAndWait();
        
        return result[0];
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
     * Creates a completely custom file chooser dialog with transparent background, 
     * rounded corners, and proper dark overlay.
     * 
     * @param owner The owner window
     * @param title Dialog title
     * @param initialDirectory Initial directory
     * @param defaultFileName Default file name
     * @param saveMode True for save dialog, false for open dialog
     * @return Selected file or null if cancelled
     */
    public static File showCustomFileDialog(Window owner, String title, File initialDirectory, 
                                            String defaultFileName, boolean saveMode) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        
        try 
        {
            dialog.getIcons().add(new javafx.scene.image.Image(
                OutputGUI.class.getResourceAsStream("/images/icon.png")));
        } 
        catch (Exception e) 
        {
            System.err.println("Error loading icon: " + e.getMessage());
        }
        
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: black;" +
            "-fx-border-width: 1px;" +
            "-fx-effect: dropshadow(three-pass-box, white, 10, 0.2, 0, 0);"
        );
        
        Label headerLabel = new Label(title);
        headerLabel.setFont(Font.font("Poly", 20));
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setPadding(new Insets(0, 0, 10, 0));
        
        HBox pathBox = new HBox(10);
        pathBox.setAlignment(Pos.CENTER_LEFT);
        
        Label pathLabel = new Label("Location:");
        pathLabel.setFont(Font.font("Poly", 14));
        
        TextField pathField = new TextField();
        pathField.setEditable(false);
        pathField.setPrefWidth(400);
        HBox.setHgrow(pathField, javafx.scene.layout.Priority.ALWAYS);
        
        Button upButton = new Button("↑");
        upButton.setStyle(createCustomButtonStyle(false));
        
        pathBox.getChildren().addAll(pathLabel, pathField, upButton);
        
        ListView<String> fileListView = new ListView<>();
        fileListView.setPrefHeight(300);
        VBox.setVgrow(fileListView, javafx.scene.layout.Priority.ALWAYS);
        
        HBox fileNameBox = new HBox(10);
        fileNameBox.setAlignment(Pos.CENTER_LEFT);
        
        Label fileNameLabel = new Label("File name:");
        fileNameLabel.setFont(Font.font("Poly", 14));
        
        TextField fileNameField = new TextField();
        fileNameField.setPrefWidth(300);
        HBox.setHgrow(fileNameField, javafx.scene.layout.Priority.ALWAYS);
        
        fileNameBox.getChildren().addAll(fileNameLabel, fileNameField);
        
        if (saveMode && defaultFileName != null) {
            fileNameField.setText(defaultFileName);
        }
        
        Label filterLabel = new Label("Show .txt files only");
        filterLabel.setFont(Font.font("Poly", 12));
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle(createCustomButtonStyle(false));
        
        Button actionButton = new Button(saveMode ? "Save" : "Open");
        actionButton.setStyle(createCustomButtonStyle(false));
        actionButton.setDisable(true);
        
        buttonBox.getChildren().addAll(cancelButton, actionButton);
        
        if (saveMode)
            layout.getChildren().addAll(headerLabel, pathBox, fileListView, fileNameBox, filterLabel, buttonBox);
        else 
            layout.getChildren().addAll(headerLabel, pathBox, fileListView, filterLabel, buttonBox);
        
        final File[] result = {null};
        final File[] currentDirectory = {initialDirectory != null ? initialDirectory : 
                                         new File(System.getProperty("user.dir"))};
        
        Runnable updateFileList = () -> {
            File dir = currentDirectory[0];
            pathField.setText(dir.getAbsolutePath());
            
            List<String> items = new ArrayList<>();
            
            if (dir.getParentFile() != null) {
                items.add("[..]");
            }
            
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        items.add("[" + f.getName() + "]");
                    } else if (f.getName().toLowerCase().endsWith(".txt")) {
                        items.add(f.getName());
                    }
                }
            }
            
            fileListView.setItems(FXCollections.observableArrayList(items));
            
            if (saveMode)
                actionButton.setDisable(fileNameField.getText().trim().isEmpty());

            else
                actionButton.setDisable(true);
        };
        
        updateFileList.run();
        
        fileListView.setOnMouseClicked(event ->
        {
            String selected = fileListView.getSelectionModel().getSelectedItem();
            if (selected != null) 
            {
                if (selected.equals("[..]")) 
                {
                    // Go up one directory
                    currentDirectory[0] = currentDirectory[0].getParentFile();
                    updateFileList.run();
                } 
                else if (selected.startsWith("[") && selected.endsWith("]")) 
                {
                    // Enter directory
                    String dirName = selected.substring(1, selected.length() - 1);
                    File newDir = new File(currentDirectory[0], dirName);
                    if (newDir.isDirectory()) 
                    {
                        currentDirectory[0] = newDir;
                        updateFileList.run();
                    }
                } 
                else if (!saveMode) 
                {
                    // Select file in open mode
                    fileNameField.setText(selected);
                    actionButton.setDisable(false);
                    
                    if (event.getClickCount() == 2) 
                    {
                        // Double-click to select and confirm
                        result[0] = new File(currentDirectory[0], selected);
                        dialog.close();
                    }
                } 
                else if (saveMode) 
                {
                    // Fill in filename in save mode
                    fileNameField.setText(selected);
                    actionButton.setDisable(false);
                }
            }
        });
        
        // Up button handler
        upButton.setOnAction(e -> 
        {
            if (currentDirectory[0].getParentFile() != null) 
            {
                currentDirectory[0] = currentDirectory[0].getParentFile();
                updateFileList.run();
            }
        });
        
        // Filename field handler (for save mode)
        fileNameField.textProperty().addListener((obs, oldVal, newVal) -> 
        {
            actionButton.setDisable(newVal.trim().isEmpty());
        });
        
        // Cancel button handler
        cancelButton.setOnAction(e -> dialog.close());
        
        // Action button handler
        actionButton.setOnAction(e -> 
        {
            String fileName = fileNameField.getText().trim();
            
            // Ensure .txt extension in save mode
            if (saveMode && !fileName.toLowerCase().endsWith(".txt")) 
            {
                fileName += ".txt";
            }
            
            result[0] = new File(currentDirectory[0], fileName);
            dialog.close();
        });
        
        Scene scene = new Scene(layout);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialog.setScene(scene);
        
        if (owner instanceof Stage) 
        {
            Stage primaryStage = (Stage) owner;
            
            dialog.setOnShown(e -> 
            {
                double dialogWidth = dialog.getWidth();
                double dialogHeight = dialog.getHeight();
                
                double centerX = primaryStage.getX() + (primaryStage.getWidth() - dialogWidth) / 2;
                double centerY = primaryStage.getY() + (primaryStage.getHeight() - dialogHeight) / 2;
                
                dialog.setX(centerX);
                dialog.setY(centerY);
                
                dialog.toFront();
            });
        }
        
        dialog.showAndWait();
        
        return result[0];
    }
    
    /**
     * Helper method for creating button styles.
     */
    private static String createCustomButtonStyle(boolean isSelected) 
    {
        String backgroundColor = isSelected ? "#808080" : "#E0E0E0";
        String textColor = isSelected ? "white" : "black";
        
        return "-fx-background-color: " + backgroundColor + ";" +
               "-fx-text-fill: " + textColor + ";" +
               "-fx-background-radius: 5;" +
               "-fx-padding: 8 20;" +
               "-fx-font-family: 'Poly';";
    }
}