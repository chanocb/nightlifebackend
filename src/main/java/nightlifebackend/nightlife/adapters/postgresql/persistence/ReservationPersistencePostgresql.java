package nightlifebackend.nightlife.adapters.postgresql.persistence;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import nightlifebackend.nightlife.adapters.postgresql.daos.AccessTypeRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.ReservationRepository;
import nightlifebackend.nightlife.adapters.postgresql.daos.UserRepository;
import nightlifebackend.nightlife.adapters.postgresql.entities.AccessTypeEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.ReservationEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.domain.models.Reservation;
import nightlifebackend.nightlife.domain.models.ReservationStatus;
import nightlifebackend.nightlife.domain.persistence_ports.ReservationPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Repository("reservationPersistence")
public class ReservationPersistencePostgresql implements ReservationPersistence {

    private final AccessTypeRepository accessTypeRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReservationPersistencePostgresql(AccessTypeRepository accessTypeRepository, ReservationRepository reservationRepository, UserRepository userRepository) {
        this.accessTypeRepository = accessTypeRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public synchronized Reservation create(Reservation reservation) {
        if(reservation.getAccessType().getCapacityMax() == accessTypeRepository.countReservationsByAccessTypeReference(reservation.getAccessType().getReference())) {
            return null;
        }
        ReservationEntity reservationEntity = new ReservationEntity(reservation);
        AccessTypeEntity accessTypeEntity = this.accessTypeRepository
                .findByReference(reservation.getAccessType().getReference())
                .orElseThrow(() -> new RuntimeException("AccessType not found with reference: " + reservation.getAccessType().getReference()));
        UserEntity userEntity = this.userRepository
                .findByEmail(reservation.getUser().getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + reservation.getUser().getEmail()));
        reservationEntity.setAccessType(accessTypeEntity);
        reservationEntity.setStatus(ReservationStatus.PENDING);
        reservationEntity.setUser(userEntity);
        ReservationEntity reservationAux =  this.reservationRepository.save(reservationEntity);

        reservationEntity.setQrCode(this.generateQR(reservationAux.getReference().toString()));
        return this.reservationRepository.save(reservationEntity).toReservation();
    }

    @Override
    public List<Reservation> findByUserEmail(String email) {
        return this.reservationRepository
                .findByUserEmail(email)
                .stream()
                .map(ReservationEntity::toReservation)
                .toList();
    }

    @Override
    public Reservation validateReservation(String qrCode) {
        ReservationEntity reservationEntity = this.reservationRepository.findByQrCode(this.generateQR(qrCode));
        if (reservationEntity != null) {
            if (reservationEntity.getStatus() == ReservationStatus.PENDING  && reservationEntity.getAccessType().getEvent().getDateTime().getDayOfYear() == LocalDateTime.now().getDayOfYear()
            && reservationEntity.getAccessType().getEvent().getDateTime().getYear() == LocalDateTime.now().getYear()) {
                reservationEntity.setStatus(ReservationStatus.ASSISTED);
                this.reservationRepository.save(reservationEntity);
                return reservationEntity.toReservation();
            } else {
                throw new RuntimeException("Reservation already validated");
            }
        }
        return null;
    }

    public byte[] generateQR(String text) {
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }
}
