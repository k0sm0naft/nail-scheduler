package fern.nail.art.nailscheduler.api.service;

import java.time.LocalDate;

public interface WeekendManager {
    void processWeekByDate(LocalDate date);
}
