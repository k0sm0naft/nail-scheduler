package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.model.PeriodType;
import fern.nail.art.nailscheduler.model.Range;
import fern.nail.art.nailscheduler.model.Workday;
import fern.nail.art.nailscheduler.model.WorkdayTemplate;
import fern.nail.art.nailscheduler.repository.WorkdayRepository;
import fern.nail.art.nailscheduler.service.StrategyHandler;
import fern.nail.art.nailscheduler.service.WorkdayService;
import fern.nail.art.nailscheduler.service.WorkdayTemplateService;
import fern.nail.art.nailscheduler.strategy.period.PeriodStrategy;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkdayServiceImpl implements WorkdayService {
    private final WorkdayRepository workdayRepository;
    private final WorkdayTemplateService templateService;
    private final StrategyHandler strategyHandler;

    @Override
    public Workday createOrUpdate(Workday workday) {
        return workdayRepository.save(workday);
    }

    @Override
    @Transactional
    public List<Workday> getByPeriod(PeriodType period, int offset) {
        PeriodStrategy periodStrategy = strategyHandler.getPeriodStrategy(period);
        Range range = periodStrategy.calculateRange(offset);
        return getByRange(range);
    }

    @Override
    @Transactional
    public List<Workday> getByRange(Range range) {
        RangeProcessor processor = new RangeProcessor(range);
        return processor.process();
    }

    @Override
    public void delete(LocalDate date) {
        if (workdayRepository.existsById(date)) {
            throw new EntityNotFoundException(WorkdayTemplate.class, date);
        }
        workdayRepository.deleteById(date);
    }

    @RequiredArgsConstructor
    private class RangeProcessor {
        private final Range range;
        private Map<LocalDate, Workday> existingWorkdayMap;
        private Map<DayOfWeek, WorkdayTemplate> templateMap;

        List<Workday> process() {
            setWorkdayTemplates();
            setExistingWorkdays();
            List<Workday> allWorkdays = new ArrayList<>();
            LocalDate currentDate = range.startDate();

            while (!currentDate.isAfter(range.endDate())) {
                Workday workday = getOrCreateWorkday(currentDate);
                allWorkdays.add(workday);
                currentDate = currentDate.plusDays(1);
            }

            return allWorkdays;
        }

        private void setExistingWorkdays() {
            List<Workday> workdays =
                    workdayRepository.findAllByDateBetween(range.startDate(), range.endDate());
            existingWorkdayMap = workdays.stream().collect(Collectors.toMap(Workday::getDate,
                    Function.identity()));
        }

        private void setWorkdayTemplates() {
            List<WorkdayTemplate> templates = templateService.getAll();
            templateMap = templates.stream().collect(Collectors.toMap(WorkdayTemplate::getDayOfWeek,
                    Function.identity()));
        }

        private Workday getOrCreateWorkday(LocalDate date) {
            Workday workday = existingWorkdayMap.get(date);
            if (workday == null) {
                WorkdayTemplate template = templateMap.get(date.getDayOfWeek());
                workday = createWorkdayFromTemplate(date, template);
            }
            return workday;
        }

        private Workday createWorkdayFromTemplate(LocalDate date, WorkdayTemplate template) {
            Workday workday = new Workday();
            workday.setDate(date);
            workday.setStartTime(template.getStartTime());
            workday.setEndTime(template.getEndTime());
            return workday;
        }
    }
}
