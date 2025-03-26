package nightlifebackend.nightlife.adapters.postgresql.daos;

import lombok.extern.log4j.Log4j2;
import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import nightlifebackend.nightlife.domain.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;

@Log4j2
@Repository
public class UserSeederDev {
    private final DatabaseStarting databaseStarting;
    private final UserRepository userRepository;

    private final VenueRepository venueRepository;
    private String PASSWORD;

    @Autowired
    public UserSeederDev(UserRepository userRepository, DatabaseStarting databaseStarting, VenueRepository venueRepository, @Value("${nightlife.password}") String PASSWORD) {
        this.userRepository = userRepository;
        this.databaseStarting = databaseStarting;
        this.venueRepository = venueRepository;
        this.PASSWORD = PASSWORD;
        this.deleteAllAndInitializeAndSeedDataBase();
    }

    public void deleteAllAndInitializeAndSeedDataBase() {
        this.deleteAllAndInitialize();
        this.seedDataBase();
    }

    public void deleteAllAndInitialize() {
        this.venueRepository.deleteAll();
        this.userRepository.deleteAll();
        log.warn("------- Deleted All -----------");
        this.databaseStarting.initialize();
    }

    private void seedDataBase() {
        log.warn("------- Initial Load from JAVA -----------");

        String pass = new BCryptPasswordEncoder().encode(PASSWORD);
        //String pass = PASSWORD;

        UserEntity[] users = {
                UserEntity.builder()
                        .email("newuser1@example.com")
                        .password(pass)
                        .firstName("NewUser1")
                        .lastName("LastName1")
                        .phone("1111111111")
                        .birthDate(LocalDate.of(1995, 2, 15))
                        .role(Role.ADMIN)
                        .build(),
                UserEntity.builder()
                        .email("newuser2@example.com")
                        .password(pass)
                        .firstName("NewUser2")
                        .lastName("LastName2")
                        .phone("2222222222")
                        .birthDate(LocalDate.of(1998, 5, 20))
                        .role(Role.ADMIN)
                        .build(),
                UserEntity.builder()
                        .email("newuser3@example.com")
                        .password(pass)
                        .firstName("NewUser3")
                        .lastName("LastName3")
                        .phone("3333333333")
                        .birthDate(LocalDate.of(2000, 7, 10))
                        .role(Role.OWNER)
                        .build(),
                UserEntity.builder()
                        .email("newuser4@example.com")
                        .password(pass)
                        .firstName("NewUser4")
                        .lastName("LastName4")
                        .phone("4444444444")
                        .birthDate(LocalDate.of(1992, 3, 5))
                        .role(Role.OWNER)
                        .build(),
                UserEntity.builder()
                        .email("newuser5@example.com")
                        .password(pass)
                        .firstName("NewUser5")
                        .lastName("LastName5")
                        .phone("5555555555")
                        .birthDate(LocalDate.of(1997, 9, 25))
                        .role(Role.CLIENT)
                        .build(),
                UserEntity.builder()
                        .email("newuser6@example.com")
                        .password(pass)
                        .firstName("NewUser6")
                        .lastName("LastName6")
                        .phone("6666666666")
                        .birthDate(LocalDate.of(1993, 12, 30))
                        .role(Role.CLIENT)
                        .build()
        };
        this.userRepository.saveAll(Arrays.asList(users));

        VenueEntity venue1 = VenueEntity.builder()
                .name("Club 1")
                .phone("1234567890")
                .LGTBFriendly(true)
                .instagram("club1_insta")
                .owner(users[2])
                .build();

        VenueEntity venue2 = VenueEntity.builder()
                .name("Club 2")
                .phone("0987654321")
                .LGTBFriendly(false)
                .instagram("club2_insta")
                .owner(users[3])
                .build();

        this.venueRepository.saveAll(Arrays.asList(venue1, venue2));

        //this.userRepository.saveAll(Arrays.asList(users));
        log.warn("        ------- users seeded");
    }
}
