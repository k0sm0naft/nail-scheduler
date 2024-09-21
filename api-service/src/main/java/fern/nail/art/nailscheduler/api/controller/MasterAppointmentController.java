package fern.nail.art.nailscheduler.api.controller;

import fern.nail.art.nailscheduler.api.dto.appointment.AppointmentRequestDto;
import fern.nail.art.nailscheduler.api.dto.appointment.AppointmentResponseDto;
import fern.nail.art.nailscheduler.api.mapper.AppointmentMapper;
import fern.nail.art.nailscheduler.api.model.Appointment;
import fern.nail.art.nailscheduler.api.model.User;
import fern.nail.art.nailscheduler.api.service.AppointmentService;
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
@RequestMapping(value = "masters/appointments")
@PreAuthorize("hasRole('ROLE_MASTER')")
@RequiredArgsConstructor
public class MasterAppointmentController {
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @PostMapping("/users/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDto create(
            @RequestBody @Valid AppointmentRequestDto requestDto,
            @PathVariable Long id) {
        Appointment appointment = appointmentMapper.toModel(requestDto);
        appointment = appointmentService.create(appointment, requestDto.procedure(), id);
        return appointmentMapper.toDto(appointment);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AppointmentResponseDto accept(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Appointment appointment = appointmentService.changeStatus(id, true, user);
        return appointmentMapper.toDto(appointment);
    }

    @GetMapping("users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<AppointmentResponseDto> getAll(@PathVariable(name = "id") Long userId) {
        List<Appointment> appointments = appointmentService.getAll(userId);
        return appointments.stream()
                           .map(appointmentMapper::toDto)
                           .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        appointmentService.delete(id, user);
    }
}
