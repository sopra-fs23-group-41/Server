package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

public class AnswerPostDTO {
    private long playerId;
    private int numOfRound;
    private String playerAnswer;
    private double timeUsed;
    private Question question;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public void setNumOfRound(int numOfRound) {
        this.numOfRound = numOfRound;
    }

    public int getNumOfRound() {
        return numOfRound;
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

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
