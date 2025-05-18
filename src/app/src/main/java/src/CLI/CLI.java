package src.CLI;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import src.ADT.Board;
import src.Algorithm.AStar;
import src.Algorithm.GBFS;
import src.Algorithm.UCS;
import src.IO.Input;
import src.IO.Output;
import src.ADT.Car;

/**
 * CLI class to run the command line interface application.
 */
public class CLI {

    /**
     * Main method for the CLI application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) 
    {
        Scanner scanner = new Scanner(System.in);
        Input input = new Input();

        try 
        {
            System.out.print("\033[H\033[2J");
            System.out.println();
            System.out.println("[#] Welcome to the Rush Hour Puzzle Solver!");
            System.out.println();
            System.out.println("[?] Specify the filename (without extension) of the puzzle input file:");
            String fileName = scanner.nextLine();
            System.out.println();

            // Validate the filename
            input.validateFilename(fileName);

            if (input.hasError()) 
            {
                System.out.println(input.getErrorMsg());
                Board board = new Board(0, 0, 0, 0, 0, input.getErrorMsg());
                System.out.println("\n[?] Save the output to a file? (Y/N)");
                Output.confirmError(fileName, board);
                System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                scanner.close();
                return;
            }

            File currentDir = new File(System.getProperty("user.dir"));
            File parentDir = currentDir.getParentFile().getParentFile();
            File file = new File(parentDir + "/test/" + fileName + ".txt");
            String absolutePath = file.getAbsolutePath();

            // Validate the file
            input.validateFile(file);

            if (input.hasError()) 
            {
                System.out.println(input.getErrorMsg());
                Board board = new Board(0, 0, 0, 0, 0, input.getErrorMsg());
                System.out.println("\n[?] Save the output to a file? (Y/N)");
                Output.confirmError(fileName, board);
                System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                scanner.close();
                return;
            }

            // Read the input file
            input.readInput(absolutePath);

            // Validate the input
            if (input.hasError()) 
            {  
                System.out.println(input.getErrorMsg());
                Board board = new Board(0, 0, 0, 0, 0, input.getErrorMsg());
                System.out.println("\n[?] Save the output to a file? (Y/N)");
                Output.confirmError(fileName, board);
                System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                scanner.close();
            }
            else
            {
                Board board = new Board(input.getRows(), 
                                        input.getCols(), 
                                        input.getNumCars(), 
                                        input.getExitRow(), 
                                        input.getExitCol(), 
                                        null);

                board.loadConfiguration(input.getBoardConfig());

                if (board.hasError()) 
                {
                    System.err.println(board.getErrorMsg());
                    System.out.println("\n[?] Save the output to a file? (Y/N)");
                    Output.confirmError(fileName, board);
                    System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                    scanner.close();
                    return;
                }

                System.out.println("[#] Puzzle loaded successfully!");
                System.out.println("[#] Puzzle configuration:");
                // System.out.println(board);
                displayBoardInfo(board, input);

                 System.out.println("\n" + "=".repeat(40));
                System.out.println("ALGORITHM SELECTION");
                System.out.println("=".repeat(40));
                System.out.println("Choose an algorithm:");
                System.out.println("1. A* Algorithm");
                System.out.println("2. Uniform Cost Search (UCS)");
                System.out.println("3. Greedy Best First Search (GBFS)");
                System.out.println();
                System.out.print("Enter your choice (1 or 2): ");
                String algoChoice = scanner.nextLine().trim();
                if (!algoChoice.equals("1") && !algoChoice.equals("2") && !algoChoice.equals("3")) {
                    System.out.println("Invalid choice. Exiting program.");
                    scanner.close();
                    return;
                }
                if (algoChoice.equals("2")) {
                    System.out.println("✓ Using Uniform Cost Search (UCS)");
                    boolean useBlockingHeuristic = getHeuristicChoice(scanner);
                    // Solve the puzzle using UCS algorithm
                    solvePuzzle(board, useBlockingHeuristic, "UCS");
                } else if (algoChoice.equals("1")) {
                    System.out.println("✓ Using A* Algorithm");
                    boolean useBlockingHeuristic = getHeuristicChoice(scanner);
                    // Solve the puzzle using A* algorithm
                    solvePuzzle(board, useBlockingHeuristic,"A*");
                }else{
                    System.out.println("✓ Using Greedy Best First Search (GBFS)");
                    boolean useBlockingHeuristic = getHeuristicChoice(scanner);
                    // Solve the puzzle using GBFS algorithm
                    solvePuzzle(board, useBlockingHeuristic,"GBFS");
                }

                

            }
        } 
        catch (IOException e) 
        {
            System.out.println("[!] An error occurred while reading the file. Please try again.");
            scanner.close();
            return;
        }
    }
    private static boolean getHeuristicChoice(Scanner scanner) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("HEURISTIC SELECTION");
        System.out.println("=".repeat(40));
        System.out.println("Choose a heuristic function:");
        System.out.println("1. Manhattan Distance Heuristic");
        System.out.println("2. Blocking Cars Heuristic");
        System.out.println();
        
        while (true) {
            System.out.print("Enter your choice (1 or 2): ");
            String choice = scanner.nextLine().trim();
            
            if (choice.equals("1")) {
                System.out.println("✓ Using Manhattan Distance Heuristic");
                return false;
            } else if (choice.equals("2")) {
                System.out.println("✓ Using Blocking Cars Heuristic");
                return true;
            } else {
                System.out.println("Invalid choice. Please enter 1 or 2.");
            }
        }
    }

    /**
     * Solves the puzzle using A* algorithm
     * 
     * @param board The initial board state
     * @param useBlockingHeuristic Whether to use blocking Cars heuristic
     */
    private static void solvePuzzle(Board board, boolean useBlockingHeuristic, String algo) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("SOLVING PUZZLE");
        System.out.println("=".repeat(50));
        String curHeu;
        if(algo.equals("A*")) {
            if(useBlockingHeuristic){
                curHeu = "blockingCars";
            } else {
                curHeu = "manhattanDistance";
            }
            AStar aStar = new AStar(board);
            List<int[]> moves = aStar.solve(curHeu);
            aStar.displaySolutions(moves);
        } else if(algo.equals("UCS")) {
            UCS ucs = new UCS(board);
            List<int[]> moves = ucs.solve();
            ucs.displaySolutions(moves);
        } else {
            GBFS gbfs = new GBFS(board);
            List<int[]> moves = gbfs.solve(useBlockingHeuristic ? "blockingCars" : "manhattanDistance");
            gbfs.displaySolutions(moves);
        }
        
    }
    
    /**
     * Displays information about the board and puzzle
     * 
     * @param board The board object
     * @param input The input object
     */
    private static void displayBoardInfo(Board board, Input input) {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("PUZZLE INFORMATION");
        System.out.println("=".repeat(40));
        System.out.println("Board size: " + input.getRows() + " x " + input.getCols());
        System.out.println("Number of Cars: " + (board.getCars().size()));
        System.out.println("Primary car: " + (board.getPrimaryCar() != null ? board.getPrimaryCar().getId() : "Not found"));
        System.out.println("Exit position: " + board.getExitRow() + ", " + board.getExitCol());
        
        System.out.println("Exit row: " + board.getExitRow());
        System.out.println("Exit column: " + board.getExitCol());
        
        System.out.println("\nInitial board state:");
        System.out.println(board.toString());
        
        // Check if already solved
        if (board.isSolved()) {
            System.out.println("\n✓ Puzzle is already solved!");
            return;
        }
        
        System.out.println("\nCars on the board:");
        for (int i = 0; i < board.getCars().size(); i++) {
            Car car = board.getCars().get(i);
            System.out.printf("  %d. %c - %s, size %d, at (%d,%d)%s\n", 
                i + 1, car.getId(),
                car.getOrientation()  == 1 ? "Horizontal" : "Vertical",
                car.getLength(),
                car.getStartRow(), car.getStartCol(),
                car.isPrimary() ? " [PRIMARY]" : "");
        }
    }

}
