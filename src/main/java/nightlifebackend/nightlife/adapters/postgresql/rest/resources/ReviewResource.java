package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
