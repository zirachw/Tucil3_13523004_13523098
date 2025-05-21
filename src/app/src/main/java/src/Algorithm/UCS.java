package src.Algorithm;
import src.ADT.*;

import java.util.*;

public class UCS extends Algorithm 
{    
    public UCS(Board board) 
    {
        super(board);
    }

    @Override
    public List<int[]> solve(String heuristic) 
    {
        // UCS doesn't use heuristics, ignore the parameter
        return solveUCS();
    }

    private List<int[]> solveUCS() 
    {
        long startTime = startTiming();
        
        PriorityQueue<State> queue = new PriorityQueue<>(Comparator.comparingInt(s -> s.getGValue()));
        List<int[]> initialMoves = new ArrayList<>();
        
        // UCS only uses g(n) - the cost from start to current node
        queue.add(new State(initialBoard, initialMoves));
        
        while (!queue.isEmpty())
        {
            State curState = queue.poll();
            Board curBoard = curState.getBoard();
            List<Car> pieces = curBoard.getCars();
            List<int[]> curMoves = curState.getMoves();
            incrementNodesExplored();

            if (curBoard.isSolved())
            {
                endTiming(startTime);

                // if (curMoves.size() < 100) return splitMovesToSteps(curMoves);
                return curMoves;
            }
            
            if (hasBeenVisited(curBoard)) continue;
            addToVisited(curBoard);
            
            for (int i = 0; i < pieces.size(); i++)
            {
                List<Integer> validMoves = curBoard.getValidMoves(i);
                for(Integer moveAmount : validMoves)
                {
                    Board newBoard = curBoard.copy();
                    newBoard = newBoard.applyMove(i, moveAmount);
                    
                    if (!hasBeenVisited(newBoard))
                    {
                        List<int[]> newMoves = new ArrayList<>(curMoves);
                        newMoves.add(new int[]{i, moveAmount});
                        
                        // UCS: Create new state with updated moves
                        queue.add(new State(newBoard, newMoves));
                    }
                }
            }
        }

        endTiming(startTime);
        return new ArrayList<>();
    }
}