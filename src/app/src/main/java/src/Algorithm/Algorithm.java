package src.Algorithm;

import src.ADT.*;
import java.util.*;

public abstract class Algorithm 
{
    protected Board initialBoard;
    protected Set<String> visitedStates;
    protected int nodesExplored;
    protected long executionTime;
    
    /**
     * Constructor for the Algorithm class
     * 
     * @param board The initial board state
     */
    public Algorithm(Board board) 
    {
        this.initialBoard = board;
        this.visitedStates = new HashSet<>();
        this.nodesExplored = 0;
        this.executionTime = 0;
    }

    public int getNodesExplored() { return nodesExplored; }
    public long getExecutionTime() { return executionTime; }
    protected void setExecutionTime(long time) { this.executionTime = time; }
    protected long startTiming() { return System.currentTimeMillis(); }
    protected void endTiming(long startTime) 
    {
        this.executionTime = System.currentTimeMillis() - startTime;
    }

    /**
     * Solve the puzzle using the appropriate algorithm
     * 
     * @param heuristic The heuristic to use (if applicable)
     * @return A list of moves to solve the puzzle
     */
    public abstract List<int[]> solve(String heuristic);
    
    /**
     * Split multi-unit moves into individual steps
     * 
     * @param moves The list of moves to split
     * @return A list of single-step moves
     */
    public static List<int[]> splitMovesToSteps(List<int[]> moves) 
    {
        List<int[]> steppedMoves = new ArrayList<>();
        
        for (int[] move : moves) 
        {
            int pieceIndex = move[0];
            int moveAmount = move[1];
            
            // Split the move into individual steps of size 1
            if (moveAmount > 0) 
            {
                // Positive movement: add moveAmount number of [pieceIndex, 1] moves
                for (int i = 0; i < moveAmount; i++)
                    steppedMoves.add(new int[]{pieceIndex, 1});
            } 
            else if (moveAmount < 0) 
            {
                // Negative movement: add |moveAmount| number of [pieceIndex, -1] moves
                for (int i = 0; i < Math.abs(moveAmount); i++)
                    steppedMoves.add(new int[]{pieceIndex, -1});
            }
            // If moveAmount is 0, we don't add anything (no movement)
        }
        
        return steppedMoves;
    }
    
    /**
     * Helper method to add a state to the visited set
     * 
     * @param board The board state to add
     */
    protected void addToVisited(Board board) 
    {
        String boardStr = State.getBoardStateString(board);
        visitedStates.add(boardStr);
    }
    
    /**
     * Helper method to check if a state has been visited
     * 
     * @param board The board state to check
     * @return true if the state has been visited, false otherwise
     */
    protected boolean hasBeenVisited(Board board) 
    {
        String boardStr = State.getBoardStateString(board);
        return visitedStates.contains(boardStr);
    }
    
    /**
     * Increment the number of nodes explored
     */
    protected void incrementNodesExplored() { nodesExplored++; }
}