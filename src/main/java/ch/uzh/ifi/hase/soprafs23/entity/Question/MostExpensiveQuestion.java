package ch.uzh.ifi.hase.soprafs23.entity.Question;

import ch.uzh.ifi.hase.soprafs23.entity.Article;

import java.util.List;
import java.util.Objects;

public class MostExpensiveQuestion extends Question{

    private final int bonus = 30;
    private int timeToAnswer = 30;

    public MostExpensiveQuestion(List<Article> fourArticles){
        articles = fourArticles;
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
}
