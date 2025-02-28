package nightlifebackend.nightlife.adapters.postgresql.daos;

import lombok.extern.log4j.Log4j2;
import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.domain.models.Role;
import nightlifebackend.nightlife.domain.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Repository
public class DatabaseStarting {
    private static final String SUPER_USER = "admin";
    private static final String EMAIL = "example@example.com";
    private static final String PASSWORD = "1234";
    private static final String PHONE = "1234";
    private static final LocalDateTime BIRTHDAY = LocalDateTime.of(2000, 1, 1, 0, 0);
    private final UserRepository userRepository;

    @Autowired
    public DatabaseStarting(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.initialize();
    }

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
