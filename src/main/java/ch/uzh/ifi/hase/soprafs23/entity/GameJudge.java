package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import java.util.Objects;

public class GameJudge {
    //question to know which answer is true
    private final Question question;
    //player to know which player to assign points
    //answer to know what the guess was and the time used etc. --> one can get answers of through player
    private final Player player;
    //answer to know what the guess was and the time used etc. --> one can get answers of through player
    private final Answer playerAnswer;

    private final int round;

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

            //look for 3 streak? and add bonus points of 300
            if(this.player.getStreak() == 2){
                points += 300;
            }
            //look for 5 streak? and add bonus points of 700
            if(this.player.getStreak() == 4){
                points += 700;
            }
            //look for 7 streak and add bonus points of 1200
            if(this.player.getStreak() == 6){
                points += 1200;
            }

        }
        else if(!answerIsCorrect(this.playerAnswer.getPlayerAnswer(), this.question.getTrueAnswer())){
            //look if the last 3 answers were wrong
            //if yes, minus points
            //else nothing
            points = 0;
        }
        if(bonusround(round)){
            points *= 2;
        }


        return points;
    }

    private boolean bonusround(int round){
        return round % 3 == 0;
    }

}
