package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.question.Question;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

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
  @Mapping(source = "birthdate",target = "birthdate")
  @Mapping(source = "creationDate",target = "creationDate")
  @Mapping(source = "numOfGameWon", target = "numOfGameWon")
  UserGetDTO convertEntityToUserGetDTO(User user);


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

  @Mapping(source="playerAnswer", target="playerAnswer")
  @Mapping(source="playerId", target="playerId")
  @Mapping(source="numOfRound", target="numOfRound")
  @Mapping(source="timeUsed", target="timeUsed")
  Answer convertAnswerPostDTOtoEntity(AnswerPostDTO answerPostDTO);

  @Mapping(source = "articles", target = "articles")
  @Mapping(source = "trueAnswer", target = "trueAnswer")
  @Mapping(source = "picUrls", target = "picUrls")
  @Mapping(source = "falseAnswers", target = "falseAnswers")
  QuestionGetDTO convertQuestionToQuestionGetDTO(Question nextQuestion);

  PlayerGetDTO convertEntityToPlayerGetDTO(Player player);
}
