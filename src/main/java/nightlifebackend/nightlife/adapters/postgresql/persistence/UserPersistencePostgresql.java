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

    @Override
    public User findByEmail(String email) {
        return this.userRepository
                .findByEmail(email)
                .map(UserEntity::toUser)
                .orElse(null);
    }

    @Override
    public User updateUser(String email, User user) {
        UserEntity existingUserEntity = this.userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + user.getEmail()));

        existingUserEntity.setFirstName(user.getFirstName());
        existingUserEntity.setLastName(user.getLastName());
        existingUserEntity.setPhone(user.getPhone());
        existingUserEntity.setBirthDate(user.getBirthDate());
        existingUserEntity.setRole(user.getRole());

        return this.userRepository
                .save(existingUserEntity)
                .toUser();
    }
}
