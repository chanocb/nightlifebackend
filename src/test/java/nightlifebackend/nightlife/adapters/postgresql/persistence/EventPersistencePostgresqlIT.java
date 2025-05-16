package nightlifebackend.nightlife.adapters.postgresql.persistence;

import nightlifebackend.nightlife.adapters.postgresql.daos.EventRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.EventEntity;
import nightlifebackend.nightlife.domain.models.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public @ExtendWith(MockitoExtension.class)
class EventPersistencePostgresqlIT {


    private EventRepository eventRepository;
    @InjectMocks
    private EventPersistencePostgresql eventPersistence;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        eventPersistence = new EventPersistencePostgresql(null, eventRepository);
    }

    @Test
    void testFindEventByAccessType_ValidReference() {
        // Arrange
        String accessTypeReference = UUID.randomUUID().toString();
        EventEntity eventEntity = mock(EventEntity.class);
        when(eventRepository.findEventByAccessType(UUID.fromString(accessTypeReference)))
                .thenReturn(Optional.of(eventEntity));
        Event expectedEvent = mock(Event.class);
        when(eventEntity.toEvent()).thenReturn(expectedEvent);

        // Act
        Event result = eventPersistence.findEventByAccessType(accessTypeReference);

        // Assert
        assertNotNull(result);
        assertEquals(expectedEvent, result);
        verify(eventRepository, times(1)).findEventByAccessType(UUID.fromString(accessTypeReference));
    }

    @Test
    void testFindEventByAccessType_InvalidReference() {
        // Arrange
        String accessTypeReference = UUID.randomUUID().toString();
        when(eventRepository.findEventByAccessType(UUID.fromString(accessTypeReference)))
                .thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventPersistence.findEventByAccessType(accessTypeReference);
        });
        assertEquals("Event not found with access type: " + accessTypeReference, exception.getMessage());
        verify(eventRepository, times(1)).findEventByAccessType(UUID.fromString(accessTypeReference));
    }
}
