package mq.gameplan.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "match")
@Data
@ToString(exclude = "id")
public class Match {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "match_id", unique = true, nullable = false)
    private long id;

    @OneToOne
    private Player player1;

    @OneToOne
    private Player player2;

    @OneToOne
    private Player player3;

    @OneToOne
    private Player player4;

    @Column
    private int round;

    public Match(Player player1, Player player2, Player player3, Player player4, int round) {
        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.player4 = player4;
        this.round = round;
    }

    public Match() {
    }
}
