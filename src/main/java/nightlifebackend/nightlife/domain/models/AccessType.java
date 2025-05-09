package nightlifebackend.nightlife.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AccessType {

    private UUID reference;
    private String title;
    private int capacityMax;
    private double price;
    private LocalTime limitHourMax;
    private int numDrinks;

    private Event event;
}
