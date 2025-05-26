package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.Reservation;
import nightlifebackend.nightlife.domain.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
     @SecurityRequirement(name = "bearerAuth")
     @PreAuthorize("hasRole('CLIENT')")
     public nightlifebackend.nightlife.domain.models.Reservation create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.Reservation reservation) {
         return this.reservationService.create(reservation);
     }

    @GetMapping("/{email}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('CLIENT')")
    public List<Reservation> findReservationsByUserEmail(@PathVariable String email) {
        return this.reservationService.findReservationsByUserEmail(email);
    }

    @GetMapping("/validate/{qrCode}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('OWNER')")
    public Reservation validateReservation(@PathVariable String qrCode) {
        return this.reservationService.validateReservation(qrCode);
    }
}
