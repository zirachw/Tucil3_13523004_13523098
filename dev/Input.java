import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Input class to read and parse Rush Hour puzzle input from a .txt file and return an Input object.
 * This class provides methods to validate and parse Rush Hour puzzle inputs.
 */
public class Input 
{
    private int rows;   // A 
    private int cols;   // B 
    private int pieces; // N 
    private ArrayList<String> boardConfig; // Configuration of the board
    private String errorMsg;
    private ExitPosition exitPosition; // Position of the exit

    public record FirstLine(int rows, int cols, String errorMessage) {}
    public record SecondLine(int pieces, String errorMessage) {}
    
    // New record to store the position of the exit
    public record ExitPosition(int row, int col, String side) {}

    /**
     * Constructs an Input object with the provided parameters.
     *
     * @param rows number of rows of the board (A)
     * @param cols number of columns of the board (B)
     * @param pieces number of pieces excluding the primary piece (N)
     * @param boardConfig list of strings representing the board configuration
     * @param errorMsg error message if any
     * @param exitPosition position of the exit
     */
    public Input(int rows, int cols, int pieces, ArrayList<String> boardConfig, String errorMsg, ExitPosition exitPosition) 
    {
        this.rows = rows;
        this.cols = cols;
        this.pieces = pieces;
        this.boardConfig = boardConfig;
        this.errorMsg = errorMsg;
        this.exitPosition = exitPosition;
    }

    /**
     * Get the number of rows in the puzzle board.
     * @return Number of rows
     */
    public int getRows() { return this.rows; }
    
    /**
     * Get the number of columns in the puzzle board.
     * @return Number of columns
     */
    public int getCols() { return this.cols; }
    
    /**
     * Get the number of non-primary pieces in the puzzle.
     * @return Number of non-primary pieces
     */
    public int getPieces() { return this.pieces; }
    
    /**
     * Get the board configuration as a list of strings.
     * Each string represents one row of the board.
     * @return ArrayList of strings representing the board configuration
     */
    public ArrayList<String> getBoardConfig() { return this.boardConfig; }
    
    /**
     * Get any error message from the input validation.
     * @return Error message or null if no errors
     */
    public String getErrorMsg() { return this.errorMsg; }
    
    /**
     * Get the position of the exit.
     * @return ExitPosition record with row, column, and side of the exit
     */
    public ExitPosition getExitPosition() { return this.exitPosition; }

    /**
     * Validates the filename.
     *
     * @param fileName Filename to validate
     * @return Error message if invalid, null otherwise
     */
    public static String validateFilename(String fileName) 
    {
        if (fileName == null || fileName.isEmpty()) 
        {
            return "Filename cannot be empty.";
        }
        return null;
    }

    /**
     * Validates the file - checks if it exists, is readable, and not empty.
     *
     * @param file File object to validate
     * @return Error message if invalid, null otherwise
     */
    public static String validateFile(File file) 
    {
        if (!file.exists()) return "'" + file.getName() + "' does not exist.";
        else if (!file.canRead()) return "File cannot be read. Please check file permissions.";
        else if (file.length() == 0) return "File is empty.";
        else return null;
    }

    /**
     * Validates the first line containing board dimensions (A B).
     * 
     * @param line First line of the input file
     * @return FirstLine record containing validated values or error message
     */
    public static FirstLine validateFirstLine(String line) 
    {
        String[] tokens = line.split("\\s+");
        if (tokens.length != 2) 
        {
            String message = "First line must contain exactly two values: A and B (board dimensions). Found " + tokens.length + " values instead.";
            return new FirstLine(-1, -1, message);
        }

        // Before parsing, check if the values are integers
        for (String token : tokens) 
        {
            if (!token.matches("\\d+")) 
            {
                String message = "A and B must be positive integers. Found " + tokens[0] + ", " + tokens[1] + " instead.";
                return new FirstLine(-1, -1, message);
            }
        }

        int rows = Integer.parseInt(tokens[0]);
        int cols = Integer.parseInt(tokens[1]);

        if (rows < 1 || cols < 1) 
        {
            String message = "A and B must be positive integers. Found A = " + rows + ", B = " + cols + ".";
            return new FirstLine(-1, -1, message);
        }
        
        return new FirstLine(rows, cols, null);
    }
    
