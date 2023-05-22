package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.entity.question.Question;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("username");
        userPostDTO.setBirthdate(LocalDate.of(2002,4,23));

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getPassword(), user.getPassword());
        assertEquals(userPostDTO.getUsername(), user.getUsername());
        assertEquals(userPostDTO.getBirthdate(), user.getBirthdate());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setPassword("password");
        user.setStatus(UserStatus.OFFLINE);
        user.setBirthdate(LocalDate.of(2002,4,24));
        user.setCreationDate(LocalDate.of(2002, 4 ,23));
        user.setToken("1");
        user.setNumOfGameWon(0);

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getPassword(), userGetDTO.getPassword());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
        assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
        assertEquals(user.getBirthdate(), userGetDTO.getBirthdate());
        assertEquals(user.getNumOfGameWon(), userGetDTO.getNumOfGameWon());
    }

    @Test
    public void testUpdateUser_fromUserPutDTO_toUser_success(){
        // create
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("aa");
        userPutDTO.setPassword("test");
        userPutDTO.setBirthdate(LocalDate.of(2002,4,24));

        User user = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);

        assertEquals(userPutDTO.getUsername(), user.getUsername());
        assertEquals(userPutDTO.getPassword(), user.getPassword());
        assertEquals(user.getBirthdate(), userPutDTO.getBirthdate());
    }

    @Test
    public void testSaveAnswer_fromAnswerPostDTO_toAnswer_success(){
        // create
        AnswerPostDTO answerPostDTO = new AnswerPostDTO();
        answerPostDTO.setPlayerAnswer("Higher");
        answerPostDTO.setPlayerId(3);
        answerPostDTO.setNumOfRound(1);
        answerPostDTO.setTimeUsed(30);

        Answer answer = DTOMapper.INSTANCE.convertAnswerPostDTOtoEntity(answerPostDTO);

        assertEquals(answerPostDTO.getPlayerAnswer(), answer.getPlayerAnswer());
        assertEquals(answerPostDTO.getNumOfRound(), answer.getNumOfRound());
        assertEquals(answerPostDTO.getPlayerId(), answer.getPlayerId());
        assertEquals(answerPostDTO.getTimeUsed(), answer.getTimeUsed());
    }

    // test for #87
    @Test
    public void testGetQuestion_fromQuestion_toQuestionGetDTO_success(){
        // create
        Question question = new GuessThePriceQuestion();
        List<Article> articles = new ArrayList<>();
        articles.add(new Article());
        List<String> pics = new ArrayList<>();
        pics.add("abcde");
        List<String> falseAns = new ArrayList<>();
        falseAns.add("100");
        question.setArticles(articles);
        question.setTrueAnswer("120");
        question.setFalseAnswers(falseAns);
        question.setPicUrls(pics);

        QuestionGetDTO questionGetDTO = DTOMapper.INSTANCE.convertQuestionToQuestionGetDTO(question);

        assertEquals(question.getArticles(), questionGetDTO.getArticles());
        assertEquals(question.getTrueAnswer(), questionGetDTO.getTrueAnswer());
        assertEquals(question.getPicUrls(), questionGetDTO.getPicUrls());
        assertEquals(question.getFalseAnswers(), questionGetDTO.getFalseAnswers());
    }

}
