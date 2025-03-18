package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import nightlifebackend.nightlife.adapters.postgresql.rest.dtos.TokenDto;
import org.springframework.security.core.userdetails.User;
import nightlifebackend.nightlife.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {

    static final String USERS = "/users";
    public static final String TOKEN = "/token";

    private final UserService userService;
    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public nightlifebackend.nightlife.domain.models.User create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.User user) {
        return this.userService.create(user);
    }

    @SecurityRequirement(name = "basicAuth")
    @PreAuthorize("authenticated")
    @PostMapping(value = TOKEN)
    public TokenDto login(@AuthenticationPrincipal User activeUser) {
        return new TokenDto(userService.login(activeUser.getUsername()));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{email}")
    @PreAuthorize("authenticated")
    public nightlifebackend.nightlife.domain.models.User readUserByEmail(@PathVariable String email) {
        return this.userService.readUserByEmail(email);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{email}")
    public nightlifebackend.nightlife.domain.models.User updateUserByEmail(@PathVariable String email, @Valid @RequestBody nightlifebackend.nightlife.domain.models.User user) {
        return this.userService.updateUserByEmail(email, user);
    }
}
