import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Example class demonstrating how to use the Input class to load Rush Hour puzzle files.
 */
public class InputUsageExample {
    
    /**
     * Demonstrates the proper usage of the Input class
     * 
     * @param args Command line arguments (expects a puzzle file path)
     */
    public static void main(String[] args) {
        // Example file path - adjust as needed
        String filePath = "D:\\Akademik\\ITB\\Semester-4\\Stima\\Tucil\\Tucil3_13523004_13523098\\dev\\puzzle4.txt";
        
        // Step 1: Validate the filename
        String filenameError = Input.validateFilename(filePath);
        if (filenameError != null) {
            System.err.println("Error with filename: " + filenameError);
            return;
        }
        
        // Step 2: Validate the file exists and is readable
        File file = new File(filePath);
        String fileError = Input.validateFile(file);
        if (fileError != null) {
            System.err.println("Error with file: " + fileError);
            return;
        }
        
        try {
            // Step 3: Read and parse the input file
            Input puzzleInput = Input.readInput(filePath);
            
            // Step 4: Check for errors in the parsed input
            if (puzzleInput.getErrorMsg() != null) {
                System.err.println("Error parsing puzzle: " + puzzleInput.getErrorMsg());
                return;
            }
            
            // Step 5: Successfully parsed the input, can now use it
            System.out.println("Puzzle loaded successfully!");

            if (puzzleInput.getBoardConfig() == null) {
                System.err.println("Error: Board configuration is null.");
                return;
            }
            
            printPuzzleInfo(puzzleInput);
            
            // At this point, we would typically create a Board object and start solving
            // createBoardFromInput(puzzleInput);
            
        } catch (IOException e) {
            System.err.println("IOException occurred: " + e.getMessage());
        }
    }
    
    /**
     * Prints information about the loaded puzzle
     * 
     * @param input The validated Input object
     */
    private static void printPuzzleInfo(Input input) {
        System.out.println("Board dimensions: " + input.getRows() + " x " + input.getCols());
        System.out.println("Number of non-primary pieces: " + input.getPieces());
        System.out.println("Exit location: " + input.getExitPosition());
        System.out.println("Board configuration:");
        
        ArrayList<String> boardConfig = input.getBoardConfig();
        for (String row : boardConfig) {
            System.out.println(row);
        }
    }
    
    /**
     * Example method showing how to convert Input to a Board
     * 
     * @param input The validated Input object
     * @return A new Board initialized with the input configuration
     */
    private static Board createBoardFromInput(Input input) {
        int rows = input.getRows();
        int cols = input.getCols();
        ArrayList<String> boardConfig = input.getBoardConfig();
        
        // Create a new board
        Board board = new Board(rows, cols);
        
        // Convert string configuration to char array
        char[][] config = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            config[i] = boardConfig.get(i).toCharArray();
        }
        
        // Load the configuration into the board
        board.loadConfiguration(config);
        
        return board;
    }
}