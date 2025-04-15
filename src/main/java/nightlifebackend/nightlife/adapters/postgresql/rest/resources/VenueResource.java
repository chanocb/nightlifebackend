package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.Venue;
import nightlifebackend.nightlife.domain.services.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(VenueResource.VENUES)
public class VenueResource {

    static final String VENUES = "/venues";

    private final VenueService venueService;
    @Autowired
    public VenueResource(VenueService venueService) {
        this.venueService = venueService;
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public nightlifebackend.nightlife.domain.models.Venue create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.Venue venue) {
        return this.venueService.create(venue);
    }

    @GetMapping
    public List<Venue> getAllVenues() {
        return this.venueService.findAll();
    }

    @GetMapping("/{reference}")
    public Venue getVenueByReference(@PathVariable String reference) {
        return this.venueService.findByReference(reference);
    }
    @GetMapping("/name/{name}")
    public List<Venue> getVenuesByName(@PathVariable String name) {
        return this.venueService.findByName(name);
    }
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{reference}")
    public Venue updateVenue(@PathVariable String reference, @Valid @RequestBody Venue venue) {
        return this.venueService.update(reference, venue);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{reference}")
    public void deleteVenue(@PathVariable String reference) {
        this.venueService.delete(reference);
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owner")
    public List<Venue> getVenuesByOwner(@RequestParam String email) {
        return venueService.getVenuesByOwner(email);
    }
}
