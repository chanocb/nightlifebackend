package nightlifebackend.nightlife.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Reservation {

    private UUID reference;
    private AccessType accessType;
    private User user;
    private double finalPrice;
    private ReservationStatus status;
    private LocalDateTime purchasedDate;
    private String qrCode;
}
