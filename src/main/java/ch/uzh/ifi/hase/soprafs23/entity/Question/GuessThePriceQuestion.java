package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.ArrayList;
import java.util.List;

public class GuessThePriceQuestion extends Question{

    private final int bonus = 20;
    private List<String> falseAnswers;
    private int timeToAnswer = 40;

    // constructor
    public GuessThePriceQuestion(Article article){
        articles.add(article);
    }

    @Override
    protected void setPicUrl(){
        picUrls.add(getArticles().get(0).getImageUrl());
    }

    @Override
    protected void setTrueAnswer() {
        this.trueAnswer =Double.toString(getArticles().get(0).getPrice());

    }

    @Override
    public void generateFalseAnswers() {
        String num = super.getTrueAnswer();
        int price = Integer.parseInt(num);
        List<String> falseAnswers = new ArrayList<>();
        for (float i = -1; i < 3; i++){
            if(i==0){
                continue;
            }
            else {
            String wrong = String.valueOf(price*(1+i*0.1));
            falseAnswers.add(wrong);
            }
        }

        this.falseAnswers = falseAnswers;
    }

    //getters
    public List<String> getFalseAnswers(){
        return this.falseAnswers;
    }

    public int getTimeToAnswer(){
        return timeToAnswer;
    }

    public int getBonus(){
        return  this.bonus;
    }

}
