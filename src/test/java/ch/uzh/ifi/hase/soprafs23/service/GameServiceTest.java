package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.asosapi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.entity.question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.question.Question;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game testGame;


    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        testGame = new Game();

        testGame.setGameId(2L);
        testGame.setGameType(GameType.SINGLE);
        testGame.setRounds(2);
        testGame.setNumOfPlayer(1);
        testGame.setCategory(Category.SNEAKERS);
        testGame.setGameMode(GameMode.GuessThePrice);

        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
    }

    @Test
    void createGame_success(){
        Game createdGame = gameService.createGame(testGame);
        Long id = createdGame.getGameId();

        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());
        assertNotNull(id);
        assertNotNull(createdGame.getGamePIN());
        assertEquals(testGame.getNumOfPlayer(), createdGame.getNumOfPlayer());
        assertEquals(testGame.getGameType(), createdGame.getGameType());
        assertEquals(testGame.getGameMode(), createdGame.getGameMode());
        assertEquals(testGame.getRounds(), createdGame.getRounds());
        assertEquals(testGame.getCategory(), createdGame.getCategory());
    }

    @Test
    void updateGameSetting_success(){
        Game createdGame = gameService.createGame(testGame);
        long id = createdGame.getGameId();
        Game updatedGame = new Game();
        updatedGame.setGameMode(GameMode.HighOrLow);
        updatedGame.setRounds(4);
        updatedGame.setNumOfPlayer(4);
        updatedGame.setCategory(Category.JEANS);

        Mockito.when(gameRepository.findByGameId(id)).thenReturn(createdGame);
        Game updated = gameService.updateGameSetting(updatedGame, id);

        Mockito.verify(gameRepository, Mockito.times(2)).save(Mockito.any());
        assertEquals(updatedGame.getGameMode(), updated.getGameMode());
        assertEquals(updatedGame.getRounds(), updated.getRounds());
        assertEquals(updatedGame.getNumOfPlayer(), updated.getNumOfPlayer());
        assertEquals(updatedGame.getCategory(), updated.getCategory());
    }

    @Test
    void findGame_doesNotExist_throwException(){
        Mockito.when(gameRepository.findByGameId(1L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> gameService.getGameById(1L));
    }

    @Test
    void getLobbyIdByGamePin_success(){
        Game createdGame = gameService.createGame(testGame);

        // it also verifies that the game pin is issued
        String pin = createdGame.getGamePIN();

        Mockito.when(gameRepository.findByGamePIN(pin)).thenReturn(createdGame);
        long id = gameService.getLobbyIdByGamePin(pin);
        assertEquals(createdGame.getGameId(), id);
    }

    @Test
    void gamePinIncorrect_throwException(){
        Mockito.when(gameRepository.findByGamePIN("123456")).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> gameService.getLobbyIdByGamePin("123456"));
    }
