package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * GameService and finally return the result.
 */
@RestController
public class GameController {

    private final Logger log = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/game-lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<GameGetDTO> getAllGames() {
        // fetch all users in the internal representation
        List<Game> loadedgames = gameService.getGames();
        List<GameGetDTO> gameGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (Game a:loadedgames) {
            gameGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGameGetDTO(a));
        }
        return gameGetDTOs;
    }

    @PostMapping("/game-lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GamePostDTO createGame(@RequestBody UserGetDTO userGetDTO) {
      // convert API user to internal representation
     User user = DTOMapper.INSTANCE.convertUserPostGETtoEntity(userGetDTO);

     Game game = gameService.createGame(user);

     return DTOMapper.INSTANCE.convertEntityToGamePostDTO(game);
    }

    @PutMapping("/game-lobby/{gameId}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void joinGame(@PathVariable Long gameId, @RequestBody UserGetDTO userGetDTO) {
      // convert API user to internal representation
     User user = DTOMapper.INSTANCE.convertUserPostGETtoEntity(userGetDTO);
 
     gameService.joinGame(user, gameId);

    }

}