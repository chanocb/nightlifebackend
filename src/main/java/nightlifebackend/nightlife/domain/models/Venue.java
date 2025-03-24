package nightlifebackend.nightlife.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Venue {

    @NotBlank(message = "The name cannot be empty.")
    private String name;
    @NotBlank(message = "The phone cannot be empty.")
    @Pattern(regexp = "^[+]?[0-9]{0,4}[-\s]?[0-9]{9}$", message = "The phone number must have a valid format.")
    private String phone;
    private boolean LGTBFriendly;
    private String instagram;
}
