package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Wall;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("wallRepository")
public interface WallRepository extends JpaRepository<Wall, Long> {
    Wall findById(long id); //probs not needed yet, but would be useful for history and jsut to keep it cohesive
    
    //used board id instead of game id because of class diagram - we can change it later if needed - Dora
    //finds the wall by the board id
    List<Wall> findByBoardId(long gameId); 

    //finds the wall by the board id and player id
    List<Wall> findByBoardIdAndUserId(long boardId, long userId); 
}