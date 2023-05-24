package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.asosapi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.ProfilePicture;
import ch.uzh.ifi.hase.soprafs23.entity.Answer;
import ch.uzh.ifi.hase.soprafs23.entity.Article;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.question.GuessThePriceQuestion;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AnswerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GameController.class)
class GameControllerTest {
    @Mock
    private GuessThePriceQuestion mockQuestion;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService gameService;
    @MockBean
    private PlayerService playerService;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createLobbyTest() throws Exception {

        //gamePostDTO
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setGameType(GameType.SINGLE);
        gamePostDTO.setRounds(3);
        gamePostDTO.setGameMode(GameMode.GuessThePrice);
        gamePostDTO.setCategory(Category.JEANS);
        gamePostDTO.setNumOfPlayer(1);

        //given
        Game game = new Game();
        game.setNumOfPlayer(1);
        game.setGameType(GameType.SINGLE);
        game.setRounds(3);
        game.setGamePIN("1234");
        game.setGameMode(GameMode.GuessThePrice);
        game.setCategory(Category.SNEAKERS);

        List<Player> players = new ArrayList<>();

        //mock gameService to return the game
        given(gameService.createGame(Mockito.any())).willReturn(game);
        given(playerService.getPlayersByLobbyId(Mockito.anyLong())).willReturn(players);

        //when
        MockHttpServletRequestBuilder postRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));
        //then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gameId", is((int) game.getGameId())))
                .andExpect(jsonPath("$.numOfPlayer", is(game.getNumOfPlayer())))
                .andExpect(jsonPath("$.gameType", is(game.getGameType().toString())))
                .andExpect(jsonPath("$.rounds", is(game.getRounds())))
                .andExpect(jsonPath("$.gamePIN", is(game.getGamePIN())))
                .andExpect(jsonPath("$.gameMode", is(game.getGameMode().toString())))
                .andExpect(jsonPath("$.players", hasSize(0)));

    }

    @Test
    void updateGameSettingTest() throws Exception {
        //gamePutDTO as input
        GamePutDTO gamePutDTO = new GamePutDTO();
        gamePutDTO.setGameId(0);
        gamePutDTO.setNumOfPlayer(1);
        gamePutDTO.setRounds(4);
        gamePutDTO.setGameMode(GameMode.HighOrLow);
        gamePutDTO.setCategory(Category.JEANS);

        //the updated game from mocked gameService
        Game game = new Game();
        game.setGameId(0);
        game.setNumOfPlayer(1);
        game.setGameType(GameType.SINGLE);
        game.setRounds(4);
        game.setGamePIN("1234");
        game.setGameMode(GameMode.HighOrLow);
        game.setCategory(Category.JEANS);

        //to mock the playerService
        List<Player> players = new ArrayList<>();

        //mock gameService to return an updated game
        given(gameService.updateGameSetting(Mockito.any(), Mockito.anyLong())).willReturn(game);
        given(playerService.getPlayersByLobbyId(Mockito.anyLong())).willReturn(players);

        //make Put call
        MockHttpServletRequestBuilder putRequest = put("/lobbies/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePutDTO));

        //look if json is correct
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is((int) game.getGameId())))
                .andExpect(jsonPath("$.numOfPlayer", is(game.getNumOfPlayer())))
                .andExpect(jsonPath("$.gameType", is(game.getGameType().toString())))
                .andExpect(jsonPath("$.rounds", is(game.getRounds())))
                .andExpect(jsonPath("$.gamePIN", is(game.getGamePIN())))
                .andExpect(jsonPath("$.gameMode", is(game.getGameMode().toString())))
                .andExpect(jsonPath("$.players", hasSize(0)));

    }

    @Test
    void addPlayerToGameTest() throws Exception {
        //mapping
        //mock userService to return the player
        //required = PlayerPostDTO, pathVariable String gamePin

        //required input
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setId(1L); //userId
        playerPostDTO.setUsername("Username");
        playerPostDTO.setName("Name");


        //required return of mocks
        Game game = new Game();
        game.setGameId(0);
        game.setNumOfPlayer(1);
        game.setGameType(GameType.SINGLE);
        game.setRounds(4);
        game.setGamePIN("1234");
        game.setGameMode(GameMode.HighOrLow);
        game.setCategory(Category.JEANS);

        Player player = new Player();
        player.setPlayerName("Username");
        player.setUserId(1L);
        player.setGameId(0);

        //mock used services
        given(gameService.getLobbyIdByGamePin(Mockito.anyString())).willReturn(game.getGameId());
        given(userService.addUserToLobby(Mockito.anyLong(), Mockito.anyLong())).willReturn(player);

        //make post call
        MockHttpServletRequestBuilder postRequest = post("/lobbies/joinGame/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(playerPostDTO));

        mockMvc.perform(postRequest)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.playerId", is(((int) player.getPlayerId()))))
                .andExpect(jsonPath("$.playerName", is(player.getPlayerName())))
                .andExpect(jsonPath("$.userId", is((int)player.getUserId())))
                .andExpect(jsonPath("$.gameId", is((int)player.getGameId())))
                .andExpect(jsonPath("$.totalScore", is(player.getTotalScore())))
                .andExpect(jsonPath("$.roundScore", is(player.getRoundScore())))
                .andExpect(jsonPath("$.answers", hasSize(0)));
    }

    @Test
    void getGameByIdTest() throws Exception {
        //mapping
        //mock gameService, userService to return the game, player
        //required = player assigned to the game, Game, saved in database

        //required return of mocks
        Game game = new Game();
        game.setGameId(0);
        game.setNumOfPlayer(1);
        game.setGameType(GameType.SINGLE);
        game.setRounds(4);
        game.setGamePIN("1234");
        game.setGameMode(GameMode.HighOrLow);
        game.setCategory(Category.JEANS);

        //assign player to game
        Player player = new Player();
        player.setPlayerName("Username");
        player.setUserId(1L);
        player.setGameId(0);
        List<Player> players = new ArrayList<>();
        players.add(player);

        //mock used services
        given(gameService.getGameById(Mockito.anyLong())).willReturn(game);
        given(playerService.getPlayersByLobbyId(Mockito.anyLong())).willReturn(players);

        //make get call
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 0)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId", is((0))))
                .andExpect(jsonPath("$.numOfPlayer", is(1)))
                .andExpect(jsonPath("$.gameType", is("SINGLE")))
                .andExpect(jsonPath("$.rounds", is(4)))
                .andExpect(jsonPath("$.gamePIN", is("1234")))
                .andExpect(jsonPath("$.gameMode", is("HighOrLow")))
                .andExpect(jsonPath("$.category", is("JEANS")));
    }

    @Test
    void getPlayersByLobbyIdTest() throws Exception {
        //mapping
        //mock gameService, player to return the game, player
        //required = player assigned to the game, Game, saved in database

        //required return of mocks
        Game game = new Game();
        game.setGameId(1L);
        game.setNumOfPlayer(1);
        game.setGameType(GameType.SINGLE);
        game.setRounds(4);
        game.setGamePIN("1234");
        game.setGameMode(GameMode.HighOrLow);
        game.setCategory(Category.JEANS);

        //assign player1 to game
        Player player1 = new Player();
        player1.setPlayerName("Username2");
        player1.setUserId(1L);
        player1.setGameId(1L);

        //assign player2 to game
        Player player2 = new Player();
        player2.setPlayerName("Username2");
        player2.setUserId(1L);
        player2.setGameId(1L);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        //mock used services
        //given(gameService.getGameById(Mockito.anyLong())).willReturn(game);
        given(playerService.getPlayersByLobbyId(Mockito.anyLong())).willReturn(players);

        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/players")
                .contentType(MediaType.APPLICATION_JSON);

        //then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].playerId", is((int) player1.getPlayerId())))
                .andExpect(jsonPath("$[0].playerName", is(player1.getPlayerName())))
                .andExpect(jsonPath("$[0].userId", is((int) player1.getUserId())))
                .andExpect(jsonPath("$[0].gameId", is((int) player1.getGameId())))
                .andExpect(jsonPath("$[1].playerId", is((int) player2.getPlayerId())))
                .andExpect(jsonPath("$[1].playerName", is(player2.getPlayerName())))
                .andExpect(jsonPath("$[1].userId", is((int) player2.getUserId())))
                .andExpect(jsonPath("$[1].gameId", is((int) player2.getGameId())));
    }

    @Test
    void beginGameWithoutGame_throwsExceptionTest() throws Exception {
        // make gameService throw the correct Exception when no game with that id exists
        given(gameService.didAllPlayersJoin(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        // make post call
        //when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/" + 1 + "/begin");
        // then
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }
    @Test
    void beginGameWithoutPlayer_throwsExceptionTest() throws Exception {
        // make gameService throw the correct Exception when no players are found
        given(gameService.didAllPlayersJoin(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        // make post call
        //when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/" + 1 + "/begin");
        // then
        mockMvc.perform(postRequest).andExpect(status().isBadRequest());
    }

    @Test
    void beginGameTest() throws Exception {
        // make gameService return true, when game exists and all players joined
        given(gameService.didAllPlayersJoin(Mockito.anyLong())).willReturn(true);
        //make gameService.begin(game) return void if successfully
        Mockito.doNothing().when(gameService).beginGame(Mockito.anyLong());
        //when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/" + 1 + "/begin");
        // then
        mockMvc.perform(postRequest).andExpect(status().isNoContent());
    }

    @Test
    void isTheGameStartedFalseTest() throws Exception {
        //return false
        given(gameService.isTheGameStarted(Mockito.anyLong())).willReturn(false);
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/beginStatus");
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk()).andExpect(jsonPath("$").value(false));
    }

    @Test
    void isTheGameStartedTrueTest() throws Exception {
        //return false
        given(gameService.isTheGameStarted(Mockito.anyLong())).willReturn(true);
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/beginStatus");
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk()).andExpect(jsonPath("$").value(true));
    }

    @Test
    void isTheGameStartedThrowExceptionTest() throws Exception {
        //return false
        given(gameService.isTheGameStarted(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/beginStatus");
        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    void isNextRoundSetFalseTest() throws Exception {
        //return false
        given(gameService.nextRoundReady(Mockito.anyLong(), Mockito.anyLong())).willReturn(false);
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/QuestionStatus/" + 1);
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk()).andExpect(jsonPath("$").value(false));
    }
    @Test
    void isNextRoundSetTrueTest() throws Exception {
        //return false
        given(gameService.nextRoundReady(Mockito.anyLong(), Mockito.anyLong())).willReturn(true);
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/QuestionStatus/" + 1);
        // then
        mockMvc.perform(getRequest).andExpect(status().isOk()).andExpect(jsonPath("$").value(true));
    }

    @Test
    void isNextRoundSetThrowsExceptionTest() throws Exception {
        // throw exception
        given(gameService.nextRoundReady(Mockito.anyLong(), Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/QuestionStatus/" + 1);
        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    void getQuestionThrowsExceptionTest() throws Exception {
        // throw exception
        given(gameService.getCurrentRoundQuestion(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/nextQuestion/");
        // then
        mockMvc.perform(getRequest).andExpect(status().isBadRequest());
    }

    @Test
    void getQuestionTest() throws Exception {
        //return false
        given(gameService.getCurrentRoundQuestion(Mockito.anyLong())).willReturn(mockQuestion);
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/nextQuestion");
        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.articles", isA(List.class)))
                .andExpect(jsonPath("$.trueAnswer").value(nullValue()))
                .andExpect(jsonPath("$.picUrls", isA(List.class)))
                .andExpect(jsonPath("$.falseAnswers", isA(List.class)));
    }

    @Test
    void getNextRoundTest() throws Exception {
        //return false
        given(gameService.getNextRound(Mockito.anyLong())).willReturn(mockQuestion);
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/nextRound");
        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.articles", isA(List.class)))
                .andExpect(jsonPath("$.trueAnswer").value(nullValue()))
                .andExpect(jsonPath("$.picUrls", isA(List.class)))
                .andExpect(jsonPath("$.falseAnswers", isA(List.class)));
    }

    @Test
    void getNextRoundThrowsExceptionTest() throws Exception {
        //return false
        given(gameService.getNextRound(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/nextRound");
        // then
        mockMvc.perform(getRequest).andExpect(status().isNotFound());
    }

    @Test
    void playerAnsweredTest() throws Exception {
        //set up AnswerPostDTO
        AnswerPostDTO answerPostDTO = new AnswerPostDTO();
        //mockGameService
        doNothing().when(gameService).savePlayerAnswer(Mockito.anyLong(),Mockito.any(Answer.class));
        //when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/" + 1 + "/player/" + 1 + "/answered")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(answerPostDTO));
        //then
        mockMvc.perform(postRequest).andExpect(status().isAccepted());
    }

    @Test
    void playerAnsweredThrowsExceptionTest() throws Exception {
        //set up AnswerPostDTO
        AnswerPostDTO answerPostDTO = new AnswerPostDTO();
        //mockGameService when no player exists
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(gameService).savePlayerAnswer(Mockito.anyLong(),Mockito.any(Answer.class));
        //when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/" + 1 + "/player/" + 1 + "/answered")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(answerPostDTO));
        //then
        mockMvc.perform(postRequest).andExpect(status().isNotFound());
    }

    @Test
    void allPlayerAnsweredTest() throws Exception {
        given(gameService.didAllPlayersAnswer(Mockito.anyLong())).willReturn(true);
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/allAnswered");
        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void getAllArticlesInGameTest() throws Exception {
        List<Article> mockedArticles = new ArrayList<>();
        Article mockArticle = new Article();
        mockArticle.setProductId(1);
        mockArticle.setName("Name");
        mockArticle.setPrice(3f);
        mockArticle.setBrandName("BrandName");
        mockArticle.setUrl("Url");
        mockArticle.setImageUrl("ImageURL");
        mockedArticles.add(mockArticle);
        given(gameService.getAllArticles(Mockito.anyLong())).willReturn(mockedArticles);
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/articles");
        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].productId", is(mockArticle.getProductId())))
                .andExpect(jsonPath("$[0].name", is(mockArticle.getName())))
                .andExpect(jsonPath("$[0].price", is(3.0)))
                .andExpect(jsonPath("$[0].brandName", is(mockArticle.getBrandName())))
                .andExpect(jsonPath("$[0].url", is(mockArticle.getUrl())))
                .andExpect(jsonPath("$[0].imageUrl", is(mockArticle.getImageUrl())));

    }

    @Test
    void getAllArticlesInGameThrowsExceptionTest() throws Exception {
        given(gameService.getAllArticles(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/articles");
        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());

    }

    @Test
    void endMiniGameTest() throws Exception {
        // set up player
        Player player = new Player();
        player.setPlayerId(1L);
        player.setPlayerName("Name");
        player.setUserId(1L);
        player.setGameId(1L);
        player.setProfilePicture(ProfilePicture.ONE);
        player.setTotalScore(1);
        player.setRoundScore(2);
        //create list to be returned by mocked method
        List<Player> players = new ArrayList<>();
        players.add(player);
        given(gameService.endMiniGame(Mockito.anyLong())).willReturn(players);
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/end");
        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].playerId", is((int) player.getPlayerId())))
                .andExpect(jsonPath("$[0].playerName", is(player.getPlayerName())))
                .andExpect(jsonPath("$[0].userId", is((int) player.getUserId())))
                .andExpect(jsonPath("$[0].profilePicture", is(player.getProfilePicture().name())))
                .andExpect(jsonPath("$[0].totalScore", is(player.getTotalScore())))
                .andExpect(jsonPath("$[0].roundScore", is(player.getRoundScore())))
                .andExpect(jsonPath("$[0].streak", is(player.getStreak())))
                .andExpect(jsonPath("$[0].answers", hasSize(0)));
    }

    @Test
    void endMiniGameThrowExceptionNoGameFoundTest() throws Exception {
        given(gameService.endMiniGame(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/end");
        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound());
    }
    @Test
    void endMiniGameThrowExceptionNoPlayerTest() throws Exception {
        given(gameService.endMiniGame(Mockito.anyLong())).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        //when
        MockHttpServletRequestBuilder getRequest = get("/lobbies/" + 1 + "/end");
        //then
        mockMvc.perform(getRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    void clearLobbyTest() throws Exception {
        Mockito.doNothing().when(gameService).clearLobby(Mockito.anyLong(), Mockito.anyLong());
        //when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/" + 1 + "/" + 1 + "/end");
        //then
        mockMvc.perform(postRequest)
                .andExpect(status().isOk());
    }

    @Test
    void clearLobbyThrowsExceptionTest() throws Exception {
        Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(gameService).clearLobby(Mockito.anyLong(), Mockito.anyLong());
        //when
        MockHttpServletRequestBuilder postRequest = post("/lobbies/" + 1 +"/"+ 1 + "/end");
        //then
        mockMvc.perform(postRequest)
                .andExpect(status().isNotFound());
    }



    /**
     * Helper Method to convert DTOs into a JSON string such that the input
     * can be processed
     * Input may look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e));
        }
    }
}
