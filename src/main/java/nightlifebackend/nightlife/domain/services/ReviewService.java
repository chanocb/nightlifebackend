package nightlifebackend.nightlife.domain.services;

import nightlifebackend.nightlife.domain.models.Review;
import nightlifebackend.nightlife.domain.persistence_ports.ReviewPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewPersistence reviewPersistence;
    private final JwtService jwtService;

    @Autowired
    public ReviewService(ReviewPersistence reviewPersistence, JwtService jwtService) {
        this.reviewPersistence = reviewPersistence;
        this.jwtService = jwtService;
    }

    public Review create(Review review) {
        return this.reviewPersistence.create(review);
    }

    public List<Review> findByVenueReference(String reference) {
        return this.reviewPersistence.findByVenueReference(reference);
    }

    public Review findByReference(String reference) {
        return this.reviewPersistence.findByReference(reference);
    }

    public void deleteByReference(String reference) {
        String userEmail = jwtService.getAuthenticatedUserEmail();
        Review review = this.reviewPersistence.findByReference(reference);

        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You are not authorized to delete this review");
        }

        this.reviewPersistence.deleteByReference(reference);
    }

    public List<Review> findByTitle(String title) {
        return this.reviewPersistence.findByTitle(title);
    }
}
