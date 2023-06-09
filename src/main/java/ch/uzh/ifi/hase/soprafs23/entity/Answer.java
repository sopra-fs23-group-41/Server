package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Answer implements Serializable {

    private long playerId;
    private int numOfRound = 0;
    private String playerAnswer;
    private double timeUsed;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getNumOfRound() {
        return this.numOfRound;
    }

    public void setNumOfRound(int numOfRound) {
        this.numOfRound = numOfRound;
    }

    public String getPlayerAnswer() {
        return playerAnswer;
    }

    public void setPlayerAnswer(String playerAnswer) {
        this.playerAnswer = playerAnswer;
    }

    public double getTimeUsed() {
        return this.timeUsed;
    }

    public void setTimeUsed(double timeUsed) {
        this.timeUsed = timeUsed;
    }

}
