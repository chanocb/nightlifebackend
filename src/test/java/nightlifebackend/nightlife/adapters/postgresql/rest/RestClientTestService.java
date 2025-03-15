package nightlifebackend.nightlife.adapters.postgresql.rest;

import lombok.Getter;
import nightlifebackend.nightlife.domain.models.Role;
import nightlifebackend.nightlife.domain.services.JwtService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Service
public class RestClientTestService {

    @Autowired
    private JwtService jwtService;

    @Getter
    private String token;

    private boolean isRole(Role role) {
        return this.token != null && jwtService.role(token).equals(role.name());
    }

    private WebTestClient login(Role role, String email, WebTestClient webTestClient) {
        if (!this.isRole(role)) {
            return login(email, webTestClient);
        } else {
            return webTestClient.mutate()
                    .defaultHeader("Authorization", "Bearer " + this.token).build();
        }
    }

    public WebTestClient login(String email, WebTestClient webTestClient) {
        String tokenDto = webTestClient
                .mutate().filter(basicAuthentication(email, "1234")).build()
                .post().uri(UserResource.USERS + "/login")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(Assertions::assertNotNull)
                .returnResult().getResponseBody();
        if (tokenDto != null) {
            this.token = tokenDto;
        }
        return webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + this.token).build();
    }

    public WebTestClient loginAdmin(WebTestClient webTestClient) {
        return this.login(Role.ADMIN, "example@example.com", webTestClient);
    }

    public WebTestClient loginOwner(WebTestClient webTestClient) {
        return this.login(Role.OWNER, "newuser5@example.com", webTestClient);
    }

    public WebTestClient loginClient(WebTestClient webTestClient) {
        return this.login(Role.CLIENT, "example@example.com", webTestClient);
    }

}
