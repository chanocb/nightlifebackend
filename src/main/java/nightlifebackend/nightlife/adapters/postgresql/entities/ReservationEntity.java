package nightlifebackend.nightlife.adapters.postgresql.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nightlifebackend.nightlife.domain.models.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reference;
    @ManyToOne
    @JoinColumn(name = "accesstype_id", nullable = false)
    private AccessTypeEntity accessType;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    private double finalPrice;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    private LocalDateTime purchasedDate;
    @Column(name = "qr_code", columnDefinition = "BYTEA")
    private byte[] qrCode;

    public ReservationEntity(Reservation reservation) {
        BeanUtils.copyProperties(reservation, this);
    }

    public Reservation toReservation() {
        Reservation reservation = new Reservation();
        reservation.setUser(this.user != null ? this.user.toUser() : null);
        reservation.setAccessType(this.accessType != null ? this.accessType.toAccessType() : null);
        reservation.setQrCode(this.qrCode != null ? Base64.getEncoder().encodeToString(this.qrCode) : null);
        reservation.setReference(this.reference);
        reservation.setFinalPrice(this.finalPrice);
        reservation.setPurchasedDate(this.purchasedDate);
        reservation.setStatus(this.status);

        return reservation;
    }
}
