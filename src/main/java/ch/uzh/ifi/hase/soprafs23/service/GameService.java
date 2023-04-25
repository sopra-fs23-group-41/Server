package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.AsosApi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepo;
//import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@Transactional
public class GameService {

    //private final Logger log = LoggerFactory.getLogger(GameRepository.class);

    //private final GameRepository gameRepository;

    private final PlayerRepository playerRepository;

    private final UserRepository userRepository;

    //Logger logger = LoggerFactory.getLogger(GameRepository.class);

    @Autowired
    public GameService(/*@Qualifier("gameRepository") GameRepository gameRepository,*/
                       @Qualifier("playerRepository")PlayerRepository playerRepository,
                       @Qualifier("userRepository")UserRepository userRepository){
        //this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
    }

    public List<Game> getGame(){
        //return this.gameRepository.findAll();
        return null;
    }

    public Game createGame(Game newGame) {
        newGame.createGamePIN();
        newGame.setGameMode(GameMode.GuessThePrice);
        newGame.setCategory(Category.SHOES);

        removePlayersFromLobby(newGame.getGameId());

        //newGame = gameRepository.save(newGame);
        //gameRepository.flush();
        GameRepo.addGame((int) newGame.getGameId(), newGame);


        //log.debug("A new Lobby has started: {}", newGame);
        return newGame;
    }

    public Game updateGameSetting(Game currentGame) throws UnirestException, JsonProcessingException {
        Game game = GameRepo.findByLobbyId((int)currentGame.getGameId());
        game.updateGameSetting(currentGame.getGameMode(),currentGame.getRounds(), currentGame.getNumOfPlayer(),currentGame.getCategory());

        //gameRepository.save(game);
        //gameRepository.flush();
        return game;
    }

    public Game getGameById(long lobbyId) {
        return GameRepo.findByLobbyId((int)lobbyId);
    }

    public void beginGame(long lobbyId) throws UnirestException, JsonProcessingException {
        Game currentGame = getGameById(lobbyId);
        currentGame.startGame(currentGame.getGameMode());

        //gameRepository.save(currentGame);
        //gameRepository.flush();

        //log.debug("A new game has initialized and ready to start");
    }

    private void removePlayersFromLobby(long gameId){
        List<Player> playerList = playerRepository.findByGameId(gameId);
        for (Player player : playerList){
            //logger.info("Player with Id : " + player.getPlayerName() + "deleted!");
            playerRepository.deleteById(player.getPlayerId());
        }
    }

    public Boolean didAllPlayersJoin(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        return currentGame.checkIfAllPlayerExist();
    }

    public Question getNextQuestion(long lobbyId) {
        Game currenteGame = getGameById(lobbyId);
        Question nextQuestion = currenteGame.getNextRound();

        //keep the information updated in the repository
        //gameRepository.save(currenteGame);
        //gameRepository.flush();

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
        //gameRepository.save(currentGame);
        //gameRepository.flush();

        //log.debug("The answer of player with ID: {} has been saved!",playerId);
    }

    public boolean didAllPlayersAnswer(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        return currentGame.checkIfAllPlayersAnswered();
    }

    public List<Player> endGame(long lobbyId) {
        Game currentGame = getGameById(lobbyId);
        playerRepository.deleteByGameId(lobbyId);
        GameRepo.removeGame((int)lobbyId);
        return currentGame.endGame();
    }
}
