package fern.nail.art.nailscheduler.service;

import java.time.LocalDate;

public interface WeekendManager {
    void processWeekByDate(LocalDate date);
}
