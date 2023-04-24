package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.AsosApi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameRepository.class);

    private final GameRepository gameRepository;

    private final PlayerRepository playerRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       @Qualifier("playerRepository")PlayerRepository playerRepository){
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public List<Game> getGame(){
        return this.gameRepository.findAll();
    }

    public Game createGame(Game newGame) {
        newGame.setGamePIN();
        newGame.setGameMode(GameMode.GuessThePrice);
        newGame.setCategory(Category.SHOES);

        newGame = gameRepository.save(newGame);
        gameRepository.flush();

        log.debug("A new Lobby has started: {}", newGame);
        return newGame;
    }

    public Game getGameById(long lobbyId) {
        return gameRepository.findByLobbyId(lobbyId);
    }

    public void beginGame(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        currentGame.startGame(currentGame.getGameMode());

        gameRepository.save(currentGame);
        gameRepository.flush();

        log.debug("A new game has initialized and ready to start");
    }

    public Boolean didAllPlayersJoin(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        return currentGame.checkIfAllPlayerExist();
    }

    public Question getNextQuestion(long lobbyId) {
        Game currenteGame = getGameById(lobbyId);
        Question nextQuestion = currenteGame.getNextRound();

        //keep the information updated in the repository
        gameRepository.save(currenteGame);
        gameRepository.flush();

        return  nextQuestion;
    }

    public void savePlayerAnswer(long playerId, Answer answer) {
        Player currentPlayer = playerRepository.findByPlayerId(playerId);
        currentPlayer.setAnswers(answer);
        playerRepository.save(currentPlayer);
        playerRepository.flush();
        long currentLobbyId = currentPlayer.getGameId();
        Game currentGame = getGameById(currentLobbyId);
        currentGame.syncPlayerInformation(currentPlayer);
        currentGame.updatePlayerPoints(); //make player as arg, return a player
        //TODO: keep playerRepo points updated
        gameRepository.save(currentGame);
        gameRepository.flush();

        log.debug("The answer of player with ID: {} has been saved!",playerId);
    }

    public boolean didAllPlayersAnswer(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        return currentGame.checkIfAllPlayersAnswered();
    }

    public List<Player> endGame(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        return currentGame.endGame();
    }
}
