package nightlifebackend.nightlife.domain.services;

import nightlifebackend.nightlife.domain.models.User;
import nightlifebackend.nightlife.domain.persistence_ports.UserPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.stream.Stream;

@Service
public class UserService {

    private final UserPersistence userPersistence;
    private final JwtService jwtService;

    @Autowired
    public UserService(UserPersistence userPersistence, JwtService jwtService) {
        this.userPersistence = userPersistence;
        this.jwtService = jwtService;
    }

    public String create(User user) {
        if (Stream.of(user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword()).anyMatch(e -> e == null || e.isEmpty()) || user.getRole() == null) {
            throw new IllegalArgumentException("All fields are required");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        User user1 = this.userPersistence.create(user);
        return jwtService.createToken(user1.getEmail(), user1.getFirstName(), user1.getRole().name());

    }

    public String login(String email) {
        User user = this.userPersistence.findByEmail(email);
        return jwtService.createToken(user.getEmail(), user.getFirstName(), user.getRole().name());

    }
}
