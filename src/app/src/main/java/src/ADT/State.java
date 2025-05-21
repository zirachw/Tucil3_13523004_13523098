package src.ADT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class State implements Comparable<State> 
{
    public static final int INVALID = 69420;
    private Board board;
    private List<int[]> moves; // History of moves: [carIndex, moveAmount, pieceId]
    private int gValue;        // Cost so far: for UCS and A*
    private int hValue;        // Heuristic value: for A* and Greedy
    private int fValue;        // For A*: f(n) = g(n) + h(n)
    
    /** 
     * Constructor for UCS
     * 
     * @param board The current board state
     * @param moves The list of moves made to reach this state
     */
    public State(Board board, List<int[]> moves) 
    {
        this.board = board;
        this.moves = new ArrayList<>(moves);
        calculateGValue();
    }

    /** 
     * Constructor for Greedy and A*
     * 
     * @param board The current board state
     * @param moves The list of moves made to reach this state
     * @param hValue The heuristic value for this state
     */
    public State(Board board, List<int[]> moves, int hValue) 
    { 
        this.board = board;
        this.moves = new ArrayList<>(moves);
        calculateGValue();
        this.hValue = hValue;
        calculateFValue();
    }
    
    public Board getBoard() { return board; }
    public List<int[]> getMoves() { return moves; }
    public int getGValue() { return gValue; }
    public int getHValue() { return hValue; }
    public int getFValue() { return fValue; }

    /** 
     * Convert state to String for visitedStates
     * 
     * @param board The current board state
     * @return A string representation of the board state
     */
    public static String getBoardStateString(Board board) 
    {
        StringBuilder sb = new StringBuilder();
        for (Car car : board.getCars()) 
        {
            sb.append(car.getId()).append(car.getStartRow()).append(car.getStartCol());
        }
        return sb.toString();
    }

    /** 
     * Calculate the g(n) value for UCS
     */
    public void calculateGValue() { this.gValue = this.moves.size(); }

    /** 
     * Calculate the f(n) = g(n) + h(n) value for A*
     */
    public void calculateFValue() { this.fValue = getGValue() + getHValue(); }

    /**
     * Calculate the heuristic value for the blocking car heuristic
     * 
     * @param board
     * @return The heuristic value for the blocking car heuristic
     */
    public static int calculateBlockingCarHeuristic(Board board) 
    {
        Car primaryCar = board.getPrimaryCar();
        
        if (primaryCar == null) return INVALID;
        
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        int blockingCars = 0;
        
        if (primaryCar.getOrientation() == 1) 
        {
            int leftEdge = primaryCar.getStartCol();
            int rightEdge = primaryCar.getStartCol() + primaryCar.getLength() - 1;
            
            if (exitCol == board.getCols()) 
            {   
                Set<Character> blockingPieceIds = new HashSet<>();
                for (int col = rightEdge + 1; col < exitCol; col++) 
                {
                    if (col >= 0 && 
                        col < board.getCols() && 
                        primaryCar.getStartRow() >= 0 && 
                        primaryCar.getStartRow() < board.getRows()) 
                    {
                        char cell = board.getGrid()[primaryCar.getStartRow()][col];
                        if (cell != '.') blockingPieceIds.add(cell);
                    }
                }
                blockingCars = blockingPieceIds.size();
            } 
            else if (exitCol == 0) 
            {   
                Set<Character> blockingPieceIds = new HashSet<>();
                for (int col = leftEdge - 1; col > exitCol; col--) 
                {
                    if (col >= 0 && 
                        col < board.getCols() && 
                        primaryCar.getStartRow() >= 0 && 
                        primaryCar.getStartRow() < board.getRows()) 
                    {
                        char cell = board.getGrid()[primaryCar.getStartRow()][col];
                        if (cell != '.') blockingPieceIds.add(cell);
                    }
                }
                blockingCars = blockingPieceIds.size();
            }
        } 
        else 
        {
            int topEdge = primaryCar.getStartRow();
            int bottomEdge = primaryCar.getStartRow() + primaryCar.getLength() - 1;
            
            if (exitRow == board.getRows()) 
            {   
                Set<Character> blockingPieceIds = new HashSet<>();
                for (int row = bottomEdge + 1; row < exitRow; row++) 
                {
                    if (row >= 0 && row < board.getRows() && 
                        primaryCar.getStartCol() >= 0 && 
                        primaryCar.getStartCol() < board.getCols()) {
                        char cell = board.getGrid()[row][primaryCar.getStartCol()];
                        if (cell != '.') blockingPieceIds.add(cell);
                    }
                }
                blockingCars = blockingPieceIds.size();
            } 
            else if (exitRow == 0) 
            {   
                Set<Character> blockingPieceIds = new HashSet<>();
                for (int row = topEdge - 1; row > exitRow; row--) 
                {
                    if (row >= 0 && row < board.getRows() && 
                        primaryCar.getStartCol() >= 0 && 
                        primaryCar.getStartCol() < board.getCols()) {
                        char cell = board.getGrid()[row][primaryCar.getStartCol()];
                        if (cell != '.') blockingPieceIds.add(cell);
                    }
                }
                blockingCars = blockingPieceIds.size();
            }
        }
        
        return blockingCars;
    }
    
    /**
     * Calculate the heuristic value for the Manhattan distance heuristic
     * 
     * @param board
     * @return The heuristic value for the Manhattan distance heuristic
     */
    public static int calculateManhattanDistanceHeuristic(Board board) 
    {
        Car primaryCar = board.getPrimaryCar();
        
        if (primaryCar == null) return INVALID;
        
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        if (primaryCar.getOrientation() == 1) 
        {
            int leftEdge = primaryCar.getStartCol();
            int rightEdge = primaryCar.getStartCol() + primaryCar.getLength() - 1;
            
            if (exitCol == board.getCols()) 
                return Math.max(0, exitCol - rightEdge - 1);

            else if (exitCol == -1) 
                return Math.max(0, leftEdge - (exitCol + 1));
        } 
        else 
        {
            int topEdge = primaryCar.getStartRow();
            int bottomEdge = primaryCar.getStartRow() + primaryCar.getLength() - 1;
            
            if (exitRow == board.getRows())
                return Math.max(0, exitRow - bottomEdge - 1);

            else if (exitRow == -1)
                return Math.max(0, topEdge - (exitRow + 1));
        }
        
        return 0;
    }
    
    @Override
    public int compareTo(State other) { return Integer.compare(this.fValue, other.fValue); }
    
    @Override
    public int hashCode() { return Objects.hash(board); }

    @Override
    public boolean equals(Object o) 
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        State state = (State) o;
        return Objects.equals(board, state.board);
    }
    
    /**
     * Add a move to the history
     * 
     * @param carIndex The index of the car moved
     * @param moveAmount The amount moved (positive or negative)
     * @param newBoard The new board state after the move
     * @param newHValue The heuristic value for the new state
     * @return A new State object with the updated board and moves
     */
    public State addMove(int carIndex, int moveAmount, Board newBoard, int newHValue) 
    {
        List<int[]> newMoves = new ArrayList<>(moves);
        char carId = board.getCars().get(carIndex).getId();
        newMoves.add(new int[]{carIndex, moveAmount, (int)carId});
        return new State(newBoard, newMoves, newHValue);
    }
        
    /** 
     * Get a string representation of the moves made
     * 
     * @return A string describing the moves made
     */
    public String getMovesAsString() 
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moves.size(); i++) 
        {
            int[] move = moves.get(i);
            int carIndex = move[0];
            int moveAmount = move[1];
            char pieceId = (char)move[2];
            
            String direction = "";
            if (moveAmount > 0) 
                direction = " right/down " + moveAmount + " steps";
            
            else
                direction = " left/up " + Math.abs(moveAmount) + " steps";
            
            sb.append("Move ")
              .append(i + 1)
              .append(": Piece '")
              .append(pieceId)
              .append("' (index ")
              .append(carIndex)
              .append(")")
              .append(direction);
            
            if (i < moves.size() - 1) sb.append("\n");
        }

        return sb.toString();
    }
    
    /** 
     * Get the last moved piece ID
     * 
     * @return The ID of the last moved piece, or '\0' if no moves were made
     */
    public char getLastMovedPieceId() 
    {
        if (moves.isEmpty())
            return '\0'; 

        int[] lastMove = moves.get(moves.size() - 1);
        return (char)lastMove[2];
    }
    
    /** 
     * Get the last move description
     * 
     * @return A string describing the last move made
     */
    public String getLastMoveDescription() 
    {
        if (moves.isEmpty()) return "No moves made";
        
        int[] lastMove = moves.get(moves.size() - 1);
        int moveAmount = lastMove[1];
        char pieceId = (char)lastMove[2];
        
        String direction = moveAmount > 0 ? "right/down" : "left/up";
        return "Piece '" + pieceId + "' moved " + direction + " " + Math.abs(moveAmount) + " steps";
    }
}