package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import nightlifebackend.nightlife.domain.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.EventResource.EVENTS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.ReviewResource.REVIEWS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.UserResource.USERS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.VenueResource.VENUES;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ApiTestConfig
public class EventResourceIT {

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



     @Test
     void testCreate() {
         User owner = createUser("owner2001@example.com", Role.OWNER);
         Venue venue = createVenue("example2", owner);
         createEvent("Test Event", "Test Description",LocalDateTime.of(2027, Month.OCTOBER, 1, 20, 0), venue);


     }

    @Test
    void testGetEventByReference() {
        User owner = createUser("owner3001@example.com", Role.OWNER);
        Venue venue = createVenue("example3", owner);
        Event event = createEvent("Event Reference Test", "Description for reference test", LocalDateTime.of(2027, Month.NOVEMBER, 15, 18, 0), venue);

        EntityExchangeResult<List<Event>> result = this.restClientTestService.loginClient(webTestClient).get().uri(EVENTS + "/name/"+event.getName()).exchange().expectStatus().isOk().expectBodyList(Event.class).returnResult();
        Event createdEvent = result.getResponseBody().get(0);
        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(EVENTS + "/" + createdEvent.getReference())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Event.class)
                .value(retrievedEvent -> {
                    assertEquals(createdEvent.getReference(), retrievedEvent.getReference());
                    assertEquals(createdEvent.getName(), retrievedEvent.getName());
                    assertEquals(createdEvent.getDescription(), retrievedEvent.getDescription());
                    assertEquals(createdEvent.getDateTime(), retrievedEvent.getDateTime());
                    assertEquals(createdEvent.getVenue().getReference(), retrievedEvent.getVenue().getReference());
                });
    }


    @Test
    void testGetEventsByName() {
        User owner = createUser("owner4001@example.com", Role.OWNER);
        Venue venue = createVenue("example4", owner);
        createEvent("Event Name Test", "Description for name test", LocalDateTime.of(2027, Month.DECEMBER, 10, 19, 0), venue);

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(EVENTS + "/name/Event Name Test")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .value(events -> {
                    assertEquals(1, events.size());
                    assertEquals("Event Name Test", events.get(0).getName());
                });
    }


    @Test
    void testDeleteEvent() {
        User owner = createUser("owner5001@example.com", Role.OWNER);
        Venue venue = createVenue("example5", owner);
        Event event = createEvent("Event Delete Test", "Description for delete test", LocalDateTime.of(2027, Month.JANUARY, 20, 21, 0), venue);

        EntityExchangeResult<List<Event>> result = this.restClientTestService.loginClient(webTestClient).get().uri(EVENTS + "/name/"+event.getName()).exchange().expectStatus().isOk().expectBodyList(Event.class).returnResult();
        Event createdEvent = result.getResponseBody().get(0);
        this.restClientTestService.loginOwner(this.webTestClient)
                .delete()
                .uri(EVENTS + "/" + createdEvent.getReference())
                .exchange()
                .expectStatus().isOk();

        this.restClientTestService.loginClient(this.webTestClient)
                .get()
                .uri(EVENTS + "/" + createdEvent.getReference())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testUpdateEvent() {
        User owner = createUser("owner6001@example.com", Role.OWNER);
        Venue venue = createVenue("example6", owner);
        Event event = createEvent("Event Update Test", "Description for update test", LocalDateTime.of(2027, Month.FEBRUARY, 15, 20, 0), venue);

        EntityExchangeResult<List<Event>> result = this.restClientTestService.loginClient(webTestClient)
                .get()
                .uri(EVENTS + "/name/" + event.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .returnResult();
        EntityExchangeResult<List<Venue>> result1 = this.restClientTestService.loginClient(webTestClient)
                .get()
                .uri(VENUES + "/name/" + venue.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Venue.class)
                .returnResult();

        Event createdEvent = result.getResponseBody().get(0);
        Venue createdVenue = result1.getResponseBody().get(0);

        Event updatedEvent = Event.builder()
                .name("Updated Event Name")
                .description("Updated Description")
                .dateTime(LocalDateTime.of(2027, Month.MARCH, 10, 18, 0))
                .venue(result1.getResponseBody().get(0))
                .build();

        this.restClientTestService.loginOwner(this.webTestClient)
                .put()
                .uri(EVENTS + "/" + createdEvent.getReference())
                .body(BodyInserters.fromValue(updatedEvent))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Event.class)
                .value(eventResponse -> {
                    assertEquals("Updated Event Name", eventResponse.getName());
                    assertEquals("Updated Description", eventResponse.getDescription());
                    assertEquals(LocalDateTime.of(2027, Month.MARCH, 10, 18, 0), eventResponse.getDateTime());
                    assertEquals(createdVenue.getReference(), eventResponse.getVenue().getReference());
                });
    }





}
