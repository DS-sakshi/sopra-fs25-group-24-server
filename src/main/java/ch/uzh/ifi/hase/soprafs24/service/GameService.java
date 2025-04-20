package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to
 * the game
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 * Position [r, c]

 Assumptions for this code, maybe be changed later
 For now the whole game logic is implemented for 2 players and with a board size of 9*9. 
 16*16 board because there is space betweeen and we start counting from 0.
 This means for example, the game automatically starts once a player joins, as this is enough players. 
 The pawn of the creator of the game starts at position 0/8 (row 0, coloumn 8). 
 The second players pawn starts at 16/8.
 So, board is actually bigger than board size. 

 Board is assumed to look like this: 
 0/0,0/1,0/2,0/3,0/4,0/5,0/6,0/7,0/8,0/9 .
 1/0,1/1,1/2,1/3,1/4,1/5,1/6,1/7,1/8,1/9 ...
 2/0,2/1,2/2,2/3,2/4,2/5,2/6,2/7,2/8,2/9 ...
 3/0,3/1,3/2,3/3,3/4,3/5,3/6,3/7,3/8,3/9 ...
 ...

 */
//TODO: correct tests so they match the new code - Dora

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final BoardRepository boardRepository;
    private final PawnRepository pawnRepository;
    private final WallRepository wallRepository;
    private final MoveService moveService;


    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
     @Qualifier("userRepository") UserRepository userRepository,
      @Qualifier("boardRepository") BoardRepository boardRepository,
       @Qualifier("pawnRepository") PawnRepository pawnRepository,
       @Qualifier("wallRepository") WallRepository wallRepository,
       MoveService moveService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.pawnRepository = pawnRepository;
        this.boardRepository = boardRepository;
        this.wallRepository = wallRepository;
        this.moveService = moveService;
    }

    // return all games
    public List<Game> getGames() {
        return this.gameRepository.findAll();
    }

    public List<Wall> getWalls(Long gameId) {
        Game gameById = getGame(gameId);
        return gameById.getBoard().getWalls();
    }

    public List<Pawn> getPawns(Long gameId) {
        Game gameById = getGame(gameId);
        return gameById.getBoard().getPawns();
    }

    // returns a specific game
    public Game getGame(Long gameId) {
        Game gameById = gameRepository.findById(gameId).orElse(null);
        String gameErrorMessage = "The Game does not exist!";
        if (gameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameErrorMessage);
        }

        log.debug("Retrieved Game: {}", gameById);
        return gameById;
    }
     
    // Creates a game. Set passed user as creator and the status to waiting for enough players. Also already define Board, Pawns and the wall 
    public Game createGame(User user) {
        //check if user (creator of the game) exists 
        User userById = userRepository.findById(user.getId()).orElse(null);

        String baseErrorMessage = "The User does not exist or is not logged in currenlty!";
        if (userById == null || userById.getStatus() == UserStatus.OFFLINE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
        }

        // Set the game properties (here use default values as only later user stories make this variable)
        Game newGame = new Game();

        newGame.setNumberUsers(2); 
        newGame.setSizeBoard(9); 
        newGame.setCreator(userById);
        newGame.setCurrentTurn(userById);
        newGame.setGameStatus(GameStatus.WAITING_FOR_USER);
        Set<User> userList = Set.of(userById);
        newGame.setCurrentUsers(userList); 

        //set board
        Board board = new Board();
        board.setSizeBoard(9);
        newGame.setBoard(board); 
        //board.setGame(newGame);

        //set pawn
        Pawn pawn = new Pawn();
        pawn.setR(0);
        pawn.setC(8);
        pawn.setColor("red");
        pawn.setUserId(userById.getId());
        pawn.setBoard(board);

        //save entities
        boardRepository.save(board);
        boardRepository.flush();

        newGame = gameRepository.save(newGame);
        gameRepository.flush();
    
        board.getPawns().add(pawn);
        
        pawnRepository.save(pawn);
        pawnRepository.flush();

        log.debug("Created Information for Game: {}", newGame);
        return newGame;
    }

    // Join game. Set passed user as creator and the status running. 
    public void joinGame(User user, Long gameId) {
        //check if user exists 
        User userById = userRepository.findById(user.getId()).orElse(null);

        String userErrorMessage = "The User does not exist or is not logged in currenlty!";
        if (userById == null || userById.getStatus() == UserStatus.OFFLINE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, userErrorMessage);
        }

        //check if game exists 
        Game gameById = gameRepository.findById(gameId).orElse(null);

        String gameErrorMessage = "The Game does not exist!";
        if (gameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameErrorMessage);
        }

        //check if game is full or running
        String gameFullErrorMessage = "The Game is already full or running!";
        if (gameById.getGameStatus() == GameStatus.RUNNING || gameById.getCurrentUsers().size() == 2) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameFullErrorMessage);
        }

        //check if user is already part of the game
        String userGameErrorMessage = "The user is already part of the game!";
        if (gameById.getCurrentUsers().contains(userById)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, userGameErrorMessage);
        }

        // add user to the game 
        gameById.addUser(userById);
        gameRepository.flush();

        // create Pawn and add to board 
        Board board = gameById.getBoard();
        Pawn pawn = new Pawn();
        pawn.setR(16);
        pawn.setC(8);
        pawn.setColor("blue");
        pawn.setUserId(userById.getId());
        pawn.setBoard(board);
    
        //board.addPawn(pawn);
        pawnRepository.save(pawn);
        pawnRepository.flush();

        board.getPawns().add(pawn);
        boardRepository.flush();

        //set Game status to running and increase times played
        if (gameById.getCurrentUsers().size() == 2) {
            gameById.setGameStatus(GameStatus.RUNNING);

            for (User player : gameById.getCurrentUsers()) {
                player.increaseTotalGamesPlayed();
            }
        }

        log.debug("Updadet Information for Game: {}", gameById);
    }

    // Move Pawn.
    public Game movePawn(Long gameId, Move move) {
        //check if game exists 
        Game gameById = gameRepository.findById(gameId).orElse(null);

        String gameErrorMessage = "The Game does not exist!";
        if (gameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameErrorMessage);
        }

        Board board = gameById.getBoard();
        List<Wall> walls = board.getWalls();  

        //check if its users turn 
        User currentUser = gameById.getCurrentTurn();
        User moveUser = userRepository.findById(move.getUser().getId()).orElse(null);

        String userErrorMessage = "User not defined!"; // check if it is actually current users turn 
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, userErrorMessage);
        }
        
        String turnErrorMessage = "Not users turn!"; // check if it is actually current users turn 
        if (currentUser != moveUser) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, turnErrorMessage);
        }

        // get pawns of current player
        List<Pawn> pawns = gameById.getBoard().getPawns();

        Pawn pawnToMove = null;
        for (Pawn pawn : pawns) {
            if (pawn.getId().equals(currentUser.getId())) {
                pawnToMove = pawn;
            } 
        }

        if (pawnToMove == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pawn not found at the given start position or not owned by the current user.");
        }

        //check if pawn is allowed to move
  

        List<Integer> endPosition = move.getEndPosition();
        int targetR = endPosition.get(0);
        int targetC = endPosition.get(1);
    

        // Validate the move 
        if (!moveService.isValidPawnMove(board, pawnToMove, targetR, targetC, walls)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid pawn move.");
        }

        // Move  pawn
        pawnToMove.setR(endPosition.get(0));
        pawnToMove.setC(endPosition.get(1));
        
        //TODO check if winning condition is complete - Dora
        //check win condition
        int goalRow = moveService.getGoalRow(gameById, board, pawnToMove);
        if (pawnToMove.getR() == goalRow) {
  
            gameById.setGameStatus(GameStatus.ENDED);

            currentUser.increaseTotalGamesWon();
            for (User player : gameById.getCurrentUsers()) {
                if (!player.equals(currentUser)) {
                    player.increaseTotalGamesLost();
                }
            } 
            return gameById;
        }
        //update turn
        nextTurn(gameId);
        gameRepository.flush();

        return gameById; 
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




    public Game placeWall(Long gameId, User user, int r, int c, WallOrientation orientation) {
        Game game = getGame(gameId);
        User currentUser = game.getCurrentTurn();
        Board board = game.getBoard();
        List<Wall> existingWalls = wallRepository.findByBoardId(board.getId());

        if (!canPlaceWall(gameId, user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot place wall. User has no walls left.");
        }

        if (!moveService.isValidWallField(board, r, c)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: wall overlaps with existing wall");
        }


        String turnErrorMessage = "Not users turn!"; // check if it is actually current users turn 
        if (!currentUser.getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, turnErrorMessage);
        }

        if (r < 0 || r >= board.getSizeBoard() || c < 0 || c >= board.getSizeBoard()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: out of bound");
        }


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

        if (moveService.wouldBlockAllPaths(game, board, existingWalls, r, c, orientation)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid wall position: wall blocks all paths");
        }

        //Create new 
        Wall wall = new Wall();
        wall.setR(r);
        wall.setC(c);
        wall.setOrientation(orientation);
        wall.setUserId(user.getId());
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
        return game;
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

    
    public void delete(Long gameId, User forfeiter) {
        Game gameById = gameRepository.findById(gameId).orElse(null);

        String gameErrorMessage = "The Game does not exist!";
        if (gameById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, gameErrorMessage);
        }
        
        if(gameById.getGameStatus() == GameStatus.RUNNING){
            gameById.setGameStatus(GameStatus.ENDED);

            // get Users to adjust User statistics (User that forfeits the game loses while the other wins)
            Set<User> users = gameById.getCurrentUsers();
            for (User user : users) {
                if (user.equals(forfeiter)) {
                    user.increaseTotalGamesLost();
                } else {
                    user.increaseTotalGamesWon();
                }
            }
        } else if (gameById.getGameStatus() == GameStatus.WAITING_FOR_USER) {
            gameRepository.delete(gameById);
        }
    }



    
        
        
}