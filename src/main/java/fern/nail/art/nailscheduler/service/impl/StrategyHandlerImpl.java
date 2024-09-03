package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.service.StrategyHandler;
import fern.nail.art.nailscheduler.strategy.period.PeriodStrategy;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class StrategyHandlerImpl implements StrategyHandler {
    private final Map<PeriodType, PeriodStrategy> strategies;

    private StrategyHandlerImpl(List<PeriodStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(PeriodStrategy::getPeriod, s -> s));
    }

    @Override
    public PeriodStrategy getPeriodStrategy(PeriodType period) {
        return strategies.get(period);
    }
}
