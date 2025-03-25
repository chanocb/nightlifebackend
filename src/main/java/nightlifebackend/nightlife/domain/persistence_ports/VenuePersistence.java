package nightlifebackend.nightlife.domain.persistence_ports;

import nightlifebackend.nightlife.domain.models.User;
import nightlifebackend.nightlife.domain.models.Venue;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenuePersistence {

    Venue create(Venue venue);

    List<Venue> findAll();

    Venue findByReference(String reference);

    Venue update(String reference, Venue venue);
}
