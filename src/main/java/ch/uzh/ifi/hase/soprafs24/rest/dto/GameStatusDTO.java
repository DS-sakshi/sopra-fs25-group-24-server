package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;

public class GameStatusDTO {

    private GameStatus gamestatus;

    public GameStatus getGameStatus() {
        return gamestatus;
    }

    public void setGameStatus(GameStatus status) {
        this.gamestatus = status;
    }

}
