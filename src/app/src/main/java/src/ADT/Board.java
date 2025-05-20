package src.ADT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class representing the game board for the Rush Hour puzzle.
 */
public class Board {
    
    // Constants
    public static final int UNKNOWN    = -1;
    public static final int VERTICAL   = 0;
    public static final int HORIZONTAL = 1;
    private static final char EXIT_CHAR = 'K';
    private static final String RESET = "\u001B[0m";

    // Attributes
    private int A;                        // Number of rows in the board
    private int B;                        // Number of columns in the board
    private int N;                        // Number of cars on the board
    private int exitRow;                  // Exit row position
    private int exitCol;                  // Exit column position
    private char[][] grid;                // The game grid
    private List<Car> cars;               // All cars on the board
    private String exitSide;              // Side of the exit
    private String errorMsg;              // Error message for invalid configurations
    private String[] palette;             // Color palette for cars
    private Integer currentMovedCarIndex; // Index of the car that is currently being moved (for highlighting)
    
    /**
     * Constructor for the Board class
     * 
     * @param rows Number of rows in the board
     * @param cols Number of columns in the board
     * @param numCars Number of cars on the board
     * @param exitRow Row position of the exit
     * @param exitCol Column position of the exit
     */
    public Board(int rows, 
                 int cols, 
                 int numCars, 
                 int exitRow, 
                 int exitCol, 
                 String exitSide, 
                 String errorMsg) 
    {
        this.A        = rows;
        this.B        = cols;
        this.N        = numCars;
        this.exitRow  = exitRow;
        this.exitCol  = exitCol;
        this.exitSide = exitSide;
        this.grid     = new char[rows][cols];
        this.cars     = new ArrayList<>();
        this.errorMsg = errorMsg;
        this.palette  = generatePalette();
        this.currentMovedCarIndex = null;
        
        // Initialize the grid with empty cells
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                grid[i][j] = '.';
    }
    
    public int getRows() { return this.A; }
    public int getCols() { return this.B; }
    public int getNumCars() { return this.N; }
    public char[][] getGrid() { return this.grid; }
    public char getElement(int i, int j) { return this.grid[i][j]; }
    public List<Car> getCars() { return this.cars; }
    public int getExitRow() { return this.exitRow; }
    public int getExitCol() { return this.exitCol; }
    public String getExitSide() { return this.exitSide; }
    public String getErrorMsg() { return this.errorMsg; }
    public boolean hasError() { return this.errorMsg != null; }
    public String[] getPalette() { return this.palette; }
    public Integer getCurrentMovedCarIndex() { return this.currentMovedCarIndex; }
    public void setCurrentMovedCarIndex(Integer index) { this.currentMovedCarIndex = index; }

    /**
     * Creates a deep copy of the current board state
     * 
     * @return A new Board object with the same state
     */
    public Board copy() 
    {
        Board newBoard = new Board(this.getRows(), 
                                   this.getCols(), 
                                   this.getNumCars(), 
                                   this.getExitRow(), 
                                   this.getExitCol(),
                                   this.getExitSide(),
                                   this.getErrorMsg());
        
        // Copy the grid
        for (int i = 0; i < this.getRows(); i++) 
            System.arraycopy(this.grid[i], 0, newBoard.grid[i], 0, this.getCols());
        
        // Copy the cars
        for (Car car : this.getCars()) 
        {
            Car newCar = car.copy();
            newBoard.cars.add(newCar);
        }
        
        newBoard.exitRow  = this.getExitRow();
        newBoard.exitCol  = this.getExitCol();
        newBoard.errorMsg = this.getErrorMsg();
        newBoard.currentMovedCarIndex = this.currentMovedCarIndex;
        
        return newBoard;
    }
    
    /**
     * Check if the board is in a solved state (primary car at exit)
     * 
     * @return True if solved, false otherwise
     */
    public boolean isSolved() 
    {
        Car primaryCar = getPrimaryCar();
        if (primaryCar == null) return false;
        
        int startRow = primaryCar.getStartRow();
        int startCol = primaryCar.getStartCol();
        int endRow = startRow + (primaryCar.getOrientation() == VERTICAL ? primaryCar.getLength() - 1 : 0);
        int endCol = startCol + (primaryCar.getOrientation() == HORIZONTAL ? primaryCar.getLength() - 1 : 0);
        
        if (primaryCar.getOrientation() == HORIZONTAL) 
        {
            if (exitCol == this.getCols() - 1)
                return (endCol == this.getCols() - 1) && (startRow == exitRow);
            
            else if (exitCol == 0)
                return (startCol == 0) && (startRow == exitRow);
        } 
        else 
        {
            if (exitRow == this.getRows()-1)
                return (endRow == this.getRows()-1) && (startCol == exitCol);
            
            else if (exitRow == 0)
                return (startRow == 0) && (startCol == exitCol);
        }
        
        return false;
    }
    
