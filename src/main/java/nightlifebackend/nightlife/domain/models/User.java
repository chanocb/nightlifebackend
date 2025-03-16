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

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "El correo electrónico debe tener un formato válido.")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String password;

    @NotBlank(message = "El primer nombre no puede estar vacío.")
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío.")
    private String lastName;

    @NotBlank(message = "El teléfono no puede estar vacío.")
    @Pattern(regexp = "^[+]?[0-9]{1,4}[-\s]?[0-9]{1,15}$", message = "El teléfono debe tener un formato válido.")
    private String phone;

    @NotNull(message = "La fecha de nacimiento no puede ser nula.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @NotNull(message = "El rol no puede ser nulo.")
    private Role role;
}