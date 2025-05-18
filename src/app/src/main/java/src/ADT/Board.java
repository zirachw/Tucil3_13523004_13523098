package src.ADT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing the game board for the Rush Hour puzzle.
 */
public class Board {
    
    // Constants
    public static final int UNKNOWN    = -1;
    public static final int VERTICAL   = 0;
    public static final int HORIZONTAL = 1;

    // Attributes
    private int A;               // Number of rows in the board
    private int B;               // Number of columns in the board
    private int N;               // Number of pieces on the board
    private int exitRow;         // Exit row position
    private int exitCol;         // Exit column position
    private char[][] grid;       // The game grid
    private List<Piece> pieces;  // All pieces on the board
    private String errorMsg;     // Error message for invalid configurations
    private String[] palette;    // Color palette for pieces
    
    /**
     * Constructor for the Board class
     * @param rows Number of rows in the board
     * @param cols Number of columns in the board
     * @param numPieces Number of pieces on the board
     * @param exitRow Row position of the exit
     * @param exitCol Column position of the exit
     */
    public Board(int rows, int cols, int numPieces, int exitRow, int exitCol, String errorMsg) {
        this.A        = rows;
        this.B        = cols;
        this.N        = numPieces;
        this.exitRow  = exitRow;
        this.exitCol  = exitCol;
        this.grid     = new char[rows][cols];
        this.pieces   = new ArrayList<>();
        this.errorMsg = errorMsg;
        this.palette  = generatePalette();
        
        // Initialize the grid with empty cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }
    }
    
    /**
     * Get the rows of the board
     * @return
     */
    public int getRows() { return this.A; }
    
    /**
     * Get the columns of the board
     * @return
     */
    public int getCols() { return this.B; }

    /**
     * Get the number of pieces on the board
     * @return
     */
    public int getNumPieces() { return this.N; }
    
    /**
     * Get the grid of the board
     * @return
     */
    public char[][] getGrid() { return this.grid; }

    /**
     * Get the element of the grid at a specific position
     * @return
     */
    public char getElement(int i, int j) { return this.grid[i][j]; }
    
    /**
     * Get the pieces on the board
     * @return
     */
    public List<Piece> getPieces() { return this.pieces; }
    
    /**
     * Get the exit row position
     * @return
     */
    public int getExitRow() { return this.exitRow; }
    
    /**
     * Get the exit column position
     * @return
     */
    public int getExitCol() { return this.exitCol; }
    
    /**
     * Get the error message
     * @return
     */
    public String getErrorMsg() { return this.errorMsg; }

    /**
     * Set the error message
     * @param errorMsg The error message
     */
    public boolean hasError() { return this.errorMsg != null; }

    /** 
     * Get the color palette for the pieces
     * @return
     */
    public String[] getPalette() { return this.palette; }

    /**
     * Creates a deep copy of the current board state
     * 
     * @return A new Board object with the same state
     */
    public Board copy() 
    {
        Board newBoard = new Board(this.getRows(), 
                                   this.getCols(), 
                                   this.getNumPieces(), 
                                   this.getExitRow(), 
                                   this.getExitCol(),
                                   this.getErrorMsg());
        
        // Copy the grid
        for (int i = 0; i < this.getRows(); i++) 
            System.arraycopy(this.grid[i], 0, newBoard.grid[i], 0, this.getCols());
        
        // Copy the pieces
        for (Piece piece : this.getPieces()) 
        {
            Piece newPiece = piece.copy();
            newBoard.pieces.add(newPiece);
        }
        
        newBoard.exitRow  = this.getExitRow();
        newBoard.exitCol  = this.getExitCol();
        newBoard.errorMsg = this.getErrorMsg();
        
        return newBoard;
    }
    
