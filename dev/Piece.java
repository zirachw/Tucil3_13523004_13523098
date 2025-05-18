/**
 * Class representing a piece (vehicle) in the Rush Hour puzzle game.
 */
public class Piece {
    // Attributes
    private char id;           // Character identifier for the piece
    private int row;           // Starting row position
    private int col;           // Starting column position
    private int size;          // Number of cells the piece occupies
    private boolean isPrimary; // Whether this is the primary piece that needs to reach the exit
    private boolean isHorizontal; // Orientation: true for horizontal, false for vertical

    /**
     * Constructor for creating a piece
     * 
     * @param id Character identifier for the piece
     * @param row Starting row position
     * @param col Starting column position
     * @param size Number of cells the piece occupies
     * @param isPrimary Whether this is the primary piece
     * @param isHorizontal Orientation: true for horizontal, false for vertical
     */
    public Piece(char id, int row, int col, int size, boolean isPrimary, boolean isHorizontal) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.size = size;
        this.isPrimary = isPrimary;
        this.isHorizontal = isHorizontal;
    }

    /**
     * Creates a copy of this piece
     * 
     * @return A new Piece object with the same attributes
     */
    public Piece copy() {
        return new Piece(id, row, col, size, isPrimary, isHorizontal);
    }

    /**
     * Move the piece by the specified amount (positive or negative)
     * Positive means right/down, negative means left/up
     * 
     * @param amount Number of cells to move
     */
    public void move(int amount) {
        if (isHorizontal) {
            col += amount;
        } else {
            row += amount;
        }
    }

    /**
     * Get all the cells occupied by this piece
     * 
     * @return A 2D array where each element is [row, col] of an occupied cell
     */
    public int[][] getOccupiedCells() {
        int[][] cells = new int[size][2];
        
        for (int i = 0; i < size; i++) {
            if (isHorizontal) {
                cells[i][0] = row;
                cells[i][1] = col + i;
            } else {
                cells[i][0] = row + i;
                cells[i][1] = col;
            }
        }
        
        return cells;
    }

    // Getters and setters
    public char getId() {
        return id;
    }

    public void setId(char id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }

    @Override
    public String toString() {
        return "Piece [id=" + id +
                ", row=" + row +
                ", col=" + col +
                ", size=" + size +
                ", isPrimary=" + isPrimary +
                ", isHorizontal=" + isHorizontal + "]";
    }
}