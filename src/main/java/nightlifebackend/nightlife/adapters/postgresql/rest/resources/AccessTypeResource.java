package nightlifebackend.nightlife.adapters.postgresql.rest.resources;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import nightlifebackend.nightlife.domain.models.AccessType;
import nightlifebackend.nightlife.domain.services.AccessTypeService;
import nightlifebackend.nightlife.domain.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
     @SecurityRequirement(name = "bearerAuth")
     @PreAuthorize("hasRole('OWNER')")
     public nightlifebackend.nightlife.domain.models.AccessType create(@Valid @RequestBody nightlifebackend.nightlife.domain.models.AccessType accessType) {
         return this.accessTypeService.create(accessType);
     }

     @DeleteMapping("/{reference}")
     @SecurityRequirement(name = "bearerAuth")
     @PreAuthorize("hasRole('OWNER')")
    public void delete(@PathVariable String reference) {
        this.accessTypeService.delete(reference);
    }

    @PutMapping("/{reference}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('OWNER')")
    public nightlifebackend.nightlife.domain.models.AccessType update(@PathVariable String reference, @Valid @RequestBody nightlifebackend.nightlife.domain.models.AccessType accessType) {
        return this.accessTypeService.update(reference, accessType);
    }

    @GetMapping("/title/{title}")
    @SecurityRequirement(name = "bearerAuth")
    public List<AccessType> findByTitle(@PathVariable String title) {
        return this.accessTypeService.findByTitle(title);
    }

}
