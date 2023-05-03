package ch.uzh.ifi.hase.soprafs23.entity;


import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.HigherLowerQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.MostExpensiveQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class MiniGame implements Serializable {

    private int rounds;
    protected int currentRound = 0;
    protected GameJudge judge; // not in the field
    private final List<Article> allArticles;
    private GameMode gameMode;
    private List<Question> gameQuestions = new ArrayList<>();

    Logger logger = LoggerFactory.getLogger(MiniGame.class);


    // constructor
    public MiniGame(int rounds, List<Article> articles, GameMode gameMode){
        this.rounds = rounds;
        this.allArticles = articles;
        this.gameMode = gameMode;
    } //pay attention to the number of articles

    //methods
    public void setGameQuestions(){
        List<Question> questions = new ArrayList<>();

        switch (this.gameMode) {
            case GuessThePrice -> {
                for (int i = 0; i < this.rounds; i++) {
                    GuessThePriceQuestion question = new GuessThePriceQuestion(allArticles.get(i));
                    questions.add(question);
                }
                logger.info("In MiniGame, GuessThePriceQuestions initialized!");
            }
            case HighOrLow -> {
                for (int i = 0; i < allArticles.size(); i += 2) {
                    List<Article> newList = allArticles.subList(i, i + 2);
                    HigherLowerQuestion question = new HigherLowerQuestion(newList.get(0), newList.get(1));
                    questions.add(question);
                }
                logger.info("In MiniGame, HighOrLowQuestions initialized!");
            }
            case MostExpensive -> {
                for (int i = 0; i < allArticles.size(); i += 4) {
                    List<Article> newList = allArticles.subList(i, i + 4);
                    List<Article> fourArticles = new ArrayList<>(newList);
                    MostExpensiveQuestion question = new MostExpensiveQuestion(fourArticles);
                    questions.add(question);
                }
                logger.info("In MiniGame, MostExpensiveQuestions initialized!");
            }
            default -> {
            }
            //unknown gameMode or error
        }

        for (Question question : questions){
            question.initializeQuestion();
        }

        this.gameQuestions = questions;
        logger.info("Questions are set for gameMode: " + this.gameMode);
    } // should the number of question is larger than the number of rounds? maybe one or two in case of unknown errors


    public boolean checkIfAllPlayersAnswered(List<Player> players){
        return players.stream()
                .allMatch(player -> player.getAnswers().size() == currentRound);
    } //frontend need to provide with a wrong answer if the player haven't answered when time was up.

    public Question showNextQuestion() {
        logger.info("Show next Question is invoked! currentRound=" + this.currentRound);
            Question question = getGameQuestions().get(currentRound);
            currentRound++;
            if (!question.isUsed()){
                question.setUsed(true);
            }
            return question;
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

    public List<Article> getAllArticles() {
        return this.allArticles;
    }

    public List<Question> getGameQuestions() {
        return gameQuestions;
    }
}
