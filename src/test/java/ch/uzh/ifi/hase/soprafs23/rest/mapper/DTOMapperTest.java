package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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

      // MAP -> Create UserGetDTO
      UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

      // check content
      assertEquals(user.getId(), userGetDTO.getId());
      assertEquals(user.getPassword(), userGetDTO.getPassword());
      assertEquals(user.getUsername(), userGetDTO.getUsername());
      assertEquals(user.getStatus(), userGetDTO.getStatus());
      assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
      assertEquals(user.getBirthdate(), userGetDTO.getBirthdate());

  }
}
