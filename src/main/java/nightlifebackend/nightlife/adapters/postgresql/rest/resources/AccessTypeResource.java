package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.AccessType;
import nightlifebackend.nightlife.domain.services.AccessTypeService;
import nightlifebackend.nightlife.domain.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static nightlifebackend.nightlife.adapters.postgresql.rest.resources.AccessTypeResource.ACCESS_TYPES;

@RestController
@RequestMapping(AccessTypeResource.ACCESS_TYPES)
public class AccessTypeResource {

    static final String ACCESS_TYPES = "/access-types";

    private final AccessTypeService accessTypeService;
    @Autowired
    public AccessTypeResource(AccessTypeService accessTypeService) {
        this.accessTypeService = accessTypeService;
    }

     @PostMapping
     public nightlifebackend.nightlife.domain.models.AccessType create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.AccessType accessType) {
         return this.accessTypeService.create(accessType);
     }

     @DeleteMapping("/{reference}")
    public void delete(@PathVariable String reference) {
        this.accessTypeService.delete(reference);
    }

    @PutMapping("/{reference}")
    public nightlifebackend.nightlife.domain.models.AccessType update(@PathVariable String reference, @Valid @RequestBody nightlifebackend.nightlife.domain.models.AccessType accessType) {
        return this.accessTypeService.update(reference, accessType);
    }

    @GetMapping("/title/{title}")
    public List<AccessType> findByTitle(@PathVariable String title) {
        return this.accessTypeService.findByTitle(title);
    }

}
