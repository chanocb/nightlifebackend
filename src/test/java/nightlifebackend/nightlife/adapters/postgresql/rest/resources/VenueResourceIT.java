package nightlifebackend.nightlife.adapters.postgresql.rest.resources;


import nightlifebackend.nightlife.domain.models.Product;
import nightlifebackend.nightlife.domain.models.Role;
import nightlifebackend.nightlife.domain.models.User;
import nightlifebackend.nightlife.domain.models.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.UserResource.USERS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.VenueResource.VENUES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ApiTestConfig
public class VenueResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {

        User owner = User.builder()
                .email("owner100@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        this.webTestClient
                .get()
                .uri(USERS + "/" + owner.getEmail())
                .headers(headers -> headers.setBasicAuth(owner.getEmail(), owner.getPassword()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(response -> {
                    assertEquals(owner.getEmail(), response.getEmail());
                    assertEquals(owner.getFirstName(), response.getFirstName());
                    assertEquals(owner.getLastName(), response.getLastName());
                    assertEquals(owner.getPhone(), response.getPhone());
                    assertEquals(owner.getBirthDate(), response.getBirthDate());
                    assertEquals(owner.getRole(), response.getRole());
                });

        Product product1 = Product.builder()
                .name("Jagger")
                .price(5.0)
                .build();

        Product product2 = Product.builder()
                .name("Tequila")
                .price(12.0)
                .build();

        Venue venue = Venue.builder()
                .name("example1")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .products(List.of(product1, product2))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .value(createdVenue -> {
                    assertEquals(venue.getName(), createdVenue.getName());
                    assertEquals(venue.getPhone(), createdVenue.getPhone());
                    assertEquals(venue.isLGTBFriendly(), createdVenue.isLGTBFriendly());
                    assertEquals(venue.getInstagram(), createdVenue.getInstagram());
                    assertEquals(venue.getOwner().getEmail(), createdVenue.getOwner().getEmail());
                    assertTrue(createdVenue.getProducts().stream()
                            .anyMatch(p -> p.getName().equals("Jagger")));
                    assertTrue(createdVenue.getProducts().stream()
                            .anyMatch(p -> p.getName().equals("Tequila")));
                });
    }

    @Test
    void testCreateVenueWithNonExistentOwnerThrowsException() {
        User nonExistentOwner = User.builder()
                .email("nonexistent@example.com")
                .password("1234")
                .firstName("Ghost")
                .lastName("User")
                .phone("000000000")
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.OWNER)
                .build();

        Venue venue = Venue.builder()
                .name("Haunted Place")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("haunted_instagram")
                .owner(nonExistentOwner)
                .build();

        Venue createdVenue = this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();
    }

    @Test
    void testFindAll() {
        this.restClientTestService.loginOwner(this.webTestClient)
                .get()
                .uri(VENUES)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class);
    }

