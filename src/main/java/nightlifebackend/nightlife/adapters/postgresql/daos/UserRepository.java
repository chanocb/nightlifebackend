package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Integer> {
}
