package fern.nail.art.nailscheduler.telegram.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Workday {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    public String getFormated() {
        return String.format("%s: %s - %s",
                date.format(DateTimeFormatter.ofPattern("dd.MM.yy")),
                startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                endTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}
