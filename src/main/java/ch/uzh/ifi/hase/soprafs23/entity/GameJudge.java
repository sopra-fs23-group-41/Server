package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.HigherLowerQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.List;
import java.util.Objects;

public class GameJudge {

    private final Question question;
    private final Player player;
    private final int round;

    public GameJudge(Question question,Player player, int round){
        this.question = question;
        this.player = player;
        this.round = round;
    }

    public boolean answerIsCorrect(){
        if(question instanceof GuessThePriceQuestion) {
            return Objects.equals(player.getAnswers().get(this.round-1).getPlayerAnswer(), question.getTrueAnswer());
        }
        else {
            HigherLowerQuestion que = (HigherLowerQuestion) question;
            return (!Objects.equals(player.getAnswers().get(round-1).getPlayerAnswer(), que.getTrueAnswer()));
        }
    }

    public int calculatePoints(){
        String ans = player.getAnswers().get(round-1).getPlayerAnswer();
        if (question instanceof GuessThePriceQuestion){
            GuessThePriceQuestion que = (GuessThePriceQuestion) question;
            int bonus = que.getBonus();
            if(answerIsCorrect()){
                return bonus;
            }
            else{
                int answer = Integer.parseInt(ans);
                String cr = question.getTrueAnswer();
                int price = Integer.parseInt(cr);
                int dif = Math.abs(answer - price)/price;
                if(dif < 0.1){
                    return (int) (bonus*0.5);
                }
                else return 0;
            }
        }
        else {
            HigherLowerQuestion que = (HigherLowerQuestion) question;
            int bonus = que.getBonus();
            if(answerIsCorrect()){
                return bonus;
            }
            else return 0;
        }
    }

}
