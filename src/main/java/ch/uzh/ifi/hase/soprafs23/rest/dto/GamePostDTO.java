package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.asosapi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;

public class GamePostDTO {
    private GameType gameType;
    private int rounds;
    private GameMode gameMode;
    private Category category;
    private int numOfPlayer;

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

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getNumOfPlayer() {
        return numOfPlayer;
    }

    public void setNumOfPlayer(int numOfPlayer) {
        this.numOfPlayer = numOfPlayer;
    }
}

