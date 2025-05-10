// package ch.uzh.ifi.hase.soprafs24.service;

// import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
// import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
// import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
// import ch.uzh.ifi.hase.soprafs24.entity.Board;
// import ch.uzh.ifi.hase.soprafs24.entity.Game;
// import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
// import ch.uzh.ifi.hase.soprafs24.entity.User;
// import ch.uzh.ifi.hase.soprafs24.entity.Wall;
// import ch.uzh.ifi.hase.soprafs24.repository.BoardRepository;
// import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
// import ch.uzh.ifi.hase.soprafs24.repository.PawnRepository;
// import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
// import ch.uzh.ifi.hase.soprafs24.repository.WallRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// public class MoveServiceTest2 {
//     @Mock
//     private GameRepository gameRepository;

//     @Mock
//     private UserRepository userRepository;

//     @Mock
//     private BoardRepository boardRepository;

//     @Mock
//     private PawnRepository pawnRepository;

//     @Mock
//     private WallRepository wallRepository;

//     @InjectMocks
//     private MoveService moveService;

//     private User testUser;
//     private User testUser2;
//     private Game testGame;
//     private Board testBoard;
//     private Pawn testPawn1;
//     private Pawn testPawn2;
//     private List<Wall> testWalls;

//     @BeforeEach
//     public void setup() {
//         MockitoAnnotations.openMocks(this);

//         // Create first test user
//         testUser = new User();
//         testUser.setId(1L);
//         testUser.setName("testName");
//         testUser.setUsername("testUsername");
//         testUser.setStatus(UserStatus.ONLINE);
//         testUser.setCreationDate(new Date());

//         // Create second test user
//         testUser2 = new User();
//         testUser2.setId(2L);
//         testUser2.setName("testName2");
//         testUser2.setUsername("testUsername2");
//         testUser2.setStatus(UserStatus.ONLINE);
//         testUser2.setCreationDate(new Date());

//         // Create test board
//         testBoard = new Board();
//         testBoard.setId(1L);
//         testBoard.setSizeBoard(9); 

//         // Create test pawn
//         testPawn1 = new Pawn();
//         testPawn1.setId(1L);
//         testPawn1.setR(0);
//         testPawn1.setC(8);
//         testPawn1.setColor("red");
//         testPawn1.setUserId(testUser.getId());
//         testPawn1.setBoard(testBoard);

//         // Create second test pawn
//         testPawn2 = new Pawn();
//         testPawn2.setId(2L);
//         testPawn2.setR(16);
//         testPawn2.setC(8);
//         testPawn2.setColor("blue");
//         testPawn2.setUserId(testUser2.getId());
//         testPawn2.setBoard(testBoard);

//         List<Pawn> pawns = new ArrayList<>();
//         pawns.add(testPawn1);
//         pawns.add(testPawn2);
//         testBoard.setPawns(pawns);

//         // Create test walls list
//         testWalls = new ArrayList<>();
//         testBoard.setWalls(testWalls);

//         // Create test game
//         testGame = new Game();
//         testGame.setId(1L);
//         testGame.setNumberUsers(2);
//         testGame.setSizeBoard(9);
//         testGame.setCreator(testUser);
//         testGame.setCurrentTurn(testUser);
//         testGame.setGameStatus(GameStatus.RUNNING);
//         Set<User> userList = new HashSet<>();
//         userList.add(testUser2);
//         userList.add(testUser);
//         testGame.setCurrentUsers(userList);
//         testGame.setBoard(testBoard);
//     }

//     /**
//      * Tests for getGoalRow functionality
//      */
//     @Test
//     public void getGoalRow_creatorPawn_returnsBottomRow() {
//         // given
//         int boardSize = testBoard.getSizeBoard();
//         int expectedGoalRow = boardSize - 1;
        
//         // when
//         int actualGoalRow = moveService.getGoalRow(testGame, testBoard, testPawn1);
        
//         // then
//         assertEquals(expectedGoalRow, actualGoalRow);
//     }
    
//     @Test
//     public void getGoalRow_joinerPawn_returnsTopRow() {
//         // given
//         int expectedGoalRow = 0;
        
