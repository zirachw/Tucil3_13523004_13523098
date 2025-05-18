package src.Algorithm;
import src.ADT.*;
import java.util.*;

public class AStar {
    private Board initialBoard;
    private Set<String> visitedStates;
    private int nodesExplored;
    
    public AStar(Board board) {
        this.initialBoard = board;
        this.visitedStates = new HashSet<>();
        this.nodesExplored = 0;
    }

    public List<int[]> solve(String heuristic){
        if(heuristic.equals("blockingCars")){
            return solveBlockingCars();
        } else if(heuristic.equals("manhattanDistance")){
            return solveManhattanDistance();
        } else {
            throw new IllegalArgumentException("Invalid heuristic: " + heuristic);
        }
    }

    private List<int[]> solveBlockingCars() {
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getFValue()));

        List<int[]> initialMoves = new ArrayList<>();
        
        int intialHValue = State.calculateBlockingCarHeuristic(initialBoard);

        queue.add(new State(initialBoard, initialMoves, intialHValue));
        
        int counter = 0;
        while(!queue.isEmpty()){
            counter++;
            System.out.println(counter);
            State curState = queue.poll();
            Board curBoard = curState.getBoard();
            System.out.println("Current board state: \n" + curBoard.toString());
            List<int[]> curMoves = curState.getMoves();

            nodesExplored++;
            if(curBoard.isSolved()){
                return curMoves;
            }

            String curBoardStr = State.getBoardStateString(curBoard);
            if(visitedStates.contains(curBoardStr)){
                continue;
            }else{
                visitedStates.add(curBoardStr);
            }
            
            List<Car> pieces = curBoard.getCars();
            for(int i = 0; i < pieces.size(); i++){
                List<Integer> validMoves = curBoard.getValidMoves(i);
                System.out.println("Moving piece " + pieces.get(i).getId() + " with valid moves: " + validMoves);
                for(Integer moveAmount : validMoves){
                    Board newBoard = curBoard.copy();
                    newBoard = newBoard.applyMove(i, moveAmount);
                    String newBoardStr = State.getBoardStateString(newBoard);
                    
                    if(!visitedStates.contains(newBoardStr)){
                        List<int[]> newMoves = new ArrayList<>(curMoves);
                        newMoves.add(new int[]{i, moveAmount});
                        
                        int hValue = State.calculateBlockingCarHeuristic(newBoard);
                        queue.add(new State(newBoard, newMoves, hValue));
                    }else{}
                }
            }
        }

        return new ArrayList<>();
    }

    private List<int[]> solveManhattanDistance() {
    PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getFValue()));

    List<int[]> initialMoves = new ArrayList<>();
    
    int initialHValue = State.calculateManhattanDistanceHeuristic(initialBoard);

    queue.add(new State(initialBoard, initialMoves, initialHValue));
    
    int counter = 0;
    while(!queue.isEmpty()){
        counter++;
        System.out.println(counter);
        State curState = queue.poll();
        Board curBoard = curState.getBoard();
        // System.out.println("Current board state: \n" + curBoard.toString());
        List<int[]> curMoves = curState.getMoves();

        nodesExplored++;
        if(curBoard.isSolved()){
            return curMoves;
        }

        String curBoardStr = State.getBoardStateString(curBoard);
        if(visitedStates.contains(curBoardStr)){
            continue;
        }else{
            visitedStates.add(curBoardStr);
        }
        
        List<Car> pieces = curBoard.getCars();
        for(int i = 0; i < pieces.size(); i++){
            List<Integer> validMoves = curBoard.getValidMoves(i);
            // System.out.println("Moving piece " + pieces.get(i).getId() + " with valid moves: " + validMoves);
            for(Integer moveAmount : validMoves){
                Board newBoard = curBoard.copy();
                newBoard = newBoard.applyMove(i, moveAmount);
                String newBoardStr = State.getBoardStateString(newBoard);
                
                if(!visitedStates.contains(newBoardStr)){
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
        for(int[] move : moves) {
            int pieceIndex = move[0];
            int moveAmount = move[1];
            resultBoard = resultBoard.applyMove(pieceIndex, moveAmount);
            System.out.println("Move piece " + resultBoard.getCars().get(pieceIndex).getId() + " by " + moveAmount);
            System.out.println(resultBoard.toString());
        }

    }
}
