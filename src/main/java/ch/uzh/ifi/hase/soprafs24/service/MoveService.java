package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.constant.MoveType;
import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.entity.Board;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// This code is not finished at all, needs to be logically analysed again and tested
/*  Assumptions for this code, maybe be changed later

The winning condition will be implemented as just reaching any column in the goal row
Creator starts from the top field and moves down to win
The user that joins starts from the bottom and moves up to win
This code will for now just check if there is a path and not provide a shortest path
A findShortestPath() will be implemented later
Boardsize is assumed to be just taken from the the boardrepository... but now we changed it to 17*17 (=0...16 * 0*16) for 9*9, have to think how to change that
Walls are assumed to be placed in the middle of their tracks
*/



@Service
public class MoveService {

    private final Logger log = LoggerFactory.getLogger(MoveService.class);

    //checks if a field is reachable
    public boolean hasPathToGoal(Game game, Board board, Pawn pawn, List<Wall> walls) {
        log.error("Checking path to goal for pawn at r={}, c={}", pawn.getR(), pawn.getC());
        
        int boardSize = board.getSizeBoard();
        int startR = pawn.getR();
        int startC = pawn.getC();
        int goalR = getGoalRow(game, board, pawn);
        
        log.error("Goal row: {}", goalR);
        
        boolean[][] visited = new boolean[boardSize][boardSize];
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startR, startC});
        visited[startR][startC] = true;
    
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int r = current[0];
            int c = current[1];
    
            log.error("Checking position r={}, c={}", r, c);
            
            // Winning Condition
            if (r == goalR) {
                log.error("Found path to goal!");
                return true;
            }
    
            // Check all possible moves
            int[][] directions = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};
            for (int[] dir : directions) {  
                int newR = r + dir[0];
                int newC = c + dir[1];
    
                if (newR >= 0 && newR < boardSize &&
                    newC >= 0 && newC < boardSize) {
                    
                    if (isValidPawnMoveHasPath(board, pawn, newR, newC, r, c,walls) && 
                        !visited[newR][newC]) {
                        visited[newR][newC] = true;
                        queue.add(new int[]{newR, newC});
                    }
                }
            }
        }
        
        log.error("No path found to goal!");
        return false;
    }



    // Fixin scope issues by adding a new function
    // Winning condition by getGoalRow
    public int getGoalRow(Game game, Board board, Pawn pawn) {
        int boardSize = board.getSizeBoard();
        if (pawn.getUser().getId() == game.getCreator().getId()) {
            return boardSize - 1;
        } else {
            return 0;
        }
    }

        // Validate move
