package ch.uzh.ifi.hase.soprafs23.entity.question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;
import java.util.Objects;

@Entity
@DiscriminatorValue("ME")
@Cacheable
public class MostExpensiveQuestion extends Question{

    @Column
    private final int BONUS = 30;

    @Column
    private final int TIME_TO_ANSWER = 30;

    public MostExpensiveQuestion(List<Article> fourArticles){
        articles = fourArticles;
    }

    public MostExpensiveQuestion() {

    }

    @Override
    void generateFalseAnswers() {
        for (Article article : articles){
            int trueAnswer = Integer.parseInt(super.trueAnswer);
            int productId = article.getProductId();
            if ( trueAnswer != productId){
                falseAnswers.add(String.valueOf(productId));
            }
        }
    }

    @Override
    protected void setPicUrl() {
        for (Article article : articles) {
            picUrls.add(article.getImageUrl());
        }
    }

    @Override
    protected void setTrueAnswer() {
        Article mostExpensive = null;
        float maxPrice = Float.MIN_VALUE;

        for (Article article : articles){
            if (article.getPrice() > maxPrice){
                maxPrice = article.getPrice();
                mostExpensive = article;
            }
        }

        trueAnswer = String.valueOf(Objects.requireNonNull(mostExpensive).getProductId());
    }

    @Override
    public int getTimeToAnswer(){
        return TIME_TO_ANSWER;
    }
    @Override
    public int getBonus(){
        return BONUS;
    }
}
