package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import ch.uzh.ifi.hase.soprafs24.constant.MoveType;
import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Move;
import ch.uzh.ifi.hase.soprafs24.entity.Board;
import ch.uzh.ifi.hase.soprafs24.entity.Game;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs24.repository.PawnRepository;
import ch.uzh.ifi.hase.soprafs24.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs24.repository.WallRepository;
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
import java.util.ArrayList;

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
    private final BoardRepository boardRepository;
    private final PawnRepository pawnRepository;
    private final WallRepository wallRepository;


    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
     @Qualifier("userRepository") UserRepository userRepository,
      @Qualifier("boardRepository") BoardRepository boardRepository,
       @Qualifier("pawnRepository") PawnRepository pawnRepository,
       @Qualifier("wallRepository") WallRepository wallRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.pawnRepository = pawnRepository;
        this.boardRepository = boardRepository;
        this.wallRepository = wallRepository;
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

    public Game getGame(Long gameId) {

        Game gameById = gameRepository.findById(gameId).orElse(null);

        String gameErrorMessage = "The Game does not exist!";
        if (gameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameErrorMessage);
        }

        log.debug("Retrieved Game: {}", gameById);
        return gameById;

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


        Board board = new Board();
        board.setSizeBoard(9);
        newGame.setBoard(board); 


        Pawn pawn = new Pawn();
        pawn.setR(9);
        pawn.setC(4);
        pawn.setColor("red");
        pawn.setUser(userById);
        pawn.setBoard(board);

        Wall wall = new Wall();
        wall.setR(0);
        wall.setC(4);
        wall.setColor("red");
        wall.setOrientation(WallOrientation.VERTICAL);
        wall.setUser(userById);
        wall.setBoard(board);

        boardRepository.save(board);
        boardRepository.flush();

        newGame = gameRepository.save(newGame);
        gameRepository.flush();
    
        board.getPawns().add(pawn);
        board.getWalls().add(wall);

        pawnRepository.save(pawn);
        pawnRepository.flush();

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

        Board board = gameById.getBoard();

        Pawn pawn = new Pawn();
        pawn.setR(0);
        pawn.setC(4);
        pawn.setColor("blue");
        pawn.setUser(userById);
        pawn.setBoard(board);

        Wall wall = new Wall();
        wall.setR(0);
        wall.setC(4);
        wall.setColor("blue");
        wall.setOrientation(WallOrientation.VERTICAL);
        wall.setUser(userById);
        wall.setBoard(board);
    
        //board.addPawn(pawn);
        pawnRepository.save(pawn);
        pawnRepository.flush();

        board.getPawns().add(pawn);
        boardRepository.flush();

        if (gameById.getCurrentUsers().size() == 2) {
            gameById.setGameStatus(GameStatus.RUNNING);
        }

        log.debug("Updadet Information for Game: {}", gameById);
    }

    public void movePawn(Long gameId, Move move) {
        Game gameById = gameRepository.findById(gameId).orElse(null);

        String gameErrorMessage = "The Game does not exist!";
        if (gameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameErrorMessage);
        }

        User currentUser = gameById.getCurrentTurn();
        User moveUser = userRepository.findById(move.getUser().getId()).orElse(null);

        String turnErrorMessage = "Not users turn!"; // check if it is actually current users turn 
        if (currentUser != moveUser) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, turnErrorMessage);
        }

        // get pawns of current player
        List<Pawn> pawns = gameById.getBoard().getPawns();

        Pawn pawnToMove = null;
        Pawn pawnNext = null;
        List<Integer> startPosition = move.getStartPosition();
        for (Pawn pawn : pawns) {
            if (pawn.getUser().getId().equals(currentUser.getId())) {
                pawnToMove = pawn;
            } else {
                pawnNext = pawn;
            }
        }

        if (pawnToMove == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pawn not found at the given start position or not owned by the current user.");
        }

        //check if pawn is allowed to move


        //move pawn
        List<Integer> endPosition = move.getEndPosition();
        pawnToMove.setR(endPosition.get(0));
        pawnToMove.setC(endPosition.get(1));
        

        //check win condition

        //update turn
        nextTurn(gameId);
        gameRepository.flush();


    }

    public boolean canPlaceWall(Long gameId, User user) {
        Game game = getGame(gameId);
        Board board = game.getBoard();

        List<Wall> userWalls = wallRepository.findByBoardIdAndUserId(board.getId(), user.getId());
        int wallsPlaced = userWalls.size();
        int maxWalls = 10;
        // how i would extend for 4 players - Dora
        // if (game.getCurrentUsers().size() == 4) {
        //     maxWalls = 5;
        // }

        return maxWalls >= wallsPlaced;

    }

    // TODO findPath()
    private boolean wallBlocksAllPaths(Board board, int r, int c, WallOrientation orientation) {
        // For now, returns false to allow any wall placement
        return false;
    }


    public void placeWall(Long gameId, User user, int r, int c, WallOrientation orientation) {
        Game game = getGame(gameId);
        User currentUser = game.getCurrentTurn();
        Board board = game.getBoard();

        if (!canPlaceWall(gameId, user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot place wall. User has no walls left.");
        }


        String turnErrorMessage = "Not users turn!"; // check if it is actually current users turn 
        if (!currentUser.getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, turnErrorMessage);
        }

        if (r < 0 || r >= board.getSizeBoard() || c < 0 || c >= board.getSizeBoard()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: out of bound");
        }

        List<Wall> existingWalls = wallRepository.findByBoardId(board.getId());
        for (Wall existingWall : existingWalls) {
            //check for same position
            if (existingWall.getR() == r && existingWall.getC() == c && existingWall.getOrientation() == orientation) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: wall already exists");
            }
            
            //check for overlap in the same orientation
            if (orientation == WallOrientation.HORIZONTAL) {
                if (existingWall.getOrientation() == WallOrientation.HORIZONTAL && existingWall.getR() == r && (existingWall.getC() == c -1 || existingWall.getC() == c + 1)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: wall overlaps with existing wall");
                }
            } else if (orientation == WallOrientation.VERTICAL) {
                if (existingWall.getOrientation() == WallOrientation.VERTICAL && existingWall.getC() == c && (existingWall.getR() == r -1 || existingWall.getR() == r + 1)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: wall overlaps with existing wall");
                }
            }
            // check for cross
            if(orientation == WallOrientation.HORIZONTAL) {
                if ( existingWall.getOrientation() == WallOrientation.VERTICAL && existingWall.getR() == r -1 && existingWall.getC() == c + 1) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: wall overlaps with existing wall");
                }
            } else if (orientation == WallOrientation.VERTICAL) {
                if (existingWall.getOrientation() == WallOrientation.HORIZONTAL && existingWall.getR() == r + 1 && existingWall.getC() == c -1 ) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: wall overlaps with existing wall");
                }
            }
        }

        if (wallBlocksAllPaths(board, r, c, orientation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: wall blocks all paths");
        }

        //Create new 
        Wall wall = new Wall();
        wall.setR(r);
        wall.setC(c);
        wall.setOrientation(orientation);
        wall.setUser(user);
        if (user.getId().equals(game.getCreator().getId())) {
            wall.setColor("red");
        } else {
            wall.setColor("blue");
        }
        wall.setBoard(board); 


        board.getWalls().add(wall);

        wallRepository.save(wall);

        //update turn
        nextTurn(gameId);
        gameRepository.flush();
    }

    public void nextTurn(Long gameId) {
        Game game = getGame(gameId);
        User currentUser = game.getCurrentTurn();
        Set<User> userSet = game.getCurrentUsers();
        
        List<User> users = new ArrayList<>(userSet);
        
        int currentIndex = users.indexOf(currentUser);
        int nextIndex = (currentIndex + 1) % users.size();
        User nextUser = users.get(nextIndex);
    
        game.setCurrentTurn(nextUser);
        gameRepository.flush();
    }
    
        
        
}