package nightlifebackend.nightlife.adapters.postgresql.rest.resources;


import nightlifebackend.nightlife.domain.models.Role;
import nightlifebackend.nightlife.domain.models.User;
import nightlifebackend.nightlife.domain.models.Venue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.UserResource.USERS;
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

        // Crear un Venue con los datos de ejemplo, y asignarle el User como owner
        Venue venue = Venue.builder()
                .name("example1")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)  // Asignar el User como propietario
                .build();

        // Realizar el POST para crear el Venue
        this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))  // Enviar el Venue con el owner en el cuerpo
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .value(createdVenue -> {
                    assertEquals(venue.getName(), createdVenue.getName());
                    assertEquals(venue.getPhone(), createdVenue.getPhone());
                    assertEquals(venue.isLGTBFriendly(), createdVenue.isLGTBFriendly());
                    assertEquals(venue.getInstagram(), createdVenue.getInstagram());
                    assertEquals(venue.getOwner().getEmail(), createdVenue.getOwner().getEmail()); // Verificar el owner
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
                .owner(nonExistentOwner)  // Asignar un owner que no existe en la base de datos
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

        // Guardar el User en la base de datos
        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        // Crear un Venue con el User guardado como propietario
        Venue venue = Venue.builder()
                .name("example2")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)  // Asignar el User guardado como propietario
                .build();

        // Guardar el Venue
        Venue createdVenue = this.restClientTestService.loginOwner(this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        // Buscar el Venue por referencia
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

        // Guardar el User en la base de datos
        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        // Crear un Venue con el User guardado como propietario
        Venue venue = Venue.builder()
                .name("example3")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)  // Asignar el User guardado como propietario
                .build();

        // Guardar el Venue
        Venue createdVenue = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        // Crear un Venue con los mismos datos, pero actualizando algunos campos
        Venue updatedVenue = Venue.builder()
                .name("updated_example3")
                .phone("987654321")
                .LGTBFriendly(false)
                .instagram("new_instagram")
                .owner(createdVenue.getOwner())  // Mantener el mismo propietario
                .build();

        // Actualizar el Venue
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
                    assertEquals(updatedVenue.getOwner().getEmail(), foundVenue.getOwner().getEmail()); // Verificar que el owner es el mismo
                });
    }

    @Test
    void testDelete() {
        // Crear un User propietario
        User owner = User.builder()
                .email("owner104@example.com")
                .password("1234")
                .firstName("John")
                .lastName("Doe")
                .phone("987654321")
                .birthDate(LocalDate.of(1992, 3, 5))
                .role(Role.OWNER)
                .build();

        // Guardar el User en la base de datos
        this.webTestClient
                .post()
                .uri(USERS)
                .body(BodyInserters.fromValue(owner))
                .exchange()
                .expectStatus().isOk();

        // Crear un Venue con el User guardado como propietario
        Venue venue = Venue.builder()
                .name("example_to_delete")
                .phone("123456789")
                .LGTBFriendly(true)
                .instagram("instagram")
                .owner(owner)  // Asignar el User guardado como propietario
                .build();

        // Guardar el Venue
        Venue createdVenue = this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .post()
                .uri(VENUES)
                .body(BodyInserters.fromValue(venue))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Venue.class)
                .returnResult()
                .getResponseBody();

        // Verificar que el Venue ha sido creado correctamente
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

        // Realizar la solicitud DELETE para eliminar el Venue
        this.restClientTestService.login(owner.getEmail(), this.webTestClient)
                .delete()
                .uri(VENUES + "/" + createdVenue.getReference())
                .exchange()
                .expectStatus().isOk(); // Verificar que la solicitud DELETE ha sido exitosa


    }
}
