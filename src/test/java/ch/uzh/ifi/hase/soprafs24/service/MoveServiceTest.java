// package ch.uzh.ifi.hase.soprafs24.service;
// package ch.uzh.ifi.hase.soprafs24.service;

// import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
// import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
// import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
// import ch.uzh.ifi.hase.soprafs24.entity.Board;
// import ch.uzh.ifi.hase.soprafs24.entity.Game;
// import ch.uzh.ifi.hase.soprafs24.entity.Move;
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
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.springframework.web.server.ResponseStatusException;

// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;


// public class MoveServiceTest {
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
//     @InjectMocks
//     private MoveService moveService;

//     private User testUser;
//     private User testUser2;
//     private Game testGame;
//     private Board testBoard;
//     private Pawn testPawn1;
//     private Pawn testPawn2;
//     private List<Wall> testWalls;
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
//         testGame.setGameStatus(GameStatus.RUNNING);
//         Set<User> userList = new HashSet<>();
//         userList.add(testUser2);
//         userList.add(testUser2);
//         userList.add(testUser);
//         testGame.setCurrentUsers(userList);
//         testGame.setBoard(testBoard);
//     }

//     @Test
//     public void hasPathToGoal_noWalls_returnsTrue() {
//         // Test that both players have a path to their goals when no walls are present
//         assertTrue(moveService.hasPathToGoal(testGame, testBoard, testPawn1, testPawn1.getR(), testPawn1.getC(),testWalls));
//         assertTrue(moveService.hasPathToGoal(testGame, testBoard, testPawn2, testPawn2.getR(), testPawn2.getC(), testWalls));
//     }

//    @Test
//     public void hasPathToGoal_withWalls_returnsTrue() {
//         // given
//         Wall wall = new Wall();
//         wall.setOrientation(WallOrientation.HORIZONTAL);
//         wall.setR(5);
//         wall.setC(5);
//         testWalls.add(wall);

//         // then
//         assertTrue(moveService.hasPathToGoal(testGame, testBoard, testPawn1, testPawn1.getR(), testPawn1.getC(),testWalls));
//         assertTrue(moveService.hasPathToGoal(testGame, testBoard, testPawn2, testPawn2.getR(), testPawn2.getC(), testWalls));
//     }

//     @Test
//     public void hasPathToGoal_noPath_WallsBlocking_returnsFalse() {
//         // Create a horizontal wall barrier across the entire board 
//         for (int c = 1; c <= 15; c += 2) {
//             Wall wall = new Wall();
//             wall.setOrientation(WallOrientation.HORIZONTAL);
//             wall.setR(5);
//             wall.setC(c);
//             testWalls.add(wall);
//         }

//         // At least one player should not have a path to goal
//         boolean player1HasPath = moveService.hasPathToGoal(testGame, testBoard, testPawn1, testPawn1.getR(), testPawn1.getC(),testWalls);
//         boolean player2HasPath = moveService.hasPathToGoal(testGame, testBoard, testPawn2, testPawn2.getR(), testPawn2.getC(), testWalls);
        
//         // Either player1 or player2 (or both) should not have a path
//         assertFalse(player1HasPath && player2HasPath);
//     }

//     @Test
//     public void getGoalRow_validPawn_returnsCorrectRow() {
//         // given
//         Wall wall = new Wall();
//         wall.setOrientation(WallOrientation.HORIZONTAL);
//         wall.setR(5);
//         wall.setC(5);
//         testWalls.add(wall);

//         // then
//         assertTrue(moveService.hasPathToGoal(testGame, testBoard, testPawn1, testWalls));
//         assertTrue(moveService.hasPathToGoal(testGame, testBoard, testPawn2, testWalls));
//     }

//     @Test
//     public void hasPathToGoal_noPath_WallsBlocking_returnsFalse() {
//         // Create a horizontal wall barrier across the entire board 
//         for (int c = 1; c <= 15; c += 2) {
//             Wall wall = new Wall();
//             wall.setOrientation(WallOrientation.HORIZONTAL);
//             wall.setR(5);
//             wall.setC(c);
//             testWalls.add(wall);
//         }

//         // At least one player should not have a path to goal
//         boolean player1HasPath = moveService.hasPathToGoal(testGame, testBoard, testPawn1, testWalls);
//         boolean player2HasPath = moveService.hasPathToGoal(testGame, testBoard, testPawn2, testWalls);
        
