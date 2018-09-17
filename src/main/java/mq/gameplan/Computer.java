package mq.gameplan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import mq.gameplan.model.Match;
import mq.gameplan.model.Pair;
import mq.gameplan.model.Player;
import mq.gameplan.repository.MatchRepository;
import mq.gameplan.repository.PairRepository;
import mq.gameplan.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
            try {
                createRound(i);
                System.out.println((i + 1) + ". kolo");
                matchRepository.findByRound(i).forEach(r -> System.out.println(
                        String.format("%s\t\t\t%s\t\t\t: %s\t\t\t%s", r.getPlayer1().getId(), r.getPlayer2().getId(),
                                r.getPlayer3().getId(), r.getPlayer4().getId())));
            } catch (RuntimeException ex) {
                matchRepository.deleteByRound(i);
                pairRepository.deleteByRound(i);
                System.out.println("next try ...");
                i--;
            }
        }

        Map<Player, List<Pair>> playerListMap = getAllPairs();
        playerListMap.entrySet().stream().forEach(e -> System.out
                .println(String.format("%s: %s", e.getKey(), String.join(",", getOtherPlayers(e.getValue(), e.getKey())))));

    }

    private List<String> getOtherPlayers(List<Pair> pairs, Player player) {
        List<Player> result = new ArrayList<>();
        pairs.forEach(p -> result.add((p.getPlayer1().equals(player) ? p.getPlayer2() : p.getPlayer1())));
        return result.stream().map(Player::getId).sorted().map(String::valueOf).collect(Collectors.toList());
    }

    private Map<Player, List<Pair>> getAllPairs() {
        Map<Player, List<Pair>> result = new HashMap<>();
        pairRepository.findAll().forEach(pair -> {
            result.computeIfAbsent(pair.getPlayer1(), k -> new ArrayList<Pair>()).add(pair);
            result.computeIfAbsent(pair.getPlayer2(), k -> new ArrayList<Pair>()).add(pair);
        });
        return result;
    }

    private void createPlayers() {
        for (int i = 0; i < playersCount; i++) {
            playerRepository.save(new Player(i + 1));
        }

        playerList = playerRepository.findAll();
    }

    private void createRound(int roundNo) {
        List<Player> remainingPlayers = new ArrayList<>(playerList);
        Collections.shuffle(remainingPlayers);
        List<Player> playersInMatch = new ArrayList<>();

        for (int i = 0; i < playersCount / 4; i++) {
            playersInMatch.clear();

            for (int p = 0; p < 4; p++) {
                Player nextPlayer = findNextPlayer(remainingPlayers, playersInMatch);
                playersInMatch.add(nextPlayer);
                remainingPlayers.remove(nextPlayer);
            }
            matchRepository
                    .save(new Match(playersInMatch.get(0), playersInMatch.get(1), playersInMatch.get(2), playersInMatch.get(3),
                            roundNo));

            pairRepository.save(new Pair(roundNo, playersInMatch.get(0), playersInMatch.get(1)));
            pairRepository.save(new Pair(roundNo, playersInMatch.get(0), playersInMatch.get(2)));
            pairRepository.save(new Pair(roundNo, playersInMatch.get(0), playersInMatch.get(3)));

            pairRepository.save(new Pair(roundNo, playersInMatch.get(1), playersInMatch.get(2)));
            pairRepository.save(new Pair(roundNo, playersInMatch.get(1), playersInMatch.get(3)));

            pairRepository.save(new Pair(roundNo, playersInMatch.get(2), playersInMatch.get(3)));
        }
    }

    private Player findNextPlayer(List<Player> players, List<Player> playersInMatch) {
        List<Player> tempList = new ArrayList<>(players);

        while (tempList.size() > 0) {
            Player player = tempList.get((int) (Math.random() * tempList.size()));
            if (!playersInMatch.contains(player) && !checkIfPlayedAlready(playersInMatch, player)) {
                return player;
            }
            tempList.remove(player);
        }

        if (tempList.size() == 0) {
            throw new RuntimeException("Cannot find player for a match");
        }
        throw new IllegalStateException("Shall not reach this line");
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
