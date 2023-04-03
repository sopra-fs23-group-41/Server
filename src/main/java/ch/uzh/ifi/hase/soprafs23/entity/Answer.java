package ch.uzh.ifi.hase.soprafs23.entity;

public class Answer {

    private long playerId;
    private int numOfRound = 0;
    private String playerAnswer;
    private String correctAnswer;
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

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public double getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(double timeUsed) {
        this.timeUsed = timeUsed;
    }

    public boolean isCorrect(){
        if (this.playerAnswer == this.correctAnswer){
            return true;
        }
        else{
            return false;
        }
    }
}
