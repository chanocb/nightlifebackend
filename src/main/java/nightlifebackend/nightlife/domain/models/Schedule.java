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
public class Schedule {
    private UUID reference;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
} 