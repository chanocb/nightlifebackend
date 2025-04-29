package nightlifebackend.nightlife.domain.services;

import nightlifebackend.nightlife.domain.models.DayOfWeek;
import nightlifebackend.nightlife.domain.models.Music;
import nightlifebackend.nightlife.domain.models.Schedule;
import nightlifebackend.nightlife.domain.models.Venue;
import nightlifebackend.nightlife.domain.persistence_ports.VenuePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.NoSuchElementException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VenueService {

    private final VenuePersistence venuePersistence;
    private final JwtService jwtService;

    @Autowired
    public VenueService(VenuePersistence venuePersistence, JwtService jwtService) {
        this.venuePersistence = venuePersistence;
        this.jwtService = jwtService;
    }

    public Venue create(Venue venue) {
        return this.venuePersistence.create(venue);
    }

    public List<Venue> findAll() {
        return this.venuePersistence.findAll();
    }

    public Venue findByReference(String reference) {
        return this.venuePersistence.findByReference(reference);
    }
    public List<Venue> findByName(String name) {
        return this.venuePersistence.findByName(name);
    }

    public Venue update(String reference, Venue venue) {
        String ownerEmail = jwtService.getAuthenticatedUserEmail();
        Venue existingVenue = venuePersistence.findByReference(reference);

        if (existingVenue == null) {
            throw new NoSuchElementException("Venue not found with reference: " + reference);
        }

        if (!existingVenue.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not authorized to update this venue");
        }

        return venuePersistence.update(reference, venue);
    }

    public void delete(String reference) {
        venuePersistence.deleteByReference(reference);
    }

    public List<Venue> getVenuesByOwner(String email) {
        String ownerEmail = jwtService.getAuthenticatedUserEmail();
        if (!ownerEmail.equals(email)) {
            throw new AccessDeniedException("You are not authorized to view this venue");
        }
        return venuePersistence.findByOwnerEmail(ownerEmail);
    }

    public List<Venue> findByLGTBFriendly(boolean LGTBFriendly) {
        return this.venuePersistence.findByLGTBFriendly(LGTBFriendly);
    }

    public List<Venue> findByMusicGenres(Set<Music> musicGenres) {
        return this.venuePersistence.findByMusicGenres(musicGenres);
    }

    public List<Venue> findByAverageRatingGreaterThanEqual(double minRating) {
        return this.venuePersistence.findByAverageRatingGreaterThanEqual(minRating);
    }

    public List<Venue> findByProductNameAndMaxPrice(String productName, double maxPrice) {
        return this.venuePersistence.findByProductNameAndMaxPrice(productName, maxPrice);
    }

    public Venue createSchedules(String reference, List<Schedule> schedules) {
        String ownerEmail = jwtService.getAuthenticatedUserEmail();
        Venue existingVenue = venuePersistence.findByReference(reference);

        if (existingVenue == null) {
            throw new NoSuchElementException("Venue not found with reference: " + reference);
        }

        if (!existingVenue.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not authorized to update this venue");
        }

        Set<DayOfWeek> days = schedules.stream()
                .map(Schedule::getDayOfWeek)
                .collect(Collectors.toSet());
        
        if (days.size() != schedules.size()) {
            throw new IllegalArgumentException("No se pueden tener días duplicados en los horarios");
        }

        return venuePersistence.createSchedules(reference, schedules);
    }

    public Schedule getSchedule(String reference, String scheduleId) {
        Venue venue = this.findByReference(reference);
        if (venue == null) {
            throw new NoSuchElementException("Venue not found with reference: " + reference);
        }
        return venue.getSchedules().stream()
                .filter(s -> s.getReference() != null && s.getReference().toString().equals(scheduleId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Schedule not found with id: " + scheduleId));
    }

    public Schedule updateSchedule(String reference, String scheduleId, Schedule schedule) {
        Venue venue = this.findByReference(reference);
        if (venue == null) {
            throw new NoSuchElementException("Venue not found with reference: " + reference);
        }
        List<Schedule> schedules = venue.getSchedules();
        boolean updated = false;
        for (int i = 0; i < schedules.size(); i++) {
            Schedule s = schedules.get(i);
            if (s.getReference() != null && s.getReference().toString().equals(scheduleId)) {
                schedule.setReference(s.getReference());
                schedules.set(i, schedule);
                updated = true;
                break;
            }
        }
        if (!updated) {
            throw new NoSuchElementException("Schedule not found with id: " + scheduleId);
        }
        venue.setSchedules(schedules);
        this.venuePersistence.update(reference, venue);
        return schedule;
    }

    public void deleteSchedule(String reference, String scheduleId) {
        Venue venue = this.findByReference(reference);
        if (venue != null) {
            List<Schedule> schedules = venue.getSchedules();
            schedules.removeIf(s -> s.getReference() != null && s.getReference().toString().equals(scheduleId));
            venue.setSchedules(schedules);
            this.venuePersistence.update(reference, venue);
        }
    }
}