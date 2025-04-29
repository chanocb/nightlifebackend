package nightlifebackend.nightlife.adapters.postgresql.rest.resources;


import nightlifebackend.nightlife.domain.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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
    void testFindbyName() {
        User owner = User.builder()
                .email("owner10053@example.com")
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
                .name("Mystery Place")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("haunted_instagram")
                .owner(owner)
                .build();


        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class);
        this.restClientTestService.loginOwner(this.webTestClient)
                .get()
                .uri(VENUES+"/name/"+venue.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class).value(venues -> {
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals(venue.getName())));
                    assertTrue(venues.size() == 1);
                });
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
                    assertEquals(createdVenue.getOwner().getEmail(), foundVenue.getOwner().getEmail());
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
        Coordinate coordinate = new Coordinate();
        coordinate.setLatitude(51.0);
        coordinate.setLongitude(12.0);

        Venue venue = Venue.builder()
                .name("example3")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .products(List.of(product1))
                .coordinate(coordinate)
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
        Coordinate coordinate2 = new Coordinate();
        coordinate2.setLatitude(1.0);
        coordinate2.setLongitude(1.0);

        Venue updatedVenue = Venue.builder()
                .name("updated_example3")
                .phone("987654321")
                .LGTBFriendly(false)
                .instagram("new_instagram")
                .owner(createdVenue.getOwner())
                .products(List.of(product2, product3))
                .coordinate(coordinate2)
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
                    assertEquals(updatedVenue.getCoordinate().getLatitude(), foundVenue.getCoordinate().getLatitude());
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

    @Test
    void testFindByLGTBFriendly() {
        User owner = User.builder()
                .email("owner200@example.com")
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

        Venue venue1 = Venue.builder()
                .name("LGTB Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .musicGenres(Set.of(Music.POP))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue1))
                .exchange()
                .expectStatus().isOk();


        Venue venue2 = Venue.builder()
                .name("Regular Venue")
                .phone("123456789")
                .LGTBFriendly(false)
                .instagram("instagram")
                .owner(owner)
                .musicGenres(Set.of(Music.ROCK))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue2))
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(VENUES + "/filter/lgtb-friendly/true")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class)
                .value(venues -> {
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("LGTB Venue")));
                    assertTrue(venues.stream().noneMatch(v -> v.getName().equals("Regular Venue")));
                });

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(VENUES + "/filter/lgtb-friendly/false")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class)
                .value(venues -> {
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("Regular Venue")));
                    assertTrue(venues.stream().noneMatch(v -> v.getName().equals("LGTB Venue")));
                });
    }

    @Test
    void testFindByMusicGenres() {
        User owner = User.builder()
                .email("owner201@example.com")
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

        Venue popVenue = Venue.builder()
                .name("Pop Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .musicGenres(Set.of(Music.POP))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(popVenue))
                .exchange()
                .expectStatus().isOk();

        Venue rockVenue = Venue.builder()
                .name("Rock Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .musicGenres(Set.of(Music.ROCK))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(rockVenue))
                .exchange()
                .expectStatus().isOk();

        Venue mixedVenue = Venue.builder()
                .name("Mixed Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .musicGenres(Set.of(Music.POP, Music.ROCK))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(mixedVenue))
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(VENUES + "/filter/music-genres?musicGenres=POP")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class)
                .value(venues -> {
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("Pop Venue")));
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("Mixed Venue")));
                    assertTrue(venues.stream().noneMatch(v -> v.getName().equals("Rock Venue")));
                });

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(VENUES + "/filter/music-genres?musicGenres=POP,ROCK")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class)
                .value(venues -> {
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("Mixed Venue")));
                    assertTrue(venues.stream().noneMatch(v -> v.getName().equals("Pop Venue")));
                    assertTrue(venues.stream().noneMatch(v -> v.getName().equals("Rock Venue")));
                });
    }

    @Test
    void testFindByAverageRating() {
        User owner = User.builder()
                .email("owner202@example.com")
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

        User client1 = User.builder()
                .email("client202@example.com")
                .password("1234")
                .firstName("Jane")
                .lastName("Smith")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.CLIENT)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(client1))
                .exchange()
                .expectStatus().isOk();

        User client2 = User.builder()
                .email("client203@example.com")
                .password("1234")
                .firstName("Bob")
                .lastName("Johnson")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.CLIENT)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(client2))
                .exchange()
                .expectStatus().isOk();

        Venue highRatedVenue = Venue.builder()
                .name("High Rated Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .musicGenres(Set.of(Music.POP))
                .build();

        Venue createdHighRatedVenue = this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(highRatedVenue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        Venue lowRatedVenue = Venue.builder()
                .name("Low Rated Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .musicGenres(Set.of(Music.ROCK))
                .build();

        Venue createdLowRatedVenue = this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(lowRatedVenue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        Review review1 = Review.builder()
                .title("Great place")
                .opinion("Amazing venue")
                .rating(5)
                .user(client1)
                .venue(createdHighRatedVenue)
                .build();

        this.restClientTestService.loginClient(this.webTestClient)
                .post()
                .uri(ReviewResource.REVIEWS)
                .body(BodyInserters.fromValue(review1))
                .exchange()
                .expectStatus().isOk();

        Review review2 = Review.builder()
                .title("Good place")
                .opinion("Nice venue")
                .rating(4)
                .user(client2)
                .venue(createdHighRatedVenue)
                .build();

        this.restClientTestService.loginClient(this.webTestClient)
                .post()
                .uri(ReviewResource.REVIEWS)
                .body(BodyInserters.fromValue(review2))
                .exchange()
                .expectStatus().isOk();

        Review review3 = Review.builder()
                .title("Average place")
                .opinion("Ok venue")
                .rating(3)
                .user(client1)
                .venue(createdLowRatedVenue)
                .build();

        this.restClientTestService.loginClient(this.webTestClient)
                .post()
                .uri(ReviewResource.REVIEWS)
                .body(BodyInserters.fromValue(review3))
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(VENUES + "/filter/rating/4.5")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class)
                .value(venues -> {
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("High Rated Venue")));
                    assertTrue(venues.stream().noneMatch(v -> v.getName().equals("Low Rated Venue")));
                });

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(VENUES + "/filter/rating/3.0")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class)
                .value(venues -> {
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("High Rated Venue")));
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("Low Rated Venue")));
                });
    }

    @Test
    void testFindByProductNameAndMaxPrice() {
        User owner = User.builder()
                .email("owner203@example.com")
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

        Venue cheapBeerVenue = Venue.builder()
                .name("Cheap Beer Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .products(List.of(
                        Product.builder().name("Beer").price(2.5).build(),
                        Product.builder().name("Coke").price(1.5).build()
                ))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(cheapBeerVenue))
                .exchange()
                .expectStatus().isOk();

        Venue expensiveBeerVenue = Venue.builder()
                .name("Expensive Beer Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .products(List.of(
                        Product.builder().name("Beer").price(4.5).build(),
                        Product.builder().name("Wine").price(8.0).build()
                ))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(expensiveBeerVenue))
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(VENUES + "/filter/product?productName=Beer&maxPrice=3.0")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class)
                .value(venues -> {
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("Cheap Beer Venue")));
                    assertTrue(venues.stream().noneMatch(v -> v.getName().equals("Expensive Beer Venue")));
                });

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(VENUES + "/filter/product?productName=Beer&maxPrice=5.0")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class)
                .value(venues -> {
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("Cheap Beer Venue")));
                    assertTrue(venues.stream().anyMatch(v -> v.getName().equals("Expensive Beer Venue")));
                });
    }

    @Test
    void testCreateAndUpdateSchedules() {
        User owner = User.builder()
                .email("owner_schedule@example.com")
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
                .name("Schedule Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("schedule_instagram")
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

        Schedule mondaySchedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(18, 0))
                .build();
        Schedule tuesdaySchedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(java.time.LocalTime.of(12, 0))
                .endTime(java.time.LocalTime.of(20, 0))
                .build();

        List<Schedule> schedules = List.of(mondaySchedule, tuesdaySchedule);

        Venue venueWithSchedules = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .body(BodyInserters.fromValue(schedules))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        assert venueWithSchedules != null;
        assert venueWithSchedules.getSchedules().size() == 2;
        assert venueWithSchedules.getSchedules().stream().anyMatch(s -> s.getDayOfWeek() == DayOfWeek.MONDAY);
        assert venueWithSchedules.getSchedules().stream().anyMatch(s -> s.getDayOfWeek() == DayOfWeek.TUESDAY);

        List<Schedule> updatedSchedules = List.of(tuesdaySchedule);
        Venue updatedVenue = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .body(BodyInserters.fromValue(updatedSchedules))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        assert updatedVenue != null;
        assert updatedVenue.getSchedules().size() == 1;
        assert updatedVenue.getSchedules().get(0).getDayOfWeek() == DayOfWeek.TUESDAY;
    }

    @Test
    void testSchedulesEndpoints() {
        User owner = User.builder()
                .email("owner_schedule2@example.com")
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

        Coordinate coordinate = Coordinate.builder().latitude(1.0).longitude(1.0).build();
        Venue venue = Venue.builder()
                .name("Schedule Venue 2")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("schedule_instagram2")
                .owner(owner)
                .coordinate(coordinate)
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

        Schedule mondaySchedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(18, 0))
                .build();
        Schedule tuesdaySchedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(java.time.LocalTime.of(12, 0))
                .endTime(java.time.LocalTime.of(20, 0))
                .build();
        List<Schedule> schedules = List.of(mondaySchedule, tuesdaySchedule);

        Venue venueWithSchedules = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .body(BodyInserters.fromValue(schedules))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        assert venueWithSchedules != null;
        assert venueWithSchedules.getSchedules().size() == 2;
        Schedule monday = venueWithSchedules.getSchedules().stream().filter(s -> s.getDayOfWeek() == DayOfWeek.MONDAY).findFirst().orElseThrow();
        Schedule tuesday = venueWithSchedules.getSchedules().stream().filter(s -> s.getDayOfWeek() == DayOfWeek.TUESDAY).findFirst().orElseThrow();

        List<Schedule> getAll = this.webTestClient.get()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class)
                .returnResult()
                .getResponseBody();
        assert getAll != null;
        assert getAll.size() == 2;

        Schedule getOne = this.webTestClient.get()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules/" + monday.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Schedule.class)
                .returnResult()
                .getResponseBody();
        assert getOne != null;
        assert getOne.getDayOfWeek() == DayOfWeek.MONDAY;

        List<Schedule> newSchedules = List.of(
                Schedule.builder().dayOfWeek(DayOfWeek.WEDNESDAY).startTime(java.time.LocalTime.of(8, 0)).endTime(java.time.LocalTime.of(16, 0)).build()
        );
        Venue updateVenue = Venue.builder()
                .reference(createdVenue.getReference())
                .name(createdVenue.getName())
                .phone(createdVenue.getPhone())
                .LGTBFriendly(createdVenue.isLGTBFriendly())
                .instagram(createdVenue.getInstagram())
                .owner(createdVenue.getOwner())
                .coordinate(createdVenue.getCoordinate())
                .schedules(newSchedules)
                .build();
        Venue updatedVenue = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .put()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .body(BodyInserters.fromValue(newSchedules))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();
        assert updatedVenue != null;
        assert updatedVenue.getSchedules().size() == 1;
        assert updatedVenue.getSchedules().get(0).getDayOfWeek() == DayOfWeek.WEDNESDAY;

        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .delete()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .exchange()
                .expectStatus().isOk();
        List<Schedule> afterDelete = this.webTestClient.get()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class)
                .returnResult()
                .getResponseBody();
        assert afterDelete != null;
        assert afterDelete.isEmpty();

        Venue venueWithSchedules2 = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .body(BodyInserters.fromValue(schedules))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();
        assert venueWithSchedules2 != null;
        Schedule monday2 = venueWithSchedules2.getSchedules().stream().filter(s -> s.getDayOfWeek() == DayOfWeek.MONDAY).findFirst().orElseThrow();
        Schedule tuesday2 = venueWithSchedules2.getSchedules().stream().filter(s -> s.getDayOfWeek() == DayOfWeek.TUESDAY).findFirst().orElseThrow();

        monday2.setStartTime(java.time.LocalTime.of(15, 0));
        Venue updateVenueSingle = Venue.builder()
                .reference(createdVenue.getReference())
                .name(createdVenue.getName())
                .phone(createdVenue.getPhone())
                .LGTBFriendly(createdVenue.isLGTBFriendly())
                .instagram(createdVenue.getInstagram())
                .owner(createdVenue.getOwner())
                .coordinate(createdVenue.getCoordinate())
                .schedules(List.of(monday2, tuesday2))
                .build();
        Schedule updatedSchedule = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .put()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules/" + monday2.getReference())
                .body(BodyInserters.fromValue(monday2))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Schedule.class)
                .returnResult()
                .getResponseBody();
        assert updatedSchedule != null;
        assert updatedSchedule.getStartTime().equals(java.time.LocalTime.of(15, 0));

        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .delete()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules/" + tuesday2.getReference())
                .exchange()
                .expectStatus().isOk();
        List<Schedule> afterDeleteOne = this.webTestClient.get()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Schedule.class)
                .returnResult()
                .getResponseBody();
        assert afterDeleteOne != null;
        assert afterDeleteOne.size() == 1;
        assert afterDeleteOne.get(0).getDayOfWeek() == DayOfWeek.MONDAY;
    }

    @Test
    void testDeleteScheduleWithNonExistentVenue() {
        String nonExistentReference = UUID.randomUUID().toString();
        String scheduleId = UUID.randomUUID().toString();

        this.restClientTestService.loginOwner(this.webTestClient)
                .delete()
                .uri(VENUES + "/" + nonExistentReference + "/schedules/" + scheduleId)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDeleteScheduleWithNonExistentSchedule() {
        User owner = User.builder()
                .email("owner_delete_schedule@example.com")
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
                .name("Schedule Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("schedule_instagram")
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

        String nonExistentScheduleId = UUID.randomUUID().toString();

        this.restClientTestService.loginOwner(this.webTestClient)
                .delete()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules/" + nonExistentScheduleId)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetScheduleWithNonExistentVenue() {
        String nonExistentReference = UUID.randomUUID().toString();
        String scheduleId = UUID.randomUUID().toString();

        this.restClientTestService.loginOwner(this.webTestClient)
                .get()
                .uri(VENUES + "/" + nonExistentReference + "/schedules/" + scheduleId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetScheduleWithNonExistentSchedule() {
        User owner = User.builder()
                .email("owner_get_schedule@example.com")
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
                .name("Schedule Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("schedule_instagram")
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

        String nonExistentScheduleId = UUID.randomUUID().toString();

        this.restClientTestService.loginOwner(this.webTestClient)
                .get()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules/" + nonExistentScheduleId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateScheduleWithNonExistentVenue() {
        String nonExistentReference = UUID.randomUUID().toString();
        String scheduleId = UUID.randomUUID().toString();

        Schedule schedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(18, 0))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .put()
                .uri(VENUES + "/" + nonExistentReference + "/schedules/" + scheduleId)
                .body(BodyInserters.fromValue(schedule))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateScheduleWithNonExistentSchedule() {
        User owner = User.builder()
                .email("owner_update_schedule@example.com")
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
                .name("Schedule Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("schedule_instagram")
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

        String nonExistentScheduleId = UUID.randomUUID().toString();

        Schedule schedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(18, 0))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .put()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules/" + nonExistentScheduleId)
                .body(BodyInserters.fromValue(schedule))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateSchedulesWithNonExistentVenue() {
        String nonExistentReference = UUID.randomUUID().toString();
        
        Schedule mondaySchedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(18, 0))
                .build();

        List<Schedule> schedules = List.of(mondaySchedule);

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES + "/" + nonExistentReference + "/schedules")
                .body(BodyInserters.fromValue(schedules))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateSchedulesWithNonOwner() {
        User owner = User.builder()
                .email("owner_schedule_non_owner@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        User nonOwner = User.builder()
                .email("nonowner_schedule@example.com")
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
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(nonOwner))
                .exchange()
                .expectStatus().isOk();

        Venue venue = Venue.builder()
                .name("Schedule Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("schedule_instagram")
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

        Schedule mondaySchedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(18, 0))
                .build();

        List<Schedule> schedules = List.of(mondaySchedule);

        this.restClientTestService.login(nonOwner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .body(BodyInserters.fromValue(schedules))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void testCreateSchedulesWithDuplicateDays() {
        User owner = User.builder()
                .email("owner_schedule_duplicate@example.com")
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
                .name("Schedule Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("schedule_instagram")
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

        Schedule mondaySchedule1 = Schedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(18, 0))
                .build();

        Schedule mondaySchedule2 = Schedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(19, 0))
                .endTime(java.time.LocalTime.of(23, 0))
                .build();

        List<Schedule> schedules = List.of(mondaySchedule1, mondaySchedule2);

        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .body(BodyInserters.fromValue(schedules))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateSchedulesSuccessfully() {
        User owner = User.builder()
                .email("owner_schedule_success@example.com")
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
                .name("Schedule Venue")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("schedule_instagram")
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

        Schedule mondaySchedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(java.time.LocalTime.of(10, 0))
                .endTime(java.time.LocalTime.of(18, 0))
                .build();

        Schedule tuesdaySchedule = Schedule.builder()
                .dayOfWeek(DayOfWeek.TUESDAY)
                .startTime(java.time.LocalTime.of(12, 0))
                .endTime(java.time.LocalTime.of(20, 0))
                .build();

        List<Schedule> schedules = List.of(mondaySchedule, tuesdaySchedule);

        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES + "/" + createdVenue.getReference() + "/schedules")
                .body(BodyInserters.fromValue(schedules))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .value(updatedVenue -> {
                    assertTrue(updatedVenue.getSchedules().size() == 2);
                    assertTrue(updatedVenue.getSchedules().stream().anyMatch(s -> s.getDayOfWeek() == DayOfWeek.MONDAY));
                    assertTrue(updatedVenue.getSchedules().stream().anyMatch(s -> s.getDayOfWeek() == DayOfWeek.TUESDAY));
                });
    }
}
