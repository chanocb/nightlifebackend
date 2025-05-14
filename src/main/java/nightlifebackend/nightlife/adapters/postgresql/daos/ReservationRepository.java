package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.adapters.postgresql.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository  extends JpaRepository<ReservationEntity, UUID> {

    List<ReservationEntity> findByUserEmail(String email);

    @Query("SELECT r FROM ReservationEntity r WHERE r.qrCode = ?1")
    ReservationEntity findByQrCode(byte[] qrCode);
}
