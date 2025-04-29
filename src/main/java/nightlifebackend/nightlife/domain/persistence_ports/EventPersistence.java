package nightlifebackend.nightlife.domain.persistence_ports;

import nightlifebackend.nightlife.domain.models.Event;
import nightlifebackend.nightlife.domain.models.Review;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventPersistence {

    Event create(Event event);

    List<Event> findByVenueReference(String reference);

    Event findByReference(String reference);

    void deleteByReference(String reference);

    List<Event> findByName(String name);
}
