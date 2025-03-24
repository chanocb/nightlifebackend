package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.services.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(VenueResource.VENUES)
public class VenueResource {

    static final String VENUES = "/venues";

    private final VenueService venueService;
    @Autowired
    public VenueResource(VenueService venueService) {
        this.venueService = venueService;
    }

    @PostMapping
    public nightlifebackend.nightlife.domain.models.Venue create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.Venue venue) {
        return this.venueService.create(venue);
    }
}
