package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.constant.MoveType;


import java.util.List;

public class MovePostDTO {

    private List<Integer> endPosition;
    private User user;
    private MoveType type;
    private List<Integer> wallPosition;
    private String wallOrientation;

    public List<Integer> getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(List<Integer> endPosition) {
        this.endPosition = endPosition;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public List<Integer> getWallPosition() {
        return wallPosition;
    }

    public void setWallPosition(List<Integer> wallPosition) {
        this.wallPosition = wallPosition;
    }

    public String getWallOrientation() {
        return wallOrientation;
    }
    
    public void setWallOrientation(String wallOrientation) {
        this.wallOrientation = wallOrientation;
    }
}
