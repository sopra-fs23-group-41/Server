package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.List;

public abstract class Question {

    protected final Article aArticle;
    private float trueAnswer;
    private String picUrl;
    private boolean isUsed = false;

    public Question(Article article){
        this.aArticle = article;
    }

    public Article getArticle(){
        return this.aArticle;
    }

    private void setPicUrl(){
        this.picUrl = this.aArticle.getImageUrl();
    }

    public String getPicUrl(){
        return this.picUrl;
    }

    private void setTrueAnswer() {
        this.trueAnswer = this.aArticle.getPrice();
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public float getTrueAnswer(){
        return this.trueAnswer;
    };

    public void initializeQuestion(){
        setPicUrl();
        setTrueAnswer();
    }

    abstract void generateFalseAnswers();

}
