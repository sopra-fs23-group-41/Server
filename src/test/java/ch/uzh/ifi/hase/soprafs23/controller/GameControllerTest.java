package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.asosapi.Category;
import ch.uzh.ifi.hase.soprafs23.constant.GameMode;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.GamePutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.service.GameService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;


@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GameService gameService;
    @MockBean
    private PlayerService playerService;
    @MockBean
    private UserService userService;

    @Test
    public void createLobbyTest() throws Exception {
        //what do i have to mock:
        //service to create a Game
        //dto mapper to convert gamepostDTO to game

        //gamepostDTO
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
    public void updateGameSettingTest() throws Exception {
        //gamePutDTO as input
        GamePutDTO gamePutDTO = new GamePutDTO();
        gamePutDTO.setGameId(0);
        gamePutDTO.setNumOfPlayer(1);
        gamePutDTO.setRounds(4);
        gamePutDTO.setGameMode(GameMode.HighOrLow);
        gamePutDTO.setCategory(Category.JEANS);

        //the updated game from mocked gameservice
        Game game = new Game();
        game.setGameId(0);
        game.setNumOfPlayer(1);
        game.setGameType(GameType.SINGLE);
        game.setRounds(4);
        game.setGamePIN("1234");
        game.setGameMode(GameMode.HighOrLow);
        game.setCategory(Category.JEANS);

        //to mock the playerservice
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
    public void addPlayerToGameTest() throws Exception {
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

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input
     * can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     *
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
