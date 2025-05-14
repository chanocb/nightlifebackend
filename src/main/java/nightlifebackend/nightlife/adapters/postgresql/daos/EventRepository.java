package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.adapters.postgresql.entities.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {

    @Query("SELECT e FROM EventEntity e WHERE e.venue.reference = ?1")
    List<EventEntity> findByVenueReference(UUID reference);

    Optional<EventEntity> findByReference(UUID reference);

    void deleteByReference(UUID reference);

    @Query("SELECT e FROM EventEntity e WHERE e.name = ?1")
    List<EventEntity> findByName(String name);

    @Query("SELECT e FROM EventEntity e JOIN e.accessTypes a WHERE a.reference = ?1")
    Optional<EventEntity> findEventByAccessType(UUID accessTypeReference);


}