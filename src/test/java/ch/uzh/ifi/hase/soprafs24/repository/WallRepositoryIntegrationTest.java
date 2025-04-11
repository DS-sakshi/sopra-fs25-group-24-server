package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Wall;
import ch.uzh.ifi.hase.soprafs24.entity.Board;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class WallRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WallRepository wallRepository;

    @Test
    public void findById_success() {
        // given
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

        // Create and persist a Board
        Board board = new Board();
        board.setSizeBoard(9);
        entityManager.persist(board);
        entityManager.flush();

        Wall wall = new Wall();
        wall.setR(1);
        wall.setC(1);
        wall.setColor("Red");
        wall.setOrientation(WallOrientation.HORIZONTAL);
        wall.setUser(user);
        wall.setBoard(board);

        entityManager.persist(wall);
        entityManager.flush();

        // when
        Optional<Wall> optionalWall = wallRepository.findById(wall.getId());
        Wall found = optionalWall.orElse(null);

        // then
        assertNotNull(found);
        assertNotNull(found.getId());
        assertEquals(wall.getR(), found.getR());
        assertEquals(wall.getC(), found.getC());
        assertEquals(wall.getColor(), found.getColor());
        assertEquals(wall.getOrientation(), found.getOrientation());
        assertEquals(user.getId(), found.getUser().getId());
        assertEquals(board.getId(), found.getBoard().getId());
    }
}
