package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(GameRepository.class);

    private final PlayerRepository playerRepository;

    private final GameService gameService;

    Logger logger = LoggerFactory.getLogger(Player.class);

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                         GameService gameService){
        this.playerRepository = playerRepository;
        this.gameService = gameService;
    }
}
