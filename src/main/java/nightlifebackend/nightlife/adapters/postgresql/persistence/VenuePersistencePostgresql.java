package nightlifebackend.nightlife.adapters.postgresql.persistence;


import nightlifebackend.nightlife.adapters.postgresql.daos.VenueRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import nightlifebackend.nightlife.domain.models.Venue;
import nightlifebackend.nightlife.domain.persistence_ports.VenuePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
}
