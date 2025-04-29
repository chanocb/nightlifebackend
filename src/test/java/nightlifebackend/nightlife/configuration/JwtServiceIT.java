package nightlifebackend.nightlife.configuration;

import nightlifebackend.nightlife.TestConfig;
import nightlifebackend.nightlife.domain.services.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
public class JwtServiceIT {

    @Autowired
    private JwtService jwtService;

    @Test
    void testJwtExceptionNotBearer() {
        assertTrue(jwtService.user("Not Bearer").isEmpty());
    }

    @Test
    void testJwtUtilExtract() {
        assertEquals("t.t.t", jwtService.extractToken("Bearer t.t.t"));
    }

    @Test
    void testCreateTokenAndVerify() {
        String token = jwtService.createToken("user-id", "name", "ROLE");
        assertEquals(3, token.split("\\.").length);
        assertTrue(token.length() > 30);
        assertEquals("user-id", jwtService.user(token));
        assertEquals("name", jwtService.name(token));
        assertEquals("ROLE", jwtService.role(token));
    }

    @Test
    void testGetAuthenticatedUserEmail_AuthenticationNull_ThrowsException() {
        SecurityContextHolder.getContext().setAuthentication(null);
        assertThrows(AccessDeniedException.class, () -> jwtService.getAuthenticatedUserEmail());
    }

    @Test
    void testGetAuthenticatedUserEmail_AuthenticationNotAuthenticated_ThrowsException() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        assertThrows(AccessDeniedException.class, () -> jwtService.getAuthenticatedUserEmail());
    }
}
