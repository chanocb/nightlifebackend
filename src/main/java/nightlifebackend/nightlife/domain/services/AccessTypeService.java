package nightlifebackend.nightlife.domain.services;

import nightlifebackend.nightlife.domain.models.AccessType;
import nightlifebackend.nightlife.domain.persistence_ports.AccessTypePersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessTypeService {

    private final AccessTypePersistence accessTypePersistence;
    private final JwtService jwtService;

    @Autowired
    public AccessTypeService(AccessTypePersistence accessTypePersistence, JwtService jwtService) {
        this.accessTypePersistence = accessTypePersistence;
        this.jwtService = jwtService;
    }

    public AccessType create(AccessType accessType) {
        return this.accessTypePersistence.create(accessType);
    }

    public void delete(String reference) {
        this.accessTypePersistence.deleteByReference(reference);
    }

    public AccessType update(String reference, AccessType accessType) {
        return this.accessTypePersistence.update(reference, accessType);
    }

    public List<AccessType> findByTitle(String title) {
        return this.accessTypePersistence.findByTitle(title);
    }


}
