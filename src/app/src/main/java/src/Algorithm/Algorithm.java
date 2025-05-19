package src.Algorithm;

import src.ADT.*;
import java.util.*;

public abstract class Algorithm 
{
    protected Board initialBoard;
    protected Set<String> visitedStates;
    protected int nodesExplored;
    
    public Algorithm(Board board) 
    {
        this.initialBoard = board;
        this.visitedStates = new HashSet<>();
        this.nodesExplored = 0;
    }

    /**
     * Solve the puzzle using the appropriate algorithm
     * @param heuristic The heuristic to use (if applicable)
     * @return A list of moves to solve the puzzle
     */
    public abstract List<int[]> solve(String heuristic);
    
    /**
     * Display the solution step by step (one move at a time)
     * This shows each individual step even for consecutive moves of the same car
     * @param moves The list of moves that solve the puzzle
     */
    public void displayPerMove(List<int[]> moves) 
    {
        if(moves.isEmpty()) {
            System.out.println("No solution found.");
            return;
        }

        System.out.println("Solution found with " + nodesExplored + " nodes explored:");
        System.out.println("Initial board state:");
        Board firstBoard = initialBoard;
        System.out.println(firstBoard.toString());
        System.out.println("Result:");
        Board resultBoard = initialBoard;
        
        int counter = 1;

        for(int[] move : moves) {
            int pieceIndex = move[0];
            int moveAmount = move[1];
            
            resultBoard = resultBoard.applyMove(pieceIndex, moveAmount);
            resultBoard.setCurrentMovedCarIndex(pieceIndex);

            int orientation = resultBoard.getCars().get(pieceIndex).getOrientation();
            String direction;

            if (orientation == 1 && moveAmount > 0) direction = "Right";
            else if (orientation == 1 && moveAmount < 0) direction = "Left";
            else if (orientation == 0 && moveAmount > 0) direction = "Down";
            else direction = "Up";

            System.out.println("Move " + counter + ": " + resultBoard.getCars().get(pieceIndex).getId() + " - " + direction + " (" + Math.abs(moveAmount) + " steps)");
            System.out.println(resultBoard);
            System.out.println();
            
            counter++;
        }
    }
    
    /**
     * Display the solution by combined states (consecutive moves of the same car are combined)
     * More suitable for CLI and file output
     * @param moves The list of moves that solve the puzzle
     */
    public void displayPerState(List<int[]> moves) 
    {
        if(moves.isEmpty()) {
            System.out.println("No solution found.");
            return;
        }

        System.out.println("Solution found with " + nodesExplored + " nodes explored:");
        System.out.println("Initial board state:");
        Board firstBoard = initialBoard;
        System.out.println(firstBoard.toString());
        System.out.println();
        System.out.println("Result:");
        Board resultBoard = initialBoard;
        
        // Group consecutive moves of the same car
        List<int[]> combinedMoves = combineConsecutiveMoves(moves);

        int counter = 1;
        
        for(int[] move : combinedMoves) {
            int pieceIndex = move[0];
            int moveAmount = move[1];
            
            // Apply the combined move
            resultBoard = applyMoveAmount(resultBoard, pieceIndex, moveAmount);
            resultBoard.setCurrentMovedCarIndex(pieceIndex);

            int orientation = resultBoard.getCars().get(pieceIndex).getOrientation();
            String direction;

            if (orientation == 1 && moveAmount > 0) direction = "Right";
            else if (orientation == 1 && moveAmount < 0) direction = "Left";
            else if (orientation == 0 && moveAmount > 0) direction = "Down";
            else direction = "Up";

            System.out.println("Move " + counter + ": " + resultBoard.getCars().get(pieceIndex).getId() + " - " + direction + " (" + Math.abs(moveAmount) + " steps)");
            System.out.println(resultBoard);
            System.out.println();

            counter++;
        }
    }
    