//         // when
//         int actualGoalRow = moveService.getGoalRow(testGame, testBoard, testPawn2);
        
//         // then
//         assertEquals(expectedGoalRow, actualGoalRow);
//     }
    
//     @Test
//     public void getGoalRow_differentBoardSize_returnsCorrectRow() {
//         // given
//         testBoard.setSizeBoard(7); // Change board size
//         int expectedGoalRow = 12; // New bottom row
        
//         // when
//         int actualGoalRow = moveService.getGoalRow(testGame, testBoard, testPawn1);
        
//         // then
//         assertEquals(expectedGoalRow, actualGoalRow);
//     }

//     /**
//      * Tests for jump moves
//      */
//     @Test
//     public void isValidPawnMove_jumpOverPawn_returnsTrue() {
//         // given
//         // Place pawn2 adjacent to pawn1
//         testPawn2.setR(2);
//         testPawn2.setC(8);
//         testPawn1.setR(0);
//         testPawn1.setC(8);
        
//         // when-then
//         // Try to jump over pawn2
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 4, 8, testWalls));
//     }
    
//     @Test
//     public void isValidPawnMove_jumpOverPawnBlocked_returnsFalse() {
//         // given
//         // Place pawn2 adjacent to pawn1
//         testPawn1.setR(0);
//         testPawn1.setC(8);
//         testPawn2.setR(2);
//         testPawn2.setC(8);

        
//         // Add a wall blocking the jump
//         Wall wall = new Wall();
//         wall.setR(3);
//         wall.setC(7);
//         wall.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall);
        
//         // when-then
//         // Try to jump over pawn2
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 4, 8, testWalls));
//     }
    
//     @Test
//     public void isValidPawnMove_diagonalJump_returnsTrue() {
//         // given
//         // Place pawn2 adjacent to pawn1
//         testPawn2.setR(2);
//         testPawn2.setC(8);
//         testPawn1.setR(0);
//         testPawn1.setC(8);
        
//         // Add a wall blocking the straight jump
//         Wall wall = new Wall();
//         wall.setR(3);
//         wall.setC(8);
//         wall.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall);
        
//         // when-then
//         // Try to jump diagonally (right)
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 2, 10, testWalls));
//     }
    
//     @Test
//     public void isValidPawnMove_diagonalJumpBlocked_returnsFalse() {
//         // given
//         // Place pawn2 adjacent to pawn1
//         testPawn1.setR(0);
//         testPawn1.setC(8);
//         testPawn2.setR(2);
//         testPawn2.setC(8);

        
//         // Add walls blocking both the straight jump and diagonal moves
//         Wall wall1 = new Wall();
//         wall1.setR(3);
//         wall1.setC(7);
//         wall1.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall1);
        
//         Wall wall2 = new Wall();
//         wall2.setR(1);
//         wall2.setC(9);
//         wall2.setOrientation(WallOrientation.VERTICAL);
//         testWalls.add(wall2);
        
//         // when-then
//         // Try to jump diagonally (right)
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 10, testWalls));
//     }
    
//     @Test
//     public void isValidPawnMove_diagonalJumpLeftBlocked_returnsFalse() {
//         // given
//         // Place pawn2 adjacent to pawn1
//         testPawn2.setR(2);
//         testPawn2.setC(8);
//         testPawn1.setR(0);
//         testPawn1.setC(8);
        
//         // Add walls blocking both the straight jump and left diagonal
//         Wall wall1 = new Wall();
//         wall1.setR(3);
//         wall1.setC(8);
//         wall1.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall1);
        
//         Wall wall2 = new Wall();
//         wall2.setR(1);
//         wall2.setC(7);
//         wall2.setOrientation(WallOrientation.VERTICAL);
//         testWalls.add(wall2);
        
//         // Allow the right diagonal
        
//         // when-then
//         // Try to jump diagonally (left) - should fail
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 6, testWalls));
        
//         // Try to jump diagonally (right) - should succeed
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 2, 10, testWalls));
//     }

