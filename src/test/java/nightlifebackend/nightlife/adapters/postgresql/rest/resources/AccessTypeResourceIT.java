package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import nightlifebackend.nightlife.adapters.postgresql.entities.AccessTypeEntity;
import nightlifebackend.nightlife.domain.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.AccessTypeResource.ACCESS_TYPES;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.EventResource.EVENTS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.UserResource.USERS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.VenueResource.VENUES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ApiTestConfig
public class AccessTypeResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;


    private AccessType createAccessType(String title, double price, int maxCapacity, Event event) {
        AccessType accessType = AccessType.builder()
                .title(title)
                .price(price)
                .capacityMax(maxCapacity)
                .event(event)
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(ACCESS_TYPES)
                .body(BodyInserters.fromValue(accessType))
                .exchange()
                .expectStatus().isOk();

        return accessType;
    }

    private Event createEvent(String name, String description, LocalDateTime dateTime, Venue venue) {
        EntityExchangeResult<List<Venue>> result = this.restClientTestService.loginClient(webTestClient).get().uri(VENUES + "/name/"+venue.getName()).exchange().expectStatus().isOk().expectBodyList(Venue.class).returnResult();
        Venue venue_created = result.getResponseBody().get(0);
        Event event = Event.builder()
                .name(name)
                .description(description)
                .dateTime(dateTime)
                .venue(venue_created)
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(EVENTS)
                .body(BodyInserters.fromValue(event))
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(EVENTS + "/venue/" + venue_created.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .value(events -> {
                    int lastIndex = events.size() - 1;
                    assertEquals(name, events.get(lastIndex).getName());
                    assertEquals(description, events.get(lastIndex).getDescription());
                    assertEquals(dateTime, events.get(lastIndex).getDateTime());
                    assertEquals(venue_created.getReference(), events.get(lastIndex).getVenue().getReference());
                });


        return event;
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

    @Test
    void testCreateAccessType() {
        User owner = createUser("owner30011@example.com", Role.OWNER);
        Venue venue = createVenue("example2", owner);
        Event event = createEvent("exampleEvent", "exampleDescription", LocalDateTime.of(2027, 10, 1, 20, 0), venue);

        EntityExchangeResult<List<Event>> result = this.restClientTestService.loginClient(webTestClient)
                .get()
                .uri(EVENTS + "/name/" + event.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .returnResult();


        Event createdEvent = result.getResponseBody().get(0);
        AccessType accessType = createAccessType("VIP", 100.0, 50, createdEvent);


    }

    @Test
    void testUpdateAccessType() {
        User owner = createUser("owner30021@example.com", Role.OWNER);
        Venue venue = createVenue("example2", owner);
        Event event = createEvent("exampleEvent", "exampleDescription", LocalDateTime.of(2027, 10, 1, 20, 0), venue);

        EntityExchangeResult<List<Event>> result = this.restClientTestService.loginClient(webTestClient)
                .get()
                .uri(EVENTS + "/name/" + event.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .returnResult();


        Event createdEvent = result.getResponseBody().get(0);
        AccessType accessType = createAccessType("Regular", 50.0, 100, createdEvent);

        EntityExchangeResult<List<AccessType>> resultTitle = this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(ACCESS_TYPES + "/title/" + accessType.getTitle())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AccessType.class)
                .returnResult();

        AccessType createdAccessType = resultTitle.getResponseBody().get(0);

        AccessType updatedAccessType = AccessType.builder()
                .title("Updated Regular")
                .price(75.0)
                .capacityMax(120)
                .event(createdEvent)
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .put()
                .uri(ACCESS_TYPES + "/" + createdAccessType.getReference())
                .body(BodyInserters.fromValue(updatedAccessType))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccessType.class)
                .value(accessTypeResponse -> {
                    assertEquals("Updated Regular", accessTypeResponse.getTitle());
                    assertEquals(75.0, accessTypeResponse.getPrice());
                    assertEquals(120, accessTypeResponse.getCapacityMax());
                    assertEquals(createdEvent.getReference(), accessTypeResponse.getEvent().getReference());
                });
    }

    @Test
    void testDeleteAccessType() {
        User owner = createUser("owner30031@example.com", Role.OWNER);
        Venue venue = createVenue("example2", owner);
        Event event = createEvent("exampleEvent", "exampleDescription", LocalDateTime.of(2027, 10, 1, 20, 0), venue);

        EntityExchangeResult<List<Event>> result = this.restClientTestService.loginClient(webTestClient)
                .get()
                .uri(EVENTS + "/name/" + event.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .returnResult();


        Event createdEvent = result.getResponseBody().get(0);
        AccessType accessType = createAccessType("Temporary", 30.0, 20, createdEvent);

        EntityExchangeResult<List<AccessType>> resultTitle = this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(ACCESS_TYPES + "/title/" + accessType.getTitle())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AccessType.class)
                .returnResult();

        AccessType createdAccessType = resultTitle.getResponseBody().get(0);

        this.restClientTestService.loginOwner(this.webTestClient)
                .delete()
                .uri(ACCESS_TYPES + "/" + createdAccessType.getReference())
                .exchange()
                .expectStatus().isOk();


    }

    @Test
    void testToAccessTypeWithNullEvent() {
        AccessTypeEntity entity = new AccessTypeEntity();
        entity.setTitle("General");
        entity.setPrice(30.0);

        AccessType result = entity.toAccessType();
        assertEquals("General", result.getTitle());
        assertNull(result.getEvent());
    }
}
