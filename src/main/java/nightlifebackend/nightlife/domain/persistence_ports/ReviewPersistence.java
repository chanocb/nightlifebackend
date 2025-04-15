package nightlifebackend.nightlife.domain.persistence_ports;

import nightlifebackend.nightlife.domain.models.Review;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewPersistence {

    Review create(Review review);

    List<Review> findByVenueReference(UUID reference);
}
