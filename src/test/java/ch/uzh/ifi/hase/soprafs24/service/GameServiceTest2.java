package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.entity.Board;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Move;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;
import ch.uzh.ifi.hase.soprafs24.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PawnRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WallRepository;
import ch.uzh.ifi.hase.soprafs24.websocket.RefreshWebSocketHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class GameServiceTest2 {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private PawnRepository pawnRepository;

    @Mock
    private WallRepository wallRepository;

    @Mock
    private MoveService moveService;

    @Mock
    private RefreshWebSocketHandler refreshWebSocketHandler;

    @InjectMocks
    private GameService gameService;

    private User testUser;
    private User testUser2;
    private Game testGame;
    private Board testBoard;
    private Pawn testPawn1;
    private Pawn testPawn2;
    private List<Wall> testWalls;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        Mockito.doNothing().when(refreshWebSocketHandler).broadcastRefresh(Mockito.anyString());

        // Create first test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setStatus(UserStatus.ONLINE);
        testUser.setCreationDate(new Date());

        // Create second test user
        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setName("testName2");
        testUser2.setUsername("testUsername2");
        testUser2.setStatus(UserStatus.ONLINE);
        testUser2.setCreationDate(new Date());

        // Create test board
        testBoard = new Board();
        testBoard.setId(1L);
        testBoard.setSizeBoard(9);

        // Create test walls
        testWalls = new ArrayList<>();
        testBoard.setWalls(testWalls);

        // Create test pawn
        testPawn1 = new Pawn();
        testPawn1.setId(1L);
        testPawn1.setR(0);
        testPawn1.setC(8);
        testPawn1.setColor("red");
        testPawn1.setUserId(testUser.getId());
        testPawn1.setBoard(testBoard);

        // Create second test pawn
        testPawn2 = new Pawn();
        testPawn2.setId(2L);
        testPawn2.setR(16);
        testPawn2.setC(8);
        testPawn2.setColor("blue");
        testPawn2.setUserId(testUser2.getId());
        testPawn2.setBoard(testBoard);

        List<Pawn> pawns = new ArrayList<>();
        pawns.add(testPawn1);
        pawns.add(testPawn2);
        testBoard.setPawns(pawns);

        // Create test game
        testGame = new Game();
        testGame.setId(1L);
        testGame.setNumberUsers(2);
        testGame.setSizeBoard(9);
        testGame.setCreator(testUser);
        testGame.setCurrentTurn(testUser);
        testGame.setGameStatus(GameStatus.WAITING_FOR_USER);
        Set<User> userList = new HashSet<>();
        userList.add(testUser);
        testGame.setCurrentUsers(userList);
        testGame.setBoard(testBoard);

        // Configure mocks
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUser2));

        when(gameRepository.save(any(Game.class))).thenReturn(testGame);
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(testPawn1);
        
        // for the MoveService mock
        when(moveService.isValidWallField(any(), anyInt(), anyInt())).thenReturn(true);
        when(moveService.isValidPawnMove(any(), any(), anyInt(), anyInt(), any())).thenReturn(true);
        when(moveService.getGoalRow(any(), any(), any())).thenReturn(16);  // Return a goal row that won't trigger win condition
        when(moveService.wouldBlockAllPaths(any(), any(), any(), anyInt(), anyInt(), any())).thenReturn(false);
    }

    /**
     * Tests for movePawn functionality
     */
    @Test
    public void movePawn_validMove_success() {
        // Given
        testGame.setGameStatus(GameStatus.RUNNING);
        Move move = new Move();
        move.setUser(testUser);
        List<Integer> endPosition = Arrays.asList(2, 8);
        move.setEndPosition(endPosition);
        
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When
        Game result = gameService.movePawn(1L, move);
        
        // Then
        verify(moveService).isValidPawnMove(eq(testBoard), any(Pawn.class), eq(2), eq(8), anyList());
        assertEquals(GameStatus.RUNNING, result.getGameStatus());
        verify(gameRepository, atLeastOnce()).flush();
       // verify(refreshWebSocketHandler, atLeastOnce()).broadcastRefresh();
    }
    
    @Test
    public void movePawn_winCondition_endsGame() {
        // Given
        testGame.setGameStatus(GameStatus.RUNNING);
        Move move = new Move();
        move.setUser(testUser);
        List<Integer> endPosition = Arrays.asList(16, 8); // Winning position
        move.setEndPosition(endPosition);
        
        // Override the mock to return the same goal row as the end position
        when(moveService.getGoalRow(any(), any(), any())).thenReturn(16);
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When
        Game result = gameService.movePawn(1L, move);
        
        // Then
        assertEquals(GameStatus.ENDED, result.getGameStatus());

        int winsBefore = testUser.getTotalGamesWon();
        gameService.movePawn(1L, move);
        int winsAfter = testUser.getTotalGamesWon();
        assertEquals(winsBefore + 1, winsAfter);

        verify(gameRepository, atLeastOnce()).save(any(Game.class));

        //verify(refreshWebSocketHandler, atLeastOnce()).broadcastRefresh();
    }
    
    @Test
    public void movePawn_invalidMove_throwsException() {
        // Given
        testGame.setGameStatus(GameStatus.RUNNING);
        Move move = new Move();
        move.setUser(testUser);
        List<Integer> endPosition = Arrays.asList(2, 8);
        move.setEndPosition(endPosition);
        
        // Invalidate the move validation
        when(moveService.isValidPawnMove(any(), any(), anyInt(), anyInt(), any())).thenReturn(false);
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When/Then
        assertThrows(ResponseStatusException.class, () -> gameService.movePawn(1L, move));
    }
    

    
    /**
     * Tests for placeWall functionality
     */
    @Test
    public void placeWall_validPlacement_success() {
        // Given
        when(wallRepository.findByBoardId(anyLong())).thenReturn(new ArrayList<>());
        when(wallRepository.findByBoardIdAndUserId(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When
        Game result = gameService.placeWall(1L, testUser, 3, 3, WallOrientation.HORIZONTAL);
        
        // Then
        verify(wallRepository, times(1)).save(any(Wall.class));
        assertEquals(GameStatus.WAITING_FOR_USER, result.getGameStatus());
        verify(gameRepository, atLeastOnce()).flush();

        //verify(refreshWebSocketHandler, atLeastOnce()).broadcastRefresh();
    }
    
    @Test
    public void placeWall_wallAlreadyExists_throwsException() {
        // Given
        List<Wall> existingWalls = new ArrayList<>();
        Wall existingWall = new Wall();
        existingWall.setR(3);
        existingWall.setC(3);
        existingWall.setOrientation(WallOrientation.HORIZONTAL);
        existingWalls.add(existingWall);
        
        when(wallRepository.findByBoardId(anyLong())).thenReturn(existingWalls);
        when(wallRepository.findByBoardIdAndUserId(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When/Then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 3, 3, WallOrientation.HORIZONTAL));
    }
    
    @Test
    public void placeWall_horizontalWallOverlap_throwsException() {
        // Given
        List<Wall> existingWalls = new ArrayList<>();
        Wall existingWall = new Wall();
        existingWall.setR(3);
        existingWall.setC(1);
        existingWall.setOrientation(WallOrientation.HORIZONTAL);
        existingWalls.add(existingWall);
        
        when(wallRepository.findByBoardId(anyLong())).thenReturn(existingWalls);
        when(wallRepository.findByBoardIdAndUserId(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When/Then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 3, 3, WallOrientation.HORIZONTAL));
    }
    
    @Test
    public void placeWall_verticalWallOverlap_throwsException() {
        // Given
        List<Wall> existingWalls = new ArrayList<>();
        Wall existingWall = new Wall();
        existingWall.setR(1);
        existingWall.setC(3);
        existingWall.setOrientation(WallOrientation.VERTICAL);
        existingWalls.add(existingWall);
        
        when(wallRepository.findByBoardId(anyLong())).thenReturn(existingWalls);
        when(wallRepository.findByBoardIdAndUserId(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When/Then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 3, 3, WallOrientation.VERTICAL));
    }
    
    @Test
    public void placeWall_crossingWalls_throwsException() {
        // Given
        List<Wall> existingWalls = new ArrayList<>();
        Wall existingWall = new Wall();
        existingWall.setR(3);
        existingWall.setC(3);
        existingWall.setOrientation(WallOrientation.VERTICAL);
        existingWalls.add(existingWall);
        
        when(wallRepository.findByBoardId(anyLong())).thenReturn(existingWalls);
        when(wallRepository.findByBoardIdAndUserId(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When/Then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 3, 3, WallOrientation.HORIZONTAL));
    }
    
    @Test
    public void placeWall_wouldBlockAllPaths_throwsException() {
        // Given
        when(wallRepository.findByBoardId(anyLong())).thenReturn(new ArrayList<>());
        when(wallRepository.findByBoardIdAndUserId(anyLong(), anyLong())).thenReturn(new ArrayList<>());
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // Override to make the wall block all paths
        when(moveService.wouldBlockAllPaths(any(), any(), any(), anyInt(), anyInt(), any())).thenReturn(true);
        
        // When/Then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 3, 3, WallOrientation.HORIZONTAL));
    }
    
    
    /**
     * Tests for canPlaceWall functionality
     */
    @Test
    public void canPlaceWall_underLimit_returnsTrue() {
        // Given
        List<Wall> walls = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            walls.add(new Wall());
        }
        
        when(wallRepository.findByBoardIdAndUserId(anyLong(), anyLong())).thenReturn(walls);
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When
        boolean result = gameService.canPlaceWall(1L, testUser);
        
        // Then
        assertTrue(result);
    }
    
    @Test
    public void canPlaceWall_atLimit_returnsFalse() {
        // Given
        List<Wall> walls = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            walls.add(new Wall());
        }
        
        when(wallRepository.findByBoardIdAndUserId(anyLong(), anyLong())).thenReturn(walls);
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When
        boolean
         result = gameService.canPlaceWall(1L, testUser);
        
        // Then
        assertFalse(result);
    }
    
    
    /**
     * Tests for getWalls and getPawns functionality
     */
    @Test
    public void getWalls_returnsCorrectWalls() {
        // Given
        List<Wall> walls = new ArrayList<>();
        Wall wall1 = new Wall();
        wall1.setId(1L);
        Wall wall2 = new Wall();
        wall2.setId(2L);
        walls.add(wall1);
        walls.add(wall2);
        testBoard.setWalls(walls);
        
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When
        List<Wall> result = gameService.getWalls(1L);
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(wall1));
        assertTrue(result.contains(wall2));
    }
    
    @Test
    public void getPawns_returnsCorrectPawns() {
        // Given
        List<Pawn> pawns = new ArrayList<>();
        pawns.add(testPawn1);
        pawns.add(testPawn2);
        testBoard.setPawns(pawns);
        
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When
        List<Pawn> result = gameService.getPawns(1L);
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(testPawn1));
        assertTrue(result.contains(testPawn2));
    }
    
    /**
     * Tests for nextTurn functionality
     */
    @Test
    public void nextTurn_twoPlayers_alternatesTurns() {
        // Given
        Set<User> users = new HashSet<>();
        users.add(testUser);
        users.add(testUser2);
        testGame.setCurrentUsers(users);
        testGame.setCurrentTurn(testUser);
        
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When
        gameService.nextTurn(1L);
        
        // Then
        assertEquals(testUser2, testGame.getCurrentTurn());
        
        // When called again
        gameService.nextTurn(1L);
        
        // Then should return to first player
        assertEquals(testUser, testGame.getCurrentTurn());
    }
    
    @Test
    public void nextTurn_fourPlayers_rotatesThroughAllPlayers() {
        // Given
        User testUser3 = new User();
        testUser3.setId(3L);
        User testUser4 = new User();
        testUser4.setId(4L);
        
        Set<User> users = new LinkedHashSet<>(); // Use LinkedHashSet to preserve order
        users.add(testUser);
        users.add(testUser2);
        users.add(testUser3);
        users.add(testUser4);
        
        testGame.setCurrentUsers(users);
        testGame.setCurrentTurn(testUser);
        
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        
        // When
        gameService.nextTurn(1L);
        
        // Then
        assertEquals(testUser2, testGame.getCurrentTurn());
        
        // Complete the rotation
        gameService.nextTurn(1L);
        assertEquals(testUser3, testGame.getCurrentTurn());
        
        gameService.nextTurn(1L);
        assertEquals(testUser4, testGame.getCurrentTurn());
        
        gameService.nextTurn(1L);
        assertEquals(testUser, testGame.getCurrentTurn());
    }
    
    /**
     * Tests for delete functionality
     */
    @Test
    public void delete_runningGame_updatesGameStatusAndStatistics() {
        // Given
        testGame.setGameStatus(GameStatus.RUNNING);
        Set<User> users = new HashSet<>();
        users.add(testUser);
        users.add(testUser2);
        testGame.setCurrentUsers(users);
        
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        
        // When
        gameService.delete(1L, testUser);
        
        // Then
        assertEquals(GameStatus.ENDED, testGame.getGameStatus());
        
        // Remove the save verification
        verify(gameRepository, times(1)).delete(testGame);
        verify(gameRepository, times(1)).flush();
    }
        
    @Test
    public void delete_waitingGame_removesGameFromRepository() {
        // Given
        testGame.setGameStatus(GameStatus.WAITING_FOR_USER);
        testGame.setCreator(testUser); // Set the test user as creator
        
        // Set up the test user to be part of the game
        Set<User> users = new HashSet<>();
        users.add(testUser);
        testGame.setCurrentUsers(users);
        
        when(gameRepository.findById(any())).thenReturn(Optional.of(testGame));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        
        // When
        gameService.delete(1L, testUser);
        
        // Then
        verify(gameRepository, times(1)).delete(testGame);
        
        // Remove the save verification
        verify(gameRepository, times(1)).flush();
        verify(refreshWebSocketHandler, times(1)).broadcastRefresh(anyString());
    }
    
}