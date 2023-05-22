package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Test
  void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setUsername("firstname@lastname");
    user.setPassword("password");
    user.setStatus(UserStatus.OFFLINE);
    user.setBirthdate(LocalDate.of(2002,4,24));
    user.setCreationDate(LocalDate.of(2002, 4 ,23));

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].password", is(user.getPassword())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].birthdate", is(String.valueOf(user.getBirthdate()))))
        .andExpect(jsonPath("$[0].creationDate", is(String.valueOf(user.getCreationDate()))))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));

  }

  // test for #81
  @Test
  void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("password");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.password", is(user.getPassword())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  void logoutUser_success() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setUsername("testUsername");
      user.setToken("1");
      user.setStatus(UserStatus.ONLINE);

      given(userService.createUser(Mockito.any())).willReturn(user);

      MockHttpServletRequestBuilder getRequest = get("/users/1/logout");

      mockMvc.perform(getRequest)
              .andExpect(status().isOk());
  }

  @Test
  void loginUser_success() throws Exception {
      // given
      User user = new User();
      user.setId(1L);
      user.setUsername("testUsername");
      user.setPassword("test");
      user.setToken("1");
      user.setStatus(UserStatus.ONLINE);

      UserGetDTO userGetDTO = new UserGetDTO();
      userGetDTO.setId(user.getId());
      userGetDTO.setPassword("test");
      userGetDTO.setUsername("testUsername");
      userGetDTO.setStatus(UserStatus.ONLINE);

      given(userService.loginUser("testUsername", "test")).willReturn(user);

      MockHttpServletRequestBuilder postRequest = post("/users/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userGetDTO));

      mockMvc.perform(postRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id", is(user.getId().intValue())))
              .andExpect(jsonPath("$.password", is(user.getPassword())))
              .andExpect(jsonPath("$.username", is(user.getUsername())))
              .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test
  void updateUserProfile_success() throws Exception {
      // given
      Long userId = 1L;
      UserPutDTO userPutDTO = new UserPutDTO();
      userPutDTO.setUsername("updatedUsername");
      userPutDTO.setPassword("update");

      User updatedUser = new User();
      updatedUser.setId(userId);
      updatedUser.setUsername(userPutDTO.getUsername());
      updatedUser.setPassword(userPutDTO.getPassword());

      given(userService.updateUserProfile(userId, DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO)))
              .willReturn(updatedUser);

      MockHttpServletRequestBuilder putRequest = put("/users/{id}", userId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPutDTO));

      mockMvc.perform(putRequest)
              .andExpect(status().isAccepted());
  }

  @Test
  void getAUser_success() throws Exception {
      // given
      Long userId = 1L;
      User user = new User();
      user.setId(userId);
      user.setUsername("testUser");
      user.setPassword("password");

      UserGetDTO userGetDTO = new UserGetDTO();
      userGetDTO.setId(user.getId());
      userGetDTO.setUsername(user.getUsername());
      userGetDTO.setPassword(user.getPassword());

      given(userService.getUserById(userId)).willReturn(user);

      MockHttpServletRequestBuilder getRequest = get("/users/{id}", userId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(userGetDTO));

      mockMvc.perform(getRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.id").value(user.getId().intValue()))
              .andExpect(jsonPath("$.username").value(user.getUsername()))
              .andExpect(jsonPath("$.password").value(user.getPassword()));
    }

    // this test also verifies that all user can be retrieved from backend #83
    // test for #148
  @Test
  void getLeaderBoard_success() throws Exception {
      List<User> users = new ArrayList<>();
      User user1 = new User();
      user1.setNumOfGameWon(1);
      User user2 = new User();
      user2.setNumOfGameWon(2);
      users.add(user1);
      users.add(user2);

      given(userService.getUserLeaderBoard()).willReturn(users);

      MockHttpServletRequestBuilder getRequest = get("/users/leaderboard");
      
      mockMvc.perform(getRequest)
              .andExpect(status().isOk());
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
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