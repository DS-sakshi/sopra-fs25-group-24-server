package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus; 


@DataJpaTest
public class GameRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private GameRepository gameRepository;

  @Test
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void findById_success() {
    // given
    User creator = new User();
    creator.setName("Creator Name");
    creator.setUsername("creator@name.com");
    creator.setCreationDate(new Date());
    creator.setToken("testToken");
    creator.setPassword("testPassword");
    creator.setStatus(UserStatus.OFFLINE); 

    creator = entityManager.merge(creator); 
    entityManager.flush();

    Game game = new Game();
    game.setNumberUsers(2);
    game.setSizeBoard(9);
    game.setCreator(creator);
    game.setGameStatus(GameStatus.WAITING_FOR_USER);
    game.setCurrentTurn(creator); 

    entityManager.persist(game);
    entityManager.flush();

    // when
    Game found = gameRepository.findById(game.getId()).orElseThrow(() -> new IllegalArgumentException("Game not found"));

    // then
    assertNotNull(found.getId());
    assertEquals(found.getNumberUsers(), game.getNumberUsers());
    assertEquals(found.getSizeBoard(), game.getSizeBoard());
    assertEquals(found.getCreator().getId(), creator.getId());
    assertEquals(found.getGameStatus(), game.getGameStatus());
  }
}
