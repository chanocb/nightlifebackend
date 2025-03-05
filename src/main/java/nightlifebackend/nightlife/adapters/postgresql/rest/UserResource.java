package nightlifebackend.nightlife.adapters.postgresql.rest;

import nightlifebackend.nightlife.domain.models.User;
import nightlifebackend.nightlife.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {

    static final String USERS = "/users";

    private final UserService userService;
    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        //user.doDefault();
        return this.userService.create(user);
    }
}
