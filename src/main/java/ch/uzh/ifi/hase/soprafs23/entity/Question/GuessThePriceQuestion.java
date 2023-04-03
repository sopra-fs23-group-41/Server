package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.List;

public class GuessThePriceQuestion extends Question{

    private String question;
    private String trueAnswer;
    private List<String> falseAnswers;
    private Article aArticle;
    private int timeToAnswer;
    private String picUrl;

    public void setArticle(Article article){
        this.aArticle = article;
    }

    public void setTrueAnswer(){
        String actualPrice = this.aArticle.getPrice();
    }

    public void setPicUrl(){
        this.picUrl = this.aArticle.getPicUrl();
    }

    @Override
    public void gameQuestion(String question, Article aArticle){

    }

}
