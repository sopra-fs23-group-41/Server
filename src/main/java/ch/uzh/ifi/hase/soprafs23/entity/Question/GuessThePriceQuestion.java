package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.ArrayList;
import java.util.List;

public class GuessThePriceQuestion extends Question{

    private List<String> falseAnswers;
    private final int timeToAnswer = 40;


    public GuessThePriceQuestion(Article article){
        super(article);
    }

    @Override
    public void generateFalseAnswers() {
        float price = super.getTrueAnswer();
        List<String> falseAnswers = new ArrayList<>();
        for (float i = -1; i < 3; i++){
            if(i==0){
                continue;
            }
            else {
            float num = (float) (price*(1+i*0.1));
            String wrong = String.valueOf(num);
            falseAnswers.add(wrong);
            }
        }

        this.falseAnswers = falseAnswers;
    }

    public List<String> getFalseAnswers(){
        return this.falseAnswers;
    }

}