    /**
     * Display solutions (default method that uses displayPerMove)
     * Kept for backward compatibility
     * @param moves The list of moves that solve the puzzle
     */
    public void displaySolutions(List<int[]> moves) 
    {
        displayPerMove(moves);
    }
    
    /**
     * Apply a move of a specific amount (possibly multiple steps)
     * @param board The board to apply the move to
     * @param pieceIndex The index of the piece to move
     * @param moveAmount The amount to move
     * @return The new board state after the move
     */
    private Board applyMoveAmount(Board board, int pieceIndex, int moveAmount) 
    {
        Board newBoard = board.copy();
        Car car = newBoard.getCars().get(pieceIndex);
        
        // Clear the current car position from the grid
        int[][] oldCells = car.getOccupiedCells();
        for (int[] cell : oldCells) {
            newBoard.getGrid()[cell[0]][cell[1]] = '.';
        }
        
        // Apply the move in one step
        car.move(moveAmount);
        
        // Update the grid with the new car position
        int[][] newCells = car.getOccupiedCells();
        for (int[] cell : newCells) {
            newBoard.getGrid()[cell[0]][cell[1]] = car.getId();
        }
        
        return newBoard;
    }
    
    /**
     * Combine consecutive moves of the same car into single moves
     * Only combines directly consecutive moves (not separated by other car moves)
     * @param moves The original list of moves
     * @return A list with consecutive moves of the same car combined
     */
    private List<int[]> combineConsecutiveMoves(List<int[]> moves) 
    {
        if (moves.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<int[]> combinedMoves = new ArrayList<>();
        int currentPieceIndex = moves.get(0)[0];
        int currentAmount = moves.get(0)[1];
        
        for (int i = 1; i < moves.size(); i++) {
            int[] move = moves.get(i);
            int pieceIndex = move[0];
            int moveAmount = move[1];
            
            if (pieceIndex == currentPieceIndex) {
                // Same car in a directly consecutive move, combine them
                currentAmount += moveAmount;
            } else {
                // Different car, add the previous combined move and start a new one
                if (currentAmount != 0) { // Only add non-zero moves
                    combinedMoves.add(new int[]{currentPieceIndex, currentAmount});
                }
                currentPieceIndex = pieceIndex;
                currentAmount = moveAmount;
            }
        }
        
        // Add the last combined move
        if (currentAmount != 0) { // Only add non-zero moves
            combinedMoves.add(new int[]{currentPieceIndex, currentAmount});
        }
        
        return combinedMoves;
    }

    /**
     * Split multi-unit moves into individual steps
     * @param moves The list of moves to split
     * @return A list of single-step moves
     */
    public static List<int[]> splitMovesToSteps(List<int[]> moves) 
    {
        List<int[]> steppedMoves = new ArrayList<>();
        
        for (int[] move : moves) {
            int pieceIndex = move[0];
            int moveAmount = move[1];
            
            // Split the move into individual steps of size 1
            if (moveAmount > 0) {
                // Positive movement: add moveAmount number of [pieceIndex, 1] moves
                for (int i = 0; i < moveAmount; i++) {
                    steppedMoves.add(new int[]{pieceIndex, 1});
                }
            } else if (moveAmount < 0) {
                // Negative movement: add |moveAmount| number of [pieceIndex, -1] moves
                for (int i = 0; i < Math.abs(moveAmount); i++) {
                    steppedMoves.add(new int[]{pieceIndex, -1});
                }
            }
            // If moveAmount is 0, we don't add anything (no movement)
        }
        
        return steppedMoves;
    }
    
    /**
     * Helper method to add a state to the visited set
     * @param board The board state to add
     */
    protected void addToVisited(Board board) 
    {
        String boardStr = State.getBoardStateString(board);
        visitedStates.add(boardStr);
    }
    
    /**
     * Helper method to check if a state has been visited
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
    protected void incrementNodesExplored() 
    {
        nodesExplored++;
    }
}