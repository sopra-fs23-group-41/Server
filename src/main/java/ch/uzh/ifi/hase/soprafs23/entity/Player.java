package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="PLAYER")
public class Player {
    @Id
    @GeneratedValue
    private long playerId;

    @Column(nullable = false)
    private String playerName;
    // private String token;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long gameId;

    @Column
    private int totalScore;

    @Column
    private int roundScore;

    @Column
    @ElementCollection
    private List<Answer> answers = new ArrayList<>();


    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
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
