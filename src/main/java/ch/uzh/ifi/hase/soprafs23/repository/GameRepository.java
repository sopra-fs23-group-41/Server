package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository("gameRepository")
public interface GameRepository extends JpaRepository <Game, Long>{

    Game findByGamePIN(String gamePIN);

    Game findByGameId(Long lobbyId);

    @Transactional
    void deleteByGameId(long lobbyId);
}
