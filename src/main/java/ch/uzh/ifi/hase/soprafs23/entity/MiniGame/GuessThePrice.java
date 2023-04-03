package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.entity.GameJudge;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.ArrayList;
import java.util.List;

public class GuessThePrice extends MiniGame{

    private List<GuessThePriceQuestion> gameQuestions = new ArrayList<>();

    // Constructor
    public GuessThePrice(int rounds, int roundPoints){
        super(rounds,roundPoints);
    }

    public void setGameQuestions(List<GuessThePriceQuestion> gameQuestions){
        this.gameQuestions = gameQuestions;
    }

    public List<GuessThePriceQuestion> getGameQuestions(){
        return this.gameQuestions;
    }

    @Override
    Question showNextQuestion() {
        return null;
    }

}
