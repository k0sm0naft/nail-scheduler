package fern.nail.art.nailscheduler.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityType, Long entityId) {
        super("Type: %s, ID: %d".formatted(entityType.getSimpleName(), entityId));
    }
}
