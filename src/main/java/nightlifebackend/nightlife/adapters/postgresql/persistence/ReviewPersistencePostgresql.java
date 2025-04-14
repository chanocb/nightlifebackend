package nightlifebackend.nightlife.adapters.postgresql.persistence;

import nightlifebackend.nightlife.adapters.postgresql.daos.ReviewRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.ReviewEntity;
import nightlifebackend.nightlife.domain.models.Review;
import nightlifebackend.nightlife.domain.persistence_ports.ReviewPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("reviewPersistence")
public class ReviewPersistencePostgresql implements ReviewPersistence {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewPersistencePostgresql(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review create(Review review) {
        if (review.getUser() != null && review.getVenue() != null) {
            ReviewEntity reviewEntity = new ReviewEntity(review);
            return this.reviewRepository.save(reviewEntity).toReview();
        }
        throw new RuntimeException("User and Venue cannot be null");
    }
}
