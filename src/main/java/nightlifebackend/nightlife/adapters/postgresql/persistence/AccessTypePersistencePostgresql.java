package nightlifebackend.nightlife.adapters.postgresql.persistence;

import nightlifebackend.nightlife.adapters.postgresql.daos.AccessTypeRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.EventRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.AccessTypeEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.EventEntity;
import nightlifebackend.nightlife.domain.models.AccessType;
import nightlifebackend.nightlife.domain.persistence_ports.AccessTypePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("accessTypePersistence")
public class AccessTypePersistencePostgresql implements AccessTypePersistence {

    private final AccessTypeRepository accessTypeRepository;
    private final EventRepository eventRepository;

    @Autowired
    public AccessTypePersistencePostgresql(AccessTypeRepository accessTypeRepository, EventRepository eventRepository) {
        this.accessTypeRepository = accessTypeRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public AccessType create(AccessType accessType) {
        // Assuming AccessTypeEntity and AccessTypeRepository are defined similarly to UserEntity and UserRepository
         AccessTypeEntity accessTypeEntity = new AccessTypeEntity(accessType);
         EventEntity eventEntity = this.eventRepository
                 .findByReference(accessType.getEvent().getReference())
                 .orElseThrow(() -> new RuntimeException("Event not found with reference: " + accessType.getEvent().getReference()));
         accessTypeEntity.setEvent(eventEntity);
         return this.accessTypeRepository.save(accessTypeEntity).toAccessType();

    }

    @Override
    public void deleteByReference(String reference) {
        AccessTypeEntity accessTypeEntity = this.accessTypeRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("AccessType not found with reference: " + reference));
        this.accessTypeRepository.delete(accessTypeEntity);
    }

    @Override
    public AccessType update(String reference, AccessType accessType) {
        AccessTypeEntity accessTypeEntity = this.accessTypeRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("AccessType not found with reference: " + reference));
        accessTypeEntity.setTitle(accessType.getTitle());
        accessTypeEntity.setPrice(accessType.getPrice());
        if(accessType.getEvent() != null) {
            accessTypeEntity.setEvent(this.eventRepository
                    .findByReference(accessType.getEvent().getReference())
                    .orElseThrow(() -> new RuntimeException("Event not found with reference: " + accessType.getEvent().getReference())));
        }
        accessTypeEntity.setCapacityMax(accessType.getCapacityMax());
        accessTypeEntity.setLimitHourMax(accessType.getLimitHourMax());
        accessTypeEntity.setNumDrinks(accessType.getNumDrinks());
        return this.accessTypeRepository.save(accessTypeEntity).toAccessType();
    }

    @Override
    public List<AccessType> findByTitle(String title) {
        return this.accessTypeRepository
                .findByTitle(title)
                .stream()
                .map(AccessTypeEntity::toAccessType)
                .toList();
    }

    @Override
    public int countReservationsByAccessTypeReference(UUID reference) {
        return this.accessTypeRepository.countReservationsByAccessTypeReference(reference);
    }

}
