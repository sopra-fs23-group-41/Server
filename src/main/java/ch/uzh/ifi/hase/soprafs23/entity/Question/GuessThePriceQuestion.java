package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@DiscriminatorValue("GTP")
public class GuessThePriceQuestion extends Question{

    @Column
    private final int bonus = 20;

    @Column
    private final int timeToAnswer = 40;

    // constructor
    public GuessThePriceQuestion(Article article){
        articles.add(article);
    }

    public GuessThePriceQuestion() {

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
        //get the true price
        String num = super.getTrueAnswer();
        float price = Float.parseFloat(num);

        //generate min max of possible wrong answers
        int min = (int) (0.6 * price);
        int max = (int) (1.4 * price);

        //generate random floats
        Random rand = new Random();
        float randomFloat1 = rand.nextFloat() * (max - min) + min;
        float randomFloat2 = rand.nextFloat() * (max - min) + min;
        float randomFloat3 = rand.nextFloat() * (max - min) + min;

        //create list for wrong answers using question super class
        super.falseAnswers.add(String.format("%.1f", randomFloat1));
        super.falseAnswers.add(String.format("%.1f", randomFloat2));
        super.falseAnswers.add(String.format("%.1f", randomFloat3));
    }

    //getters
    @Override
    public int getTimeToAnswer(){
        return timeToAnswer;
    }
    @Override
    public int getBonus(){
        return  this.bonus;
    }

}
