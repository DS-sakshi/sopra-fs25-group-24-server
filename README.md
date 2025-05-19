# Quoridor Server - SoPra FS25 Group 24

An online multiplayer implementation of the classic board game Quoridor, built with Spring Boot. This server provides RESTful APIs and WebSocket connections for real-time gameplay.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [High-Level Components](#high-level-components)
- [Project Structure](#project-structure)
- [Launch & Deployment](#launch--deployment)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Detailed Component Documentation](#detailed-component-documentation)
- [Game Logic Implementation](#game-logic-implementation)
- [Real-time Updates](#real-time-updates)
- [Roadmap](#roadmap)
- [Authors and Acknowledgment](#authors-and-acknowledgment)
- [License](#license)

## Overview

Quoridor is a strategic board game where players race to reach the opposite side of the board while strategically placing walls to block opponents. This digital implementation supports:

- 2-player games
- Real-time multiplayer functionality
- User management and statistics
- WebSocket-based real-time updates

## Features

### Core Gameplay
- **Pawn Movement**: Move pawns one square at a time toward the goal
- **Wall Placement**: Strategically place walls to block opponents
- **Jump Mechanics**: Jump over adjacent pawns when possible
- **Path Validation**: Ensures no player can be completely blocked
- **Turn Management**: Automated turn-based gameplay

### User Management
- User registration and authentication
- Profile management with statistics
- Online status management

### Game Features
- Create and join game lobbies
- Real-time game state synchronization
- Game forfeit functionality

## Technologies

- **Framework**: Spring Boot 2.7+ - For rapid, production-ready application development
- **Language**: Java 17 - Latest LTS version with modern language features
- **Database**: H2 (development) - Lightweight database
- **Build Tool**: Gradle 7.0+ - Dependency management and build automation
- **Real-time Communication**: WebSockets - For live game state synchronization
- **Testing**: JUnit 5, Mockito - Comprehensive testing framework
- **Authentication**: Token-based - Secure user session management
- **ORM**: JPA/Hibernate - Object-relational mapping for database operations
- **API Documentation**: REST endpoints with comprehensive error handling

## High-Level Components

### 1. [Game Management System](src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameService.java)
**Role**: Core game logic and state management
- Handles game creation, player joining, and turn progression
- Manages game lifecycle from creation to completion
- Integrates with move validation and user statistics

### 2. [Move Validation Engine](src/main/java/ch/uzh/ifi/hase/soprafs24/service/MoveService.java)
**Role**: Ensures game rule compliance and fair play
- Validates pawn movements including jumping mechanics
- Implements pathfinding algorithm to prevent game-breaking wall placements
- Enforces Quoridor rules for both basic and complex moves

### 3. [User Authentication & Management](src/main/java/ch/uzh/ifi/hase/soprafs24/service/UserService.java)
**Role**: Handles all user-related operations
- Manages registration, login, and profile updates
- Tracks game statistics and user status
- Provides security through token-based authentication

### 4. [Real-time Communication Layer](src/main/java/ch/uzh/ifi/hase/soprafs24/websocket/)
**Role**: Enables live multiplayer experience
- WebSocket handler for instant game state updates
- Broadcasts game events to all connected players
- Maintains connection management for multiplayer sessions

### 5. [REST API Controllers](src/main/java/ch/uzh/ifi/hase/soprafs24/controller/)
**Role**: HTTP request handling and API endpoints
- Game operations (create, join, move, forfeit)
- User operations (register, login, profile management)
- Coordinates between frontend requests and backend services

**Component Correlation**: The controllers receive HTTP requests and delegate to appropriate services. The GameService orchestrates game flow while utilizing MoveService for validation. UserService manages authentication used across all game operations. The WebSocket layer broadcasts updates triggered by successful game operations, creating a seamless real-time experience.

## Project Structure

```
src/main/java/ch/uzh/ifi/hase/soprafs24/
├── Application.java                 # Main Spring Boot application
├── constant/                        # Enums and constants
│   ├── GameStatus.java
│   ├── MoveType.java
│   ├── UserStatus.java
│   └── WallOrientation.java
├── controller/                      # REST controllers
│   ├── GameController.java
│   ├── RefreshController.java
│   └── UserController.java
├── entity/                         # JPA entities
│   ├── Board.java
│   ├── Game.java
│   ├── Move.java
│   ├── Pawn.java
│   ├── User.java
│   └── Wall.java
├── exceptions/                     # Exception handling
│   └── GlobalExceptionAdvice.java
├── repository/                     # Data access layer
│   ├── BoardRepository.java
│   ├── GameRepository.java
│   ├── PawnRepository.java
│   ├── UserRepository.java
│   └── WallRepository.java
├── rest/                          # DTOs and mappers
│   ├── dto/
│   └── mapper/
├── service/                       # Business logic
│   ├── GameService.java
│   ├── MoveService.java
│   └── UserService.java
└── websocket/                     # WebSocket configuration
    ├── RefreshWebSocketHandler.java
    └── WebSocketConfig.java
```

## Launch & Deployment

### Prerequisites

- Java 17 or higher
- Gradle 7.0+
- Git

### Installation

1. **Clone the repository**
   ```bash
   git clone [https://github.com/DS-sakshi/sopra-fs25-group-24-server]
   cd sopra-fs25-group-24-server
   ```

2. **Build the project**
   ```bash
   ./gradlew build
   ```
2.5. **Build the project without tests**
   ```bash
   ./gradlew build -x test
   ```
3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Verify the server is running**
   Visit `http://localhost:8080` in your browser

### Development Mode

For development with automatic reloading:

```bash
# Terminal 1: Continuous build
./gradlew build --continuous

# Terminal 2: Run application
./gradlew bootRun
```

To skip tests during development:
```bash
./gradlew build --continuous -xtest
```

## API Documentation

### Authentication Endpoints

#### User Registration
```http
POST /login/users
Content-Type: application/json

{
    "username": "string",
    "name": "string"
    "password": "string"
}
```

#### User Login
```http
POST /login
Content-Type: application/json

{
    "username": "string",
    "name": "string"
    "password": "string"
}
```

#### User Logout
```http
POST /login/logout
Authorization: Bearer {token}
```

### Game Management

#### Create Game
```http
POST /game-lobby
Authorization: Bearer {token}
Content-Type: application/json

{
    "id": 2,
    "name": "Test Player 2",
    "username": "player2",
    "status": "ONLINE",
    "creationDate": "2025-05-02T11:21:24.855+00:00",
    "birthday": null,
    "token": "5049268f-c3ff-4042-9b26-9fd8f6e7daa6"
}
```

**Response (201 Created):**
```json
{
    "id": 1,
    "numberUsers": 2,
    "sizeBoard": 9,
    "timeLimit": 0,
    "gameStatus": "WAITING_FOR_USER",
    "creator": {
        "id": 2,
        "name": "Test Player 1",
        "username": "player1",
        "token": "f2b6f0b5-315a-4be1-9440-c8f58b4b79b2",
        "status": "ONLINE",
        "password": "password123",
        "creationDate": "2025-05-19T15:30:07.001+00:00",
        "birthday": null,
        "totalGamesWon": 0,
        "totalGamesLost": 0,
        "totalGamesPlayed": 0
    },
   "currentUsers": [
        {
            "id": 2,
            "name": "Test Player 1",
            "username": "player1",
            "token": "f2b6f0b5-315a-4be1-9440-c8f58b4b79b2",
            "status": "ONLINE",
            "password": "password123",
            "creationDate": "2025-05-19T15:30:07.001+00:00",
            "birthday": null,
            "totalGamesWon": 0,
            "totalGamesLost": 0,
            "totalGamesPlayed": 0
        }
    ]
}
}
```

#### Join Game
```http
PUT /game-lobby/{gameId}/join
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 2,
}
```

#### Get Game Details
```http
GET /game-lobby/{gameId}
Authorization: Bearer {token}
```

#### Get Game Pawns
```http
GET /game-lobby/{gameId}/pawns
Authorization: Bearer {token}
```

#### Get Game Walls
```http
GET /game-lobby/{gameId}/walls
Authorization: Bearer {token}
```

#### Make Move
```http
POST /game-lobby/{gameId}/move
Authorization: Bearer {token}
Content-Type: application/json

{
  "startPosition": [
    0,
    8
  ],
  "endPosition": [
    0,
    10
  ],
  "user": {
    "id": 2,
    "username": "player2"
  },
  "type": "MOVE_PAWN"
}
```

#### Place Wall
```http
POST /game-lobby/{gameId}/move
Authorization: Bearer {token}
Content-Type: application/json

{
    "wallPosition": [3, 7],
    "user": {
    "id": 1,
    "username": "player1"
    }
    "type": "ADD_WALL",
    "wallOrientation": "HORIZONTAL"
}
```

#### Forfeit Game
```http
DELETE /game-lobby/{gameId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "username": "player1"
}
```

### User Profile

#### Get User Profile
```http
GET /users/{userId}
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
    "id": 2,
    "name": "Test Player 1",
    "username": "player1",
    "status": "ONLINE",
    "creationDate": "2025-05-19T15:30:07.001+00:00",
    "birthday": null,
    "token": "f2b6f0b5-315a-4be1-9440-c8f58b4b79b2",
    "totalGamesPlayed": 0,
    "totalGamesWon": 0,
    "totalGamesLost": 0
}
```

#### Update User Profile
```http
PUT /users/{userId}
Authorization: Bearer {token}
CurrentUserId: {currentUserId}
Content-Type: application/json

{
  "username": "new_username",
  "birthday": "1995-05-20T00:00:00.000Z"
}
```

#### Get All Users
```http
GET /users
Authorization: Bearer {token}
```

### WebSocket Endpoint

#### Real-time Game Updates
```
WebSocket URL: ws://localhost:8080/refresh-websocket
```

**Message Format:**
```json
{
  "type": "refresh",
  "gameId": "123"
}
```

### Response Status Codes

| Status | Description |
|--------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Request successful, no content to return |
| 400 | Bad Request - Invalid request parameters |
| 401 | Unauthorized - Authentication required |
| 403 | Forbidden - Access denied |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists or constraint violation |
| 500 | Internal Server Error - Server error |

## Database Schema

### Core Entities

**User**
- User management with authentication
- Game statistics tracking
- Online status management

**Game**
- Game configuration and state
- Player management
- Turn tracking

**Board**
- Game board representation
- Size configuration

**Pawn**
- Player pieces on the board
- Position tracking

**Wall**
- Wall placements
- Orientation (horizontal/vertical)

**Move**
- Game move history
- Move type tracking

## Detailed Component Documentation

### Constants (`constant/`)

#### GameStatus.java
Enum defining the three possible states of a game:
- `RUNNING`: Game is active and players can make moves
- `WAITING_FOR_USER`: Game is created but waiting for players to join
- `ENDED`: Game has concluded (win/forfeit)

#### MoveType.java
Enum defining the two types of moves players can make:
- `MOVE_PAWN`: Moving a player's pawn to a new position
- `ADD_WALL`: Placing a wall to block opponent movement

#### UserStatus.java
Enum defining user connection status:
- `ONLINE`: User is logged in and available
- `OFFLINE`: User is logged out

#### WallOrientation.java
Enum defining wall placement orientations:
- `HORIZONTAL`: Wall blocks vertical movement
- `VERTICAL`: Wall blocks horizontal movement

### Controllers (`controller/`)

#### GameController.java
Handles all game-related HTTP requests and coordinates with GameService.

**Key Functions:**
- `getAllGames()`: Returns list of all available games
- `getGame(Long gameId)`: Retrieves specific game details
- `getWalls(Long gameId)`: Gets all walls placed in a game
- `getPawns(Long gameId)`: Gets all pawns in a game
- `createGame(UserGetDTO userGetDTO)`: Creates new game lobby
- `joinGame(Long gameId, UserGetDTO userGetDTO)`: Adds user to existing game
- `handleMove(Long gameId, MovePostDTO movePostDTO)`: Processes pawn moves and wall placements
- `delete(Long gameId, UserGetDTO userGetDTO)`: Handles game forfeit/deletion

#### UserController.java
Manages user authentication, registration, and profile operations.

**Key Functions:**
- `getAllUsers()`: Returns list of all registered users
- `getUser(Long userId)`: Retrieves specific user profile
- `createUser(UserPostDTO userPostDTO)`: Registers new user
- `updateUser(Long userId, UserPutDTO userPutDTO, String currentUserIdStr)`: Updates user profile with permission validation
- `loginUser(UserPostDTO userPostDTO)`: Authenticates user login
- `logoutUser(Long userId)`: Logs out user

#### RefreshController.java
Handles WebSocket refresh triggers for real-time updates.

**Key Functions:**
- `triggerRefresh()`: Broadcasts refresh message to all connected clients

### Entities (`entity/`)

#### User.java
Represents registered users with authentication and statistics.

**Key Attributes:**
- Authentication: `username`, `password`, `token`
- Status: `status` (ONLINE/OFFLINE)
- Statistics: `totalGamesWon`, `totalGamesLost`, `totalGamesPlayed`
- Relationships: Many-to-many with Game
- Profile: `name`, `birthday`, `creationDate`

**Key Methods:**
- `increaseTotalGamesWon()`: Increments win counter
- `increaseTotalGamesLost()`: Increments loss counter  
- `increaseTotalGamesPlayed()`: Increments total games counter

#### Game.java
Central entity representing a Quoridor game session.

**Key Attributes:**
- Configuration: `sizeBoard`, `timeLimit`, `numberUsers`
- State: `gameStatus`, `currentTurn`
- Relationships: One-to-one with Board, many-to-many with Users
- Management: `creator`, `currentUsers`

**Key Methods:**
- `addUser(User user)`: Adds player to game
- `removeUser(User user)`: Removes player from game

#### Board.java
Represents the game board containing pawns and walls.

**Key Attributes:**
- `sizeBoard`: Board dimensions
- `pawns`: List of player pieces
- `walls`: List of placed walls
- Relationship: One-to-one with Game

**Key Methods:**
- `addPawn(Pawn pawn)`: Adds pawn to board
- `removePawn(Pawn pawn)`: Removes pawn from board
- `addWall(Wall wall)`: Adds wall to board
- `removeWall(Wall wall)`: Removes wall from board
- `getSizeBoard()`: Returns actual board size (2*sizeBoard-1)

#### Pawn.java
Represents player pieces on the board.

**Key Attributes:**
- Position: `r` (row), `c` (column)
- Identity: `userId`, `color`
- Relationship: Many-to-one with Board

#### Wall.java
Represents walls placed by players to block movement.

**Key Attributes:**
- Position: `r` (row), `c` (column)
- `orientation`: HORIZONTAL or VERTICAL
- Identity: `userId`, `color`
- Relationship: Many-to-one with Board

#### Move.java
Non-persistent entity representing a player's move action.

**Key Attributes:**
- `type`: MOVE_PAWN or ADD_WALL
- `endPosition`: Target position for pawn moves
- `wallPosition`: Position for wall placement
- `wallOrientation`: Orientation for wall placement
- `user`: Player making the move

### Services (`service/`)

#### GameService.java
Core business logic for game management and gameplay.

**Key Functions:**

**Game Management:**
- `getGames()`: Retrieves all games
- `getGame(Long gameId)`: Gets specific game with lazy loading
- `createGame(User user)`: Creates new game with initial board setup
- `joinGame(User user, Long gameId)`: Adds player to game and starts if full
- `delete(Long gameId, User forfeiter)`: Handles forfeit/deletion with statistics

**Gameplay:**
- `movePawn(Long gameId, Move move)`: Validates and executes pawn moves
- `placeWall(Long gameId, User user, int r, int c, WallOrientation orientation)`: Validates and places walls
- `canPlaceWall(Long gameId, User user)`: Checks if user has walls remaining
- `nextTurn(Long gameId)`: Advances turn to next player

**Game Components:**
- `getWalls(Long gameId)`: Retrieves all walls in game
- `getPawns(Long gameId)`: Retrieves all pawns in game

#### MoveService.java
Specialized service for move validation and pathfinding logic.

**Key Functions:**

**Path Validation:**
- `hasPathToGoal(Game game, Board board, Pawn pawn, int startR, int startC, List<Wall> walls)`: BFS algorithm to check if goal is reachable
- `wouldBlockAllPaths(Game game, Board board, List<Wall> existingWalls, int r, int c, WallOrientation orientation)`: Validates wall doesn't block all paths

**Move Validation:**
- `isValidPawnMove(Board board, Pawn pawn, int targetR, int targetC, List<Wall> walls)`: Comprehensive pawn move validation
- `isValidPawnMoveHasPath(...)`: Similar validation for pathfinding context
- `isValidJumpMove(Board board, Pawn pawn, int targetR, int targetC, List<Wall> walls)`: Validates jumping over adjacent pawns
- `isValidDiagonalJump(...)`: Validates diagonal jumps when straight jump blocked

**Position Validation:**
- `isValidPawnField(Board board, Pawn pawn, int targetR, int targetC)`: Checks if position valid for pawns
- `isValidWallField(Board board, int r, int c)`: Checks if position valid for walls
- `isWallBlockingPath(int startR, int startC, int targetR, int targetC, List<Wall> walls)`: Checks if walls block movement

**Utility:**
- `getGoalRow(Game game, Board board, Pawn pawn)`: Determines winning row for specific pawn

#### UserService.java
Manages user registration, authentication, and profile operations.

**Key Functions:**
- `getUsers()`: Returns all users
- `getUserById(Long id)`: Retrieves specific user
- `createUser(User userInput)`: Registers new user with validation
- `loginUser(String username, String password)`: Authenticates and logs in user
- `logoutUser(Long userId)`: Logs out user
- `updateUser(Long userId, Long currentUserId, UserPutDTO userPutDTO)`: Updates profile with permission checks

### Repositories (`repository/`)

#### GameRepository.java
JPA repository for Game entities.
- `findById(long id)`: Finds game by ID

#### UserRepository.java  
JPA repository for User entities.
- `findByName(String name)`: Finds user by name
- `findByUsername(String username)`: Finds user by username
- `findByToken(String token)`: Finds user by authentication token

#### BoardRepository.java
JPA repository for Board entities.
- `findById(long id)`: Finds board by ID

#### PawnRepository.java
JPA repository for Pawn entities.
- `findById(long id)`: Finds pawn by ID

#### WallRepository.java
JPA repository for Wall entities.
- `findById(long id)`: Finds wall by ID
- `findByBoardId(long boardId)`: Finds all walls on specific board
- `findByBoardIdAndUserId(long boardId, long userId)`: Finds walls placed by specific user

### DTOs (Data Transfer Objects) (`rest/dto/`)

#### GameGetDTO & GamePostDTO
Transfer objects for game information retrieval and creation.
- **Fields**: `id`, `numberUsers`, `sizeBoard`, `timeLimit`, `gameStatus`, `creator`, `currentUsers`
- **Usage**: API responses for game queries and game creation requests

#### UserGetDTO, UserPostDTO & UserPutDTO
Handle user data transfer for different operations.
- **UserGetDTO**: Complete user profile including statistics (`totalGamesWon`, `totalGamesLost`, `totalGamesPlayed`)
- **UserPostDTO**: User registration data (`username`, `password`, `name`, `birthday`)
- **UserPutDTO**: Profile update data (`username`, `birthday`)

#### MovePostDTO
Encapsulates player move requests.
- **Fields**: `type` (MOVE_PAWN/ADD_WALL), `endPosition`, `wallPosition`, `wallOrientation`, `user`
- **Usage**: Handles both pawn movements and wall placements in a single DTO

#### PawnGetDTO & WallGetDTO
Transfer objects for game board elements.
- **PawnGetDTO**: Position (`r`, `c`), `color`, `userId`, `boardId`
- **WallGetDTO**: Position (`r`, `c`), `orientation`, `color`, `userId`, `boardId`

#### GameStatusDTO
Lightweight DTO for game state updates.
- **Fields**: `id`, `currentTurn`, `gameStatus`
- **Usage**: WebSocket updates and turn progression notifications

### Data Mapping (`rest/mapper/`)

#### DTOMapper
MapStruct-based interface for automatic entity-DTO conversion.

**Key Mappings:**
- `convertEntityToUserGetDTO(User user)`: Entity to API response
- `convertUserPostDTOtoEntity(UserPostDTO dto)`: API request to entity
- `convertMovePostDTOtoEntity(MovePostDTO dto)`: Move request processing
- `updateUserFromDTO(UserPutDTO dto, User user)`: Profile updates

### Exception Handling (`exceptions/`)

#### GlobalExceptionAdvice
Centralized exception handling for consistent API responses.

**Key Functions:**
- `handleResponseStatusException()`: Custom error responses with proper HTTP status codes
- `handleConflict()`: Business logic conflicts (e.g., duplicate usernames)
- `handleTransactionSystemException()`: Database transaction errors

**Error Response Format:**
```json
{
  "timestamp": 1640995200000,
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid pawn move.",
  "path": "/game-lobby/1/move"
}
```

## Game Logic Implementation

### Board Coordinate System
The game uses a coordinate system where:
- Board size is represented as (2*size-1) to accommodate walls between cells
- For a 9x9 game, the actual grid is 17x17 (coordinates 0-16)
- Pawns can only be placed on even coordinates (0, 2, 4, ...)
- Walls can only be placed on odd coordinates (1, 3, 5, ...)

### Movement Rules

#### Pawn Movement
1. **Basic Movement**: Pawns move 2 units in cardinal directions (up, down, left, right)
2. **Jumping**: When a pawn is adjacent to another pawn, it can jump over it
3. **Diagonal Jumping**: When a straight jump is blocked by a wall, diagonal jumps are allowed
4. **Validation**: All moves check for board boundaries, wall blockages, and occupied spaces

#### Wall Placement
1. **Limitations**: Each player starts with 10 walls (5 in 4-player games)
2. **Orientation**: Walls can be horizontal or vertical
3. **Blocking Rules**: Walls cannot completely block any player's path to their goal
4. **Overlapping**: Walls cannot overlap or cross existing walls

### Game Flow

#### Game States
1. **WAITING_FOR_USER**: Game created, waiting for players
2. **RUNNING**: Game in progress, players taking turns
3. **ENDED**: Game finished (win/forfeit)

#### Turn Management
- Automatic turn progression after each valid move
- Circular turn order for multiple players
- Turn validation ensures only current player can move

#### Win Conditions
- Creator's pawn: Reach bottom row (row = boardSize-1)
- Joiner's pawn: Reach top row (row = 0)
- Game ends immediately when win condition met
- Statistics updated automatically (wins/losses/total games)

### Path Finding Algorithm

The server uses Breadth-First Search (BFS) to validate wall placements:

1. **Goal Validation**: Before placing a wall, check if all players still have a path to their goal
2. **Queue-based Search**: Uses queue to explore all possible moves from current position
3. **Wall Awareness**: Considers existing walls when determining valid moves
4. **Multiple Paths**: Ensures at least one path remains open for each player

## Real-time Updates

### WebSocket Integration
- Automatic game state synchronization
- Immediate updates on move completion
- Refresh triggers sent to all connected clients
- Handles connection management for multiplayer games

### Event Triggers
- Game creation/joining
- Move completion (pawn/wall)
- Game ending (win/forfeit)
- Turn changes

## API Response Patterns

### Success Responses
```json
{
  "id": 1,
  "gameStatus": "RUNNING",
  "currentTurn": {
    "id": 2,
    "username": "player1"
  },
  "board": {
    "sizeBoard": 17,
    "pawns": [...],
    "walls": [...]
  }
}
```

### Error Responses
```json
{
  "timestamp": "2024-01-01T12:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid pawn move.",
  "path": "/game-lobby/1/move"
}
```

## Security Considerations

### Authentication
- Token-based authentication for all protected endpoints
- User ID validation in headers for profile updates
- Permission checks for game actions

### Input Validation
- Move validation prevents invalid game states
- Path validation prevents game-breaking wall placements
- Boundary checks for all coordinates
- User ownership verification for game actions

## Performance Optimizations

### Database Queries
- Lazy loading for game relationships
- Efficient queries with JPA repositories
- Proper indexing on foreign keys

### Game Logic
- Early validation to prevent unnecessary computations
- Caching of game state during operations
- Optimized pathfinding algorithms

## Roadmap

### High-Priority Features for New Contributors

#### 1. 4-Player Game Support
**Description**: Extend the current 2-player system to support 4-player games
- **Implementation**: Modify game creation to accept player count parameter
- **Complexity**: Medium - requires turn management updates and board logic changes
- **Files to modify**: `GameService.java`, `MoveService.java`, game creation endpoints
- **Benefits**: Significantly expands gameplay possibilities

#### 2. Dynamic Board Sizes
**Description**: Allow players to choose board sizes (7x7, 9x9, 11x11)
- **Implementation**: Make board size configurable during game creation
- **Complexity**: Medium - affects coordinate systems and pathfinding
- **Files to modify**: `Board.java`, `GameService.java`, move validation logic
- **Benefits**: Provides variety and different game experiences

#### 3. Move Time Limits
**Description**: Add optional time constraints per move to increase game pace
- **Implementation**: Implement timer system with automatic turn progression
- **Complexity**: High - requires timer management and WebSocket notifications
- **Files to modify**: `GameService.java`, WebSocket handlers, frontend integration
- **Benefits**: Creates more dynamic and competitive gameplay

### Future Enhancements
- **Spectator Mode**: Allow users to watch ongoing games
- **Game History & Replay**: Store and replay completed games
- **Enhanced Statistics**: Detailed performance analytics
- **Tournament System**: Organize competitive events
- **AI Opponent**: Single-player mode with computer opponent

### Technical Improvements
- **Performance Optimization**: Database query optimization and caching
- **Stable Database**: Introduce a more robust Database
- **Enhanced Security**: Rate limiting and advanced authentication
- **Mobile Responsiveness**: Optimized mobile game interface
- **Internationalization**: Multi-language support

## Development

### Code Quality

- **Linting**: Follow Java coding standards
- **Formatting**: Use consistent code formatting
- **Testing**: Maintain high test coverage
- **Documentation**: Keep code well-documented

### Git Workflow

1. Create feature branches for new development
2. Link commits to GitHub issues
3. Ensure all tests pass before merging
4. Use meaningful commit messages

### Deployment

The application is configured for deployment on cloud platforms. Environment-specific configurations should be managed through application properties.

## Authors and acknowledgment

**Group 24 - SoPra FS25**

- **Tobias Lippuner** (22-730-592) - GitHub: [@Tolipp](https://github.com/Tolipp)
- **Moana Stadelmann** (19-607-357) - GitHub: [@MoanaStadelmann](https://github.com/MoanaStadelmann)
- **Sakshi Chaudhari** (24-744-716) - GitHub: [@DS-sakshi](https://github.com/DS-sakshi)
- **Dora Silva** (20-934-402) - GitHub: [@DorSilva](https://github.com/DorSilva)

**Supervisor**: Silvan Schlegel

## License

MIT License

Copyright (c) [2025] [SoPra FS25 Group 24 - University of Zürich]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---

For more information about the client-side implementation, see the [client repository](https://github.com/DS-sakshi/sopra-fs25-group-24-client).
