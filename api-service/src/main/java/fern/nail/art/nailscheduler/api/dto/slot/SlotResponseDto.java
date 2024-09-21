package fern.nail.art.nailscheduler.api.dto.slot;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SlotResponseDto {
    private long id;

    private LocalDate date;

    private LocalTime startTime;
}
