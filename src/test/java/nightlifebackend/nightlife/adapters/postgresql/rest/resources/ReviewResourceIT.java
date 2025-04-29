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
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.hibernate.validator.internal.util.Contracts.assertTrue;
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
                    int lastIndex = reviews.size() - 1;
                    assertEquals(opinion, reviews.get(lastIndex).getOpinion());
                    assertEquals(rating, reviews.get(lastIndex).getRating());
                    assertEquals(title, reviews.get(lastIndex).getTitle());
                    assertEquals(client.getEmail(), reviews.get(lastIndex).getUser().getEmail());
                    assertEquals(venue_created.getReference(), reviews.get(lastIndex).getVenue().getReference());
                });

        return review;
    }

    private void deleteReview( String title, String opinion, int rating, User client, Venue venue) {
        EntityExchangeResult<List<Review>> result = this.restClientTestService.login(client.getEmail(), webTestClient).get().uri(REVIEWS + "/title/"+title).exchange().expectStatus().isOk().expectBodyList(Review.class).returnResult();
        Review review_created = result.getResponseBody().get(0);

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(REVIEWS + "/" + review_created.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .value(r -> {
                    assertEquals(review_created.getTitle(), r.getTitle());
                    assertEquals(review_created.getOpinion(), r.getOpinion());
                    assertEquals(review_created.getRating(), r.getRating());
                    assertEquals(review_created.getUser().getEmail(), r.getUser().getEmail());
                    assertEquals(review_created.getVenue().getReference(), r.getVenue().getReference());
                });

        this.restClientTestService.login(client.getEmail(), this.webTestClient)
                .delete()
                .uri(REVIEWS + "/" + review_created.getReference())
                .exchange()
                .expectStatus().isOk();
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

    @Test
    void testDeleteReviewByAuthor() {
        User owner = createUser("owner1003@example.com", Role.OWNER);
        Venue venue = createVenue("example2", owner);
        User client = createUser("client1004@example.com", Role.CLIENT);
        Review review = createReview("Test Title 1", "This is a review", 5, client, venue);

        deleteReview("Test Title 1", "This is a review", 5, client, venue);
    }

    @Test
    void testDeleteReviewByNonAuthor() {
        User author = createUser("author@example.com", Role.CLIENT);
        User owner = createUser("owner1005@example.com", Role.OWNER);
        Venue venue = createVenue("Test Venue", owner);
        Review review = createReview("Test Title", "Test Opinion", 5, author, venue);

        EntityExchangeResult<List<Review>> result = this.restClientTestService.loginClient(webTestClient).get().uri(REVIEWS + "/title/"+review.getTitle()).exchange().expectStatus().isOk().expectBodyList(Review.class).returnResult();
        Review review_created = result.getResponseBody().get(0);

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(REVIEWS + "/" + review_created.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .value(r -> {
                    assertEquals(review_created.getTitle(), r.getTitle());
                    assertEquals(review_created.getOpinion(), r.getOpinion());
                    assertEquals(review_created.getRating(), r.getRating());
                    assertEquals(review_created.getUser().getEmail(), r.getUser().getEmail());
                    assertEquals(review_created.getVenue().getReference(), r.getVenue().getReference());
                });

        this.restClientTestService.loginClient(this.webTestClient)
                .delete()
                .uri(REVIEWS + "/" + review_created.getReference())
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void testFindByTitleReturnsEmptyList() {
        String nonExistentTitle = "NonExistentTitle";

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(REVIEWS + "/title/" + nonExistentTitle)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertNotNull(reviews, "The response should not be null");
                    assertTrue(reviews.isEmpty(), "The list of reviews should be empty");
                });
    }
}
