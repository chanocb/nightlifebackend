package nightlifebackend.nightlife.domain.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Product {

    @NotBlank(message = "The name cannot be empty.")
    String name;
    @NotBlank(message = "The price cannot be empty.")
    double price;
}
