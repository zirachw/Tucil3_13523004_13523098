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
    private ExitPosition exitPosition; // Position of the exit
    private String errorMsg;

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
    public Input(int rows, int cols, int pieces, ArrayList<String> boardConfig, ExitPosition exitPosition, String errorMsg) 
    {
        this.rows = rows;
        this.cols = cols;
        this.pieces = pieces;
        this.boardConfig = boardConfig;
        this.exitPosition = exitPosition;
        this.errorMsg = errorMsg;
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
        String[] tokens = line.split("\\s+");
        if (tokens.length != 1) 
        {
            String message = "Second line must contain exactly one value: N (number of non-primary pieces). Found: " + line;
            return new SecondLine(-1, message);
        }

        for (String token : tokens) 
        {
            if (!token.matches("\\d+")) 
            {
                String message = "N must be a non-negative integer. Found N = " + token + ".";
                return new SecondLine(-1, message);
            }
        }

        int pieces = Integer.parseInt(tokens[0]);

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
        ExitPosition exitPosition = null;
        
        if (config.size() > rows && config.get(0).matches("K")) {

            if (config.get(0).matches("^\\s*K\\s*$"))
            {
                exitPosition = new ExitPosition(0, config.get(0).indexOf('K'), "TOP");
                config.remove(0);
            }
            else 
                return new Object[] {null, null, "Exit (K) only allowed along with whitespaces"};
        }
        
        if (config.size() > rows && config.get(config.size() - 1).trim().matches("K")) 
        {
            if (exitPosition == null) 
            {
                if (config.get(config.size() - 1).matches("^\\s*K\\s*$"))
                {
                    exitPosition = new ExitPosition(config.size() - 2, config.get(config.size() - 1).indexOf('K'), "BOTTOM");
                    config.remove(config.size() - 1);
                }
                else
                    return new Object[] {null, null, "Exit (K) only allowed along with whitespaces"};
            }   
            else 
                return new Object[] {null, null, "Found multiple exits (K) - at both top and bottom. Only one exit is allowed."};
        }
        
        if (config.size() != rows) 
            return new Object[] {null, null, "Board configuration must have exactly " + rows + " rows. Found " + config.size() + " rows."};
        
        boolean foundPrimaryPiece = false;
        boolean foundLeftSpace = false;
        boolean hasLeftExit    = false;
        
        for (int i = 0; i < config.size(); i++) 
        {
            String line = config.get(i);

            if (line.length() > cols) 
            {
                if (line.length() == cols + 1 && line.charAt(cols) == 'K') 
                {
                    if (exitPosition == null) 
                    {
                        exitPosition = new ExitPosition(i, cols - 1, "RIGHT");
                        line = line.substring(0, cols);
                    }
                    else
                        return new Object[] {null, null, "Found multiple exits (K). Only one exit is allowed."};
                } 
                else 
                {
                    return new Object[] {null, null, "Row " + (i + 1) + " exceeds expected length. Found " + line.length() + 
                           " characters when expected " + cols + " or " + (cols + 1) + " with exit."};
                }
            } 
            else if (line.length() < cols) 
            {
                return new Object[] {null, null, "Row " + (i + 1) + " must have at least " + cols + " columns. Found " + line.length() + " columns."};
            }

            if (line.charAt(0) == ' ')
            {
                foundLeftSpace = true;
                line           = line.substring(1);
            }

            if (line.charAt(0) == 'K') 
            {
                if (!foundLeftSpace && i > 0) 
                    return new Object[] {null, null, "Exit (K) must be outside the board."};

                if (exitPosition == null) 
                {
                    hasLeftExit  = true;
                    exitPosition = new ExitPosition(i, -1, "LEFT");
                    line         = line.substring(1);
                }
                else
                    return new Object[] {null, null, "Found multiple exits (K). Only one exit is allowed."};
            }
            
            for (int j = 0; j < line.length(); j++) 
            {
                char c = line.charAt(j);
                
                if (c == 'P') foundPrimaryPiece = true;
            }

            config.set(i, line);
        }
        
        if (!foundPrimaryPiece) 
        {
            return new Object[] {null, null, "Primary piece (P) not found in board configuration."};
        }
        
        if (exitPosition == null) 
        {
            return new Object[] {null, null, "Exit (K) not found in board configuration." };
        }

        if (hasLeftExit && !foundLeftSpace) 
        {
            return new Object[] {null, null, "Exit (K) must be outside the board."};
        }
        
        return new Object[] {config, exitPosition, null}; // No errors found
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
        boolean secondLine = true;
        String errorMessage = null;
        int rows = -1, cols = -1, pieces = -1;
        ArrayList<String> boardConfig = new ArrayList<>();
        
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        BufferedReader checker = new BufferedReader(new FileReader(filePath));

        int length  = 0;
        while (checker.readLine() != null) length++;
        checker.close();

        String buffer;
        while ((buffer = br.readLine()) != null) 
        {
            if (firstLine) 
            {
                FirstLine first = validateFirstLine(buffer);
                rows = first.rows();
                cols = first.cols();
                firstLine = false;
                
                if (first.errorMessage() != null) 
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, first.errorMessage());
                }
                else if (length < 2) 
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, "No number of non-primary pieces found in the file.");
                }
                else continue;
            }
            else if (secondLine)
            {
                SecondLine second = validateSecondLine(buffer);
                pieces = second.pieces();
                secondLine = false;

                if (second.errorMessage() != null) 
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, second.errorMessage());
                }
                else if (length < 3) 
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, "No board configuration found in the file.");
                }
                else continue;
            }
            else
            {
                // Check if the line is empty or contains only whitespace
                if (buffer.matches("^\s*$")) 
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, "Found empty line in board configuration");
                }

                // Check regex for valid characters, which is Uppercase A-Z, ' ', and '.'.
                if (!buffer.matches("^[A-Z \\.]*$"))
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, "Found invalid character in board configuration");
                }

                // Check if the line contains more than one exit (K)
                if (!buffer.matches("^[^K]*K?[^K]*$"))
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, "Found multiple exits (K) in the same line");
                }

                boardConfig.add(buffer);
            }
        }
        
        br.close();
        
        Object[]          validationResult = validateBoardConfig (boardConfig, rows, cols);
        ArrayList<String> actualBoard      = (ArrayList<String>) validationResult[0];
        ExitPosition      exitPosition     = (ExitPosition)      validationResult[1];
                          errorMessage     = (String)            validationResult[2];
        
        if (errorMessage != null) 
            return new Input(-1, -1, -1, null, null, errorMessage);
        
        else
            return new Input(rows, cols, pieces, actualBoard, exitPosition, null);
    }
}