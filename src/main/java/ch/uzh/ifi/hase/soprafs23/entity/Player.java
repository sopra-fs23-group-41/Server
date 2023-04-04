package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.List;

public class Player {

    private String playerName;
    // private String token;
    private long userId;
    private long playerId;
    private long gameId;
    private int points;
    private int totalScore;
    private int roundScore;
    private List<Answer> answers;

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /*public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
    */

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public void setRoundScore(int roundScore) {
        this.roundScore = roundScore;
    }

    public void setAnswers(Answer answer) {
        this.answers.add(answer);
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public long getUserId() {
        return this.userId;
    }

    public long getPlayerId() {
        return this.playerId;
    }

    public long getGameId() {
        return this.gameId;
    }

    public int getPoints() {
        return this.points;
    }

    public int getTotalScore() {
        return this.totalScore;
    }

    public int getRoundScore() {
        return this.roundScore;
    }

    public List<Answer> getAnswers() {
        return this.answers;
    }
}
