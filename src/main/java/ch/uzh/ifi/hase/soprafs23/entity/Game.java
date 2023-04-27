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
import javassist.tools.web.BadHttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private long gameId;
    private int numOfPlayer = 1;
    private GameType gameType;
    private int rounds = 1;
    private String gamePIN;
    private GameMode gameMode;
    //private final User lobbyOwner;
    @ElementCollection
    private List<Article> articleList = new ArrayList<>();
    private MiniGame miniGame = null;
    private Category category;

    /*
    public Game(GameType gameType){
        this.gameType = gameType;
        //this.lobbyOwner = owner;
        createGamePIN();
    }
    */

    public Game(){}

    public boolean checkIfAllPlayersAnswered(List<Player> players) {
        if (!miniGame.checkIfAllPlayersAnswered(players)) {
            return false;
        }
        int currentRound = miniGame.getCurrentRound();
        return players.stream()
                .allMatch(player -> player.getAnswers().size() == currentRound);
    }


    //methods
    public void startGame(GameMode gameMode, List<Player> players) throws UnirestException, JsonProcessingException {
        if(checkIfAllPlayerExist(players)){
            if (gameMode == GameMode.GuessThePrice){
                createArticles(this.rounds);
                miniGame = new GuessThePrice(this.rounds,this.articleList, gameMode);
            }
            else{
                createArticles(this.rounds * 2);
                miniGame = new HigherOrLower(this.rounds,this.articleList, gameMode);
            }
            miniGame.setGameQuestions();

        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The number of player doesn't match to game setting!");
    }

    public void updateGameSetting(GameMode gameMode, int rounds, int numOfPlayer, Category category) {
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

    public Question getNextRound(List<Player> players){
        if(this.miniGame == null){
            throw new NullPointerException("The game has not started yet!");
        }
        if (!checkIfAllPlayersAnswered(players)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not all players have answered the current question");
        }
        return miniGame.showNextQuestion();
    }

    public Question getCurrentRoundQuestion(){
        //currentRounds starts with 1, so the index should be -1;
        return miniGame.getGameQuestions().get(miniGame.getCurrentRound()-1);
    }

    public List<Player> endGame(List<Player> players){
        long count = miniGame.getGameQuestions().stream()
                .filter(Question::isUsed)
                .count();
        if (count < rounds) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The game is not ended yet.");
        }
        return getGameLeaderBoard(players);
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

    public boolean checkIfAllPlayerExist(List<Player> players){
        if(players.size() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No players in the Lobby!");
        }
        return (players.size() == this.numOfPlayer);
    }

    public List<Player> getGameLeaderBoard(List<Player> players){
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

    public String getGamePIN(){
        return this.gamePIN;
    }

    public long getGameId(){
        return this.gameId;
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
    public int getCurrentRound(){
        return miniGame.getCurrentRound();
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public void setMiniGame(MiniGame miniGame) {
        this.miniGame = miniGame;
    }
}
