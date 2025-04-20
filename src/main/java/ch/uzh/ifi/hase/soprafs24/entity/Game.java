package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;


@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int numberUsers;

    @Column(nullable = false)
    private int sizeBoard;

    @Column(nullable = true)
    private int timeLimit;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "creator_id", nullable = false) 
    private User creator;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "game_users",
        joinColumns = @JoinColumn(name = "game_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> currentUsers = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_turn_id", nullable = false)
    private User currentTurn;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", unique = true)
    private Board board;

    @Column(nullable = false)
    private GameStatus gameStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumberUsers() {
        return numberUsers;
    }

    public void setNumberUsers(int numberUsers) {
        this.numberUsers = numberUsers;
    }

    public int getSizeBoard() {
        return sizeBoard;
    }

    public void setSizeBoard(int sizeBoard) {
        this.sizeBoard = sizeBoard;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<User> getCurrentUsers() {
        return currentUsers;
    }

    public void setCurrentUsers(Set<User> currentUsers) {
        this.currentUsers = currentUsers;
    }

    public void addUser(User user) {
        this.currentUsers.add(user);
        user.getGames().add(this);
    }

    public void removeUser(User user) {
        this.currentUsers.remove(user);
        user.getGames().remove(this);
    }

    public User getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(User currentTurn) {
        this.currentTurn = currentTurn;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
