package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.List;

public class HigherOrLower extends MiniGame{
    public HigherOrLower(int rounds, List<Article> articles) {
        super(rounds, articles);
    }

    @Override
    Question showNextQuestion() {
        return null;
    }

    @Override
    void updatePlayerPoints() {

    }
}
