package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  public void createUser_validInputs_success() {
      // given
      assertNull(userRepository.findByUsername("testUsername"));

      // given
      User testUser = new User();
      testUser.setUsername("firstname@lastname");
      testUser.setPassword("password");
      testUser.setStatus(UserStatus.OFFLINE);
      testUser.setBirthdate(LocalDate.of(2002,4,24));
      testUser.setCreationDate(LocalDate.of(2002, 4 ,23));

      // when
      User createdUser = userService.createUser(testUser);

      // then
      assertEquals(testUser.getId(), createdUser.getId());
      assertEquals(testUser.getPassword(), createdUser.getPassword());
      assertEquals(testUser.getUsername(), createdUser.getUsername());
      assertNotNull(createdUser.getToken());
      assertEquals(UserStatus.ONLINE, createdUser.getStatus());
      assertEquals(testUser.getCreationDate(), createdUser.getCreationDate());
      assertEquals(testUser.getBirthdate(), createdUser.getBirthdate());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
      assertNull(userRepository.findByUsername("testUsername"));

      // given
      User testUser = new User();
      testUser.setUsername("testUsername");
      testUser.setPassword("password");
      testUser.setStatus(UserStatus.OFFLINE);
      testUser.setBirthdate(LocalDate.of(2002,4,24));
      testUser.setCreationDate(LocalDate.of(2002, 4 ,23));
      User createdUser = userService.createUser(testUser);

      // attempt to create second user with same username
      User testUser2 = new User();

      // create new User with same userName
      testUser2.setPassword("testName2");
      testUser2.setUsername("testUsername");

      // check that an error is thrown
      assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }
}
