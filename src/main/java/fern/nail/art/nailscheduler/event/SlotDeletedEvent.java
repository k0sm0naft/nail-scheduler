package fern.nail.art.nailscheduler.event;

import fern.nail.art.nailscheduler.model.User;

public record SlotDeletedEvent(
        Long slotId,
        User user
) {
}
