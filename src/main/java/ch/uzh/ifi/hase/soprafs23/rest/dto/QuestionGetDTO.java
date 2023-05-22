package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.List;

public class QuestionGetDTO {
    private List<Article> articles;
    private String trueAnswer;
    private List<String> picUrls;
    private List<String> falseAnswers;

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public String getTrueAnswer() {
        return trueAnswer;
    }

    public void setTrueAnswer(String trueAnswer) {
        this.trueAnswer = trueAnswer;
    }

    public List<String> getPicUrls() {
        return picUrls;
    }

    public void setPicUrls(List<String> picUrls) {
        this.picUrls = picUrls;
    }

    public List<String> getFalseAnswers() {
        return falseAnswers;
    }

    public void setFalseAnswers(List<String> falseAnswers) {
        this.falseAnswers = falseAnswers;
    }

}
