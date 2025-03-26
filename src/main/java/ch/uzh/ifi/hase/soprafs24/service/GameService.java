package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final UserRepository userRepository;
    private final GameRepository gameRepository;


    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("userRepository") UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }


    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */

    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }
     
    public Game createGame(User user) {
        User userById = userRepository.findById(user.getId()).orElse(null);

        String baseErrorMessage = "The User does not exist or is not logged in currenlty!";
        if (userById == null || userById.getStatus() == UserStatus.OFFLINE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }

        Game newGame = new Game();

        // Set the game properties (here use default values as only later user stories make this variable)
        newGame.setNumberUsers(2); 
        newGame.setSizeBoard(9); 
        newGame.setCreator(userById);
        newGame.setCurrentTurn(userById);
        newGame.setGameStatus(GameStatus.WAITING_FOR_USER);
        Set<User> userList = Set.of(userById);
        newGame.setCurrentUsers(userList); 

        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }


    public void joinGame(User user, Long gameId) {
        User userById = userRepository.findById(user.getId()).orElse(null);

        String userErrorMessage = "The User does not exist or is not logged in currenlty!";
        if (userById == null || userById.getStatus() == UserStatus.OFFLINE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, userErrorMessage);
        }

        Game gameById = gameRepository.findById(gameId).orElse(null);

        String gameErrorMessage = "The Game does not exist!";
        if (gameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameErrorMessage);
        }

        String gameFullErrorMessage = "The Game is already full or running!";
        if (gameById.getGameStatus() == GameStatus.RUNNING || gameById.getCurrentUsers().size() == 2) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameFullErrorMessage);
        }

        String userGameErrorMessage = "The user is already part of the game!";
        if (gameById.getCurrentUsers().contains(userById)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, userGameErrorMessage);
        }

        gameById.addUser(userById);
        gameRepository.flush();

        if (gameById.getCurrentUsers().size() == 2) {
            gameById.setGameStatus(GameStatus.RUNNING);
        }

        log.debug("Updadet Information for Game: {}", gameById);
    }


    
}