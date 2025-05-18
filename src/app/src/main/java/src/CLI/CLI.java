package src.CLI;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import src.ADT.Board;
import src.IO.Input;
import src.IO.Output;

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
                System.out.println(board);
            }
        } 
        catch (IOException e) 
        {
            System.out.println("[!] An error occurred while reading the file. Please try again.");
            scanner.close();
            return;
        }
    }

}
