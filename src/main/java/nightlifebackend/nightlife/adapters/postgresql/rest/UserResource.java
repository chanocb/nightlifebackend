package nightlifebackend.nightlife.adapters.postgresql.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.userdetails.User;
import nightlifebackend.nightlife.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String create(@RequestBody nightlifebackend.nightlife.domain.models.User user) {
        //user.doDefault();
        return this.userService.create(user);
    }

    @SecurityRequirement(name = "basicAuth")
    @PreAuthorize("authenticated")
    @PostMapping(value = TOKEN)
    public String login(@AuthenticationPrincipal User activeUser) {
        return userService.login(activeUser.getUsername());
    }
}
