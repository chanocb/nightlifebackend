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
@Data
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

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonProperty("user")
    private UserEntity owner;

    private String imageUrl;

    @OneToOne
    @JoinColumn(name = "coordinate_id")
    private CoordinateEntity coordinate;

    public VenueEntity(Venue venue) {
        BeanUtils.copyProperties(venue, this);
        if (venue.getOwner() != null) {
            this.owner = new UserEntity(venue.getOwner());
        }
        if (venue.getCoordinate() != null) {
            this.coordinate = new CoordinateEntity(venue.getCoordinate());
        }
    }

    public Venue toVenue() {
        Venue venue = new Venue();
        BeanUtils.copyProperties(this, venue);
        if (this.owner != null) {
            venue.setOwner(this.owner.toUser());
        }
        if (this.coordinate != null) {
            venue.setCoordinate(this.coordinate.toCoordinate());
        }
        return venue;
    }
}