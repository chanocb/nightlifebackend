package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.TestConfig;
import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import nightlifebackend.nightlife.domain.models.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class VenueRepositoryIT {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByReference() {

        UserEntity owner = new UserEntity();
        owner.setEmail("test@example.com");
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setPhone("123456789");
        owner.setPassword("password");
        owner.setRole(Role.OWNER);
        this.userRepository.save(owner);

        VenueEntity venueEntity = new VenueEntity();
        venueEntity.setName("Test Venue");
        venueEntity.setPhone("123456789");
        venueEntity.setInstagram("test_venue");
        venueEntity.setLGTBFriendly(true);
        venueEntity.setOwner(owner);

        VenueEntity savedVenueEntity = this.venueRepository.save(venueEntity);

        VenueEntity foundVenueEntity = this.venueRepository.findByReference(savedVenueEntity.getReference())
                .orElseThrow(() -> new RuntimeException("Venue no encontrado"));

        assertEquals(savedVenueEntity.getReference(), foundVenueEntity.getReference());
        assertEquals(savedVenueEntity.getName(), foundVenueEntity.getName());
        assertEquals(savedVenueEntity.getPhone(), foundVenueEntity.getPhone());
        assertEquals(savedVenueEntity.getInstagram(), foundVenueEntity.getInstagram());
        assertEquals(savedVenueEntity.isLGTBFriendly(), foundVenueEntity.isLGTBFriendly());
        assertEquals(savedVenueEntity.getOwner().getEmail(), foundVenueEntity.getOwner().getEmail());
    }
}
