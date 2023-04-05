package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.GameJudge;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.HigherLowerQuestion;
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

    // abstract methods
    abstract Question showNextQuestion();

    abstract void updatePlayerPoints();

    // constructor
    public MiniGame(int rounds, List<Article> articles){
        this.rounds = rounds;
        this.allArticles = articles;
    } //pay attention to the number of articles

    //methods
    public void setGameQuestions(){
        List<Question> questions = new ArrayList<>();
        if (this.gameMode == GameMode.GuessThePrice){
            for (int i=0; i< this.rounds; i++){
                GuessThePriceQuestion question = new GuessThePriceQuestion(allArticles.get(i));
                question.initializeQuestion();
                questions.add(question);
            }
            this.gameQuestions = questions;
        }
        else if(this.gameMode == GameMode.HighOrLow){
            for (int i = 0; i<this.rounds;){
                HigherLowerQuestion question = new HigherLowerQuestion(allArticles.get(i),allArticles.get(i+1));
                question.initializeQuestion();
                questions.add(question);
                i = i+2;
            }
            this.gameQuestions = questions;
        }
    }

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

    // getters and setters
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

    public List<Question> getGameQuestions() {
        return gameQuestions;
    }

}
