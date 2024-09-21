package fern.nail.art.nailscheduler.api.service.impl;

import static fern.nail.art.nailscheduler.api.model.Appointment.Status.CANCELED;
import static fern.nail.art.nailscheduler.api.model.Appointment.Status.CONFIRMED;
import static fern.nail.art.nailscheduler.api.model.Slot.Status.UNPUBLISHED;

import fern.nail.art.nailscheduler.api.event.AppointmentCreatedEvent;
import fern.nail.art.nailscheduler.api.exception.AppointmentStatusException;
import fern.nail.art.nailscheduler.api.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.api.exception.SlotAvailabilityException;
import fern.nail.art.nailscheduler.api.model.Appointment;
import fern.nail.art.nailscheduler.api.model.ProcedureType;
import fern.nail.art.nailscheduler.api.model.Slot;
import fern.nail.art.nailscheduler.api.model.User;
import fern.nail.art.nailscheduler.api.model.UserProcedureTime;
import fern.nail.art.nailscheduler.api.repository.AppointmentRepository;
import fern.nail.art.nailscheduler.api.service.AppointmentService;
import fern.nail.art.nailscheduler.api.service.SlotService;
import fern.nail.art.nailscheduler.api.service.UserProcedureTimeService;
import fern.nail.art.nailscheduler.api.service.UserService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final SlotService slotService;
    private final UserProcedureTimeService procedureTimeService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @CachePut(value = "appointmentCache", key = "#result.id")
    public Appointment create(Appointment appointment, ProcedureType procedure, Long userId) {
        UserProcedureTime userProcedureTime = procedureTimeService.get(procedure, userId);
        int duration = userProcedureTime.getDuration();
        Slot slot = slotService.getModified(appointment.getSlot().getId(), duration);

        if (slot.getAppointment() != null) {
            throw new SlotAvailabilityException(slot);
        }

        appointment.setUserProcedureTime(userProcedureTime);
        appointment.setStatus(Appointment.Status.PENDING);
        appointment.setSlot(slot);
        appointment = appointmentRepository.save(appointment);

        slot.setAppointment(appointment);
        slotService.createOrUpdate(slot);

        eventPublisher.publishEvent(new AppointmentCreatedEvent(appointment));
        return appointment;
    }

    @Override
    @Transactional
    @CachePut(value = "appointmentCache", key = "#result.id")
    public Appointment changeStatus(Long appointmentId, boolean isConfirmed, User user) {
        Appointment appointment = getAppointment(appointmentId, user);

        Appointment.Status status = appointment.getStatus();
        if (status == CANCELED && !isConfirmed || status == CONFIRMED && isConfirmed) {
            throw new AppointmentStatusException(status);
        }

        if (isConfirmed) {
            appointment.setStatus(CONFIRMED);
        } else {
            appointment.setStatus(CANCELED);
            appointment.getSlot().setAppointment(null);
        }

        return appointmentRepository.save(appointment);
    }

    @Override
    @Cacheable(value = "appointmentCache", key = "#appointmentId")
    public Appointment get(Long appointmentId, User user) {
        return getAppointment(appointmentId, user);
    }

    @Override
    public List<Appointment> getAll(Long userId) {
        List<Appointment> appointments =
                appointmentRepository.findAllByClientIdWithSlot(userId);
        return appointments;
    }

    @Override
    @Transactional
    public void delete(Long appointmentId, User user) {
        Appointment appointment = getAppointment(appointmentId, user);
        delete(appointment);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "appointmentCache", key = "#appointmen.id"),
            @CacheEvict(value = "slotCache", allEntries = true)
    })
    public void delete(Appointment appointment) {
        Slot slot = appointment.getSlot();
        LocalDateTime slotStart = LocalDateTime.of(slot.getDate(), slot.getStartTime());

        if (appointment.getStatus() != CANCELED && slotStart.isBefore(LocalDateTime.now())) {
            throw new AppointmentStatusException(appointment.getStatus());
        }

        appointmentRepository.deleteById(appointment.getId());
    }

    private void validateAccess(Slot.Status status, User user) {
        if (status.equals(UNPUBLISHED) && !userService.isMaster(user)) {
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
