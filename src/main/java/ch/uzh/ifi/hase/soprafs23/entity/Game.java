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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;

public class Game {
    private long gameId;

    private int numOfPlayer;

    private GameType gameType;

    private int rounds;

    private String gamePIN;

    private GameMode gameMode;

    private List<Player> players = new ArrayList<>();

    private List<Article> articleList = new ArrayList<>();

    private MiniGame miniGame;

    private Category category;

    Logger logger = LoggerFactory.getLogger(Game.class);

    public Game() {
            setGamePIN();
    }

    public Game(GameType gameType, GameMode gameMode, int rounds, int numOfPlayer, Category category){
        this.gameType = gameType;
        this.gameMode = gameMode;
        this.rounds = rounds;
        this.category = category;
        this.numOfPlayer = numOfPlayer;
        setGamePIN();
    }

    public boolean checkIfAllPlayersAnswered() {
        return this.miniGame.checkIfAllPlayersAnswered();
    }


    //methods
    public void startGame(GameMode gameMode) throws UnirestException, JsonProcessingException {
        createArticles();
        if (gameMode == GameMode.GuessThePrice){
            miniGame = new GuessThePrice(this.rounds,this.articleList, gameMode);
        }
        else{
            miniGame = new HigherOrLower(this.rounds,this.articleList, gameMode);
        }
        logger.info("Game with Id: " + this.gameId + " wants to set players and questions to minigame");
        miniGame.setActivePlayers(this.players);
        logger.info("Game with Id: " + this.gameId + " set players to miniGame");
        miniGame.setGameQuestions();
        logger.info("Game with Id: " + this.gameId + " set questions to miniGame");
    }

    public void updateGameSetting(GameType gameType, GameMode gameMode, int rounds, int numOfPlayer, Category category){
        this.gameType = gameType;
        this.gameMode = gameMode;
        this.rounds = rounds;
        this.numOfPlayer = numOfPlayer;
        this.category = category;
    }

    public void createArticles() throws UnirestException, JsonProcessingException {
        this.articleList = AsosApiUtility.getArticles(this.rounds, this.category);
    }

    public void endGame(){}

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
    }



    //getters and setters
    public void setGamePIN(){
        UUID uuid = UUID.randomUUID();

        // Extract the first 6 characters of the UUID's hexadecimal representation
        String uuidStr = uuid.toString().replace("-", "");

        this.gamePIN = uuidStr.substring(0, 6);
    }

    public void setPlayers(List<User> users){
        for (int i=0; i<numOfPlayer; i++){
            Player player = new Player();
            User user = users.get(i);
            player.setPlayerName(user.getUsername());
            player.setUserId(user.getId());
            player.setGameId(this.gameId);
            this.players.add(player);
        }
    }

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
