package nightlifebackend.nightlife.adapters.postgresql.persistence;


import nightlifebackend.nightlife.adapters.postgresql.daos.VenueRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import nightlifebackend.nightlife.domain.models.Venue;
import nightlifebackend.nightlife.domain.persistence_ports.VenuePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("venuePersistence")
public class VenuePersistencePostgresql implements VenuePersistence {

    private final VenueRepository venueRepository;

    @Autowired
    public VenuePersistencePostgresql(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Override
    public Venue create(Venue venue) {
        return this.venueRepository
                .save(new VenueEntity(venue))
                .toVenue();
    }

    @Override
    public List<Venue> findAll() {
        return this.venueRepository
                .findAll()
                .stream()
                .map(VenueEntity::toVenue)
                .toList();
    }

    @Override
    public Venue findByReference(String reference) {
        return this.venueRepository
                .findByReference(UUID.fromString(reference))
                .map(VenueEntity::toVenue)
                .orElse(null);
    }

    @Override
    public Venue update(String reference, Venue venue) {
        VenueEntity existingVenueEntity = this.venueRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("Locla no encontrado con referencia: " + venue.getReference()));

        existingVenueEntity.setName(venue.getName());
        existingVenueEntity.setPhone(venue.getPhone());
        existingVenueEntity.setInstagram(venue.getInstagram());
        existingVenueEntity.setLGTBFriendly(venue.isLGTBFriendly());

        return this.venueRepository
                .save(existingVenueEntity)
                .toVenue();
    }
}
