package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ch.uzh.ifi.hase.soprafs24.entity.Pawn;
import org.springframework.stereotype.Repository;

@Repository("pawnRepository")
public interface PawnRepository extends JpaRepository<Pawn, Long> {
  Pawn findById(long id);
}
