package nightlifebackend.nightlife.domain.persistence_ports;

import nightlifebackend.nightlife.domain.models.Review;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewPersistence {

    Review create(Review review);
}
