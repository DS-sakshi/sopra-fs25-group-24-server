package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.constant.MoveType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Move;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MovePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStatusDTO;
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

    @GetMapping("/game-lobby/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGame(@PathVariable Long gameId) {
        // fetch all users in the internal representation
        Game game = gameService.getGame(gameId);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
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

    @PostMapping("/game-lobby/{gameId}/move")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameStatusDTO handleMove(@PathVariable Long gameId, @RequestBody MovePostDTO movePostDTO) {

    Move move =  DTOMapper.INSTANCE.convertMovePostDTOtoEntity(movePostDTO);
    Game game; 
    if (move.getType() == MoveType.MOVE_PAWN) { 
        game = gameService.movePawn(gameId, move);

    } else if (move.getType() == MoveType.ADD_WALL) {

        if (move.getWallOrientation() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wall orientation is required");
        }

        User user = move.getUser();
        List<Integer> wallPosition = move.getWallPosition();

        if (move.getType() != MoveType.ADD_WALL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid move type for placing a wall");
        }


        int r = wallPosition.get(0);
        int c = wallPosition.get(1);
        WallOrientation orientation = move.getWallOrientation();

        game = gameService.placeWall(gameId, user, r, c, orientation);
    } else {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid move type");
    }
        
    return DTOMapper.INSTANCE.convertEntityToGameStatusDTO(game);
    
    }

    @DeleteMapping("/game-lobby/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void delete(@PathVariable Long gameId, @RequestBody UserGetDTO userGetDTO) {
        User user = DTOMapper.INSTANCE.convertUserPostGETtoEntity(userGetDTO);
        gameService.delete(gameId, user);
    }
}