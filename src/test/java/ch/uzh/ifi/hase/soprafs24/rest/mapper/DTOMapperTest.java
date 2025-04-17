package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.MoveType;
import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Move;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
  @Test
  public void testCreateUser_fromUserPostDTO_toUser_success() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setName("name");
    userPostDTO.setUsername("username");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getName(), user.getName());
    assertEquals(userPostDTO.getUsername(), user.getUsername());
  }

  @Test
  public void testGetUser_fromUser_toUserGetDTO_success() {
    // create User
    User user = new User();
    user.setName("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setToken("1");

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getName(), userGetDTO.getName());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
  }

  @Test
  public void testCreateUser_fromUserGetDTO_toUser_success() {
    // create UserGetDTO
    UserGetDTO userGetDTO = new UserGetDTO();
    userGetDTO.setId(1L);
    userGetDTO.setName("Firstname Lastname");
    userGetDTO.setUsername("firstname@lastname");
    userGetDTO.setStatus(UserStatus.ONLINE);
    userGetDTO.setToken("token123");
    Date creationDate = new Date();
    userGetDTO.setCreationDate(creationDate);
    Date birthday = new Date();
    userGetDTO.setBirthday(birthday);

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostGETtoEntity(userGetDTO);

    // check content
    assertEquals(userGetDTO.getId(), user.getId());
    assertEquals(userGetDTO.getName(), user.getName());
    assertEquals(userGetDTO.getUsername(), user.getUsername());
    assertEquals(userGetDTO.getStatus(), user.getStatus());
    assertEquals(userGetDTO.getCreationDate(), user.getCreationDate());
    assertEquals(userGetDTO.getBirthday(), user.getBirthday());
    assertEquals(userGetDTO.getToken(), user.getToken());
  }

  @Test
  public void testCreateMove_fromMovePostDTO_toMove_success() {
    // create MovePostDTO
    MovePostDTO movePostDTO = new MovePostDTO();
    movePostDTO.setEndPosition(Arrays.asList(0, 2));
    movePostDTO.setType(MoveType.MOVE_PAWN);
    movePostDTO.setWallPosition(Arrays.asList(1, 3));
    movePostDTO.setWallOrientation("HORIZONTAL"); // Using WallOrientation enum
    
    User user = new User();
    user.setId(1L);
    user.setUsername("testuser");
    movePostDTO.setUser(user);

    // MAP -> Create move
    Move move = DTOMapper.INSTANCE.convertMovePostDTOtoEntity(movePostDTO);

    // check content
    assertEquals(movePostDTO.getEndPosition(), move.getEndPosition());
    assertEquals(movePostDTO.getType(), move.getType());
    assertEquals(movePostDTO.getWallPosition(), move.getWallPosition());
    //assertEquals(movePostDTO.getWallOrientation(), move.getWallOrientation());
    assertEquals(movePostDTO.getUser(), move.getUser());
  }

  @Test
  public void testUpdateUser_fromUserPutDTO_toUser_success() {
    // create original User
    User user = new User();
    user.setId(1L);
    user.setName("Original Name");
    user.setUsername("original_username");
    user.setBirthday(new Date());

    // create UserPutDTO with updated values
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setUsername("updated_username");
    Date updatedBirthday = new Date();
    userPutDTO.setBirthday(updatedBirthday);

    // Update user from DTO
    DTOMapper.INSTANCE.updateUserFromDTO(userPutDTO, user);

    // check content
    assertEquals("updated_username", user.getUsername());
    assertEquals(updatedBirthday, user.getBirthday());
    // Name should remain unchanged as it's not mapped in the updateUserFromDTO method
    assertEquals("Original Name", user.getName());
  }

  @Test
  public void testGetGame_fromGame_toGameGetDTO_success() {
    // create Game
    Game game = new Game();
    game.setId(1L);
    game.setNumberUsers(2);
    game.setSizeBoard(9);
    game.setTimeLimit(600);
    
    User creator = new User();
    creator.setId(1L);
    creator.setUsername("creator");
    game.setCreator(creator);
    
    Set<User> currentUsers = new HashSet<>();
    currentUsers.add(creator);
    User player2 = new User();
    player2.setId(2L);
    player2.setUsername("player2");
    currentUsers.add(player2);
    game.setCurrentUsers(currentUsers);
    
    game.setGameStatus(GameStatus.RUNNING);

    // MAP -> Create GameGetDTO
    GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

    // check content
    assertEquals(game.getId(), gameGetDTO.getId());
    assertEquals(game.getNumberUsers(), gameGetDTO.getNumberUsers());
    assertEquals(game.getSizeBoard(), gameGetDTO.getSizeBoard());
    assertEquals(game.getTimeLimit(), gameGetDTO.getTimeLimit());
    assertEquals(game.getCreator(), gameGetDTO.getCreator());
    assertEquals(game.getCurrentUsers(), gameGetDTO.getCurrentUsers());
    assertEquals(game.getGameStatus(), gameGetDTO.getGameStatus());
  }

  @Test
  public void testGetGame_fromGame_toGamePostDTO_success() {
    // create Game
    Game game = new Game();
    game.setId(1L);
    game.setNumberUsers(4);
    game.setSizeBoard(11);
    game.setTimeLimit(300);
    
    User creator = new User();
    creator.setId(1L);
    creator.setUsername("creator");
    game.setCreator(creator);
    
    Set<User> currentUsers = new HashSet<>();
    currentUsers.add(creator);
    game.setCurrentUsers(currentUsers);
    
    game.setGameStatus(GameStatus.WAITING_FOR_USER);

    // MAP -> Create GamePostDTO
    GamePostDTO gamePostDTO = DTOMapper.INSTANCE.convertEntityToGamePostDTO(game);

    // check content
    assertEquals(game.getId(), gamePostDTO.getId());
    assertEquals(game.getNumberUsers(), gamePostDTO.getNumberUsers());
    assertEquals(game.getSizeBoard(), gamePostDTO.getSizeBoard());
    assertEquals(game.getTimeLimit(), gamePostDTO.getTimeLimit());
    assertEquals(game.getCreator(), gamePostDTO.getCreator());
    assertEquals(game.getCurrentUsers(), gamePostDTO.getCurrentUsers());
    assertEquals(game.getGameStatus(), gamePostDTO.getGameStatus());
  }
}