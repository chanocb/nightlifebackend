package nightlifebackend.nightlife.adapters.postgresql.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nightlifebackend.nightlife.domain.models.Review;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

@Builder
@Data //@ToString, @EqualsAndHashCode, @Getter, @Setter, @RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reference;
    private String title;
    private String opinion;
    private int rating;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private VenueEntity venue;

    public ReviewEntity(Review review){
        BeanUtils.copyProperties(review, this);

    }

    public Review toReview(){
        Review review = new Review();
        BeanUtils.copyProperties(this, review);
        review.setUser(this.user.toUser());
        review.setVenue(this.venue.toVenue());
        return review;
    }
}
