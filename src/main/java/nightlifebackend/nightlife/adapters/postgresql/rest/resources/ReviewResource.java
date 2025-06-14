package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.Review;
import nightlifebackend.nightlife.domain.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENT')")
    public nightlifebackend.nightlife.domain.models.Review create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.Review review) {
        return this.reviewService.create(review);
    }

    @GetMapping("/venue/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    public List<Review> findByVenueReference(@PathVariable String reference) {
        return this.reviewService.findByVenueReference(reference);
    }

    @GetMapping("/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    public Review findByReference(@PathVariable String reference) {
        return this.reviewService.findByReference(reference);
    }

    @DeleteMapping("/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENT')")
    public void deleteByReference(@PathVariable String reference) {
        this.reviewService.deleteByReference(reference);
    }

    @GetMapping("/title/{title}")
    @SecurityRequirement(name = "bearerAuth")
    public List<Review> findByTitle(@PathVariable String title) {
        return this.reviewService.findByTitle(title);
    }
}
