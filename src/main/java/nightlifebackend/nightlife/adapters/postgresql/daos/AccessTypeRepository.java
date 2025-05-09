package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.adapters.postgresql.entities.AccessTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccessTypeRepository extends JpaRepository<AccessTypeEntity, UUID> {

    Optional<AccessTypeEntity> findByReference(UUID reference);

    @Query("SELECT at FROM AccessTypeEntity at WHERE at.title = ?1")
    List<AccessTypeEntity> findByTitle(String title);
}
