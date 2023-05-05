package ch.uzh.ifi.hase.soprafs23.Entity;

import ch.uzh.ifi.hase.soprafs23.AsosApi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    private Game game;

    @BeforeEach
    public void setUp(){
        this.game = new Game();
        this.game.setGameId(0);
    }

    @Test
    public void updateGameSettingTest(){
        this.game.setGameType(GameType.SINGLE);
        this.game.updateGameSetting(GameMode.GuessThePrice, 3, 1, Category.SNEAKERS);

        assertEquals(GameMode.GuessThePrice, this.game.getGameMode());
        assertEquals(3, this.game.getRounds());
        assertEquals(1, this.game.getNumOfPlayer());
        assertEquals(Category.SNEAKERS, this.game.getCategory());
    }


    @Test
    public void checkNotAllPlayerExist(){
        this.game.setNumOfPlayer(2);
        List<Player> players = new ArrayList<>();
        players.add(new Player());

        assertFalse(this.game.checkIfAllPlayerExist(players));
    }

    @Test
    public void checkAllPlayerExist(){
        this.game.setNumOfPlayer(2);
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        assertTrue(this.game.checkIfAllPlayerExist(players));
    }

    @Test
    public void createGamePINTest(){
        this.game.createGamePIN();
        String str = this.game.getGamePIN();

        assertTrue((str != null));
        assertTrue((str.length() == 6));
        //assertTrue((str.matches("[a-z]+") && str.matches(".*\\d.*")));
    }

    @Test
    public void startGame_GuessThePrice_createsGame() throws UnirestException, JsonProcessingException {
        this.game.setGameType(GameType.MULTI);
        this.game.updateGameSetting(GameMode.GuessThePrice, 2, 2, Category.SNEAKERS);
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        if(this.game.checkIfAllPlayerExist(players)){
            this.game.startGame(this.game.getGameMode(), players);
        }

        assertNotNull(this.game.getMiniGame());
        assertEquals(2, this.game.getMiniGame().get(0).getRounds());
        assertNotNull(this.game.getMiniGame().get(0).getGameQuestions());
        assertEquals(2, this.game.getArticleList().size());
    }

    @Test
    public void startGame_HighOrLow_createsGame() throws UnirestException, JsonProcessingException {
        this.game.setGameType(GameType.MULTI);
        this.game.updateGameSetting(GameMode.HighOrLow, 2, 2, Category.JEWELRY);
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        this.game.startGame(this.game.getGameMode(), players);

        assertNotNull(this.game.getMiniGame());
        assertNotNull(this.game.getMiniGame().get(0).getGameQuestions());
        assertEquals(4, this.game.getArticleList().size());
    }

    @Test
    public void cannotStartGameBecauseNotAllPlayersJoined() throws UnirestException, JsonProcessingException {
        this.game.setGameType(GameType.MULTI);
        this.game.updateGameSetting(GameMode.GuessThePrice, 2, 2, Category.JEWELRY);
        List<Player> players = new ArrayList<>();
        players.add(new Player());

        assertThrows(ResponseStatusException.class, () -> game.startGame(this.game.getGameMode(), players) );
    }

    @Test
    public void checkNotAllPlayerAnsweredTest(){
        List<Article> articles = new ArrayList<>();
        articles.add(new Article());
        MiniGame miniGame = new MiniGame(2, articles, GameMode.GuessThePrice);
        List<MiniGame> aList = new ArrayList<>();
        aList.add(miniGame);
        this.game.setMiniGame(aList);
        this.game.getMiniGame().get(0).setCurrentRound(2);
        List<Player> players1 = new ArrayList<>();
        players1.add(new Player());
        players1.add(new Player());

        assertFalse(game.checkIfAllPlayersAnswered(players1));
    }

    @Test
    public void checkAllPlayersAnsweredTest() {
        // Test case when all players have answered
        List<Article> articles = new ArrayList<>();
        articles.add(new Article());
        this.game.setArticleList(articles);
        MiniGame miniGame = new MiniGame(2, articles, GameMode.GuessThePrice);
        List<MiniGame> aList = new ArrayList<>();
        aList.add(miniGame);
        this.game.setMiniGame(aList);
        this.game.getMiniGame().get(0).setCurrentRound(1);
        List<Player> players2 = new ArrayList<>();
        Player alice = new Player();
        alice.setAnswers(new Answer());
        players2.add(alice);
        Player bob = new Player();
        bob.setAnswers(new Answer());
        players2.add(bob);
        assertTrue(game.checkIfAllPlayersAnswered(players2));
    }

    @Test
    public void cannotGetNextRoundBecauseMiniGameNotExist(){
        List<Player> players1 = new ArrayList<>();
        players1.add(new Player());
        players1.add(new Player());

        assertThrows(NullPointerException.class, () -> this.game.getNextRound(players1));
    }

    @Test
    public void cannotGetNextRoundBecauseNotAllPlayersAnswered() throws UnirestException, JsonProcessingException {
        List<Player> players2 = new ArrayList<>();
        Player alice = new Player();
        alice.setAnswers(new Answer());
        players2.add(alice);
        Player bob = new Player();
        bob.setAnswers(new Answer());
        players2.add(bob);

        this.game.setGameType(GameType.MULTI);
        this.game.updateGameSetting(GameMode.HighOrLow, 2, 2, Category.SNEAKERS);
        this.game.startGame(GameMode.HighOrLow, players2);
        this.game.getMiniGame().get(0).setCurrentRound(2);

        assertThrows(ResponseStatusException.class, () -> this.game.getNextRound(players2));
    }

    @Test
    public void getNextRoundTest() throws UnirestException, JsonProcessingException {
        this.game.setGameType(GameType.MULTI);
        this.game.updateGameSetting(GameMode.HighOrLow, 2, 2, Category.HOODIES);
        List<Player> players = new ArrayList<>();
        players.add(new Player());
        players.add(new Player());

        this.game.startGame(this.game.getGameMode(), players);

        assertNotNull(this.game.getNextRound(players));
    }

    @Test
    public void getCurrentRoundQuestionTest() throws UnirestException, JsonProcessingException {
        List<Player> players = new ArrayList<>();
        Player alice = new Player();
        alice.setAnswers(new Answer());
        players.add(alice);
        this.game.setGameType(GameType.SINGLE);
        this.game.updateGameSetting(GameMode.GuessThePrice, 2, 1, Category.HOODIES);
        this.game.startGame(GameMode.GuessThePrice, players);
        this.game.getMiniGame().get(0).setCurrentRound(1);

        assertNotNull(this.game.getCurrentRoundQuestion());
    }

    @Test
    public void endGameTest() throws UnirestException, JsonProcessingException {
        List<Player> players = new ArrayList<>();
        Player alice = new Player();
        alice.setAnswers(new Answer());
        players.add(alice);
        this.game.setGameType(GameType.SINGLE);
        this.game.updateGameSetting(GameMode.GuessThePrice, 1, 1, Category.ACCESSORIES);
        this.game.startGame(GameMode.GuessThePrice, players);
        this.game.getMiniGame().get(0).setCurrentRound(1);
        this.game.getMiniGame().get(0).getGameQuestions().get(0).setUsed(true);

        assertNotNull(this.game.endGame(players));
    }


}
