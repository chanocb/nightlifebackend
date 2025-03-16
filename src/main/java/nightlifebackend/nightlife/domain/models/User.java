package nightlifebackend.nightlife.domain.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {

    @NotBlank(message = "The email cannot be empty.")
    @Email(message = "The email must have a valid format.")
    private String email;

    @NotBlank(message = "The password cannot be empty.")
    private String password;

    @NotBlank(message = "The first name cannot be empty.")
    private String firstName;

    @NotBlank(message = "The last name cannot be empty.")
    private String lastName;

    @NotBlank(message = "The phone number cannot be empty.")
    @Pattern(regexp = "^[+]?[0-9]{1,4}[-\s]?[0-9]{1,15}$", message = "The phone number must have a valid format.")
    private String phone;

    @NotNull(message = "The birth date cannot be null.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @NotNull(message = "The role cannot be null.")
    private Role role;
}