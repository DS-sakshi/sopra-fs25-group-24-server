package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.constant.MoveType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Move;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MovePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.WallGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PawnGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStatusDTO;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.websocket.RefreshWebSocketHandler;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

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

    private final GameService gameService;
    private final UserService userService;

    GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/game-lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public List<GameGetDTO> getAllGames(@RequestHeader(value = "Authorization", required = true) String token) {
        //check Auth
        userService.isValidToken(token);

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
    @Transactional
    public GameGetDTO getGame(@PathVariable Long gameId,  @RequestHeader(value = "Authorization", required = true) String token) {
        //check Auth
        userService.isValidToken(token);


        // fetch all users in the internal representation
        Game game = gameService.getGame(gameId);

        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    @GetMapping("/game-lobby/{gameId}/walls")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public List<WallGetDTO> getWalls(@PathVariable Long gameId,  @RequestHeader(value = "Authorization", required = true) String token) {
        //check Auth
        userService.isValidToken(token);


        // fetch all users in the internal representation
        List<Wall> listWalls = gameService.getWalls(gameId);
        List<WallGetDTO> wallGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (Wall a:listWalls) {
            wallGetDTOs.add(DTOMapper.INSTANCE.convertEntityToWallGetDTO(a));
        }
        return wallGetDTOs;
    }

    @GetMapping("/game-lobby/{gameId}/pawns")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Transactional
    public List<PawnGetDTO> getPawns(@PathVariable Long gameId,  @RequestHeader(value = "Authorization", required = true) String token) {
        //check Auth
        userService.isValidToken(token);
        

        // fetch all users in the internal representation
        List<Pawn> listPawns = gameService.getPawns(gameId);
        List<PawnGetDTO> pawnGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (Pawn a:listPawns) {
            pawnGetDTOs.add(DTOMapper.INSTANCE.convertEntityToPawnGetDTO(a));
        }
        return pawnGetDTOs;
    }


    @PostMapping("/game-lobby")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Transactional
    public GamePostDTO createGame(@RequestBody UserGetDTO userGetDTO,  @RequestHeader(value = "Authorization", required = true) String token) {
    //check Auth
    userService.isValidToken(token);

    // convert API user to internal representation

     User user = DTOMapper.INSTANCE.convertUserPostGETtoEntity(userGetDTO);

     Game game = gameService.createGame(user);

     return DTOMapper.INSTANCE.convertEntityToGamePostDTO(game);
    }

    @PutMapping("/game-lobby/{gameId}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    @Transactional
    public void joinGame(@PathVariable Long gameId, @RequestBody UserGetDTO userGetDTO,  @RequestHeader(value = "Authorization", required = true) String token) {
        //check Auth
        userService.isValidToken(token);

        // convert API user to internal representation

     User user = DTOMapper.INSTANCE.convertUserPostGETtoEntity(userGetDTO);
 
     gameService.joinGame(user, gameId);

    }

    @PostMapping("/game-lobby/{gameId}/move")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Transactional
    public GameStatusDTO handleMove(@PathVariable Long gameId, @RequestBody MovePostDTO movePostDTO,  @RequestHeader(value = "Authorization", required = true) String token) {
        //check Auth
        userService.isValidToken(token);


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
        if (wallPosition == null || wallPosition.size() != 2) { // New validation
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wall position requires exactly 2 coordinates");
        }

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

    // turnUpdate.setGameId(gameId); 
    // websocketService.sendTurnUpdate(turnUpdate);
        
    return DTOMapper.INSTANCE.convertEntityToGameStatusDTO(game);
    
    }

    @DeleteMapping("/game-lobby/{gameId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    @Transactional
    public void delete(@PathVariable Long gameId, @RequestBody UserGetDTO userGetDTO,  @RequestHeader(value = "Authorization", required = true) String token) {
        //check Auth
        userService.isValidToken(token);


        User user = DTOMapper.INSTANCE.convertUserPostGETtoEntity(userGetDTO);
        try{
            gameService.delete(gameId, user);
        } catch (ResponseStatusException e) {
        // Re-throw the exception with the same status and message
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred while deleting the game");
        }
    }
    

}