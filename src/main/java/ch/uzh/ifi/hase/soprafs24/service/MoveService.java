package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.entity.Board;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public boolean hasPathToGoal(Game game, Board board, Pawn pawn, int startR, int startC, List<Wall> walls) {
        log.error("Checking path to goal for pawn at r={}, c={}", startR, startC);
        
        int boardSize = board.getSizeBoard();
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

                // Skip if out of bounds
                if (newR < 0 || newR >= boardSize || newC < 0 || newC >= boardSize) {
                    continue;
                }
                
                // Skip if already visited
                if (visited[newR][newC]) {
                    continue;
                }

                // Check if there's a pawn at the target position
                boolean pawnAtTarget = false;
                for (Pawn otherPawn : board.getPawns()) {
                    if (otherPawn.getR() == newR && otherPawn.getC() == newC) {
                        pawnAtTarget = true;
                        
                        // Try to jump over this pawn
                        int jumpR = newR + dir[0];
                        int jumpC = newC + dir[1];
                        
                        // Check if jump is valid (within bounds and not blocked by wall)
                        if (jumpR >= 0 && jumpR < boardSize && 
                            jumpC >= 0 && jumpC < boardSize && 
                            !visited[jumpR][jumpC] &&
                            !isWallBlockingPath(newR, newC, jumpR, jumpC, walls)) {
                            
                            visited[jumpR][jumpC] = true;
                            queue.add(new int[]{jumpR, jumpC});
                            log.error("Jumped to and added to queue r={}, c={}", jumpR, jumpC);
                        } 
                        // If can't jump straight, try diagonal jumps
                        else if (isWallBlockingPath(newR, newC, jumpR, jumpC, walls)) {
                            // Try diagonal jumps (perpendicular to the move direction)
                            // For vertical movement, try horizontal diagonals
                            if (dir[0] != 0) {
                                tryDiagonalJump(r, c, newR, newC, newR, newC + 2, boardSize, visited, queue, walls);
                                tryDiagonalJump(r, c, newR, newC, newR, newC - 2, boardSize, visited, queue, walls);
                            }
                            // For horizontal movement, try vertical diagonals
                            else {
                                tryDiagonalJump(r, c, newR, newC, newR + 2, newC, boardSize, visited, queue, walls);
                                tryDiagonalJump(r, c, newR, newC, newR - 2, newC, boardSize, visited, queue, walls);
                            }
                        }
                        break;
                    }
                }

                // If no pawn and move is valid, add to queue
                if (!pawnAtTarget && !isWallBlockingPath(r, c, newR, newC, walls)) {
                    visited[newR][newC] = true;
                    queue.add(new int[]{newR, newC});
                    log.error("Added to queue r={}, c={}", newR, newC);
                }
            }
        }
        
        log.error("No path found to goal!");
        return false;
    }

    // Helper method for diagonal jumps
    private void tryDiagonalJump(int startR, int startC, int pawnR, int pawnC, int diagR, int diagC, 
                            int boardSize, boolean[][] visited, Queue<int[]> queue, List<Wall> walls) {
        // Check if diagonal position is valid
        if (diagR >= 0 && diagR < boardSize && 
            diagC >= 0 && diagC < boardSize && 
            !visited[diagR][diagC] && 
            !isWallBlockingPath(pawnR, pawnC, diagR, diagC, walls)) {
            
            visited[diagR][diagC] = true;
            queue.add(new int[]{diagR, diagC});
            log.error("Added diagonal jump to queue r={}, c={}", diagR, diagC);
        }
    }



    // Fixin scope issues by adding a new function
    // Winning condition by getGoalRow
    public int getGoalRow(Game game, Board board, Pawn pawn) {
        int boardSize = board.getSizeBoard();
        if (pawn.getUserId() == game.getCreator().getId()) { // PAWN ID IS NOT EQUAL TO USER ID - unfortunately
            return boardSize - 1;
        } else {
            return 0;
        }
    }

