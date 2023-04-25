package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.AsosApi.AsosApiUtility;
import ch.uzh.ifi.hase.soprafs23.AsosApi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame.GuessThePrice;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame.HigherOrLower;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame.MiniGame;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

public class Game {
    private long gameId;

    private int numOfPlayer = 1;

    private GameType gameType;

    private int rounds = 1;

    private String gamePIN;

    private GameMode gameMode;

    //private final User lobbyOwner;

    private List<Player> players = new ArrayList<>();

    private List<Article> articleList = new ArrayList<>();

    private MiniGame miniGame;

    private Category category;

    Logger logger = LoggerFactory.getLogger(Game.class);


    public Game(GameType gameType){
        this.gameType = gameType;
        //this.lobbyOwner = owner;
        createGamePIN();
    }

    public Game(){}

    public boolean checkIfAllPlayersAnswered() {
        if (!miniGame.checkIfAllPlayersAnswered()) {
            return false;
        }
        int currentRound = miniGame.getCurrentRound();
        return players.stream()
                .allMatch(player -> player.getAnswers().size() == currentRound);
    }


    //methods
    public void startGame(GameMode gameMode) throws UnirestException, JsonProcessingException {
        if(checkIfAllPlayerExist()){
            if (gameMode == GameMode.GuessThePrice){
                createArticles(this.rounds);
                miniGame = new GuessThePrice(this.rounds,this.articleList, gameMode);
            }
            else{
                createArticles(this.rounds * 2);
                miniGame = new HigherOrLower(this.rounds,this.articleList, gameMode);
            }
            logger.info("Game with Id: " + this.gameId + " wants to set players and questions to miniGame");
            miniGame.setActivePlayers(this.players);
            logger.info("Game with Id: " + this.gameId + " set players to miniGame");
            miniGame.setGameQuestions();
            logger.info("Game with Id: " + this.gameId + " set questions to miniGame");
        }
        else throw new IllegalStateException("The number of player doesn't match");
    }

    public void updateGameSetting(GameMode gameMode, int rounds, int numOfPlayer, Category category) throws UnirestException, JsonProcessingException {
        this.gameMode = gameMode;
        this.rounds = rounds;
        this.numOfPlayer = numOfPlayer;
        this.category = category;

        //createArticles();
        //can we put creatArticle() here?
    }

    public void createArticles(int numOfArticles) throws UnirestException, JsonProcessingException {
        this.articleList = AsosApiUtility.getArticles(numOfArticles, this.category);
    }

    public Question getNextRound(){
        if (!checkIfAllPlayersAnswered()) {
            throw new IllegalStateException("Not all players have answered the current question");
        }
        return miniGame.showNextQuestion();
    }

    public List<Player> endGame(){
        long count = miniGame.getGameQuestions().stream()
                .filter(Question::isUsed)
                .count();
        if (count < rounds) {
            throw new IllegalStateException("The game is not ended yet.");
        }
        return getGameLeaderBoard();
    }

    /*
    public Player declareWinner(){
        Player winner = null;
        int highestScore = Integer.MIN_VALUE;

        for (Player player : players) {
            int score = player.getTotalScore();

            if (score > highestScore) {
                highestScore = score;
                winner = player;
            }
        }

        return winner;
    }  */

    public boolean checkIfAllPlayerExist(){
        return (this.players.size() == this.numOfPlayer);
    }

    public boolean checkIfUserIsAPlayer(Long userId) {
        for (Player player : players) {
            if (player.getUserId() == userId) {
                return true;
            }
        }
        return false;
    }

    public void syncPlayerInformation(Player currentPlayer){
        Optional<Player> foundPlayer = players.stream()
                .filter(player -> player.getPlayerId() == currentPlayer.getPlayerId())
                .findFirst();

        foundPlayer.ifPresent(player -> {
            player.copyFrom(currentPlayer);
            miniGame.syncPlayerInfo(player);
        });
    }

    public List<Player> getGameLeaderBoard(){
        return players.stream()
                .sorted(Comparator.comparingInt(Player::getTotalScore).reversed())
                .collect(Collectors.toList());
    }




    //getters and setters
    public void createGamePIN(){
        UUID uuid = UUID.randomUUID();

        // Extract the first 6 characters of the UUID's hexadecimal representation
        String uuidStr = uuid.toString().replace("-", "");

        this.gamePIN = uuidStr.substring(0, 6);
    }

    public void setGamePIN(String gamePIN) {
        this.gamePIN = gamePIN;
    }

    /*
    public void setPlayers(List<User> users){
        for (User user : users){
            Player player = new Player();
            player.setPlayerName(user.getUsername());
            player.setUserId(user.getId());
            player.setGameId(this.gameId);
            this.players.add(player);
        }
    } */

    public void addPlayer(Player player){
        if(this.players.size() < this.numOfPlayer) {
            this.players.add(player);
        }
        else throw new RejectedExecutionException("Lobby already full");
    }

    public void updatePlayerPoints(){
        this.miniGame.updatePlayerPoints();
        this.players = miniGame.getActivePlayers();
    }

    public String getGamePIN(){
        return this.gamePIN;
    }

    public long getGameId(){
        return this.gameId;
    }

    public List<Player> getPlayers(){
        return this.players;
    }

    public List<Player> getAllPlayers(){
        return this.players;
    }

    public MiniGame getMiniGame(){
        return this.miniGame;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public int getNumOfPlayer() {
        return numOfPlayer;
    }

    public void setNumOfPlayer(int numOfPlayer) {
        this.numOfPlayer = numOfPlayer;
    }

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public Player getPlayer(long playerId) {
        for(Player player: this.players){
            if(player.getPlayerId() == playerId){
                return player;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player with Id: " + playerId + " is not in this game");
    }
}
