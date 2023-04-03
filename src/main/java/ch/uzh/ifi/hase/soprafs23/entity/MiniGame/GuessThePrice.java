package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.GameJudge;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.ArrayList;
import java.util.List;

public class GuessThePrice extends MiniGame{

    private final int roundPoints = 20;
    private List<GuessThePriceQuestion> gameQuestions = new ArrayList<>();

    // Constructor
    public GuessThePrice(int rounds, List<Article> articles){
        super(rounds,articles);
    }

    public void setGameQuestions(){
        List<GuessThePriceQuestion> questions = new ArrayList<>();
        for (int i=0; i< super.getRounds(); i++){
            GuessThePriceQuestion question = new GuessThePriceQuestion(super.getAllArticles().get(i));
            questions.add(question);
        }
        this.gameQuestions = questions;
    }

    public List<GuessThePriceQuestion> getGameQuestions(){
        return this.gameQuestions;
    }

    @Override
    public Question showNextQuestion() {
        if (super.checkIfAllPlayersAnswered()){
            int num = super.currentRound+1;
            super.setCurrentRound(num);
            GuessThePriceQuestion question = getGameQuestions().get(num-1);
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