    /**
     * Get all valid moves for a specific car
     * 
     * @param carIndex Index of the car in the cars list
     * @return List of valid move amounts (positive for right/down, negative for left/up)
     */
    public List<Integer> getValidMoves(int carIndex) 
    {
        Car car = cars.get(carIndex);
        List<Integer> validMoves = new ArrayList<>();
        
        if (car.getOrientation() == HORIZONTAL) 
        {
            // Check moves to the left
            int leftMost = car.getStartCol();
            int leftBound = 0;
            
            for (int col = leftMost - 1; col >= 0; col--) 
            {
                if (isOccupied(car.getStartRow(), col)) 
                {
                    leftBound = col + 1;
                    break;
                }
            }
            
            // Add all possible left moves
            for (int col = leftMost - 1; col >= leftBound; col--) 
                validMoves.add(col - leftMost); // This will be negative (left)
            
            // Check moves to the right
            int rightMost = car.getStartCol() + car.getLength() - 1;
            int rightBound = this.getCols() - 1;
            
            for (int col = rightMost + 1; col < this.getCols(); col++) 
            {
                if (isOccupied(car.getStartRow(), col)) 
                {
                    rightBound = col - 1;
                    break;
                }
            }
            
            // Add all possible right moves
            for (int delta = 1; delta <= rightBound - rightMost; delta++) 
                validMoves.add(delta); // This will be positive (right)

        } 
        else 
        {
            // Check moves up
            int topMost = car.getStartRow();
            int topBound = 0;
            
            for (int row = topMost - 1; row >= 0; row--) 
            {
                if (isOccupied(row, car.getStartCol())) 
                {
                    topBound = row + 1;
                    break;
                }
            }
            
            // Add all possible up moves
            for (int row = topMost - 1; row >= topBound; row--)
                validMoves.add(row - topMost); // This will be negative (up)
            
            // Check moves down
            int bottomMost = car.getStartRow() + car.getLength() - 1;
            int bottomBound = this.getRows() - 1;
            
            for (int row = bottomMost + 1; row < this.getRows(); row++) 
            {
                if (isOccupied(row, car.getStartCol())) 
                {
                    bottomBound = row - 1;
                    break;
                }
            }
            
            // Add all possible down moves
            for (int delta = 1; delta <= bottomBound - bottomMost; delta++) 
                validMoves.add(delta); // This will be positive (down)
        }
        
        return validMoves;
    }
    
    /**
     * Check if a cell is occupied by any car
     * 
     * @param row Row index
     * @param col Column index
     * @return True if occupied, false otherwise
     */
    private boolean isOccupied(int row, int col) 
    {
        // Check if cell is within bounds
        if (row < 0 || row >= this.getRows() || col < 0 || col >= this.getCols())
            return true; // Treat out-of-bounds as occupied

        return grid[row][col] != '.';
    }
    
    /**
     * Apply a move to a car
     * 
     * @param carIndex Index of the car in the cars list
     * @param move Amount to move (positive for right/down, negative for left/up)
     * @return A new Board with the move applied
     */
    public Board applyMove(int carIndex, int move) 
    {
        Board newBoard = this.copy();
        Car car = newBoard.cars.get(carIndex);
        
        // Clear the current car position
        int[][] oldCells = car.getOccupiedCells();
        for (int[] cell : oldCells)
            newBoard.grid[cell[0]][cell[1]] = '.';
        
        // Move the car
        car.move(move);
        
        // Update the grid with the new car position
        int[][] newCells = car.getOccupiedCells();
        for (int[] cell : newCells)
            newBoard.grid[cell[0]][cell[1]] = car.getId();
        
        newBoard.setCurrentMovedCarIndex(carIndex);
        
        return newBoard;
    }
    
