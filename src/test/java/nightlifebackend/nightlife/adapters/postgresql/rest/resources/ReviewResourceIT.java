package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import nightlifebackend.nightlife.domain.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.util.List;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.ReviewResource.REVIEWS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.UserResource.USERS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.VenueResource.VENUES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ApiTestConfig
public class ReviewResourceIT {

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
        User client = User.builder()
                .email("client100@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.CLIENT)
                .build();
        Review review = Review.builder()
                .title("titulo")
                .opinion("This is a review")
                .rating(5)
                .user(client)
                .venue(venue)
                .build();
        this.restClientTestService.loginClient(this.webTestClient)
                .post()
                .uri(REVIEWS)
                .body(BodyInserters.fromValue(review))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .value(r -> {
                    assertEquals("This is a review", r.getOpinion());
                });
    }
}
