package fern.nail.art.nailscheduler.telegram.model;

public record RegistrationResult(Long userId, String errorMessage) {
    public boolean hasUserId() {
        return userId != null;
    }
}
