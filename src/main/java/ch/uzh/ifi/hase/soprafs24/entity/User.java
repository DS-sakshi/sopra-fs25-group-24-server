package ch.uzh.ifi.hase.soprafs24.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import com.fasterxml.jackson.annotation.JsonIgnore;



/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "app_user")
@JsonIgnoreProperties("games")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Date creationDate;

    @Column
    private Date birthday;

    @ManyToMany(mappedBy = "currentUsers", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<Game> games = new HashSet<>();

    @Column
    private int totalGamesWon;

    @Column
    private int totalGamesLost;

    @Column
    private int totalGamesPlayed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }

    public int getTotalGamesWon() {
        return totalGamesWon;
    }

    public void increaseTotalGamesWon() {
        this.totalGamesWon++;
    }

    public int getTotalGamesLost() {
        return totalGamesLost;
    }

    public void increaseTotalGamesLost() {
        this.totalGamesLost++;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void increaseTotalGamesPlayed() {
        this.totalGamesPlayed++;
    }
}