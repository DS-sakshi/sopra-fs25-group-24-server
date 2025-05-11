package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

@Entity
@Table(name = "board")
public class Board implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int sizeBoard;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Pawn> pawns = new ArrayList<>();
    
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Wall> walls = new ArrayList<>();

    //easier to move between the two Services, makes also sense to have it here
    @OneToOne(mappedBy = "board")
    private Game game;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSizeBoard() {
        return (2*sizeBoard-1);
    }

    public void setSizeBoard(int sizeBoard) {
        this.sizeBoard = sizeBoard;
    }

    public List<Pawn> getPawns() {
        return pawns;
    }

    public void setPawns(List<Pawn> pawns) {
        this.pawns = pawns;
    }


    public void addPawn(Pawn pawn) {
        pawns.add(pawn);
        pawn.setBoard(this);
    }

    public void removePawn(Pawn pawn) {
        pawns.remove(pawn);
        pawn.setBoard(null);
    }

    public List<Wall> getWalls() {
        return walls;
    }
    public void setWalls(List<Wall> walls) {
        this.walls = walls;
    }
     public void addWall(Wall wall) {
        walls.add(wall);
        wall.setBoard(this);
    }

    public void removeWall(Wall wall) {
        walls.remove(wall);
        wall.setBoard(null);
    }
    
    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }


}
