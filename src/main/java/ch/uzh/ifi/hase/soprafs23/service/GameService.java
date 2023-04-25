package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.AsosApi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.GameJudge;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepo;
//import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
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
    //private final GameRepository gameRepository;

    private final PlayerRepository playerRepository;

    private final UserRepository userRepository;
    private long gameId = 0;
    Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    public GameService(/*@Qualifier("gameRepository") GameRepository gameRepository,*/ @Qualifier("playerRepository")PlayerRepository playerRepository,
            @Qualifier("userRepository")UserRepository userRepository){
        //this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
    }

    public Game createGame(Game newGame) {
        newGame.createGamePIN();
        newGame.setGameMode(GameMode.GuessThePrice);
        newGame.setCategory(Category.SHOES);
        newGame.setGameId(gameId);
        gameId++;

        removePlayersFromLobby(newGame.getGameId());
        //newGame = gameRepository.save(newGame);
        //gameRepository.flush();

        GameRepo.addGame((int) newGame.getGameId(), newGame);


        logger.debug("A new Lobby has started: {}", newGame);
        return newGame;
    }

    public Game updateGameSetting(Game currentGame, long lobbyId) throws UnirestException, JsonProcessingException {
        Game game = GameRepo.findByLobbyId((int) lobbyId);
        game.updateGameSetting(currentGame.getGameMode(),currentGame.getRounds(), currentGame.getNumOfPlayer(),currentGame.getCategory());
        //gameRepository.save(game);
        //gameRepository.flush();
        return game;
    }

    public Game getGameById(long lobbyId) {
        //return gameRepository.findByGameId(lobbyId);
        return GameRepo.findByLobbyId((int) lobbyId);
    }
    public long getLobbyIdByGamePin(String gamePin){
        return GameRepo.findByGamePin(gamePin).getGameId();
    }

    public void beginGame(long lobbyId) throws UnirestException, JsonProcessingException {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        currentGame.startGame(currentGame.getGameMode(), players);

        //gameRepository.save(currentGame);
        //gameRepository.flush();

        logger.debug("A new game has initialized and ready to start");
    }

    private void removePlayersFromLobby(long gameId){
        List<Player> playerList = playerRepository.findByGameId(gameId);
        for (Player player : playerList){
            logger.info("Player with Id : " + player.getPlayerName() + "deleted!");
            playerRepository.deleteById(player.getPlayerId());
        }
    }

    public Boolean didAllPlayersJoin(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        return currentGame.checkIfAllPlayerExist(players);
    }

    public Question getNextRound(long lobbyId) {
        Game currenteGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        Question nextQuestion = currenteGame.getNextRound(players);

        //keep the information updated in the repository
        //gameRepository.save(currenteGame);
        //gameRepository.flush();

        return  nextQuestion;
    }

    public Question getCurrentRoundQuestion(long lobbyId){
        Game currentGame = getGameById(lobbyId);

        return currentGame.getCurrentRoundQuestion();
    }

    public void savePlayerAnswer(long playerId, Answer answer) {
        Player currentPlayer = playerRepository.findByPlayerId(playerId);
        currentPlayer.setAnswers(answer);
        Player updatedPlayer = calculatePlayerPoints(currentPlayer, currentPlayer.getGameId());
        playerRepository.save(updatedPlayer);
        playerRepository.flush();

        logger.debug("The answer of player with ID: {} has been saved!", playerId);
    }

    public boolean didAllPlayersAnswer(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        return currentGame.checkIfAllPlayersAnswered(players);
    }

    public List<Player> endGame(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        playerRepository.deleteByGameId(lobbyId);
        //gameRepository.deleteByGameId(lobbyId);
        GameRepo.removeGame((int) lobbyId);
        return currentGame.endGame(players);
    }
    public Player calculatePlayerPoints(Player player, long lobbyId){
        //Game game = gameRepository.findByGameId(lobbyId);
        Game game = GameRepo.findByLobbyId((int) lobbyId);
        int currentRound = game.getCurrentRound();
        GameJudge judge = new GameJudge(game.getMiniGame().getGameQuestions().get(currentRound-1), player, currentRound);
        int point = judge.calculatePoints();
        player.setRoundScore(point);
        player.setTotalScore(player.getTotalScore()+point);

        return player;
    }

    public boolean isTheGameStarted(long lobbyId) {
        Game currentGame = GameRepo.findByLobbyId((int)lobbyId);
        return (currentGame.getMiniGame().getGameQuestions().size() > 0);
    }
}
