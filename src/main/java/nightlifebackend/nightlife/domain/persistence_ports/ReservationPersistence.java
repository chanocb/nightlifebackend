package nightlifebackend.nightlife.domain.persistence_ports;


import nightlifebackend.nightlife.domain.models.Reservation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationPersistence {

    Reservation create(Reservation reservation);

    List<Reservation> findByUserEmail(String email);


    Reservation validateReservation(String qrCode);
}
