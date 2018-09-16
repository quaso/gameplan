package mq.gameplan.repository;

import mq.gameplan.model.Pair;
import mq.gameplan.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PairRepository extends JpaRepository<Pair, Long> {

    boolean existsByPlayer1AndPlayer2(Player player1, Player player2);

}
