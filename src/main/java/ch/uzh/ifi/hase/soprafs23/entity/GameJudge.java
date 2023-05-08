package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.entity.Question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.HigherLowerQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;

import java.util.List;
import java.util.Objects;

public class GameJudge {
    //question to know which answer is true
    private final Question question;
    //player to know which player to assign points
    //answer to know what the guess was and the time used etc. --> one can get answers of through player
    private final Player player;
    //answer to know what the guess was and the time used etc. --> one can get answers of through player
    private final int round;
    private final Answer playerAnswer;

    public GameJudge(Question question,Player player, int round){
        this.question = question;
        this.player = player;
        this.round = round;
        this.playerAnswer = player.getAnswerOfRound(round);
    }

    public boolean answerIsCorrect(String playerAnswer, String trueAnswer){
        return Objects.equals(playerAnswer, trueAnswer);
    }

    public int calculatePoints(){
        //if answer of player is correct or false
        //if correct assign points --> look if player is on streak.
        //if wrong assign no points --> look if player get penalty for bad answers.
        int points = 0;

        if(answerIsCorrect(this.playerAnswer.getPlayerAnswer(), this.question.getTrueAnswer())){
            //calculate the points
            points = (int) (10/this.playerAnswer.getTimeUsed());
            points *= this.question.getTimeToAnswer() * question.getBonus();

            //look for 3 streak? and add bonus
            //look for 5 streak? and add bonus
            //look for 10 streak and add bonus

        }
        else if(!answerIsCorrect(this.playerAnswer.getPlayerAnswer(), this.question.getTrueAnswer())){
            //look if the last 3 answers were wrong
            //if yes, minus points
            //else nothing
            points = 0;
        }
        return points;
    }

}
