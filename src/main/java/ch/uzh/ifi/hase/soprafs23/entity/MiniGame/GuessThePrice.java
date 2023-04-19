package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.List;

public class GuessThePrice extends MiniGame{

    private final int roundPoints = 20;

    // Constructor
    public GuessThePrice(int rounds, List<Article> articles){
        super(rounds,articles);
    }

    @Override
    public void updatePlayerPoints(){

    }
}
