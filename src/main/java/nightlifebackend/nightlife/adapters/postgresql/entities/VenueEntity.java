package nightlifebackend.nightlife.adapters.postgresql.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nightlifebackend.nightlife.domain.models.Music;
import nightlifebackend.nightlife.domain.models.Product;
import nightlifebackend.nightlife.domain.models.Schedule;
import nightlifebackend.nightlife.domain.models.Venue;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    @OneToOne(cascade = CascadeType.ALL)
    private CoordinateEntity coordinate;

    @ElementCollection(targetClass = Music.class)
    @CollectionTable(
            name = "venue_music",
            joinColumns = @JoinColumn(name = "venue_id")
    )
    @Column(name = "enum_value")
    @Builder.Default
    private Set<Music> musicGenres = new HashSet<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductEntity> products = new ArrayList<>();

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScheduleEntity> schedules = new ArrayList<>();

    public VenueEntity(Venue venue) {
        BeanUtils.copyProperties(venue, this);
        if (venue.getOwner() != null) {
            this.owner = new UserEntity(venue.getOwner());
        }
        if (venue.getCoordinate() != null) {
            this.coordinate = new CoordinateEntity(venue.getCoordinate());
        }
        this.musicGenres = venue.getMusicGenres();
        if (venue.getProducts() != null) {
            this.products = venue.getProducts().stream()
                    .map(product -> {
                        ProductEntity productEntity = new ProductEntity(product);
                        productEntity.setVenue(this);
                        return productEntity;
                    })
                    .collect(Collectors.toList());
        }
        if (venue.getSchedules() != null) {
            this.schedules = venue.getSchedules().stream()
                    .map(schedule -> {
                        ScheduleEntity scheduleEntity = new ScheduleEntity(schedule);
                        scheduleEntity.setVenue(this);
                        return scheduleEntity;
                    })
                    .collect(Collectors.toList());
        }
    }

    public Venue toVenue() {
        Venue venue = new Venue();
        venue.setReference(this.reference);
        venue.setName(this.name);
        venue.setPhone(this.phone);
        venue.setLGTBFriendly(this.LGTBFriendly);
        venue.setInstagram(this.instagram);
        venue.setImageUrl(this.imageUrl);
        venue.setMusicGenres(this.musicGenres);
        if (this.owner != null) {
            venue.setOwner(this.owner.toUser());
        }
        if (this.coordinate != null) {
            venue.setCoordinate(this.coordinate.toCoordinate());
        }
        venue.setMusicGenres(this.musicGenres);
        if (this.products != null) {
            venue.setProducts(this.products.stream()
                    .map(ProductEntity::toProduct)
                    .collect(Collectors.toList()));
        } else {
            venue.setProducts(new ArrayList<>());
        }
        if (this.schedules != null) {
            venue.setSchedules(this.schedules.stream()
                    .map(ScheduleEntity::toSchedule)
                    .collect(Collectors.toList()));
        } else {
            venue.setSchedules(new ArrayList<>());
        }
        return venue;
    }
}