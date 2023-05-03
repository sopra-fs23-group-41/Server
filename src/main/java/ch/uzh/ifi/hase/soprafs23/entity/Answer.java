package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import java.io.Serializable;


public class Answer implements Serializable {

    private long playerId;
    private int numOfRound = 0;
    private String playerAnswer;
    private double timeUsed;
    //private Question question;

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
        return timeUsed;
    }

    public void setTimeUsed(double timeUsed) {
        this.timeUsed = timeUsed;
    }

}
