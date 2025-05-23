package src.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Input class to read and parse Rush Hour puzzle input from a .txt file and return an Input object.
 * This class provides methods to validate and parse Rush Hour puzzle inputs.
 */
public class Input 
{
    private int A;                         // Number of rows in the board
    private int B;                         // Number of columns in the board
    private int N;                         // Number of cars on the board
    private int exitRow;                   // Row of the exit
    private int exitCol;                   // Column of the exit
    private int maxBufLen;                 // Maximum buffer length for the board configuration
    private String exitSide;               // Side of the exit (LEFT, RIGHT, TOP, BOTTOM)
    private String errorMsg;               // Error message if any
    private ArrayList<String> boardConfig; // Configuration of the board
    public record ExitPosition(int row, int col, String side) {}

    /**
     * Default constructor for Input class.
     * Initializes the object with default values.
     */
    public Input()
    {
        this.A = -1;
        this.B = -1;
        this.N = -1;
        this.exitRow = -1;
        this.exitCol = -1;
        this.exitSide = null;
        this.errorMsg = null;
        this.boardConfig = new ArrayList<>();
    }

    /**
     * Constructs an Input object with the provided parameters.
     *
     * @param rows number of rows of the board (A)
     * @param cols number of columns of the board (B)
     * @param cars number of cars excluding the primary car (N)
     * @param boardConfig list of strings representing the board configuration
     * @param errorMsg error message if any
     * @param exitPosition position of the exit
     */
    public Input(int rows, int cols, int cars, ArrayList<String> boardConfig, ExitPosition exitPosition, String errorMsg) 
    {
        this.A = rows;
        this.B = cols;
        this.N = cars;
        this.exitRow = exitPosition.row;
        this.exitCol = exitPosition.col;
        this.exitSide = exitPosition.side;
        this.errorMsg = errorMsg;
        this.boardConfig = boardConfig;
    }

    /**
     * Get the number of rows in the puzzle board.
     * @return Number of rows
     */
    public int getRows() { return this.A; }
    
    /**
     * Get the number of columns in the puzzle board.
     * @return Number of columns
     */
    public int getCols() { return this.B; }
    
    /**
     * Get the number of non-primary cars in the puzzle.
     * @return Number of non-primary cars
     */
    public int getNumCars() { return this.N; }
    
    /**
     * Get the row of the exit position.
     * @return Row of the exit
     */
    public int getExitRow() { return this.exitRow; }

    /**
     * Get the column of the exit position.
     * @return Column of the exit
     */
    public int getExitCol() { return this.exitCol; }

    /**
     * Get the side of the exit position.
     * @return Side of the exit
     */
    public String getExitSide() { return this.exitSide; }

    /**
     * Get any error message from the input validation.
     * @return Error message or null if no errors
     */
    public String getErrorMsg() { return this.errorMsg; }

    /**
     * Check if there was an error during input validation.
     * @return True if there was an error, false otherwise
     */
    public boolean hasError() { return this.errorMsg != null; }

    /**
     * Get the board configuration as a list of strings.
     * Each string represents one row of the board.
     * @return ArrayList of strings representing the board configuration
     */
    public ArrayList<String> getBoardConfig() { return this.boardConfig; }

    /**
     * Validates the filename.
     *
     * @param fileName Filename to validate
     * @return Error message if invalid, null otherwise
     */
    public void validateFilename(String fileName) 
    {
        if (fileName == null || fileName.isEmpty()) 
            this.errorMsg = "Filename cannot be empty.";
    }

    /**
     * Validates the file - checks if it exists, is readable, and not empty.
     *
     * @param file File object to validate
     * @return Error message if invalid, null otherwise
     */
    public void validateFile(File file) 
    {
        if (!file.exists()) 
        {
            this.errorMsg = "'" + file.getName() + "' does not exist.";
            return;
        }
        else if (!file.canRead()) 
        {
            this.errorMsg = "File cannot be read. Please check file permissions.";
            return;
        }
        else if (file.length() == 0) 
        {
            this.errorMsg = "File is empty.";
            return;
        }
    }

