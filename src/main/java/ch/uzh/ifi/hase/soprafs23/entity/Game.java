package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.AsosApi.AsosApiUtility;
import ch.uzh.ifi.hase.soprafs23.AsosApi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;


@Entity
@Table(name = "GAME")
@JsonIgnoreProperties(value="miniGames")
public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "game_id", unique = true)
    private long gameId;

    @Column
    private int numOfPlayer = 1;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GameType gameType;

    @Column
    private int rounds = 1;

    @Column(unique = true)
    private String gamePIN;

    @Column
    private GameMode gameMode;

    @ElementCollection
    private List<Article> articleList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private List<MiniGame> miniGames = new ArrayList<>();

    @Column
    @Enumerated(EnumType.STRING)
    private Category category;


    public Game(){}

    public boolean checkIfAllPlayersAnswered(List<Player> players) {
        if (!miniGames.get(0).checkIfAllPlayersAnswered(players)) {
            return false;
        }
        int currentRound = miniGames.get(0).getCurrentRound();
        return players.stream()
                .allMatch(player -> player.getAnswers().size() == currentRound);
    }


    //methods
    public void startGame(GameMode gameMode, List<Player> players) throws UnirestException, JsonProcessingException {
        if(checkIfAllPlayerExist(players)){
            if (gameMode == GameMode.GuessThePrice){
                createArticles(this.rounds);
                miniGames.add(new MiniGame(this.rounds,this.articleList, gameMode));
            }
            else if (gameMode == GameMode.HighOrLow){
                createArticles(this.rounds * 2);
                miniGames.add(new MiniGame(this.rounds,this.articleList, gameMode));
            }
            else {
                createArticles(this.rounds * 4);
                miniGames.add(new MiniGame(this.rounds, this.articleList, gameMode));
            }

            miniGames.get(0).setGameQuestions();

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
        if (!checkIfAllPlayersAnswered(players)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not all players have answered the current question");
        }
        return miniGames.get(0).showNextQuestion();
    }

    public Question getCurrentRoundQuestion(){
        //currentRounds starts with 1, so the index should be -1;
        return miniGames.get(0).getGameQuestions().get(miniGames.get(0).getCurrentRound()-1);
    }

    public List<Player> endGame(List<Player> players){
        long count = miniGames.get(0).getGameQuestions().stream()
                .filter(Question::isUsed)
                .count();
        if (count < rounds) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The game is not ended yet.");
        }

        return getGameLeaderBoard(players);
    }


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

    public void createGamePIN(){
        UUID uuid = UUID.randomUUID();

        // Extract the first 6 characters of the UUID's hexadecimal representation
        String uuidStr = uuid.toString().replace("-", "");

        this.gamePIN = uuidStr.substring(0, 6);
    }

    //getters and setters
    public Question getQuestionOfRound(int round){
        return this.miniGames.get(0).getGameQuestions().get(round);
    }

    public void setGamePIN(String gamePIN) {
        this.gamePIN = gamePIN;
    }

    public String getGamePIN(){
        return this.gamePIN;
    }

    public long getGameId(){
        return this.gameId;
    }
    public List<MiniGame> getMiniGame(){
        return this.miniGames;
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
        return miniGames.get(0).getCurrentRound();
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public void setMiniGame(List<MiniGame> miniGames) {
        this.miniGames = miniGames;
    }
}
