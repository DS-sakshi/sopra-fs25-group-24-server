# Quoridor Server - SoPra FS25 Group 24

An online multiplayer implementation of the classic board game Quoridor, built with Spring Boot. This server provides RESTful APIs and WebSocket connections for real-time gameplay.

## Table of Contents
- [Introduction](#introduction)
- [Technologies](#technologies)
- [High-Level Components](#high-level-components)
- [Launch & Deployment](#launch--deployment)
- [Roadmap](#roadmap)
- [Authors and Acknowledgment](#authors-and-acknowledgment)
- [License](#license)

## Introduction 

Quoridor Online is a digital adaptation of the classic board game Quoridor, designed to connect players across geographical distances. The game challenges players to strategically navigate their pawns to the opposite side of the board while placing walls to obstruct opponents. 
Our implementation offers a web-based platform where users can register, create profiles, and engage in turn-based gameplay with friends or other online players. 

The motivation behind this project was to create an accessible version of a beloved board game that preserves its strategic depth while adding digital enhancements like customizable board sizes, time limits, and an AI chatbot assistant to help new players learn the rules and strategies. 

## Technologies

- **Framework**: Spring Boot 2.7+ - For rapid, production-ready application development. For Frontend React framework Next.js
- **Language**: Java 17 - Latest LTS version with modern language features
- **Database**: H2 (development) - Lightweight database
- **Build Tool**: Gradle 7.0+ - Dependency management and build automation. Docker: used to simplify building, testing and deploying application
- **Testing**: JUnit 5, Mockito - Comprehensive testing framework. SonarQube to measure Code Quality
- **Authentication**: Token-based - Secure user session management
- **ORM**: JPA/Hibernate - Object-relational mapping for database operations
- **connection Client and Server**: REST endpoints with comprehensive error handling. WebSockets - For live game state synchronization
- **Deployment**: Google Cloud: handling server deployment. Vercel: handling client deployment and hosting client


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
   Inside the repository folder (with ls you can list files) there is a bash script setup.sh that will install everything you need, according to the system you are using.
   Run the following command and follow the instructions

   ```bash
   source setup.sh
   ```
3. **Build the project**
You can use the local Gradle Wrapper to build the application.
macOS: ./gradlew
Linux: ./gradlew
Windows: ./gradlew.bat

   ```bash
   ./gradlew build
   ```
2.1. **Build the project without tests**
   ```bash
   ./gradlew build -x test
   ```
2.2. **Just Runt the tests**
   ```bash
    ./gradlew test
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

### Release
A release is triggered with each push to the main branch of the online repository. 
### Git Workflow
1. Create feature branches for new development
2. Link commits to GitHub issues
3. Ensure all tests pass before merging
4. Use meaningful commit messages

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
