package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.Question.Question;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class GameController {
    private final GameService gameService;
    private final PlayerService playerService;
    private final UserService userService;
    Logger logger = LoggerFactory.getLogger(GameController.class);

    GameController(GameService gameService,
                   PlayerService playerService,
                   UserService userService){
        this.gameService=gameService;
        this.playerService = playerService;
        this.userService = userService;
    }

    // create Lobby with GameSettings:
    // GameType (SINGLE or MULTI)
    // int rounds
    // GameMode (GuessThePrice or HigherOrLower)
    // Category (JEANS, SHOES, ACCESSORIES)
    @PostMapping("/lobbies")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public GameGetDTO createLobby(@RequestBody GamePostDTO gamePostDTO){
        Game gameInput = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);
        Game createdGame = gameService.createGame(gameInput); //how do we solve this? with game input or updateSettings?
        logger.info("Lobby " + createdGame.getGameId() + " created!");
        return addPlayersToGameGetDTO(createdGame);
    }

    @PutMapping("/lobbies/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO updateGameSetting(@PathVariable long lobbyId, @RequestBody GamePutDTO gamePutDTO) throws UnirestException, JsonProcessingException {
        Game updateGame = DTOMapper.INSTANCE.convertGamePutDTOToEntity(gamePutDTO);
        Game currentGame = gameService.updateGameSetting(updateGame, lobbyId);

        return addPlayersToGameGetDTO(currentGame);
    }

    //what does a player need? authorization?
    //add a player to the lobby with id
    @PostMapping("/lobbies/joinGame/{gamePin}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public PlayerGetDTO addPlayerToGame(@RequestBody PlayerPostDTO playerPostDTO, @PathVariable String gamePin){
        long userId = playerPostDTO.getId();
        Player player = userService.addUserToLobby(userId, gameService.getLobbyIdByGamePin(gamePin));
        logger.info("User with id: " + userId + " added to Lobby with id: " + player.getGameId() + " as Player with id: " + player.getPlayerId());
        return DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);
    }

    //Get mapping for game by id
    @GetMapping("lobbies/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameById(@PathVariable long lobbyId){
        Game game = gameService.getGameById(lobbyId);
        return addPlayersToGameGetDTO(game);
    }

    //Get mapping for players in lobby
    //return All Players of lobby
    @GetMapping("lobbies/{lobbyId}/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Player> getPlayersByLobbyId(@PathVariable long lobbyId){
        return playerService.getPlayersByLobbyId(lobbyId);
    }


    //Mapping to start a game (with new Settings?)
    @PostMapping("/lobbies/{lobbyId}/begin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void beginGame(@PathVariable long lobbyId) throws UnirestException, JsonProcessingException {
        //should only start if all player joined
        if (!gameService.didAllPlayersJoin(lobbyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cannot start the game until all players have joined the lobby.");
        }

        // Start the game
        logger.info("Lobby with Id: " + lobbyId + " started the game!");
        gameService.beginGame(lobbyId);
    }

    @GetMapping("/lobbies/{lobbyId}/beginStatus")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean isTheGameStarted(@PathVariable long lobbyId){
        return gameService.isTheGameStarted(lobbyId);
    }


    //would it be better to get Int-th question of lobby with lobby id?
    //get next question of lobby
    @GetMapping("/lobbies/{lobbyId}/nextQuestion")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public QuestionGetDTO getQuestion(@PathVariable long lobbyId){
        logger.info("Lobby with Id: " + lobbyId + "requested next question");
        Question nextQuestion = gameService.getCurrentRoundQuestion(lobbyId);
        return DTOMapper.INSTANCE.convertQuestionToQuestionGetDTO(nextQuestion);
    }

    /*
    use the getNextRound below first for whoever firstly go to the next round
    (also for first round call getNextRound first)
    and for others use the getQuestion above to fetch the same question
    without accumulate the currentRound count
     */

    @GetMapping("/lobbies/{lobbyId}/nextRound")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public QuestionGetDTO getNextRound(@PathVariable long lobbyId){
        logger.info("Lobby with Id: " + lobbyId + "requested next question");
        Question nextQuestion = gameService.getNextRound(lobbyId);
        return DTOMapper.INSTANCE.convertQuestionToQuestionGetDTO(nextQuestion);
    }


    //AnswerPostDTO:
    // long playerId;
    // int numOfRound;
    // String playerAnswer;
    // double timeUsed;
    // Question question;

    //player with id answered
    @PostMapping("lobbies/{lobbyId}/player/{playerId}/answered")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public void playerAnswered(@PathVariable long lobbyId, @PathVariable long playerId, @RequestBody AnswerPostDTO answerPostDTO){
        logger.info("Player with Id: " + playerId + " and lobbyId: " + lobbyId + " answered");
        Answer answer = DTOMapper.INSTANCE.convertAnswerPostDTOtoEntity(answerPostDTO);
        logger.info("The player answer is: " + answer);
        gameService.savePlayerAnswer(playerId, answer);
    }

    //did all player in lobby answer the question? (same amount of question?)
    @GetMapping("lobbies/{lobbyId}/allAnswered")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public boolean allPlayerAnswered(@PathVariable long lobbyId){
        boolean allPlayerAnswered = gameService.didAllPlayersAnswer(lobbyId);
        logger.info("in lobby with Id: " + lobbyId + " did all player answer: " + allPlayerAnswered);
        return allPlayerAnswered;
    }

    @GetMapping("/lobbies/{lobbyId}/articles")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Article> getAllArticlesInGame(@PathVariable long lobbyId){
        return gameService.getAllArticles(lobbyId);
    }

    public GameGetDTO addPlayersToGameGetDTO(Game game){
        List<Player> players = playerService.getPlayersByLobbyId(game.getGameId());
        GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
        gameGetDTO.setPlayers(players);
        return gameGetDTO;
    }

    @GetMapping("/lobbies/{lobbyId}/end")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public List<Player> endMiniGame(@PathVariable long lobbyId){
        logger.info("Lobby with Id: " + lobbyId + "ended the game!");
        return gameService.endMiniGame(lobbyId);
    }

    @PostMapping("/lobbies/{lobbyId}/end")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void clearLobby(@PathVariable long lobbyId){
        gameService.clearLobby(lobbyId);
    }

}
