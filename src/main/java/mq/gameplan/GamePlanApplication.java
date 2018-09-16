package mq.gameplan;

import mq.gameplan.model.Player;
import mq.gameplan.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.List;

@SpringBootApplication
public class GamePlanApplication {

    public static void main(String[] args) {
        SpringApplication.run(GamePlanApplication.class, args);
    }
}
