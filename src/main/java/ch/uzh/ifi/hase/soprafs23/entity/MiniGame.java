package ch.uzh.ifi.hase.soprafs23.entity;


import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.entity.question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.question.HigherLowerQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.question.MostExpensiveQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.question.Question;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "MINIGAME")
@JsonIgnoreProperties(value="gameQuestions")
public class MiniGame implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(unique = true)
    private long miniGameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private Game game;

    @Column
    private int rounds;

    @Column
    protected int currentRound = 0;

    @ElementCollection
    private List<Article> allArticles;

    @Column
    @Enumerated(EnumType.STRING)
    private GameMode gameMode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true ,fetch = FetchType.LAZY)
    @JoinColumn(name = "miniGameId")
    @JsonIgnore
    private List<Question> gameQuestions = new ArrayList<>();



    // constructor
    public MiniGame(int rounds, List<Article> articles, GameMode gameMode){
        this.rounds = rounds;
        this.allArticles = articles;
        this.gameMode = gameMode;
    } //pay attention to the number of articles

    public MiniGame() {

    }

    //methods
    public void setGameQuestions(){
        List<Question> questions = new ArrayList<>();

        switch (this.gameMode) {
            case GuessThePrice -> {
                for (int i = 0; i < this.rounds; i++) {
                    GuessThePriceQuestion question = new GuessThePriceQuestion(allArticles.get(i));
                    question.setQuestionType("GTP");
                    questions.add(question);
                }
            }
            case HighOrLow -> {
                for (int i = 0; i < allArticles.size(); i += 2) {
                    List<Article> newList = allArticles.subList(i, i + 2);
                    HigherLowerQuestion question = new HigherLowerQuestion(newList.get(0), newList.get(1));
                    question.setQuestionType("HOL");
                    questions.add(question);
                }
            }
            case MostExpensive -> {
                for (int i = 0; i < allArticles.size(); i += 4) {
                    List<Article> newList = allArticles.subList(i, i + 4);
                    List<Article> fourArticles = new ArrayList<>(newList);
                    MostExpensiveQuestion question = new MostExpensiveQuestion(fourArticles);
                    question.setQuestionType("ME");
                    questions.add(question);
                }
            }
            default -> {
            }
            //unknown gameMode or error
        }

        for (Question question : questions){
            question.initializeQuestion();
        }

        this.gameQuestions = questions;
    } // should the number of question is larger than the number of rounds? maybe one or two in case of unknown errors


    public boolean checkIfAllPlayersAnswered(List<Player> players){
        return players.stream()
                .allMatch(player -> player.getAnswers().size() == currentRound);
    } //frontend need to provide with a wrong answer if the player haven't answered when time was up.

    public Question showNextQuestion() {
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
    }

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

    public long getMiniGameId() {
        return miniGameId;
    }

    public void setMiniGameId(long miniGameId) {
        this.miniGameId = miniGameId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setAllArticles(List<Article> allArticles) {
        this.allArticles = allArticles;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setGameQuestions(List<Question> gameQuestions) {
        this.gameQuestions = gameQuestions;
    }
}
