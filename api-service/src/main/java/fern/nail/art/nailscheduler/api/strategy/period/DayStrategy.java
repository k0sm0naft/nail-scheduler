package fern.nail.art.nailscheduler.api.strategy.period;

import fern.nail.art.nailscheduler.api.model.PeriodType;
import fern.nail.art.nailscheduler.api.model.Range;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class DayStrategy extends PeriodStrategy {

    public DayStrategy() {
        super(PeriodType.DAY);
    }

    @Override
    public Range calculateRange(int offset) {
        LocalDate targetDate = LocalDate.now().plusDays(offset);
        return new Range(targetDate, targetDate);
    }
}
