package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.AsosApi.Article;

import java.util.ArrayList;
import java.util.List;

public abstract class Question {

    protected final List<Article> articles = new ArrayList<>();
    protected String trueAnswer;
    protected List<String> picUrls;
    private boolean isUsed = false;

    abstract void generateFalseAnswers();
    abstract protected void setPicUrl();
    abstract protected void setTrueAnswer();

    // methods
    public void initializeQuestion(){
        setPicUrl();
        setTrueAnswer();
    } // will it recognize which realization to use?

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

}
