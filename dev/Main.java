import java.util.*;

/**
 * Main driver class for the Rush Hour puzzle game.
 * Demonstrates how to use the Board and Piece classes to create and solve Rush Hour puzzles.
 */
public class Main {

    /**
     * Main method to run the Rush Hour game
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("Rush Hour Puzzle Game");
        System.out.println("---------------------");
        
        // Create a new board (standard 6x6 board for Rush Hour)
        Board board = createSampleBoard();
        
        // Print the initial board state
        System.out.println("Initial Board:");
        System.out.println(board);
        System.out.println();
        
        // Solve the puzzle
        List<Move> solution = solvePuzzle(board);
        
        // Print solution
        if (solution != null) {
            System.out.println("Solution found! " + solution.size() + " moves required:");
            printSolution(board, solution);
        } else {
            System.out.println("No solution found!");
        }
    }
    
    /**
     * Creates a sample Rush Hour board configuration
     * @return A board with a sample configuration
     */
    private static Board createSampleBoard() {
        // Create a standard 6x6 Rush Hour board
        Board board = new Board(6, 6);
        
        // Sample configuration (classic Rush Hour layout)
        // P = Primary piece (the red car)
        // A, B, C, etc. = Other vehicles
        // K = Exit location
        char[][] config = {
            {'.','.','.','.','.','.'},
            {'.','.','A','A','A','.'},
            {'P','P','C','D','B','K'},
            {'.','.','C','D','B','.'},
            {'E','E','C','.','.','.'},
            {'.','.','.','.','.','.'}
        };
        
        board.loadConfiguration(config);
        return board;
    }
    
    /**
     * Solves the Rush Hour puzzle using breadth-first search
     * @param initialBoard The starting board configuration
     * @return A list of moves that solve the puzzle, or null if no solution exists
     */
    private static List<Move> solvePuzzle(Board initialBoard) {
        Queue<BoardState> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, Move> moveMap = new HashMap<>();
        Map<String, String> parentMap = new HashMap<>();
        
        // Start with the initial board
        String initialBoardStr = initialBoard.toString();
        queue.add(new BoardState(initialBoard, null));
        visited.add(initialBoardStr);
        
        System.out.println("Solving puzzle...");
        
        while (!queue.isEmpty()) {
            BoardState currentState = queue.poll();
            Board currentBoard = currentState.board;
            String currentBoardStr = currentBoard.toString();
            
            // Check if the puzzle is solved
            if (currentBoard.isSolved()) {
                return reconstructPath(parentMap, moveMap, initialBoardStr, currentBoardStr);
            }
            
            // Try all possible moves for all pieces
            List<Piece> pieces = currentBoard.getPieces();
            for (int i = 0; i < pieces.size(); i++) {
                List<Integer> validMoves = currentBoard.getValidMoves(i);
                
                for (Integer moveAmount : validMoves) {
                    Board newBoard = currentBoard.applyMove(i, moveAmount);
                    String newBoardStr = newBoard.toString();
                    
                    if (!visited.contains(newBoardStr)) {
                        visited.add(newBoardStr);
                        
                        // Save the move that led to this board
                        Move move = new Move(i, pieces.get(i).getId(), moveAmount);
                        moveMap.put(newBoardStr, move);
                        parentMap.put(newBoardStr, currentBoardStr);
                        
                        // Add new board to the queue
                        queue.add(new BoardState(newBoard, currentBoard));
                    }
                }
            }
        }
        
        // No solution found
        return null;
    }
    
    /**
     * Reconstructs the solution path from the parent and move maps
     * @param parentMap Map linking board states to their parent states
     * @param moveMap Map linking board states to the moves that created them
     * @param initialBoardStr String representation of the initial board state
     * @param finalBoardStr String representation of the final board state
     * @return List of moves forming the solution
     */
    private static List<Move> reconstructPath(Map<String, String> parentMap, Map<String, Move> moveMap, 
                                             String initialBoardStr, String finalBoardStr) {
        List<Move> solution = new ArrayList<>();
        String currentBoardStr = finalBoardStr;
        
        // Trace back from the final state to the initial state
        while (!currentBoardStr.equals(initialBoardStr)) {
            Move move = moveMap.get(currentBoardStr);
            solution.add(0, move);  // Add to beginning of list
            currentBoardStr = parentMap.get(currentBoardStr);
        }
        
        return solution;
    }
    
    /**
     * Prints the solution steps
     * @param initialBoard The starting board configuration
     * @param solution List of moves in the solution
     */
    private static void printSolution(Board initialBoard, List<Move> solution) {
        Board currentBoard = initialBoard;
        
        System.out.println("\nSolution Steps:");
        System.out.println("Step 0 (Initial state):");
        System.out.println(currentBoard);
        
        for (int i = 0; i < solution.size(); i++) {
            Move move = solution.get(i);
            
            // Find the piece index
            int pieceIndex = findPieceIndex(currentBoard, move.pieceId);
            if (pieceIndex == -1) {
                System.err.println("Error: Piece " + move.pieceId + " not found!");
                return;
            }
            
            // Apply the move
            currentBoard = currentBoard.applyMove(pieceIndex, move.amount);
            
            // Print the step
            Piece piece = currentBoard.getPieces().get(pieceIndex);
            String direction = "";
            if (piece.isHorizontal()) {
                direction = move.amount > 0 ? "right" : "left";
            } else {
                direction = move.amount > 0 ? "down" : "up";
            }
            
            System.out.println("\nStep " + (i + 1) + ":");
            System.out.println("Move piece " + move.pieceId + " " + direction + 
                    " by " + Math.abs(move.amount) + " spaces");
            System.out.println(currentBoard);
        }
    }
    
    /**
     * Finds the index of a piece in the board's piece list by its ID
     * @param board The board to search
     * @param pieceId The ID of the piece to find
     * @return The index of the piece, or -1 if not found
     */
    private static int findPieceIndex(Board board, char pieceId) {
        List<Piece> pieces = board.getPieces();
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).getId() == pieceId) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Class representing a board state in the BFS algorithm
     */
    private static class BoardState {
        Board board;
        Board parent;
        
        public BoardState(Board board, Board parent) {
            this.board = board;
            this.parent = parent;
        }
    }
    
    /**
     * Class representing a move in the solution
     */
    private static class Move {
        int pieceIndex;
        char pieceId;
        int amount;
        
        public Move(int pieceIndex, char pieceId, int amount) {
            this.pieceIndex = pieceIndex;
            this.pieceId = pieceId;
            this.amount = amount;
        }
        
        @Override
        public String toString() {
            return "Move piece " + pieceId + " by " + amount;
        }
    }
}