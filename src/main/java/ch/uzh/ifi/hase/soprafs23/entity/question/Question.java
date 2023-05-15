package ch.uzh.ifi.hase.soprafs23.entity.question;

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
@DiscriminatorColumn(name = "questionType", discriminatorType = DiscriminatorType.STRING)
public abstract class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(unique = true)
    private long id;

    @ElementCollection(targetClass = Article.class)
    protected List<Article> articles = new ArrayList<>();

    @Column
    protected String trueAnswer;

    @ElementCollection(targetClass = String.class)
    protected List<String> falseAnswers = new ArrayList<>();

    @ElementCollection(targetClass = String.class)
    protected List<String> picUrls = new ArrayList<>();

    @Column
    private boolean isUsed = false;

    @Column(insertable = false, updatable = false)
    private String questionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "miniGameId")
    @JsonIgnore
    private MiniGame miniGame;

    abstract void generateFalseAnswers();

    protected abstract void setPicUrl();

    protected abstract void setTrueAnswer();

    public abstract int getBonus();

    public abstract int getTimeToAnswer();

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

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
