package mq.gameplan.repository;

import javax.transaction.Transactional;
import mq.gameplan.model.Pair;
import mq.gameplan.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

@Transactional
public interface PairRepository extends JpaRepository<Pair, Long> {

    boolean existsByPlayer1AndPlayer2(Player player1, Player player2);

    void deleteByRound(int round);

}
