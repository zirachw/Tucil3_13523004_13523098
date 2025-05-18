package src.ADT;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class State implements Comparable<State> {
    private Board board;
    private List<int[]> moves; // History of moves: [carIndex, moveAmount, pieceId]
    private int gValue; // Cost so far: for UCS and A*
    private int hValue; // Heuristic value: for A* and Greedy
    private int fValue; // For A*: f(n) = g(n) + h(n)
    
    // Constructor for UCS (doesn't need hValue)
    public State(Board board, List<int[]> moves) {
        this.board = board;
        this.moves = new ArrayList<>(moves);
        calculateGValue();
    }

    /* Constructor for Greedy and A*  
     * hValue calculation is made as parameter because there are
     * several methods for calculating hValue */
    public State(Board board, List<int[]> moves, int hValue) { 
        this.board = board;
        this.moves = new ArrayList<>(moves);
        calculateGValue();
        this.hValue = hValue;
        calculateFValue();
    }
    
    // Getters
    public Board getBoard() {
        return board;
    }
    
    public List<int[]> getMoves() {
        return moves;
    }
    
    public int getGValue() {
        return gValue;
    }
    
    public int getHValue() {
        return hValue;
    }
    
    public int getFValue() {
        return fValue;
    }

    /* Convert state to String for visitedStates
     * to avoid duplicates and cycles */
    public static String getBoardStateString(Board board) {
        StringBuilder sb = new StringBuilder();
        for (Car car : board.getCars()) {
            sb.append(car.getId()).append(car.getStartRow()).append(car.getStartCol());
        }
        return sb.toString();
    }

    // Calculation functions
    public void calculateGValue() {
        this.gValue = this.moves.size();
    }

    // f(n) = g(n) + h(n)
    public void calculateFValue() {
        this.fValue = getGValue() + getHValue(); 
    }

    public static int calculateBlockingCarHeuristic(Board board) {
        Car primaryCar = board.getPrimaryCar();
        
        if (primaryCar == null) return 9999999;
        
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        int distance = 0;
        int blockingCars = 0;
        
        if (primaryCar.getOrientation() == 1) {
            // Primary car moves horizontally
            int rightEdge = primaryCar.getStartCol() + primaryCar.getLength() - 1;
            distance = exitCol - rightEdge - 1;
            
            // Count blocking cars between primary car and exit
            Set<Character> blockingPieceIds = new HashSet<>();
            for (int col = rightEdge + 1; col < exitCol; col++) {
                if (col >= 0 && col < board.getCols() && primaryCar.getStartRow() >= 0 && primaryCar.getStartRow() < board.getRows()) {
                    char cell = board.getGrid()[primaryCar.getStartRow()][col];
                    if (cell != '.') {
                        blockingPieceIds.add(cell);
                    }
                }
            }
            blockingCars = blockingPieceIds.size();
        } else {
            // Primary car moves vertically
            int bottomEdge = primaryCar.getStartRow() + primaryCar.getLength() - 1;
            distance = exitRow - bottomEdge - 1;
            
            // Count blocking cars between primary car and exit
            Set<Character> blockingPieceIds = new HashSet<>();
            for (int row = bottomEdge + 1; row < exitRow; row++) {
                if (row >= 0 && row < board.getRows() && primaryCar.getStartCol() >= 0 && primaryCar.getStartCol() < board.getCols()) {
                    char cell = board.getGrid()[row][primaryCar.getStartCol()];
                    if (cell != '.') {
                        blockingPieceIds.add(cell);
                    }
                }
            }
            blockingCars = blockingPieceIds.size();
        }
        
        // Return distance + penalty for blocking cars
        return Math.max(0, distance) + blockingCars * 2;
    }
    
    public static int calculateManhattanDistanceHeuristic(Board board) {
        Car primaryCar = board.getPrimaryCar();
        
        if (primaryCar == null) return 9999999;
        
        int exitRow = board.getExitRow();
        int exitCol = board.getExitCol();
        
        if (primaryCar.getOrientation() == 1) {
            // For horizontal car, calculate distance from right edge to exit
            int rightEdge = primaryCar.getStartCol() + primaryCar.getLength() - 1;
            return Math.max(0, exitCol - rightEdge - 1);
        } else {
            // For vertical car, calculate distance from bottom edge to exit
            int bottomEdge = primaryCar.getStartRow() + primaryCar.getLength() - 1;
            return Math.max(0, exitRow - bottomEdge - 1);
        }
    }
    
    @Override
    public int compareTo(State other) {
        return Integer.compare(this.fValue, other.fValue);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { 
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        State state = (State) o;
        return Objects.equals(board, state.board);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(board);
    }
    
    public State addMove(int carIndex, int moveAmount, Board newBoard, int newHValue) {
        List<int[]> newMoves = new ArrayList<>(moves);
        char carId = board.getCars().get(carIndex).getId();
        newMoves.add(new int[]{carIndex, moveAmount, (int)carId});
        return new State(newBoard, newMoves, newHValue);
    }
        
    // Helper method to get readable move history
    public String getMovesAsString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < moves.size(); i++) {
            int[] move = moves.get(i);
            int carIndex = move[0];
            int moveAmount = move[1];
            char pieceId = (char)move[2];
            
            String direction = "";
            if (moveAmount > 0) {
                direction = " right/down " + moveAmount + " steps";
            } else {
                direction = " left/up " + Math.abs(moveAmount) + " steps";
            }
            
            sb.append("Move ").append(i + 1).append(": Piece '").append(pieceId)
              .append("' (index ").append(carIndex).append(")").append(direction);
            
            if (i < moves.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    // Helper method to get the last moved car
    public char getLastMovedPieceId() {
        if (moves.isEmpty()) {
            return '\0'; // null character if no moves
        }
        int[] lastMove = moves.get(moves.size() - 1);
        return (char)lastMove[2];
    }
    
    // Helper method to get the last move details
    public String getLastMoveDescription() {
        if (moves.isEmpty()) {
            return "No moves made";
        }
        
        int[] lastMove = moves.get(moves.size() - 1);
        int carIndex = lastMove[0];
        int moveAmount = lastMove[1];
        char pieceId = (char)lastMove[2];
        
        String direction = moveAmount > 0 ? "right/down" : "left/up";
        return "Piece '" + pieceId + "' moved " + direction + " " + Math.abs(moveAmount) + " steps";
    }
}