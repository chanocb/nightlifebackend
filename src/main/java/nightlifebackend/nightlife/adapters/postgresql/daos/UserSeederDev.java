package nightlifebackend.nightlife.adapters.postgresql.daos;

import lombok.extern.log4j.Log4j2;
import nightlifebackend.nightlife.domain.models.Role;
import nightlifebackend.nightlife.domain.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;

@Log4j2
@Repository // @Profile("dev")
public class UserSeederDev {
    private final DatabaseStarting databaseStarting;
    private final UserRepository userRepository;

    @Autowired
    public UserSeederDev(UserRepository userRepository, DatabaseStarting databaseStarting) {
        this.userRepository = userRepository;
        this.databaseStarting = databaseStarting;
        this.deleteAllAndInitializeAndSeedDataBase();
    }
    public void deleteAllAndInitializeAndSeedDataBase() {
        this.deleteAllAndInitialize();
        this.seedDataBase();
    }
    public void deleteAllAndInitialize() {
        this.userRepository.deleteAll();
        log.warn("------- Deleted All -----------");
        this.databaseStarting.initialize();
    }
    private void seedDataBase() {
        log.warn("------- Initial Load from JAVA -----------");
        User[] users = {
                new User("newuser1@example.com", new BCryptPasswordEncoder().encode("pass1"), "NewUser1", "LastName1", "1111111111", LocalDateTime.of(1995, 2, 15, 0, 0), Role.ADMIN),
                new User("newuser2@example.com", new BCryptPasswordEncoder().encode("pass2"), "NewUser2", "LastName2", "2222222222", LocalDateTime.of(1998, 5, 20, 0, 0), Role.ADMIN),
                new User("newuser3@example.com", new BCryptPasswordEncoder().encode("pass3"), "NewUser3", "LastName3", "3333333333", LocalDateTime.of(2000, 7, 10, 0, 0), Role.OWNER),
                new User("newuser4@example.com", new BCryptPasswordEncoder().encode("pass4"), "NewUser4", "LastName4", "4444444444", LocalDateTime.of(1992, 3, 5, 0, 0), Role.OWNER),
                new User("newuser5@example.com", new BCryptPasswordEncoder().encode("pass5"), "NewUser5", "LastName5", "5555555555", LocalDateTime.of(1997, 9, 25, 0, 0), Role.CLIENT),
                new User("newuser6@example.com", new BCryptPasswordEncoder().encode("pass6"), "NewUser6", "LastName6", "6666666666", LocalDateTime.of(1993, 12, 30, 0, 0), Role.CLIENT)
        };
        this.userRepository.saveAll(Arrays.asList(users));
        log.warn("        ------- users");
    }
}
