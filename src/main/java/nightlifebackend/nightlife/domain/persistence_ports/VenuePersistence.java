package nightlifebackend.nightlife.domain.persistence_ports;

import nightlifebackend.nightlife.domain.models.Music;
import nightlifebackend.nightlife.domain.models.User;
import nightlifebackend.nightlife.domain.models.Venue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VenuePersistence {

    Venue create(Venue venue);

    List<Venue> findAll();

    Venue findByReference(String reference);
    List<Venue> findByName(String name);

    Venue update(String reference, Venue venue);

    void deleteByReference(String reference);

    List<Venue> findByOwnerEmail(String email);

    List<Venue> findByLGTBFriendly(boolean LGTBFriendly);

    List<Venue> findByMusicGenres(Set<Music> musicGenres);

    List<Venue> findByAverageRatingGreaterThanEqual(double minRating);

    List<Venue> findByProductNameAndMaxPrice(String productName, double maxPrice);
}
