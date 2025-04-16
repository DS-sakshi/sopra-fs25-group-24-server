package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.MoveType;
import ch.uzh.ifi.hase.soprafs24.constant.WallOrientation;

import javax.persistence.*;
import java.util.List;

public class Move {

    @ElementCollection
    private List<Integer> endPosition;

    @ElementCollection
    private List<Integer> wallPosition;

    @ElementCollection
    private WallOrientation wallOrientation;

    private MoveType type;

    private User user;

    public List<Integer> getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(List<Integer> endPosition) {
        this.endPosition = endPosition;
    }

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Integer> getWallPosition() {
        return wallPosition;
    }

    public void setWallPosition(List<Integer> wallPosition) {
        this.wallPosition = wallPosition;
    }

    public WallOrientation getWallOrientation() {
        return wallOrientation;
    }

    public void setWallOrientation(WallOrientation wallOrientation) {
        this.wallOrientation = wallOrientation;
    }
}
