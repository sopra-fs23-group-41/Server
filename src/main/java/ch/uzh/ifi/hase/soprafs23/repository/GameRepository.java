package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository("gameRepository")
public interface GameRepository extends JpaRepository <Game, Long>{

    Game findByGamePIN(String gamePIN);

    @EntityGraph(attributePaths = { "miniGames"})
    Game findByGameId(Long lobbyId);

    @Transactional
    void deleteByGameId(long lobbyId);

}