    @Test
    void testFindByReference() {
        User owner = User.builder()
                .email("owner101@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        Venue venue = Venue.builder()
                .name("example2")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .build();

        Venue createdVenue = this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        this.restClientTestService.loginOwner(this.webTestClient)
                .get()
                .uri(VENUES + "/" + createdVenue.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .value(foundVenue -> {
                    assertEquals(createdVenue.getReference(), foundVenue.getReference());
                    assertEquals(createdVenue.getName(), foundVenue.getName());
                    assertEquals(createdVenue.getPhone(), foundVenue.getPhone());
                    assertEquals(createdVenue.getInstagram(), foundVenue.getInstagram());
                    assertEquals(createdVenue.isLGTBFriendly(), foundVenue.isLGTBFriendly());
                    assertEquals(createdVenue.getOwner().getEmail(), foundVenue.getOwner().getEmail()); // Verificar el owner
                });
    }

    @Test
    void testUpdate() {
        User owner = User.builder()
                .email("owner102@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        Product product1 = Product.builder()
                .name("Jagger")
                .price(4.5)
                .build();

        Venue venue = Venue.builder()
                .name("example3")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .products(List.of(product1))
                .build();

        Venue createdVenue = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        Product product2 = Product.builder()
                .name("Gin Tonic")
                .price(8.0)
                .build();

        Product product3 = Product.builder()
                .name("Mojito")
                .price(7.0)
                .build();

        Venue updatedVenue = Venue.builder()
                .name("updated_example3")
                .phone("987654321")
                .LGTBFriendly(false)
                .instagram("new_instagram")
                .owner(createdVenue.getOwner())
                .products(List.of(product2, product3))
                .build();

        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .put()
                .uri(VENUES + "/" + createdVenue.getReference())
                .body(BodyInserters.fromValue(updatedVenue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .value(foundVenue -> {
                    assertEquals(createdVenue.getReference(), foundVenue.getReference());
                    assertEquals(updatedVenue.getName(), foundVenue.getName());
                    assertEquals(updatedVenue.getPhone(), foundVenue.getPhone());
                    assertEquals(updatedVenue.getInstagram(), foundVenue.getInstagram());
                    assertEquals(updatedVenue.isLGTBFriendly(), foundVenue.isLGTBFriendly());
                    assertEquals(updatedVenue.getOwner().getEmail(), foundVenue.getOwner().getEmail());
                    assertTrue(foundVenue.getProducts().stream()
                            .anyMatch(p -> p.getName().equals("Gin Tonic")));
                    assertTrue(foundVenue.getProducts().stream()
                            .anyMatch(p -> p.getName().equals("Mojito")));
                });
    }

    @Test
    void testUpdateVenueByNonOwnerThrowsForbidden() {
        User owner = User.builder()
                .email("owner105@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        Venue venue = Venue.builder()
                .name("example_to_update")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .build();

        Venue createdVenue = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        User nonOwner = User.builder()
                .email("nonowner105@example.com")
                .password("1234")
                .firstName("Jane")
                .lastName("Smith")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(nonOwner))
                .exchange()
                .expectStatus().isOk();

        Venue updatedVenue = Venue.builder()
                .name("updated_example_to_update")
                .phone("987654321")
                .LGTBFriendly(false)
                .instagram("new_instagram")
                .owner(createdVenue.getOwner())
                .build();

        this.restClientTestService.login(nonOwner.getEmail(), this.webTestClient)
                .put()
                .uri(VENUES + "/" + createdVenue.getReference())
                .body(BodyInserters.fromValue(updatedVenue))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void testUpdateVenueNotFoundThrowsNotFound() {
        User owner = User.builder()
                .email("owner107@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        Venue updatedVenue = Venue.builder()
                .name("updated_nonexistent")
                .phone("987654321")
                .LGTBFriendly(false)
                .instagram("new_instagram")
                .owner(owner)
                .build();

        String nonexistentReference = UUID.randomUUID().toString();

        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .put()
                .uri(VENUES + "/" + nonexistentReference)
                .body(BodyInserters.fromValue(updatedVenue))
                .exchange()
                .expectStatus().isNotFound();
    }



    @Test
    void testDelete() {
        User owner = User.builder()
                .email("owner104@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        Venue venue = Venue.builder()
                .name("example_to_delete")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .build();


        Venue createdVenue = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();


        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .get()
                .uri(VENUES + "/" + createdVenue.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .value(foundVenue -> {
                    assertEquals(createdVenue.getReference(), foundVenue.getReference());
                    assertEquals(createdVenue.getName(), foundVenue.getName());
                });


        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .delete()
                .uri(VENUES + "/" + createdVenue.getReference())
                .exchange()
                .expectStatus().isOk();


    }

    @Test
    void testGetVenueByOwner() {
        User owner = User.builder()
                .email("owner109@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        Venue venue = Venue.builder()
                .name("example_to_delete")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .build();

        Venue createdVenue = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .get()
                .uri(VENUES + "/owner?email=" + owner.getEmail())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class);
    }
    @Test
    void testGetVenueByNonOwnerThrowsForbidden() {
        User owner = User.builder()
                .email("owner110@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        Venue venue = Venue.builder()
                .name("example_to_delete")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .build();

        Venue createdVenue = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        User nonOwner = User.builder()
                .email("nonowner109@example.com")
                .password("1234")
                .firstName("Jane")
                .lastName("Smith")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(nonOwner))
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.login(nonOwner.getEmail(), this.webTestClient)
                .get()
                .uri(VENUES + "/owner?email=" + owner.getEmail())
                .exchange()
                .expectStatus().isForbidden();
    }
}
