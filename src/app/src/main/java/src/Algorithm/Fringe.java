package src.Algorithm;

import src.ADT.*;
import java.util.*;

/**
 * Implementation of Fringe Search algorithm for the Rush Hour puzzle.
 * Fringe Search is a memory-efficient alternative to A* that uses a linked list 
 * instead of a priority queue, reducing memory overhead while maintaining 
 * comparable performance.
 */
public class Fringe extends Algorithm 
{
    public Fringe(Board board) 
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
        
        List<State> fringe = new ArrayList<>();
        List<int[]> initialMoves = new ArrayList<>();
        
        int initialHValue = State.calculateBlockingCarHeuristic(initialBoard);
        int fLimit = initialHValue; 

        addToVisited(initialBoard);
        fringe.add(new State(initialBoard, initialMoves, initialHValue));
        
        while (!fringe.isEmpty())
        {
            Collections.sort(fringe, Comparator.comparingInt(s -> s.getFValue()));
            
            int nextFLimit = Integer.MAX_VALUE;
            List<State> nextFringe = new ArrayList<>();
            
            for (State curState : fringe) {
                Board curBoard = curState.getBoard();
                List<Car> pieces = curBoard.getCars();
                List<int[]> curMoves = curState.getMoves();
                int curFValue = curState.getFValue();
                
                if (curFValue > fLimit) {
                    nextFLimit = Math.min(nextFLimit, curFValue);
                    nextFringe.add(curState);
                    continue;
                }
                
                incrementNodesExplored();

                if (curBoard.isSolved())
                {
                    endTiming(startTime);

                    // if (curMoves.size() < 100) return splitMovesToSteps(curMoves);
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
                        
                        if (!hasBeenVisited(newBoard))
                        {
                            addToVisited(newBoard);
                            
                            List<int[]> newMoves = new ArrayList<>(curMoves);
                            newMoves.add(new int[]{i, moveAmount});
                            
                            int hValue = State.calculateBlockingCarHeuristic(newBoard);
                            int fValue = newMoves.size() + hValue;
                            
                            State newState = new State(newBoard, newMoves, hValue);
                            
                            if (fValue <= fLimit) {
                                nextFringe.add(newState);
                            } else {
                                nextFLimit = Math.min(nextFLimit, fValue);
                                nextFringe.add(newState);
                            }
                        }
                    }
                }
            }
            
            if (nextFLimit == Integer.MAX_VALUE && nextFringe.isEmpty()) {
                endTiming(startTime);
                return new ArrayList<>();
            }
            
            fLimit = nextFLimit;
            fringe = nextFringe;
        }
        
        endTiming(startTime);
        return new ArrayList<>();
    }

    private List<int[]> solveManhattanDistance() 
    {
        long startTime = startTiming();
        
        List<State> fringe = new ArrayList<>();
        List<int[]> initialMoves = new ArrayList<>();
        
        int initialHValue = State.calculateManhattanDistanceHeuristic(initialBoard);
        int fLimit = initialHValue;

        addToVisited(initialBoard);
        fringe.add(new State(initialBoard, initialMoves, initialHValue));
        
        while (!fringe.isEmpty())
        {
            Collections.sort(fringe, Comparator.comparingInt(s -> s.getFValue()));
            
            int nextFLimit = Integer.MAX_VALUE;
            List<State> nextFringe = new ArrayList<>();
            
            for (State curState : fringe) {
                Board curBoard = curState.getBoard();
                List<Car> pieces = curBoard.getCars();
                List<int[]> curMoves = curState.getMoves();
                int curFValue = curState.getFValue();
                
                if (curFValue > fLimit) {
                    nextFLimit = Math.min(nextFLimit, curFValue);
                    nextFringe.add(curState);
                    continue;
                }
                
                incrementNodesExplored();

                if (curBoard.isSolved())
                {
                    endTiming(startTime);

                    // if (curMoves.size() < 100) return splitMovesToSteps(curMoves);
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
                        
                        if (!hasBeenVisited(newBoard))
                        {
                            addToVisited(newBoard);
                            
                            List<int[]> newMoves = new ArrayList<>(curMoves);
                            newMoves.add(new int[]{i, moveAmount});
                            
                            int hValue = State.calculateManhattanDistanceHeuristic(newBoard);
                            int fValue = newMoves.size() + hValue;
                            
                            State newState = new State(newBoard, newMoves, hValue);
                            
                            if (fValue <= fLimit) {
                                nextFringe.add(newState);
                            } else {
                                nextFLimit = Math.min(nextFLimit, fValue);
                                nextFringe.add(newState);
                            }
                        }
                    }
                }
            }
            
            if (nextFLimit == Integer.MAX_VALUE && nextFringe.isEmpty()) {
                endTiming(startTime);
                return new ArrayList<>();
            }
            
            fLimit = nextFLimit;
            fringe = nextFringe;
        }
        
        endTiming(startTime);
        return new ArrayList<>();
    }
}