package nightlifebackend.nightlife.domain.services;

import nightlifebackend.nightlife.domain.models.Venue;
import nightlifebackend.nightlife.domain.persistence_ports.VenuePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.NoSuchElementException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;

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
        String ownerEmail = jwtService.getAuthenticatedUserEmail();
        Venue venue = venuePersistence.findByReference(reference);
        if (venue == null) {
            throw new NoSuchElementException("Venue not found with reference: " + reference);
        }

        if (!venue.getOwner().getEmail().equals(ownerEmail)) {
            throw new AccessDeniedException("You are not authorized to delete this venue");
        }
        venuePersistence.deleteByReference(reference);
    }
}