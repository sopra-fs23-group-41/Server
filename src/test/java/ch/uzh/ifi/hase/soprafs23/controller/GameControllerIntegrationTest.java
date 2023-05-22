package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.asosapi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;

    @Test
    void beginGameTest() {
        //given users as players in a lobby with id 1 and where all players joined
        Game game = new Game();
        game.setGameId(1);
        game.setNumOfPlayer(1);
        game.setGameType(GameType.SINGLE);
        game.setRounds(4);
        game.setGamePIN("1234");
        game.setGameMode(GameMode.GuessThePrice);
        game.setCategory(Category.JEANS);

        //add player to game add save it
        Player player = new Player();
        player.setUserId(0);
        player.setPlayerName("Name");
        player.setGameId(1);

        //when
        //try to begin the game before game is in the database
        ResponseEntity<Void> postResponse = restTemplate.postForEntity("/lobbies/" + 1 + "/begin", null, Void.class);
        assertEquals(HttpStatus.NOT_FOUND, postResponse.getStatusCode());

        //add Game to database
        gameRepository.save(game);
        gameRepository.flush();

        //when
        //try to begin the game before all players joined
        postResponse = restTemplate.postForEntity("/lobbies/" + 1 + "/begin", null, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, postResponse.getStatusCode());

        //add missing player to game
        playerRepository.save(player);
        playerRepository.flush();

        //start game after all joined
        postResponse = restTemplate.postForEntity("/lobbies/" + 1 + "/begin", null, Void.class);

        Game finalGame = gameRepository.findByGameId(1L);

        //test if it worked
        assertEquals(HttpStatus.NO_CONTENT, postResponse.getStatusCode());
        assertTrue(gameService.isTheGameStarted(finalGame.getGameId()));
    }
}
