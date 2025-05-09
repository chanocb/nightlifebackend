package nightlifebackend.nightlife.adapters.postgresql.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nightlifebackend.nightlife.domain.models.AccessType;
import org.springframework.beans.BeanUtils;

import java.time.LocalTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accesstype")
public class AccessTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reference;
    private String title;
    private int capacityMax;
    private double price;
    private LocalTime limitHourMax;
    private int numDrinks;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    public AccessTypeEntity(AccessType accessType){
        BeanUtils.copyProperties(accessType, this);
    }

    public AccessType toAccessType(){
        AccessType accessType = new AccessType();
        BeanUtils.copyProperties(this, accessType);
        return accessType;
    }
}
