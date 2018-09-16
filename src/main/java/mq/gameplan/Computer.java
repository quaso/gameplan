package mq.gameplan;

import mq.gameplan.model.Match;
import mq.gameplan.model.Pair;
import mq.gameplan.model.Player;
import mq.gameplan.repository.MatchRepository;
import mq.gameplan.repository.PairRepository;
import mq.gameplan.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class Computer {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PairRepository pairRepository;

    @Value("${playersCount}")
    private int playersCount;

    @Value("${rounds}")
    private int rounds;

    private List<Player> playerList;

    @PostConstruct
    public void init() {
        pairRepository.deleteAll();
        matchRepository.deleteAll();
        playerRepository.deleteAll();

        createPlayers();

        for (int i = 0; i < rounds; i++) {
            createRound(i);
            matchRepository.findByRound(i).forEach(r -> System.out.println(r));

        }

    }

    private void createPlayers() {

        for (int i = 0; i < playersCount; i++) {
            playerRepository.save(new Player(i + 1));
        }

        playerList = playerRepository.findAll();
    }

    private void createRound(int roundNo) {
        List<Player> remainingPlayers = new ArrayList<>(playerList);
        for (int i = 0; i < playersCount / 4; i++) {
            List<Player> playersInMatch = new ArrayList<>();
            for (int p = 0; p < 4; p++) {
                Player nextPlayer = findNextPlayer(remainingPlayers, playersInMatch);
                if (nextPlayer == null) {
                    throw new RuntimeException("Cannot find player for a match round " + roundNo);
                }
                playersInMatch.add(nextPlayer);
                remainingPlayers.remove(nextPlayer);
            }
            matchRepository.save(new Match(playersInMatch.get(0), playersInMatch.get(1), playersInMatch.get(2), playersInMatch.get(3), roundNo));

            pairRepository.save(new Pair(playersInMatch.get(0), playersInMatch.get(1)));
            pairRepository.save(new Pair(playersInMatch.get(0), playersInMatch.get(2)));
            pairRepository.save(new Pair(playersInMatch.get(0), playersInMatch.get(3)));

            pairRepository.save(new Pair(playersInMatch.get(1), playersInMatch.get(2)));
            pairRepository.save(new Pair(playersInMatch.get(1), playersInMatch.get(3)));

            pairRepository.save(new Pair(playersInMatch.get(2), playersInMatch.get(3)));
        }
    }

    private Player findNextPlayer(List<Player> remainingPlayers, List<Player> playersInMatch) {
        for (Player player : remainingPlayers) {
            if (!playersInMatch.contains(player) && !checkIfPlayedAlready(playersInMatch, player)) {
                return player;
            }
        }
        return null;
    }

    private boolean checkIfPlayedAlready(List<Player> playersInMatch, Player player) {
        if (playersInMatch.size() == 0) {
            return false;
        }

        for (Player p : playersInMatch) {
            if (pairRepository.existsByPlayer1AndPlayer2(p, player) || pairRepository.existsByPlayer1AndPlayer2(player, p)) {
                return true;
            }
        }

        return false;
    }
}
