package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Board;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;
import ch.uzh.ifi.hase.soprafs24.entity.User;  // Import User
import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus; // Import UserStatus
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Date; // Import Date

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


@DataJpaTest
public class BoardRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void findById_success() {
        // given
        Board board = new Board();
        board.setSizeBoard(9);

        // Create and persist a User
        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setToken("token");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate(new Date());
        user = entityManager.persist(user);
        entityManager.flush();

        // Add a Pawn to the Board, associating it with the User
        Pawn pawn = new Pawn();
        pawn.setR(1);
        pawn.setC(1);
        pawn.setColor("Red");
        pawn.setUserId(user.getId());  // Associate with the User
        board.addPawn(pawn);

        // Add a Wall to the Board, associating it with the User
        Wall wall = new Wall();
        wall.setR(2);
        wall.setC(2);
        wall.setOrientation(WallOrientation.HORIZONTAL);
        wall.setColor("Gray");
        wall.setUser(user); // Associate with the User
        board.addWall(wall);

        entityManager.persist(board); // Persist the Board (cascades to Pawns and Walls)
        entityManager.flush();

        // when
        Optional<Board> optionalBoard = boardRepository.findById(board.getId());
        Board found = optionalBoard.orElse(null);

        // then
        assertNotNull(found);
        assertNotNull(found.getId());
        assertEquals(found.getSizeBoard(), board.getSizeBoard());

        // Validate Pawns
        List<Pawn> foundPawns = found.getPawns();
        assertEquals(1, foundPawns.size());
        assertEquals(pawn.getR(), foundPawns.get(0).getR());
        assertEquals(pawn.getC(), foundPawns.get(0).getC());
        assertEquals(pawn.getColor(), foundPawns.get(0).getColor());
        assertEquals(user.getId(), foundPawns.get(0).getUserId());

        // Validate Walls
        List<Wall> foundWalls = found.getWalls();
        assertEquals(1, foundWalls.size());
        assertEquals(wall.getR(), foundWalls.get(0).getR());
        assertEquals(wall.getC(), foundWalls.get(0).getC());
        assertEquals(wall.getOrientation(), foundWalls.get(0).getOrientation());
        assertEquals(wall.getColor(), foundWalls.get(0).getColor());
        assertEquals(user.getId(), foundWalls.get(0).getUser().getId());
    }

    @Test
    public void removePawnAndWall_success() {
        // given
        Board board = new Board();
        board.setSizeBoard(9);

        User user = new User();
        user.setName("Test User");
        user.setUsername("testuser");
        user.setPassword("password");
        user.setToken("token");
        user.setStatus(UserStatus.OFFLINE);
        user.setCreationDate(new Date());
        user = entityManager.persist(user);
        entityManager.flush();

        Pawn pawn = new Pawn();
        pawn.setR(1);
        pawn.setC(1);
        pawn.setColor("Red");
        pawn.setUserId(user.getId());
        board.addPawn(pawn);

        Wall wall = new Wall();
        wall.setR(2);
        wall.setC(2);
        wall.setOrientation(WallOrientation.HORIZONTAL);
        wall.setColor("Gray");
        wall.setUser(user);
        board.addWall(wall);

        entityManager.persist(board);
        entityManager.flush();

        // when: Remove pawn and wall
        board.removePawn(pawn);
        board.removeWall(wall);

        // then: Check that they are removed from the board
        assertFalse(board.getPawns().contains(pawn), "Pawn should be removed from board");
        assertFalse(board.getWalls().contains(wall), "Wall should be removed from board");

        // and: Their board reference should be null
        assertNull(pawn.getBoard(), "Pawn's board reference should be null after removal");
        assertNull(wall.getBoard(), "Wall's board reference should be null after removal");
    }

    @Test
    public void setAndGetGame_success() {
        // given
        Board board = new Board();
        board.setSizeBoard(9);

        Game game = new Game();

        // when: Set game to board
        board.setGame(game);

        // then: getGame should return the same object
        assertEquals(game, board.getGame(), "getGame should return the set Game object");
    }
}

