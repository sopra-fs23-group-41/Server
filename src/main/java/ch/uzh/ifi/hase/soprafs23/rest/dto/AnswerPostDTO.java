package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class AnswerPostDTO {
    private long playerId;
    private int numOfRound;
    private String playerAnswer;
    private double timeUsed;

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

}
