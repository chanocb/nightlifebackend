package nightlifebackend.nightlife.adapters.postgresql.persistence;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

public @ExtendWith(MockitoExtension.class)
class ReservationPersistencePostgresqlIT {

    @InjectMocks
    private ReservationPersistencePostgresql reservationPersistencePostgresql;

    @Test
    void generateQR_ShouldThrowRuntimeException() {
        String invalidText = null;

        assertThrows(RuntimeException.class, () -> reservationPersistencePostgresql.generateQR(invalidText));
    }
}
