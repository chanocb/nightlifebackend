package nightlifebackend.nightlife.adapters.postgresql.daos;


import nightlifebackend.nightlife.TestConfig;
import nightlifebackend.nightlife.domain.models.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
public class UserRepositoryIT {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByRoleIn() {
        List<Role> roles = List.of(Role.ADMIN, Role.OWNER, Role.CLIENT);
        assertTrue(this.userRepository.findByRoleIn(roles).stream().allMatch(user -> roles.contains(user.getRole())));
    }
}
