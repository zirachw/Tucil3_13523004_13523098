package src.GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import src.ADT.Board;

import java.io.File;
import java.io.IOException;

/**
 * OutputGUI class to save the output to a text file or an image file.
 */
public class OutputGUI 
{
    private String fileName;
    private Board board;
    private long time;
    private int attempts;

    /**
     * Constructs an OutputGUI object with the provided parameters.
     *
     * @param fileName Filename
     * @param board Board object
     * @param time Time taken to solve the puzzle
     * @param attempts Number of iterations
     */
    public OutputGUI(String fileName, Board board, long time, int attempts) 
    {
        this.fileName = fileName;
        this.board = board;
        this.time = time;
        this.attempts = attempts;
    }

    /**
     * Saves the output to a text file.
     *
     * @throws IOException if an I/O error occurs
     */
    public void saveToText() throws IOException 
    {
        File txtFile = getOutputTextFile();
        if (!shouldOverwrite(txtFile)) return;

        java.io.FileWriter writer = new java.io.FileWriter(txtFile);
        
        if (board.hasError()) writer.write(board.getErrorMsg());
        else 
        {
            writer.write("Solution:\n");
            for (int i = 0; i < board.getRows(); i++) 
            {
                for (int j = 0; j < board.getCols(); j++) 
                {
                    writer.write(board.getElement(i, j));
                }
                writer.write("\n");
            }
            writer.write("\n");
            writer.write("Searching Time: " + time + "ms\n");
            writer.write("\n");
            writer.write("Number of Iterations: " + attempts);
        }
        writer.close();
    }

    /**
     * Saves the output to an image file.
     *
     * @throws IOException if an I/O error occurs
     */
    public void saveToImage() throws IOException 
    {
        // PuzzleImage image = new PuzzleImage(board);
        // image.saveToImage(fileName);
    }

    /**
     * Prompts the user to confirm overwriting an existing file.
     *
     * @param file File to overwrite
     * @return true if the file should be overwritten, false otherwise
     */
    public boolean shouldOverwrite(File file) 
    {
        if (!file.exists()) return true;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Overwrite");
        confirm.setHeaderText(null);
        confirm.setContentText("'" + file.getName() + "' already exists. Overwrite?");
        
        return confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /**
     * Returns the output text file.
     *
     * @return Output text file
     */
    public File getOutputTextFile() 
    {
        File currentDir = new File(System.getProperty("user.dir"));
        File testDir = currentDir.getParentFile().getParentFile();
        return new File(testDir + "/test/" + fileName + "-output.txt");
    }

    /**
     * Returns the output image file.
     *
     * @return Output image file
     */
    public File getOutputImageFile() 
    {
        File currentDir = new File(System.getProperty("user.dir"));
        File testDir = currentDir.getParentFile().getParentFile();
        return new File(testDir + "/test/" + fileName + "-output.png");
    }
}