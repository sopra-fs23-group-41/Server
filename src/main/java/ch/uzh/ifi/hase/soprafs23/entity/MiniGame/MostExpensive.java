package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.List;

public class MostExpensive extends MiniGame{

    private final int roundPoints = 30;

    public MostExpensive(int rounds, List<Article> articles, GameMode gameMode) {
        super(rounds, articles, gameMode);
    }


}
