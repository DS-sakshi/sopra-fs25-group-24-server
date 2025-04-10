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
import static org.mockito.Mockito.*;

public class GameServiceTest {

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

    @InjectMocks
    private GameService gameService;

    private User testUser;
    private User testUser2;
    private Game testGame;
    private Board testBoard;
    private Pawn testPawn;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

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
        testBoard.setPawns(new ArrayList<>());
        testBoard.setWalls(new ArrayList<>());

        // Create test pawn
        testPawn = new Pawn();
        testPawn.setId(1L);
        testPawn.setR(1);
        testPawn.setC(9);
        testPawn.setColor("red");
        testPawn.setUser(testUser);
        testPawn.setBoard(testBoard);
        testBoard.getPawns().add(testPawn);

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

        // Fix for the error: use orElse pattern instead of Optional
        when(gameRepository.findById(any(Long.class))).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);
        when(boardRepository.save(any(Board.class))).thenReturn(testBoard);
        when(pawnRepository.save(any(Pawn.class))).thenReturn(testPawn);
    }
 
    @Test
    public void getGames_returnsAllGames() {
        // given
        List<Game> games = new ArrayList<>();
        games.add(testGame);
        when(gameRepository.findAll()).thenReturn(games);

        // when
        List<Game> result = gameService.getGames();

        // then
        assertEquals(1, result.size());
        assertEquals(testGame.getId(), result.get(0).getId());
    }

    @Test
    public void getGame_validId_returnsGame() {
        // when
        Game result = gameService.getGame(1L);

        // then
        assertEquals(testGame.getId(), result.getId());
    }

    // @Test
    // public void getGame_invalidId_throwsException() {
    //     // given
    //     when(gameRepository.findById(99L)).thenReturn(null);
    //     when(gameRepository.findById(99L)).thenReturn(Optional.empty());

    //     // when/then
    //     assertThrows(ResponseStatusException.class, () -> gameService.getGame(99L));
    // }

    @Test
    public void createGame_validUser_success() {
        // when
        Game createdGame = gameService.createGame(testUser);

        // then
        assertEquals(testUser, createdGame.getCreator());
        assertEquals(GameStatus.WAITING_FOR_USER, createdGame.getGameStatus());
        assertEquals(2, createdGame.getNumberUsers());
        assertEquals(9, createdGame.getSizeBoard());
        verify(gameRepository, times(1)).save(any(Game.class));
        verify(boardRepository, times(1)).save(any(Board.class));
        verify(pawnRepository, times(1)).save(any(Pawn.class));
    }

    @Test
    public void createGame_invalidUser_throwsException() {
        // given
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        User invalidUser = new User();
        invalidUser.setId(3L);

        // when/then
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(invalidUser));
    }

    @Test
    public void createGame_offlineUser_throwsException() {
        // given
        testUser.setStatus(UserStatus.OFFLINE);

        // when/then
        assertThrows(ResponseStatusException.class, () -> gameService.createGame(testUser));
    }

    @Test
    public void joinGame_validUserAndGame_success() {
        // when
        gameService.joinGame(testUser2, 1L);

        // then
        assertTrue(testGame.getCurrentUsers().contains(testUser2));
        assertEquals(GameStatus.RUNNING, testGame.getGameStatus());
        verify(pawnRepository, times(1)).save(any(Pawn.class));
    }

    @Test
    public void joinGame_invalidUser_throwsException() {
        // given
        User invalidUser = new User();
        invalidUser.setId(3L);
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        // when/then
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(invalidUser, 1L));
    }

    @Test
    public void joinGame_offlineUser_throwsException() {
        // given
        testUser2.setStatus(UserStatus.OFFLINE);

        // when/then
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(testUser2, 1L));
    }

    // @Test
    // public void joinGame_invalidGame_throwsException() {
    //     // given
    //     when(gameRepository.findById(99L)).thenReturn(null);
        
    //     // when/then
    //     assertThrows(ResponseStatusException.class, () -> gameService.joinGame(testUser2, 99L));
    // }

    @Test
    public void joinGame_runningGame_throwsException() {
        // given
        testGame.setGameStatus(GameStatus.RUNNING);

        // when/then
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(testUser2, 1L));
    }

    @Test
    public void joinGame_gameAlreadyFull_throwsException() {
        // given
        Set<User> users = new HashSet<>();
        users.add(testUser);
        users.add(testUser2);
        testGame.setCurrentUsers(users);

        // when/then
        User testUser3 = new User();
        testUser3.setId(3L);
        testUser3.setStatus(UserStatus.ONLINE);
        when(userRepository.findById(3L)).thenReturn(Optional.of(testUser3));

        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(testUser3, 1L));
    }

    @Test
    public void joinGame_userAlreadyInGame_throwsException() {
        // when/then
        assertThrows(ResponseStatusException.class, () -> gameService.joinGame(testUser, 1L));
    }

    @Test
    public void movePawn_validMove_success() {
        // given
        testGame.setGameStatus(GameStatus.RUNNING);
        Move move = new Move();
        move.setUser(testUser);
        List<Integer> endPosition = new ArrayList<>();
        endPosition.add(2);
        endPosition.add(9);
        move.setEndPosition(endPosition);

        // when
        gameService.movePawn(1L, move);

        // then
        assertEquals(2, testPawn.getR());
        assertEquals(9, testPawn.getC());
        verify(gameRepository, times(2)).flush();
    }

    @Test
    public void movePawn_notUsersTurn_throwsException() {
        // given
        testGame.setCurrentTurn(testUser2);
        Move move = new Move();
        move.setUser(testUser);
        List<Integer> endPosition = new ArrayList<>();
        endPosition.add(2);
        endPosition.add(9);
        move.setEndPosition(endPosition);

        // when/then
        assertThrows(ResponseStatusException.class, () -> gameService.movePawn(1L, move));
    }

    @Test
    public void canPlaceWall_underLimit_returnsTrue() {
        // given
        when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(new ArrayList<>());

        // when
        boolean result = gameService.canPlaceWall(1L, testUser);

        // then
        assertTrue(result);
    }

    // @Test
    // public void canPlaceWall_atLimit_returnsFalse() {
    //     // given
    //     List<Wall> walls = new ArrayList<>();
    //     for (int i = 0; i < 10; i++) {
    //         walls.add(new Wall());
    //     }
    //     when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(walls);

    //     // when
    //     boolean result = gameService.canPlaceWall(1L, testUser);

    //     // then
    //     assertFalse(result);
    // }

    @Test
    public void placeWall_validPosition_success() {
        // given
        when(wallRepository.findByBoardId(1L)).thenReturn(new ArrayList<>());
        when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(new ArrayList<>());

        // when
        gameService.placeWall(1L, testUser, 2, 2, WallOrientation.HORIZONTAL);

        // then
        verify(wallRepository, times(1)).save(any(Wall.class));
        verify(gameRepository, times(2)).flush();
    }

    // @Test
    // public void placeWall_noWallsLeft_throwsException() {
    //     // given
    //     List<Wall> walls = new ArrayList<>();
    //     for (int i = 0; i < 10; i++) {
    //         walls.add(new Wall());
    //     }
    //     when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(walls);

    //     // when/then
    //     assertThrows(ResponseStatusException.class, () -> 
    //         gameService.placeWall(1L, testUser, 2, 2, WallOrientation.HORIZONTAL));
    // }

    @Test
    public void placeWall_notUsersTurn_throwsException() {
        // given
        testGame.setCurrentTurn(testUser2);
        when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(new ArrayList<>());

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 2, 2, WallOrientation.HORIZONTAL));
    }

    @Test
    public void placeWall_outOfBounds_throwsException() {
        // given
        when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(new ArrayList<>());

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, -1, 2, WallOrientation.HORIZONTAL));
    }

    @Test
    public void placeWall_existingWallAtPosition_throwsException() {
        // given
        List<Wall> walls = new ArrayList<>();
        Wall existingWall = new Wall();
        existingWall.setR(2);
        existingWall.setC(2);
        existingWall.setOrientation(WallOrientation.HORIZONTAL);
        walls.add(existingWall);
        
        when(wallRepository.findByBoardId(1L)).thenReturn(walls);
        when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(new ArrayList<>());

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 2, 2, WallOrientation.HORIZONTAL));
    }

    @Test
    public void placeWall_overlappingHorizontalWall_throwsException() {
        // given
        List<Wall> walls = new ArrayList<>();
        Wall existingWall = new Wall();
        existingWall.setR(2);
        existingWall.setC(1);
        existingWall.setOrientation(WallOrientation.HORIZONTAL);
        walls.add(existingWall);
        
        when(wallRepository.findByBoardId(1L)).thenReturn(walls);
        when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(new ArrayList<>());

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 2, 2, WallOrientation.HORIZONTAL));
    }

    @Test
    public void placeWall_overlappingVerticalWall_throwsException() {
        // given
        List<Wall> walls = new ArrayList<>();
        Wall existingWall = new Wall();
        existingWall.setR(1);
        existingWall.setC(2);
        existingWall.setOrientation(WallOrientation.VERTICAL);
        walls.add(existingWall);
        
        when(wallRepository.findByBoardId(1L)).thenReturn(walls);
        when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(new ArrayList<>());

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 2, 2, WallOrientation.VERTICAL));
    }

    @Test
    public void placeWall_crossingWalls_throwsException() {
        // given
        List<Wall> walls = new ArrayList<>();
        Wall existingWall = new Wall();
        existingWall.setR(1);
        existingWall.setC(3);
        existingWall.setOrientation(WallOrientation.VERTICAL);
        walls.add(existingWall);
        
        when(wallRepository.findByBoardId(1L)).thenReturn(walls);
        when(wallRepository.findByBoardIdAndUserId(1L, 1L)).thenReturn(new ArrayList<>());

        // when/then
        assertThrows(ResponseStatusException.class, () -> 
            gameService.placeWall(1L, testUser, 2, 2, WallOrientation.HORIZONTAL));
    }

    @Test
    public void nextTurn_validGame_changesCurrentTurn() {
        // given
        Set<User> users = new HashSet<>();
        users.add(testUser);
        users.add(testUser2);
        testGame.setCurrentUsers(users);
        testGame.setCurrentTurn(testUser);

        // when
        gameService.nextTurn(1L);

        // then
        assertEquals(testUser2, testGame.getCurrentTurn());
        verify(gameRepository, times(1)).flush();
    }

    @Test
    public void delete_runningGame_setsGameStatusToEnded() {
        // given
        testGame.setGameStatus(GameStatus.RUNNING);

        // when
        gameService.delete(1L, testUser);

        // then
        assertEquals(GameStatus.ENDED, testGame.getGameStatus());
    }

    @Test
    public void delete_waitingGame_deletesGame() {
        // given
        testGame.setGameStatus(GameStatus.WAITING_FOR_USER);

        // when
        gameService.delete(1L, testUser);

        // then
        verify(gameRepository, times(1)).delete(testGame);
    }

    // @Test
    // public void delete_invalidGameId_throwsException() {
    //     // given
    //     when(gameRepository.findById(99L)).thenReturn(null);
        
    //     // when/then
    //     assertThrows(ResponseStatusException.class, () -> gameService.delete(99L, testUser));
    // }

    // @Test
    // public void delete_runningGame_updatesUserStatistics() {
    //     // given
    //     testGame.setGameStatus(GameStatus.RUNNING);
    //     Set<User> users = new HashSet<>();
    //     users.add(testUser);
    //     users.add(testUser2);
    //     testGame.setCurrentUsers(users);

    //     // when
    //     gameService.delete(1L, testUser);

    //     // then
    //     verify(testUser, times(1)).increaseTotalGamesLost();
    //     verify(testUser2, times(1)).increaseTotalGamesWon();
    // }
}