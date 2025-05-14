package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.Reservation;
import nightlifebackend.nightlife.domain.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ReservationResource.RESERVATIONS)
public class ReservationResource {

    static final String RESERVATIONS = "/reservations";

    private final ReservationService reservationService;
    @Autowired
    public ReservationResource(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

     @PostMapping
     public nightlifebackend.nightlife.domain.models.Reservation create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.Reservation reservation) {
         return this.reservationService.create(reservation);
     }

    @GetMapping("/{email}")
    public List<Reservation> findReservationsByUserEmail(@PathVariable String email) {
        return this.reservationService.findReservationsByUserEmail(email);
    }

    @GetMapping("/validate/{qrCode}")
    public Reservation validateReservation(@PathVariable String qrCode) {
        return this.reservationService.validateReservation(qrCode);
    }
}
