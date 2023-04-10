package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.GameJudge;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.List;

public class HigherOrLower extends MiniGame{

    private final int roundPoints = 10;

    public HigherOrLower(int rounds, List<Article> articles) {
        super(rounds, articles);
    }
}
