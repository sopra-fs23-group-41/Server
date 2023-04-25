package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

public class HigherLowerQuestion extends Question{

    private final int bonus = 10;
    private int timeToAnswer = 30;

    //constructor
    public HigherLowerQuestion(Article article1, Article article2) {
        articles.add(article1);
        articles.add(article2);
    }

    @Override
    void generateFalseAnswers() {
        float price1 = articles.get(0).getPrice();
        float price2 = articles.get(1).getPrice();
        if (price1 < price2){
            this.falseAnswers.add("Lower");
        }
        else if (price1 > price2){
            this.falseAnswers.add("Higher");
        }
        // TODO: prices equals
    }

    @Override
    protected void setPicUrl() {
        picUrls.add(getArticles().get(0).getImageUrl());
        picUrls.add(getArticles().get(1).getImageUrl());
    }

    @Override
    // true answer in this case indicate two prices of the two articles
    protected void setTrueAnswer() {
        float price1 = getArticles().get(0).getPrice();
        float price2 = getArticles().get(1).getPrice();
        if (price1 > price2) {
            this.trueAnswer = "Lower";
        }
        if (price1 < price2) {
            this.trueAnswer = "Higher";
        }
    }

    public int getTimeToAnswer(){
        return  this.timeToAnswer;
    }

    public int getBonus(){
        return this.bonus;
    }
}
