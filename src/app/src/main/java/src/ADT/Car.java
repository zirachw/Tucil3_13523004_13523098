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

    public char getId() { return this.id; }
    public void setId(char id) { this.id = id; }

    public int getStartRow() { return this.startRow; }
    public void setStartRow(int row) { this.startRow = row; }

    public int getStartCol() { return this.startCol; }
    public void setStartCol(int col) { this.startCol = col; }

    public int getLength() { return this.length; }
    public void setLength(int length) { this.length = length; }

    public boolean isPrimary() { return this.isPrimary; }
    public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public int getOrientation() { return this.orientation; }
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