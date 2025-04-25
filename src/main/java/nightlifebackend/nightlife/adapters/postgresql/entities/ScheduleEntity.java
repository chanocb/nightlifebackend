package nightlifebackend.nightlife.adapters.postgresql.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nightlifebackend.nightlife.domain.models.DayOfWeek;
import nightlifebackend.nightlife.domain.models.Schedule;
import org.springframework.beans.BeanUtils;

import java.time.LocalTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedule")
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reference;

    private LocalTime startTime;
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private VenueEntity venue;

    public ScheduleEntity(Schedule schedule) {
        BeanUtils.copyProperties(schedule, this);
    }

    public Schedule toSchedule() {
        Schedule schedule = new Schedule();
        BeanUtils.copyProperties(this, schedule);
        return schedule;
    }
} 