//         // Either player1 or player2 (or both) should not have a path
//         assertFalse(player1HasPath && player2HasPath);
//     }

//     @Test
//     public void getGoalRow_validPawn_returnsCorrectRow() {
//         // given
//         int expectedRow = testBoard.getSizeBoard() - 1; // 16 for a 17x17 board
//         int expectedRow = testBoard.getSizeBoard() - 1; // 16 for a 17x17 board

//         // when
//         int goalRow = moveService.getGoalRow(testGame, testBoard, testPawn1);
//         int goalRow = moveService.getGoalRow(testGame, testBoard, testPawn1);

//         // then
//         assertEquals(expectedRow, goalRow);
//         assertEquals(expectedRow, goalRow);
//     }

//     @Test
//     public void getGoalRow_forSecondPlayer() {
//     public void getGoalRow_forSecondPlayer() {
//         // given
//         int expectedRow = 0;

//         // when
//         int goalRow = moveService.getGoalRow(testGame, testBoard, testPawn2);

//         // then
//         assertEquals(expectedRow, goalRow);
//     }

//     @Test
//     public void isValidPawnMove_toAdjacentCell() {
//         // Move right
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 0, 10, testWalls));
        
//         // Move down
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
//     }

//     @Test
//     public void isValidPawnMove_toNonAdjacentSquare_returnsFalse() {
//         // Try to move diagonally
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 10, testWalls));
        
//         // Try to move two squares away
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 4, 8, testWalls));
//     }

//     @Test
//     public void isValidPawnMove_toOccupiedSquare_returnsFalse() {
//         // Place pawn2 adjacent to pawn1
//         testPawn2.setR(2);
//         testPawn2.setC(8);
        
//         // Try to move to the occupied square
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
//     }

//     @Test
//     public void isValidPawnMove_outOfBounds_returnsFalse() {
//         // Move out of bounds
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, -2, 8, testWalls));
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, testBoard.getSizeBoard() + 2, 8, testWalls));
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 0, -2, testWalls));
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 0, testBoard.getSizeBoard() + 2, testWalls));
//     }

//     @Test
//     public void isValidPawnMove_blockedByWall_returnsFalse() {
//         // Add a wall blocking the move down
//         Wall wall = new Wall();
//         wall.setR(1);
//         wall.setC(8);
//         wall.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall);
        
//         // Try to move through the wall (down)
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
//     }

//     @Test
//     public void isWallBlockingPath_horizontalWall_blocksVerticalMovement() {
//         // Create horizontal wall
//         Wall horizontalWall = new Wall();
//         horizontalWall.setR(1);
//         horizontalWall.setC(8);
//         horizontalWall.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(horizontalWall);
        
//         // Vertical movement (down) should be blocked
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
        
//         // But horizontal movement should still be possible
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 0, 10, testWalls));
//         int expectedRow = 0;

//         // when
//         int goalRow = moveService.getGoalRow(testGame, testBoard, testPawn2);

//         // then
//         assertEquals(expectedRow, goalRow);
//     }

//     @Test
//     public void isValidPawnMove_toAdjacentCell() {
//         // Move right
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 0, 10, testWalls));
        
//         // Move down
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
//     }

//     @Test
//     public void isValidPawnMove_toNonAdjacentSquare_returnsFalse() {
//         // Try to move diagonally
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 10, testWalls));
        
//         // Try to move two squares away
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 4, 8, testWalls));
//     }

//     @Test
//     public void isValidPawnMove_toOccupiedSquare_returnsFalse() {
//         // Place pawn2 adjacent to pawn1
//         testPawn2.setR(2);
//         testPawn2.setC(8);
        
//         // Try to move to the occupied square
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
//     }

//     @Test
//     public void isValidPawnMove_outOfBounds_returnsFalse() {
//         // Move out of bounds
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, -2, 8, testWalls));
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, testBoard.getSizeBoard() + 2, 8, testWalls));
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 0, -2, testWalls));
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 0, testBoard.getSizeBoard() + 2, testWalls));
//     }

//     @Test
//     public void isValidPawnMove_blockedByWall_returnsFalse() {
//         // Add a wall blocking the move down
//         Wall wall = new Wall();
//         wall.setR(1);
//         wall.setC(8);
//         wall.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall);
        
