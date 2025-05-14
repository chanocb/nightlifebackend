package nightlifebackend.nightlife.domain.services;

import nightlifebackend.nightlife.domain.models.Reservation;
import nightlifebackend.nightlife.domain.persistence_ports.ReservationPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationPersistence reservationPersistence;
    private final JwtService jwtService;

    @Autowired
    public ReservationService(ReservationPersistence reservationPersistence, JwtService jwtService) {
        this.reservationPersistence = reservationPersistence;
        this.jwtService = jwtService;
    }

    public Reservation create(Reservation reservation) {
        return this.reservationPersistence.create(reservation);
    }


    public List<Reservation> findReservationsByUserEmail(String email) {
        return this.reservationPersistence.findByUserEmail(email);
    }

    public Reservation validateReservation(String qrCode) {
        return this.reservationPersistence.validateReservation(qrCode);
    }
}
