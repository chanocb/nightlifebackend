package nightlifebackend.nightlife.domain.persistence_ports;

import nightlifebackend.nightlife.domain.models.Venue;
import org.springframework.stereotype.Repository;

@Repository
public interface VenuePersistence {

    Venue create(Venue venue);
}