    /**
     * Validates the second line containing number of pieces (N).
     * 
     * @param line Second line of the input file
     * @return SecondLine record containing validated values or error message
     */
    public static SecondLine validateSecondLine(String line) 
    {
        if (!line.matches("\\d+")) 
        {
            String message = "Second line must contain exactly one value: N (number of non-primary pieces). Found: " + line;
            return new SecondLine(-1, message);
        }

        int pieces = Integer.parseInt(line);

        if (pieces < 0) 
        {
            String message = "N must be a non-negative integer. Found N = " + pieces + ".";
            return new SecondLine(-1, message);
        }
        
        return new SecondLine(pieces, null);
    }
    
    /**
     * Validates the board configuration, including exits on all four sides.
     * 
     * @param config Board configuration lines
     * @param rows Expected number of rows
     * @param cols Expected number of columns
     * @return Array with error message and exit position; first element is error (null if valid),
     *         second element is ExitPosition if exit is found
     */
    public static Object[] validateBoardConfig(ArrayList<String> config, int rows, int cols) 
    {
        // Check for exits in top or bottom rows (extra rows)
        boolean hasTopExit = false;
        boolean hasBottomExit = false;
        ExitPosition exitPosition = null;
        
        if (config.size() > rows && config.get(0).trim().equals("K")) {
            hasTopExit = true;
            exitPosition = new ExitPosition(-1, cols / 2, "TOP");
            config.remove(0);
        }
        
        if (config.size() > rows && config.get(config.size() - 1).trim().equals("K")) {
            if (hasTopExit) {
                return new Object[] {"Found multiple exits (K) - at both top and bottom. Only one exit is allowed.", null};
            }
            hasBottomExit = true;
            exitPosition = new ExitPosition(-1, cols / 2, "BOTTOM");
            config.remove(config.size() - 1);
        }
        
        if (config.size() != rows) 
        {
            return new Object[] {"Board configuration must have exactly " + rows + " rows. Found " + config.size() + " rows.", null};
        }
        
        boolean foundPrimaryPiece = false;
        boolean foundExit = hasTopExit || hasBottomExit;
        
        for (int i = 0; i < config.size(); i++) 
        {
            String line = config.get(i);
            
            if (line.length() > 0 && line.charAt(0) == 'K') 
            {
                if (foundExit) {
                    return new Object[] {"Found multiple exits (K). Only one exit is allowed.", null};
                }
                foundExit = true;
                exitPosition = new ExitPosition(i, -1, "LEFT");
                line = line.substring(1);
            }
            
            if (line.length() > cols) 
            {
                if (line.length() == cols + 1 && line.charAt(cols) == 'K') 
                {
                    if (foundExit) {
                        return new Object[] {"Found multiple exits (K). Only one exit is allowed.", null};
                    }
                    foundExit = true;
                    exitPosition = new ExitPosition(i, cols, "RIGHT");
                    line = line.substring(0, cols);
                } 
                else 
                {
                    return new Object[] {"Row " + (i + 1) + " exceeds expected length. Found " + line.length() + 
                           " characters when expected " + cols + " or " + (cols + 1) + " with exit.", null};
                }
            } 
            else if (line.length() < cols) 
            {
                return new Object[] {"Row " + (i + 1) + " must have at least " + cols + " columns. Found " + line.length() + " columns.", null};
            }
            
            for (int j = 0; j < line.length(); j++) 
            {
                char c = line.charAt(j);
                
                if (c == 'P') 
                {
                    foundPrimaryPiece = true;
                } 
                else if (c == 'K') 
                {
                    return new Object[] {"Exit (K) must be located outside the board, not on it. Found at row " + (i + 1) + 
                          ", column " + (j + 1) + ".", null};
                } 
                else if (c != '.' && !Character.isLetter(c)) 
                {
                    return new Object[] {"Invalid character in board configuration at row " + (i + 1) + 
                           ", column " + (j + 1) + ": " + c + ". Expected letters or '.' (for empty cells).", null};
                }
            }
        }
        
        if (!foundPrimaryPiece) 
        {
            return new Object[] {"Primary piece (P) not found in board configuration.", null};
        }
        
        if (!foundExit) 
        {
            return new Object[] {"Exit (K) not found in board configuration.", null};
        }
        
        return new Object[] {null, exitPosition}; // No errors found, return the exit position
    }

