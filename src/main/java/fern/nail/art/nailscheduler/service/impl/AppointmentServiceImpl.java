package fern.nail.art.nailscheduler.service.impl;

import static fern.nail.art.nailscheduler.model.Appointment.Status.CANCELED;
import static fern.nail.art.nailscheduler.model.Appointment.Status.CONFIRMED;

import fern.nail.art.nailscheduler.dto.appointment.AppointmentRequestDto;
import fern.nail.art.nailscheduler.dto.appointment.AppointmentResponseDto;
import fern.nail.art.nailscheduler.exception.AppointmentStatusException;
import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.mapper.AppointmentMapper;
import fern.nail.art.nailscheduler.model.Appointment;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.model.User;
import fern.nail.art.nailscheduler.repository.AppointmentRepository;
import fern.nail.art.nailscheduler.service.AppointmentService;
import fern.nail.art.nailscheduler.service.SlotAvailabilityService;
import fern.nail.art.nailscheduler.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final AppointmentMapper appointmentMapper;
    private final SlotAvailabilityService slotAvailabilityService;

    @Override
    @Transactional
    public AppointmentResponseDto create(AppointmentRequestDto requestDto, User user) {
        Appointment appointment = appointmentMapper.toModel(requestDto);
        Slot slot = slotAvailabilityService
                .changeSlotAvailability(appointment.getSlot().getId(), false);
        appointment.setSlot(slot);
        appointment.setClientId(user.getId());
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

        appointment.setStatus(isConfirmed ? CONFIRMED : CANCELED);
        slotAvailabilityService.changeSlotAvailability(appointment.getSlot().getId(), !isConfirmed);
        appointment = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(appointment);
    }

    @Override
    public AppointmentResponseDto get(Long appointmentId, User user) {
        return appointmentMapper.toDto(getAppointment(appointmentId, user));
    }

    @Override
    public List<AppointmentResponseDto> getAll(User user) {
        List<Appointment> appointments;
        if (userService.isMaster(user)) {
            appointments = appointmentRepository.findAllWithSlots();
        } else {
            appointments = appointmentRepository.findAllByClientIdWithSlot(user.getId());
        }
        return appointments.stream()
                           .map(appointmentMapper::toDto)
                           .toList();
    }

    @Override
    public void delete(Long appointmentId) {
        //todo: can delete if canceled? or also that is in past?
        validateExistence(appointmentId);
        appointmentRepository.deleteById(appointmentId);
    }

    private void validateExistence(Long appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new EntityNotFoundException(Appointment.class, appointmentId);
        }
    }

    private Appointment getAppointment(Long appointmentId, User user) {
        return appointmentRepository
                .findByIdWithSlots(appointmentId)
                .filter(a -> a.getClientId() == user.getId() || userService.isMaster(user))
                .orElseThrow(() -> new EntityNotFoundException(Appointment.class, appointmentId));
    }
}
