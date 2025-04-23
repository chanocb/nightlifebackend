package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.Review;
import nightlifebackend.nightlife.domain.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ReviewResource.REVIEWS)
public class ReviewResource {

    static final String REVIEWS = "/reviews";

    private final ReviewService reviewService;
    @Autowired
    public ReviewResource(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public nightlifebackend.nightlife.domain.models.Review create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.Review review) {
        return this.reviewService.create(review);
    }

    @GetMapping("/venue/{reference}")
    public List<Review> findByVenueReference(@PathVariable UUID reference) {
        return this.reviewService.findByVenueReference(reference);
    }

    @GetMapping("/{reference}")
    public Review findByReference(@PathVariable UUID reference) {
        return this.reviewService.findByReference(reference);
    }

    @DeleteMapping("/{reference}")
    public void deleteByReference(@PathVariable UUID reference) {
        this.reviewService.deleteByReference(reference);
    }

    @GetMapping("/title/{title}")
    public List<Review> findByTitle(@PathVariable String title) {
        return this.reviewService.findByTitle(title);
    }
}
