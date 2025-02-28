package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.domain.models.Role;
import nightlifebackend.nightlife.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    List<UserEntity> findByRoleIn(Collection<Role> roles);
}
