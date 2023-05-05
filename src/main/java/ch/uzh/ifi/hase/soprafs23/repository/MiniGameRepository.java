package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.MiniGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("miniGameRepository")
public interface MiniGameRepository extends JpaRepository <MiniGame, Long> {

    MiniGame  findMiniGameByMiniGameId(long minigameId);

}
