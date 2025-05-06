package nightlifebackend.nightlife.adapters.postgresql.persistence;

import nightlifebackend.nightlife.adapters.postgresql.daos.EventRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.UserRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.VenueRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.EventEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import nightlifebackend.nightlife.domain.models.Event;
import nightlifebackend.nightlife.domain.persistence_ports.EventPersistence;
import nightlifebackend.nightlife.domain.persistence_ports.ReviewPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("eventPersistence")
public class EventPersistencePostgresql implements EventPersistence {

    private final EventRepository eventRepository;

    private final VenueRepository venueRepository;

    @Autowired
    public EventPersistencePostgresql(VenueRepository venueRepository, EventRepository eventRepository) {
        this.venueRepository = venueRepository;
        this.eventRepository = eventRepository;
    }
    @Override
    public Event create(Event event) {
        if(event.getVenue() != null){
            // Assuming VenueEntity and VenueRepository are defined similarly to UserEntity and UserRepository
            EventEntity eventEntity = new EventEntity(event);
            VenueEntity venueEntity = this.venueRepository
                    .findByReference(event.getVenue().getReference())
                    .orElseThrow(() -> new RuntimeException("Venue not found with reference: " + event.getVenue().getReference()));
            eventEntity.setVenue(venueEntity);
            return this.eventRepository.save(eventEntity).toEvent();
        }
        throw new RuntimeException("Venue cannot be null");
    }

    @Override
    public List<Event> findByVenueReference(String reference) {
        return this.eventRepository
                .findByVenueReference(UUID.fromString(reference))
                .stream()
                .map(EventEntity::toEvent)
                .toList();
    }

    @Override
    public Event findByReference(String reference) {
        return this.eventRepository
                .findByReference(UUID.fromString(reference))
                .map(EventEntity::toEvent)
                .orElse(null);
    }

    @Override
    public void deleteByReference(String reference) {
        EventEntity eventEntity = this.eventRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("Event not found with reference: " + reference));
        this.eventRepository.delete(eventEntity);

    }

    @Override
    public List<Event> findByName(String name) {
        return this.eventRepository
                .findByName(name)
                .stream()
                .map(EventEntity::toEvent)
                .toList();
    }

    @Override
    public Event update(String reference, Event event) {
        EventEntity eventEntity = this.eventRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("Event not found with reference: " + reference));
        eventEntity.setName(event.getName());
        eventEntity.setDescription(event.getDescription());
        eventEntity.setDateTime(event.getDateTime());
        VenueEntity venueEntity = this.venueRepository
                .findByReference(event.getVenue().getReference())
                .orElseThrow(() -> new RuntimeException("Venue not found with reference: " + event.getVenue().getReference()));
        eventEntity.setVenue(venueEntity);
        return this.eventRepository.save(eventEntity).toEvent();
    }
}
