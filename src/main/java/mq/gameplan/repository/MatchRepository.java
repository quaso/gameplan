package mq.gameplan.repository;

import javax.transaction.Transactional;
import mq.gameplan.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Transactional
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByRound(int round);

    void deleteByRound(int round);
}
