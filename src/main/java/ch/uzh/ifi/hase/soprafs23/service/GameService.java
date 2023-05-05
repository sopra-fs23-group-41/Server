package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;

    private final MiniGameRepository miniGameRepository;

    private final QuestionRepository questionRepository;

    private final PlayerRepository playerRepository;

    private final UserRepository userRepository;
    private long gameId = 0;
    Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("playerRepository")PlayerRepository playerRepository,
            @Qualifier("userRepository")UserRepository userRepository, @Qualifier("miniGameRepository")MiniGameRepository miniGameRepository, @Qualifier("questionRepository")QuestionRepository questionRepository){
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        this.miniGameRepository = miniGameRepository;
        this.questionRepository = questionRepository;
    }

    public Game createGame(Game newGame) {
        newGame.createGamePIN();
        newGame.setGameMode(GameMode.GuessThePrice);
        newGame.setGameId(gameId);
        newGame.setRounds(2);
        gameId++;

        removePlayersFromLobby(newGame.getGameId());
        gameRepository.save(newGame);
        gameRepository.flush();

        //GameRepo.addGame((int) newGame.getGameId(), newGame);

        logger.debug("A new Lobby has started: {}", newGame);
        return newGame;
    }

    public Game updateGameSetting(Game currentGame, long lobbyId) {
        Game game = gameRepository.findByGameId(lobbyId);
        //Game game = GameRepo.findByLobbyId((int) lobbyId);
        game.updateGameSetting(currentGame.getGameMode(),currentGame.getRounds(), currentGame.getNumOfPlayer(),currentGame.getCategory());

        gameRepository.save(game);
        gameRepository.flush();

        return game;
    }

    public Game getGameById(long lobbyId) {
        return gameRepository.findByGameId(lobbyId);
        //return GameRepo.findByLobbyId((int) lobbyId);
    }
    public long getLobbyIdByGamePin(String gamePin){
        return gameRepository.findByGamePIN(gamePin).getGameId();
        //return GameRepo.findByGamePin(gamePin).getGameId();
    }

    public void beginGame(long lobbyId) throws UnirestException, JsonProcessingException {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        currentGame.startGame(currentGame.getGameMode(), players);
        //MiniGame currentMiniGame = currentGame.getMiniGame();

        gameRepository.save(currentGame);
        gameRepository.flush();

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
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);

        //keep the information updated in the repository
        gameRepository.save(currentGame);
        gameRepository.flush();

        return currentGame.getNextRound(players);
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

    public List<Player> endMiniGame(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);

        List<Player> leaderBoard = currentGame.endGame(players);

        // Check if there are multiple winners
        List<Player> winners = new ArrayList<>();
        long topScore = leaderBoard.get(0).getTotalScore();
        for (Player player : leaderBoard) {
            if (player.getTotalScore() == topScore) {
                winners.add(player);
            }
        }

        // Increment the number of games won for each winner
        for (Player winner : winners) {
            long userId = winner.getUserId();
            User user = userRepository.findById(userId);
            user.setNumOfGameWon(user.getNumOfGameWon() + 1);
        }

        //gameRepository.deleteByGameId(lobbyId);
        return leaderBoard;
    }

    public Player calculatePlayerPoints(Player player, long lobbyId){
        Game game = gameRepository.findByGameId(lobbyId);
        //Game game = GameRepo.findByLobbyId((int) lobbyId);
        int currentRound = game.getCurrentRound();
        GameJudge judge = new GameJudge(game.getMiniGame().get(0).getGameQuestions().get(currentRound-1), player, currentRound);
        int point = judge.calculatePoints();
        player.setRoundScore(point);
        player.setTotalScore(player.getTotalScore()+point);

        playerRepository.save(player);
        playerRepository.flush();

        return player;
    }

    public boolean isTheGameStarted(long lobbyId) {
        Game currentGame = gameRepository.findByGameId(lobbyId);
        //Game currentGame = GameRepo.findByLobbyId((int)lobbyId);
        if(currentGame.getMiniGame().size() == 0){
            return false;
        }
        else {
            return (currentGame.getMiniGame().get(0).getGameQuestions().size() > 0);
        }
    }

    public List<Article> getAllArticles(long lobbyId){
        Game currentGame = gameRepository.findByGameId(lobbyId);
        //Game currentGame = GameRepo.findByLobbyId((int) lobbyId);
        return currentGame.getArticleList();
    }

    public void clearLobby(long lobbyId) {
        gameRepository.deleteByGameId(lobbyId);
        playerRepository.deleteByGameId(lobbyId);
    }
}
