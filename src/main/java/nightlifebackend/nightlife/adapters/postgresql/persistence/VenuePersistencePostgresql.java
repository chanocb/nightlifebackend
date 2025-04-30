package nightlifebackend.nightlife.adapters.postgresql.persistence;

import nightlifebackend.nightlife.adapters.postgresql.daos.EventRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.ReviewRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.UserRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.VenueRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.*;
import nightlifebackend.nightlife.domain.models.Music;
import nightlifebackend.nightlife.domain.models.Product;
import nightlifebackend.nightlife.domain.models.Schedule;
import nightlifebackend.nightlife.domain.models.Venue;
import nightlifebackend.nightlife.domain.persistence_ports.VenuePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository("venuePersistence")
public class VenuePersistencePostgresql implements VenuePersistence {

    private final VenueRepository venueRepository;

    private final ReviewRepository reviewRepository;

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public VenuePersistencePostgresql(VenueRepository venueRepository, ReviewRepository reviewRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.venueRepository = venueRepository;
        this.reviewRepository = reviewRepository;
        this.eventRepository = eventRepository;
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
    public List<Venue> findByName(String name) {
       return this.venueRepository
                .findByName(name).stream().map(VenueEntity::toVenue)
               .collect(Collectors.toList());
    }

    @Override
    public Venue update(String reference, Venue venue) {
        VenueEntity existingVenueEntity = this.venueRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("Local no encontrado con referencia: " + venue.getReference()));

        existingVenueEntity.setName(venue.getName());
        existingVenueEntity.setPhone(venue.getPhone());
        existingVenueEntity.setInstagram(venue.getInstagram());
        existingVenueEntity.setLGTBFriendly(venue.isLGTBFriendly());
        existingVenueEntity.setImageUrl(venue.getImageUrl());
        existingVenueEntity.setMusicGenres(venue.getMusicGenres());
        if (venue.getCoordinate() != null) {
            existingVenueEntity.setCoordinate(new CoordinateEntity(venue.getCoordinate()));
        } else {
            existingVenueEntity.setCoordinate(null);
        }

        if (venue.getProducts() != null) {
            existingVenueEntity.getProducts().clear();
            for (Product product : venue.getProducts()) {
                ProductEntity productEntity = new ProductEntity(product);
                productEntity.setVenue(existingVenueEntity);
                existingVenueEntity.getProducts().add(productEntity);
            }
        }

        if (venue.getSchedules() != null) {
            existingVenueEntity.getSchedules().clear();
            for (Schedule schedule : venue.getSchedules()) {
                ScheduleEntity scheduleEntity = new ScheduleEntity(schedule);
                scheduleEntity.setVenue(existingVenueEntity);
                existingVenueEntity.getSchedules().add(scheduleEntity);
            }
        }

        return this.venueRepository
                .save(existingVenueEntity)
                .toVenue();
    }

    @Override
    public void deleteByReference(String reference) {
        VenueEntity existingVenueEntity = this.venueRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("Local no encontrado con referencia: " + reference));
        List<ReviewEntity> reviewEntityList = this.reviewRepository.findByVenueReference(existingVenueEntity.getReference());
        for(ReviewEntity reviewEntity : reviewEntityList){
            this.reviewRepository.delete(reviewEntity);
        }
        List<EventEntity> eventEntityList = this.eventRepository.findByVenueReference(existingVenueEntity.getReference());
        for(EventEntity eventEntity : eventEntityList){
            this.eventRepository.delete(eventEntity);
        }
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

    public List<Venue> findByLGTBFriendly(boolean LGTBFriendly) {
        return this.venueRepository.findByLGTBFriendly(LGTBFriendly).stream()
                .map(VenueEntity::toVenue)
                .collect(Collectors.toList());
    }

    public List<Venue> findByMusicGenres(Set<Music> musicGenres) {
        return this.venueRepository.findByMusicGenres(musicGenres, musicGenres.size()).stream()
                .map(VenueEntity::toVenue)
                .collect(Collectors.toList());
    }

    public List<Venue> findByAverageRatingGreaterThanEqual(double minRating) {
        return this.venueRepository.findByAverageRatingGreaterThanEqual(minRating).stream()
                .map(VenueEntity::toVenue)
                .collect(Collectors.toList());
    }

    public List<Venue> findByProductNameAndMaxPrice(String productName, double maxPrice) {
        return this.venueRepository.findByProductNameAndMaxPrice(productName, maxPrice).stream()
                .map(VenueEntity::toVenue)
                .collect(Collectors.toList());
    }

    @Override
    public Venue createSchedules(String reference, List<Schedule> schedules) {
        VenueEntity existingVenueEntity = this.venueRepository
                .findByReference(UUID.fromString(reference))
                .orElseThrow(() -> new RuntimeException("Local no encontrado con referencia: " + reference));

        existingVenueEntity.getSchedules().clear();

        for (Schedule schedule : schedules) {
            ScheduleEntity scheduleEntity = new ScheduleEntity(schedule);
            scheduleEntity.setVenue(existingVenueEntity);
            existingVenueEntity.getSchedules().add(scheduleEntity);
        }

        return this.venueRepository
                .save(existingVenueEntity)
                .toVenue();
    }
}
