package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.List;

public abstract class Question {

    void gameQuestion(String question, Article aArticle){};

    String getQuestion() {
        return null;
    }

    String getTrueAnswer(){
        return null;
    };

    List<String> getFalseAnswer(){
        return null;
    };
}
