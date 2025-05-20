package src.IO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.ADT.Board;
import src.ADT.Car;

/**
 * Output class to save the puzzle solution to a file.
 */
public class Output 
{
    private String fileName;
    private Board board;
    private long time;
    private int attempts;
    private List<int[]> moves;

    /**
     * Constructs an Output object with the provided parameters.
     *
     * @param fileName Filename to save to
     * @param board Board object
     * @param time Time taken to solve the puzzle
     * @param attempts Number of attempts to solve the puzzle
     */
    public Output(String fileName, Board board, long time, int attempts)
    {
        this.fileName = fileName;
        this.board = board;
        this.time = time;
        this.attempts = attempts;
        this.moves = new ArrayList<>();
    }

    /**
     * Constructs an Output object with solution moves for better formatting
     *
     * @param fileName Filename to save to
     * @param board Board object
     * @param time Time taken to solve the puzzle
     * @param attempts Number of attempts to solve the puzzle
     * @param moves List of solution moves
     */
    public Output(String fileName, Board board, long time, int attempts, List<int[]> moves)
    {
        this.fileName = fileName;
        this.board = board;
        this.time = time;
        this.attempts = attempts;
        this.moves = moves;
    }

    public String getFilename() {return this.fileName;}
    public Board getBoard() {return this.board;}
    public long getTime() {return this.time;}
    public int getAttempts() {return this.attempts;}
    public List<int[]> getMoves() {return this.moves;}

    /**
     * Saves the output of invalid format errors to a file.
     * 
     * @param filename The name of the file to save to
     * @param board The board object containing the error message
     */
    public static void confirmError(String filename, Board board)
    {
        Scanner scanner = new Scanner(System.in);
        boolean valid = false;
        while (!valid) 
        {
            String save = scanner.nextLine();
            System.out.println();
            if (save.equalsIgnoreCase("Y")) 
            {
                if (board.getErrorMsg() == "Filename cannot be empty.")
                {
                    Output output = new Output("invalid-file-1", board, 0, 0);
                    output.saveToTextCLI();
                    valid = true;
                }

                // Use regex to check if board.getType() contains "does not exist in the ~/test directory."
                else if (board.getErrorMsg().matches(".*does not exist in the ~/test directory.*"))
                {
                    Output output = new Output("invalid-file-2", board, 0, 0);
                    output.saveToTextCLI();
                    valid = true;
                }

                else
                {
                    Output output = new Output(filename, board, 0, 0);
                    output.saveToTextCLI();
                    valid = true;
                }
            } 
            else if (save.equalsIgnoreCase("N")) 
            {
                valid = true;
            } 
            else 
            {
                System.out.println("\nInvalid input. Please enter Y or N.");
            }
        }
        scanner.close();
        return;
    }

    /**
     * Saves the output of the puzzle solution to a file with options.
     * 
     * @param filename The name of the file to save to
     * @param board The board object
     * @param time The time taken to solve the puzzle
     * @param attempts The number of attempts to solve the puzzle
     * @param moves The list of moves that solve the puzzle
     */
    public static void confirmOptionCLI(String filename, Board board, long time, int attempts, List<int[]> moves)
    {
        System.out.println("\nSave the output to .txt? (Y/N)");
        Scanner scanner = new Scanner(System.in);
        boolean valid = false;

        while (!valid) 
        {
            String overwrite = scanner.nextLine();
            if (overwrite.equalsIgnoreCase("Y")) 
            {
                Output output = new Output(filename, board, time, attempts, moves);
                output.saveToTextCLI();
                System.out.println("\n[~] Successfully saved as '" + filename + "-output.txt'.\n");
                valid = true;
            } 
            else if (overwrite.equalsIgnoreCase("N")) 
            {
                System.err.println();
                valid = true;
            } 
            else 
            {
                System.out.println("\n[!] Invalid input. Please enter Y or N.");
            }
        }
        scanner.close();
        return;
    }

