package nightlifebackend.nightlife.adapters.postgresql.daos;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.domain.models.Role;
import nightlifebackend.nightlife.domain.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@Repository
//@Profile("dev")
public class DatabaseStarting {
    private static final String SUPER_USER = "admin";
    private static final String EMAIL = "example@example.com";
    private String PASSWORD;
    private static final String PHONE = "1234";
    private static final LocalDate BIRTHDAY = LocalDate.of(2000, 1, 1);

    private final UserRepository userRepository;

    @Autowired
    public DatabaseStarting(UserRepository userRepository, @Value("${nightlife.password}") String PASSWORD) {
        this.userRepository = userRepository;
        this.PASSWORD = PASSWORD;
        this.initialize();
    }

    //@PostConstruct
    void initialize() {
        if (this.userRepository.findByRoleIn(List.of(Role.ADMIN)).isEmpty()) {
            UserEntity adminUser = UserEntity.builder()
                    .email(EMAIL)
                    .password(new BCryptPasswordEncoder().encode(PASSWORD))
                    .firstName(SUPER_USER)
                    .lastName(SUPER_USER)
                    .phone(PHONE)
                    .birthDate(BIRTHDAY)
                    .role(Role.ADMIN)
                    .build();

            this.userRepository.save(adminUser);
            log.warn("------- Created Admin -----------");
        }
    }
}
