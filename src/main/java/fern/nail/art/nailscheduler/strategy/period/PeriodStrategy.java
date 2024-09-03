package fern.nail.art.nailscheduler.strategy.period;

import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.Range;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class PeriodStrategy {
    private final PeriodType period;

    public abstract Range calculateRange(int offset);
}
