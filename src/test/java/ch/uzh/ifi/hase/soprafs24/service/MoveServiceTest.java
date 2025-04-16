/* package ch.uzh.ifi.hase.soprafs24.service;

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


public class MoveServiceTest {
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

        // for the MoveService mock
        when(moveService.isValidPawnMove(any(), any(), anyInt(), anyInt(), any())).thenReturn(true);
        when(moveService.getGoalRow(any(), any(), any())).thenReturn(17);  // Return a goal row that won't trigger win condition
        when(moveService.wouldBlockAllPaths(any(), any(), any(), anyInt(), anyInt(), any())).thenReturn(false);
    
    }

    @Test
    public void testMovePawn_validMove() {
        // given
        int newRow = 2;
        int newCol = 8;

        // when
        Move move = moveService.movePawn(testGame, testUser, testPawn, newRow, newCol);

        // then
        assertNotNull(move);
        assertEquals(testPawn.getId(), move.getPawn().getId());
        assertEquals(newRow, move.getNewRow());
        assertEquals(newCol, move.getNewCol());

        // Verify that the pawn's position has been updated
        assertEquals(newRow, testPawn.getR());
        assertEquals(newCol, testPawn.getC());
    }

    @Test
    public void testMovePawn_invalidMove() {
        // given
        int invalidRow = 10;  // Out of bounds
        int invalidCol = 10;  // Out of bounds

        // when & then
        assertThrows(ResponseStatusException.class, () -> {
            moveService.movePawn(testGame, testUser, testPawn, invalidRow, invalidCol);
        });

        // Verify that the pawn's position has not changed
        assertEquals(1, testPawn.getR());
        assertEquals(9, testPawn.getC());
    }

    @Test
    public void testMovePawn_blockingMove() {
        // given
        int blockingRow = 2;
        int blockingCol = 8;

        // when & then
        assertThrows(ResponseStatusException.class, () -> {
            moveService.movePawn(testGame, testUser, testPawn, blockingRow, blockingCol);
        });

        // Verify that the pawn's position has not changed
        assertEquals(1, testPawn.getR());
        assertEquals(9, testPawn.getC());
    }

    @Test


}
 */