/*
    @Test
    void beginGame_success() throws UnirestException, JsonProcessingException {
        Game createdGame = gameService.createGame(testGame);
        long id = createdGame.getGameId();

        Mockito.when(gameRepository.findByGameId(id)).thenReturn(createdGame);

        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        player1.setGameId(id);
        players.add(player1);
        Mockito.when(playerRepository.save(players.get(0))).thenReturn(players.get(0));
        Mockito.when(playerRepository.findByGameId(id)).thenReturn(players);

        createdGame.startGame(GameMode.GuessThePrice, players);
        Mockito.when(gameRepository.save(createdGame)).thenReturn(createdGame);
        gameService.beginGame(id);

        assertNotNull(createdGame.getMiniGame());
        assertNotNull(createdGame.getArticleList());
        assertNotNull(createdGame.getMiniGame().get(0).getGameQuestions());
    }*/

    @Test
    void allPlayerJoinedCheck_true(){
        testGame.setGameId(2L);
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);

        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        player1.setGameId(2L);
        players.add(player1);

        Mockito.when(playerRepository.findByGameId(2L)).thenReturn(players);
        assertTrue(gameService.didAllPlayersJoin(2L));
    }

    @Test
    void canGetNextRound_andReturnAQuestion(){
        testGame.setGameId(2L);
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);

        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        player1.setGameId(2L);
        players.add(player1);

        Mockito.when(playerRepository.findByGameId(2L)).thenReturn(players);
        List<MiniGame> games = new ArrayList<>();
        MiniGame miniGame = new MiniGame();
        List<Question> questions = new ArrayList<>();
        Question question = new GuessThePriceQuestion();
        questions.add(question);
        miniGame.setGameQuestions(questions);
        games.add(miniGame);
        testGame.setMiniGame(games);
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);
        Question next = gameService.getNextRound(2L);

        assertEquals(1, testGame.getCurrentRound());
        assertTrue(next.isUsed());
    }

    @Test
    void getSameRoundQuestion_returnSameQuestion(){
        testGame.setGameId(2L);
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);

        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        player1.setGameId(2L);
        players.add(player1);

        Mockito.when(playerRepository.findByGameId(2L)).thenReturn(players);
        List<MiniGame> games = new ArrayList<>();
        MiniGame miniGame = new MiniGame();
        List<Question> questions = new ArrayList<>();
        Question question = new GuessThePriceQuestion();
        questions.add(question);
        miniGame.setGameQuestions(questions);
        games.add(miniGame);
        testGame.setMiniGame(games);
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);
        Question next = gameService.getNextRound(2L);
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);
        Question current = gameService.getCurrentRoundQuestion(2L);

        assertEquals(next, current);
    }

    @Test
    void saveAnswerToPlayer_success(){
        testGame.setGameId(2L);
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);

        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        player1.setGameId(2L);
        player1.setPlayerId(3L);
        players.add(player1);
        Mockito.when(playerRepository.findByPlayerId(3L)).thenReturn(player1);

        Answer answer = new Answer();
        answer.setPlayerId(3L);
        answer.setNumOfRound(1);
        answer.setPlayerAnswer("110.0");
        answer.setTimeUsed(30);
        Mockito.when(playerRepository.findByGameId(2L)).thenReturn(players);

        List<MiniGame> games = new ArrayList<>();
        MiniGame miniGame = new MiniGame();
        List<Question> questions = new ArrayList<>();
        Question question = new GuessThePriceQuestion();
        questions.add(question);
        miniGame.setGameQuestions(questions);
        games.add(miniGame);
        testGame.setMiniGame(games);
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);
        gameService.getNextRound(2L);
        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);

        player1.setAnswers(answer);
        player1.setRoundScore(20);
        player1.setTotalScore(20);

        Player calculated = gameService.calculatePlayerPoints(player1, 2L);
        gameService.savePlayerAnswer(3L, answer);

        Mockito.verify(gameRepository, Mockito.times(1)).save(Mockito.any());
        assertEquals(20, calculated.getTotalScore());
        assertEquals(0, calculated.getRoundScore());
    }

    @Test
    void endMiniGame_getRightLeaderBoard(){
        testGame.setGameId(2L);
        testGame.setNumOfPlayer(2);
        testGame.setGameType(GameType.MULTI);

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        List<Player> players = new ArrayList<>();
        Player player1 = new Player();
        player1.setGameId(testGame.getGameId());
        player1.setUserId(user1.getId());
        player1.setTotalScore(2000);
        players.add(player1);
        Player player2 = new Player();
        player2.setGameId(testGame.getGameId());
        player2.setUserId(user2.getId());
        player2.setTotalScore(3000);
        players.add(player2);

        List<MiniGame> game = new ArrayList<>();
        MiniGame miniGame = new MiniGame();
        List<Question> questions = new ArrayList<>();
        Question question1 = new GuessThePriceQuestion();
        question1.setUsed(true);
        questions.add(question1);
        Question question2 = new GuessThePriceQuestion();
        question2.setUsed(true);
        questions.add(question2);
        miniGame.setGameQuestions(questions);
        game.add(miniGame);
        testGame.setMiniGame(game);

        Mockito.when(gameRepository.findByGameId(2L)).thenReturn(testGame);
        Mockito.when(playerRepository.findByGameId(2L)).thenReturn(players);
        Mockito.when(userRepository.findById(player2.getUserId())).thenReturn(user2);

        List<Player> leaderBoard = gameService.endMiniGame(2L);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
        assertEquals(user2.getId(),leaderBoard.get(0).getUserId());
        assertEquals(user1.getId(), leaderBoard.get(1).getUserId());
        assertEquals(1, user2.getNumOfGameWon());
    }

}
