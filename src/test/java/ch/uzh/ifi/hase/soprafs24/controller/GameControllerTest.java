package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.MoveType;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MovePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private GameService gameService;

    // Test 1: getAllGames (success case - 200)
    @Test
    public void getAllGames_returnsGameList() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);
        String token = "1";

        Game game = new Game();
        game.setId(1L);
        game.setNumberUsers(2);
        game.setSizeBoard(9);
        game.setCreator(user);
        game.setGameStatus(GameStatus.WAITING_FOR_USER);
        game.setCurrentUsers(Set.of(user));

        List<Game> allGames = Collections.singletonList(game);

        given(gameService.getGames()).willReturn(allGames);

        // when/then
        mockMvc.perform(get("/game-lobby")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].numberUsers", is(2)))
            .andExpect(jsonPath("$[0].sizeBoard", is(9)))
            .andExpect(jsonPath("$[0].creator.id", is(1)))
            .andExpect(jsonPath("$[0].creator.username", is("testUsername")))
            .andExpect(jsonPath("$[0].gameStatus", is("WAITING_FOR_USER")))
            .andExpect(jsonPath("$[0].currentUsers[0].name", is(user.getName())))
            .andExpect(jsonPath("$[0].currentUsers[0].username", is(user.getUsername())))
            .andExpect(jsonPath("$[0].currentUsers[0].status", is("ONLINE")));
    }
    


    // Test 2: createGame (success case - 201)
    @Test
    public void createGame_validInput_userCreated() throws Exception {
        // given
        String token = "1";

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setToken(token);
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(new Date());

        Game newGame = new Game();
        newGame.setId(1L); 
        newGame.setNumberUsers(2); 
        newGame.setSizeBoard(9); 
        newGame.setCreator(user);
        newGame.setCurrentTurn(user);
        newGame.setGameStatus(GameStatus.WAITING_FOR_USER);
        Set<User> userList = Set.of(user);
        newGame.setCurrentUsers(userList); 

        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        given(gameService.createGame(Mockito.any())).willReturn(newGame);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/game-lobby")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userGetDTO))
                .header("Authorization", "Bearer " + token);

        // then
        mockMvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.numberUsers", is(2)))
            .andExpect(jsonPath("$.sizeBoard", is(9)))
            .andExpect(jsonPath("$.creator.id", is(1)))
            .andExpect(jsonPath("$.creator.username", is("testUsername")))
            .andExpect(jsonPath("$.gameStatus", is("WAITING_FOR_USER")))
            .andExpect(jsonPath("$.currentUsers[0].name", is(user.getName())))
            .andExpect(jsonPath("$.currentUsers[0].username", is(user.getUsername())))
            .andExpect(jsonPath("$.currentUsers[0].status", is("ONLINE")));
    }

    // Test 3: create Game (fail case - 409)
    @Test
    public void createGame_UserNotFound() throws Exception {
        // given
        String token = "1";
        UserGetDTO userGetDTO = new UserGetDTO();
        userGetDTO.setId(1L);
        userGetDTO.setName("Nonexistent User");
        userGetDTO.setUsername("nonexistentUsername");
        userGetDTO.setToken("invalidToken");
        userGetDTO.setStatus(UserStatus.OFFLINE);
    
        // Setup the mock to throw a not found exception
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "User does not exist"))
                .when(gameService).createGame(Mockito.any());
    
        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/game-lobby")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userGetDTO))
                .header("Authorization", "Bearer " + token);
    
        // Expect 404 Not Found status
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }

    // Test 4: join Game (valid case - 201)
    @Test
    public void joinGame_validInput_noContent() throws Exception {
        // given
        Long gameId = 1L;
        User user = new User();
        user.setId(2L);
        user.setName("Joining User");
        user.setUsername("joiningUser");
        user.setStatus(UserStatus.ONLINE);
        String token = "1";

        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // when/then
        MockHttpServletRequestBuilder putRequest = put("/game-lobby/{gameId}/join", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userGetDTO))
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(putRequest)
            .andExpect(status().isNoContent());

        Mockito.verify(gameService).joinGame(Mockito.any(User.class), Mockito.eq(gameId));
    }

    // Test 5: joinGame (fail case - 404 Game not found)
    @Test
    public void joinGame_gameNotFound() throws Exception {
        // given
        String token = "1";
        Long gameId = 999L;
        User user = new User();
        user.setId(2L);
        user.setName("Joining User");
        user.setUsername("joiningUser");
        user.setStatus(UserStatus.ONLINE);

        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"))
                .when(gameService).joinGame(Mockito.any(User.class), Mockito.eq(gameId));

        // when/then
        MockHttpServletRequestBuilder putRequest = put("/game-lobby/{gameId}/join", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userGetDTO))
                .header("Authorization", "Bearer " + token);

        mockMvc.perform(putRequest)
            .andExpect(status().isNotFound());
    }

    // Test 6: Get existing game (200 OK)
    @Test
    public void getGame_existingId_returnsGame() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setUsername("testUsername");
        user.setStatus(UserStatus.ONLINE);

        Game game = new Game();
        game.setId(1L);
        game.setNumberUsers(2);
        game.setSizeBoard(9);
        game.setCreator(user);
        game.setGameStatus(GameStatus.WAITING_FOR_USER);
        game.setCurrentUsers(Set.of(user));
        String token = "1";

        given(gameService.getGame(1L)).willReturn(game);

        mockMvc.perform(get("/game-lobby/1")
        .header("Authorization", "Bearer " + token))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id", is(1)))            
           .andExpect(jsonPath("$.numberUsers", is(2)))
           .andExpect(jsonPath("$.sizeBoard", is(9)))
           .andExpect(jsonPath("$.creator.id", is(1)))
           .andExpect(jsonPath("$.creator.username", is("testUsername")))
           .andExpect(jsonPath("$.gameStatus", is("WAITING_FOR_USER")))
           .andExpect(jsonPath("$.currentUsers[0].name", is(user.getName())))
           .andExpect(jsonPath("$.currentUsers[0].username", is(user.getUsername())))
           .andExpect(jsonPath("$.currentUsers[0].status", is("ONLINE")));
    }

    // Test 7: Get non-existent game (404 Not Found)
    @Test
    public void getGame_invalidId_throwsNotFound() throws Exception {
        given(gameService.getGame(999L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        String token = "1";

        mockMvc.perform(get("/game-lobby/999")
        .header("Authorization", "Bearer " + token))
           .andExpect(status().isNotFound());
        }

        // Test 8: Valid pawn move (201 Created)
    @Test
    public void handleMove_validPawnMove() throws Exception {
        MovePostDTO moveDTO = new MovePostDTO();
        moveDTO.setType(MoveType.MOVE_PAWN);
        moveDTO.setEndPosition(List.of(2, 3));
        String token = "1";

        mockMvc.perform(post("/game-lobby/1/move")
           .contentType(MediaType.APPLICATION_JSON)
           .content(asJsonString(moveDTO))
           .header("Authorization", "Bearer " + token))
           .andExpect(status().isCreated());
    }

    // Test 9: Invalid wall placement (400 Bad Request)
    @Test
    public void handleMove_missingWallOrientation() throws Exception {
        MovePostDTO moveDTO = new MovePostDTO();
        moveDTO.setType(MoveType.ADD_WALL);
        moveDTO.setWallPosition(List.of(4, 5));

        mockMvc.perform(post("/game-lobby/1/move")
           .contentType(MediaType.APPLICATION_JSON)
           .content(asJsonString(moveDTO)))
           .andExpect(status().isBadRequest());
        }

    // Test 10: Delete game successfully (204 No Content)
    @Test
    public void deleteGame_validRequest() throws Exception {
        UserGetDTO userDTO = new UserGetDTO();
        userDTO.setId(1L);
        String token = "1";
    
        mockMvc.perform(delete("/game-lobby/1")
           .contentType(MediaType.APPLICATION_JSON)
           .content(asJsonString(userDTO))
           .header("Authorization", "Bearer " + token))
           .andExpect(status().isNoContent());
        }

    // Test 11: Delete non-existent game (404 Not Found)
    @Test
    public void deleteGame_invalidId() throws Exception {
        UserGetDTO userDTO = new UserGetDTO();
        userDTO.setId(1L);
        userDTO.setName("Test User");
        userDTO.setUsername("testUsername");
        String token = "1";
    
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"))
            .when(gameService).delete(Mockito.eq(999L), Mockito.any(User.class));
    
        MockHttpServletRequestBuilder deleteRequest = delete("/game-lobby/{gameId}", 999L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userDTO))
            .header("Authorization", "Bearer " + token);
    
        mockMvc.perform(deleteRequest)
            .andExpect(status().isNotFound());
    }

    // Test 12: getWalls (success case - 200)
    @Test
    public void getWalls_returnsWallList() throws Exception {
        // given
        Wall wall = new Wall();
        wall.setId(1L);
        wall.setR(3);
        wall.setC(3);
        wall.setOrientation(WallOrientation.HORIZONTAL);
        List<Wall> walls = Collections.singletonList(wall);
        String token = "1";

        // Mocking the service 
        given(gameService.getWalls(1L)).willReturn(walls);

        // when/then
        mockMvc.perform(get("/game-lobby/1/walls").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].r", is(3)))
            .andExpect(jsonPath("$[0].c", is(3)))
            .andExpect(jsonPath("$[0].orientation", is("HORIZONTAL")));
    }

    // Test 13: getPawns (success case - 200)
    @Test
    public void getPawns_returnsPawnList() throws Exception {
        // given
        Pawn pawn = new Pawn();
        pawn.setId(1L);
        pawn.setR(6);
        pawn.setC(6);
        pawn.setColor("BLUE");
        List<Pawn> pawns = Collections.singletonList(pawn);
        String token = "1";

        // Mocking the service and DTO mapper
        given(gameService.getPawns(1L)).willReturn(pawns);
                 
        // when/then
        mockMvc.perform(get("/game-lobby/1/pawns")
        .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].r", is(6)))
            .andExpect(jsonPath("$[0].c", is(6)))
            .andExpect(jsonPath("$[0].color", is("BLUE")));
    }

    // Test 14: Valid Wall Placement (success case - 200)
    @Test
    public void handleMove_validWallPlacement() throws Exception {
        MovePostDTO moveDTO = new MovePostDTO();
        moveDTO.setType(MoveType.ADD_WALL);
        moveDTO.setWallPosition(List.of(4, 5));
        moveDTO.setWallOrientation("VERTICAL");
        String token = "1";

        mockMvc.perform(post("/game-lobby/1/move")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(moveDTO))
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isCreated());
    }

    // Test 14: Inalid Wall Placement (success case - 200)
    @Test
    public void handleMove_invalidWallPositionSize() throws Exception {
        MovePostDTO moveDTO = new MovePostDTO();
        moveDTO.setType(MoveType.ADD_WALL);
        moveDTO.setWallPosition(List.of(4)); // Single element
        moveDTO.setWallOrientation("HORIZONTAL");

        mockMvc.perform(post("/game-lobby/1/move")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(moveDTO)))
        .andExpect(status().isBadRequest());
    }

    //Test 15: Inalid Wall Placement (success case - 200)
    @Test
    public void handleMove_invalidMoveType() throws Exception {
        String invalidMoveJson = "{\"type\":\"INVALID_TYPE\",\"wallPosition\":[4,5]}";

        mockMvc.perform(
            post("/game-lobby/1/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidMoveJson))
        )
        .andExpect(status().isBadRequest());
    }

    // Test 16: Inalid Wall Placement (success case - 200)
    @Test
    public void handleMove_missingWallPosition() throws Exception {
        MovePostDTO moveDTO = new MovePostDTO();
        moveDTO.setType(MoveType.ADD_WALL);
        moveDTO.setWallOrientation("HORIZONTAL");

    mockMvc.perform(post("/game-lobby/1/move")
       .contentType(MediaType.APPLICATION_JSON)
       .content(asJsonString(moveDTO)))
       .andExpect(status().isBadRequest());
    }




    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}