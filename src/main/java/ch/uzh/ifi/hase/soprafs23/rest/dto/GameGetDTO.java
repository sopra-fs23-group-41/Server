package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.asosapi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameGetDTO {

    private long gameId;
    private int numOfPlayer;
    private GameType gameType;
    private int rounds;
    private String gamePIN;
    private GameMode gameMode;
    private Category category;
    private List<Player> players = new ArrayList<>();

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

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public String getGamePIN() {
        return gamePIN;
    }

    public void setGamePIN(String gamePIN) {
        this.gamePIN = gamePIN;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
