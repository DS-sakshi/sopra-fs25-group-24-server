package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Board;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        pawn.setUser(user);  // Associate with the User
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
        assertEquals(user.getId(), foundPawns.get(0).getUser().getId());

        // Validate Walls
        List<Wall> foundWalls = found.getWalls();
        assertEquals(1, foundWalls.size());
        assertEquals(wall.getR(), foundWalls.get(0).getR());
        assertEquals(wall.getC(), foundWalls.get(0).getC());
        assertEquals(wall.getOrientation(), foundWalls.get(0).getOrientation());
        assertEquals(wall.getColor(), foundWalls.get(0).getColor());
        assertEquals(user.getId(), foundWalls.get(0).getUser().getId());
    }
}
