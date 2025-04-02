package nightlifebackend.nightlife.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Venue {

    private UUID reference;

    @NotBlank(message = "The name cannot be empty.")
    private String name;
    @NotBlank(message = "The phone cannot be empty.")
    @Pattern(regexp = "^[+]?[0-9]{0,4}[-\s]?[0-9]{9}$", message = "The phone number must have a valid format.")
    private String phone;
    @JsonProperty("LGTBFriendly")
    private boolean LGTBFriendly;
    private String instagram;
    @JsonProperty("user")
    private User owner;
    private String imageUrl;
    @Valid
    private Coordinate coordinate;
}