    /**
     * Load a board configuration from a 2D character array
     * 
     * @param config The board configuration
     * @return True if successful, false otherwise
     */
    public void loadConfiguration(ArrayList<String> boardConfig) 
    {
        char[][] config = new char[this.getRows()][this.getCols()];

        for (int i = 0; i < this.getRows(); i++) 
            config[i] = boardConfig.get(i).toCharArray();
        
        // Extract pieces from the grid
        Map<Character, ArrayList<int[]>> pieceLocations = new HashMap<>();
        boolean foundPrimary = false;

        for (int i = 0; i < this.getRows(); i++) 
        {
            for (int j = 0; j < this.getCols(); j++) 
            {
                grid[i][j] = config[i][j];

                if (grid[i][j] != '.') 
                {
                    // If the piece is not already in the map, add it
                    if (!pieceLocations.containsKey(grid[i][j]))
                    {
                        pieceLocations.put(grid[i][j], new ArrayList<int []>());
                        pieceLocations.get(grid[i][j]).add(new int[]{i, j, UNKNOWN});
                        
                        if (grid[i][j] == 'P') foundPrimary = true;
                    }
                    else
                    {
                        int[] firstLocation = pieceLocations.get(grid[i][j]).get(0);
                        int[] lastLocation = pieceLocations.get(grid[i][j]).get(pieceLocations.get(grid[i][j]).size() - 1);

                        if (pieceLocations.get(grid[i][j]).size() == 1) 
                        {
                            if (firstLocation[0] != i && firstLocation[1] != j)
                            {
                                this.errorMsg = "Found duplicate piece " + grid[i][j] + " at (" + i + ", " + j + 
                                                ") and (" + firstLocation[0] + ", " + firstLocation[1] + ")";
                                return;
                            }
                            else if (firstLocation[0] == i && firstLocation[1] == j - 1)
                            {
                                pieceLocations.get(grid[i][j]).get(0)[2] = HORIZONTAL;
                                pieceLocations.get(grid[i][j]).add(new int[]{i, j, HORIZONTAL});
                            }
                            else if (firstLocation[0] == i - 1 && firstLocation[1] == j)
                            {
                                pieceLocations.get(grid[i][j]).get(0)[2] = VERTICAL;
                                pieceLocations.get(grid[i][j]).add(new int[]{i, j, VERTICAL});
                            }
                        }
                        else
                        {
                            if (firstLocation[0] == i && lastLocation[1] == j - 1 && firstLocation[2] == HORIZONTAL)
                            {
                                pieceLocations.get(grid[i][j]).add(new int[]{i, j, HORIZONTAL});
                            }
                            else if (lastLocation[0] == i - 1 && firstLocation[1] == j && lastLocation[2] == VERTICAL)
                            {
                                pieceLocations.get(grid[i][j]).add(new int[]{i, j, VERTICAL});
                            }

                            if (grid[i][j] == 'P')
                            {
                                if (firstLocation[2] == HORIZONTAL && i != exitRow)
                                {
                                    this.errorMsg = "Primary piece is not at the exit row";
                                    return;
                                }
                                else if (firstLocation[2] == VERTICAL && j != exitCol)
                                {
                                    this.errorMsg = "Primary piece is not at the exit column";
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Process all pieces
        for (Map.Entry<Character, ArrayList<int[]>> entry : pieceLocations.entrySet()) 
        {
            char id = entry.getKey();
            ArrayList<int[]> locations = entry.getValue();
            
            boolean isPrimary = (id == 'P');
            int size = locations.size();
            
            // Determine orientation
            int orientation = (locations.get(0)[2] == HORIZONTAL) ? HORIZONTAL : VERTICAL;
            int startRow = locations.get(0)[0];
            int startCol = locations.get(0)[1];
            
            // Create the piece
            Piece piece = new Piece(id, startRow, startCol, size, isPrimary, orientation);
            this.pieces.add(piece);
        }

        if (!foundPrimary) 
        {
            this.errorMsg = "No primary piece found on the board";
            return;
        }
        
        if (pieceLocations.size() < this.getNumPieces())
        {
            this.errorMsg = "There are too few pieces. Expected " + N + " but found " + pieceLocations.size();
            return;
        }

        if (pieceLocations.size() > this.getNumPieces())
        {
            this.errorMsg = "There are too many pieces. Expected " + N + " but found " + pieceLocations.size();
            return;
        }
    }
    
    /**
     * Check if the board is in a solved state (primary piece at exit)
     * 
     * @return True if solved, false otherwise
     */
    public boolean isSolved() 
    {
        for (Piece piece : this.getPieces()) 
        {
            if (piece.isPrimary()) 
            {
                if (piece.getOrientation() == HORIZONTAL)
                    return (piece.getStartCol() + piece.getLength() == exitCol && piece.getStartRow() == exitRow);
                
                else
                    return (piece.getStartRow() + piece.getLength() == exitRow && piece.getStartCol() == exitCol);
            }
        }
        return false;
    }
    
    /**
     * Get all valid moves for a specific piece
     * 
     * @param pieceIndex Index of the piece in the pieces list
     * @return List of valid move amounts (positive for right/down, negative for left/up)
     */
    public List<Integer> getValidMoves(int pieceIndex) 
    {
        Piece piece = pieces.get(pieceIndex);
        List<Integer> validMoves = new ArrayList<>();
        
        if (piece.getOrientation() == HORIZONTAL) 
        {
            // Check moves to the left
            int leftMost = piece.getStartCol();
            int leftBound = 0;
            
            for (int col = leftMost - 1; col >= 0; col--) 
            {
                if (isOccupied(piece.getStartRow(), col)) 
                {
                    leftBound = col + 1;
                    break;
                }
            }
            
            // Add all possible left moves
            for (int col = leftMost - 1; col >= leftBound; col--) 
                validMoves.add(col - leftMost); // This will be negative (left)
            
            // Check moves to the right
            int rightMost = piece.getStartCol() + piece.getLength() - 1;
            int rightBound = this.getCols() - 1;
            
            for (int col = rightMost + 1; col < this.getCols(); col++) 
            {
                if (isOccupied(piece.getStartRow(), col)) 
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
            int topMost = piece.getStartRow();
            int topBound = 0;
            
            for (int row = topMost - 1; row >= 0; row--) 
            {
                if (isOccupied(row, piece.getStartCol())) 
                {
                    topBound = row + 1;
                    break;
                }
            }
            
            // Add all possible up moves
            for (int row = topMost - 1; row >= topBound; row--)
                validMoves.add(row - topMost); // This will be negative (up)
            
            // Check moves down
            int bottomMost = piece.getStartRow() + piece.getLength() - 1;
            int bottomBound = this.getRows() - 1;
            
            for (int row = bottomMost + 1; row < this.getRows(); row++) 
            {
                if (isOccupied(row, piece.getStartCol())) 
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
     * Check if a cell is occupied by any piece
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
     * Apply a move to a piece
     * 
     * @param pieceIndex Index of the piece in the pieces list
     * @param move Amount to move (positive for right/down, negative for left/up)
     * @return A new Board with the move applied
     */
    public Board applyMove(int pieceIndex, int move) 
    {
        Board newBoard = this.copy();
        Piece piece = newBoard.pieces.get(pieceIndex);
        
        // Clear the current piece position
        int[][] oldCells = piece.getOccupiedCells();
        for (int[] cell : oldCells)
            newBoard.grid[cell[0]][cell[1]] = '.';
        
        // Move the piece
        piece.move(move);
        
        // Update the grid with the new piece position
        int[][] newCells = piece.getOccupiedCells();
        for (int[] cell : newCells)
            newBoard.grid[cell[0]][cell[1]] = piece.getId();
        
        return newBoard;
    }
    
    /**
     * Get a string representation of the current board state
     * 
     * @return String representation of the board
     */
    @Override
    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        
        // Create a temporary grid with exit marked
        char[][] tempGrid = new char[this.getRows()][this.getCols()];
        for (int i = 0; i < this.getRows(); i++)
            System.arraycopy(grid[i], 0, tempGrid[i], 0, this.getCols());
        
        // Mark the exit
        if (exitRow >= 0 && exitRow < this.getRows() && exitCol >= 0 && exitCol < this.getCols())
            tempGrid[exitRow][exitCol] = 'K';
        
        // Convert grid to string
        for (int i = 0; i < this.getRows(); i++) 
        {
            for (int j = 0; j < this.getCols(); j++)
                sb.append(tempGrid[i][j]);

            if (i < this.getRows() - 1)
                sb.append("\n");
        }
        
        return sb.toString();
    }

    /**
     * Get a string representation of the pieces on the board
     * 
     * @return String representation of the pieces
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
     * Generates a palette of colors for the pieces.
     *
     * @return Array of ANSI color codes
     */
    public String[] generatePalette() 
    {
        String[] palette = 
        {
            "\u001B[38;2;255;50;50m",    // A: Bright Red (#FF3232)
            "\u001B[38;2;50;255;50m",    // B: Bright Green (#32FF32)
            "\u001B[38;2;50;150;255m",   // C: Bright Blue (#3296FF)
            "\u001B[38;2;255;255;50m",   // D: Bright Yellow (#FFFF32)
            "\u001B[38;2;255;50;255m",   // E: Bright Magenta (#FF32FF)
            "\u001B[38;2;50;255;255m",   // F: Bright Cyan (#32FFFF)
            "\u001B[38;2;255;128;0m",    // G: Orange (#FF8000)
            "\u001B[38;2;128;255;128m",  // H: Light Green (#80FF80)
            "\u001B[38;2;180;180;255m",  // I: Light Blue (#B4B4FF)
            "\u001B[38;2;255;200;100m",  // J: Light Orange (#FFC864)
            "\u001B[38;2;200;100;255m",  // K: Light Purple (#C864FF)
            "\u001B[38;2;100;255;200m",  // L: Light Turquoise (#64FFC8)
            "\u001B[38;2;255;150;150m",  // M: Light Red (#FF9696)
            "\u001B[38;2;150;255;150m",  // N: Pale Green (#96FF96)
            "\u001B[38;2;255;160;122m",  // O: Light Salmon (#FFA07A)
            "\u001B[38;2;255;215;0m",    // P: Gold (#FFD700)
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