package mq.gameplan.model;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "pair")
@Data
public class Pair {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "pair_id", unique = true, nullable = false)
    private long id;

    @OneToOne
    private Player player1;

    @OneToOne
    private Player player2;

    public Pair(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public Pair() {
    }
}
