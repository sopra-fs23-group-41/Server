package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.HigherLowerQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.List;
import java.util.Objects;

public class GameJudge {

    private final List<Question> questionList;
    private final Player player;
    private final int round;

    public GameJudge(List<Question> questions,Player player, int round){
        this.questionList = questions;
        this.player = player;
        this.round = round;
    }

    public boolean answerIsCorrect(){
        if(questionList.get(0) instanceof GuessThePriceQuestion) {
            return Objects.equals(player.getAnswers().get(this.round).getPlayerAnswer(), questionList.get(this.round).getTrueAnswer());
        }
        else {
            HigherLowerQuestion que = (HigherLowerQuestion) questionList.get(round);
            return (!Objects.equals(player.getAnswers().get(round).getPlayerAnswer(), que.getTrueAnswer()));
        }
    }

    public int calculatePoints(){
        String ans = player.getAnswers().get(round).getPlayerAnswer();
        if (questionList.get(0) instanceof GuessThePriceQuestion){
            GuessThePriceQuestion que = (GuessThePriceQuestion) questionList.get(round);
            int bonus = que.getBonus();
            if(answerIsCorrect()){
                return bonus;
            }
            else{
                int answer = Integer.parseInt(ans);
                String cr = questionList.get(round).getTrueAnswer();
                int price = Integer.parseInt(cr);
                int dif = Math.abs(answer - price)/price;
                if(dif < 0.1){
                    return (int) (bonus*0.5);
                }
                else return 0;
            }
        }
        else {
            HigherLowerQuestion que = (HigherLowerQuestion) questionList.get(round);
            int bonus = que.getBonus();
            if(answerIsCorrect()){
                return bonus;
            }
            else return 0;
        }
    }

}