//     /**
//      * Tests for wall placement and validation
//      */
//     @Test
//     public void wouldBlockAllPaths_completeHorizontalBarrier_returnsTrue() {
//         // given
//         // Create horizontal barriers across the board except for one gap
//         for (int c = 1; c < 14; c += 2) {
//             Wall wall = new Wall();
//             wall.setOrientation(WallOrientation.HORIZONTAL);
//             wall.setR(7);
//             wall.setC(c);
//             testWalls.add(wall);
//         }
        
//         // This wall would complete the barrier
//         int lastGapColumn = 15;
        
//         // when-then
//         assertTrue(moveService.wouldBlockAllPaths(testGame, testBoard, testWalls, 7, lastGapColumn, WallOrientation.HORIZONTAL));
//     }
    
//     @Test
//     public void wouldBlockAllPaths_incompleteBarrier_returnsFalse() {
//         // given
//         // Create partial horizontal barrier
//         for (int c = 1; c < 10; c += 2) {
//             Wall wall = new Wall();
//             wall.setOrientation(WallOrientation.HORIZONTAL);
//             wall.setR(7);
//             wall.setC(c);
//             testWalls.add(wall);
//         }
        
//         // This wall would extend but not complete the barrier
//         int nextColumn = 11;
        
//         // when-then
//         assertFalse(moveService.wouldBlockAllPaths(testGame, testBoard, testWalls, 7, nextColumn, WallOrientation.HORIZONTAL));
//     }
    
//     @Test
//     public void wouldBlockAllPaths_verticalBarrier_returnsTrue() {
//         // given
//         // Create vertical barriers across the board except for one gap
//         for (int r = 1; r < 14; r += 2) {
//             Wall wall = new Wall();
//             wall.setOrientation(WallOrientation.VERTICAL);
//             wall.setR(r);
//             wall.setC(7);
//             testWalls.add(wall);
//         }
        
//         // This wall would complete the barrier
//         int lastGapRow = 15;
        
//         // when-then
//         assertTrue(moveService.wouldBlockAllPaths(testGame, testBoard, testWalls, lastGapRow, 7, WallOrientation.VERTICAL));
//     }
    
//     @Test
//     public void hasPathToGoal_complexMaze_findAlternativePath() {
//         // given
//         // Create a complex maze-like structure with walls
//         // Horizontal walls
//         Wall wall1 = new Wall();
//         wall1.setOrientation(WallOrientation.HORIZONTAL);
//         wall1.setR(3);
//         wall1.setC(3);
//         testWalls.add(wall1);
        
//         Wall wall2 = new Wall();
//         wall2.setOrientation(WallOrientation.HORIZONTAL);
//         wall2.setR(3);
//         wall2.setC(7);
//         testWalls.add(wall2);
        
//         Wall wall3 = new Wall();
//         wall3.setOrientation(WallOrientation.HORIZONTAL);
//         wall3.setR(7);
//         wall3.setC(5);
//         testWalls.add(wall3);
        
//         // Vertical walls
//         Wall wall4 = new Wall();
//         wall4.setOrientation(WallOrientation.VERTICAL);
//         wall4.setR(5);
//         wall4.setC(3);
//         testWalls.add(wall4);
        
//         Wall wall5 = new Wall();
//         wall5.setOrientation(WallOrientation.VERTICAL);
//         wall5.setR(5);
//         wall5.setC(9);
//         testWalls.add(wall5);
        
//         // when-then
//         // Despite the complex maze, there should still be a path to goal
//         assertTrue(moveService.hasPathToGoal(testGame, testBoard, testPawn1, testPawn1.getR(), testPawn1.getC(), testWalls));
//         assertTrue(moveService.hasPathToGoal(testGame, testBoard, testPawn2, testPawn2.getR(), testPawn2.getC(), testWalls));
//     }

//     /**
//      * Tests for jump mechanics with different arrangements
//      */
//     @Test
//     public void isValidPawnMove_horizontalJumpOverPawn_returnsTrue() {
//         // given
//         // Place pawns side by side
//         testPawn1.setR(8);
//         testPawn1.setC(8);
//         testPawn2.setR(8);
//         testPawn2.setC(10);
        
//         // when-then
//         // Try to jump over pawn2 horizontally
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 8, 12, testWalls));
//     }
    
