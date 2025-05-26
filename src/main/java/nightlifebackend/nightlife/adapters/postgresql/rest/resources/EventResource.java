package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.Event;
import nightlifebackend.nightlife.domain.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(EventResource.EVENTS)
public class EventResource {

    static final String EVENTS = "/events";

    private final EventService eventService;
    @Autowired
    public EventResource(EventService eventService) {
        this.eventService = eventService;
    }


    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('OWNER')")
    public nightlifebackend.nightlife.domain.models.Event create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.Event event) {
        return this.eventService.create(event);
    }


    @GetMapping("/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    public Event getEventByReference(@PathVariable String reference) {
        return this.eventService.findByReference(reference);
    }

    @GetMapping("/name/{name}")
    @SecurityRequirement(name = "bearerAuth")
    public List<Event> getEventsByName(@PathVariable String name) {
        return this.eventService.findByName(name);
    }

    @GetMapping("/venue/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    public List<Event> getEventsByVenueReference(@PathVariable String reference) {
        return this.eventService.findByVenueReference(reference);
    }

    @DeleteMapping("/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('OWNER')")
    public void deleteEvent(@PathVariable String reference) {
        this.eventService.delete(reference);
    }

    @PutMapping("/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('OWNER')")
    public Event updateEvent(@PathVariable String reference, @Valid @RequestBody Event event) {
        return this.eventService.update(reference, event);
    }

    @GetMapping("/{reference}/access-types")
    @SecurityRequirement(name = "bearerAuth")
    public List<nightlifebackend.nightlife.domain.models.AccessType> getAccessTypeByEventReference(@PathVariable String reference) {
        return this.eventService.getAccessTypeByEventReference(reference);
    }

}
