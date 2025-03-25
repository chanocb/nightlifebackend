package nightlifebackend.nightlife.domain.services;


import nightlifebackend.nightlife.domain.models.Venue;
import nightlifebackend.nightlife.domain.persistence_ports.VenuePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    private final VenuePersistence venuePersistence;

    @Autowired
    public VenueService(VenuePersistence venuePersistence) {
        this.venuePersistence = venuePersistence;
    }

    public Venue create(Venue venue) {
        return this.venuePersistence.create(venue);
    }

    public List<Venue> findAll() {
        return this.venuePersistence.findAll();
    }

    public Venue findByReference(String reference) {
        return this.venuePersistence.findByReference(reference);
    }

    public Venue update(String reference, Venue venue) {
        return this.venuePersistence.update(reference, venue);
    }
}
