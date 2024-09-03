package fern.nail.art.nailscheduler.model;

public record SlotDeletedEvent(
        Long slotId,
        User user
) {
}