    /**
     * Validates the first line containing board dimensions (A B).
     * 
     * @param line First line of the input file
     * @return FirstLine record containing validated values or error message
     */
    public void validateFirstLine(String line) 
    {
        String[] tokens = line.split("\\s+");
        if (tokens.length != 2) 
        {
            this.errorMsg = "First line must contain exactly two values: A and B (board dimensions). Found " + tokens.length + " values instead.";
            return;
        }

        // Before parsing, check if the values are integers
        for (String token : tokens) 
        {
            if (!token.matches("\\d+")) 
            {
                this.errorMsg = "A and B must be positive integers. Found " + tokens[0] + ", " + tokens[1] + " instead.";
                return;
            }
        }

        int rows = Integer.parseInt(tokens[0]);
        int cols = Integer.parseInt(tokens[1]);

        if (rows < 1 || cols < 1) 
        {
            this.errorMsg = "A and B must be positive integers. Found A = " + rows + ", B = " + cols + ".";
            return;
        }
        
        this.A = rows;
        this.B = cols;
    }
    
    /**
     * Validates the second line containing number of cars (N).
     * 
     * @param line Second line of the input file
     * @return SecondLine record containing validated values or error message
     */
    public void validateSecondLine(String line) 
    {
        String[] tokens = line.split("\\s+");
        if (tokens.length != 1) 
        {
            this.errorMsg = "Second line must contain exactly one value: N (number of non-primary cars). Found: " + line;
            return;
        }

        for (String token : tokens) 
        {
            if (!token.matches("\\d+")) 
            {
                this.errorMsg = "N must be a non-negative integer. Found N = " + token + ".";
                return;
            }
        }

        int cars = Integer.parseInt(tokens[0]);

        if (cars < 0 || cars > 24) 
        {
            this.errorMsg = "Invalid number of cars. N must be between 0 and 24. Found N = " + cars + ".";
            return;
        }
        
        this.N = cars;
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
    public void validateBoardConfig() 
    {
        ExitPosition exitPosition = null;
                
        if (this.boardConfig.size() > this.getRows() && 
            this.boardConfig.get(0).contains("K")) 
         {
            // Check if the top exit line only contains 'K' and whitespace
            if (this.boardConfig.get(0).matches("^\\s*K\\s*$")) 
            {
                int kIndex = this.boardConfig.get(0).indexOf('K');
                
                // Create exit position
                exitPosition = new ExitPosition(0, kIndex, "TOP");
                
                // Check if K is within the valid column range
                if (kIndex >= this.getCols()) 
                {
                    this.errorMsg = "Exit (K) cannot be in Corner or exceeds further.";
                    return;
                }
                
                // Check if there is a valid path from exit to a car
                boolean foundValidPath = false;
                for (int i = 1; i < this.boardConfig.size(); i++) 
                {
                    // Ensure the row has enough columns to check the K position
                    if (this.boardConfig.get(i).length() > kIndex) 
                    {
                        char cellBelow = this.boardConfig.get(i).charAt(kIndex);
                        // If we found a car or empty space, the path is valid
                        if ((cellBelow >= 'A' && cellBelow <= 'Z') || cellBelow == '.') 
                        {
                            foundValidPath = true;
                            break;
                        }
                    }
                }
                
                if (!foundValidPath) 
                {
                    this.errorMsg = "Exit (K) cannot be in Corner or inside board have whitespaces.";
                    return;
                }
                
                // If all checks pass, remove the top exit line
                this.boardConfig.remove(0);
            } 
            else 
            {
                this.errorMsg = "Exit (K) only allowed along with whitespaces";
                return;
            }
        }
        
        if (this.boardConfig.size() > this.getRows() && 
            this.boardConfig.get(this.boardConfig.size() - 1).trim().matches("K")) 
        {
            if (exitPosition == null) 
            {
                if (this.boardConfig.get(this.boardConfig.size() - 1).matches("^\\s*K\\s*$"))
                {
                    if (this.boardConfig.get(this.boardConfig.size() - 1).indexOf('K') > this.getCols()) 
                    {
                        this.errorMsg = "Exit (K) cannot be in Corner or exceeds further.";
                        return;
                    }
                    else
                    {
                        exitPosition = new ExitPosition(this.boardConfig.size() - 2, 
                                                        this.boardConfig.get(this.boardConfig.size() - 1).indexOf('K'), "BOTTOM");
                        this.boardConfig.remove(this.boardConfig.size() - 1);
                    }
                }
                else 
                {   
                    this.errorMsg = "Exit (K) only allowed along with whitespaces";
                    return;
                }
            }   
            else
            {
                this.errorMsg = "Found multiple exits (K) - at both top and bottom. Only one exit is allowed.";
                return;
            } 
        }

        if (this.boardConfig.size() == this.getRows() + 1 && 
            this.boardConfig.get(this.boardConfig.size() - 1).contains("K")) 
        {
            if (this.boardConfig.get(this.boardConfig.size() - 1).matches("^\\s*K\\s*$"))
            {
                exitPosition = new ExitPosition(this.boardConfig.size() - 1, 
                                                this.boardConfig.get(this.boardConfig.size() - 1).indexOf('K'), "BOTTOM");
                this.boardConfig.remove(this.boardConfig.size() - 1);
            }
            else
            {
                this.errorMsg = "Exit (K) only allowed along with whitespaces";
                return;
            }
        }
        
        if (this.boardConfig.size() != this.getRows())
        {
            this.errorMsg = "Board configuration must have exactly " + this.getRows() + " rows. Found " + this.boardConfig.size() + " rows.";
            return;
        }
        
        boolean foundPrimaryCar = false;
        boolean foundLeftSpace    = false;
        boolean hasLeftExit       = false;
        
        for (int i = 0; i < this.boardConfig.size(); i++) 
        {
            String line = this.boardConfig.get(i);

            if (line.charAt(0) == ' ')
            {
                if (line.charAt(1) != ' ')
                {
                    foundLeftSpace = true;
                    line           = line.substring(1);
                }
                else
                {
                    this.errorMsg = "Too many spaces at the beginning of row " + (i + 1) + ". Maximum 1 space allowed.";
                    return;
                }
            }
            
            if (line.charAt(0) == 'K') 
            {
                if (!foundLeftSpace && i > 0) 
                {
                    this.errorMsg = "Exit (K) must be outside the board.";
                    return;
                }

                if (line.charAt(1) == ' ' || i == this.boardConfig.size() - 1) 
                {
                    this.errorMsg = "Exit cannot be in Corner.";
                    return;
                }

                if (exitPosition == null) 
                {
                    hasLeftExit  = true;
                    exitPosition = new ExitPosition(i, 0, "LEFT");
                    line         = line.substring(1);
                }
                else
                {
                    this.errorMsg = "Found multiple exits (K). Only one exit is allowed.";
                    return;
                }
            }
            
            if (line.length() > this.getCols()) 
            {
                if (line.length() == this.getCols() + 1 && line.charAt(this.getCols()) == 'K') 
                {
                    if (line.charAt(this.getCols() - 1) == ' ') 
                    {
                        this.errorMsg = "Exit cannot be in Corner.";
                        return;
                    }

                    if (exitPosition == null) 
                    {
                        exitPosition  = new ExitPosition(i, this.getCols() - 1, "RIGHT");
                        line          = line.substring(0, this.getCols());
                    }
                    else
                    {
                        this.errorMsg = "Found multiple exits (K). Only one exit is allowed.";
                        return;
                    }
                } 
                else 
                {
                    int idx = i;
                    if (exitPosition != null && exitPosition.side.equals("TOP")) idx++;
                    this.errorMsg     = "Row " + (idx) + " exceeds expected length. Found " + line.length() + 
                                        " characters when expected " + this.getCols() + " or " + (this.getCols() + 1) + " with exit.";
                    return;
                }
            } 
            else if (line.length() < this.getCols()) 
            {
                int idx = i;
                if (exitPosition != null && exitPosition.side.equals("TOP")) idx++;
                this.errorMsg = "Row " + (idx) + " must have at least " + this.getCols() + " columns. Found " + line.length() + " columns.";
                return;
            }
            
            for (int j = 0; j < line.length(); j++) 
            {
                char c = line.charAt(j);
                if (c == 'P') foundPrimaryCar = true;
            }

            this.boardConfig.set(i, line);
        }
        
        if (!foundPrimaryCar) 
        {
            this.errorMsg = "Primary car (P) not found in board configuration.";
            return;
        }
        
        if (exitPosition == null) 
        {
            this.errorMsg = "Exit (K) not found in board configuration.";
            return;
        }

        if (hasLeftExit && !foundLeftSpace) 
        {
            this.errorMsg = "Exit (K) must be outside the board.";
            return;
        }

        if (foundLeftSpace && !hasLeftExit) 
        {
            this.errorMsg = "Found space at the left side of the board, but no Exit (K) on most left column found.";
            return;
        }

        this.A = this.boardConfig.size();
        this.B = this.boardConfig.get(0).length();

        this.exitRow     = exitPosition.row;
        this.exitCol     = exitPosition.col;
        this.exitSide    = exitPosition.side;
    }

    /**
     * Reads Rush Hour puzzle input from a .txt file and returns a new Input object.
     *
     * Expected file format:
     *   A B      // Board dimensions (rows x columns)
     *   N        // Number of non-primary cars
     *   [Board configuration] - may include rows with exit 'K' outside the board
     *
     * @param filePath path to the puzzle specification file
     * @return an Input object containing parsed puzzle data or an error message
     * @throws IOException if an I/O error occurs while reading the file
     */
    public void readInput(String filePath) throws IOException 
    {
        boolean firstLine = true;
        boolean secondLine = true;
        
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
                validateFirstLine(buffer);
                firstLine = false;
                
                if (this.errorMsg != null) 
                {
                    br.close();
                    return;
                }
                else if (length < 2) 
                {
                    br.close();
                    this.errorMsg = "No number of non-primary cars found in the file.";
                    return;
                }
                else continue;
            }
            else if (secondLine)
            {
                validateSecondLine(buffer);
                secondLine = false;

                if (this.errorMsg != null) 
                {
                    br.close();
                    return;
                }
                else if (length < 3) 
                {
                    br.close();
                    this.errorMsg = "No board configuration found in the file.";
                    return;
                }
                else continue;
            }
            else
            {
                // Check if the line is empty or contains only whitespace
                if (buffer.matches("^\s*$")) 
                {
                    br.close();
                    this.errorMsg = "Found empty line in board configuration";
                    return;
                }

                // Check regex for valid characters, which is Uppercase A-Z, ' ', and '.'.
                if (!buffer.matches("^[A-Z \\.]*$"))
                {
                    br.close();
                    this.errorMsg = "Found invalid character in board configuration";
                    return;
                }

                // Check if the line contains more than one exit (K)
                if (!buffer.matches("^[^K]*K?[^K]*$"))
                {
                    br.close();
                    this.errorMsg = "Found multiple exits (K) in the same line";
                    return;
                }
                
                this.maxBufLen = Math.max(this.maxBufLen, buffer.length());
                this.boardConfig.add(buffer);
            }
        }
        
