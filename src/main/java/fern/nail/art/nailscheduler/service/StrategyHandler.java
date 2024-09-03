package fern.nail.art.nailscheduler.service;

import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.strategy.period.PeriodStrategy;

public interface StrategyHandler {
    PeriodStrategy getPeriodStrategy(PeriodType period);
}
