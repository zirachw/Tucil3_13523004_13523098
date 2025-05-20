package src.CLI;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import src.ADT.Board;
import src.Algorithm.AStar;
import src.Algorithm.GBFS;
import src.Algorithm.UCS;
import src.Algorithm.Algorithm;
import src.IO.Input;
import src.IO.Output;
import src.ADT.Car;

/**
 * CLI class to run the command line interface application.
 */
public class CLI 
{
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

            input.validateFilename(fileName);

            if (input.hasError()) 
            {
                System.out.println(input.getErrorMsg());
                Board board = new Board(0, 0, 0, 0, 0, null, input.getErrorMsg());
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
                Board board = new Board(0, 0, 0, 0, 0, null, input.getErrorMsg());
                System.out.println("\n[?] Save the output to a file? (Y/N)");
                Output.confirmError(fileName, board);
                System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                scanner.close();
                return;
            }

            input.readInput(absolutePath);

            if (input.hasError()) 
            {  
                System.out.println(input.getErrorMsg());
                Board board = new Board(0, 0, 0, 0, 0, null, input.getErrorMsg());
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
                                        input.getExitSide(),
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
                displayBoardInfo(board, input);
                System.out.println();
                
                String algoChoice = validateOption(scanner, 3);
                String heuristic = null;
                Algorithm algorithm = null;
                List<int[]> moves;

                switch (algoChoice) 
                {
                    case "A*":
                        heuristic = validateOption(scanner, 2);
                        algorithm = new AStar(board);
                        break;

                    case "GBFS":
                        heuristic = validateOption(scanner, 2);
                        algorithm = new GBFS(board);
                        break;

                    case "UCS":
                        heuristic = "none";
                        algorithm = new UCS(board);
                        break;
                }

                // Solve the puzzle and display the solution
                if (algorithm != null) 
                {
                    moves = algorithm.solve(heuristic);
                    Output.displayPerState(board,
                                           algorithm.getNodesExplored(), 
                                           algorithm.getExecutionTime(), 
                                           moves);
                    
                    // Ask to save the solution after showing it
                    if (!moves.isEmpty()) 
                    {
                        Output.confirmOptionCLI(fileName, 
                                                board, 
                                                algorithm.getExecutionTime(), 
                                                algorithm.getNodesExplored(), 
                                                moves);
                    }
                    else 
                    {
                        Board noSol = new Board(0, 0, 0, 0, 0, null, "No solution found.");
                        System.out.println("\n[?] Save the output to a file? (Y/N)");
                        Output.confirmError(fileName, noSol);
                        System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                        scanner.close();
                        return;
                    }
                }

                System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                scanner.close();
            }
        } 
        catch (IOException e) 
        {
            System.out.println("[!] An error occurred while reading the file. Please try again.");
            scanner.close();
            return;
        }
    }

    public static String validateOption(Scanner scanner, int numOptions)
    {
        boolean valid = false;
        String algo = null;
        int option = -1;
        
        while (!valid) 
        {
            if (numOptions == 3)
            {
                System.out.println("[#] Algorithm selection:");
                System.out.println();
                System.out.println("[-] 1. A* Algorithm");
                System.out.println("[-] 2. Greedy Best First Search (GBFS)");
                System.out.println("[-] 3. Uniform Cost Search (UCS)");
                System.out.println();
                System.out.println("[?] Enter your choice (1, 2, or 3)");
            }
            else
            {
                System.out.println("[#] Heuristic selection:");
                System.out.println();
                System.out.println("[-] 1. Manhattan Distance");
                System.out.println("[-] 2. Blocking Cars");
                System.out.println();   
                System.out.println("[?] Enter your choice (1 or 2)");
            }
            
            System.out.println();
            String line = scanner.nextLine();
            System.out.println();

            String[] tokens = line.split("\\s+");
            if (tokens.length != 1) 
            {
                System.out.println("[!] Invalid input. Please enter a single integer, example \"1\".");
                System.out.println();
                continue;
            }

            if (!tokens[0].matches("\\d+")) 
            {
                System.out.println("Invalid input. Please enter a non-negative integer.");
                System.out.println();
                continue;
            }

            option = Integer.parseInt(tokens[0]);

            if (option < 1 || option > numOptions) 
            {
                System.out.println("Invalid input. Please enter \"1\" to \"" + numOptions + "\".");
                System.out.println();
                continue;
            }
            else
            {
                valid = true;
                if (numOptions == 3)
                {
                    switch (option) 
                    {
                        case 1:
                            algo = "A*";
                            break;
                        case 2:
                            algo = "GBFS";
                            break;
                        case 3:
                            algo = "UCS";
                            break;
                    }
                }
                else
                {
                    switch (option) 
                    {
                        case 1:
                            algo = "Manhattan";
                            break;
                        case 2:
                            algo = "Blocking";
                            break;
                    }   
                }
            }
        }
        return algo;
    }
    
    /**
     * Displays information about the board and puzzle
     * 
     * @param board The board object
     * @param input The input object
     */
    private static void displayBoardInfo(Board board, Input input) 
    {
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
        if (board.isSolved()) 
        {
            System.out.println("\n✓ Puzzle is already solved!");
            return;
        }
        
        System.out.println("\nCars on the board:");
        for (int i = 0; i < board.getCars().size(); i++) 
        {
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