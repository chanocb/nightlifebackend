package nightlifebackend.nightlife.domain.models;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Review {

    private UUID reference;
    private String title;
    private String opinion;
    @Min(1) @Max(5)
    private int rating;

    private User user;
    private Venue venue;
}
