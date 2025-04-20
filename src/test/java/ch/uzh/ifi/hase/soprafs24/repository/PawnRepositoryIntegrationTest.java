package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.entity.Board;
import ch.uzh.ifi.hase.soprafs24.entity.User; // Import User
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus; // Import UserStatus
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.Date; // Import Date

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class PawnRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PawnRepository pawnRepository;

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


        Pawn pawn = new Pawn();
        pawn.setR(1);
        pawn.setC(1);
        pawn.setColor("Red");
        pawn.setUserId(user.getId());  // Associate with the User
        pawn.setBoard(board); // Associate with the Board

        entityManager.persist(pawn);
        entityManager.flush();

        // when
        Optional<Pawn> optionalPawn = pawnRepository.findById(pawn.getId());
        Pawn found = optionalPawn.orElse(null);
        
        // then
        assertNotNull(found.getId());
        assertEquals(pawn.getR(), found.getR());
        assertEquals(pawn.getC(), found.getC());
        assertEquals(pawn.getColor(), found.getColor());
        assertEquals(user.getId(), found.getUserId());
        assertEquals(board.getId(), found.getBoard().getId());
    }
}
