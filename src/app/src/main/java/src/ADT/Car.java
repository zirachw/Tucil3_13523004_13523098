package src.ADT;

/**
 * Class representing a car (vehicle) in the Rush Hour puzzle game.
 */
public class Car 
{
    private char id;           // Character identifier for the car
    private int startRow;      // Starting row position
    private int startCol;      // Starting column position
    private int length;        // Number of cells the car occupies
    private int orientation;   // Orientation: true for horizontal, false for vertical
    private boolean isPrimary; // Whether this is the primary car that needs to reach the exit

    /**
     * Constructor for creating a car
     * 
     * @param id Character identifier for the car
     * @param row Starting row position
     * @param col Starting column position
     * @param length Number of cells the car occupies
     * @param isPrimary Whether this is the primary car
     * @param orientation Orientation: true for horizontal, false for vertical
     */
    public Car(char id, 
                 int row, 
                 int col, 
                 int length, 
                 boolean isPrimary, 
                 int orientation) 
    {
        this.id          = id;
        this.startRow    = row;
        this.startCol    = col;
        this.length      = length;
        this.isPrimary   = isPrimary;
        this.orientation = orientation;
    }

    /**
     * Get the character identifier of the car
     * @return The character identifier
     */
    public char getId() { return this.id; }

    /**
     * Set the character identifier of the car
     * @param id The character identifier
     */
    public void setId(char id) { this.id = id; }

    /**
     * Get the starting row position of the car
     * @return The starting row position
     */
    public int getStartRow() { return this.startRow; }

    /**
     * Set the starting row position of the car
     * @param row The starting row position
     */
    public void setStartRow(int row) { this.startRow = row; }

    /**
     * Get the starting column position of the car
     * @return The starting column position
     */
    public int getStartCol() { return this.startCol; }

    /**
     * Set the starting column position of the car
     * @param col The starting column position
     */
    public void setStartCol(int col) { this.startCol = col; }

    /**
     * Get the length of the car
     * @return The length of the car
     */
    public int getLength() { return this.length; }

    /**
     * Set the length of the car
     * @param length The length of the car
     */
    public void setLength(int length) { this.length = length; }

    /**
     * Check if the car is primary
     * @return true if primary, false otherwise
     */
    public boolean isPrimary() { return this.isPrimary; }

    /**
     * Set if the car is primary
     * @param isPrimary true if primary, false otherwise
     */
    public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }

    /**
     * Get the orientation of the car
     * @return 1 for horizontal, 0 for vertical
     */
    public int getOrientation() { return this.orientation; }

    /**
     * Set the orientation of the car
     * @param orientation 1 for horizontal, 0 for vertical
     */
    public void setOrientation(int orientation) { this.orientation = orientation; }

    /**
     * Creates a copy of this car
     * 
     * @return A new Car object with the same attributes
     */
    public Car copy() 
    {
        return new Car(id, startRow, startCol, length, isPrimary, orientation);
    }

    /**
     * Move the car by the specified amount (positive or negative)
     * Positive means right/down, negative means left/up
     * 
     * @param amount Number of cells to move
     */
    public void move(int amount) 
    {
        if (orientation == 1) this.startCol += amount;
        else this.startRow += amount;
    }

    /**
     * Get all the cells occupied by this car
     * 
     * @return A 2D array where each element is [row, col] of an occupied cell
     */
    public int[][] getOccupiedCells() 
    {
        int[][] cells = new int[length][2];
        
        for (int i = 0; i < length; i++) 
        {
            if (orientation == 1) 
            {
                cells[i][0] = this.getStartRow();
                cells[i][1] = this.getStartCol() + i;
            } 
            else 
            {
                cells[i][0] = this.getStartRow() + i;
                cells[i][1] = this.getStartCol();
            }
        }
        return cells;
    }

    /**
     * Get the string representation of the car
     * 
     * @return A string representing the car
     */
    @Override
    public String toString() 
    {
        return "Car [id=" + this.getId() +
                ", row=" + this.getStartRow() +
                ", col=" + this.getStartCol() +
                ", length=" + this.getLength() +
                ", isPrimary=" + this.isPrimary() +
                ", orientation=" + this.getOrientation() + "]";
    }
}