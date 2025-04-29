package nightlifebackend.nightlife.adapters.postgresql.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nightlifebackend.nightlife.domain.models.Coordinate;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coordinate")
public class CoordinateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reference;
    private double latitude;
    private double longitude;

    public CoordinateEntity(Coordinate coordinate) {
        BeanUtils.copyProperties(coordinate, this);
    }
    public Coordinate toCoordinate() {
       Coordinate coordinate = new Coordinate();
        BeanUtils.copyProperties(this, coordinate);
        return coordinate;
    }
}
