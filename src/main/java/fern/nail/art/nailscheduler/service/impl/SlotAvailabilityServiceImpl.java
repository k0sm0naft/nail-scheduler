package fern.nail.art.nailscheduler.service.impl;

import fern.nail.art.nailscheduler.exception.EntityNotFoundException;
import fern.nail.art.nailscheduler.exception.SlotAvailabilityException;
import fern.nail.art.nailscheduler.model.Slot;
import fern.nail.art.nailscheduler.repository.SlotRepository;
import fern.nail.art.nailscheduler.service.SlotAvailabilityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlotAvailabilityServiceImpl implements SlotAvailabilityService {
    private final SlotRepository slotRepository;

    @Override
    @Transactional
    public Slot changeSlotAvailability(Long slotId, boolean idAvailable) {
        Slot slot =
                slotRepository.findById(slotId)
                              .orElseThrow(() -> new EntityNotFoundException(Slot.class, slotId));
        boolean oldIdAvailable = slot.getIsAvailable();
        if (idAvailable == oldIdAvailable) {
            throw new SlotAvailabilityException(slot);
        }
        slot.setIsAvailable(!slot.getIsAvailable());
        return slotRepository.save(slot);
    }
}
