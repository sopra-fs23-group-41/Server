package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.entity.question.Question;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;

    private final PlayerRepository playerRepository;

    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("playerRepository")PlayerRepository playerRepository,
            @Qualifier("userRepository")UserRepository userRepository){
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
    }

    public Game createGame(Game newGame) {
        Game game = new Game();
        game.setNumOfPlayer(newGame.getNumOfPlayer());
        game.setGameType(newGame.getGameType());
        game.setRounds(newGame.getRounds());
        game.setGameMode(newGame.getGameMode());
        game.setCategory(newGame.getCategory());

        game.createGamePIN();

        removePlayersFromLobby(game.getGameId());
        gameRepository.save(game);
        gameRepository.flush();

        logger.debug("A new Lobby has started: {}", game);
        return game;
    }

    public Game updateGameSetting(Game currentGame, long lobbyId) {
        Game game = gameRepository.findByGameId(lobbyId);
        if (game == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The requested lobby with Id: " + lobbyId + " does not exist!");
        }
        game.updateGameSetting(currentGame.getGameMode(),currentGame.getRounds(), currentGame.getNumOfPlayer(),currentGame.getCategory());

        gameRepository.save(game);
        gameRepository.flush();

        return game;
        //Game game = GameRepo.findByLobbyId((int) lobbyId);
    }

    public Game getGameById(long lobbyId) throws InvalidDataAccessResourceUsageException {
        Game game = gameRepository.findByGameId(lobbyId);
        if (game == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requested lobby does not exist!");
        }
        return game;
    }
    public long getLobbyIdByGamePin(String gamePin){
        Game game = gameRepository.findByGamePIN(gamePin);
        if (game == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "the PIN code is not correct!");
        }

        return game.getGameId();
        //return GameRepo.findByGamePin(gamePin).getGameId();
    }

    public void beginGame(long lobbyId) throws UnirestException, JsonProcessingException {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        if (currentGame == null || players == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The lobby is not ready!");
        }

        currentGame.startGame(currentGame.getGameMode(), players);
        //MiniGame currentMiniGame = currentGame.getMiniGame();

        gameRepository.save(currentGame);
        gameRepository.flush();

        logger.info("A new game has been initialized and is ready to start");
    }

    private void removePlayersFromLobby(long gameId){
        List<Player> playerList = playerRepository.findByGameId(gameId);
        for (Player player : playerList){
            logger.info("Player with Id : " + player.getPlayerName() + "deleted!");
            playerRepository.deleteById(player.getPlayerId());
        }
    }

    public boolean didAllPlayersJoin(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        return currentGame.checkIfAllPlayerExist(players);
    }

    public Question getNextRound(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        if (currentGame == null || players == null || players.size() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is something wrong with the game!");
        }

        Question question = currentGame.getNextRound(players);
        gameRepository.save(currentGame);
        gameRepository.flush();

        return question;
        //keep the information updated in the repository
    }

    public Question getCurrentRoundQuestion(long lobbyId){
        Game currentGame = getGameById(lobbyId);
        if (currentGame == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong lobby!");
        }

        return currentGame.getCurrentRoundQuestion();
    }

    public void savePlayerAnswer(long playerId, Answer answer) {
        Player currentPlayer = playerRepository.findByPlayerId(playerId);
        if (currentPlayer == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such player exist!");
        }

        currentPlayer.setAnswers(answer);
        Player updatedPlayer = calculatePlayerPoints(currentPlayer, currentPlayer.getGameId());
        playerRepository.save(updatedPlayer);
        playerRepository.flush();

        logger.debug("The answer of player with ID: {} has been saved!", playerId);
    }

    public boolean didAllPlayersAnswer(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        if (currentGame == null || players == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is something wrong with the game!");
        }

        return currentGame.checkIfAllPlayersAnswered(players);
    }

    public List<Player> endMiniGame(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        List<Player> players = playerRepository.findByGameId(lobbyId);
        if (currentGame == null || players == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is something wrong with the game!");
        }

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
            if (currentGame.getNumOfPlayer() > 1){
                user.setNumOfGameWon(user.getNumOfGameWon() + 1);
            }
        }

        //gameRepository.deleteByGameId(lobbyId);
        return leaderBoard;
    }

    public Player calculatePlayerPoints(Player player, long lobbyId){
        int points;
        int currentRound;
        Question currentQuestion;
        Game game = gameRepository.findByGameId(lobbyId);

        if (game == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No game with that Id found!");
        }

        currentRound = game.getCurrentRound() - 1;
        currentQuestion = game.getQuestionOfRound(currentRound);
        GameJudge aGameJudge = new GameJudge(currentQuestion, player, currentRound);
        points = aGameJudge.calculatePoints();

        player.updatePointsAndStreak(points);

        return player;
    }

    public boolean isTheGameStarted(long lobbyId) {
        Game currentGame = gameRepository.findByGameId(lobbyId);
        if (currentGame == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such lobby exist!");
        }
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
        if (currentGame ==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The lobby does not exist!");
        }
        else {
            return currentGame.getArticleList();
        }
        //Game currentGame = GameRepo.findByLobbyId((int) lobbyId);
    }

    public void clearLobby(long lobbyId) {
        gameRepository.deleteByGameId(lobbyId);
        playerRepository.deleteByGameId(lobbyId);
    }

    public boolean nextRoundReady(long lobbyId, long playerId) {
        Game game = gameRepository.findByGameId(lobbyId);
        Player player = playerRepository.findByPlayerId(playerId);
        return game.getCurrentRound() == player.getAnswers().size();
    }
}
