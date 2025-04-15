package nightlifebackend.nightlife.domain.services;

import nightlifebackend.nightlife.domain.models.Review;
import nightlifebackend.nightlife.domain.persistence_ports.ReviewPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<Review> findByVenueReference(UUID reference) {
        return this.reviewPersistence.findByVenueReference(reference);
    }
}
