package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.asosapi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.question.HigherLowerQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.question.Question;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class GameServiceIntegrationTest {

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    Game newGame = new Game();

    Player player = new Player();

    @BeforeEach
    void setup(){
        newGame.setGameType(GameType.MULTI);
        newGame.setNumOfPlayer(1);
        newGame.setCategory(Category.JACKETS);
        newGame.setRounds(2);
        newGame.setGameId(1L);
        newGame.setGamePIN("123456");

        player.setGameId(newGame.getGameId());
        player.setPlayerName("Aa");
        player.setPlayerId(1L);
        playerRepository.save(player);
    }

    @AfterEach
    void cleanUp(){
        gameRepository.deleteAll();
        playerRepository.deleteAll();
    }

    @Test
    void createNewGameLobby_success(){
        newGame.setGameMode(GameMode.Mix);

        Game game = gameService.createGame(newGame);

        assertEquals(newGame.getGameType(), game.getGameType());
        assertEquals(newGame.getGameMode(), game.getGameMode());
        assertEquals(newGame.getGameMode(), game.getGameMode());
        assertEquals(newGame.getCategory(), game.getCategory());
        assertEquals(newGame.getNumOfPlayer(), game.getNumOfPlayer());
        //verify pin code is generated and provided #85
        assertNotNull(game.getGamePIN());
    }

    @Test
    void updateGameSetting_success(){
        newGame.setGameMode(GameMode.Mix);
        gameRepository.save(newGame);
        gameRepository.flush();

        Game theGame = gameRepository.findByGamePIN("123456");

        Game current = new Game();
        current.setRounds(4);
        current.setGameMode(GameMode.MostExpensive);
        current.setCategory(Category.T_SHIRTS);
        current.setNumOfPlayer(2);

        Game game = gameService.updateGameSetting(current, theGame.getGameId());

        assertEquals(current.getNumOfPlayer(), game.getNumOfPlayer());
        assertEquals(current.getCategory(), game.getCategory());
        assertEquals(current.getGameMode(), game.getGameMode());
        assertEquals(current.getRounds(), game.getRounds());
    }

    // begin game tests use actual api calls, which verify the api is working properly #111 #103
    // the questions are created within this method, verifies #102
    @Test
    void beginGame_ME_success() throws UnirestException, JsonProcessingException {
        newGame.setGameMode(GameMode.MostExpensive);
        gameRepository.save(newGame);

        Game theGame = gameRepository.findByGamePIN("123456");
        player.setGameId(theGame.getGameId());
        playerRepository.save(player);

        gameService.beginGame(theGame.getGameId());
        Game game = gameRepository.findByGameId(theGame.getGameId());

        assertEquals(2, game.getMiniGame().get(0).getRounds());
        assertEquals(GameMode.MostExpensive, game.getMiniGame().get(0).getGameMode());
    }

    @Test
    void beginGame_HOL_success() throws UnirestException, JsonProcessingException {
        newGame.setGameMode(GameMode.Mix);
        gameRepository.save(newGame);
        Game theGame = gameRepository.findByGamePIN("123456");
        player.setGameId(theGame.getGameId());
        playerRepository.save(player);

        gameService.beginGame(theGame.getGameId());
        Game game = gameRepository.findByGameId(theGame.getGameId());

        assertEquals(2, game.getMiniGame().get(0).getRounds());
        assertEquals(GameMode.Mix, game.getMiniGame().get(0).getGameMode());

    }

    @Test
    void cannotBeginGame_NotAllPlayerExist_OrNoLobby(){
        newGame.setGameMode(GameMode.GuessThePrice);
        newGame.setNumOfPlayer(2);
        long id = newGame.getGameId();
        gameRepository.save(newGame);

        playerRepository.save(player);

        assertThrows(ResponseStatusException.class, () -> gameService.beginGame(id));
        assertThrows(ResponseStatusException.class, () -> gameService.beginGame(2L));
    }

    @Test
    void getNextRoundQuestion_success(){
        newGame.setGameMode(GameMode.HighOrLow);
        MiniGame miniGame = new MiniGame();
        HigherLowerQuestion que1 = new HigherLowerQuestion();
        que1.setTrueAnswer("Higher");
        HigherLowerQuestion que2 = new HigherLowerQuestion();
        que2.setTrueAnswer("lower");
        miniGame.setGameQuestions(List.of(que1, que2));
        newGame.setMiniGame(List.of(miniGame));
        gameRepository.save(newGame);

        Game theGame = gameRepository.findByGamePIN("123456");
        player.setGameId(theGame.getGameId());
        playerRepository.save(player);

        Question question = gameService.getNextRound(theGame.getGameId());

        assertEquals("Higher", question.getTrueAnswer());
        assertEquals(1, gameRepository.findByGameId(theGame.getGameId()).getMiniGame().get(0).getCurrentRound());
    }

    @Test
    void getCurrentRoundQuestion_same(){
        newGame.setGameMode(GameMode.HighOrLow);
        MiniGame miniGame = new MiniGame();
        HigherLowerQuestion que1 = new HigherLowerQuestion();
        que1.setTrueAnswer("Higher");
        HigherLowerQuestion que2 = new HigherLowerQuestion();
        que2.setTrueAnswer("lower");
        miniGame.setGameQuestions(List.of(que1, que2));
        newGame.setMiniGame(List.of(miniGame));
        gameRepository.save(newGame);
        playerRepository.save(player);

        gameService.getNextRound(newGame.getGameId());
        Question current = gameService.getCurrentRoundQuestion(newGame.getGameId());

        assertEquals("Higher", current.getTrueAnswer());
        assertEquals(1, gameRepository.findByGameId(newGame.getGameId()).getMiniGame().get(0).getCurrentRound());
    }

    @Test
    void savePlayerAnswer_success_andVerifyAllPlayersAnswered(){
        newGame.setGameMode(GameMode.HighOrLow);
        MiniGame miniGame = new MiniGame();
        HigherLowerQuestion que1 = new HigherLowerQuestion();
        que1.setTrueAnswer("Higher");
        HigherLowerQuestion que2 = new HigherLowerQuestion();
        que2.setTrueAnswer("lower");
        miniGame.setGameQuestions(List.of(que1, que2));
        miniGame.setCurrentRound(1);
        newGame.setMiniGame(List.of(miniGame));
        gameRepository.save(newGame);

        Game theGame = gameRepository.findByGamePIN("123456");
        player.setGameId(theGame.getGameId());
        playerRepository.save(player);

        Player thePlayer = playerRepository.findByGameId(theGame.getGameId()).get(0);

        Answer answer = new Answer();
        answer.setPlayerAnswer("Higher");
        answer.setTimeUsed(20);

        gameService.savePlayerAnswer(thePlayer.getPlayerId(), answer);

        Player current = playerRepository.findByPlayerId(thePlayer.getPlayerId());

        assertEquals(90, current.getRoundScore());
        assertEquals(90, current.getTotalScore());
        assertEquals(1, current.getStreak());
        assertTrue(gameService.didAllPlayersAnswer(theGame.getGameId()));
    }

    //test for #93
    @Test
    void getAllArticles_success() throws UnirestException, JsonProcessingException {
        newGame.setGameMode(GameMode.Mix);
        newGame.setRounds(3);
        gameRepository.save(newGame);

        Game theGame = gameRepository.findByGamePIN("123456");
        player.setGameId(theGame.getGameId());
        playerRepository.save(player);

        gameService.beginGame(theGame.getGameId());

        assertEquals(7, gameService.getAllArticles(theGame.getGameId()).size());
    }


    @Test
    void gameDataIsCleanedAfterGameEnd(){
        gameService.createGame(newGame);
        Player player = new Player();
        player.setGameId(newGame.getGameId());
        player.setPlayerName("Aa");
        player.setPlayerId(1L);
        playerRepository.save(player);

        gameService.clearLobby(newGame.getGameId(), player.getPlayerId());

        assertNull(gameRepository.findByGameId(newGame.getGameId()));
        assertNull(playerRepository.findByPlayerId(newGame.getGameId()));

    }



}
