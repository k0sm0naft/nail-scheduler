package fern.nail.art.nailscheduler.api.service.impl;

import fern.nail.art.nailscheduler.api.model.PeriodType;
import fern.nail.art.nailscheduler.api.service.PeriodStrategyHandler;
import fern.nail.art.nailscheduler.api.strategy.period.PeriodStrategy;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PeriodStrategyHandlerImpl implements PeriodStrategyHandler {
    private final Map<PeriodType, PeriodStrategy> strategies;

    private PeriodStrategyHandlerImpl(List<PeriodStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(PeriodStrategy::getPeriod, s -> s));
    }

    @Override
    public PeriodStrategy getPeriodStrategy(PeriodType period) {
        return strategies.get(period);
    }
}