    /**
     * Saves the text output of the puzzle solution to a file.
     */
    public void saveToTextCLI()
    {
        File currentDir = new File(System.getProperty("user.dir"));
        File testDir = currentDir.getParentFile().getParentFile();

        File txtFile = new File(testDir + "/test/" + getFilename() + "-output.txt");
        String txtPath = txtFile.getAbsolutePath();

        File fileCheck = new File(txtPath);
        if (getFilename() == "invalid-file-1")
        {
            writeTextCLI(txtPath);
            return;
        }

        else if (fileCheck.exists())
        {
            System.out.println("\n[?] '" + getFilename() + "-output.txt' already exists. Overwrite? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            boolean valid = false;

            while (!valid) 
            {
                String overwrite = scanner.nextLine();
                if (overwrite.equalsIgnoreCase("Y")) 
                {
                    writeTextCLI(txtPath);
                    valid = true;
                } 
                else if (overwrite.equalsIgnoreCase("N")) 
                {
                    System.err.println();
                    valid = true;
                } 
                else 
                {
                    System.out.println("\n[!] Invalid input. Please enter Y or N.");
                }
            }
            scanner.close();
            return;
        }
        else
        {
            writeTextCLI(txtPath);
            return;
        }
    }

    /**
     * Writes the text output of the puzzle solution to a file in the displayPerState format.
     * 
     * @param path The path to the file to save to
     */
    public void writeTextCLI(String path)
    {
        File currentDir = new File(System.getProperty("user.dir"));
        File testDir = currentDir.getParentFile().getParentFile();

        File txtFile = new File(testDir + "/test/" + getFilename() + "-output.txt");
        String txtPath = txtFile.getAbsolutePath();

        try
        {
            File file = new File(txtPath);
            file.createNewFile();
        }
        catch (IOException e)
        {
            System.out.println("[!] An error occurred while creating the file. Please try again.");
            return;
        }

        try
        {
            java.io.FileWriter writer = new java.io.FileWriter(txtPath);

            if (board.hasError())
            {
                writer.write(getBoard().getErrorMsg());
                writer.close();
                return;
            }
            else
            {
                writer.write("Nodes Explored: " + getAttempts() + "\n");
                writer.write("Searching Time: " + getTime() + " ms\n\n");
                
                writer.write("Initial board state:\n");
                writer.write(boardToString(board) + "\n\n");
                
                if (moves != null && !moves.isEmpty()) 
                {
                    writer.write("Solution:\n\n");
                    
                    Board resultBoard = board.copy();
                    List<int[]> combinedMoves = combineConsecutiveMoves(moves);
                    
                    for (int[] move : combinedMoves) 
                    {
                        int pieceIndex = move[0];
                        int moveAmount = move[1];
                        
                        resultBoard = applyMoveAmount(resultBoard, pieceIndex, moveAmount);
                        int orientation = resultBoard.getCars().get(pieceIndex).getOrientation();

                        String direction;
                        
                        if (orientation == 1 && moveAmount > 0) direction = "Right";
                        else if (orientation == 1 && moveAmount < 0) direction = "Left";
                        else if (orientation == 0 && moveAmount > 0) direction = "Down";
                        else direction = "Up";
                        
                        writer.write("Move piece " + resultBoard.getCars().get(pieceIndex).getId() + 
                                     " " + Math.abs(moveAmount) + " spaces " + direction + "\n");
                        
                        writer.write(boardToString(resultBoard) + "\n\n");
                    }
                } 
                else writer.write("No solution found.\n");
                
                writer.close();
                return;
            }
        }
        catch (IOException e)
        {
            System.out.println("[!] An error occurred while writing to the file. Please try again.");
            return;
        }
    }

    /**
     * Convert a board to a string without ANSI colors
     * 
     * @param board The board to convert
     * @return String representation of the board
     */
    public static String boardToString(Board board) 
    {
        StringBuilder sb = new StringBuilder();
        
        // Case: Top exit
        if (board.getExitSide() != null && board.getExitSide().equalsIgnoreCase("TOP")) 
        {
            for (int j = 0; j < board.getCols(); j++) 
            {
                if (j == board.getExitCol()) sb.append('K');
                else sb.append(' ');
            }
            sb.append("\n");
        }
        
        for (int i = 0; i < board.getRows(); i++) 
        {
            // Case: Left exit
            if (board.getExitSide() != null && 
                board.getExitSide().equalsIgnoreCase("LEFT") && 
                i == board.getExitRow()) 
            {
                sb.append('K');
            } 
            else if (board.getExitSide() != null && 
                     board.getExitSide().equalsIgnoreCase("LEFT")) 
            {
                sb.append(' ');
            }
            
            // Add the main grid
            for (int j = 0; j < board.getCols(); j++) sb.append(board.getElement(i, j));
            
            // Case: Right exit
            if (board.getExitSide() != null && 
                board.getExitSide().equalsIgnoreCase("RIGHT") && 
                i == board.getExitRow()) 
            {
                sb.append('K');
            }
            
            // Add newline if not the last row
            if (i < board.getRows() - 1) sb.append("\n");
        }
        
        // Case: Bottom exit
        if (board.getExitSide() != null && board.getExitSide().equalsIgnoreCase("BOTTOM")) 
        {
            sb.append("\n");
            for (int j = 0; j < board.getCols(); j++) 
            {
                if (j == board.getExitCol()) sb.append('K');
                else sb.append(' ');
            }
        }
        
        return sb.toString();
    }

    /**
     * Display the solution step by step (one move at a time)
     * This shows each individual step even for consecutive moves of the same car
     * @param moves The list of moves that solve the puzzle
     */
    public static void displayPerMove(Board initialBoard, int nodesExplored, long executionTime, List<int[]> moves) 
    {
        if(moves.isEmpty()) {
            System.out.println("No solution found.");
            return;
        }

        System.out.println("Initial board state:");
        Board firstBoard = initialBoard;
        System.out.println(firstBoard.toString());
        System.out.println();
        System.out.println("Result:");
        Board resultBoard = initialBoard;
        
        for(int[] move : moves) 
        {
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

            System.out.println("Move piece " + resultBoard.getCars().get(pieceIndex).getId() + " " + Math.abs(moveAmount) + " spaces " + direction);
            System.out.println(resultBoard);
            System.out.println();
        }

        System.out.println("Nodes Explored: " + nodesExplored);
        System.out.println("Searching Time: " + executionTime + " ms");
    }
    
    /**
     * Display the solution by combined states (consecutive moves of the same car are combined)
     * More suitable for CLI and file output
     * @param moves The list of moves that solve the puzzle
     */
    public static void displayPerState(Board initialBoard, int nodesExplored, long executionTime, List<int[]> moves) 
    {
        if(moves.isEmpty()) 
        {
            System.out.println("No solution found.");
            return;
        }

        System.out.println("Initial board state:");
        Board firstBoard = initialBoard;
        System.out.println(firstBoard.toString());
        System.out.println();
        System.out.println("Result:");
        Board resultBoard = initialBoard;
        
        List<int[]> combinedMoves = combineConsecutiveMoves(moves);
        
        for(int[] move : combinedMoves) 
        {
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

            System.out.println("Move piece " + resultBoard.getCars().get(pieceIndex).getId() + " " + Math.abs(moveAmount) + " spaces " + direction);
            System.out.println(resultBoard);
            System.out.println();
        }

        System.out.println("Nodes Explored: " + nodesExplored);
        System.out.println("Searching Time: " + executionTime + " ms");
    }
    
    /**
     * Apply a move of a specific amount (possibly multiple steps)
     * @param board The board to apply the move to
     * @param pieceIndex The index of the piece to move
     * @param moveAmount The amount to move
     * @return The new board state after the move
     */
    public static Board applyMoveAmount(Board board, int pieceIndex, int moveAmount) 
    {
        Board newBoard = board.copy();
        Car car = newBoard.getCars().get(pieceIndex);
        
        int[][] oldCells = car.getOccupiedCells();
        for (int[] cell : oldCells) 
            newBoard.getGrid()[cell[0]][cell[1]] = '.';
        
        // Apply the move in one step
        car.move(moveAmount);
        
        // Update the grid with the new car position
        int[][] newCells = car.getOccupiedCells();
        for (int[] cell : newCells)
            newBoard.getGrid()[cell[0]][cell[1]] = car.getId();
        
        return newBoard;
    }
    
    /**
     * Combine consecutive moves of the same car into single moves
     * Only combines directly consecutive moves (not separated by other car moves)
     * 
     * @param moves The original list of moves
     * @return A list with consecutive moves of the same car combined
     */
    public static List<int[]> combineConsecutiveMoves(List<int[]> moves) 
    {
        if (moves.isEmpty()) return new ArrayList<>();
        
        List<int[]> combinedMoves = new ArrayList<>();
        int currentPieceIndex = moves.get(0)[0];
        int currentAmount = moves.get(0)[1];
        
        for (int i = 1; i < moves.size(); i++) 
        {
            int[] move = moves.get(i);
            int pieceIndex = move[0];
            int moveAmount = move[1];
            
            if (pieceIndex == currentPieceIndex) 
                currentAmount += moveAmount;
            else 
            {
                // Different car
                if (currentAmount != 0)
                    combinedMoves.add(new int[]{currentPieceIndex, currentAmount});

                currentPieceIndex = pieceIndex;
                currentAmount = moveAmount;
            }
        }
        
        // Add the last combined move
        if (currentAmount != 0)
            combinedMoves.add(new int[]{currentPieceIndex, currentAmount});
        
        return combinedMoves;
    }
}