    /**
     * Load a board configuration from a 2D character array
     * 
     * @param boardConfig The board configuration as ArrayList of strings
     */
    public void loadConfiguration(ArrayList<String> boardConfig) 
    {
        char[][] config = new char[this.getRows()][this.getCols()];

        for (int i = 0; i < this.getRows(); i++) 
            config[i] = boardConfig.get(i).toCharArray();
        
        // Extract cars from the grid
        Map<Character, ArrayList<int[]>> carLocations = new HashMap<>();
        boolean foundPrimary = false;

        for (int i = 0; i < this.getRows(); i++) 
        {
            for (int j = 0; j < this.getCols(); j++) 
            {
                grid[i][j] = config[i][j];

                if (grid[i][j] != '.') 
                {
                    // If the car is not already in the map, add it
                    if (!carLocations.containsKey(grid[i][j]))
                    {
                        carLocations.put(grid[i][j], new ArrayList<int[]>());
                        carLocations.get(grid[i][j]).add(new int[]{i, j, UNKNOWN});
                        
                        if (grid[i][j] == 'P') foundPrimary = true;
                    }
                    else
                    {
                        int[] firstLocation = carLocations.get(grid[i][j]).get(0);
                        int[] lastLocation = carLocations.get(grid[i][j]).get(carLocations.get(grid[i][j]).size() - 1);
                        
                        if (carLocations.get(grid[i][j]).size() == 1) 
                        {
                            if ((firstLocation[0] != i || firstLocation[1] != j - 1) && 
                                (firstLocation[0] != i - 1 || firstLocation[1] != j))
                            {
                                this.errorMsg = "Found duplicate car " + grid[i][j];
                                return;
                            }
                            else if (firstLocation[0] == i && firstLocation[1] == j - 1)
                            {
                                carLocations.get(grid[i][j]).get(0)[2] = HORIZONTAL;
                                carLocations.get(grid[i][j]).add(new int[]{i, j, HORIZONTAL});
                            }
                            else if (firstLocation[0] == i - 1 && firstLocation[1] == j)
                            {
                                carLocations.get(grid[i][j]).get(0)[2] = VERTICAL;
                                carLocations.get(grid[i][j]).add(new int[]{i, j, VERTICAL});
                            }
                        }
                        else
                        {
                            // For cars with 2+ segments, check if the new piece aligns with existing orientation
                            int orientation = firstLocation[2];
                            
                            if (orientation == HORIZONTAL)
                            {
                                // For horizontal cars, must be in same row and adjacent column
                                if (firstLocation[0] == i && lastLocation[1] == j - 1)
                                {
                                    carLocations.get(grid[i][j]).add(new int[]{i, j, HORIZONTAL});
                                }
                                else
                                {
                                    this.errorMsg = "Car " + grid[i][j] + " has invalid shape";
                                    return;
                                }
                            }
                            else if (orientation == VERTICAL)
                            {
                                // For vertical cars, must be in same column and adjacent row
                                if (lastLocation[0] == i - 1 && firstLocation[1] == j)
                                {
                                    carLocations.get(grid[i][j]).add(new int[]{i, j, VERTICAL});
                                }
                                else
                                {
                                    this.errorMsg = "Car " + grid[i][j] + " has invalid shape";
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Process all cars and create Car objects
        for (Map.Entry<Character, ArrayList<int[]>> entry : carLocations.entrySet()) 
        {
            char id = entry.getKey();
            ArrayList<int[]> locations = entry.getValue();
            
            boolean isPrimary = (id == 'P');
            int size = locations.size();
            
            // Determine orientation
            int orientation = (locations.get(0)[2] == HORIZONTAL) ? HORIZONTAL : VERTICAL;
            int startRow = locations.get(0)[0];
            int startCol = locations.get(0)[1];
            
            // Create the car
            Car car = new Car(id, startRow, startCol, size, isPrimary, orientation);
            this.cars.add(car);
        }

        if (!foundPrimary) 
        {
            this.errorMsg = "No primary car found on the board";
            return;
        }
        
        if (carLocations.size() - 1 < this.getNumCars())
        {
            this.errorMsg = "There are too few cars. Expected " + N + " but found " + (carLocations.size() - 1);
            return;
        }

        if (carLocations.size() - 1 > this.getNumCars())
        {
            this.errorMsg = "There are too many cars. Expected " + N + " but found " + (carLocations.size() - 1);
            return;
        }
        
        // Validate the primary car's position relative to the exit
        if (foundPrimary) 
        {
            ArrayList<int[]> primaryLocations = carLocations.get('P');
            if (primaryLocations != null && !primaryLocations.isEmpty()) 
            {
                int orientation = primaryLocations.get(0)[2];
                
                // For LEFT or RIGHT exits, the primary car must be HORIZONTAL and in the exit row
                if (exitSide.equals("LEFT") || exitSide.equals("RIGHT")) 
                {
                    if (orientation != HORIZONTAL)
                    {
                        this.errorMsg = "Unsolvable: Primary car must be horizontal for left/right exits";
                        return;
                    }
                    
                    boolean isInExitRow = false;
                    for (int[] location : primaryLocations) 
                    {
                        if (location[0] == exitRow) 
                        {
                            isInExitRow = true;
                            break;
                        }
                    }
                    
                    if (!isInExitRow) 
                    {
                        this.errorMsg = "Unsolvable: Primary car is not in the same row as the exit";
                        return;
                    }
                }
                // For TOP or BOTTOM exits, the primary car must be VERTICAL and in the exit column
                else if (exitSide.equals("TOP") || exitSide.equals("BOTTOM")) 
                {
                    if (orientation != VERTICAL) 
                    {
                        this.errorMsg = "Unsolvable: Primary car must be vertical for top/bottom exits";
                        return;
                    }
                    
                    boolean isInExitColumn = false;
                    for (int[] location : primaryLocations) 
                    {
                        if (location[1] == exitCol) 
                        {
                            isInExitColumn = true;
                            break;
                        }
                    }
                    
                    if (!isInExitColumn) 
                    {
                        this.errorMsg = "Unsolvable: Primary car is not in the same column as the exit";
                        return;
                    }
                }
            }
        }
    }

    /**
     * Get a string representation of the current board state with colored cars
     * and appropriate exit rendering based on exitSide
     * 
     * @return String representation of the board
     */
    @Override
    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        
        if (exitSide != null && exitSide.equalsIgnoreCase("TOP")) 
        {
            // Case: Top exit
            for (int j = 0; j < this.getCols(); j++) 
            {
                if (j == exitCol) 
                {
                    int exitColorIndex = EXIT_CHAR - 'A';
                    if (exitColorIndex >= 0 && exitColorIndex < palette.length)
                        sb.append(palette[exitColorIndex]).append(EXIT_CHAR).append(RESET);
                    
                    else sb.append(EXIT_CHAR);
                } 
                else sb.append(' ');
            }
            sb.append("\n");
        }
        
        for (int i = 0; i < this.getRows(); i++) 
        {
            // Case: Left exit
            if (exitSide != null && 
                exitSide.equalsIgnoreCase("LEFT") && 
                i == exitRow) 
            {
                int exitColorIndex = EXIT_CHAR - 'A';
                if (exitColorIndex >= 0 && exitColorIndex < palette.length)
                    sb.append(palette[exitColorIndex]).append(EXIT_CHAR).append(RESET);
                
                else sb.append(EXIT_CHAR);
            } 
            else if (exitSide != null && exitSide.equalsIgnoreCase("LEFT"))
                sb.append(' ');
            
            // Add the main grid
            for (int j = 0; j < this.getCols(); j++) 
            {
                char cell = grid[i][j];
                
                if (cell == '.') sb.append(cell);
                else 
                {
                    boolean shouldColor = (currentMovedCarIndex == null) || 
                                         (currentMovedCarIndex != null && 
                                          cars.get(currentMovedCarIndex).getId() == cell);
                    
                    if (shouldColor) 
                    {
                        int colorIndex = cell - 'A';
                        if (colorIndex >= 0 && colorIndex < palette.length)
                            sb.append(palette[colorIndex]).append(cell).append(RESET);

                        else sb.append(cell);
                    } 
                    else sb.append(cell);
                }
            }
            
            // Case: Right exit
            if (exitSide != null && exitSide.equalsIgnoreCase("RIGHT") && i == exitRow) 
            {
                int exitColorIndex = EXIT_CHAR - 'A';
                if (exitColorIndex >= 0 && exitColorIndex < palette.length)
                    sb.append(palette[exitColorIndex]).append(EXIT_CHAR).append(RESET);

                else sb.append(EXIT_CHAR);
            } 
            else if (exitSide != null && exitSide.equalsIgnoreCase("RIGHT"))
                sb.append(' ');
            
            if (i < this.getRows() - 1) sb.append("\n");
        }
        
        // Case: Bottom exit
        if (exitSide != null && exitSide.equalsIgnoreCase("BOTTOM")) 
        {
            sb.append("\n");
            for (int j = 0; j < this.getCols(); j++) 
            {
                if (j == exitCol) 
                {
                    int exitColorIndex = EXIT_CHAR - 'A';
                    if (exitColorIndex >= 0 && exitColorIndex < palette.length)
                        sb.append(palette[exitColorIndex]).append(EXIT_CHAR).append(RESET);
                    
                    else sb.append(EXIT_CHAR);
                } 
                else sb.append(' ');
            }
        }
        
        return sb.toString();
    }

    /**
     * Get a string representation of the cars on the board
     * 
     * @return String representation of the cars
     */
    @Override
    public int hashCode() 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.getRows(); i++)
            for (int j = 0; j < this.getCols(); j++)
                sb.append(grid[i][j]);
        
        return sb.toString().hashCode();
    }
    
    /**
     * Check if two boards are equal
     * 
     * @param obj The object to compare with
     * @return True if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Board other = (Board) obj;
        if (this.getRows() != other.getRows() || this.getCols() != other.getCols()) return false;
        
        for (int i = 0; i < this.getRows(); i++)
            for (int j = 0; j < this.getCols(); j++)
                if (grid[i][j] != other.grid[i][j])
                    return false;
        
        return true;
    }

    /**
     * Get Primary Car
     * 
     * @return Primary Car
     */
    public Car getPrimaryCar() 
    {
        for (Car car : this.getCars()) 
        {
            if (car.isPrimary()) 
                return car;
        }
        return null; 
    }

    /**
     * Generates a palette of colors for the cars.
     *
     * @return Array of ANSI color codes
     */
    public String[] generatePalette() 
    {
        String[] palette = 
        {
            "\u001B[38;2;50;255;50m",    // A: Bright Green (#32FF32)
            "\u001B[38;2;200;100;255m",  // B: Light Purple (#C864FF)
            "\u001B[38;2;255;215;0m",    // C: Gold (#FFD700)
            "\u001B[38;2;255;255;50m",   // D: Bright Yellow (#FFFF32)
            "\u001B[38;2;255;50;255m",   // E: Bright Magenta (#FF32FF)
            "\u001B[38;2;50;255;255m",   // F: Bright Cyan (#32FFFF)
            "\u001B[38;2;255;128;0m",    // G: Orange (#FF8000)
            "\u001B[38;2;128;255;128m",  // H: Light Green (#80FF80)
            "\u001B[38;2;180;180;255m",  // I: Light Blue (#B4B4FF)
            "\u001B[38;2;255;200;100m",  // J: Light Orange (#FFC864)
            "\u001B[38;2;50;150;255m",   // K: Bright Blue (#3296FF)
            "\u001B[38;2;100;255;200m",  // L: Light Turquoise (#64FFC8)
            "\u001B[38;2;255;150;150m",  // M: Light Red (#FF9696)
            "\u001B[38;2;150;255;150m",  // N: Pale Green (#96FF96)
            "\u001B[38;2;255;160;122m",  // O: Light Salmon (#FFA07A)
            "\u001B[38;2;255;50;50m",    // P: Bright Red (#FF3232)
            "\u001B[38;2;173;255;47m",   // Q: Green Yellow (#ADFF2F)
            "\u001B[38;2;64;224;208m",   // R: Turquoise (#40E0D0)
            "\u001B[38;2;238;130;238m",  // S: Violet (#EE82EE)
            "\u001B[38;2;255;160;160m",  // T: Light Coral (#FFA0A0)
            "\u001B[38;2;135;206;250m",  // U: Light Sky Blue (#87CEFA)
            "\u001B[38;2;200;200;200m",  // V: Light Gray (#C8C8C8)
            "\u001B[38;2;255;100;100m",  // W: Salmon (#FF6464)
            "\u001B[38;2;255;105;180m",  // X: Hot Pink (#FF69B4)
            "\u001B[38;2;150;255;0m",    // Y: Bright Lime (#96FF00)
            "\u001B[38;2;255;80;80m"     // Z: Light Crimson (#FF5050)
        };
        return palette;
    }
}