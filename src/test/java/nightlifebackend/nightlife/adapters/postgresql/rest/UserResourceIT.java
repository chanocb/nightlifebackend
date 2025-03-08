package nightlifebackend.nightlife.adapters.postgresql.rest;

import nightlifebackend.nightlife.domain.models.Role;
import nightlifebackend.nightlife.domain.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static nightlifebackend.nightlife.adapters.postgresql.rest.UserResource.USERS;
import org.springframework.web.reactive.function.BodyInserters;


@ApiTestConfig
public class UserResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        User user =
                User.builder().email("example1@example.com")
                        .phone("99999")
                        .firstName("Pepe")
                        .lastName("LL")
                        .birthDate(LocalDate.of(1990, Month.JANUARY, 15))
                        .role(Role.ADMIN)
                        .build();
        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(Assertions::assertNotNull);
    }
}
