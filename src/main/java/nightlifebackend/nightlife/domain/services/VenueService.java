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

        // Verificar que no haya días duplicados
        Set<DayOfWeek> days = schedules.stream()
                .map(Schedule::getDayOfWeek)
                .collect(Collectors.toSet());
        
        if (days.size() != schedules.size()) {
            throw new IllegalArgumentException("No se pueden tener días duplicados en los horarios");
        }

        return venuePersistence.createSchedules(reference, schedules);
    }
}