package fern.nail.art.nailscheduler.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "slots")
public class Slot implements Comparable<Slot> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public int compareTo(Slot s) {
        return LocalDateTime.of(date, startTime).compareTo(LocalDateTime.of(s.date, s.startTime));
    }

    public enum Status {
        UNPUBLISHED,
        PUBLISHED,
        //todo after confirming appointment that makeshift it changes on published again (visible)
        SHIFTED,
        //todo after confirming appointment changes it's will be deleted (invisible)
        DELETED
    }
}
