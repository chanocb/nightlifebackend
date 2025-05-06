package nightlifebackend.nightlife.domain.services;

import nightlifebackend.nightlife.domain.models.Event;
import nightlifebackend.nightlife.domain.persistence_ports.EventPersistence;
import nightlifebackend.nightlife.domain.persistence_ports.VenuePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventPersistence eventPersistence;
    private final JwtService jwtService;

    @Autowired
    public EventService(EventPersistence eventPersistence, JwtService jwtService) {
        this.eventPersistence = eventPersistence;
        this.jwtService = jwtService;
    }

    public Event create(Event event) {
        return this.eventPersistence.create(event);
    }

    public Event findByReference(String reference) {
        return this.eventPersistence.findByReference(reference);
    }
    public List<Event> findByVenueReference(String reference) {
        return this.eventPersistence.findByVenueReference(reference);
    }
    public List<Event> findByName(String name) {
        return this.eventPersistence.findByName(name);
    }
    public void delete(String reference) {
        eventPersistence.deleteByReference(reference);
    }

    public Event update(String reference, Event event) {
        return this.eventPersistence.update(reference, event);
    }
}
