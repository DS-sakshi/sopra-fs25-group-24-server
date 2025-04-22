package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Move;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.GameStatusDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MovePostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.PawnGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.WallGetDTO;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "birthday", target = "birthday")
        // Password is handled separately in the service
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "token", target = "token")
    User convertUserPostGETtoEntity(UserGetDTO UserGetDTO);

    @Mapping(source = "endPosition", target = "endPosition")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "wallPosition", target = "wallPosition")
    @Mapping(source = "wallOrientation", target = "wallOrientation")
    Move convertMovePostDTOtoEntity(MovePostDTO MovePostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "creationDate", target = "creationDate")
    @Mapping(source = "birthday", target = "birthday")
    @Mapping(source = "token", target = "token")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "birthday", target = "birthday")
    void updateUserFromDTO(UserPutDTO userPutDTO, @MappingTarget User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "numberUsers", target = "numberUsers")
    @Mapping(source = "sizeBoard", target = "sizeBoard")
    @Mapping(source = "timeLimit", target = "timeLimit")
    @Mapping(source = "creator", target = "creator")
    @Mapping(source = "currentUsers", target = "currentUsers")
    @Mapping(source = "gameStatus", target = "gameStatus")
    GamePostDTO convertEntityToGamePostDTO(Game game);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "numberUsers", target = "numberUsers")
    @Mapping(source = "sizeBoard", target = "sizeBoard")
    @Mapping(source = "timeLimit", target = "timeLimit")
    @Mapping(source = "creator", target = "creator")
    @Mapping(source = "currentUsers", target = "currentUsers")
    @Mapping(source = "gameStatus", target = "gameStatus")
    GameGetDTO convertEntityToGameGetDTO(Game game);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "currentTurn", target = "currentTurn")
    @Mapping(source = "gameStatus", target = "gameStatus")
    GameStatusDTO convertEntityToGameStatusDTO(Game game);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "color", target = "color")
    @Mapping(source = "r", target = "r")
    @Mapping(source = "c", target = "c")
    @Mapping(source = "orientation", target = "orientation")
    @Mapping(source = "board.id", target = "boardId")  // Get ID from nested Board
    @Mapping(source = "userId", target = "userId") 
    WallGetDTO convertEntityToWallGetDTO(Wall wall);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "color", target = "color")
    @Mapping(source = "r", target = "r")
    @Mapping(source = "c", target = "c")
    @Mapping(source = "board.id", target = "boardId")
    @Mapping(source = "userId", target = "userId")
    PawnGetDTO convertEntityToPawnGetDTO(Pawn pawn);
}
