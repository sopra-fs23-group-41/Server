package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @BeforeEach
  public void setup() {
      MockitoAnnotations.openMocks(this);

      // given
      testUser = new User();
      testUser.setUsername("firstname@lastname");
      testUser.setPassword("password");
      testUser.setStatus(UserStatus.OFFLINE);
      testUser.setBirthdate(LocalDate.of(2002,4,24));
      testUser.setCreationDate(LocalDate.of(2002, 4 ,23));

      // when -> any object is being saved in the userRepository -> return the dummy
      // testUser
      Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertEquals(testUser.getCreationDate(), createdUser.getCreationDate());
    assertEquals(testUser.getBirthdate(), createdUser.getBirthdate());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void addUserToLobby_success(){
      long userId = 1L;
      long lobbyId = 2L;
      testUser.setId(userId);

      Mockito.when(userRepository.findById(userId)).thenReturn(testUser);

      Player player = new Player();
      player.setUserId(userId);
      player.setGameId(lobbyId);

      Mockito.when(playerRepository.save(player)).thenReturn(player);

      Player addedPlayer = userService.addUserToLobby(userId,lobbyId);

      Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
      Mockito.verify(playerRepository, Mockito.times(1)).findByUserId(userId);
      Mockito.verify(playerRepository, Mockito.times(1)).save(Mockito.any());

      //assertEquals(addedPlayer, player);
  }

  @Test
  public void userLogin_withFalseCredential_throwException(){
      String loginUsername = "testUser";
      String password = "incorrectPassword";
      Mockito.when(userRepository.findByUsername(loginUsername)).thenReturn(null);

      assertThrows(ResponseStatusException.class, () -> userService.loginUser(loginUsername, password));
  }

  @Test
  public void userLogin_whenUserAlreadyLogin_throwException(){
      String loginUsername = "testUser";
      String password = "password";
      User userToLogin = new User();
      userToLogin.setPassword(password);
      userToLogin.setUsername(loginUsername);
      userToLogin.setStatus(UserStatus.ONLINE);
      userService.createUser(userToLogin);
      Mockito.when(userRepository.findByUsername(loginUsername)).thenReturn(userToLogin);

      assertThrows(ResponseStatusException.class, () -> {
          userService.loginUser(loginUsername, password);
      });
  }

    @Test
    void loginUser_ValidCredentials_SuccessfulLogin() {
        // Arrange
        String loginUsername = "testUser";
        String password = "password";
        User userToLogin = new User();
        userToLogin.setUsername(loginUsername);
        userToLogin.setPassword(password);
        User user = userService.createUser(userToLogin);
        user.setStatus(UserStatus.OFFLINE);

        String encodedPassword = passwordEncoder.encode(password);

        user.setPassword(encodedPassword);

        Mockito.when(userRepository.findByUsername(loginUsername)).thenReturn(user);

        // Act
        User loggedInUser = userService.loginUser(loginUsername, password);

        // Assert
        assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(userRepository, Mockito.times(2)).flush();
    }

    @Test
    void logoutUser_success(){
      Mockito.when(userRepository.save(testUser)).thenReturn(testUser);
      User loginUser = userService.createUser(testUser);
      loginUser.setId(1L);
      loginUser.setStatus(UserStatus.ONLINE);
      Long id = loginUser.getId();

      Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(loginUser));
      userService.logoutUser(id);

      User logoutUser = userService.getUserById(id);

      Mockito.verify(userRepository, Mockito.times(2)).save(Mockito.any());
      assertEquals(UserStatus.OFFLINE, logoutUser.getStatus());
    }

    @Test
    void cannotLogoutUser_userLoggedOut_throwException(){
        Mockito.when(userRepository.save(testUser)).thenReturn(testUser);
        User loginUser = userService.createUser(testUser);
        loginUser.setId(1L);
        loginUser.setStatus(UserStatus.OFFLINE);
        Long id = loginUser.getId();

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(loginUser));
        assertThrows(ResponseStatusException.class, () -> userService.logoutUser(id));
    }

    @Test
    void cannotFindUserInRepository_throwException(){
      Mockito.when(userRepository.findById(1L)).thenReturn(null);
        assertThrows(ResponseStatusException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUserProfile_success(){
        Mockito.when(userRepository.save(testUser)).thenReturn(testUser);
        User loginUser = userService.createUser(testUser);
        loginUser.setId(1L);
        Long id = loginUser.getId();

        User updateUser = new User();
        updateUser.setUsername("update");
        updateUser.setPassword("newPassword");
        updateUser.setBirthdate(LocalDate.of(1999, 1, 14));
        updateUser.setId(id);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(loginUser));
        Mockito.when(userRepository.findByUsername("update")).thenReturn(null);
        userService.updateUserProfile(updateUser);

        User user = userService.getUserById(id);
        Mockito.verify(userRepository,Mockito.times(2)).save(Mockito.any());
        assertEquals(updateUser.getUsername(), user.getUsername());
        assertTrue(passwordEncoder.matches(updateUser.getPassword(), user.getPassword()));
        assertEquals(updateUser.getBirthdate(), user.getBirthdate());
    }

    @Test
    void cannotUpdateProfile_userNameExist_throwException(){
        Mockito.when(userRepository.save(testUser)).thenReturn(testUser);
        User loginUser = userService.createUser(testUser);
        loginUser.setId(1L);
        Long id = loginUser.getId();

        User updateUser = new User();
        updateUser.setUsername("firstname@lastname");
        updateUser.setPassword("newPassword");
        updateUser.setBirthdate(LocalDate.of(1999, 1, 14));
        updateUser.setId(id);

        Mockito.when(userRepository.findById(id)).thenReturn(Optional.of(loginUser));
        Mockito.when(userRepository.findByUsername("firstname@lastname")).thenReturn(loginUser);
        assertThrows(ResponseStatusException.class, ()-> userService.updateUserProfile(updateUser));
    }

    @Test
    void getUserLeaderBoard_success(){
        Mockito.when(userRepository.save(testUser)).thenReturn(testUser);
        User loginUser = userService.createUser(testUser);
        List<User> users = new ArrayList<>();
        users.add(loginUser);

        Mockito.when(userRepository.findAll()).thenReturn(users);
        List<User> board = userService.getUserLeaderBoard();
        assertEquals(users, board);
    }

}
