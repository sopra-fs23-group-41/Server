package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.constant.ProfilePicture;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name="PLAYER")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "PlayerIdentity")
    private long playerId;

    @Column(nullable = false)
    private String playerName;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long gameId;

    @Column
    private ProfilePicture profilePicture;

    @Column
    private int totalScore;

    @Column
    private int roundScore;

    @Column
    private int streak = 0;

    @Column
    @ElementCollection
    private List<Answer> answers = new ArrayList<>();

    @Column
    private long lastActivityTimestamp = 0;

    public void updatePointsAndStreak(int points){
        if(points == 0){
            this.streak = 0;
            this.roundScore=0;
        }
        else{
            this.roundScore = points;
            this.totalScore += points;
            this.streak++;
        }
    }


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

    public ProfilePicture getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(ProfilePicture profilePicture) {
        this.profilePicture = profilePicture;
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

    public Answer getAnswerOfRound(int round) {
        return this.answers.get(round);
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public long getLastActivityTimestamp() {
        return lastActivityTimestamp;
    }

    public void setLastActivityTimestamp(long lastActivityTimestamp) {
        this.lastActivityTimestamp = lastActivityTimestamp;
    }
}
