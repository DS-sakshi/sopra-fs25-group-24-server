package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.MoveType;


import javax.persistence.*;
import java.util.List;

public class Move {

    @ElementCollection
    private List<Integer> startPosition;

    @ElementCollection
    private List<Integer> endPosition;

    private MoveType type;

    private User user;

    
    public List<Integer> getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(List<Integer> startPosition) {
        this.startPosition = startPosition;
    }

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
}
