package nightlifebackend.nightlife.adapters.postgresql.persistence;

import nightlifebackend.nightlife.adapters.postgresql.daos.EventRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.ReviewRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.VenueRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.EventEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.ReviewEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

public @ExtendWith(MockitoExtension.class)
class VenuePersistencePostgresqlIT {

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private VenuePersistencePostgresql venuePersistencePostgresql;

    @Test
    void deleteByReference_ShouldDeleteReviewsAndEvents() {
        // Arrange
        UUID venueReference = UUID.randomUUID();
        VenueEntity venueEntity = new VenueEntity();
        venueEntity.setReference(venueReference);

        List<ReviewEntity> reviewEntities = List.of(new ReviewEntity(), new ReviewEntity());
        List<EventEntity> eventEntities = List.of(new EventEntity(), new EventEntity());

        when(venueRepository.findByReference(venueReference)).thenReturn(Optional.of(venueEntity));
        when(reviewRepository.findByVenueReference(venueReference)).thenReturn(reviewEntities);
        when(eventRepository.findByVenueReference(venueReference)).thenReturn(eventEntities);

        // Act
        venuePersistencePostgresql.deleteByReference(venueReference.toString());

        // Assert
        verify(reviewRepository, times(reviewEntities.size())).delete(any(ReviewEntity.class));
        verify(eventRepository, times(eventEntities.size())).delete(any(EventEntity.class));
        verify(venueRepository).delete(venueEntity);
    }
}
