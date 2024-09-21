package fern.nail.art.nailscheduler.api.event;

import fern.nail.art.nailscheduler.api.model.User;

public record SlotDeletedEvent(
        Long slotId,
        User user
) {
}
