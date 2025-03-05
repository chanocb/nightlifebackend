package nightlifebackend.nightlife.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDateTime birthDate;
    private Role role;



}