public boolean isValidPawnMoveHasPath(Board board, Pawn pawn, int targetR, int targetC, int startR , int startC, List<Wall> walls) {
    
        // Check if the target position is within the board boundaries
            if (!isValidPawnField(board, pawn, targetR, targetC)) {
                return false;
            }
    
            // Check if the target position is adjacent to the current position
            if (!((Math.abs(targetR - startR) == 2 && Math.abs(targetC - startC) == 0) ||
                (Math.abs(targetR - startR) == 0 && Math.abs(targetC - startC) == 2))) {
                // if (!isValidJumpMove(board, pawn, targetR, targetC, walls)) {
                //     return false;
                // }
                return false;
            }
            // Check if the target position is blocked by walls
            if (isWallBlockingPath(startR, startC, targetR, targetC, walls)) {
                return false;
                
            }
    
            // if (!isValidJumpMove(board, pawn, targetR, targetC, walls)) {
            //     return false;
            // }
            return true;
            }


    // Validate move
    public boolean isValidPawnMove(Board board, Pawn pawn, int targetR, int targetC, List<Wall> walls) {
        int startR = pawn.getR();
        int startC = pawn.getC();

        // Check if the target position is within the board boundaries
        if (!isValidPawnField(board, pawn, targetR, targetC)) {
            return false;
        }

        // Check if the target position is adjacent to the current position
        if (!((Math.abs(targetR - startR) == 2 && Math.abs(targetC - startC) == 0) ||
            (Math.abs(targetR - startR) == 0 && Math.abs(targetC - startC) == 2))) {
            // if (!isValidJumpMove(board, pawn, targetR, targetC, walls)) {
            //     return false;
            // }
            return false;
        }

        // Check if the target position is occupied by another pawn
        for (Pawn otherPawn : board.getPawns()) {
            if (otherPawn.getR() == targetR &&
                otherPawn.getC() == targetC) {
                return false;
            }
        }

        // Check if the target position is blocked by walls
        if (isWallBlockingPath(startR, startC, targetR, targetC, walls)) {
            return false;
            
        }

        // if (!isValidJumpMove(board, pawn, targetR, targetC, walls)) {
        //     return false;
        // }
        return true;
        }

    



    /**
     * 
     * Helper functions
     * 
     */


    
    // Check if a wall is blocking the path between two positions
    // For new board, wall are checked as the intersection of the two positions
    private boolean isWallBlockingPath(int startR, int startC, int targetR, int targetC, List<Wall> walls) {
        // Moving right
        if (startR == targetR && startC + 2 == targetC) {
            for (Wall wall : walls) {
                if (wall.getOrientation() == WallOrientation.VERTICAL && 
                    Math.abs(startR - wall.getR()) <= 1 && wall.getC() == startC + 1) {
                    return true;
                }
            }
        } 
        // Moving left
        else if (startR == targetR && startC - 2 == targetC) {
            for (Wall wall : walls) {
                if (wall.getOrientation() == WallOrientation.VERTICAL && 
                    Math.abs(startR - wall.getR()) <= 1 && wall.getC() == targetC - 1) { 
                    return true;
                }
            }
        }
        // Moving down
        else if (startC == targetC && startR + 2 == targetR) {
            for (Wall wall : walls) {
                if (wall.getOrientation() == WallOrientation.HORIZONTAL && 
                    Math.abs(startC - wall.getC()) <= 1 && wall.getR() == startR + 1) { 
                    return true;
                }
            }
        }
        // Moving up
        else if (startC == targetC && startR - 2 == targetR) {
            for (Wall wall : walls) {
                if (wall.getOrientation() == WallOrientation.HORIZONTAL && 
                    Math.abs(startC - wall.getC()) <= 1 && wall.getR() == startR - 1) { 
                    return true;
                }
            }
        }
        
        return false;
    }

    // Maybe will put this one in the GameService later
    // Check if a wall would block all paths for at least one player
    // this results in walls beign unable to be placed for uneven numbers 
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


    // // Checks if a jump move is valid
    // private boolean isValidJumpMove(Board board, Pawn pawn, int targetR, int targetC, List<Wall> walls) {
    //     int startR = pawn.getR();
    //     int startC = pawn.getC();
        
    //     // First, check if there's a pawn adjacent to the current pawn
    //     Pawn adjacentPawn = null; // For scope
        
    //     // Possible adjacent positions: up, down, left, right
    //     int[][] adjacentDirections = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
    //     for (int[] dir : adjacentDirections) {
    //         int adjR = startR + dir[0];
    //         int adjC = startC + dir[1];
            
    //         // Check if position is within board and contains a pawn
    //         if (adjR >= 0 && adjR < board.getSizeBoard() &&
    //             adjC >= 0 && adjC < board.getSizeBoard()) {
    //             for (Pawn otherPawn : board.getPawns()) {
    //                 if (otherPawn.getR() == adjR && otherPawn.getC() == adjC) {
    //                     adjacentPawn = otherPawn;
                        
    //                     // Check if the jump would be in the direction of this adjacent pawn
    //                     int jumpR = adjR + dir[0];
    //                     int jumpC = adjC + dir[1];
                        
    //                     // If the jump target matches our target position
    //                     if (jumpR == targetR && jumpC == targetC) {
    //                         // Check if there's a wall blocking the jump
    //                         if (isWallBlockingPath(adjR, adjC, jumpR, jumpC, walls)) {
    //                             // If jump is blocked by wall, check if diagonal moves are possible
    //                             return isValidDiagonalJump(board, pawn, adjR, adjC, walls);
    //                         }
    //                         return true; // Valid jump move
    //                     }
    //                 }
    //             }
    //         }
    //     }
        
    //     return false;
    // }
    
    // private boolean isValidDiagonalJump(Board board, Pawn pawn, int adjR, int adjC, List<Wall> walls) {
    //     int startR = pawn.getR();
    //     int startC = pawn.getC();
        
    //     // Calculate the direction from start to adjacent pawn
    //     int dirR = adjR - startR;
    //     int dirC = adjC - startC;
        
    //     // Possible diagonal directions based on the adjacent pawn position
    //     int[][] diagonalOptions = {
    //         {dirR, dirC + 2},  // Diagonal right from direction
    //         {dirR, dirC - 2},  // Diagonal left from direction
    //         {dirR + 2, dirC},  // Diagonal down from direction
    //         {dirR - 2, dirC}   // Diagonal up from direction
    //     };
        
    //     for (int[] diag : diagonalOptions) {
    //         int diagR = startR + diag[0];
    //         int diagC = startC + diag[1];
            
    //         // Check if diagonal position is within board and not occupied
    //         if (diagR >= 0 && diagR < board.getSizeBoard() && diagC >= 0 && diagC < board.getSizeBoard()) {
    //             boolean isOccupied = false;
    //             for (Pawn otherPawn : board.getPawns()) {
    //                 if (otherPawn.getR() == diagR && otherPawn.getC() == diagC) {
    //                     isOccupied = true;
    //                     break;
    //                 }
    //             }
                
    //             if (!isOccupied && !isWallBlockingPath(startR, startC, diagR, diagC, walls)) {
    //                 return true; // Valid diagonal jump
    //             }
    //         }
    //     }
        
    //     return false; // No valid diagonal jump found
    // }

    //Check if pawn field
    public boolean isValidPawnField(Board board, Pawn pawn, int targetR, int targetC) {
        // Check if the target position is within the board boundaries
        if (targetR < 0 || targetR >= board.getSizeBoard() ||
            targetC < 0 || targetC >= board.getSizeBoard()) {
            return false;
        }

        //Check if even field
        if (targetR % 2 != 0 || targetC % 2 != 0) {
            return false;
        }
        return true;
    }

    //Check if wall field
    public boolean isValidWallField(Board board, int r, int c) {
        // Check if the target position is within the board boundaries
        if (r < 0 || r >= board.getSizeBoard() ||
            c < 0 || c >= board.getSizeBoard()) {
            return false;
        }

        //Check if odd field
        if ( r % 2 == 0 || c % 2 == 0) {
            return false;
        }
        return true;
    }
}