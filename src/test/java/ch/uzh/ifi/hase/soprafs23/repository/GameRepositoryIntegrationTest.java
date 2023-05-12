package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.asosapi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class GameRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void findByGamePIN_success(){
        // given
        Game game = new Game();
        game.setNumOfPlayer(2);
        game.setGameType(GameType.MULTI);
        game.setRounds(2);
        game.setGamePIN("aaaaaa");
        game.setGameMode(GameMode.GuessThePrice);
        game.setCategory(Category.SNEAKERS);

        List<Article> articles = new ArrayList<>();
        articles.add(new Article());
        List<MiniGame> miniGames = new ArrayList<>();
        miniGames.add(new MiniGame());

        game.setArticleList(articles);
        game.setMiniGame(miniGames);

        entityManager.persist(game);
        entityManager.flush();

        //when
        Game found = gameRepository.findByGamePIN(game.getGamePIN());

        //then
        Long id = found.getGameId();
        assertNotNull(id);
        assertNotNull(found.getMiniGame());
        assertNotNull(found.getArticleList());
        assertEquals(found.getGameMode(), game.getGameMode());
        assertEquals(found.getCategory(), game.getCategory());
        assertEquals(found.getGameType(), game.getGameType());
        assertEquals(found.getRounds(), game.getRounds());
        assertEquals(found.getGamePIN(), game.getGamePIN());
        assertEquals(found.getNumOfPlayer(), game.getNumOfPlayer());
    }
}
