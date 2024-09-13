package fern.nail.art.nailscheduler.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class<?> entityType, Object entityId) {
        super("Type: %s, ID: %s".formatted(entityType.getSimpleName(), entityId));
    }
}
