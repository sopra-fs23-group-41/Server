package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepo;
//import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    newUser.setStatus(UserStatus.OFFLINE);
    newUser.setCreationDate(new Date());
    checkIfUserExists(newUser);

    // hash the password using BCrypt
    String hashedPassword = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt(12));
    newUser.setPassword(hashedPassword);

    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
  }

  //add user to game with gamepin
  public Player addUserToLobby(long userId, String gamePin){
      //check if game to gamepin exists
      Game game = GameRepo.findByGamePin(gamePin); //should throw ResponseStatusException Lobby not found

      //check if user exists
      User userToConvert = userRepository.findById(userId);

      //check if user is already in player repository
      Player player = playerRepository.findByUserId(userId);
      if(player != null){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with Id:" + userId + "is already in a lobby!");
      }

      //check if user is already in the game
      if(game.checkIfUserIsAPlayer(userId)){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with Id:" + userId + "is already in a lobby!");
      }

      //add user to game with gamePin
      Player convertedUser = new Player();

      //set player
      convertedUser.setPlayerName(userToConvert.getUsername());
      convertedUser.setUserId(userId);
      convertedUser.setGameId(game.getGameId());

      Player addedPlayer = playerRepository.save(convertedUser);
      playerRepository.flush();

      //update add Player to game instance
      game.addPlayer(convertedUser);

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
    } else if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    }
  }

    public User loginUser(String loginUsername, String password) {
        User userToLogin = userRepository.findByUsername(loginUsername);
        if (userToLogin == null || !BCrypt.checkpw(password, userToLogin.getPassword()))  {
            throw new RuntimeException("Invalid login credentials, make sure that username and password are correct.");
        }
        if (userToLogin.getStatus() == UserStatus.ONLINE) {
            throw new RuntimeException("User is already logged in.");
        }

        userToLogin.setStatus(UserStatus.ONLINE);
        userRepository.save(userToLogin);
        userRepository.flush();
        return userToLogin;
    }

    public void logoutUser(long id) {
      User user = getUserById(id);
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

    public void updateUserProfile(User currentUser) {
      Optional<User> userOp = userRepository.findById(currentUser.getId());
        if (userOp.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Could not find user with id %d.", currentUser.getId()));
        }
        String message = "This user name is taken. Please use another one";

        User user = userOp.get();
        if (userRepository.findByUsername(currentUser.getUsername()) != null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(message));
        }
        user.setUsername(currentUser.getUsername());
        user.setPassword(currentUser.getPassword());
        user.setBirthdate(currentUser.getBirthdate());
    }
}
