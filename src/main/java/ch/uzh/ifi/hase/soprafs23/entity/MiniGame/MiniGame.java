package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.GameJudge;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.ArrayList;
import java.util.List;

public abstract class MiniGame {

    private int rounds;
    protected int currentRound = 0;
    private GameJudge judge = new GameJudge();
    private List<Player> activePlayers = new ArrayList<>();
    private final List<Article> allArticles;
    private GameMode gameMode;
    private List<Question> gameQuestions = new ArrayList<>();

    public MiniGame(int rounds, List<Article> articles){
        this.rounds = rounds;
        this.allArticles = articles;
    }

    public void setCurrentRound(int currentRound){
        this.currentRound = currentRound;
    }

    public int getCurrentRound(){
        return this.currentRound;
    };

    public void setRounds(int rounds){
        this.rounds = rounds;
    }

    public int getRounds(){
        return this.rounds;
    }

    public void setActivePlayers(List<Player> activePlayers){
        this.activePlayers = activePlayers;
    }

    public List<Player> getActivePlayers(){
        return this.activePlayers;
    }

    public List<Article> getAllArticles() {
        return this.allArticles;
    }

    public void setGameQuestions(){
        List<Question> questions = new ArrayList<>();
        if (this.gameMode == GameMode.GuessThePrice){
            for (int i=0; i< this.rounds; i++){
                GuessThePriceQuestion question = new GuessThePriceQuestion(allArticles.get(i));
                questions.add(question);
            }
            this.gameQuestions = questions;
        }
        else if(this.gameMode == GameMode.HighOrLow){

        }
    }

    abstract Question showNextQuestion();

    abstract void updatePlayerPoints();

    public boolean checkIfAllPlayersAnswered(){
        List<Player> players = this.activePlayers;
        for (int i=0; i< players.size();i++){
            if(players.get(i).getAnswers().size() == currentRound){
                continue;
            }
            else return false;
        }
        return true;
    }

    public List<Question> getGameQuestions() {
        return gameQuestions;
    }

}
