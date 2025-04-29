package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

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

    @GetMapping("/filter/lgtb-friendly/{LGTBFriendly}")
    public List<Venue> findByLGTBFriendly(@PathVariable boolean LGTBFriendly) {
        return this.venueService.findByLGTBFriendly(LGTBFriendly);
    }

    @GetMapping("/filter/music-genres")
    public List<Venue> findByMusicGenres(@RequestParam Set<Music> musicGenres) {
        return this.venueService.findByMusicGenres(musicGenres);
    }

    @GetMapping("/filter/rating/{minRating}")
    public List<Venue> findByAverageRatingGreaterThanEqual(@PathVariable double minRating) {
        return this.venueService.findByAverageRatingGreaterThanEqual(minRating);
    }

    @GetMapping("/filter/product")
    public List<Venue> findByProductNameAndMaxPrice(@RequestParam String productName, @RequestParam double maxPrice) {
        return this.venueService.findByProductNameAndMaxPrice(productName, maxPrice);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{reference}/schedules")
    public Venue createSchedules(@PathVariable String reference, @Valid @RequestBody List<Schedule> schedules) {
        return this.venueService.createSchedules(reference, schedules);
    }

    @GetMapping("/{reference}/schedules")
    public List<Schedule> getSchedules(@PathVariable String reference) {
        return this.venueService.findByReference(reference).getSchedules();
    }

    @GetMapping("/{reference}/schedules/{scheduleId}")
    public Schedule getSchedule(@PathVariable String reference, @PathVariable String scheduleId) {
        return this.venueService.getSchedule(reference, scheduleId);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{reference}/schedules")
    public Venue updateSchedules(@PathVariable String reference, @Valid @RequestBody List<Schedule> schedules) {
        return this.venueService.createSchedules(reference, schedules);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{reference}/schedules")
    public void deleteSchedules(@PathVariable String reference) {
        this.venueService.createSchedules(reference, List.of());
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{reference}/schedules/{scheduleId}")
    public Schedule updateSchedule(@PathVariable String reference, @PathVariable String scheduleId, @Valid @RequestBody Schedule schedule) {
        return this.venueService.updateSchedule(reference, scheduleId, schedule);
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{reference}/schedules/{scheduleId}")
    public void deleteSchedule(@PathVariable String reference, @PathVariable String scheduleId) {
        this.venueService.deleteSchedule(reference, scheduleId);
    }
}
