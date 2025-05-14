package src.ADT;

import java.util.ArrayList;

/**
 * Represents a board for the puzzle.
 */
public class Board 
{
    private int N;
    private int M;
    private int P;
    private String S;
    private char[][] board;
    private String[] palette;

    /**
     * Constructs a Board object with the provided parameters.
     *
     * @param N number of rows of the board
     * @param M number of columns of the board
     * @param P number of puzzle pieces
     * @param S puzzle type (e.g., "DEFAULT", "CUSTOM", "PYRAMID")
     * @param custom list of custom configuration lines (for CUSTOM puzzles)
     */
    public Board(int N, int M, int P, String S,  ArrayList<String> custom)
    {
        this.N = N;
        this.M = M;
        this.P = P;
        this.S = S;
        this.board = createBoard(N, M, S, custom);
        this.palette = generatePalette();
    }

    public int getHeight() {return this.N;}
    public int getWidth() {return this.M;}
    public int getP() {return this.P;}
    public String getType() {return this.S;}
    public char[][] getBoard() {return this.board;}
    public String[] getPalette() {return this.palette;}

    public char getElement(int i, int j) 
    {
        return this.board[i][j];
    }

    public void setElement(int i, int j, char letter) 
    {
        this.board[i][j] = letter;
    }

    /**
     * Validates the puzzle type.
     *
     * @param puzzleType Puzzle type
     * @return Puzzle type if valid, null otherwise (invalid)
     */
    public static String validateType(String puzzleType) 
    {
        if (!"DEFAULT".equalsIgnoreCase(puzzleType) && !"CUSTOM".equalsIgnoreCase(puzzleType) && !"PYRAMID".equalsIgnoreCase(puzzleType)) 
        {
            puzzleType = null;
        }
        return puzzleType;
    }

    /**
     * Validates the custom configuration.
     *
     * @param custom Custom configuration
     * @param n Number of rows
     * @param m Number of columns
     * @return Custom configuration if valid, null otherwise (invalid)
     */
    public static String validateCustom(ArrayList<String> custom, int n, int m) 
    {
        if (custom.size() != n) 
        {
            return "Custom configuration must have N lines. Found " + custom.size() + " lines instead of " + n + ".";
        }

        for (String line : custom) 
        {
            if (line.length() != m) 
            {
                return "Custom configuration lines must have M characters. Found a line with " + line.length() + " characters instead of " + m + ".";
            }
            for (char c : line.toCharArray()) 
            {
                if (c != '.' && c != 'X') 
                {
                    return "Custom configuration lines must contain only '.' and 'X' characters. Found an invalid character '" + c + "'.";
                }
            }
        }
        return null;
    }

    /**
     * Creates a board with the provided parameters.
     *
     * @param N Number of rows of the board
     * @param M Number of columns of the board
     * @param puzzleType Puzzle type (e.g., "DEFAULT" or "CUSTOM")
     * @param custom List of custom configuration lines (for CUSTOM puzzles)
     * @return Board with the provided parameters
     */
    public static char[][] createBoard(int N, int M, String puzzleType, ArrayList<String> custom) 
    {
        char[][] board = new char[N][M];

        if (puzzleType.equalsIgnoreCase("DEFAULT")) 
        {
            for (int i = 0; i < N; i++) 
            {
                for (int j = 0; j < M; j++) 
                {
                    board[i][j] = '*';
                }
            }
        } 
        else if (puzzleType.equalsIgnoreCase("CUSTOM") && custom != null) 
        {
            for (int i = 0; i < N; i++) 
            {
                String line = custom.get(i);

                for (int j = 0; j < M; j++) 
                {
                    char ch = line.charAt(j);

                    if (ch == 'X') board[i][j] = '*';
                    else board[i][j] = ' ';
                }
            }
        }
        return board;
    }

    /**
     * Counts the number of initial free cells in the board.
     * 
     * @return Number of initial free cells (cells with '*' at the beginning)
     */
    public int initialFreeCells() 
    {
        int count = 0;
        for (int i = 0; i < getHeight(); i++) 
        {
            for (int j = 0; j < getWidth(); j++) 
            {
                if (getElement(i, j) == '*') count++;
            }
        }
        return count;
    }

    /**
     * Checks if a part of a piece fits in the board at the specified position.
     *
     * @param i Row index of the board
     * @param j Column index of the board
     * @return True if the part fits, false otherwise
     */
    public boolean isPartFit(int i, int j) 
    {
        return i >= 0 && i < getHeight() && j >= 0 && j < getWidth() && getElement(i, j) == '*';
    }

