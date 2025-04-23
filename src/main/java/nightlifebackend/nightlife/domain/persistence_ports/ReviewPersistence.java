package nightlifebackend.nightlife.domain.persistence_ports;

import nightlifebackend.nightlife.domain.models.Review;
import nightlifebackend.nightlife.domain.models.Venue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewPersistence {

    Review create(Review review);

    List<Review> findByVenueReference(UUID reference);

    Review findByReference(UUID reference);

    void deleteByReference(UUID reference);

    List<Review> findByTitle(String title);
}
