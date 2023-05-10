package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Transactional
public class PlayerRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void findByUserId_success(){
        // given
        Player player = new Player();
        player.setUserId(1);
        player.setGameId(2);
        player.setRoundScore(20);
        player.setTotalScore(40);
        player.setStreak(2);
        player.setAnswers(new Answer());
        player.setPlayerName("aa");

        entityManager.persist(player);
        entityManager.flush();

        // when
        Player found = playerRepository.findByUserId(player.getUserId());

        // then
        assertNotNull(found.getPlayerId());
        assertNotNull(found.getAnswers());
        assertEquals(found.getUserId(), player.getUserId());
        assertEquals(found.getGameId(), player.getGameId());
        assertEquals(found.getStreak(), player.getStreak());
        assertEquals(found.getRoundScore(),player.getRoundScore());
        assertEquals(found.getTotalScore(), player.getTotalScore());
    }

}
