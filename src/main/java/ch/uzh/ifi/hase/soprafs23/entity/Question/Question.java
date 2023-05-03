package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public abstract class Question implements Serializable {

    protected List<Article> articles = new ArrayList<>();

    protected String trueAnswer;
    protected List<String> falseAnswers = new ArrayList<>();
    protected List<String> picUrls = new ArrayList<>();
    private boolean isUsed = false;

    abstract void generateFalseAnswers();
    abstract protected void setPicUrl();
    abstract protected void setTrueAnswer();

    // methods
    public void initializeQuestion(){
        setPicUrl();
        setTrueAnswer();
        generateFalseAnswers();
    }

    // getters and setters
    public List<Article> getArticles(){
        return this.articles;
    }

    public List<String> getPicUrls(){
        return this.picUrls;
    }

    public boolean isUsed() {
        return this.isUsed;
    }

    public void setUsed(boolean used) {
        this.isUsed = used;
    }

    public String getTrueAnswer(){
        return this.trueAnswer;
    };

    public List<String> getFalseAnswers() {
        return falseAnswers;
    }

    public void setFalseAnswers(List<String> falseAnswers) {
        this.falseAnswers = falseAnswers;
    }
}
