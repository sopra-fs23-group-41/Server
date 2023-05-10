package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
//import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PlayerService {

    //private final Logger log = LoggerFactory.getLogger(GameRepository.class);

    private final PlayerRepository playerRepository;

    private final GameService gameService;


    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                         GameService gameService){
        this.playerRepository = playerRepository;
        this.gameService = gameService;
    }

    public List<Player> getPlayersByLobbyId(long gameId) {
        return playerRepository.findByGameId(gameId);
    }
}
