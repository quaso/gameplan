package mq.gameplan.repository;

import mq.gameplan.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByRound(int round);
}
