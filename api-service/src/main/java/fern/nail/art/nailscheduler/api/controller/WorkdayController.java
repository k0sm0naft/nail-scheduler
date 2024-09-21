package fern.nail.art.nailscheduler.api.controller;

import fern.nail.art.nailscheduler.api.dto.workday.WorkdayDto;
import fern.nail.art.nailscheduler.api.dto.workday.WorkdayTemplateRequestDto;
import fern.nail.art.nailscheduler.api.dto.workday.WorkdayTemplateResponseDto;
import fern.nail.art.nailscheduler.api.mapper.WorkdayMapper;
import fern.nail.art.nailscheduler.api.mapper.WorkdayTemplateMapper;
import fern.nail.art.nailscheduler.api.model.PeriodType;
import fern.nail.art.nailscheduler.api.model.Workday;
import fern.nail.art.nailscheduler.api.model.WorkdayTemplate;
import fern.nail.art.nailscheduler.api.service.WorkdayService;
import fern.nail.art.nailscheduler.api.service.WorkdayTemplateService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/workdays")
@PreAuthorize("hasRole('ROLE_MASTER')")
@RequiredArgsConstructor
public class WorkdayController {
    private final WorkdayService workdayService;
    private final WorkdayMapper workdayMapper;
    private final WorkdayTemplateService templateService;
    private final WorkdayTemplateMapper templateMapper;

    @GetMapping("/defaults")
    @ResponseStatus(HttpStatus.OK)
    public List<WorkdayTemplateResponseDto> getDefault() {
        List<WorkdayTemplate> templates = templateService.getAll();
        return templates.stream()
                        .map(templateMapper::toDto)
                        .toList();
    }

    @PutMapping("/defaults")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public List<WorkdayTemplateResponseDto> update(
            @RequestBody WorkdayTemplateRequestDto templateDto
    ) {
        WorkdayTemplate template = templateMapper.toModel(templateDto);
        List<WorkdayTemplate> templates =
                templateService.update(templateDto.daysOfWeek(), template);
        return templates.stream()
                        .map(templateMapper::toDto)
                        .toList();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public WorkdayDto createOrUpdate(@RequestBody WorkdayDto workdayDto) {
        Workday workday = workdayMapper.toModel(workdayDto);
        workday = workdayService.createOrUpdate(workday);
        return workdayMapper.toDto(workday);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<WorkdayDto> getByPeriod(
            @RequestParam PeriodType period,
            @RequestParam(defaultValue = "0", required = false) int offset
    ) {
        List<Workday> workdays = workdayService.getByPeriod(period, offset);
        return workdays.stream()
                .map(workdayMapper::toDto)
                .toList();
    }

    @DeleteMapping("/{date}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable LocalDate date) {
        workdayService.delete(date);
    }
}

