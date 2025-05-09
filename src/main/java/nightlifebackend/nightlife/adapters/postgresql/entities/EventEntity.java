package nightlifebackend.nightlife.adapters.postgresql.entities;

import jakarta.persistence.*;
import lombok.*;
import nightlifebackend.nightlife.domain.models.Event;
import nightlifebackend.nightlife.domain.models.User;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
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

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<AccessTypeEntity> accessTypes;

    public EventEntity(Event event) {
        BeanUtils.copyProperties(event, this);

    }

    public Event toEvent() {
        Event event = new Event();
        event.setReference(this.reference);
        event.setName(this.name);
        event.setDescription(this.description);
        event.setDateTime(this.dateTime);
        event.setVenue(this.venue != null ? this.venue.toVenue() : null);
        event.setAccessTypes(this.accessTypes != null
                ? this.accessTypes.stream().map(AccessTypeEntity::toAccessType).toList()
                : List.of());
        return event;
    }
}
