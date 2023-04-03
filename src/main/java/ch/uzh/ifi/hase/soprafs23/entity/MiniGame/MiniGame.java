package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.entity.GameJudge;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.ArrayList;
import java.util.List;

public abstract class MiniGame {

    private int roundPoints;
    private int rounds;
    private int currentRound = 0;
    private GameJudge judge = new GameJudge();
    private List<Player> activePlayers = new ArrayList<>();

    abstract Question showNextQuestion();

    public MiniGame(int roundPoints, int rounds){
        this.rounds = rounds;
        this.roundPoints = roundPoints;
    }

    public void setCurrentRound(int currentRound){
        this.currentRound = currentRound;
    }

    public int getCurrentRound(){
        return this.currentRound;
    };

    public void setRounds(int rounds){
        this.rounds = rounds;
    }

    public int getRounds(){
        return this.rounds;
    }

    public void setRoundPoints(int roundPoints){
        this.roundPoints = roundPoints;
    }

    public int getRoundPoints(){
        return this.roundPoints;
    };

    public void setActivePlayers(List<Player> activePlayers){
        this.activePlayers = activePlayers;
    }

    public List<Player> getActivePlayers(){
        return this.activePlayers;
    }

    private void updatePlayerPoints() {}
}
