package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.Music;
import nightlifebackend.nightlife.domain.models.Schedule;
import nightlifebackend.nightlife.domain.models.Venue;
import nightlifebackend.nightlife.domain.services.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
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

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public nightlifebackend.nightlife.domain.models.Venue create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.Venue venue) {
        return this.venueService.create(venue);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<Venue> getAllVenues() {
        return this.venueService.findAll();
    }

    @GetMapping("/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    public Venue getVenueByReference(@PathVariable String reference) {
        return this.venueService.findByReference(reference);
    }
    @GetMapping("/name/{name}")
    @SecurityRequirement(name = "bearerAuth")
    public List<Venue> getVenuesByName(@PathVariable String name) {
        return this.venueService.findByName(name);
    }
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    public Venue updateVenue(@PathVariable String reference, @Valid @RequestBody Venue venue) {
        return this.venueService.update(reference, venue);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    public void deleteVenue(@PathVariable String reference) {
        this.venueService.delete(reference);
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owner")
    @SecurityRequirement(name = "bearerAuth")
    public List<Venue> getVenuesByOwner(@RequestParam String email) {
        return venueService.getVenuesByOwner(email);
    }

    @GetMapping("/filter/lgtb-friendly/{LGTBFriendly}")
    @SecurityRequirement(name = "bearerAuth")
    public List<Venue> findByLGTBFriendly(@PathVariable boolean LGTBFriendly) {
        return this.venueService.findByLGTBFriendly(LGTBFriendly);
    }

    @GetMapping("/filter/music-genres")
    @SecurityRequirement(name = "bearerAuth")
    public List<Venue> findByMusicGenres(@RequestParam Set<Music> musicGenres) {
        return this.venueService.findByMusicGenres(musicGenres);
    }

    @GetMapping("/filter/rating/{minRating}")
    @SecurityRequirement(name = "bearerAuth")
    public List<Venue> findByAverageRatingGreaterThanEqual(@PathVariable double minRating) {
        return this.venueService.findByAverageRatingGreaterThanEqual(minRating);
    }

    @GetMapping("/filter/product")
    @SecurityRequirement(name = "bearerAuth")
    public List<Venue> findByProductNameAndMaxPrice(@RequestParam String productName, @RequestParam double maxPrice) {
        return this.venueService.findByProductNameAndMaxPrice(productName, maxPrice);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{reference}/schedules")
    @SecurityRequirement(name = "bearerAuth")
    public Venue createSchedules(@PathVariable String reference, @Valid @RequestBody List<Schedule> schedules) {
        return this.venueService.createSchedules(reference, schedules);
    }

    @GetMapping("/{reference}/schedules")
    @SecurityRequirement(name = "bearerAuth")
    public List<Schedule> getSchedules(@PathVariable String reference) {
        return this.venueService.findByReference(reference).getSchedules();
    }

    @GetMapping("/{reference}/schedules/{scheduleId}")
    @SecurityRequirement(name = "bearerAuth")
    public Schedule getSchedule(@PathVariable String reference, @PathVariable String scheduleId) {
        return this.venueService.getSchedule(reference, scheduleId);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{reference}/schedules")
    @SecurityRequirement(name = "bearerAuth")
    public Venue updateSchedules(@PathVariable String reference, @Valid @RequestBody List<Schedule> schedules) {
        return this.venueService.createSchedules(reference, schedules);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{reference}/schedules")
    @SecurityRequirement(name = "bearerAuth")
    public void deleteSchedules(@PathVariable String reference) {
        this.venueService.createSchedules(reference, List.of());
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{reference}/schedules/{scheduleId}")
    @SecurityRequirement(name = "bearerAuth")
    public Schedule updateSchedule(@PathVariable String reference, @PathVariable String scheduleId, @Valid @RequestBody Schedule schedule) {
        return this.venueService.updateSchedule(reference, scheduleId, schedule);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{reference}/schedules/{scheduleId}")
    @SecurityRequirement(name = "bearerAuth")
    public void deleteSchedule(@PathVariable String reference, @PathVariable String scheduleId) {
        this.venueService.deleteSchedule(reference, scheduleId);
    }
}
