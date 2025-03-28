package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VenueRepository extends JpaRepository<VenueEntity, UUID> {

    Optional<VenueEntity> findByReference(UUID reference);
    List<VenueEntity> findByOwnerEmail(String ownerEmail);
}
