package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.ProfilePicture;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final PlayerRepository playerRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository,
                     @Qualifier("playerRepository") PlayerRepository playerRepository) {
    this.userRepository = userRepository;
    this.playerRepository = playerRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(LocalDate.now());
    newUser.setProfilePicture(ProfilePicture.ONE); // needs to be randomized
    checkIfUserExists(newUser);

    // hash the password using BCrypt
      String encodedPassword = passwordEncoder.encode(newUser.getPassword());
      newUser.setPassword(encodedPassword);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  //add user to game with game pin
  public Player addUserToLobby(long userId, long lobbyId){
      //check if user exists
      User userToConvert = getUserById(userId);
      if (userToConvert == null){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not exist!");
      }

      //check if user is already in player repository
      Player player = playerRepository.findByUserId(userId);
      if(player != null){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with Id:" + userId + "is already in a lobby!");
      }

      //add user to game with gamePin
      Player convertedUser = new Player();

      //set player
      convertedUser.setPlayerName(userToConvert.getUsername());
      convertedUser.setUserId(userId);
      convertedUser.setGameId(lobbyId);
      convertedUser.setProfilePicture(userToConvert.getProfilePicture());

      Player addedPlayer = playerRepository.save(convertedUser);
      playerRepository.flush();

      return addedPlayer;
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBeCreated
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserExists(User userToBeCreated) {
    User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format(baseErrorMessage, "username and the name", "are"));
    }
  }

    public User loginUser(String loginUsername, String password) {
        User userToLogin = userRepository.findByUsername(loginUsername);
        if (userToLogin == null || !passwordEncoder.matches(password, userToLogin.getPassword()))  {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid login credentials, make sure that username and password are correct.");
        }
        if (userToLogin.getStatus() == UserStatus.ONLINE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already logged in.");
        }

        userToLogin.setStatus(UserStatus.ONLINE);
        userRepository.save(userToLogin);
        userRepository.flush();
        return userToLogin;
    }

    public void logoutUser(long id) {
      User user = getUserById(id);
      if (user == null || user.getStatus() == UserStatus.OFFLINE){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found!");
      }

      user.setStatus(UserStatus.OFFLINE);
      userRepository.save(user);
      userRepository.flush();
    }

    public User getUserById(Long id) {
      Optional<User> user = userRepository.findById(id);
      if (user.isEmpty()){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                  String.format("Could not find user with id %d.", id));
      }
      return user.get();
    }

    public User updateUserProfile(long userId, User userWithUpdates) {
        User user = getUserById(userId);
        String message = "This user name is taken. Please use another one";

        if (userRepository.findByUsername(userWithUpdates.getUsername()) != null && (userRepository.findByUsername(userWithUpdates.getUsername()).getId() != userId)){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(message));
        }
        user.setUsername(userWithUpdates.getUsername());
        String encodedPassword = passwordEncoder.encode(userWithUpdates.getPassword());
        user.setPassword(encodedPassword);
        user.setBirthdate(userWithUpdates.getBirthdate());
        user.setProfilePicture(userWithUpdates.getProfilePicture());

        userRepository.save(user);
        userRepository.flush();

        return user;
    }

    public List<User> getUserLeaderBoard(){
      List<User> users = userRepository.findAll();
      users.sort((u1, u2) -> u2.getNumOfGameWon() - u1.getNumOfGameWon());
      return users;
    }
}