//     @Test
//     public void isValidPawnMove_horizontalJumpBlocked_returnsFalse() {
//         // given
//         // Place pawns side by side
//         testPawn1.setR(8);
//         testPawn1.setC(8);
//         testPawn2.setR(8);
//         testPawn2.setC(10);
        
//         // Add a wall blocking the jump
//         Wall wall = new Wall();
//         wall.setR(8);
//         wall.setC(11);
//         wall.setOrientation(WallOrientation.VERTICAL);
//         testWalls.add(wall);
        
//         // when-then
//         // Try to jump over pawn2 horizontally
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 8, 12, testWalls));
//     }
    
//     @Test
//     public void isValidPawnMove_diagonalHorizontalJump_returnsTrue() {
//         // given
//         // Place pawns side by side
//         testPawn1.setR(8);
//         testPawn1.setC(8);
//         testPawn2.setR(8);
//         testPawn2.setC(10);
        
//         // Add a wall blocking the straight jump
//         Wall wall = new Wall();
//         wall.setR(8);
//         wall.setC(11);
//         wall.setOrientation(WallOrientation.VERTICAL);
//         testWalls.add(wall);
        
//         // when-then
//         // Try to jump diagonally (up)
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 6, 10, testWalls));
//     }
    
//     @Test
//     public void isValidPawnMoveHasPath_validPath_returnsTrue() {
//         // given
//         testPawn1.setR(8);
//         testPawn1.setC(8);
        
//         // when-then
//         assertTrue(moveService.isValidPawnMoveHasPath(testBoard, testPawn1, 10, 8, 8, 8, testWalls));
//     }
    
//     @Test
//     public void isValidPawnMoveHasPath_invalidPath_returnsFalse() {
//         // given
//         testPawn1.setR(8);
//         testPawn1.setC(8);
        
//         // Add a blocking wall
//         Wall wall = new Wall();
//         wall.setR(9);
//         wall.setC(8);
//         wall.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall);
        
//         // when-then
//         assertFalse(moveService.isValidPawnMoveHasPath(testBoard, testPawn1, 10, 8, 8, 8, testWalls));
//     }

//     /**
//      * Tests for wall orientation and placement
//      */
//     @Test
//     public void isWallBlockingPath_horizontalWallAtIntersection_blocksVerticalMovement() {
//         // given
//         // Create a horizontal wall
//         Wall wall = new Wall();
//         wall.setR(1);
//         wall.setC(8);
//         wall.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall);
        
//         // when-then
//         // Pawn trying to move down through the wall
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
//     }
    
//     @Test
//     public void isWallBlockingPath_verticalWallAtIntersection_blocksHorizontalMovement() {
//         // given
//         // Create a vertical wall
//         Wall wall = new Wall();
//         wall.setR(0);
//         wall.setC(9);
//         wall.setOrientation(WallOrientation.VERTICAL);
//         testWalls.add(wall);
        
//         // when-then
//         // Pawn trying to move right through the wall
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 0, 10, testWalls));
//     }
    
//     @Test
//     public void isValidWallField_oddCoordinates_returnsTrue() {
//         // when-then
//         assertTrue(moveService.isValidWallField(testBoard, 1, 1));
//         assertTrue(moveService.isValidWallField(testBoard, 3, 5));
//         assertTrue(moveService.isValidWallField(testBoard, 7, 7));
//     }
    
//     @Test
//     public void isValidWallField_evenCoordinates_returnsFalse() {
//         // when-then
//         assertFalse(moveService.isValidWallField(testBoard, 0, 0));
//         assertFalse(moveService.isValidWallField(testBoard, 2, 4));
//         assertFalse(moveService.isValidWallField(testBoard, 6, 6));
//     }
    
//     @Test
//     public void isValidWallField_outOfBounds_returnsFalse() {
//         // when-then
//         assertFalse(moveService.isValidWallField(testBoard, -1, 3));
//         assertFalse(moveService.isValidWallField(testBoard, 3, -1));
//         assertFalse(moveService.isValidWallField(testBoard, 17, 3));
//         assertFalse(moveService.isValidWallField(testBoard, 3, 17));
//     }
// }
//  