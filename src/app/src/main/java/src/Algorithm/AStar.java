package src.Algorithm;

import src.ADT.*;
import java.util.*;

public class AStar {
    private Board initialBoard;
    private Set<String> visitedStates;
    private int nodesExplored;
    
    public AStar(Board board) 
    {
        this.initialBoard = board;
        this.visitedStates = new HashSet<>();
        this.nodesExplored = 0;
    }

    public List<int[]> solve(String heuristic)
    {
        if(heuristic.equals("Blocking")) return solveBlockingCars();
        else if(heuristic.equals("Manhattan")) return solveManhattanDistance();
        else throw new IllegalArgumentException("Invalid heuristic: " + heuristic);
    }

    private List<int[]> solveBlockingCars() 
    {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getFValue()));
        List<int[]> initialMoves = new ArrayList<>();
        
        int intialHValue = State.calculateBlockingCarHeuristic(initialBoard);

        String initialBoardStr = State.getBoardStateString(initialBoard);
        visitedStates.add(initialBoardStr);

        queue.add(new State(initialBoard, initialMoves, intialHValue));
        
        while(!queue.isEmpty()){
            State curState = queue.poll();
            Board curBoard = curState.getBoard();
            List<int[]> curMoves = curState.getMoves();

            nodesExplored++;

            if(curBoard.isSolved()){
                return curMoves;
            }

            // Set maximum cost limit
            int maxCost = curBoard.getRows() * curBoard.getCols() * 50;
            if(curMoves.size() > maxCost){
                continue;
            }
            
            List<Car> pieces = curBoard.getCars();
            for(int i = 0; i < pieces.size(); i++)
            {
                List<Integer> validMoves = curBoard.getValidMoves(i);
                for(Integer moveAmount : validMoves){
                    Board newBoard = curBoard.copy();
                    newBoard = newBoard.applyMove(i, moveAmount);
                    String newBoardStr = State.getBoardStateString(newBoard);
                    
                    if(!visitedStates.contains(newBoardStr)){
                        visitedStates.add(newBoardStr);
                        
                        List<int[]> newMoves = new ArrayList<>(curMoves);
                        newMoves.add(new int[]{i, moveAmount});
                        
                        int hValue = State.calculateBlockingCarHeuristic(newBoard);
                        queue.add(new State(newBoard, newMoves, hValue));
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private List<int[]> solveManhattanDistance() {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getFValue()));

        List<int[]> initialMoves = new ArrayList<>();
        
        int initialHValue = State.calculateManhattanDistanceHeuristic(initialBoard);

        String initialBoardStr = State.getBoardStateString(initialBoard);
        visitedStates.add(initialBoardStr);

        queue.add(new State(initialBoard, initialMoves, initialHValue));
        
        while(!queue.isEmpty()){
            State curState = queue.poll();
            Board curBoard = curState.getBoard();
            List<int[]> curMoves = curState.getMoves();

            nodesExplored++;
            
            if(curBoard.isSolved()){
                return curMoves;
            }

            int maxCost = curBoard.getRows() * curBoard.getCols() * 50;
            if(curMoves.size() > maxCost){
                continue;
            }
            
            List<Car> pieces = curBoard.getCars();
            for(int i = 0; i < pieces.size(); i++){
                List<Integer> validMoves = curBoard.getValidMoves(i);
                for(Integer moveAmount : validMoves){
                    Board newBoard = curBoard.copy();
                    newBoard = newBoard.applyMove(i, moveAmount);
                    String newBoardStr = State.getBoardStateString(newBoard);
                    
                    // Check if state has been visited BEFORE adding to queue
                    if(!visitedStates.contains(newBoardStr)){
                        visitedStates.add(newBoardStr); // Add to visited immediately
                        
                        List<int[]> newMoves = new ArrayList<>(curMoves);
                        newMoves.add(new int[]{i, moveAmount});
                        
                        int hValue = State.calculateManhattanDistanceHeuristic(newBoard);
                        queue.add(new State(newBoard, newMoves, hValue));
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    public void displaySolutions(List<int[]> moves) {
        if(moves.isEmpty()){
            System.out.println("No solution found.");
            return;
        }

        System.out.println("Solution found with " + nodesExplored + " nodes explored:");
        System.out.println("Intial board state:");
        Board firstBoard = initialBoard;
        System.out.println(firstBoard.toString());
        System.out.println("Result:");
        Board resultBoard = initialBoard;

        for(int[] move : moves) 
        {
            int pieceIndex = move[0];
            int moveAmount = move[1];
            resultBoard = resultBoard.applyMove(pieceIndex, moveAmount);
            System.out.println("Move piece " + resultBoard.getCars().get(pieceIndex).getId() + " by " + moveAmount);
            System.out.println(resultBoard.toString());
        }
    }
}