    /**
     * Fits a piece in the board at the specified position.
     *
     * @param boardX Row index of the board
     * @param boardY Column index of the board
     * @param piece Piece to fit
     * @return True if the piece fits, false otherwise
     */
    public boolean fitPiece(int boardX, int boardY, Piece piece)
    {
        for (int i = 0; i < piece.getHeight(); i++) 
        {
            for (int j = 0; j < piece.getWidth(); j++) 
            {
                if (piece.getPart(i, j) != '#' && !isPartFit(boardX + i, boardY + j)) 
                {
                    return false;
                }
            }
        }

        for (int i = 0; i < piece.getHeight(); i++) 
        {
            for (int j = 0; j < piece.getWidth(); j++) 
            {
                char letter = piece.getPart(i, j);
                if (letter != '#') 
                {
                    setElement(boardX + i, boardY + j, letter);
                }
            }
        }
        return true;
    }

    /**
     * Removes a piece from the board at the specified position.
     *
     * @param boardX Row index of the board
     * @param boardY Column index of the board
     * @param piece Piece to remove
     */
    public void removePiece(int boardX, int boardY, Piece piece) 
    {
        for (int i = 0; i < piece.getHeight(); i++) 
        {
            for (int j = 0; j < piece.getWidth(); j++) 
            {
                if (piece.getPart(i, j) != '#') 
                {
                    setElement(boardX + i, boardY + j, '*');
                }
            }
        }
    }
    
    /**
     * Generates a palette of colors for the pieces.
     *
     * @return Array of ANSI color codes
     */
    public String[] generatePalette() 
    {
        String[] palette = 
        {
            "\u001B[38;2;255;50;50m",    // A: Bright Red (#FF3232)
            "\u001B[38;2;50;255;50m",    // B: Bright Green (#32FF32)
            "\u001B[38;2;50;150;255m",   // C: Bright Blue (#3296FF)
            "\u001B[38;2;255;255;50m",   // D: Bright Yellow (#FFFF32)
            "\u001B[38;2;255;50;255m",   // E: Bright Magenta (#FF32FF)
            "\u001B[38;2;50;255;255m",   // F: Bright Cyan (#32FFFF)
            "\u001B[38;2;255;128;0m",    // G: Orange (#FF8000)
            "\u001B[38;2;128;255;128m",  // H: Light Green (#80FF80)
            "\u001B[38;2;180;180;255m",  // I: Light Blue (#B4B4FF)
            "\u001B[38;2;255;200;100m",  // J: Light Orange (#FFC864)
            "\u001B[38;2;200;100;255m",  // K: Light Purple (#C864FF)
            "\u001B[38;2;100;255;200m",  // L: Light Turquoise (#64FFC8)
            "\u001B[38;2;255;150;150m",  // M: Light Red (#FF9696)
            "\u001B[38;2;150;255;150m",  // N: Pale Green (#96FF96)
            "\u001B[38;2;255;160;122m",  // O: Light Salmon (#FFA07A)
            "\u001B[38;2;255;215;0m",    // P: Gold (#FFD700)
            "\u001B[38;2;173;255;47m",   // Q: Green Yellow (#ADFF2F)
            "\u001B[38;2;64;224;208m",   // R: Turquoise (#40E0D0)
            "\u001B[38;2;238;130;238m",  // S: Violet (#EE82EE)
            "\u001B[38;2;255;160;160m",  // T: Light Coral (#FFA0A0)
            "\u001B[38;2;135;206;250m",  // U: Light Sky Blue (#87CEFA)
            "\u001B[38;2;200;200;200m",  // V: Light Gray (#C8C8C8)
            "\u001B[38;2;255;100;100m",  // W: Salmon (#FF6464)
            "\u001B[38;2;255;105;180m",  // X: Hot Pink (#FF69B4)
            "\u001B[38;2;150;255;0m",    // Y: Bright Lime (#96FF00)
            "\u001B[38;2;255;80;80m"     // Z: Light Crimson (#FF5050)
        };
        return palette;
    }
    
    /**
     * Prints the board with colors.
     */
    public void print() 
    {
        for (int i = 0; i < getHeight(); i++) 
        {
            for (int j = 0; j < getWidth(); j++) 
            {
                char current = getElement(i, j);
                if (current != ' ') 
                {
                    int index = current - 'A';
                    System.out.print(getPalette()[index] + current + "\u001B[0m");
                } 
                else 
                {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

}
