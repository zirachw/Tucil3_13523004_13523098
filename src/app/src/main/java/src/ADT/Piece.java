package src.ADT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a puzzle piece.
 */
public class Piece 
{
    private int N;
    private int M;
    private char[][] Shadow;
    private String errorMsg;

    /**
     * Constructs a Piece object with the provided parameters.
     * 
     * @param N Maximum height of the piece
     * @param M Maximum width of the piece
     * @param Shadow 2D array representing the piece shadow (maximum rectangle containing the piece)
     * @param errorMsg Error message if the piece is invalid
     */
    public Piece(int N, int M, char[][] Shadow, String errorMsg) 
    {
        this.N = N;
        this.M = M;
        this.Shadow = Shadow;
        this.errorMsg = errorMsg;
    }

    public int getHeight() {return this.N;}
    public int getWidth() {return this.M;}
    public char[][] getShadow() {return this.Shadow;}
    public String getErrorMsg() {return this.errorMsg;}

    public char getPart(int i, int j) 
    {
        return this.Shadow[i][j];
    }

    public void setPart(int i, int j, char character) 
    {
        this.Shadow[i][j] = character;
    }

    /**
     * Overrides the equals method to compare two Object instances of Piece.
     * 
     * @param o Object to compare
     * @return True if the piece is equal to the object, false otherwise
     */
    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (!(o instanceof Piece)) return false;
        Piece other = (Piece) o;
        return this.N == other.N  && this.M == other.M && Arrays.deepEquals(this.Shadow, other.Shadow);
    }

    /**
     * Overrides the hashCode method to return the hash code of the piece.
     * 
     * @return Hash code of the piece
     */
    @Override
    public int hashCode() 
    {
        return 31 * (31 * N + M) + Arrays.deepHashCode(Shadow);
    }
    
    /**
     * Checks if the character is an uppercase letter.
     * 
     * @param c Character to check
     * @return True if the character is an uppercase letter, false otherwise
     */
    public static boolean isUppercase(char c) 
    {
        return c >= 'A' && c <= 'Z';
    }

    /**
     * Gets the letter that represents the puzzle piece or other special characters if invalid.
     * 
     * @param a The first string to compare
     * @param b The second string to compare
     * @return The letter that represents the puzzle piece or '?' if the piece is invalid or '#' if moving to the next piece
     */
    public static char getLetter(String a, String b)
    {
        char aChar = '?';
        for (int i = 0; i < a.length(); i++) 
        {
            if (isUppercase(a.charAt(i))) 
            {
                if (aChar != '?' && aChar != a.charAt(i)) return '?';
                else aChar = a.charAt(i);
            }
            else if (a.charAt(i) != ' ') return a.charAt(i);
        }

        if (a == b) return aChar;

        char bChar = '?';
        for (int i = 0; i < b.length(); i++) 
        {
            if (isUppercase(b.charAt(i))) 
            {
                if (bChar != '?' && bChar != b.charAt(i)) return '?';
                else bChar = b.charAt(i);
            }
            else if (b.charAt(i) != ' ') return b.charAt(i);
        }

        if (aChar == bChar) return aChar;
        else return '\0';
    }

    /**
     * Creates a Piece object from the provided parameters.
     * 
     * @param Piece List of strings representing the puzzle piece
     * @param letter Letter representing the puzzle piece
     * @return Piece object
     */
    public static Piece createOnePiece(List<String> Piece, char letter)
    {
        int N = Piece.size();
        int M = -1;

        for (String line : Piece) 
        {
            M = Math.max(M, line.length());
        }

        char[][] Shadow = new char[N][M];

        for (int i = 0; i < N; i++) 
        {
            String line = Piece.get(i);
            for (int j = 0; j < M; j++) 
            {
                if (j < line.length()) 
                {
                    if (line.charAt(j) == letter) Shadow[i][j] = letter;
                    else Shadow[i][j] = '#';
                }
                else Shadow[i][j] = '#';
            }
        }

        return new Piece(N, M, Shadow, null);
    }

    /**
     * Creates an array of Piece objects from the provided parameters.
     * 
     * @param P Number of pieces
     * @param Pieces List of strings representing the puzzle pieces
     * @return Array of Piece objects
     */
    public static Piece[] createPieces(int P, ArrayList<String> Pieces) 
    {   
        int index = -1;
        Piece[] pieces = new Piece[P];
        Set<Character> unique = new HashSet<>();
    
        for (int start = 0; start < Pieces.size();) 
        {
            int end = start;

            // Continue until we find a different letter or a non-uppercase character.
            while (end < Pieces.size()) 
            {
                char letter = getLetter(Pieces.get(start), Pieces.get(end));

                if (letter == '?') 
                {
                    return new Piece[] { new Piece(-1, -1, null, "Found different letters in the same piece") };
                }
                else if (letter == '\0') 
                {
                    break;
                }
                else if (!isUppercase(letter))
                {
                    return new Piece[] { new Piece(-1, -1, null, "Found invalid character for pieces: '" + letter + "'")};
                }
                end++;
            }

            char pieceLetter = getLetter(Pieces.get(start), Pieces.get(end - 1));

            // Check if we have duplicate pieces with the same letter.
            if (unique.contains(pieceLetter)) 
            {
                return new Piece[] { new Piece(-1, -1, null, "There are duplicate pieces. Found letter '" + pieceLetter + "'' twice") };
            }
            unique.add(pieceLetter);
            
            // Check if we have too many pieces.
            index++;

            if (index + 1 > P) 
            {
                return new Piece[] { new Piece(-1, -1, null, "There are too many pieces. Found " + (index + 1) + " pieces instead of " + P) };
            }
            
            // The sublist Pieces[start, end) represents one piece.
            pieces[index] = createOnePiece(Pieces.subList(start, end), pieceLetter);

            // Advance 'start' to the next group.
            start = end;
        }
        
        // Check if we have too few pieces.
        if (index + 1 < P) 
        {
            return new Piece[] { new Piece(-1, -1, null, "There are too few pieces. Found " + (index + 1) + " pieces instead of " + P) };
        }
        
        return pieces;
    }

    /**
     * Counts the total number of cells for each pieces parts.
     * 
     * @param pieces Array of pieces
     * @return Total number of cells in the pieces
     */
    public static int sumOfCells(Piece[] pieces) 
    {
        int count = 0;
        for (Piece piece : pieces) 
        {
            for (int i = 0; i < piece.getHeight(); i++) 
            {
                for (int j = 0; j < piece.getWidth(); j++) 
                {
                    if (piece.getPart(i, j) != '#') count++;
                }
            }
        }
        return count;
    }

    /**
     * Rotates the piece 90 degrees clockwise.
     * 
     * @return New Piece object representing the rotated piece
     */
    public Piece rotate() 
    {
        char[][] rotated = new char[getWidth()][getHeight()];

        for (int i = 0; i < getHeight(); i++) 
        {
            for (int j = 0; j < getWidth(); j++) 
            {
                rotated[j][getHeight() - i - 1] = getPart(i, j);
            }
        }

        return new Piece(getWidth(), getHeight(), rotated, null);
    }

    /**
     * Flips the piece horizontally.
     * 
     * @return New Piece object representing the flipped piece
     */
    public Piece flip() 
    {
        char[][] flipped = new char[getHeight()][getWidth()];

        for (int i = 0; i < getHeight(); i++) 
        {
            for (int j = 0; j < getWidth(); j++) 
            {
                flipped[i][j] = getPart(i, getWidth() - j - 1);
            }
        }

        return new Piece(this.N, this.M, flipped, null);
    }

    /**
     * Generates all unique permutations of a Piece.
     * 
     * @param Pieces Array of pieces
     * @return Array of Piece objects representing all unique permutations of the input pieces
     */
    public static Piece[][] uniquePermutations(Piece[] Pieces) 
    {
        Piece[][] result = new Piece[Pieces.length][];
        
        for (int i = 0; i < Pieces.length; i++) 
        {
            Set<Piece> uniqueSet = new HashSet<>();
            Piece current = Pieces[i];
            
            for (int j = 0; j < 4; j++) 
            {
                uniqueSet.add(current);
                uniqueSet.add(current.flip());
                current = current.rotate();
            }

            result[i] = uniqueSet.toArray(new Piece[0]);
        }
        return result;
    }
    
    /**
     * Prints the piece to the console. (For debugging purposes)
     */
    public void print() 
    {
        for (int i = 0; i < this.N; i++) 
        {
            for (int j = 0; j < this.M; j++) 
            { 
                System.out.print(getPart(i, j));
            }
            System.out.println();
        }
    }

}
