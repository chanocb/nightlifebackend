package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import nightlifebackend.nightlife.adapters.postgresql.entities.ReservationStatusEntity;
import nightlifebackend.nightlife.domain.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.AccessTypeResource.ACCESS_TYPES;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.EventResource.EVENTS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.ReservationResource.RESERVATIONS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.UserResource.USERS;
import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.VenueResource.VENUES;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ApiTestConfig
public class ReservationResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    private Reservation createReservation(AccessType accessType, User user) {
        Reservation reservation = Reservation.builder()
                .accessType(accessType)
                .user(user)
                .status(ReservationStatus.PENDING)
                .finalPrice(accessType.getPrice())
                .purchasedDate(LocalDateTime.now())
                .qrCode(null)
                .build();

        Reservation reservation_created = this.webTestClient
                .post()
                .uri(RESERVATIONS)
                .body(BodyInserters.fromValue(reservation))
                .exchange()
                .expectStatus().isOk().returnResult(Reservation.class).getResponseBody().blockFirst();

        return reservation_created;
    }

    private AccessType createAccessType(String title, double price, int maxCapacity, Event event) {
        AccessType accessType = AccessType.builder()
                .title(title)
                .price(price)
                .capacityMax(maxCapacity)
                .event(event)
                .build();

        AccessType accessType_created = this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(ACCESS_TYPES)
                .body(BodyInserters.fromValue(accessType))
                .exchange()
                .expectStatus().isOk().returnResult(AccessType.class).getResponseBody().blockFirst();;

        return accessType_created;
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

        Event event_created = this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(EVENTS)
                .body(BodyInserters.fromValue(event))
                .exchange()
                .expectStatus().isOk().returnResult(Event.class).getResponseBody().blockFirst();;

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
                    assertEquals(dateTime.truncatedTo(ChronoUnit.MILLIS), events.get(lastIndex).getDateTime().truncatedTo(ChronoUnit.MILLIS));
                    assertEquals(venue_created.getReference(), events.get(lastIndex).getVenue().getReference());
                });


        return event_created;
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
    void testCreateReservation() {
        User owner = createUser("owner52668@example.com", Role.OWNER);
        User client = createUser("client34895@example.com", Role.CLIENT);
        Venue venue = createVenue("example3", owner);
        Event event = createEvent("exampleEvent", "exampleDescription", LocalDateTime.of(2027, 10, 1, 20, 0), venue);


        AccessType accessType = createAccessType("VIP", 100.0, 50, event);



        Reservation reservation = createReservation(accessType, client);


    }

    @Test
    void testCreateReservationWithOutCapacity() {
        User owner = createUser("owner52669@example.com", Role.OWNER);
        User client = createUser("client34896@example.com", Role.CLIENT);
        Venue venue = createVenue("example4", owner);
        Event event = createEvent("exampleEvent2", "exampleDescription", LocalDateTime.of(2027, 10, 1, 20, 0), venue);


        AccessType accessType = createAccessType("VIP", 100.0, 1, event);



        Reservation reservation = createReservation(accessType, client);
        Reservation reservation2 = createReservation(accessType, client);

        assertEquals(null, reservation2);


    }

    @Test
    void testfindReservationsByUserEmail() {
        User owner = createUser("owner52338@example.com", Role.OWNER);
        User client = createUser("client3352@example.com", Role.CLIENT);
        Venue venue = createVenue("example3", owner);
        Event event = createEvent("exampleEvent", "exampleDescription", LocalDateTime.of(2027, 10, 1, 20, 0), venue);


        AccessType accessType = createAccessType("VIP", 100.0, 50, event);


        Reservation reservation = createReservation(accessType, client);

        this.webTestClient
                .get()
                .uri(RESERVATIONS + "/" + client.getEmail())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Reservation.class)
                .returnResult();


    }

    @Test
    void testValidateQrCode() {
        User owner = createUser("owner12333@example.com", Role.OWNER);
        User client = createUser("client123333@example.com", Role.CLIENT);
        Venue venue = createVenue("TestVenue33", owner);
        Event event = createEvent("TestEvent", "TestDescription", LocalDateTime.now().plusHours(1), venue);

        AccessType accessType = createAccessType("Standard", 20.0, 100, event);
        Reservation reservation = createReservation(accessType, client);

        this.webTestClient
                .get()
                .uri(RESERVATIONS + "/validate/" + reservation.getReference().toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Reservation.class)
                .value(validatedReservation -> {
                    assertEquals(reservation.getUser().getEmail(), validatedReservation.getUser().getEmail());
                    assertEquals(reservation.getAccessType().getTitle(), validatedReservation.getAccessType().getTitle());
                });
    }

    @Test
    void testValidateQrCodeWrongDayDate() {
        User owner = createUser("owner123@example.com", Role.OWNER);
        User client = createUser("client123@example.com", Role.CLIENT);
        Venue venue = createVenue("TestVenue", owner);
        Event event = createEvent("TestEvent", "TestDescription", LocalDateTime.now().plusDays(1), venue);


        AccessType accessType = createAccessType("Standard", 50.0, 100, event);

        Reservation reservation = createReservation(accessType, client);



        this.webTestClient
                .get()
                .uri(RESERVATIONS + "/validate/" + reservation.getReference().toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testValidateQrCodeWrongYearDate() {
        User owner = createUser("owner123232@example.com", Role.OWNER);
        User client = createUser("client123541@example.com", Role.CLIENT);
        Venue venue = createVenue("TestVenue", owner);
        Event event = createEvent("TestEvent", "TestDescription", LocalDateTime.now().plusYears(1), venue);


        AccessType accessType = createAccessType("Standard", 50.0, 100, event);

        Reservation reservation = createReservation(accessType, client);

        this.webTestClient
                .get()
                .uri(RESERVATIONS + "/validate/" + reservation.getReference().toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testValidateQrCodeWrongReservationStatus() {
        User owner = createUser("owner1292883@example.com", Role.OWNER);
        User client = createUser("client122323@example.com", Role.CLIENT);
        Venue venue = createVenue("TestVenue", owner);
        Event event = createEvent("TestEvent", "TestDescription", LocalDateTime.now().plusHours(1), venue);

        AccessType accessType = createAccessType("Standard", 50.0, 100, event);

        Reservation reservation = createReservation(accessType, client);

        this.webTestClient
                .get()
                .uri(RESERVATIONS + "/validate/" + reservation.getReference().toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Reservation.class)
                .value(validatedReservation -> {
                    assertEquals(reservation.getUser().getEmail(), validatedReservation.getUser().getEmail());
                    assertEquals(reservation.getAccessType().getTitle(), validatedReservation.getAccessType().getTitle());
                });

        this.webTestClient
                .get()
                .uri(RESERVATIONS + "/validate/" + reservation.getReference().toString())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testValidateQrCodeWrong() {
        User owner = createUser("owner12333333@example.com", Role.OWNER);
        User client = createUser("client121233@example.com", Role.CLIENT);
        Venue venue = createVenue("TestVenue", owner);
        Event event = createEvent("TestEvent", "TestDescription", LocalDateTime.now().plusDays(1), venue);


        AccessType accessType = createAccessType("Standard", 50.0, 100, event);

        Reservation reservation = createReservation(accessType, client);

        UUID reference = UUID.fromString("12345678-1234-5678-1234-123456789012");


        Reservation reservation_validated = this.webTestClient
                .get()
                .uri(RESERVATIONS + "/validate/" + reference.toString())
                .exchange()
                .expectStatus().isOk().returnResult(Reservation.class).getResponseBody().blockFirst();

        assertEquals(null, reservation_validated);
    }

    @Test
    void testOfWithValidPrefix() {
        assertEquals(ReservationStatusEntity.ASSISTED, ReservationStatusEntity.of("RESERVATION_STATUS_ASSISTED"));
        assertEquals(ReservationStatusEntity.PENDING, ReservationStatusEntity.of("RESERVATION_STATUS_PENDING"));
        assertEquals(ReservationStatusEntity.EXPIRED, ReservationStatusEntity.of("RESERVATION_STATUS_EXPIRED"));
    }
}
