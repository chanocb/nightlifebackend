package nightlifebackend.nightlife.adapters.postgresql.persistence;

import nightlifebackend.nightlife.adapters.postgresql.daos.UserRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.domain.models.User;
import nightlifebackend.nightlife.domain.persistence_ports.UserPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("userPersistence")
public class UserPersistencePostgresql implements UserPersistence {

    private final UserRepository userRepository;

    @Autowired
    public UserPersistencePostgresql(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        return this.userRepository
                .save(new UserEntity(user))
                .toUser();
    }
}
