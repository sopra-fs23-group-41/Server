package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.MiniGame;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(unique = true)
    private long Id;

    @ElementCollection
    protected List<Article> articles = new ArrayList<>();

    @Column
    protected String trueAnswer;

    @ElementCollection
    protected List<String> falseAnswers = new ArrayList<>();

    @ElementCollection
    protected List<String> picUrls = new ArrayList<>();

    @Column
    private boolean isUsed = false;

    @Column(insertable = false, updatable = false)
    private String question_type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "miniGameId")
    @JsonIgnore
    private MiniGame miniGame;

    abstract void generateFalseAnswers();
    abstract protected void setPicUrl();
    abstract protected void setTrueAnswer();

    // methods
    public void initializeQuestion(){
        setPicUrl();
        setTrueAnswer();
        generateFalseAnswers();
    }

    // getters and setters
    public List<Article> getArticles(){
        return this.articles;
    }

    public List<String> getPicUrls(){
        return this.picUrls;
    }

    public boolean isUsed() {
        return this.isUsed;
    }

    public void setUsed(boolean used) {
        this.isUsed = used;
    }

    public String getTrueAnswer(){
        return this.trueAnswer;
    }

    public List<String> getFalseAnswers() {
        return falseAnswers;
    }

    public void setQuestion_type(String question_type) {
        this.question_type = question_type;
    }

    public String getQuestion_type() {
        return question_type;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public void setTrueAnswer(String trueAnswer) {
        this.trueAnswer = trueAnswer;
    }

    public void setFalseAnswers(List<String> falseAnswers) {
        this.falseAnswers = falseAnswers;
    }

    public void setPicUrls(List<String> picUrls) {
        this.picUrls = picUrls;
    }

    public MiniGame getMiniGame() {
        return miniGame;
    }

    public void setMiniGame(MiniGame miniGame) {
        this.miniGame = miniGame;
    }
}