        br.close();
        validateBoardConfig();
    }

    public static String validateOption(Scanner scanner, int numOptions)
    {
        boolean valid = false;
        String algo = null;
        int option = -1;
        
        while (!valid) 
        {
            if (numOptions == 3)
            {
                System.out.println("[#] Algorithm selection:");
                System.out.println();
                System.out.println("[-] 1. A* Algorithm");
                System.out.println("[-] 2. Greedy Best First Search (GBFS)");
                System.out.println("[-] 3. Uniform Cost Search (UCS)");
                System.out.println();
                System.out.println("[?] Enter your choice (1, 2, or 3)");
            }
            else
            {
                System.out.println("[#] Heuristic selection:");
                System.out.println();
                System.out.println("[-] 1. Manhattan Distance");
                System.out.println("[-] 2. Blocking Cars");
                System.out.println();   
                System.out.println("[?] Enter your choice (1 or 2)");
            }
            
            System.out.println();
            String line = scanner.nextLine();
            System.out.println();

            String[] tokens = line.split("\\s+");
            if (tokens.length != 1) 
            {
                System.out.println("[!] Invalid input. Please enter a single integer, example \"1\".");
                System.out.println();
                continue;
            }

            if (!tokens[0].matches("\\d+")) 
            {
                System.out.println("Invalid input. Please enter a non-negative integer.");
                System.out.println();
                continue;
            }

            option = Integer.parseInt(tokens[0]);

            if (option < 1 || option > numOptions) 
            {
                System.out.println("Invalid input. Please enter \"1\" to \"" + numOptions + "\".");
                System.out.println();
                continue;
            }
            else
            {
                valid = true;
                if (numOptions == 3)
                {
                    switch (option) 
                    {
                        case 1:
                            algo = "A*";
                            break;
                        case 2:
                            algo = "GBFS";
                            break;
                        case 3:
                            algo = "UCS";
                            break;
                    }
                }
                else
                {
                    switch (option) 
                    {
                        case 1:
                            algo = "Manhattan";
                            break;
                        case 2:
                            algo = "Blocking";
                            break;
                    }   
                }
            }
        }
        return algo;
    }
}