package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.ArrayList;
import java.util.List;

public class GuessThePriceQuestion extends Question{

    private final int bonus = 20;
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
        this.trueAnswer = Float.toString(getArticles().get(0).getPrice());

    }

    @Override
    public void generateFalseAnswers() {
        String num = super.getTrueAnswer();
        float price = Float.parseFloat(num);
        List<String> falseAnswers = new ArrayList<>();
        for (float i = -1; i < 3; i++){
            if(i==0){
                continue;
            }
            else {
                float ans = (float) (price*(1+i*0.1));
                String wrong = String.format("%.1f", ans);
                falseAnswers.add(wrong);
            }
        }

        this.falseAnswers = falseAnswers;
    }

    //getters
    public int getTimeToAnswer(){
        return timeToAnswer;
    }

    public int getBonus(){
        return  this.bonus;
    }

}
