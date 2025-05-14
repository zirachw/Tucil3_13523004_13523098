package src.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import src.ADT.Board;

/**
 * Input class to read and parse puzzle input from a .txt file and return an Input object.
 */
public class Input 
{
    private int N;
    private int M;
    private int P;
    private String S;
    private ArrayList<String> Custom;
    private ArrayList<String> Pieces;
    private String errorMsg;

    public record FirstLine(int n, int m, int p, String errorMessage) {}

    /**
     * Constructs an Input object with the provided parameters.
     *
     * @param N number of rows of the board
     * @param M number of columns of the board
     * @param P number of puzzle pieces
     * @param S puzzle type (e.g., "DEFAULT", "CUSTOM", "PYRAMID")
     * @param Custom list of custom configuration lines (for CUSTOM puzzles)
     * @param Pieces list of strings representing the puzzle pieces
     */
    public Input(int N, int M, int P, String S, ArrayList<String> Custom, ArrayList<String> Pieces, String errorMsg) 
    {
        this.N = N;
        this.M = M;
        this.P = P;
        this.S = S;
        this.Custom = Custom;
        this.Pieces = Pieces;
        this.errorMsg = errorMsg;
    }

    public int getN() { return this.N; }
    public int getM() { return this.M; }
    public int getP() { return this.P; }
    public String getS() { return this.S; }
    public ArrayList<String> getCustom() { return this.Custom; }
    public ArrayList<String> getPieces() { return this.Pieces; }
    public String getErrorMsg() { return this.errorMsg; }

    /**
     * Validates the filename.
     *
     * @param fileName Filename
     * @return Error message if invalid, null otherwise
     */
    public static String validateFilename(String fileName) 
    {
        if (fileName == null || fileName.isEmpty()) 
        {
            return "Filename cannot be empty.";
        }
        return null;
    }

    /**
     * Validates the file.
     *
     * @param file File object
     * @return Error message if invalid, null otherwise
     */
    public static String validateFile(File file) 
    {
        if (!file.exists()) return "'" + file.getName() + "' does not exist in the ~/test directory.";
        else if (!file.canRead()) return "File cannot be read. Please check file permissions.";
        else if (file.length() == 0) return "File is empty.";
        else return null;
    }

    public static FirstLine validateFirstLine(String line) 
    {
        String[] tokens = line.split("\\s+");
        if (tokens.length != 3) 
        {
            String message = "First line must contain exactly three values: N, M, P. Found " + tokens.length + " values instead.";
            return new FirstLine(-1, -1, -1, message);
        }

        // Before parsing, check if the values are integers
        for (String token : tokens) 
        {
            if (!token.matches("\\d+")) 
            {
                String message = "N, M, and P must be positive integers. Found " + tokens[0] + ", " + tokens[1] + ", " + tokens[2] + " instead.";
                return new FirstLine(-1, -1, -1, message);
            }
        }

        int n = Integer.parseInt(tokens[0]);
        int m = Integer.parseInt(tokens[1]);
        int p = Integer.parseInt(tokens[2]);

        if (n < 1 || m < 1 || p < 1) 
        {
            String message = "N, M, and P must be positive integers. Found N = " + n + ", M = " + m + ", P = " + p + ".";
            return new FirstLine(-1, -1, -1, message);
        }
        else if (p > 26) 
        {
            String message = "P must be at most 26. Found P = " + p + ".";
            return new FirstLine(-1, -1, -1, message);
        }
        return new FirstLine(n, m, p, null);
    }

    /**
     * Reads puzzle input from a .txt file and returns a new Input object.
     *
     * Expected file format:
     * 
     *   N M P
     *   S
     *   [If S == "CUSTOM", then read N lines for the custom board configuration]
     *   Then read puzzle pieces until the end of the file
     *
     * @param filePath path to the puzzle specification file
     * @return an Input object containing parsed puzzle data or an error message
     */
    public static Input readInput(String filePath) throws IOException 
    {
        boolean firstLine = true;
        boolean secondLine = true;
        int N = -1, M = -1, P = -1;
        String S = null, message = null;
        ArrayList<String> custom = new ArrayList<>();
        ArrayList<String> pieces = new ArrayList<>();
        int countCustom = 0;

        BufferedReader br = new BufferedReader(new FileReader(filePath));
        BufferedReader checker = new BufferedReader(new FileReader(filePath));

        int length = 0;
        while (checker.readLine() != null) length++;
        checker.close();
        
        String buffer;
        while ((buffer = br.readLine()) != null) 
        {
            if (firstLine) 
            {
                FirstLine result = validateFirstLine(buffer);
                N = result.n();
                M = result.m();
                P = result.p();
                firstLine = false;
                
                if (result.errorMessage() != null) 
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, null, result.errorMessage());
                }
                else if (length < 2) 
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, null, "No puzzle type found in the file.");
                }
                else continue;
            }
            else if (secondLine)
            {
                S = Board.validateType(buffer);
                secondLine = false;

                if (S == null) 
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, null, "Invalid puzzle type, must be DEFAULT or CUSTOM. Found: " + buffer);
                }
                else if (S.equalsIgnoreCase("PYRAMID"))
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, null, "Mff banh, sy skil isu bikin Pyramid :(");
                }
                else if (S.equalsIgnoreCase("CUSTOM") && length < 3) 
                {
                    br.close();
                    return new Input(-1, -1, -1, null, null, null, "No custom configuration found in the file.");
                }
                else continue;
            }
            else 
            {
                if ("CUSTOM".equalsIgnoreCase(S)) 
                {
                    if (!(buffer.matches("\\s+") || buffer.isEmpty()))
                    {
                        if (buffer.matches(".*[\\.X].*") && countCustom < N) 
                        {
                            custom.add(buffer);
                            countCustom++;
                        }
                        else
                        {
                            message = Board.validateCustom(custom, N, M);
                            if (message != null) 
                            {
                                br.close();
                                return new Input(-1, -1, -1, null, null, null, message);
                            }
                            else 
                            {
                                if (!(buffer.matches("\\s+") || buffer.isEmpty())) pieces.add(buffer);
                                else
                                {
                                    br.close();
                                    return new Input(-1, -1, -1, null, null, null, "Found invalid line, expected continuation of puzzle pieces line for every newline");
                                }
                            }
                        }
                    }
                    else
                    {
                        br.close();
                        return new Input(-1, -1, -1, null, null, null, "Found invalid line, expected continuation of custom configuration for every newline");
                    }
                }
                else 
                {
                    if (!(buffer.matches("\\s+") || buffer.isEmpty())) pieces.add(buffer);
                    else
                    {
                        br.close();
                        return new Input(-1, -1, -1, null, null, null, "Found invalid line, expected continuation of puzzle pieces line for every newline");
                    }
                }
            } 
        }
        
        br.close();
        return new Input(N, M, P, S, custom, pieces, null);
    }
    
}
