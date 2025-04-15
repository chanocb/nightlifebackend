package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.adapters.postgresql.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<ReviewEntity, UUID> {

    @Query("SELECT r FROM ReviewEntity r WHERE r.venue.reference = ?1")
    List<ReviewEntity> findByVenueReference(UUID reference);
}
