package nightlifebackend.nightlife.adapters.postgresql.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;



public enum MusicEntity {
    POP, ROCK, HIP_HOP;

    public static final String PREFIX = "MUSIC_";

    @ManyToMany(mappedBy = "musicGenres")  // Relación inversa desde MusicEntity hacia VenueEntity
    private Set<VenueEntity> venues;

}
