package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final GameService gameService;

    Logger logger = LoggerFactory.getLogger(Player.class);

    @Autowired
    public PlayerService(PlayerRepository playerRepository,
                         GameService gameService){
        this.playerRepository = playerRepository;
        this.gameService = gameService;
    }

}
