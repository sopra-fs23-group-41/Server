package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;

import ch.uzh.ifi.hase.soprafs23.AsosApi.Article;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
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
    protected GameJudge judge;
    private List<Player> activePlayers = new ArrayList<>();
    private final List<Article> allArticles;
    private GameMode gameMode;
    private List<Question> gameQuestions = new ArrayList<>();


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
    } // should the number of question is larger than the number of rounds? maybe one or two in case of unknown errors

    public boolean checkIfAllPlayersAnswered(){
        List<Player> players = this.activePlayers;
        for (int i=0; i< players.size();i++){
            if(players.get(i).getAnswers().size() == currentRound){
                continue;
            }
            else return false;
        }
        return true;
    } //frontend need to provide with a wrong answer if the player haven't answered when time was up.

    public Question showNextQuestion() {
            Question question = getGameQuestions().get(currentRound);
            currentRound++;
            if (!question.isUsed()){
                question.setUsed(true);
            }
            return question;
    }

    public void updatePlayerPoints() {
        for (Player player : getActivePlayers()){
            judge = new GameJudge(getGameQuestions(), player, currentRound);
            int point = judge.calculatePoints();
            player.setRoundScore(point);
            player.setTotalScore(player.getTotalScore()+point);
        }
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
