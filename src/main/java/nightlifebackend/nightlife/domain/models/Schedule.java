package nightlifebackend.nightlife.domain.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Schedule {
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek dayOfWeek;
} 