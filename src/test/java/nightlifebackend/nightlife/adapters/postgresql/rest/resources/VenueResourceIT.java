package nightlifebackend.nightlife.adapters.postgresql.rest.resources;


import nightlifebackend.nightlife.domain.models.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.VenueResource.VENUES;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ApiTestConfig
public class VenueResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Test
    void testCreate() {
        Venue venue = Venue.builder()
                .name("example1")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()  // Debe ser POST, no GET
                .uri(VENUES)  // No necesitas query params
                .body(BodyInserters.fromValue(venue))  // Enviar el Venue en el cuerpo
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .value(createdVenue -> {
                    assertEquals(venue.getName(), createdVenue.getName());
                    assertEquals(venue.getPhone(), createdVenue.getPhone());
                    assertEquals(venue.isLGTBFriendly(), createdVenue.isLGTBFriendly());
                    assertEquals(venue.getInstagram(), createdVenue.getInstagram());
                });
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
        Venue venue = Venue.builder()
                .name("example2")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
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
                });
    }

    @Test
    void testUpdate() {
        Venue venue = Venue.builder()
                .name("example3")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
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

        Venue updatedVenue = Venue.builder()
                .name("example3")
                .phone("987654321")
                .LGTBFriendly(false)
                .instagram("instagram")
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
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
                });
    }
}
