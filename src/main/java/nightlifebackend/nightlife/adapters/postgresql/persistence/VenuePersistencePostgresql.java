package nightlifebackend.nightlife.adapters.postgresql.persistence;


import nightlifebackend.nightlife.adapters.postgresql.daos.UserRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.VenueRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import nightlifebackend.nightlife.domain.models.Venue;
import nightlifebackend.nightlife.domain.persistence_ports.VenuePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository("venuePersistence")
public class VenuePersistencePostgresql implements VenuePersistence {

    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    @Autowired
    public VenuePersistencePostgresql(VenueRepository venueRepository, UserRepository userRepository) {
        this.venueRepository = venueRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Venue create(Venue venue) {
        if(venue.getOwner() != null){
            UserEntity owner = userRepository.findByEmail(venue.getOwner().getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + venue.getOwner().getEmail()));

            VenueEntity venueEntity = new VenueEntity(venue);
            venueEntity.setOwner(owner);
            return this.venueRepository.save(venueEntity).toVenue();
        }
        throw new RuntimeException("Owner cannot be null");
    }

    @Override
    public List<Venue> findAll() {
        return this.venueRepository
                .findAll()
                .stream()
                .map(VenueEntity::toVenue)
                .toList();
    }

    @Override
    public Venue findByReference(String reference) {
        return this.venueRepository
                .findByReference(UUID.fromString(reference))
                .map(VenueEntity::toVenue)
                .orElse(null);
    }

    @Override
    public Venue update(String reference, Venue venue) {
        VenueEntity existingVenueEntity = this.venueRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("Locla no encontrado con referencia: " + venue.getReference()));

        existingVenueEntity.setName(venue.getName());
        existingVenueEntity.setPhone(venue.getPhone());
        existingVenueEntity.setInstagram(venue.getInstagram());
        existingVenueEntity.setLGTBFriendly(venue.isLGTBFriendly());
        existingVenueEntity.setImageUrl(venue.getImageUrl());
        existingVenueEntity.setMusicGenres(venue.getMusicGenres());

        return this.venueRepository
                .save(existingVenueEntity)
                .toVenue();
    }

    @Override
    public void deleteByReference(String reference) {
        VenueEntity existingVenueEntity = this.venueRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("Local no encontrado con referencia: " + reference));
        this.venueRepository.delete(existingVenueEntity);
    }

    @Override
    public List<Venue> findByOwnerEmail(String email) {
        return this.venueRepository
                .findByOwnerEmail(email)
                .stream()
                .map(VenueEntity::toVenue)
                .toList();
    }
}
