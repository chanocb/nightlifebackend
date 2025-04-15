package nightlifebackend.nightlife.adapters.postgresql.persistence;

import nightlifebackend.nightlife.adapters.postgresql.daos.ReviewRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.UserRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.VenueRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.ReviewEntity;
import nightlifebackend.nightlife.domain.models.Review;
import nightlifebackend.nightlife.domain.persistence_ports.ReviewPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("reviewPersistence")
public class ReviewPersistencePostgresql implements ReviewPersistence {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final VenueRepository venueRepository;

    @Autowired
    public ReviewPersistencePostgresql(ReviewRepository reviewRepository,
                                       UserRepository userRepository,
                                       VenueRepository venueRepository) {

        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.venueRepository = venueRepository;
    }

    @Override
    public Review create(Review review) {
        if (review.getUser() != null && review.getVenue() != null) {
            ReviewEntity reviewEntity = new ReviewEntity(review);
            reviewEntity.setUser(userRepository.findByEmail(review.getUser().getEmail()).get());
            reviewEntity.setVenue(venueRepository.findByReference(review.getVenue().getReference()).get());
            return this.reviewRepository.save(reviewEntity).toReview();
        }
        throw new RuntimeException("User and Venue cannot be null");
    }

    @Override
    public List<Review> findByVenueReference(UUID reference) {
        return this.reviewRepository
                .findByVenueReference(reference)
                .stream()
                .map(ReviewEntity::toReview)
                .toList();
    }
}
