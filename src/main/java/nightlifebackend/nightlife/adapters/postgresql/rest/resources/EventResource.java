package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.Event;
import nightlifebackend.nightlife.domain.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public nightlifebackend.nightlife.domain.models.Event create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.Event event) {
        return this.eventService.create(event);
    }


    @GetMapping("/{reference}")
    public Event getEventByReference(@PathVariable String reference) {
        return this.eventService.findByReference(reference);
    }

    @GetMapping("/name/{name}")
    public List<Event> getEventsByName(@PathVariable String name) {
        return this.eventService.findByName(name);
    }

    @GetMapping("/venue/{reference}")
    public List<Event> getEventsByVenueReference(@PathVariable String reference) {
        return this.eventService.findByVenueReference(reference);
    }

    @DeleteMapping("/{reference}")
    public void deleteEvent(@PathVariable String reference) {
        this.eventService.delete(reference);
    }

    @PutMapping("/{reference}")
    public Event updateEvent(@PathVariable String reference, @Valid @RequestBody Event event) {
        return this.eventService.update(reference, event);
    }

    @GetMapping("/{reference}/access-types")
    public List<nightlifebackend.nightlife.domain.models.AccessType> getAccessTypeByEventReference(@PathVariable String reference) {
        return this.eventService.getAccessTypeByEventReference(reference);
    }

}
