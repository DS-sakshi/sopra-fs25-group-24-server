package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.MoveType;
import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.entity.Board;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

// This code is not finished at all, needs to be logically analysed again and tested
/*  Assumptions for this code, maybe be changed later

The winning condition will be implemented as just reaching any column in the goal row
Creator starts from the top field and moves down to win
The user that joins starts from the bottom and moves up to win
This code will for now just check if there is a path and not provide a shortest path
A findShortestPath() will be implemented later

*/

public class findPath {
    
    //checks if a field is reachable
    public boolean isReachable(Board board, Pawn pawn, int startR, int startC, int goalR, List<Wall> walls) {
        int boardSize = board.getSizeBoard();
        boolean[][] visited = new boolean[boardSize][boardSize];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startR, startC});
        visited[startR][startC] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0];
            int c = current[1];

            // Winning Condition
            if (r == goalR) {
                return true;
            }

            // Check all possible moves
            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0} };
            for (int[] dir : directions) {  
                int newR = r + dir[0];
                int newC = c + dir[1];

                if (newR >= 0 && newR < boardSize && newC >= 0 && newC < boardSize) {
                    
                    if (isValidPawnMove(board, pawn, newR, newC, walls) && !visited[newR][newC]) {
                        visited[newR][newC] = true;
                        queue.add(new int[]{newR, newC});
                    }
                }
            }
        }
        return false;
    }


    // Check if there is a path to the goal
    public boolean hasPathToGoal(Game game, Board board, Pawn pawn, List<Wall> walls) {
        int boardSize = board.getSizeBoard();
        int goalR; // TODO: scope issues here - Dora
        if (pawn.getUser().getId() == game.getCreator().getId()) {
            int goalR = boardSize -1;
        } else {
            int goalR = 0;
        }
        if (isReachable(board, pawn, pawn.getR(), pawn.getC(), goalR, walls )) {return true;}
        return false;
    }
 


    // Implement the logic to validate the move
    public boolean isValidPawnMove(Board board, Pawn pawn, int targetR, int targetC, List<Wall> walls) {
        int startR = pawn.getR();
        int startC = pawn.getC();

        // Check if the target position is within the board boundaries
        if (targetR < 0 || targetR >= board.getSizeBoard() || targetC < 0 || targetC >= board.getSizeBoard()) {
            return false;
        }

        // Check if the target position is adjacent to the current position
        if (!((Math.abs(targetR - startR) == 1 && Math.abs(targetC - startC) == 1) || (Math.abs(targetR - startR) == 0 && Math.abs(targetC - startC) == 1))) {
            return false;
        }

        // Check if the target position is occupied by another pawn
        for (Pawn otherPawn : board.getPawns()) {
            if (otherPawn.getR() == targetR && otherPawn.getC() == targetC) {
                return false;
            }
        }

        // Check if the target position is blocked by walls
        if (isWallBlockingPath(startR, startC, targetR, targetC, walls)) {
            return false;
            
        }

        // TODO: check bc of jumping
        return true;
        }

    



    /**
     * 
     * Helper functions
     * 
     */


    
    // Check if a wall is blocking the path between two positions
    // The walls are on the upper left corner of a field
    private boolean isWallBlockingPath(int startR, int startC, int targetR, int targetC, List<Wall> walls) {
        for (Wall wall : walls) {
            if (wall.getOrientation() == WallOrientation.HORIZONTAL) {
                //Moves up
                if (startR == wall.getR() && targetR > wall.getR() && (Math.abs(startC - wall.getC()) <= 1 )){
                    return true;
                    //moves down
                } else if (startR > wall.getR() && targetR == wall.getR() && (Math.abs(startC - wall.getC()) <= 1 )) {
                    return true;
                }
                 
            } else  {
                // Moves right
                if (startC < wall.getC() && targetC == wall.getC() && (Math.abs(startR- wall.getR()) <= 1 )) {
                    return true;
                // Moves left
                } else if (startC == wall.getC() && targetC < wall.getC() && (Math.abs(startR- wall.getR()) <= 1 )){
                    return true;
                
                }
            }
        }
        
        return false;
    }


    // Maybe will put this one in the GameService later
    // Check if a wall would block all paths for at least one player
    public boolean wouldBlockAllPaths(Game game, Board board, List<Wall> existingWalls, int r, int c, WallOrientation orientation) {
        Wall tempWall = new Wall(); // Creates a temporary wall and adds it to the list of walls
        tempWall.setR(r);
        tempWall.setC(c);
        tempWall.setOrientation(orientation);
        
        //Creates temp list and checks if it works with new wall
        List<Wall> wallsWithNew = new ArrayList<>(existingWalls);
        wallsWithNew.add(tempWall);
        
        for (Pawn pawn : board.getPawns()) {
            if (!hasPathToGoal(game, board, pawn, wallsWithNew)) {
                return true;
            }
        }
        
        return false; 
    }
}