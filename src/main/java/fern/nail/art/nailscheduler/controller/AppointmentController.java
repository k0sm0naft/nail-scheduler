package fern.nail.art.nailscheduler.controller;

import fern.nail.art.nailscheduler.dto.appointment.AppointmentRequestDto;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentResponseDto;
import fern.nail.art.nailscheduler.dto.appointment.StatusDto;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.service.AppointmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDto create(
            @RequestBody @Valid AppointmentRequestDto requestDto,
            @AuthenticationPrincipal User user) {
        return appointmentService.create(requestDto, user);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("!#statusDto.isConfirmed() || hasRole('ROLE_MASTER')")
    public AppointmentResponseDto changeStatus(
            @PathVariable Long id,
            @RequestBody @Valid StatusDto statusDto,
            @AuthenticationPrincipal User user
    ) {
        return appointmentService.changeStatus(id, statusDto.isConfirmed(), user);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentResponseDto get(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        return appointmentService.get(id, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentResponseDto> getAll(@AuthenticationPrincipal User user) {
        return appointmentService.getAll(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MASTER')")
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        appointmentService.delete(id, user);
    }
}
