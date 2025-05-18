import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing the game board for the Rush Hour puzzle.
 */
public class Board {
    // Attributes
    private int rows;           // Number of rows in the board
    private int cols;           // Number of columns in the board
    private char[][] grid;      // The game grid
    private List<Piece> pieces; // All pieces on the board
    private Piece primaryPiece; // The primary piece that needs to reach the exit
    private int exitRow;        // Exit row position
    private int exitCol;        // Exit column position
    
    /**
     * Constructor for the Board class
     * @param rows Number of rows in the board
     * @param cols Number of columns in the board
     */
    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        this.pieces = new ArrayList<>();
        
        // Initialize the grid with empty cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }
    }
    
    /**
     * Creates a deep copy of the current board state
     * 
     * @return A new Board object with the same state
     */
    public Board copy() {
        Board newBoard = new Board(rows, cols);
        
        // Copy the grid
        for (int i = 0; i < rows; i++) {
            System.arraycopy(grid[i], 0, newBoard.grid[i], 0, cols);
        }
        
        // Copy the pieces
        for (Piece piece : pieces) {
            Piece newPiece = piece.copy();
            newBoard.pieces.add(newPiece);
            
            if (piece.isPrimary()) {
                newBoard.primaryPiece = newPiece;
            }
        }
        
        newBoard.exitRow = exitRow;
        newBoard.exitCol = exitCol;
        
        return newBoard;
    }
    
    /**
     * Load a board configuration from a 2D character array
     * 
     * @param config The board configuration
     * @return True if successful, false otherwise
     */
    public boolean loadConfiguration(char[][] config) {
        // Check dimensions
        if (config.length != rows || config[0].length != cols) {
            return false;
        }
        
        // Clear existing pieces
        pieces.clear();
        primaryPiece = null;
        
        // Copy the configuration
        for (int i = 0; i < rows; i++) {
            System.arraycopy(config[i], 0, grid[i], 0, cols);
        }
        
        // Find exit location (K)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 'K') {
                    exitRow = i;
                    exitCol = j;
                    grid[i][j] = '.'; // Remove K from the grid
                }
            }
        }
        
        // Extract pieces from the grid
        Map<Character, List<int[]>> pieceLocations = new HashMap<>();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = grid[i][j];
                if (cell != '.') {
                    pieceLocations.putIfAbsent(cell, new ArrayList<>());
                    pieceLocations.get(cell).add(new int[]{i, j});
                }
            }
        }
        
        // Process all pieces
        for (Map.Entry<Character, List<int[]>> entry : pieceLocations.entrySet()) {
            char id = entry.getKey();
            List<int[]> locations = entry.getValue();
            
            boolean isPrimary = id == 'P';
            int size = locations.size();
            
            // Determine orientation
            boolean isHorizontal = true;
            int firstRow = locations.get(0)[0];
            for (int i = 1; i < locations.size(); i++) {
                if (locations.get(i)[0] != firstRow) {
                    isHorizontal = false;
                    break;
                }
            }
            
            // Find the starting position (top-left)
            int startRow = Integer.MAX_VALUE;
            int startCol = Integer.MAX_VALUE;
            for (int[] loc : locations) {
                if (isHorizontal) {
                    startRow = loc[0];
                    startCol = Math.min(startCol, loc[1]);
                } else {
                    startRow = Math.min(startRow, loc[0]);
                    startCol = loc[1];
                }
            }
            
            // Create the piece
            Piece piece = new Piece(id, startRow, startCol, size, isPrimary, isHorizontal);
            pieces.add(piece);
            
            if (isPrimary) {
                primaryPiece = piece;
            }
        }
        
        return true;
    }
    
    /**
     * Check if the board is in a solved state (primary piece at exit)
     * 
     * @return True if solved, false otherwise
     */
    public boolean isSolved() {
        if (primaryPiece == null) {
            return false;
        }
        
        // For horizontal primary piece
        if (primaryPiece.isHorizontal()) {
            // Check if the primary piece's right edge is at the exit
            int rightEdge = primaryPiece.getCol() + primaryPiece.getSize() - 1;
            
            // The piece is solved if:
            // 1. Its right edge is at the exit column, and they're in the same row
            return primaryPiece.getRow() == exitRow && rightEdge + 1 == exitCol;
        } else {
            // For vertical primary piece
            int bottomEdge = primaryPiece.getRow() + primaryPiece.getSize() - 1;
            
            // The piece is solved if:
            // 1. Its bottom edge is at the exit row, and they're in the same column
            return primaryPiece.getCol() == exitCol && bottomEdge + 1 == exitRow;
        }
    }
    
    /**
     * Get all valid moves for a specific piece
     * 
     * @param pieceIndex Index of the piece in the pieces list
     * @return List of valid move amounts (positive for right/down, negative for left/up)
     */
    public List<Integer> getValidMoves(int pieceIndex) {
        Piece piece = pieces.get(pieceIndex);
        List<Integer> validMoves = new ArrayList<>();
        
        if (piece.isHorizontal()) {
            // Check moves to the left
            int leftMost = piece.getCol();
            int leftBound = 0;
            
            for (int col = leftMost - 1; col >= 0; col--) {
                if (isOccupied(piece.getRow(), col)) {
                    leftBound = col + 1;
                    break;
                }
            }
            
            // Add all possible left moves
            for (int col = leftMost - 1; col >= leftBound; col--) {
                validMoves.add(col - leftMost); // This will be negative (left)
            }
            
            // Check moves to the right
            int rightMost = piece.getCol() + piece.getSize() - 1;
            int rightBound = cols - 1;
            
            for (int col = rightMost + 1; col < cols; col++) {
                if (isOccupied(piece.getRow(), col)) {
                    rightBound = col - 1;
                    break;
                }
            }
            
            // Add all possible right moves
            for (int delta = 1; delta <= rightBound - rightMost; delta++) {
                validMoves.add(delta); // This will be positive (right)
            }
        } else {
            // Check moves up
            int topMost = piece.getRow();
            int topBound = 0;
            
            for (int row = topMost - 1; row >= 0; row--) {
                if (isOccupied(row, piece.getCol())) {
                    topBound = row + 1;
                    break;
                }
            }
            
            // Add all possible up moves
            for (int row = topMost - 1; row >= topBound; row--) {
                validMoves.add(row - topMost); // This will be negative (up)
            }
            
            // Check moves down
            int bottomMost = piece.getRow() + piece.getSize() - 1;
            int bottomBound = rows - 1;
            
            for (int row = bottomMost + 1; row < rows; row++) {
                if (isOccupied(row, piece.getCol())) {
                    bottomBound = row - 1;
                    break;
                }
            }
            
            // Add all possible down moves
            for (int delta = 1; delta <= bottomBound - bottomMost; delta++) {
                validMoves.add(delta); // This will be positive (down)
            }
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
    private boolean isOccupied(int row, int col) {
        // Check if cell is within bounds
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return true; // Treat out-of-bounds as occupied
        }
        return grid[row][col] != '.';
    }
    
    /**
     * Apply a move to a piece
     * 
     * @param pieceIndex Index of the piece in the pieces list
     * @param move Amount to move (positive for right/down, negative for left/up)
     * @return A new Board with the move applied
     */
    public Board applyMove(int pieceIndex, int move) {
        Board newBoard = this.copy();
        Piece piece = newBoard.pieces.get(pieceIndex);
        
        // Clear the current piece position
        int[][] oldCells = piece.getOccupiedCells();
        for (int[] cell : oldCells) {
            newBoard.grid[cell[0]][cell[1]] = '.';
        }
        
        // Move the piece
        piece.move(move);
        
        // Update the grid with the new piece position
        int[][] newCells = piece.getOccupiedCells();
        for (int[] cell : newCells) {
            newBoard.grid[cell[0]][cell[1]] = piece.getId();
        }
        
        return newBoard;
    }
    
    /**
     * Get a string representation of the current board state
     * 
     * @return String representation of the board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Create a temporary grid with exit marked
        char[][] tempGrid = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(grid[i], 0, tempGrid[i], 0, cols);
        }
        
        // Mark the exit
        if (exitRow >= 0 && exitRow < rows && exitCol >= 0 && exitCol < cols) {
            tempGrid[exitRow][exitCol] = 'K';
        }
        
        // Convert grid to string
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(tempGrid[i][j]);
            }
            if (i < rows - 1) {
                sb.append("\n");
            }
        }
        
        return sb.toString();
    }
    
    // Getters and setters
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
    
    public char[][] getGrid() {
        return grid;
    }
    
    public List<Piece> getPieces() {
        return pieces;
    }
    
    public Piece getPrimaryPiece() {
        return primaryPiece;
    }
    
    public int getExitRow() {
        return exitRow;
    }
    
    public int getExitCol() {
        return exitCol;
    }
    
    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sb.append(grid[i][j]);
            }
        }
        return sb.toString().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Board other = (Board) obj;
        if (rows != other.rows || cols != other.cols) return false;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != other.grid[i][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
}