package fern.nail.art.nailscheduler.strategy.period;

import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.Range;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import org.springframework.stereotype.Component;

@Component
public class WeekStrategy extends PeriodStrategy {

    public WeekStrategy() {
        super(PeriodType.WEEK);
    }

    @Override
    public Range calculateRange(int offset) {
        LocalDate dayOfWeek = LocalDate.now().plusWeeks(offset);

        LocalDate startOfWeek =
                dayOfWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());

        LocalDate endOfWeek = dayOfWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue());

        return new Range(startOfWeek, endOfWeek);
    }
}
