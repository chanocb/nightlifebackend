package nightlifebackend.nightlife.domain.models;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Coordinate {

    @Min(-90) @Max(90)
    private double latitude;

    @Min(-180) @Max(180)
    private double longitude;
}
