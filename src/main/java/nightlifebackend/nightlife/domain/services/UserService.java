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

    public User create(User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return this.userPersistence.create(user);
    }

    public String login(String email) {
        User user = this.userPersistence.findByEmail(email);
        return jwtService.createToken(user.getEmail(), user.getFirstName(), user.getRole().name());

    }

    public User readUserByEmail(String email) {
        return this.userPersistence.findByEmail(email);

    }

    public User updateUserByEmail(String email, User user) {
        return this.userPersistence.updateUser(email, user);

    }
}
