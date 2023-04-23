package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByPlayerId(long id);

    Player findByUserId(long id);

    List<Player> findByGameId(long id);

    long deleteByGameId(long gameId);

}