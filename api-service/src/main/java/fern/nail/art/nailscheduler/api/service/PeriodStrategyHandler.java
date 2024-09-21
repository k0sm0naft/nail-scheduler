package fern.nail.art.nailscheduler.api.service;

import fern.nail.art.nailscheduler.api.model.PeriodType;
import fern.nail.art.nailscheduler.api.strategy.period.PeriodStrategy;

public interface PeriodStrategyHandler {
    PeriodStrategy getPeriodStrategy(PeriodType period);
}
