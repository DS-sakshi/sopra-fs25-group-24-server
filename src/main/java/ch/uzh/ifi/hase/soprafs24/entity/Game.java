package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.EAGER) // Define the relationship
    @JoinColumn(name = "creator_id", nullable = false) // Specify the foreign key column
    private User creator;

    // @Column(nullable = false)
    // private List<User> currentUsers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_turn_id", nullable = false)
    private User currentTurn;

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

    // public List<User> getCurrentUsers() {
    //     return currentUsers;
    // }

    // public void setCurrentUsers(List<User> currentUsers) {
    //     this.currentUsers = currentUsers;
    // }

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
}
