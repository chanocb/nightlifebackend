package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<VenueEntity, Integer> {
}
