package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.AsosApi.Article;

public class HigherLowerQuestion extends Question{

    private final int bonus = 10;
    private String falseAnswer;
    private int timeToAnswer = 30;

    //constructor
    public HigherLowerQuestion(Article article1, Article article2) {
        articles.add(article1);
        articles.add(article2);
    }

    @Override
    void generateFalseAnswers() {
        double price1 = articles.get(0).getPrice().getCurrent().getValue();
        double price2 = articles.get(1).getPrice().getCurrent().getValue();
        if (price1 < price2){
            this.falseAnswer = "Higher";
        }
        else if (price1 > price2){
            this.falseAnswer = "Lower";
        }
        // TODO: prices equals
    }

    @Override
    protected void setPicUrl() {
        picUrls.add(getArticles().get(0).getMedia().getImages().get(0).getUrl());
        picUrls.add(getArticles().get(1).getMedia().getImages().get(0).getUrl());
    }

    @Override
    // true answer in this case indicate two prices of the two articles
    protected void setTrueAnswer() {
        this.trueAnswer =Double.toString(getArticles().get(0).getPrice().getCurrent().getValue());
        this.trueAnswer =Double.toString(getArticles().get(1).getPrice().getCurrent().getValue());
    }

    //getters
    public String getFalseAnswer(){
        return this.falseAnswer;
    }

    public int getTimeToAnswer(){
        return  this.timeToAnswer;
    }

    public int getBonus(){
        return this.bonus;
    }
}
