package nightlifebackend.nightlife.domain.persistence_ports;

import nightlifebackend.nightlife.domain.models.AccessType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccessTypePersistence {

    AccessType create(AccessType accessType);

    void deleteByReference(String reference);

    AccessType update(String reference, AccessType accessType);

    List<AccessType> findByTitle(String title);

    int countReservationsByAccessTypeReference(UUID reference);



}
