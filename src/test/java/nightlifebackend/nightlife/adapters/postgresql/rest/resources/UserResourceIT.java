package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import nightlifebackend.nightlife.domain.models.Role;
import nightlifebackend.nightlife.domain.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.Month;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.UserResource.USERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                        .phone("123456789")
                        .firstName("Pepe")
                        .lastName("LL")
                        .birthDate(LocalDate.of(1990, Month.JANUARY, 15))
                        .role(Role.ADMIN)
                        .password("1234")
                        .build();
        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testCreateWithDuplicateEmail() {
        User user =
                User.builder().email("duplicate@example.com")
                        .phone("123456789")
                        .firstName("Pepe")
                        .lastName("LL")
                        .birthDate(LocalDate.of(1990, Month.JANUARY, 15))
                        .role(Role.ADMIN)
                        .password("1234")
                        .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateWithoutValidNumber() {
        User user =
                User.builder().email("example2@example.com")
                        .phone("99999")
                        .firstName("Pepe")
                        .lastName("LL")
                        .birthDate(LocalDate.of(1990, Month.JANUARY, 15))
                        .role(Role.ADMIN)
                        .password("1234")
                        .build();
        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testLoginAdmin() {
        this.restClientTestService.loginAdmin(this.webTestClient);
        assertTrue(this.restClientTestService.getToken().length() > 10);
    }

    @Test
    void testLoginOwner() {
        this.restClientTestService.loginOwner(this.webTestClient);
        assertTrue(this.restClientTestService.getToken().length() > 10);
    }

    @Test
    void testLoginClient() {
        this.restClientTestService.loginClient(this.webTestClient);
        assertTrue(this.restClientTestService.getToken().length() > 10);
    }

    @Test
    void testReadUserByEmail() {
        User user = User.builder()
                .email("testuser@example.com")
                .phone("123456789")
                .firstName("Test")
                .lastName("User")
                .birthDate(LocalDate.of(1990, Month.JANUARY, 15))
                .role(Role.CLIENT)
                .password("password")
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk();

        this.webTestClient
                .get()
                .uri(USERS + "/" + user.getEmail())
                .headers(headers -> headers.setBasicAuth(user.getEmail(), user.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(response -> {
                    assertEquals(user.getEmail(), response.getEmail());
                    assertEquals(user.getFirstName(), response.getFirstName());
                    assertEquals(user.getLastName(), response.getLastName());
                    assertEquals(user.getPhone(), response.getPhone());
                    assertEquals(user.getBirthDate(), response.getBirthDate());
                    assertEquals(user.getRole(), response.getRole());
                });
    }

    @Test
    void testUpdateUserByEmail() {
        User user = User.builder()
                .email("update@example.com")
                .phone("123456789")
                .firstName("Pepe")
                .lastName("LL")
                .birthDate(LocalDate.of(1990, Month.JANUARY, 15))
                .role(Role.CLIENT)
                .password("1234")
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk();

        User updatedUser = User.builder()
                .email("update@example.com")
                .phone("987654321")
                .firstName("Jose")
                .lastName("Lopez")
                .birthDate(LocalDate.of(1995, Month.FEBRUARY, 20))
                .role(Role.OWNER)
                .password("newpass")
                .build();

        this.webTestClient
                .put()
                .uri(USERS + "/" + user.getEmail())
                .body(BodyInserters.fromValue(updatedUser))
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(response -> {
                    assertEquals("Jose", response.getFirstName());
                    assertEquals("Lopez", response.getLastName());
                    assertEquals("987654321", response.getPhone());
                    assertEquals(Role.OWNER, response.getRole());
                });
    }


}
