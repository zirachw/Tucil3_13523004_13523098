package src.Algorithm;

import src.ADT.*;
import java.util.*;

public class AStar extends Algorithm 
{
    public AStar(Board board) 
    {
        super(board);
    }

    @Override
    public List<int[]> solve(String heuristic) 
    {
        if (heuristic.equals("Blocking")) return solveBlockingCars();
        else if (heuristic.equals("Manhattan")) return solveManhattanDistance();
        else throw new IllegalArgumentException("Invalid heuristic: " + heuristic);
    }

    private List<int[]> solveBlockingCars() 
    {
        long startTime = startTiming();
        
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getFValue()));
        List<int[]> initialMoves = new ArrayList<>();
        
        int intialHValue = State.calculateBlockingCarHeuristic(initialBoard);

        addToVisited(initialBoard);
        queue.add(new State(initialBoard, initialMoves, intialHValue));
        
        while (!queue.isEmpty())
        {
            State curState = queue.poll();
            Board curBoard = curState.getBoard();
            List<Car> pieces = curBoard.getCars();
            List<int[]> curMoves = curState.getMoves();
            incrementNodesExplored();

            if (curBoard.isSolved())
            {
                endTiming(startTime);
                return curMoves;
            }
            
            // Set maximum cost limit
            int maxCost = curBoard.getRows() * curBoard.getCols() * 50;
            if (curMoves.size() > maxCost) continue;

            for (int i = 0; i < pieces.size(); i++) 
            {
                List<Integer> validMoves = curBoard.getValidMoves(i);
                for (Integer moveAmount : validMoves)
                {
                    Board newBoard = curBoard.copy();
                    newBoard = newBoard.applyMove(i, moveAmount);
                    
                    if (!hasBeenVisited(newBoard))
                    {
                        addToVisited(newBoard);
                        
                        List<int[]> newMoves = new ArrayList<>(curMoves);
                        newMoves.add(new int[]{i, moveAmount});
                        
                        int hValue = State.calculateBlockingCarHeuristic(newBoard);
                        queue.add(new State(newBoard, newMoves, hValue));
                    }
                }
            }
        }
        
        endTiming(startTime);
        return new ArrayList<>();
    }

    private List<int[]> solveManhattanDistance() 
    {
        long startTime = startTiming();
        
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getFValue()));
        List<int[]> initialMoves = new ArrayList<>();
        
        int initialHValue = State.calculateManhattanDistanceHeuristic(initialBoard);

        addToVisited(initialBoard);
        queue.add(new State(initialBoard, initialMoves, initialHValue));
        
        while (!queue.isEmpty())
        {
            State curState = queue.poll();
            Board curBoard = curState.getBoard();
            List<Car> pieces = curBoard.getCars();
            List<int[]> curMoves = curState.getMoves();

            incrementNodesExplored();
            
            if (curBoard.isSolved())
            {
                endTiming(startTime);
                return curMoves;
            }

            int maxCost = curBoard.getRows() * curBoard.getCols() * 50;
            if (curMoves.size() > maxCost) continue;
            
            for (int i = 0; i < pieces.size(); i++)
            {
                List<Integer> validMoves = curBoard.getValidMoves(i);
                for (Integer moveAmount : validMoves)
                {
                    Board newBoard = curBoard.copy();
                    newBoard = newBoard.applyMove(i, moveAmount);
                    
                    // Check if state has been visited BEFORE adding to queue
                    if (!hasBeenVisited(newBoard))
                    {
                        addToVisited(newBoard);
                        
                        List<int[]> newMoves = new ArrayList<>(curMoves);
                        newMoves.add(new int[]{i, moveAmount});
                        
                        int hValue = State.calculateManhattanDistanceHeuristic(newBoard);
                        queue.add(new State(newBoard, newMoves, hValue));
                    }
                }
            }
        }
        
        endTiming(startTime);
        return new ArrayList<>();
    }
}