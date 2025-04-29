package nightlifebackend.nightlife.adapters.postgresql.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nightlifebackend.nightlife.domain.models.Event;
import nightlifebackend.nightlife.domain.models.User;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reference;
    private String name;
    private String description;
    private LocalDateTime dateTime;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private VenueEntity venue;

    public EventEntity(Event event) {
        BeanUtils.copyProperties(event, this);

    }

    public Event toEvent() {
        Event event = new Event();
        BeanUtils.copyProperties(this, event);
        event.setVenue(this.venue.toVenue());
        return event;
    }
}
