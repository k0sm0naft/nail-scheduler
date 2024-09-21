package fern.nail.art.nailscheduler.api.service.impl;

import fern.nail.art.nailscheduler.api.exception.WorkdayTemplateSizeException;
import fern.nail.art.nailscheduler.api.model.WorkdayTemplate;
import fern.nail.art.nailscheduler.api.repository.WorkdayTemplateRepository;
import fern.nail.art.nailscheduler.api.service.WorkdayTemplateService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkdayTemplateServiceImpl implements WorkdayTemplateService {
    private final WorkdayTemplateRepository templateRepository;

    @Override
    @Transactional
    @CacheEvict(cacheNames = {"workdayTemplateCache", "workdayCache"}, allEntries = true)
    public List<WorkdayTemplate> update(Set<DayOfWeek> days, WorkdayTemplate template) {
        Set<WorkdayTemplate> newTemplates = days.stream()
                                           .map(dayOfWeek -> new WorkdayTemplate(dayOfWeek,
                                                   template.getStartTime(), template.getEndTime()))
                                           .collect(Collectors.toSet());
        return templateRepository.saveAll(newTemplates);
    }

    @Override
    @Cacheable(value = "workdayTemplateCache")
    public List<WorkdayTemplate> getAll() {
        List<WorkdayTemplate> templates = templateRepository.findAll();
        if (templates.size() != DayOfWeek.values().length) {
            throw new WorkdayTemplateSizeException(templates.size());
        }
        return templates;
    }

    @PostConstruct
    @Transactional
    @CacheEvict(cacheNames = {"workdayTemplateCache", "workdayCache"}, allEntries = true)
    public void init() {
        List<DayOfWeek> existingDaysOfWeek = templateRepository.findAll().stream()
                                                              .map(WorkdayTemplate::getDayOfWeek)
                                                              .toList();
        List<WorkdayTemplate> newTemplates =
                Arrays.stream(DayOfWeek.values())
                      .filter(dayOfWeek -> !existingDaysOfWeek.contains(dayOfWeek))
                      .map(this::generateDefaultTemplate)
                      .toList();

        if (!newTemplates.isEmpty()) {
            templateRepository.saveAll(newTemplates);
        }
    }

    private WorkdayTemplate generateDefaultTemplate(DayOfWeek dayOfWeek) {
        //todo make working hours variables and set them from properties
        return new WorkdayTemplate(dayOfWeek, LocalTime.of(8, 0), LocalTime.of(22, 0));
    }
}
