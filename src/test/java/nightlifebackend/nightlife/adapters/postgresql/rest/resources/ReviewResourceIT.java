package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import nightlifebackend.nightlife.domain.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.util.List;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.ReviewResource.REVIEWS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.UserResource.USERS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.VenueResource.VENUES;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ApiTestConfig
public class ReviewResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    private User createUser(String email, Role role) {
        User user = User.builder()
                .email(email)
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(role)
                .build();

        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(user))
                .exchange()
                .expectStatus().isOk();

        return user;
    }

    private Venue createVenue(String name, User owner) {
        Venue venue = Venue.builder()
                .name(name)
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)
                .products(List.of(
                        Product.builder().name("Jagger").price(5.0).build(),
                        Product.builder().name("Tequila").price(12.0).build()
                ))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk();

        return venue;
    }

    private Review createReview(String title, String opinion, int rating, User client, Venue venue) {
        EntityExchangeResult<List<Venue>> result = this.restClientTestService.loginClient(webTestClient).get().uri(VENUES + "/name/"+venue.getName()).exchange().expectStatus().isOk().expectBodyList(Venue.class).returnResult();
        Venue venue_created = result.getResponseBody().get(0);
        Review review = Review.builder()
                .title(title)
                .opinion(opinion)
                .rating(rating)
                .user(client)
                .venue(venue_created)
                .build();

        this.restClientTestService.loginClient(this.webTestClient)
                .post()
                .uri(REVIEWS)
                .body(BodyInserters.fromValue(review))
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(REVIEWS + "/venue/" + venue_created.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(1, reviews.size());
                    assertEquals("This is a review", reviews.get(0).getOpinion());
                    assertEquals(5, reviews.get(0).getRating());
                    assertEquals("titulo", reviews.get(0).getTitle());
                    assertEquals(client.getEmail(), reviews.get(0).getUser().getEmail());
                    assertEquals(venue_created.getReference(), reviews.get(0).getVenue().getReference());
                });

        return review;
    }
    @Test
    void testCreate() {
        User owner = createUser("owner1001@example.com", Role.OWNER);
        Venue venue = createVenue("example1", owner);
        User client = createUser("client1003@example.com", Role.CLIENT);

        createReview("titulo", "This is a review", 5, client, venue);
    }

    @Test
    void testFindByVenueReference() {
        User owner = createUser("owner1002@example.com", Role.OWNER);
        Venue venue = createVenue("example2", owner);
        User client = createUser("client1002@example.com", Role.CLIENT);

        createReview("titulo", "This is a review", 5, client, venue);



    }
}
