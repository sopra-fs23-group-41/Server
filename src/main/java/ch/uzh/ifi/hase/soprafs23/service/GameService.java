package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame.MiniGame;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class GameService {

    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    //private final GameRepository gameRepository;

    private int lobbyId = 0;

    Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    public GameService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                       @Qualifier("userRepository") UserRepository userRepository
                       //@Qualifier("gameRepository") GameRepository gameRepository
                        ){
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        //this.gameRepository = gameRepository;
    }

    public Game createGame(Game gameToCreate){
        //GameType gameType, GameMode gameMode, int rounds, int numOfPlayer, Category category
        logger.info("max number of player in lobby = " + gameToCreate.getNumOfPlayer());
        Game game = new Game(gameToCreate.getGameType(),
                gameToCreate.getGameMode(),
                gameToCreate.getRounds(),
                gameToCreate.getNumOfPlayer(),
                gameToCreate.getCategory());
        //remove players from the game id if an earlier game took place
        removePlayersFromLobby(game.getGameId());
        //save game in repository
        //Game newGame = gameRepository.save(game);
        //gameRepository.flush();
        game.setGameId(this.lobbyId);
        GameRepository.addGame(this.lobbyId, game);
        this.lobbyId++;
        //GameRepository.addGame((int)game.getGameId(), game);
        return game;
        //return newGame;
    }

    public void beginGame(long gameId) throws UnirestException, JsonProcessingException {
        Game game = GameRepository.findByLobbyId((int)gameId);
        game.startGame(game.getGameMode());
    }

    public Game getGameById(long gameId){
        return GameRepository.findByLobbyId((int)gameId);
    }

    private void removePlayersFromLobby(long gameId){
        List<Player> playerList = playerRepository.findByGameId(gameId);
        for(Player player : playerList){
            logger.info("Player with Id : " + player.getPlayerName() + " deleted!");
            playerRepository.deleteById(player.getPlayerId());
        }
    }

    public Boolean didAllPlayersJoin(long lobbyId){
        //Game game = gameRepository.findById(lobbyId);
        Game game = GameRepository.findByLobbyId((int) lobbyId);
        return game.getPlayers().size() == game.getNumOfPlayer();
    }

    public Question getNextQuestion(long lobbyId){
        //Game game = gameRepository.findById(lobbyId);
        Game game = GameRepository.findByLobbyId((int) lobbyId);
        return game.getMiniGame().showNextQuestion();
    }

    public void savePlayerAnswer(long playerId, Answer answer){
        Player player = playerRepository.findByPlayerId(playerId);
        player.setAnswers(answer);
        logger.info("Answer added to Player with Id: " + playerId);
        //Game game = gameRepository.findByGameId(player.getGameId());
        Game game = GameRepository.findByLobbyId((int) player.getGameId());
        game.getPlayer(playerId).setAnswers(answer);
        game.updatePlayerPoints();
    }

    public boolean didAllPlayersAnswer(long lobbyId){
        //Game game = gameRepository.findById(lobbyId);
        Game game = GameRepository.findByLobbyId((int) lobbyId);
        return game.checkIfAllPlayersAnswered();
    }

    //public Player addUserToLobby(Player player, String gamePin){throw new RuntimeException("not implemented error");}

    public List<Player> endGame(long lobbyId){
        List<Player> players = playerRepository.findByGameId(lobbyId);
        playerRepository.deleteByGameId(lobbyId);
        GameRepository.removeGame((int) lobbyId);
        return players;
    }

}