//  Even though redundant, needed to not make cicular dependencies
    public boolean isValidPawnMoveHasPath(Board board, Pawn pawn, int targetR, int targetC, int startR , int startC, List<Wall> walls) {
    
        // Check if the target position is within the board boundaries
        if (!isValidPawnField(board, pawn, targetR, targetC)) {
            return false;
        }

        //Check first if occupied so the returning true or false logic is correct
        // Check if the target position is occupied by another pawn
        for (Pawn otherPawn : board.getPawns()) {
            if (otherPawn.getR() == targetR &&
                otherPawn.getC() == targetC) {
                return false;
            }
        }

        
        // Check if the target position is adjacent to the current position
        if (((Math.abs(targetR - startR) == 2 && Math.abs(targetC - startC) == 0) ||
            (Math.abs(targetR - startR) == 0 && Math.abs(targetC - startC) == 2))) {
                  // Check if the target position is blocked by walls
            if (isWallBlockingPath(startR, startC, targetR, targetC, walls)) {
                return false;

            }
            return true;
            
        } else if (isValidJumpMove(board, pawn, targetR, targetC, walls)) {
            log.error("start isValidJumpMove");
            return true;
        } else {
            log.error("start isValidDiagonalJump");
            return isValidDiagonalJump(board, pawn, targetR, targetC, walls);

        }

    }
 

    // Validate move
    public boolean isValidPawnMove(Board board, Pawn pawn, int targetR, int targetC, List<Wall> walls) {
        int startR = pawn.getR();
        int startC = pawn.getC();

        // Check if the target position is within the board boundaries
        if (!isValidPawnField(board, pawn, targetR, targetC)) {
            return false;
        }

        //Check first if occupied so the returning true or false logic is correct
        // Check if the target position is occupied by another pawn
        for (Pawn otherPawn : board.getPawns()) {
            if (otherPawn.getR() == targetR &&
                otherPawn.getC() == targetC) {
                return false;
            }
        }

        // Check if the target position is adjacent to the current position
        if (((Math.abs(targetR - startR) == 2 && Math.abs(targetC - startC) == 0) ||
            (Math.abs(targetR - startR) == 0 && Math.abs(targetC - startC) == 2))) {

            // Check if the target position is blocked by walls
            if (isWallBlockingPath(startR, startC, targetR, targetC, walls)) {
                return false;
            
            }

            return true;
        } else if (isValidJumpMove(board, pawn, targetR, targetC, walls)) {
            log.error("start isValidJumpMove");
            return true;
        } else {
            log.error("start isValidDiagonalJump");
            return isValidDiagonalJump(board, pawn, targetR, targetC, walls);

        }

    }
    


    /**
     * 
     * Helper functions
     * 
     */


    
    // Check if a wall is blocking the path between two positions
    // For new board, wall are checked as the intersection of the two positions
    private boolean isWallBlockingPath(int startR, int startC, int targetR, int targetC, List<Wall> walls) {
        // Calculate the wall position that would block this move
        int wallR = (startR + targetR) / 2;
        int wallC = (startC + targetC) / 2;
        
        // Check if a horizontal or vertical wall exists at this position
        for (Wall wall : walls) {
            // For a move in the vertical direction (row changes)
            if (startC == targetC) {
                if (wall.getOrientation() == WallOrientation.HORIZONTAL && 
                    wall.getR() == wallR && 
                    Math.abs(wall.getC() - wallC) <= 2) {
                    return true;
                }
            }
            // For a move in the horizontal direction (column changes)
            else if (startR == targetR) {
                if (wall.getOrientation() == WallOrientation.VERTICAL && 
                    wall.getC() == wallC && 
                    Math.abs(wall.getR() - wallR) <= 2) {
                    return true;
                }
            }
        }
        return false;
    }

 
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
            int pawnR = pawn.getR();
            int pawnC= pawn.getC();
            if (!hasPathToGoal(game, board, pawn, pawnR, pawnC, wallsWithNew)) {
                return true;
            }
        }
        
        return false; 
    }


    // Checks if a jump move is valid
    private boolean isValidJumpMove(Board board, Pawn pawn, int targetR, int targetC, List<Wall> walls) {
        log.error("Checkpoint: isValidJumpMove started");
        int startR = pawn.getR();
        int startC = pawn.getC();
        
        // Possible adjacent positions: up, down, left, right
        int[][] adjacentDirections = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        for (int[] dir : adjacentDirections) {
            int adjR = startR + dir[0];
            int adjC = startC + dir[1];
            log.error("isValidJumpMove adjacent check adjR={}, adjC={}", adjR, adjC);
            // Check if position is within board and contains a pawn
            if (adjR >= 0 && adjR < board.getSizeBoard() &&
                adjC >= 0 && adjC < board.getSizeBoard()) {

                // First, check if there's a pawn adjacent to the current pawn
                Pawn adjacentPawn = null; // For scope
                for (Pawn otherPawn : board.getPawns()) {
                    if (otherPawn.getR() == adjR && otherPawn.getC() == adjC) {
                        adjacentPawn = otherPawn;
                        break;
                    }
                }

                if (adjacentPawn != null) {
                    log.error("isValidJumpMove  found adjacent: adjR={}, adjC={}", adjR, adjC);
                    // Check if the jump would be in the direction of this adjacent pawn
                    int jumpR = adjR + dir[0];
                    int jumpC = adjC + dir[1];
                    log.error("isValidJumpMove  jump check: jumpR={}, jumpC={}", jumpR, jumpC);
                    // If the jump target matches our target position
                    if (jumpR == targetR && jumpC == targetC) {
                        log.error("isValidJumpMove jump equals target: jumpR={}, jumpC={}", jumpR, jumpC);
                        // Check if there's a wall blocking the jump
                        if (isWallBlockingPath(adjR, adjC, jumpR, jumpC, walls)) {
                            log.error("isValidJumpMove  found wall: jumpR={}, jumpC={}", jumpR, jumpC);
                            // Do not check here if diagonal jumps are possible, otherwise wall check does not make sense
                            return false; 
                        

                        } else {
                            log.error("isValidJumpMove  returns true: jumpR={}, jumpC={}", jumpR, jumpC);
                            return true;
                        }
                    }
                }
            }
        }
        
        return false;
    }
    
    private boolean isValidDiagonalJump(Board board, Pawn pawn, int targetR, int targetC, List<Wall> walls) {
        log.error("Checkpoint: isDiagonalJump started");
        int startR = pawn.getR();
        int startC = pawn.getC();

        int[][] adjacentDirections = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        for (int[] dir : adjacentDirections) {
            int adjR = startR + dir[0];
            int adjC = startC + dir[1];
            log.error("isDiagonalJump adjacent check adjR={}, adjC={}", adjR, adjC);
            // Check if position is within board and contains a pawn
            if (adjR >= 0 && adjR < board.getSizeBoard() &&
                adjC >= 0 && adjC < board.getSizeBoard()) {
                
                Pawn adjacentPawn = null; // For scope
                for (Pawn otherPawn : board.getPawns()) {
                    if (otherPawn.getR() == adjR && otherPawn.getC() == adjC) {
                        adjacentPawn = otherPawn;
                        break;
                    }
                }

                if (adjacentPawn != null) {
                    log.error("isDiagonalJump adjacent found adjR={}, adjC={}", adjR, adjC);
                    int prohibitedR = adjR +  dir[0];
                    int prohibitedC = adjC +  dir[1];
                
                    if (!isValidPawnField(board, pawn, prohibitedR, prohibitedC)) {
                        log.error("isDiagonalJump, normal jump would be outofbounds probhibitedR={}, prohibitedC={}", prohibitedR, prohibitedC);
                        return false; // Should do normal Jump
                    }
                    if (!isWallBlockingPath(adjR, adjC, prohibitedR, prohibitedC, walls)) {
                        log.error("isDiagonalJump normal Jump possible prohibitedR={}, prohibitedC={}", prohibitedR, prohibitedC);
                        return false; // Should do normal Jump
                    }


/*                     // Calculate the direction from start to adjacent pawn
                    int dirR = targetR - adjR;
                    int dirC = targetC - adjC;
                     */
                    int[][] diagonalOptions = {{2, 2}, {2, -2}, {-2, 2}, {-2, -2}};
                    for (int[] diag : diagonalOptions) {
                        int diagR = startR + diag[0];
                        int diagC = startC + diag[1];
                        log.error("isDiagonalJump normal Jump possible diagR={}, diagC={}", diagR, diagC);
                        // Check if diagonal position is within board and not occupied
                        if (diagR == targetR && diagC == targetC &&
                            !isWallBlockingPath(adjR, adjC, diagR, diagC, walls)) {
                                return true;
                        }
                    } 
                }
            }
        }
        
        return false; // No valid diagonal jump found
    }

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