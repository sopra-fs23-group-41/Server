package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Game {

    private int numOfPlayer;
    private GameType gameType;
    private int rounds;
    private String gamePIN;
    private GameMode gameMode;
    private List<Player> players = new ArrayList<>();
    private long gameId;
    private List<Article> articleList;

    public Game(GameType gameType, GameMode gameMode, int rounds, int numOfPlayer){
        this.gameType = gameType;
        this.gameMode = gameMode;
        this.rounds = rounds;
        this.numOfPlayer = numOfPlayer;
    }

    public void setRounds(int rounds){
        this.rounds = rounds;
    }

    public void setGameType(GameType gameType){
        this.gameType = gameType;
    }

    public void setGameId(){
        Random rand = new Random();
        int num = rand.nextInt(900000) + 100000;
        this.gameId = (long) num;
    }

    public void setGamePIN(){
        UUID uuid = UUID.randomUUID();

        // Extract the first 6 characters of the UUID's hexadecimal representation
        String uuidStr = uuid.toString().replace("-", "");
        String pin = uuidStr.substring(0, 6);

        this.gamePIN = pin;
    }

    public void setGameMode(GameMode gameMode){
        this.gameMode = gameMode;
    }

    public String getGamePIN(){
        return this.gamePIN;
    }

    public long getGameId(){
        return this.gameId;
    }

    public void createMiniGame(GameMode gameMode){

    }

    public Player getPlayers(List<Player> players){
        return null;
    }

    public List<Player> getAllPlayers(){
        return this.players;
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

    public void createArticles(){

    }

    public List<Question> createQuestions(GameMode gameMode){
        return  null;
    }

    public void endGame(){}

    public Player declareWinner(List<Player> players){
        return  null;
    }


}
