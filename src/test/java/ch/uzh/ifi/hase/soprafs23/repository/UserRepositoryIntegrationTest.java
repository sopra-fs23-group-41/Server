package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByUserName_success() {
      // given
      User user = new User();
      user.setUsername("firstname@lastname");
      user.setPassword("password");
      user.setStatus(UserStatus.OFFLINE);
      user.setBirthdate(LocalDate.of(2002,4,24));
      user.setCreationDate(LocalDate.of(2002, 4 ,23));
      user.setToken("1");
      user.setNumOfGameWon(2);

      entityManager.persist(user);
      entityManager.flush();

      // when
      User found = userRepository.findByUsername(user.getUsername());

      // then
      assertNotNull(found.getId());
      assertEquals(found.getPassword(), user.getPassword());
      assertEquals(found.getUsername(), user.getUsername());
      assertEquals(found.getToken(), user.getToken());
      assertEquals(found.getStatus(), user.getStatus());
      assertEquals(found.getBirthdate(), user.getBirthdate());
      assertEquals(found.getCreationDate(), user.getCreationDate());
      assertEquals(found.getNumOfGameWon(), user.getNumOfGameWon());
  }
}
