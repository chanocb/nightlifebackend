package nightlifebackend.nightlife.domain.persistence_ports;

import nightlifebackend.nightlife.domain.models.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPersistence {

    User create(User user);

    User findByEmail(String email);
}
