package src.CLI;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import src.ADT.Board;
import src.ADT.Piece;
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
            if (Input.validateFilename(fileName) != null) 
            {
                String errorMsg = Input.validateFilename(fileName);
                System.out.println(errorMsg);

                Board board = new Board(0, 0, 0, errorMsg, null);
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
            if (Input.validateFile(file) != null) 
            {
                String errorMsg = Input.validateFile(file);
                System.out.println(errorMsg);

                Board board = new Board(0, 0, 0, errorMsg, null);
                System.out.println("\n[?] Save the output to a file? (Y/N)");
                Output.confirmError(fileName, board);
                System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                scanner.close();
                return;
            }

            Input puzzleInput = Input.readInput(absolutePath);

            // Validate the input
            if (puzzleInput.getErrorMsg() != null) 
            {  
                String errorMsg = puzzleInput.getErrorMsg();
                System.out.println(errorMsg);

                Board board = new Board(0, 0, 0, errorMsg, null);
                System.out.println("\n[?] Save the output to a file? (Y/N)");
                Output.confirmError(fileName, board);
                System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                scanner.close();
            }
            else
            {
                Board board = new Board(puzzleInput.getN(), puzzleInput.getM(), puzzleInput.getP(), puzzleInput.getS(), puzzleInput.getCustom());
                Piece[] pieces = Piece.createPieces(puzzleInput.getP(), puzzleInput.getPieces());

                // Validate the pieces
                if (pieces.length == 1 && pieces[0].getErrorMsg() != null) 
                {
                    String errorMsg = pieces[0].getErrorMsg();
                    System.out.println(errorMsg);

                    board = new Board(0, 0, 0, errorMsg, null);
                    System.out.println("\n[?] Save the output to a file? (Y/N)");
                    Output.confirmError(fileName, board);
                    System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                    scanner.close();
                    return;
                }
                
                // Validate the area of free cells
                if (board.initialFreeCells() != Piece.sumOfCells(pieces))
                {
                    String errorMsg = "Area mismath: \n~\nFree cells = " + board.initialFreeCells() + " units \nPiece cells: " + Piece.sumOfCells(pieces) + " units";
                    System.out.println(errorMsg);

                    board = new Board(0, 0, 0, errorMsg, null);
                    System.out.println("\n[?] Save the output to a file? (Y/N)");
                    Output.confirmError(fileName, board);
                    System.out.println("[#] Thank you for using the Rush Hour Puzzle Solver!\n");
                    scanner.close();
                    return;
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

}
