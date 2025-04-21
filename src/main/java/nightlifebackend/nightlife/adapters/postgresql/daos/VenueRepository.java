package nightlifebackend.nightlife.adapters.postgresql.daos;

import nightlifebackend.nightlife.adapters.postgresql.entities.UserEntity;
import nightlifebackend.nightlife.adapters.postgresql.entities.VenueEntity;
import nightlifebackend.nightlife.domain.models.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface VenueRepository extends JpaRepository<VenueEntity, UUID> {

    Optional<VenueEntity> findByReference(UUID reference);
    List<VenueEntity> findByName(String name);
    List<VenueEntity> findByOwnerEmail(String ownerEmail);
    
    List<VenueEntity> findByLGTBFriendly(boolean LGTBFriendly);
    
    @Query("SELECT v FROM VenueEntity v JOIN v.musicGenres m WHERE m IN :musicGenres GROUP BY v HAVING COUNT(DISTINCT m) = :genreCount")
    List<VenueEntity> findByMusicGenres(@Param("musicGenres") Set<Music> musicGenres, @Param("genreCount") long genreCount);
    
    @Query("SELECT v FROM VenueEntity v LEFT JOIN ReviewEntity r ON v.reference = r.venue.reference GROUP BY v HAVING COALESCE(AVG(r.rating), 0) >= :minRating")
    List<VenueEntity> findByAverageRatingGreaterThanEqual(@Param("minRating") double minRating);

    @Query("SELECT v FROM VenueEntity v LEFT JOIN v.products p WHERE p.name = :productName AND p.price <= :maxPrice")
    List<VenueEntity> findByProductNameAndMaxPrice(@Param("productName") String productName, @Param("maxPrice") double maxPrice);
}
