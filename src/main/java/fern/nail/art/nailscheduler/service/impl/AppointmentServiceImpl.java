package fern.nail.art.nailscheduler.service.impl;

import static fern.nail.art.nailscheduler.model.Appointment.Status.CANCELED;
import static fern.nail.art.nailscheduler.model.Appointment.Status.CONFIRMED;
import static fern.nail.art.nailscheduler.model.Slot.Status.DELETED;
import static fern.nail.art.nailscheduler.model.Slot.Status.UNPUBLISHED;

import fern.nail.art.nailscheduler.dto.appointment.AppointmentRequestDto;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentResponseDto;
import fern.nail.art.nailscheduler.exception.AppointmentStatusException;
import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.exception.SlotAvailabilityException;
import fern.nail.art.nailscheduler.mapper.AppointmentMapper;
import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.repository.AppointmentRepository;
import fern.nail.art.nailscheduler.service.AppointmentService;
import fern.nail.art.nailscheduler.service.SlotService;
import fern.nail.art.nailscheduler.service.UserProcedureTimeService;
import fern.nail.art.nailscheduler.service.UserService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final AppointmentMapper appointmentMapper;
    private final SlotService slotService;
    private final UserProcedureTimeService procedureTimeService;

    @Override
    @Transactional
    public AppointmentResponseDto create(AppointmentRequestDto requestDto, User user) {
        Appointment appointment = appointmentMapper.toModel(requestDto);
        Slot slot = slotService.get(user, appointment.getSlot().getId());
        if (slot.getAppointment() != null) {
            throw new SlotAvailabilityException(slot);
        }
        slot.setAppointment(appointment);
        appointment.setSlot(slot);
        appointment.setUserProcedureTime(procedureTimeService.get(requestDto.procedure(), user));
        appointment.setStatus(Appointment.Status.PENDING);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponseDto changeStatus(Long appointmentId, boolean isConfirmed, User user) {
        Appointment appointment = getAppointment(appointmentId, user);

        Appointment.Status status = appointment.getStatus();
        if (status == CANCELED && !isConfirmed || status == CONFIRMED && isConfirmed) {
            throw new AppointmentStatusException(status);
        }

        if (isConfirmed){
            appointment.setStatus(CONFIRMED);
        } else {
            appointment.setStatus(CANCELED);
            appointment.getSlot().setAppointment(null);
        }

        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(appointment);
    }

    @Override
    public AppointmentResponseDto get(Long appointmentId, User user) {
        Appointment appointment = getAppointment(appointmentId, user);
        return appointmentMapper.toDto(appointment);
    }

    @Override
    public List<AppointmentResponseDto> getAll(User user) {
        List<Appointment> appointments;
        if (userService.isMaster(user)) {
            appointments = appointmentRepository.findAll();
        } else {
            appointments = appointmentRepository.findAllByClientIdWithSlot(user.getId());
        }
        return appointments.stream()
                           .map(appointmentMapper::toDto)
                           .toList();
    }

    @Override
    @Transactional
    public void delete(Long appointmentId, User user) {
        Appointment appointment = getAppointment(appointmentId, user);
        delete(appointment);
    }

    @Override
    @Transactional
    public void delete(Appointment appointment) {
        Slot slot = appointment.getSlot();
        LocalDateTime slotStart = LocalDateTime.of(slot.getDate(), slot.getStartTime());

        if (appointment.getStatus() != CANCELED && slotStart.isBefore(LocalDateTime.now())) {
            throw new AppointmentStatusException(appointment.getStatus());
        }

        appointmentRepository.deleteById(appointment.getId());
    }

    private void validateAccess(Slot.Status status, User user) {
        if ((status.equals(UNPUBLISHED) || status.equals(DELETED)) && !userService.isMaster(user)) {
            throw new AccessDeniedException("Access denied. User ID: %s.".formatted(user.getId()));
        }
    }

    private Appointment getAppointment(Long appointmentId, User user) {
        return appointmentRepository
                .findById(appointmentId)
                .filter(appointment -> hasAccess(user, appointment))
                .orElseThrow(() -> new EntityNotFoundException(Appointment.class, appointmentId));
    }

    private boolean hasAccess(User user, Appointment appointment) {
        return appointment.getUserProcedureTime().getUser().getId().equals(user.getId())
                || userService.isMaster(user);
    }
}
