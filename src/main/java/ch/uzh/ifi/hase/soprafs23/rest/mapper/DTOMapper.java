package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.AsosApi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

  DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "status", target = "status")
  UserGetDTO convertEntityToUserGetDTO(User user);


  @Mapping(source = "id", target = "id")
  @Mapping(source = "password", target = "password")
  @Mapping(source = "username", target = "username")
  @Mapping(source = "birthdate", target = "birthdate")
  User convertUserPutDTOToEntity(UserPutDTO userPutDTO);


  @Mapping(source = "gameType", target = "gameType")
  @Mapping(source = "rounds", target = "rounds")
  @Mapping(source = "gameMode", target = "gameMode")
  @Mapping(source = "category", target = "category")
  @Mapping(source = "numOfPlayer", target = "numOfPlayer")
  Game convertGamePostDTOtoEntity(GamePostDTO gamePostDTO);


  @Mapping(source = "gameId" , target = "gameId")
  @Mapping(source = "numOfPlayer" , target = "numOfPlayer")
  @Mapping(source = "gameType" , target = "gameType")
  @Mapping(source = "rounds" , target = "rounds")
  @Mapping(source = "gamePIN" , target = "gamePIN")
  @Mapping(source = "gameMode" , target = "gameMode")
  GameGetDTO convertEntityToGameGetDTO(Game game);

  @Mapping(source = "gameId" , target = "gameId")
  @Mapping(source = "numOfPlayer" , target = "numOfPlayer")
  @Mapping(source = "rounds" , target = "rounds")
  @Mapping(source = "gameMode" , target = "gameMode")
  @Mapping(source = "category", target = "category")
  Game convertGamePutDTOToEntity(GamePutDTO gamePutDTO);

  Answer convertAnswerPostDTOtoEntity(AnswerPostDTO answerPostDTO);

  @Mapping(source = "articles", target = "articles")
  @Mapping(source = "trueAnswer", target = "trueAnswer")
  @Mapping(source = "picUrls", target = "picUrls")
  @Mapping(source = "falseAnswers", target = "falseAnswers")
  QuestionGetDTO convertQuestionToQuestionGetDTO(Question nextQuestion);

  PlayerGetDTO convertEntityToPlayerGetDTO(Player player);
}
