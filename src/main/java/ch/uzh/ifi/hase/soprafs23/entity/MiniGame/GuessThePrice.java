package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.GameJudge;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.ArrayList;
import java.util.List;

public class GuessThePrice extends MiniGame{

    private final int roundPoints = 20;


    // Constructor
    public GuessThePrice(int rounds, List<Article> articles){
        super(rounds,articles);
    }



    @Override
    public Question showNextQuestion() {
        if (super.checkIfAllPlayersAnswered()){
            int num = super.currentRound+1;
            super.setCurrentRound(num);
            Question question = getGameQuestions().get(num-1);
            if (!question.isUsed()){
                question.setUsed(true);
            }
            else return null;
            return question;
        }
        else return null;
    }

    @Override
    public void updatePlayerPoints(){

    }
}
