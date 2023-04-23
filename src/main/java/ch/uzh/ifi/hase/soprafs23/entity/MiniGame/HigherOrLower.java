package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;



import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.List;

public class HigherOrLower extends MiniGame{

    private final int roundPoints = 10;

    public HigherOrLower(int rounds, List<Article> articles, GameMode gameMode) {
        super(rounds, articles, gameMode);
    }
}
