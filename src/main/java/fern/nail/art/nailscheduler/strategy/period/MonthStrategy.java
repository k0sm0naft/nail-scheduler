package fern.nail.art.nailscheduler.strategy.period;

import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.Range;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import org.springframework.stereotype.Component;

@Component
public class MonthStrategy extends PeriodStrategy {

    public MonthStrategy() {
        super(PeriodType.MONTH);
    }

    @Override
    public Range calculateRange(int offset) {
        LocalDate dayOfMonth = LocalDate.now().plusMonths(offset);

        LocalDate startOfMonth = dayOfMonth.with(TemporalAdjusters.firstDayOfMonth());

        LocalDate endOfMonth = dayOfMonth.with(TemporalAdjusters.lastDayOfMonth());

        return new Range(startOfMonth, endOfMonth);
    }
}
