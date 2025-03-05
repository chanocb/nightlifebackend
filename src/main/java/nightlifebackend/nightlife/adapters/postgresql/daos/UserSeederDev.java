package nightlifebackend.nightlife.adapters.postgresql.daos;

import lombok.extern.log4j.Log4j2;
import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.domain.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;

@Log4j2
@Repository
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

        String passwordTest = System.getenv("PASSWORD_TEST");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserEntity[] users = {
                UserEntity.builder()
                        .email("newuser1@example.com")
                        .password(passwordEncoder.encode(passwordTest))
                        .firstName("NewUser1")
                        .lastName("LastName1")
                        .phone("1111111111")
                        .birthDate(LocalDateTime.of(1995, 2, 15, 0, 0))
                        .role(Role.ADMIN)
                        .build(),
                UserEntity.builder()
                        .email("newuser2@example.com")
                        .password(passwordEncoder.encode(passwordTest))
                        .firstName("NewUser2")
                        .lastName("LastName2")
                        .phone("2222222222")
                        .birthDate(LocalDateTime.of(1998, 5, 20, 0, 0))
                        .role(Role.ADMIN)
                        .build(),
                UserEntity.builder()
                        .email("newuser3@example.com")
                        .password(passwordEncoder.encode(passwordTest))
                        .firstName("NewUser3")
                        .lastName("LastName3")
                        .phone("3333333333")
                        .birthDate(LocalDateTime.of(2000, 7, 10, 0, 0))
                        .role(Role.OWNER)
                        .build(),
                UserEntity.builder()
                        .email("newuser4@example.com")
                        .password(passwordEncoder.encode(passwordTest))
                        .firstName("NewUser4")
                        .lastName("LastName4")
                        .phone("4444444444")
                        .birthDate(LocalDateTime.of(1992, 3, 5, 0, 0))
                        .role(Role.OWNER)
                        .build(),
                UserEntity.builder()
                        .email("newuser5@example.com")
                        .password(passwordEncoder.encode(passwordTest))
                        .firstName("NewUser5")
                        .lastName("LastName5")
                        .phone("5555555555")
                        .birthDate(LocalDateTime.of(1997, 9, 25, 0, 0))
                        .role(Role.CLIENT)
                        .build(),
                UserEntity.builder()
                        .email("newuser6@example.com")
                        .password(passwordEncoder.encode(passwordTest))
                        .firstName("NewUser6")
                        .lastName("LastName6")
                        .phone("6666666666")
                        .birthDate(LocalDateTime.of(1993, 12, 30, 0, 0))
                        .role(Role.CLIENT)
                        .build()
        };

        this.userRepository.saveAll(Arrays.asList(users));
        log.warn("        ------- users seeded");
    }
}
