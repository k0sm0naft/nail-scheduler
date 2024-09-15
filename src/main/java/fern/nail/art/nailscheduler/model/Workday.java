package fern.nail.art.nailscheduler.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "workdays")
public class Workday {
    @Id
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}
