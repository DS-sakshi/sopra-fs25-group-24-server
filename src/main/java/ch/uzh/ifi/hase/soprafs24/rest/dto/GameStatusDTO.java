package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

public class GameStatusDTO {

    private Long id;
    private UserGetDTO currentTurn;
    private GameStatus gamestatus;

    public GameStatus getGameStatus() {
        return gamestatus;
    }

    public void setGameStatus(GameStatus status) {
        this.gamestatus = status;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }   
    public UserGetDTO getCurrentTurn() {
        return currentTurn;
    }
    public void setCurrentTurn(UserGetDTO currentTurn) {
        this.currentTurn = currentTurn;
    }
}
