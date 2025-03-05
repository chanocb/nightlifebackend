package nightlifebackend.nightlife;

import nightlifebackend.nightlife.adapters.postgresql.daos.DatabaseStarting;
import nightlifebackend.nightlife.adapters.postgresql.daos.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NightlifeApplication {

	public static void main(String[] args) {
		SpringApplication.run(NightlifeApplication.class, args);
	}

}
