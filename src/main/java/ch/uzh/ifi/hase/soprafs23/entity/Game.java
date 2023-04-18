package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.AsosApi.Article;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame.GuessThePrice;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame.HigherOrLower;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame.MiniGame;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Game {

    private int numOfPlayer = 1;
    private GameType gameType = GameType.MULTI;
    private int rounds = 1;
    private String gamePIN;
    private GameMode gameMode = GameMode.GuessThePrice;
    private List<Player> players = new ArrayList<>();
    private long gameId;
    private List<Article> articleList;
    private MiniGame miniGame;


    //methods
    public void startGame(GameMode gameMode){
        createArticles();
        if (gameMode == GameMode.GuessThePrice){
            miniGame = new GuessThePrice(this.rounds,this.articleList);
        }
        else{
            miniGame = new HigherOrLower(this.rounds,this.articleList);
        }
        miniGame.setActivePlayers(this.players);
        miniGame.setGameQuestions();
    }

    public void updateGameSetting(GameType gameType, GameMode gameMode, int rounds, int numOfPlayer){
        this.gameType = gameType;
        this.gameMode = gameMode;
        this.rounds = rounds;
        this.numOfPlayer = numOfPlayer;
    }

    public void createArticles(){

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

    public String getGamePIN(){
        return this.gamePIN;
    }

    public long getGameId(){
        return this.gameId;
    }

    public Player getPlayers(List<Player> players){
        return null;
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
}
