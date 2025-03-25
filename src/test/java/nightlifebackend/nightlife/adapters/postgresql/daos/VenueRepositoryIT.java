package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.TestConfig;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class VenueRepositoryIT {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    public void testFindByReference() {
        // Given
        VenueEntity venueEntity = new VenueEntity();
        venueEntity.setName("Test Venue");
        venueEntity.setPhone("123456789");
        venueEntity.setInstagram("test_venue");
        venueEntity.setLGTBFriendly(true);
        this.venueRepository.save(venueEntity);

        // When
        VenueEntity foundVenueEntity = this.venueRepository.findByReference(venueEntity.getReference()).get();

        // Then
        assertEquals(venueEntity.getReference(), foundVenueEntity.getReference());
        assertEquals(venueEntity.getName(), foundVenueEntity.getName());
        assertEquals(venueEntity.getPhone(), foundVenueEntity.getPhone());
        assertEquals(venueEntity.getInstagram(), foundVenueEntity.getInstagram());
        assertEquals(venueEntity.isLGTBFriendly(), foundVenueEntity.isLGTBFriendly());
    }
}