//         // Try to move through the wall (down)
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
//     }

//     @Test
//     public void isWallBlockingPath_horizontalWall_blocksVerticalMovement() {
//         // Create horizontal wall
//         Wall horizontalWall = new Wall();
//         horizontalWall.setR(1);
//         horizontalWall.setC(8);
//         horizontalWall.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(horizontalWall);
        
//         // Vertical movement (down) should be blocked
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
        
//         // But horizontal movement should still be possible
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 0, 10, testWalls));
//     }

//     @Test
//     public void isWallBlockingPath_verticalWall_blocksHorizontalMovement() {
//         // Create vertical wall
//         Wall verticalWall = new Wall();
//         verticalWall.setR(0);
//         verticalWall.setC(9);
//         verticalWall.setOrientation(WallOrientation.VERTICAL);
//         testWalls.add(verticalWall);
        
//         // Horizontal movement (right) should be blocked
//         assertFalse(moveService.isValidPawnMove(testBoard, testPawn1, 0, 10, testWalls));
        
//         // But vertical movement should still be possible
//         assertTrue(moveService.isValidPawnMove(testBoard, testPawn1, 2, 8, testWalls));
//     }

//     @Test
//     public void wouldBlockAllPaths_blockingWall_returnsTrue() {
//         // Create a nearly complete barrier with some walls
//         Wall wall1 = new Wall();
//         wall1.setR(15);
//         wall1.setC(1);
//         wall1.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall1);

//         Wall wall2 = new Wall();
//         wall2.setR(15);
//         wall2.setC(5);
//         wall2.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall2);

//         Wall wall3 = new Wall();
//         wall3.setR(15);
//         wall3.setC(9);
//         wall3.setOrientation(WallOrientation.HORIZONTAL);
//         testWalls.add(wall3);


//         // This wall would block the last remaining path
//         boolean result = moveService.wouldBlockAllPaths(testGame, testBoard, testWalls, 16, 9, WallOrientation.VERTICAL);
//         assertTrue(result);
//     }

//     @Test
//     public void wouldBlockAllPaths_nonBlockingWall_returnsFalse() {
//         // Check that a wall which doesn't block all paths returns false
//         boolean result = moveService.wouldBlockAllPaths(testGame, testBoard, testWalls, 7, 8, WallOrientation.HORIZONTAL);
//         assertFalse(result);
//     }

//     @Test
//     public void isValidPawnField_withValidField_returnsTrue() {
//         // Test with even coordinates (valid pawn positions)
//         assertTrue(moveService.isValidPawnField(testBoard, testPawn1, 0, 0));
//         assertTrue(moveService.isValidPawnField(testBoard, testPawn1, 2, 4));
//         assertTrue(moveService.isValidPawnField(testBoard, testPawn1, 16, 16));
//     }

//     @Test
//     public void isValidPawnField_withInvalidField_returnsFalse() {
//         // Test with odd coordinates (wall positions)
//         assertFalse(moveService.isValidPawnField(testBoard, testPawn1, 1, 1));
//         assertFalse(moveService.isValidPawnField(testBoard, testPawn1, 5, 4));
        
//         // Test with out of bounds coordinates
//         assertFalse(moveService.isValidPawnField(testBoard, testPawn1, -2, 4));
//         assertFalse(moveService.isValidPawnField(testBoard, testPawn1, 18, 4));
//     }

//     @Test
//     public void isValidWallField_withValidField_returnsTrue() {
//         // Test with odd coordinates (valid wall positions)
//         assertTrue(moveService.isValidWallField(testBoard, 1, 1));
//         assertTrue(moveService.isValidWallField(testBoard, 3, 7));
//         assertTrue(moveService.isValidWallField(testBoard, 15, 15));
//     }

//     @Test
//     public void isValidWallField_withInvalidField_returnsFalse() {
//         // Test with even coordinates (pawn positions)
//         assertFalse(moveService.isValidWallField(testBoard, 0, 0));
//         assertFalse(moveService.isValidWallField(testBoard, 2, 7));
        
//         // Test with out of bounds coordinates
//         assertFalse(moveService.isValidWallField(testBoard, -1, 3));
//         assertFalse(moveService.isValidWallField(testBoard, 17, 3));
//     }
// } 