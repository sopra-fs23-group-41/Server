package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository("gameRepository")
public interface GameRepository extends JpaRepository <Game, Long>{

    @PersistenceContext
    EntityManager entityManager = null;

    static Game findByGamePin(String gamePIN){
        Query query = entityManager.createQuery("SELECT g FROM Game g WHERE g.gamePIN = :gamePIN");
        query.setParameter("gamePIN", gamePIN);
        try {
            return (Game) query.getSingleResult();
        } catch (NoResultException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    };

    Game findByLobbyId(Long id);
}
