package nightlifebackend.nightlife.adapters.postgresql.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nightlifebackend.nightlife.domain.models.Venue;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

@Builder
@Data //@ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venue")
public class VenueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reference;
    private String name;
    private String phone;
    @JsonProperty("LGTBFriendly")
    private boolean LGTBFriendly;
    private String instagram;

    public VenueEntity(Venue venue) {
        BeanUtils.copyProperties(venue, this);

    }

    public Venue toVenue() {
        Venue venue = new Venue();
        BeanUtils.copyProperties(this, venue);
        return venue;
    }
}
