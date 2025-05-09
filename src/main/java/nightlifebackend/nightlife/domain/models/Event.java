package nightlifebackend.nightlife.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Event {


    private UUID reference;

    @NotBlank(message = "The name cannot be empty.")
    private String name;
    @NotBlank(message = "The description cannot be empty.")
    private String description;
    @NotNull(message = "Date and time must not be null")
    @Future(message = "Date and time must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;
    @JsonProperty("venue")
    private Venue venue;
    @JsonProperty("accessTypes")
    private List<AccessType> accessTypes;


}