    /**
     * Reads Rush Hour puzzle input from a .txt file and returns a new Input object.
     *
     * Expected file format:
     *   A B      // Board dimensions (rows x columns)
     *   N        // Number of non-primary pieces
     *   [Board configuration] - may include rows with exit 'K' outside the board
     *
     * @param filePath path to the puzzle specification file
     * @return an Input object containing parsed puzzle data or an error message
     * @throws IOException if an I/O error occurs while reading the file
     */
    public static Input readInput(String filePath) throws IOException 
    {
        boolean firstLine = true;
        boolean secondLine = false;
        int rows = -1, cols = -1, pieces = -1;
        ArrayList<String> boardConfig = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String buffer;
            while ((buffer = br.readLine()) != null) 
            {
                buffer = buffer.trim();
                
                if (buffer.isEmpty()) 
                {
                    continue;
                }
                
                if (firstLine) 
                {
                    FirstLine result = validateFirstLine(buffer);
                    rows = result.rows();
                    cols = result.cols();
                    
                    if (result.errorMessage() != null) 
                    {
                        return new Input(-1, -1, -1, null, result.errorMessage(), null);
                    }
                    
                    firstLine = false;
                    secondLine = true;
                    continue;
                }
                
                if (secondLine) 
                {
                    SecondLine result = validateSecondLine(buffer);
                    pieces = result.pieces();
                    
                    if (result.errorMessage() != null) 
                    {
                        return new Input(-1, -1, -1, null, result.errorMessage(), null);
                    }
                    
                    secondLine = false;
                    continue;
                }
                
                boardConfig.add(buffer);
            }
        }
        
        Object[] validationResult = validateBoardConfig(boardConfig, rows, cols);
        String configError = (String) validationResult[0];
        ExitPosition exitPosition = (ExitPosition) validationResult[1];
        
        if (configError != null) 
        {
            return new Input(-1, -1, -1, null, configError, null);
        }
        
        // Extract the actual board rows (removing any exit-only rows and exit characters)
        ArrayList<String> actualBoardRows = new ArrayList<>();
        ArrayList<String> originalBoardConfig = new ArrayList<>(boardConfig);
        
        // Case 1: Extra row at the top with K (already handled in validateBoardConfig)
        // Case 2: Extra row at the bottom with K (already handled in validateBoardConfig)
        
        // Process the rows that are part of the actual board
        for (int i = 0; i < Math.min(rows, boardConfig.size()); i++) {
            String line = boardConfig.get(i);
            
            // Case 3: Remove left-side exit
            if (line.startsWith("K")) {
                line = line.substring(1);
            }
            
            // Case 4: Remove right-side exit
            if (line.length() > cols && line.charAt(cols) == 'K') {
                line = line.substring(0, cols);
            }
            
            // Ensure the line is exactly cols characters long
            if (line.length() < cols) {
                StringBuilder sb = new StringBuilder(line);
                while (sb.length() < cols) {
                    sb.append('.');  // Pad with empty cells if needed
                }
                line = sb.toString();
            } else if (line.length() > cols) {
                line = line.substring(0, cols);  // Truncate if too long
            }
            
            actualBoardRows.add(line);
        }
        
        // Pad the board if we don't have enough rows
        while (actualBoardRows.size() < rows) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < cols; j++) {
                sb.append('.');
            }
            actualBoardRows.add(sb.toString());
        }
        
        return new Input(rows, cols, pieces, actualBoardRows, null, exitPosition);
    }
}