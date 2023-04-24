package ch.uzh.ifi.hase.soprafs23.entity.MiniGame;


import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.GameJudge;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.HigherLowerQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class MiniGame {

    private int rounds;
    protected int currentRound = 0;
    protected GameJudge judge;
    private List<Player> activePlayers = new ArrayList<>();
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
        logger.info("Questions are set for gameMode: " + this.gameMode);
        if (this.gameMode == GameMode.GuessThePrice){
            logger.info("In MiniGame are GuessThePriceQuestions initialized!");
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
        return activePlayers.stream()
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

    public void updatePlayerPoints() {
        for (Player player : this.getActivePlayers()){
            judge = new GameJudge(this.getGameQuestions(), player, currentRound);
            int point = judge.calculatePoints();
            player.setRoundScore(point);
            player.setTotalScore(player.getTotalScore()+point);
        }
    }

    public void syncPlayerInfo(Player currentPlayer) {
        Optional<Player> foundPlayer = activePlayers.stream()
                .filter(player -> player.getPlayerId() == currentPlayer.getPlayerId())
                .findFirst();

        foundPlayer.ifPresent(player -> {
                    player.copyFrom(currentPlayer);
        });
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
